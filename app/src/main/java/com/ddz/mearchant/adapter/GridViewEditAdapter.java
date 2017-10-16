package com.ddz.mearchant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ddz.mearchant.R;
import com.ddz.mearchant.utils.ImageUrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GridViewEditAdapter extends BaseAdapter {

	private Context mContext;
	private List<String> list = new ArrayList<String>();
	private OnDeleteImageListener mListener;
	public GridViewEditAdapter() {
		super();
	}
	private int maxNum=8;
/**
 * 获取列表数据
 * @param list
 */
	public void setList(List<String> list){
		this.list = list;
		this.notifyDataSetChanged();
	}

	public GridViewEditAdapter(Context mContext, List<String> list, int maxNum) {
		super();
		this.mContext = mContext;
		this.list = list;
		this.maxNum = maxNum;
	}

	@Override
	public int getCount() {
		if(list==null){
			return 1;
		}else if(list.size()==maxNum){
			return maxNum;
		}else{
			return list.size()+1;
		}
	}

	@Override
	public Object getItem(int position) {
		if (list != null
				&& list.size() == maxNum)
		{
			return list.get(position);
		}

		else if (list == null || position - 1 < 0
				|| position > list.size())
		{
			return null;
		}
		else
		{
			return list.get(position - 1);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position,View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView==null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_published_grida, null);
			holder = new ViewHolder();
			holder.item_grida_image = (ImageView) convertView.findViewById(R.id.item_grida_image);
			holder.item_grida_delete = (ImageView) convertView.findViewById(R.id.item_grida_delete);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.item_grida_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.click(position);
			}
		});
		if (isShowAddItem(position))
		{
			holder.item_grida_image.setImageResource(R.drawable.manage_details_add);
			holder.item_grida_image.setBackgroundResource(R.color.bg_color);
			holder.item_grida_delete.setVisibility(View.GONE);
		}
		else
		{
			try {
				ImageUrl.setbitmap(list.get(position),holder.item_grida_image);
			}catch (IOException e){
				e.printStackTrace();
			}
			holder.item_grida_image.setBackgroundResource(R.color.bg_color);
			holder.item_grida_delete.setVisibility(View.VISIBLE);
		}
		return convertView;
	}
	/**
	 * 判断当前下标是否是最大值
	 * @param position  当前下标
	 * @return
	 */
	private boolean isShowAddItem(int position)
	{
		int size = list == null ? 0 : list.size();
		return position == size;
	}
	
	class ViewHolder{
		ImageView item_grida_image,item_grida_delete;
	}
	public interface OnDeleteImageListener{
		public void click(int position);
	}
	public void setOnDeleteImageListener(OnDeleteImageListener listener){
		this.mListener=listener;
	}
}
