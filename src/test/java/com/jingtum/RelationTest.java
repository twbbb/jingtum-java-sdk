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
import com.jingtum.model.Relation;
import com.jingtum.model.RelationAmount;
import com.jingtum.model.RelationCollection;
import com.jingtum.model.RequestResult;
import com.jingtum.model.Wallet;
import com.jingtum.model.Relation.RelationType;

public class RelationTest {

	/**
	*
	* add relationship
	 * @throws FailedException 
	* 
	*/	
	@Test
	public void testAddRelation() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		Wallet wallet = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW"); //根据井通地址生成钱包
		
		RelationAmount amount = new RelationAmount();
		amount.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
		amount.setCurrency(Jingtum.getCurrencyUSD());
		amount.setLimit(15); //使用limit，而不是value
		
		//正常情况   RelationType为authorize,validate为true时
		RequestResult pr1 = wallet.addRelation(RelationType.authorize, "jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S", amount, true);
		//判断关系增加是否成功
		assertEquals(true,pr1.getSuccess());
		assertEquals("validated",pr1.getState());
		
		//正常情况   validate为false时
		amount.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
		amount.setCurrency(Jingtum.getCurrencyCNY());
		amount.setLimit(20); //使用limit，而不是value
		RequestResult pr2 = wallet.addRelation(RelationType.authorize, "jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S", amount, false);
		//处理等待中
		assertEquals(true,pr2.getSuccess());
		assertEquals("pending",pr2.getState());
	    
