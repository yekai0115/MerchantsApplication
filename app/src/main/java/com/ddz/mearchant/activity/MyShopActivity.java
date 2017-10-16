package com.ddz.mearchant.activity;

import android.Manifest;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.bumptech.glide.Glide;
import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.aliutil.PutObjectSamples;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.common.CreateFile;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.config.MyOSSConfig;
import com.ddz.mearchant.dialog.DialogSelPhoto;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.interfaces.PermissionListener;
import com.ddz.mearchant.models.ShopBase;
import com.ddz.mearchant.utils.FileUtil;
import com.ddz.mearchant.utils.ImageUrl;
import com.ddz.mearchant.utils.ImgSetUtil;
import com.ddz.mearchant.view.CircleImageView;
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
import retrofit2.Response;


public class MyShopActivity extends BaseActivity implements View.OnClickListener,PermissionListener,ActivityCompat.OnRequestPermissionsResultCallback{
    private DialogSelPhoto dialogSelPhoto;
    private final int MODIFY = 13;
    private final int IMG1=0, IMG2=1, IMG3=2, IMG4=3,IMG5 = 4,IMG6 = 5;
    private final int IMG1_C = 3, IMG1_A = 4, IMG2_C = 5, IMG2_A = 6, IMG3_C = 7, IMG3_A = 8, IMG4_C = 9, IMG4_A = 10,IMG5_C = 11, IMG5_A = 12,IMG6_C = 14, IMG6_A = 15;
    private int type=0;
    private Context mContext;
    private LinearLayout returnBack;
    private ImageView upHeadImage,upHeadImages;
    private ImageView shopInsideImage1,shopInsideImages1,shopInsideImages2,shopInsideImage2,shopInsideImage3,shopInsideImages3;
    private ImageView bookInsideImage,bookInsideImages;
    private ShopBase shopBase;
    private CircleImageView shopLogo;
    private File cameraFile;
    private String localTempImageFileName;
    private static int IMAGE_CAPTURE = 1;
    private static int IMAGE_SELECT = 2;
    private LinearLayout up_image_linear,shopInsideLinear1,shopInsideLinear2,shopInsideLinear3,bookInsideLinear;
    private String img1Path=null, img2Path=null, img3Path=null, img4Path=null, img5Path=null, img6Path=null;
    private String img1Key, img2Key, img3Key, img4Key, img5Key, img6Key;
    private HandyTextView shopName,shopCatName,shopAddress,shopMobile,saveShopInfo;
    private RelativeLayout shopNameLinear;
    private EditText shopTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_shop);
        mContext=this;
        dialogSelPhoto = new DialogSelPhoto();
        if (getIntent().getBundleExtra("ShopBase")!=null){
            shopBase = (ShopBase) getIntent().getBundleExtra("ShopBase").getSerializable("ShopBase");
        }
        oss = new OSSClient(getApplicationContext(), Constants.ALI_MEARCHENT_ENDPOINT, MyOSSConfig.getProvider(), MyOSSConfig.getOSSConfig());
        initViews();
        initEvents();
        initDialog();
    }

    @Override
    protected void initViews() {

        saveShopInfo = (HandyTextView)findViewById(R.id.save_shop_info);
        returnBack = (LinearLayout) findViewById(R.id.return_back);
        upHeadImage = (ImageView)findViewById(R.id.up_head_image);
        upHeadImages = (ImageView)findViewById(R.id.up_head_images);
        shopInsideLinear1 = (LinearLayout)findViewById(R.id.shop_inside_linear1);
        shopInsideImage1 = (ImageView)findViewById(R.id.shop_inside_image1);
        shopInsideImages1 = (ImageView)findViewById(R.id.shop_inside_images1);
        shopInsideLinear2 = (LinearLayout)findViewById(R.id.shop_inside_linear2);
        shopInsideImage2 = (ImageView)findViewById(R.id.shop_inside_image2);
        shopInsideImages2 = (ImageView)findViewById(R.id.shop_inside_images2);
        shopInsideLinear3 = (LinearLayout)findViewById(R.id.shop_inside_linear3);
        shopInsideImage3 = (ImageView)findViewById(R.id.shop_inside_image3);
        shopInsideImages3 = (ImageView)findViewById(R.id.shop_inside_images3);
        bookInsideLinear = (LinearLayout)findViewById(R.id.book_inside_linear);
        bookInsideImage = (ImageView)findViewById(R.id.book_inside_image);
        bookInsideImages = (ImageView)findViewById(R.id.book_inside_images);
        up_image_linear = (LinearLayout)findViewById(R.id.up_image_linear);

        shopNameLinear = (RelativeLayout)findViewById(R.id.shop_name_linear);

        shopLogo = (CircleImageView) findViewById(R.id.shop_logo);
        shopName = (HandyTextView) findViewById(R.id.shop_name);
        shopCatName = (HandyTextView) findViewById(R.id.shop_cat_name);
        shopTime = (EditText) findViewById(R.id.shop_time);
        shopMobile = (HandyTextView) findViewById(R.id.shop_mobile);
        shopAddress = (HandyTextView) findViewById(R.id.shop_address);
        returnBack.setOnClickListener(this);
        up_image_linear.setOnClickListener(this);
        shopInsideLinear1.setOnClickListener(this);
        shopInsideLinear2.setOnClickListener(this);
        shopInsideLinear3.setOnClickListener(this);
        bookInsideLinear.setOnClickListener(this);
        shopNameLinear.setOnClickListener(this);
        shopLogo.setOnClickListener(this);
        saveShopInfo.setOnClickListener(this);
        if(shopBase!=null){
            try {
                if (shopBase.getLogo()!=null){
                    img6Key = shopBase.getLogo();
//                    Glide.with(mContext).load(Constants.ALI_MEARCHENT_LOAD+shopBase.getLogo()).fitCenter()
//                            .placeholder(getResources().getDrawable(R.drawable.default_mearent)).error(getResources().getDrawable(R.drawable.default_mearent)).into(shopLogo);
                    ImageUrl.setbitmap(Constants.ALI_MEARCHENT_LOAD+shopBase.getLogo(),shopLogo);
                }
                if (shopBase.getShop_headpic()!=null){
                    img1Key = shopBase.getShop_headpic();
                    upHeadImage.setVisibility(View.GONE);
                    upHeadImages.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(Constants.ALI_MEARCHENT_LOAD+shopBase.getShop_headpic()).fitCenter()
                            .placeholder(getResources().getColor(R.color.tab_text_diveder)).error(getResources().getColor(R.color.tab_text_diveder)).into(upHeadImages);
//                    ImageUrl.setbitmap(Constants.ALI_MEARCHENT_LOAD+shopBase.getShop_headpic(),upHeadImages);
                }
                if (shopBase.getAblum()!=null){
                    String[] ablunm = shopBase.getAblum().split(",");
                    switch (ablunm.length){
                        case 1:
                            shopInsideImage1.setVisibility(View.GONE);
                            shopInsideImages1.setVisibility(View.VISIBLE);
                            img2Key = ablunm[0];
                            Glide.with(mContext).load(Constants.ALI_MEARCHENT_LOAD+ablunm[0]).fitCenter()
                                    .placeholder(getResources().getColor(R.color.tab_text_diveder)).error(getResources().getColor(R.color.tab_text_diveder)).into(shopInsideImages1);
//                            ImageUrl.setbitmap(Constants.ALI_MEARCHENT_LOAD+ablunm[0],shopInsideImages1);
                            break;
                        case 2:
                            shopInsideImage1.setVisibility(View.GONE);
                            shopInsideImages1.setVisibility(View.VISIBLE);
                            Glide.with(mContext).load(Constants.ALI_MEARCHENT_LOAD+ablunm[0]).fitCenter()
                                    .placeholder(getResources().getColor(R.color.tab_text_diveder)).error(getResources().getColor(R.color.tab_text_diveder)).into(shopInsideImages1);
                            shopInsideImage2.setVisibility(View.GONE);
                            shopInsideImages2.setVisibility(View.VISIBLE);
                            img2Key = ablunm[0];
                            img3Key = ablunm[1];
                            Glide.with(mContext).load(Constants.ALI_MEARCHENT_LOAD+ablunm[1]).fitCenter()
                                    .placeholder(getResources().getColor(R.color.tab_text_diveder)).error(getResources().getColor(R.color.tab_text_diveder)).into(shopInsideImages2);
                            break;
                        case 3:
                            shopInsideImage1.setVisibility(View.GONE);
                            shopInsideImages1.setVisibility(View.VISIBLE);
                            Glide.with(mContext).load(Constants.ALI_MEARCHENT_LOAD+ablunm[0]).fitCenter()
                                    .placeholder(getResources().getColor(R.color.tab_text_diveder)).error(getResources().getColor(R.color.tab_text_diveder)).into(shopInsideImages1);
                            shopInsideImage2.setVisibility(View.GONE);
                            shopInsideImages2.setVisibility(View.VISIBLE);
                            Glide.with(mContext).load(Constants.ALI_MEARCHENT_LOAD+ablunm[1]).fitCenter()
                                    .placeholder(getResources().getColor(R.color.tab_text_diveder)).error(getResources().getColor(R.color.tab_text_diveder)).into(shopInsideImages2);
                            shopInsideImage3.setVisibility(View.GONE);
                            shopInsideImages3.setVisibility(View.VISIBLE);
                            img2Key = ablunm[0];
                            img3Key = ablunm[1];
                            img4Key = ablunm[2];
                            Glide.with(mContext).load(Constants.ALI_MEARCHENT_LOAD+ablunm[2]).fitCenter()
                                    .placeholder(getResources().getColor(R.color.tab_text_diveder)).error(getResources().getColor(R.color.tab_text_diveder)).into(shopInsideImages3);
                            break;
                    }
                }
                if (shopBase.getShop_licence()!=null){
                    bookInsideImage.setVisibility(View.GONE);
                    bookInsideImages.setVisibility(View.VISIBLE);
                    img5Key = shopBase.getShop_licence();
                    Glide.with(mContext).load(Constants.ALI_MEARCHENT_LOAD+shopBase.getShop_licence()).fitCenter()
                            .placeholder(getResources().getColor(R.color.tab_text_diveder)).error(getResources().getColor(R.color.tab_text_diveder)).into(bookInsideImages);
//                    ImageUrl.setbitmap(Constants.ALI_MEARCHENT_LOAD+shopBase.getShop_licence(),bookInsideImages);
                }
                shopName.setText(shopBase.getName());
                shopCatName.setText(shopBase.getCat_name());
                shopTime.setText(shopBase.getService_time());
                shopMobile.setText(shopBase.getLegal_phone());
                shopAddress.setText(shopBase.getAddress());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void initEvents() {
        dialogSelPhoto.setListener(new DialogSelPhoto.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isCameraSel, int imgIndex) {
                if(isCameraSel) {
                    int value = 0;
                    type=1;
                    switch (imgIndex) {
                        case IMG1:
                            value = IMG1_C;
                            break;
                        case IMG2:
                            value = IMG2_C;
                            break;
                        case IMG3:
                            value = IMG3_C;
                            break;
                        case IMG4:
                            value = IMG4_C;
                            break;
                        case IMG5:
                            value = IMG5_C;
                            break;
                        case IMG6:
                            value = IMG6_C;
                            break;
                    }
                    //拍照
                    takePhotoForCamera(value);
                } else {
                    int value = 0;
                    type=2;
                    switch (imgIndex) {
                        case IMG1:
                            value = IMG1_A;
                            break;
                        case IMG2:
                            value = IMG2_A;
                            break;
                        case IMG3:
                            value = IMG3_A;
                            break;
                        case IMG4:
                            value = IMG4_A;
                            break;
                        case IMG5:
                            value = IMG5_A;
                            break;
                        case IMG6:
                            value = IMG6_A;
                            break;
                    }
                    //相册
                    takePhotoForAlbum(value);
                }
            }
        });
    }
    private void takePhotoForAlbum(int value) {
        String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE };
        requestRuntimePermission(permissions, this,value);
    }

    private void takePhotoForCamera(int value) {
        String[] permissions = { Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE };
        requestRuntimePermission(permissions, this, value);
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
        if(type==1){
            captureImage(CreateFile.FEEDBACK_PATH,value);
        }else if(type==2){
            selectImage(value);
        }
    }



    @Override
    public void onDenied(List<String> deniedPermission) {


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_back:
                    defaultFinish();
                break;
            case R.id.up_image_linear:
                dialogSelPhoto.showDialog(MyShopActivity.this,IMG1);
                break;
            case R.id.shop_inside_linear1:
                dialogSelPhoto.showDialog(MyShopActivity.this,IMG2);
                break;
            case R.id.shop_inside_linear2:
                dialogSelPhoto.showDialog(MyShopActivity.this,IMG3);
                break;
            case R.id.shop_inside_linear3:
                dialogSelPhoto.showDialog(MyShopActivity.this,IMG4);
                break;
            case R.id.book_inside_linear:
                dialogSelPhoto.showDialog(MyShopActivity.this,IMG5);
                break;
            case R.id.shop_logo:
                dialogSelPhoto.showDialog(MyShopActivity.this,IMG6);
                break;
            case R.id.shop_name_linear:
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                intent.setClass(MyShopActivity.this,ModifyShopName.class);
                bundle.putSerializable("ShopBase",shopBase);
                intent.putExtra("ShopBase",bundle);
                startActivityForResult(intent,MODIFY);
                break;
            case R.id.save_shop_info:
                save_shop_info();
                break;

        }
    }
    private boolean checkParams() {
        if(img1Key==null || img2Key==null || img5Key==null|| img6Key==null) {
            Toast.makeText(this, "信息不完整，无法上传", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void save_shop_info(){
        if(!checkParams())
            return;
        dialog.show();
        sumbitShopInfo();
    }
    private  void sumbitShopInfo(){
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        String sbule = img2Key;
        if (img3Key != null){
            sbule = sbule+","+img3Key;
        }if(img4Key != null){
            sbule = sbule+","+img4Key;
        }
        final String sbules = sbule;
        Call<BaseResponse<String>> call = userBiz.updateShopInfoName(baseApplication.mUser.token,
                shopName.getText().toString(),
                shopTime.getText().toString(),
                img6Key.toString(),
                sbule,
                img1Key.toString(),
                "1",
                img5Key.toString());
        call.enqueue(new HttpCallBack<BaseResponse<String>>() {

            @Override
            public void onResponse(Call<BaseResponse<String>> arg0,
                                   Response<BaseResponse<String>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<String> baseResponse = response.body();
                if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
                    String status = baseResponse.getStatus();
                    String data = baseResponse.getData();
                    baseApplication.mShopBase.setName(shopName.getText().toString());
                    baseApplication.mShopBase.setService_time(shopTime.getText().toString());
                    baseApplication.mShopBase.setAblum(sbules);
                    baseApplication.mShopBase.setCat_id("1");
                    baseApplication.mShopBase.setLogo(img6Key.toString());
                    baseApplication.mShopBase.setShop_headpic(img1Key.toString());
                    baseApplication.mShopBase.setShop_licence(img5Key.toString());
                    baseApplication.mCache.put(Constants.AUTH_SHOP_USER,baseApplication.mShopBase);
                    defaultFinish();
                    Toast.makeText(MyShopActivity.this, "更改店铺信息成功", Toast.LENGTH_SHORT).show();
                }else{

                }

            }

            @Override
            public void onFailure(Call<BaseResponse<String>> arg0,
                                  Throwable arg1) {
            }
        });
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
                case MODIFY: {
                    String shopNames = data.getStringExtra("shopName");
                    shopName.setText(shopNames);
                    break;
                }
                case IMG1_C: {
                    handleCameraRet(data, IMG1_C);
                    break;
                }
                case IMG2_C: {
                    handleCameraRet(data, IMG2_C);
                    break;
                }
                case IMG3_C: {
                    handleCameraRet(data, IMG3_C);
                    break;
                }
                case IMG4_C: {
                    handleCameraRet(data, IMG4_C);
                    break;
                }
                case IMG5_C: {
                    handleCameraRet(data, IMG5_C);
                    break;
                }
                case IMG6_C: {
                    handleCameraRet(data, IMG6_C);
                    break;
                }
                case IMG1_A: {
                    handleAlbumRet(data, IMG1_A);
                    break;
                }
                case IMG2_A: {
                    handleAlbumRet(data, IMG2_A);
                    break;
                }
                case IMG3_A: {
                    handleAlbumRet(data, IMG3_A);
                    break;
                }
                case IMG4_A: {
                    handleAlbumRet(data, IMG4_A);
                    break;
                }
                case IMG5_A: {
                    handleAlbumRet(data, IMG5_A);
                    break;
                }
                case IMG6_A: {
                    handleAlbumRet(data, IMG6_A);
                    break;
                }

                default:
                    break;
            }
        }
    }
    private void handleCameraRet(Intent data, int value){
        // 将保存在本地的图片取出并缩小后显示在界面上
        String imgKey = ImgSetUtil.getImgKeyString();
        String path = CreateFile.FEEDBACK_PATH + localTempImageFileName;
        String newPath = CreateFile.FEEDBACK_PATH + localTempImageFileName;
        compress(path, localTempImageFileName);
        Bitmap bitmap = FileUtil.decodeFile(path);
        switch (value) {
            case IMG1_C:
                img1Path = path;
                img1Key = imgKey;
                upHeadImage.setVisibility(View.GONE);
                upHeadImages.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(path).fitCenter()
                        .placeholder(getResources().getColor(R.color.tab_text_diveder)).error(getResources().getColor(R.color.tab_text_diveder)).into(upHeadImages);
//                upHeadImages.setImageBitmap(bitmap);
                break;
            case IMG2_C:
                img2Path = path;
                img2Key = imgKey;
                shopInsideImage1.setVisibility(View.GONE);
                shopInsideImages1.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(path).fitCenter()
                        .placeholder(getResources().getColor(R.color.tab_text_diveder))
                        .error(getResources().getColor(R.color.tab_text_diveder)).into(shopInsideImages1);
//                shopInsideImages1.setImageBitmap(bitmap);
                break;
            case IMG3_C:
                img3Path = path;
                img3Key = imgKey;
                shopInsideImage2.setVisibility(View.GONE);
                shopInsideImages2.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(path).fitCenter()
                        .placeholder(getResources().getColor(R.color.tab_text_diveder))
                        .error(getResources().getColor(R.color.tab_text_diveder)).into(shopInsideImages2);
//                shopInsideImages2.setImageBitmap(bitmap);
                break;
            case IMG4_C:
                img4Path = path;
                img4Key = imgKey;
                shopInsideImage3.setVisibility(View.GONE);
                shopInsideImages3.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(path).fitCenter()
                        .placeholder(getResources().getColor(R.color.tab_text_diveder))
                        .error(getResources().getColor(R.color.tab_text_diveder)).into(shopInsideImages3);
//                shopInsideImages3.setImageBitmap(bitmap);
                break;
            case IMG5_C:
                img5Path = path;
                img5Key = imgKey;
                bookInsideImage.setVisibility(View.GONE);
                bookInsideImages.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(path).fitCenter()
                        .placeholder(getResources().getColor(R.color.tab_text_diveder))
                        .error(getResources().getColor(R.color.tab_text_diveder)).into(bookInsideImages);
//                bookInsideImages.setImageBitmap(bitmap);
                break;
            case IMG6_C:
                img6Path = path;
                img6Key = imgKey;
                Glide.with(mContext).load(path).fitCenter()
                        .placeholder(getResources().getColor(R.color.tab_text_diveder))
                        .error(getResources().getColor(R.color.tab_text_diveder)).into(shopLogo);
//                shopLogo.setImageBitmap(bitmap);
                break;
        }
        upLoadAli(imgKey,path);
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
                            showShortToast("上传成功");
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showShortToast("上传失败");
                        }
                    });
                }
            }
        }).start();
    }

    private void handleAlbumRet(Intent data, int value){
        String fileName;
        String imgKey = ImgSetUtil.getImgKeyString();
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
                    case IMG1_A:
                            img1Path = path2;
                            img1Key = imgKey;
                            upHeadImage.setVisibility(View.GONE);
                            upHeadImages.setVisibility(View.VISIBLE);
                            Glide.with(mContext).load(path2).fitCenter()
                                .placeholder(getResources().getColor(R.color.tab_text_diveder))
                                .error(getResources().getColor(R.color.tab_text_diveder)).into(upHeadImages);
//                            upHeadImages.setImageBitmap(newBitmap);
                        break;
                    case IMG2_A:
                            img2Path = path2;
                            img2Key = imgKey;
                            shopInsideImage1.setVisibility(View.GONE);
                            shopInsideImages1.setVisibility(View.VISIBLE);
                            Glide.with(mContext).load(path2).fitCenter()
                                .placeholder(getResources().getColor(R.color.tab_text_diveder))
                                .error(getResources().getColor(R.color.tab_text_diveder)).into(shopInsideImages1);
//                            shopInsideImages1.setImageBitmap(newBitmap);
                        break;
                    case IMG3_A:
                            img3Path = path2;
                            img3Key = imgKey;
                            shopInsideImage2.setVisibility(View.GONE);
                            shopInsideImages2.setVisibility(View.VISIBLE);
                            Glide.with(mContext).load(path2).fitCenter()
                                .placeholder(getResources().getColor(R.color.tab_text_diveder))
                                .error(getResources().getColor(R.color.tab_text_diveder)).into(shopInsideImages2);
//                            shopInsideImages2.setImageBitmap(newBitmap);
                        break;
                    case IMG4_A:
                            img4Path = path2;
                            img4Key = imgKey;
                            shopInsideImage3.setVisibility(View.GONE);
                            shopInsideImages3.setVisibility(View.VISIBLE);
                            Glide.with(mContext).load(path2).fitCenter()
                                .placeholder(getResources().getColor(R.color.tab_text_diveder))
                                .error(getResources().getColor(R.color.tab_text_diveder)).into(shopInsideImages3);
//                            shopInsideImages3.setImageBitmap(newBitmap);
                        break;
                    case IMG5_A:
                            img5Path = path2;
                            img5Key = imgKey;
                            bookInsideImage.setVisibility(View.GONE);
                            bookInsideImages.setVisibility(View.VISIBLE);
                            Glide.with(mContext).load(path2).fitCenter()
                                .placeholder(getResources().getColor(R.color.tab_text_diveder))
                                .error(getResources().getColor(R.color.tab_text_diveder)).into(bookInsideImages);
//                            bookInsideImages.setImageBitmap(newBitmap);
                        break;
                    case IMG6_A:
                        img6Path = path2;
                        img6Key = imgKey;
                        Glide.with(mContext).load(path2).fitCenter()
                                .placeholder(getResources().getColor(R.color.tab_text_diveder))
                                .error(getResources().getColor(R.color.tab_text_diveder)).into(shopLogo);
//                        shopLogo.setImageBitmap(newBitmap);
                        break;
                }
                upLoadAli(imgKey,path2);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private OSS oss;


}
