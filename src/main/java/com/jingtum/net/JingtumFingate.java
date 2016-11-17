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

package com.jingtum.net;

import com.jingtum.Jingtum;
import com.jingtum.JingtumMessage;
import com.jingtum.core.crypto.ecdsa.Seed;
import com.jingtum.exception.*;
import com.jingtum.model.*;
import com.jingtum.util.Config;
import com.jingtum.util.Utility;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;

/**
 * Created by yifan on 11/15/16.
 */
public class JingtumFingate extends BaseWallet {
    private static final String PROPERTY_FILE = "src/main/java/com/jingtum/conf/prod.config.yaml"; // property file for prod
    private static final String DEV_PROPERTY_FILE = "src/test/java/com/jingtum/conf/test.config.yaml"; // property file for testing
    private static final String ISSUE_CURRENCY = "/currency/issue";
    private static final String QUERY_ISSUE = "/currency/queryIssue";
    private static final String CURRENCY_STATUS = "/currency/status";

    private double activateAmount; // default amout of SWT to activate wallet
    private double trustLimit;
    private double pathRate;
    private String tt_server;
    private String custom;
    private String customSecret;
    private Wallet wallet;

    private static JingtumFingate instance = null;

    /**
     * Singleton mode
     *
     * @return JingtumFingate instance
     */
    public static final JingtumFingate getInstance() {
        if (instance == null) {
            instance = new JingtumFingate(false);
        }
        return instance;
    }

    /**
     * Singleton mode
     *
     * @return JingtumFingate instance
     */
    public static final JingtumFingate getTestInstance() {
        if (instance == null) {
            instance = new JingtumFingate(true);
        }
        return instance;
    }

    /**
     * Read yaml config file and initialize jingtumServer class
     */
    private JingtumFingate(Boolean isTest) {
        init(isTest);
    }

