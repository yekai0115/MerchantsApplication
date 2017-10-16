package com.ddz.mearchant.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.ddz.mearchant.BaseFragment;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.GivingRecords;
import com.ddz.mearchant.models.GivingRecordsBase;
import com.ddz.mearchant.view.HandyTextView;
import com.ddz.mearchant.view.PullToRefreshLayout;
import com.ddz.mearchant.view.PullableListView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class GivingOutRecordFragment extends BaseFragment implements PullToRefreshLayout.OnRefreshListener {
	//定义一个布局
	private LayoutInflater layoutInflater;
	//定义FragmentTabHost对象
	private FragmentTabHost mTabHost;
	private View view;
	private PullableListView listGivindOut;
	private GivingRecordAdapter givingRecordAdapter;
	private HandyTextView sevenDownNum,totalDownNum;
	private List<GivingRecords> givingRecords = new ArrayList<>();
	//Tab选项卡的文字
	private PullToRefreshLayout layout;
	private int refreshCount = 1;
	private boolean pull = false;
	private boolean isLastRecord = false;
	public GivingOutRecordFragment(){
		super();
	}
	public LinearLayout noListShow;
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_message_list, container, false);
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
		listGivindOut = (PullableListView)view.findViewById(R.id.list_message);
		noListShow = (LinearLayout)view.findViewById(R.id.no_list_show);
		layout.setOnRefreshListener(this);
		givingRecordAdapter = new GivingRecordAdapter();
		listGivindOut.setAdapter(givingRecordAdapter);
	}

	@Override
	protected void initEvents() {
		givingRecords.clear();
		queryRecordList("1","1");
	}

	@Override
	public void onResume() {
		super.onResume();

	}
	
	@Override
	public void onPause() {
		super.onPause();

	}
	public void queryRecordList(final String mType,final String page){
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<GivingRecordsBase>>	call = userBiz.transferRecord(baseApplication.mUser.token,mType,page);
		call.enqueue(new HttpCallBack<BaseResponse<GivingRecordsBase>>() {

			@Override
			public void onResponse(Call<BaseResponse<GivingRecordsBase>> arg0,
								   Response<BaseResponse<GivingRecordsBase>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onResponse(arg0,response);
				BaseResponse<GivingRecordsBase> baseResponse = response.body();
				if (null != baseResponse) {
					String status = baseResponse.getStatus();
					GivingRecordsBase data = baseResponse.getData();
					if (data.getLists()!=null && data.getLists().size()>0){
						listGivindOut.setPullUp(false);
						givingRecords.addAll(data.getLists());
						layout.setVisibility(View.VISIBLE);
						noListShow.setVisibility(View.GONE);
						givingRecordAdapter.notifyDataSetChanged();
						if (givingRecords.size() < 10){
							isLastRecord = true;
							listGivindOut.setPullUp(true);
						}
					}else{
						if (pull){
							isLastRecord = true;
							listGivindOut.setPullUp(true);
						}else{
							layout.setVisibility(View.GONE);
							noListShow.setVisibility(View.VISIBLE);
						}
					}

				}
			}

			@Override
			public void onFailure(Call<BaseResponse<GivingRecordsBase>> arg0,
								  Throwable arg1) {
			}
		});
	}
	class GivingRecordAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return givingRecords.size();
		}

		@Override
		public Object getItem(int position) {
			return givingRecords.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null){
				convertView = View.inflate(getActivity(),R.layout.adapter_giving_out_list,null);
				holder = new ViewHolder();
				holder.givingName = (HandyTextView) convertView.findViewById(R.id.giving_name);
				holder.givingPhone = (HandyTextView) convertView.findViewById(R.id.giving_phone);
				holder.givingAmount = (HandyTextView) convertView.findViewById(R.id.giving_amount);
				holder.givingTime = (HandyTextView) convertView.findViewById(R.id.giving_time);
				holder.givingType = (HandyTextView) convertView.findViewById(R.id.giving_type);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			try {
				holder.givingName.setText("收款账户:");
				holder.givingPhone.setText(givingRecords.get(position).getAccess_user());
				holder.givingAmount.setText("－"+givingRecords.get(position).getMoney());
				holder.givingTime.setText(givingRecords.get(position).getPay_time());
				holder.givingType.setText(givingRecords.get(position).getType());
			}catch (Exception e){
				e.printStackTrace();
			}
			return convertView;
		}
	}

	public  class ViewHolder{
		private HandyTextView givingPhone;
		private HandyTextView givingAmount;
		private HandyTextView givingTime;
		private HandyTextView givingType;
		private HandyTextView givingName;
	}
	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
		// 下拉刷新操作
		givingRecords.clear();
		initpullView();
		givingRecordAdapter.notifyDataSetChanged();
		queryRecordList("1","1");
		pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
	}
	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
		// 加载操作
		if (!isLastRecord) {
			refreshCount++;
			pull = true;
			queryRecordList("1",String.valueOf(refreshCount));
		}
		pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
	}
	private void initpullView(){
		refreshCount = 1;
		isLastRecord = false;
		listGivindOut.setPullUp(false);
		pull = false;
	}
}
