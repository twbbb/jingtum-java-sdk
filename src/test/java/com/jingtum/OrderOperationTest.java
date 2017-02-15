package com.jingtum;

import com.jingtum.exception.*;
import com.jingtum.model.*;
import com.jingtum.model.Transaction.DirectionType;
import com.jingtum.net.FinGate;
import org.junit.Test;
import org.junit.runner.Request;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OrderOperationTest {
	/**
	 *
	 * FinGate.getOrderBook 挂单
	 *
	 *
	 */
	@Test
	public void testGetOrderBook() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		//设置测试环境
		FinGate.getInstance().setMode(1);

		//正常情况1,同步调用
		String cur_pair = "SWT/CNY:jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT";
		OrderBookResult order01 = FinGate.getInstance().getOrderBook(cur_pair);


		System.out.println("result:" + order01.toString());

		//正常情况1  是否等待结果为true时
		assertEquals(true, order01.getSuccess()); //返回是否成功
		assertEquals(true, order01.getValidated()); //返回方式
		assertEquals(cur_pair, order01.getOrderbook()); //返回方式
		//

//		if(order01.getBids() > 0){
//			System.out.println("Submit done, search the order!");
//			Order order02 = wallet.getOrder(order01.getHash());
//
//			System.out.println("Search for:" + order02.toString());
//			assertEquals(true, order02.getSuccess()); //挂单是否查询成功
//			assertEquals(order01.getHash(), order02.getHash());//是否查找到正确的挂单
//
//
//			System.out.println("Now canceling "+order01.getSequence());
//
//
//			CancelOrderOperation cop = new CancelOrderOperation(wallet);
//			cop.setSequence(order01.getSequence());
//			cop.setValidate(true);
//			RequestResult order03 = cop.submit();
//
//			System.out.println("Cancel:" + order03.toString());
//			assertEquals(true, order03.getSuccess()); //挂单是否取消成功
//
//		}


		//正常情况2,异步调用 getOrderBook

//		op.submit(new OrderListener() {
//			public void onComplete(RequestResult result) {
//				//正常情况1  是否等待结果为true时
//				assertNotNull(result);
//				assertEquals(true, result.getSuccess()); //交易是否成功
//				assertEquals("validated", result.getState()); //交易状态
//				assertEquals("tesSUCCESS", result.getResult()); //支付服务器结果
//			}
//		});


	}

	/**
	* 
	* submitOrder 挂单
	 * CancleOrder 取消挂单测试
	* 
	*/
	@Test
	public void testSubmitAndCancelOrder() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		//设置测试环境
		FinGate.getInstance().setMode(1);
		Wallet wallet = new Wallet("snwjtucx9vEP7hCazriMbVz8hFiK9"); //根据钱包地址和密钥生成钱包实例


		//正常情况1,同步调用
		OrderOperation op = new OrderOperation(wallet);

		op.setPair("SWT/CNY:jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT");
		op.setType(OrderOperation.SELL);
		op.setAmount(1.00);
		op.setPrice(5);
		op.setValidate(true);
		RequestResult order01 = op.submit();

		System.out.println("result:" + order01.toString());

		//正常情况1  是否等待结果为true时
		assertEquals(true, order01.getSuccess()); //挂单是否成功
		assertEquals("validated", order01.getState()); //挂单方式

		//

		if(order01.getSequence() > 0){
			System.out.println("Submit done, search the order!");
			Order order02 = wallet.getOrder(order01.getHash());

			System.out.println("Search for:" + order02.toString());
			assertEquals(true, order02.getSuccess()); //挂单是否查询成功
			assertEquals(order01.getHash(), order02.getHash());//是否查找到正确的挂单


			System.out.println("Now canceling "+order01.getSequence());


			CancelOrderOperation cop = new CancelOrderOperation(wallet);
            cop.setSequence(order01.getSequence());
            cop.setValidate(true);
            RequestResult order03 = cop.submit();

			System.out.println("Cancel:" + order03.toString());
			assertEquals(true, order03.getSuccess()); //挂单是否取消成功

		}


		//正常情况2,异步调用

//		op.submit(new OrderListener() {
//			public void onComplete(RequestResult result) {
//				//正常情况1  是否等待结果为true时
//				assertNotNull(result);
//				assertEquals(true, result.getSuccess()); //交易是否成功
//				assertEquals("validated", result.getState()); //交易状态
//				assertEquals("tesSUCCESS", result.getResult()); //支付服务器结果
//			}
//		});


	}


	/**
	* 
	* GetOrders 获取全部挂单
	* 
	*/
	@Test
	public void testGetOrders() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException{

		FinGate.getInstance().setMode(1);
		Wallet wallet = new Wallet("snwjtucx9vEP7hCazriMbVz8hFiK9");
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
		Wallet wallet01 = new Wallet("saadV1p5vQeh4N1YdPGo3N3NS7dZo");
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

		FinGate.getInstance().setMode(1);
		Wallet wallet = new Wallet("snqFcHzRe22JTM8j7iZVpQYzxEEbW","js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa");


		OrderOperation op = new OrderOperation(wallet);

		op.setPair("USD:jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS/CNY:janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f");
		op.setType(OrderOperation.SELL);
		op.setAmount(1.1);
		op.setPrice(5);
		op.setValidate(true);
		RequestResult od_4 = op.submit();
		System.out.println("OD 4 returns:"+od_4.toString());
		//正常情况  hash值正确时
		Order od_5 = wallet.getOrder(od_4.getHash()); //根据hash值获得挂单

System.out.println("OD 5 returns:"+od_5.toString());
        //判断获取挂单信息是否成功
		assertEquals(true,od_5.getSuccess()); //请求结果
		assertEquals(od_4.getHash(),od_5.getHash()); //交易hash
		assertEquals("true",od_5.getValidated()); //交易服务器状态
		assertEquals(DirectionType.outgoing,od_5.getDirection()); //交易方向，incoming或outgoing
		assertEquals("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa",od_5.getAccount()); //交易帐号
		assertEquals("jBciDE8Q3uJjf111VeiUNM775AMKHEbBLS",od_5.getPay().getCounterparty());
		assertEquals("USD",od_5.getPay().getCurrency());
		assertEquals("1.1",String.valueOf(od_5.getPay().getValue()));
		assertEquals("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f",od_5.getReceive().getCounterparty());
		assertEquals("CNY",od_5.getReceive().getCurrency());
		assertEquals("5.5",String.valueOf(od_5.getReceive().getValue()));
		assertEquals(false,od_5.getPassive()); //交易是否是被动交易
		assertEquals(OrderOperation.SELL, od_5.getType().toString()); //交易类型，sell或buy
		
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
			Wallet wallet4 = new Wallet("saadV1p5vQeh4N1YdPGo3N3NS7dZo","jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z");
			@SuppressWarnings("unused")
			Order od_04 = wallet4.getOrder("C05613A5A956FF98DFAB8EFD12A0BC91D862C735E9C8FFF8B500811EDA1DB2BB"); //根据hash值获得挂单
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
	}
}