    /**
     * Read yaml config file
     */
    private void init(Boolean isTest) {
        try {
            String configFile = isTest ? DEV_PROPERTY_FILE : PROPERTY_FILE;
            Config FingateConfig = Config.loadConfig(configFile);
            this.activateAmount = FingateConfig.getActivateAmount();
            this.tt_server = FingateConfig.getApiServer();
            this.trustLimit = FingateConfig.getDefaultTrustLimit();
            this.pathRate = FingateConfig.getPaymentPathRate();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get custom number
     *
     * @return custom
     */
    public String getCustom() {
        return custom;
    }

    /**
     * Set custom number
     *
     * @param custom
     */
    public void setCustom(String custom) {
        this.custom = custom;
    }

    /**
     * Get custom secret
     *
     * @return custom secret
     */
    public String getCustomSecret() {
        return customSecret;
    }

    /**
     * Set custom secret
     *
     * @param customSecret
     */
    public void setCustomSecret(String customSecret) {
        this.customSecret = customSecret;
    }

    /**
     * @return default trust limit
     */
    public double getTrustLimit() {
        return trustLimit;
    }

    /**
     * Set default trust limit
     *
     * @param trustLimit
     */
    public void setTrustLimit(double trustLimit) {
        this.trustLimit = trustLimit;
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
     * Initialize JingtumFingate
     *
     * @param address
     * @param secret
     * @throws InvalidParameterException
     */
    public void setFinGate(String address, String secret) throws InvalidParameterException {
        if (!Utility.validateKeyPair(address, secret)) {
            throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS_OR_SECRET, address + secret,
                    null);
        }
        this.address = address;
        this.secret = secret;
    }

    /**
     * @return JingtumFingate address
     */
    public String getFinGate() {
        return address;
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
        if (Utility.isEmpty(this.custom)) {
            throw new InvalidParameterException(JingtumMessage.EMPTY_CUSTOM, this.custom, null);
        }

        if (Utility.isEmpty(this.customSecret)) {
            throw new InvalidParameterException(JingtumMessage.EMPTY_SECRET, this.customSecret, null);
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
            } catch (JingtumException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }

        StringBuffer sb = new StringBuffer();
        sb.append(TongTong.CmdType.IssueCurrency);
        sb.append(this.custom);
        sb.append(orderNumber);
        sb.append(currency);
        sb.append(amountString);
        sb.append(account);
        String hmac = Utility.buildHmac(sb.toString(), this.customSecret);
        
//build the paramers in JSON format
//var postData = {
//      cmd: 'IssueTum', // 业务类型，固定值“IssueTum” 签名顺序1
//      custom: '00000002', // 商户编号 签名顺序2
//      order: '01', // 发行订单号 签名顺序3
//      currency: '8100000002000020160013000000000020000001',  // 用户通编码 签名顺序4
//      amount: '1000.00', // 发行量，保留两位小数 签名顺序5
//      account: 'jnRLtkRNKEcfi2f6cyvJfUjXHWJ5CSLp3W', // 接收用户通的用户地址签名顺序6
//      hmac: "8f99ffe04f1a953d5f439f04ccc83fac"  // 签名数据
//      }

        StringBuffer param = new StringBuffer();
        param.append("cmd:");
        param.append(TongTong.CmdType.IssueCurrency);
        param.append(",custom:");
        param.append(this.custom);
        param.append(",order:");
        param.append(orderNumber);
        param.append(",currency:");
        param.append(currency);
        param.append(",amount:");
        param.append(amountString);
        param.append(",account:");
        param.append(account);
        param.append(",hmac:");
        param.append(hmac);

        try {
            //Note the tt_server value is fixed at this moment
            //https://fingate.jingtum.com/v1/business/node
            tt = APIProxy.request(APIProxy.RequestMethod.POST_FORM, tt_server, param.toString(), TongTong.class);
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
        if (Utility.isEmpty(this.custom)) {
            throw new InvalidParameterException(JingtumMessage.EMPTY_CUSTOM, this.custom, null);
        }

        if (Utility.isEmpty(this.customSecret)) {
            throw new InvalidParameterException(JingtumMessage.EMPTY_SECRET, this.customSecret, null);
        }

        if (Utility.isEmpty(order)) {
            throw new InvalidParameterException(JingtumMessage.INVALID_ORDER_NUMBER, order, null);
        }

        StringBuffer sb = new StringBuffer();
        sb.append(TongTong.CmdType.QueryIssue);
        sb.append(this.custom);
        sb.append(order);
        String hmac = Utility.buildHmac(sb.toString(), this.customSecret);

        StringBuffer param = new StringBuffer();
        param.append("cmd:");
        param.append(TongTong.CmdType.QueryIssue);
        param.append(",custom:");
        param.append(this.custom);
        param.append(",order:");
        param.append(order);
        param.append(",hmac:");
        param.append(hmac);

        try {
            return APIProxy.request(APIProxy.RequestMethod.GET, tt_server, param.toString(), IssueRecord.class);
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
     * @param currency
     * @return TumInfo
     * @throws InvalidParameterException
     */
    public TumInfo queryCustomTum(String currency) throws InvalidParameterException {
        if (Utility.isEmpty(this.custom)) {
            throw new InvalidParameterException(JingtumMessage.EMPTY_CUSTOM, this.custom, null);
        }

        if (Utility.isEmpty(this.customSecret)) {
            throw new InvalidParameterException(JingtumMessage.EMPTY_SECRET, this.customSecret, null);
        }

        if (Utility.isEmpty(currency)) {
            throw new InvalidParameterException(JingtumMessage.ERROR_INPUT, currency, null);
        }

        long unix = System.currentTimeMillis() / 1000L;

        StringBuffer sb = new StringBuffer();
        sb.append(TongTong.CmdType.QueryTum);
        sb.append(this.custom);
        sb.append(currency);
        sb.append(unix);
        String hmac = Utility.buildHmac(sb.toString(), this.customSecret);

        StringBuffer param = new StringBuffer();
        param.append("cmd:");
        param.append(TongTong.CmdType.QueryTum);
        param.append(",custom:");
        param.append(this.custom);
        param.append(",currency:");
        param.append(currency);
        param.append(",date:");
        param.append(unix);
        param.append(",hmac:");
        param.append(hmac);

        try {
            return APIProxy.request(APIProxy.RequestMethod.GET, tt_server, param.toString(), TumInfo.class);
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
            return new Wallet(address, secret);
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Activate newly created wallet
     *
     * @param address
     * @return true if successfully activated wallet
     * @throws APIException
     * @throws InvalidParameterException
     */
    public boolean activateWallet(String address) throws APIException, InvalidParameterException {
        if (this.address == null) { // Must initialized the gateway first
            throw new APIException(JingtumMessage.GATEWAY_NOT_INITIALIZED, null);
        }
        if (!Utility.isValidAddress(address)) {
            throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS, address, null);
        }
        RequestResult payment = null;
        Amount jtc = new Amount(); // build jingtum amount
        try {
            jtc.setCounterparty("");
            jtc.setCurrency(Jingtum.getCurrencySWT());
            jtc.setValue(this.activateAmount); // value
            // send default amount of SWT to each wallet
            payment = getMyWallet().submitPayment(address, jtc, true, JingtumAPIAndWSServer.getInstance().getNextUUID());
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
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }
        if (payment != null) {
            return payment.getSuccess();
        }
        return false;
    }

    /**
     * @return wallet
     * @throws InvalidParameterException
     */
    private Wallet getMyWallet() throws InvalidParameterException {
        if (wallet == null) {
            wallet = new Wallet(this.address, this.secret);
        }
        return wallet;
    }
}
