package com.ddz.mearchant.models;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/10 0010.
 */

public class Orderdata implements Serializable{
    private String order_detail_id;
    private int goods_number;
    private int pay_status;
    private String goods_name;
    private String goods_fee;
    private String order_status;
    private String attr_value;
    private String profit;
    private String goods_thumb;
    public static final int STATUS_ALL = 0;//全部
    public static final int STATUS_PAY = 1;//待支付
    public static final int STATUS_SEND = 2;//待发货
    public static final int STATUS_RECEIVE = 3;//待收货
    public static final int STATUS_COMPLETE = 4;//已完成

    public static final int ORDER_STATUS_SEND = 0;//待发货
    public static final int ORDER_STATUS_RECEIVE = 1;//待收货
    public static final int ORDER_STATUS_COMPLETE  = 2;//已收货
    public static final int ORDER_STATUS_RRTURN = 3;//退货


    public int getGoods_number() {
        return goods_number;
    }

    public void setGoods_number(int goods_number) {
        this.goods_number = goods_number;
    }

    public int getPay_status() {
        return pay_status;
    }

    public void setPay_status(int pay_status) {
        this.pay_status = pay_status;
    }

    public String getOrder_detail_id() {
        return order_detail_id;
    }

    public void setOrder_detail_id(String order_detail_id) {
        this.order_detail_id = order_detail_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getGoods_fee() {
        return goods_fee;
    }

    public void setGoods_fee(String goods_fee) {
        this.goods_fee = goods_fee;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getAttr_value() {
        return attr_value;
    }

    public void setAttr_value(String attr_value) {
        this.attr_value = attr_value;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getGoods_thumb() {
        return goods_thumb;
    }

    public void setGoods_thumb(String goods_thumb) {
        this.goods_thumb = goods_thumb;
    }
}
