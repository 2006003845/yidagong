package com.zzl.zl_app.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.entity.GroupMsg;
import com.zzl.zl_app.entity.Msg;

public class GroupMsgDBOper extends IMsgDBOper {

	private static GroupMsgDBOper oper;

	private GroupMsgDBOper(Context context) {
		super(context);
	}

	public static IMsgDBOper getDBOper(Context context) {
		if (oper == null) {
			oper = new GroupMsgDBOper(context);
		}
		return oper;
	}

	public static final String GroupMsg_Gid = "gid";
	public static final int GroupMsg_Gid_Index = 12;
	public static final String GroupMsg_MType = "mtype";
	public static final int GroupMsg_MType_Index = 13;
	public static final String GroupMsg_SImg = "simg";
	public static final int GroupMsg_SImg_Index = 14;
	public static final String GroupMsg_BImg = "bimg";
	public static final int GroupMsg_BImg_Index = 15;
	public static final String GroupMsg_Voice = "voice";
	public static final int GroupMsg_Voice_Index = 16;
	public static final String GroupMsg_Length = "length";
	public static final int GroupMsg_Length_Index = 17;

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
				+ BaseMsgTable.Msg_NewItem + " integer," + GroupMsg_Gid
				+ " varchar(10)," + GroupMsg_MType + " varchar,"
				+ GroupMsg_SImg + " varchar," + GroupMsg_BImg + " varchar,"
				+ GroupMsg_Voice + " varchar," + GroupMsg_Length + " varchar)";
		helper.createTable(sql);
		return true;
	}

	@Override
	public boolean insertMsg(Msg msg, String tableName) throws SQLiteException {
		GroupMsg m = (GroupMsg) msg;
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
		values.put(GroupMsg_Gid, m.gId);
		values.put(GroupMsg_MType, m.mtype);
		values.put(GroupMsg_SImg, m.img_small);
		values.put(GroupMsg_BImg, m.img_big);
		values.put(GroupMsg_Voice, m.voice);
		values.put(GroupMsg_Length, m.length);
		return helper.insertObj(getTableName(m.gId), BaseMsgTable._ID, values);

	}

	@Override
	public boolean updateMsg(Msg msg, String tableName) throws SQLiteException {
		GroupMsg m = (GroupMsg) msg;
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
		values.put(GroupMsg_Gid, m.gId);
		values.put(GroupMsg_MType, m.mtype);
		values.put(GroupMsg_SImg, m.img_small);
		values.put(GroupMsg_BImg, m.img_big);
		values.put(GroupMsg_Voice, m.voice);
		values.put(GroupMsg_Length, m.length);
		return helper.updateObj(getTableName(m.gId), BaseMsgTable.Msg_ID
				+ "=? and " + BaseMsgTable.Msg_Account + "=?", new String[] {
				m.id, LlkcBody.USER_ACCOUNT }, values);
	}

	@Override
	public String getTableName(String value) {
		return "group_3msg" + value;
	}

	@Override
	public Msg getMsg(Cursor cursor) {
		if (cursor == null || cursor.isClosed())
			return null;
		GroupMsg m = new GroupMsg();
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
		m.gId = cursor.getString(GroupMsg_Gid_Index);
		m.mtype = cursor.getString(GroupMsg_MType_Index);
		m.img_small = cursor.getString(GroupMsg_SImg_Index);
		m.img_big = cursor.getString(GroupMsg_BImg_Index);
		m.voice = cursor.getString(GroupMsg_Voice_Index);
		m.length = cursor.getString(GroupMsg_Length_Index);
		return m;
	}

}
