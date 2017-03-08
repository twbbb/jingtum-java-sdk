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
 * Created by zpli on 2/8/17.
 */


public class SettingsOperation extends OperationClass{
    //Amount used for the submit order
    @Expose
    private String issuer;
    @Expose
    private boolean disable_master;
    @Expose
    private boolean disallow_swt;
    @Expose
    private String domain ;
    @Expose
    private String email_hash;
    @Expose
    private boolean global_freeze;
    @Expose
    private String message_key;
    @Expose
    private boolean no_freeze;  //read only
    //@Expose
    //private boolean password_spent;
    @Expose
    private boolean require_authorization;
    @Expose
    private boolean require_destination_tag;
    @Expose
    private String regular_key;
    @Expose
    private String transfer_rate ;
    @Expose
    private String wallet_locator;
    @Expose
    private String wallet_size;
    @Expose
    private String nickname;

    public SettingsOperation(Wallet src_wallet){
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
    public void setDisableMaster(boolean in_var) {
        disable_master =  in_var;
    }


    /**
     * set disallow_swt flag
     * @param in_var
     */
    public void setDisallowSwt(boolean in_var){disallow_swt = in_var;}

    /**
     * set the domain string
     *
     * @param in_str
     */
    public void setDomain(String in_str){ domain = in_str;}

    /**
     * set the email_hash string
     *
     * @param in_str
     */
    public void setEmail(String in_str){ email_hash = in_str;}

    /**
     * set the message_key string
     *
     * @param in_str
     */
    public void setMessageKey(String in_str){ message_key = in_str;}

    /**
     * set the nickname string
     *
     * @param in_str
     */
    public void setNickname(String in_str){ nickname = in_str;}

    /**
     * set require_authorization flag
     * @param in_var
     */
    public void setRequireAuthorization(boolean in_var){ require_authorization = in_var;}

    /**
     * set require_destination_tag flag
     * @param in_var
     */
    public void setRequireDestinationTag(boolean in_var){ require_destination_tag = in_var;}
    /**
     * set the transfer_rate value
     * transfer_rate should be no less than 1.0
     * @param in_val
     */
    public void setTransferRate(double in_val)throws InvalidParameterException{
        if (in_val >= 1.0 )
            transfer_rate = Utility.doubleToString(in_val);
        else
            throw new InvalidParameterException("Transfer rate should >= 1.0",null,null);
    }

    /**
     * set the 钱包定位器
     *
     * @param in_str
     */
    public void setWalletLocator(String in_str){
        wallet_locator = in_str;
    }

    /**
     * set the wallet_size value
     *
     * @param in_val
     */
    public void setWalletSize(int in_val)throws InvalidParameterException{
        if( in_val > 0 )
            wallet_size = in_val + "";
        else
            throw new InvalidParameterException("Transfer rate should >= 1.0",null,null);
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


        //Set the source_amount and destination amount with pair, price and amount value
        HashMap<String, String> settings_data = new HashMap<String, String>();

        settings_data.put("domain", domain);
        settings_data.put("transfer_rate", transfer_rate);


        HashMap<String, Object> content = new HashMap<String, Object>();
        content.put("secret", this.getSrcSecret());
        content.put("settings", settings_data);

        String params = APIServer.GSON.toJson(content);
        String url = APIServer.formatURL(Settings.class, this.getSrcAddress(), VALIDATED + Boolean.toString(this.validate));

        return APIServer.request(APIServer.RequestMethod.POST, url, params, RequestResult.class);
    }
}
