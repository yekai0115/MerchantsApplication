package com.ddz.mearchant.dialog.avloading;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddz.mearchant.R;


/**
 * Created by StormShadow on 2017/3/18.
 * Knowledge is power.
 */
public class DialogGraphicDetails {

    public void showDialog(Activity activity, String msg){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_graphic_details);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView text = (TextView) dialog.findViewById(R.id.dialog_confirm_message);
        text.setText(msg);

        TextView okBtn = (TextView) dialog.findViewById(R.id.dialog_confirm_ok);
        TextView cancelBtn = (TextView) dialog.findViewById(R.id.dialog_confirm_cancel);
        LinearLayout textLinear = (LinearLayout) dialog.findViewById(R.id.text_introduce_linear);
        LinearLayout imageLinear = (LinearLayout) dialog.findViewById(R.id.image_introduce_linear);
        textLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onClick(true);
                }
                dialog.dismiss();
            }
        });
        imageLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onClick(false);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private OnTextImageClickedListener listener;

    public void setListener(OnTextImageClickedListener listener) {
        this.listener = listener;
    }

    public interface OnTextImageClickedListener {
        void onClick(boolean isTextClicked);
    }
}
