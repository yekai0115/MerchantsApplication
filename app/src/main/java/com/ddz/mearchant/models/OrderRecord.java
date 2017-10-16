package com.ddz.mearchant.models;

/**
 * Created by Administrator on 2017/6/12 0012.
 */

public class OrderRecord {

   private int total;
   private int week;
   private int delivery;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getDelivery() {
        return delivery;
    }

    public void setDelivery(int delivery) {
        this.delivery = delivery;
    }
}
