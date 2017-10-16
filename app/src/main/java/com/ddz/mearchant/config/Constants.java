package com.ddz.mearchant.config;

import android.os.Environment;

/**
 * Created by Administrator on 2017/6/10 0010.
 */

public class Constants {
    public static final String IMG_PATH = Environment.getExternalStorageDirectory().toString() + "/DCIM/ddz/meadchant";
    public static final String AUTH_USER="authUser";
    public static final String AUTH_SHOP_USER="shopUser";
    public static final String CLOSE_ON_KEYDOWN="close_on_keydown";
    public static final String T_OK="T_OK";
    public static final String NO_SESSION="会话过期";

//    public static final String WEB_SERVICE_BASE="http://10.0.0.12/index-3.php/";//本地测试
    public static final String WEB_SERVICE_BASE="https://app.ddzyigou.com/index-3.php/";
    public static final int DEF_IMG_W = 800;
    public static final int DEF_IMG_H = 640;
    /**阿里云EndPoint*/
    public static final String ALI_MEARCHENT_ENDPOINT = "http://oss-cn-shenzhen.aliyuncs.com";//店铺
    public static final String ALI_ENDPOINT = "http://oss-cn-shanghai.aliyuncs.com";//商品
    public static final String ALI_MEARCHENT_LOAD = "http://ddz-public-shop.oss-cn-shenzhen.aliyuncs.com/";
    public static final String ALI_PRODUCT_LOAD = "http://ddz-public-goods.oss-cn-shanghai.aliyuncs.com/";
    public static final String ALI_KEYID = "LTAIsxUPdsSknfT0";
    public static final String ALI_KEYSECRET = "kUDMQin1Han5PrJIyvXnmifTjWCk6g";

    public static final String ALI_BUCKET_AUTH = "ddz-private-personal"; // 实名认证
    public static final String ALI_BUCKET_BANK = "ddz-private-bank"; // 银行卡
    /**上传下载的bucketName*/

    public static final String ALI_BUCKET_RECOMMEND = "ddz-public-shop"; // 商家目录
    public static final String ALI_PRODUCT_RECOMMEND = "ddz-public-goods"; // 商品目录
    public static final String ERR_NOREGISTER = "ERR_NOREGISTER";
    public static final String ERR_VERIFYCODE = "ERR_VERIFYCODE";
    public static final String T_ERR_AUTH = "T_ERR_AUTH";//登录过期
    public static final int PAGE = 10; //每次查询11条
    public static final String COM_PHONE_NUM = "075566690068";
    public static final String COM_ABOUT = "http://www2-2.ddzyigou.com/public/about";


}
