package com.ddz.mearchant.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ddz.mearchant.BaseFragment;
import com.ddz.mearchant.R;
import com.ddz.mearchant.adapter.MyOrderListAdapter;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.dialog.DialogCalendar;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.OrderBase;
import com.ddz.mearchant.models.OrderCollection;
import com.ddz.mearchant.utils.CommonUtils;
import com.ddz.mearchant.view.HandyTextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class SearchOnlineOrderFragment extends BaseFragment implements View.OnClickListener{
	//定义一个布局
	private LayoutInflater layoutInflater;
	//定义FragmentTabHost对象
	private FragmentTabHost mTabHost;
	private View view;
	private MyOrderListAdapter myOrderListAdapter;
	private OrderAdapter orderAdapter;
	private ExpandableListView orderList;
		private ArrayList<OrderBase> orderBase = new ArrayList<OrderBase>();
	private List<OrderCollection> orders = new ArrayList<OrderCollection>();
	private HandyTextView searchAction,searchStartTime,searchEndTime;
	private LinearLayout noListShow;
	public SearchOnlineOrderFragment(){
		super();
	}
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.search_online_order_fragment, container, false);
		orders.clear();
		initViews();
		initDialog();
		queryOrderList(0);
		return view;

	}

	@Override
	protected void init() {

	}

	@Override
	protected void initViews() {
			orderList = (ExpandableListView)view.findViewById(R.id.order_list);
			searchStartTime = (HandyTextView) view.findViewById(R.id.search_start_time);
			searchEndTime = (HandyTextView) view.findViewById(R.id.search_end_time);
			searchAction = (HandyTextView) view.findViewById(R.id.search_action);
			noListShow = (LinearLayout)view.findViewById(R.id.no_list_show);
			searchStartTime.setText(CommonUtils.sdfDatePoint.format(new Date()));
			searchEndTime.setText(CommonUtils.sdfDatePoint.format(new Date()));
			searchStartTime.setOnClickListener(this);
			searchEndTime.setOnClickListener(this);
			searchAction.setOnClickListener(this);

			myOrderListAdapter = new MyOrderListAdapter(getActivity());
			orderList.setAdapter(myOrderListAdapter);
			orderList.setGroupIndicator(null);
	}

	@Override
	protected void initEvents() {

	}

	private void updateListView(ArrayList<OrderBase> orderBases, int position) {

		myOrderListAdapter.setList(orderBases,position);

		myOrderListAdapter.notifyDataSetChanged();

		expandAllGroup();

	}
	/**

	 * 展开所有组

	 */

	private void expandAllGroup() {

		for (int i = 0; i < orderBase.size(); i++) {

			orderList.expandGroup(i);

		}

	}
	public void queryOrderListByTime(Integer page,Integer beginTime,Integer endTime){
		dialog.show();
		orderBase.clear();
		myOrderListAdapter.notifyDataSetChanged();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<List<OrderBase>>>	call = userBiz.getOrderListByTime(baseApplication.mUser.token,page,beginTime,endTime);
		call.enqueue(new HttpCallBack<BaseResponse<List<OrderBase>>>() {

			@Override
			public void onResponse(Call<BaseResponse<List<OrderBase>>> arg0,
								   Response<BaseResponse<List<OrderBase>>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onResponse(arg0,response);
				BaseResponse<List<OrderBase>> baseResponse = response.body();
				if (null != baseResponse) {
					String status = baseResponse.getStatus();
					List<OrderBase> data = baseResponse.getData();
					if (data.size()>0){
						orderBase.addAll(data);
						orderList.setVisibility(View.VISIBLE);
						noListShow.setVisibility(View.GONE);
						updateListView(orderBase,0);
					}else{
						orderList.setVisibility(View.GONE);
						noListShow.setVisibility(View.VISIBLE);
					}
				}

			}

			@Override
			public void onFailure(Call<BaseResponse<List<OrderBase>>> arg0,
								  Throwable arg1) {
			}
		});
	}
	public void queryOrderList(final Integer page){
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<List<OrderBase>>>	call = userBiz.getOrderAllList(baseApplication.mUser.token,1, Constants.PAGE);
		call.enqueue(new HttpCallBack<BaseResponse<List<OrderBase>>>() {

			@Override
			public void onResponse(Call<BaseResponse<List<OrderBase>>> arg0,
								   Response<BaseResponse<List<OrderBase>>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onResponse(arg0,response);
				BaseResponse<List<OrderBase>> baseResponse = response.body();
				orderBase.clear();
				myOrderListAdapter.notifyDataSetChanged();
				if (null != baseResponse) {
					String status = baseResponse.getStatus();
					List<OrderBase> data = baseResponse.getData();
					if (data.size()>0){
						orderBase.addAll(data);
						orderList.setVisibility(View.VISIBLE);
						noListShow.setVisibility(View.GONE);
						updateListView(orderBase,page);
					}else{
						orderList.setVisibility(View.GONE);
						noListShow.setVisibility(View.VISIBLE);
					}
				}

			}

			@Override
			public void onFailure(Call<BaseResponse<List<OrderBase>>> arg0,
								  Throwable arg1) {
			}
		});
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
					queryOrderListByTime(1,beginTime.intValue(),endTime.intValue());
				}catch (ParseException e){
					e.printStackTrace();
				}
				break;
		}
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
			return orderBase.size();
		}

		@Override
		public Object getItem(int position) {
			return orderBase.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null){
				convertView = View.inflate(getActivity(),R.layout.adapter_order_list,null);
				holder = new ViewHolder();
				holder.pOrderCode= (HandyTextView) convertView.findViewById(R.id.order_code);
				holder.pStatus = (HandyTextView) convertView.findViewById(R.id.order_status);
				holder.pImage = (ImageView) convertView.findViewById(R.id.order_image);
				holder.pName = (HandyTextView) convertView.findViewById(R.id.order_name);
				holder.pButton = (Button) convertView.findViewById(R.id.order_button);
				holder.pColor = (HandyTextView) convertView.findViewById(R.id.order_color);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}

			return convertView;
		}
	}

	public  class ViewHolder{
		private HandyTextView pOrderCode;
		private HandyTextView pStatus;
		private HandyTextView pName;
		private HandyTextView pContent;
		private HandyTextView pColor;
		private Button pButton;
		private ImageView pImage;

	}
}
