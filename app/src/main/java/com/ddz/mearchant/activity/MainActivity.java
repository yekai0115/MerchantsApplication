package com.ddz.mearchant.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.fragment.CommodityManageFragment;
import com.ddz.mearchant.fragment.EarningsFragment;
import com.ddz.mearchant.fragment.MineFragment;
import com.ddz.mearchant.fragment.PurchaseOrderFragment;
import com.ddz.mearchant.view.HandyTextView;
import com.ddz.mearchant.view.MyViewPager;

import java.util.ArrayList;


public class MainActivity extends BaseActivity {
    private TextView tv_shouye,tv_fujin,tv_role,tv_geren;
    private HandyTextView radioOrder,radioProduct,radioProfit,radioMine;
    private Context mContext;
    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    private MyViewPager viewPagerMain;
    //定义一个布局
    private LayoutInflater layoutInflater;
    private LinearLayout radioGroupMain;
    private PurchaseOrderFragment purchaseOrderFragment;
    private CommodityManageFragment commodityManageFragment;
    private EarningsFragment commodityTakeFragment;
    private MineFragment mineFragment;
    private ViewPager viewPager;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.home_indent,R.drawable.home_manage,R.drawable.home_obligation,
            R.drawable.home_my};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initViews() {

        viewPagerMain =  (MyViewPager)findViewById(R.id.view_pager_main);
        radioGroupMain =  (LinearLayout)findViewById(R.id.radio_group_main);
        radioOrder =  (HandyTextView)findViewById(R.id.radio_order);
        radioProduct =  (HandyTextView)findViewById(R.id.radio_product);
        radioProfit =  (HandyTextView)findViewById(R.id.radio_profit);
        radioMine =  (HandyTextView)findViewById(R.id.radio_mine);
        fragments.clear();
        purchaseOrderFragment = new PurchaseOrderFragment();
        commodityManageFragment = new CommodityManageFragment();
        commodityTakeFragment = new EarningsFragment();
        mineFragment = new MineFragment();
        fragments.add(purchaseOrderFragment);
        fragments.add(commodityManageFragment);
        fragments.add(commodityTakeFragment);
        fragments.add(mineFragment);
        radioOrder.setSelected(true);
        radioProduct.setSelected(false);
        radioProfit.setSelected(false);
        radioMine.setSelected(false);
        viewPagerMain.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        radioOrder.setSelected(true);
                        radioProduct.setSelected(false);
                        radioProfit.setSelected(false);
                        radioMine.setSelected(false);
                        viewPagerMain.setCurrentItem(0);
                        radioOrder.setTextColor(getResources().getColor(R.color.tab_line_color));
                        radioProduct.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioProfit.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioMine.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        break;
                    case 1://附近
                        radioOrder.setSelected(false);
                        radioProduct.setSelected(true);
                        radioProfit.setSelected(false);
                        radioMine.setSelected(false);
                        viewPagerMain.setCurrentItem(1);
                        radioOrder.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioProduct.setTextColor(getResources().getColor(R.color.tab_line_color));
                        radioProfit.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioMine.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        break;
                    case 2://角色
                        radioOrder.setSelected(false);
                        radioProduct.setSelected(false);
                        radioProfit.setSelected(true);
                        radioMine.setSelected(false);
                        viewPagerMain.setCurrentItem(2);
                        radioOrder.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioProduct.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioProfit.setTextColor(getResources().getColor(R.color.tab_line_color));
                        radioMine.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        break;
                    case 3://我的
                        radioOrder.setSelected(false);
                        radioProduct.setSelected(false);
                        radioProfit.setSelected(false);
                        radioMine.setSelected(true);
                        viewPagerMain.setCurrentItem(3);
                        radioOrder.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioProduct.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioProfit.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioMine.setTextColor(getResources().getColor(R.color.tab_line_color));
                        break;
                }
                viewPagerMain.setCurrentItem(position);
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
        viewPagerMain.setAdapter(fragmentPagerAdapter);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.radio_order://首页
                        radioOrder.setSelected(true);
                        radioProduct.setSelected(false);
                        radioProfit.setSelected(false);
                        radioMine.setSelected(false);
                        viewPagerMain.setCurrentItem(0);
                        radioOrder.setTextColor(getResources().getColor(R.color.tab_line_color));
                        radioProduct.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioProfit.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioMine.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        break;
                    case R.id.radio_product://附近
                        radioOrder.setSelected(false);
                        radioProduct.setSelected(true);
                        radioProfit.setSelected(false);
                        radioMine.setSelected(false);
                        viewPagerMain.setCurrentItem(1);
                        radioOrder.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioProduct.setTextColor(getResources().getColor(R.color.tab_line_color));
                        radioProfit.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioMine.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        break;
                    case R.id.radio_profit://角色
                        radioOrder.setSelected(false);
                        radioProduct.setSelected(false);
                        radioProfit.setSelected(true);
                        radioMine.setSelected(false);
                        viewPagerMain.setCurrentItem(2);
                        radioOrder.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioProduct.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioProfit.setTextColor(getResources().getColor(R.color.tab_line_color));
                        radioMine.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        break;
                    case R.id.radio_mine://我的
                        radioOrder.setSelected(false);
                        radioProduct.setSelected(false);
                        radioProfit.setSelected(false);
                        radioMine.setSelected(true);
                        viewPagerMain.setCurrentItem(3);
                        radioOrder.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioProduct.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioProfit.setTextColor(getResources().getColor(R.color.tab_line_gray));
                        radioMine.setTextColor(getResources().getColor(R.color.tab_line_color));
                        break;
                }
            }
        };
        radioOrder.setOnClickListener(onClickListener);
        radioProduct.setOnClickListener(onClickListener);
        radioProfit.setOnClickListener(onClickListener);
        radioMine.setOnClickListener(onClickListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }else
            return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void initEvents() {

    }



}
