package com.ddz.mearchant.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.ddz.mearchant.BaseFragment;
import com.ddz.mearchant.R;
import com.ddz.mearchant.activity.OrderDownDetailActivity;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.dialog.DialogCalendar;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.OrderDownBase;
import com.ddz.mearchant.models.OrderDownDetail;
import com.ddz.mearchant.utils.CommonUtils;
import com.ddz.mearchant.view.HandyTextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class SearchDownlineOrderFragment extends BaseFragment implements View.OnClickListener{
	//定义一个布局
	private LayoutInflater layoutInflater;
	//定义FragmentTabHost对象
	private FragmentTabHost mTabHost;
	private View view;
	private ListView orderDownList;
	//Tab选项卡的文字
	private LinearLayout noListShow;

	private String mTextviewArray[] = {"线上", "线下"};
	private ArrayList<OrderDownBase> orderDownBase = new ArrayList<OrderDownBase>();
	private OrderAdapter orderAdapter;
	private HandyTextView searchAction,searchStartTime,searchEndTime;

	public SearchDownlineOrderFragment(){
		super();
	}

	@Override
	protected void initViews() {
		orderDownList = (ListView)view.findViewById(R.id.order_down_list);
		searchStartTime = (HandyTextView) view.findViewById(R.id.search_start_time);
		searchEndTime = (HandyTextView) view.findViewById(R.id.search_end_time);
		searchAction = (HandyTextView) view.findViewById(R.id.search_action);
		noListShow = (LinearLayout)view.findViewById(R.id.no_list_show);

		searchStartTime.setText(CommonUtils.sdfDatePoint.format(new Date()));
		searchEndTime.setText(CommonUtils.sdfDatePoint.format(new Date()));
		searchStartTime.setOnClickListener(this);
		searchEndTime.setOnClickListener(this);
		searchAction.setOnClickListener(this);

		orderAdapter = new OrderAdapter();
		orderDownList.setAdapter(orderAdapter);
		orderDownList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				getOrderDownDetailById(orderDownBase.get(position).getPay_id());
			}
		});
	}
	private void getOrderDownDetailById(int orderId){
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<OrderDownDetail>>	call = userBiz.orderLineDetail(baseApplication.mUser.token,orderId);
		call.enqueue(new HttpCallBack<BaseResponse<OrderDownDetail>>() {

			@Override
			public void onResponse(Call<BaseResponse<OrderDownDetail>> arg0,
								   Response<BaseResponse<OrderDownDetail>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				BaseResponse<OrderDownDetail> baseResponse = response.body();
				if (null != baseResponse) {
					String status = baseResponse.getStatus();
					OrderDownDetail data = baseResponse.getData();
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable("orderDownDetail",data);
					intent.putExtra("orderDownDetail",bundle);
					intent.setClass(getActivity(), OrderDownDetailActivity.class);
					startActivity(intent);
				}
			}

			@Override
			public void onFailure(Call<BaseResponse<OrderDownDetail>> arg0,
								  Throwable arg1) {
			}
		});

	}
	@Override
	protected void init() {

	}

	@Override
	protected void initEvents() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.search_downline_order_fragment, container, false);
		initViews();
		initDialog();
		queryOrderDownList(1);
		return view;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.search_start_time:
				showCalender(searchStartTime);
				break;
			case R.id.search_end_time:
				showCalender(searchEndTime);
				break;
			case R.id.search_action:
				try {
					Long beginTime = CommonUtils.getLongFromString(searchStartTime.getText().toString()+ " " +"00:00:00");
					Long endTime = CommonUtils.getLongFromString(searchEndTime.getText().toString()+ " " +"23:59:59");
					queryOrderDownListByTime(1,beginTime.intValue(),endTime.intValue());
				}catch (ParseException e){
					e.printStackTrace();
				}
				break;
		}
	}
	public void queryOrderDownList(final Integer page){
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<List<OrderDownBase>>>	call = userBiz.getOrderDownList(baseApplication.mUser.token,page);
		call.enqueue(new HttpCallBack<BaseResponse<List<OrderDownBase>>>() {

			@Override
			public void onResponse(Call<BaseResponse<List<OrderDownBase>>> arg0,
								   Response<BaseResponse<List<OrderDownBase>>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onResponse(arg0,response);
				BaseResponse<List<OrderDownBase>> baseResponse = response.body();
				if (null != baseResponse) {
					String status = baseResponse.getStatus();
					List<OrderDownBase> data = baseResponse.getData();
					if (data.size()>0){
						orderDownBase.addAll(data);
						orderDownList.setVisibility(View.VISIBLE);
						noListShow.setVisibility(View.GONE);
						orderAdapter.notifyDataSetChanged();
					}else{
						orderDownList.setVisibility(View.GONE);
						noListShow.setVisibility(View.VISIBLE);
					}
				}else {
				}
			}

			@Override
			public void onFailure(Call<BaseResponse<List<OrderDownBase>>> arg0,
								  Throwable arg1) {
			}
		});
	}
	public void queryOrderDownListByTime(Integer page,Integer beginTime,Integer endTime){
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<List<OrderDownBase>>>	call = userBiz.getOrderDownListByTime(baseApplication.mUser.token,page,beginTime,endTime);
		call.enqueue(new HttpCallBack<BaseResponse<List<OrderDownBase>>>() {

			@Override
			public void onResponse(Call<BaseResponse<List<OrderDownBase>>> arg0,
								   Response<BaseResponse<List<OrderDownBase>>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onResponse(arg0,response);
				BaseResponse<List<OrderDownBase>> baseResponse = response.body();
				if (null != baseResponse) {
					orderDownBase.clear();
					orderAdapter.notifyDataSetChanged();
					String status = baseResponse.getStatus();
					List<OrderDownBase> data = baseResponse.getData();
					if (data.size()>0){
						orderDownBase.addAll(data);
						orderDownList.setVisibility(View.VISIBLE);
						noListShow.setVisibility(View.GONE);
						orderAdapter.notifyDataSetChanged();
					}else{
						orderDownList.setVisibility(View.GONE);
						noListShow.setVisibility(View.VISIBLE);
					}
				}

			}

			@Override
			public void onFailure(Call<BaseResponse<List<OrderDownBase>>> arg0,
								  Throwable arg1) {
			}
		});
	}
	private void showCalender(final HandyTextView textView) {
		new DialogCalendar().showDialog(getActivity(),new DialogCalendar.onConfirmClickedListener() {
			@Override
			public void onClick(Date selectedDate) {

				String dateStr;
				if(selectedDate != null) {
					dateStr = CommonUtils.sdfDatePoint.format(selectedDate);
					textView.setText(dateStr);
				} else {
					Date date = new Date();
					dateStr = CommonUtils.sdfDatePoint.format(date);
					textView.setText(dateStr);
				}
			}
		});
	}
	@Override
	public void onResume() {
		super.onResume();

	}
	
	@Override
	public void onPause() {
		super.onPause();

	}

	class OrderAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		@Override
		public int getCount() {
			return orderDownBase.size();
		}

		@Override
		public Object getItem(int position) {
			return orderDownBase.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null){
				convertView = View.inflate(getActivity(),R.layout.adapter_order_down_list,null);
				holder = new ViewHolder();
				holder.mPic= (ImageView) convertView.findViewById(R.id.saler_pic);
				holder.mName = (HandyTextView) convertView.findViewById(R.id.saler_name);
				holder.mPrice = (HandyTextView) convertView.findViewById(R.id.price);
				holder.mTime = (HandyTextView) convertView.findViewById(R.id.time);
				holder.mOrderCode = (HandyTextView) convertView.findViewById(R.id.order_code);
				holder.mProyit = (HandyTextView) convertView.findViewById(R.id.proyit);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			try {
//				ImageUrl.setbitmap(orderDownBase.get(position).getHeadpic(),holder.mPic);
				holder.mName.setText(orderDownBase.get(position).getNickname());
				holder.mPrice.setText(orderDownBase.get(position).getMoney());
				holder.mTime.setText(orderDownBase.get(position).getAdd_time().substring(0,10));
				holder.mOrderCode.setText(orderDownBase.get(position).getOrder_id());
				switch (orderDownBase.get(position).getProfit_id()){
					case 1:
						holder.mProyit.setText("20%");
						break;
					case 2:
						holder.mProyit.setText("10%");
						break;
					case 5:
						holder.mProyit.setText("5%");
						break;
				}
				Glide.with(getActivity()).load(Constants.ALI_PRODUCT_LOAD+orderDownBase.get(position).getHeadpic()).fitCenter()
						.placeholder(getResources().getColor(R.color.tab_text_red)).error(R.drawable.login_icon).into(holder.mPic);
			}catch (Exception e){
				e.printStackTrace();
			}
			return convertView;
		}
	}

	public  class ViewHolder{
		private ImageView mPic;
		private HandyTextView mName;
		private HandyTextView mPrice;
		private HandyTextView mTime;
		private HandyTextView mOrderCode;
		private HandyTextView mProyit;

	}
	
}
