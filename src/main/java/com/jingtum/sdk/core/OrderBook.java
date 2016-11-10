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

package com.jingtum.sdk.core;
/**
 * @author jzhao
 * @version 1.0
 * Order book class
 */
public class OrderBook extends JingtumObject {	
	private Amount price;
	private Amount taker_gets_funded;
	private Amount taker_gets_total;
	private Amount taker_pays_funded;
	private Amount taker_pays_total;
	private String order_maker;
	private long sequence;
	private boolean passive;
	private boolean sell;	
	/**
	 * Get current price
	 * @return price
	 */
	public Amount getPrice() {
		return price;
	}
	/**
	 * Get actual amount get
	 * @return taker_gets_funded
	 */
	public Amount getTaker_gets_funded() {
		return taker_gets_funded;
	}
	/**
	 * Get total amount get
	 * @return taker_gets_total
	 */
	public Amount getTaker_gets_total() {
		return taker_gets_total;
	}
	/**
	 * Get actual amount pay
	 * @return taker_pays_funded
	 */
	public Amount getTaker_pays_funded() {
		return taker_pays_funded;
	}
	/**
	 * Get total amount pay
	 * @return taker_pays_total
	 */
	public Amount getTaker_pays_total() {
		return taker_pays_total;
	}
	/**
	 * Get order maker
	 * @return order_maker
	 */
	public String getOrder_maker() {
		return order_maker;
	}
	/**
	 * Get transaction sequence
	 * @return sequence
	 */
	public long getSequence() {
		return sequence;
	}
	/**
	 * Return true if transaction is passive
	 * @return passive
	 */
	public boolean getPassive() {
		return passive;
	}
	/**
	 * Return true if the order is sell
	 * @return sell
	 */
	public boolean getSell() {
		return sell;
	}	
}
