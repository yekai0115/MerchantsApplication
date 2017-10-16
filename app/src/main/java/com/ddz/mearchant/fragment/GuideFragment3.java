package com.ddz.mearchant.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddz.mearchant.BaseApplication;
import com.ddz.mearchant.R;
import com.ddz.mearchant.activity.LoginActivity;
import com.ddz.mearchant.activity.MainActivity;


public class GuideFragment3 extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final View rootView = inflater.inflate(R.layout.guide_fragment_3, container, false);
		TextView tv_go = (TextView) rootView.findViewById(R.id.tv_go);
		tv_go.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent;
				if (BaseApplication.getInstance().mUser != null && BaseApplication.getInstance().mUser.token != null){
					intent=new Intent(getActivity(), MainActivity.class);
				}else{
					intent=new Intent(getActivity(), LoginActivity.class);
				}
				getActivity().startActivity(intent);
				getActivity().finish();
			}
		});
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);



		
		
	}
	
	
	@Override
	public void onResume() {
		super.onResume();

	}
	
	@Override
	public void onPause() {
		super.onPause();

	}
}
