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
 * PaymentChoice class
 * @author zpli
 * @version 1.0
 */

public class PaymentChoice extends JingtumObject {
	private Amount choice;
	private String key;
	private String path;

	public void setChoice(Amount in_amt){ choice = in_amt;}
	public void setKey(String in_str){ key = in_str;}
	public void setPath(String in_str){ path = in_str;}

	public Amount getChoice() {
		return choice;
	}
	public String getKey() {
		return key;
	}
	public String getPath(){return  path;}
	
}
