package com.ddz.mearchant.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ddz.mearchant.BaseFragment;
import com.ddz.mearchant.R;
import com.ddz.mearchant.activity.ExchangeActivity;
import com.ddz.mearchant.activity.GivingFriendActivity;
import com.ddz.mearchant.activity.GreatDetailActivity;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.EarnBase;
import com.ddz.mearchant.view.HandyTextView;

import retrofit2.Call;
import retrofit2.Response;


public class EarningsFragment extends BaseFragment implements View.OnClickListener{
	private View view;
	private LinearLayout exchangeLinear,greatDetailLinear,givingLinear;
	private HandyTextView totalSavingPoints,incentivePoints,greatChapter;
	private HandyTextView onlineCumulativeTurnover,onlineGreatChapter,onlineIncentivePoints;
	private HandyTextView downlineCumulativeTurnover,downlineGreatChapter,downlineIncentivePoints;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.commodity_takefragment, container, false);
		initViews();
		initDialog();
		initEvents();
		return view;
	}

	@Override
	protected void init() {

	}

	@Override
	protected void initViews() {
		exchangeLinear = (LinearLayout)view.findViewById(R.id.exchange_linear);
		totalSavingPoints = (HandyTextView)view.findViewById(R.id.total_saving_points);
		incentivePoints = (HandyTextView)view.findViewById(R.id.incentive_points);
		greatChapter = (HandyTextView)view.findViewById(R.id.great_chapter);
		onlineCumulativeTurnover = (HandyTextView)view.findViewById(R.id.online_cumulative_turnover);
		onlineGreatChapter = (HandyTextView)view.findViewById(R.id.online_great_chapter);
		onlineIncentivePoints = (HandyTextView)view.findViewById(R.id.online_incentive_points);
		downlineCumulativeTurnover = (HandyTextView)view.findViewById(R.id.downline_cumulative_turnover);
		downlineGreatChapter = (HandyTextView)view.findViewById(R.id.downline_great_chapter);
		downlineIncentivePoints = (HandyTextView)view.findViewById(R.id.downline_incentive_points);
		greatDetailLinear = (LinearLayout)view.findViewById(R.id.great_detail_in_chapter);
		givingLinear  = (LinearLayout)view.findViewById(R.id.giving_linear);
		exchangeLinear.setOnClickListener(this);
		greatDetailLinear.setOnClickListener(this);
		givingLinear.setOnClickListener(this);
	}

	@Override
	protected void initEvents() {
		getEarnIngInfo();
	}

	public EarningsFragment(){
		super();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.exchange_linear:
					startActivity(ExchangeActivity.class);
				break;
			case R.id.great_detail_in_chapter:
				startActivity(GreatDetailActivity.class);
				break;
			case R.id.giving_linear:
				startActivity(GivingFriendActivity.class);
				break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();

	}
	
	@Override
	public void onPause() {
		super.onPause();

	}

	private void getEarnIngInfo() {
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<EarnBase>> call = userBiz.getEarnIngInfo(baseApplication.mUser.token);
		call.enqueue(new HttpCallBack<BaseResponse<EarnBase>>() {
			@Override
			public void onResponse(Call<BaseResponse<EarnBase>> arg0,
								   Response<BaseResponse<EarnBase>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onResponse(arg0,response);
				BaseResponse<EarnBase> baseResponse = response.body();
				if (null != baseResponse) {
					String status = baseResponse.getStatus();
					if (status.equals(Constants.T_OK)){
						EarnBase data = baseResponse.getData();
						if (data!=null){
							totalSavingPoints.setText(data.getAccount().getCash());
							incentivePoints.setText(data.getAccount().getPoints());
							greatChapter.setText(data.getAccount().getPraise());
							onlineCumulativeTurnover.setText(data.getOnline().getTotal_trade());
							onlineGreatChapter.setText(data.getOnline().getPraise());
							onlineIncentivePoints.setText(data.getOnline().getPoints());
							downlineCumulativeTurnover.setText(data.getLine().getTotal_trade());
							downlineGreatChapter.setText(data.getLine().getPraise());
							downlineIncentivePoints.setText(data.getLine().getPoints());
						}
					}
				}

			}

			@Override
			public void onFailure(Call<BaseResponse<EarnBase>> arg0,
								  Throwable arg1) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onFailure(arg0,arg1);
			}
		});

	}
}
