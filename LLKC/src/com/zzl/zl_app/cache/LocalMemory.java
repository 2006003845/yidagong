package com.zzl.zl_app.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;

import com.zzl.zl_app.util.FileReaderAndWriter;
import com.zzl.zl_app.util.FileTools;

public class LocalMemory {

	private LocalMemory() {

	}

	private static LocalMemory localMem;

	public void initStoreData(Context context, int appStringID) {
		String path = "";
		String app = context.getResources().getString(appStringID) + "_file";
		if (checkSDCard())
			path = Environment.getExternalStorageDirectory().getAbsolutePath();
		else
			path = context.getCacheDir().getAbsolutePath();
		FileConstant.savePath = path + "/" + app;
		// 先判断目录是否存在
		File dir = new File(FileConstant.savePath);
		if (!dir.exists()) { // 不存在则创建
			dir.mkdirs();
		}
		File dir2 = new File(FileConstant.savePath + FileConstant.Path_Img);
		if (!dir2.exists()) { // 不存在则创建
			dir2.mkdirs();
		}
		File dir3 = new File(FileConstant.savePath + FileConstant.Path_Txt);
		if (!dir3.exists()) { // 不存在则创建
			dir3.mkdirs();
		}
	}

	public static LocalMemory getInstance() {
		if (localMem == null) {
			localMem = new LocalMemory();
		}
		return localMem;
	}

	public boolean checkSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	public boolean clearFiles() throws IOException {
		if (FileConstant.savePath != null && !FileConstant.savePath.equals(""))
			return FileTools.deleteFiles(FileConstant.savePath);
		return false;
	}

	/**
	 * 
	 * @param drawable
	 * @param filename
	 */
	public void saveDrawable(BitmapDrawable drawable, String filename) {
		// 判断文件是否存在
		File file = new File(FileConstant.savePath + FileConstant.Path_Img
				+ "/" + filename);
		if (!file.exists()) { // 不存在则保存
			try {
				file.createNewFile();
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				if (drawable.getBitmap().compress(Bitmap.CompressFormat.PNG,
						100, fileOutputStream)) {
					fileOutputStream.flush();
				}
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveTxt(String txt, String fileName) {
		// 判断文件是否存在
		File file = new File(FileConstant.savePath + FileConstant.Path_Txt
				+ "/" + fileName);
		if (!file.exists()) {
			try {
				FileTools.creatFile(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileReaderAndWriter.fileWriter(file.getAbsolutePath(), txt);
		}
	}

	public String getTxt(String fileName) {
		// 判断文件是否存在
		File file = new File(FileConstant.savePath + FileConstant.Path_Txt
				+ "/" + fileName);
		if (!file.exists()) {
			return null;
		}
		return FileReaderAndWriter.fileReader(FileConstant.savePath
				+ FileConstant.Path_Txt + "/" + fileName);
	}

	public File getFile(String fileName) {
		// 判断文件是否存在
		File file = new File(FileConstant.savePath + FileConstant.Path_Txt
				+ "/" + fileName);
		if (!file.exists()) {
			return null;
		}
		return file;
	}

	/**
	 * 
	 * @param filename
	 * @param cate
	 * @return
	 */
	public BitmapDrawable getDrawable(String filename) {
		File file = new File(FileConstant.savePath + FileConstant.Path_Img
				+ "/" + filename);
		if (file.exists()) {
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				BitmapDrawable drawable = new BitmapDrawable(fileInputStream);
				return drawable;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * @param bm
	 * @param filename
	 * @param cate
	 * @return
	 */
	public String saveBitmap(Bitmap bm, String filename) {

		// 判断文件是否存在
		File image = new File(FileConstant.savePath + FileConstant.Path_Img
				+ "/" + filename);
		if (!image.exists()) { // 不存在则保存
			try {
				// image.createNewFile();
				FileTools.creatFile(image);
				FileOutputStream fileOutputStream = new FileOutputStream(image);
				if (bm.compress(Bitmap.CompressFormat.PNG, 100,
						fileOutputStream)) {
					fileOutputStream.flush();
				}
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image.getAbsolutePath();
	}

	public String saveBitmap2(Bitmap bm, String path) {

		// 判断文件是否存在
		File image = new File(path);
		try {
			FileTools.deleteFile(image);
		} catch (IOException e1) {
		}
		if (!image.exists()) { // 不存在则保存
			try {
				// image.createNewFile();
				FileTools.creatFile(image);
				FileOutputStream fileOutputStream = new FileOutputStream(image);
				if (bm.compress(Bitmap.CompressFormat.PNG, 100,
						fileOutputStream)) {
					fileOutputStream.flush();
				}
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image.getAbsolutePath();
	}

	/**
	 * 
	 * @param bm
	 * @param filename
	 * @return
	 */
	public String saveTempBitmap(Bitmap bm, String filename) {
		// 先判断目录是否存在
		File dir = new File(FileConstant.savePath + FileConstant.Path_Img
				+ "/temp");
		if (!dir.exists()) { // 不存在则创建
			dir.mkdirs();
		}
		// 判断文件是否存在
		File image = new File(FileConstant.savePath + FileConstant.Path_Img
				+ "/temp/" + filename);
		image.delete();
		if (!image.exists()) { // 不存在则保存
			try {
				// image.createNewFile();
				FileTools.creatFile(image);
				FileOutputStream fileOutputStream = new FileOutputStream(image);
				if (bm.compress(Bitmap.CompressFormat.PNG, 100,
						fileOutputStream)) {
					fileOutputStream.flush();
				}
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image.getAbsolutePath();
	}

	/**
	 * 
	 * @param filename
	 * @param cate
	 * @return
	 */
	public Bitmap getBitmap(String filename) {
		File image = new File(FileConstant.savePath + FileConstant.Path_Img
				+ "/" + filename);
		if (image.exists()) {
			try {
				FileInputStream fileInputStream = new FileInputStream(image);
				Bitmap drawable = BitmapFactory.decodeStream(fileInputStream);
				return drawable;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
