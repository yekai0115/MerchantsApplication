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
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.common.CreateFile;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.config.MyOSSConfig;
import com.ddz.mearchant.dialog.DialogSelPhoto;
import com.ddz.mearchant.dialog.avloading.DialogGraphicDetails;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.interfaces.PermissionListener;
import com.ddz.mearchant.models.AddProductBase;
import com.ddz.mearchant.models.EditProduct;
import com.ddz.mearchant.models.ProductAttar;
import com.ddz.mearchant.models.ProductEditAttr;
import com.ddz.mearchant.models.ProductType;
import com.ddz.mearchant.utils.FileUtil;
import com.ddz.mearchant.utils.GsonUtil;
import com.ddz.mearchant.utils.ImgSetUtil;
import com.ddz.mearchant.utils.NumberFormat;
import com.ddz.mearchant.utils.StringUtils;
import com.ddz.mearchant.view.ClearEditText;
import com.ddz.mearchant.view.HandyTextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class EditProductActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener,PermissionListener,ActivityCompat.OnRequestPermissionsResultCallback{

    private Context mContext;
    private HandyTextView htvCenter,htvRight,selectProductType;
    private LinearLayout htvLeft;
    private GridView addGirdviewProduct;
    private GridViewAdapter gridViewAdapter;
    private DialogSelPhoto dialogSelPhoto;
    private LinearLayout graphicDetailsLinear,addParameterLinear,mainParameterLinear;
    private Button productNextButton;
    private int type=0;
    private String introduceSaveText;
    private String introduceSaveImage;
    private ArrayList<String> introduceImages = new ArrayList<>();
    private ArrayList<String> shufflingImages = new ArrayList<>();
    private ClearEditText addProductName;
    private ArrayList<String> paths = new ArrayList<>();
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private ArrayList<String> bitmapUrls = new ArrayList<>();


    private String localTempImageFileName;
    private int PHOTO_SELECT = 1;
    private int CARMER_SELECT = 2;
    private ProductType selectType;
    private String imageHeadKey = "";//图片轮播存储上传osskey
    private String imageHeadPath = "";//图片轮播存储上传osskpath
    private String imageBodyKey = "";//图片详情存储上传osskey
    private String imageBodyPath = "";//图片详情存储上传osspath
    private ArrayList<ProductEditAttr> productAttars = new ArrayList<>();
    private ClearEditText productAttarName,productAttarPrice,productAttarNumber;
    private AddProductBase addProductBase;
    private int ClickDefaule  = 0;
    private int productId;
    private EditProduct data;
    private ArrayList<String> imageHeadKeyList = new ArrayList<>();
    private OSS oss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        mContext=this;
        dialogSelPhoto = new DialogSelPhoto();
        productId = Integer.valueOf(getIntent().getStringExtra("productId"));
        oss = new OSSClient(getApplicationContext(), Constants.ALI_ENDPOINT, MyOSSConfig.getProvider(), MyOSSConfig.getOSSConfig());
        initViews();
        initDialog();
        initEvents();

    }

    @Override
    protected void initViews() {
        htvRight = (HandyTextView)findViewById(R.id.title_htv_rigit);
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        addProductName = (ClearEditText)findViewById(R.id.add_product_name);
        selectProductType = (HandyTextView)findViewById(R.id.select_product_type);
        addGirdviewProduct = (GridView)findViewById(R.id.add_girdview_product);
        graphicDetailsLinear = (LinearLayout)findViewById(R.id.graphic_details_linear);
        productAttarName = (ClearEditText)findViewById(R.id.product_attar_name);
        productAttarPrice = (ClearEditText)findViewById(R.id.product_attar_price);
        productAttarNumber = (ClearEditText)findViewById(R.id.product_attar_number);
        addParameterLinear = (LinearLayout)findViewById(R.id.add_product_parameter_linear);
        mainParameterLinear = (LinearLayout)findViewById(R.id.main_parent_parameter_linear);
        productNextButton = (Button)findViewById(R.id.product_next_button);
        gridViewAdapter = new GridViewAdapter(this,bitmapUrls,8);
        addGirdviewProduct.setAdapter(gridViewAdapter);
        addGirdviewProduct.setOnItemClickListener(this);
        gridViewAdapter.setOnDeleteImageListener(new GridViewAdapter.OnDeleteImageListener() {
            @Override
            public void click(int position) {
                bitmapUrls.remove(position);
                imageHeadKeyList.remove(position);
                gridViewAdapter.setList(bitmapUrls);
            }
        });
        htvCenter.setText("编辑商品");
        htvLeft.setOnClickListener(this);
        graphicDetailsLinear.setOnClickListener(this);
        selectProductType.setOnClickListener(this);
        productNextButton.setOnClickListener(this);
        addParameterLinear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                    defaultFinish();
                break;
            case R.id.add_product_parameter_linear:
                    addParameterLinearView();
                break;
            case R.id.select_product_type:
                    Intent intent = new Intent();
                    intent.setClass(this,SelectProductTypeActivity.class);
                    startActivityForResult(intent,3);
                break;
            case R.id.product_next_button:
                    addProductBase = saveProductInfo();
                    if (addProductBase!=null){
                        Intent intent1 = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("addProductBase",addProductBase);
                        intent1.putExtra("addProductBase",bundle);
                        intent1.setClass(EditProductActivity.this,EditProductNextActivity.class);
                        startActivityForResult(intent1,5);
                    }
                break;
            case R.id.graphic_details_linear:
                if (ClickDefaule == 0&&introduceSaveText == null) {
                    DialogGraphicDetails dialogDetails = new DialogGraphicDetails();
                    dialogDetails.setListener(new DialogGraphicDetails.OnTextImageClickedListener() {
                        @Override
                        public void onClick(boolean isTextClicked) {
                            if (isTextClicked) {
                                Intent intent = new Intent();
                                if (introduceSaveText != null && introduceSaveText.length() > 0) {
                                    intent.putExtra("introduceText", introduceSaveText);
                                }
                                intent.setClass(EditProductActivity.this, ProductTextintroduceActivity.class);
                                startActivityForResult(intent, 4);
                            } else {
                                Intent intent2 = new Intent();
                                if (introduceSaveImage != null && introduceSaveImage.length() > 0) {
                                    intent2.putExtra("introduceSaveImage", introduceSaveImage);
                                }
                                if (introduceImages.size()>0){
                                    intent2.putStringArrayListExtra("introduceImages",introduceImages);
                                }
                                intent2.setClass(EditProductActivity.this, ProductImageintroduceActivity.class);
                                startActivityForResult(intent2, 6);
                            }

                        }
                    });
                    dialogDetails.showDialog(this, "选择图文方式");
                }else if(ClickDefaule == 1){
                    Intent intent2 = new Intent();
                    if (introduceSaveText != null && introduceSaveText.length() > 0) {
                        intent2.putExtra("introduceText", introduceSaveText);
                    }
                    intent2.setClass(EditProductActivity.this, ProductTextintroduceActivity.class);
                    startActivityForResult(intent2, 4);
                }else {
                    Intent intent2 = new Intent();
                    if (introduceSaveImage != null && introduceSaveImage.length() > 0) {
                        intent2.putExtra("introduceSaveImage", introduceSaveImage);
                    }
                    intent2.putExtra("picurl", data.getGoods_pic_uri());
                    intent2.setClass(EditProductActivity.this, ProductImageintroduceActivity.class);
                    startActivityForResult(intent2, 6);
                }
//                Intent intent = new Intent();
//                intent.setClass(this,SelectProductTypeActivity.class);
//                startActivityForResult(intent,3);
                break;
        }
    }

    int itemId  =0;
    private int position = 0;
    private int clickItemId;
    private HashMap<Integer,LinearLayout> conditions = new HashMap<>();
    private  HashMap<Integer,ArrayList<ProductAttar>> listObjMap = new HashMap<>();
    private void addParameterLinearView(){

        //每次创建一个view
        LinearLayout ll_limit = (LinearLayout) View.inflate(this, R.layout.activity_add_parameter_model, null);
        HandyTextView guige_title = (HandyTextView)ll_limit.findViewById(R.id.guige_title);
        String number = NumberFormat.foematInteger(itemId+2);
        guige_title.setText("规格"+ number);
        conditions.put(itemId,ll_limit);
        mainParameterLinear.addView(ll_limit);
        itemId++;
        position++;
    }
    private void initParameterLinearView(ProductEditAttr attr){

        //每次创建一个view
        LinearLayout ll_limit = (LinearLayout) View.inflate(this, R.layout.activity_add_parameter_model, null);
        ClearEditText attarName = (ClearEditText) ll_limit.findViewById(R.id.product_attar_name);
        ClearEditText attarPrice = (ClearEditText) ll_limit.findViewById(R.id.product_attar_price);
        ClearEditText attarNumber = (ClearEditText) ll_limit.findViewById(R.id.product_attar_number);
        HandyTextView guige_title = (HandyTextView)ll_limit.findViewById(R.id.guige_title);

        attarName.setText(attr.getAttr_value());
        attarName.setTag(attr.getGoods_attr_id());
        attarPrice.setText(attr.getAttr_price());
        attarNumber.setText(attr.getAttr_number());
        String number = NumberFormat.foematInteger(itemId+2);
        guige_title.setText("规格"+ number);
        conditions.put(itemId,ll_limit);
        mainParameterLinear.addView(ll_limit);
        itemId++;
        position++;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == getDataSize()){
            dialogSelPhoto.showDialog(EditProductActivity.this,1);
        }
    }
    private int totalAubme = 0 ;
    private AddProductBase saveProductInfo(){
        if (addProductBase==null){
            addProductBase = new AddProductBase();
        }
        productAttars.clear();
        totalAubme = 0;
        addProductBase.setGoods_id(Integer.valueOf(data.getGoods_id()));
        if (selectType!=null){
            addProductBase.setCat_id(selectType.getCat_id());
        }else{
            showShortToast("您还没有选择分类");
            return null;
        }
        if (addProductName.getText().toString()!=null&&addProductName.getText().toString().length()>0){
            addProductBase.setGoods_name(addProductName.getText().toString());
        }else{
            showShortToast("您还没有输入商品名称");
            return null;
        }
        if (imageHeadKeyList.size()>0){
            String image = "";
            for (int i = 0; i < imageHeadKeyList.size();i++){
                image = image + imageHeadKeyList.get(i)+",";
            }
            String subText = image.substring(0,image.length()-1);
            addProductBase.setGoods_img(subText);

        }else{
            showShortToast("您还没有选择轮播图");
            return null;
        }
        if (imageBodyKey!=null&&imageBodyKey.length()>0) {
            addProductBase.setDetail(imageBodyKey);
            addProductBase.setDetail_type(0);
        }
        if (introduceSaveText!=null) {
            addProductBase.setDetail(introduceSaveText);
            addProductBase.setDetail_type(1);
        }
        if(imageBodyKey.length()==0 && introduceSaveText==null){
            showShortToast("您还没有填写图文详情");
            return null;
        }
        ProductEditAttr productAttar = new ProductEditAttr();
        if (StringUtils.isBlank(productAttarPrice.getText().toString())
                ||StringUtils.isBlank(productAttarName.getText().toString())
                ||StringUtils.isBlank(productAttarNumber.getText().toString())){
            showShortToast("规格填写不完整");
            return null;
        }
        productAttar.setAttr_price(productAttarPrice.getText().toString());
        productAttar.setAttr_value(productAttarName.getText().toString());
        productAttar.setAttr_number(productAttarNumber.getText().toString());
        productAttar.setGoods_attr_id(productAttarName.getTag().toString());
        totalAubme +=(Integer.valueOf(productAttarNumber.getText().toString()));
        productAttars.add(productAttar);
        if (conditions.size()>0) {
            for (int i = 0; i < conditions.size(); i++) {
                LinearLayout val = conditions.get(i);
                ClearEditText attarName = (ClearEditText) val.findViewById(R.id.product_attar_name);
                ClearEditText attarPrice = (ClearEditText) val.findViewById(R.id.product_attar_price);
                ClearEditText attarNumber = (ClearEditText) val.findViewById(R.id.product_attar_number);
                if (!StringUtils.isBlank(attarName.getText().toString())
                        && !StringUtils.isBlank(attarPrice.getText().toString())
                        && !StringUtils.isBlank(attarNumber.getText().toString())) {
                    ProductEditAttr attar = new ProductEditAttr();
                    attar.setAttr_price(attarPrice.getText().toString());
                    attar.setAttr_value(attarName.getText().toString());
                    attar.setAttr_number(attarNumber.getText().toString());
                    if (attarName.getTag()!=null){
                        attar.setGoods_attr_id(attarName.getTag().toString());
                    }else{
                        //编辑新添加规格
                        attar.setGoods_attr_id("0");
                    }
                    totalAubme +=(Integer.valueOf(attarNumber.getText().toString()));
                    productAttars.add(attar);
                }
            }
        }
        if (totalAubme == 0){
            showShortToast("库存为0,无法添加商品");
            return null;
        }
        String json = GsonUtil.ListToJson(productAttars);
        addProductBase.setGoods_attr(json);
        addProductBase.setProfit(Integer.valueOf(data.getProfit()));
        addProductBase.setIs_on_sale(Integer.valueOf(data.getIs_on_sale()));
        if (data.getShipping_tpl_id()!=null&&data.getShipping_tpl_id().length()>0){
            addProductBase.setShipping_tpl_id(Integer.valueOf(data.getShipping_tpl_id()));
            addProductBase.setShipping_tpl_name(data.getShipping_name());
        }
        if (data.getGoods_user_cat_id()!=null&&data.getGoods_user_cat_id().length()>0){
            addProductBase.setGoods_user_cat_id(Integer.valueOf(data.getGoods_user_cat_id()));
            addProductBase.setGoods_user_cat_name(data.getUser_cate_name());
        }


        return addProductBase;
    }
    @Override
    protected void initEvents() {
        getProductById();
        dialogSelPhoto.setListener(new DialogSelPhoto.OnOkCancelClickedListener() {
            @Override
            public void onClick(boolean isCameraSel, int imgIndex) {
                if(isCameraSel) {
                    type=1;
                     takePhotoForCamera();
                    //拍照
                } else {
                    type=2;
                    //相册
                    takePhotoForAlbum();
                }
            }
        });
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
                case 3: {
                    selectType = (ProductType)data.getBundleExtra("productType").getSerializable("productType");
                    selectProductType.setText(selectType.getCat_name());
                    break;
                }
                case 4: {
                    introduceSaveText = data.getStringExtra("introduceText");
                    ClickDefaule = 1;
                    break;
                }
                case 5: {
                    addProductBase = (AddProductBase)data.getBundleExtra("addProductBase").getSerializable("addProductBase");
                    break;
                }
                case 6: {
                    introduceSaveImage = data.getStringExtra("introduceImage");
                    imageBodyKey = introduceSaveImage;
                    ClickDefaule = 2;
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
        imageHeadKey = imageHeadKey + imgKey1 + ",";
        imageHeadPath = imageHeadPath + path + ",";
        shufflingImages.add(path);
        imageHeadKeyList.add(imgKey1);
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
                imageHeadKey = imageHeadKey + imgKey2 + ",";
                imageHeadPath = imageHeadPath + path2 + ",";
                shufflingImages.add(path2);
                imageHeadKeyList.add(imgKey2);
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
    private void initEditProductInfo(final EditProduct editProduct){
        addProductName.setText(editProduct.getGoods_name());
        addProductName.setSelection(editProduct.getGoods_name().length());
        selectType = new ProductType();
        selectType.setCat_id(Integer.parseInt(editProduct.getCat_id()));
        selectProductType.setText(editProduct.getCat_name());
        for (int i = 0; i<editProduct.getImg().size();i++){
            imageHeadKeyList.add(editProduct.getImg().get(i));
            imageHeadKey = imageHeadKey + editProduct.getImg().get(i) + ",";
            bitmapUrls.add(editProduct.getGoods_pic_uri().toString()+editProduct.getImg().get(i).toString());
        }
        gridViewAdapter.setList(bitmapUrls);
        if (editProduct.getAttr().size() <= 1 && editProduct.getAttr().size()>0){
            productAttarName.setText(editProduct.getAttr().get(0).getAttr_value());
            productAttarPrice.setText(editProduct.getAttr().get(0).getAttr_price());
            productAttarNumber.setText(editProduct.getAttr().get(0).getAttr_number());
            productAttarName.setTag(editProduct.getAttr().get(0).getGoods_attr_id());
        }else if(editProduct.getAttr().size() > 1){
            productAttarName.setText(editProduct.getAttr().get(0).getAttr_value());
            productAttarPrice.setText(editProduct.getAttr().get(0).getAttr_price());
            productAttarNumber.setText(editProduct.getAttr().get(0).getAttr_number());
            productAttarName.setTag(editProduct.getAttr().get(0).getGoods_attr_id());
            for (int i = 1 ; i< editProduct.getAttr().size();i++){
                initParameterLinearView(editProduct.getAttr().get(i));
            }
        }
        if (editProduct.getDetail_type().equals("1")){
            ClickDefaule = 1;
            introduceSaveText = editProduct.getDetail();
        }else {
            ClickDefaule = 2;
            imageBodyKey = editProduct.getDetail();
            introduceSaveImage = editProduct.getDetail();
        }

    }
    /**
     * 获取账户余额
     */
    private void getProductById() {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<EditProduct>> call = userBiz.getGoodsDetail(baseApplication.mUser.token,productId);
        call.enqueue(new HttpCallBack<BaseResponse<EditProduct>>() {

            @Override
            public void onResponse(Call<BaseResponse<EditProduct>> arg0,
                                   Response<BaseResponse<EditProduct>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<EditProduct> baseResponse = response.body();
                if (null != baseResponse) {
                    String status = baseResponse.getStatus();
                    data = baseResponse.getData();
                    initEditProductInfo(data);
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<EditProduct>> arg0,
                                  Throwable arg1) {
            }
        });

    }
    private int getDataSize(){
        return  bitmapUrls == null ? 0 : bitmapUrls.size();
    }
}
