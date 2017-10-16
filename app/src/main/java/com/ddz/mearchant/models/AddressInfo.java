package com.ddz.mearchant.models;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/20 0020.
 */

public class AddressInfo implements Serializable{
    private String addr_name;
    private String addr_mobile;
    private String addr_province;
    private String addr_city;
    private String addr_county;
    private String addr_detail;

    public String getAddr_name() {
        return addr_name;
    }

    public void setAddr_name(String addr_name) {
        this.addr_name = addr_name;
    }

    public String getAddr_mobile() {
        return addr_mobile;
    }

    public void setAddr_mobile(String addr_mobile) {
        this.addr_mobile = addr_mobile;
    }

    public String getAddr_province() {
        return addr_province;
    }

    public void setAddr_province(String addr_province) {
        this.addr_province = addr_province;
    }

    public String getAddr_city() {
        return addr_city;
    }

    public void setAddr_city(String addr_city) {
        this.addr_city = addr_city;
    }

    public String getAddr_county() {
        return addr_county;
    }

    public void setAddr_county(String addr_county) {
        this.addr_county = addr_county;
    }

    public String getAddr_detail() {
        return addr_detail;
    }

    public void setAddr_detail(String addr_detail) {
        this.addr_detail = addr_detail;
    }
}