		//异常情况1  RelationType为all时
		try {
			@SuppressWarnings("unused")
			RequestResult pr01 = wallet.addRelation(RelationType.all, "jsTo3VKjQrgDKdm5iQdMjc9YqDfrWJn1hw", amount, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid relation type!",ex.getMessage());
	    }
        
		//异常情况2  counterParty为空时
		try {
			@SuppressWarnings("unused")
			RequestResult pr02 = wallet.addRelation(RelationType.authorize, "", amount, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//异常情况3  counterParty为空时
		try {
			@SuppressWarnings("unused")
			RequestResult pr03 = wallet.addRelation(RelationType.authorize, null, amount, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//异常情况4  counterParty为无效时
		try {
			@SuppressWarnings("unused")
			RequestResult pr04 = wallet.addRelation(RelationType.authorize, "jsTo3VKjQrgDKdm5iQdMj1111111c9YqDfrWJn1hw", amount, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//异常情况5  Currency为SWT时
		amount.setIssuer(""); //Currency issuer
		amount.setCurrency(Jingtum.getCurrencySWT());
		amount.setLimit(200); //使用limit，而不是value		
		try {
			@SuppressWarnings("unused")
			RequestResult pr05 = wallet.addRelation(RelationType.authorize, "jsTo3VKjQrgDKdm5iQdMjc9YqDfrWJn1hw", amount, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Please set currency other than SWT",ex.getMessage());
	    }
	    
		//异常情况6  Currency为CNY且Issuer为空时
		amount.setIssuer(""); //Currency issuer
		amount.setCurrency(Jingtum.getCurrencyCNY());
		amount.setLimit(200); //使用limit，而不是value
		try {
			@SuppressWarnings("unused")
			RequestResult pr06 = wallet.addRelation(RelationType.authorize, "jsTo3VKjQrgDKdm5iQdMjc9YqDfrWJn1hw", amount, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
	    }
		
		//异常情况7  Issuer为null时
		RelationAmount amount07 = new RelationAmount();
		amount07.setCurrency(Jingtum.getCurrencyCNY());
		amount07.setLimit(100); //使用limit，而不是value
		try {
			amount07.setIssuer(null); //Currency issuer
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//异常情况8  Issuer为 无效地址时
		RelationAmount amount08 = new RelationAmount();
		amount08.setCurrency(Jingtum.getCurrencyCNY());
		amount08.setLimit(100); //使用limit，而不是value
		try {
			amount08.setIssuer("janxMdrWE2SUzTqRUtfy1111111cH4UGewMMeHa9f"); //Currency issuer
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//异常情况9  Currency为无效时
		RelationAmount amount09 = new RelationAmount();
		amount09.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
		amount09.setLimit(100); //使用limit，而不是value
		try {
			amount09.setCurrency("AAAAA");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid currency!",ex.getMessage());
	    }
		
		//异常情况10  Currency为空时
		RelationAmount amount10 = new RelationAmount();
		amount10.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
		amount10.setLimit(100); //使用limit，而不是value
		try {
			amount10.setCurrency("");
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid currency!",ex.getMessage());
	    }
		
		//异常情况11  Currency为null时
		RelationAmount amount11 = new RelationAmount();
		amount11.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
		amount11.setLimit(100); //使用limit，而不是value
		try {
			amount11.setCurrency(null);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid currency!",ex.getMessage());
	    }
        
		//异常情况12  limit小于零时
		RelationAmount amount12 = new RelationAmount();
		amount12.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
		amount12.setCurrency(Jingtum.getCurrencyCNY());
		//捕获异常信息
		try {
			amount12.setLimit(-0.01); //使用limit，而不是value
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid limit!",ex.getMessage());
	    }
		
		//异常情况13     钱包未激活时
		Wallet wallet13 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo");
		RelationAmount amount13 = new RelationAmount();
		amount13.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
		amount13.setCurrency(Jingtum.getCurrencyCNY());
		amount13.setLimit(50); //使用limit，而不是value
		//捕获异常信息
		try {
			@SuppressWarnings("unused")
			RequestResult pr13 = wallet13.addRelation(RelationType.authorize, "jsTo3VKjQrgDKdm5iQdMjc9YqDfrWJn1hw", amount13, true);
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
	}

	/**
	*
	* delete relationship
	 * @throws FailedException 
	* 
	*/
	@Test
	public void testDeleteRelation() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		//初始化信任
		Wallet wallet = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW");; //根据井通地址生成钱包
		RelationAmount currency = new RelationAmount();
		
		//正常情况1  validate为ture时
		currency.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
		currency.setCurrency(Jingtum.getCurrencyUSD());
		RequestResult pr1 = wallet.removeRelation(RelationType.authorize, "jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S", currency, true);
		assertEquals(true,pr1.getSuccess());
		assertEquals("validated",pr1.getState());
		
		//正常情况2  validate为false时
		currency.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
		currency.setCurrency(Jingtum.getCurrencyCNY());
        RequestResult pr = wallet.removeRelation(RelationType.authorize, "jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf", currency, false);
		assertEquals(true,pr.getSuccess());
		assertEquals("pending",pr.getState());
		
		//异常情况1  RelationType为all时
		currency.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
		currency.setCurrency(Jingtum.getCurrencyCNY());
		//捕获异常信息
		try {
			@SuppressWarnings("unused")
			RequestResult pr01 = wallet.removeRelation(RelationType.all, "jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf", currency, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid relation type!",ex.getMessage());
	    }
		
		//异常情况2  counterParty为空时
		currency.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
		currency.setCurrency(Jingtum.getCurrencyCNY());
		//捕获异常信息
		try {
			@SuppressWarnings("unused")
			RequestResult pr02 = wallet.removeRelation(RelationType.authorize, "", currency, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//异常情况3  counterParty为null时
		currency.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
		currency.setCurrency(Jingtum.getCurrencyCNY());
		//捕获异常信息
		try {
			@SuppressWarnings("unused")
			RequestResult pr03 = wallet.removeRelation(RelationType.authorize, null, currency, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
		
		//异常情况4  counterParty为无效时
		currency.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
		currency.setCurrency(Jingtum.getCurrencyCNY());
		//捕获异常信息
		try {
			@SuppressWarnings("unused")
			RequestResult pr04 = wallet.removeRelation(RelationType.authorize, "1111111ssssssaaaa2222", currency, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Invalid Jingtum address!",ex.getMessage());
	    }
        
		//异常情况5  setCurrency为SWT时
		currency.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
		currency.setCurrency(Jingtum.getCurrencySWT());
		//捕获异常信息
		try {
			@SuppressWarnings("unused")
			RequestResult pr05 = wallet.removeRelation(RelationType.authorize, "jMhLAPaNFo288PNo5HMC37kg6ULjJg8vPf", currency, true);
	    } catch (InvalidParameterException ex) {
	    	assertEquals("Please set currency other than SWT",ex.getMessage());
	    }
		
		//异常处理6   钱包未激活时
		Wallet wallet06 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo");; //根据井通地址生成钱包
		RelationAmount currency06 = new RelationAmount();
		currency06.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
		currency06.setCurrency(Jingtum.getCurrencyCNY());
		//捕获异常信息
		try {
			@SuppressWarnings("unused")
			RequestResult pr12 = wallet06.removeRelation(RelationType.authorize, "jsTo3VKjQrgDKdm5iQdMjc9YqDfrWJn1hw", currency06, true);
	    } catch (APIException ex) {
	    	assertEquals("Inactivated Account;",ex.getMessage());
	    }
	 }
		
		/**
		*
		* get relationship
		 * @throws FailedException 
		* 
		*/
		@Test
		public void testGetRelations() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {

			Wallet wallet = new Wallet("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa","snqFcHzRe22JTM8j7iZVpQYzxEEbW");
			//正常情况1  authorize作为查询条件
			RelationCollection rc1 = wallet.getRelationList(RelationType.authorize, null, null, null);//参数均为可选参数,作为查询条件
			//测试对象rc2是否为null
			assertNotNull(rc1);
			
			Iterator<Relation> it1 = rc1.getData().iterator();
			Relation re1;
			while(it1.hasNext())
			{
				re1 = (Relation)it1.next();
				//判断关系列表是否正确
				assertEquals("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa",re1.getAccount());
				assertEquals("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f",re1.getAmount().getIssuer());
			}
			
			//正常情况2  counterparty为jsTo3VKjQrgDKdm5iQdMjc9YqDfrWJn1hw时
			RelationCollection rc2 = wallet.getRelationList(RelationType.authorize, "jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S", null, null);//参数均为可选参数,作为查询条件
			//测试对象rc2是否为null
			assertNotNull(rc2);
			
			Iterator<Relation> it2 = rc2.getData().iterator();
			Relation re2;
			while(it2.hasNext())
			{
				re2 = (Relation)it2.next();
				//判断关系列表是否正确
				assertEquals("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa",re2.getAccount());
				assertEquals(RelationType.authorize,re2.getType());
				assertEquals("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",re2.getCounterparty());
				assertEquals("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f",re2.getAmount().getIssuer());
			}
			
			//正常情况3  counterparty和amount都有效时
			RelationAmount amount3 = new RelationAmount();
			amount3.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
			amount3.setCurrency(Jingtum.getCurrencyCNY());
			RelationCollection rc3 = wallet.getRelationList(RelationType.authorize, "jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S", amount3, null);//参数均为可选参数,作为查询条件
			Iterator<Relation> it3 = rc3.getData().iterator();
			Relation re3;
			while(it3.hasNext())
			{   
				re3 = (Relation)it3.next();
				//判断关系列表是否正确
				assertEquals("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa",re3.getAccount());
				assertEquals(RelationType.authorize,re3.getType());
				assertEquals("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S",re3.getCounterparty());
				assertEquals("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f",re3.getAmount().getIssuer());
			}
			
			//正常情况4  counterparty为空时
			RelationAmount amount4 = new RelationAmount();
			amount4.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
			amount4.setCurrency(Jingtum.getCurrencyCNY());
			RelationCollection rc4 = wallet.getRelationList(RelationType.authorize, "", amount4, null);//参数均为可选参数,作为查询条件
			Iterator<Relation> it4 = rc4.getData().iterator();
			Relation re4;
			while(it4.hasNext())
			{   
				re4 = (Relation)it4.next();
				//判断关系列表是否正确
				assertEquals("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa",re4.getAccount());
				assertEquals("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f",re4.getAmount().getIssuer());
			}
			
			//异常处理1   钱包未激活时
			Wallet wallet1 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo");
			//捕获异常信息
			try {
				@SuppressWarnings("unused")
				RelationCollection rc01 = wallet1.getRelationList(RelationType.authorize, null, null, null);//参数均为可选参数,作为查询条件
		    } catch (APIException ex) {
		    	assertEquals("Inactivated Account;",ex.getMessage());
		    }
			
			//异常情况2  counterparty为无效时
			Wallet wallet02 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
			//捕获异常信息
			try {
				@SuppressWarnings("unused")
				RelationCollection rc03 = wallet02.getRelationList(RelationType.authorize, "jsTo3VKjQrgDKdm5iQdMjc9YqDfrWJn00000001hw", null, null);//参数均为可选参数,作为查询条件
		    } catch (InvalidParameterException ex) {
		    	assertEquals("Invalid Jingtum address!",ex.getMessage());
		    }
			
			//异常情况3  currency为SWT时
			RelationAmount amount03 = new RelationAmount();
			amount03.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
			amount03.setCurrency(Jingtum.getCurrencySWT());
			Wallet wallet03 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
			//捕获异常信息
			try {
				@SuppressWarnings("unused")
				RelationCollection rc03 = wallet03.getRelationList(RelationType.authorize, null, amount03, null);//参数均为可选参数,作为查询条件
		    } catch (InvalidParameterException ex) {
		    	assertEquals("Please set currency other than SWT",ex.getMessage());
		    }
			
			//异常情况4  counterparty没初始化 时
			Wallet wallet04 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
			RelationAmount amount04 = new RelationAmount();
			//amount04.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
			amount04.setCurrency(Jingtum.getCurrencyCNY());
			//捕获异常信息
			try {
				@SuppressWarnings("unused")
				RelationCollection rc04 = wallet04.getRelationList(RelationType.authorize, null, amount04, null);//参数均为可选参数,作为查询条件
		    } catch (InvalidParameterException ex) {
		    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
		    }
	   }
		
		/**
		*
		* get CounterpartyRelations
		 * @throws FailedException 
		* 
		*/
		@Test
		public void testGetCounterpartyRelations() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
			Wallet wallet1 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
			//正常情况1  authorize作为查询条件
			RelationCollection rc1 = wallet1.getCoRelationList(RelationType.authorize, null, null, null);//参数均为可选参数
			Iterator<Relation> it1 = rc1.getData().iterator();
			Relation re1;
			while(it1.hasNext())
			{   
				re1 = (Relation)it1.next();
				assertEquals("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa",re1.getAccount());
				assertEquals(RelationType.authorize,re1.getType()); //关系类型
				assertEquals("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f",re1.getAmount().getIssuer()); //货币发行方
			}
			
			//正常情况2    关系主动方的井通地址address作为查询条件
			RelationCollection rc2 = wallet1.getCoRelationList(null, "js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa", null, null);//参数均为可选参数
			Iterator<Relation> it2 = rc2.getData().iterator();
			Relation re2;
			while(it2.hasNext())
			{   
				re2 = (Relation)it2.next();
				assertEquals("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa",re2.getAccount());
				assertEquals(RelationType.authorize,re2.getType()); //关系类型
				assertEquals("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f",re2.getAmount().getIssuer()); //货币发行方
			}
			
			//正常情况3  amount作为查询条件
			RelationAmount amount3 = new RelationAmount();
			amount3.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
			amount3.setCurrency(Jingtum.getCurrencyCNY());
			RelationCollection rc3 = wallet1.getCoRelationList(null, null, amount3, null);//参数均为可选参数
			Iterator<Relation> it3 = rc3.getData().iterator();
			Relation re3;
			while(it3.hasNext())
			{   
				re3 = (Relation)it3.next();
				assertEquals("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa",re3.getAccount());
				assertEquals(RelationType.authorize,re3.getType()); //关系类型
				assertEquals("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f",re3.getAmount().getIssuer()); //货币发行方
			}
			
			//正常情况4    关系主动方地址address和amount作为查询条件
			RelationAmount amount4 = new RelationAmount();
			amount4.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
			amount4.setCurrency(Jingtum.getCurrencyUSD());
			RelationCollection rc4 = wallet1.getCoRelationList(RelationType.authorize, "js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa", amount4, null);//参数均为可选参数
			Iterator<Relation> it4 = rc4.getData().iterator();
			Relation re4;
			while(it4.hasNext())
			{   
				re4 = (Relation)it4.next();
				assertEquals("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa",re4.getAccount());
				assertEquals(RelationType.authorize,re4.getType()); //关系类型
				assertEquals("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f",re4.getAmount().getIssuer()); //货币发行方
			}
			
			//异常情况1   钱包未激活时
			Wallet wallet01 = new Wallet("jhoitsF8aPz6tzxFW4JmiNWoxsHtsnds5z","saadV1p5vQeh4N1YdPGo3N3NS7dZo");
			try {
				@SuppressWarnings("unused")
				RelationCollection rc01 = wallet01.getCoRelationList(RelationType.authorize, null, null, null);//参数均为可选参数
		    } catch (APIException ex) {
		    	assertEquals("Inactivated Account;",ex.getMessage());
		    }
			
			//异常情况2    关系主动方地址address为无效时
			Wallet wallet02 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
			//捕获异常信息
			try {
				@SuppressWarnings("unused")
				RelationCollection rc03 = wallet02.getCoRelationList(RelationType.authorize, "jsTo3VKjQrgDKdm5iQdMjc9YqDfrWJn00000001hw", null, null);//参数均为可选参数,作为查询条件
		    } catch (InvalidParameterException ex) {
		    	assertEquals("Invalid Jingtum address!",ex.getMessage());
		    }
			
			//异常情况3     关系主动方地址address为null且Currency为SWT时
			RelationAmount amount03 = new RelationAmount();
			amount03.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
			amount03.setCurrency(Jingtum.getCurrencySWT());
			Wallet wallet03 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
			//捕获异常信息
			try {
				@SuppressWarnings("unused")
				RelationCollection rc03 = wallet03.getCoRelationList(RelationType.authorize, null, amount03, null);//参数均为可选参数,作为查询条件
		    } catch (InvalidParameterException ex) {
		    	assertEquals("Please set currency other than SWT",ex.getMessage());
		    }
			
			//异常情况4  issuer没初始化 时
			Wallet wallet04 = new Wallet("jfCiWtSt4juFbS3NaXvYV9xNYxakm5yP9S","snwjtucx9vEP7hCazriMbVz8hFiK9");
			RelationAmount amount04 = new RelationAmount();
			//amount04.setIssuer("janxMdrWE2SUzTqRUtfycH4UGewMMeHa9f"); //Currency issuer
			amount04.setCurrency(Jingtum.getCurrencyCNY());
			//捕获异常信息
			try {
				@SuppressWarnings("unused")
				RelationCollection rc04 = wallet04.getCoRelationList(RelationType.authorize, null, amount04, null);//参数均为可选参数,作为查询条件
		    } catch (InvalidParameterException ex) {
		    	assertEquals("Invalid JingtumAmount! Please make sure Currency and Counterparty are all valid.",ex.getMessage());
		    }
	   }
}