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

import com.jingtum.sdk.ERROR;
import com.jingtum.sdk.exception.InvalidParameterException;
import com.jingtum.sdk.utils.Utils;

/**
 * @author jzhao
 * @version 1.0
 */
public class Currency extends JingtumObject {
	private String currency; // e.g. CNY, USD
	private String counterparty;

	public Currency(String currency, String counterparty) throws InvalidParameterException {
		if (!Utils.isValidCurrency(currency)) {
			throw new InvalidParameterException(ERROR.INVALID_CURRENCY, currency, null);
		}
		this.currency = currency;

		if (!"".equals(counterparty) && !Utils.isValidAddress(counterparty)) {
			throw new InvalidParameterException(ERROR.INVALID_JINGTUM_ADDRESS, counterparty, null);
		}
		this.counterparty = counterparty;
	}

	/**
	 * @return currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @return counterParty
	 */
	public String getCounterparty() {
		return counterparty;
	}
}
