package com.ddz.mearchant.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ddz.mearchant.R;

/**
 * Created by Administrator on 2017/6/20 0020.
 */

public class ScrolHorinListView extends ListView implements GestureDetector.OnGestureListener,View.OnTouchListener{

    /**
     * 隐藏按钮是否显示出来，默认是false
     */
    boolean isHideViewShow = false;
    /**
     * 点击的是哪个item
     */
    int itemPosition;
    /**
     * 删除按钮的视图
     */
    View hideView;
    /**
     * 点击的item的整行视图
     */
    ViewGroup itemLayout;
    /**
     * 响应OnGestureListener方法
     */
    GestureDetector detector;
    /**
     * 自定义的删除监听器
     */
    onDeleteListener deleteListener;
    /**
     * 自定义的上架监听器
     */
    onAddListener addListener;
    public ScrolHorinListView(Context context) {
        this(context, null);
    }

    public ScrolHorinListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public ScrolHorinListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        detector=new GestureDetector(getContext(), this);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(isHideViewShow){
            // 如果隐藏按钮显示出来
            itemLayout.removeView(hideView);
            hideView = null;
            isHideViewShow = false;
            return false;
        }else{
            // 如果隐藏按钮没有显示出来
            return detector.onTouchEvent(event);
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (isHideViewShow) {
            // 如果隐藏按钮显示出来

        } else {
            // 如果隐藏按钮没有显示出来
            itemPosition = pointToPosition((int) e.getX(), (int) e.getY());
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        //当隐藏按钮没有显示，并且横向滑动速度大于纵向滑动速度
        if (!isHideViewShow && Math.abs(velocityX) > Math.abs(velocityY)) {
            hideView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_hide_button, null);
            HandyTextView deleteText = (HandyTextView)hideView.findViewById(R.id.delete_button);
            HandyTextView downText = (HandyTextView)hideView.findViewById(R.id.down_button);

            deleteText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemLayout.removeView(hideView);
                    hideView=null;
                    isHideViewShow=false;
                    deleteListener.onDelete(itemPosition);
                }
            });
            downText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemLayout.removeView(hideView);
                    hideView=null;
                    isHideViewShow=false;
                    addListener.onAdd(itemPosition);
                }
            });
            //动态添加隐藏按钮到item视图上
            itemLayout = (ViewGroup) getChildAt(itemPosition
                    - getFirstVisiblePosition());
            if (itemLayout!=null){
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                itemLayout.addView(hideView, params);
                isHideViewShow=true;
            }
        }
        return false;
    }

    public void setDeleteListener(onDeleteListener listener){
        deleteListener=listener;
    }
    public interface onDeleteListener{
        void onDelete(int index);
    }
    public void setAddListener(onAddListener listener){
        addListener=listener;
    }
    public interface onAddListener{
        void onAdd(int index);
    }

}
