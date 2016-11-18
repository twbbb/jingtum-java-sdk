package com.jingtum;

import static org.junit.Assert.*;

import com.jingtum.net.JingtumAPIAndWSServer;
import org.junit.Test;

import com.jingtum.exception.APIConnectionException;
import com.jingtum.exception.APIException;
import com.jingtum.exception.AuthenticationException;
import com.jingtum.exception.ChannelException;
import com.jingtum.exception.FailedException;
import com.jingtum.exception.InvalidParameterException;
import com.jingtum.exception.InvalidRequestException;
import com.jingtum.model.Amount;
import com.jingtum.model.Order;
import com.jingtum.model.Wallet;


public class SubscribeTest {
	
	/**
	* 
	* 建立连接、发起订阅、取消订阅、断开连接
	* 正常情况
	 * @throws InterruptedException 
	*/
	@Test
	public void testSubscribe() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, InterruptedException, FailedException {
		Wallet wallet = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW");	
		//SubscribeEventHandlerTest sme = new SubscribeEventHandlerTest();
		
		//建立连接
		boolean isConnected = JingtumAPIAndWSServer.getTestInstance().connet();
		assertEquals(true, isConnected);  //为true时，建立连接成功
		
		//发起订阅
		boolean isSubscribed = wallet.subscribe();
		assertEquals(true, isSubscribed);  //为true时，建立订阅成功
		
		//异步支付
		Amount jtc = new Amount(); //构建支付的货币
		jtc.setCounterparty(""); //货币发行方
		jtc.setCurrency(Jingtum.getCurrencySWT()); //货币单位
		jtc.setValue(1000); //金额
		wallet.submitPayment("jsTo3VKjQrgDKdm5iQdMjc9YqDfrWJn1hw",jtc, false, JingtumAPIAndWSServer.getTestInstance().getNextUUID());
		
		//异步挂单
		Amount pay = new Amount(); //构建JingtumCurrency 实例
		pay.setCounterparty(""); //Currency counterparty
		pay.setCurrency(Jingtum.getCurrencySWT()); //单位
		pay.setValue(1); //数量
		Amount get = new Amount();
		get.setCounterparty("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
		get.setCurrency(Jingtum.getCurrencyCNY());
		get.setValue(2);
		wallet.createOrder(Order.OrderType.sell, pay, get, false);
        
		//取消订阅
		Thread.sleep(15000); //等待之前两个操作返回的消息，之后收钱的操作应该收不到消息了
		boolean isUnsubscribed = wallet.unsubscribe();
		assertEquals(true, isUnsubscribed);  //为true时，关闭订阅成功
		
		//收钱
		Wallet wallet2 = new Wallet("jsTo3VKjQrgDKdm5iQdMjc9YqDfrWJn1hw","sseBFE4ZydZCaVEYctv6UedGnbnwn");
		Amount jtc2 = new Amount(); //构建支付的货币
		jtc2.setCounterparty(""); //货币发行方
		jtc2.setCurrency(Jingtum.getCurrencySWT()); //货币单位
		jtc2.setValue(10); //金额
		wallet2.submitPayment("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa",jtc2, false, JingtumAPIAndWSServer.getTestInstance().getNextUUID() );
		
		//断开连接
		Thread.sleep(15000); //等待能否收到收钱的消息
		boolean isClosed = JingtumAPIAndWSServer.getTestInstance().disconnect();
		assertEquals(true, isClosed);  //为true时，断开连接成功
	}
	
	/**
	*
	* 建立连接
	* 异常情况
	 * @throws InvalidParameterException 
	 * @throws APIException 
	*/
	@Test
	public void testExceptionOpenConnection() throws APIException, InvalidParameterException {
		//异常情况1  sme为null时
		//捕获异常
		try {
			JingtumAPIAndWSServer.getTestInstance().setTxHandler(null);
			JingtumAPIAndWSServer.getTestInstance().connet();
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Message handler cannot be null!",ex.getMessage());
	    }
		
		//异常情况2   网络断开时
		//SubscribeEventHandlerTest sme02 = new SubscribeEventHandlerTest();
		
		//判断连接是否成功
		//boolean isConnected = Utility.openConnection(sme02);
		//assertEquals(false, isConnected);  //为true时，建立连接成功
	}
	
	/**
	*
	* 发起订阅
	* 异常情况
	 * @throws InvalidParameterException 
	 * @throws APIException 
	*/
	@Test
	public void testExceptionSubscribe() throws APIException, InvalidParameterException {
		//异常情况1   钱包未激活
		Wallet wallet = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo");	
		SubscribeEventHandlerTest sme = new SubscribeEventHandlerTest();
		JingtumAPIAndWSServer.getTestInstance().setTxHandler(sme);
		boolean isConnected = JingtumAPIAndWSServer.getTestInstance().connet();
		assertEquals(true, isConnected);
		
		//捕获异常信息
		try {
			@SuppressWarnings("unused")
			boolean isSubscribed01 = wallet.subscribe();
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
		
		//异常情况2     断开网络 时
		//@SuppressWarnings("unused")
		//boolean isSubscribed02 = wallet.subscribe();
		//assertEquals(false, isConnected);
	}
	
	/**
	*
	* 取消订阅
	* 异常情况
	 * @throws InvalidParameterException 
	 * @throws APIException 
	*/
	@Test
	public void testExceptionUnsubscribe() throws APIException, InvalidParameterException {
		//异常情况1   钱包未激活
		Wallet wallet = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo");	
		SubscribeEventHandlerTest sme = new SubscribeEventHandlerTest();
		JingtumAPIAndWSServer.getTestInstance().setTxHandler(sme);
		boolean isConnected = JingtumAPIAndWSServer.getTestInstance().connet();
		assertEquals(true, isConnected);
		
		//捕获异常信息
		try {
			@SuppressWarnings("unused")
			boolean isUnbscribed01 = wallet.unsubscribe();
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
		
		//异常情况2    网络断开时
		//@SuppressWarnings("unused")
		//boolean isUnbscribed02 = wallet.unsubscribe();
		//assertEquals(false, isConnected);
	}
}
