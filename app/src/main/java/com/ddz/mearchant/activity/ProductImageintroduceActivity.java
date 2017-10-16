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
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.adapter.GridViewAdapter;
import com.ddz.mearchant.aliutil.PutObjectSamples;
import com.ddz.mearchant.common.CreateFile;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.config.MyOSSConfig;
import com.ddz.mearchant.dialog.DialogSelPhoto;
import com.ddz.mearchant.interfaces.PermissionListener;
import com.ddz.mearchant.models.ImageBitmapBase;
import com.ddz.mearchant.utils.FileUtil;
import com.ddz.mearchant.utils.ImgSetUtil;
import com.ddz.mearchant.view.ClearEditText;
import com.ddz.mearchant.view.HandyTextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ProductImageintroduceActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener,PermissionListener,ActivityCompat.OnRequestPermissionsResultCallback{

    private Context mContext;
    private ClearEditText shopName;
    private GridView introduceImageGrid;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private Button previewButton;
    private GridViewAdapter gridViewAdapter;
    private int PHOTO_SELECT = 1;
    private int CARMER_SELECT = 2;
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private ArrayList<String> bitmapUrls = new ArrayList<>();
    private DialogSelPhoto dialogSelPhoto;
    private ArrayList<String> imageKeyList = new ArrayList<>();
    private ArrayList<String> imageList = new ArrayList<>();
    private ArrayList<String> imagePreviewList = new ArrayList<>();
    private String localTempImageFileName;
    private String imageBodyKey = "";//图片详情存储上传osskey
    private String imageBodyPath = "";//图片详情存储上传osspath
    private String picurl;
    private MyHandler handler;
    private OSS oss;
    private ImageBitmapBase imageBitmapBase = new ImageBitmapBase();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_image_introduce);
        mContext=this;
        oss = new OSSClient(getApplicationContext(), Constants.ALI_ENDPOINT, MyOSSConfig.getProvider(), MyOSSConfig.getOSSConfig());
        if (getIntent().getStringExtra("introduceSaveImage")!=null){
            imageBodyKey = getIntent().getStringExtra("introduceSaveImage");
            imageBodyKey = imageBodyKey+",";
        }
        if (getIntent().getStringExtra("picurl")!=null){
            picurl = getIntent().getStringExtra("picurl");
        }
        handler = new MyHandler();
        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        htvRight = (HandyTextView)findViewById(R.id.title_htv_rigit);
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        introduceImageGrid = (GridView)findViewById(R.id.add_image_instronce_girdview);
        previewButton = (Button)findViewById(R.id.preview_button);
        htvRight.setVisibility(View.VISIBLE);
        htvCenter.setText("图片介绍");
        htvRight.setText("完成");
        htvRight.setOnClickListener(this);
        htvLeft.setOnClickListener(this);
        previewButton.setOnClickListener(this);
        gridViewAdapter = new GridViewAdapter(this,bitmapUrls,10);
        introduceImageGrid.setAdapter(gridViewAdapter);
        introduceImageGrid.setOnItemClickListener(this);
        dialogSelPhoto = new DialogSelPhoto();
        gridViewAdapter.setOnDeleteImageListener(new GridViewAdapter.OnDeleteImageListener() {
            @Override
            public void click(int position) {
                bitmapUrls.remove(position);
                imagePreviewList.remove(position);
                imageKeyList.remove(position);
                gridViewAdapter.setList(bitmapUrls);
            }
        });
         if (imageBodyKey!=null && imageBodyKey.length()>0) {
             String[] image = imageBodyKey.split(",");
             if (image.length > 0 && image[0].length() > 0) {
                 for (int i = 0; i < image.length; i++) {
                     imagePreviewList.add(picurl + image[i]);
                     bitmapUrls.add(picurl + image[i]);
                     imageKeyList.add(image[i]);
                     //标志是哪个线程传数据
                 }
             }
         }
        gridViewAdapter.setList(bitmapUrls);
    }
    class MyHandler extends Handler
    {
        //接受message的信息
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if(msg.what==1)
            {
                gridViewAdapter.setList(bitmapUrls);
            }

        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == getDataSize()){
            dialogSelPhoto.showDialog(ProductImageintroduceActivity.this,1);
        }
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
            case R.id.preview_button:
                    previewIntroduce();
                break;
        }
    }
    private  void  previewIntroduce(){
        imageBitmapBase.setBitmaps(bitmaps);
        Intent intent = new Intent();
        intent.putStringArrayListExtra("imagePreviewList",imagePreviewList);
        intent.setClass(ProductImageintroduceActivity.this,PreviewImageActivity.class);
        startActivity(intent);
    }
    private  void  saveTextIntroduce(){
        if (imageKeyList!=null&&imageKeyList.size()>0){
            Intent mIntent = new Intent();
            String image = "";
            for (int i = 0; i < imageKeyList.size();i++){
                image = image + imageKeyList.get(i)+",";
            }
            String subText = image.substring(0,image.length()-1);
            mIntent.putExtra("introduceImage",subText);
            setResult(RESULT_OK,mIntent);
            defaultFinish();
        }else{
            showShortToast("您还没有选择图片");
        }

    }
    private void takePhotoForAlbum() {
        String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE };
        requestRuntimePermission(permissions, this,PHOTO_SELECT);
    }

    private void takePhotoForCamera() {
        String[] permissions = { Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE };
        requestRuntimePermission(permissions, this, CARMER_SELECT);
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
        if(value==CARMER_SELECT){
            captureImage(CreateFile.FEEDBACK_PATH);
        }else if(value==PHOTO_SELECT){
            selectImage();
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
    public void captureImage(String path) {
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
        startActivityForResult(intent, CARMER_SELECT);
    }
    /**
     * 从图库中选取图片
     */
    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent,PHOTO_SELECT);
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
            switch (requestCode) {
                case 2: {
                    handleCameraRet(data);
                    break;
                }
                case 1: {
                    handleAlbumRet(data);
                    break;
                }
                default:
                    break;
            }
        }
    }

    private void handleCameraRet(Intent data){
        // 将保存在本地的图片取出并缩小后显示在界面上
        final String imgKey1 = ImgSetUtil.getImgKeyString();
        final String path = CreateFile.FEEDBACK_PATH + localTempImageFileName;
        String newPath = CreateFile.FEEDBACK_PATH + localTempImageFileName;
        compress(path, localTempImageFileName);
        Bitmap bitmap = FileUtil.decodeFile(path);
        bitmapUrls.add(path);
        gridViewAdapter.setList(bitmapUrls);
        imageBodyKey = imageBodyKey + imgKey1 + ",";
        imageBodyPath = imageBodyPath + path + ",";
        imageList.add(path);
        imageKeyList.add(imgKey1);
        imagePreviewList.add(path);
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag2 = new PutObjectSamples(oss, Constants.ALI_PRODUCT_RECOMMEND, imgKey1, path).putObjectFromLocalFile();
                if (flag2){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showShortToast("上传成功");
                        }
                    });
                }
            }
        }).start();
    }
    private void handleAlbumRet(Intent data){
        String fileName;
        final String imgKey2 = ImgSetUtil.getImgKeyString();
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
                final String path2 = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                compress(path2, fileName);
                Bitmap newBitmap = FileUtil.decodeFile(path2);
                bitmapUrls.add(path2);
                gridViewAdapter.setList(bitmapUrls);
                imageBodyKey = imageBodyKey + imgKey2 + ",";
                imageBodyPath = imageBodyPath + path2 + ",";
                imageList.add(path2);
                imageKeyList.add(imgKey2);
                imagePreviewList.add(path2);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean flag = new PutObjectSamples(oss, Constants.ALI_PRODUCT_RECOMMEND, imgKey2, path2).putObjectFromLocalFile();
                        if (flag){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showShortToast("上传成功");
                                }
                            });
                        }
                    }
                }).start();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void initEvents() {
        dialogSelPhoto.setListener(new DialogSelPhoto.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isCameraSel, int imgIndex) {
                if(isCameraSel) {
                    takePhotoForCamera();
                    //拍照
                } else {
                    //相册
                    takePhotoForAlbum();
                }
            }
        });
    }
    private int getDataSize(){
        return  bitmapUrls == null ? 0 : bitmapUrls.size();
    }

}
