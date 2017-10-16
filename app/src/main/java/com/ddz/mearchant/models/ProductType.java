package com.ddz.mearchant.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/6/21 0021.
 */

public class ProductType implements Serializable {

    private int cat_id;
    private String cat_name;
    private int parent_id;
    private List<ProductType> son;

    public int getCat_id() {
        return cat_id;
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public List<ProductType> getSon() {
        return son;
    }

    public void setSon(List<ProductType> son) {
        this.son = son;
    }
}
