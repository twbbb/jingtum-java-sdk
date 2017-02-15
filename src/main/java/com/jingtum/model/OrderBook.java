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
 * Order book class
 * changed to new format by simplifing the inputs
 * "price": {
"currency": "USD",
"counterparty": "jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS",
"value": "0.88"
},
"taker_gets_funded": {
"currency": "CNY",
"counterparty": "jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS",
"value": "93"
},
"taker_gets_total": {
"currency": "CNY",
"counterparty": "jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS",
"value": "93"
},
"taker_pays_funded": {
"currency": "USD",
"counterparty": "jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS",
"value": "81.84"
},
"taker_pays_total": {
"currency": "USD",
"counterparty": "jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS",
"value": "81.84"
},
"order_maker": "js46SK8GtxSeGRR6hszxozFxftEnwEK8my",
"sequence": 12,
"passive": false,
"sell": true
}
 to
{ price: '6',
order_maker: 'js46SK8GtxSeGRR6hszxozFxftEnwEK8my',
sequence: 5,
funded: '3',
total: '3' }

 *
 *
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
	 * Get  base currency price as Amount object
	 * @return price
	 *
	 */
	public Amount getAmountPrice() {
		return price;
	}

	/**
	 * Get  base currency price
	 * @return price
	 *
	 */
	public String getPrice() {
		return String.valueOf(price.getValue());
	}

	/**
	* Get funded value of the base currency
	* asks(sell = true)
	 * Funded
	* bids(sell = false)
	 *
	 *
	 */
	public String getFunded() {
		if (sell)
		  return String.valueOf(taker_gets_funded.getValue());
		else
			return String.valueOf(taker_pays_funded.getValue());
	}

	public String getTotal() {
		if (sell)
			return String.valueOf(taker_gets_total.getValue());
		else
			return String.valueOf(taker_pays_total.getValue());
	}

	/**
	 * Get actual amount get
	 * @return taker_gets_funded
	 * obsoleted
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
	/**
	 * Get base and countr Tum pair from the order
	 * @return tum_pair
	 */
	public String getPair() {

		return order_maker;
	}
}
