package com.ebabu.tooreest.beans;

/**
 * Created by hp on 02/03/2017.
 */
public class Quote {
    private int request_id;
    private String name;
    private String email;
    private String mobile;
    private String description;
    private String budget;
    private String request_dt;
    private String create_at;

    public int getRequest_id() {
        return request_id;
    }

    public void setRequest_id(int request_id) {
        this.request_id = request_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getRequest_dt() {
        return request_dt;
    }

    public void setRequest_dt(String request_dt) {
        this.request_dt = request_dt;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }
}
