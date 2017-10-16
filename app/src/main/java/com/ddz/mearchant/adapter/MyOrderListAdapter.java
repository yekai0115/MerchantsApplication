package com.ddz.mearchant.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ddz.mearchant.BaseApplication;
import com.ddz.mearchant.R;
import com.ddz.mearchant.activity.OrderCloseActivity;
import com.ddz.mearchant.activity.OrderDetailActivity;
import com.ddz.mearchant.activity.OrderQueryLogisticsActivity;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.dialog.LoadingDialog;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.OrderBase;
import com.ddz.mearchant.models.OrderDetailBase;
import com.ddz.mearchant.models.Orderdata;
import com.ddz.mearchant.view.HandyTextView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;


public class MyOrderListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<OrderBase> orderBaseLists = new ArrayList<OrderBase>();
    private boolean isSelectAll = false;
    private ProgressDialog progressDialog;
    private int total = 0;
    private int position = 0;
    private String totalMoney = "0";
    private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_PAYING_FLAG = 2;
	private static final int SDK_ERROR_FLAG = 3;
	private String payInfo;
	private int posioioms = -1;
    protected LoadingDialog dialog = null;
    private BaseApplication baseApplication = BaseApplication.getInstance();
    public MyOrderListAdapter(Context context) {
        mContext = context;
        initDialog();
    }

    public void setList(ArrayList<OrderBase> orderLists, int position) {
        this.orderBaseLists = orderLists;
        this.position = position;
//        setSettleInfo();
    }


    public OnClickListener getAdapterListener() {
        return listener;
    }

    
    public int getGroupCount() {
        return orderBaseLists.size();
    }

    
    public int getChildrenCount(int groupPosition) {
        return orderBaseLists.get(groupPosition).getGoods_list().size();
    }

    
    public Object getGroup(int groupPosition) {
        return orderBaseLists.get(groupPosition);
    }

    
    public Object getChild(int groupPosition, int childPosition) {
        return orderBaseLists.get(groupPosition).getGoods_list().get(childPosition);
    }

    
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    
    public boolean hasStableIds() {
        return false;
    }
    public int getOrderList() {
        return orderBaseLists.size();
    }
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder = null;
        if (convertView == null) {
            holder = new GroupViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_order_code_show, parent, false);
            holder.orderCode = (HandyTextView) convertView.findViewById(R.id.order_code);
            holder.orderStatus = (HandyTextView) convertView.findViewById(R.id.order_status);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        holder.orderCode.setText(orderBaseLists.get(groupPosition).getTrade_id());
        if (orderBaseLists.get(groupPosition).getOrder_status()!=null){
            Integer status = Integer.valueOf(orderBaseLists.get(groupPosition).getOrder_status());
            switch (status) {
                case 0:
                    holder.orderStatus.setText("待收货");
                    break;
                case 1:
                    holder.orderStatus.setText("已完成");
                    break;
                case 2:
                    holder.orderStatus.setText("待付款");
                    break;
                case 3:
                    holder.orderStatus.setText("待发货");
                    break;
                case 4:
                    holder.orderStatus.setText("商家关闭订单");
                    break;
                case 5:
                    holder.orderStatus.setText("订单交易关闭");
                    break;
                case 6:
                    holder.orderStatus.setText("退款中");
                    break;
            }
        }
        holder.orderCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
        return convertView;
    }

    /**
     * child view
     */

    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder = null;
        if (convertView == null) {
            holder = new ChildViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_order_list, parent, false);
            holder.orderLinear = (LinearLayout) convertView.findViewById(R.id.to_detail_linear);
			holder.orderImage = (ImageView) convertView.findViewById(R.id.order_image);
            holder.orderName = (HandyTextView) convertView.findViewById(R.id.order_name);
            holder.orderPrice = (HandyTextView) convertView.findViewById(R.id.order_price);
            holder.orderType = (HandyTextView) convertView.findViewById(R.id.type);
            holder.orderColor = (HandyTextView) convertView.findViewById(R.id.order_color);
            holder.orderButton = (Button) convertView.findViewById(R.id.order_button);
            holder.orderButton2 = (Button) convertView.findViewById(R.id.order_button2);
            holder.dividerShow  = (View) convertView.findViewById(R.id.divider_show);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        if (groupPosition == 0 && childPosition == 0) {
        	total = 0;
		}
        Orderdata orderdata = orderBaseLists.get(groupPosition).getGoods_list().get(childPosition);
//		holder.orderLinear.setTag(orderdata);
		final String orderNo = orderBaseLists.get(groupPosition).getTrade_id();
        holder.orderLinear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                  getOrderDetailById(orderBaseLists.get(groupPosition).getTrade_id());
