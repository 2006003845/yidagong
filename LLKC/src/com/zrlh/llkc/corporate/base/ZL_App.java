package com.zrlh.llkc.corporate.base;

import java.io.File;

import android.app.Application;

public class ZL_App extends Application {

	private static ZL_App mInstance;

	public boolean m_bKeyRight = true;

	// private FinalBitmap fb;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		// fb = FinalBitmap.create(this);

	}

	public static ZL_App getInstance() {
		return mInstance;
	}

	public long getFreeMenorySize() {
		long totleM = Runtime.getRuntime().freeMemory();

		return totleM;
	}

	public void cleanCache() {
		// fb.clearCache();
		// File file = CacheManager.getCacheFileBaseDir();
		// if (file != null && file.exists() && file.isDirectory()) {
		// for (File item : file.listFiles()) {
		// item.delete();
		// }
		// file.delete();
		// }
		this.deleteDatabase("webview.db");
		this.deleteDatabase("webviewCache.db");
	}

	public int version;

}
