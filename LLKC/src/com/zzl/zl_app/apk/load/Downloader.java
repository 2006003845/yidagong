package com.zzl.zl_app.apk.load;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.database.Cursor;
import android.net.wifi.WifiConfiguration.Protocol;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zrlh.llkc.corporate.ApplicationData;
import com.zzl.zl_app.apk.db.APKLoadDB;
import com.zzl.zl_app.connection.Connection;
import com.zzl.zl_app.connection.IOWriter;
import com.zzl.zl_app.entity.Game;
import com.zzl.zl_app.util.FileTools;

public class Downloader {
	private String urlstr;// 下载的地址
	private String apkName;
	private String localfile;// 保存路径
	private int threadId;// 线程数
	private Handler mHandler;// 消息处理器
	private int fileSize;// 所要下载的文件的大小
	private APKLoadThreadInfo info;// 存放下载信息类的集合
	private static final int INIT = 1;// 定义三种下载的状态：初始化状态，正在下载状态，暂停状态
	private static final int DOWNLOADING = 2;
	private static final int PAUSE = 3;
	private int state = INIT;
	private APKLoadDB apkDB;
	private Game gameItem;

	public Downloader(String urlstr, String localfile, int threadcount,
			Context context, Handler mHandler, Game item) {
		this.urlstr = urlstr;
		this.localfile = localfile;
		this.threadId = threadcount;
		this.mHandler = mHandler;
		this.gameItem = item;
		apkDB = APKLoadDB.getHallDBInstance(context);
	}

	/**
	 * 判断是否正在下载
	 */
	public boolean isdownloading() {
		return state == DOWNLOADING;
	}

	/**
	 * 得到downloader里的信息 首先进行判断是否是第一次下载，如果是第一次就要进行初始化，并将下载器的信息保存到数据库中
	 * 如果不是第一次下载，那就要从数据库中读出之前下载的信息（起始位置，结束为止，文件大小等），并将下载信息返回给下载器
	 */
	public void getDownloaderInfors() {
		if (isFirst(urlstr)) {
			return;
		} else {
			// 得到数据库中已有的urlstr的下载器的具体信息
			info = APKLoadThreadInfo.ThreadTable
					.getAPKLoadThread(apkDB, urlstr);
		}
	}

	/**
	 * 判断是否是第一次 下载
	 */
	private boolean isFirst(String urlstr) {
		int count = 0;
		try {
			Cursor cursor = apkDB.query(APKLoadThreadInfo.ThreadTable.TAB_NAME,
					null, APKLoadThreadInfo.ThreadTable.ThreadTable_apk_url
							+ "=?", new String[] { urlstr }, null, null, null);
			count = cursor.getCount();
			cursor.close();
		} catch (Exception e) {
		}
		return count == 0;
	}

	/**
	 * 114 * 利用线程开始下载数据 115
	 */
	public void download() {
		if (info != null) {
			if (state == DOWNLOADING) {
				Message msg = mHandler.obtainMessage(0, "正在下载...");
				mHandler.sendMessage(msg);
				return;
			}
			state = DOWNLOADING;
			new LoadThread(info).start();
		} else {
			if (state == DOWNLOADING)
				return;
			state = DOWNLOADING;
			new LoadThread(true, null).start();
		}
	}

	public class LoadThread extends Thread {
		private APKLoadThreadInfo APKLoadThreadInfo;
		private boolean isFirst;

		public void setAPKLoadThreadInfo(APKLoadThreadInfo APKLoadThreadInfo) {
			this.APKLoadThreadInfo = APKLoadThreadInfo;
		}

		public LoadThread(boolean isFirst, APKLoadThreadInfo APKLoadThreadInfo) {
			this.APKLoadThreadInfo = APKLoadThreadInfo;
			this.isFirst = isFirst;
			gameItem.isLoading = true;
			gameItem.isStartLoad = true;
		}

		public LoadThread(APKLoadThreadInfo APKLoadThreadInfo) {
			this.APKLoadThreadInfo = APKLoadThreadInfo;
		}

		@Override
		public void run() {
			HttpURLConnection connection = null;
			RandomAccessFile randomAccessFile = null;
			InputStream is = null;
			try {
				URL url = new URL(urlstr);
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(5000);
				connection.setRequestMethod("GET");

				if (isFirst) {
					fileSize = connection.getContentLength();
					info = this.APKLoadThreadInfo = new APKLoadThreadInfo(
							threadId, 0, fileSize, 0, urlstr, apkName, fileSize);
					apkDB.insertAPKLoadThread(info);
				} else {
					// 设置范围，格式为Range：bytes x-y;
					String value = "bytes="
							+ (APKLoadThreadInfo.start_pos + APKLoadThreadInfo.compelete_size)
							+ "-" + APKLoadThreadInfo.end_pos;
					connection.setRequestProperty("Range", value);
				}

				FileTools.creatFile(localfile);
				if (randomAccessFile == null) {
					// 本地访问文件
					randomAccessFile = new RandomAccessFile(localfile, "rwd");
					randomAccessFile.setLength(APKLoadThreadInfo.file_size);
				}
				randomAccessFile.seek(APKLoadThreadInfo.start_pos
						+ APKLoadThreadInfo.compelete_size);
				// 将要下载的文件写到保存在保存路径下的文件中
				is = connection.getInputStream();
				byte[] buffer = new byte[4096];
				int length = -1;
				while ((length = is.read(buffer)) != -1) {
					randomAccessFile.write(buffer, 0, length);
					APKLoadThreadInfo.compelete_size += length;
					// 更新数据库中的下载信息
					apkDB.updateAPKLoadThread(APKLoadThreadInfo.thread_id,
							urlstr, APKLoadThreadInfo.compelete_size);
					// 用消息将下载信息传给进度条，对进度条进行更新
					Message message = Message.obtain();
					message.what = 0;
					if (gameItem.fileSize == 1
							&& APKLoadThreadInfo.file_size != 0) {
						gameItem.fileSize = APKLoadThreadInfo.file_size;
					}
					gameItem.progress = APKLoadThreadInfo.compelete_size;
					message.obj = urlstr;
					message.arg1 = length;
					mHandler.sendMessage(message);

					if (APKLoadThreadInfo.compelete_size == APKLoadThreadInfo.file_size
							&& APKLoadThreadInfo.file_size > 0) {

						// 下载完成
						Message message2 = mHandler.obtainMessage();
						message2.what = 1;
						gameItem.fileSize = APKLoadThreadInfo.file_size;
						gameItem.progress = APKLoadThreadInfo.compelete_size;
						message2.obj = localfile;
						message2.arg1 = length;
						mHandler.sendMessage(message2);
						delete(urlstr);
						gameItem.isLoading = false;
						gameItem.isStartLoad = false;
					}
					if (state == PAUSE) {
						gameItem.isLoading = false;
						break;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
					randomAccessFile.close();
					connection.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (state == PAUSE) {
				return;
			}
		}
	}

	// 删除数据库中urlstr对应的下载器信息
	public void delete(String urlstr) {
		apkDB.deleteData(APKLoadThreadInfo.ThreadTable.TAB_NAME,
				APKLoadThreadInfo.ThreadTable.ThreadTable_apk_url + "=?",
				new String[] { urlstr });
	}

	// 设置暂停
	public void pause() {
		state = PAUSE;
	}

	// 重置下载状态
	public void reset() {
		state = INIT;
	}
}
