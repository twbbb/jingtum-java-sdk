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

/**
 * @author jzhao
 * @version 1.0
 * Wallet class, main entry point
 */
public abstract class Operation {
	private BaseWallet wallet;
	private String method;
	private String memo;
	private String url;
	// default is async mode
	private boolean validated = false;
	
	public Operation(BaseWallet bw) {
		this.wallet = bw;
	}
	
	public BaseWallet getWallet() {
		return wallet;
	}
	public void setWallet(BaseWallet wallet) {
		this.wallet = wallet;
	}

	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}

	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getUrl() {
		return url;
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	protected abstract String payload();
	
	public void submit() {
		// TODO
	}
}
