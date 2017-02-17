package com.jingtum;

import com.jingtum.exception.*;
import com.jingtum.model.*;
import com.jingtum.model.OperationClass.OperationListener;
import com.jingtum.net.FinGate;
import net.jodah.concurrentunit.Waiter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PaymentPathTest {


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
		jtc.setIssuer("jMcCACcfG37xHy7FgqHerzovjLM5FCk7tT"); //货币发行方
		jtc.setCurrency("CNY"); //货币单位
		jtc.setValue(0.05); //金额

		//Init the payment operation
		PaymentChoiceCollection pcc = wallet1.getChoices("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ", jtc);


		//op.setDestAddress();
		if (pcc.getData().size() < 0) {
			// 2. construct payment operation
			PaymentOperation op = new PaymentOperation(wallet1);
			op.setDestAddress("jJwtrfvKpvJf2w42rmsEzK5fZRqP9Y2xhQ");
			op.setAmount(jtc);
			String payment_id = "paymentWithPath" + Long.toString(System.currentTimeMillis());
			op.setClientID(payment_id);//optional
			op.setPath(pcc.getData().get(0).getPath());

			// 3. submit payment
			RequestResult payment01 = op.submit();
		}else
			System.out.println("path found "+pcc.getData().size());

	}
	
//	@Test
//    public void testListener() throws Throwable {
//		FinGate.getInstance().setMode(1);
//		//已有钱包1余额充足  作为支付方
//		Wallet wallet1 = new Wallet("ssHC71HCbhp6FVLLcK2oyyUVjcAY4"); //如进行支付，密钥为必须参数
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
//    }
}
