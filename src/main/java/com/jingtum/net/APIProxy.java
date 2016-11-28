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

package com.jingtum.net;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jingtum.JingtumMessage;
import com.jingtum.model.*;

import com.jingtum.util.Utility;
import com.jingtum.exception.APIConnectionException;
import com.jingtum.exception.InvalidRequestException;
import com.jingtum.exception.APIException;
import com.jingtum.exception.AuthenticationException;
import com.jingtum.exception.ChannelException;
import com.jingtum.exception.FailedException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.JsonNode;

/**
 * @author jzhao
 * @version 1.0
 */
/**
 * Extends the abstract class when you need request anything from jingtum
 */
public class APIProxy extends JingtumObject {
    private static final String VERSION_URL = String.format("/%s/accounts", JingtumAPIAndWSServer.getInstance().getApiVersion());
    /**
     * URLEncoder charset
     */
    public static final String CHARSET = "UTF-8";

    /**
     * Http request method:
     * Get, Post and Delete
     */
    public enum RequestMethod {
        GET, POST, DELETE, POST_FORM
    }

    /**
     * Gson object use to transform json string to Jingtum object
     */
    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Wallet.class, new WalletDeserializer())
            .registerTypeAdapter(OrderBookResult.class, new OrderBookResultDeserializer())
            .registerTypeAdapter(PaymentCollection.class, new PaymentCollectionDeserializer())
            .registerTypeAdapter(EffectCollection.class, new EffectCollectionDeserializer())
            .create();

    /**
     * Based on class name to built the string needed in URL
     *
     * @param clazz
     * @return clazz
     */
    private static String className(Class<?> clazz) {
        String className = clazz.getSimpleName().toLowerCase().replace("$", " ");
        if (className.equals("orderbook")) {
            className = "order_book";
        } else {
            className = className.concat("s");
        }
        return className;
    }

    /**
     * Built base URL
     *
     * @return class URL
     */
    protected static String classURL() {
        return JingtumAPIAndWSServer.getInstance().getJingtumServerHost().concat(VERSION_URL);
    }

    /**
     * @param param
     * @return formed URL
     */
    public static String formatURL(String param) {
        return String.format("%s/%s/%s", JingtumAPIAndWSServer.getInstance().getJingtumServerHost(), JingtumAPIAndWSServer.getInstance().getApiVersion(), param);
    }

    /**
     * @param url
     * @param query
     * @return url
     */
    private static String formatURL(String url, String query) {
        if (query == null || query.isEmpty()) {
            return url;
        } else {
            String separator = url.contains("?") ? "&" : "?";
            return String.format("%s%s%s", url, separator, query);
        }
    }

    /**
     * @param clazz
     * @param param
     * @return formed URL
     * @throws InvalidRequestException
     */
    public static String formatURL(Class<?> clazz, String address, String param) throws InvalidRequestException {
        return String.format("%s/%s/%s%s", classURL(), address, className(clazz), param);
    }

    /**
     * Error class
     */
    private static class Error {
        private String error_type;
        private String error;
        private String message;
        private String r1_code;
        private String r3_error_msg;
        private String ra_error_msg;
        private String ri_error_msg;

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            if (Utility.isNotEmpty(error_type)) {
                sb.append("Error type: ");
                sb.append(error_type);
                sb.append("\n");
            }
            if (Utility.isNotEmpty(error) || Utility.isNotEmpty(error_type)) {
                sb.append("\t ");
                sb.append(JingtumMessage.ERROR_MESSAGE);
                if (JingtumMessage.ACCOUNT_NOT_FOUND.equals(message)) {
                    sb.append(JingtumMessage.INACTIVATED_ACCOUNT);
                }
                sb.append(message);
                sb.append("\n");
            }
            if (Utility.isNotEmpty(r1_code)) {
                sb.append("\t ");
                sb.append(JingtumMessage.ERROR_CODE);
                sb.append(r1_code);
                sb.append("\n");
            }
            if (Utility.isNotEmpty(r3_error_msg)) {
                sb.append("\t ");
                sb.append(JingtumMessage.ERROR_MESSAGE);
                sb.append(r3_error_msg);
                sb.append("\n");
            }
            if (Utility.isNotEmpty(ra_error_msg)) {
                sb.append("\t ");
                sb.append(JingtumMessage.ERROR_MESSAGE);
                sb.append(ra_error_msg);
                sb.append("\n");
            }
            if (Utility.isNotEmpty(ri_error_msg)) {
                sb.append("\t ");
                sb.append(JingtumMessage.ERROR_MESSAGE);
                sb.append(ri_error_msg);
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * @param method
     * @param url
     * @param query
     * @return JingtumResponse
     * @throws APIConnectionException
     */
    private static JingtumResponse makeRequest(
            APIProxy.RequestMethod method, String url, String query)
            throws APIConnectionException {
        HttpResponse<JsonNode> jsonResponse = null;
        Unirest.setTimeouts(30 * 1000, 80 * 1000);

        System.out.println("In make req: "+url);
        System.out.println("In make req query: "+query);
        try {
            switch (method) {
                case GET:
                    jsonResponse = Unirest.get(url).asJson();
                    break;
                case POST:
                case POST_FORM:
                    jsonResponse = Unirest.post(url).header("Content-Type", "application/json").body(query).asJson();
                    break;
                case DELETE:
                    jsonResponse = Unirest.delete(url).header("Content-Type", "application/json").body(query).asJson();
                    break;
                default:
                    throw new APIConnectionException(
                            String.format(JingtumMessage.UNRECOGNIZED_HTTP_METHOD, method));
            }
            // trigger the request
            String rBody = jsonResponse.getBody().toString();
            Integer rCode = jsonResponse.getStatus();
            return new JingtumResponse(rCode, rBody);
        } catch (UnirestException e) {
            throw new APIConnectionException(
                    String.format(
                            JingtumMessage.SERVER_ERROR,
                            JingtumAPIAndWSServer.getInstance().getJingtumServerHost(), e.getMessage()), e);
        }
    }

    /**
     * Build class instance from jason format http response
     *
     * @param method
     * @param url
     * @param params
     * @param clazz
     * @param <T>
     * @return class instance
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws APIException
     * @throws FailedException
     */
    public static <T> T request(APIProxy.RequestMethod method, String url, String params, Class<T> clazz) throws AuthenticationException,
            InvalidRequestException, APIConnectionException, ChannelException, APIException, FailedException {

        JingtumResponse response;

        System.out.println(method);
        System.out.println("API proxy URL: "+url);
        System.out.println("PARAMS: "+params);
        try {
            response = makeRequest(method, url, params);
        } catch (ClassCastException ce) {
            throw ce;
        }
        int rCode = response.getResponseCode();
        String rBody = response.getResponseBody();
        if (rCode < 200 || rCode >= 300) {
            handleAPIError(rBody, rCode, params);
        }

        return GSON.fromJson(rBody, clazz);
    }

    /**
     * Error handling
     *
     * @param rBody
     * @param rCode
     * @throws InvalidRequestException
     * @throws AuthenticationException
     * @throws APIException
     * @throws FailedException
     */
    private static void handleAPIError(String rBody, int rCode, String query)
            throws InvalidRequestException, AuthenticationException,
            APIException, ChannelException, FailedException {
        APIProxy.Error error;
        switch (rCode) {
            case 400:
                error = GSON.fromJson(rBody,
                        APIProxy.Error.class);
                throw new InvalidRequestException(error.toString(), query, null);
            case 404:
                error = GSON.fromJson(rBody,
                        APIProxy.Error.class);
                throw new InvalidRequestException(error.toString(), query, null);
            case 403:
                error = GSON.fromJson(rBody,
                        APIProxy.Error.class);
                throw new ChannelException(error.toString(), query, null);
            case 401:
                error = GSON.fromJson(rBody,
                        APIProxy.Error.class);
                throw new AuthenticationException(error.toString());
            case 500:
                error = GSON.fromJson(rBody,
                        APIProxy.Error.class);
                throw new FailedException(error.toString());
            default:
                throw new APIException(rBody.toString(), null);
        }
    }
}
