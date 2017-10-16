package com.ddz.mearchant.models;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/16 0016.
 */

public class PostageData implements Serializable{
    private String price;
    private String region_str;
    private String region;


    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRegion_str() {
        return region_str;
    }

    public void setRegion_str(String region_str) {
        this.region_str = region_str;
    }
}
