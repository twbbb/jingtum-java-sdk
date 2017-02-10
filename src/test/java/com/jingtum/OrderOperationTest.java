package com.jingtum;

import com.jingtum.exception.*;
import com.jingtum.model.*;
import com.jingtum.model.Transaction.DirectionType;
import com.jingtum.net.FinGate;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OrderOperationTest {

	/**
	* 
	* submitOrder 挂单
	* 
	*/
	@Test
	public void testPutOrder() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		//设置测试环境
		FinGate.getInstance().setMode(1);
		Wallet wallet = new Wallet("snwjtucx9vEP7hCazriMbVz8hFiK9"); //根据钱包地址和密钥生成钱包实例

		Amount pay = new Amount(); //构建JingtumCurrency 实例
		pay.setCounterparty(""); //Currency counter party
		pay.setCurrency(Jingtum.getCurrencySWT()); //单位
		pay.setValue(9.9); //数量

		Amount get = new Amount();
		get.setCounterparty("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
		get.setCurrency(Jingtum.getCurrencyCNY());
		get.setValue(9);

		//正常情况1
		RequestResult od = wallet.createOrder(Order.OrderType.sell, pay, get, true); //挂单，参数为：挂单类型，支付的currency，获得的currency，是否等待交易结果返回
		//判断挂单是否成功
		assertEquals(true,od.getSuccess()); //请求的结果，是否成功
		assertEquals("validated",od.getState()); //交易状态
		
		//异常情况1  挂单类型OrderType为null时
		//捕获异常
		try {
			@SuppressWarnings("unused")
			RequestResult od1 = wallet.createOrder(null, pay, get, true); 
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Please specify an order type!",ex.getMessage());
	    }
		
		//异常情况2   pay为null时
		Amount get2 = new Amount();
		get2.setCounterparty("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
		get2.setCurrency(Jingtum.getCurrencyCNY());
		get2.setValue(5);
		//捕获异常
		try {
			@SuppressWarnings("unused")
			RequestResult od2 = wallet.createOrder(Order.OrderType.sell, null, get2, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况3    支付方的Counterparty没初始化时
		Amount pay3 = new Amount(); //构建JingtumCurrency 实例
		//pay3.setCounterparty(""); //Currency counter party
		pay3.setCurrency(Jingtum.getCurrencySWT()); //单位
		pay3.setValue(2); //数量
		Amount get3 = new Amount();
		get3.setCounterparty("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
		get3.setCurrency(Jingtum.getCurrencyCNY());
		get3.setValue(1);
		//捕获异常
		try {
			@SuppressWarnings("unused")
			RequestResult od3 = wallet.createOrder(Order.OrderType.sell, pay3, get3, true); 
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况4   支付方的Currency没初始化时
		Amount pay4 = new Amount(); //构建JingtumCurrency 实例
		pay4.setCounterparty(""); //Currency counter party
		//pay4.setCurrency(Jingtum.getCurrencySWT()); //单位
		pay4.setValue(2); //数量
		Amount get4 = new Amount();
		get4.setCounterparty("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
		get4.setCurrency(Jingtum.getCurrencyCNY());
		get4.setValue(1);
		//捕获异常
		try {
			@SuppressWarnings("unused")
			RequestResult od4 = wallet.createOrder(Order.OrderType.sell, pay4, get4, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
        
		//异常情况6   get为null时
		Amount pay6 = new Amount(); //构建JingtumCurrency 实例
		pay6.setCounterparty(""); //Currency counter party
		pay6.setCurrency(Jingtum.getCurrencySWT()); //单位
		pay6.setValue(2); //数量
		//捕获异常
		try {
			@SuppressWarnings("unused")
			RequestResult od6 = wallet.createOrder(Order.OrderType.sell, pay6, null, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
        
		//异常情况7   获得方Counterparty没初始化时
		Amount pay7 = new Amount(); //构建JingtumCurrency 实例
		pay7.setCounterparty(""); //Currency counter party
		pay7.setCurrency(Jingtum.getCurrencySWT()); //单位
		pay7.setValue(2); //数量
		Amount get7 = new Amount();
		//get7.setCounterparty("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
		get7.setCurrency(Jingtum.getCurrencyCNY());
		get7.setValue(1);
		//捕获异常
		try {
			@SuppressWarnings("unused")
			RequestResult od7 = wallet.createOrder(Order.OrderType.sell, pay7, get7, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况8   获得方的Currency为null时
		Amount pay8 = new Amount(); //构建JingtumCurrency 实例
		pay8.setCounterparty(""); //Currency counter party
		pay8.setCurrency(Jingtum.getCurrencySWT()); //单位
		pay8.setValue(2); //数量
		Amount get8 = new Amount();
		get8.setCounterparty("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
		//get8.setCurrency(Jingtum.getCurrencyCNY()); //单位
		get8.setValue(1);
		//捕获异常
		try {
			@SuppressWarnings("unused")
			RequestResult od8 = wallet.createOrder(Order.OrderType.sell, pay8, get8, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
	    
		//异常情况9  钱包未激活时
		Wallet wallet9 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo");
		Amount pay9 = new Amount(); //构建JingtumCurrency 实例
		pay9.setCounterparty(""); //Currency counter party
		pay9.setCurrency(Jingtum.getCurrencySWT()); //单位
		pay9.setValue(15.1); //数量
		Amount get9 = new Amount();
		get9.setCounterparty("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
		get9.setCurrency(Jingtum.getCurrencyCNY());
		get9.setValue(15);
		//捕获异常
		try {
			@SuppressWarnings("unused")
			RequestResult od9 = wallet9.createOrder(Order.OrderType.sell, pay9, get9, true);
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
	}

	/**
	* 
	* CancelOrder 取消挂单
	* 
	*/
	@Test
	public void testCancelOrder() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		Wallet wallet = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9"); //根据钱包地址和密钥生成钱包实例
		Amount pay = new Amount(); //构建JingtumCurrency 实例
		pay.setCounterparty(""); //Currency counter party
		pay.setCurrency(Jingtum.getCurrencySWT()); //单位
		pay.setValue(9.9); //数量
		Amount get = new Amount();
		get.setCounterparty("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
		get.setCurrency(Jingtum.getCurrencyCNY());
		get.setValue(9);
		RequestResult od = wallet.createOrder(Order.OrderType.buy, pay, get, true); //挂单，参数为：挂单类型，支付的currency，获得的currency，是否等待交易结果返回
	    
		//正常情况   validate为true
		long sequence = od.getSequence();
		RequestResult od2 = wallet.cancelOrder(sequence, true); //取消挂单，参数为交易序列号和是否等待交易结果
		//判断取消挂单是否成功
		assertEquals(true,od2.getSuccess()); //请求的结果，是否成功
		assertEquals("validated",od2.getState()); //交易的服务器状态
	    
		//异常情况1  sequence为无效时
		try {
			@SuppressWarnings("unused")
			RequestResult od01 = wallet.cancelOrder(12345678, true); //取消挂单，参数为交易序列号和是否等待交易结果
	    } catch (FailedException ex) {
	    	assertEquals("Error type: transaction\n\t Error message: Malformed: Sequence is not in the past.\n",ex.getMessage());
	    }
		
		//异常情况2   钱包未激活且sequence为36时
		Wallet wallet02 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo");
		//捕获异常
		try {
			@SuppressWarnings("unused")
			RequestResult od02 = wallet02.cancelOrder(36, true); //取消挂单，参数为交易序列号和是否等待交易结果
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
	}

	/**
	* 
	* GetOrders 获取全部挂单
	* 
	*/
	@Test
	public void testGetOrders() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException{
		Wallet wallet = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9"); 
		OrderCollection oc = wallet.getOrderList(); //获取所有挂单
		//测试对象oc是否为null
		assertNotNull(oc);
		
		Order od_3;
		Iterator<Order> it_3 = oc.getData().iterator();
		while(it_3.hasNext())
		{	
		    od_3 = (Order)it_3.next();
		    assertEquals(Order.OrderType.sell,od_3.getType()); //挂单类型:sell
		    assertEquals(false,od_3.getPassive()); //是否被动交易
		    assertEquals("CNY",od_3.getReceive().getCurrency()); //获得的货币单位
		    assertEquals("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f",od_3.getReceive().getCounterparty()); //获得货币的发行方
		    assertEquals("SWT",od_3.getPay().getCurrency()); //支付的货币单位
		    assertEquals("",od_3.getPay().getCounterparty()); //支付货币的发行方
		}
		
		//异常情况1    钱包未激活时
		Wallet wallet01 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo");
		//捕获异常
		try {
			@SuppressWarnings("unused")
			OrderCollection oc01 = wallet01.getOrderList(); //获取所有挂单
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
	}

	/**
	* 
	* GetOrderByHash 根据hash值获取挂单信息
	* 
	*/
	@Test
	public void testGetOrderByHash() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException{
		Wallet wallet = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW");
		Amount pay2 = new Amount();
		pay2.setCounterparty("");
		pay2.setCurrency(Jingtum.getCurrencySWT());
		pay2.setValue(1.1);
		Amount get2 = new Amount();
		get2.setCounterparty("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
		get2.setCurrency(Jingtum.getCurrencyCNY());
		get2.setValue(1);
		RequestResult od_4 = wallet.createOrder(Order.OrderType.sell, pay2, get2, true);
		//正常情况  hash值正确时
		Order od_5 = wallet.getOrder(od_4.getHash()); //根据hash值获得挂单
        //判断获取挂单信息是否成功
		assertEquals(true,od_5.getSuccess()); //请求结果
		assertEquals(od_4.getHash(),od_5.getHash()); //交易hash
		assertEquals("true",od_5.getValidated()); //交易服务器状态
		assertEquals(DirectionType.outgoing,od_5.getDirection()); //交易方向，incoming或outgoing
		assertEquals("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa",od_5.getAccount()); //交易帐号
		assertEquals("",od_5.getPay().getCounterparty());
		assertEquals("SWT",od_5.getPay().getCurrency());
		assertEquals("1.1",String.valueOf(od_5.getPay().getValue()));
		assertEquals("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f",od_5.getReceive().getCounterparty());
		assertEquals("CNY",od_5.getReceive().getCurrency());
		assertEquals("1.0",String.valueOf(od_5.getReceive().getValue()));
		assertEquals(false,od_5.getPassive()); //交易是否是被动交易
		assertEquals(Order.OrderType.sell, od_5.getType()); //交易类型，sell或buy
		
		//异常情况1 hash值为空时
		//捕获异常
		try {
			@SuppressWarnings("unused")
			Order od_01 = wallet.getOrder(""); //根据hash值获得挂单
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid ID!",ex.getMessage());
	    }
		
		//异常情况2  hash值为null时
		//捕获异常
		try {
			@SuppressWarnings("unused")
			Order od_02 = wallet.getOrder(null); //根据hash值获得挂单	
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid ID!",ex.getMessage());
	    }
		
		//异常情况3  hash值为无效时
		try {
			@SuppressWarnings("unused")
			Order od_03 = wallet.getOrder("fhfhhfhfhhfhfhhffhfffhh11111sssshhhhhhahhhhh"); //根据hash值获得挂单
	    } catch (InvalidRequestException ex) {
	    	assertEquals("Error type: invalid_request\n\t Error message: Parameter is not a valid transaction hash: identifier\n",ex.getMessage());
	    }
		
		//异常情况4    钱包未激活且hash值有效时
		//捕获异常
		try {
			Wallet wallet4 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo");
			@SuppressWarnings("unused")
			Order od_04 = wallet4.getOrder("C05613A5A956FF98DFAB8EFD12A0BC91D862C735E9C8FFF8B500811EDA1DB2BB"); //根据hash值获得挂单
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
	}
}
