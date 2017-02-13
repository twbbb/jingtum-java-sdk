package com.jingtum;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import com.jingtum.net.FinGate;
import org.junit.Test;

import com.jingtum.exception.APIConnectionException;
import com.jingtum.exception.APIException;
import com.jingtum.exception.AuthenticationException;
import com.jingtum.exception.ChannelException;
import com.jingtum.exception.FailedException;
import com.jingtum.exception.InvalidParameterException;
import com.jingtum.exception.InvalidRequestException;
import com.jingtum.model.Balance;
import com.jingtum.model.BalanceCollection;
import com.jingtum.model.Wallet;

public class BalancesTest {

	/**
	* 
	* 查询所有余额
	* @throws FailedException 
	* 
	*/
	@Test
	public void testGetBalance() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, NoSuchAlgorithmException, FailedException {

		//设置测试环境
		FinGate.getInstance().setMode(1);
		//已激活的钱包
		Wallet wallet = new Wallet("ssHC71HCbhp6FVLLcK2oyyUVjcAY4");
		BalanceCollection bc = wallet.getBalance();
		//测试对象bc是否为null
		assertNotNull(bc);
		
		Iterator<Balance> it = bc.getData().iterator();
		while(it.hasNext())
		{   
			Balance bl = (Balance)it.next();
			assertNotNull(bl.getValue());
		}
		
		//正常情况2    钱包未激活时
		Wallet wallet01 = new Wallet("saadV1p5vQeh4N1YdPGo3N3NS7dZo");
		try {
			@SuppressWarnings("unused")
			BalanceCollection bc1 = wallet01.getBalance();
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
	public void testGetParaBalance() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		//设置测试环境
		FinGate.getInstance().setMode(1);

		//已激活的钱包
		Wallet wallet = new Wallet("snqFcHzRe22JTM8j7iZVpQYzxEEbW");
		//正常情况1   货币为SWT，无银关时
		BalanceCollection bc = wallet.getBalance("SWT","");
		//测试对象bc是否为null
		assertNotNull(bc);
		Balance bl;
		Iterator<Balance> it = bc.getData().iterator();
		while(it.hasNext())
		{	
		    bl = (Balance)it.next();
		    //判断获取账户余额信息是否正确
		    assertEquals("SWT",bl.getCurrency());
		    assertEquals("", bl.getIssuer());
		}
		
		//正常情况2   货币为SWT，银关为janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f时
		BalanceCollection bc2 = wallet.getBalance("SWT","janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
		Balance bl2;
		Iterator<Balance> it2 = bc2.getData().iterator();
		while(it2.hasNext())
		{	
		    bl2 = (Balance)it2.next();
		    //判断获取账户余额信息是否正确
		    assertEquals("",String.valueOf(bl2.getValue()));
		    assertEquals("",bl2.getCurrency());
		    assertEquals("", bl2.getIssuer());
		    assertEquals("",String.valueOf(bl2.getFreezed()));
		}
		
		//正常情况3   货币为CNY，银关为janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f时
		BalanceCollection bc3 = wallet.getBalance("CNY","janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
		Balance bl3;
		Iterator<Balance> it3 = bc3.getData().iterator();
		while(it3.hasNext())
		{	
		    bl3 = (Balance)it3.next();
		    //判断获取账户余额信息是否正确
		    assertEquals("CNY",bl3.getCurrency());
		    assertEquals("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f", bl3.getIssuer());
		}
		
		//正常情况4   货币为CNY，银关为空时
		BalanceCollection bc4 = wallet.getBalance("CNY","");
		Balance bl4;
		Iterator<Balance> it4 = bc4.getData().iterator();
		while(it4.hasNext())
		{	
		    bl4 = (Balance)it4.next();
		    //判断获取账户余额信息是否正确
		    assertEquals("CNY",bl4.getCurrency());
		}
		
		//正常情况5   货币为CNY，银关为null时
		BalanceCollection bc5 = wallet.getBalance("CNY",null);
		Balance bl5;
		Iterator<Balance> it5 = bc5.getData().iterator();
		while(it5.hasNext())
		{	
		    bl5 = (Balance)it5.next();
		    //判断获取账户余额信息是否正确
		    assertEquals("CNY",bl5.getCurrency());
		}
        
		//异常情况1   钱包未激活时
		Wallet wallet01 = new Wallet("saadV1p5vQeh4N1YdPGo3N3NS7dZo","jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z");
		try {
			@SuppressWarnings("unused")
			BalanceCollection bc01 = wallet01.getBalance("SWT","");
	    } catch (InvalidRequestException ex) {
	    	assertEquals("Error type: transaction\n\t Error message: Inactivated Account;Account not found.\n",ex.getMessage());
	    }
		
		//异常情况2   钱包未激活时
		Wallet wallet02 = new Wallet("saadV1p5vQeh4N1YdPGo3N3NS7dZo");
		try {
			@SuppressWarnings("unused")
			BalanceCollection bc02 = wallet02.getBalance("CNY","janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
	    } catch (InvalidRequestException ex) {
	    	assertEquals("Error type: transaction\n\t Error message: Inactivated Account;Account not found.\n",ex.getMessage());
	    }
		
		//异常情况3   货币为无效时，捕获异常
		try {
			Wallet wallet03 = new Wallet("snqFcHzRe22JTM8j7iZVpQYzxEEbW");
			@SuppressWarnings("unused")
			BalanceCollection bc03 = wallet03.getBalance("CNY01","janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid currency!",ex.getMessage());
	    }
		
		//异常情况4    银关为无效时,捕获异常
		try {
			Wallet wallet03 = new Wallet("snqFcHzRe22JTM8j7iZVpQYzxEEbW");
			@SuppressWarnings("unused")
			BalanceCollection bc03 = wallet03.getBalance("CNY","janxMdrWE2SUzTqRUtfycH4UGewMMe0000");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
	}
}
