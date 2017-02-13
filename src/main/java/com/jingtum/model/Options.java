package com.jingtum.model;

import com.google.gson.annotations.Expose;

/**
 * Created by zpli on 2/1/17.
 * options used to filter the payment and transactions
 *     source_account String 支付方地址
 *     destination_account String 支付接收方地址
 *     exclude_failed Boolean 是否移除失败的支付历史
 *     direction String 支付方向，incoming或outgoing
 *     results_per_page Integer 返回的每页数据量，默认每页10项
 *     page Integer 返回第几页的数据，从第1页开始
 */
public class Options {

    private String source_account;
    private String destination_account;

    private Boolean exclude_failed;

    private int results_per_page;
    private int page;

    public Options(){
        source_account = "";
        destination_account = "";
        exclude_failed = false;
        results_per_page = 0;
        page = 0;
    }
    /**
     * Set the source account parameter
     * @return void
     */
    public void setSourceAccount(String in_str) {
        this.source_account = in_str;
    }

    /**
     * Set the destination account parameter
     * @return void
     */
    public void setDestinationAccount(String in_str) {
        this.destination_account = in_str;
    }

    /**
     * Set the source account parameter
     * @return void
     */
    public void setExcludeFailed(Boolean in_flag) {
        this.exclude_failed = in_flag;
    }

    /**
     * Set the results displayed per page
     * @return void
     */
    public void setResultsPerPage(int in_int) {
        this.results_per_page = in_int;
    }

    /**
     * Set the source account parameter
     * @return void
     */
    public void setPage(int in_int) {
        this.page = in_int;
    }


    /**
     * Get source account
     * @return source_account
     */
    public String getSourceAccount() {
        return source_account;
    }
    /**
     * Get destination account
     * @return source_account
     */
    public String getDestinationAccount() {
        return destination_account;
    }

    public Boolean getExcludeFailed() {
        return exclude_failed;
    }

    public int getPage() {
        return page;
    }

    public int getResultsPerPage() {
        return results_per_page;
    }

}
