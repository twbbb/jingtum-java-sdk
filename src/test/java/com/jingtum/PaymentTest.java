package com.jingtum;

import com.jingtum.exception.*;
import com.jingtum.model.*;
import com.jingtum.net.FinGate;
import com.jingtum.net.JingtumAPIAndWSServer;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class PaymentTest {

	/**
	*
	* Pay test
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
		//op.setValidate(true);
		//op.setClientId("20611171957");//can be skipped

// 3. submit payment
		RequestResult payment01 = op.submit();

		//正常情况1  是否等待结果为true时
		assertEquals(true, payment01.getSuccess()); //交易是否成功
		assertEquals("validated", payment01.getState()); //交易状态
		assertEquals("tesSUCCESS", payment01.getResult()); //支付服务器结果

	}

}
