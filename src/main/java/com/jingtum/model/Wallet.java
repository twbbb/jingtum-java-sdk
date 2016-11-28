/*
 * Copyright www.jingtum.com Inc. 
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jingtum.model;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.annotations.Expose;
import com.jingtum.net.APIProxy;
import com.jingtum.net.JingtumAPIAndWSServer;
import com.jingtum.net.JingtumFingate;
import com.jingtum.util.Utility;
import com.jingtum.Jingtum;
import com.jingtum.JingtumMessage;
import com.jingtum.core.crypto.ecdsa.Seed;
import com.jingtum.exception.APIConnectionException;
import com.jingtum.exception.InvalidRequestException;
import com.jingtum.exception.APIException;
import com.jingtum.exception.AuthenticationException;
import com.jingtum.exception.ChannelException;
import com.jingtum.exception.FailedException;
import com.jingtum.exception.InvalidParameterException;

import java.lang.reflect.Type;
import java.util.HashMap;
/**
 * @author jzhao
 * @version 1.0
 * Wallet class, main entry point
 */
public class Wallet extends BaseWallet {
	@Expose
	private boolean success; //Operation success or not
	@Expose
	private long ledger;
	@Expose
	private boolean validated;  
	@Expose
	private BalanceCollection balances;
	@Expose
	private PaymentCollection payments;
	@Expose
	private OrderCollection orders;
	@Expose
	private TrustLineCollection trustlines;
	@Expose
	private Notification notification;
	@Expose
	private TransactionCollection transactions;
	@Expose
	private Transaction transaction;
	@Expose
	private RelationCollection relations;

	private static final String VALIDATED = "?validated=";

