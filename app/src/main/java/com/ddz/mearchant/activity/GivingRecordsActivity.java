package com.ddz.mearchant.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.fragment.GivingOutRecordFragment;
import com.ddz.mearchant.fragment.GivingReceiveRecordFragment;
import com.ddz.mearchant.view.HandyTextView;

import java.util.ArrayList;


public class GivingRecordsActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext;
    private HandyTextView htvCenter,htvRight,platMes,custMes;
    private LinearLayout htvLeft;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private GivingOutRecordFragment givingOutRecordFragment;
    private GivingReceiveRecordFragment givingReceiveRecordFragment;
    private ViewPager viewPager;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private View platLineShow,custLineShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measage_fragment);
        mContext=this;
        initViews();
    }

    @Override
    protected void initViews() {
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        viewPager =  (ViewPager)findViewById(R.id.pager);
        platMes = (HandyTextView)findViewById(R.id.plat_mes);
        custMes = (HandyTextView)findViewById(R.id.cust_mes);
        platLineShow = (View)findViewById(R.id.plat_line_show);
        custLineShow = (View)findViewById(R.id.cust_line_show);
        htvCenter.setText("赠送记录");
        platMes.setText("赠送出去");
        custMes.setText("收到赠送");
        htvLeft.setOnClickListener(this);
        givingOutRecordFragment = new GivingOutRecordFragment();
        givingReceiveRecordFragment = new GivingReceiveRecordFragment();
        fragments.add(givingOutRecordFragment);
        fragments.add(givingReceiveRecordFragment);
        platMes.setOnClickListener(this);
        custMes.setOnClickListener(this);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        platMes.setTextColor(getResources().getColor(R.color.tab_line_color));
                        custMes.setTextColor(getResources().getColor(R.color.order_text_color));
                        platLineShow.setBackgroundDrawable(getResources().getDrawable(R.drawable.tap_line_show2));
                        custLineShow.setBackgroundColor(getResources().getColor(R.color.toming));
                        break;
                    case 1:
                        platMes.setTextColor(getResources().getColor(R.color.order_text_color));
                        custMes.setTextColor(getResources().getColor(R.color.tab_line_color));
                        custLineShow.setBackgroundDrawable(getResources().getDrawable(R.drawable.tap_line_show2));
                        platLineShow.setBackgroundColor(getResources().getColor(R.color.toming));
                        break;
                }
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        viewPager.setAdapter(fragmentPagerAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                defaultFinish();
                break;
            case R.id.plat_mes:
                platMes.setTextColor(getResources().getColor(R.color.tab_line_color));
                custMes.setTextColor(getResources().getColor(R.color.order_text_color));
                platLineShow.setBackgroundDrawable(getResources().getDrawable(R.drawable.tap_line_show2));
                custLineShow.setBackgroundColor(getResources().getColor(R.color.toming));
                viewPager.setCurrentItem(0);
                break;
            case R.id.cust_mes:
                platMes.setTextColor(getResources().getColor(R.color.order_text_color));
                custMes.setTextColor(getResources().getColor(R.color.tab_line_color));
                custLineShow.setBackgroundDrawable(getResources().getDrawable(R.drawable.tap_line_show2));
                platLineShow.setBackgroundColor(getResources().getColor(R.color.toming));
                viewPager.setCurrentItem(1);
                break;
        }
    }
    @Override
    protected void initEvents() {

    }
}
