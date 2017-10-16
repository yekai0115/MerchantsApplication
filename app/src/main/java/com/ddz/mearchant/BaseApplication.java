package com.ddz.mearchant;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import com.ddz.mearchant.activity.LoginActivity;
import com.ddz.mearchant.common.CreateFile;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.crash.CrashHandler;
import com.ddz.mearchant.models.ShopBase;
import com.ddz.mearchant.models.User;

import org.afinal.simple.cache.ACache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import cn.smssdk.SMSSDK;



/**
 * Created by wyouflf on 15/10/28.
 */
public class BaseApplication extends Application {
	private static final String TAG=BaseApplication.class.getName();
	public static BaseApplication mApplication;
	public static ExecutorService TASK_EXECUTOR;
	public static ExecutorService FIXED_EXECUTOR;
	
	private static final int PORT = 1234;
	public static final String APP_SECRET = "yunlong";
	public static final String APP_ID="com.sunray.yunlong";
	/**保存添加到购物车的数据*/
	public static int arrayList_cart_id=0;
	/**保存添加到购物车的数据*/
	public static ArrayList<HashMap<String, Object>> arrayList_cart=new ArrayList<HashMap<String,Object>>();
	/**保存购物车中选择的商品的总价数据*/
    public static float Allprice_cart=0;
	// 缓存相关
    public ACache mCache;
	public static BaseApplication getInstance() {
		return mApplication;
	}
	static{
		TASK_EXECUTOR = (ExecutorService) Executors.newCachedThreadPool(); 
		FIXED_EXECUTOR = (ExecutorService) Executors.newFixedThreadPool(1); //线程池中只有一个线程，任务排队执行 
	}
	public User mUser = new User();// 用户信息
	public ShopBase mShopBase = new ShopBase();// 商户信息
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        mCache = ACache.get(getApplicationContext());
        if(null!=mCache.getAsObject(Constants.AUTH_USER))
			mUser = (User)mCache.getAsObject(Constants.AUTH_USER);
		if(null!=mCache.getAsObject(Constants.AUTH_SHOP_USER))
			mShopBase = (ShopBase)mCache.getAsObject(Constants.AUTH_SHOP_USER);
        CrashHandler.getInstance().init(this,new File(CreateFile.CRASH_PATH), null);
        CreateFile creat = new CreateFile(mApplication);
        creat.Create();
    }

    public void startLoginActivity() {
		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		intent.putExtra(Constants.CLOSE_ON_KEYDOWN, true);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Toast.makeText(getApplicationContext(), "登录过期,请重新登录", Toast.LENGTH_SHORT).show();
		startActivity(intent);
	}
	/** 短暂显示Toast提示(来自res) **/
	public void showShortToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
	}

	/** 短暂显示Toast提示(来自String) **/
	public void showShortToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
