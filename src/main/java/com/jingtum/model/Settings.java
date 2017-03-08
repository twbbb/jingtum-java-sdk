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
 * @author zpli
 * @version 1.0
 * Settings of the account
 */
import com.google.gson.annotations.Expose;

public class Settings extends JingtumObject{

	@Expose
	private String issuer;
	@Expose
	private boolean disable_master;
	@Expose
	private boolean disallow_swt;
	@Expose
	private String domain ;
	@Expose
	private String email_hash;
	@Expose
	private boolean global_freeze;
	@Expose
	private String message_key;
	@Expose
	private boolean no_freeze;  //read only
	//@Expose
	//private boolean password_spent;
	@Expose
	private boolean require_authorization;
	@Expose
	private boolean require_destination_tag;
	@Expose
	private String regular_key;
	@Expose
	private String transfer_rate ;
	@Expose
	private String wallet_locator;
	@Expose
	private String wallet_size;
	@Expose
	private String nickname;


	/**
	 * Get disable_master flag.
	 * This flag only to be true if
	 * Regular Key is set
	 * @return disable_master
	 */
	public boolean getDisableMaster() {
		return disable_master;
	}


	/**
	 * Get disallow_swt flag
	 * @return disallow_swt
	 */
	public boolean getDisallowSwt(){return disallow_swt;}

	/**
	 * Get the domain string
	 *
	 * @return domain
	 */
	public String getDomain(){return domain;}

	/**
	 * Get the email_hash string
	 *
	 * @return email_hash
	 */
	public String getEmail(){return email_hash;}

	/**
	 * Get the message_key string
	 *
	 * @return message_key
	 */
	public String getMessageKey(){return message_key;}

	/**
	 * Get the nickname string
	 *
	 * @return nickname
	 */
	public String getNickname(){return nickname;}

	/**
	 * Get global_freeze flag
	 *
	 * @return global_freeze
	 */
	public boolean getGlobalFreeze(){return global_freeze;}
	/**
	 * Get disallow_swt flag
	 * @return disallow_swt
	 */
	public boolean getNoFreeze(){return no_freeze;}

	/**
	 * Get require_authorization flag
	 * @return require_authorization
	 */
	public boolean getRequireAuthorization(){return require_authorization;}
	/**
	 * Get require_destination_tag flag
	 * @return require_destination_tag
	 */
	public boolean getRequireDestinationTag(){return require_destination_tag;}
	/**
	 * Get the transfer_rate value
	 * transfer_rate should no less than 1.0
	 * @return transfer_rate
	 */
	public String getTransferRate(){return transfer_rate;}

	/**
	 * Get the 钱包定位器
	 *
	 * @return wallet_locator
	 */
	public String getWalletLocator(){return wallet_locator;}

	/**
	 * Get the transfer_rate value
	 * transfer_rate should no less than 1.0
	 * @return transfer_rate
	 */
	public String getWalletSize(){return wallet_size;}


}
