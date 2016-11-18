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

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

/**
 * WebSocket connection handler class
 * @author jzhao
 * @version 1.0
 */
public class JingtumWebSocket extends WebSocketClient {
	private JSONObject jsObj = null;
	private SubscribeEventHandler eventHandler; //Event handler
	private boolean isConnected = false; //if the connected	
	/**
	 * Constructor
	 * @throws URISyntaxException
	 */
	public JingtumWebSocket() throws URISyntaxException{
		super(new URI( JingtumAPIAndWSServer.getInstance().getWebSocketServer()), new Draft_10());
	}	
	/**
	 * @param eventHandler
	 */
	public void setEventHandler(SubscribeEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}
	/**
	 * Get connection status
	 * @return isConnected
	 */
	public boolean isConnected() {
		return isConnected;
	}
	/**
	 * Set connected or not
	 * @param isConnected
	 */
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	/**
	 * Get message json object
	 * @return jsObje
	 */
	public JSONObject getJsonObj() {
		return jsObj;
	}
	@Override
	public void onOpen(ServerHandshake handshakedata) {
		if(eventHandler != null){
			this.eventHandler.onConnected();
		}
	}
	@Override
	public void onMessage(String message) {
		jsObj = new JSONObject(message);
		if(eventHandler != null){
			this.eventHandler.onMessage(jsObj);;
		}
	}
	@Override
	public void onClose(int code, String reason, boolean remote) {
		if(eventHandler != null){
			this.eventHandler.onDisconnected(code, reason, remote);
		}
	}
	@Override
	public void onError(Exception ex) {
		if(eventHandler != null){
			this.eventHandler.onError(ex);
		}
	}
	/**
	 * Get the socket message in json format
	 * @return JSONObject
	 */
	public JSONObject getObj() {
		return jsObj;
	}	
	/**
	 * Open Web Socket connection
	 * @param address
	 * @param secret
	 * @return true if subscribe successfully
	 */
	public boolean subscribe(String address, String secret){
		JSONObject obj = new JSONObject();
 	    obj.put("command", "subscribe");
 	    obj.put("account", address);
 	    obj.put("secret", secret);
 	    //send message
 	    send(obj.toString());
		while(jsObj == null || !("subscribe".equals(jsObj.getString("type")) 
				&& address.equals(jsObj.getString("account")))){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Boolean(jsObj.getBoolean("success"));
	}	
	/**
	 * Unsubscribe web socket connection
	 * @return true if unsubscribe successfully
	 */
	public boolean unsubscribe(String address){

		JSONObject obj = new JSONObject();
 	    obj.put("command", "unsubscribe");
 	    obj.put("account", address);
 	    //send message
 	    send(obj.toString());
		while(jsObj == null || !("unsubscribe".equals(jsObj.getString("type")) 
				&& address.equals(jsObj.getString("account")))){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Boolean(jsObj.getBoolean("success"));
	}
	/**
	 * Close Web Socket connection
	 * @return true if successfully closed connection
	 */
	public boolean closeWebSocket(){
		jsObj = null;
		JSONObject obj = new JSONObject();
 	    obj.put("command", "close");
 	    send(obj.toString());
		while(jsObj == null){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		boolean isClosed = new Boolean(jsObj.getBoolean("success"));
		if(isClosed){
			isConnected = false;			
		}
		return isClosed;
	}
	
	/**
	 * @param handler
	 * @return true if opened connection successfully
	 */
	public boolean openWebSocket(SubscribeEventHandler handler){
		SSLContext sslContext = null;
		try {
		    sslContext = SSLContext.getInstance( "TLS" );
		    sslContext.init( null, null, null ); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates
		} catch (NoSuchAlgorithmException e) {
		    e.printStackTrace();
		} catch (KeyManagementException e) {
		    e.printStackTrace();
		}
		setWebSocketFactory( new DefaultSSLWebSocketClientFactory( sslContext ) );
		
    	connect();  
    	int loop = 0;
    	boolean isConnected = false;
    	setEventHandler(handler);
		while((jsObj == null || !"connection".equals(jsObj.getString("type"))) 
				&& loop <20){
			try {
				Thread.sleep(100);
				loop++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(jsObj != null){
			isConnected = new Boolean(jsObj.getBoolean("success"));
		}
		setConnected(isConnected);
		return isConnected; 
	}
}
