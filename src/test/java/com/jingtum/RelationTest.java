package com.jingtum;

import com.jingtum.exception.*;
import com.jingtum.model.*;
import com.jingtum.model.OperationClass.OperationListener;
import com.jingtum.net.FinGate;
import com.jingtum.util.Utility;
import net.jodah.concurrentunit.Waiter;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RelationTest {

	/**
	*
	*  Get Relation test
	 * @throws FailedException
	 * @throws InvalidParameterException
	 * @throws AuthenticationException,
	 * @throws InvalidRequestException,
	 * @throws APIConnectionException,
	 * @throws APIException,
	 * @throws ChannelException,
	 * @throws FailedException
	*  
	*/
	@Test
	public void testGetRelation() throws InvalidParameterException, AuthenticationException, InvalidRequestException,
			APIConnectionException, APIException, ChannelException, FailedException {

		//设置环境
		FinGate.getInstance().setMode(FinGate.getInstance().DEVELOPMENT);

		//产生钱包
		Wallet wallet1 = new Wallet("ssHC71HCbhp6FVLLcK2oyyUVjcAY4");

		//正常情况 1  获取关系数据，类型为authorize
		RelationCollection rc = wallet1.getRelation("authorize");

		//测试对象rc是否为null
		assertNotNull(rc);

		Iterator<Relation> it = rc.getData().iterator();

		while(it.hasNext())
		{
			Relation rl = (Relation)it.next();
			assertNotNull(rl);
			assertEquals("authorize", rl.getType().toString()); //关系是否成功
		}

		System.out.println(Utility.isValidAddress("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ"));

		//正常情况 2  获取关系数据，类型为authorize,，对家为指定地址。

		RelationCollection rc2 = wallet1.getRelation("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ");

		assertNotNull(rc2);

		it = rc2.getData().iterator();

		while(it.hasNext())
		{
			Relation rl = (Relation)it.next();
			assertNotNull(rl);
			assertEquals("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ", rl.getCounterparty()); //关系是否成功
		}



	}

	/**
	 *
	 * Set relation
	 * @throws FailedException
	 *
	 */
	@Test
	public void testSetRelation() throws InvalidParameterException, AuthenticationException,
			InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {


		//设置环境
		FinGate.getInstance().setMode(FinGate.getInstance().DEVELOPMENT);

		//产生钱包
		Wallet wallet1 = new Wallet("ssHC71HCbhp6FVLLcK2oyyUVjcAY4");

		RelationCollection rc2 = wallet1.getRelation("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ");

		assertNotNull(rc2);

		int relation_num = rc2.getData().size();

		System.out.println("Relation num "+relation_num);
		System.out.println("Address j44rkkVKxnqhm9cP7kQqpj27YGYTFAEFRh is "+Utility.isValidAddress("j44rkkVKxnqhm9cP7kQqpj27YGYTFAEFRh"));
		String tadd = "j44rkkVKxnqhm9cP7kQqpj27YGYTFAEFRh";
		System.out.println("Address "+tadd+" is "+Utility.isValidAddress(tadd));

		Amount jtc = new Amount(); //构建支付的货币
		jtc.setIssuer("jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT"); //货币发行方
		jtc.setCurrency("USD"); //货币单位
		jtc.setValue(50.00); //金额


		// 2. construct relation operation
		RelationOperation op = new RelationOperation(wallet1);
		op.setType("authorize");
		op.setCounterparty("j44rkkVKxnqhm9cP7kQqpj27YGYTFAEFRh");
		op.setAmount(jtc);



		// 3. submit relation
		RequestResult relation01 = op.submit();
		System.out.println("result:" + relation01.toString());

		//正常情况1  是否等待结果为true时
		assertEquals(true, relation01.getSuccess()); //交易是否成功
		assertEquals("validated", relation01.getState()); //交易状态
		assertEquals(true, relation01.getSuccess()); //服务器结果

		//update the relation number
		rc2 = wallet1.getRelation("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ");

		assertNotNull(rc2);

		System.out.println("Relation num"+rc2.getData().size());

		// 4. construct relation operation
		RemoveRelationOperation op2 = new RemoveRelationOperation(wallet1);
		op2.setType("authorize");
		op2.setCounterparty("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ");
		op2.setAmount(jtc);


		// 5. submit relation
		RequestResult relation02 = op2.submit();
		System.out.println("result:" + relation02.toString());

		//6. check if the remove succeed
		assertEquals(true, relation02.getSuccess()); //交易是否成功
		assertEquals("validated", relation02.getState()); //交易状态
		//assertEquals("tesSUCCESS", relation02.getResult()); //返回服务器结果
		assertEquals(true, relation01.getSuccess()); //
	}
	
	@Test 
    public void testListener() throws Throwable { 
		FinGate.getInstance().setMode(1);
		//已有钱包1余额充足  作为支付方
		Wallet wallet1 = new Wallet("ssHC71HCbhp6FVLLcK2oyyUVjcAY4");
//		Amount jtc = new Amount(); //构建支付的货币
//		jtc.setIssuer(""); //货币发行方
//		jtc.setCurrency("SWT"); //货币单位
//		jtc.setValue(0.1); //金额
//
//		//Init the relation operation
//		//relationOperation op = new relationOperation(wallet1);
//
//		//op.setDestAddress();
//		// 2. construct relation operation
//		relationOperation op = new relationOperation(wallet1);
//		op.setDestAddress("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ");
//		op.setAmount(jtc);
////		op.setValidate(true);
////		op.setClientId("20611171957");//can be set by user
//
//// 		3. submit relation
//		final Waiter waiter = new Waiter();
//		op.submit(new OperationListener() {
//			public void onComplete(RequestResult result) {
//
//				//正常情况1  是否等待结果为true时
//				waiter.assertNotNull(result);
//				waiter.assertEquals(true, result.getSuccess()); //交易是否成功
//				waiter.assertEquals("validated", result.getState()); //交易状态
//				waiter.assertEquals("tesSUCCESS", result.getResult()); //支付服务器结果
//				System.out.println("submit done.");
//
//				waiter.resume();
//			}
//		});
//		waiter.await();
    }
}
