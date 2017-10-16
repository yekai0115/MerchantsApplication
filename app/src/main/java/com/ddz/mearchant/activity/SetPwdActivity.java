package com.ddz.mearchant.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.view.ClearEditText;


public class SetPwdActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext;
    private ClearEditText inputConfirmPwd,inputNewPwd;
    private Button setPwdComplete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pwd);
        mContext=this;
        initViews();
    }

    @Override
    protected void initViews() {
        inputConfirmPwd = (ClearEditText)findViewById(R.id.input_confirm_pwd);
        inputNewPwd = (ClearEditText)findViewById(R.id.input_new_pwd);
        setPwdComplete = (Button)findViewById(R.id.set_pwd_complete);
        setPwdComplete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_pwd_complete:
                startActivity(MainActivity.class);
                break;
        }
    }

    @Override
    protected void initEvents() {

    }

}
