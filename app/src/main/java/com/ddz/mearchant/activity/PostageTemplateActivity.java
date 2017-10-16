package com.ddz.mearchant.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.adapter.MyPostageListAdapter;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.Postage;
import com.ddz.mearchant.view.HandyTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.ddz.mearchant.R.id.title_htv_rigit;


public class PostageTemplateActivity extends BaseActivity implements View.OnClickListener,MyPostageListAdapter.OnDeleterListener{
    private Context mContext;
    private HandyTextView htvCenter;
    private ImageView htvRight;
    private LinearLayout htvLeft,customClassificationLinear;
    private ExpandableListView postageListView;
    private ArrayList<Postage> postages = new ArrayList<Postage>();
    private MyPostageListAdapter myPostageListAdapter;
    private LinearLayout noListShow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postage_template);
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
        htvRight = (ImageView) findViewById(title_htv_rigit);
        postageListView = (ExpandableListView) findViewById(R.id.postage_template_list_view);
        htvCenter.setText("邮费模板");
        htvLeft.setOnClickListener(this);
        htvRight.setOnClickListener(this);
        myPostageListAdapter = new MyPostageListAdapter(this);
        postageListView.setAdapter(myPostageListAdapter);
        postageListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        myPostageListAdapter.setOnDeleterListener(this);
    }

    @Override
    public void getProductSize(int size) {
        if (size == 0){
            postageListView.setVisibility(View.GONE);
            noListShow.setVisibility(View.VISIBLE);
        }else{
            postageListView.setVisibility(View.VISIBLE);
            noListShow.setVisibility(View.GONE);
        }
    }

    private void updateListView(ArrayList<Postage> postages) {

        myPostageListAdapter.setList(postages);

        myPostageListAdapter.notifyDataSetChanged();

        expandAllGroup();

    }
    /**
     * 展开所有组
     */
    private void expandAllGroup() {
        for (int i = 0; i < postages.size(); i++) {
            postageListView.expandGroup(i);
        }
    }
    @Override
    protected void initEvents() {
        getPostageList();
    }
    private void getPostageList(){
        dialog.show();
        dialog.setCancelable(false);
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
                    if (data.size()>0){
                        postageListView.setVisibility(View.VISIBLE);
                        noListShow.setVisibility(View.GONE);
                        postages.addAll(data);
                        updateListView(postages);
                    }else{
                        postageListView.setVisibility(View.GONE);
                        noListShow.setVisibility(View.VISIBLE);
                    }

                }

            }

            @Override
            public void onFailure(Call<BaseResponse<List<Postage>>> arg0,
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
            case R.id.title_htv_rigit:
                startActivity(AddPostageActivity.class);
                break;

        }
    }

}
