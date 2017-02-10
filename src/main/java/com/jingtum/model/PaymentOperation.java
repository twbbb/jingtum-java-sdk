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


/**
 * Created by zpli on 2/8/17.
 */
import com.jingtum.JingtumMessage;
import com.jingtum.exception.*;
import com.jingtum.model.Amount;
import com.jingtum.model.Wallet;
import com.jingtum.model.Payment;
import com.jingtum.model.OperationClass;
import com.jingtum.net.APIServer;
import com.jingtum.net.FinGate;
import com.jingtum.util.Utility;

import java.util.HashMap;


public class PaymentOperation extends OperationClass{

    //Amount used for the payment
    private String paths;
    private String dest_address;
    private Amount source_amount;
    private double source_slippage;
    private Amount destination_amount;

    private String client_resource_id;
    private String prefix;

    public PaymentOperation(Wallet src_wallet){
        //check if the wallet if an active one, this may delay the process of Operation
        //
        this.setSrcAddress(src_wallet.getAddress());
        this.setSrcSecret(src_wallet.getSecret());

        client_resource_id = "";

    }

    /* Get payment paths
	 * @return paths
	 */
    public String getPaths() {
        return paths;
    }

    /**
     * Get payment amount
     * @return amount
     */
    public Amount getAmount() {
        return source_amount;
    }
    /**
     * Get payment resource id
     * @return client_resource_id
     */
    public String getClientID() {
        return client_resource_id;
    }

    /**
     * Get payment resource id
     * @return client_resource_id
     */
    public void setClientID(String in_id){this.client_resource_id = in_id;};
    public void setPrefix(String in_id){this.prefix = in_id;};

    public void setClientId(String id){};

    public void setAmount(Amount in_amt)throws InvalidParameterException{
        if(!Utility.isValidAmount(in_amt)){
            throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_AMOUNT,null,null);
        }
        this.source_amount = in_amt;
    };

    public void setDestAddress(String in_address){dest_address = in_address;};

    /**
     * Submit a payment
     * @return PostResult instance
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException
     */
    public RequestResult submit()
            throws AuthenticationException, InvalidRequestException,
            APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{

//        try{
//            if(!isActivated()){
//                throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
//            }
//        }catch(InvalidRequestException e){
//            throw new APIException(JingtumMessage.INACTIVATED_ACCOUNT,null);
//        }

        if(!Utility.isValidAddress(this.dest_address)){
            throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,this.dest_address,null);
        }
        if(!Utility.isValidAmount(this.source_amount)){
            throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_AMOUNT,null,null);
        }
        if(this.source_amount.getValue() <= 0){
            throw new InvalidParameterException(JingtumMessage.INVALID_VALUE,String.valueOf(source_amount.getValue()),null);
        }

        if(Utility.isEmpty(this.client_resource_id)){ // if uid is null

            //Generate an uid if the user didn't set it.
            this.client_resource_id = "paymentid"+Long.toString(System.currentTimeMillis() ); //获得唯一单号payment_id// generate a resouce ID

        }

        HashMap<String, String> destination_amount = new HashMap<String, String>();
        destination_amount.put("currency", source_amount.getCurrency());
        destination_amount.put("value", Utility.doubleToString(source_amount.getValue()));
        destination_amount.put("issuer",source_amount.getCounterparty());

        HashMap<String, Object> payment = new HashMap<String, Object>();
        payment.put("source_account", this.getSrcAddress());
        payment.put("destination_account", this.dest_address);
        payment.put("destination_amount", destination_amount);

        HashMap<String, Object> content = new HashMap<String, Object>();
        content.put("secret", this.getSrcSecret());
        content.put("client_resource_id", this.client_resource_id);
        content.put("payment", payment);

        String params = APIServer.GSON.toJson(content);
        String url = APIServer.formatURL(Payment.class, this.dest_address, VALIDATED + Boolean.toString(this.validate));
        System.out.println("Payment URL:" + url);

        return APIServer.request(APIServer.RequestMethod.POST, url, params, RequestResult.class);
    }
    
    public void submit(PaymentListener listener)throws AuthenticationException, InvalidRequestException,
    			APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
    	RequestResult payment01 = this.submit();
		System.out.println("result01:" + payment01.toString());
		if(payment01.getSuccess()){
			try {
				Thread.sleep(5000);
				RequestResult payment02 = this.queryResult();
				System.out.println("result02:" + payment01.toString());
				listener.onComplete(payment02);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{
			listener.onComplete(payment01);
		}
    }
    
    public RequestResult queryResult()
            throws AuthenticationException, InvalidRequestException,
            APIConnectionException, APIException, ChannelException, InvalidParameterException, FailedException{
        if(Utility.isEmpty(this.client_resource_id)){
            throw new InvalidParameterException(JingtumMessage.ERROR_CLIENT_ID,this.client_resource_id,null);
        }
        String url = APIServer.formatURL(Payment.class, this.dest_address, "/" + this.client_resource_id);
        System.out.println("Payment URL:" + url);

        return APIServer.request(APIServer.RequestMethod.GET, url, null, RequestResult.class);
    }
    
    public interface PaymentListener {
    	public void onComplete(RequestResult result);
    }
}
