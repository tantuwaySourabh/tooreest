package com.ebabu.tooreest.beans;

/**
 * Created by Sahitya on 2/4/2017.
 */

public class TransactionHistory {

    private String trans_id;
    private String user_id;
    private String order_id;
    private String title;
    private String amount;
    private String add_or_deduct;
    private String create_at;

    public String getTrans_id() {
        return trans_id;
    }

    public void setTrans_id(String trans_id) {
        this.trans_id = trans_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAdd_or_deduct() {
        return add_or_deduct;
    }

    public void setAdd_or_deduct(String add_or_deduct) {
        this.add_or_deduct = add_or_deduct;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }
}
