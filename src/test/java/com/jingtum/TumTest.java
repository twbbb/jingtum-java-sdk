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
import com.jingtum.net.JingtumAPIAndWSServer;
import com.jingtum.net.JingtumFingate;

//http://developer.jingtum.com/tongtong-start.html

public class TumTest {
	public static void main(String[] args) throws AuthenticationException, InvalidRequestException, APIConnectionException, ChannelException, APIException, FailedException, InvalidParameterException{
		JingtumFingate.getTestInstance().setFinGate("js4UaG1pjyCEi9f867QHJbWwD3eo6C5xsa", "snqFcHzRe22JTM8j7iZVpQYzxEEbW"); //FinGate地址密码
		JingtumFingate.getTestInstance().setCustom("00000003");  //设置用户编号
		JingtumFingate.getTestInstance().setCustomSecret("feef59f67303bf7d0b8dcbb1cc99e802b937ff87"); //用户密码
		String orderNumber = JingtumAPIAndWSServer.getTestInstance().getNextUUID(); //获得唯一订单号
		
		System.out.println("================issue custom tum================");
		Wallet wallet = JingtumFingate.getTestInstance().createWallet();
		System.out.println(wallet.getAddress());
		System.out.println(wallet.getSecret());
		JingtumFingate.getTestInstance().activateWallet(wallet.getAddress());
		
		boolean isSuccessful = JingtumFingate.getTestInstance().issueCustomTum(orderNumber, "8100000003000020160022201800220020000001", 5, wallet.getAddress());
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
		while(it.hasNext())
		{	
		    bl = (Balance)it.next();
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
		//IssueRecord ir = JingtumFingate.getTestInstance().queryIssue("prefix1479331641000001");
                IssueRecord ir = JingtumFingate.getTestInstance().queryIssue(orderNumber);
		System.out.println(ir.getAccount());
		System.out.println(ir.getAmount());
		System.out.println(ir.getCurrency());
		System.out.println(ir.getDate());
		System.out.println(ir.getOrder());
		System.out.println(ir.getStatus());
		System.out.println(ir.getTxHash());
		
		System.out.println("================query custom tum status================");
		TumInfo ti = JingtumFingate.getTestInstance().queryCustomTum("8300000027000020160415201704150120000003");
		System.out.println(ti.getCurrency());
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
		System.out.println(ti.getLogoUrl());
	}
}
