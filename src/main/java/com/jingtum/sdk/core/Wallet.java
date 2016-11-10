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
 * @version 1.0
 * Wallet class, main entry point
 */
public class Wallet extends BaseWallet {
	@Expose
	private boolean success; //Operation success or not
	@Expose
	private long ledger;
	@Expose
	private boolean validated;  
	@Expose
	private BalanceCollection balances;
	@Expose
	private PaymentCollection payments;
	@Expose
	private OrderCollection orders;
	@Expose
	private TrustLineCollection trustlines;
	@Expose
	private TransactionCollection transactions;
	@Expose
	private Transaction transaction;
	@Expose
	private RelationCollection relations;
	
	/**
	 * @param secret
	 */
	public Wallet(String secret) {
		super(secret);
	}
	
	public Wallet(String secret, String address) {
		super(secret, address);
	}
	
	//If the account is activated, at least 25 SWT needed
	private boolean activated = false;	
	
	public boolean isActivated() {
		// TODO
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public long getLedger() {
		return ledger;
	}

	public BalanceCollection getBalances() {
		// TODO
		return balances;
	}

	public PaymentCollection getPayments() {
		// TODO
		return payments;
	}

	public OrderCollection getOrders() {
		// TODO
		return orders;
	}

	public TrustLineCollection getTrustlines() {
		// TODO
		return trustlines;
	}

	public TransactionCollection getTransactions() {
		// TODO
		return transactions;
	}

	public Transaction getTransaction() {
		// TODO
		return transaction;
	}

	public RelationCollection getRelations() {
		// TODO
		return relations;
	}
}
