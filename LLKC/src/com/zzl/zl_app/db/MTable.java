package com.zzl.zl_app.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.entity.MMsg;
import com.zzl.zl_app.entity.Msg;

/**
 * 消息总的索引
 * 
 * @author zzl
 * 
 */
public class MTable extends IMsgDBOper {

	private static MTable oper;

	public static final String MTable_Label = "_label";
	public static final int MTable_Label_Index = 12;
	public static final String MTable_LName = "_lname";
	public static final int MTable_LName_Index = 13;

	private MTable(Context context) {
		super(context);
	}

	public static IMsgDBOper getDBOper(Context context) {
		if (oper == null) {
			oper = new MTable(context);
		}
		return oper;
	}

	@Override
	public String getTableName(String value) {
		return "t_msg";
	}

	@Override
	public boolean creatTable(String tableName) throws SQLiteException {
		String sql = "create table if not exists " + getTableName("") + "("
				+ BaseMsgTable._ID + " integer primary key autoincrement,"
				+ BaseMsgTable.Msg_ID + " varchar(10)," + BaseMsgTable.Msg_Type
				+ " varchar(5)," + BaseMsgTable.Msg_Content + " varchar,"
				+ BaseMsgTable.Msg_Time + " varchar,"
				+ BaseMsgTable.Msg_SenderId + " varchar,"
				+ BaseMsgTable.Msg_SenderName + " varchar,"
				+ BaseMsgTable.Msg_SenderRname + " varchar,"
				+ BaseMsgTable.Msg_Head + " varchar,"
				+ BaseMsgTable.Msg_Account + " varchar,"
				+ BaseMsgTable.Msg_SendState + " integer,"
				+ BaseMsgTable.Msg_NewItem + " integer," + MTable_Label
				+ " varchar," + MTable_LName + " varchar)";
		helper.createTable(sql);
		return true;
	}

	@Override
	public boolean insertMsg(Msg msg, String tableName) throws SQLiteException {
		Msg m = msg;
		ContentValues values = new ContentValues();
		values.put(BaseMsgTable.Msg_ID, m.id);
		values.put(BaseMsgTable.Msg_Type, m.type);
		values.put(BaseMsgTable.Msg_Content, m.content);
		values.put(BaseMsgTable.Msg_Time, m.time);
		values.put(BaseMsgTable.Msg_SenderId, m.senderId);
		values.put(BaseMsgTable.Msg_SenderName, m.senderName);
		values.put(BaseMsgTable.Msg_SenderRname, m.sRName);
		values.put(BaseMsgTable.Msg_Head, m.head);
		if (LlkcBody.USER_ACCOUNT != null)
			values.put(BaseMsgTable.Msg_Account, LlkcBody.USER_ACCOUNT);
		else {
			SharedPreferences sp = mContext.getSharedPreferences("account",
					Context.MODE_PRIVATE);
			String name = sp.getString("account", "");
			values.put(BaseMsgTable.Msg_Account, name);
		}
		values.put(BaseMsgTable.Msg_SendState, m.state);
		values.put(BaseMsgTable.Msg_NewItem, m.newitems);
		return helper.insertObj(tableName, BaseMsgTable._ID, values);
	}

	public boolean insertMsg(Msg msg, String tableName, String label,
			String lname, String head) throws SQLiteException {
		Msg m = msg;
		ContentValues values = new ContentValues();
		values.put(BaseMsgTable.Msg_ID, m.id);
		values.put(BaseMsgTable.Msg_Type, m.type);
		values.put(BaseMsgTable.Msg_Content, m.content);
		values.put(BaseMsgTable.Msg_Time, m.time);
		values.put(BaseMsgTable.Msg_SenderId, m.senderId);
		values.put(BaseMsgTable.Msg_SenderName, m.senderName);
		values.put(BaseMsgTable.Msg_SenderRname, m.sRName);
		values.put(BaseMsgTable.Msg_Head, head);
		if (LlkcBody.USER_ACCOUNT != null)
			values.put(BaseMsgTable.Msg_Account, LlkcBody.USER_ACCOUNT);
		else {
			SharedPreferences sp = mContext.getSharedPreferences("account",
					Context.MODE_PRIVATE);
			String name = sp.getString("account", "");
			values.put(BaseMsgTable.Msg_Account, name);
		}
		values.put(BaseMsgTable.Msg_SendState, m.state);
		values.put(BaseMsgTable.Msg_NewItem, m.newitems);
		values.put(MTable_Label, label);
		values.put(MTable_LName, lname);
		return helper.insertObj(tableName, BaseMsgTable._ID, values);
	}

