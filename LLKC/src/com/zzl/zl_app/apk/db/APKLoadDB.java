package com.zzl.zl_app.apk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zzl.zl_app.apk.load.APKLoadThreadInfo;

public class APKLoadDB extends SQLiteOpenHelper {
	// download.db-->数据库名

	private SQLiteDatabase sqliteDB;

	private static APKLoadDB gagaDB;

	public static APKLoadDB getHallDBInstance(Context context) {
		if (gagaDB == null) {
			gagaDB = new APKLoadDB(context);
		}
		return gagaDB;
	}

	private APKLoadDB(Context context) {
		super(context, "download.db3", null, 1);
	}

	/**
	 * 在download.db数据库下创建一个download_info表存储下载信息
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table  if not exists "
				+ APKLoadThreadInfo.ThreadTable.TAB_NAME
				+ "(_id integer PRIMARY KEY AUTOINCREMENT, "
				+ APKLoadThreadInfo.ThreadTable.ThreadTable_ID + " integer, "
				+ APKLoadThreadInfo.ThreadTable.ThreadTable_start_pos
				+ " integer, "
				+ APKLoadThreadInfo.ThreadTable.ThreadTable_end_pos
				+ " integer, "
				+ APKLoadThreadInfo.ThreadTable.ThreadTable_compelete_size
				+ " integer,"
				+ APKLoadThreadInfo.ThreadTable.ThreadTable_apk_url + " char,"
				+ APKLoadThreadInfo.ThreadTable.ThreadTable_apk_name + " char,"
				+ APKLoadThreadInfo.ThreadTable.ThreadTable_file_size
				+ " integer)");
	}

	/**
	 * 查询表数据
	 * 
	 * @param tableName
	 *            表名
	 * @param columns
	 *            字段
	 * @param where
	 *            判断句
	 * @param whereArgs
	 *            判断字段对于的判断值
	 * @param groupBy
	 *            分组
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public synchronized Cursor query(String tableName, String[] columns,
			String where, String[] whereArgs, String groupBy, String having,
			String orderBy) {
		// create and/or open a database (只读)
		Cursor cursor = null;
		try {
			sqliteDB = getReadableDatabase();
			cursor = sqliteDB.query(tableName, columns, where, whereArgs,
					groupBy, having, orderBy);
		} catch (Exception e) {
		}
		return cursor;
	}

	public boolean insertAPKLoadThread(APKLoadThreadInfo thread) {
		SQLiteDatabase sqldb = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(APKLoadThreadInfo.ThreadTable.ThreadTable_ID,
				thread.thread_id);
		values.put(APKLoadThreadInfo.ThreadTable.ThreadTable_start_pos,
				thread.start_pos);
		values.put(APKLoadThreadInfo.ThreadTable.ThreadTable_end_pos,
				thread.end_pos);
		values.put(APKLoadThreadInfo.ThreadTable.ThreadTable_compelete_size,
				thread.compelete_size);
		values.put(APKLoadThreadInfo.ThreadTable.ThreadTable_apk_url,
				thread.apk_url);
		values.put(APKLoadThreadInfo.ThreadTable.ThreadTable_apk_name,
				thread.apk_name);
		values.put(APKLoadThreadInfo.ThreadTable.ThreadTable_file_size,
				thread.file_size);
		sqldb.insert(APKLoadThreadInfo.ThreadTable.TAB_NAME,
				APKLoadThreadInfo.ThreadTable._ID, values);
		sqldb.close();
		return true;
	}

	public boolean updateAPKLoadThread(int thread_id, String url,
			int compelete_size) {
		SQLiteDatabase sqldb = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(APKLoadThreadInfo.ThreadTable.ThreadTable_compelete_size,
				compelete_size);
		sqldb.update(APKLoadThreadInfo.ThreadTable.TAB_NAME, values,
				APKLoadThreadInfo.ThreadTable.ThreadTable_ID + "=? and "
						+ APKLoadThreadInfo.ThreadTable.ThreadTable_apk_url
						+ "=?", new String[] { thread_id + "", url });
		sqldb.close();
		return true;
	}

	/**
	 * 删除数据
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteData(String tableName, String whereClause,
			String[] whereArgs) {
		SQLiteDatabase sqldb = getWritableDatabase();
		sqldb.delete(tableName, whereClause, whereArgs);
		sqldb.close();
		return true;
	}

	/**
	 * 关闭数据库
	 */
	public void closeDB() {
		if (sqliteDB != null) {
			sqliteDB.close();
			sqliteDB = null;
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
