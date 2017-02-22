package com.jingtum.net;
/*
 *
 *  * Copyright www.jingtum.com Inc.
 *  *
 *  * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */


        import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.HashMap;

import com.jingtum.Jingtum;
import com.jingtum.JingtumMessage;
import com.jingtum.core.crypto.ecdsa.Seed;
import com.jingtum.exception.APIConnectionException;
import com.jingtum.exception.APIException;
import com.jingtum.exception.AuthenticationException;
import com.jingtum.exception.ChannelException;
import com.jingtum.exception.FailedException;
import com.jingtum.exception.InvalidParameterException;
import com.jingtum.exception.InvalidRequestException;
import com.jingtum.exception.JingtumException;
        import com.jingtum.model.*;
        import com.jingtum.util.Config;
import com.jingtum.util.Utility;

/**
 * Created by yifan on 11/15/16.
 * Modified by zpli on 2017/01/28
 * Added the setMode to replace setTest
 * Mode
 * 0 - production mode, default
 * 1 - development mode
 * others - reserved for future usage
 */
public class FinGate extends AccountClass {


    private static final String PROPERTY_FILE = "src/main/java/com/jingtum/conf/prod.config.yaml"; // property file for production
    private static final String DEV_PROPERTY_FILE = "src/main/java/com/jingtum/conf/dev.config.yaml"; // property file for development

    private static final String ISSUE_CURRENCY = "/currency/issue";
    private static final String QUERY_ISSUE = "/currency/queryIssue";
    private static final String CURRENCY_STATUS = "/currency/status";

    private double activateAmount; // default amount of SWT to activate wallet, this may be larger than the MIN_ACTIVATED_AMOUNT
    //private double trustLimit;
    private double pathRate;

    private String token;
    private String signKey;

    //A wallet object to active new wallets
    private Wallet wallet;

    //Server classes
    private APIServer api_server = null;
    private TumServer tum_server = null;
    
    private static FinGate instance = null;

    public static int DEVELOPMENT = 1;
    public static int PRODUCTION = 0;
    /**
     * Singleton mode
     *
     * @return FinGate instance
     */
    public static final FinGate getInstance() {

        //default is production  mode 0
        if (instance == null) {
            instance = new FinGate(0);
        }
        return instance;
    }


    /**
     * Read yaml config file and initialize FinGate class
     * with information from config files.
     */
    private FinGate(int in_mode) {
        init(in_mode);
    }

