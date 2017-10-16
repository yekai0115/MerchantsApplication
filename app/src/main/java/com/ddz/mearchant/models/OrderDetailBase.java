package com.ddz.mearchant.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/6/10 0010.
 */

public class OrderDetailBase implements Serializable{

    /**
     * Auto-generated: 2017-06-20 12:15:47
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
        private String trade_id;
        private String shipping_status;
        private String addr_id;
        private String delivery_id;
        private String order_id;
        private String pay_type;
        private String pay_return_id;
        private String order_status;
        private String goods_pic_uri;
        private String add_time;
        private String points;
        private AddressInfo addr_info;
        private List<Orderdata> goods_list;
        private DeliveryInfo delivery_info;

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getTrade_id() {
        return trade_id;
    }

    public void setTrade_id(String trade_id) {
        this.trade_id = trade_id;
    }

    public String getShipping_status() {
        return shipping_status;
    }

    public void setShipping_status(String shipping_status) {
        this.shipping_status = shipping_status;
    }

    public String getAddr_id() {
        return addr_id;
    }

    public void setAddr_id(String addr_id) {
        this.addr_id = addr_id;
    }

    public String getDelivery_id() {
        return delivery_id;
    }

    public void setDelivery_id(String delivery_id) {
        this.delivery_id = delivery_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getPay_return_id() {
        return pay_return_id;
    }

    public void setPay_return_id(String pay_return_id) {
        this.pay_return_id = pay_return_id;
    }

    public String getGoods_pic_uri() {
        return goods_pic_uri;
    }

    public void setGoods_pic_uri(String goods_pic_uri) {
        this.goods_pic_uri = goods_pic_uri;
    }

    public AddressInfo getAddr_info() {
        return addr_info;
    }

    public void setAddr_info(AddressInfo addr_info) {
        this.addr_info = addr_info;
    }

    public List<Orderdata> getGoods_list() {
        return goods_list;
    }

    public void setGoods_list(List<Orderdata> goods_list) {
        this.goods_list = goods_list;
    }

    public DeliveryInfo getDelivery_info() {
        return delivery_info;
    }

    public void setDelivery_info(DeliveryInfo delivery_info) {
        this.delivery_info = delivery_info;
    }
}
