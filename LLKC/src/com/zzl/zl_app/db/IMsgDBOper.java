package com.zzl.zl_app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.zzl.zl_app.entity.Msg;

public abstract class IMsgDBOper {
	protected Context mContext;

	protected IMsgDBOper(Context context) {
		if (helper == null) {
			helper = DBHelper.getHallDBInstance(context);
		}
		mContext = context;
	}

	protected DBHelper helper;

	public void closeDB() {
		helper.closeDB();
	}

	public abstract String getTableName(String value);

	public abstract boolean creatTable(String tableName) throws SQLiteException;

	public Cursor query(String tableName, String[] columns, String where,
			String[] whereArgs, String groupBy, String having, String orderBy)
			throws SQLiteException {
		return helper.query(tableName, columns, where, whereArgs, groupBy,
				having, orderBy);
	}

	public Cursor query(String tableName, String[] columns, String where,
			String[] whereArgs, String groupBy, String having, String orderBy,
			String limite) throws SQLiteException {
		return helper.query(false, tableName, columns, where, whereArgs,
				groupBy, having, orderBy, limite);
	}

	public abstract boolean insertMsg(Msg msg, String tableName)
			throws SQLiteException;

	public abstract boolean updateMsg(Msg msg, String tableName)
			throws SQLiteException;

	public boolean delete(String tableName, String whereClause,
			String[] whereArgs) throws SQLiteException {
		return helper.deleteData(tableName, whereClause, whereArgs);
	}

	public boolean dropTable(String tableName) throws SQLiteException {
		helper.dropeTable(tableName);
		return true;
	}

	public abstract Msg getMsg(Cursor cursor);

	public List<Msg> getMsgList(Cursor cursor) throws SQLiteException {
		if (cursor == null) {
			return null;
		}
		if (cursor.isClosed())
			return null;
		cursor.moveToFirst();
		int count = cursor.getCount();
		ArrayList<Msg> list = new ArrayList<Msg>();
		do {
			if (count == 0) {
				break;
			}
			Msg item = getMsg(cursor);
			if (item != null) {
				list.add(item);
			}
		} while (cursor.moveToNext());
		cursor.close();
		return list;
	}

	public Msg isMsgExist(String tableName, String where, String[] whereArgs)
			throws SQLiteException {
		Cursor c = helper.query(tableName, null, where, whereArgs, null, null,
				null);
		if (c == null)
			return null;
		c.moveToFirst();
		int count = c.getCount();
		if (count == 0) {
			c.close();
			return null;
		}
		return getMsg(c);
	}

	public boolean tabbleIsExist(String tableName) {
		return helper.tabbleIsExist(tableName);
	}

}
