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
 * Notification class
 * @author jzhao
 * @version 1.0
 */
public class Notification extends JingtumObject {
	private String account;
	private String type;
	private Transaction.DirectionType direction;
	private String state;
	private String result;
	private String hash;
	private long date;
	private String previous_hash;
	private String next_hash;	
	/**
	 * Get notification related account
	 * @return account
	 */
	public String getAccount() {
		return account;
	}
	/**
	 * Get notification type
	 * @return type
	 */
	public String getType() {
		return type;
	}
	/**
	 * Get payment direction, incoming or outgoing
	 * @return direction
	 */
	public Transaction.DirectionType getDirection() {
		return direction;
	}
	/**
	 * Get transaction state
	 * @return state
	 */
	public String getState() {
		return state;
	}
	/**
	 * Get transaction result
	 * @return result
	 */
	public String getResult() {
		return result;
	}
	/**
	 * Get transaction hash value
	 * @return hash
	 */
	public String getHash() {
		return hash;
	}
	/**
	 * Get transaction time, in UNIXTIME
	 * @return date
	 */
	public long getDate() {
		return date;
	}
	/**
	 * Get previous transaction URL
	 * @return previous_hash
	 */
	public String getPreviousHash() {
		return previous_hash;
	}
	/**
	 * Get next transaction URL
	 * @return next_hash
	 */
	public String getNextHash() {
		return next_hash;
	}
}
