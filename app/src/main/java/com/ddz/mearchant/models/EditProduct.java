package com.ddz.mearchant.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/6/26 0026.
 */

public class EditProduct implements Serializable {

    private String goods_id;
    private String goods_name;
    private String profit;
    private String cat_id;
    private String is_on_sale;
    private String cat_name;
    private String detail;
    private String detail_type;
    private String goods_user_cat_id;
    private String user_cate_name;
    private String shipping_tpl_id;
    private String shipping_name;
    private List<String> img;
    private String goods_pic_uri;
    private List<ProductEditAttr> attr;


    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getIs_on_sale() {
        return is_on_sale;
    }

    public void setIs_on_sale(String is_on_sale) {
        this.is_on_sale = is_on_sale;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDetail_type() {
        return detail_type;
    }

    public void setDetail_type(String detail_type) {
        this.detail_type = detail_type;
    }

    public String getGoods_user_cat_id() {
        return goods_user_cat_id;
    }

    public void setGoods_user_cat_id(String goods_user_cat_id) {
        this.goods_user_cat_id = goods_user_cat_id;
    }

    public String getUser_cate_name() {
        return user_cate_name;
    }

    public void setUser_cate_name(String user_cate_name) {
        this.user_cate_name = user_cate_name;
    }

    public String getShipping_tpl_id() {
        return shipping_tpl_id;
    }

    public void setShipping_tpl_id(String shipping_tpl_id) {
        this.shipping_tpl_id = shipping_tpl_id;
    }

    public String getShipping_name() {
        return shipping_name;
    }

    public void setShipping_name(String shipping_name) {
        this.shipping_name = shipping_name;
    }

    public List<String> getImg() {
        return img;
    }

    public void setImg(List<String> img) {
        this.img = img;
    }

    public String getGoods_pic_uri() {
        return goods_pic_uri;
    }

    public void setGoods_pic_uri(String goods_pic_uri) {
        this.goods_pic_uri = goods_pic_uri;
    }

    public List<ProductEditAttr> getAttr() {
        return attr;
    }

    public void setAttr(List<ProductEditAttr> attr) {
        this.attr = attr;
    }
}


