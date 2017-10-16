package com.ddz.mearchant.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.Logistics;
import com.ddz.mearchant.models.LogisticsDetail;
import com.ddz.mearchant.models.OrderDetailBase;
import com.ddz.mearchant.models.Orderdata;
import com.ddz.mearchant.utils.GsonUtil;
import com.ddz.mearchant.view.ClearEditText;
import com.ddz.mearchant.view.HandyTextView;
import com.ddz.mearchant.widget.ListViewForScrollView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class OrderQueryLogisticsActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext;
    private ClearEditText shopName;
    private HandyTextView orderDetailStatus,receiveName,receivePhone,receiveAddress,orderCode;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private OrderDetailBase orderDetail;
    private ListViewForScrollView orderDetailList,logisticsList;
    private List<Orderdata> lists = new ArrayList<>();
    private List<LogisticsDetail> logisticsDetails = new ArrayList<>();
    private OrderDetailAdapter orderDetailAdapter;
    private EditText closeReason;
    private Button closeButton;
    private LinearLayout scanOrderLinear,orderDetailComplete,noListShow;
    private OrderLogisticsAdapter orderLogisticsAdapter;
    private View showView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_query_logistics);
        mContext=this;
        if (getIntent().getBundleExtra("detail")!=null){
            orderDetail = (OrderDetailBase) getIntent().getBundleExtra("detail").getSerializable("orderDetail");
        }
        initViews();
        initDialog();
        initEvents();
    }

    @Override
    protected void initViews() {
        showView = (View)findViewById(R.id.show_view);
        noListShow = (LinearLayout)findViewById(R.id.no_list_show);
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout) findViewById(R.id.title_htv_left);
        htvRight = (HandyTextView)findViewById(R.id.title_htv_rigit);
        orderCode = (HandyTextView)findViewById(R.id.order_code);
        orderDetailList = (ListViewForScrollView)findViewById(R.id.order_detail_list);
        logisticsList = (ListViewForScrollView)findViewById(R.id.logistics_list);
        htvCenter.setText("查看物流");
        orderDetailAdapter = new OrderDetailAdapter();
        orderDetailList.setAdapter(orderDetailAdapter);
        htvLeft.setOnClickListener(this);
        orderCode.setText(orderDetail.getTrade_id());
        lists.clear();
        orderDetailAdapter.notifyDataSetChanged();
        lists = orderDetail.getGoods_list();
        orderDetailAdapter.notifyDataSetChanged();
        orderLogisticsAdapter = new OrderLogisticsAdapter();
        logisticsList.setAdapter(orderLogisticsAdapter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                    defaultFinish();
                break;
        }
    }
    private void queryOrderLogistics(){
        dialog.show();
       String delivery_id = orderDetail.getDelivery_id();
//        String delivery_id = "667635407217";
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<Object>> call = userBiz.deliveryQuery(baseApplication.mUser.token,delivery_id);
        call.enqueue(new HttpCallBack<BaseResponse<Object>>() {

            @Override
            public void onResponse(Call<BaseResponse<Object>> arg0,
                                   Response<BaseResponse<Object>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<Object> baseResponse = response.body();
                if (null != baseResponse) {
                    String status = baseResponse.getStatus();
                    Object data = baseResponse.getData();
                    if (status.equals(Constants.T_OK)){
                        Logistics logistics = GsonUtil.GsonToBean(GsonUtil.toJsonString(data),Logistics.class);
                        if (data != null && logistics.getDelivery_detail()!=null && logistics.getDelivery_detail().size()>0){
                            logisticsDetails.addAll(logistics.getDelivery_detail());
                            logisticsList.setVisibility(View.VISIBLE);
                            showView.setVisibility(View.VISIBLE);
                            noListShow.setVisibility(View.GONE);
                            orderLogisticsAdapter.notifyDataSetChanged();
                        }else{
                            logisticsList.setVisibility(View.GONE);
                            showView.setVisibility(View.GONE);
                            noListShow.setVisibility(View.VISIBLE);

                        }
                    }else{
                        logisticsList.setVisibility(View.GONE);
                        showView.setVisibility(View.GONE);
                        noListShow.setVisibility(View.VISIBLE);

                    }
                }else{
                    showShortToast("暂无物流信息");
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    class OrderLogisticsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return logisticsDetails.size();
        }

        @Override
        public Object getItem(int position) {
            return logisticsDetails.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LogisticsViewHolder holder;
            if (convertView == null){
                convertView = View.inflate(OrderQueryLogisticsActivity.this,R.layout.adapter_order_logistics_show,null);
                holder = new LogisticsViewHolder();
                holder.lHide = (View) convertView.findViewById(R.id.view_hide);
                holder.lImage = (ImageView) convertView.findViewById(R.id.logistics_image);
                holder.lImage2 = (ImageView) convertView.findViewById(R.id.logistics_image2);

                holder.lStatus = (HandyTextView) convertView.findViewById(R.id.logistics_status);
                holder.lTime = (HandyTextView) convertView.findViewById(R.id.logistics_time);
                convertView.setTag(holder);
            }else{
                holder = (LogisticsViewHolder) convertView.getTag();
            }
            if (position == 0){
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(30,30);
//                holder.lImage.setLayoutParams(layoutParams);
                holder.lImage2.setVisibility(View.VISIBLE);
                holder.lImage.setVisibility(View.GONE);
//                holder.lImage.setBackground(getResources().getDrawable(R.drawable.bg_cirle_logistics_style));
                holder.lHide.setVisibility(View.GONE);
                holder.lStatus.setTextColor(getResources().getColor(R.color.tab_line_color));
                holder.lTime.setTextColor(getResources().getColor(R.color.tab_line_color));
            }else{
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20,20);
//                holder.lImage.setLayoutParams(layoutParams);
                holder.lImage2.setVisibility(View.GONE);
                holder.lImage.setVisibility(View.VISIBLE);
//                holder.lImage.setBackground(getResources().getDrawable(R.drawable.bg_cirle_logistics_default_style));
                holder.lHide.setVisibility(View.VISIBLE);
                holder.lStatus.setTextColor(getResources().getColor(R.color.tab_text_gray4));
                holder.lTime.setTextColor(getResources().getColor(R.color.tab_text_gray4));
            }
            holder.lStatus.setText(String.valueOf(logisticsDetails.get(position).getStatus()));
            holder.lTime.setText(logisticsDetails.get(position).getTime());
            return convertView;
        }
    }

    public  class LogisticsViewHolder{
        private ImageView lImage,lImage2;
        private HandyTextView lStatus;
        private HandyTextView lTime;
        private View lHide;

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
                convertView = View.inflate(OrderQueryLogisticsActivity.this,R.layout.adapter_order_detail_list,null);
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
            /*if (orderDetail.getOrder_status().equals("0")||orderDetail.getOrder_status().equals("1")){
                if (position == lists.size()-1){
                    holder.mShowParmerLinear.setVisibility(View.VISIBLE);
                    Integer payType = -1;
                    if(orderDetail.getPay_type()!=null && orderDetail.getPay_type().length()>0) {
                        payType = Integer.valueOf(orderDetail.getPay_type());
                    }
                    Long time= new Long(orderDetail.getAdd_time());
                    String d = CommonUtils.sdfDatePoint.format(time*1000);
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
                    }

                }else {
                    holder.mShowParmerLinear.setVisibility(View.GONE);
                }
            }*/

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



    @Override
    protected void initEvents() {
        queryOrderLogistics();
    }
}
