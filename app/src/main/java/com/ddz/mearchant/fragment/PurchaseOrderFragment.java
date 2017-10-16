package com.ddz.mearchant.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddz.mearchant.R;
import com.ddz.mearchant.activity.SearchOrderActivity;
import com.ddz.mearchant.view.HandyTextView;

import java.util.ArrayList;
import java.util.List;


public class PurchaseOrderFragment extends Fragment implements View.OnClickListener{
	//定义一个布局
	private LayoutInflater layoutInflater;
	//定义FragmentTabHost对象
	private FragmentTabHost mTabHost;
	private View view;
	private FragmentPagerAdapter mAdapter;
	private ViewPager viewPager;
	private HandyTextView onlineTap,downTap,searchOrderText;
	private View upLineShow,downLineShow;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	//定义数组来存放Fragment界面
	private Class fragmentArray[] = {OnlineOrderFragment.class,DownlineOrderFragment.class};

	//Tab选项卡的文字
	private String mTextviewArray[] = {"线上", "线下"};
	private boolean isCreated = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isCreated = true;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.purchase_order_fragment, container, false);
		initViews();
		return view;

	}
	protected void initViews() {
		onlineTap = (HandyTextView)view.findViewById(R.id.online_tap);
		downTap = (HandyTextView)view.findViewById(R.id.downline_tap);
		upLineShow = (View)view.findViewById(R.id.up_line_show);
		downLineShow = (View)view.findViewById(R.id.donw_line_show);
		searchOrderText = (HandyTextView)view.findViewById(R.id.downline_order_in);

		viewPager = (ViewPager) view.findViewById(R.id.pager);
		onlineTap.setOnClickListener(this);
		downTap.setOnClickListener(this);

		OnlineOrderFragment olFragment = new OnlineOrderFragment();
		DownlineOrderFragment doFragment = new DownlineOrderFragment();
		fragments.clear();
		fragments.add(olFragment);
		fragments.add(doFragment);
		searchOrderText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(),SearchOrderActivity.class);
				startActivity(intent);
			}
		});
		mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
			@Override
			public Fragment getItem(int position) {
				return fragments.get(position);
			}

			@Override
			public int getCount() {
				return fragments.size();
			}
		};
		viewPager.setAdapter(mAdapter);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			private int currentIndex;
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				switch (position){
					case 0:
						onlineTap.setTextColor(getResources().getColor(R.color.bg_color));
						downTap.setTextColor(getResources().getColor(R.color.bg_color));
						upLineShow.setBackgroundDrawable(getResources().getDrawable(R.drawable.tap_line_show));
						downLineShow.setBackgroundColor(getResources().getColor(R.color.toming));
						break;
					case 1:
						downTap.setTextColor(getResources().getColor(R.color.bg_color));
						onlineTap.setTextColor(getResources().getColor(R.color.bg_color));
						upLineShow.setBackgroundColor(getResources().getColor(R.color.toming));
						downLineShow.setBackgroundDrawable(getResources().getDrawable(R.drawable.tap_line_show));
						break;
				}
				currentIndex = position;
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}
	@Override
	public void onResume() {
		super.onResume();

	}
	
	@Override
	public void onPause() {
		super.onPause();

	}
	protected boolean isSelect = false;
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (!isCreated) {
			return;
		}

		if (isVisibleToUser) {
			initViews();
		}else {
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.online_tap:
				upLineShow.setBackgroundDrawable(getResources().getDrawable(R.drawable.tap_line_show));
				downLineShow.setBackgroundColor(getResources().getColor(R.color.toming));
				viewPager.setCurrentItem(0);
				break;
			case R.id.downline_tap:
				downLineShow.setBackgroundDrawable(getResources().getDrawable(R.drawable.tap_line_show));
				upLineShow.setBackgroundColor(getResources().getColor(R.color.toming));
				viewPager.setCurrentItem(1);
				break;
		}
	}
}
