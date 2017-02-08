package com.zzl.zl_app.apk.load;

import android.database.Cursor;

import com.zzl.zl_app.apk.db.APKLoadDB;

/**
 * 下载线程
 * 
 * @author zhouzhilong
 * 
 */
public class APKLoadThreadInfo {

	public int thread_id;
	public int start_pos;
	public int end_pos;
	public int compelete_size;
	public String apk_url;
	public String apk_name;
	public int file_size;

	public APKLoadThreadInfo() {

	}

	public APKLoadThreadInfo(int threadId, int startPos, int endPos,
			int compeleteSize, String url, String name, int fileSize) {
		super();
		this.thread_id = threadId;
		this.start_pos = startPos;
		this.end_pos = endPos;
		this.compelete_size = compeleteSize;
		this.apk_url = url;
		this.apk_name = name;
		this.file_size = fileSize;
	}

	public static class ThreadTable {

		public static final String TAB_NAME = "apk_load_thread";

		// 字段名
		public static final String _ID = "_id";
		public static final String ThreadTable_ID = "thread_id";
		public static final String ThreadTable_start_pos = "start_pos";
		public static final String ThreadTable_end_pos = "end_pos";
		public static final String ThreadTable_compelete_size = "compelete_size";
		public static final String ThreadTable_apk_url = "apk_url";
		public static final String ThreadTable_apk_name = "apk_name";
		public static final String ThreadTable_file_size = "file_size";

		// 列编号
		public static final int ThreadTable_ID_INDEX = 1;
		public static final int ThreadTable_start_pos_INDEX = 2;
		public static final int ThreadTable_end_pos_INDEX = 3;
		public static final int ThreadTable_compelete_size_INDEX = 4;
		public static final int ThreadTable_apk_url_INDEX = 5;
		public static final int ThreadTable_apk_name_INDEX = 6;
		public static final int ThreadTable_file_size_INDEX = 7;

		public static APKLoadThreadInfo getAPKLoadThread(APKLoadDB db,
				String url) {
			Cursor cursor = db.query(TAB_NAME, null,
					ThreadTable_apk_url + "=?", new String[] { url }, null,
					null, null);
			cursor.moveToFirst();
			if (cursor.getCount() == 0) {
				cursor.close();
				return null;
			}
			return getAPKLoadThread(cursor);

		}

		public static APKLoadThreadInfo getAPKLoadThread(Cursor cursor) {
			APKLoadThreadInfo thread = new APKLoadThreadInfo();
			thread.thread_id = cursor.getInt(ThreadTable_ID_INDEX);
			thread.start_pos = cursor.getInt(ThreadTable_start_pos_INDEX);
			thread.end_pos = cursor.getInt(ThreadTable_end_pos_INDEX);
			thread.compelete_size = cursor
					.getInt(ThreadTable_compelete_size_INDEX);
			thread.apk_url = cursor.getString(ThreadTable_apk_url_INDEX);
			thread.apk_name = cursor.getString(ThreadTable_apk_name_INDEX);
			thread.file_size = cursor.getInt(ThreadTable_file_size_INDEX);
			cursor.close();
			return thread;
		}
	}

}
