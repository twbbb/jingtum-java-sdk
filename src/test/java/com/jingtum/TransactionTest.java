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
import com.jingtum.model.Transaction.TranType;

public class TransactionTest {

	/**
	* 
	* 获取transaction信息
	* 
	*/
	@Test
	public void testGetTransactions() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		Wallet wallet = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW");
		Transaction tran;
		//正常情况1  接收方地址为null,excludeFailed为true
		TransactionCollection tc = wallet.getTransactionList(null,true,Transaction.DirectionType.outgoing,5,2); //参数为：支付交易的接收方地址，是否移除失败的交易历史，支付交易的方向->incoming或outgoing,每页数据量,第几页
		//测试对象tc是否为null
		assertNotNull(tc);
		
		Iterator<Transaction> it1 = tc.getData().iterator();
		Integer i = 0;
		while(it1.hasNext())
		{
			i++;
			tran = (Transaction)it1.next();			
			assertEquals("tesSUCCESS",tran.getResult()); //交易结果
			assertEquals("",tran.getClient_resource_id()); //交易资源号
			assertEquals("0.000012",tran.getFee()); //交易费用，井通计价
			
			if(tran.getAmount() != null){
				if(tran.getAmount().getCurrency() == "SWT"){
					assertEquals("",tran.getAmount().getIssuer()); //货币发行方
				}
			}
			
			if(tran.getGets() != null){
				if(tran.getGets().getCurrency() == "SWT"){
					assertEquals("",tran.getGets().getIssuer()); //货币发行方
				}
			}
			
			if(tran.getPays() != null){
				if(tran.getGets().getCurrency() != "SWT"){
					assertEquals("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f",tran.getPays().getIssuer()); //货币发行方
				}else {
					assertEquals("",tran.getPays().getIssuer()); //货币发行方
				}
			}
		}
		
		//正常情况2  excludeFailed为true时
		Wallet wallet2 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
		//正常情况1  接收方地址为null,excludeFailed为false
		TransactionCollection tc2 = wallet2.getTransactionList(null,true,Transaction.DirectionType.outgoing,10,1); //参数为：支付交易的接收方地址，是否移除失败的交易历史，支付交易的方向->incoming或outgoing,每页数据量,第几页
		//测试对象tc是否为null
		assertNotNull(tc2);
        
		//正常情况3  excludeFailed为false时
		Wallet wallet3 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
		//正常情况1  接收方地址为null,excludeFailed为false
		TransactionCollection tc3 = wallet3.getTransactionList(null,false,null,0,0); //参数为：支付交易的接收方地址，是否移除失败的交易历史，支付交易的方向->incoming或outgoing,每页数据量,第几页
		//测试对象tc是否为null
		assertNotNull(tc3);
        
		//正常情况4    接收方地址为null,excludeFailed为false
		TransactionCollection tc4 = wallet.getTransactionList(null,false,Transaction.DirectionType.outgoing,5,2); //参数为：支付交易的接收方地址，是否移除失败的交易历史，支付交易的方向->incoming或outgoing,每页数据量,第几页
		//测试对象tc4是否为null
		assertNotNull(tc4);
		
		Iterator<Transaction> it4 = tc.getData().iterator();
		Integer i4 = 0;
		while(it4.hasNext())
		{
			i4++;
			tran = (Transaction)it4.next();			
			assertEquals("",tran.getClient_resource_id()); //交易资源号
			assertEquals("0.000012",tran.getFee()); //交易费用，井通计价
			
			if(tran.getAmount() != null){
				if(tran.getAmount().getCurrency() == "SWT"){
					assertEquals("",tran.getAmount().getIssuer()); //货币发行方
				}
			}
			
			if(tran.getGets() != null){
				if(tran.getGets().getCurrency() == "SWT"){
					assertEquals("",tran.getGets().getIssuer()); //货币发行方
				}
			}
			
			if(tran.getPays() != null){
				if(tran.getGets().getCurrency() != "SWT"){
					assertEquals("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f",tran.getPays().getIssuer()); //货币发行方
				}else {
					assertEquals("",tran.getPays().getIssuer()); //货币发行方
				}
			}
		}
		
