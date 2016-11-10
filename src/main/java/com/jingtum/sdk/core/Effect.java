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
 * Effect class, additional information about payments or transactions
 * 
 * @author jzhao *
 * @version 1.0
 */
public class Effect extends JingtumObject {
	private EffectType effect;
	private Order.OrderType type;
	private Amount gets;
	private Amount pays;
	private String price;
	private boolean cancelled;
	private boolean remaining;
	private int seq;
	private Amount got;
	private Amount paid;
	private String counterparty;
	private Amount limit;
	private String currency;
	private Amount from;
	private Amount to;
	private Amount amount;

	/**
	 * Effect type: offer_funded, offer_partially_funded, offer_cancelled,
	 * offer_created, offer_bought, trust_create_local, trust_create_remote,
	 * trust_change_local, trust_change_remote, trust_change_balance,
	 * balance_change
	 */
	public enum EffectType {
		offer_funded, offer_partially_funded, offer_cancelled, offer_created, offer_bought, trust_create_local, trust_create_remote, trust_change_local, trust_change_remote, trust_change_balance, balance_change
	}

	/**
	 * Order sequence number
	 * 
	 * @return seq
	 */
	public int getSeq() {
		return seq;
	}

	/**
	 * Get effect type
	 * 
	 * @return effect
	 */
	public EffectType getEffect() {
		return effect;
	}

	/**
	 * Get order type, sell or buy
	 * 
	 * @return type
	 */
	public Order.OrderType getType() {
		return type;
	}

	/**
	 * For offer_funded, offer_cancelled, offer_created, get "get" currency
	 * amount
	 * 
	 * @return gets
	 */
	public Amount getGets() {
		return gets;
	}

	/**
	 * For offer_funded, offer_cancelled, offer_created, get pays currency
	 * amount
	 * 
	 * @return pays
	 */
	public Amount getPays() {
		return pays;
	}

	/**
	 * Get price
	 * 
	 * @return price
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * For offer_partially_funded
	 * 
	 * @return true if remaining order cancelled
	 */
	public boolean getCancelled() {
		return cancelled;
	}

	/**
	 * For offer_partially_funded, get remaining order amount
	 * 
	 * @return remaining
	 */
	public boolean getRemaining() {
		return remaining;
	}

	/**
	 * For offer_bought, get got amount
	 * 
	 * @return got
	 */
	public Amount getGot() {
		return got;
	}

	/**
	 * For offer_bought, get paid amount
	 * 
	 * @return paid
	 */
	public Amount getPaid() {
		return paid;
	}

	/**
	 * For trust_create_local and trust_change_local, get counter party trusted
	 * 
	 * @return counter party
	 */
	public String getCounterparty() {
		return counterparty;
	}

	/**
	 * For trust_create_local, get amount trusted
	 * 
	 * @return limit
	 */
	public Amount getLimit() {
		return limit;
	}

	/**
	 * For trust_change_local, get changed trusted currency unit
	 * 
	 * @return currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * For trust_change_local, trust_change_remote
	 * 
	 * @return from
	 */
	public Amount getFrom() {
		return from;
	}

	/**
	 * For trust_change_local, trust_change_remote
	 * 
	 * @return to
	 */
	public Amount getTo() {
		return to;
	}

	/**
	 * For trust_change_balance, balance_change
	 * 
	 * @return amount
	 */
	public Amount getAmount() {
		return amount;
	}
}
