package com.ddz.mearchant.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.dialog.DialogConfirm;
import com.ddz.mearchant.dialog.DialogSelectType;
import com.ddz.mearchant.dialog.DialogWidget;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.Balance;
import com.ddz.mearchant.models.BalanceBase;
import com.ddz.mearchant.view.HandyTextView;
import com.ddz.mearchant.view.PayPasswordView;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Response;

import static com.ddz.mearchant.R.id.giving_button;
import static com.ddz.mearchant.R.id.title_htv_left;
import static com.ddz.mearchant.R.id.title_htv_rigit;

/**
 * Created by Administrator on 2017/7/6 0006.
 */

public class GivingFriendActivity extends BaseActivity implements View.OnClickListener{
    private LinearLayout givingTypeLinear;
    private HandyTextView givingTypeName,givingTypeSelect,givingIntegralNum;
    private EditText givingAmount,givingPhone;
    private Button givingButton;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private Balance balance;
    private double mAmount;
    private DialogSelectType dialogSelectType;
    private DialogWidget mDialogWidget;
    private String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giving_friend);
        initViews();
        initDialog();
        initEvents();
    }

    @Override
    protected void initEvents() {
    }

    @Override
    protected void initViews() {
        givingTypeLinear = (LinearLayout)findViewById(R.id.giving_type_linear);
        givingTypeName = (HandyTextView)findViewById(R.id.giving_type_name);
        givingTypeSelect = (HandyTextView)findViewById(R.id.giving_type_select);
        givingIntegralNum = (HandyTextView)findViewById(R.id.giving_integral_num);
        givingAmount = (EditText)findViewById(R.id.giving_amount);
        givingPhone = (EditText)findViewById(R.id.giving_phone);
        givingButton = (Button)findViewById(giving_button);
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(title_htv_left);
        htvRight = (HandyTextView)findViewById(title_htv_rigit);
        htvCenter.setText("赠送给好友");
        dialogSelectType = new DialogSelectType();
        htvLeft.setOnClickListener(this);
        givingTypeLinear.setOnClickListener(this);
        givingButton.setOnClickListener(this);
        htvRight.setVisibility(View.VISIBLE);
        htvRight.setText("记录");
        htvRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.giving_type_linear:
                getTransfer();
                break;
            case R.id.title_htv_left:
                    defaultFinish();
                break;
            case R.id.giving_button:
                    confirmGiving();
                break;
            case R.id.title_htv_rigit:
                    startActivity(GivingRecordsActivity.class);
                break;

        }
    }
    private void  confirmGiving(){
        if (givingIntegralNum.getText().toString().length()==0){
            showShortToast("您还没有选择赠送类型");
            return;
        }
        if(Float.valueOf(givingIntegralNum.getText().toString())<=0.0){
            showShortToast("积分不足");
            return;
        }
        if (givingAmount.getText().toString().length()==0){
            showShortToast("你还没有输入赠送积分数量");
            return;
        }
        if (Float.valueOf(givingAmount.getText().toString())>Float.valueOf(givingIntegralNum.getText().toString())){
            showShortToast("赠送积分超过最大可用积分");
            return;
        }
        if(givingPhone.getText().toString()==null || givingPhone.getText().toString().length() == 0){
            showShortToast("您还没有输入赠送人手机号码");
            return;
        }
        if(givingPhone.getText().toString()==null || givingPhone.getText().toString().length() != 11){
            showShortToast("请输入正确的手机号码");
            return;
        }
        double amount = Double.parseDouble(givingAmount.getText().toString());
        mAmount = amount;
        mDialogWidget = new DialogWidget(GivingFriendActivity.this, getDecorViewDialog(amount));
        mDialogWidget.show();
    }
    protected View getDecorViewDialog(final double amount) {
        return PayPasswordView.getInstance("" + amount,"赠送",this,new PayPasswordView.OnPayListener() {

            @Override
            public void onSurePay(String password) {
                mDialogWidget.dismiss();
                mDialogWidget=null;
                confirmPwd(password);
            }

            @Override
            public void onCancelPay() {
                mDialogWidget.dismiss();
                mDialogWidget=null;
            }
        }).getView();
    }
    private  void confirmPwd(final String password){
        //验证支付密码
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.verifyPayPassword(baseApplication.mUser.token,password);
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
                        //验证成功 提现
                        withdrawalAmount(password);
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
                        alert.showDialog(GivingFriendActivity.this,"支付密码错误,请重试","确定","忘记密码");
                    }
                }
            }
            @Override
            public void onFailure(Call<BaseResponse<String>> arg0,
                                  Throwable arg1) {
            }
        });

    }
    private void withdrawalAmount(String password){
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.transferSubmit(baseApplication.mUser.token,type,givingAmount.getText().toString(),givingPhone.getText().toString(),password);
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
                        //提现成功
                        float total = Float.valueOf(givingIntegralNum.getText().toString());
                        float amount = Float.valueOf(givingAmount.getText().toString());
                        BigDecimal b1 = new BigDecimal(givingIntegralNum.getText().toString());
                        BigDecimal b2 = new BigDecimal(givingAmount.getText().toString());
                        float remainAmount = b1.subtract(b2).floatValue();
                        givingAmount.setText("");
                        givingPhone.setText("");
                        givingIntegralNum.setText(String.valueOf(remainAmount));
                        showShortToast("赠送积分成功");
                    }else{
                        showShortToast(data);
                    }
                }
            }
            @Override
            public void onFailure(Call<BaseResponse<String>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    private void getTransfer() {
        dialog.show();
        dialog.setCancelable(false);
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<BalanceBase>> call = userBiz.getTransfer(baseApplication.mUser.token);
        call.enqueue(new HttpCallBack<BaseResponse<BalanceBase>>() {

            @Override
            public void onResponse(Call<BaseResponse<BalanceBase>> arg0,
                                   Response<BaseResponse<BalanceBase>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<BalanceBase> baseResponse = response.body();
                if (null != baseResponse) {
                    String status = baseResponse.getStatus();
                    if (status.equals(Constants.T_OK)){
                        if (baseResponse.getData()!=null){
                            balance = new Balance();
                            BalanceBase balanceBase = baseResponse.getData();
                            if (balanceBase!=null){
                                balance.setPoints(balanceBase.getBalance().getPoints());
                            }else{
                                balance.setPoints("0");
                            }
                            if (baseResponse.getData().getBalance().getMoney()!=null){
                                balance.setMoney(baseResponse.getData().getBalance().getMoney());
                            }else{
                                balance.setMoney("0");
                            }
                            showSelectDialog();
                        }
                    }else{
                        showShortToast(baseResponse.getData().toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<BalanceBase>> arg0,
                                  Throwable arg1) {
            }
        });

    }
    private void showSelectDialog(){
        dialogSelectType.showDialog(GivingFriendActivity.this,balance,new DialogSelectType.onConfirmClickedListener(){
            @Override
            public void onClick(int positoin) {
                switch (positoin){
                    case 0:
                        givingTypeSelect.setVisibility(View.GONE);
                        givingTypeName.setText("激励分");
                        givingIntegralNum.setText(balance.getPoints());
                        type = "2";
                        break;
                    case 1:
                        givingTypeSelect.setVisibility(View.GONE);
                        givingTypeName.setText("赞分");
                        givingIntegralNum.setText(balance.getMoney());
                        type = "1";
                        break;
                }
            }
        });
    }
}
