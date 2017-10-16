package com.ddz.mearchant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BannerBean;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.User;
import com.ddz.mearchant.view.ClearEditText;
import com.ddz.mearchant.view.HandyTextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_shouye,tv_fujin,tv_role,tv_geren;

    private Context mContext;
    private ClearEditText inputLoginId,inputPwd;
    private HandyTextView forgetPwd;
    private Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext=this;
        initViews();
        initDialog();
        //login();
        //getbanner();
    }

    @Override
    protected void initViews() {
        inputLoginId = (ClearEditText)findViewById(R.id.input_loginId);
        inputPwd = (ClearEditText)findViewById(R.id.input_pwd);
        loginButton = (Button)findViewById(R.id.login_button);
        forgetPwd = (HandyTextView)findViewById(R.id.forget_pwd);
        loginButton.setOnClickListener(this);
        forgetPwd.setOnClickListener(this);
        setEditTextInhibitInputSpeChat(inputLoginId);
        setEditTextInhibitInputSpeChat(inputPwd);
        if (baseApplication.mUser!=null&&baseApplication.mUser.token != null) {
            inputLoginId.setText(baseApplication.mUser.getLoginId().toString());
            inputPwd.setText(baseApplication.mUser.getPassword().toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                    login();
                break;
            case R.id.forget_pwd:
                startActivity(ForgetPassWordActivity.class);
                break;
        }
    }

    @Override
    protected void initEvents() {

    }
    public static void setEditTextInhibitInputSpace(EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(" "))return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    /**
     * 禁止EditText输入特殊字符
     * @param editText
     */
    public static void setEditTextInhibitInputSpeChat(EditText editText){

        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String speChat="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if(matcher.find())return "";
                if(source.equals(" "))return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }
    /**
     * 获取账户余额
     */
    private void login() {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.loginRepo(inputLoginId.getText().toString()+":"+inputPwd.getText().toString());//"18813904075:123456789"
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
                    if (null != mUser && !mUser.getLoginId().equals(inputLoginId.getText().toString())) {

                        baseApplication.mCache.clear();// 说明用户切换帐号登陆，则清空本地缓存

                        baseApplication.mUser = new User();

                    }
                    baseApplication.mUser.setLoginId(inputLoginId.getText().toString());
                    baseApplication.mUser.setPassword(inputPwd.getText().toString());
                    baseApplication.mUser.setToken(data);
                    baseApplication.mCache.put(Constants.AUTH_USER,baseApplication.mUser);
                    defaultFinish();
                    activityManager.closeAllActivity();
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }else{
                    showShortToast("用户名或密码错误");
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }else
        return super.onKeyDown(keyCode, event);
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
            }
        });

    }



}
