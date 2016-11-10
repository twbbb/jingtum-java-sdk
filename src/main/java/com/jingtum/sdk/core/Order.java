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
 * Order model class
 * 
 * @author jzhao
 * @version 1.0
 */
public class Order extends JingtumObject {
	private Boolean success;
	private String hash;
	private double fee;
	private long sequence;
	private OrderType type;
	private Amount taker_gets; // what payer get
	private Amount taker_pays; // what payer need to pay
	private boolean passive; // passive order or not
	private MyOrder order;
	private String action;
	private Transaction.DirectionType direction;
	private String validated;

	/**
	 * Order type, sell or buy *
	 */
	public enum OrderType {
		sell, buy
	}

	/**
	 * Based on the jason structure returned from Http request, need a private
	 * order structure here
	 *
	 */
	private class MyOrder {
		String account;
		Amount taker_gets;
		Amount taker_pays;
		Boolean passive;
		OrderType type;
		long sequence;

		public String getAccount() {
			return account;
		}

		public Amount getTaker_gets() {
			return taker_gets;
		}

		public Amount getTaker_pays() {
			return taker_pays;
		}

		public Boolean getPassive() {
			return passive;
		}

		public OrderType getType() {
			return type;
		}

		public long getSequence() {
			return sequence;
		}
	}

	/**
	 * Get server state
	 * 
	 * @return validated
	 */
	public String getValidated() {
		return validated;
	}

	/**
	 * Get action type
	 * 
	 * @return action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Get direction type, incoming or outgoing
	 * 
	 * @return direction
	 */
	public Transaction.DirectionType getDirection() {
		return direction;
	}

	/**
	 * Get account
	 * 
	 * @return account
	 */
	public String getAccount() {
		if (order != null) {
			return order.getAccount();
		}
		return null;
	}

	/**
	 * Get order type
	 * 
	 * @return type
	 */
	public OrderType getType() {
		if (order != null) {
			return order.getType();
		}
		return type;
	}

	/**
	 * Get amount pays in the order
	 * 
	 * @return taker_gets
	 */
	public Amount getPay() {
		if (this.taker_gets == null && order != null) {
			return order.getTaker_gets();
		}
		return taker_gets;
	}

	/**
	 * Get amount receive
	 * 
	 * @return taker_pays
	 */
	public Amount getReceive() {
		if (this.taker_gets == null && order != null) {
			return order.getTaker_pays();
		}
		return taker_pays;
	}

	/**
	 * Return true if it is passive transaction
	 * 
	 * @return passive
	 */
	public boolean getPassive() {
		if (order != null) {
			return order.getPassive();
		}
		return passive;
	}

	/**
	 * Return true if the request is successful
	 * 
	 * @return success
	 */
	public boolean getSuccess() {
		return success;
	}

	/**
	 * Get transaction hash value
	 * 
	 * @return hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * Get transaction fee, in SWT
	 * 
	 * @return fee
	 */
	public double getFee() {
		return fee;
	}

	/**
	 * Get transaction sequence number
	 * 
	 * @return sequence
	 */
	public long getSequence() {
		if (order != null) {
			return order.getSequence();
		}
		return sequence;
	}
}
