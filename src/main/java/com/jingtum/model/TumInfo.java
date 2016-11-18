/*
 * Copyright www.jingtum.com Inc. 
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jingtum.model;

/**
 * @author jzhao
 * @version 1.0
 * TumInfo model
 */

public class TumInfo extends JingtumObject{
	private String r3_currency;
	private String r4_name;
	private String r5_circulation;
	private String r6_status;
	private String r7_start_date;
	private String r8_end_date;
	private String r9_description;
	private int ra_value;
	private long rb_credit;
	private String rc_type;
	private boolean rd_break;
	private boolean re_forbid;
	private boolean rf_exchange;
	private boolean rg_swt;
	private String rh_url;
	
	public String getCurrency() {
		return r3_currency;
	}
	public String getName() {
		return r4_name;
	}
	public double getCirculation() {
		return Double.parseDouble(r5_circulation);
	}
	public boolean getStatus() {
		return "NORMAL".equals(r6_status);
	}
	public String getStartDate() {
		return r7_start_date;
	}
	public String getEndDate() {
		return r8_end_date;
	}
	public String getDescription() {
		return r9_description;
	}
	public int getValue() {
		return ra_value;
	}
	public long getCredit() {
		return rb_credit;
	}
	public String getType() {
		return rc_type;
	}
	public long getFlags(){
		long flags = 0;
		if(rd_break){
			flags += 1;
		}
		if(rf_exchange){
			flags += 10;
		}
		if(re_forbid){
			flags += 100;
		}
		if(rg_swt){
			flags += 1000;
		}
		return flags;
	}
	public String getLogoUrl() {
		return rh_url;
	}
	
	
}
