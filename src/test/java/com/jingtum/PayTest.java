package com.jingtum;

import static org.junit.Assert.*;

import java.util.Iterator;

import com.jingtum.model.*;
import com.jingtum.net.JingtumAPIAndWSServer;
import org.junit.Test;

import com.jingtum.exception.APIConnectionException;
import com.jingtum.exception.APIException;
import com.jingtum.exception.AuthenticationException;
import com.jingtum.exception.ChannelException;
import com.jingtum.exception.FailedException;
import com.jingtum.exception.InvalidParameterException;
import com.jingtum.exception.InvalidRequestException;

public class PayTest {

	/**
	*
	* Pay test
	 * @throws FailedException 
	*  
	*/
	@Test
	public void testPay() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		//已有钱包1余额充足  作为支付方
		Wallet wallet1 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); //如进行支付，密钥为必须参数
		Amount jtc = new Amount(); //构建支付的货币
		jtc.setCounterparty(""); //货币发行方
		jtc.setCurrency("SWT"); //货币单位
		jtc.setValue(18); //金额
		
		//正常情况1  是否等待结果为true时
		RequestResult payment01 = wallet1.submitPayment("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",jtc, true, JingtumAPIAndWSServer.getTestInstance().getNextUUID() ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
		assertEquals(true,payment01.getSuccess()); //交易是否成功
		assertEquals("validated",payment01.getState()); //交易状态
		assertEquals("tesSUCCESS",payment01.getResult()); //支付服务器结果
       
		//正常情况2  资源号为空时
		RequestResult payment02 = wallet1.submitPayment("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",jtc, true, "" ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
		assertEquals(true,payment02.getSuccess()); //交易是否成功
		assertEquals("validated",payment02.getState()); //交易状态
		assertEquals("tesSUCCESS",payment02.getResult()); //支付服务器结果		
         
		//正常情况3  资源号为null时
		RequestResult payment03 = wallet1.submitPayment("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",jtc, true, null ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
		assertEquals(true,payment03.getSuccess()); //交易是否成功
		assertEquals("validated",payment03.getState()); //交易状态
		assertEquals("tesSUCCESS",payment03.getResult()); //支付服务器结果
		
		//正常情况  是否等待结果为false时
		RequestResult payment08 = wallet1.submitPayment("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",jtc, false, JingtumAPIAndWSServer.getTestInstance().getNextUUID() ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
		assertEquals(true,payment08.getSuccess()); //交易是否成功
		assertEquals(null,payment08.getState()); //交易状态
		assertEquals(null,payment08.getResult()); //支付服务器结果
		
		//异常情况1  接收方地址为空时
		try {
			@SuppressWarnings("unused")
			RequestResult payment1 = wallet1.submitPayment("",jtc, true, JingtumAPIAndWSServer.getTestInstance().getNextUUID() ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//异常情况2  接收方地址为null时
		try {
			@SuppressWarnings("unused")
			RequestResult payment2 = wallet1.submitPayment(null,jtc, true, JingtumAPIAndWSServer.getTestInstance().getNextUUID() ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//异常情况3  接收方地址为无效时
		try {
			@SuppressWarnings("unused")
			RequestResult payment3 = wallet1.submitPayment("11111ssssssssssss",jtc, true, JingtumAPIAndWSServer.getTestInstance().getNextUUID() ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//异常情况4 pay为null时
		try {
			@SuppressWarnings("unused")
			RequestResult payment4 = wallet1.submitPayment("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",null, true, JingtumAPIAndWSServer.getTestInstance().getNextUUID() ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况7  资源号无效时
		try {
			@SuppressWarnings("unused")
			RequestResult payment7 = wallet1.submitPayment("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",jtc, true, "aaaaa12345aaAAA" ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
	    } catch (FailedException ex) {
	    	assertEquals("Error type: server\n\t Error message: duplicate transaction client resource id\n",ex.getMessage());
	    }
		
		//异常情况8    货币没有赋值时
		Wallet wallet08 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); //如进行支付，密钥为必须参数
		Amount jtc08 = new Amount(); //构建支付的货币
		jtc08.setCounterparty(""); //货币发行方
		//jtc08.setCurrency("SWT"); //货币单位
		jtc08.setValue(15); //金额
		try {
			@SuppressWarnings("unused")
			RequestResult payment20 = wallet08.submitPayment("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",jtc08, true, JingtumAPIAndWSServer.getTestInstance().getNextUUID() ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况9     货币长度等于3位，也不等于40位时
		Amount jtc09 = new Amount(); //构建支付的货币
		jtc09.setCounterparty(""); //货币发行方
		jtc09.setValue(15); //金额
		try {
			jtc09.setCurrency("SWTaa"); //货币单位
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid currency!",ex.getMessage());
	    }
        
		//异常情况10     货币发行方没赋值时
		Wallet wallet10 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); //如进行支付，密钥为必须参数
		Amount jtc10 = new Amount(); //构建支付的货币
		//jtc10.setCounterparty(""); //货币发行方
		jtc10.setCurrency("CNY"); //货币单位
		jtc10.setValue(15); //金额
		try {
			@SuppressWarnings("unused")
			RequestResult payment10 = wallet10.submitPayment("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",jtc10, true, JingtumAPIAndWSServer.getTestInstance().getNextUUID() ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况11    金额金额小于0时
		Amount jtc11 = new Amount(); //构建支付的货币
		jtc11.setCounterparty(""); //货币发行方
		jtc11.setCurrency("SWT"); //货币单位
		try {
			jtc11.setValue(-1); //金额
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid value!",ex.getMessage());
	    }
        
		//异常情况12  货币为SWT且Counterparty为janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f时
		Wallet wallet12 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); //如进行支付，密钥为必须参数
		Amount jtc12 = new Amount(); //构建支付的货币
		jtc12.setCounterparty("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //货币发行方
		jtc12.setCurrency("SWT"); //货币单位
		jtc12.setValue(15); //金额
		try {
			@SuppressWarnings("unused")
			RequestResult payment10 = wallet12.submitPayment("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",jtc12, true, JingtumAPIAndWSServer.getTestInstance().getNextUUID() ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
	    } catch (InvalidRequestException ex) {
	    	assertEquals("Error type: invalid_request\n\t Error message: Invalid parameter: destination_amount. SWT cannot have issuer\n",ex.getMessage());
	    }
		
		//异常情况14  钱包未激活时
		Wallet wallet14 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo"); //如进行支付，密钥为必须参数
		Amount jtc14 = new Amount(); //构建支付的货币
		jtc14.setCounterparty(""); //货币发行方
		jtc14.setCurrency("SWT"); //货币单位
		jtc14.setValue(15); //金额
		//捕获异常信息
		try {
			@SuppressWarnings("unused")
			RequestResult payment14 = wallet14.submitPayment("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",jtc14, true, JingtumAPIAndWSServer.getTestInstance().getNextUUID() ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
		
		//异常情况15    金额金额等于0时
		Amount jtc15 = new Amount(); //构建支付的货币
		jtc15.setCounterparty(""); //货币发行方
		jtc15.setCurrency("SWT"); //货币单位
		jtc15.setValue(0); //金额
		try {
			@SuppressWarnings("unused")
			RequestResult payment15 = wallet1.submitPayment("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",jtc15, true, JingtumAPIAndWSServer.getTestInstance().getNextUUID() ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid value!",ex.getMessage());
	    }
		
		//异常情况16   支付方账户余额不足时
		Wallet wallet016 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); //如进行支付，密钥为必须参数
		Amount jtc016 = new Amount(); //构建支付的货币
		jtc016.setCounterparty(""); //货币发行方
		jtc016.setCurrency("SWT"); //货币单位
		jtc016.setValue(18000000000000.0); //金额
		try {
			@SuppressWarnings("unused")
			RequestResult payment16 = wallet016.submitPayment("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",jtc016, true, JingtumAPIAndWSServer.getTestInstance().getNextUUID() ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
	    } catch (FailedException ex) {
	    	assertEquals("Error type: transaction\n\t Error message: Can only send positive amounts.\n",ex.getMessage());
	    }
	}

	/**
	*
	* 获取资源号
	 * @throws FailedException 
	*  
	*/
	@Test
	public void testGetUID() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {

		String uid = JingtumAPIAndWSServer.getTestInstance().getNextUUID();
		//判断资源号是否为null
		assertNotNull(uid);
	}

	
	/**
	*
	* 根据hash值或者资源号获取Payment信息
	 * @throws FailedException 
	*  
	*/	
	@Test
	public void testGetPaymentByID() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException{
		Wallet wallet1 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");

		//首先提交一个payment
		Amount jtc = new Amount(); //构建支付的货币
		jtc.setCounterparty(""); //货币发行方
		jtc.setCurrency("SWT"); //货币单位
		jtc.setValue(18); //金额
		RequestResult payment01 = wallet1.submitPayment("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa", jtc, true, JingtumAPIAndWSServer.getTestInstance().getNextUUID() ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
		assertEquals(true,payment01.getSuccess()); //交易是否成功
		assertEquals("validated",payment01.getState()); //交易状态
		assertEquals("tesSUCCESS",payment01.getResult()); //支付服务器结果
		String payment_hash = payment01.getHash();

		//正常情况下
		Payment payment1 = wallet1.getPayment(payment_hash);
		assertEquals(payment_hash,payment1.getHash());
		assertEquals(true,payment1.getSuccess());
		assertEquals("tesSUCCESS",payment1.getResult());
		assertEquals("sent",payment1.getType());
		
		//异常情况1 ID值为空时
		Wallet wallet001 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
		try {
			@SuppressWarnings("unused")
			Payment payment001 = wallet001.getPayment(""); 
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid ID!",ex.getMessage());
	    }
		
		//异常情况2 ID值为null时
        Wallet wallet002 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
		try {
			@SuppressWarnings("unused")
			Payment payment002 = wallet002.getPayment(null); 
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid ID!",ex.getMessage());
	    }
        
		//异常情况3 hash值为无效时
        Wallet wallet003 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
		try {
			@SuppressWarnings("unused")
			Payment payment004 = wallet003.getPayment("1111111111111111111111111sssssssssssssssaaa"); 
	    } catch (FailedException ex) {
	    	assertEquals("Error type: server\n\t Error message: Transaction not found.\n",ex.getMessage());
	    }
		
		//异常情况4  钱包未激活时
        Wallet wallet004 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo");
		try {
			@SuppressWarnings("unused")
			Payment payment004 = wallet004.getPayment("ED849BCC91781CBBC49E06D86BCE78380305BC077D8267F9C1514FA569139CC7"); 
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
	}
	
	/**
	*
	* 获取全部 Payments信息
	 * @throws FailedException 
	*  
	*/	
	@Test
	public void testGetPayments() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException{
		Wallet wallet002 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S", "snwjtucx9vEP7hCazriMbVz8hFiK9");
		//正常情况下
		PaymentCollection pc = wallet002.getPaymentList();
		//测试对象pc是否为null
		assertNotNull(pc);
		
		Payment pay;
		Iterator<Payment> it_2 = pc.getData().iterator();
		while(it_2.hasNext())
		{
		    pay = (Payment)it_2.next();
		    //判断获取信息的结果是否成功
            assertTrue("tesSUCCESS".equals(pay.getResult()) || "temREDUNDANT".equals(pay.getResult()));
		}
		
		//异常情况1    钱包未激活时
		Wallet wallet01 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo");
		try {
			@SuppressWarnings("unused")
			PaymentCollection pc01 = wallet01.getPaymentList();
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
	}

	/**
	*
	* 加条件查询，获取Payments信息
	 * @throws FailedException 
	*  
	*/	
	@Test
	public void testGetParaPayments() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		Wallet wallet = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
		//正常情况1  无分页功能
		PaymentCollection pc = wallet.getPaymentList("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","",false,Payment.Direction.all,0,0);
		//测试对象pc是否为null
		assertNotNull(pc);
		
		Payment pay;
		Iterator<Payment> it_1 = pc.getData().iterator();
		while(it_1.hasNext())
		{
		    pay = (Payment)it_1.next();
		    //判断获取信息的结果是否成功
			assertEquals("tesSUCCESS",pay.getResult());
		}
		
		//正常情况2   无分页功能
		PaymentCollection pc2 = wallet.getPaymentList("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",false,Payment.Direction.all,0,0);
		//测试对象pc2是否为null
		assertNotNull(pc2);
		
		Payment pay2;
		Iterator<Payment> it_2 = pc2.getData().iterator();
		while(it_2.hasNext())
		{
		    pay2 = (Payment)it_2.next();
		    //判断获取信息的结果是否成功
			assertEquals("tesSUCCESS",pay2.getResult());
		}
		
		//正常情况3   有分页功能
		PaymentCollection pc3 = wallet.getPaymentList("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",false,Payment.Direction.all,5,2);
		//测试对象pc3是否为null
		assertNotNull(pc3);
		
		Payment pay3;
		Iterator<Payment> it_3 = pc3.getData().iterator();
		while(it_3.hasNext())
		{
		    pay3 = (Payment)it_3.next();
		    //判断获取信息的结果是否成功
			assertEquals("tesSUCCESS",pay3.getResult());
		}

		//正常情况4   Direction为incoming时
		PaymentCollection pc4 = wallet.getPaymentList("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",false,Payment.Direction.incoming,5,2);
		//测试对象pc4是否为null
		assertNotNull(pc4);
		
		Payment pay4;
		Iterator<Payment> it_4 = pc4.getData().iterator();
		while(it_4.hasNext())
		{
		    pay4 = (Payment)it_4.next();
		    //判断获取信息的结果是否成功
			assertEquals("tesSUCCESS",pay4.getResult());
		}
        
		//正常情况5   Direction为outgoing时
		//PaymentCollection pc5 = wallet.getPayments("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",false,Payment.Direction.outgoing,0,0);
		//测试对象pc5是否为null
		//assertNotNull(pc5);
		
		//Payment pay5;
		//Iterator<Payment> it_5 = pc5.getData().iterator();
		//while(it_5.hasNext())
		//{
		//    pay5 = (Payment)it_5.next();
		    //判断获取信息的结果是否成功
		//	assertEquals("tesSUCCESS",pay5.getResult());
		//}
        
		//正常情况6   sourceAccount为空时
		PaymentCollection pc6 = wallet.getPaymentList("","jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",false,Payment.Direction.incoming,0,0);
		//测试对象pc5是否为null
		assertNotNull(pc6);
		
		Payment pay6;
		Iterator<Payment> it_6 = pc6.getData().iterator();
		while(it_6.hasNext())
		{
		    pay6 = (Payment)it_6.next();
		    //判断获取信息的结果是否成功
			assertTrue("tesSUCCESS".equals(pay6.getResult()) || "temREDUNDANT".equals(pay6.getResult()));
		}
		
		//异常情况1    钱包未激活时
		Wallet wallet01 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo");
		try {
			@SuppressWarnings("unused")
			PaymentCollection pc01 = wallet01.getPaymentList("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",false,Payment.Direction.incoming,5,2);
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
		
		//异常情况2   sourceAccount为无效时
		Wallet wallet02 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
		try {
			@SuppressWarnings("unused")
			PaymentCollection pc02 = wallet02.getPaymentList("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa11111","jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",false,Payment.Direction.incoming,5,2);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//异常情况3   destinationAccount为无效时
		Wallet wallet03 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
		try {
			@SuppressWarnings("unused")
			PaymentCollection pc03 = wallet03.getPaymentList("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S1111ssss",false,Payment.Direction.incoming,5,2);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//异常情况4   resultPerPage为负数时
		Wallet wallet04 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
		try {
			@SuppressWarnings("unused")
			PaymentCollection pc04 = wallet04.getPaymentList("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",false,Payment.Direction.incoming,-1,2);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid paging option!",ex.getMessage());
	    }
		
		//异常情况5   page为负数时
		Wallet wallet05 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
		try {
			@SuppressWarnings("unused")
			PaymentCollection pc05 = wallet05.getPaymentList("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",false,Payment.Direction.incoming,5,-1);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid paging option!",ex.getMessage());
	    }
	}
	
	/**
	*
	* 使用路径进行支付
	 * @throws FailedException 
	*  
	*/
	@Test
	public void testGetPaymentPath() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, ChannelException, APIException, FailedException {
		//Wallet wallet = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); //如进行支付，密钥为必须参数
		Wallet wallet = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9"); //如进行支付，密钥为必须参数
		Amount jtc = new Amount(); //构建支付的货币
		jtc.setValue(1.0); //金额
		jtc.setCounterparty("jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT");
		jtc.setCurrency(Jingtum.getCurrencyCNY());
		//jtc.setValue(100); //金额
		//jtc.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		//jtc.setCurrency(Jingtum.getCurrencyUSD());
		PaymentCollection pc = wallet.getPathList("jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT", jtc);
		//PaymentCollection pc = wallet.getPathList("jHb9CJAWyB4jr91VRWn96DkukG4bwdtyTh", jtc);
		Payment pay = null;
		Iterator<Payment> it_2 = pc.getData().iterator();
		while(it_2.hasNext())
		{	
		    pay = (Payment)it_2.next();
		    
		    //判断获取信息的结果是否成功
			assertEquals("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S", pay.getSourceAccount());
			assertEquals("", pay.getSourceAmount().getIssuer());
			assertEquals("0.0", String.valueOf(pay.getSourceSlippage()));
			assertEquals("jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT", pay.getDestinationAccount());
			assertEquals("1.0", String.valueOf(pay.getDestinationAmount().getValue()));
			assertEquals("CNY", pay.getDestinationAmount().getCurrency());
			assertEquals("jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT", pay.getDestinationAmount().getIssuer());
		}
		//使用路径进行支付
		RequestResult payment = wallet.submitPayment(pay, true, JingtumAPIAndWSServer.getTestInstance().getNextUUID()); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
		//判断支付是否成功
		assertEquals(true, payment.getSuccess()); //交易是否成功
		assertEquals("validated", payment.getState()); //交易状态
		assertEquals("tesSUCCESS", payment.getResult()); //支付服务器结果
		
		//异常情况1  接收方地址为空时
		Wallet wallet01 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); //如进行支付，密钥为必须参数
		Amount jtc01 = new Amount(); //构建支付的货币
		jtc01.setValue(100); //金额
		jtc01.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		jtc01.setCurrency(Jingtum.getCurrencyUSD());
		try {
			@SuppressWarnings("unused")
			PaymentCollection pc01 = wallet01.getPathList("",jtc01);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//异常情况2  接收方地址为null时
		Wallet wallet02 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); //如进行支付，密钥为必须参数
		Amount jtc02 = new Amount(); //构建支付的货币
		jtc02.setValue(100); //金额
		jtc02.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		jtc02.setCurrency(Jingtum.getCurrencyUSD());
		try {
			@SuppressWarnings("unused")
			PaymentCollection pc02 = wallet02.getPathList(null,jtc02);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//异常情况3   接收方地址为无效时
		Wallet wallet03 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); //如进行支付，密钥为必须参数
		Amount jtc03 = new Amount(); //构建支付的货币
		jtc03.setValue(100); //金额
		jtc03.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		jtc03.setCurrency(Jingtum.getCurrencyUSD());
		try {
			@SuppressWarnings("unused")
			PaymentCollection pc03 = wallet03.getPathList("js4UaG111111167QHJbWwD3eo6C5xsa",jtc03);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//异常情况4  Counterparty没赋值时
		Wallet wallet04 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); //如进行支付，密钥为必须参数
		Amount jtc04 = new Amount(); //构建支付的货币
		jtc04.setValue(100); //金额
		//jtc03.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		jtc04.setCurrency(Jingtum.getCurrencyUSD());
		try {
			@SuppressWarnings("unused")
			PaymentCollection pc04 = wallet04.getPathList("jHb9CJAWyB4jr91VRWn96DkukG4bwdtyTh",jtc04);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况5   货币没有赋值时
		Wallet wallet05 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); //如进行支付，密钥为必须参数
		Amount jtc05 = new Amount(); //构建支付的货币
		jtc05.setValue(20); //金额
		jtc05.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		//jtc05.setCurrency(Jingtum.getCurrencyUSD());
		try {
			@SuppressWarnings("unused")
			PaymentCollection pc05 = wallet05.getPathList("jHb9CJAWyB4jr91VRWn96DkukG4bwdtyTh",jtc05);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况6    金额小于0时
		Amount jtc06 = new Amount(); //构建支付的货币
		jtc06.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		jtc06.setCurrency(Jingtum.getCurrencyUSD());
		try {
			jtc06.setValue(-1); //金额
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid value!",ex.getMessage());
	    }

		//异常情况9  Currency为SWT时 
		Amount jtc09 = new Amount(); //构建支付的货币
		jtc09.setValue(20); //金额
		jtc09.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		jtc09.setCurrency(Jingtum.getCurrencySWT());
		try {
			@SuppressWarnings("unused")
			PaymentCollection pc09 = wallet.getPathList("jHb9CJAWyB4jr91VRWn96DkukG4bwdtyTh",jtc09);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Please set currency other than SWT",ex.getMessage());
	    }
		
		//异常情况7    钱包未激活时
		Wallet wallet07 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo"); //如进行支付，密钥为必须参数
		Amount jtc07 = new Amount(); //构建支付的货币
		jtc07.setValue(10); //金额
		jtc07.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		jtc07.setCurrency(Jingtum.getCurrencyUSD());
		try {
			@SuppressWarnings("unused")
			PaymentCollection pc06 = wallet07.getPathList("jHb9CJAWyB4jr91VRWn96DkukG4bwdtyTh",jtc07);
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
	}
}
