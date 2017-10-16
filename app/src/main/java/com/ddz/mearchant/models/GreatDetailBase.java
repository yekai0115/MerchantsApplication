package com.ddz.mearchant.models;

import java.util.List;

/**
 * Created by Administrator on 2017/7/8 0008.
 */

public class GreatDetailBase {
    private String total_praise;
    private List<GreatDetail> detail;

    public String getTotal_praise() {
        return total_praise;
    }

    public void setTotal_praise(String total_praise) {
        this.total_praise = total_praise;
    }

    public List<GreatDetail> getDetail() {
        return detail;
    }

    public void setDetail(List<GreatDetail> detail) {
        this.detail = detail;
    }
}
