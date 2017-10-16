package com.ddz.mearchant.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.Custom;
import com.ddz.mearchant.utils.StringUtils;
import com.ddz.mearchant.view.HandyTextView;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/6/24 0024.
 */

public class SetPayPwdActivity extends BaseActivity implements View.OnClickListener{
    private Context mContext;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private Custom mCustom;
    private Button confirmButton;
    private EditText editTextPwd,editTextConfirmPwd;
    private String pwd1, pwd2;
    private String restToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_payinfo_pwd);
        mContext=this;
        restToken = getIntent().getBundleExtra("tokem").getString("tokem");
        initViews();
        initDialog();
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void initViews() {
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        confirmButton = (Button)findViewById(R.id.confirm_button);
        editTextPwd = (EditText) findViewById(R.id.asp_pwd1);
        editTextConfirmPwd = (EditText) findViewById(R.id.asp_pwd2);
        htvCenter.setText("支付密码");
        htvLeft.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
    }
    private boolean validate1() {
        pwd1 = editTextPwd.getText().toString();
        pwd2 = editTextConfirmPwd.getText().toString();
        if(StringUtils.isBlank(pwd1) || pwd1.length() < 6 ) {
            Toast.makeText(this, getResources().getString(R.string.pay_pwd_length), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(StringUtils.isBlank(pwd2)|| pwd2.length() < 6) {
            Toast.makeText(this, getResources().getString(R.string.pay_pwd_length), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!pwd2.equals(pwd1)) {
            Toast.makeText(this, getResources().getString(R.string.asp_pwd_diff), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                defaultFinish();
                break;
            case R.id.confirm_button:
                setNewPwd();
                break;
        }
    }
    private void setNewPwd() {
        if(!validate1())
            return;
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.resetPayPwdPos(baseApplication.mUser.token,baseApplication.mUser.loginId,restToken,pwd2);//"18813904075:123456789"
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
//                        Intent intent = new Intent();
//                        intent.setClass(SetPayPwdActivity.this,BindBankActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    }else{
                        showShortToast("设置密码失败，请稍后重试");
                    }
                }
            }
            @Override
            public void onFailure(Call<BaseResponse<String>> arg0,
                                  Throwable arg1) {
            }
        });
    }
}