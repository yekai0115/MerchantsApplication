package com.ddz.mearchant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.view.ClearEditText;
import com.ddz.mearchant.view.HandyTextView;


public class ProductTextintroduceActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext;
    private ClearEditText shopName;
    private EditText IntroduceText;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_text_introduce);
        mContext=this;
        initViews();
        if (getIntent().getStringExtra("introduceText")!=null){
            text = getIntent().getStringExtra("introduceText");
            IntroduceText.setText(text);
            IntroduceText.setSelection(text.length()-1);
        }

    }

    @Override
    protected void initViews() {
        htvRight = (HandyTextView)findViewById(R.id.title_htv_rigit);
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        IntroduceText = (EditText)findViewById(R.id.product_introduce_text);
        htvRight.setVisibility(View.VISIBLE);
        htvCenter.setText("文字介绍");
        htvRight.setText("完成");
        htvRight.setOnClickListener(this);
        htvLeft.setOnClickListener(this);
        IntroduceText.setSelection(IntroduceText.length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_rigit:
                    saveTextIntroduce();
                break;
            case R.id.title_htv_left:
                    defaultFinish();
                break;
        }
    }
    private  void  saveTextIntroduce(){
            Intent mIntent = new Intent();
            mIntent.putExtra("introduceText", IntroduceText.getText().toString());
            setResult(RESULT_OK,mIntent);
            defaultFinish();
    }
    @Override
    protected void initEvents() {

    }


}
