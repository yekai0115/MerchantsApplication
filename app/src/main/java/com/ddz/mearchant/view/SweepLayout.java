package com.ddz.mearchant.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.ddz.mearchant.R;

public class SweepLayout extends LinearLayout {
	
	private Scroller mScroller;
	private int mHolderWidth = 70;
	
	public SweepLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public SweepLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.sweeplayout);
		mHolderWidth = (int)a.getDimension(R.styleable.sweeplayout_holderwidth, 120);
		a.recycle();
		init();
	}

	private void init(){
		mScroller = new Scroller(getContext());
	}
	
	public int getHolderWidth(){
		return this.mHolderWidth;
	}
	
	public void shrik(int duration){
		mScroller.startScroll(this.getScrollX(), 0, 0 - this.getScrollX(), 0, duration);
		invalidate();
	}
	
	public void showSlide(int duration){
		mScroller.startScroll(this.getScrollX(), 0, mHolderWidth - this.getScrollX(), 0, duration);
		invalidate();
	}
	
	@Override
	public void computeScroll(){
		if(mScroller.computeScrollOffset()){
			scrollTo(mScroller.getCurrX(), 0);
			postInvalidate();
		}
	}
}
