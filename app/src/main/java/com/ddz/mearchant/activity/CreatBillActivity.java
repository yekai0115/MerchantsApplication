package com.ddz.mearchant.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.dialog.avloading.DialogPayType;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.BillRecord;
import com.ddz.mearchant.models.PayInfo;
import com.ddz.mearchant.models.PayResult;
import com.ddz.mearchant.models.ShopBase;
import com.ddz.mearchant.utils.CommonUtils;
import com.ddz.mearchant.utils.GsonUtil;
import com.ddz.mearchant.view.ClearEditText;
import com.ddz.mearchant.view.HandyTextView;
import com.unionpay.UPPayAssistEx;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

import static com.ddz.mearchant.R.id.acb_radio1;

/**
 * Created by Administrator on 2017/6/28 0028.
 */

public class CreatBillActivity extends BaseActivity implements View.OnClickListener,TextWatcher,Runnable{

    private Context mContext;
    private ClearEditText shopName;
    private HandyTextView save;
    private ShopBase shopBase;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private HandyTextView tvName,tvAddr,tvAmount2,hide_text;
    private EditText etPhone,etAmount;
    private RadioGroup radioGroup;
    private RadioButton radioButton1,radioButton2,radioButton3,radioButton4;
    private Button fButton;
    private int type; // 1 2 5
    private BillRecord billRecord;
    private DialogPayType dialogPayType;

    public static final String LOG_TAG = "PayDemo";
    private int mGoodsIdx = 0;
    private Handler mYinHandler = null;
    private ProgressDialog mLoadingDialog = null;

    public static final int PLUGIN_VALID = 0;
    public static final int PLUGIN_NOT_INSTALLED = -1;
    public static final int PLUGIN_NEED_UPGRADE = 2;

