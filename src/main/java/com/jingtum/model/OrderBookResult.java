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
import com.jingtum.exception.InvalidParameterException;

/**
 * Order book result class, used in getOrdeBook method
 * @author jzhao
 * @version 1.0
 */
public class OrderBookResult extends JingtumObject{
	private String order_book;
	private String pair;
	private boolean success;
	private boolean validated;
	private OrderBookCollection bids;
	private OrderBookCollection asks;	
	/**
	 * Get currency pair in the order. e.g. CNY+jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS/USD+jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS
	 * @return order_book
	 */
	public String getOrderbook() {
		return order_book;
	}
	public String getPair() throws InvalidParameterException{
		if (pair==null){
			//If the pair is not set yet, create it from the order_maker String
			//

			if (order_book.length() < 1)
				throw new InvalidParameterException(JingtumMessage.INVALID_TUM_PAIR, "Empty tum pair in order book", null);
//			String[] temp_str = order_book.split("+");
//			StringBuffer new_pair = new StringBuffer();
//			for ( int i = 0; i < temp_str.length; i ++ ) {
//				new_pair.append(temp_str[i]);
//				if (i < temp_str.length -1)
//					new_pair.append(":");
//			}
			System.out.println("Init pair");
			this.pair = order_book.replace("+",":");//new_pair.toString();
			System.out.println(order_book);
			System.out.println(pair);

		}
		return pair;
	}
	/**
	 * Return true if the request is successful
	 * @return success
	 */
	public boolean getSuccess() {
		return success;
	}
	/**
	 * Return server status
	 * @return validated
	 */
	public boolean getValidated() {
		return validated;
	}
	/**
	 * Get bid order book collection
	 * @return bids
	 */
	public OrderBookCollection getBids() {
		return bids;
	}
	/**
	 * Get ask order book collection
	 * @return asks
	 */
	public OrderBookCollection getAsks() {
		return asks;
	}
}
