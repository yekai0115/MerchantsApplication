package com.ddz.mearchant.activity;

import android.content.Context;
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
import com.ddz.mearchant.view.HandyTextView;

import retrofit2.Call;
import retrofit2.Response;


public class GetPayinfoCodeActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext;
    private HandyTextView getCodeTextView;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private LinearLayout linearLayout1,linearLayout2;
    private EditText editPhoneText,editVercodeText,editPwdText1,editPwdText2;
    private Button button1,button2;
    private String phone, code;
    private String reasetToken ;
    private int curStatus = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        mContext=this;
        initViews();
        initDialog();
    }

    @Override
    protected void initViews() {
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        linearLayout1 = (LinearLayout) findViewById(R.id.arp_linear1);
        linearLayout2 = (LinearLayout) findViewById(R.id.arp_linear2);
        editPhoneText = (EditText) findViewById(R.id.arp_phone);
        editVercodeText = (EditText) findViewById(R.id.arp_vercode);
        editPwdText1 = (EditText) findViewById(R.id.arp_pwd1);
        editPwdText2 = (EditText) findViewById(R.id.arp_pwd2);
        button1 = (Button) findViewById(R.id.confim_button);
        button2 = (Button) findViewById(R.id.confim_button2);
        getCodeTextView = (HandyTextView) findViewById(R.id.arp_getcode);
        htvCenter.setText("设置支付密码");
        editPhoneText.setText(baseApplication.mUser.loginId);
        editPhoneText.setEnabled(false);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        getCodeTextView.setOnClickListener(this);
        htvLeft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                defaultFinish();
                break;
            case R.id.confim_button:
                validateCode();
                break;
            case R.id.confim_button2:
                setNewPwd();
                break;
            case R.id.arp_getcode:
                getCode();
                break;
        }
    }
    private void setNewPwd() {
        String pwd1 = editPwdText1.getText().toString();
        String pwd2 = editPwdText2.getText().toString();
        if(pwd1 == null || pwd1 == null || !pwd1.equals(pwd2) || pwd1.length() < 6 || pwd1.length() > 30) {
            Toast.makeText(this, getResources().getString(R.string.arp_set_pwd_err), Toast.LENGTH_SHORT).show();
            return;
        }
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.resetPayPwdPos(baseApplication.mUser.token,editPhoneText.getText().toString(),reasetToken,pwd2);//"18813904075:123456789"
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
                        showShortToast("设置支付密码成功");
                        defaultFinish();
                        startActivity(BindBankActivity.class);
                    }else{
                        showShortToast("设置密码失败，请稍后重试");
                        showLinear1();
                    }
                }
            }
            @Override
            public void onFailure(Call<BaseResponse<String>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    private void validateCode() {
        dialog.show();
        if(!validateInput1()||!validateInput2())
            return;
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.getResetPayPwdToken(baseApplication.mUser.token,editPhoneText.getText().toString(),editVercodeText.getText().toString());//"18813904075:123456789"
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
                        reasetToken = data;
                        showLinear2();
                    }else if(status.equals(Constants.ERR_VERIFYCODE)){
                        showShortToast("验证码错误");
                    }else{
                        showShortToast("未知错误,请稍后重试");
                    }
                }
            }
            @Override
            public void onFailure(Call<BaseResponse<String>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    @Override
    protected void initEvents() {

    }

    /**
     * 获取验证码
     */
    private void getCode() {
        if(!validateInput1())
            return;
        triggerTimer();
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.getCode(baseApplication.mUser.token,editPhoneText.getText().toString());//"18813904075:123456789"
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

    private boolean validateInput1() {
        phone = editPhoneText.getText().toString();
        if(phone == null || phone.length() != 11) {
            Toast.makeText(this, getResources().getString(R.string.al_hint_inputphone), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateInput2() {
        if(!validateInput1())
            return false;
        code = editVercodeText.getText().toString();
        if(code == null || code.length() < 6) {
            Toast.makeText(this, getResources().getString(R.string.afl_code_len_err), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showLinear1() {
        curStatus = 0;
        editVercodeText.setText("");
        linearLayout1.setVisibility(View.VISIBLE);
        linearLayout2.setVisibility(View.GONE);
    }

    /**
     * 验证码正确，显示输入密码UI
     */
    private void showLinear2() {
        curStatus = 1;
        linearLayout1.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.VISIBLE);
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
                        getCodeTextView.setClickable(false);
                    }
                });

                do {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(getCodeTextView == null) return;
                            getCodeTextView.setText(value[0] + "秒");
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
                        if(getCodeTextView == null) return;
                        getCodeTextView.setText(getResources().getString(R.string.al_getcode));
                        getCodeTextView.setClickable(true);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if(curStatus == 1) {
            showLinear1();
            return;
        }
        super.onBackPressed();
    }

}
