package com.ddz.mearchant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.Product;
import com.ddz.mearchant.view.HandyTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/6/28 0028.
 */

public class SearchProductResultActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext;
    private HandyTextView searchShow;
    private String searchText;
    private ArrayList<Product> products = new ArrayList<>();
    private ProductAdapter productAdapter;
    private ListView searchResultList;
    private ImageView returnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product_result);
        mContext=this;
        searchText = getIntent().getStringExtra("searchText");
        initViews();
        initDialog();
        initEvents();
    }

    @Override
    protected void initViews() {
        searchShow = (HandyTextView)findViewById(R.id.search_show);
        searchResultList = (ListView)findViewById(R.id.search_result_list);
        returnBack = (ImageView)findViewById(R.id.return_back);
        searchShow.setOnClickListener(this);
        returnBack.setOnClickListener(this);
        searchShow.setText(searchText);
        productAdapter = new ProductAdapter();
        searchResultList.setAdapter(productAdapter);
        searchResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("productId",products.get(position).getGoods_id().toString());
                intent.setClass(SearchProductResultActivity.this, EditProductActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_show:
                    defaultFinish();
                break;
            case R.id.return_back:
                defaultFinish();
                break;


        }
    }
    @Override
    protected void initEvents() {
        SearchProductList();
    }
    private void SearchProductList() {
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<Product>>> call = userBiz.findGoodsKeyowrd(baseApplication.mUser.token,searchText);
        call.enqueue(new HttpCallBack<BaseResponse<List<Product>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<Product>>> arg0,
                                   Response<BaseResponse<List<Product>>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<List<Product>> baseResponse = response.body();
                if (null != baseResponse) {
                    String status = baseResponse.getStatus();

                    if (status.equals(Constants.T_OK)){
                        List<Product> data = baseResponse.getData();
                        products.addAll(data);
                        productAdapter.notifyDataSetChanged();
                    }else{
                        showShortToast(baseResponse.getData().toString());
                    }

                }

            }

            @Override
            public void onFailure(Call<BaseResponse<List<Product>>> arg0,
                                  Throwable arg1) {
            }
        });

    }
    class ProductAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Object getItem(int position) {
            return products.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView = View.inflate(SearchProductResultActivity.this,R.layout.adapter_product_list,null);
                holder = new ViewHolder();
                holder.productDetailLinear= (LinearLayout) convertView.findViewById(R.id.product_detail_linear);
                holder.productCheckbox= (CheckBox) convertView.findViewById(R.id.show_product_checkbox);
                holder.productImage= (ImageView) convertView.findViewById(R.id.product_image);
                holder.productStatus = (HandyTextView) convertView.findViewById(R.id.product_status);
                holder.productName = (HandyTextView) convertView.findViewById(R.id.product_name);
                holder.productPrice = (HandyTextView) convertView.findViewById(R.id.product_price);
                holder.productType = (HandyTextView) convertView.findViewById(R.id.product_type);
                holder.productNum = (HandyTextView) convertView.findViewById(R.id.product_num);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.productCheckbox.setVisibility(View.GONE);
            Glide.with(SearchProductResultActivity.this).load(products.get(position).getGoods_pid_uri()+products.get(position).getGoods_thumb()).fitCenter()
                    .placeholder(getResources().getColor(R.color.tab_text_diveder)).error(getResources().getColor(R.color.tab_text_diveder)).into(holder.productImage);
//				ImageUrl.setbitmap(products.get(position).getGoods_pid_uri()+products.get(position).getGoods_thumb(),holder.productImage);
            holder.productStatus.setText(products.get(position).getIs_on_sale());
            holder.productName.setText(products.get(position).getGoods_name());
            holder.productPrice.setText(products.get(position).getGoods_price());
            final Integer profit = Integer.valueOf(products.get(position).getProfit());
            switch (profit){
                case 1:
                    holder.productType.setText("10%");
                    break;
                case 2:
                    holder.productType.setText("20%");
                    break;
                case 5:
                    holder.productType.setText("5%");
                    break;
            }
            holder.productNum.setText(products.get(position).getGoods_number()+"件");
            return convertView;
        }
    }

    public  class ViewHolder{
        private ImageView productImage;
        private HandyTextView productStatus;
        private HandyTextView productName;
        private HandyTextView productPrice;
        private HandyTextView productType;
        private HandyTextView productNum;
        private CheckBox productCheckbox;
        private LinearLayout productDetailLinear;
    }
}