	private boolean __isActivated = false;	//If the account is activated, at least 25 SWT needed
	/**
	 * @return true if the wallet is activated
	 * @throws InvalidRequestException 
	 */
	public boolean isActivated() throws InvalidRequestException {
		BalanceCollection bc = null;
		if(!__isActivated){
			try {
				bc = this.getBalance(Jingtum.getCurrencySWT(), ""); //check if have enough SWT
			} catch (FailedException e) {
				e.printStackTrace();
			}catch (AuthenticationException e) {
				e.printStackTrace();
			} catch (APIConnectionException e) {
				e.printStackTrace();
			} catch (APIException e) {
				e.printStackTrace();
			} catch (ChannelException e) {
				e.printStackTrace();
			} catch (InvalidParameterException e) {
				e.printStackTrace();
			}
			
			if(bc != null){
				Balance bl = bc.getData().get(0);
				if(bl != null && bl.getValue() >= JingtumFingate.getInstance().getActivateAmount()){
					__isActivated = true;
				}
			}
		}
		return __isActivated;
	}
	/**
	 * @param __isActivated
	 */
	public void setActivated(boolean __isActivated) {
		this.__isActivated = __isActivated;
	}
	/**
	 * Get private RelationCollection instance
	 * @return relations
	 */
	private RelationCollection getMyRelations(){
		return relations;
	}
	/**
	 * get private Transaction instance
	 * @return transaction
	 */
	private Transaction getTransaction() {
		return transaction;
	}
	/**
	 * get private TransactionCollection instance
	 * @return transactions
	 */
	private TransactionCollection getMyTransactionCollection(){
		return this.transactions;
	}	
	/**
	 * get private notification instance
	 * @return notification
	 */
	private Notification getMyNotification(){
		return notification;
	}	
	/**
	 * get private TrustLineCollection instance
	 * @return trustlines
	 */
	private TrustLineCollection getTrustLinesCollection() {
		return trustlines;
	}
	/**
	 * get private Order Collection instance
	 * @return orders
	 */
	private OrderCollection getOrdersCollection() {
		return orders;
	}
	/**
	 * get private PaymentCollection instance
	 * @return payments
	 */
	private PaymentCollection getPaymentsCollection() {
		return payments;
	}
	/**
	 * Create Wallet instance with address and secret key
	 * @param address
	 * @param secret
	 * @throws InvalidParameterException 
	 */
	public Wallet (String address, String secret) throws InvalidParameterException{
		if(!Utility.validateKeyPair(address, secret)){
			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS_OR_SECRET, address + secret, null);
		}
		this.address = address;
		this.secret = secret;
	}	
	/*
	 * Create Wallet instance from secret
	 * Only use secret can derive address
	 * @param secret
	 * @throws InvalidParameterException 
	 */
	public Wallet (String secret) throws InvalidParameterException {
		if(!Utility.isValidSecret(secret)){
			throw new InvalidParameterException(JingtumMessage.INVALID_SECRET, secret, null);
		}
		this.address = Seed.computeAddress(secret);
		this.secret = secret;
	}	
    /**
     * Gson builder
     */
    public static final Gson PRETTY_PRINT_GSON = new GsonBuilder().
            setPrettyPrinting().
            serializeNulls().
            disableHtmlEscaping().
            setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).
            setLongSerializationPolicy(LongSerializationPolicy.STRING).
            registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
                public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                    if (src == src.longValue())
                        return new JsonPrimitive(src.longValue());
                    return new JsonPrimitive(src);
                }
            }).
            create();
	/**
	 * Get private balance collection
	 * @return balances
	 */
	private BalanceCollection getBalances() {
		return balances;
	}
	/**
	 * Get wallet address
	 * @return address
	 */
	public String getAddress() {
		return this.address;
	}
	/**
	 * Get wallet secret key
	 * @return secret
	 */
	public String getSecret() {
		return this.secret;
	}	
    /**
     * Static method to get the balance collection of a given address
     * @return BalanceCollection instance
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws FailedException 
     */
    public BalanceCollection getBalance()
            throws AuthenticationException, InvalidRequestException,
            APIConnectionException, APIException, ChannelException, FailedException {
		return APIProxy.request(
				APIProxy.RequestMethod.GET,
				APIProxy.formatURL(Balance.class, this.getAddress(), Utility.buildSignString(this.getAddress(), this.getSecret())),
				null,
				Wallet.class).getBalances();
    }
    /**
     * Get balance filtered by currency/counterparty
     * @param currency
     * @param counterparty
     * @return BalanceCollection instance
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public BalanceCollection getBalance(String currency, String counterparty)
            throws AuthenticationException, InvalidRequestException,
            APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException {
    	StringBuilder sb = new StringBuilder();
    	//check if currcncy is valid
    	if(Utility.isNotEmpty(currency)){ 
    		if(!Utility.isValidCurrency(currency)){
    			throw new InvalidParameterException(JingtumMessage.INVALID_CURRENCY,currency,null);
    		}else{
    	    	sb.append("&currency=");
    	    	sb.append(currency);
    		}
    	}
    	//check counterparty is valid
    	if(Utility.isNotEmpty(counterparty)){
    		if(!Utility.isValidAddress(counterparty)){
    			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,counterparty,null);
    		}else{
    	    	sb.append("&counterparty=");
    	    	sb.append(counterparty);
    		}
    	}
		return APIProxy.request(
		        APIProxy.RequestMethod.GET,
                APIProxy.formatURL(Balance.class, this.getAddress(), Utility.buildSignString(this.getAddress(), this.getSecret()) + sb.toString()),
                null,
                Wallet.class).getBalances();
    }
    /**
     * Post a payment
     * @param receiver payment receiver
     * @param pay payment amount
     * @param validate true if wait for payment result
     * @param resourceID payment resource ID
     * @return PostResult instance
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public RequestResult submitPayment(String receiver, Amount pay, boolean validate, String resourceID)
    		throws AuthenticationException, InvalidRequestException,
            APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	String uid = resourceID;
    	
    	if(!Utility.isValidAddress(receiver)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,receiver,null);
    	}
    	if(!Utility.isValidAmount(pay)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_AMOUNT,null,null);
    	}
    	if(pay.getValue() <= 0){
    		throw new InvalidParameterException(JingtumMessage.INVALID_VALUE,String.valueOf(pay.getValue()),null);
    	}
    	if(Utility.isEmpty(uid)){ // if uid is null generate a resouce ID
    		uid = JingtumAPIAndWSServer.getInstance().getNextUUID();
    	}
    	
    	HashMap<String, String> destination_amount = new HashMap<String, String>();  
    	destination_amount.put("currency", pay.getCurrency());
    	destination_amount.put("value", Utility.doubleToString(pay.getValue()));    	
    	destination_amount.put("issuer",pay.getCounterparty()); 
    	
    	HashMap<String, Object> payment = new HashMap<String, Object>();  
    	payment.put("source_account", this.getAddress());
    	payment.put("destination_account", receiver);
    	payment.put("destination_amount", destination_amount);
    	
    	HashMap<String, Object> content = new HashMap<String, Object>();
    	content.put("secret", this.getSecret());
    	content.put("client_resource_id", uid);
    	content.put("payment", payment);
    	
    	String params = APIProxy.GSON.toJson(content);
    	return APIProxy.request(APIProxy.RequestMethod.POST, APIProxy.formatURL(Payment.class, this.getAddress(), VALIDATED + Boolean.toString(validate)), params, RequestResult.class);
    }     
    /**
     * Pay synchronously, waiting for the request result
     * @param receiver receiver wallet address
     * @param pay pay info in JingtumAmount
     * @param resourceID 
     * @return PostResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
    public RequestResult syncSubmitPayment(String receiver, Amount pay,String resourceID) 
    		throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	return submitPayment(receiver, pay, true, resourceID);    	
    }    
    /**
     * Pay Asynchronously, not waiting for the request result
     * @param receiver receiver wallet address
     * @param pay pay info in JingtumAmount
     * @param resourceID
     * @return PostResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
    public RequestResult asyncSubmitPayment(String receiver, Amount pay,String resourceID) 
    		throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	return submitPayment(receiver, pay, false, resourceID);    	
    }
    /**
     * Pay with payment path
     * @param paymentPath payment path, can obtain by calling getPaymentPath
     * @param validate sync/async
     * @param resourceID
     * @return RequestResult instance
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
    public RequestResult submitPayment(Payment paymentPath, boolean validate, String resourceID)
    		throws AuthenticationException, InvalidRequestException,
            APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	String uid = resourceID;

    	if(Utility.isEmpty(uid)){
    		uid = JingtumAPIAndWSServer.getInstance().getNextUUID();
    	}
    	HashMap<String, String> source_amount = new HashMap<String, String>();  
    	source_amount.put("currency", paymentPath.getSourceAmount().getCurrency());
    	source_amount.put("value", Utility.doubleToString(paymentPath.getSourceAmount().getValue() * JingtumFingate.getInstance().getPathRate()));
    	source_amount.put("issuer",paymentPath.getSourceAmount().getIssuer()); 
    	
    	HashMap<String, String> destination_amount = new HashMap<String, String>();  
    	destination_amount.put("currency", paymentPath.getDestinationAmount().getCurrency());
    	destination_amount.put("value", Utility.doubleToString(paymentPath.getDestinationAmount().getValue()));    	
    	destination_amount.put("issuer",paymentPath.getDestinationAmount().getIssuer()); 
    	
    	HashMap<String, Object> payment = new HashMap<String, Object>();  
    	payment.put("source_account", this.getAddress());
    	payment.put("source_amount", source_amount);
    	payment.put("source_slippage", Utility.doubleToString(paymentPath.getSourceSlippage()));
    	payment.put("destination_account", paymentPath.getDestinationAccount());
    	payment.put("destination_amount", destination_amount);    	
    	payment.put("paths", paymentPath.getPaths());
    	
    	HashMap<String, Object> content = new HashMap<String, Object>();
    	content.put("secret", this.getSecret());
    	content.put("client_resource_id", uid);
    	content.put("payment", payment);
    	
    	String params = APIProxy.GSON.toJson(content);
    	return APIProxy.request(APIProxy.RequestMethod.POST, APIProxy.formatURL(Payment.class,this.getAddress(),VALIDATED + Boolean.toString(validate)), params, RequestResult.class);
    } 
    /**
     * Pay synchronously, waiting for the request result
     * @param paymentPath payment path, can obtain by calling getPaymentPath
     * @param resourceID
     * @return RequestResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
    public RequestResult syncSubmitPayment(Payment paymentPath, String resourceID) 
    		throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	return submitPayment(paymentPath, true, resourceID);
    }
    /**
     * Pay Asynchronously, not waiting for the request result
     * @param paymentPath payment path, can obtain by calling getPaymentPath
     * @param resourceID
     * @return RequestResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
    public RequestResult asyncSubmitPayment(Payment paymentPath, String resourceID) 
    		throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	return submitPayment(paymentPath, false, resourceID);
    }
    /**
     * Take hash number or resource ID to get payment information 
     * @param id 
     * @return Payment instance
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public Payment getPayment(String id)throws AuthenticationException, InvalidRequestException,
            APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	if(Utility.isEmpty(id)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_ID,id,null);
    	}
    	return APIProxy.request(APIProxy.RequestMethod.GET, APIProxy.formatURL(Payment.class,this.getAddress(),"/" + id + Utility.buildSignString(this.getAddress(), this.getSecret())), null, Payment.class);
    }  
    /**
     * @return PaymentCollection
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws FailedException 
     */
    public PaymentCollection getPaymentList() throws AuthenticationException, InvalidRequestException, 
    APIConnectionException, APIException, ChannelException, FailedException{
    	try {
			return getPaymentList(null,null,false,Payment.Direction.all,0,0);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		}
		return null;
    }
    /**
     * @param sourceAccount
     * @param destinationAccount
     * @param excludeFailed
     * @param direction incoming/outgoing/all
     * @param resultPerPage result per page
     * @param page page number
     * @return PaymentCollection
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
    public PaymentCollection getPaymentList(String sourceAccount, String destinationAccount, boolean excludeFailed, Payment.Direction direction, int resultPerPage, int page)
    		throws AuthenticationException, InvalidRequestException,
    		APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	StringBuilder param = new StringBuilder();
    	if(Utility.isNotEmpty(sourceAccount)){
    		if(!Utility.isValidAddress(sourceAccount)){
    			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,sourceAccount,null);
    		}else{
    			param.append("&");
    			param.append("source_account=");
    			param.append(sourceAccount);
    		}
    	}
       	if(Utility.isNotEmpty(destinationAccount)){
    		if(!Utility.isValidAddress(destinationAccount)){
    			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,destinationAccount,null);
    		}else{
    			param.append("&");
    			param.append("destination_account=");
    			param.append(destinationAccount);
    		}
    	}
       	if(excludeFailed){
       		param.append("&");
       		param.append("exclude_failed=");
       		param.append(excludeFailed);
       	}
       	if(null != direction && direction != Payment.Direction.all){
       		param.append("&");
       		param.append("direction=");
       		param.append(direction);
       	}
       	if(resultPerPage < 0){
       		throw new InvalidParameterException(JingtumMessage.INVALID_PAGE_INFO,String.valueOf(resultPerPage),null);
       	}
       	if(resultPerPage > 0){
       		param.append("&");
       		param.append("results_per_page=");
       		param.append(resultPerPage);
       	}
       	if(page < 0){
       		throw new InvalidParameterException(JingtumMessage.INVALID_PAGE_INFO,String.valueOf(page),null);
       	}
       	if(page > 0){
       		param.append("&");
       		param.append("page=");
       		param.append(page);
       	}
    	return APIProxy.request(APIProxy.RequestMethod.GET, APIProxy.formatURL(Payment.class,this.getAddress(),Utility.buildSignString(this.getAddress(), this.getSecret()) + param.toString()), null, Wallet.class).getPaymentsCollection();
    }    
    /**
     * Post a new order request
     * @param orderType buy or sell
     * @param pay 
     * @param get
     * @param validate synce/async
     * @return PostRestul instance
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public RequestResult createOrder(Order.OrderType orderType, Amount pay, Amount get, boolean validate)throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	if(orderType == null){
    		throw new InvalidParameterException(JingtumMessage.SPECIFY_ORDER_TYPE,null,null);
    	}
    	if(!Utility.isValidAmount(pay) ||!Utility.isValidAmount(get)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_AMOUNT,null,null);
    	}
    	
    	HashMap<String, String> taker_pays = new HashMap<String, String>(); 
    	taker_pays.put("currency", get.getCurrency());
    	taker_pays.put("counterparty", get.getCounterparty());
    	taker_pays.put("value", Utility.doubleToString(get.getValue()));
    	
    	HashMap<String, String> taker_gets = new HashMap<String, String>();
    	taker_gets.put("currency", pay.getCurrency());
    	taker_gets.put("counterparty", pay.getCounterparty());
    	taker_gets.put("value", Utility.doubleToString(pay.getValue()));
    	
    	HashMap<String, Object> order = new HashMap<String, Object>();
    	order.put("type", orderType);
    	order.put("taker_pays", taker_pays);
    	order.put("taker_gets", taker_gets);
    	
    	HashMap<String, Object> content = new HashMap<String, Object>();
    	content.put("secret", this.getSecret());
    	content.put("order", order);
    	
    	String params = APIProxy.GSON.toJson(content);
    	return APIProxy.request(APIProxy.RequestMethod.POST, APIProxy.formatURL(Order.class,this.getAddress(),VALIDATED + Boolean.toString(validate)), params, RequestResult.class);
    }  
    /**
     * Put an order synchronously. i.e. waiting for the request from server
     * @param orderType
     * @param pay
     * @param get
     * @return RequestResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
    public RequestResult syncCreateOrder(Order.OrderType orderType, Amount pay, Amount get) 
    		throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	 return createOrder(orderType, pay, get, true);
    }
    /**
     * Put an order asynchronously. i.e. not waiting for the request from server
     * @param orderType
     * @param pay
     * @param get
     * @return RequestResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
    public RequestResult asyncCreateOrder(Order.OrderType orderType, Amount pay, Amount get) 
    		throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	 return createOrder(orderType, pay, get, false);
    }
    /**
     * Cancel a posted order given a order sequence number
     * @param sequence
     * @param validate
     * @return PostResult instance
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws FailedException 
     */
    public RequestResult cancelOrder(long sequence, boolean validate)throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException, ChannelException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	HashMap<String, Object> content = new HashMap<String, Object>();
    	content.put("secret", this.getSecret());
    	
    	String params = APIProxy.GSON.toJson(content);
    	return APIProxy.request(APIProxy.RequestMethod.DELETE, APIProxy.formatURL(Order.class,this.getAddress(),"/" + Long.toString(sequence) + VALIDATED + Boolean.toString(validate)), params, RequestResult.class);
    }    
    /**
     * Cancel an order synchronously
     * @param sequence
     * @return RequestResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws FailedException 
     */
    public RequestResult syncCancelOrder(long sequence) 
    		throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException{
    	 return cancelOrder(sequence, true);
    }
    /**
     * Cancel an order asynchronously
     * @param sequence
     * @return RequestResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws FailedException 
     */
    public RequestResult asyncCancelOrder(long sequence) 
    		throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException{
    	 return cancelOrder(sequence, false);
    }
    /**
     * Get all orders of a wallet
     * @return OrderCollection instance
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException 
     * @throws FailedException 
     */
    public OrderCollection getOrderList()throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException, ChannelException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	return APIProxy.request(APIProxy.RequestMethod.GET, APIProxy.formatURL(Order.class,this.getAddress(),Utility.buildSignString(this.getAddress(), this.getSecret())), null, Wallet.class).getOrdersCollection();
    }    
    /**
     * Get order by ID
     * @param id
     * @return Order instance
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public Order getOrder(String id)throws AuthenticationException, InvalidRequestException,
    		APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	if(Utility.isEmpty(id)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_ID,id,null);
    	}
    	return APIProxy.request(APIProxy.RequestMethod.GET, APIProxy.formatURL(Order.class,this.getAddress(),"/" + id + Utility.buildSignString(this.getAddress(), this.getSecret())), null, Order.class);
    }    
    /**
     * Get all trust lines
     * @return TrustLineCollection
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws FailedException 
     */
    public TrustLineCollection getTrustLineList() throws AuthenticationException, InvalidRequestException, 
    APIConnectionException, APIException, ChannelException, FailedException{
    	try {
			return getTrustLineList(null,null);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		}
		return null;
    }
    /**
     * Get trustline based on currency/counterparty/limit
     * @param currency
     * @param counterparty
     * @return TrustLineCollection
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public TrustLineCollection getTrustLineList(String currency, String counterparty)throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	StringBuffer param = new StringBuffer();
    	if(Utility.isNotEmpty(currency)){
    		if(!Utility.isValidCurrency(currency)){
    			throw new InvalidParameterException(JingtumMessage.INVALID_CURRENCY,currency,null);
    		}else{
    			param.append("&");
    			param.append("currency=");
    			param.append(currency);
    		}
    	}
    	if(Utility.isNotEmpty(counterparty)){
    		if(!Utility.isValidAddress(counterparty)){
    			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,counterparty,null);
    		}else{
    			param.append("&");
    			param.append("counterparty=");
    			param.append(counterparty);
    		}
    	}
    	return APIProxy.request(APIProxy.RequestMethod.GET, APIProxy.formatURL(TrustLine.class,this.getAddress(),Utility.buildSignString(this.getAddress(), this.getSecret()) + param.toString()), null, Wallet.class).getTrustLinesCollection();
    }    
    /**
     * Add a new trust line
     * @param trustLine
     * @param validate
     * @return PostResult instance
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public RequestResult addTrustLine(TrustLine trustLine, boolean validate)throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	if(!Utility.isValidTrustline(trustLine)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_TRUST_LINE,null,null);
    	}
    	HashMap<String, String> trustline = new HashMap<String, String>();
    	trustline.put("limit", Utility.doubleToString(trustLine.getLimit()));
    	trustline.put("currency", trustLine.getCurrency());
    	trustline.put("counterparty", trustLine.getCounterparty());
    	
    	HashMap<String, Object> content = new HashMap<String, Object>();
    	content.put("secret", this.getSecret());
    	content.put("trustline", trustline);
    	
    	String params = APIProxy.GSON.toJson(content);
    	return APIProxy.request(APIProxy.RequestMethod.POST, APIProxy.formatURL(TrustLine.class,this.getAddress(),VALIDATED + Boolean.toString(validate)), params, RequestResult.class);
    } 
    /**
     * Add a trustline synchronously
     * @param trustLine
     * @return RequestResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
    public RequestResult syncAddTrustLine(TrustLine trustLine) 
    		throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	return addTrustLine(trustLine, true);
    }    
    /**
     * Add a trustline asynchronously
     * @param trustLine
     * @return RequestResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
    public RequestResult asyncAddTrustLine(TrustLine trustLine) 
    		throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	return addTrustLine(trustLine, false);
    }
    /**
     * Remove a trust line
     * @param trustLine
     * @param validate
     * @return RequestResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public RequestResult removeTrustLine(TrustLine trustLine, boolean validate)throws AuthenticationException, InvalidRequestException,
	APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	if(!Utility.isValidTrustline(trustLine)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_TRUST_LINE,null,null);
    	}
    	trustLine.setLimit(0);
		return addTrustLine(trustLine,validate);
    }
    /**
     * Remove a trustline synchronously
     * @param trustLine
     * @return RequestResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
    public RequestResult synRemoveTrustLine(TrustLine trustLine) 
    		throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	return removeTrustLine(trustLine, true);
    }
    /**
     * Remove a trustline asynchronously
     * @param trustLine
     * @return RequestResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
    public RequestResult asynRemoveTrustLine(TrustLine trustLine) 
    		throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	return removeTrustLine(trustLine, false);
    }
    /**
     * Get notification
     * @param ID
     * @return Notification instance
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public Notification getNotification(String ID)throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	
    	if(Utility.isEmpty(ID)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_ID,ID,null);
    	}
    	return APIProxy.request(APIProxy.RequestMethod.GET, APIProxy.formatURL(Notification.class,this.getAddress(),"/" + ID + Utility.buildSignString(this.getAddress(), this.getSecret())), null, Wallet.class).getMyNotification();
    } 
    
    /**
     * @return TransactionCollection
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws FailedException 
     */
    public TransactionCollection getTransactionList() throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException{
    	try {
			return getTransactionList(null,false,null,0,0);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		}
		return null;
    }
    /**
     * @param destinationAccount
     * @param excludeFailed
     * @param direction
     * @param resultPerPage
     * @param page
     * @return TransactionCollection
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public TransactionCollection getTransactionList(String destinationAccount, boolean excludeFailed, Transaction.DirectionType direction, int resultPerPage, int page)
    		throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	StringBuffer param = new StringBuffer();
    	
    	if(Utility.isNotEmpty(destinationAccount)){
    		if(!Utility.isValidAddress(destinationAccount)){
    			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,destinationAccount,null);
    		}else{
    			param.append("&");
    			param.append("destination_account=");
    			param.append(destinationAccount);
    		}
    	}
    	if(resultPerPage < 0 || page < 0){
    		throw new InvalidParameterException(JingtumMessage.INVALID_PAGE_INFO,destinationAccount,null);
    	}
		if(resultPerPage != 0){
			param.append("&");
			param.append("results_per_page=");
			param.append(resultPerPage);
		}
		if(page != 0){
			param.append("&");
			param.append("page=");
			param.append(page);
		}
		if(excludeFailed){
			param.append("&");
			param.append("exclude_failed=");
			param.append(excludeFailed);
		} 
		if(direction != null && direction != Transaction.DirectionType.all){
			param.append("&");
			param.append("direction=");
			param.append(direction);
		}

    	return APIProxy.request(APIProxy.RequestMethod.GET, APIProxy.formatURL(Transaction.class,this.getAddress(),Utility.buildSignString(this.getAddress(), this.getSecret()) + param.toString()), null, Wallet.class).getMyTransactionCollection();
    }    
    /**
     * Get transaction by hash number
     * @param id
     * @return Transaction instance
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public Transaction getTransaction(String id)throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	if(Utility.isEmpty(id)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_ID,id,null);
    	}
    	return APIProxy.request(APIProxy.RequestMethod.GET, APIProxy.formatURL(Transaction.class,this.getAddress(),"/" + id + Utility.buildSignString(this.getAddress(), this.getSecret())), null, Wallet.class).getTransaction();
    }
    /**
     * Add relation
     * @param type relation type
     * @param counterParty
     * @param amount optional, only apply when type is authorize
     * @param validate
     * @return PostResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public RequestResult addRelation(Relation.RelationType type, String counterParty, RelationAmount amount, boolean validate)throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	if(amount != null && amount.getCurrency().equals(Jingtum.getCurrencySWT())){
    		throw new InvalidParameterException(JingtumMessage.CURRENCY_OTHER_THAN_SWT + Jingtum.getCurrencySWT(),null,null);
    	}
    	if(!Utility.isValidAddress(counterParty)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,counterParty,null);
    	}
    	if(null == type || type.equals(Relation.RelationType.all)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_RELATION_TYPE,type.toString(),null);
    	}
    	if(!Utility.isValidRelationAmount(amount)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_AMOUNT,null,null);
    	}
    	HashMap<String, Object> content = new HashMap<String, Object>();
    	content.put("secret", this.getSecret());
    	content.put("type", type);
    	content.put("counterparty", counterParty);
    	content.put("amount", amount); 	
    	String params = APIProxy.GSON.toJson(content);
    	return APIProxy.request(APIProxy.RequestMethod.POST, APIProxy.formatURL(Relation.class,this.getAddress(),VALIDATED + Boolean.toString(validate)), params, RequestResult.class);
    } 
    /**
     * Add a relation synchronously
     * @param type relation type
     * @param counterParty
     * @param amount
     * @return RequestResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
    public RequestResult syncAddRelation(Relation.RelationType type, String counterParty, RelationAmount amount) 
    		throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	return addRelation(type, counterParty, amount, true);
    }
    /**
     * Add a relation asynchronously
     * @param type relation type
     * @param counterParty
     * @param amount
     * @return RequestResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
    public RequestResult asyncAddRelation(Relation.RelationType type, String counterParty, RelationAmount amount) 
    		throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	return addRelation(type, counterParty, amount, false);
    }
    /**
     * Delete relation
     * @param type relation type
     * @param counterParty
     * @param amount
     * @param validate
     * @return PostResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public RequestResult removeRelation(Relation.RelationType type, String counterParty, RelationAmount amount, boolean validate)throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	if(amount != null && amount.getCurrency().equals(Jingtum.getCurrencySWT())){
    		throw new InvalidParameterException(JingtumMessage.CURRENCY_OTHER_THAN_SWT + Jingtum.getCurrencySWT(),null,null);
    	}
    	if(!Utility.isValidAddress(counterParty)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,counterParty,null);
    	}
    	if(null == type || type.equals(Relation.RelationType.all )){
    		throw new InvalidParameterException(JingtumMessage.INVALID_RELATION_TYPE,type.toString(),null);
    	}
    	if(!Utility.isValidRelationAmount(amount)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_AMOUNT,null,null);
    	}
    	HashMap<String, Object> content = new HashMap<String, Object>();
    	content.put("secret", this.getSecret());
    	content.put("type", type);
    	content.put("counterparty", counterParty);
    	content.put("amount", amount);
    	String params = APIProxy.GSON.toJson(content);
    	return APIProxy.request(APIProxy.RequestMethod.DELETE, APIProxy.formatURL(Relation.class,this.getAddress(),VALIDATED + Boolean.toString(validate)), params, RequestResult.class);
    } 
    /**
     * Delete a relation synchronously
     * @param type relation type
     * @param counterParty
     * @param amount
     * @return RequestResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
    public RequestResult syncRemoveRelation(Relation.RelationType type, String counterParty, RelationAmount amount) 
    		throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	return removeRelation(type, counterParty, amount, true);
    }
    /**
     * Delete a relation asynchronously
     * @param type relation type
     * @param counterParty
     * @param amount
     * @return RequestResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
    public RequestResult asyncRemoveRelation(Relation.RelationType type, String counterParty, RelationAmount amount) 
    		throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	return removeRelation(type, counterParty, amount, false);
    }
    /**
     * Get all relations
     * @return RelationCollection
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws FailedException 
     */
    public RelationCollection getRelationList() throws AuthenticationException, InvalidRequestException, 
    	APIConnectionException, APIException, ChannelException, FailedException{
    	try {
			return getRelationList(Relation.RelationType.all,null,null,null);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		}
		return null;
    }
    /**
     * Get relations filter by relation type, counterparty, amount
     * @param type optional
     * @param counterparty optional
     * @param amount optional
     * @return RelationCollection
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public RelationCollection getRelationList(Relation.RelationType type, String counterparty, RelationAmount amount, String marker)
    		throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	StringBuffer param = new StringBuffer();
    	
    	if(Utility.isNotEmpty(counterparty)){
    		if(!Utility.isValidAddress(counterparty)){
    			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,counterparty,null);
    		}else{
     			param.append("&");
    			param.append("counterparty=");
    			param.append(counterparty);
    		}
    	}
    	if(type != null && type != Relation.RelationType.all){
			param.append("&");
			param.append("type=");
			param.append(type);
    	}
    	if(Utility.isNotEmpty(marker)){
 			param.append("&");
			param.append("marker=");
			param.append(marker);
    	}
    	if(amount != null){
    		if(Jingtum.getCurrencySWT().equals(amount.getCurrency())){
    			throw new InvalidParameterException(JingtumMessage.CURRENCY_OTHER_THAN_SWT + Jingtum.getCurrencySWT(),null,null);    	    	
    		}
    		if(!Utility.isValidRelationAmount(amount)){
    			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_AMOUNT,null,null);
    		}else{
    			param.append("&");
    			param.append("currency=");
    			param.append(amount.getCurrency());
    			param.append("+");
    			param.append(amount.getIssuer());
    		}
    	}
    	return APIProxy.request(APIProxy.RequestMethod.GET, APIProxy.formatURL(Relation.class,this.getAddress(),Utility.buildSignString(this.getAddress(), this.getSecret()) + param.toString()), null, Wallet.class).getMyRelations();
    } 
    /**
     * Get all counter party relations
     * @return RelationCollection
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws FailedException 
     */
    public RelationCollection getCoRelationList() throws AuthenticationException, 
    InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException{
    	try {
			return getCoRelationList(Relation.RelationType.all,null,null,null);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		}
		return null;
    }
    /**
     * Get relation collection using counter party as input, filtered by type, address, amount
     * @param type
     * @param address
     * @param amount
     * @return RelationCollection
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public RelationCollection getCoRelationList(Relation.RelationType type, String address, RelationAmount amount, String marker)
    		throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	StringBuffer param = new StringBuffer();
    	if(Utility.isNotEmpty(address)){
    		if(!Utility.isValidAddress(address)){
    			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,address,null);
    		}else{
     			param.append("&");
    			param.append("address=");
    			param.append(address);
    		}
    	}
    	if(null != type && type != Relation.RelationType.all){
			param.append("&");
			param.append("type=");
			param.append(type);
    	}
    	if(amount != null){
    		if(Jingtum.getCurrencySWT().equals(amount.getCurrency())){
    			throw new InvalidParameterException(JingtumMessage.CURRENCY_OTHER_THAN_SWT + Jingtum.getCurrencySWT(),null,null);    		    
    		}
    		if(!Utility.isValidRelationAmount(amount)){
    			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_AMOUNT,null,null);
    		}else{
    			param.append("&");
    			param.append("currency=");
    			param.append(amount.getCurrency());
    			param.append("+");
    			param.append(amount.getIssuer());
    		}
    	}
    	if(Utility.isNotEmpty(marker)){
			param.append("&");
			param.append("marker=");
			param.append(marker);
    	}

    	StringBuffer request = new StringBuffer();
    	request.append("counterparties/");
    	request.append(this.getAddress());
    	request.append("/relations");
    	request.append(Utility.buildSignString(this.getAddress(), this.getSecret()));
    	request.append(param.toString());
    	return APIProxy.request(APIProxy.RequestMethod.GET, APIProxy.formatURL(request.toString()), null, Wallet.class).getMyRelations();
    }    
    /**
     * Subscribe to web socket connection
     * @return true if successfully subscribed
     * @throws InvalidParameterException
     * @throws APIException
     */
    public boolean subscribe() throws InvalidParameterException, APIException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	if(null == JingtumAPIAndWSServer.getInstance().getJtWebSocket() || JingtumAPIAndWSServer.getInstance().getJtWebSocket().isConnected() == false){
    		throw new APIException(JingtumMessage.NO_CONNECTION_AVAIABLE,null);
    	}
    	return JingtumAPIAndWSServer.getInstance().getJtWebSocket().subscribe(this.getAddress(), this.getSecret());
    } 
    /**
     * Unsubscribe WebSocket connection
     * @return true if unsubscribe successfully
     * @throws APIException 
     */
    public boolean unsubscribe() throws APIException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	if(null == JingtumAPIAndWSServer.getInstance().getJtWebSocket() || JingtumAPIAndWSServer.getInstance().getJtWebSocket().isConnected() == false){
    		throw new APIException(JingtumMessage.NO_CONNECTION_AVAIABLE,null);
    	}
		return JingtumAPIAndWSServer.getInstance().getJtWebSocket().unsubscribe(this.getAddress());
    }
    /**
     * Get payment available payment path
     * @param receiver
     * @param amount
     * @return PaymentCollection
     * @throws InvalidParameterException
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws ChannelException
     * @throws APIException
     * @throws FailedException 
     */
    public PaymentCollection getPathList(String receiver, Amount amount) throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, ChannelException, APIException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}    	
    	if(!Utility.isValidAddress(receiver)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,receiver,null);
    	}
    	if(!Utility.isValidAmount(amount)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_AMOUNT,null,null);
    	}
    	if(Jingtum.getCurrencySWT().equals(amount.getCurrency())){
    		throw new InvalidParameterException(JingtumMessage.CURRENCY_OTHER_THAN_SWT + Jingtum.getCurrencySWT(),null,null);
    	}
    	StringBuilder sb = new StringBuilder();
    	sb.append("/paths/");
    	sb.append(receiver);
    	sb.append("/");
    	sb.append(amount.getValue());
    	sb.append("+");
    	sb.append(amount.getCurrency());
    	sb.append("+");
    	sb.append(amount.getCounterparty());
    	sb.append(Utility.buildSignString(this.getAddress(), this.getSecret()));
        System.out.println(sb);

    	return APIProxy.request(APIProxy.RequestMethod.GET, APIProxy.formatURL(Payment.class,this.getAddress(),sb.toString()), null, Wallet.class).getPaymentsCollection();
    }
}
