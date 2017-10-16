package com.ddz.mearchant.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.ddz.mearchant.BaseFragment;
import com.ddz.mearchant.R;
import com.ddz.mearchant.activity.OrderDetailActivity;
import com.ddz.mearchant.adapter.MyOrderListAdapter;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.OrderBase;
import com.ddz.mearchant.models.OrderCollection;
import com.ddz.mearchant.models.OrderDetailBase;
import com.ddz.mearchant.models.OrderRecord;
import com.ddz.mearchant.view.HandyTextView;
import com.ddz.mearchant.view.PullToRefreshLayout;
import com.ddz.mearchant.view.PullableExpandableListView;
import com.ddz.mearchant.view.PullableScrollView;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class OnlineOrderFragment extends BaseFragment implements View.OnClickListener,PullToRefreshLayout.OnRefreshListener
,PullableScrollView.OnScrollListener{
	//定义一个布局
	private LayoutInflater layoutInflater;
	//定义FragmentTabHost对象
	private FragmentTabHost mTabHost;
	private MyOrderListAdapter myOrderListAdapter;
	private PullableExpandableListView orderList;
	private HandyTextView allTapText,payTapText,sendTapText,receiveTapText,completeTapText;
	private ArrayList<OrderBase> orderBase = new ArrayList<OrderBase>();
	private List<OrderCollection> orders = new ArrayList<OrderCollection>();
	private LinearLayout zhuangtaiView,allTapLiner,payTapLiner,sendTapLiner,receiveTapLiner,completeTapLiner,noListShow;
	private ImageView allTapImage,payTapImage,sendTapImage,receiveTapImage,completeTapImage;
	private HandyTextView cumulativeOrdersNum,sevenDaysOrdersNum,sendOrdersNum;
	private PullToRefreshView mPullToRefreshView;
	private PullToRefreshLayout layout;
	private int orderType = 1;
	private boolean isLastRecord = false;
	private View view;
	private PullableScrollView pullScrolView;
	private boolean isCreated = false;
	private int mLastMotionY;
	private int searchLayoutTop;
	private LinearLayout search01,search02,rlayout;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isCreated = true;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.online_order_fragment, container, false);
		orderBase.clear();
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
		search01 = (LinearLayout)view.findViewById(R.id.search01);
		search02 = (LinearLayout)view.findViewById(R.id.search02);
		rlayout = (LinearLayout)view.findViewById(R.id.rlayout);
		zhuangtaiView = (LinearLayout)view.findViewById(R.id.zhuangtai_view);
		layout = (PullToRefreshLayout)view.findViewById(R.id.product_refresh_view);
		pullScrolView = (PullableScrollView)view.findViewById(R.id.pull_scrol_view);
		allTapLiner = (LinearLayout)view.findViewById(R.id.all_tap_linear);
		payTapLiner = (LinearLayout)view.findViewById(R.id.pay_tap_linear);
		sendTapLiner = (LinearLayout)view.findViewById(R.id.send_tap_linear);
		receiveTapLiner = (LinearLayout)view.findViewById(R.id.receive_tap_linear);
		completeTapLiner = (LinearLayout)view.findViewById(R.id.complete_tap_linear);
		allTapImage = (ImageView)view.findViewById(R.id.all_tap_image);
		payTapImage = (ImageView)view.findViewById(R.id.pay_tap_image);
		sendTapImage = (ImageView)view.findViewById(R.id.send_tap_image);
		receiveTapImage = (ImageView)view.findViewById(R.id.receive_tap_image);
		completeTapImage = (ImageView)view.findViewById(R.id.complete_tap_image);
		allTapText = (HandyTextView)view.findViewById(R.id.all_tap_text);
		payTapText = (HandyTextView)view.findViewById(R.id.pay_tap_text);
		sendTapText = (HandyTextView)view.findViewById(R.id.send_tap_text);
		receiveTapText = (HandyTextView)view.findViewById(R.id.receive_tap_text);
		completeTapText = (HandyTextView)view.findViewById(R.id.complete_tap_text);
		orderList = (PullableExpandableListView)view.findViewById(R.id.order_list);
		cumulativeOrdersNum = (HandyTextView)view.findViewById(R.id.cumulative_orders_num);
		sevenDaysOrdersNum = (HandyTextView)view.findViewById(R.id.seven_days_orders_num);
		sendOrdersNum = (HandyTextView)view.findViewById(R.id.send_orders_num);
		noListShow = (LinearLayout)view.findViewById(R.id.no_list_show);
		allTapLiner.setOnClickListener(this);
		payTapLiner.setOnClickListener(this);
		sendTapLiner.setOnClickListener(this);
		receiveTapLiner.setOnClickListener(this);
		completeTapLiner.setOnClickListener(this);
		layout.setOnRefreshListener(this);
		allTapText.setTextColor(getResources().getColor(R.color.tab_line_color));
		allTapImage.setImageResource(R.drawable.home_all1);
