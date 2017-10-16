package com.ddz.mearchant.models;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/7 0007.
 */

public class OrderDownDetail implements Serializable{
    private String money;
    private String profit;
    private String order_id;
    private String pay_return_id;
    private String pay_type;
    private String add_time;
    private String mobile;
    private String name;
    private String points;

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPay_return_id() {
        return pay_return_id;
    }

    public void setPay_return_id(String pay_return_id) {
        this.pay_return_id = pay_return_id;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
