package com.ddz.mearchant.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.ddz.mearchant.R;
import com.ddz.mearchant.adapter.TitlePopRightAdapter;
import com.ddz.mearchant.models.Custom;

import java.util.ArrayList;


public class TitlePopRightWindow extends PopupWindow implements OnItemClickListener,OnTouchListener {

	private TitlePopRightWindow mWindow;

	private RelativeLayout rl_pop;

	private TitlePopRightAdapter.PopItemClickListener mListener;

	public TitlePopRightWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TitlePopRightWindow(Activity activity, int width, int height, ArrayList<Custom> mActionItems){
		LayoutInflater inflater=activity.getLayoutInflater();
		View contentView=inflater.inflate(R.layout.title_pop_right, null);
		// 设置PopupWindow的View 
		this.setContentView(contentView);
		 // 设置PopupWindow弹出窗体的宽  
		this.setWidth(width);
		setHeight(height);  
		this.setFocusable(true);  
        this.setOutsideTouchable(true);  
        // 刷新状态  
        this.update();   
     // 实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(000000000);
        setBackgroundDrawable(dw); 
                  
        ListView listView=(ListView) contentView.findViewById(R.id.lv_list);
        mWindow=this;

		TitlePopRightAdapter adapter=new TitlePopRightAdapter(mWindow,activity,mActionItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        rl_pop=(RelativeLayout) contentView.findViewById(R.id.rl_pop);
        rl_pop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				close();
			}
		});
	}

	public void setPopItemClickListener(TitlePopRightAdapter.PopItemClickListener listener){
		this.mListener=listener;
	}
	public TitlePopRightAdapter.PopItemClickListener getListener(){
		//可以通过this的实例来获取设置好的listener
		return mListener;
	}
	/**
	 * listview点击事件
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
		TitlePopRightWindow.this.dismiss();
		
	}

	public void close(){
		this.dismiss();
	}
	
	public int position(){
		
		return 0;	
	}
	

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mWindow.dismiss();
		return false;
	}
	
	

	
	
	
	
}
