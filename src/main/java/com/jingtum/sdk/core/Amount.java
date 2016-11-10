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
package com.jingtum.sdk.core;

import com.google.gson.annotations.Expose;

/**
 * @author jzhao
 * @version 1.0 Currency class
 */
public class Amount extends JingtumObject {
	@Expose
	private double value;
	@Expose
	private String currency;
	// used in trust line related methods
	@Expose
	private String issuer; 
	@Expose
	private String counterparty;

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getCounterparty() {
		return counterparty;
	}

	public void setCounterparty(String counterparty) {
		this.counterparty = counterparty;
	}
}