//		orderAdapter = new OrderAdapter();
		myOrderListAdapter = new MyOrderListAdapter(getActivity());
		orderList.setAdapter(myOrderListAdapter);
		orderList.setGroupIndicator(null);
		orderList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				getOrderDetailById(orderBase.get(groupPosition).getTrade_id());
				return true;
			}
		});
		pullScrolView.setOnScrollListener(this);
	}
//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//		super.onWindowFocusChanged(hasFocus);
//		if(hasFocus){
//			searchLayoutTop = rlayout.getBottom();//获取searchLayout的顶部位置
//		}
//	}
	//监听滚动Y值变化，通过addView和removeView来实现悬停效果
	@Override
	public void onScroll(int scrollY) {
		searchLayoutTop = rlayout.getBottom();//获取searchLayout的顶部位置
		if(scrollY >= searchLayoutTop){
			if (zhuangtaiView.getParent()!=search01) {
				search02.removeView(zhuangtaiView);
				search01.addView(zhuangtaiView);
				initSmotth = true;
			}
		}else{
			if (zhuangtaiView.getParent()!=search02) {
				search01.removeView(zhuangtaiView);
				search02.addView(zhuangtaiView);
				initSmotth = false;
			}
		}
	}
private boolean initSmotth = false;
	@Override
	protected void initEvents() {
		initpullView();
		queryOrderRecord();
		queryOrderList(refreshCount);
	}

	private void updateListView(ArrayList<OrderBase> orderBases, int position) {
		myOrderListAdapter.setList(orderBases,position);
		myOrderListAdapter.notifyDataSetChanged();
		expandAllGroup();
		setListViewHeightBasedOnChildren(orderList);
		if (!pull){
			pullScrolView.smoothScrollTo(0,0);
			if (search01.getChildAt(0)!=null&&search02.getChildAt(0) == null){
				search01.removeView(zhuangtaiView);
				search02.addView(zhuangtaiView);
			}
		}

	}

	/**

	 * 展开所有组

	 */

	private void expandAllGroup() {

		for (int i = 0; i < orderBase.size(); i++) {

			orderList.expandGroup(i);

		}

	}
	public void queryOrderRecord(){
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<OrderRecord>>	call = userBiz.getOrderRecord(baseApplication.mUser.token);
		call.enqueue(new HttpCallBack<BaseResponse<OrderRecord>>() {

			@Override
			public void onResponse(Call<BaseResponse<OrderRecord>> arg0,
								   Response<BaseResponse<OrderRecord>> response) {
				BaseResponse<OrderRecord> baseResponse = response.body();
				if (null != baseResponse) {
					String status = baseResponse.getStatus();
					OrderRecord data = baseResponse.getData();
//					orderBase.addAll(data);
//					updateListView(orderBase,orderType);
					cumulativeOrdersNum.setText(String.valueOf(data.getTotal()));
					sevenDaysOrdersNum.setText(String.valueOf(data.getWeek()));
					sendOrdersNum.setText(String.valueOf(data.getDelivery()));
				}

			}

			@Override
			public void onFailure(Call<BaseResponse<OrderRecord>> arg0,
								  Throwable arg1) {
			}
		});
	}
	//全部
	public void queryOrderList(final Integer page){
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<List<OrderBase>>>	call = userBiz.getOrderAllList(baseApplication.mUser.token,page,Constants.PAGE);
		call.enqueue(new HttpCallBack<BaseResponse<List<OrderBase>>>() {

			@Override
			public void onResponse(Call<BaseResponse<List<OrderBase>>> arg0,
								   Response<BaseResponse<List<OrderBase>>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onResponse(arg0,response);
				BaseResponse<List<OrderBase>> baseResponse = response.body();
				if (null != baseResponse) {
					String status = baseResponse.getStatus();
					if (baseResponse.getData()!=null) {
						List<OrderBase> data = baseResponse.getData();
						if (data.size()>0){
							pullScrolView.setPullUp(false);
							orderBase.addAll(data);
							orderList.setVisibility(View.VISIBLE);
							noListShow.setVisibility(View.GONE);
							updateListView(orderBase,orderType);
							if (orderBase.size() < 10){
								isLastRecord = true;
								pullScrolView.setPullUp(true);
							}
						}else{
							if (pull){
								isLastRecord = true;
								pullScrolView.setPullUp(true);
							}else{
								orderList.setVisibility(View.GONE);
								noListShow.setVisibility(View.VISIBLE);
								pullScrolView.setPullUp(true);
							}
						}

					}
				}else{
				}


			}

			@Override
			public void onFailure(Call<BaseResponse<List<OrderBase>>> arg0,
								  Throwable arg1) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onFailure(arg0,arg1);
			}
		});
	}
	//待发货
	public void queryOrdeDeliveryrList(final Integer page){
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<List<OrderBase>>>	call = userBiz.getOrderDeliveryList(baseApplication.mUser.token,page);
		call.enqueue(new HttpCallBack<BaseResponse<List<OrderBase>>>() {

			@Override
			public void onResponse(Call<BaseResponse<List<OrderBase>>> arg0,
								   Response<BaseResponse<List<OrderBase>>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onResponse(arg0,response);
				BaseResponse<List<OrderBase>> baseResponse = response.body();
				if (null != baseResponse) {
					String status = baseResponse.getStatus();
					if (baseResponse.getData()!=null) {
						List<OrderBase> data = baseResponse.getData();
						if (data.size()>0){
							pullScrolView.setPullUp(false);
							orderBase.addAll(data);
							orderList.setVisibility(View.VISIBLE);
							noListShow.setVisibility(View.GONE);
							updateListView(orderBase,orderType);
							if (orderBase.size() < 10){
								isLastRecord = true;
								pullScrolView.setPullUp(true);
							}
						}else{
							if (pull){
								isLastRecord = true;
								pullScrolView.setPullUp(true);
							}else{
								pullScrolView.setPullUp(true);
								orderList.setVisibility(View.GONE);
								noListShow.setVisibility(View.VISIBLE);
							}
						}
					}
				}else{
				}

			}

			@Override
			public void onFailure(Call<BaseResponse<List<OrderBase>>> arg0,
								  Throwable arg1) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onFailure(arg0,arg1);
			}
		});
	}
	private void initpullView(){
		refreshCount = 1;
		isLastRecord = false;
		orderList.setPullUp(false);
		pull = false;
	}
	//已完成
	public void queryOrderSign(final Integer page){
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<List<OrderBase>>>	call = userBiz.orderSign(baseApplication.mUser.token,page);
		call.enqueue(new HttpCallBack<BaseResponse<List<OrderBase>>>() {

			@Override
			public void onResponse(Call<BaseResponse<List<OrderBase>>> arg0,
								   Response<BaseResponse<List<OrderBase>>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onResponse(arg0,response);
				BaseResponse<List<OrderBase>> baseResponse = response.body();
				if (null != baseResponse) {
					String status = baseResponse.getStatus();
					if (baseResponse.getData()!=null) {
						List<OrderBase> data = baseResponse.getData();
						if (data.size()>0){
							pullScrolView.setPullUp(false);
							orderBase.addAll(data);
							orderList.setVisibility(View.VISIBLE);
							noListShow.setVisibility(View.GONE);
							updateListView(orderBase,orderType);
							if (orderBase.size() < 10){
								isLastRecord = true;
								pullScrolView.setPullUp(true);
							}
						}else{
							if (pull){
								isLastRecord = true;
								pullScrolView.setPullUp(true);
							}else{
								pullScrolView.setPullUp(true);
								orderList.setVisibility(View.GONE);
								noListShow.setVisibility(View.VISIBLE);
							}
						}
					}
				}else{
				}
			}

			@Override
			public void onFailure(Call<BaseResponse<List<OrderBase>>> arg0,
								  Throwable arg1) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onFailure(arg0,arg1);
			}
		});
	}
	//待收货
	public void queryOrderSignList(final Integer page){
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<List<OrderBase>>>	call = userBiz.getOrderSignList(baseApplication.mUser.token,page);
		call.enqueue(new HttpCallBack<BaseResponse<List<OrderBase>>>() {

			@Override
			public void onResponse(Call<BaseResponse<List<OrderBase>>> arg0,
								   Response<BaseResponse<List<OrderBase>>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onResponse(arg0,response);
				BaseResponse<List<OrderBase>> baseResponse = response.body();
				if (null != baseResponse) {
					String status = baseResponse.getStatus();
					if (baseResponse.getData()!=null) {
						List<OrderBase> data = baseResponse.getData();
						if (data.size()>0){
							pullScrolView.setPullUp(false);
							orderBase.addAll(data);
							orderList.setVisibility(View.VISIBLE);
							noListShow.setVisibility(View.GONE);
							updateListView(orderBase,orderType);
							if (orderBase.size() < 10){
								isLastRecord = true;
								pullScrolView.setPullUp(true);
							}
						}else{
							if (pull){
								isLastRecord = true;
								pullScrolView.setPullUp(true);
							}else{
								pullScrolView.setPullUp(true);
								orderList.setVisibility(View.GONE);
								noListShow.setVisibility(View.VISIBLE);
							}
						}
					}else{
					}
				}else{
				}
			}

			@Override
			public void onFailure(Call<BaseResponse<List<OrderBase>>> arg0,
								  Throwable arg1) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onFailure(arg0,arg1);
			}
		});
	}
	//待付款
	public void queryOrdeorDerNopay(final Integer page){
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<List<OrderBase>>>	call = userBiz.orderNopay(baseApplication.mUser.token,page);
		call.enqueue(new HttpCallBack<BaseResponse<List<OrderBase>>>() {

			@Override
			public void onResponse(Call<BaseResponse<List<OrderBase>>> arg0,
								   Response<BaseResponse<List<OrderBase>>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onResponse(arg0,response);
				BaseResponse<List<OrderBase>> baseResponse = response.body();
				if (null != baseResponse) {
					String status = baseResponse.getStatus();
					if (baseResponse.getData()!=null) {
						List<OrderBase> data = baseResponse.getData();
						if (data.size()>0){
							pullScrolView.setPullUp(false);
							orderBase.addAll(data);
							orderList.setVisibility(View.VISIBLE);
							noListShow.setVisibility(View.GONE);
							updateListView(orderBase,orderType);
							if (orderBase.size() < 10){
								isLastRecord = true;
								pullScrolView.setPullUp(true);
							}
						}else{
							if (pull){
								isLastRecord = true;
								pullScrolView.setPullUp(true);
							}else{
								pullScrolView.setPullUp(true);
								orderList.setVisibility(View.GONE);
								noListShow.setVisibility(View.VISIBLE);
							}
						}
					}else{
					}
				}else{
				}
			}

			@Override
			public void onFailure(Call<BaseResponse<List<OrderBase>>> arg0,
								  Throwable arg1) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onFailure(arg0,arg1);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.all_tap_linear:
				allTapText.setTextColor(getResources().getColor(R.color.tab_line_color));
				payTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				sendTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				receiveTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				completeTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				allTapImage.setImageResource(R.drawable.home_all1);
				payTapImage.setImageResource(R.drawable.home_obligation);
				sendTapImage.setImageResource(R.drawable.home_wait_deliver_goods);
				receiveTapImage.setImageResource(R.drawable.home_wait_receiving);
				completeTapImage.setImageResource(R.drawable.home_off_stocks);
				orderType = 1;
				orderBase.clear();
				myOrderListAdapter.notifyDataSetChanged();
				initpullView();
				queryOrderList(1);
				break;
			case R.id.pay_tap_linear:
				allTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				payTapText.setTextColor(getResources().getColor(R.color.tab_line_color));
				sendTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				receiveTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				completeTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				allTapImage.setImageResource(R.drawable.home_all);
				payTapImage.setImageResource(R.drawable.home_obligation1);
				sendTapImage.setImageResource(R.drawable.home_wait_deliver_goods);
				receiveTapImage.setImageResource(R.drawable.home_wait_receiving);
				completeTapImage.setImageResource(R.drawable.home_off_stocks);
				orderType = 2;
				orderBase.clear();
				myOrderListAdapter.notifyDataSetChanged();
				initpullView();
				queryOrdeorDerNopay(1);
				break;
			case R.id.send_tap_linear:
				allTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				payTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				sendTapText.setTextColor(getResources().getColor(R.color.tab_line_color));
				receiveTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				completeTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				allTapImage.setImageResource(R.drawable.home_all);
				payTapImage.setImageResource(R.drawable.home_obligation);
				sendTapImage.setImageResource(R.drawable.home_wait_deliver_goods1);
				receiveTapImage.setImageResource(R.drawable.home_wait_receiving);
				completeTapImage.setImageResource(R.drawable.home_off_stocks);
				orderType = 3;
				orderBase.clear();
				myOrderListAdapter.notifyDataSetChanged();
				initpullView();
				queryOrdeDeliveryrList(1);
				break;
			case R.id.receive_tap_linear:
				allTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				payTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				sendTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				receiveTapText.setTextColor(getResources().getColor(R.color.tab_line_color));
				completeTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				allTapImage.setImageResource(R.drawable.home_all);
				payTapImage.setImageResource(R.drawable.home_obligation);
				sendTapImage.setImageResource(R.drawable.home_wait_deliver_goods);
				receiveTapImage.setImageResource(R.drawable.home_wait_receiving1);
				completeTapImage.setImageResource(R.drawable.home_off_stocks);
				orderType = 4;
				orderBase.clear();
				myOrderListAdapter.notifyDataSetChanged();
				initpullView();
				queryOrderSignList(1);
				break;
			case R.id.complete_tap_linear:
				allTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				payTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				sendTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				receiveTapText.setTextColor(getResources().getColor(R.color.tab_text_gray));
				completeTapText.setTextColor(getResources().getColor(R.color.tab_line_color));
				allTapImage.setImageResource(R.drawable.home_all);
				payTapImage.setImageResource(R.drawable.home_obligation);
				sendTapImage.setImageResource(R.drawable.home_wait_deliver_goods);
				receiveTapImage.setImageResource(R.drawable.home_wait_receiving);
				completeTapImage.setImageResource(R.drawable.home_off_stocks1);
				orderType = 5;
				orderBase.clear();
				myOrderListAdapter.notifyDataSetChanged();
				initpullView();
				queryOrderSign(1);
				break;
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (!isCreated) {
			return;
		}

		if (isVisibleToUser) {
			if (search01.getChildAt(0)==null&&search02.getChildAt(0) != null){
				pullScrolView.smoothScrollTo(0,0);
			}
		}else {
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
	private void getOrderDetailById(String trade_id){
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<OrderDetailBase>> call = userBiz.orderDetail(baseApplication.mUser.token,Integer.valueOf(trade_id));//"18813904075:123456789"
		call.enqueue(new HttpCallBack<BaseResponse<OrderDetailBase>>() {

			@Override
			public void onResponse(Call<BaseResponse<OrderDetailBase>> arg0,
								   Response<BaseResponse<OrderDetailBase>> response) {
				dialog.dismiss();
				super.onResponse(arg0,response);
				BaseResponse<OrderDetailBase> baseResponse = response.body();
				if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
					String status = baseResponse.getStatus();
					OrderDetailBase data = baseResponse.getData();
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable("orderDetail",data);
					intent.putExtra("detail",bundle);
					intent.setClass(getActivity(),OrderDetailActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(getActivity(),"未找到订单详情，请稍后再试",Toast.LENGTH_SHORT).show();
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
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
		// 下拉刷新操作
		orderBase.clear();
		initpullView();
		myOrderListAdapter.notifyDataSetChanged();
		switch (orderType){
			case 1:
				queryOrderList(1);
				break;
			case 2:
				queryOrdeorDerNopay(1);
				break;
			case 3:
				queryOrdeDeliveryrList(1);
				break;
			case 4:
				queryOrderSignList(1);
				break;
			case 5:
				queryOrderSign(1);
				break;
		}
		pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
	}
	private int refreshCount = 1;
	private boolean pull = false;
	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
		// 加载操作
		if (!isLastRecord) {
			refreshCount++;
			pull = true;
			switch (orderType) {
				case 1:
					queryOrderList(refreshCount);
					break;
				case 2:
					queryOrdeorDerNopay(refreshCount);
					break;
				case 3:
					queryOrdeDeliveryrList(refreshCount);
					break;
				case 4:
					queryOrderSignList(refreshCount);
					break;
				case 5:
					queryOrderSign(refreshCount);
					break;
			}
		}else{

		}
		pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
	}
		private void setListViewHeightBasedOnChildren(ExpandableListView listView) {
			ListAdapter listAdapter = listView.getAdapter();
			int totalHeight = 0;
			int count = listAdapter.getCount();
			for (int i = 0; i < listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight
					+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			listView.setLayoutParams(params);
			listView.requestLayout();
		}

}