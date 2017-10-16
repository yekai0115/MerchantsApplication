package com.ddz.mearchant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

/**
 * Created by Administrator on 2017/6/21 0021.
 */

public class SelectCustomTypeActivity extends BaseActivity implements View.OnClickListener{
    private Context mContext;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private ListView listProductType;
    private ArrayList<Custom> customs = new ArrayList<>();
    private LinearLayout noListShow;
    private CustomAdapter customAdapter;
    private LinearLayout addCustomerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_custom_type_name);
        mContext=this;
        initViews();
        initDialog();
        initEvents();
    }

    @Override
    protected void initViews() {
        noListShow = (LinearLayout) findViewById(R.id.no_list_show);
        htvRight = (HandyTextView)findViewById(R.id.title_htv_rigit);
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        listProductType = (ListView)findViewById(R.id.list_product_type);
        addCustomerButton = (LinearLayout) findViewById(R.id.add_customer_button);
        customAdapter = new CustomAdapter();
        listProductType.setAdapter(customAdapter);
        htvCenter.setText("自定义分类");
        htvLeft.setOnClickListener(this);
        addCustomerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                defaultFinish();
                break;
            case R.id.add_customer_button:
                Intent intent = new Intent();
                intent.setClass(SelectCustomTypeActivity.this,AddCustomSelectActivity.class);
                startActivityForResult(intent,1);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 1: {
                        customs.clear();
                        customAdapter.notifyDataSetChanged();
                        getProductTypeList();
                    break;
                }
                default:
                    break;
            }
        }
    }
    private  void  selectProductType(Custom custom){
        Intent mIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("custom",custom);
        mIntent.putExtra("custom", bundle);
        setResult(RESULT_OK,mIntent);
        defaultFinish();
    }
    @Override
    protected void initEvents() {
        getProductTypeList();
    }

    private void getProductTypeList(){
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
                    List<Custom> data = baseResponse.getData();
                    if (data.size()>0){
                        listProductType.setVisibility(View.VISIBLE);
                        noListShow.setVisibility(View.GONE);
                        customs.addAll(data);
                        customAdapter.notifyDataSetChanged();
                    }else{
                        listProductType.setVisibility(View.GONE);
                        noListShow.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<List<Custom>>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    private  boolean isFirstTypeClick;
    class CustomAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

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
                convertView = View.inflate(SelectCustomTypeActivity.this,R.layout.adapter_product_type_list,null);
                holder = new ViewHolder();
                holder.pNname = (HandyTextView) convertView.findViewById(R.id.product_type_name);
                holder.pLinear = (LinearLayout) convertView.findViewById(R.id.type_linear);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
                holder.pNname.setText(customs.get(position).getUser_cate_name());
                holder.pLinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selectProductType(customs.get(position));

                    }
                });
            return convertView;
        }
    }

    public  class ViewHolder{
        private LinearLayout pLinear;
        private HandyTextView pNname;

    }

}
