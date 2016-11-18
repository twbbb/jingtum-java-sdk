package com.jingtum;

import static org.junit.Assert.*;

import com.jingtum.net.JingtumFingate;
import org.junit.Test;

import com.jingtum.exception.APIConnectionException;
import com.jingtum.exception.APIException;
import com.jingtum.exception.AuthenticationException;
import com.jingtum.exception.ChannelException;
import com.jingtum.exception.InvalidParameterException;
import com.jingtum.exception.InvalidRequestException;
import com.jingtum.model.Wallet;

public class WalletTest {
	
	/**
	*
	* No parameters
	*/
	@Test
	public void testWallet() throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException {
		
		//正常创建钱包
		Wallet wallet = JingtumFingate.getTestInstance().createWallet();
		assertEquals("j", (wallet.getAddress()).substring(0, 1));
		assertEquals("s", (wallet.getSecret()).substring(0, 1));
	}
    
	/**
	*
	* Having parameters
	* @throws InvalidParameterException 
	*/
	@Test
	public void testParametersWallet() throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InvalidParameterException {
		
		//正常创建钱包
		Wallet wallet2 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW");
		assertEquals("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa", wallet2.getAddress());
		assertEquals("snqFcHzRe22JTM8j7iZVpQYzxEEbW", wallet2.getSecret());
		
		//异常情况1     地址为空时
		try {
			@SuppressWarnings("unused")
			Wallet wallet01 = new Wallet("","snqFcHzRe22JTM8j7iZVpQYzxEEbW");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//异常情况2     密钥为空时		
		try {
			@SuppressWarnings("unused")
			Wallet wallet02 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//异常情况3   地址和密钥都为空时
		try {
			@SuppressWarnings("unused")
			Wallet wallet03 = new Wallet("","");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//异常情况4    地址为null时
		try {
			@SuppressWarnings("unused")
			Wallet wallet04 = new Wallet(null,"snqFcHzRe22JTM8j7iZVpQYzxEEbW");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//异常情况5     密钥为null时
		try {
			@SuppressWarnings("unused")
			Wallet wallet05 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa",null);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//异常情况6    地址和密钥都为null时
		try {
			@SuppressWarnings("unused")
			Wallet wallet06 = new Wallet(null,null);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//异常情况07    地址和密钥不配对时
		try {
			@SuppressWarnings("unused")
			Wallet wallet07 = new Wallet("snqFcHzRe22JTM8j7iZVpQYzxEEbW","js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//异常情况8    地址为无效时
        try {
        	@SuppressWarnings("unused")
			Wallet wallet08 = new Wallet("111ssssssss","snqFcHzRe22JTM8j7iZVpQYzxEEbW");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
        
        //异常情况9  密钥为无效时
		try {
			@SuppressWarnings("unused")
			Wallet wallet09 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","aaaaaa1111");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//异常情况10    地址和密钥都无效时
		try {
			@SuppressWarnings("unused")
			Wallet wallet10 = new Wallet("@@@@bbbb2222ssssssssssssssssssssssssss001","***aaa1111");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
	}
	
	/**
	*
	*  secret parameter
	*/
	@Test
	public void testSecretWallet() throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InvalidParameterException {		
		//正常创建钱包    密钥有效时
		Wallet wallet = new Wallet("snwjtucx9vEP7hCazriMbVz8hFiK9");
		assertEquals("snwjtucx9vEP7hCazriMbVz8hFiK9", wallet.getSecret());
		assertEquals("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S", wallet.getAddress());
        
		//异常情况1   密钥为空时	
		try {
			@SuppressWarnings("unused")
			Wallet wallet01 = new Wallet("");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum account secret!",ex.getMessage());
	    }
		
		//异常情况2   密钥为null时	
		try {
			@SuppressWarnings("unused")
			Wallet wallet02 = new Wallet(null);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum account secret!",ex.getMessage());
	    }
		
		//异常情况3   密钥为无效时	
		try {
			@SuppressWarnings("unused")
			Wallet wallet03 = new Wallet("1111111111111ssssssssaaaaa22222");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum account secret!",ex.getMessage());
	    }
	}
}