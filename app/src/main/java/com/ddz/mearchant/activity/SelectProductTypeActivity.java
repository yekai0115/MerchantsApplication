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
import com.ddz.mearchant.models.ProductType;
import com.ddz.mearchant.view.HandyTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/6/21 0021.
 */

public class SelectProductTypeActivity extends BaseActivity implements View.OnClickListener{
    private Context mContext;
    private HandyTextView htvCenter,htvRight,parentTypeShow;
    private LinearLayout htvLeft;
    private ListView listProductType;
    private ArrayList<ProductType> productTypes = new ArrayList<>();
    private ArrayList<ProductType> productCenterTypes = new ArrayList<>();//中转

    private ProductTypeAdapter productTypeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type_name);
        mContext=this;
        initViews();
        initDialog();
        initEvents();
    }

    @Override
    protected void initViews() {
        htvRight = (HandyTextView)findViewById(R.id.title_htv_rigit);
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        listProductType = (ListView)findViewById(R.id.list_product_type);
        parentTypeShow  = (HandyTextView)findViewById(R.id.parent_type_show);
        productTypeAdapter = new ProductTypeAdapter();
        listProductType.setAdapter(productTypeAdapter);
        htvCenter.setText("选择分类");
        htvLeft.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                defaultFinish();
                break;
        }
    }
    private  void  selectProductType(ProductType productType){
        Intent mIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("productType",productType);
        mIntent.putExtra("productType", bundle);
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
        Call<BaseResponse<List<ProductType>>> call = userBiz.goodsCategory(baseApplication.mUser.token);//"18813904075:123456789"
        call.enqueue(new HttpCallBack<BaseResponse<List<ProductType>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<ProductType>>> arg0,
                                   Response<BaseResponse<List<ProductType>>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<List<ProductType>> baseResponse = response.body();
                if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
                    String status = baseResponse.getStatus();
                    List<ProductType> data = baseResponse.getData();
                    productTypes.addAll(data);
                    productCenterTypes.addAll(data);
                    productTypeAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<List<ProductType>>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    private  boolean isFirstTypeClick;
    class ProductTypeAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        @Override
        public int getCount() {
            return productCenterTypes.size();
        }

        @Override
        public Object getItem(int position) {
            return productCenterTypes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView = View.inflate(SelectProductTypeActivity.this,R.layout.adapter_product_type_list,null);
                holder = new ViewHolder();
                holder.pNname = (HandyTextView) convertView.findViewById(R.id.product_type_name);
                holder.pLinear = (LinearLayout) convertView.findViewById(R.id.type_linear);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
                holder.pNname.setText(productCenterTypes.get(position).getCat_name());
                holder.pLinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (productCenterTypes.get(position).getSon().size()>0){
                            parentTypeShow.setVisibility(View.VISIBLE);
                            parentTypeShow.setText(productTypes.get(position).getCat_name());
                            productCenterTypes.clear();
                            productCenterTypes.addAll(productTypes.get(position).getSon());
                            productTypeAdapter.notifyDataSetChanged();
                        }else{
                            selectProductType(productCenterTypes.get(position));
                        }

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
