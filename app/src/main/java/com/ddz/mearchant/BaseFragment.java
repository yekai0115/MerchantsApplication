package com.ddz.mearchant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.ddz.mearchant.dialog.LoadingDialog;
import com.google.gson.Gson;

public abstract class BaseFragment extends Fragment {
	private TranslateAnimation mShowAction;
	private TranslateAnimation mHiddenAction;
	protected Context mContext;
	protected Activity mActivity;
	protected View mView;
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected ProgressDialog progressDialog;
	protected float mDensity;
	protected Gson gsonParser=new Gson();
	protected LoadingDialog dialog;
	protected BaseApplication baseApplication = BaseApplication.getInstance();
	public BaseFragment() {
		super();
	}
	public BaseFragment(Activity activity, Context context) {
		mActivity = activity;
		mContext = context;
		/**
		 * 获取屏幕宽度、高度、密度
		 */
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		mDensity = metric.density;
		
		
		mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,   
                 Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,   
                 -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);   
        mShowAction.setDuration(500); 
        
        mHiddenAction =  new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f); 
        mHiddenAction.setDuration(500);  
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initViews();
		initEvents();
		init();
		return mView;
	}
	protected void initDialog() {
		dialog = new LoadingDialog(getActivity(), R.style.dialog, "请稍候...");
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
	}
	protected abstract void initViews();

	protected abstract void initEvents();

	protected abstract void init();
	public View findViewById(int id) {
		return mView.findViewById(id);
	}
	protected void createProgressDialog(String text) {
		progressDialog = new ProgressDialog(getActivity());
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
	/** 通过Class跳转界面 **/
	protected void startActivity(Class<?> cls) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(getActivity(), cls);
		startActivity(intent);
	}
	
}
