package com.zzl.zl_app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.zrlh.llkc.corporate.PlatformAPI;

/**
 * 
 * @author zhouzhilong
 * 
 */
public class DBHelper extends SQLiteOpenHelper {

	private SQLiteDatabase sqliteDB;

	private static DBHelper epDB;

	public static DBHelper getHallDBInstance(Context context) {
		if (epDB == null) {
			epDB = new DBHelper(context);
		}
		return epDB;
	}

	private DBHelper(Context context) {
		super(context, PlatformAPI.DB, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		sqliteDB = db;
	}

	public void createTable(String sql) {
		if (sqliteDB == null)
			sqliteDB = getWritableDatabase();
		if (sqliteDB != null)
			sqliteDB.execSQL(sql);
	}

	/**
	 * 查询表数据
	 * 
	 * @param tableName
	 *            表名
	 * @param columns
	 *            字段
	 * @param where
	 *            判断�?
	 * @param whereArgs
	 *            判断字段对于的判断�?
	 * @param groupBy
	 *            分组
	 * @param having
	 * @param orderBy
	 * @author zhouzhilong
	 * @return
	 */
	public synchronized Cursor query(String tableName, String[] columns,
			String where, String[] whereArgs, String groupBy, String having,
			String orderBy) throws SQLiteException {
		if (sqliteDB == null)
			sqliteDB = getWritableDatabase();
		Cursor c = sqliteDB.query(tableName, columns, where, whereArgs,
				groupBy, having, orderBy);
		return c;
	}

	/**
	 * 
	 * @param distinct
	 * @param tableName
	 * @param columns
	 * @param where
	 * @param whereArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limite
	 * @return
	 * @throws SQLiteException
	 */
	public synchronized Cursor query(boolean distinct, String tableName,
			String[] columns, String where, String[] whereArgs, String groupBy,
			String having, String orderBy, String limite)
			throws SQLiteException {
		// create and/or open a database (只读)
		if (sqliteDB == null)
			sqliteDB = getWritableDatabase();
		Cursor cursor = sqliteDB.query(distinct, tableName, columns, where,
				whereArgs, groupBy, having, orderBy, limite);
		return cursor;
	}

	/**
	 * 插入数据
	 * 
	 * @param tableName
	 * @param nullColumnHack
	 * @param values
	 * @return
	 * @throws SQLiteException
	 */
	public boolean insertObj(String tableName, String nullColumnHack,
			ContentValues values) throws SQLiteException {
		// SQLiteDatabase sqldb = getWritableDatabase();
		if (sqliteDB == null)
			sqliteDB = getWritableDatabase();
		long row = sqliteDB.insert(tableName, nullColumnHack, values);
		// sqldb.close();
		return row != -1;
	}

	/**
	 * 更新数据
	 * 
	 * @param tableName
	 * @param where
	 * @param whereArgs
	 * @param values
	 * @return
	 * @throws SQLiteException
	 */
	public boolean updateObj(String tableName, String where,
			String[] whereArgs, ContentValues values) throws SQLiteException {
		// SQLiteDatabase sqldb = getWritableDatabase();
		if (sqliteDB == null)
			sqliteDB = getWritableDatabase();
		int row = sqliteDB.update(tableName, values, where, whereArgs);
		// sqldb.close();
		return row >= 0;
	}

	/**
	 * 删除数据
	 * 
	 * @param id
	 * @return
	 * @author zhouzhilong
	 */
	public boolean deleteData(String tableName, String whereClause,
			String[] whereArgs) {
		// SQLiteDatabase sqldb = getWritableDatabase();
		if (sqliteDB == null)
			sqliteDB = getWritableDatabase();
		sqliteDB.delete(tableName, whereClause, whereArgs);
		// sqldb.close();
		return true;
	}

	public void dropeTable(String tableName) {
		sqliteDB.execSQL("DROP TABLE " + tableName);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public boolean tabbleIsExist(String tableName) {
		boolean result = false;
		if (tableName == null) {
			return false;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.getReadableDatabase();
			String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"
					+ tableName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
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
}
