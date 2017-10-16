package com.ddz.mearchant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.Custom;
import com.ddz.mearchant.view.ClearEditText;
import com.ddz.mearchant.view.HandyTextView;

import retrofit2.Call;
import retrofit2.Response;


public class AddCustomActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext;
    private ClearEditText customName;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private Custom mCustom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_name);
        mContext=this;
        initDialog();
        initViews();
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initViews() {
        customName = (ClearEditText)findViewById(R.id.custom_name);
        htvRight = (HandyTextView)findViewById(R.id.title_htv_rigit);
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        htvRight.setVisibility(View.VISIBLE);
        htvCenter.setText("添加分类");
        htvRight.setOnClickListener(this);
        htvLeft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_rigit:
                    addCustomName();
                break;
            case R.id.title_htv_left:
                    defaultFinish();
                break;
        }
    }
    private  void  addCustomName(){
        if (TextUtils.isEmpty(customName.getText().toString())){
            showShortToast("您还没有输入分类名称");
            return;
        }
        dialog.show();
        dialog.setCancelable(false);
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.addGoodsCategory(baseApplication.mUser.token,customName.getText().toString());//"18813904075:123456789"
        call.enqueue(new HttpCallBack<BaseResponse<String>>() {

            @Override
            public void onResponse(Call<BaseResponse<String>> arg0,
                                   Response<BaseResponse<String>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<String> baseResponse = response.body();
                if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
                    String status = baseResponse.getStatus();
                    String data = baseResponse.getData();
                    if (status.equals(Constants.T_OK)){
                        defaultFinish();
                        showShortToast("添加分类成功");
                        Intent intent = new Intent();
                        intent.setClass(AddCustomActivity.this,CustomClassificationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<String>> arg0,
                                  Throwable arg1) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onFailure(arg0,arg1);
            }

        });
    }

}
