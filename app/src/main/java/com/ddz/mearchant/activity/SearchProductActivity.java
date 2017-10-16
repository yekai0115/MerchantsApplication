package com.ddz.mearchant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.utils.StringUtils;
import com.ddz.mearchant.view.HandyTextView;

/**
 * Created by Administrator on 2017/6/28 0028.
 */

public class SearchProductActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext;
    private EditText searchEdit;
    private HandyTextView canaelSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        mContext=this;
        initViews();
    }

    @Override
    protected void initViews() {
        searchEdit = (EditText)findViewById(R.id.search_edit);
        canaelSearch = (HandyTextView)findViewById(R.id.canael_search);
        canaelSearch.setOnClickListener(this);
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,KeyEvent event)  {
                if (actionId== EditorInfo.IME_ACTION_SEND ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER))
                {
                    InputMethodManager in = (InputMethodManager)searchEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (in.isActive()){
                        in.hideSoftInputFromWindow(searchEdit.getApplicationWindowToken(),0);
                        ToSearchProductActivity();
                    }
                    return false;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.canael_search:
                    defaultFinish();
                break;

        }
    }
    @Override
    protected void initEvents() {

    }

    /**
     * 获取账户余额
     */
    private void ToSearchProductActivity() {
        if (StringUtils.isBlank(searchEdit.getText().toString())){
            showShortToast("您还没有输入文字");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("searchText",searchEdit.getText().toString());
        intent.setClass(SearchProductActivity.this,SearchProductResultActivity.class);
        startActivity(intent);
    }

}