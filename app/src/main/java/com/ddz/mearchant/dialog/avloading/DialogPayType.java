package com.ddz.mearchant.dialog.avloading;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ddz.mearchant.R;
import com.ddz.mearchant.view.HandyTextView;

/**
 * Created by StormShadow on 2017/3/18.
 * Knowledge is power.
 */
public class DialogPayType {
    private String type = "6";
    public void showDialog(Activity activity,final onConfirmClickedListener listener,String amount,String payType) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_pay_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        int screenWidth = dm.widthPixels;
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.width = screenWidth;
        dialog.getWindow().setAttributes(layoutParams);

        final HandyTextView tv_order_money = (HandyTextView) dialog.findViewById(R.id.tv_order_money);
        final CheckBox cb_zhifubao = (CheckBox) dialog.findViewById(R.id.cb_zhifubao);
        final CheckBox cb_weixin = (CheckBox) dialog.findViewById(R.id.cb_weixin);
        final CheckBox cb_yinlian = (CheckBox) dialog.findViewById(R.id.cb_yinlian);
        final HandyTextView tv_pay = (HandyTextView) dialog.findViewById(R.id.tv_pay);
        final ImageView close_dialog = (ImageView) dialog.findViewById(R.id.close_dialog);
        LinearLayout zfb_linear = (LinearLayout) dialog.findViewById(R.id.zfb_linear);
        LinearLayout wx_linear = (LinearLayout) dialog.findViewById(R.id.wx_linear);
        LinearLayout yl_linear = (LinearLayout) dialog.findViewById(R.id.yl_linear);

        tv_order_money.setText(amount);
        if (payType.contains("1")){
            wx_linear.setVisibility(View.VISIBLE);
        }
        if (payType.contains("6")){
            zfb_linear.setVisibility(View.VISIBLE);
        }
        if (payType.contains("9")){
            yl_linear.setVisibility(View.VISIBLE);
        }
        type = "6";
        cb_zhifubao.setChecked(true);
        cb_zhifubao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    cb_zhifubao.setChecked(true);
                    cb_weixin.setChecked(false);
                    cb_yinlian.setChecked(false);
                    type = "6";
            }
        });
        cb_weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    cb_zhifubao.setChecked(false);
                    cb_weixin.setChecked(true);
                    cb_yinlian.setChecked(false);
                    type = "1";
            }
        });
        cb_yinlian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    cb_zhifubao.setChecked(false);
                    cb_weixin.setChecked(false);
                    cb_yinlian.setChecked(true);
                    type = "9";
            }
        });


        tv_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(type);
                dialog.dismiss();
            }
        });

        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public interface onConfirmClickedListener {
        void onClick(String type);
    }
}
