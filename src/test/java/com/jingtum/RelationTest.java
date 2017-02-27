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

		//设置
		FinGate.getInstance().setMode(1);

		//已有钱包1
		Wallet wallet1 = new Wallet("ssHC71HCbhp6FVLLcK2oyyUVjcAY4");

		//Test get the relation with one type
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
		//Test get the relation with one counterparty
		RelationCollection rc2 = wallet1.getRelation("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ");

		assertNotNull(rc);

		it = rc2.getData().iterator();

		while(it.hasNext())
		{
			Relation rl = (Relation)it.next();
			assertNotNull(rl);
			assertEquals("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ", rl.getCounterparty()); //关系是否成功
		}


		//正常情况1  是否等待结果为true时
//		assertEquals("authorize", it.next().getType()); //关系是否成功
//		assertEquals("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ", it.next().getCounterparty()); //关系对家
		//assertEquals(500, it.next().getLimit()); //支付服务器结果

		//正常情况2    钱包未激活时
		Wallet wallet01 = new Wallet("saadV1p5vQeh4N1YdPGo3N3NS7dZo");
		try {
			@SuppressWarnings("unused")
			RelationCollection bc1 = wallet01.getRelation();
		} catch (InvalidRequestException ex) {
			assertEquals("Error type: transaction\n\t Error message: Inactivated Account;Account not found.\n",ex.getMessage());
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


		FinGate.getInstance().setMode(FinGate.getInstance().DEVELOPMENT);

		//已有钱包1余额充足  作为支付方

		Wallet wallet1 = new Wallet("ssHC71HCbhp6FVLLcK2oyyUVjcAY4"); //如进行支付，密钥为必须参数
//		Amount jtc = new Amount(); //构建支付的货币
//		jtc.setIssuer("jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT"); //货币发行方
//		jtc.setCurrency("CNY"); //货币单位
//		jtc.setValue(0.05); //金额
//
//		//Init the payment operation
//		PaymentChoiceCollection pcc = wallet1.getChoices("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ", jtc);
//
//
//		//op.setDestAddress();
//		if (pcc.getData().size() > 0) {
//			// 2. construct payment operation
//			PaymentOperation op = new PaymentOperation(wallet1);
//			op.setDestAddress("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ");
//			op.setAmount(jtc);
//			String payment_id = "paymentWithPath" + Long.toString(System.currentTimeMillis());
//			op.setClientID(payment_id);//optional
//			op.setPath(pcc.getData().get(0).getPath());
//
//			// 3. submit payment
//			RequestResult payment01 = op.submit();
//			System.out.println("result:" + payment01.toString());
//
//			//正常情况1  是否等待结果为true时
//			assertEquals(true, payment01.getSuccess()); //交易是否成功
//			assertEquals("validated", payment01.getState()); //交易状态
//			assertEquals("tesSUCCESS", payment01.getResult()); //支付服务器结果
//
//		}else
//			System.out.println("path found "+pcc.getData().size());

	}
	
	@Test 
    public void testListener() throws Throwable { 
		FinGate.getInstance().setMode(1);
		//已有钱包1余额充足  作为支付方
		Wallet wallet1 = new Wallet("ssHC71HCbhp6FVLLcK2oyyUVjcAY4"); //如进行支付，密钥为必须参数
//		Amount jtc = new Amount(); //构建支付的货币
//		jtc.setIssuer(""); //货币发行方
//		jtc.setCurrency("SWT"); //货币单位
//		jtc.setValue(0.1); //金额
//
//		//Init the payment operation
//		//PaymentOperation op = new PaymentOperation(wallet1);
//
//		//op.setDestAddress();
//		// 2. construct payment operation
//		PaymentOperation op = new PaymentOperation(wallet1);
//		op.setDestAddress("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ");
//		op.setAmount(jtc);
////		op.setValidate(true);
////		op.setClientId("20611171957");//can be set by user
//
//// 		3. submit payment
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
