package com.ddz.mearchant.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ddz.mearchant.R;
import com.ddz.mearchant.models.Balance;
import com.ddz.mearchant.view.HandyTextView;

/**
 * Created by StormShadow on 2017/3/18.
 * Knowledge is power.
 */
public class DialogSelectType {

    public void showDialog(Activity activity, Balance balance,final onConfirmClickedListener listener) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_select_giving_type);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        int screenWidth = dm.widthPixels;
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.width = screenWidth;
        dialog.getWindow().setAttributes(layoutParams);

        final LinearLayout incentiveLinear = (LinearLayout) dialog.findViewById(R.id.incentive_points_linear);
        LinearLayout goodLinear = (LinearLayout) dialog.findViewById(R.id.good_points_linear);
        HandyTextView incentiveNum = (HandyTextView) dialog.findViewById(R.id.incentive_num);
        HandyTextView goodNum = (HandyTextView) dialog.findViewById(R.id.good_num);
        ImageView givingTypeClose = (ImageView) dialog.findViewById(R.id.giving_type_close);
        incentiveNum.setText("("+balance.getPoints()+")");
        goodNum.setText("("+balance.getMoney()+")");
        givingTypeClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        incentiveLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(0);
                dialog.dismiss();
            }
        });
        goodLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(1);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public interface onConfirmClickedListener {
        void onClick(int positoin);
    }
}
