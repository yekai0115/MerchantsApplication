package com.ddz.mearchant.models;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/14 0014.
 */

public class ShopBase implements Serializable{
//    参数名	类型	说明
//    name	string	商铺名称
//    service_time	string	营业时间，商家自行填写，格式12:00-01:54
//    logo	string	门头照
//    ablum	string	店内照
//    shop_licence	string	资质证书
//    shop_headpic	string	头像
//    cat_id	string	主营业务id
//    legal_phone	string	联系电话
//    cat_name	string	主营业务
//    address	string	地址

    private  String name;
    private  String service_time;
    private  String logo;
    private  String ablum;
    private  String shop_licence;
    private  String shop_headpic;
    private  String cat_id;
    private  String legal_phone;
    private  String cat_name;
    private  String address;
    private  String record_order;
    private  String qr_pay;

    public String getQr_pay() {
        return qr_pay;
    }

    public void setQr_pay(String qr_pay) {
        this.qr_pay = qr_pay;
    }

    public String getRecord_order() {
        return record_order;
    }

    public void setRecord_order(String record_order) {
        this.record_order = record_order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getService_time() {
        return service_time;
    }

    public void setService_time(String service_time) {
        this.service_time = service_time;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAblum() {
        return ablum;
    }

    public void setAblum(String ablum) {
        this.ablum = ablum;
    }

    public String getShop_licence() {
        return shop_licence;
    }

    public void setShop_licence(String shop_licence) {
        this.shop_licence = shop_licence;
    }

    public String getShop_headpic() {
        return shop_headpic;
    }

    public void setShop_headpic(String shop_headpic) {
        this.shop_headpic = shop_headpic;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getLegal_phone() {
        return legal_phone;
    }

    public void setLegal_phone(String legal_phone) {
        this.legal_phone = legal_phone;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
