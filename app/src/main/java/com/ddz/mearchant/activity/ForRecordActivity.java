package com.ddz.mearchant.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.ForRecordBase;
import com.ddz.mearchant.view.HandyTextView;
import com.ddz.mearchant.view.PullToRefreshLayout;
import com.ddz.mearchant.view.PullableListView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/6/26 0026.
 */

public class ForRecordActivity extends BaseActivity implements View.OnClickListener,PullToRefreshLayout.OnRefreshListener{

    private Context mContext;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private List<ForRecordBase> forRecordBases = new ArrayList<>();
    private ForRecordAdapter forRecordAdapter;
    private PullableListView recordList;
    private PullToRefreshLayout layout;
    private int refreshCount = 1;
    private boolean pull = false;
    private boolean isLastRecord = false;
    public LinearLayout noListShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_record);
        mContext=this;
        initViews();
        initDialog();
        initEvents();
    }

    @Override
    protected void initViews() {
        layout = (PullToRefreshLayout)findViewById(R.id.product_refresh_view);
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        noListShow = (LinearLayout)findViewById(R.id.no_list_show);
        recordList = (PullableListView)findViewById(R.id.record_list);
        htvCenter.setText("兑换记录");
        htvLeft.setOnClickListener(this);
        layout.setOnRefreshListener(this);
        forRecordAdapter = new ForRecordAdapter();
        recordList.setAdapter(forRecordAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                defaultFinish();
                break;
        }
    }
    private void getForRescord(String page){
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<ForRecordBase>>> call = userBiz.getEarnIngRecord(baseApplication.mUser.token,page);//"18813904075:123456789"
        call.enqueue(new HttpCallBack<BaseResponse<List<ForRecordBase>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<ForRecordBase>>> arg0,
                                   Response<BaseResponse<List<ForRecordBase>>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<List<ForRecordBase>> baseResponse = response.body();
                if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
                    String status = baseResponse.getStatus();
                    if (baseResponse.getStatus().equals(Constants.T_OK)){
                        List<ForRecordBase> data = baseResponse.getData();
                        if (data.size()>0){
                            recordList.setPullUp(false);
                            forRecordBases.addAll(data);
                            layout.setVisibility(View.VISIBLE);
                            noListShow.setVisibility(View.GONE);
                            forRecordAdapter.notifyDataSetChanged();
                            if (forRecordBases.size() < 10){
                                isLastRecord = true;
                                recordList.setPullUp(true);
                            }
                        }else{
                            if (pull){
                                isLastRecord = true;
                                recordList.setPullUp(true);
                            }else{
                                layout.setVisibility(View.GONE);
                                noListShow.setVisibility(View.VISIBLE);
                            }
                        }
                    }


                }

            }

            @Override
            public void onFailure(Call<BaseResponse<List<ForRecordBase>>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    @Override
    protected void initEvents() {
        getForRescord("1");
    }

    class ForRecordAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        @Override
        public int getCount() {
            return forRecordBases.size();
        }

        @Override
        public Object getItem(int position) {
            return forRecordBases.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView = View.inflate(ForRecordActivity.this,R.layout.adapter_for_record_list,null);
                holder = new ViewHolder();
                holder.recordName = (HandyTextView) convertView.findViewById(R.id.record_name);
                holder.recordTime = (HandyTextView) convertView.findViewById(R.id.record_time);
                holder.recordAmount = (HandyTextView) convertView.findViewById(R.id.record_amount);
                holder.recordType = (HandyTextView) convertView.findViewById(R.id.iar_type);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            String bankCard = forRecordBases.get(position).getBank_card();
            String bandFour = bankCard.substring(bankCard.length()-4,bankCard.length());
            String addTime = forRecordBases.get(position).getAdd_time();
            String recordTimes = addTime.substring(0,9);
                holder.recordName.setText("兑换到"+forRecordBases.get(position).getBankname()+"卡("+bandFour+")");
                holder.recordTime.setText(forRecordBases.get(position).getAdd_time());
                holder.recordAmount.setText(forRecordBases.get(position).getMoney());
                holder.recordType.setText(forRecordBases.get(position).getStatus());
            return convertView;
        }
    }

    public  class ViewHolder{
        private HandyTextView recordName;
        private HandyTextView recordTime;
        private HandyTextView recordAmount;
        private HandyTextView recordType;

    }
    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        // 下拉刷新操作
        forRecordBases.clear();
        initpullView();
        forRecordAdapter.notifyDataSetChanged();
        getForRescord("1");
        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
    }
    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        // 加载操作
        if (!isLastRecord) {
            refreshCount++;
            pull = true;
            getForRescord(String.valueOf(refreshCount));
        }
        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
    }
    private void initpullView(){
        refreshCount = 1;
        isLastRecord = false;
        recordList.setPullUp(false);
        pull = false;
    }
}
