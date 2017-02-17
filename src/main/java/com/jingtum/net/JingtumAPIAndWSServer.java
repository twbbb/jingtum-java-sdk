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

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import com.jingtum.JingtumMessage;
import com.jingtum.core.crypto.ecdsa.Seed;
import com.jingtum.exception.APIConnectionException;
import com.jingtum.exception.APIException;
import com.jingtum.exception.AuthenticationException;
import com.jingtum.exception.ChannelException;
import com.jingtum.exception.FailedException;
import com.jingtum.exception.InvalidParameterException;
import com.jingtum.exception.InvalidRequestException;
import com.jingtum.model.*;
import com.jingtum.util.Utility;
import com.jingtum.util.Config;

/**
 * @author jzhao
 * @version 1.0 JingtumAPIAndWSServer class
 */
public class JingtumAPIAndWSServer extends AccountClass {
    private static final String PROPERTY_FILE = "src/main/java/com/jingtum/conf/prod.config.yaml"; // property file for prod
    private static final String DEV_PROPERTY_FILE = "src/test/java/com/jingtum/conf/test.config.yaml"; // property file for testing

    private String webSocketServer; // web socket server
    private String apiBase; // api server
    private String prefix; // prefix
    private String apiVersion; // api version
    private boolean isTest;

    private SubscribeEventHandler handler;
    private JingtumWebSocket jtWebSocket;


    private static JingtumAPIAndWSServer instance = null;

    /**
     * Singleton mode
     *
     * @return JingtumAPIAndWSServer instance
     */
    public static final JingtumAPIAndWSServer getInstance() {
        if (instance == null) {
            instance = new JingtumAPIAndWSServer(false);
        }
        return instance;
    }

    /**
     * Singleton mode
     *
     * @return JingtumAPIAndWSServer instance
     */
    public static final JingtumAPIAndWSServer getTestInstance() {
        if (instance == null) {
            instance = new JingtumAPIAndWSServer(true);
        }
        return instance;
    }

    /**
     * Read yaml config file and initialize jingtumServer class
     */
    private JingtumAPIAndWSServer(Boolean isTest) {
        init(isTest);
    }

    /**
     * Read yaml config file
     */
    private void init(Boolean isTest) {
        try {
            String configFile = isTest ? DEV_PROPERTY_FILE : PROPERTY_FILE;
            Config jingtumConfig = Config.loadConfig(configFile);
            this.webSocketServer = jingtumConfig.getWebSocketServer();
            this.apiBase = jingtumConfig.getApiServer();
            this.apiVersion = jingtumConfig.getApiVersion();
            this.prefix = jingtumConfig.getPrefix();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Server utility class
     *
     * @author jzhao
     */
    private class ServerUtility {
        private boolean connected;
        private String uuid;

        public boolean getConnected() {
            return connected;
        }

        public String getUuid() {
            return uuid;
        }
    }

    /**
     * Set api server
     *
     * @param server
     */
    public void setJingtumServerHost(String server) {
        this.apiBase = server;
    }

    /**
     * @return api server
     */
    public String getJingtumServerHost() {
        return apiBase;
    }

    /**
     * @return prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Set prefix
     *
     * @param prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return web socket server
     */
    public String getWebSocketServer() {
        return webSocketServer;
    }

    /**
     * Set web socket server
     *
     * @param webSocketServer
     */
    public void setWebSocketServer(String webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    /**
     * @return api version
     */
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * Set api version
     *
     * @param apiVersion
     */
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    /**
     * @return server status
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws ChannelException
     * @throws APIException
     * @throws FailedException
     */
    public boolean getStatus() throws AuthenticationException, InvalidRequestException, APIConnectionException,
            ChannelException, APIException, FailedException {
        return APIProxy.request(APIProxy.RequestMethod.GET, APIProxy.formatURL("server/connected"), null, ServerUtility.class).getConnected();
    }

    /**
     * @return next uuid
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws ChannelException
     * @throws APIException
     * @throws FailedException
     */
    public String getNextUUID() throws AuthenticationException, InvalidRequestException, APIConnectionException,
            ChannelException, APIException, FailedException {
        return APIProxy.request(APIProxy.RequestMethod.GET, APIProxy.formatURL("uuid"), null, ServerUtility.class).getUuid();
    }

    /**
     * Set handler in subscription
     *
     * @param handler
     */
    public void setTxHandler(SubscribeEventHandler handler) {
        this.handler = handler;
    }

    /**
     * @return websocket
     */
    public JingtumWebSocket getJtWebSocket() {
        return jtWebSocket;
    }

    /**
     * Open connection to Web Socket server
     *
     * @return true if opened connection successfully
     * @throws APIException
     * @throws InvalidParameterException
     */
    public boolean connet() throws APIException, InvalidParameterException {
        if (null == handler) {
            throw new InvalidParameterException(JingtumMessage.NOT_NULL_MESSAGE_HANDLER, null, null);
        }
        try {
            jtWebSocket = new JingtumWebSocket();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
        return jtWebSocket.openWebSocket(handler);
    }

    /**
     * Close Web Socket connection
     *
     * @return true if successfully closed connection
     * @throws APIException
     */
    public boolean disconnect() throws APIException {
        if (null == jtWebSocket || jtWebSocket.isConnected() == false) {
            throw new APIException(JingtumMessage.NO_CONNECTION_AVAIABLE, null);
        }

        boolean isClosed = jtWebSocket.closeWebSocket();
        if (isClosed) {
            jtWebSocket = null;
        }
        return isClosed;
    }

    /**
     * Get order book
     *
     * @param base
     * @param counter
     * @return OrderBookResult
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws ChannelException
     * @throws InvalidParameterException
     * @throws FailedException
     */
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
            this.secret = Seed.generateSecret();
            this.address = Seed.computeAddress(secret);
        }
        return APIProxy.request(APIProxy.RequestMethod.GET,
                APIProxy.formatURL(
                        OrderBook.class,
                        this.address,
                        sb.toString()),
                null, OrderBookResult.class);
    }
}