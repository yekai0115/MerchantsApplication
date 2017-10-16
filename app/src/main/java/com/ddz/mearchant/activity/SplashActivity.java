package com.ddz.mearchant.activity;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.RelativeLayout;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.interfaces.PermissionListener;
import com.ddz.mearchant.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 启动页面
 */
public class SplashActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback,PermissionListener {
    private RelativeLayout rl_splash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        rl_splash=(RelativeLayout)findViewById(R.id.rl_splash);
        if (Build.VERSION.SDK_INT >= 23) {// 6.0以上系统申请文件权限
            checkPermisson();
        }else{
            getTableId();
            //    startMain();
        }

    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void checkPermisson() {
        String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION  };
        requestRuntimePermission(permissions, this, 1);
    }

    // andrpoid 6.0 及以上需要写运行时权限
    public void requestRuntimePermission(String[] permissions,
                                         PermissionListener listener, int type) {

        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {// 如果permissionList不为空，说明需要申请这些权限
            ActivityCompat.requestPermissions(this,
                    permissionList.toArray(new String[permissionList.size()]),
                    type);
        }else{//权限已有
            getTableId();
            //   startMain();
        }
    }


    //获得权限
    @Override
    public void onGranted(int type) {
        getTableId();
//        startMain();
    }

    // 权限被拒绝
    @Override
    public void onDenied(List<String> deniedPermission) {
        getTableId();
//        startMain();
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            List<String> deniedPermissions = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                String permission = permissions[i];
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permission);
                }
            }
            if (deniedPermissions.isEmpty()) {
                onGranted(requestCode);
            } else {
                onDenied(deniedPermissions);
            }
        }
    }

    private void startMain(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                start(SplashActivity.this);
            }
        }, 500);
    }

    protected void start(Context ctx) {
        Boolean isfirst = (Boolean) SPUtils.get(ctx, "isFirstRun", true);// 是否为第一次运行程序
        if (isfirst) {
            toMainActivity(2);
        } else {
            toMainActivity(1);
        }
    }

    private void toMainActivity(final int type) {
        rl_splash.animate().scaleXBy(0.1f).scaleYBy(0.1f).alphaBy(0.1f).setDuration(1000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                startActivity(new Intent(mContext, MainActivity.class));
//                overridePendingTransition(0,0);
                rl_splash.clearAnimation();
                if(type==1){
                    if (baseApplication.mUser != null && baseApplication.mUser.token != null){
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }else
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }else{//第一次运行
                    startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                    SPUtils.put(SplashActivity.this, "isFirstRun", false);
                }
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    /**
     * 获取高德地图表ID
     */
    private void getTableId() {
        startMain();
//        MyOkHttpClient.Param[] paramArray = new MyOkHttpClient.Param[]{
//        };
//        ParamPacket paramPacket = new ParamPacket(Urls.GET_TABLEID, paramArray, MsgType.GET_PROVICELIST);
//        MyOkHttpClient.postAsynWithoutHeader(SplashActivity.this,
//                paramPacket.getPostUrl(),
//                paramPacket.getParamArray(),
//                new MyResultCallback<String>() {
//                    @Override
//                    public void onError(Request request, Exception e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            MyConstants.mTableID= JsonHandler.getJsonString(jsonObject, "table_id", "59193e002376c145fc950432");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//
//                    @Override
//                    public void onAfter() {
//                        startMain();
//                        super.onAfter();
//                    }
//                }
//        );
    }


}
