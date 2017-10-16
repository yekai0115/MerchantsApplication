package com.ddz.mearchant.interfaces;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.ListView;

import com.ddz.mearchant.view.SweepLayout;

public class SweepOnTouchListener implements OnTouchListener {

	private ListView mListView;
	private SweepLayout mDownView;
	private SweepLayout mLastDownView;
	
	private int mLastX, mLastY;
	
	private boolean mSweep = false;
	private boolean mIntercept = false;//是否取消点击事件
	
	private int mMinFlingVelocity;
	private int mMaxFlingVelocity;
	
	private final static int TAN = 2;
	
	private VelocityTracker mVelocityTracker;
	
	public SweepOnTouchListener(ListView listview){
		this.mListView = listview;
		ViewConfiguration vc = ViewConfiguration.get(mListView.getContext());
		mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
		mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
	}
	
	//取消点击事件
	private void cancelTouchEvent(MotionEvent event){
		MotionEvent cancelEvent = MotionEvent.obtain(event);
		cancelEvent.setAction(MotionEvent.ACTION_CANCEL | (event.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
		mListView.onTouchEvent(cancelEvent);
		cancelEvent.recycle();
	}
	public void setmLastDownView(){
		if (mLastDownView!=null){
			mLastDownView = null;
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		// TODO Auto-generated method stub
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			Rect rect = new Rect();
			int mDownX = (int)event.getX();
			int mDownY = (int)event.getY();
			
			//获得item
			for(int i = 0; i < mListView.getChildCount(); i ++){
				View childView = mListView.getChildAt(i);
				childView.getHitRect(rect);
				if(rect.contains(mDownX, mDownY)){
					mDownView = (SweepLayout) childView;
					break;
				}else {
					if(mDownView != null && mDownView.getScrollX() > 0) {
						mDownView.shrik(100);
						mDownView = null;
						mIntercept = true;
						mLastDownView = null;
					}else{
						mDownView = null;
						mIntercept = true;
					}
				}
			}
			
			mVelocityTracker = VelocityTracker.obtain();
			mVelocityTracker.addMovement(event);
			
			//当获得的item与上一个item不一致时，item恢复初始位置，取消其点击事件
			if(mDownView != mLastDownView){
				if(mLastDownView != null && mLastDownView.getScrollX() > 0) {
					mLastDownView.shrik(100);
					mDownView = null;
					mIntercept = true;
				}
			}
			view.onTouchEvent(event);
			break;
		case MotionEvent.ACTION_MOVE:
			if(mDownView == null) return false;
			mVelocityTracker.addMovement(event);
			int deltaX = (int)event.getX() - mLastX;
			int deltaY = (int)event.getY() - mLastY;
			if((deltaX < 0 || mDownView.getScrollX() >0) && Math.abs(deltaX) >= TAN * Math.abs(deltaY)){
				mSweep = true;
				mIntercept = true;
			}
			if(mSweep && mDownView != null){
				int distance = mDownView.getScrollX() - deltaX;
				if(distance < 0) distance = 0;
				if(distance > mDownView.getHolderWidth()) distance = mDownView.getHolderWidth();
				mDownView.scrollTo(distance, 0);
				mListView.requestDisallowInterceptTouchEvent(true);
			}
			if(mIntercept){
				cancelTouchEvent(event);
			}
			break;
		case MotionEvent.ACTION_UP:
			if(mVelocityTracker != null){
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
			}
			if(mDownView != null){
				boolean finishScroll = false;
				if(mDownView.getScrollX() > 0.35 * mDownView.getHolderWidth()){
					finishScroll = true;
				}
				if(mVelocityTracker != null){
					float velocityX = mVelocityTracker.getXVelocity();
					float velocityY = mVelocityTracker.getYVelocity();
					//计算滑动速度和方向，如果速度为负值则为左滑，item为滑动位置，否则为初始位置
					if(velocityX < 0 && Math.abs(velocityX) >= mMinFlingVelocity && Math.abs(velocityX) <= mMaxFlingVelocity && Math.abs(velocityX) >= TAN * Math.abs(velocityY)){
						finishScroll = true;
						mIntercept = true;
					}else if(velocityX > 0 && Math.abs(velocityX) >= mMinFlingVelocity && Math.abs(velocityX) <= mMaxFlingVelocity && Math.abs(velocityX) >= TAN * Math.abs(velocityY)){
						finishScroll = false;
						mIntercept = true;
					}
				}
				if(finishScroll){
					mDownView.showSlide(100);
				}else{
					mDownView.shrik(100);
				}
			}
			//如果点击的item为展开的item，并且没有滑动过，则item回到初始位置。
			if(mDownView == mLastDownView){
				if(mDownView != null){
					if(mDownView.getScrollX() > 0 && !mSweep){
						mDownView.shrik(100);
						mIntercept = true;
					}
				}
			}
			if(mIntercept){
				cancelTouchEvent(event);
			}
			mSweep = false;
			mIntercept = false;
			mLastDownView = mDownView;
			
			mVelocityTracker.recycle();
			break;
		}
		mLastX = (int)event.getX();
		mLastY = (int)event.getY();
		return false;
	}
}