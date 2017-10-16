package com.ddz.mearchant.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.adapter.BankAdapter;
import com.ddz.mearchant.aliutil.PutObjectSamples;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.common.CreateFile;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.config.MyOSSConfig;
import com.ddz.mearchant.dialog.DialogSelPhoto;
import com.ddz.mearchant.dialog.DialogSelectAddr;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.interfaces.PermissionListener;
import com.ddz.mearchant.models.Addrs;
import com.ddz.mearchant.models.BandCardBase;
import com.ddz.mearchant.models.BindCard;
import com.ddz.mearchant.models.Custom;
import com.ddz.mearchant.models.Proivince;
import com.ddz.mearchant.utils.FileUtil;
import com.ddz.mearchant.utils.ImgSetUtil;
import com.ddz.mearchant.utils.StringUtils;
import com.ddz.mearchant.view.HandyTextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/6/24 0024.
 */

public class BindBankActivity extends BaseActivity implements View.OnClickListener,PermissionListener,ActivityCompat.OnRequestPermissionsResultCallback{
    private Context mContext;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private Custom mCustom;
    private Button confirmButton;
    private String localTempImageFileName;
    private EditText bankNumber, subBankName;
    private TextView tvProvince, tvCity, tvArea,bankName,perName;
    private ImageView bankImg;
    private int type=0;
    private String imgPath=null;
    private String imgKey;
    private List<Proivince> proivinces = new ArrayList<>();
    private int proivinceSelect,citySelection,areaSelection;
    private Proivince addrProvince =null, addrCity=null, addrArea=null;
    /**银行卡数据*/
    private List<BindCard> bankItems;
    private BandCardBase bandCode;
    private String bankCode;
    private DialogSelPhoto dialogSelPhoto;
    private ImageView frameImage1,frameImage2;
    private OSS oss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_bank);
        mContext=this;
        dialogSelPhoto = new DialogSelPhoto();
        if (getIntent().hasExtra("bandCardBase")){
            bandCode = (BandCardBase)getIntent().getBundleExtra("bandCardBase").getSerializable("bandCardBase");
            if (bandCode.getBanklist()!=null&&bandCode.getBanklist().size()>0)
                bankItems = bandCode.getBanklist();
        }
        oss = new OSSClient(getApplicationContext(), Constants.ALI_MEARCHENT_ENDPOINT, MyOSSConfig.getProvider(), MyOSSConfig.getOSSConfig());
        initViews();
        initEvents();
        initDialog();
        dialogSelPhoto.setListener(new DialogSelPhoto.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isCameraSel, int imgIndex) {
                if(isCameraSel) {
                    //拍照
                    takePhotoForCamera(imgIndex);
                } else {
                    //相册
                    takePhotoForAlbum(imgIndex);
                }
            }
        });
    }
    @Override
    protected void initEvents() {
        getProivinceList();
    }

    @Override
    protected void initViews() {
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        confirmButton = (Button)findViewById(R.id.confirm_button);
        perName = (TextView) findViewById(R.id.abc_name);
        bankName = (TextView) findViewById(R.id.abc_bank_name);
        bankNumber = (EditText) findViewById(R.id.abc_bank_number);
        subBankName = (EditText) findViewById(R.id.abc_sub_bank_name);
        tvProvince = (TextView) findViewById(R.id.abc_select_province);
        tvCity = (TextView) findViewById(R.id.abc_select_city);
        tvArea = (TextView) findViewById(R.id.abc_select_area);
        bankImg = (ImageView) findViewById(R.id.abc_bank_img);
        frameImage1 = (ImageView) findViewById(R.id.frame_image1);
        frameImage2 = (ImageView) findViewById(R.id.frame_image2);
        htvCenter.setText("绑定银行卡");
        if(bandCode.getUserinfo()!=null && bandCode.getUserinfo().getLegal_name()!=null) {
            perName.setText(bandCode.getUserinfo().getLegal_name());
        }
        htvLeft.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        tvProvince.setOnClickListener(this);
        tvCity.setOnClickListener(this);
        tvArea.setOnClickListener(this);
        bankName.setOnClickListener(this);
        bankImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                defaultFinish();
                break;
            case R.id.confirm_button:
                sumbitBindBankInfo();
                break;
            case R.id.abc_select_province:
                showProvinceList();
                break;
            case R.id.abc_select_city:
                    showCityList();
                break;
            case R.id.abc_select_area:
                showAreaList();
                break;
            case R.id.abc_bank_name:
                if(null==bankItems||bankItems.isEmpty()){//银行数据为空
//                    getBankLst();
                }else{//银行数据已经取到
                    alertBankList(BindBankActivity.this,bankItems);
                }
                break;
            case R.id.abc_bank_img:
                dialogSelPhoto.showDialog(BindBankActivity.this,0);
                break;
        }
    }
    private void showProvinceList() {
        new DialogSelectAddr().showProvincesDialog(BindBankActivity.this, Addrs.getProvinceNameList(), proivinceSelect,new DialogSelectAddr.OnItemClickedListener() {
            @Override
            public void onClick(int dialogIndex, int selectedIndex) {
                proivinceSelect = selectedIndex;
                Proivince addrObject = Addrs.getProvinceObjects().get(selectedIndex);
                addrProvince = addrObject;
                tvProvince.setText(addrProvince.getRegion_name());
                tvCity.setText(getResources().getString(R.string.abc_select_city));
                tvArea.setText(getResources().getString(R.string.abc_select_area));
                citySelection = 0;
                areaSelection = 0;
                addrCity = null;
                addrArea = null;
                getCityList();
            }

        }, new DialogSelectAddr.OnConfirmClickedListener() {
            @Override
            public void onConfirmClick(int dialogIndex, int selectedIndex) {
                proivinceSelect = selectedIndex;
                Proivince addrObject = Addrs.getProvinceObjects().get(selectedIndex);
                addrProvince = addrObject;
                tvProvince.setText(addrProvince.getRegion_name());
                tvCity.setText(getResources().getString(R.string.abc_select_city));
                tvArea.setText(getResources().getString(R.string.abc_select_area));
                citySelection = 0;
                areaSelection = 0;
                addrCity = null;
                addrArea = null;
                getCityList();
            }
        }, new DialogSelectAddr.OnCancelClickedListener() {
            @Override
            public void OnCancelClick() {

            }
        });

    }
    private void showAreaList() {
        if(addrCity == null) {
            Toast.makeText(BindBankActivity.this, getResources().getString(R.string.common_sel_city), Toast.LENGTH_SHORT).show();
            return;
        }
        new DialogSelectAddr().showProvincesDialog(BindBankActivity.this, Addrs.getAreaNameList(), areaSelection,new DialogSelectAddr.OnItemClickedListener() {
            @Override
            public void onClick(int dialogIndex, int selectedIndex) {
                areaSelection = selectedIndex;
                Proivince addrObject = Addrs.getAreaObjects().get(selectedIndex);
                addrArea = addrObject;
                tvArea.setText(addrArea.getRegion_name());
            }

        }, new DialogSelectAddr.OnConfirmClickedListener() {
            @Override
            public void onConfirmClick(int dialogIndex, int selectedIndex) {
                areaSelection = selectedIndex;
                Proivince addrObject = Addrs.getAreaObjects().get(selectedIndex);
                addrArea = addrObject;
                tvArea.setText(addrArea.getRegion_name());
            }
        }, new DialogSelectAddr.OnCancelClickedListener() {
            @Override
            public void OnCancelClick() {

            }
        });

    }
    private void showCityList() {
        if(addrProvince == null) {
            Toast.makeText(BindBankActivity.this, getResources().getString(R.string.common_sel_province), Toast.LENGTH_SHORT).show();
            return;
        }
        new DialogSelectAddr().showProvincesDialog(BindBankActivity.this, Addrs.getCityNameList(), citySelection,new DialogSelectAddr.OnItemClickedListener() {
            @Override
            public void onClick(int dialogIndex, int selectedIndex) {
                citySelection = selectedIndex;
                Proivince addrObject = Addrs.getCityObjects().get(selectedIndex);
                addrCity = addrObject;
                tvCity.setText(addrCity.getRegion_name());
                tvArea.setText(getResources().getString(R.string.abc_select_area));
                areaSelection = 0;
                addrArea = null;
                getAreaList();
            }

        }, new DialogSelectAddr.OnConfirmClickedListener() {
            @Override
            public void onConfirmClick(int dialogIndex, int selectedIndex) {
                citySelection = selectedIndex;
                Proivince addrObject = Addrs.getCityObjects().get(selectedIndex);
                addrCity = addrObject;
                tvCity.setText(addrCity.getRegion_name());
                tvArea.setText(getResources().getString(R.string.abc_select_area));
                areaSelection = 0;
                addrArea = null;
                getAreaList();
            }
        }, new DialogSelectAddr.OnCancelClickedListener() {
            @Override
            public void OnCancelClick() {

            }
        });

    }
    private void getAreaList() {
        tvArea.setClickable(false);
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<Proivince>>> call = userBiz.getCityList(baseApplication.mUser.token,addrCity.getRegion_id());
        call.enqueue(new Callback<BaseResponse<List<Proivince>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Proivince>>> arg0,
                                   Response<BaseResponse<List<Proivince>>> response) {
                BaseResponse<List<Proivince>> baseResponse = response.body();
                if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
                    String status = baseResponse.getStatus();
                    List<Proivince> data = baseResponse.getData();
                    if (status.equals(Constants.T_OK)){
//                        proivinces.addAll(data);
                        tvArea.setClickable(true);
                        Addrs.setAreaObjects(data);
                    }else{

                    }
                }
            }
            @Override
            public void onFailure(Call<BaseResponse<List<Proivince>>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    private void getCityList() {
        tvCity.setClickable(false);
        tvArea.setClickable(false);
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<Proivince>>> call = userBiz.getCityList(baseApplication.mUser.token,addrProvince.getRegion_id());
        call.enqueue(new Callback<BaseResponse<List<Proivince>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Proivince>>> arg0,
                                   Response<BaseResponse<List<Proivince>>> response) {
                BaseResponse<List<Proivince>> baseResponse = response.body();
                if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
                    String status = baseResponse.getStatus();
                    List<Proivince> data = baseResponse.getData();
                    if (status.equals(Constants.T_OK)){
//                        proivinces.addAll(data);
                        tvCity.setClickable(true);
                        tvArea.setClickable(false);
                        Addrs.setCityObjects(data);
                    }else{

                    }
                }
            }
            @Override
            public void onFailure(Call<BaseResponse<List<Proivince>>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    private void getProivinceList(){
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<Proivince>>> call = userBiz.getProivinceList(baseApplication.mUser.token);//"18813904075:123456789"
        call.enqueue(new Callback<BaseResponse<List<Proivince>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Proivince>>> arg0,
                                   Response<BaseResponse<List<Proivince>>> response) {
                BaseResponse<List<Proivince>> baseResponse = response.body();
                if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
                    String status = baseResponse.getStatus();
                    List<Proivince> data = baseResponse.getData();
                    if (status.equals(Constants.T_OK)){
                        proivinces.addAll(data);
                        Addrs.setProvinceObjects(proivinces);
                    }else{

                    }
                }
            }
            @Override
            public void onFailure(Call<BaseResponse<List<Proivince>>> arg0,
                                  Throwable arg1) {
            }
        });
    }


    private void sumbitBindBankInfo(){
        if(!checkParmer()){
            return;
        }
        dialog.show();
        upLoadAli(imgKey,imgPath);

    }
    private void sumbitInfo(){
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.bindBankSubmit(baseApplication.mUser.token,
                bandCode.getToken(),perName.getText().toString(),
                bankNumber.getText().toString(),imgKey,
                bankName.getText().toString(),bankCode,
                tvProvince.getText().toString()+tvCity.getText().toString()+tvArea.getText().toString()
                        +subBankName.getText().toString());
        call.enqueue(new HttpCallBack<BaseResponse<String>>() {

            @Override
            public void onResponse(Call<BaseResponse<String>> arg0,
                                   Response<BaseResponse<String>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<String> baseResponse = response.body();
                if (null != baseResponse) {
                    showShortToast("添加银行卡信息成功");
                    defaultFinish();
                    Intent intent = new Intent();
                    intent.setClass(BindBankActivity.this,ExchangeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<String>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    private boolean checkParmer(){
        if (StringUtils.isBlank(perName.getText().toString())){
            showShortToast("持卡人不能为空");
            return false;
        }
        if (StringUtils.isBlank(bankNumber.getText().toString())){
            showShortToast("银行卡号不能为空");
            return false;
        }
        if (StringUtils.isBlank(bankName.getText().toString())){
            showShortToast("您还没有选择开卡银行");
            return false;
        }
        if (StringUtils.isBlank(tvProvince.getText().toString())){
            showShortToast("您还没有选择省");
            return false;
        }
        if (StringUtils.isBlank(tvCity.getText().toString())){
            showShortToast("您还没有选择市");
            return false;
        }
        if (StringUtils.isBlank(tvArea.getText().toString())){
            showShortToast("您还没有选择区");
            return false;
        }
        if (StringUtils.isBlank(subBankName.getText().toString())){
            showShortToast("您还没有输入开户支行");
            return false;
        }
        if (StringUtils.isBlank(imgKey)){
            showShortToast("您还没有上传银行卡正面照");
            return false;
        }
        return true;
    }
    /**
     *
     * @param ctx
     *
     */
    private void alertBankList(final Context ctx, final List<BindCard> items) {
//        final Dialog lDialog = new Dialog(ctx, R.style.huodongstyle);
        final Dialog lDialog = new Dialog(ctx);
        lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        lDialog.setContentView(R.layout.dialog_provider1);
        final TextView title = (TextView) lDialog.findViewById(R.id.title_dialog);
        title.setText("选择银行卡");
        ListView listView=(ListView) lDialog.findViewById(R.id.lv_hangye);
        BankAdapter adapter=new BankAdapter(ctx, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                for (int i = 0; i < items.size(); i++)
                {
                    if(i==position){
                        items.get(i).setChecked(true);
                    }else{
                        items.get(i).setChecked(false);
                    }
                }
                bankName.setText(items.get(position).getBankname());
                bankName.setTextColor(mContext.getResources().getColor(R.color.gray_33));
                bankCode = items.get(position).getCode();
                lDialog.dismiss();
            }
        });
        lDialog.setCancelable(true);
        lDialog.setCanceledOnTouchOutside(true);
        lDialog.show();
    }

    private void takePhotoForAlbum(int value) {
        String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE };
        requestRuntimePermission(permissions, this,2);
    }

    private void takePhotoForCamera(int value) {
        String[] permissions = { Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE };
        requestRuntimePermission(permissions, this, 1);
    }
    // andrpoid 6.0 及以上需要写运行时权限
    public void requestRuntimePermission(String[] permissions, PermissionListener listener, int type) {

        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this,permissionList.toArray(new String[permissionList.size()]),type);
        } else {
            onGranted(type);
        }
    }

    //获得权限
    @Override
    public void onGranted(int value) {
        if(value==1){
            captureImage(CreateFile.FEEDBACK_PATH,1);
        }else if(value==2){
            selectImage(2);
        }
    }



    @Override
    public void onDenied(List<String> deniedPermission) {


    }

    /**
     * 拍照
     *
     * @param path
     *            照片存放的路径
     */
    public void captureImage(String path,int value) {
        Uri data;
        localTempImageFileName = String.valueOf((new Date()).getTime()) + ".jpg";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(CreateFile.FEEDBACK_PATH, localTempImageFileName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
            data = FileProvider.getUriForFile(this, "com.ddz.mearchant.fileprovider", f);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(f);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, data);
        startActivityForResult(intent, value);
    }
    /**
     * 从图库中选取图片
     */
    public void selectImage(int value) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, value);
    }
    public void compress(String srcPath, String fileName) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float hh = dm.heightPixels;
        float ww = dm.widthPixels;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, opts);
        opts.inJustDecodeBounds = false;
        int w = opts.outWidth;
        int h = opts.outHeight;
        int size = 0;
        if (w <= ww && h <= hh) {
            size = 1;
        } else {
            double scale = w >= h ? w / ww : h / hh;
            double log = Math.log(scale) / Math.log(2);
            double logCeil = Math.ceil(log);
            size = (int) Math.pow(2, logCeil);
        }
        opts.inSampleSize = size;
        bitmap = BitmapFactory.decodeFile(srcPath, opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        System.out.println(baos.toByteArray().length);
        while (baos.toByteArray().length > 45 * 1024) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 20;
            System.out.println(baos.toByteArray().length);
        }
        try {
            baos.writeTo(new FileOutputStream(CreateFile.FEEDBACK_PATH + fileName));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {
            String fileName;
            switch (requestCode) {
                case 1: {
                    handleCameraRet(data, 0);
                    break;
                }
                case 2: {
                    handleAlbumRet(data, 0);
                    break;
                }

                default:
                    break;
            }
        }
    }
    private void handleCameraRet(Intent data, int value){
        // 将保存在本地的图片取出并缩小后显示在界面上
        String imgKey1 = ImgSetUtil.getImgKeyString();
        String path = CreateFile.FEEDBACK_PATH + localTempImageFileName;
        String newPath = CreateFile.FEEDBACK_PATH + localTempImageFileName;
        compress(path, localTempImageFileName);
        Bitmap bitmap = FileUtil.decodeFile(path);
        switch (value) {
            case 0:
                imgPath = path;
                imgKey = imgKey1;
                frameImage1.setVisibility(View.GONE);
                frameImage2.setVisibility(View.GONE);
//                        upHeadImages.setVisibility(View.VISIBLE);
                bankImg.setImageBitmap(bitmap);
                break;
        }
    }
    private void upLoadAli(final String key,final String path){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = new PutObjectSamples(oss, Constants.ALI_BUCKET_RECOMMEND, key, path).putObjectFromLocalFile();
                if (flag) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sumbitInfo();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            showShortToast("上传失败,请重新上传");
                        }
                    });
                }
            }
        }).start();
    }

    private void handleAlbumRet(Intent data, int value){
        String fileName;
        String imgKey1 = ImgSetUtil.getImgKeyString();
        ContentResolver resolver = getContentResolver();
        // 照片的原始资源地址
        Uri originalUri = data.getData();
        try {
            Cursor cursor = resolver.query(originalUri, new String[] { MediaStore.Images.Media.DATA }, null,
                    null, null);
            // 使用ContentProvider通过URI获取原始图片
            Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
            if (photo != null) {
                // 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                Bitmap smallBitmap = FileUtil.zoomBitmap(photo, photo.getWidth() / 5,
                        photo.getHeight() / 5);
                // 释放原始图片占用的内存，防止out of memory异常发生
                photo.recycle();
                // 生成一个图片文件名
                fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
                cursor.moveToFirst();
                String path2 = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                compress(path2, fileName);
                Bitmap newBitmap = FileUtil.decodeFile(path2);
                switch (value) {
                    case 0:
                        imgPath = path2;
                        imgKey = imgKey1;
                        frameImage1.setVisibility(View.GONE);
                        frameImage2.setVisibility(View.GONE);
//                        upHeadImages.setVisibility(View.VISIBLE);
                        bankImg.setImageBitmap(newBitmap);
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}