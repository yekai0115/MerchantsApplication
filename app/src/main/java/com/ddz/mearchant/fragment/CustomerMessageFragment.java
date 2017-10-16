package com.ddz.mearchant.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.ddz.mearchant.BaseFragment;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.Message;
import com.ddz.mearchant.models.MessageBase;
import com.ddz.mearchant.view.CircleImageView;
import com.ddz.mearchant.view.HandyTextView;
import com.ddz.mearchant.view.PullToRefreshLayout;
import com.ddz.mearchant.view.PullableListView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class CustomerMessageFragment extends BaseFragment implements PullToRefreshLayout.OnRefreshListener {
	//定义一个布局
	private LayoutInflater layoutInflater;
	//定义FragmentTabHost对象
	private FragmentTabHost mTabHost;
	private View view;
	private PullableListView listMessage;
	private MessageAdapter messageAdapter;
	private List<Message> messageList = new ArrayList<>();
	public LinearLayout noListShow;
	private PullToRefreshLayout layout;
	private int refreshCount = 1;
	private boolean pull = false;
	private boolean isLastRecord = false;
	public CustomerMessageFragment(){
		super();
	}

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
		listMessage = (PullableListView)view.findViewById(R.id.list_message);
		noListShow = (LinearLayout)view.findViewById(R.id.no_list_show);
		layout.setOnRefreshListener(this);
		messageAdapter = new MessageAdapter();
		listMessage.setAdapter(messageAdapter);
	}

	@Override
	protected void initEvents() {
		messageList.clear();
		queryMessageList("1","1");
	}

	@Override
	public void onResume() {
		super.onResume();

	}
	
	@Override
	public void onPause() {
		super.onPause();

	}
	public void queryMessageList(final String mType,final String page){
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<MessageBase>>	call = userBiz.myMessage(baseApplication.mUser.token,mType,page);
		call.enqueue(new HttpCallBack<BaseResponse<MessageBase>>() {

			@Override
			public void onResponse(Call<BaseResponse<MessageBase>> arg0,
								   Response<BaseResponse<MessageBase>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onResponse(arg0,response);
				BaseResponse<MessageBase> baseResponse = response.body();
				if (null != baseResponse) {
					String status = baseResponse.getStatus();
					MessageBase data = baseResponse.getData();
					if (data.getMsg_list()!=null && data.getMsg_list().size()>0){
						listMessage.setPullUp(false);
						messageList.addAll(data.getMsg_list());
						layout.setVisibility(View.VISIBLE);
						noListShow.setVisibility(View.GONE);
						messageAdapter.notifyDataSetChanged();
						if (messageList.size() < 10){
							isLastRecord = true;
							listMessage.setPullUp(true);
						}
					}else{
						if (pull){
							isLastRecord = true;
							listMessage.setPullUp(true);
						}else{
							layout.setVisibility(View.GONE);
							noListShow.setVisibility(View.VISIBLE);
						}
					}
				}
			}

			@Override
			public void onFailure(Call<BaseResponse<MessageBase>> arg0,
								  Throwable arg1) {
			}
		});
	}
	class MessageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		@Override
		public int getCount() {
			return messageList.size();
		}

		@Override
		public Object getItem(int position) {
			return messageList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null){
				convertView = View.inflate(getActivity(),R.layout.adapter_customer_message_list,null);
				holder = new ViewHolder();
				holder.cMesImage= (CircleImageView) convertView.findViewById(R.id.cus_mes_image);
				holder.cMesTitle = (HandyTextView) convertView.findViewById(R.id.cus_mes_title);
				holder.cMesTime = (HandyTextView) convertView.findViewById(R.id.cus_mes_time);
				holder.cMesContent = (HandyTextView) convertView.findViewById(R.id.cus_mes_content);
				holder.cMesNumber = (HandyTextView) convertView.findViewById(R.id.cus_mes_number);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			try {
//				ImageUrl.setbitmap(orderDownBase.get(position).getHeadpic(),holder.mPic);
				holder.cMesTitle.setText(messageList.get(position).getNickname());
				holder.cMesTime.setText(messageList.get(position).getAdd_time());
				holder.cMesContent.setText(messageList.get(position).getContent());
				holder.cMesNumber.setText(messageList.get(position).getContact_number());
				Glide.with(getActivity()).load(Constants.ALI_PRODUCT_LOAD+messageList.get(position).getHeadpic()).fitCenter()
						.placeholder(getResources().getDrawable(R.drawable.default_user)).error(getResources().getDrawable(R.drawable.default_user)).into(holder.cMesImage);
			}catch (Exception e){
				e.printStackTrace();
			}
			return convertView;
		}
	}

	public  class ViewHolder{
		private CircleImageView cMesImage;
		private HandyTextView cMesTitle;
		private HandyTextView cMesTime;
		private HandyTextView cMesContent;
		private HandyTextView cMesNumber;

	}
	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
		// 下拉刷新操作
		messageList.clear();
		initpullView();
		messageAdapter.notifyDataSetChanged();
		queryMessageList("1","1");
		pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
	}
	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
		// 加载操作
		if (!isLastRecord) {
			refreshCount++;
			pull = true;
			queryMessageList("1",String.valueOf(refreshCount));
		}
		pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
	}
	private void initpullView(){
		refreshCount = 1;
		isLastRecord = false;
		listMessage.setPullUp(false);
		pull = false;
	}
}
