package com.ddz.mearchant.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BannerBean;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.fragment.SearchDownlineOrderFragment;
import com.ddz.mearchant.fragment.SearchOnlineOrderFragment;
import com.ddz.mearchant.view.HandyTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchOrderActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_shouye,tv_fujin,tv_role,tv_geren;

    private Context mContext;
    //定义FragmentTabHost对象
    private FragmentPagerAdapter mAdapter;
    private ViewPager viewPager;
    private HandyTextView onlineTap,downTap,searchStartTime,searchEndTime;
    private View upLineShow,downLineShow;
    private List<Fragment> fragments = new ArrayList<Fragment>();
    //定义一个布局
    private ImageView returnBack;
    private LayoutInflater layoutInflater;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {SearchOnlineOrderFragment.class,SearchDownlineOrderFragment.class};

    //Tab选项卡的文字
    private String mTextviewArray[] = {"线上", "线下"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adtivity_searcher_order);
        mContext=this;
        initViews();
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
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
            case R.id.return_back:
                    defaultFinish();
                break;

        }
    }
    @Override
    protected void initViews() {
        //实例化布局对象
        onlineTap = (HandyTextView) findViewById(R.id.online_tap);
        downTap = (HandyTextView) findViewById(R.id.downline_tap);
        upLineShow = (View) findViewById(R.id.up_line_show);
        downLineShow = (View) findViewById(R.id.donw_line_show);
        returnBack = (ImageView) findViewById(R.id.return_back);


        viewPager = (ViewPager) findViewById(R.id.pager);
        onlineTap.setOnClickListener(this);
        downTap.setOnClickListener(this);
        returnBack.setOnClickListener(this);
        SearchOnlineOrderFragment olFragment = new SearchOnlineOrderFragment();
        SearchDownlineOrderFragment doFragment = new SearchDownlineOrderFragment();

        fragments.add(olFragment);
        fragments.add(doFragment);

    }
    @Override
    protected void initEvents() {

    }

    /**
     * 获取账户余额
     */
    private void login() {
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.loginRepo("15257072866:teq135");
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
//                dialog.dismiss();
//                String throwable = arg1.toString();
//                if (throwable.contains(XingYunConstant.SocketTimeoutException)
//                        || throwable.contains(XingYunConstant.ConnectException)) {
//                    MyTools.showToast(mContext, "网络状态不佳,请稍后再试");
//                }
            }
        });

    }


    private void getbanner() {
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<BannerBean>>> call = userBiz.getbanner();
        call.enqueue(new Callback<BaseResponse<List<BannerBean>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<BannerBean>>> arg0,
                                   Response<BaseResponse<List<BannerBean>>> response) {
                //    dialog.dismiss();
                BaseResponse<List<BannerBean>> baseResponse = response.body();
                if (null != baseResponse) {
                    String status = baseResponse.getStatus();
                    List<BannerBean> bannerBeanList=baseResponse.getData();
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<List<BannerBean>>> arg0,
                                  Throwable arg1) {
//                dialog.dismiss();
//                String throwable = arg1.toString();
//                if (throwable.contains(XingYunConstant.SocketTimeoutException)
//                        || throwable.contains(XingYunConstant.ConnectException)) {
//                    MyTools.showToast(mContext, "网络状态不佳,请稍后再试");
//                }
            }
        });

    }



}
