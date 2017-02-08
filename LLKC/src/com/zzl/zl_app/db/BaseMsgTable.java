package com.zzl.zl_app.db;

public class BaseMsgTable {

	// 字段
	public static final String _ID = "_id";

	public static final String Msg_ID = "id";
	public static final String Msg_Type = "type";
	public static final String Msg_Content = "content";
	public static final String Msg_Time = "time";
	public static final String Msg_SenderId = "sid";
	public static final String Msg_SenderName = "sname";
	public static final String Msg_SenderRname = "srname";
	public static final String Msg_Head = "head";
	public static final String Msg_Account = "account";
	public static final String Msg_SendState = "sendstate";
	public static final String Msg_NewItem = "newitems";

	public static final int SendState_Success = 1;
	public static final int SendState_Failed = 2;
	public static final int SendState_Ing = 3;

	// 列编号
	public static final int Msg_ID_Index = 1;
	public static final int Msg_Type_Index = 2;
	public static final int Msg_Content_Index = 3;
	public static final int Msg_Time_Index = 4;
	public static final int Msg_SenderId_Index = 5;
	public static final int Msg_SenderName_Index = 6;
	public static final int Msg_SenderRname_Index = 7;
	public static final int Msg_Head_Index = 8;
	public static final int Msg_Account_Index = 9;
	public static final int Msg_SendState_Index = 10;
	public static final int Msg_NewItem_Index = 11;

}
