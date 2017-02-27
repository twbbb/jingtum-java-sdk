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


import com.jingtum.JingtumMessage;
import com.jingtum.exception.*;
import com.jingtum.net.APIServer;
import com.jingtum.util.Utility;

import java.util.HashMap;

/**
 * Created by zpli on 2/8/17.
 * Send out the message operation
 *
 */


public class MessageOperation extends OperationClass{
    //destination address
    private String destination_account;

    //message to send out
    private String message_hash;


    public MessageOperation(Wallet src_wallet){
        //check if the wallet if an active one, this may delay the process of Operation
        //
        this.setSrcAddress(src_wallet.getAddress());
        this.setSrcSecret(src_wallet.getSecret());
        //set default mode to syn
        this.validate = true;
    }

    /**
     * Set the destination address to send the message
     *
     * @param in_address Input string to set the Order type
     *               must be either sell or buy
     *
     */
    public void setDestAddress(String in_address)throws InvalidParameterException {
        if (Utility.isValidAddress(in_address)){

            destination_account = in_address;
        }else
            throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,in_address,null);

    }

    /**
     * Set the destination address to send the message
     *
     * @param in_str Input string to set the Order type
     *               must be either sell or buy
     *
     */
    public void setMessage(String in_str)throws InvalidParameterException {
        if (in_str.length() > 0){

            message_hash = in_str;
        }else
            throw new InvalidParameterException("Empty message!",null,null);

    }


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
        //build the data to submit
        HashMap<String, Object> content = new HashMap<String, Object>();
        content.put("secret", this.getSrcSecret());
        content.put("destination_account", destination_account);
        content.put("message_hash", message_hash);

        String params = APIServer.GSON.toJson(content);
        String url = APIServer.formatURL(Message.class, this.getSrcAddress(), VALIDATED + Boolean.toString(this.validate));

        System.out.println("data: " + params);

        return APIServer.request(APIServer.RequestMethod.POST, url, params, RequestResult.class);
    }
}
