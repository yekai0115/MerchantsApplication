package com.ddz.mearchant.models;

/**
 * Created by Administrator on 2017/6/10 0010.
 */

public class OrderCollection {

    private String pic;
    private String name;
    private String good_id;
    private int profit_type;
    private float price;


    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGood_id() {
        return good_id;
    }

    public void setGood_id(String good_id) {
        this.good_id = good_id;
    }

    public int getProfit_type() {
        return profit_type;
    }

    public void setProfit_type(int profit_type) {
        this.profit_type = profit_type;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
