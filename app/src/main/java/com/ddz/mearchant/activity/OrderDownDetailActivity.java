package com.ddz.mearchant.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.models.OrderDownDetail;
import com.ddz.mearchant.view.HandyTextView;

/**
 * Created by Administrator on 2017/7/7 0007.
 */

public class OrderDownDetailActivity extends BaseActivity implements View.OnClickListener{
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private HandyTextView consumptionAmount,incentivePoints,paymentAccount;
    private HandyTextView tradingHours,orderCode,payRreturn;
    private HandyTextView payType,payAcount;
    private OrderDownDetail orderDownDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_down_detail);
        orderDownDetail = (OrderDownDetail) getIntent().getBundleExtra("orderDownDetail").getSerializable("orderDownDetail");
        initViews();
    }

    @Override
    protected void initViews() {
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        consumptionAmount = (HandyTextView)findViewById(R.id.consumption_amount);
        incentivePoints = (HandyTextView)findViewById(R.id.incentive_points);
        paymentAccount = (HandyTextView)findViewById(R.id.payment_account);
        tradingHours = (HandyTextView)findViewById(R.id.trading_hours);
        orderCode = (HandyTextView)findViewById(R.id.order_code);
        payRreturn = (HandyTextView)findViewById(R.id.pay_return);
        payType = (HandyTextView)findViewById(R.id.pay_type);
        payAcount = (HandyTextView)findViewById(R.id.pay_acount);
        htvCenter.setText("订单详情");
        htvLeft.setOnClickListener(this);
        if (orderDownDetail!=null){
            consumptionAmount.setText(orderDownDetail.getMoney());
            incentivePoints.setText(orderDownDetail.getPoints());
            paymentAccount.setText(orderDownDetail.getName());
            tradingHours.setText(orderDownDetail.getAdd_time());
            orderCode.setText(orderDownDetail.getOrder_id());
            payRreturn.setText(orderDownDetail.getPay_return_id());
            payType.setText(orderDownDetail.getPay_type());
            payAcount.setText(orderDownDetail.getMobile());

        }
    }

    @Override
    protected void initEvents() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_left:
                defaultFinish();
                break;
        }
    }
}
