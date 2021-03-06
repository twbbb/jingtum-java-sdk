/**
 * 
 */
package com.jingtum.model;

/**
 * @author zpli
 * Base account contains the address and the secret
 * and the minimum amount required for an activated
 * account.
 */
public class AccountClass {
	protected static final int MIN_ACTIVATED_AMOUNT = 25;
	protected String address;
	protected String secret;
}
