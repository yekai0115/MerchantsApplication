package com.ddz.mearchant.models;

/**
 * Created by Administrator on 2017/7/6 0006.
 */

public class GivingRecords {
    private String access_user;
    private String pay_time;
    private String money;
    private String type;

    public String getAccess_user() {
        return access_user;
    }

    public void setAccess_user(String access_user) {
        this.access_user = access_user;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
