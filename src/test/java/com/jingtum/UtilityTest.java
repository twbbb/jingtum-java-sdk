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

public class UtilityTest {

	@Test
	public void test() throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException, InvalidParameterException {
		// 获取 server连接信息
		boolean isConnected = JingtumAPIAndWSServer.getTestInstance().getStatus();
		//判断是否连接成功
		assertEquals(true,isConnected);  //为 true 代表 API服务器可以连接;反之，连接失败
		
		//生成uuid，用于提交支付时的资源号
		String uuid = JingtumAPIAndWSServer.getTestInstance().getNextUUID();
		//测试 uuid是否为null
		assertNotNull(uuid);

	}
	
	/**
	* 
	* GetOrderBook 获得货币对的挂单列表
	* Changed the Counterparty used. 
	*/
	@Test
	public void testGetOrderBook() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		//Wallet wallet = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); 
		Amount base = new Amount(); //基准货币（currency+counterparty）
		base.setCurrency(Jingtum.getCurrencyCNY());
		base.setCounterparty("jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT");
		//base.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		
		Amount counter = new Amount(); //目标货币（currency+counterparty）
		counter.setCurrency(Jingtum.getCurrencyUSD());
		//counter.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		counter.setCounterparty("jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT");
		
		//正常情况 
		OrderBookResult oBR = JingtumAPIAndWSServer.getTestInstance().getOrderBook(base, counter);
		//判断获取挂单列表是否成功
		assertEquals(true,oBR.getSuccess()); //请求结果
		assertEquals(true,oBR.getValidated()); //交易服务器状态
		assertEquals("CNY+jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT/USD+jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT",oBR.getOrderbook()); //挂单货币对
//		assertEquals("CNY+jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS/USD+jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS",oBR.getOrderbook()); //挂单货币对
		
		//获取Asks
		OrderBookCollection asks = oBR.getAsks();
		//测试对象asks是否为null
		assertNotNull(asks);
		
		Iterator<OrderBook> it_4 = asks.getData().iterator();
		OrderBook ob;
		while(it_4.hasNext())
		{
			ob = (OrderBook)it_4.next();
		    
			//System.out.println(ob.getOrder_maker());
			assertEquals(false,ob.getPassive());
			//assertEquals(true,ob.getSell());
			assertEquals("USD",ob.getAmountPrice().getCurrency());
			assertEquals("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS",ob.getAmountPrice().getCounterparty());
			assertEquals("CNY",ob.getTaker_gets_funded().getCurrency());
			assertEquals("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS",ob.getTaker_gets_funded().getCounterparty());
			assertEquals("CNY",ob.getTaker_gets_total().getCurrency());
			assertEquals("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS",ob.getTaker_gets_total().getCounterparty());
			assertEquals("USD",ob.getTaker_pays_funded().getCurrency());
			assertEquals("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS",ob.getTaker_pays_funded().getCounterparty());
			assertEquals("USD",ob.getTaker_pays_total().getCurrency());
			assertEquals("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS",ob.getTaker_pays_total().getCounterparty());
		}
		
		//获取Bids
		OrderBookCollection bids = oBR.getBids();
		//测试对象bids是否为null
		assertNotNull(bids);
		
		it_4 = bids.getData().iterator();
		while(it_4.hasNext())
		{   
			ob = (OrderBook)it_4.next();
			
			assertEquals(false,ob.getPassive());
			assertEquals(true,ob.getSell());
			assertEquals("USD",ob.getAmountPrice().getCurrency());
			assertEquals("jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT",ob.getAmountPrice().getCounterparty());
			assertEquals("USD",ob.getTaker_gets_funded().getCurrency());
			assertEquals("jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT",ob.getTaker_gets_funded().getCounterparty());
			assertEquals("USD",ob.getTaker_gets_total().getCurrency());
			assertEquals("jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT",ob.getTaker_gets_total().getCounterparty());
			assertEquals("CNY",ob.getTaker_pays_funded().getCurrency());
			assertEquals("jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT",ob.getTaker_pays_funded().getCounterparty());
			assertEquals("CNY",ob.getTaker_pays_total().getCurrency());
			assertEquals("jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT",ob.getTaker_pays_total().getCounterparty());
		}
		
		//异常情况1  base为null时
		try {
			@SuppressWarnings("unused")
			OrderBookResult oBR01 = JingtumAPIAndWSServer.getTestInstance().getOrderBook(null, counter);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况2  counter为null时
		try {
			@SuppressWarnings("unused")
			OrderBookResult oBR02 = JingtumAPIAndWSServer.getTestInstance().getOrderBook(base, null);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况3  base的currency没初始化时
		//Wallet wallet03 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); 
		Amount base03 = new Amount(); //基准货币（currency+counterparty）
		//base03.setCurrency(Jingtum.getCurrencyCNY());
		base03.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		
		Amount counter03 = new Amount(); //目标货币（currency+counterparty）
		counter03.setCurrency(Jingtum.getCurrencyUSD());
		counter03.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		//捕获异常信息
		try {
			@SuppressWarnings("unused")
			OrderBookResult oBR03 = JingtumAPIAndWSServer.getTestInstance().getOrderBook(base03, counter03);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况4  base的Counterparty没初始化时
		//Wallet wallet04 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); 
		Amount base04 = new Amount(); //基准货币（currency+counterparty）
		base04.setCurrency(Jingtum.getCurrencyCNY());
		//base04.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		
		Amount counter04 = new Amount(); //目标货币（currency+counterparty）
		counter04.setCurrency(Jingtum.getCurrencyUSD());
		counter04.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		//捕获异常信息
		try {
			@SuppressWarnings("unused")
			OrderBookResult oBR04 = JingtumAPIAndWSServer.getTestInstance().getOrderBook(base04, counter04);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况5  counter的currency没初始化时
		//Wallet wallet05 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); 
		Amount base05 = new Amount(); //基准货币（currency+counterparty）
		base05.setCurrency(Jingtum.getCurrencyCNY());
		base05.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		
		Amount counter05 = new Amount(); //目标货币（currency+counterparty）
		//counter05.setCurrency(Jingtum.getCurrencyUSD());
		counter05.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		//捕获异常信息
		try {
			@SuppressWarnings("unused")
			OrderBookResult oBR05 = JingtumAPIAndWSServer.getTestInstance().getOrderBook(base05, counter05);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况6  counter的Counterparty没初始化时
		//Wallet wallet06 = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); 
		Amount base06 = new Amount(); //基准货币（currency+counterparty）
		base06.setCurrency(Jingtum.getCurrencyCNY());
		base06.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		
		Amount counter06 = new Amount(); //目标货币（currency+counterparty）
		counter06.setCurrency(Jingtum.getCurrencyUSD());
		//counter06.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		//捕获异常信息
		try {
			@SuppressWarnings("unused")
			OrderBookResult oBR06 = JingtumAPIAndWSServer.getTestInstance().getOrderBook(base06, counter06);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况7    钱包未激活时
		//Wallet wallet07 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo"); 
		Amount base07 = new Amount(); //基准货币（currency+counterparty）
		base07.setCurrency(Jingtum.getCurrencyCNY());
		base07.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		
		Amount counter07 = new Amount(); //目标货币（currency+counterparty）
		counter07.setCurrency(Jingtum.getCurrencyUSD());
		counter07.setCounterparty("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS");
		//捕获异常
		try {
			@SuppressWarnings("unused")
			OrderBookResult oBR07 = JingtumAPIAndWSServer.getTestInstance().getOrderBook(base07, counter07);
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
	}
}
