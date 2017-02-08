package com.zzl.zl_app.cache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/**
 * 异步图像保存
 * 
 * @author starry
 * 
 */
public class AsyncImageSaver {
	private ExecutorService executorService = Executors
			.newSingleThreadExecutor();

	private static AsyncImageSaver asyncImageSaver;

	public static AsyncImageSaver getInstance() {
		if (asyncImageSaver == null) {
			asyncImageSaver = new AsyncImageSaver();
		}
		return asyncImageSaver;
	}

	public void saveImage(final Bitmap bitmap, final String name) {
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				LocalMemory.getInstance().saveBitmap(bitmap, name);
			}
		});
	}

	public void saveImage(final Context mContext, final BitmapDrawable bitmap,
			final String name, final String cate) {
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				LocalMemory.getInstance().saveDrawable(bitmap, name);
			}
		});
	}
}
