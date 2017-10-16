package com.ddz.mearchant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.AddPostageData;
import com.ddz.mearchant.models.PostageData;
import com.ddz.mearchant.models.Proivince;
import com.ddz.mearchant.utils.GsonUtil;
import com.ddz.mearchant.utils.StringUtils;
import com.ddz.mearchant.view.HandyTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;


public class AddPostageActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext;
    private EditText addPostageName,addPostagePrice;
    private HandyTextView addPostageAddress,htvCenter,htvRight;
    private LinearLayout htvLeft;
    private AddPostageData addPostageData = new AddPostageData();
    private LinearLayout addPostageLinear,mainPostageLiinear,deletePostageLinear;
    ArrayList<Proivince> listObj = new ArrayList<Proivince>();
    ArrayList<PostageData> postageDatas = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_postage);
        mContext=this;
        initDialog();
        initViews();
    }

    @Override
    protected void initViews() {
        addPostageName = (EditText)findViewById(R.id.add_postage_name);
        addPostagePrice = (EditText)findViewById(R.id.add_postage_price);
        addPostageAddress = (HandyTextView)findViewById(R.id.add_postage_address);
        addPostageLinear = (LinearLayout)findViewById(R.id.add_postage_linear);
        mainPostageLiinear = (LinearLayout)findViewById(R.id.main_postage_liinear);
        deletePostageLinear =  (LinearLayout)findViewById(R.id.delete_postage_button);
        htvRight = (HandyTextView)findViewById(R.id.title_htv_rigit);
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        htvRight.setVisibility(View.VISIBLE);
        htvCenter.setText("添加模板");
        htvRight.setOnClickListener(this);
        htvLeft.setOnClickListener(this);
//        addPostageAddress.setOnClickListener(this);
        addPostageLinear.setOnClickListener(this);
        deletePostageLinear.setOnClickListener(this);
        mainPostageLiinear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_rigit:
                    addPosrage();
                break;
            case R.id.title_htv_left:
                    defaultFinish();
                break;
            case R.id.add_postage_address:
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                intent.setClass(AddPostageActivity.this,AddAddressProivinceActivity.class);
                if (listObj.size()>0){
                    intent.putExtra("city",(Serializable) listObj);
                }
                startActivityForResult(intent,1);
                break;
            case R.id.add_postage_linear:
                    addPsetageLinearView();
                break;
            case R.id.delete_postage_button:
                if (conditions.size()>0&&itemId>=0){
                    if (null != listObjMap.get(itemId-1)){
                        listObjMap.remove(itemId-1);
                    }
                    mainPostageLiinear.removeView(conditions.get(itemId-1));
                    conditions.remove(itemId-1);
                    itemId--;
                }
//                    addPsetageLinearView();
                break;

        }
    }
    int itemId  =0;
    private int position = 0;
    private int clickItemId;
    private  HashMap<Integer,LinearLayout> conditions = new HashMap<>();
    private  HashMap<Integer,ArrayList<Proivince>> listObjMap = new HashMap<>();
    private void addPsetageLinearView(){

        //每次创建一个view
        LinearLayout ll_limit = (LinearLayout) View.inflate(this, R.layout.activity_add_postage_model, null);
        HandyTextView addPostageAddress2 = (HandyTextView)ll_limit.findViewById(R.id.add_postage_address);
        addPostageAddress2.setTag(itemId);
        addPostageAddress2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickItemId = (int)v.getTag();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                HandyTextView add_post = (HandyTextView) conditions.get(clickItemId).findViewById(R.id.add_postage_address);
                add_post.getText().toString();
                intent.setClass(AddPostageActivity.this,AddAddressProivinceActivity.class);
                if (!TextUtils.isEmpty(add_post.getText().toString())&&listObjMap.get(clickItemId)!=null){
                    ArrayList<Proivince> list = listObjMap.get(clickItemId);
                    intent.putExtra("city",(Serializable) list);
                }
                startActivityForResult(intent,2);
            }
        });
        conditions.put(itemId,ll_limit);
        mainPostageLiinear.addView(ll_limit);
        itemId++;
        position++;


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {
            String fileName;
            switch (requestCode) {
                case 1:
                    String resultStr = "";
                    listObj = (ArrayList<Proivince>) data.getSerializableExtra("cities");
                    for (int i = 0; i < listObj.size(); i++) {
                        resultStr = resultStr + listObj.get(i).getRegion_name()+',';
                    }
                    resultStr = resultStr.substring(0, resultStr.length() - 1);
                    addPostageAddress.setText(resultStr);
                    break;
                case 2:
                    String resultStrs = "";
                    ArrayList<Proivince> listObj2 = (ArrayList<Proivince>) data.getSerializableExtra("cities");
                    for (int i = 0; i < listObj2.size(); i++) {
                        resultStrs = resultStrs + listObj2.get(i).getRegion_name()+',';
                    }
                    resultStrs = resultStrs.substring(0, resultStrs.length() - 1);
                    HandyTextView addPost = (HandyTextView)conditions.get(clickItemId).findViewById(R.id.add_postage_address);
                    addPost.setText(resultStrs);
                    listObjMap.put(clickItemId,listObj2);
                    break;
            }
        }
    }
    private void addPosrage(){
        if(StringUtils.isBlank(addPostageName.getText().toString())||
                StringUtils.isBlank(addPostagePrice.getText().toString())||
                StringUtils.isBlank(addPostageAddress.getText().toString())){
            showShortToast("信息填写不完整");
            return;
        }
        for (int i = 0;i<conditions.size();i++){
            LinearLayout val = conditions.get(i);
            EditText add_price = (EditText) val.findViewById(R.id.add_postage_price);
            HandyTextView add_address = (HandyTextView) val.findViewById(R.id.add_postage_address);
            String s1 = add_price.getText().toString().trim();
            String s2 = add_address.getText().toString().trim();
            if (TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2)){
                showShortToast("信息填写不完整");
                return;
            }
            PostageData postageData2 = new PostageData();
            postageData2.setPrice(s1);
            ArrayList<Proivince> proivinces = listObjMap.get(i);
            String resultIds2 = "";
            for (int j = 0 ; j<proivinces.size();j++){
                resultIds2 = resultIds2+proivinces.get(j).getRegion_id()+',';
            }
            resultIds2 = resultIds2.substring(0, resultIds2.length() - 1);
            postageData2.setRegion(resultIds2);
            postageDatas.add(postageData2);
        };
        PostageData postageData = new PostageData();
        postageData.setPrice(addPostagePrice.getText().toString());
        String resultIds = "";
