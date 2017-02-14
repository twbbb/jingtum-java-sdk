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

package com.jingtum.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jingtum.Jingtum;
import com.jingtum.core.crypto.ecdsa.IKeyPair;
import com.jingtum.core.crypto.ecdsa.KeyPair;
import com.jingtum.core.crypto.ecdsa.Seed;


import com.jingtum.exception.InvalidParameterException;
import com.jingtum.model.Amount;
import com.jingtum.model.RelationAmount;
import com.jingtum.model.TrustLine;
import com.jingtum.model.OperationClass.OperationRunnable;

import static com.jingtum.core.config.Config.getB58IdentiferCodecs;
/**
 * Utility class
 * @author jzhao
 * @version 1.0
 */
public class Utility {
	@SuppressWarnings("unused")
	private boolean success;	
	final protected static char[] hexArray = "0123456789abcdef".toCharArray();

    /**
     * Convert byte to hex
     * @param bytes
     * @return String
     */
    public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}    
    /**
     * Build signature string
     * @param address
     * @param secret
     * @return String
     */
    public static String buildSignString(String address, String secret){
/*    	if(!Jingtum.isSignOn()){ // check if the functionality is turned on
    		return "?";
    	}*/

		long timestamp = System.currentTimeMillis();
		String messageString = Jingtum.getSignString() + address + timestamp;
    	IKeyPair keyPair = null;
		try {
			keyPair = Seed.getKeyPair(secret);
		} catch (InvalidParameterException e1) {
			e1.printStackTrace();
		}
		//get hash
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] digest = md.digest(messageString.getBytes());
		byte[] signData = Arrays.copyOfRange(digest, 0, 32);

		//get signature
		byte[] signature = KeyPair.sign(signData , keyPair.priv());

		//transmit hex parameters, they are what we actually need
		String messageHex = Utility.bytesToHex(signData);
		String signatureHex = Utility.bytesToHex(signature);
		String publicKeyHex = keyPair.pubHex();
		
		StringBuffer sb = new StringBuffer();
		sb.append("?k=");
		sb.append(publicKeyHex);
		sb.append("&s=");
		sb.append(signatureHex);
		sb.append("&h=");
		sb.append(messageHex);
		sb.append("&t=");
		sb.append(timestamp);
		return sb.toString();    	
    }    

    /**
     * @param aValue
     * @param aKey
     * @return hmac string
     */
    public static String buildHmac(String aValue, String aKey){
		byte k_ipad[] = new byte[64];
		byte k_opad[] = new byte[64];
		byte keyb[];
		byte value[];
		try {
			keyb = aKey.getBytes("UTF-8");
			value = aValue.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			keyb = aKey.getBytes();
			value = aValue.getBytes();
		}

		Arrays.fill(k_ipad, keyb.length, 64, (byte) 54);
		Arrays.fill(k_opad, keyb.length, 64, (byte) 92);
		for (int i = 0; i < keyb.length; i++) {
			k_ipad[i] = (byte) (keyb[i] ^ 0x36);
			k_opad[i] = (byte) (keyb[i] ^ 0x5c);
		}

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		md.update(k_ipad);
		md.update(value);
		byte dg[] = md.digest();
		md.reset();
		md.update(k_opad);
		md.update(dg, 0, 16);
		dg = md.digest();
		return Utility.bytesToHex(dg);
		
    }
    /**
     * Validate the address and secret pair
     * @param address
     * @param secret
     * @return true if the address and secret is valid
     */
    public static boolean validateKeyPair(String address, String secret){
    	String myAddress = null;
        try {
			myAddress = Seed.computeAddress(secret); // compute address from secret
		} catch (Exception e) {
			return false;
		}
        if(Utility.isNotEmpty(address) && address.equals(myAddress)){
        	return true;
        }
        return false;
    }     
    /**
     * Verify if an address is valid
     * @param address
     * @return true if address is valid
     */
    public static boolean isValidAddress(String address){
		try{
			getB58IdentiferCodecs().decodeAddress(address);
		}
		catch(Exception e){
			return false;
		}
		return true;
    }
    /**
     * Verify if the secret is valid
     * @param secret
     * @return true if secret is valid
     */
	@SuppressWarnings("unused")
	public static boolean isValidSecret(String secret){
        try {
			String myAddress = Seed.computeAddress(secret);
		} catch (Exception e) {
			return false;
		}
        return true;
    }
    /**
     * Verify if the currency is valid
     * @param currency
     * @return true if the currency is valid
     */
    public static boolean isValidCurrency(String currency){
    	// Currently only check the length of the currency
    	if(currency != null && ( currency.length() == 3 || currency.length() == 40 )){
    		return true;
    	}
		return false;    	
    }
    /**
     * Return true if string is not null or empty
     * @param str
     * @return true if string is not null or empty
     */
    public static boolean isNotEmpty(String str){
    	return !isEmpty(str);
    }
    /**
     * Return true if string is null or empty
     * @param str
     * @return true if string is null or empty
     */
    public static boolean isEmpty(String str){
    	return null == str || "".equals(str);
    }
    /**
     * Get the String format of double value
     * @param value
     * @return String 
     */
    public static String doubleToString(double value){
    	DecimalFormat df = new DecimalFormat("#0.0######");
    	return df.format(value);
    }
    /**
     * Check if the JingtumAmount is valid
     * @param amount
     * @return true if the JingtumAmount is valid
     */
    public static boolean isValidAmount(Amount amount){
    	return (amount != null) && isValidCurrency(amount.getCurrency()) && isValidAddress(amount.getCounterparty())
    			|| ((amount != null) && Jingtum.getCurrencySWT().equals(amount.getCurrency()) && "".equals(amount.getCounterparty()));
    }
    /**
     * Check if the RelationAmount is valid
     * @param amount
     * @return true if the RelationAmount is valid
     */
    public static boolean isValidRelationAmount(RelationAmount amount){
    	return (amount != null) && isValidCurrency(amount.getCurrency()) && isValidAddress(amount.getIssuer())
    			|| ((amount != null) && Jingtum.getCurrencySWT().equals(amount.getCurrency()) && "".equals(amount.getIssuer()));
    }
    /**
     * Check if the trustline is valid
     * @param trustline
     * @return true if the trustline is valid
     */
    public static boolean isValidTrustline(TrustLine trustline){
    	return ((trustline != null) && isValidCurrency(trustline.getCurrency()) && isValidAddress(trustline.getCounterparty())) 
    			|| ((trustline != null) && Jingtum.getCurrencySWT().equals(trustline.getCurrency()) && "".equals(trustline.getCounterparty()));
    }
    
    /**
     * sync call
     */
    public static void callback(Runnable runnable){
    	//Later all runnables in one ExecutorService?
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(runnable);
		exec.shutdown();
    }
}