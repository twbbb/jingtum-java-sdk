/**
 * Copyright@2016 Jingtum Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jingtum.sdk.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import com.jingtum.sdk.exception.APIConnectionException;

/**
 * @author Administrator
 *
 */
public class ApiRequest {
	/**
	 * URLEncoder charset
	 */
	public static final String CHARSET = "UTF-8";

	/**
	 * Http request method: Get, Post and Delete
	 */
	public enum RequestMethod {
		GET, POST, DELETE, POST_FORM
	}

	/**
	 * @param param
	 * @return formed URL
	 */
	protected static String formatURL(String param) {
		// TODO
		return String.format("%s/%s/%s", "", "", param);
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
	 * Get http connection instance
	 * 
	 * @param url
	 * @return conn
	 * @throws IOException
	 */
	private static java.net.HttpURLConnection request(String url) throws IOException {
		URL jingtumURL = null;
		HttpURLConnection conn;
		jingtumURL = new URL(url);
		if (url.startsWith("https")) {
			conn = (HttpsURLConnection) jingtumURL.openConnection();
		} else {
			conn = (HttpURLConnection) jingtumURL.openConnection();
		}
		conn.setConnectTimeout(30 * 1000);
		conn.setReadTimeout(80 * 1000);
		conn.setUseCaches(false);
		return conn;
	}

	/**
	 * Get a http Get request connection
	 * 
	 * @param url
	 * @param query
	 * @return conn
	 * @throws IOException
	 * @throws APIConnectionException
	 */
	public static java.net.HttpURLConnection getRequest(String url, String query)
			throws IOException, APIConnectionException {
		String getURL = formatURL(url, query);
		java.net.HttpURLConnection conn = request(getURL);
		conn.setRequestMethod(RequestMethod.GET.toString());
		return conn;
	}

	/**
	 * Get a http delete connection
	 * 
	 * @param url
	 * @param query
	 * @param method
	 * @return http connection
	 * @throws IOException
	 * @throws APIConnectionException
	 */
	public static java.net.HttpURLConnection deleteRequest(String url, String query, RequestMethod method)
			throws IOException, APIConnectionException {
		java.net.HttpURLConnection conn = request(url);
		conn.setDoOutput(true);
		conn.setRequestMethod(method.toString());
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Content-Length", String.valueOf(query.getBytes().length));
		OutputStream output = null;
		try {
			output = conn.getOutputStream();
			output.write(query.getBytes());
			output.flush();

		} finally {
			if (output != null) {
				output.close();
			}
		}
		return conn;
	}

	/**
	 * Get a http post connection
	 * 
	 * @param url
	 * @param query
	 * @return conn
	 * @throws IOException
	 * @throws APIConnectionException
	 */
	public static java.net.HttpURLConnection postRequest(String url, String query, RequestMethod method)
			throws IOException, APIConnectionException {
		java.net.HttpURLConnection conn = request(url);
		conn.setDoOutput(true);
		conn.setRequestMethod(RequestMethod.POST.toString());
		if (method.equals(RequestMethod.POST)) {
			conn.setRequestProperty("Content-Type", "application/json");
		}
		conn.setRequestProperty("Content-Length", String.valueOf(query.getBytes().length));
		OutputStream output = null;
		try {
			output = conn.getOutputStream();
			output.write(query.getBytes());
			output.flush();

		} finally {
			if (output != null) {
				output.close();
			}
		}
		return conn;
	}
}