//                Orderdata orderdata = (Orderdata) v.getTag();
			}
		});

        if (isLastChild) {
            if (orderBaseLists.get(groupPosition).getOrder_status().equals("3")){
                holder.orderButton.setVisibility(View.VISIBLE);
                holder.orderButton.setText("关闭订单");
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                holder.orderButton2.setVisibility(View.VISIBLE);
//                layoutParams.width = 280;
//                layoutParams.height = 84;
                holder.orderButton2.setText("录入物流单号");
                holder.orderButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getOrderCloseById(orderBaseLists.get(groupPosition).getTrade_id());
                    }
                });
                holder.orderButton2.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getOrderDetailById(orderBaseLists.get(groupPosition).getTrade_id());
                    }
                });
            }else if(orderBaseLists.get(groupPosition).getOrder_status().equals("0")||orderBaseLists.get(groupPosition).getOrder_status().equals("1")){
                holder.orderButton2.setVisibility(View.VISIBLE);
                holder.orderButton2.setText("查看物流");
                holder.orderButton.setVisibility(View.GONE);
                holder.orderButton2.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getOrderLogisticsById(orderBaseLists.get(groupPosition).getTrade_id());
                    }
                });
            }else {
                holder.orderButton.setVisibility(View.GONE);
                holder.orderButton2.setVisibility(View.GONE);
            }
            holder.dividerShow.setVisibility(View.VISIBLE);
		}else{
            holder.orderButton.setVisibility(View.GONE);
            holder.orderButton2.setVisibility(View.GONE);
            holder.dividerShow.setVisibility(View.GONE);
		}
        holder.orderName.setText(orderdata.getGoods_name());
        String price = String.valueOf(orderdata.getGoods_fee());
        SpannableString styledText = new SpannableString(String.valueOf(orderdata.getGoods_fee()));
        styledText.setSpan(new AbsoluteSizeSpan(40), 0, price.indexOf("."), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new AbsoluteSizeSpan(30), price.indexOf("."), styledText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.orderPrice.setText(styledText, TextView.BufferType.SPANNABLE);
//        holder.orderPrice.setText(String.valueOf(orderdata.getGoods_fee()));
		holder.orderColor.setText(orderdata.getAttr_value());
        if (orderdata.getProfit()!=null){
            Integer profit = Integer.valueOf(orderdata.getProfit());
            switch (profit){
                case 1:
                    holder.orderType.setText("20%");
                    break;
                case 2:
                    holder.orderType.setText("10%");
                    break;
                case 5:
                    holder.orderType.setText("5%");
                    break;
            }
        }
        Glide.with(mContext).load(orderBaseLists.get(groupPosition).getGoods_pic_uri()+orderdata.getGoods_thumb()).fitCenter()
                .placeholder(R.drawable.no_image).error(R.drawable.no_image).into(holder.orderImage);
//        try {
//            ImageUrl.setbitmap(orderBaseLists.get(groupPosition).getGoods_pic_uri()+orderdata.getGoods_thumb(),holder.orderImage);
//        }catch (IOException e){
//            e.printStackTrace();
//        }

        return convertView;
    }


    OnClickListener listener = new OnClickListener() {
        
        public void onClick(View v) {
            switch (v.getId()) {
            }
        }
    };

    class GroupViewHolder {
        HandyTextView orderCode;
		HandyTextView orderStatus;
    }

    class ChildViewHolder {
		ImageView orderImage;
		HandyTextView orderPrice;
		HandyTextView orderType;
		HandyTextView orderColor;
        Button orderButton,orderButton2;
		HandyTextView orderName;
		LinearLayout orderLinear;
        View dividerShow;
    }
    private void getOrderLogisticsById(String trade_id){
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<OrderDetailBase>> call = userBiz.orderDetail(baseApplication.mUser.token,Integer.valueOf(trade_id));//"18813904075:123456789"
        call.enqueue(new HttpCallBack<BaseResponse<OrderDetailBase>>() {

            @Override
            public void onResponse(Call<BaseResponse<OrderDetailBase>> arg0,
                                   Response<BaseResponse<OrderDetailBase>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<OrderDetailBase> baseResponse = response.body();
                if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
                    String status = baseResponse.getStatus();
                    OrderDetailBase data = baseResponse.getData();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("orderDetail",data);
                    intent.putExtra("detail",bundle);
                    intent.setClass(mContext,OrderQueryLogisticsActivity.class);
                    mContext.startActivity(intent);
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<OrderDetailBase>> arg0,
                                  Throwable arg1) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onFailure(arg0,arg1);
            }
        });

    }
    private void initDialog() {
        dialog = new LoadingDialog(mContext, R.style.dialog, "请稍候...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
    }
    private void getOrderDetailById(String trade_id){
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<OrderDetailBase>> call = userBiz.orderDetail(baseApplication.mUser.token,Integer.valueOf(trade_id));//"18813904075:123456789"
        call.enqueue(new HttpCallBack<BaseResponse<OrderDetailBase>>() {

            @Override
            public void onResponse(Call<BaseResponse<OrderDetailBase>> arg0,
                                   Response<BaseResponse<OrderDetailBase>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<OrderDetailBase> baseResponse = response.body();
                if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
                    String status = baseResponse.getStatus();
                    OrderDetailBase data = baseResponse.getData();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("orderDetail",data);
                    intent.putExtra("detail",bundle);
                    intent.setClass(mContext,OrderDetailActivity.class);
                    mContext.startActivity(intent);
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<OrderDetailBase>> arg0,
                                  Throwable arg1) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onFailure(arg0,arg1);
            }
        });

    }
    private void getOrderCloseById(String trade_id){
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<OrderDetailBase>> call = userBiz.orderDetail(baseApplication.mUser.token,Integer.valueOf(trade_id));//"18813904075:123456789"
        call.enqueue(new HttpCallBack<BaseResponse<OrderDetailBase>>() {

            @Override
            public void onResponse(Call<BaseResponse<OrderDetailBase>> arg0,
                                   Response<BaseResponse<OrderDetailBase>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<OrderDetailBase> baseResponse = response.body();
                if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
                    String status = baseResponse.getStatus();
                    OrderDetailBase data = baseResponse.getData();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("orderDetail",data);
                    intent.putExtra("detail",bundle);
                    intent.setClass(mContext,OrderCloseActivity.class);
                    mContext.startActivity(intent);
                }else{
                    Toast.makeText(mContext,"未找到订单详情，请稍后再试",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<OrderDetailBase>> arg0,
                                  Throwable arg1) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onFailure(arg0,arg1);
            }
        });

    }
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
