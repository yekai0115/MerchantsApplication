package com.ddz.mearchant.bean;

import java.io.Serializable;

/**
 * 网络返回基类 支持泛型
 *
 * @param <T>
 * @author yg
 */
public class BaseResponse<T> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6453824795394683688L;

    private String status;
    private T data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
