package com.ddz.mearchant.models;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/22 0022.
 */

public class AddProductBase implements Serializable{
    private int goods_id;
    private int cat_id;
    private String goods_name;
    private int profit; //让利模式 ,1为20%, 2为10%, 5为5%
    private String detail;
    private int detail_type; //商品图文详情类型，0图片，1文字
    private String goods_img; //商品图片轮播图,多个图片使用半角逗号”,”隔开
    private String goods_attr;
    private int goods_user_cat_id;
    private String shipping_tpl_name;
    private String goods_user_cat_name;
    private int shipping_tpl_id;
    private int is_on_sale;

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public String getShipping_tpl_name() {
        return shipping_tpl_name;
    }

    public void setShipping_tpl_name(String shipping_tpl_name) {
        this.shipping_tpl_name = shipping_tpl_name;
    }

    public String getGoods_user_cat_name() {
        return goods_user_cat_name;
    }

    public void setGoods_user_cat_name(String goods_user_cat_name) {
        this.goods_user_cat_name = goods_user_cat_name;
    }

    public int getShipping_tpl_id() {
        return shipping_tpl_id;
    }

    public void setShipping_tpl_id(int shipping_tpl_id) {
        this.shipping_tpl_id = shipping_tpl_id;
    }

    public int getIs_on_sale() {
        return is_on_sale;
    }

    public void setIs_on_sale(int is_on_sale) {
        this.is_on_sale = is_on_sale;
    }

    public int getCat_id() {
        return cat_id;
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getDetail_type() {
        return detail_type;
    }

    public void setDetail_type(int detail_type) {
        this.detail_type = detail_type;
    }

    public String getGoods_img() {
        return goods_img;
    }

    public void setGoods_img(String goods_img) {
        this.goods_img = goods_img;
    }

    public String getGoods_attr() {
        return goods_attr;
    }

    public void setGoods_attr(String goods_attr) {
        this.goods_attr = goods_attr;
    }

    public int getGoods_user_cat_id() {
        return goods_user_cat_id;
    }

    public void setGoods_user_cat_id(int goods_user_cat_id) {
        this.goods_user_cat_id = goods_user_cat_id;
    }
}