//        for (int i = 0 ; i<listObj.size();i++){
//            resultIds = resultIds+listObj.get(i ).getRegion_id()+',';
//        }
//        resultIds = resultIds.substring(0, resultIds.length() - 1);
        postageData.setRegion("0");
        postageDatas.add(postageData);
        String json = GsonUtil.ListToJson(postageDatas);
        dialog.show();
        dialog.setCancelable(false);
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.addShippingTpl(baseApplication.mUser.token,addPostageName.getText().toString(),json);//"18813904075:123456789"
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
                        defaultFinish();
                        showShortToast("添加模板成功");
                        Intent intent = new Intent();
                        intent.setClass(AddPostageActivity.this,PostageTemplateActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<String>> arg0,
                                  Throwable arg1) {
            }
        });
        }
//        Set<Map.Entry<Integer, LinearLayout>> items = conditions.entrySet();
//        Iterator iter = items.iterator();
//        while (iter.hasNext()) {
//            Map.Entry entry = (Map.Entry) iter.next();
//            LinearLayout val = (LinearLayout) entry.getValue();
//            EditText add_price = (EditText) val.findViewById(R.id.add_postage_price);
//            HandyTextView add_address = (HandyTextView) val.findViewById(R.id.add_postage_address);
//            String s1 = add_price.getText().toString().trim();
//            String s2 = add_address.getText().toString().trim();
//            if (TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2)){
//                showShortToast("信息填写不完整");
//                return;
//            }
//            PostageData[] data = new PostageData[]{
//                    new PostageData(s1,s2){}
//            };
//            addPostageData.setShipping(data);
//        }
        /**/

    @Override
    protected void initEvents() {

    }


}
