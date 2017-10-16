package com.ddz.mearchant.models;

/**
 * Created by Administrator on 2017/6/10 0010.
 */

public class OrderDownBase {



    private int pay_id;
    private String order_id;
    private String money;
    private int profit_id;
    private String add_time;
    private String nickname;
    private String headpic;
    private String mobile;

    public int getPay_id() {
        return pay_id;
    }

    public void setPay_id(int pay_id) {
        this.pay_id = pay_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getProfit_id() {
        return profit_id;
    }

    public void setProfit_id(int profit_id) {
        this.profit_id = profit_id;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