    /*****************************************************************
     * mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
     *****************************************************************/
    private final String mMode = "00";
    private static final String TN_URL_01 = "http://101.231.204.84:8091/sim/getacptn";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_bill);
        mContext=this;
        initViews();
        initDialog();
        initEvents();
    }

    @Override
    protected void initViews() {
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        tvName = (HandyTextView) findViewById(R.id.acb_name); // 固定
        tvAddr = (HandyTextView) findViewById(R.id.acb_addr); // 固定
        htvCenter.setText("录单");
        etPhone = (EditText) findViewById(R.id.acb_phone);
        etAmount = (EditText) findViewById(R.id.acb_amount);
        tvAmount2 = (HandyTextView) findViewById(R.id.acb_amount2);
        hide_text  = (HandyTextView) findViewById(R.id.hide_text);
        radioGroup = (RadioGroup) findViewById(R.id.acb_radio_group);
        radioButton1 = (RadioButton) findViewById(R.id.acb_radio1);
        radioButton2 = (RadioButton) findViewById(R.id.acb_radio2);
        radioButton3 = (RadioButton) findViewById(R.id.acb_radio3);
        fButton = (Button) findViewById(R.id.acb_confirm);
        htvLeft.setOnClickListener(this);
        etAmount.addTextChangedListener(this);
        fButton.setOnClickListener(this);
        dialogPayType = new DialogPayType();
        type = 1;
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                String str = etAmount.getText().toString();
                if(str!=null && !str.equals("")) {
                    BigDecimal value = new BigDecimal(str);
                    switch (checkedId) {
                        case acb_radio1:
                            double v1 = value.multiply(new BigDecimal(0.2)).doubleValue();
                            String str1 = CommonUtils.formatDouble2(v1);
                            tvAmount2.setText(str1);
                            type = 1;
                            break;
                        case R.id.acb_radio2:
                            double v2 = value.multiply(new BigDecimal(0.1)).doubleValue();
                            String str2 = CommonUtils.formatDouble2(v2);
                            tvAmount2.setText(str2);
                            type = 2;
                            break;
                        case R.id.acb_radio3:
                            double v3 = value.multiply(new BigDecimal(0.05)).doubleValue();
                            String str3 = CommonUtils.formatDouble2(v3);
                            tvAmount2.setText(str3);
                            type = 5;
                            break;
                    }
                }else{
                    switch (checkedId) {
                        case acb_radio1:
                            type = 1;
                            break;
                        case R.id.acb_radio2:
                            type = 2;
                            break;
                        case R.id.acb_radio3:
                            type = 5;
                            break;
                    }
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                    defaultFinish();
                break;
            case R.id.acb_confirm:
                    openPayInfo();
                break;
        }
    }
    private void openPayInfo(){
        String phone = etPhone.getText().toString();
        String amount = etAmount.getText().toString();
        if(phone==null || phone.equals("") || amount==null || amount.equals("")) {
            Toast.makeText(CreatBillActivity.this, "信息填写不完整，无法快捷上报", Toast.LENGTH_SHORT).show();
            return;
        }
        if(phone.length() != 11) {
            Toast.makeText(CreatBillActivity.this, "手机号码长度有误，无法快捷上报", Toast.LENGTH_SHORT).show();
            return;
        }
        sumbitBillImformation(amount);
    }
    @Override
    protected void initEvents() {
        getBillImformation();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String str = s.toString();
        if(str==null || str.equals("")) {
            tvAmount2.setText("0.00");
            return;
        }

        BigDecimal inputAmount = new BigDecimal(s.toString());
        if (inputAmount.compareTo(new BigDecimal(billRecord.getLeave_money()))==1){
            showShortToast("超过最大可兑换金额");
            return;
        }
        int checkId = radioGroup.getCheckedRadioButtonId();
        switch (checkId) {
            case acb_radio1:
                double v1 = inputAmount.multiply(new BigDecimal(0.2)).doubleValue();
                String str1 = CommonUtils.formatDouble2(v1);
                tvAmount2.setText(str1);
                break;
            case R.id.acb_radio2:
                double v2 = inputAmount.multiply(new BigDecimal(0.1)).doubleValue();
                String str2 = CommonUtils.formatDouble2(v2);
                tvAmount2.setText(str2);
                break;
            case R.id.acb_radio3:
                double v3 = inputAmount.multiply(new BigDecimal(0.05)).doubleValue();
                String str3 = CommonUtils.formatDouble2(v3);
                tvAmount2.setText(str3);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
    /**
     * 获取录单信息
     */
    private void getBillImformation() {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<BillRecord>> call = userBiz.getRecordOrderInfo(baseApplication.mUser.token);
        call.enqueue(new HttpCallBack<BaseResponse<BillRecord>>() {

            @Override
            public void onResponse(Call<BaseResponse<BillRecord>> arg0,
                                   Response<BaseResponse<BillRecord>> response) {
                BaseResponse<BillRecord> baseResponse = response.body();
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                if (null != baseResponse) {
                    String status = baseResponse.getStatus();
                    billRecord = baseResponse.getData();
                    if (status.equals(Constants.T_OK)){
                        if(billRecord!=null){
                            tvName.setText(billRecord.getName());
                            tvAddr.setText(billRecord.getAddress());
                            String[] profit = billRecord.getProfit().split(",");
                            for (int i=0;i<profit.length;i++) {
                                if (profit[i].equals("2")) {
                                    radioButton1.setVisibility(View.VISIBLE);
                                }else if(profit[i].equals("1")){
                                    radioButton2.setVisibility(View.VISIBLE);
                                }else if(profit[i].equals("5")){
                                    radioButton3.setVisibility(View.VISIBLE);
                                }
                            }
                            if (radioButton1.getVisibility() == View.VISIBLE){
                                radioButton1.setChecked(true);
                                type = 1;
                            }else if (radioButton2.getVisibility() == View.VISIBLE){
                                radioButton2.setChecked(true);
                                type = 2;
                            }else if(radioButton2.getVisibility() == View.VISIBLE){
                                radioButton3.setChecked(true);
                                type = 5;
                            }
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<BillRecord>> arg0,
                                  Throwable arg1) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onFailure(arg0,arg1);
            }
        });

    }

    /**
     * 提交录单信息
     */
    private void sumbitBillImformation(String amount) {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<Object>> call = userBiz.recordOrderSubmit(baseApplication.mUser.token,
                etPhone.getText().toString(),etAmount.getText().toString(),String.valueOf(type));
        call.enqueue(new HttpCallBack<BaseResponse<Object>>() {

            @Override
            public void onResponse(Call<BaseResponse<Object>> arg0,
                                   Response<BaseResponse<Object>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<Object> baseResponse = response.body();
                if (null != baseResponse) {
                    String status = baseResponse.getStatus();
                    Object payInfo = baseResponse.getData();
                    if (status.equals(Constants.T_OK)){
                        try{
                            JSONObject jsonObject = new JSONObject(GsonUtil.toJsonString(payInfo));
                            PayInfo payInfoBean = new PayInfo();
                            payInfoBean.setPay_id(jsonObject.getString("pay_id"));
                            payInfoBean.setMoney(jsonObject.getString("money"));
                            payInfoBean.setPay_type(jsonObject.getString("pay_type"));
                            showPayInfoType(payInfoBean);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }else{
                        showShortToast(baseResponse.getData().toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0,
                                  Throwable arg1) {
            }
        });

    }
    private void showPayInfoType(final PayInfo payInfoBean){
        dialogPayType.showDialog(CreatBillActivity.this, new DialogPayType.onConfirmClickedListener() {
            @Override
            public void onClick(String type) {
                if (type.equals("1")){
                    showShortToast("微信支付待开发");
                    return;
                }
                getPayinfoSign(payInfoBean.getPay_id(),type);
            }
        },payInfoBean.getMoney(),payInfoBean.getPay_type());

    }
    private void getPayinfoSign(String payId,final String type) {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.getPayParam(baseApplication.mUser.token,payId,type);
        call.enqueue(new HttpCallBack<BaseResponse<String>>() {

            @Override
            public void onResponse(Call<BaseResponse<String>> arg0,
                                   Response<BaseResponse<String>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<String> baseResponse = response.body();
                if (null != baseResponse) {
                    String status = baseResponse.getStatus();
                    String payInfo = baseResponse.getData();
                    if (status.equals(Constants.T_OK)){
                        if (type.equals("6")){
                            payforAli(payInfo);//支付宝支付
                        }else if(type.equals("9")){
                            payYinLian(payInfo);//银联支付
                        }
                    }else{
                        showShortToast(baseResponse.getData().toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<String>> arg0,
                                  Throwable arg1) {
            }
        });

    }
    private void payYinLian(String payInfo){
        dialog.show();
        Message msg = mHandler.obtainMessage();
        msg.obj = payInfo;
        msg.what = SDK_PAY_YINLIAN;
        mHandler.sendMessage(msg);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {

            // 如果想对结果数据验签，可使用下面这段代码，但建议不验签，直接去商户后台查询交易结果
            // result_data结构见c）result_data参数说明
            /*if (data.hasExtra("result_data")) {
                String result = data.getExtras().getString("result_data");
                try {
                    JSONObject resultJson = new JSONObject(result);
                    String sign = resultJson.getString("sign");
                    String dataOrg = resultJson.getString("data");
                    // 此处的verify建议送去商户后台做验签
                    // 如要放在手机端验，则代码必须支持更新证书
                    boolean ret = verify(dataOrg, sign, "00");
                    if (ret) {
                        // 验签成功，显示支付结果
                        msg = "支付成功！";
                    } else {
                        // 验签失败
                        msg = "支付失败！";
                    }
                } catch (JSONException e) {
                }
            }*/
            // 结果result_data为成功时，去商户后台查询一下再展示成功
            etPhone.setText("");
            etAmount.setText("");
            tvAmount2.setText("");
            type = 1;
            radioButton1.setChecked(true);
            msg = "支付成功！";

        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
        }
        showShortToast(msg);
    }

    int startpay(Activity act, String tn, int serverIdentifier) {
        return 0;
    }

    private boolean verify(String msg, String sign64, String mode) {
        // 此处的verify，商户需送去商户后台做验签
        return true;

    }
    private static final int SDK_PAY_FLAG = 1;//支付宝
    private static final int SDK_PAY_YINLIAN = 2;//支付宝
    private static final int SDK_PAY_WEIXIN = 3;//微信
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        etPhone.setText("");
                        etAmount.setText("");
                        tvAmount2.setText("");
                        type = 1;
                        radioButton1.setChecked(true);
                        Toast.makeText(CreatBillActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(CreatBillActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_PAY_YINLIAN:
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    String tn = "";
                    if (msg.obj == null || ((String) msg.obj).length() == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreatBillActivity.this);
                        builder.setTitle("错误提示");
                        builder.setMessage("网络连接失败,请重试!");
                        builder.setNegativeButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create().show();
                    } else {
                        tn = (String) msg.obj;
                        /*************************************************
                         * 步骤2：通过银联工具类启动支付插件
                         ************************************************/
                        // “00” – 银联正式环境
                        // “01” – 银联测试环境，该环境中不发生真实交易
                        UPPayAssistEx.startPay(CreatBillActivity.this, null, null, tn, mMode);
                    }
                    break;
            }
        }
    };
    //调用支付宝支付
    private  void payforAli(final String payInfo){

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(CreatBillActivity.this);
                Map<String, String> result = alipay.payV2(payInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }










    @Override
    public void run() {
        String tn = null;
        InputStream is;
        try {

            String url = TN_URL_01;

            URL myURL = new URL(url);
            URLConnection ucon = myURL.openConnection();
            ucon.setConnectTimeout(120000);
            is = ucon.getInputStream();
            int i = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((i = is.read()) != -1) {
                baos.write(i);
            }

            tn = baos.toString();
            is.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Message msg = mHandler.obtainMessage();
        msg.obj = tn;
        msg.what = SDK_PAY_YINLIAN;
        mHandler.sendMessage(msg);
    }





}