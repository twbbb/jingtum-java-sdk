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

import com.jingtum.util.Utility;

/**
 * Momo class
 * @author zpli
 * @version 1.0
 */

public class Message extends JingtumObject {
	private String destination_account;
	private String message_hash;

	public void setDestinationAccount(String in_str){
		if (Utility.isValidAddress(in_str))
			destination_account = in_str;
	}

	public void setMessage(String in_str){ message_hash = in_str;}

	public String getDestinationAccount() {
		return destination_account;
	}
	public String getMessage() {
		return message_hash;
	}
	
}
