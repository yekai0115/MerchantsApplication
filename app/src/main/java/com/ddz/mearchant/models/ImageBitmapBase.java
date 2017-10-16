package com.ddz.mearchant.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/6/30 0030.
 */

public class ImageBitmapBase implements Serializable{
    private List<Bitmap> bitmaps;

    public List<Bitmap> getBitmaps() {
        return bitmaps;
    }

    public void setBitmaps(List<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }
}
