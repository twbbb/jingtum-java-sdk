/*
 * Copyright 2017 jingtum Inc..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jingtum.model;

/**
 *
 * @author zpli
 */
//import com.jingtum.net.APIServer;
//import com.jingtum.net.FinGate;

public class OperationClass extends JingtumObject{


	private String src_address;
	private String src_secret;

	public Boolean validate = true;

	//String used to identify the asy mode or sy mode
	protected static final String VALIDATED = "?validated=";

	/**
	 * Get source address in the Operation
	 * @return source account address
	 */
	public String getSrcAddress() {
		return src_address;
	}
	/**
	 * Get source secret in the Operation
	 * @return src_secret
	 */
	public String getSrcSecret() {
		return src_secret;
	}
	
	/**
	 * Waiting for validated result or not
	 * @return validate
	 */
	public Boolean getValidate() {
		return validate;
	}


	public void setSrcAddress(String in_address){src_address = in_address;}

	public void setSrcSecret(String in_secret){src_secret = in_secret;}

	public void setValidate(Boolean in_bool){validate = in_bool;}
}
