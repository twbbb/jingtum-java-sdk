package com.jingtum;

import java.util.Iterator;

import com.jingtum.exception.APIConnectionException;
import com.jingtum.exception.APIException;
import com.jingtum.exception.AuthenticationException;
import com.jingtum.exception.ChannelException;
import com.jingtum.exception.FailedException;
import com.jingtum.exception.InvalidParameterException;
import com.jingtum.exception.InvalidRequestException;
import com.jingtum.model.*;
import com.jingtum.model.*;
import com.jingtum.net.FinGate;

//http://developer.jingtum.com/tongtong-start.html

public class TumTest {
	public static void main(String[] args) throws AuthenticationException, InvalidRequestException,
			APIConnectionException, ChannelException, APIException, FailedException, InvalidParameterException {
		//FinGate fg = FinGate.getInstance();
		FinGate.getInstance().setMode(1);
		FinGate.getInstance().setAccount("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa", "snqFcHzRe22JTM8j7iZVpQYzxEEbW"); //FinGate地址密码
		FinGate.getInstance().setToken("00000003");  //设置用户编号
		FinGate.getInstance().setKey("feef59f67303bf7d0b8dcbb1cc99e802b937ff87"); //用户密码

		String order_id = "thisorderid01"; //获得唯一订单号

		System.out.println("================issue custom tum================");
		//Wallet wallet = FinGate.getInstance().createWallet();
		//used an existing wallet instead
		Wallet wallet = new Wallet("snqFcHzRe22JTM8j7iZVpQYzxEEbW");
		System.out.println(wallet.getAddress());
		System.out.println(wallet.getSecret());
		//FinGate.getInstance().activateWallet(wallet.getAddress());

		boolean isSuccessful = FinGate.getInstance().issueCustomTum(order_id, "8100000003000020160022201800220020000001", 11.27, wallet.getAddress());
		System.out.println(isSuccessful);


		System.out.println("================wallet balance================");

		try {
			Thread.sleep(10000);  //等待发行通成功
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BalanceCollection bc = wallet.getBalance();

		Balance bl;
		Iterator<Balance> it = bc.getData().iterator();
		while (it.hasNext()) {
			bl = (Balance) it.next();
			System.out.println(bl.getCurrency());
			System.out.println(bl.getCounterparty());
			System.out.println(bl.getValue());
		}

		System.out.println("================query custom tum issue================");
		try {
			Thread.sleep(5000);  //等待发行通成功
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Another test order number is
		//IssueRecord ir = FinGate.getInstance().queryIssue("prefix1479331641000001");
		IssueRecord ir = FinGate.getInstance().queryIssue(order_id);
		/*System.out.println(ir.getAccount());
		System.out.println(ir.getAmount());
		System.out.println(ir.getCurrency());
		System.out.println(ir.getDate());
		System.out.println(ir.getOrder());

		System.out.println(ir.getTxHash());*/
		System.out.println("res:" + ir.getStatus());

		System.out.println("================query custom tum status================");
		TumInfo ti = FinGate.getInstance().queryCustomTum("8300000027000020160415201704150120000003");
		System.out.println("res name: " + ti.getName());
		/*System.out.println(ti.getCurrency());
		System.out.println(ti.getName());
		System.out.println(ti.getCirculation());
		System.out.println(ti.getStatus());
		System.out.println(ti.getStartDate());
		System.out.println(ti.getEndDate());
		System.out.println(ti.getDescription());
		System.out.println(ti.getValue());
		System.out.println(ti.getCredit());
		System.out.println(ti.getType());
		System.out.println(ti.getFlags());
		System.out.println(ti.getLogoUrl());*/
	}
}
