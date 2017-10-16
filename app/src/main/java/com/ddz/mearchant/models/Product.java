package com.ddz.mearchant.models;

/**
 * Created by Administrator on 2017/6/20 0020.
 */

public class Product {
    private String goods_id;
    private String goods_name;
    private String is_on_sale;
    private String profit;
    private String goods_price;
    private String goods_volume;
    private String warn_number;
    private String goods_number;
    private String goods_thumb;
    private String goods_pid_uri;
    private int goods_status;

    public int getGoods_status() {
        return goods_status;
    }

    public void setGoods_status(int goods_status) {
        this.goods_status = goods_status;
    }

    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getGoods_thumb() {
        return goods_thumb;
    }

    public void setGoods_thumb(String goods_thumb) {
        this.goods_thumb = goods_thumb;
    }

    public String getGoods_pid_uri() {
        return goods_pid_uri;
    }

    public void setGoods_pid_uri(String goods_pid_uri) {
        this.goods_pid_uri = goods_pid_uri;
    }

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

    public String getIs_on_sale() {
        return is_on_sale;
    }

    public void setIs_on_sale(String is_on_sale) {
        this.is_on_sale = is_on_sale;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(String goods_price) {
        this.goods_price = goods_price;
    }

    public String getGoods_volume() {
        return goods_volume;
    }

    public void setGoods_volume(String goods_volume) {
        this.goods_volume = goods_volume;
    }

    public String getWarn_number() {
        return warn_number;
    }

    public void setWarn_number(String warn_number) {
        this.warn_number = warn_number;
    }

    public String getGoods_number() {
        return goods_number;
    }

    public void setGoods_number(String goods_number) {
        this.goods_number = goods_number;
    }
}
