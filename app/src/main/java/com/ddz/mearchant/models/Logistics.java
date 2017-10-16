package com.ddz.mearchant.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/6/29 0029.
 */

public class Logistics implements Serializable {
    private  String delivery_no;
    private  String type;
    private  String issign;
    private List<LogisticsDetail> delivery_detail;

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

    public List<LogisticsDetail> getDelivery_detail() {
        return delivery_detail;
    }

    public void setDelivery_detail(List<LogisticsDetail> delivery_detail) {
        this.delivery_detail = delivery_detail;
    }
}
