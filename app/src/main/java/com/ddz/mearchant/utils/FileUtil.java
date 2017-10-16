package com.ddz.mearchant.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
	/**
	 * 把图片压缩到200K
	 * 
	 * @param oldpath
	 *            压缩前的图片路径
	 * @param newPath
	 *            压缩后的图片路径
	 * @return
	 */
	public static File compressFile(String oldpath, String newPath) {
		Bitmap compressBitmap = FileUtil.decodeFile(oldpath);
		Bitmap newBitmap = ratingImage(oldpath, compressBitmap);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		newBitmap.compress(CompressFormat.PNG, 100, os);
		byte[] bytes = os.toByteArray();
		
		File file = null ;
		try {
			file = FileUtil.getFileFromBytes(bytes, newPath);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(newBitmap != null ){
				if(!newBitmap.isRecycled()){
					newBitmap.recycle();
				}
				newBitmap  = null;
			}
			if(compressBitmap != null ){
				if(!compressBitmap.isRecycled()){
					compressBitmap.recycle();
				}
				compressBitmap  = null;
			}
		}
		return file;
	}
	/**
	 * Save image to the SD card 
	 * @param photoBitmap
	 * @param photoName
	 * @param path
	 */
	public static void savePhotoToSDCard(Bitmap photoBitmap,String path,String photoName){
		if (checkSDCardAvailable()) {
			File dir = new File(path);
			if (!dir.exists()){
				dir.mkdirs();
			}
			
			File photoFile = new File(path , photoName);
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(CompressFormat.JPEG, 90, fileOutputStream)) {
						fileOutputStream.flush();
//						fileOutputStream.close();
					}
				}
			} catch (FileNotFoundException e) {
				photoFile.delete();
				e.printStackTrace();
			} catch (IOException e) {
				photoFile.delete();
				e.printStackTrace();
			} finally{
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} 
	}
	//存储进SD卡
    public static void saveFile(Bitmap bm, String fileName) throws Exception {
        File dirFile = new File(fileName);  
        //检测图片是否存在
        if(dirFile.exists()){  
            dirFile.delete();  //删除原图片
        }  
        File myCaptureFile = new File(fileName);  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
        //100表示不进行压缩，70表示压缩率为30%
        bm.compress(CompressFormat.JPEG, 100, bos);
        bos.flush();  
        bos.close();  
    } 
	private static Bitmap ratingImage(String filePath,Bitmap bitmap){
		int degree = readPictureDegree(filePath);
		return rotaingImageView(degree, bitmap);
	}
	/**
	 * 检查sd卡是否存在 
	 * @return
	 */
	public static boolean checkSDCardAvailable(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	/**
	 *  旋转图片
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
		Matrix matrix = new Matrix();;
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
        		bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
	
	/**
	 * 读取图片属性：旋转的角度
	 * @param path 图片绝对路径
	 * @return degree旋转的角度
	 */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
                ExifInterface exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
        } catch (IOException e) {
                e.printStackTrace();
        }
        return degree;
    }

	/**
	 * 把字节数组保存为一个文件
	 * 
	 * @param b
	 * @param outputFile
	 * @return
	 */
	public static File getFileFromBytes(byte[] b, String outputFile) {
		File ret = null;
		BufferedOutputStream stream = null;
		try {
			ret = new File(outputFile);
			FileOutputStream fstream = new FileOutputStream(ret);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			// log.error("helper:get file from byte process error!");
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// log.error("helper:get file from byte process error!");
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
	/**
	 * 等比压缩图片
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}
	 //压缩图片尺寸
    public static Bitmap compressBySize(String pathName, int targetWidth,  
            int targetHeight) {  
        BitmapFactory.Options opts = new BitmapFactory.Options();  
        opts.inJustDecodeBounds = true;// 不去真的解析图片，只是获取图片的头部信息，包含宽高等；  
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);
        // 得到图片的宽度、高度；  
        float imgWidth = opts.outWidth;  
        float imgHeight = opts.outHeight;  
        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于等于该比例的最小整数；  
        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);  
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight); 
        opts.inSampleSize = 1;
        if (widthRatio > 1 || widthRatio > 1) {  
            if (widthRatio > heightRatio) {  
                opts.inSampleSize = widthRatio;  
            } else {  
                opts.inSampleSize = heightRatio;  
            }  
        }  
        //设置好缩放比例后，加载图片进内容；  
        opts.inJustDecodeBounds = false;  
        bitmap = BitmapFactory.decodeFile(pathName, opts);  
        return bitmap;  
    }
	/**
	 * 图片压缩
	 * 
	 * @param fPath
	 * @return
	 */
	public static Bitmap decodeFile(String fPath) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		opts.inDither = false; // Disable Dithering mode
		opts.inPurgeable = true; // Tell to gc that whether it needs free
		opts.inInputShareable = true; // Which kind of reference will be used to
		BitmapFactory.decodeFile(fPath, opts);
		final int REQUIRED_SIZE = 200;
		int scale = 1;
		if (opts.outHeight > REQUIRED_SIZE || opts.outWidth > REQUIRED_SIZE) {
			final int heightRatio = Math.round((float) opts.outHeight
					/ (float) REQUIRED_SIZE);
			final int widthRatio = Math.round((float) opts.outWidth
					/ (float) REQUIRED_SIZE);
			scale = heightRatio < widthRatio ? heightRatio : widthRatio;//
		}
		Log.i("scale", "scal ="+ scale);
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = scale;
		Bitmap bm = BitmapFactory.decodeFile(fPath, opts).copy(Config.ARGB_8888, false);
		return bm;
	}
	
	
	
	/**
	 * 创建目录
	 * @param path
	 */
	public static void setMkdir(String path)
	{
		File file = new File(path);
		if(!file.exists())
		{
			file.mkdirs();
			Log.e("file", "目录不存在  创建目录    ");
		}else{
			Log.e("file", "目录存在");
		}
	}
	
	/**
	 * 获取目录名称
	 * @param url
	 * @return FileName
	 */
	public static String getFileName(String url)
	{
		int lastIndexStart = url.lastIndexOf("/");
		if(lastIndexStart!=-1)
		{
			return url.substring(lastIndexStart+1, url.length());
		}else{
			return null;
		}
	}
	
	/**
	 * 删除该目录下的文件
	 * 
	 * @param path
	 */
	public static void delFile(String path) {
		if (!TextUtils.isEmpty(path)) {
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
		}
	}
}
