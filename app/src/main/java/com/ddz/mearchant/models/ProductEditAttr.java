package com.ddz.mearchant.models;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/26 0026.
 */

public class ProductEditAttr implements Serializable{
    private String goods_attr_id;
    private String attr_value;
    private String attr_price;
    private String attr_number;


    public String getGoods_attr_id() {
        return goods_attr_id;
    }

    public void setGoods_attr_id(String goods_attr_id) {
        this.goods_attr_id = goods_attr_id;
    }

    public String getAttr_value() {
        return attr_value;
    }

    public void setAttr_value(String attr_value) {
        this.attr_value = attr_value;
    }

    public String getAttr_price() {
        return attr_price;
    }

    public void setAttr_price(String attr_price) {
        this.attr_price = attr_price;
    }

    public String getAttr_number() {
        return attr_number;
    }

    public void setAttr_number(String attr_number) {
        this.attr_number = attr_number;
    }
}
