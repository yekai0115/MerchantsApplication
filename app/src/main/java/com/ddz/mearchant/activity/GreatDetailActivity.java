package com.ddz.mearchant.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.GreatDetail;
import com.ddz.mearchant.models.GreatDetailBase;
import com.ddz.mearchant.view.HandyTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/6/26 0026.
 */

public class GreatDetailActivity extends BaseActivity implements View.OnClickListener{
    private Context mContext;
    private HandyTextView htvCenter,greatDetaileTotal;
    private LinearLayout htvLeft;
    private ListView greatDetailList;
    private List<GreatDetail> greatDetails = new ArrayList<>();
    private GreatDetailAdapter greatDetailAdapter = new GreatDetailAdapter();
    private static int refWidth, refHeight, baseWidth;
    private static double perWidth;
    private RelativeLayout relativeLayout;
    private double maxValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_great_detail);
        mContext=this;
        initViews();
        initDialog();
        initEvents();
    }

    @Override
    protected void initViews() {
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout) findViewById(R.id.title_htv_left);
        greatDetaileTotal = (HandyTextView)findViewById(R.id.great_detaile_total);
        greatDetailList = (ListView)findViewById(R.id.great_detail_list);
        relativeLayout = (RelativeLayout) findViewById(R.id.ael_relative);

        htvCenter.setText("赞章收益明细");
        htvLeft.setOnClickListener(this);
        greatDetailAdapter = new GreatDetailAdapter();
        greatDetailList.setAdapter(greatDetailAdapter);
        relativeLayout.post(new Runnable() {
            @Override
            public void run() {
                refWidth = relativeLayout.getWidth();
                refHeight = relativeLayout.getHeight();
                baseWidth = 2*refWidth/3;
            }
        });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                defaultFinish();
                break;
        }
    }
    private void findMaxValue() {
        maxValue = 0d;

        for (GreatDetail item : greatDetails) {

            double curV;
            if(item.getMoney().equals("")) {
                curV = 0;
            } else {
                curV = Double.parseDouble(item.getMoney());
            }
            if(curV > maxValue) {
                maxValue = curV;
            }
        }
        if(maxValue <= 0) {
            maxValue = 1;
        }
        perWidth = (double) (refWidth-baseWidth)/maxValue;
    }
    @Override
    protected void initEvents() {
        getGreatDtailList();
    }
    private void getGreatDtailList(){
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<GreatDetailBase>> call = userBiz.getshopPraiseDetail(baseApplication.mUser.token);
        call.enqueue(new HttpCallBack<BaseResponse<GreatDetailBase>>() {
            @Override
            public void onResponse(Call<BaseResponse<GreatDetailBase>> arg0,
                                   Response<BaseResponse<GreatDetailBase>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<GreatDetailBase> baseResponse = response.body();
                if (null != baseResponse) {
                    String status = baseResponse.getStatus();
                    if (status.equals(Constants.T_OK)){
                        GreatDetailBase data = baseResponse.getData();
                        if (data!=null){
                            greatDetails.addAll(data.getDetail());
                            greatDetaileTotal.setText(data.getTotal_praise());
                            findMaxValue();
                            greatDetailAdapter.notifyDataSetChanged();
                        }
                    }else{
                        //无数据处理
                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<GreatDetailBase>> arg0,
                                  Throwable arg1) {
            }
        });
    }

    class GreatDetailAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        @Override
        public int getCount() {
            return greatDetails.size();
        }

        @Override
        public Object getItem(int position) {
            return greatDetails.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView = View.inflate(GreatDetailActivity.this,R.layout.adapter_great_detail_list,null);
                holder = new ViewHolder();
                holder.ielRelative = (RelativeLayout) convertView.findViewById(R.id.iel_relative);
                holder.ielLeft = (HandyTextView) convertView.findViewById(R.id.iel_tv_left);
                holder.ielRight = (HandyTextView) convertView.findViewById(R.id.iel_tv_right);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            if(position == 0) {
                holder.ielRelative.setBackgroundColor(getResources().getColor(R.color.tab_line_color));
            } else {
                holder.ielRelative.setBackgroundColor(getResources().getColor(R.color.bg_color_peter_river));
            }
            int width = (int) (perWidth * Double.parseDouble(greatDetails.get(position).getMoney()));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width+baseWidth , refHeight);
            layoutParams.setMargins(30,0,30,0);
            holder.ielRelative.setLayoutParams(layoutParams);
            holder.ielLeft.setText(greatDetails.get(position).getAdd_time());
//            holder.ielLeft.setText(CommonUtils.sdfDatePoint.format(greatDetails.get(position).getAdd_time()));
            holder.ielRight.setText(greatDetails.get(position).getMoney());
            return convertView;
        }
    }

    public  class ViewHolder{
        private RelativeLayout ielRelative;
        private HandyTextView ielLeft;
        private HandyTextView ielRight;

    }
}