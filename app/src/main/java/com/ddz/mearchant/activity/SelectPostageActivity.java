package com.ddz.mearchant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.Postage;
import com.ddz.mearchant.view.HandyTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/6/21 0021.
 */

public class SelectPostageActivity extends BaseActivity implements View.OnClickListener{
    private Context mContext;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private ListView listProductType;
    private LinearLayout addPostageLinear;
    private ArrayList<Postage> postages = new ArrayList<>();
    private Postage selectPostage;
    private PostageAdapter postageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_postage_name);
        mContext=this;
        if (getIntent().getBundleExtra("mSelectPostage")!=null){
            selectPostage =(Postage) getIntent().getBundleExtra("mSelectPostage").getSerializable("mSelectPostage");
        }
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
        addPostageLinear = (LinearLayout)findViewById(R.id.add_postage_linear);
        postageAdapter = new PostageAdapter();
        listProductType.setAdapter(postageAdapter);
        htvRight.setVisibility(View.VISIBLE);
        htvRight.setText("完成");
        htvCenter.setText("邮费方式");
        htvLeft.setOnClickListener(this);
        addPostageLinear.setOnClickListener(this);
        htvRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                defaultFinish();
                break;
            case R.id.add_postage_linear:
                Intent intent = new Intent();
                intent.setClass(this,AddPostageActivitySecond.class);
                startActivityForResult(intent,1);
                break;
            case R.id.title_htv_rigit:
                    if(selectPostage!=null){
                        selectProductType(selectPostage);
                    }else{
                        showShortToast("您还没有选择邮费方式");
                    }
                break;
        }
    }
    private  void  selectProductType(Postage postage){
        Intent mIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("postage",postage);
        mIntent.putExtra("postage", bundle);
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
        Call<BaseResponse<List<Postage>>> call = userBiz.getShippingTpl(baseApplication.mUser.token);//"18813904075:123456789"
        call.enqueue(new HttpCallBack<BaseResponse<List<Postage>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<Postage>>> arg0,
                                   Response<BaseResponse<List<Postage>>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<List<Postage>> baseResponse = response.body();
                if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
                    String status = baseResponse.getStatus();
                    List<Postage> data = baseResponse.getData();
                    postages.addAll(data);
                    postageAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<List<Postage>>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    private  boolean isFirstTypeClick;
    class PostageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private String[] beans;
        // 用于记录每个RadioButton的状态，并保证只可选一个
        HashMap<String, Boolean> states = new HashMap<String, Boolean>();
        @Override
        public int getCount() {
            return postages.size();
        }

        @Override
        public Object getItem(int position) {
            return postages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView = View.inflate(SelectPostageActivity.this,R.layout.adapter_select_postage_list,null);
                holder = new ViewHolder();
                holder.pNname = (HandyTextView) convertView.findViewById(R.id.select_postage_name);
//                holder.pRadio = (RadioButton) convertView.findViewById(R.id.select_postage_radio);
                holder.pLinear = (LinearLayout) convertView.findViewById(R.id.type_linear);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            final RadioButton radio=(RadioButton) convertView.findViewById(R.id.select_postage_radio);
            holder.pRadio = radio;
            holder.pRadio.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    // 重置，确保最多只有一项被选中
                    for (String key : states.keySet()) {
                        states.put(key, false);

                    }
                    if (radio.isChecked()){
                        selectPostage = new Postage();
                        selectPostage = postages.get(position);
                    }
                    states.put(String.valueOf(position), radio.isChecked());
                    postageAdapter.notifyDataSetChanged();
                }
            });
            boolean res = false;
            if (states.get(String.valueOf(position)) == null
                    || states.get(String.valueOf(position)) == false) {
                res = false;
                states.put(String.valueOf(position), false);
            } else
                res = true;

            holder.pRadio.setChecked(res);
            if (selectPostage!=null && selectPostage.getShipping_tpl_id().equals(postages.get(position).getShipping_tpl_id())){
                holder.pRadio.setChecked(true);
            }
            holder.pNname.setText(postages.get(position).getShipping_name());
                holder.pLinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        selectProductType(postages.get(position));

                    }
                });
            return convertView;
        }
    }

    public  class ViewHolder{
        private LinearLayout pLinear;
        private HandyTextView pNname;
        private RadioButton pRadio;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 1: {
                    postages.clear();
                    postageAdapter.notifyDataSetChanged();
                    getProductTypeList();
                    break;
                }
                default:
                    break;
            }
        }
    }
}
