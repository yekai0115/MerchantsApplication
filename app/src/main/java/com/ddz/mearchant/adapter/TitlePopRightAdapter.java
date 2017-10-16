package com.ddz.mearchant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddz.mearchant.R;
import com.ddz.mearchant.models.Custom;
import com.ddz.mearchant.view.TitlePopRightWindow;

import java.util.ArrayList;


public class TitlePopRightAdapter extends BaseAdapter {

	private int mPosition;
	private ArrayList<Custom> mActionItems;
	private TitlePopRightWindow mWindow;
	private Context context;
	private PopItemClickListener mListener;
	private ViewHolder holder;
	private LayoutInflater mInflater;

	public TitlePopRightAdapter(TitlePopRightWindow window, Context context, ArrayList<Custom> mActionItems){
		this.context=context;
		this.mActionItems=mActionItems;
		this.mWindow=window;
		this.mInflater= LayoutInflater.from(context);
	}
	
	public int getCount() {
		return mActionItems.size();
	}

	public Object getItem(int position) {
		return mActionItems.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	private static class ViewHolder {
		private TextView text;
		private ImageView img_checked;
	}
			
	public View getView(final int arg0, final View arg1, ViewGroup arg2) {
		//获取设置好的listener
		mListener=mWindow.getListener();
		View view=arg1;
		holder=null;
		if(view==null){
			view= View.inflate(context, R.layout.title_pop_list_item, null);
			holder = new ViewHolder();
			holder.text=(TextView)view.findViewById(R.id.tv_text);
			view.setTag(holder);
		}else {
			holder = (ViewHolder) view.getTag();
		}
		holder.text.setText(mActionItems.get(arg0).getUser_cate_name());
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mPosition=arg0;	
				mWindow.close();
				mListener.click(mPosition,arg1);
			}
		});
		return view;
	}
	//定义接口和一个为实现的方法
	public interface PopItemClickListener{
		public void click(int position, View view);
	}	
}
