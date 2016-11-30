package com.jingtum.model;

/**
 * Created by yifan on 11/29/16.
 */
public class CounterParty extends JingtumObject{
    private String account;
    private int seq;
    private String hash;
    public int getSeq() {
        return seq;
    }
    public String getAccount() {
        return account;
    }
    public String getHash() {
        return hash;
    }
}
