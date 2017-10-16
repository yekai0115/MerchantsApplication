package com.ddz.mearchant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.AddressInfo;
import com.ddz.mearchant.models.OrderDetailBase;
import com.ddz.mearchant.models.Orderdata;
import com.ddz.mearchant.utils.CommonUtils;
import com.ddz.mearchant.view.ClearEditText;
import com.ddz.mearchant.view.HandyTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class OrderCloseActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext;
    private ClearEditText shopName;
    private HandyTextView orderDetailStatus,receiveName,receivePhone,receiveAddress,orderCode;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private OrderDetailBase orderDetail;
    private ListView orderDetailList;
    private List<Orderdata> lists = new ArrayList<>();
    private OrderDetailAdapter orderDetailAdapter;
    private HandyTextView closeReason;
    private Button closeButton;
    private LinearLayout scanOrderLinear,orderDetailComplete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_close);
        mContext=this;
        if (getIntent().getBundleExtra("detail")!=null){
            orderDetail = (OrderDetailBase) getIntent().getBundleExtra("detail").getSerializable("orderDetail");
        }
        initViews();
        initDialog();
    }

    @Override
    protected void initViews() {
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout) findViewById(R.id.title_htv_left);
        htvRight = (HandyTextView)findViewById(R.id.title_htv_rigit);
        orderDetailStatus = (HandyTextView)findViewById(R.id.order_detail_status);
        receiveName = (HandyTextView)findViewById(R.id.receive_name);
        receivePhone = (HandyTextView)findViewById(R.id.receive_phone);
        receiveAddress = (HandyTextView)findViewById(R.id.receive_address);
        orderCode = (HandyTextView)findViewById(R.id.order_code);
        orderDetailList = (ListView)findViewById(R.id.order_detail_list);
        closeReason = (HandyTextView)findViewById(R.id.close_reason);
        closeButton = (Button)findViewById(R.id.close_button);
        htvCenter.setText("关闭订单");
        orderDetailAdapter = new OrderDetailAdapter();
        orderDetailList.setAdapter(orderDetailAdapter);
        htvLeft.setOnClickListener(this);
        orderDetailStatus.setText("待发货");
        if (orderDetail.getAddr_info()!=null){
            AddressInfo info = orderDetail.getAddr_info();
            receiveName.setText(info.getAddr_name());
            receivePhone.setText(info.getAddr_mobile());
            receiveAddress.setText("收货地址:"+info.getAddr_province()
                    +info.getAddr_city()+info.getAddr_county()+info.getAddr_detail());
        }
        orderCode.setText(orderDetail.getTrade_id());
        lists = orderDetail.getGoods_list();
        orderDetailAdapter.notifyDataSetChanged();
        closeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                closeOrder();
