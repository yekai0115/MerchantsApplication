package com.ddz.mearchant.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.Custom;
import com.ddz.mearchant.view.HandyTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class CustomClassificationActivity extends BaseActivity implements View.OnClickListener{
    private Context mContext;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private ListView listView;
    private LinearLayout addCustomerButton;
    private boolean isDelete;
    private ArrayList<Custom> customs = new ArrayList<>();
    private ArrayList<Custom> selectCustoms = new ArrayList<>();
    private ArrayList<Integer> selectPostion = new ArrayList<>();
    private CustomAdapter customAdapter;
    private LinearLayout noListShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_classification);
        mContext=this;
        initViews();
        initDialog();
        initEvents();

    }

    @Override
    protected void initViews() {
        noListShow = (LinearLayout) findViewById(R.id.no_list_show);
        htvLeft = (LinearLayout) findViewById(R.id.title_htv_left);
        htvCenter = (HandyTextView) findViewById(R.id.title_htv_center);
        htvRight = (HandyTextView) findViewById(R.id.title_htv_rigit);
        listView = (ListView) findViewById(R.id.list_view);
        addCustomerButton = (LinearLayout) findViewById(R.id.add_customer_button);
        htvCenter.setText("自定义分类");
        htvRight.setText("删除");
        htvRight.setVisibility(View.VISIBLE);
        htvLeft.setOnClickListener(this);
        htvRight.setOnClickListener(this);
        addCustomerButton.setOnClickListener(this);
        customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);


    }
    @Override
    protected void initEvents() {
        getCustomList();
    }
    private void getCustomList(){
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<Custom>>> call = userBiz.getGoodsCategory(baseApplication.mUser.token);//"18813904075:123456789"
        call.enqueue(new HttpCallBack<BaseResponse<List<Custom>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<Custom>>> arg0,
                                   Response<BaseResponse<List<Custom>>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<List<Custom>> baseResponse = response.body();
                if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
                    String status = baseResponse.getStatus();
                    if (status.equals(Constants.T_OK)){
                        List<Custom> data = baseResponse.getData();
                        if (data.size()>0){
                            listView.setVisibility(View.VISIBLE);
                            noListShow.setVisibility(View.GONE);
                            customs.addAll(data);
                            customAdapter.notifyDataSetChanged();
                        }else{
                            listView.setVisibility(View.GONE);
                            noListShow.setVisibility(View.VISIBLE);
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<List<Custom>>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                    defaultFinish();
                break;
            case R.id.add_customer_button:
                    startActivity(AddCustomActivity.class);
                break;
            case R.id.title_htv_rigit:
                    if (!isDelete){
                        selectCustoms.clear();
                        htvRight.setText("确定");
                        isDelete = true;
                    }else{
                        htvRight.setText("删除");
                        isDelete = false;
                        deleteCustom();
                    }
                    customAdapter.notifyDataSetChanged();
                break;
        }
    }
    private void deleteCustom(){
        if (selectCustoms.size() <= 0){
            return;
        }
        String ids = "";
        for (int i = 0 ; i<selectCustoms.size();i++){
            ids = ids+selectCustoms.get(i).getGoods_user_cat_id()+',';
        }
        ids = ids.substring(0, ids.length() - 1);
        dialog.setCancelable(false);
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.delGoodsCategory(baseApplication.mUser.token,ids);//"18813904075:123456789"
        call.enqueue(new HttpCallBack<BaseResponse<String>>() {

            @Override
            public void onResponse(Call<BaseResponse<String>> arg0,
                                   Response<BaseResponse<String>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<String> baseResponse = response.body();
                if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
                    String status = baseResponse.getStatus();
                    String data = baseResponse.getData();
                    if (status.equals(Constants.T_OK)){
                        showShortToast("删除分类成功");
                        for (int i = 0; i < selectCustoms.size(); i++){
                            customs.remove(selectCustoms.get(i));
                        }
                        if (customs.size()>0){
                            customAdapter.notifyDataSetChanged();
                            listView.setVisibility(View.VISIBLE);
                            noListShow.setVisibility(View.GONE);
                        }else {
                            listView.setVisibility(View.GONE);
                            noListShow.setVisibility(View.VISIBLE);
                        }


                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<String>> arg0,
                                  Throwable arg1) {
            }
        });
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return customs.size();
        }

        @Override
        public Object getItem(int position) {
            return customs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView = View.inflate(CustomClassificationActivity.this,R.layout.adapter_postage_custoer,null);
                holder = new ViewHolder();
                holder.customNameLinear= (LinearLayout) convertView.findViewById(R.id.custom_name_linear);
                holder.customName = (HandyTextView) convertView.findViewById(R.id.custom_name);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.add_custom_checkbox);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            if (isDelete){
                holder.checkBox.setChecked(false);
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (customs.get(position).isChecked()){
                            customs.get(position).setChecked(false);
                            for (int i = 0; i < selectCustoms.size(); i++) {
                                if (selectCustoms.get(i).getGoods_user_cat_id() == customs.get(position).getGoods_user_cat_id()) {
                                    selectCustoms.remove(i);
                                }
                            }
                        }else{
                            selectCustoms.add(customs.get(position));
                            customs.get(position).setChecked(true);
                        }
                    }
                });
            }else{
                holder.checkBox.setVisibility(View.GONE);
            }
            if (selectCustoms.size() > 0) {
                for (int i = 0; i < selectCustoms.size(); i++) {
                    if (selectCustoms.get(i).getGoods_user_cat_id() == customs.get(position).getGoods_user_cat_id()) {
                        holder.checkBox.setChecked(true);
                        customs.get(position).setChecked(true);
                    }
                }
            }
            holder.customName.setText(customs.get(position).getUser_cate_name());
            return convertView;
        }
    }

    public  class ViewHolder{
        private LinearLayout customNameLinear;
        private HandyTextView customName;
        private CheckBox checkBox;


    }

}
