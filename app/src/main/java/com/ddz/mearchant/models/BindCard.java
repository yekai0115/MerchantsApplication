package com.ddz.mearchant.models;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/28 0028.
 */

public class BindCard implements Serializable{
    private  String code;
    private String bankname;
    private boolean isChecked=false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }
}
