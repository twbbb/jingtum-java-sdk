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
 * IssueRecord model
 */

public class IssueRecord extends JingtumObject{
	private boolean r9_status;
	private String r3_order;
	private String r4_currency;
	private String r5_amount;
	private String r6_account;
	private long r7_date;
	private String r8_tx;
	
	/**
	 * Get issue status, true if issue successfully
	 * @return true or false
	 */
	public boolean getStatus() {
		return r9_status;
	}
	/**
	 * Get issue order number
	 * @return order number
	 */
	public String getOrder() {
		return r3_order;
	}
	/**
	 * Get custom tum code
	 * @return currency
	 */
	public String getCurrency() {
		return r4_currency;
	}
	/**
	 * Get custom tum issue amount
	 * @return amount
	 */
	public double getAmount() {
		return Double.parseDouble(r5_amount);
	}
	/**
	 * Get custom tum issued account
	 * @return account
	 */
	public String getAccount() {
		return r6_account;
	}
	/**
	 * Get custom tum issue date in UNIXTIME
	 * @return date
	 */
	public long getDate() {
		return r7_date;
	}
	/**
	 * Get issue tx hash number
	 * @return tx number
	 */
	public String getTxHash() {
		return r8_tx;
	}	
}
