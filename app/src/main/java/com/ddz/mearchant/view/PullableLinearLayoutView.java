package com.ddz.mearchant.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.ddz.mearchant.interfaces.Pullable;

/**
 * Created by Administrator on 2017/7/16 0016.
 */

public class PullableLinearLayoutView extends LinearLayout implements Pullable
{
    private boolean isNoList =false;
    public PullableLinearLayoutView(Context context)
    {
        super(context);
    }

    public PullableLinearLayoutView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PullableLinearLayoutView(Context context, AttributeSet attrs, int defStyle)
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
}
