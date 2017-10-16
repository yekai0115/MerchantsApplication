package com.ddz.mearchant;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.ddz.mearchant.activity.LoginActivity;
import com.ddz.mearchant.activity.MainActivity;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.common.ActivityManager;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.dialog.LoadingDialog;
import com.ddz.mearchant.models.User;
import com.ddz.mearchant.view.ClearEditText;
import com.ddz.mearchant.view.HandyTextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseActivity extends FragmentActivity implements OnClickListener {
	private static final String TAG = BaseActivity.class.getName();
	protected List<AsyncTask<Void, Void, String>> mAsyncTasks = new ArrayList<AsyncTask<Void, Void, String>>();
	public ActivityManager activityManager = ActivityManager.getInstance();
	private static final int DOWN_ERROR = 0;
	protected static final int FLAG_CHOOSE_IMG = 5;
	protected Gson gsonParser = new GsonBuilder().create();
	protected ProgressDialog progressDialog;
	protected HandyTextView vTitleLeft;
	protected HandyTextView vTitleCenter;
	protected HandyTextView vTitleRight;
	protected ImageView vTitleRightImage;
	private boolean isExit;
	private boolean isRememberUsername = false;
	private String content = "";
	private Boolean isManual = false;
	private JSONArray array;
	private int newVerCode = 0;
	private String newVerName = "";
	private Boolean isNeedUpdate = true;
	protected LoadingDialog dialog = null;
	protected BaseApplication baseApplication = BaseApplication.getInstance();
	// 请求相关
	protected Gson gson;
	protected User mUser;
	/**
	 * 屏幕的宽度、高度、密度
	 */
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected float mDensity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {// 如果是被系统回收过,此对象不会为null
			this.onRestoreInstanceState(savedInstanceState);
		}
		activityManager.add(this);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		mDensity = metric.density;
	}

	protected void hideKeyboard() {
		((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	protected boolean checkLoginedUser(Class<?> toClass){
		if(baseApplication.mUser==null||baseApplication.mUser.userId==null||baseApplication.mUser.userId==0){
			Intent intent=new Intent(this,LoginActivity.class);
			intent.putExtra(Constants.CLOSE_ON_KEYDOWN, true);
			startActivity(intent);
			defaultFinish();
			return false;
		}
		return true;
	}
	protected void openPhotos(){//打开相册
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, FLAG_CHOOSE_IMG);
	}
	protected void initDialog() {
		dialog = new LoadingDialog(this, R.style.dialog, "请稍候...");
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
	}
	protected void createProgressDialog(String text) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		if (null == text || text.trim().length() <= 0)
			text = getString(R.string.loading);
		progressDialog.setMessage(text);

		progressDialog.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface arg0) {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				Log.d("map", "cancel retrieve location");
			}
		});

	}
	@Override
	public void onSaveInstanceState(Bundle outState){//当activity被系统回收调用
		super.onSaveInstanceState(outState);
	    outState.putSerializable("mCustomer", baseApplication.mUser);
	}
	    
	@Override  
	public void onRestoreInstanceState(Bundle savedInstanceState){//当activity被恢复时调用
	    super.onRestoreInstanceState(savedInstanceState);
	    baseApplication.mUser = (User) savedInstanceState.getSerializable("mCustomer");
	}
	
	@Override
	protected void onDestroy() {
		clearAsyncTask();
		activityManager.remove(this);
		super.onDestroy();
	}

	/** 初始化视图 **/
	protected abstract void initViews();

	/** 初始化事件 **/
	protected abstract void initEvents();

	protected void putAsyncTask(AsyncTask<Void, Void, String> asyncTask) {
		mAsyncTasks.add(asyncTask.executeOnExecutor(BaseApplication.TASK_EXECUTOR));
	}

	protected void clearAsyncTask() {
		Iterator<AsyncTask<Void, Void, String>> iterator = mAsyncTasks.iterator();
		while (iterator.hasNext()) {
			AsyncTask<Void, Void, String> asyncTask = iterator.next();
			if (asyncTask != null && !asyncTask.isCancelled()) {
				asyncTask.cancel(true);
			}
		}
		mAsyncTasks.clear();
	}

	/** 短暂显示Toast提示(来自res) **/
	protected void showShortToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
	}

	/** 短暂显示Toast提示(来自String) **/
	protected void showShortToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/** 长时间显示Toast提示(来自res) **/
	protected void showLongToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show();
	}

	/** 长时间显示Toast提示(来自String) **/
	protected void showLongToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	/** Debug输出Log日志 **/
	protected void showLogDebug(String tag, String msg) {
		Log.d(tag, msg);
	}

	/** Error输出Log日志 **/
	protected void showLogError(String tag, String msg) {
		Log.e(tag, msg);
	}

	/** 通过Class跳转界面 **/
	protected void startActivity(Class<?> cls) {
		startActivity(cls, null);
	}

	/** 含有Bundle通过Class跳转界面 **/
	protected void startActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	/** 通过Action跳转界面 **/
	protected void startActivity(String action) {
		startActivity(action, null);
	}

	/** 含有Bundle通过Action跳转界面 **/
	protected void startActivity(String action, Bundle bundle) {
		Intent intent = new Intent();
		intent.setAction(action);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	/** 默认退出 **/
	protected void defaultFinish() {
		super.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
				defaultFinish();
			return false;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {// 这种方式捕捉已经失效
			moveTaskToBack(false);
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void exit() {
		if (!isExit) {
			isExit = true;
			showShortToast("再按一次退出");
			exitHandler.sendEmptyMessageDelayed(0, 2000);
		} else {// 退出当前帐号到登陆页面再按两次返回会执行这里
			isExit = false;
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			activityManager.exit();
		}
	}

	Handler exitHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}

	};

	protected String getRunningActivityName() {
		android.app.ActivityManager activityManager = (android.app.ActivityManager) getSystemService(
				Context.ACTIVITY_SERVICE);
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
		return runningActivity;
	}

	/**
	 * 验证是否为空
	 * 
	 * @param editText
	 * @return
	 */
	protected boolean isNull(ClearEditText editText) {
		String text = editText.getText().toString().trim();
		if (text != null && text.length() > 0) {
			return false;
		}
//		editText.requestFocus();
		return true;
	}

	/**
	 * 验证是否为空
	 * 
	 * @param
	 * @return
	 */
	protected boolean isNull(HandyTextView textview) {
		String text = textview.getText().toString().trim();
		if (text != null && text.length() > 0) {
			return false;
		}
		return true;
	}

	private void loginToMain() {
		activityManager.closeAllActivity();
		startActivity(MainActivity.class);
		defaultFinish();
	}

	/**
	 * 登录
	 */
	private void login() {
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<String>> call = userBiz.loginRepo("18813904075:123456789");
		call.enqueue(new Callback<BaseResponse<String>>() {

			@Override
			public void onResponse(Call<BaseResponse<String>> arg0,
								   Response<BaseResponse<String>> response) {
				//    dialog.dismiss();
				BaseResponse<String> baseResponse = response.body();
				if (null != baseResponse) {
					String status = baseResponse.getStatus();
					String data = baseResponse.getData();
					baseApplication.mUser.setLoginId("18813904075");
					baseApplication.mUser.setPassword("123456789");
					baseApplication.mUser.setToken(data);
					baseApplication.mCache.put(Constants.AUTH_USER, baseApplication.mUser);

				}

			}

			@Override
			public void onFailure(Call<BaseResponse<String>> arg0,
								  Throwable arg1) {
			}
		});

	}



	public void remenber(Boolean isRemebmerPwd, String loginId, String password) {
		if (isRemebmerPwd) {
			isRememberUsername = true;
			baseApplication.mCache.put("remember_name", loginId);
			baseApplication.mCache.put("remember_password", password);
		} else {
			baseApplication.mCache.put("remember_name", loginId);
			isRememberUsername = false;
		}
		baseApplication.mCache.put("isRememberUsername", String.valueOf(isRememberUsername));
	}


	@Override
	public void onClick(DialogInterface dialog, int which) {
	}

	/**
	 * 单选列表类型的弹出框
	 * 
	 * @param cancelable
	 *            设置是否能让用户主动取消弹窗
	 * 
	 * @param title
	 *            弹窗标题
	 * @param items
	 *            弹窗的列表数据源
	 * @param selectListener
	 *            弹窗列表选择事件
	 */
	public void showAlertDialog(boolean cancelable, String title,
			String items[], OnClickListener selectListener) {
		new AlertDialog.Builder(this)
				// 设置按系统返回键的时候按钮弹窗不取消
				.setCancelable(cancelable).setTitle(title)
				.setItems(items, selectListener).show();
	}
	/**
	 * 带点击事件的双按钮AlertDialog
	 * 
	 * @param title
	 *            弹框标题
	 * @param message
	 *            弹框消息内容
	 * @param positiveButton
	 *            弹框第一个按钮的文字
	 * @param negativeButton
	 *            弹框第二个按钮的文字
	 * @param positiveClickListener
	 *            弹框第一个按钮的单击事件
	 * @param negativeClickListener
	 *            弹框第二个按钮的单击事件
	 */
	public void showAlertDialog(String title, String message,
			String positiveButton, String negativeButton,
			OnClickListener positiveClickListener,
			OnClickListener negativeClickListener) {
		new AlertDialog.Builder(this).setCancelable(false).setTitle(title)
				.setMessage(message)
				.setPositiveButton(positiveButton, positiveClickListener)
				.setNegativeButton(negativeButton, negativeClickListener)
				.show();

	}
}
