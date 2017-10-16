package com.ddz.mearchant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.ddz.mearchant.view.HandyTextView;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/6/24 0024.
 */

public class ForgetPayinfoPwdActivity extends BaseActivity implements View.OnClickListener{
    private Context mContext;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private HandyTextView arpPhone,arpGetcode;
    private Custom mCustom;
    private EditText arpVercode;
    private Button confirmButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgey_pay_pwd);
        mContext=this;
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
        arpPhone = (HandyTextView)findViewById(R.id.arp_phone);
        arpVercode = (EditText)findViewById(R.id.arp_vercode);
        arpGetcode = (HandyTextView)findViewById(R.id.arp_getcode);
        arpGetcode.setOnClickListener(this);
        htvCenter.setText("银行卡验证码");
        htvLeft.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        arpPhone.setText(baseApplication.mUser.loginId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                defaultFinish();
                break;
            case R.id.confirm_button:
                VerifyValidation();
                break;
            case R.id.arp_getcode:
                getCode();
                break;
        }
    }
    private boolean validateInput1() {
        String code = arpVercode.getText().toString();
        if(code == null || code.length() < 6) {
            Toast.makeText(this, getResources().getString(R.string.afl_code_len_err), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void VerifyValidation(){
            if(!validateInput1())
                return;
            dialog.show();
            APIService userBiz = RetrofitWrapper.getInstance().create(
                    APIService.class);
            Call<BaseResponse<String>> call = userBiz.getResetPayPwdToken(baseApplication.mUser.token,arpPhone.getText().toString(),arpVercode.getText().toString());//"18813904075:123456789"
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
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("tokem",data);
                            intent.putExtra("tokem",bundle);
                            intent.setClass(ForgetPayinfoPwdActivity.this,SetPayPwdActivity.class);
                            startActivity(intent);
                        }else if(status.equals(Constants.ERR_VERIFYCODE)){
                            showShortToast("验证码错误");
                        }else{
                            showShortToast("未知错误,请稍后重试");
                        }
                    }else{
                        showShortToast("验证码错误");
                    }
                }
                @Override
                public void onFailure(Call<BaseResponse<String>> arg0,
                                      Throwable arg1) {
                }
            });
        }
    /**
     * 获取验证码
     */
    private void getCode() {
        triggerTimer();
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.getCode(baseApplication.mUser.token,arpPhone.getText().toString());//"18813904075:123456789"
        call.enqueue(new HttpCallBack<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> arg0,
                                   Response<BaseResponse<String>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<String> baseResponse = response.body();
                if (null != baseResponse ) {
                    String status = baseResponse.getStatus();
                    String data = baseResponse.getData();
                    if (status.equals(Constants.T_OK)){
                        showShortToast("发送验证码成功");
                    }else if(status.equals(Constants.ERR_NOREGISTER)){
                        showShortToast("手机号未注册");
                    }
                }
            }
            @Override
            public void onFailure(Call<BaseResponse<String>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    private void triggerTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int value[] = new int[1];
                value[0] = 60;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        arpGetcode.setClickable(false);
                    }
                });

                do {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(arpGetcode == null) return;
                            arpGetcode.setText(value[0] + "秒");
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    value[0]--;

                } while (value[0] > 0);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(arpGetcode == null) return;
                        arpGetcode.setText(getResources().getString(R.string.al_getcode));
                        arpGetcode.setClickable(true);
                    }
                });
            }
        }).start();
    }
}