	@Override
	public boolean updateMsg(Msg msg, String tableName) throws SQLiteException {
		Msg m = msg;
		ContentValues values = new ContentValues();
		values.put(BaseMsgTable.Msg_ID, m.id);
		values.put(BaseMsgTable.Msg_Type, m.type);
		values.put(BaseMsgTable.Msg_Content, m.content);
		values.put(BaseMsgTable.Msg_Time, m.time);
		values.put(BaseMsgTable.Msg_SenderId, m.senderId);
		values.put(BaseMsgTable.Msg_SenderName, m.senderName);
		values.put(BaseMsgTable.Msg_SenderRname, m.sRName);
		values.put(BaseMsgTable.Msg_Head, m.head);
		if (LlkcBody.USER_ACCOUNT != null)
			values.put(BaseMsgTable.Msg_Account, LlkcBody.USER_ACCOUNT);
		else {
			SharedPreferences sp = mContext.getSharedPreferences("account",
					Context.MODE_PRIVATE);
			String name = sp.getString("account", "");
			values.put(BaseMsgTable.Msg_Account, name);
		}
		values.put(BaseMsgTable.Msg_SendState, m.state);
		values.put(BaseMsgTable.Msg_NewItem, m.newitems);

		return helper.updateObj(tableName, BaseMsgTable.Msg_ID + "=? and "
				+ BaseMsgTable.Msg_Account + "=?", new String[] { m.id,
				LlkcBody.USER_ACCOUNT }, values);
	}

	public boolean updateMsg(Msg msg, String tableName, String label,
			String lname, String head) throws SQLiteException {
		Msg m = msg;
		ContentValues values = new ContentValues();
		values.put(BaseMsgTable.Msg_ID, m.id);
		values.put(BaseMsgTable.Msg_Type, m.type);
		values.put(BaseMsgTable.Msg_Content, m.content);
		values.put(BaseMsgTable.Msg_Time, m.time);
		values.put(BaseMsgTable.Msg_SenderId, m.senderId);
		values.put(BaseMsgTable.Msg_SenderName, m.senderName);
		values.put(BaseMsgTable.Msg_SenderRname, m.sRName);
		values.put(BaseMsgTable.Msg_Head, head);
		if (LlkcBody.USER_ACCOUNT != null)
			values.put(BaseMsgTable.Msg_Account, LlkcBody.USER_ACCOUNT);
		else {
			SharedPreferences sp = mContext.getSharedPreferences("account",
					Context.MODE_PRIVATE);
			String name = sp.getString("account", "");
			values.put(BaseMsgTable.Msg_Account, name);
		}
		values.put(BaseMsgTable.Msg_SendState, m.state);
		values.put(BaseMsgTable.Msg_NewItem, m.newitems);
		values.put(MTable_Label, label);
		values.put(MTable_LName, lname);
		return helper.updateObj(tableName, MTable_Label + "=? and "
				+ BaseMsgTable.Msg_Account + "=?", new String[] { label,
				LlkcBody.USER_ACCOUNT }, values);
	}

	@Override
	public Msg getMsg(Cursor cursor) {
		if (cursor == null || cursor.isClosed())
			return null;
		MMsg m = new MMsg();
		m._id = cursor.getInt(0);
		m.id = cursor.getString(BaseMsgTable.Msg_ID_Index);
		m.type = cursor.getString(BaseMsgTable.Msg_Type_Index);
		m.head = cursor.getString(BaseMsgTable.Msg_Head_Index);
		m.time = cursor.getString(BaseMsgTable.Msg_Time_Index);
		m.content = cursor.getString(BaseMsgTable.Msg_Content_Index);
		m.senderId = cursor.getString(BaseMsgTable.Msg_SenderId_Index);
		m.senderName = cursor.getString(BaseMsgTable.Msg_SenderName_Index);
		m.state = cursor.getInt(BaseMsgTable.Msg_SendState_Index);
		m.newitems = cursor.getInt(BaseMsgTable.Msg_NewItem_Index);
		m.sRName = cursor.getString(BaseMsgTable.Msg_SenderRname_Index);
		m.label = cursor.getString(MTable_Label_Index);
		m.lName = cursor.getString(MTable_LName_Index);
		return m;
	}

}
