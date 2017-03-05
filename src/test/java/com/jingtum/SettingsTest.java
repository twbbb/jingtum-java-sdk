package com.jingtum;

import com.jingtum.exception.*;
import com.jingtum.model.Settings;
import com.jingtum.model.SettingsCollection;
import com.jingtum.model.SettingsOperation;
import com.jingtum.model.Wallet;
import com.jingtum.model.RequestResult;
import com.jingtum.net.FinGate;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SettingsTest {

	/**
	* 
	* 查询所有余额
	* @throws FailedException 
	* 
	*/
	@Test
	public void testGetSettings() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, NoSuchAlgorithmException, FailedException {

		//设置测试环境
		FinGate.getInstance().setMode(1);
		//已激活的钱包
		Wallet wallet = new Wallet("ssHC71HCbhp6FVLLcK2oyyUVjcAY4");
		Settings bc = wallet.getSettings();
		//测试对象bc是否为null
		assertNotNull(bc);
		String test_domain = "Test1.domain.com";

		SettingsOperation op = new SettingsOperation(wallet);
		op.setDomain(test_domain);

// 3. submit payment
		RequestResult set01 = op.submit();

		System.out.println("result:" + set01.toString());
		bc = wallet.getSettings();
		assertNotNull(bc);
		assertEquals(test_domain,wallet.getDomain());


		
		//正常情况2    钱包未激活时
		Wallet wallet01 = new Wallet("saadV1p5vQeh4N1YdPGo3N3NS7dZo");
		try {
			@SuppressWarnings("unused")
			Settings bc2 = wallet01.getSettings();
	    } catch (InvalidRequestException ex) {
	    	assertEquals("Error type: transaction\n\t Error message: Inactivated Account;Account not found.\n",ex.getMessage());
	    }
	}

	/**
	* 
	* 用currency或counterparty作为条件查询余额
	* SWT 无counterparty
	 * @throws FailedException 
	*/
	@Test
	public void testGetParaSettings() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		//设置测试环境
		FinGate.getInstance().setMode(1);

		//已激活的钱包
		Wallet wallet = new Wallet("snqFcHzRe22JTM8j7iZVpQYzxEEbW");
		//正常情况1   货币为SWT，无银关时
		Settings bc = wallet.getSettings();
		//测试对象bc是否为null
//		assertNotNull(bc);
//		Settings bl;
//		Iterator<Settings> it = bc.getData().iterator();
//		while(it.hasNext())
//		{
//		    bl = (Settings)it.next();
//		    //判断获取账户余额信息是否正确
//		    assertEquals("SWT",bl.getCurrency());
//		    assertEquals("", bl.getIssuer());
//		}
//
//		//正常情况2   货币为SWT，银关为janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f时
//		SettingsCollection bc2 = wallet.getSettings("SWT","janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
//		Settings bl2;
//		Iterator<Settings> it2 = bc2.getData().iterator();
//		while(it2.hasNext())
//		{
//		    bl2 = (Settings)it2.next();
//		    //判断获取账户余额信息是否正确
//		    assertEquals("",String.valueOf(bl2.getValue()));
//		    assertEquals("",bl2.getCurrency());
//		    assertEquals("", bl2.getIssuer());
//		    assertEquals("",String.valueOf(bl2.getFreezed()));
//		}
//
//		//正常情况3   货币为CNY，银关为janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f时
//		SettingsCollection bc3 = wallet.getSettings("CNY","janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
//		Settings bl3;
//		Iterator<Settings> it3 = bc3.getData().iterator();
//		while(it3.hasNext())
//		{
//		    bl3 = (Settings)it3.next();
//		    //判断获取账户余额信息是否正确
//		    assertEquals("CNY",bl3.getCurrency());
//		    assertEquals("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f", bl3.getIssuer());
//		}
//
//		//正常情况4   货币为CNY，银关为空时
//		SettingsCollection bc4 = wallet.getSettings("CNY","");
//		Settings bl4;
//		Iterator<Settings> it4 = bc4.getData().iterator();
//		while(it4.hasNext())
//		{
//		    bl4 = (Settings)it4.next();
//		    //判断获取账户余额信息是否正确
//		    assertEquals("CNY",bl4.getCurrency());
//		}
//
//		//正常情况5   货币为CNY，银关为null时
//		SettingsCollection bc5 = wallet.getSettings("CNY",null);
//		Settings bl5;
//		Iterator<Settings> it5 = bc5.getData().iterator();
//		while(it5.hasNext())
//		{
//		    bl5 = (Settings)it5.next();
//		    //判断获取账户余额信息是否正确
//		    assertEquals("CNY",bl5.getCurrency());
//		}
//
//		//异常情况1   钱包未激活时
//		Wallet wallet01 = new Wallet("saadV1p5vQeh4N1YdPGo3N3NS7dZo","jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z");
//		try {
//			@SuppressWarnings("unused")
//			SettingsCollection bc01 = wallet01.getSettings("SWT","");
//	    } catch (InvalidRequestException ex) {
//	    	assertEquals("Error type: transaction\n\t Error message: Inactivated Account;Account not found.\n",ex.getMessage());
//	    }
//
//		//异常情况2   钱包未激活时
//		Wallet wallet02 = new Wallet("saadV1p5vQeh4N1YdPGo3N3NS7dZo");
//		try {
//			@SuppressWarnings("unused")
//			SettingsCollection bc02 = wallet02.getSettings("CNY","janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
//	    } catch (InvalidRequestException ex) {
//	    	assertEquals("Error type: transaction\n\t Error message: Inactivated Account;Account not found.\n",ex.getMessage());
//	    }
//
//		//异常情况3   货币为无效时，捕获异常
//		try {
//			Wallet wallet03 = new Wallet("snqFcHzRe22JTM8j7iZVpQYzxEEbW");
//			@SuppressWarnings("unused")
//			SettingsCollection bc03 = wallet03.getSettings("CNY01","janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
//	    } catch (InvalidParameterException ex) {
//	    	assertEquals("Invalid currency!",ex.getMessage());
//	    }
//
//		//异常情况4    银关为无效时,捕获异常
//		try {
//			Wallet wallet03 = new Wallet("snqFcHzRe22JTM8j7iZVpQYzxEEbW");
//			@SuppressWarnings("unused")
//			SettingsCollection bc03 = wallet03.getSettings("CNY","janxMdrWE2SUzTqRUtfycH4UGewMMe0000");
//	    } catch (InvalidParameterException ex) {
//	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
//	    }
	}
}
