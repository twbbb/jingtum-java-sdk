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


import com.google.gson.annotations.Expose;
import com.jingtum.JingtumMessage;
import com.jingtum.exception.*;
import com.jingtum.net.APIServer;
import com.jingtum.util.Utility;

import java.util.HashMap;

/**
 * Created by zpli on 2/18/17.
 * Create a relation between the two accounts
 *
 */


public class RemoveRelationOperation extends OperationClass{
    //Amount used for the submit order
    @Expose
    private String dest_address;
    @Expose
    private Amount amount_limit ;
    @Expose
    private String type;



    public RemoveRelationOperation(Wallet src_wallet){
        //check if the wallet if an active one, this may delay the process of Operation
        //
        this.setSrcAddress(src_wallet.getAddress());
        this.setSrcSecret(src_wallet.getSecret());
        //set default mode to syn
        this.validate = true;
    }

    /**
     * set disable_master flag.
     * This flag only to be true if
     * Regular Key is set
     * @param in_var
     */
    public void setType(String in_var)throws InvalidParameterException{
        //check to make sure input is a valid type
        if(in_var.compareTo("authorize") == 0)
            type = in_var;
        else
            throw new InvalidParameterException("Invalid relation type",null,null);
    };


    /**
     * set the counterparty address
     * @param in_address
     */
    public void setCounterparty(String in_address)throws InvalidParameterException{

        if(Utility.isValidAddress(in_address))
            dest_address = in_address;
        else
            throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,null,null);
    };

    /**
     * set the counterparty amount
     *
     * @param in_amt The amount of the relations 
     */
    public void setAmount(Amount in_amt)throws InvalidParameterException{
        if(!Utility.isValidAmount(in_amt)){
            throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_AMOUNT,null,null);
        }
        this.amount_limit = in_amt;
    };

    /**
     * Submit the order with the info
     *
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


        //Set the source_amount and destination amount with pair, price and amount value
        HashMap<String, String> param1 = new HashMap<String, String>();

        param1.put("limit", String.valueOf(amount_limit.getValue()));
        param1.put("currency", amount_limit.getCurrency());
        param1.put("issuer", amount_limit.getIssuer());


        HashMap<String, Object> content = new HashMap<String, Object>();
        content.put("secret", this.getSrcSecret());
        content.put("counterparty", dest_address);
        content.put("type", type);
        content.put("amount", param1);

        String params = APIServer.GSON.toJson(content);
        String url = APIServer.formatURL(Relation.class, this.getSrcAddress(), VALIDATED + Boolean.toString(this.validate));

        return APIServer.request(APIServer.RequestMethod.DELETE, url, params, RequestResult.class);
    }
}
