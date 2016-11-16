package com.jingtum;

import static org.junit.Assert.*;

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

public class NotificationTest {

	@Test
	public void testGetNotification() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		Wallet wallet = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9"); //根据井通地址生成钱包

		//首先提交一个payment
		Amount jtc = new Amount(); //构建支付的货币
		jtc.setCounterparty(""); //货币发行方
		jtc.setCurrency("SWT"); //货币单位
		jtc.setValue(18); //金额
		RequestResult payment01 = wallet.submitPayment("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa", jtc, true, JingtumAPIAndWSServer.getTestInstance().getNextUUID() ); //支付，参数为：获取方地址，货币，是否等待支付结果，和资源号（选填）
		assertEquals(true,payment01.getSuccess()); //交易是否成功
		assertEquals("validated",payment01.getState()); //交易状态
		assertEquals("tesSUCCESS",payment01.getResult()); //支付服务器结果
		String payment_hash = payment01.getHash();
		
		//正常情况   hash值有效时
		Notification noti = wallet.getNotification(payment_hash); //根据hash值获取notification实例
		//判断获取数据是否成功
		assertEquals("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",noti.getAccount()); //通知相关账号
		assertEquals(payment_hash,noti.getHash()); //交易hash值
		assertEquals("validated",noti.getState()); //交易的状态
		assertEquals("tesSUCCESS",noti.getResult()); //交易结果
		
		//异常情况1  hash值为空时
		try {
			@SuppressWarnings("unused")
			Notification noti01 = wallet.getNotification(""); //根据hash值获取notification实例
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid ID!",ex.getMessage());
	    }
		
		//异常情况2  hash值为null时
		try {
			@SuppressWarnings("unused")
			Notification noti02 = wallet.getNotification(null); //根据hash值获取notification实例
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid ID!",ex.getMessage());
	    }
		
		//异常情况2  hash值为无效时
		try {
			@SuppressWarnings("unused")
			Notification noti03 = wallet.getNotification("112223444sssss"); //根据hash值获取notification实例
	    } catch (FailedException ex) {
	    	assertEquals("Error type: server\n\t Error message: Transaction not found.\n",ex.getMessage());
	    }
		
		//异常情况4  钱包未激活时
		Wallet wallet04 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo"); //根据井通地址生成钱包
		try {
			@SuppressWarnings("unused")
			Notification noti03 = wallet04.getNotification("1B369C49E655D1C2A93BAC2DFC0D8A97FB489F38A3EEDD95B9D4E2DD32B6DF80"); //根据hash值获取notification实例
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
	}
}
