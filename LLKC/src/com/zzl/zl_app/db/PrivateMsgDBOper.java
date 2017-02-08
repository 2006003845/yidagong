package com.zzl.zl_app.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.entity.Msg;
import com.zzl.zl_app.entity.PrivateMsg;

public class PrivateMsgDBOper extends IMsgDBOper {

	private static PrivateMsgDBOper oper;

	private PrivateMsgDBOper(Context context) {
		super(context);
	}

	public static IMsgDBOper getDBOper(Context context) {
		if (oper == null) {
			oper = new PrivateMsgDBOper(context);
		}
		return oper;
	}

	public static final String PrivateMsg_MType = "_mtype";
	public static final int PrivateMsg_MType_Index = 12;
	public static final String PrivateMsg_SImg = "_simg";
	public static final int PrivateMsg_SImg_Index = 13;
	public static final String PrivateMsg_BImg = "_bimg";
	public static final int PrivateMsg_BImg_Index = 14;
	public static final String PrivateMsg_Voice = "_voice";
	public static final int PrivateMsg_Voice_Index = 15;
	public static final String PrivateMsg_Length = "_length";
	public static final int PrivateMsg_Length_Index = 16;

	@Override
	public boolean creatTable(String tableName) throws SQLiteException {
		String sql = "create table if not exists " + tableName + "("
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
				+ BaseMsgTable.Msg_NewItem + " integer," + PrivateMsg_MType
				+ " varchar," + PrivateMsg_SImg + " varchar," + PrivateMsg_BImg
				+ " varchar," + PrivateMsg_Voice + " varchar,"
				+ PrivateMsg_Length + " varchar)";
		helper.createTable(sql);
		return true;
	}

	@Override
	public boolean insertMsg(Msg msg, String tableName) throws SQLiteException {
		PrivateMsg m = (PrivateMsg) msg;
		ContentValues values = new ContentValues();
		values.put(BaseMsgTable.Msg_ID, m.id);
		values.put(BaseMsgTable.Msg_SenderName, m.senderName);
		values.put(BaseMsgTable.Msg_Type, m.type);
		values.put(BaseMsgTable.Msg_Head, m.head);
		values.put(BaseMsgTable.Msg_Time, m.time);
		values.put(BaseMsgTable.Msg_Content, m.content);
		values.put(BaseMsgTable.Msg_SenderId, m.senderId);
		if (LlkcBody.USER_ACCOUNT != null)
			values.put(BaseMsgTable.Msg_Account, LlkcBody.USER_ACCOUNT);
		else {
			SharedPreferences sp = mContext.getSharedPreferences("account",
					Context.MODE_PRIVATE);
			String name = sp.getString("account", "");
			values.put(BaseMsgTable.Msg_Account, name);
		}
		values.put(BaseMsgTable.Msg_SenderRname, m.sRName);

		values.put(PrivateMsg_MType, m.mtype);
		values.put(PrivateMsg_SImg, m.img_small);
		values.put(PrivateMsg_BImg, m.img_big);
		values.put(PrivateMsg_Voice, m.voice);
		values.put(PrivateMsg_Length, m.length);
		return helper.insertObj(tableName, BaseMsgTable._ID, values);

	}

	@Override
	public boolean updateMsg(Msg msg, String tableName) throws SQLiteException {
		PrivateMsg m = (PrivateMsg) msg;
		ContentValues values = new ContentValues();
		values.put(BaseMsgTable.Msg_ID, m.id);
		values.put(BaseMsgTable.Msg_SenderName, m.senderName);
		values.put(BaseMsgTable.Msg_Type, m.type);
		values.put(BaseMsgTable.Msg_Head, m.head);
		values.put(BaseMsgTable.Msg_Time, m.time);
		values.put(BaseMsgTable.Msg_Content, m.content);
		values.put(BaseMsgTable.Msg_SenderId, m.senderId);
		if (LlkcBody.USER_ACCOUNT != null)
			values.put(BaseMsgTable.Msg_Account, LlkcBody.USER_ACCOUNT);
		else {
			SharedPreferences sp = mContext.getSharedPreferences("account",
					Context.MODE_PRIVATE);
			String name = sp.getString("account", "");
			values.put(BaseMsgTable.Msg_Account, name);
		}
		values.put(BaseMsgTable.Msg_SenderRname, m.sRName);
		values.put(PrivateMsg_MType, m.mtype);
		values.put(PrivateMsg_SImg, m.img_small);
		values.put(PrivateMsg_BImg, m.img_big);
		values.put(PrivateMsg_Voice, m.voice);
		values.put(PrivateMsg_Length, m.length);
		return helper.updateObj(tableName, BaseMsgTable.Msg_ID + "=? and "
				+ BaseMsgTable.Msg_Account + "=?", new String[] { m.id,
				LlkcBody.USER_ACCOUNT }, values);

	}

	@Override
	public String getTableName(String value) {
		return "private_3msg" + value;
	}

	@Override
	public Msg getMsg(Cursor cursor) {
		if (cursor == null || cursor.isClosed())
			return null;
		PrivateMsg m = new PrivateMsg();
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
		m.mtype = cursor.getString(PrivateMsg_MType_Index);
		m.img_small = cursor.getString(PrivateMsg_SImg_Index);
		m.img_big = cursor.getString(PrivateMsg_BImg_Index);
		m.voice = cursor.getString(PrivateMsg_Voice_Index);
		m.length = cursor.getString(PrivateMsg_Length_Index);
		return m;
	}

}
