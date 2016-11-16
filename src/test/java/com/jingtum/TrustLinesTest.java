package com.jingtum;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import com.jingtum.Jingtum;
import com.jingtum.exception.APIConnectionException;
import com.jingtum.exception.APIException;
import com.jingtum.exception.AuthenticationException;
import com.jingtum.exception.ChannelException;
import com.jingtum.exception.FailedException;
import com.jingtum.exception.InvalidParameterException;
import com.jingtum.exception.InvalidRequestException;
import com.jingtum.model.RequestResult;
import com.jingtum.model.TrustLine;
import com.jingtum.model.TrustLineCollection;
import com.jingtum.model.Wallet;

public class TrustLinesTest {
	
	/**
	*
	* 增加信任
	* 
	*/
	@Test
	public void testAddTrustLine() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		Wallet wallet2 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9"); //增加授信密钥为必需值，否则提交会失败
		TrustLine trustline = new TrustLine();
		trustline.setCounterparty("jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf");
		trustline.setCurrency(Jingtum.getCurrencyJPY());
		trustline.setLimit(200);
		//正常情况1   validate为true时
		RequestResult pr = wallet2.addTrustLine(trustline, true);
		//判断授权是否成功
		assertEquals(true,pr.getSuccess());
		assertEquals("validated",pr.getState());
        
		//正常情况2  validate为false时
		RequestResult pr9 = wallet2.addTrustLine(trustline, false);
		//判断授权是否成功
		assertEquals(true,pr9.getSuccess());
		assertEquals("pending",pr9.getState());
        
		//正常情况3  limit没初始化时，采用默认值：10000000000
		TrustLine trustline05 = new TrustLine();
		trustline05.setCounterparty("jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf");
		trustline05.setCurrency(Jingtum.getCurrencyUSD());
		//trustline.setLimit(500);
		//判断授权是否成功
		assertEquals(true,pr.getSuccess());
		assertEquals("validated",pr.getState());	
		
