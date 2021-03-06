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
import com.jingtum.net.APIServer;
import com.jingtum.net.JingtumAPIAndWSServer;
import com.jingtum.net.RequestListener;
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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.jingtum.core.utils.HashUtils;//for HASH function


/**
 * @author zli
 * @version 1.2
 * Wallet class, main entry point
 * Removed
 */
public class Wallet extends AccountClass {
	@Expose
	private boolean success; //Operation success or not
	@Expose
	private long ledger;
	@Expose
	private boolean validated;  
	@Expose
	private BalanceCollection balances;
	@Expose
	private Message message;
	@Expose
	private PaymentChoiceCollection payment_choices;
	@Expose
	private PaymentCollection payments;
	@Expose
	private OrderCollection orders;
	@Expose
	private SettingsCollection settings_list;
	@Expose
	private Settings settings;
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
				//Notice that this amount may be diff from FinGate
				//since user can set the FinGate one.
				if(bl != null && bl.getValue() >= this.MIN_ACTIVATED_AMOUNT){
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
	 * Get private SettingsCollection instance
	 * @return settings
	 */
	private Message getMyMessage(){
		return message;
	}

	/**
	 * Get private RelationCollection instance
	 * @return relations
	 */
	private Settings getMySettings(){
		return settings;
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
	public Wallet (String secret, String address) throws InvalidParameterException{
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
	 * without any parameters, this returns all the balances
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

		return APIServer.request(
				APIServer.RequestMethod.GET,
				APIServer.formatURL(
				        Balance.class,
                        this.getAddress()),
				null,
				Wallet.class).getBalances();
    }
    /**
     * Get balance filtered by currency/counterparty
     * @param currency
     * @param issuer
     * @return BalanceCollection instance
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public BalanceCollection getBalance(String currency, String issuer)
            throws AuthenticationException, InvalidRequestException,
            APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException {
    	StringBuilder sb = new StringBuilder();
    	//check if currency is valid
    	if(Utility.isNotEmpty(currency)){ 
    		if(!Utility.isValidCurrency(currency)){
    			throw new InvalidParameterException(JingtumMessage.INVALID_CURRENCY,currency,null);
    		}else{
    	    	sb.append("?currency=");
    	    	sb.append(currency);
    		}
    	}
    	//check counterparty is valid
    	if(Utility.isNotEmpty(issuer)){
    		if(!Utility.isValidAddress(issuer)){
    			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,issuer,null);
    		}else{
//if only one parameter is available, use ?
				//otherwise use & to seperate the parameters.
    			if (sb.length() > 0)
    	    		sb.append("&counterparty=");
    			else
					sb.append("?counterparty=");
    	    	sb.append(issuer);
    		}
    	}
		return APIServer.request(
				APIServer.RequestMethod.GET,
				APIServer.formatURL(
                        Balance.class,
                        this.getAddress(),
                        sb.toString()),
                null,
                Wallet.class).getBalances();
    }

	/**
	 * Get order by ID
	 * @param payment_id
	 * @return Order instance
	 * @throws AuthenticationException
	 * @throws InvalidRequestException
	 * @throws APIConnectionException
	 * @throws APIException
	 * @throws ChannelException
	 * @throws InvalidParameterException
	 * @throws FailedException
	 */
	public Payment getPayment(String payment_id)throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
		try{
			if(!isActivated()){
				throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
			}
		}catch(InvalidRequestException e){
			throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
		}
		if(Utility.isEmpty(payment_id)){
			throw new InvalidParameterException(JingtumMessage.INVALID_ID,payment_id,null);
		}

		return APIServer.request(
				APIServer.RequestMethod.GET,
				APIServer.formatURL(
						Payment.class,
						this.getAddress(),"/" + payment_id),
				null,
				Payment.class);
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
    	Options ops = new Options();
    	try {
			return getPaymentList(ops);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		}
		return null;
    }
    public void getPaymentList(RequestListener<PaymentCollection> listener) throws AuthenticationException, InvalidRequestException,
    APIConnectionException, APIException, ChannelException, FailedException{
    	Options ops = new Options();
    	try {
			getPaymentList(ops, listener);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		}
    }
    /**
     * @param in_ops: contains options with sourceAccount, destinationAccount
     * @return PaymentCollection
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException 
     */
	//public PaymentCollection getPaymentList(String sourceAccount, String destinationAccount, boolean excludeFailed, Payment.Direction direction, int resultPerPage, int page)
	public PaymentCollection getPaymentList(Options in_ops)
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
    	//Note, the 1st option needs to be "?", not "&"
		int op_num = 0;

    	if(Utility.isNotEmpty(in_ops.getSourceAccount())){
    		if(!Utility.isValidAddress(in_ops.getSourceAccount())){
    			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,in_ops.getSourceAccount(),null);
    		}else{
    			if(op_num > 0)
    			    param.append("&");
    			else
    				param.append("?");
				op_num ++;
    			param.append("source_account=");
    			param.append(in_ops.getSourceAccount());

    		}
    	}
       	if(Utility.isNotEmpty(in_ops.getDestinationAccount())){
    		if(!Utility.isValidAddress(in_ops.getDestinationAccount())){
    			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,in_ops.getDestinationAccount(),null);
    		}else{
				if(op_num > 0)
					param.append("&");
				else
					param.append("?");
				op_num ++;
    			param.append("destination_account=");
    			param.append(in_ops.getDestinationAccount());
    		}
    	}
       	if(in_ops.getExcludeFailed()){
			if(op_num > 0)
				param.append("&");
			else
				param.append("?");
			op_num ++;
       		param.append("exclude_failed=");
       		param.append(in_ops.getExcludeFailed());
       	}

       	if(in_ops.getResultsPerPage() < 0){
       		throw new InvalidParameterException(JingtumMessage.INVALID_PAGE_INFO,String.valueOf(in_ops.getResultsPerPage()),null);
       	}
       	if(in_ops.getResultsPerPage() > 0){
			if(op_num > 0)
				param.append("&");
			else
				param.append("?");
			op_num ++;
       		param.append("results_per_page=");
       		param.append(in_ops.getResultsPerPage());
       	}
       	if(in_ops.getPage() < 0){
       		throw new InvalidParameterException(JingtumMessage.INVALID_PAGE_INFO,String.valueOf(in_ops.getPage()),null);
       	}
       	if(in_ops.getPage() > 0){
			if(op_num > 0)
				param.append("&");
			else
				param.append("?");
			op_num ++;
       		param.append("page=");
       		param.append(in_ops.getPage());
       	}

		return APIServer.request(
				APIServer.RequestMethod.GET,
				APIServer.formatURL(
						Payment.class,
						this.getAddress(),param.toString()),
				null,
				Wallet.class).getPaymentsCollection();

    }    
	
	public void getPaymentList(Options in_ops, RequestListener<PaymentCollection> listener)
			throws AuthenticationException, InvalidRequestException,
    		APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException {
		Utility.callback(new PaymentRunnable(this, in_ops, listener));
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

		return APIServer.request(
				APIServer.RequestMethod.GET,
				APIServer.formatURL(
						Order.class,
						this.getAddress()),
				null,
				Wallet.class).getOrdersCollection();

    }    
    /**
     * Get order by ID
     * @param hash_id
     * @return Order instance
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public Order getOrder(String hash_id)throws AuthenticationException, InvalidRequestException,
    		APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	try{
        	if(!isActivated()){
        		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
        	}     		
    	}catch(InvalidRequestException e){
    		throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
    	}
    	if(Utility.isEmpty(hash_id)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_ID,hash_id,null);
    	}

		return APIServer.request(
				APIServer.RequestMethod.GET,
				APIServer.formatURL(
						Order.class,
						this.getAddress(),"/" + hash_id),
				null,
				Order.class);
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

		return APIServer.request(
				APIServer.RequestMethod.GET,
				APIServer.formatURL(
						TrustLine.class,
						this.getAddress()),
				null,
				Wallet.class).getTrustLinesCollection();

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
	//public TransactionCollection getTransactionList(Options in_ops)
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

    	//Option number setup in the list
    	int op_num = 0;

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

			if(op_num > 0)
				param.append("&");
			else
				param.append("?");
			op_num ++;
			param.append("results_per_page=");
			param.append(resultPerPage);
		}
		if(page != 0){
			if(op_num > 0)
				param.append("&");
			else
				param.append("?");
			op_num ++;
			param.append("page=");
			param.append(page);
		}
		if(excludeFailed){
			if(op_num > 0)
				param.append("&");
			else
				param.append("?");
			op_num ++;
			param.append("exclude_failed=");
			param.append(excludeFailed);
		} 
		if(direction != null && direction != Transaction.DirectionType.all){
			if(op_num > 0)
				param.append("&");
			else
				param.append("?");
			op_num ++;
			param.append("direction=");
			param.append(direction);
		}

		return APIServer.request(
				APIServer.RequestMethod.GET,
				APIServer.formatURL(
						Transaction.class,
						this.getAddress(),
				param.toString()),
				null,
				Wallet.class).getMyTransactionCollection();

    }

	/**
	 * @param in_ops input options to filter out the outputs
	 * @return TransactionCollection trans records with the acount with filtered conditions
	 * @throws AuthenticationException
	 * @throws InvalidRequestException
	 * @throws APIConnectionException
	 * @throws APIException
	 * @throws ChannelException
	 * @throws FailedException
	 */
	public TransactionCollection getTransactionList(Options in_ops)
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

		if(Utility.isNotEmpty(in_ops.getSourceAccount())){
			if(!Utility.isValidAddress(in_ops.getSourceAccount())){
				throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,in_ops.getSourceAccount(),null);
			}else{

				param.append("&");
				param.append("source_account=");
				param.append(in_ops.getSourceAccount());
			}
		}
		if(Utility.isNotEmpty(in_ops.getDestinationAccount())){
			if(!Utility.isValidAddress(in_ops.getDestinationAccount())){
				throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,in_ops.getDestinationAccount(),null);
			}else{
				param.append("&");
				param.append("destination_account=");
				param.append(in_ops.getDestinationAccount());
			}
		}
		if(in_ops.getExcludeFailed()){
			param.append("&");
			param.append("exclude_failed=");
			param.append(in_ops.getExcludeFailed());
		}

		if(in_ops.getResultsPerPage() < 0){
			throw new InvalidParameterException(JingtumMessage.INVALID_PAGE_INFO,String.valueOf(in_ops.getResultsPerPage()),null);
		}
		if(in_ops.getResultsPerPage() > 0){
			param.append("&");
			param.append("results_per_page=");
			param.append(in_ops.getResultsPerPage());
		}
		if(in_ops.getPage() < 0){
			throw new InvalidParameterException(JingtumMessage.INVALID_PAGE_INFO,String.valueOf(in_ops.getPage()),null);
		}
		if(in_ops.getPage() > 0){
			param.append("&");
			param.append("page=");
			param.append(in_ops.getPage());
		}

		return APIServer.request(
				APIServer.RequestMethod.GET,
				APIServer.formatURL(
						Transaction.class,
						this.getAddress(),
						param.toString()),
				null,
				Wallet.class).getMyTransactionCollection();
	}
	public void getTransactionList(Options in_ops, RequestListener<TransactionCollection> listener)
			throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
		Utility.callback(new TransactionRunnable(this, in_ops, listener));
	}

	/**
	 * Get message by hash number
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
	public Message getMessage(String id)throws AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException {
	   Transaction ts = getTransaction(id);


		return ts.getMessage();
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

		return APIServer.request(
				APIServer.RequestMethod.GET,
				APIServer.formatURL(
						Transaction.class,
						this.getAddress(),
						"/" + id),
				null,
				Wallet.class).getTransaction();

//    	return APIProxy.request(
//    	        APIProxy.RequestMethod.GET,
//                APIProxy.formatURL(
//                        Transaction.class,
//                        this.getAddress(),
//                        "/" + id + Utility.buildSignString(this.getAddress(), this.getSecret())),
//                null,
//                Wallet.class).getTransaction();
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
    public RelationCollection getRelation() throws AuthenticationException, InvalidRequestException,
    	APIConnectionException, APIException, ChannelException, FailedException{
    	try {
			return getRelation(Relation.RelationType.authorize,null,null,null);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		}
		return null;
    }

	/**
	 * Get relations fits the input filter
	 * Input could be one of the followings
	 * 1. authorize, friend: relation type
	 * 2.
	 * @return RelationCollection
	 * @throws AuthenticationException
	 * @throws InvalidRequestException
	 * @throws APIConnectionException
	 * @throws APIException
	 * @throws ChannelException
	 * @throws FailedException
	 */
	public RelationCollection getRelation(String in_str) throws AuthenticationException, InvalidRequestException,
			InvalidParameterException, APIConnectionException, APIException, ChannelException, FailedException{

		//relation filter check
		if(in_str.compareToIgnoreCase("authorize") == 0){
			try {
				return getRelation(Relation.RelationType.authorize,null,null,null);
			} catch (InvalidParameterException e) {
				e.printStackTrace();
			}
		}else if(Utility.isValidAddress(in_str)){
			//Counter party
			try {
				return getRelation(Relation.RelationType.authorize,in_str,null,null);
			} catch (InvalidParameterException e) {
				e.printStackTrace();
			}
		}else if(Utility.isValidTumString(in_str)) {
			//Tum pair
			try {
				return getRelation(Relation.RelationType.authorize, null, in_str, null);
			} catch (InvalidParameterException e) {
				e.printStackTrace();
			}
		}else
			throw new InvalidParameterException("Invalid getRelation condition!",in_str,null);
		return null;
	}

    /**
     * Get relations filter by relation type, counterparty, amount
     * @param type optional
     * @param counterparty optional
     * @param tum_string optional
     * @return RelationCollection
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException 
     * @throws FailedException 
     */
    public RelationCollection getRelation(Relation.RelationType type, String counterparty, String tum_string, String marker)
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

		//if(type != null && type != Relation.RelationType.all){
