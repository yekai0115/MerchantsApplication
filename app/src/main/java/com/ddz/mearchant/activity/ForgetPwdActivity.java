package com.ddz.mearchant.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BannerBean;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.view.ClearEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForgetPwdActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_shouye,tv_fujin,tv_role,tv_geren;

    private Context mContext;
    private ClearEditText inputMobile,inputYzm;
    private Button loginNext,getYzmButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        mContext=this;
        initViews();
        //login();
        //getbanner();
    }

    @Override
    protected void initViews() {
        inputMobile = (ClearEditText)findViewById(R.id.input_mobile);
        inputYzm = (ClearEditText)findViewById(R.id.input_yzm);
        getYzmButton = (Button)findViewById(R.id.get_yzm_button);
        loginNext = (Button)findViewById(R.id.login_next);
        loginNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_next:
                startActivity(SetPwdActivity.class);
                break;
        }
    }

    @Override
    protected void initEvents() {

    }
    /**
     * 获取账户余额
     */
    private void login() {
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.loginRepo("18813904075:123456789");//"18813904075:123456789"
        call.enqueue(new Callback<BaseResponse<String>>() {

            @Override
            public void onResponse(Call<BaseResponse<String>> arg0,
                                   Response<BaseResponse<String>> response) {
            //    dialog.dismiss();
                BaseResponse<String> baseResponse = response.body();
                if (null != baseResponse) {
                    String status = baseResponse.getStatus();
                   String data = baseResponse.getData();
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<String>> arg0,
                                  Throwable arg1) {
//                dialog.dismiss();
//                String throwable = arg1.toString();
//                if (throwable.contains(XingYunConstant.SocketTimeoutException)
//                        || throwable.contains(XingYunConstant.ConnectException)) {
//                    MyTools.showToast(mContext, "网络状态不佳,请稍后再试");
//                }
            }
        });

    }


    private void getbanner() {
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<BannerBean>>> call = userBiz.getbanner();
        call.enqueue(new Callback<BaseResponse<List<BannerBean>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<BannerBean>>> arg0,
                                   Response<BaseResponse<List<BannerBean>>> response) {
                //    dialog.dismiss();
                BaseResponse<List<BannerBean>> baseResponse = response.body();
                if (null != baseResponse) {
                    String status = baseResponse.getStatus();
                    List<BannerBean> bannerBeanList=baseResponse.getData();
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<List<BannerBean>>> arg0,
                                  Throwable arg1) {
//                dialog.dismiss();
//                String throwable = arg1.toString();
//                if (throwable.contains(XingYunConstant.SocketTimeoutException)
//                        || throwable.contains(XingYunConstant.ConnectException)) {
//                    MyTools.showToast(mContext, "网络状态不佳,请稍后再试");
//                }
            }
        });

    }



}