//                    modifyShopName();
                //关闭订单
                
                break;
            case R.id.title_htv_left:
                    defaultFinish();
                break;
        }
    }
    private void closeOrder(){
       int trade_id = Integer.valueOf(orderDetail.getTrade_id());
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>>	call = userBiz.orderClose(baseApplication.mUser.token,trade_id);
        call.enqueue(new HttpCallBack<BaseResponse<String>>() {

            @Override
            public void onResponse(Call<BaseResponse<String>> arg0,
                                   Response<BaseResponse<String>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<String> baseResponse = response.body();
                if (null != baseResponse) {
                    String status = baseResponse.getStatus();
                    String data = baseResponse.getData();
                    if (status.equals(Constants.T_OK)){
                        showShortToast("关闭订单成功");
                        defaultFinish();
                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setClass(OrderCloseActivity.this,MainActivity.class);
                        startActivity(intent);
                    }else{
                        showShortToast(data.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<String>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    class OrderDetailAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView = View.inflate(OrderCloseActivity.this,R.layout.adapter_order_detail_list,null);
                holder = new ViewHolder();

                holder.mShowParmerLinear= (LinearLayout) convertView.findViewById(R.id.show_hide_parmer_linear);
                holder.mPic= (ImageView) convertView.findViewById(R.id.order_image);
                holder.mName = (HandyTextView) convertView.findViewById(R.id.order_name);
                holder.mPrice = (HandyTextView) convertView.findViewById(R.id.order_price);
                holder.mType = (HandyTextView) convertView.findViewById(R.id.type);
                holder.mOrderColor = (HandyTextView) convertView.findViewById(R.id.order_color);
                holder.mReceiveTime = (HandyTextView) convertView.findViewById(R.id.receive_time);
                holder.mPayType = (HandyTextView) convertView.findViewById(R.id.pay_type);
                holder.mRebackCode = (HandyTextView) convertView.findViewById(R.id.reback_code);
                holder.mProfitMoney = (HandyTextView) convertView.findViewById(R.id.profit_money);
                convertView.setTag(holder);
//                支付方式:1微信支付2赞分3后台添加6支付宝9快捷支付99报单
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            holder.mName.setText(lists.get(position).getGoods_name());
            String price = lists.get(position).getGoods_fee();
            SpannableString styledText = new SpannableString(lists.get(position).getGoods_fee());
            styledText.setSpan(new AbsoluteSizeSpan(40), 0, price.indexOf("."), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new AbsoluteSizeSpan(30), price.indexOf("."), styledText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.mPrice.setText(styledText, TextView.BufferType.SPANNABLE);
//            holder.mPrice.setText(String.valueOf(lists.get(position).getGoods_fee()));
            holder.mOrderColor.setText(lists.get(position).getAttr_value());
            Integer profit = Integer.valueOf(lists.get(position).getProfit());
            switch (profit){
                case 1:
                    holder.mType.setText("20%");
                    break;
                case 2:
                    holder.mType.setText("10%");
                    break;
                case 5:
                    holder.mType.setText("5%");
                    break;
            }
            try {
                Glide.with(mContext).load(orderDetail.getGoods_pic_uri()+lists.get(position).getGoods_thumb()).fitCenter()
                        .placeholder(getResources().getColor(R.color.tab_text_diveder)).error(getResources().getColor(R.color.tab_text_diveder)).into(holder.mPic);
//                ImageUrl.setbitmap(orderDetail.getGoods_pic_uri()+lists.get(position).getGoods_thumb(),holder.mPic);
            }catch (Exception e){
                e.printStackTrace();
            }
            if (!orderDetail.getOrder_status().equals("0")){
                if (position == lists.size()-1){
                    Integer payType = -1;
                    holder.mShowParmerLinear.setVisibility(View.VISIBLE);
                    if(orderDetail.getPay_type()!=null && orderDetail.getPay_type().length()>0) {
                        payType = Integer.valueOf(orderDetail.getPay_type());
                    }
                    Long time= new Long(orderDetail.getAdd_time());
                    String d = CommonUtils.sdfDatePoint.format(time);
                    holder.mReceiveTime.setText(d);
                    holder.mRebackCode.setText(orderDetail.getPay_return_id());
                    holder.mProfitMoney.setText(orderDetail.getPoints());
                    switch (payType){
                        case 1:
                            holder.mPayType.setText("微信支付");
                            break;
                        case 2:
                            holder.mPayType.setText("赞分");
                            break;
                        case 3:
                            holder.mPayType.setText("后台添加");
                            break;
                        case 6:
                            holder.mPayType.setText("支付宝");
                            break;
                        case 9:
                            holder.mPayType.setText("快捷支付");
                            break;
                        case 99:
                            holder.mPayType.setText("报单");
                            break;
                        default:
                            holder.mPayType.setText("未知");
                            break;
                    }

                }
            }

            return convertView;
        }
    }

    public  class ViewHolder{
        private ImageView mPic;
        private HandyTextView mName;
        private HandyTextView mPrice;
        private HandyTextView mType;
        private HandyTextView mOrderColor;
        private HandyTextView mReceiveTime;
        private HandyTextView mPayType;
        private HandyTextView mRebackCode;
        private HandyTextView mProfitMoney;
        private LinearLayout mShowParmerLinear;

    }



    private  void  modifyShopName(){
            Intent mIntent = new Intent();
            mIntent.putExtra("shopName", shopName.getText().toString());
            setResult(RESULT_OK,mIntent);
            defaultFinish();
    }
    @Override
    protected void initEvents() {

    }

}
