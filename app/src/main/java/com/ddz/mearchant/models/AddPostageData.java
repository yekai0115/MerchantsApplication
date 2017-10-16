package com.ddz.mearchant.models;

/**
 * Created by Administrator on 2017/6/16 0016.
 */

public class AddPostageData {
    private String tpl_name;
    private PostageData[] shipping;

    public String getTpl_name() {
        return tpl_name;
    }

    public void setTpl_name(String tpl_name) {
        this.tpl_name = tpl_name;
    }

    public PostageData[] getShipping() {
        return shipping;
    }

    public void setShipping(PostageData[] shipping) {
        this.shipping = shipping;
    }
}
