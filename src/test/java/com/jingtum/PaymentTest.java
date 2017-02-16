package com.jingtum;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jingtum.exception.APIConnectionException;
import com.jingtum.exception.APIException;
import com.jingtum.exception.AuthenticationException;
import com.jingtum.exception.ChannelException;
import com.jingtum.exception.FailedException;
import com.jingtum.exception.InvalidParameterException;
import com.jingtum.exception.InvalidRequestException;
import com.jingtum.model.Amount;
import com.jingtum.model.OperationClass.OperationListener;
import com.jingtum.model.Payment;
import com.jingtum.model.PaymentOperation;
import com.jingtum.model.RequestResult;
import com.jingtum.model.Wallet;
import com.jingtum.net.FinGate;

import net.jodah.concurrentunit.Waiter;

public class PaymentTest {

	/**
	*
	* Pay validate=true(default) test
	 * @throws FailedException 
	*  
	*/
	@Test
	public void testPay() throws InvalidParameterException, AuthenticationException, InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {
		FinGate.getInstance().setMode(1);

		//已有钱包1余额充足  作为支付方
		Wallet wallet1 = new Wallet("ssHC71HCbhp6FVLLcK2oyyUVjcAY4"); //如进行支付，密钥为必须参数
		Amount jtc = new Amount(); //构建支付的货币
		jtc.setCounterparty(""); //货币发行方
		jtc.setCurrency("SWT"); //货币单位
		jtc.setValue(0.1); //金额

		//Init the payment operation
		//PaymentOperation op = new PaymentOperation(wallet1);

		//op.setDestAddress();
		// 2. construct payment operation
		PaymentOperation op = new PaymentOperation(wallet1);
		op.setDestAddress("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ");
		op.setAmount(jtc);
//		op.setValidate(true);
		String payment_id = "paymenttest"+Long.toString(System.currentTimeMillis());
		op.setClientID(payment_id);//optional
		op.setMemo("Java test memo");
// 3. submit payment
		RequestResult payment01 = op.submit();
		
		System.out.println("result:" + payment01.toString());

		//正常情况1  是否等待结果为true时
		assertEquals(true, payment01.getSuccess()); //交易是否成功
		assertEquals("validated", payment01.getState()); //交易状态
		assertEquals("tesSUCCESS", payment01.getResult()); //支付服务器结果

		//Check the payment after ledger close
		Payment payment1 = wallet1.getPayment(payment_id);
		assertEquals(payment_id,payment1.getClient_resource_id());
		assertEquals(true,payment1.getSuccess());
		assertEquals("tesSUCCESS",payment1.getResult());
		assertEquals("sent",payment1.getType());
		assertEquals("Java test memo",payment1.getMemos().getData().get(0).getMemoData().toString());

	}

	/**
	 *
	 * Pay with path
	 * @throws FailedException
	 *
	 */
	@Test
	public void testPathPay() throws InvalidParameterException, AuthenticationException,
			InvalidRequestException, APIConnectionException, APIException, ChannelException, FailedException {


		FinGate.getInstance().setMode(FinGate.getInstance().DEVELOPMENT);

		//已有钱包1余额充足  作为支付方
		Wallet wallet1 = new Wallet("ssHC71HCbhp6FVLLcK2oyyUVjcAY4"); //如进行支付，密钥为必须参数
		Amount jtc = new Amount(); //构建支付的货币
		jtc.setCounterparty(""); //货币发行方
		jtc.setCurrency("SWT"); //货币单位
		jtc.setValue(0.1); //金额

		//Init the payment operation
		//PaymentOperation op = new PaymentOperation(wallet1);

		//op.setDestAddress();
		// 2. construct payment operation
		PaymentOperation op = new PaymentOperation(wallet1);
		op.setDestAddress("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ");
		op.setAmount(jtc);
//		op.setValidate(true);
		String payment_id = "paymenttest"+Long.toString(System.currentTimeMillis());
		op.setClientID(payment_id);//optional
		op.setMemo("Java test memo");
// 3. submit payment
		RequestResult payment01 = op.submit();
	}
	
	@Test 
    public void testListener() throws Throwable { 
		FinGate.getInstance().setMode(1);
		//已有钱包1余额充足  作为支付方
		Wallet wallet1 = new Wallet("ssHC71HCbhp6FVLLcK2oyyUVjcAY4"); //如进行支付，密钥为必须参数
		Amount jtc = new Amount(); //构建支付的货币
		jtc.setCounterparty(""); //货币发行方
		jtc.setCurrency("SWT"); //货币单位
		jtc.setValue(0.1); //金额

		//Init the payment operation
		//PaymentOperation op = new PaymentOperation(wallet1);

		//op.setDestAddress();
		// 2. construct payment operation
		PaymentOperation op = new PaymentOperation(wallet1);
		op.setDestAddress("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ");
		op.setAmount(jtc);
//		op.setValidate(true);
//		op.setClientId("20611171957");//can be set by user
		
// 		3. submit payment
		final Waiter waiter = new Waiter();
		op.submit(new OperationListener() {
			public void onComplete(RequestResult result) {
				
				//正常情况1  是否等待结果为true时
				waiter.assertNotNull(result);
				waiter.assertEquals(true, result.getSuccess()); //交易是否成功
				waiter.assertEquals("validated", result.getState()); //交易状态
				waiter.assertEquals("tesSUCCESS", result.getResult()); //支付服务器结果
				System.out.println("submit done.");
				
				waiter.resume();
			}
		});
		waiter.await();
    }
}
