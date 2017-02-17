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

import com.google.gson.annotations.Expose;
import com.jingtum.JingtumMessage;
import com.jingtum.exception.InvalidParameterException;
import com.jingtum.util.Utility;

/**
 * @author jzhao
 * @version 1.0
 * Currency class
 */

public class Amount extends JingtumObject{
	@Expose
	private double value;
	@Expose
	private String currency;
	@Expose
	private String issuer; //used in trust line related methods
	//@Expose
	//private String counterparty;
	/**
	 * Set JingtumCurrency
	 * @param jtc
	 * @throws InvalidParameterException
	 */
	public void setJingtumCurrency(Currency jtc) throws InvalidParameterException{
		if(jtc == null){
			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_CURRENCY,currency,null);
		}
		this.currency = jtc.getCurrency();
		this.issuer = jtc.getIssuer();
	}

	public boolean isSWT(){
		if (currency.compareTo("SWT") == 0)
			return true;
		else
			return false;
	}
	/**
	 * Get value
	 * @return value
	 */
	public double getValue() {
		return value;
	}
	/**
	 * Set value
	 * @param value
	 * @throws InvalidParameterException
	 */
	public void setValue(double value) throws InvalidParameterException {
		if(value < 0){
			throw new InvalidParameterException(JingtumMessage.INVALID_VALUE,currency,null);
		}
		this.value = value;
	}
	/**
	 * Get currency
	 * @return currency
	 */
	public String getCurrency() {
		return currency;
	}
	/**
	 * Set currency
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
	 * Get issuer
	 * @return issuer
	 */
	public String getIssuer() {
		return issuer;
	}
	/**
	 * Set issuer
	 * @param issuer
	 * @throws InvalidParameterException
	 */
	public void setIssuer(String issuer) throws InvalidParameterException {
		if(issuer!="" && !Utility.isValidAddress(issuer)){
			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,issuer,null);
		}
		this.issuer = issuer;
	}		
	/**
	 * Get issuer
	 * @return issuer
	 */
//	public String getIssuer() {
//		return issuer;
//	}
	/**
	 * Set counter party
	 * @param issuer
	 * @throws InvalidParameterException 
	 */
//	public void setCounterparty(String counterparty) throws InvalidParameterException{
//		if(!"".equals(counterparty) && !Utility.isValidAddress(counterparty)){
//			throw new InvalidParameterException(JingtumMessage.INVALID_JINGTUM_ADDRESS,counterparty,null);
//		}
//		this.counterparty = counterparty;
//	}
}

