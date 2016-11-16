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
import com.jingtum.util.Utility;
/**
 * @author jzhao
 * @version 1.0
 * Trust line class
 */
public class TrustLine extends JingtumObject{
	private String account;
	private String counterparty;
	private String currency;
	private double limit;	
	/**
	 * Get current account
	 * @return account
	 */
	public String getAccount() {
		return account;
	}
	/**
	 * Get trusted counter party
	 * @return counter party
	 */
	public String getCounterparty() {
		return counterparty;
	}
	/**
	 * Get trusted currency unit
	 * @return currency
	 */
	public String getCurrency() {
		return currency;
	}
	/**
	 * Get trusted amount limit
	 * @return limit
	 */
	public double getLimit() {
		return limit;
	}
	/**
	 * Set trust line counter party
	 * @param counterparty
	 * @throws InvalidParameterException 
	 */
	public void setCounterparty(String counterparty) throws InvalidParameterException {
    	if(!"".equals(counterparty) && !Utility.isValidAddress(counterparty)){
    		throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,counterparty,null);
    	}
		this.counterparty = counterparty;
	}
	/**
	 * Set trust line currency
	 * @param currency
	 * @throws InvalidParameterException 
	 */
	public void setCurrency(String currency) throws InvalidParameterException {
		if(!Utility.isValidCurrency(currency)){
			throw new InvalidParameterException(JingtumMessage.INVALID_CURRENCY,currency,null);
		}
		this.currency = currency;
	}
	/**
	 * Set trust line limit
	 * @param limit
	 * @throws InvalidParameterException 
	 */
	public void setLimit(double limit) throws InvalidParameterException {
		if(limit < 0){
			throw new InvalidParameterException(JingtumMessage.INVALID_LIMIT,currency,null);
		}
		this.limit = limit;
	}		
}