//Note: the current REST-API not allow no type
		if (type == null)
			type = Relation.RelationType.authorize;
		//param.append("&");
		param.append("type=");
		param.append(type);
		//}

    	if(Utility.isNotEmpty(counterparty)){
    		if(!Utility.isValidAddress(counterparty)){
    			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,counterparty,null);
    		}else{
     			param.append("&");
    			param.append("counterparty=");
    			param.append(counterparty);
    		}
    	}

    	if(Utility.isNotEmpty(marker)){
 			param.append("&");
			param.append("marker=");
			param.append(marker);
    	}
    	if(tum_string != null){

    		//The filter cannot used for SWT
    		if(Jingtum.getCurrencySWT().equals(tum_string)){
    			throw new InvalidParameterException(JingtumMessage.CURRENCY_OTHER_THAN_SWT + Jingtum.getCurrencySWT(),null,null);    	    	
    		}
    		else {
				String[] tmp_str = tum_string.split(":");
				//System.out.println("INput "+tum_string);
				if (tmp_str.length == 2) {

					param.append("&");
					param.append("currency=");
					param.append(tmp_str[0].toString());//amount.getCurrency());
					param.append("%2B");//this is the "+"
					param.append(tmp_str[1].toString());//amount.getIssuer());
				}
				else
					throw new InvalidParameterException("Error in Relation currency info!",null,null);
			}
    	}

		return APIServer.request(
				APIServer.RequestMethod.GET,
				APIServer.formatURL(
						Relation.class,
						this.getAddress(),
						"?"+param.toString()),
				null,
				Wallet.class).getMyRelations();

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
    public Settings getSettings() throws AuthenticationException,
    InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException{

		try{
			if(!isActivated()){
				throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
			}
		}catch(InvalidRequestException e){
			throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
		}

    	StringBuffer request = new StringBuffer();


		return APIServer.request(
				APIServer.RequestMethod.GET,
				APIServer.formatURL(
						Settings.class,
						this.getAddress()),
				null,
				Wallet.class).getMySettings();

    }
	/**
	 * Get disable_master flag.
	 * This flag only to be true if
	 * Regular Key is set
	 * @return disable_master
	 */
	public boolean getDisableMaster() {
		return settings.getDisableMaster();//disable_master;
	}


	/**
	 * Get disallow_swt flag
	 * @return disallow_swt
	 */
	public boolean getDisallowSwt(){return settings.getDisallowSwt();}//disallow_swt;}

	/**
	 * Get the domain string
	 *
	 * @return domain
	 */
	public String getDomain(){return settings.getDomain();}//domain;}

	/**
	 * Get the email_hash string
	 *
	 * @return email_hash
	 */
	public String getEmail(){return settings.getEmail();}//email_hash;}

	/**
	 * Get the message_key string
	 *
	 * @return message_key
	 */
	public String getMessageKey(){return settings.getMessageKey();}//message_key;}

	/**
	 * Get the nickname string
	 *
	 * @return nickname
	 */
	public String getNickname(){return settings.getNickname();}//nickname;}

	/**
	 * Get global_freeze flag
	 *
	 * @return global_freeze
	 */
	public boolean getGlobalFreeze(){return settings.getGlobalFreeze();}//global_freeze;}
	/**
	 * Get disallow_swt flag
	 * @return disallow_swt
	 */
	public boolean getNoFreeze(){return settings.getNoFreeze();}//no_freeze;}

	/**
	 * Get require_authorization flag
	 * @return require_authorization
	 */
	public boolean getRequireAuthorization(){return settings.getRequireAuthorization();}//require_authorization;}
	/**
	 * Get require_destination_tag flag
	 * @return require_destination_tag
	 */
	public boolean getRequireDestinationTag(){return settings.getRequireDestinationTag();}//require_destination_tag;}
	/**
	 * Get the transfer_rate value
	 * transfer_rate should larger than or equal 1.0
	 * @return transfer_rate
	 */
	public String getTransferRate(){return settings.getTransferRate();}//transfer_rate;}

	/**
	 * Get the 钱包定位器
	 *
	 * @return wallet_locator
	 */
	public String getWalletLocator(){return settings.getWalletLocator();}//wallet_locator;}

	/**
	 * Get the transfer_rate value
	 * transfer_rate should larger or equal 1.0
	 * @return transfer_rate
	 */
	public String getWalletSize(){return settings.getWalletSize();}


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
    			param.append("%2B");
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

		return APIServer.request(
				APIServer.RequestMethod.GET,
				APIServer.formatURL(request.toString()),
				null,
				Wallet.class).getMyRelations();

    //	return APIProxy.request(APIProxy.RequestMethod.GET, APIProxy.formatURL(request.toString()), null, Wallet.class).getMyRelations();
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
    	sb.append(String.valueOf(amount.getValue()));
		//sb.append(amount.getValue());
    	sb.append("+" + amount.getCurrency());
    	sb.append("+" + amount.getIssuer());
        String urlsb = new String();
    	try {
            urlsb = URLEncoder.encode(sb.toString(), "UTF-8");
        }catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            System.out.println("UTF-8 not supported");
        }

		return APIServer.request(
				APIServer.RequestMethod.GET,
				APIServer.formatURL(
						Payment.class,
						this.getAddress(),
                        urlsb),
				null,
				Wallet.class).getPaymentsCollection();

    }

    /**
     * Form the choices array with input path list
     * @param in_path_list
     */
	public PaymentChoiceCollection getChoicesFromPathList(PaymentCollection in_path_list)
			throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException,
			ChannelException, APIException, FailedException{
		// create an empty array list with an initial capacity
		PaymentChoice choice = new PaymentChoice();
		List<PaymentChoice> choice_list = new ArrayList<PaymentChoice>();

		if(this.payment_choices == null)
			this.payment_choices = new PaymentChoiceCollection();

		int path_num = in_path_list.getData().size();
		//Conver the input path to the
		for (int i = 0; i < path_num; i ++) {
			String cur_path = in_path_list.getData().get(i).getPaths().toString();

			choice.setChoice(in_path_list.getData().get(i).getSourceAmount());
			choice.setPath(cur_path);
			HashUtils path_hash = new HashUtils();
			String key = path_hash.SHA256_RIPEMD160(cur_path.getBytes()).toString();

			choice.setKey(key);
			choice_list.add(choice);
		}

        payment_choices.setData(choice_list);

        return payment_choices;
	}

 	/**
     * Get payment choice collection using receive and amount
     * @param receiver
     * @param amount
     */
	public PaymentChoiceCollection getChoices(String receiver, Amount amount) throws InvalidParameterException,
			AuthenticationException, InvalidRequestException, APIConnectionException,
			ChannelException, APIException, FailedException{

		return getChoicesFromPathList(getPathList(receiver,amount));

	}

    private class PaymentRunnable implements Runnable {
    	private Wallet wallet;
    	private Options option;
    	private RequestListener<PaymentCollection> listener;
    	
    	private PaymentRunnable(Wallet wallet, Options option, RequestListener<PaymentCollection> listener) {
    		this.wallet = wallet;
    		this.option = option;
    		this.listener = listener;
		}
    	
    	public void run() {
    		try{
	    		PaymentCollection list = this.wallet.getPaymentList(this.option);
	    		this.listener.onComplete(list);
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    }
    
    private class TransactionRunnable implements Runnable {
    	private Wallet wallet;
    	private Options option;
    	private RequestListener<TransactionCollection> listener;
    	
    	private TransactionRunnable(Wallet wallet, Options option, RequestListener<TransactionCollection> listener) {
    		this.wallet = wallet;
    		this.option = option;
    		this.listener = listener;
		}
    	
    	public void run() {
    		try{
	    		TransactionCollection list = this.wallet.getTransactionList(this.option);
	    		this.listener.onComplete(list);
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    }
}
