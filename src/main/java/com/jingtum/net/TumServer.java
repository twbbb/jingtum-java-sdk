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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jingtum.JingtumMessage;
import com.jingtum.exception.*;
import com.jingtum.model.EffectCollection;
import com.jingtum.net.ServerClass;
import com.jingtum.net.JingtumAPIAndWSServer;
import com.jingtum.util.Utility;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

/**
 * @author Jingtum Inc.
 * @version 1.0 JingtumAPIAndWSServer class
 */
public class TumServer extends ServerClass {
	
	private static TumServer INSTANCE = null;
    static {
    	INSTANCE = new TumServer();
    }
    
    private TumServer(){
    	super("");
    }

    public TumServer(String in_url){
        //call the parent constructor to create the class
        super(in_url);

        INSTANCE.setServerURL(in_url);
    }
    
    @Override
    public void setServerURL(String in_url) {
    	super.setServerURL(in_url);
    	
    	INSTANCE.serverURL = in_url;
    }

    /**
     * Http request method:
     * Get, Post and Delete
     */
    public enum RequestMethod {
        GET, POST, DELETE, POST_FORM
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
            RequestMethod method, String url, String query)
            throws APIConnectionException {
        HttpResponse<JsonNode> jsonResponse = null;
        Unirest.setTimeouts(30 * 1000, 80 * 1000);
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
            System.out.println("TumServer Res: "+rBody);
            Integer rCode = jsonResponse.getStatus();

            return new JingtumResponse(rCode, rBody);
        } catch (UnirestException e) {
            throw new APIConnectionException(
                    String.format(
                            JingtumMessage.SERVER_ERROR,
                            INSTANCE.getServerURL(), e.getMessage()), e);
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
    public static <T> T request(RequestMethod method, String url, String params, Class<T> clazz) throws AuthenticationException,
            InvalidRequestException, APIConnectionException, ChannelException, APIException, FailedException {

        JingtumResponse response;
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
        TumServer.Error error;
        switch (rCode) {
            case 400:
                error = GSON.fromJson(rBody,
                        TumServer.Error.class);
                throw new InvalidRequestException(error.toString(), query, null);
            case 404:
                error = GSON.fromJson(rBody,
                        TumServer.Error.class);
                throw new InvalidRequestException(error.toString(), query, null);
            case 403:
                error = GSON.fromJson(rBody,
                        TumServer.Error.class);
                throw new ChannelException(error.toString(), query, null);
            case 401:
                error = GSON.fromJson(rBody,
                        TumServer.Error.class);
                throw new AuthenticationException(error.toString());
            case 500:
                error = GSON.fromJson(rBody,
                        TumServer.Error.class);
                throw new FailedException(error.toString());
            default:
                throw new APIException(rBody.toString(), null);
        }
    }

}