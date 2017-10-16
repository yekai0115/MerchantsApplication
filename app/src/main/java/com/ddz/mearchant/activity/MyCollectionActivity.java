package com.ddz.mearchant.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.dialog.DialogConfirm;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.ChargeQRCod;
import com.ddz.mearchant.utils.CommonUtils;
import com.ddz.mearchant.utils.FileUtils;
import com.ddz.mearchant.view.HandyTextView;

import java.io.File;

import retrofit2.Call;
import retrofit2.Response;


public class MyCollectionActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private RadioGroup radioGroup;
    private RadioButton firstRadio,twoRadio,threeRadio;
    private ImageView qrView;
    private Bitmap qrBitmap, qrForSave;
    private int imgHeight;
    private String protfitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);
        mContext=this;
        initViews();
        initDialog();
        initEvents();
//        createQrCode();
    }



    //文件存储根目录
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }


    @Override
    protected void initViews() {
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        radioGroup = (RadioGroup)findViewById(R.id.radio_group);
        firstRadio = (RadioButton)findViewById(R.id.first_radio);
        twoRadio = (RadioButton)findViewById(R.id.two_radio);
        threeRadio = (RadioButton)findViewById(R.id.three_radio);
        qrView = (ImageView) findViewById(R.id.qr_code_image);
        htvCenter.setText("收款");
        htvLeft.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.first_radio:
                        getChargeQRCode("1");
                        break;
                    case R.id.two_radio:
                        getChargeQRCode("2");
                        break;
                    case R.id.three_radio:
                        getChargeQRCode("5");
                        break;
                }
            }
        });
        qrView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DialogConfirm dialogConfirm = new DialogConfirm();
                dialogConfirm.setListener(new DialogConfirm.OnOkCancelClickedListener() {
                    @Override
                    public void onClick(boolean isOkClicked) {
                        if(isOkClicked) {
                            if(FileUtils.saveBitmap(MyCollectionActivity.this, qrForSave)) {
                                Toast.makeText(MyCollectionActivity.this, getResources().getString(R.string.a_recommend_save_success), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                dialogConfirm.showDialog(MyCollectionActivity.this,getResources().getString(R.string.a_recommend_save),"确定","取消");
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                defaultFinish();
                break;
        }
    }
    @Override
    protected void initEvents() {
//        getChargeQRCode("2");
        queryPrfittInfo();
    }

    private void getChargeQRCode(String profitId){
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<ChargeQRCod>> call = userBiz.chargeQRCode(baseApplication.mUser.token,profitId,baseApplication.mUser.loginId);
        call.enqueue(new HttpCallBack<BaseResponse<ChargeQRCod>>() {
            @Override
            public void onResponse(Call<BaseResponse<ChargeQRCod>> arg0,
                                   Response<BaseResponse<ChargeQRCod>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<ChargeQRCod> baseResponse = response.body();
                if (null != baseResponse) {
                    String status = baseResponse.getStatus();
                    if (status.equals(Constants.T_OK)){
                        final ChargeQRCod data = baseResponse.getData();
                        if (data!=null){
//                            String url ="http://www2.ddzyigou.com/public/pay/payorder/22820/8049/1/0/22809";
                            /**
                             * 二维码的网址格式为 http://www2.ddzyigou.com/public/pay/payorder/22820/8049/1/0
                             * 在扫码支付的时候，末尾要加上 userId，即
                             * http://www2.ddzyigou.com/public/pay/payorder/22820/8049/1/0/***
                             */
                            final String url = Constants.ALI_MEARCHENT_LOAD+"2017/0627/f26d0c08c4b73addf28446915d1de375.jpg";
                            /*new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    qrBitmap = QrCodeCreator.createQRCode(data.getUrl(), imgHeight,BitmapFactory.decodeResource(getResources(), R.drawable.icon));
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                qrView.setImageBitmap(qrBitmap);
                                            }
                                        });
                                }
                            }).start();*/
                            final String filePath = getFileRoot(MyCollectionActivity.this) + File.separator
                                    + "qr_" + System.currentTimeMillis() + ".jpg";
                            //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    boolean success = CommonUtils.createQRImage(data.getUrl(), imgHeight, imgHeight, data.getHeadpic(),filePath);
                                    if (success) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                qrForSave = BitmapFactory.decodeFile(filePath);
                                                qrView.setImageBitmap(qrForSave);
                                            }
                                        });
                                    }
                                }
                            }).start();
//                            qrBitmap = createQRCode(data.getUrl(), imgHeight);
//                            qrView.setImageBitmap(qrBitmap);
//                            qrForSave = createQRCode(data.getUrl(), 640);
//                            qrView.setImageBitmap(qrForSave);
                        }
                    }
                }

            }
            @Override
            public void onFailure(Call<BaseResponse<ChargeQRCod>> arg0,
                                  Throwable arg1) {
            }
        });

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
                                firstRadio.setVisibility(View.VISIBLE);
                            }else if(profit[i].equals("2")){
                                twoRadio.setVisibility(View.VISIBLE);
                            }else if(profit[i].equals("5")){
                                threeRadio.setVisibility(View.VISIBLE);
                            }
                        }
                        if (firstRadio.getVisibility() == View.VISIBLE){
                            firstRadio.setChecked(true);
                            protfitId = "1";
                        }else if (twoRadio.getVisibility() == View.VISIBLE){
                            twoRadio.setChecked(true);
                            protfitId = "2";
                        }else if (threeRadio.getVisibility() == View.VISIBLE){
                            threeRadio.setChecked(true);
                            protfitId = "5";
                        }
                        qrView.post(new Runnable() {
                            @Override
                            public void run() {
                                imgHeight = qrView.getHeight();
                                getChargeQRCode(protfitId);
                            }
                        });
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