		//异常情况1    钱包未激活时
		Wallet wallet01 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo");
		//捕获异常
		try {
			@SuppressWarnings("unused")
			TransactionCollection tc01 = wallet01.getTransactionList("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",false,Transaction.DirectionType.outgoing,10,1); //参数为：支付交易的接收方地址，是否移除失败的交易历史，支付交易的方向->incoming或outgoing,每页数据量,第几页
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
		
		//异常情况2  resultPerPage为负数时
		Wallet wallet02 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
		//捕获异常
		try {
			@SuppressWarnings("unused")
			TransactionCollection tc02 = wallet02.getTransactionList("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",false,Transaction.DirectionType.outgoing,-1,1); //参数为：支付交易的接收方地址，是否移除失败的交易历史，支付交易的方向->incoming或outgoing,每页数据量,第几页
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid paging option!",ex.getMessage());
	    }
        
		//异常情况3  page为负数时
		Wallet wallet03 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
		//捕获异常
		try {
			@SuppressWarnings("unused")
			TransactionCollection tc03 = wallet03.getTransactionList("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",false,Transaction.DirectionType.outgoing,10,-1); //参数为：支付交易的接收方地址，是否移除失败的交易历史，支付交易的方向->incoming或outgoing,每页数据量,第几页
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid paging option!",ex.getMessage());
	    }
		
		//异常情况4  destinationAccount为无效时
		Wallet wallet04 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
		//捕获异常
		try {
			@SuppressWarnings("unused")
			TransactionCollection tc04 = wallet04.getTransactionList("jfCiWtS111111t4juFbS3NaXvYV9xNYxakm5yP9S",false,Transaction.DirectionType.outgoing,10,-1); //参数为：支付交易的接收方地址，是否移除失败的交易历史，支付交易的方向->incoming或outgoing,每页数据量,第几页
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
   }
	
	/**
	* 
	* 根据hash获取transaction
	* 
	*/
	@Test
	public void testGetTransactionByHash() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		Wallet wallet = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW");
		Transaction tran;

        //首先提交一个payment
        Amount jtc = new Amount(); //构建支付的货币
        jtc.setCounterparty(""); //货币发行方
        jtc.setCurrency("SWT"); //货币单位
        jtc.setValue(18); //金额
        String next_uuid = JingtumAPIAndWSServer.getTestInstance().getNextUUID();
        RequestResult payment01 = wallet.submitPayment("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S", jtc, true, next_uuid); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
        assertEquals(true,payment01.getSuccess()); //交易是否成功
        assertEquals("validated",payment01.getState()); //交易状态
        assertEquals("tesSUCCESS",payment01.getResult()); //支付服务器结果
        String payment_hash = payment01.getHash();
		
		//正常情况   hash值有效时
		tran = wallet.getTransaction(payment_hash); //参数：hash值
		//判断获取数据是否成功
		assertEquals("tesSUCCESS",tran.getResult()); //交易结果
		assertEquals(TranType.sent, tran.getType()); //交易类型
		assertEquals(next_uuid,tran.getClient_resource_id()); //交易资源号
		assertEquals("0.000012",tran.getFee()); //交易费用，井通计价
		assertEquals(payment_hash, tran.getHash());//交易hash值
		
		if(tran.getGets() != null){
			if(tran.getGets().getCurrency() == "SWT"){
				assertEquals("",tran.getGets().getIssuer()); //货币发行方
			}
		}
		
		if(tran.getPays() != null){
			if(tran.getGets().getCurrency() != "SWT"){
				assertEquals("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f",tran.getPays().getIssuer()); //货币发行方
			}else {
				assertEquals("",tran.getPays().getIssuer()); //货币发行方
			}
		}
		
		//异常情况1   hash值为空时
		try {
			tran = wallet.getTransaction(""); //参数：hash值
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid ID!",ex.getMessage());
	    }
		
		//异常情况2   hash值为null时
		try {
			tran = wallet.getTransaction(null); //参数：hash值
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid ID!",ex.getMessage());
	    }
		
		//异常情况3   hash值为无效时
		try {
			tran = wallet.getTransaction("1111111111111111ssssssssssssssssssssssssbbbbbbbbbbbbb"); //参数：hash值
	    } catch (FailedException ex) {
	    	assertEquals("Error type: server\n\t Error message: Transaction not found.\n",ex.getMessage());
	    }
        
		//异常情况1   钱包未激活时
		Wallet wallet01 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo");
		//捕获异常
		try {
			@SuppressWarnings("unused")
			Transaction tran01 = wallet01.getTransaction("9937AA23F6B4B11674A9696F16BE62C947DAFA92D464DDDF26B26B9BE0CA178B"); //参数：hash值
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
	}
}
