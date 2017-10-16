package com.ddz.mearchant.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.view.ClearEditText;
import com.ddz.mearchant.view.HandyTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/30 0030.
 */

public class PreviewImageActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext;
    private ClearEditText shopName;
    private HandyTextView save;
    private ArrayList<String> imageBitmapBase;
    private ListView imageList;
    private List<Bitmap> bitmaps;
    private ImageAdapter imageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);
        mContext=this;
        imageBitmapBase = getIntent().getStringArrayListExtra("imagePreviewList");
        initViews();
    }

    @Override
    protected void initViews() {
        imageList = (ListView)findViewById(R.id.image_list);
        imageAdapter = new ImageAdapter();
        imageList.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_rigit:
                break;
        }
    }
    @Override
    protected void initEvents() {

    }

    class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        @Override
        public int getCount() {
            return imageBitmapBase.size();
        }

        @Override
        public Object getItem(int position) {
            return imageBitmapBase.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView = View.inflate(PreviewImageActivity.this,R.layout.adapter_preview_image_list,null);
                holder = new ViewHolder();
                holder.mPic= (ImageView) convertView.findViewById(R.id.image_preview);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            try {

//                ImageUrl.setbitmap(tt,holder.mPic);
                Glide.with(mContext).load(imageBitmapBase.get(position)).fitCenter()
                            .placeholder(getResources().getColor(R.color.tab_text_diveder)).error(getResources().getColor(R.color.tab_text_diveder)).into(holder.mPic);

            }catch (Exception e){
                e.printStackTrace();
            }
            return convertView;
        }
    }

    public  class ViewHolder{
        private ImageView mPic;

    }
}