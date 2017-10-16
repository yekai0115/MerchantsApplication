package com.ddz.mearchant.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.ddz.mearchant.interfaces.Pullable;

/**
 * Created by Administrator on 2017/7/16 0016.
 */

public class PullableScrollView extends ScrollView implements Pullable
{
    private boolean isNoList =false;
    private OnScrollListener onScrollListener;
    /**
     * 主要是用在用户手指离开MyScrollView，MyScrollView还在继续滑动，我们用来保存Y的距离，然后做比较
     */
    private int lastScrollY;
    public PullableScrollView(Context context)
    {
        super(context);
    }

    public PullableScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PullableScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown()
    {
        if (getScrollY() == 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean canPullUp()
    {
        if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
            if (isNoList){
                return false;
            }else{
                return true;
            }
        else
            return false;
    }
    public void setPullUp(boolean nolist){
        isNoList = nolist;
    }
//    @Override
//
//    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
//
//        super.onScrollChanged(x, y, oldx, oldy);
//
//
//
//        if( getChildCount()>=1&&getHeight() + getScrollY() == getChildAt(getChildCount()-1).getBottom()){
//
//            onGetBottomListener.onBottom();
//
//        }
//
//    }



    public interface ScrollViewListener {
        void onScrollChanged(PullableScrollView scrollView, int x, int y, int oldx, int oldy);
    }


    public void setBottomListener(OnGetBottomListener listener){
        onGetBottomListener = listener;
    }

    public interface OnGetBottomListener {

        public void onBottom();

    }
    /**

     * 设置滚动接口

     * @param onScrollListener

     */

    public void setOnScrollListener(OnScrollListener onScrollListener){

        this.onScrollListener = onScrollListener;

    }

    /**

     * 用于用户手指离开MyScrollView的时候获取MyScrollView滚动的Y距离，然后回调给onScroll方法中

     */

    private Handler handler = new Handler() {



        public void handleMessage(android.os.Message msg) {

            int scrollY = PullableScrollView.this.getScrollY();



            //此时的距离和记录下的距离不相等，在隔5毫秒给handler发送消息

            if(lastScrollY != scrollY){

                lastScrollY = scrollY;

                handler.sendMessageDelayed(handler.obtainMessage(), 5);

            }

            if(onScrollListener != null){

                onScrollListener.onScroll(scrollY);

            }



        };



    };

    /**

     * 重写onTouchEvent， 当用户的手在MyScrollView上面的时候，

     * 直接将MyScrollView滑动的Y方向距离回调给onScroll方法中，当用户抬起手的时候，

     * MyScrollView可能还在滑动，所以当用户抬起手我们隔5毫秒给handler发送消息，在handler处理

     * MyScrollView滑动的距离

     */

    @Override

    public boolean onTouchEvent(MotionEvent ev) {

        if(onScrollListener != null){

            onScrollListener.onScroll(lastScrollY = this.getScrollY());

        }

        switch(ev.getAction()){

            case MotionEvent.ACTION_UP:

                handler.sendMessageDelayed(handler.obtainMessage(), 20);

                break;

        }

        return super.onTouchEvent(ev);

    }



    /**

     * 滚动的回调接口

     */

    public interface OnScrollListener{

        /**

         * 回调方法， 返回MyScrollView滑动的Y方向距离

         */

        public void onScroll(int scrollY);

    }



    private OnGetBottomListener onGetBottomListener;

}
