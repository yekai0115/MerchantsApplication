package com.ddz.mearchant.models;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/20 0020.
 */

public class DeliveryInfo implements Serializable{
    private String delivery_id;
    private String delivery_no;
    private String type;
    private String issign;
    private String delivery_detail;
    private String update_time;

    public String getDelivery_id() {
        return delivery_id;
    }

    public void setDelivery_id(String delivery_id) {
        this.delivery_id = delivery_id;
    }

    public String getDelivery_no() {
        return delivery_no;
    }

    public void setDelivery_no(String delivery_no) {
        this.delivery_no = delivery_no;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIssign() {
        return issign;
    }

    public void setIssign(String issign) {
        this.issign = issign;
    }

    public String getDelivery_detail() {
        return delivery_detail;
    }

    public void setDelivery_detail(String delivery_detail) {
        this.delivery_detail = delivery_detail;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }
}
