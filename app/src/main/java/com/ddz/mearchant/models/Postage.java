package com.ddz.mearchant.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/6/16 0016.
 */

public class Postage implements Serializable{
    private String shipping_tpl_id;
    private String shipping_name;
    private List<PostageData> area_list;

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

    public List<PostageData> getArea_list() {
        return area_list;
    }

    public void setArea_list(List<PostageData> area_list) {
        this.area_list = area_list;
    }
}
