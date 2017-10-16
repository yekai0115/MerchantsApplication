package com.ddz.mearchant.models;

import java.util.List;

/**
 * Created by Administrator on 2017/6/10 0010.
 */

public class OrderBase {
    private String trade_id;
    private String order_status;
    private String order_id;
    private List<Orderdata> goods_list;
    private String goods_pic_uri;

    public String getGoods_pic_uri() {
        return goods_pic_uri;
    }

    public void setGoods_pic_uri(String goods_pic_uri) {
        this.goods_pic_uri = goods_pic_uri;
    }

    public String getTrade_id() {
        return trade_id;
    }

    public void setTrade_id(String trade_id) {
        this.trade_id = trade_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public List<Orderdata> getGoods_list() {
        return goods_list;
    }

    public void setGoods_list(List<Orderdata> goods_list) {
        this.goods_list = goods_list;
    }
}
