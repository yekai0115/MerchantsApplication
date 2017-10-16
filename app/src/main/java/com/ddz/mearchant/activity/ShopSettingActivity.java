package com.ddz.mearchant.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.dialog.LoadingDialog;
import com.ddz.mearchant.view.HandyTextView;


public class ShopSettingActivity extends BaseActivity implements View.OnClickListener{
    private Context mContext;
    private HandyTextView htvCenter;
    private LinearLayout htvLeft;
    private LinearLayout postageTemplateLinear,customClassificationLinear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_setting);
        mContext=this;
        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        htvLeft = (LinearLayout) findViewById(R.id.title_htv_left);
        htvCenter = (HandyTextView) findViewById(R.id.title_htv_center);
        postageTemplateLinear = (LinearLayout) findViewById(R.id.postage_template_linear);
        customClassificationLinear = (LinearLayout) findViewById(R.id.custom_classification_linear);


        htvCenter.setText("商品设置");
        htvLeft.setOnClickListener(this);
        postageTemplateLinear.setOnClickListener(this);
        customClassificationLinear.setOnClickListener(this);
    }
    @Override
    protected void initEvents() {
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                    defaultFinish();
                break;
            case R.id.postage_template_linear:
                    startActivity(PostageTemplateActivity.class);
                break;
            case R.id.custom_classification_linear:
                    startActivity(CustomClassificationActivity.class);
                break;
        }
    }
    private LoadingDialog dialog;


}
