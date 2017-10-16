package com.ddz.mearchant.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.ddz.mearchant.BaseApplication;
import com.ddz.mearchant.common.CreateFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ImageUrl {
	public static ArrayList<String> smallImageArrayList = new ArrayList<String>();
	public static final String USER_COMPRESS_IMAGE_SIZE = "75x75";// 存放用户压缩图片的文件名，以压缩大小命名
	public static final String USER_COMPRESS_IMAGE_SIZE_WIDTH = "75";// 压缩后的宽
	public static final String USER_COMPRESS_IMAGE_SIZE_HIGHT = "75";// 压缩后的高

	public static final String FEEDBACK_COMPRESS_IMAGE_SIZE = "800*480";// 存放投诉压缩图片的文件名，以压缩大小命名
	public static final String FEEDBACK_COMPRESS_IMAGE_SIZE_WIDTH = "800";// 压缩后的宽
	public static final String FEEDBACK_COMPRESS_IMAGE_SIZE_HIGHT = "480";// 压缩后的高
	/**
	 * 对图片进行管理的工具类
	 */
	public static File imageFile = null ;


	/**
     * 根据一个网络连接(String)获取bitmap图像 
     *  
     * @param imageUri 
     * @return 
     * @throws
     */  
    public static void setbitmap(final String imageUri,final ImageView imageView) throws IOException {  
        // 显示网络上的图片  
        final Bitmap bitmap;  
        new AsyncTask<Void, Void, Bitmap>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected Bitmap doInBackground(Void... params) {
				Bitmap imageBitmap = null;
				try {
					URL myFileUrl = new URL(imageUri);  
		            HttpURLConnection conn = (HttpURLConnection) myFileUrl  
		                    .openConnection();  
		            conn.setDoInput(true);  
		            conn.connect();  
		            InputStream is = conn.getInputStream();  
		            imageBitmap = BitmapFactory.decodeStream(is);  
		            is.close();  
				} catch (Exception e) {
					Log.e("GirdViewFeedBackAdapter", e.getMessage(), e);
				}
				return imageBitmap;
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				if (result == null) {
				} else {
					imageView.setImageBitmap(result);
				}
			}
		}.executeOnExecutor(BaseApplication.FIXED_EXECUTOR);
    }

	/**
	 * 根据图片的url路径获得Bitmap对象
	 * @param url
	 * @return
	 */
	public static Bitmap returnBitmap(String url) {
		URL fileUrl = null;
		Bitmap bitmap = null;

		try {
			URL myFileUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;

	}
	/**
	 * 根据传入的URL，对图片进行加载。如果这张图片已经存在于SD卡中，则直接从SD卡里读取，否则就从网络上下载。
	 * 
	 * @param imageUrl
	 *            图片的URL地址
	 * @return 加载到内存的图片。
	 * @throws Exception 
	 */
	public static Bitmap loadImage(String imageUrl,String localImageUrl) throws Exception {
		File imageFile = new File(localImageUrl);
		if (!imageFile.exists()) {
			downFile(imageUrl,imageFile);
		}
		if (imageFile.exists()) {
			Bitmap bitmap = decodeSampledBitmapFromResource(imageFile.getPath());
			if (bitmap != null) {
				return bitmap;
			}
		}
		return null;
	}
	
	/**
	 * 根据传入的URL，对图片进行加载。如果这张图片已经存在于SD卡中，则直接从SD卡里读取，否则就从网络上下载。
	 * 
	 * @param imageUrl
	 *            图片的URL地址
	 * @return 加载到内存的图片。
	 * @throws Exception 
	 */
	/*public static Bitmap loadImageForImageWall(String imageUrl,boolean isSmall) {
		String localImageUrl = getLocalImagePath(imageUrl);
		File imageFile = new File(localImageUrl);
		if (!imageFile.exists()&&imageUrl!=null) {
			String photowall = EventImageWallActivity.PHOTOWALL_SMALL;
			if(!isSmall)
				photowall = EventImageWallActivity.PHOTOWALL;
			String newImageUrl = imageUrl.replace(EventImageWallActivity.PHOTOWALL_SMALL, photowall);
			downFile(newImageUrl,imageFile);
		}
		if (imageFile.exists()) {
			Bitmap bitmap = decodeSampledBitmapFromResource(imageFile.getPath());
			if (bitmap != null) {
				return bitmap;
			}else{
				imageFile.delete();
			}
		}
		return null;
	}*/
	/**
	 * 获取图片的本地存储路径。
	 * 
	 * @param imageUrl   图片的URL地址。
	 * @param isSmall  true为缩略图，false为大图
	 * @return 图片的本地存储路径。
	 */
	/*public static String getImagePathForImageWall(String imageUrl,boolean isSmall) {
		String photowall = EventImageWallActivity.PHOTOWALL_SMALL ;
		if(!isSmall)
			photowall = EventImageWallActivity.PHOTOWALL;
		int lastSlashIndex = imageUrl.lastIndexOf("/");
		String imageName = imageUrl.substring(lastSlashIndex + 1);
		String imageDir = CreateFile.Third_PATH+photowall+"/";
		File file = new File(imageDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		String imagePath = imageDir + imageName;
		return imagePath;
	}*/
	
	/**
	 * 获取图片的本地存储路径。
	 * 
	 * @param imageUrl   图片的URL地址。比如 
	 * http://osstest.ezoutwork.com/event/image/241/publicize/1414032259929.jpg
	 * http://osstest.ezoutwork.com/event/image/241/describe.jpg
	 * 得到的本地路径为241_publicize_1414032259929.jpg，241_describe.jpg
	 * @param
	 * @return 图片的本地存储路径。
	 */
	public static String getLocalImagePath(String imageUrl) {
		String imageName ="";
		String[] args1 = imageUrl.split("image/");
		if(args1.length >1){
			imageName =args1[1].replace("/", "_");
		}
		String imageDir = CreateFile.Quartus_PATH;
		File file = new File(imageDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		String imagePath = imageDir + imageName ;
		return imagePath;
	}
	
	/**
	 * 根据传入的URL，对图片进行加载。如果这张图片已经存在于SD卡中，则直接从SD卡里读取，否则就从网络上下载。
	 * 
	 * @param imageUrl
	 *            图片的URL地址
	 * @return 加载到内存的图片。
	 * @throws Exception 
	 */
	public static Bitmap loadEventInfoImage(String imageUrl) throws Exception {
		String localImageUrl = getLocalImagePath(imageUrl);
		File imageFile = new File(localImageUrl);
		if (!imageFile.exists()) {
			File file = downFile(imageUrl, imageFile);
			return  BitmapFactory.decodeFile(file.getPath());
		}
		if (imageUrl != null) {
			Bitmap bitmap = decodeSampledBitmapFromResource(imageFile.getPath());
			if (bitmap != null) {
				return bitmap;
			}
		}
		return null;
	}
	
	public static File downFile(String imageUrl ,File imageFile){
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Log.d("TAG", "monted sdcard");
		} else {
			Log.d("TAG", "has no sdcard");
		}
		HttpURLConnection con = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try{
			URL url = new URL(imageUrl);
			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(5 * 1000);
			con.setReadTimeout(15 * 1000);
			con.setDoInput(true);
			// con.setDoOutput(true);
			bis = new BufferedInputStream(con.getInputStream());
			if(!imageFile.getParentFile().exists()){
				imageFile.getParentFile().mkdirs();
			}
			fos = new FileOutputStream(imageFile);
			bos = new BufferedOutputStream(fos);
			byte[] b = new byte[1024];
			int length;
			while ((length = bis.read(b)) != -1) {
				bos.write(b, 0, length);
				bos.flush();
			}
		}catch(Exception e){
			imageFile.delete();
			Log.e(ImageUrl.class.getName(), e.getMessage(), e);
		}finally{
			try{
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
			}catch(Exception e){
				imageFile.delete();
				Log.e(ImageUrl.class.getName(), e.getMessage(), e);
			}
			if (con != null) {
				con.disconnect();
			}
		}
		return imageFile;
	}
	
	public static Bitmap decodeSampledBitmapFromResource(String pathName) {
		return CommonUtils.decodeFile(pathName);
	}
}
