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
 * @version 1.0 Relation class
 */
public class Relation extends JingtumObject {
	private String account;
	private RelationType type;
	private String counterparty;
	private RelationAmount amount;

	/**
	 * Relation type: authorize, friend, subscribe, all
	 */
	public enum RelationType {
		authorize, friend, subscribe, all
	}

	/**
	 * Get current account.
	 * 
	 * @return account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * Get relation type
	 * 
	 * @return type
	 */
	public RelationType getType() {
		return type;
	}

	/**
	 * Get relation counter party
	 * 
	 * @return counter party
	 */
	public String getCounterparty() {
		return counterparty;
	}

	/**
	 * Get amount
	 * 
	 * @return amount
	 */
	public RelationAmount getAmount() {
		return amount;
	}
}
