package com.ddz.mearchant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.config.MyOSSConfig;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.AddProductBase;
import com.ddz.mearchant.models.Custom;
import com.ddz.mearchant.models.Postage;
import com.ddz.mearchant.utils.GsonUtil;
import com.ddz.mearchant.utils.StringUtils;
import com.ddz.mearchant.view.HandyTextView;
import com.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;


public class EditProductNextActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext;
    private HandyTextView htvCenter,htvRight,sCustomName,sPostageName,handyTextView4;
    private LinearLayout htvLeft;
    private ToggleButton toggleBtn;
    private RadioGroup radioGroup;
    private RadioButton radioOne,radioTwo,radioThree,radioFour;
    private LinearLayout sCustomLinear,sPostageLinear;
    private Postage mSelectPostage;
    private AddProductBase addProductBase;
    private Button productSumbitButton;
    private ArrayList<String> imageDetail = new ArrayList<>();
    private ArrayList<String> shufflingDetail = new ArrayList<>();
    private View radio_one_view,radio_two_view,radio_three_view;
    private OSS oss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_next_product);
        mContext=this;
        addProductBase = (AddProductBase)getIntent().getBundleExtra("addProductBase").getSerializable("addProductBase");
        if (getIntent().getStringArrayListExtra("introduceImages")!=null){
            imageDetail = getIntent().getStringArrayListExtra("introduceImages");
        }
        if (getIntent().getStringArrayListExtra("shufflingImages")!=null){
            shufflingDetail = getIntent().getStringArrayListExtra("shufflingImages");
        }

        initViews();
        initEvents();
        initDialog();
        oss = new OSSClient(getApplicationContext(), Constants.ALI_ENDPOINT, MyOSSConfig.getProvider(), MyOSSConfig.getOSSConfig());
    }

    @Override
    protected void initViews() {
        htvRight = (HandyTextView)findViewById(R.id.title_htv_rigit);
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        toggleBtn = (ToggleButton)findViewById(R.id.togglebutton);
        radioGroup = (RadioGroup)findViewById(R.id.radio_product_proift);
        radioOne = (RadioButton) findViewById(R.id.radio_one);
        radioTwo = (RadioButton) findViewById(R.id.radio_two);
        radioThree = (RadioButton) findViewById(R.id.radio_three);
        radioFour = (RadioButton) findViewById(R.id.radio_four);
        handyTextView4 = (HandyTextView) findViewById(R.id.handyTextView4);
        radio_one_view = (View) findViewById(R.id.radio_one_view);
        radio_two_view = (View) findViewById(R.id.radio_two_view);
        radio_three_view = (View) findViewById(R.id.radio_three_view);
        sCustomLinear = (LinearLayout) findViewById(R.id.select_custom_product_linear);
        sCustomName = (HandyTextView) findViewById(R.id.select_custom_name);
        sPostageName = (HandyTextView) findViewById(R.id.select_postage_name);
        sPostageLinear = (LinearLayout) findViewById(R.id.select_postage_product_linear);
        productSumbitButton = (Button) findViewById(R.id.product_sumbit_button);
        if (addProductBase.getProfit() == 0){
            addProductBase.setProfit(1);
        }
        if (addProductBase.getIs_on_sale() == 0){
            addProductBase.setIs_on_sale(0);
            toggleBtn.setToggleOn();
        }else{
            addProductBase.setIs_on_sale(1);
            toggleBtn.setToggleOff();
        }

        if (addProductBase.getShipping_tpl_name()!=null){
            sPostageName.setText(addProductBase.getShipping_tpl_name());
            mSelectPostage = new Postage();
            mSelectPostage.setShipping_tpl_id(String.valueOf(addProductBase.getShipping_tpl_id()));
            mSelectPostage.setShipping_name(addProductBase.getShipping_tpl_name());
        }
        if (addProductBase.getGoods_user_cat_name()!=null){
            sCustomName.setText(addProductBase.getGoods_user_cat_name());
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.radio_one:
                            addProductBase.setProfit(1);
                        break;
                    case R.id.radio_two:
                            addProductBase.setProfit(2);
                        break;
                    case R.id.radio_three:
                            addProductBase.setProfit(5);
                        break;
                    case R.id.radio_four:
                            addProductBase.setProfit(10);
                        break;
                }
            }
        });
        sCustomLinear.setOnClickListener(this);
        sPostageLinear.setOnClickListener(this);
        productSumbitButton.setOnClickListener(this);
        htvCenter.setText("编辑商品");
        htvLeft.setOnClickListener(this);
        toggleBtn.setOnToggleChanged(new ToggleButton.OnToggleChanged(){
            @Override
            public void onToggle(boolean on) {
                if (on){
                    addProductBase.setIs_on_sale(0);
                }else{
                    addProductBase.setIs_on_sale(1);
                }
                //这个boolean参数代表的是改变之后的状态

            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                    Intent intent3 = new Intent();
                    Bundle bundle2 = new Bundle();
                    bundle2.putSerializable("addProductBase",addProductBase);
                    intent3.putExtra("addProductBase",bundle2);
                    setResult(RESULT_OK,intent3);
                    defaultFinish();
                break;
            case R.id.select_custom_product_linear:
                    Intent intent = new Intent();
                    intent.setClass(this,SelectCustomTypeActivity.class);
                    startActivityForResult(intent,2);
                break;
            case R.id.select_postage_product_linear:
                Intent intent1 = new Intent();
                if (mSelectPostage!=null&&mSelectPostage.getShipping_tpl_id()!=null){
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("mSelectPostage",mSelectPostage);
                    intent1.putExtra("mSelectPostage",bundle);
                }
                intent1.setClass(this,SelectPostageActivity.class);
                startActivityForResult(intent1,1);
                break;
            case R.id.product_sumbit_button:
                    sumbitProduct();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent3 = new Intent();
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("addProductBase",addProductBase);
            intent3.putExtra("addProductBase",bundle2);
            setResult(RESULT_OK,intent3);
            defaultFinish();
            return false;
        }else
            return super.onKeyDown(keyCode, event);
    }
    private ArrayList<Boolean> flagList = new ArrayList<>();
    private void sumbitProduct(){
        if (StringUtils.isBlank(sPostageName.getText().toString())){
            showShortToast("请选择邮费方式");
            return;
        }
        if (StringUtils.isBlank(sCustomName.getText().toString())){
            showShortToast("请选择自定义分类");
            return;
        }
        dialog.show();
        sumbitProductInfo();
    }
    private void sumbitProductInfo(){
        dialog.show();
        String gson = GsonUtil.toJsonString(addProductBase);
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.editGoodsPost(baseApplication.mUser.token,
                addProductBase.getGoods_id(),
                addProductBase.getCat_id(),
                addProductBase.getGoods_name(),
                addProductBase.getProfit(),
                addProductBase.getDetail(),
                addProductBase.getDetail_type(),
                addProductBase.getGoods_img(),
                addProductBase.getGoods_attr(),
                addProductBase.getShipping_tpl_id(),
                addProductBase.getIs_on_sale(),
                addProductBase.getGoods_user_cat_id());
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
                        dialog.dismiss();
                        showShortToast("编辑商品成功");
                        defaultFinish();
                        activityManager.closeAllActivity();
                        Intent intent = new Intent();
                        intent.setClass(EditProductNextActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }else{
                    dialog.dismiss();
                    showShortToast("服务器连接失败");
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<String>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 1: {
                    mSelectPostage = (Postage)data.getBundleExtra("postage").getSerializable("postage");
                    sPostageName.setText(mSelectPostage.getShipping_name());
                    addProductBase.setShipping_tpl_id(Integer.valueOf(mSelectPostage.getShipping_tpl_id()));
                    addProductBase.setShipping_tpl_name(mSelectPostage.getShipping_name());
                    break;
                }
                case 2: {Custom custom = (Custom)data.getBundleExtra("custom").getSerializable("custom");
                    sCustomName.setText(custom.getUser_cate_name());
                    addProductBase.setGoods_user_cat_id(custom.getGoods_user_cat_id());
                    addProductBase.setGoods_user_cat_name(custom.getUser_cate_name());
                    break;
                }
                default:
                    break;
            }
        }
    }
    @Override
    protected void initEvents() {
        queryPrfittInfo();
    }
    private void queryPrfittInfo(){
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.profitAvailable(baseApplication.mUser.token);
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
                        String[] profit = data.split(",");
                        for (int i=0;i<profit.length;i++) {
                            if (profit[i].equals("1")) {
                                radioOne.setVisibility(View.VISIBLE);
                                radio_one_view.setVisibility(View.VISIBLE);
                            }else if(profit[i].equals("2")){
                                radioTwo.setVisibility(View.VISIBLE);
                                radio_two_view.setVisibility(View.VISIBLE);
                            }else if(profit[i].equals("5")){
                                radioThree.setVisibility(View.VISIBLE);
                                radio_three_view.setVisibility(View.VISIBLE);
                            }else if(profit[i].equals("10")){
                                radioFour.setVisibility(View.VISIBLE);
                            }
                        }
                        //默认20%分利
                        //默认不上架
                        int profits = addProductBase.getProfit();
                                if (radioOne.getVisibility() == View.VISIBLE){
                                    radioOne.setChecked(true);
                                    addProductBase.setProfit(1);
                                }else if (radioTwo.getVisibility() == View.VISIBLE){
                                    radioTwo.setChecked(true);
                                    addProductBase.setProfit(2);
                                }else if (radioThree.getVisibility() == View.VISIBLE){
                                    radioThree.setChecked(true);
                                    addProductBase.setProfit(5);
                                }else {
                                    addProductBase.setProfit(10);
                                    handyTextView4.setText("暂无可选的让利模式");
                                }
                        switch (profits){
                            case 1:
                                if (radioOne.getVisibility() == View.VISIBLE){
                                    addProductBase.setProfit(1);
                                    radioOne.setChecked(true);
                                }
                                break;
                            case 2:
                                if (radioTwo.getVisibility() == View.VISIBLE){
                                    addProductBase.setProfit(2);
                                    radioTwo.setChecked(true);
                                }
                                break;
                            case 5:
                                if (radioThree.getVisibility() == View.VISIBLE){
                                    addProductBase.setProfit(5);
                                    radioThree.setChecked(true);
                                }
                                break;
                            case 10:
                                if (radioFour.getVisibility() == View.VISIBLE){
                                    addProductBase.setProfit(10);
                                    radioFour.setChecked(true);
                                }
                                break;
                        }
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
