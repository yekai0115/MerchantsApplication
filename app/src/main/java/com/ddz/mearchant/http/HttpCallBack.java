package com.ddz.mearchant.http;


import com.ddz.mearchant.BaseApplication;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.utils.GsonUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/7/1 0001.
 */

public class HttpCallBack<T> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.body() == null) {
            try {
                String errorResponse = response.errorBody().string();
                BaseResponse baseResponse1 = GsonUtil.GsonToBean(errorResponse, BaseResponse.class);
                if (baseResponse1.getStatus().equals(Constants.T_ERR_AUTH)) {
                    BaseApplication.getInstance().startLoginActivity();
                }else{
                    BaseApplication.getInstance().showShortToast("服务器连接失败");
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable throwable) {
        BaseApplication.getInstance().showShortToast("服务器连接失败");
    }
}
