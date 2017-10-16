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
import com.ddz.mearchant.dialog.DialogConfirm;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.Custom;
import com.ddz.mearchant.view.HandyTextView;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/6/24 0024.
 */

public class ConfirmPayPwdActivity extends BaseActivity implements View.OnClickListener{
    private Context mContext;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private Custom mCustom;
    private Button confirmPwdButton;
    private EditText acpPwdEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pwd);
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
        confirmPwdButton = (Button)findViewById(R.id.confirm_pwd_button);
        acpPwdEdit = (EditText) findViewById(R.id.acp_pwd);
        htvCenter.setText("支付密码");
        htvLeft.setOnClickListener(this);
        confirmPwdButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                defaultFinish();
                break;
            case R.id.confirm_pwd_button:
                confirmPwd();
                break;

        }
    }
    private boolean validate1() {
        String pwd = acpPwdEdit.getText().toString();
        if(pwd == null || pwd.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.asp_input_incomplete), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private  void confirmPwd(){
        if(!validate1())
            return;
        //验证支付密码
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.verifyPayPassword(baseApplication.mUser.token,acpPwdEdit.getText().toString());
        call.enqueue(new HttpCallBack<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> arg0,
                                   Response<BaseResponse<String>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<String> baseResponse = response.body();
                if (null != baseResponse) {
                    String status = baseResponse.getStatus();
                    String data = baseResponse.getData();
                    if (status.equals(Constants.T_OK)){
                        defaultFinish();
                        startActivity(GetBindCodeActivity.class);
                    }else{
                        DialogConfirm alert = new DialogConfirm();
                        alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
                            @Override
                            public void onClick(boolean isOkClicked) {
                                if (isOkClicked){
                                }else{
                                    startActivity(ForgetPayinfoPwdActivity.class);
                                }
                            }
                        });
                        alert.showDialog(ConfirmPayPwdActivity.this,"支付密码错误,请重试","确定","忘记密码");
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
