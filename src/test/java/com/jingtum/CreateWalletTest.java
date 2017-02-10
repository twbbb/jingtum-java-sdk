package com.jingtum;

import com.jingtum.exception.*;
import com.jingtum.net.FinGate;
import com.jingtum.net.JingtumFingate;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

/*
 * Test the FinGate function to create a new wallet address and send active amount to the address
 */
public class CreateWalletTest {

	@Test
	public void testNewWallet() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, NoSuchAlgorithmException {
		//初始化Gateway
		//设置测试环境
		FinGate.getInstance().setMode(1);
		FinGate.getInstance().setAccount("snqFcHzRe22JTM8j7iZVpQYzxEEbW"); //FinGate地址密码

		//正常情况    钱包尚未激活且地址正确时
		boolean isActivated = FinGate.getInstance().activateWallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S"); //支付，参数为：获取方地址
        //判断钱包激活是否成功
		assertEquals(true, isActivated);
		
		//异常情况1  初始化Gateway时，地址为空时
		try {
			FinGate.getInstance().setAccount("snqFcHzRe22JTM8j7iZVpQYzxEEbW","");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//异常情况2  初始化Gateway时，密钥为空时
		try {
			FinGate.getInstance().setAccount("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa", "");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//异常情况3  初始化Gateway时，地址和密钥都为空时
		try {
			FinGate.getInstance().setAccount("", "");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//异常情况4  初始化Gateway时，地址为null时
		try {
			FinGate.getInstance().setAccount(null, "snqFcHzRe22JTM8j7iZVpQYzxEEbW");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//异常情况5  初始化Gateway时，密钥为null时
		try {
			FinGate.getInstance().setAccount("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa",null);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//异常情况6  初始化Gateway时，地址和密钥都为null时
		try {
			FinGate.getInstance().setAccount(null,null);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//异常情况7  初始化Gateway时，地址和密钥不配对时
		try {
			FinGate.getInstance().setAccount("snqFcHzRe22JTM8j7iZVpQYzxEEbW", "js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }	
		
		//异常情况8  初始化Gateway时，地址无效时
		try {
			FinGate.getInstance().setAccount("11111sssssssss", "js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//异常情况9  初始化Gateway时，密钥无效时	
		try {
			FinGate.getInstance().setAccount("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa", "22222aaaaaaaaa");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//异常情况10  初始化Gateway时，地址和密钥都无效时
		try {
			FinGate.getInstance().setAccount("11111sssssssss", "22222aaaaaaaaa");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid address or secret!",ex.getMessage());
	    }
		
		//调用激活方法时，异常情况11  没有初始化Gateway时
		//Jingtum.setGateWay("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa", "snqFcHzRe22JTM8j7iZVpQYzxEEbW");
		try {
			@SuppressWarnings("unused")
			boolean isActivated11 = FinGate.getInstance().activateWallet("jfNT7apcuN6jkwn74epTB4XcPJMx9YMfP4"); //支付，参数为：获取方地址
	    } catch (APIException ex) {
	    	assertEquals("Gateway is not initialized",ex.getMessage());
	    }
		
		//调用激活方法时，异常情况12  地址为null时
		FinGate.getInstance().setAccount("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa", "snqFcHzRe22JTM8j7iZVpQYzxEEbW");
		try {
			@SuppressWarnings("unused")
			boolean isActivated12 = FinGate.getInstance().activateWallet(null); //支付，参数为：获取方地址
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
        
		//调用激活方法时，异常情况13  地址无效时
		FinGate.getInstance().setAccount("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa", "snqFcHzRe22JTM8j7iZVpQYzxEEbW");
		try {
			@SuppressWarnings("unused")
			boolean isActivated13 = FinGate.getInstance().activateWallet("11111111111111ssssss"); //支付，参数为：获取方地址
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//调用激活方法时，异常情况14   地址空时
		FinGate.getInstance().setAccount("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa", "snqFcHzRe22JTM8j7iZVpQYzxEEbW");
		try {
			@SuppressWarnings("unused")
			boolean isActivated14 = FinGate.getInstance().activateWallet(""); //支付，参数为：获取方地址
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
	}
}
