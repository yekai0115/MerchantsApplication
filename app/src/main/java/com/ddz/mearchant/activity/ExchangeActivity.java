package com.ddz.mearchant.activity;

import android.content.Context;
import android.content.Intent;
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
import com.ddz.mearchant.dialog.DialogWidget;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.Convertibility;
import com.ddz.mearchant.models.ShopBase;
import com.ddz.mearchant.utils.CommonUtils;
import com.ddz.mearchant.view.HandyTextView;
import com.ddz.mearchant.view.PayPasswordView;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Response;


public class ExchangeActivity extends BaseActivity implements View.OnClickListener{
    private Context mContext;
    private HandyTextView htvCenter,htvRight,canCash;
    private ShopBase shopBase;
    private LinearLayout htvLeft;
    private HandyTextView aeBankName;
    private boolean isSecPwdSetted=false;
    private int type;
    private Button confirmButton;
    private EditText exchangeEdit;
    private double maxExchangeAmount = 10000;
    private long mAmount;
    private DialogConfirm dialogConfirm;
    private DialogWidget mDialogWidget;
    private Convertibility convertibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        mContext=this;
        dialogConfirm = new DialogConfirm();
        initViews();
        initDialog();
        initEvents();

    }

    @Override
    protected void initViews() {
        htvRight = (HandyTextView)findViewById(R.id.title_htv_rigit);
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout) findViewById(R.id.title_htv_left);
        aeBankName = (HandyTextView)findViewById(R.id.ae_bank_name);
        confirmButton = (Button)findViewById(R.id.confirm_button);
        exchangeEdit = (EditText)findViewById(R.id.act_exchange_edit);
        canCash = (HandyTextView)findViewById(R.id.ae_can_cash);



        htvRight.setVisibility(View.VISIBLE);
        htvCenter.setText("兑换");
        htvRight.setOnClickListener(this);
        htvLeft.setOnClickListener(this);
        aeBankName.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                defaultFinish();
                break;
            case R.id.ae_bank_name:
                startPayForActivity();
                break;
            case R.id.confirm_button:
                confirmExchange();
                break;
            case R.id.title_htv_rigit:
                startActivity(ForRecordActivity.class);
                break;
        }
    }
    private void  confirmExchange(){
        if (convertibility == null || convertibility.getBank_id() == null){
            showShortToast("您还没有绑定银行卡");
            return;
        }
        String value = exchangeEdit.getText().toString();
        if(value!=null && !value.equals("")) {
            long amount = Long.parseLong(value);
            BigDecimal amountMoney = new BigDecimal(value);
            BigDecimal money = new BigDecimal(canCash.getText().toString());
//            Long monet = Long.valueOf(canCash.getText().toString());
            if(amountMoney.compareTo(money) == 1 ) {
                showShortToast("超过最大可用提现金额");
                return;
            }
            if(amount<=0 ) {
                showShortToast("提现金额不合法，请重新输入");
                return;
            }
            if(amount%100 != 0) {
                showShortToast("提现金额必须是100的整数倍");
                return;
            }
            int compare= CommonUtils.compareTo(value,"10000");
            if(compare==1){
                showShortToast("一次最多只能兑换10000");
                return;
            }
            mAmount = amount;
            mDialogWidget = new DialogWidget(ExchangeActivity.this, getDecorViewDialog(amount));
            mDialogWidget.show();
        } else {
            showShortToast("请输入提现金额");
            return;
        }
    }
    private void  startPayForActivity(){
        if(aeBankName.getText().equals(getResources().getString(R.string.ae_add_bank))) {
            //未设置支付密码
            startOtherActivity();
        }else{
            bindBankActivity();
        }
    }
    private void bindBankActivity() {
        dialogConfirm.showDialog(ExchangeActivity.this, getResources().getString(R.string.ae_alter_bank),"确定","取消");
        dialogConfirm.setListener(new DialogConfirm.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isOkClicked) {
                if (isOkClicked) {
                    startOtherActivity();
                }
            }
        });
    }
    private void startOtherActivity() {
        if (convertibility.getUp_password().equals("0")) {
            type=1;
            startActivity(new Intent(ExchangeActivity.this, GetPayinfoCodeActivity.class));
        } else {
            startActivity(new Intent(ExchangeActivity.this, ConfirmPayPwdActivity.class));
        }
    }

    protected View getDecorViewDialog(final long amount) {
        return PayPasswordView.getInstance("" + amount,"兑换",this,new PayPasswordView.OnPayListener() {

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
        @Override
    protected void initEvents() {
            getCanCash();
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
                        alert.showDialog(ExchangeActivity.this,"支付密码错误,请重试","确定","忘记密码");
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
        Call<BaseResponse<String>> call = userBiz.canCashExchange(baseApplication.mUser.token,convertibility.getBank_id(),
                String.valueOf(mAmount),password);
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
                        exchangeEdit.setText("");
                        canCash.setText(data);
                        showShortToast("提现成功");
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
    /**
     * 获取账户余额
     */
    private void getCanCash() {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<Convertibility>> call = userBiz.getCanCash(baseApplication.mUser.token);
        call.enqueue(new HttpCallBack<BaseResponse<Convertibility>>() {

            @Override
            public void onResponse(Call<BaseResponse<Convertibility>> arg0,
                                   Response<BaseResponse<Convertibility>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<Convertibility> baseResponse = response.body();
                if (null != baseResponse) {
                    String status = baseResponse.getStatus();
                    if (status.equals(Constants.T_OK)){
                        if(baseResponse.getData()!=null){
                            convertibility = baseResponse.getData();
                            if (convertibility.getBank_id()!=null && convertibility.getBank_id().toString().length()>0d){
                                aeBankName.setText(convertibility.getBankname());
                            }
                            canCash.setText(convertibility.getCan_money());
                        }
                    }else{
                    }


                }

            }

            @Override
            public void onFailure(Call<BaseResponse<Convertibility>> arg0,
                                  Throwable arg1) {
            }
        });

    }

}
