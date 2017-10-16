package com.ddz.mearchant.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.ddz.mearchant.BaseFragment;
import com.ddz.mearchant.R;
import com.ddz.mearchant.activity.OrderDownDetailActivity;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.OrderDownBase;
import com.ddz.mearchant.models.OrderDownDetail;
import com.ddz.mearchant.models.OrderRecord;
import com.ddz.mearchant.view.HandyTextView;
import com.ddz.mearchant.view.PullToRefreshLayout;
import com.ddz.mearchant.view.PullableListView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class DownlineOrderFragment extends BaseFragment implements PullToRefreshLayout.OnRefreshListener{
	//定义一个布局
	private LayoutInflater layoutInflater;
	//定义FragmentTabHost对象
	private FragmentTabHost mTabHost;
	private View view;
	private PullableListView orderDownList;
	private LinearLayout noListShow;
	private ArrayList<OrderDownBase> orderDownBase = new ArrayList<OrderDownBase>();
	private OrderAdapter orderAdapter;
	private HandyTextView sevenDownNum,totalDownNum;
	private PullToRefreshLayout layout;
	private int refreshCount = 1;
	private boolean pull = false;
	private boolean isLastRecord = false;
	//Tab选项卡的文字
	private String mTextviewArray[] = {"线上", "线下"};
	public DownlineOrderFragment(){
		super();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.down_order_fragment, container, false);
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
		layout = (PullToRefreshLayout)view.findViewById(R.id.product_refresh_view);
		sevenDownNum = (HandyTextView)view.findViewById(R.id.seven_down_num);
		totalDownNum = (HandyTextView)view.findViewById(R.id.total_down_num);
		orderDownList = (PullableListView)view.findViewById(R.id.order_down_list);
		noListShow = (LinearLayout)view.findViewById(R.id.no_list_show);
		layout.setOnRefreshListener(this);
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
	protected void initEvents() {
		orderDownBase.clear();
		queryOrderDownRecord();
		queryOrderDownList(1);
	}

	@Override
	public void onResume() {
		super.onResume();

	}
	
	@Override
	public void onPause() {
		super.onPause();

	}
	public void queryOrderDownList(final Integer page){
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<List<OrderDownBase>>>	call = userBiz.getOrderDownList(baseApplication.mUser.token,page);
		call.enqueue(new HttpCallBack<BaseResponse<List<OrderDownBase>>>() {

			@Override
			public void onResponse(Call<BaseResponse<List<OrderDownBase>>> arg0,
								   Response<BaseResponse<List<OrderDownBase>>> response) {
				BaseResponse<List<OrderDownBase>> baseResponse = response.body();
				if (null != baseResponse) {
					String status = baseResponse.getStatus();
					List<OrderDownBase> data = baseResponse.getData();
					if (data.size()>0){
						orderDownList.setPullUp(false);
						orderDownBase.addAll(data);
						layout.setVisibility(View.VISIBLE);
						noListShow.setVisibility(View.GONE);
						orderAdapter.notifyDataSetChanged();
						if (orderDownBase.size() < 10){
							isLastRecord = true;
							orderDownList.setPullUp(true);
						}
					}else{
						if (pull){
							isLastRecord = true;
							orderDownList.setPullUp(true);
						}else{
							layout.setVisibility(View.GONE);
							noListShow.setVisibility(View.VISIBLE);
						}
					}

				}
			}

			@Override
			public void onFailure(Call<BaseResponse<List<OrderDownBase>>> arg0,
								  Throwable arg1) {
			}
		});
	}
	public void queryOrderDownRecord(){
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<OrderRecord>>	call = userBiz.getOrderDownRecord(baseApplication.mUser.token);
		call.enqueue(new HttpCallBack<BaseResponse<OrderRecord>>() {

			@Override
			public void onResponse(Call<BaseResponse<OrderRecord>> arg0,
								   Response<BaseResponse<OrderRecord>> response) {
				BaseResponse<OrderRecord> baseResponse = response.body();
				if (null != baseResponse) {
					String status = baseResponse.getStatus();
					if(status.equals(Constants.T_OK)){
						OrderRecord data = baseResponse.getData();
						totalDownNum.setText(String.valueOf(data.getTotal()));
						sevenDownNum.setText(String.valueOf(data.getWeek()));
					}

				}

			}

			@Override
			public void onFailure(Call<BaseResponse<OrderRecord>> arg0,
								  Throwable arg1) {
			}
		});
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
						.placeholder(R.drawable.default_user).error(R.drawable.default_user).into(holder.mPic);
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
	private void initpullView(){
		refreshCount = 1;
		isLastRecord = false;
		orderDownList.setPullUp(false);
		pull = false;
	}
	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
		// 下拉刷新操作
		orderDownBase.clear();
		initpullView();
		orderAdapter.notifyDataSetChanged();
		queryOrderDownList(1);
		pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
	}
	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
		// 加载操作
		if (!isLastRecord) {
			refreshCount++;
			pull = true;
			queryOrderDownList(refreshCount);
		}
		pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
	}
}