    /**
     * Read yaml config file
     *
     */
    private void init(int in_mode) {
        String configFile = null;
        try {
            if ( in_mode == 0) {
                configFile = PROPERTY_FILE;
            }else if( in_mode == 1){
                configFile = DEV_PROPERTY_FILE;
            }else {
                throw new InvalidParameterException(JingtumMessage.UNKNOWN_MODE, null, null);
            }
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }

        try {
            Config config = Config.loadConfig(configFile);
            //need to check and make sure the input is larger than
            //the MIN_ACTIVATED_AMOUNT
            this.activateAmount = config.getActivateAmount();
            if (this.activateAmount < MIN_ACTIVATED_AMOUNT)


            this.pathRate = config.getPaymentPathRate();


            //Setup the servers with input string from config file
            if (this.tum_server == null) {
                this.tum_server = new TumServer(config.getTumServer());
            }else {
                this.tum_server.setServerURL(config.getTumServer());
            }
            if (this.api_server == null) {
                this.api_server = new APIServer(config.getApiServer(), config.getApiVersion());
            }else {
                this.api_server.setServerURL(config.getApiServer());
                this.api_server.setVersionURL(config.getApiVersion());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Return the object
    public APIServer getAPIServer(){ return api_server; }

    public TumServer getTumServer(){ return tum_server; }

    /**
     * Get token number
     *
     * @return token
     */
    public String getToken() {
        return token;
    }

    /**
     * Set token number
     *
     * @param token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Get token secret
     *
     * @return token secret
     */
    public String getKey() {
        return signKey;
    }

    /**
     * Set token secret
     *
     * @param signKey
     */
    public void setKey(String signKey) {
        this.signKey = signKey;
    }

    /**
     * @return path rate
     */
    public double getPathRate() {
        return pathRate;
    }

    /**
     * Set path rate
     *
     * @param pathRate
     */
    public void setPathRate(double pathRate) {
        this.pathRate = pathRate;
    }

    /**
     * @return activate amount
     */
    public double getActivateAmount() {
        return activateAmount;
    }

    /**
     * Set default activate amount
     *
     * @param activateAmount
     */
    public void setActivateAmount(double activateAmount) {
        this.activateAmount = activateAmount;
    }

    /**
     * Initialize FinGate
     *
     * @param address
     * @param secret
     * @throws InvalidParameterException
     */
    public void setAccount(String secret, String address) throws InvalidParameterException {
        if (!Utility.validateKeyPair(secret, address)) {
            throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS_OR_SECRET, secret+address ,
                    null);
        }
        this.address = address;
        this.secret = secret;
    }


    public void setAccount (String secret) throws InvalidParameterException {
        if(!Utility.isValidSecret(secret)){
            throw new InvalidParameterException(JingtumMessage.INVALID_SECRET, secret, null);
        }
        this.address = Seed.computeAddress(secret);
        this.secret = secret;

    }

    /**
     * @param order
     * @param currency
     * @param amount
     * @param account
     * @return true if issue successfully
     * @throws InvalidParameterException
     */
    public boolean issueCustomTum(String order, String currency, double amount, String account) throws InvalidParameterException {
        System.out.println("Tum Server: " + tum_server.getServerURL());
        if (Utility.isEmpty(this.token)) {
            throw new InvalidParameterException(JingtumMessage.EMPTY_TOKEN, this.token, null);
        }

        if (Utility.isEmpty(this.signKey)) {
            throw new InvalidParameterException(JingtumMessage.EMPTY_KEY, this.signKey, null);
        }

        if (Utility.isEmpty(currency) || Utility.isEmpty(account) || amount <= 0) {
            throw new InvalidParameterException(JingtumMessage.ERROR_INPUT, null, null);
        }

        if (!Utility.isValidAddress(account)) {
            throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS, account, null);
        }

        TongTong tt;

        DecimalFormat myFormatter = new DecimalFormat("0.00");
        String amountString = myFormatter.format(amount);

        String orderNumber = order;
        if (Utility.isEmpty(orderNumber)) {
            try {
                orderNumber = JingtumAPIAndWSServer.getInstance().getNextUUID();
                System.out.println(orderNumber);
            } catch (JingtumException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }

        //build the HMAC signature using input info
        StringBuffer sb = new StringBuffer();
        sb.append(TongTong.CmdType.IssueTum);
        sb.append(this.token);
        sb.append(orderNumber);
        sb.append(currency);
        sb.append(amountString);
        sb.append(account);
        String hmac = Utility.buildHmac(sb.toString(), this.signKey);
        //System.out.println(sb);
//build the paramers in JSON format
//var postData = {
//      cmd: 'IssueTum', // 业务类型，固定值“IssueTum” 签名顺序1
//      token: '00000002', // 商户编号 签名顺序2
//      order: '01', // 发行订单号 签名顺序3
//      currency: '8100000002000020160013000000000020000001',  // 用户通编码 签名顺序4
//      amount: '1000.00', // 发行量，保留两位小数 签名顺序5
//      account: 'jnRLtkRNKEcfi2f6cyvJfUjXHWJ5CSLp3W', // 接收用户通的用户地址签名顺序6
//      hmac: "8f99ffe04f1a953d5f439f04ccc83fac"  // 签名数据
//      }

        HashMap<String, Object> content = new HashMap<String, Object>();
        content.put("cmd", TongTong.CmdType.IssueTum);
        content.put("custom", this.token);
        content.put("order", orderNumber);
        content.put("currency", currency);
        content.put("amount", amountString);
        content.put("account", account);
        content.put("hmac", hmac);

        /*StringBuffer param = new StringBuffer();
        param.append("cmd:");
        param.append(TongTong.CmdType.IssueTum);
        param.append(",token:");
        param.append(this.token);
        param.append(",order:");
        param.append(orderNumber);
        param.append(",currency:");
        param.append(currency);
        param.append(",amount:");
        param.append(amountString);
        param.append(",account:");
        param.append(account);
        param.append(",hmac:");
        param.append(hmac);*/
        String param = TumServer.GSON.toJson(content);

        System.out.println(param);
        System.out.println("send to: " + tum_server.getServerURL());

        try {
            //Note the tum_server value is fixed at this moment
            //https://fingate.jingtum.com/v1/business/node
            //tt = APIServer.request(APIServer.RequestMethod.POST_FORM, tum_server, param.toString(), TongTong.class);
            tt = TumServer.request(TumServer.RequestMethod.POST, tum_server.getServerURL(), param, TongTong.class);
        } catch (JingtumException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        if (tt != null && "1".equals(tt.getSystemCode())) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * @param order
     * @return IssueRecord
     * @throws InvalidParameterException
     */
    public IssueRecord queryIssue(String order) throws InvalidParameterException {
        if (Utility.isEmpty(this.token)) {
            throw new InvalidParameterException(JingtumMessage.EMPTY_TOKEN, this.token, null);
        }

        if (Utility.isEmpty(this.signKey)) {
            throw new InvalidParameterException(JingtumMessage.EMPTY_KEY, this.signKey, null);
        }

        if (Utility.isEmpty(order)) {
            throw new InvalidParameterException(JingtumMessage.INVALID_ORDER_NUMBER, order, null);
        }

        StringBuffer sb = new StringBuffer();
        sb.append(TongTong.CmdType.QueryIssue);
        sb.append(this.token);
        sb.append(order);
        String hmac = Utility.buildHmac(sb.toString(), this.signKey);

        HashMap<String, Object> content = new HashMap<String, Object>();
        content.put("cmd", TongTong.CmdType.QueryIssue);
        content.put("custom", this.token);
        content.put("order", order);
        content.put("hmac", hmac);

        String param = TumServer.GSON.toJson(content);
        try {
            return TumServer.request(TumServer.RequestMethod.POST, tum_server.getServerURL(), param, IssueRecord.class);
        } catch (AuthenticationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (APIConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ChannelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (APIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return the custom Tum information
     * at the present time.
     * @param tum_code
     *
     * @return TumInfo
     * @throws InvalidParameterException
     */
    public TumInfo queryCustomTum(String tum_code) throws InvalidParameterException {
        if (Utility.isEmpty(this.token)) {
            throw new InvalidParameterException(JingtumMessage.EMPTY_TOKEN, this.token, null);
        }

        if (Utility.isEmpty(this.signKey)) {
            throw new InvalidParameterException(JingtumMessage.EMPTY_KEY, this.signKey, null);
        }

        if (Utility.isEmpty(tum_code)) {
            throw new InvalidParameterException(JingtumMessage.ERROR_INPUT, tum_code, null);
        }

        long unix = System.currentTimeMillis() / 1000L;

        StringBuffer sb = new StringBuffer();
        sb.append(TongTong.CmdType.QueryTum);
        sb.append(this.token);
        sb.append(tum_code);
        sb.append(unix);
        String hmac = Utility.buildHmac(sb.toString(), this.signKey);

        HashMap<String, Object> content = new HashMap<String, Object>();
        content.put("cmd", TongTong.CmdType.QueryTum);
        content.put("custom", this.token);
        content.put("currency", tum_code);
        content.put("date", unix);
        content.put("hmac", hmac);

        String param = TumServer.GSON.toJson(content);
        try {
            return TumServer.request(TumServer.RequestMethod.POST, tum_server.getServerURL(), param, TumInfo.class);
        } catch (AuthenticationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (APIConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ChannelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (APIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creat wallet
     *
     * @return wallet
     */
    public Wallet createWallet() {
        String secret = Seed.generateSecret();
        String address = null;
        try {
            address = Seed.computeAddress(secret);
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }
        try {
            return new Wallet(secret, address);
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Activate newly created wallet
     *
     * @param dest_address, wallet address to active, need to check if the address is already actived or not
     * @return true if successfully activated wallet
     * @throws APIException
     * @throws InvalidParameterException
     */
    public boolean activateWallet(String dest_address) throws APIException, InvalidParameterException {
        if (this.address == null) { // Must initialized the FinGate first
            throw new APIException(JingtumMessage.GATEWAY_NOT_INITIALIZED, null);
        }
        if (!Utility.isValidAddress(dest_address)) {
            throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS, dest_address, null);
        }
        RequestResult payment = null;
//        Amount jtc = new Amount(); // build jingtum amount
        try {

            // send default amount of SWT to each wallet

            HashMap<String, String> destination_amount = new HashMap<String, String>();
            destination_amount.put("currency", "SWT");
            destination_amount.put("value", Utility.doubleToString(this.activateAmount));
            destination_amount.put("issuer","");

            //Build a payment object with active amount of SWT
            HashMap<String, Object> active_payment = new HashMap<String, Object>();
            active_payment.put("source_account", this.address);
            active_payment.put("destination_account", dest_address);
            active_payment.put("destination_amount", destination_amount);

            HashMap<String, Object> content = new HashMap<String, Object>();
            long unix = System.currentTimeMillis() / 1000L;
            String uid = "activewallet"+Long.toString(unix); //获得唯一订单号order_id
            content.put("secret", this.secret);
            content.put("client_resource_id", uid);
            content.put("payment", active_payment);

            String params = APIServer.GSON.toJson(content);
            payment = APIServer.request(APIServer.RequestMethod.POST,
                    APIServer.formatURL(Payment.class, this.address, "?VALIDATED=true"),
                    params,
                    RequestResult.class);

             //= submitPayment(address, jtc, true, JingtumAPIAndWSServer.getInstance().getNextUUID());
        } catch (FailedException e) {
            e.printStackTrace();
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (ChannelException e) {
            e.printStackTrace();
        }

        if (payment != null) {
            return payment.getSuccess();
        }
        return false;
    }

    /**
     * @param in_mode input operation mode
     */
    public void setMode(int in_mode) {
        init(in_mode);
    }

    /*
     * Convert the Tum pair string to
     * amount value
     */
    private void fillAmountfromPair(String in_pair, Amount base_amount, Amount counter_amount) throws InvalidParameterException{
    	//split the pair to get the base and counter tum
        String tum_codes[] = in_pair.split("/");

        for (int i = 0; i<tum_codes.length; i ++)
            System.out.println("Tumcodes:"+tum_codes[i]);

        if ( tum_codes.length != 2)
            throw new InvalidParameterException(JingtumMessage.INVALID_TUM_PAIR,in_pair,null);


        String[] base_tum = tum_codes[0].split(":", 2);
        String[] counter_tum = tum_codes[1].split(":", 2);


        //Set the source_amount and destination amount with pair, price and amount value
        base_amount.setCurrency(base_tum[0]);
        if (base_tum.length < 2) {
            if (!base_tum[0].equals("SWT")) {
             System.out.println(base_tum[0]);
                throw new InvalidParameterException(JingtumMessage.INVALID_TUM_PAIR, in_pair, null);
            }

            base_amount.setIssuer("");
        }
        else
            base_amount.setIssuer(base_tum[1]);

        counter_amount.setCurrency(counter_tum[0]);

        if (counter_tum.length < 2){
            if (counter_tum[0] != "SWT")
                throw new InvalidParameterException(JingtumMessage.INVALID_TUM_PAIR,in_pair,null);

            counter_amount.setIssuer("");
        }
        else
            counter_amount.setIssuer(counter_tum[1]);
    }
    /*
     *
     */
    public OrderBookResult getOrderBook(String in_pair) throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException,
    ChannelException, InvalidParameterException, FailedException {
        Amount base_amount = new Amount();
        Amount counter_amount = new Amount();
        fillAmountfromPair(in_pair, base_amount, counter_amount);
        try {
            return getOrderBook(base_amount, counter_amount);
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getOrderBook(String in_pair, RequestListener<OrderBookResult> listener) throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException,
    ChannelException, InvalidParameterException, FailedException {
        Amount base_amount = new Amount();
        Amount counter_amount = new Amount();
        fillAmountfromPair(in_pair, base_amount, counter_amount);
        try {
            getOrderBook(base_amount, counter_amount, listener);
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }
    }

    public OrderBookResult getOrderBook(Amount base, Amount counter)
            throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException,
            ChannelException, InvalidParameterException, FailedException {

        if (!Utility.isValidAmount(base) || !Utility.isValidAmount(counter)) {
            throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_AMOUNT, null, null);
        }
        StringBuffer sb = new StringBuffer();
        sb.append("/");
        sb.append(base.getCurrency());
        sb.append("%2B");
        sb.append(base.getIssuer());
        sb.append("/");
        sb.append(counter.getCurrency());
        sb.append("%2B");
        sb.append(counter.getIssuer());

        if (this.secret == null) {
            throw new InvalidParameterException(JingtumMessage.INACTIVATED_ACCOUNT, null, null);
        }

        return APIServer.request(APIServer.RequestMethod.GET,
                APIServer.formatURL(OrderBook.class, this.address, sb.toString()),
                null,
                OrderBookResult.class);
    }

    public void getOrderBook(Amount base, Amount counter, RequestListener<OrderBookResult> listener)
            throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException,
            ChannelException, InvalidParameterException, FailedException {
		Utility.callback(new OrderBookRunnable(this, base, counter, listener));
    }

    private class OrderBookRunnable implements Runnable {
        private FinGate gate;
        private Amount base;
        private Amount counter;
        private RequestListener<OrderBookResult> listener;

        private OrderBookRunnable(FinGate gate, Amount base, Amount counter, RequestListener<OrderBookResult> listener){
            this.gate = gate;
            this.base = base;
            this.counter = counter;
            this.listener = listener;
        }
        public void run() {
            try {
				OrderBookResult result = this.gate.getOrderBook(this.base, this.counter);
				this.listener.onComplete(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    }
}
