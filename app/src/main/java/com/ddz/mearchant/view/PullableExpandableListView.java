package com.ddz.mearchant.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;

import com.ddz.mearchant.interfaces.Pullable;

public class PullableExpandableListView extends ExpandableListView implements
		Pullable
		{
			private boolean isNoList =false;
			private int mLastMotionY;
			public PullableExpandableListView(Context context)
			{
				super(context);
			}

			public PullableExpandableListView(Context context, AttributeSet attrs)
			{
				super(context, attrs);
			}

			public PullableExpandableListView(Context context, AttributeSet attrs,
					int defStyle)
			{
				super(context, attrs, defStyle);
			}

			@Override
			public boolean canPullDown()
			{
				if (getCount() == 0)
				{
					// 没有item的时候也可以下拉刷新
					return false;
				} else if (getFirstVisiblePosition() == 0
						&& getChildAt(0).getTop() >= 0)
				{
					// 滑到顶部了
					return true;
				} else
					return false;
			}

			@Override
			public boolean canPullUp()
			{
				if (getCount() == 0)
				{
					// 没有item的时候也可以上拉加载
					return false;
				} else if (getLastVisiblePosition() == (getCount() - 1))
				{
					// 滑到底部了
					if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
							&& getChildAt(
									getLastVisiblePosition()
											- getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
						if (isNoList){
							return false;
						}else{
							return true;
						}
				}
				return false;
			}
			public void setPullUp(boolean nolist){
				isNoList = nolist;
			}

			@Override
			public boolean onTouchEvent(MotionEvent ev) {
				int y = (int) ev.getRawY();
				switch (ev.getAction()) {
					case MotionEvent.ACTION_DOWN:
						// 首先拦截down事件,记录y坐标
						mLastMotionY = y;
						break;
					case MotionEvent.ACTION_MOVE:
						// deltaY > 0 是向下运动,< 0是向上运动
						int deltaY = y - mLastMotionY;
						if (deltaY > 0) {
							View child = getChildAt(0);
							if (child != null) {
								if (getFirstVisiblePosition() == 0 && child.getTop() == 0) {
									bottomFlag = false;
									getParent().requestDisallowInterceptTouchEvent(false);
								}
								int top = child.getTop();
								int padding = getPaddingTop();
								if (getFirstVisiblePosition() == 0 && Math.abs(top - padding) <= 8) {// 这里之前用3可以判断,但现在不行,还没找到原因
									bottomFlag = false;
									getParent().requestDisallowInterceptTouchEvent(false);
								}
							}
						}
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_CANCEL:
						break;
				}
				return super.onTouchEvent(ev);

			}
			@Override

			public boolean onInterceptTouchEvent(MotionEvent ev) {

				// 阻止父类拦截事件

				if (bottomFlag) {

					getParent().requestDisallowInterceptTouchEvent(true);

				}
				return super.onInterceptTouchEvent(ev);
			}
			public void setBottomFlag(boolean flag) {
				bottomFlag = flag;
			}
			boolean bottomFlag;

		}
