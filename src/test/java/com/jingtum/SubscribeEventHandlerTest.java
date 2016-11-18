package com.jingtum;

import org.json.JSONObject;

import com.jingtum.net.SubscribeEventHandler;

public class SubscribeEventHandlerTest implements SubscribeEventHandler {

	public void onMessage(JSONObject msg) {
		System.out.println(msg);
	}

	public void onDisconnected(int code, String reason, boolean remote) {
		
	}

	public void onError(Exception error) {
		
	}

	public void onConnected() {
		
	}
}