		//异常情况2  trustline为null时
		try {
			@SuppressWarnings("unused")
			RequestResult pr2 = wallet2.addTrustLine(null, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid trust line! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况3  Counterparty没初始化时
		//trustline.setCounterparty("jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf");
		trustline.setCurrency(Jingtum.getCurrencyUSD());
		trustline.setLimit(700);	
		try {
			@SuppressWarnings("unused")
			RequestResult pr3 = wallet2.addTrustLine(trustline, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid trust line! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况4    货币没初始化时
		trustline.setCounterparty("jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf");
		//trustline.setCurrency(Jingtum.getCurrencyUSD());
		trustline.setLimit(700);
		try {
			@SuppressWarnings("unused")
			RequestResult pr4 = wallet2.addTrustLine(trustline, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid trust line! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况6  Counterparty为无效时
		TrustLine trustline6 = new TrustLine();
		try {
			trustline6.setCounterparty("jMhLAPaNFo288PNo11115HMC37kg6ULjJg8vPf");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//异常情况7  Currency为无效时
		TrustLine trustline7 = new TrustLine();
		try {
			trustline7.setCurrency("AAAA");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid currency!",ex.getMessage());
	    }
		
		//异常情况8  limit值小于0时		
		TrustLine trustline8 = new TrustLine();
		try {
			trustline8.setLimit(-1);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid limit!",ex.getMessage());
	    }
		
		//异常情况9    钱包未激活时
		Wallet wallet09 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo"); //增加授信密钥为必需值，否则提交会失败
		TrustLine trustline09 = new TrustLine();
		trustline09.setCounterparty("jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf");
		trustline09.setCurrency(Jingtum.getCurrencyUSD());
		trustline09.setLimit(700);
		try {
			@SuppressWarnings("unused")
			RequestResult pr09 = wallet09.addTrustLine(trustline09, true);
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
	}

	/**
	*
	* 获取信任列表
	*  
	*/
	@Test
	public void testGetTrustLine() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		Wallet wallet1 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9"); //根据地址和密钥生成钱包，如只为获取全部授信，密钥可为空
		TrustLineCollection tlc = wallet1.getTrustLineList();
		//判断对象tlc是否为null
		assertNotNull(tlc);
		
		TrustLine tl1;
		Iterator<TrustLine> it1 = tlc.getData().iterator();
		while(it1.hasNext()){
			tl1 = (TrustLine)it1.next();
			assertNotNull(tl1.getLimit());
			//assertEquals("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",tl1.getAccount()); //当前账号地址
		}
		
		//异常情况1    钱包未激活时
		Wallet wallet01 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo"); //根据地址和密钥生成钱包，如只为获取全部授信，密钥可为空
		try {
			@SuppressWarnings("unused")
			TrustLineCollection tlc01 = wallet01.getTrustLineList();
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
	}

	/**
	*
	* currency或counterparty或limit作为查询条件，获取信任列表
	*  
	*/
	@Test
	public void testGetParaTrustLine() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		Wallet wallet1 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9"); //根据地址和密钥生成钱包，如只为获取全部授信，密钥可为空
		//正常情况1  currency作为查询条件
		TrustLineCollection tlc1 = wallet1.getTrustLineList("JPY",null);  //currency,counterparty
		//判断对象tlc是否为null
		assertNotNull(tlc1);
		
		TrustLine tl1;
		Iterator<TrustLine> it1 = tlc1.getData().iterator();
		while(it1.hasNext()){
			tl1 = (TrustLine)it1.next();
			assertEquals("JPY",tl1.getCurrency()); //货币单位
		}
		
		//正常情况2  currency和counterparty作为查询条件
		TrustLineCollection tlc2 = wallet1.getTrustLineList("JPY","jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf");  //currency,counterparty,limit
		//判断对象tlc是否为null
		assertNotNull(tlc2);
		
		TrustLine tl2;
		Iterator<TrustLine> it2 = tlc2.getData().iterator();
		while(it2.hasNext()){
			tl2 = (TrustLine)it2.next();
			assertEquals("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",tl2.getAccount()); //当前账号地址
			assertEquals("jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf",tl2.getCounterparty()); //授信方
			assertEquals("JPY",tl2.getCurrency()); //货币单位
		}
		
		//正常情况3  counterparty作为查询条件
		TrustLineCollection tlc3 = wallet1.getTrustLineList(null,"jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf");  //currency,counterparty,limit
		//判断对象tlc是否为null
		assertNotNull(tlc3);
		
		TrustLine tl3;
		Iterator<TrustLine> it3 = tlc3.getData().iterator();
		while(it3.hasNext()){
			tl3 = (TrustLine)it3.next();
			assertEquals("jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf",tl3.getCounterparty()); //授信方
		}
        
		//正常情况4  currency和counterparty作为查询条件
		TrustLineCollection tlc5 = wallet1.getTrustLineList("JPY","jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf");  //currency,counterparty
		//判断对象tlc是否为null
		assertNotNull(tlc5);
		
		TrustLine tl5;
		Iterator<TrustLine> it5 = tlc5.getData().iterator();
		while(it5.hasNext()){
			tl5 = (TrustLine)it5.next();
			assertEquals("JPY",tl5.getCurrency()); //当前账号地址
			assertEquals("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",tl5.getAccount()); //当前账号地址
			assertEquals("200.0",String.valueOf(tl5.getLimit())); //授信方
		}
		
		//异常情况1    钱包未激活时
		Wallet wallet01 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo"); //根据地址和密钥生成钱包，如只为获取全部授信，密钥可为空
		try {
			@SuppressWarnings("unused")
			TrustLineCollection tlc01 = wallet01.getTrustLineList("JPY","jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf");  //currency,counterparty,limit
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
		
		//异常情况2  currency为无效时
		try {
			@SuppressWarnings("unused")
			TrustLineCollection tlc02 = wallet1.getTrustLineList("JPYaaa","jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf");  //currency,counterparty,limit
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid currency!",ex.getMessage());
	    }
        
		//异常情况3  counterparty为无效时
		try {
			@SuppressWarnings("unused")
			TrustLineCollection tlc03 = wallet1.getTrustLineList("JPY","jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf00001");  //currency,counterparty,limit
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
	}
	
	/**
	*
	* 移除信任
	* 
	*/
	@Test
	public void testDeleteTrustLine() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		Wallet wallet3 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9"); //增加授信密钥为必需值，否则提交会失败
		TrustLine trustline = new TrustLine();
		trustline.setCounterparty("jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf");
		trustline.setCurrency(Jingtum.getCurrencyUSD());
		trustline.setLimit(700);
		RequestResult pr = wallet3.addTrustLine(trustline, true);
		
		//正常情况1 validate为true时
		trustline.setCounterparty("jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf");
		trustline.setCurrency(Jingtum.getCurrencyUSD());
		pr = wallet3.removeTrustLine(trustline, true);
		//判断信任是否删除成功
		assertEquals(true,pr.getSuccess());
		assertEquals("validated",pr.getState());
        
		//正常情况2  正在处理  validate为false时,
		trustline.setCounterparty("jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf");
		trustline.setCurrency(Jingtum.getCurrencyUSD());
		pr = wallet3.removeTrustLine(trustline, false);
		assertEquals(true,pr.getSuccess());
		assertEquals("pending",pr.getState());//表示正在处理
		
		//异常情况1  Counterparty没初始化时
		trustline.setCounterparty("");
		trustline.setCurrency(Jingtum.getCurrencyUSD());
		trustline.setLimit(10000000000.0);
		try {
			@SuppressWarnings("unused")
			RequestResult pr01 = wallet3.removeTrustLine(trustline, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid trust line! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况2  Currency没初始化时
		trustline.setCounterparty("jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf");
		//trustline.setCurrency("USD");
		trustline.setLimit(10000000000.0);
		try {
			@SuppressWarnings("unused")
			RequestResult pr02 = wallet3.removeTrustLine(trustline, true);
	    } catch (FailedException ex) {
	    	assertEquals("Error type: transaction\n\t Error message: Can't set non-existent line to default.\n",ex.getMessage());
	    }
		
		//异常情况3  limit没初始化时
		trustline.setCounterparty("jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf");
		trustline.setCurrency(Jingtum.getCurrencyUSD());
		//trustline.setLimit(10000000000.0);
		try {
			@SuppressWarnings("unused")
			RequestResult pr03 = wallet3.removeTrustLine(trustline, true);
	    } catch (FailedException ex) {
	    	assertEquals("Error type: transaction\n\t Error message: Can't set non-existent line to default.\n",ex.getMessage());
	    }
		
		//异常情况4    对象trustline为null时
		try {
			@SuppressWarnings("unused")
			RequestResult pr04 = wallet3.removeTrustLine(null, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid trust line! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况5    钱包未激活时
		Wallet wallet05 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo");
		TrustLine trustline05 = new TrustLine();
		trustline05.setCounterparty("jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf");
		trustline05.setCurrency(Jingtum.getCurrencyUSD());
		try {
			@SuppressWarnings("unused")
			RequestResult pr05 = wallet05.removeTrustLine(trustline05, true);
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
	}
}
