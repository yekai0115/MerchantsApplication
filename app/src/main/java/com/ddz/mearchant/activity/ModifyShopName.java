package com.ddz.mearchant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.models.ShopBase;
import com.ddz.mearchant.view.ClearEditText;
import com.ddz.mearchant.view.HandyTextView;


public class ModifyShopName extends BaseActivity implements View.OnClickListener{

    private Context mContext;
    private ClearEditText shopName;
    private HandyTextView save;
    private ShopBase shopBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_name);
        mContext=this;
        if (getIntent().getBundleExtra("ShopBase")!=null){
            shopBase = (ShopBase) getIntent().getBundleExtra("ShopBase").getSerializable("ShopBase");
        }
        initViews();
    }

    @Override
    protected void initViews() {
        shopName = (ClearEditText)findViewById(R.id.shop_name);
        save = (HandyTextView)findViewById(R.id.title_htv_rigit);
        save.setVisibility(View.VISIBLE);
        save.setOnClickListener(this);
        shopName.setText(shopBase.getName());
        shopName.setSelection(shopName.getText().length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_rigit:
                    modifyShopName();
                break;
        }
    }
    private  void  modifyShopName(){
            Intent mIntent = new Intent();
            mIntent.putExtra("shopName", shopName.getText().toString());
            setResult(RESULT_OK,mIntent);
            defaultFinish();
    }
    @Override
    protected void initEvents() {

    }
}
