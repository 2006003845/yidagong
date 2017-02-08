package com.zrlh.llkc.contentprovider;

import android.net.Uri;
import android.provider.BaseColumns;

public class ShareData {

	public static final String AUTHORITY = "com.zzl.zrlh.DataProvider";

	// BaseColumn类中已经包含了 _id字段
	public static final class User implements BaseColumns {
		public static final Uri CONTENT_URI = Uri
				.parse("content://com.zzl.zrlh.DataProvider");
		// 表数据列
		public static final String USER_ACCOUNT = "USER_ACCOUNT";// 账户
		public static final String USER_PWD = "USER_PWD";// 密码
		public static final String USER_ID = "USER_ID";// 昵称
		public static final String USER_NAME = "USER_NAME";// 昵称
		public static final String USER_SEX = "USER_SEX";// 性别
		public static final String USER_HEAD = "USER_HEAD";// 头像
		public static final String USER_SCORE = "USER_SCORE";// 积分

	}
}
