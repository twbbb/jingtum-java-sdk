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
/**
 * @author jzhao
 * @version 1.0
 * Balance model
 */
import com.google.gson.annotations.Expose;

public class Balance extends JingtumObject{
	//TODO changed the counterparty to issuer
	@Expose
	private double value;
	@Expose
	private String currency;
	@Expose
	private String counterparty;	
	@Expose
	private double freezed;	//Certain amount would be freezed after user post an order.
	/**
	 * Get freeze amount. 
	 * @return freezed
	 */
	public double getFreezed() {
		return freezed;
	}
	/**
	 * Get balance value
	 * @return value
	 */
	public double getValue() {
		return value;
	}	
	/**
	 * Get balance currency unit. eg. SWT, CNY
	 * @return currency
	 */
	public String getCurrency() {
		return currency;
	}
	/**
	 * Get balance currency counterparty
	 * @return counter party
	 */
	public String getCounterparty() {
		return counterparty;
	}

	/**
	 * Get balance currency issuer, which equals the counterparty
	 * in Jingtum System
	 * @return issuer
	 */
	public String getIssuer() {
		return counterparty;
	}
}
