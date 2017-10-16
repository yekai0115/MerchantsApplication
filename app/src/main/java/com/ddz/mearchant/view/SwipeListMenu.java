package com.ddz.mearchant.view;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class SwipeListMenu {

	private Context mContext;
	private List<SwipeListMenuItem> mItems;
	private int mViewType;

	public SwipeListMenu(Context context) {
		mContext = context;
		mItems = new ArrayList<SwipeListMenuItem>();
	}

	public Context getContext() {
		return mContext;
	}

	public void addMenuItem(SwipeListMenuItem item) {
		mItems.add(item);
	}

	public void removeMenuItem(SwipeListMenuItem item) {
		mItems.remove(item);
	}

	public List<SwipeListMenuItem> getMenuItems() {
		return mItems;
	}

	public SwipeListMenuItem getMenuItem(int index) {
		return mItems.get(index);
	}

	public int getViewType() {
		return mViewType;
	}

	public void setViewType(int viewType) {
		this.mViewType = viewType;
	}

}
