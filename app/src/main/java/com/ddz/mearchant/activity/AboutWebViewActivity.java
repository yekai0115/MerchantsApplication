package com.ddz.mearchant.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.LinearLayout;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.view.AdvancedWebView;
import com.ddz.mearchant.view.HandyTextView;

/**
 * Created by Administrator on 2017/7/15 0015.
 */

public class AboutWebViewActivity extends BaseActivity implements View.OnClickListener{
    private AdvancedWebView webView;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_diandian_web);
        initViews();




    }

    @Override
    protected void initViews() {
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        htvCenter.setText("关于点点赞");
        htvLeft.setOnClickListener(this);
        webView = (AdvancedWebView) findViewById(R.id.acw_webview);
        WebSettings settings=webView.getSettings();
        //   settings.setTextSize(WebSettings.TextSize.SMALLEST);
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        webView.removeJavascriptInterface("searchBoxJavaBredge_");
///        settings.setTextSize(WebSettings.TextSize.SMALLER);
//        settings.setTextSize(WebSettings.TextSize.SMALLEST);
        settings.setTextSize(WebSettings.TextSize.LARGER);
////        settings.setTextSize(WebSettings.TextSize.LARGEST);
        webView.loadUrl(Constants.COM_ABOUT);
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
        }
    }
}
