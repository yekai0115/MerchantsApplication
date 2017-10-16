package com.ddz.mearchant.models;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/29 0029.
 */

public class BillRecord implements Serializable {
    private String leave_money;
    private String name;
    private String address;
    private String profit;

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getLeave_money() {
        return leave_money;
    }

    public void setLeave_money(String leave_money) {
        this.leave_money = leave_money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
