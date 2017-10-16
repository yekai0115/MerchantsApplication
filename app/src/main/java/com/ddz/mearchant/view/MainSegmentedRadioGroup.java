package com.ddz.mearchant.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioGroup;


/*
 * 首页的radioGroup
 */
public class MainSegmentedRadioGroup extends RadioGroup{
	
	private static final String TAG=MainSegmentedRadioGroup.class.getName();
	
	public MainSegmentedRadioGroup(Context context) {
		super(context);
	}

	public MainSegmentedRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		changeButtonsImages();
	}

	private void changeButtonsImages() {
		try {
//			super.getChildAt(0).setBackgroundResource(R.drawable.bg_radio_one);
//			super.getChildAt(1).setBackgroundResource(R.drawable.bg_radio_two);
//			super.getChildAt(2).setBackgroundResource(R.drawable.bg_radio_three);
//			super.getChildAt(3).setBackgroundResource(R.drawable.bg_radio_four);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		
	}
}
