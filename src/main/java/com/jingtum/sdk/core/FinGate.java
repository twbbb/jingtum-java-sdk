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

import com.jingtum.sdk.core.crypto.ecdsa.Seed;
import com.jingtum.sdk.exception.InvalidParameterException;

/**
 * @author Administrator
 *
 */
public class FinGate extends BaseWallet {

	/**
	 * @param secret
	 */
	public FinGate(String secret) {
		super(secret);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create wallet
	 * @return wallet
	 */
	public Wallet createWallet() {
		String secret = Seed.generateSecret();
		String address = null;
		try {
			address = Seed.computeAddress(secret);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		}
		return new Wallet(address, secret);
	}
	
	public void activateWallet(BaseWallet bw) {
		// TODO
	}
	
	/**
	 * Open connection to Web Socket server
	 * 
	 * @return true if opened connection successfully
	 * @throws APIException
	 * @throws InvalidParameterException
	 */
	public void connect() {
		// TODO
	}
	
	/**
	 * Close Web Socket connection
	 * 
	 * @return true if successfully closed connection
	 * @throws APIException
	 */
	public void disconnect() {
		// TODO
	}
	
	/**
	 * Get order book
	 * 
	 */
	public OrderBookResult getOrderBook(Amount base, Amount counter) {
		// TODO
		return null;
	}
	
	public void issueTum() {
		// TODO
	}
	
	public void queryIssue() {
		// TODO
	}
	
	public void queryTum() {
		// TODO
	}
}
