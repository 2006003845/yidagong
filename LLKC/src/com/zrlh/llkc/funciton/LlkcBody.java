package com.zrlh.llkc.funciton;

import java.util.List;

import net.tsz.afinal.FinalDb;
import android.content.Context;
import android.os.Environment;

import com.zrlh.llkc.joggle.Account;
import com.zrlh.llkc.ui.LLKCApplication;

public class LlkcBody {

	public static boolean isGzip = false;

	public static String JPUSH_APPKEY = "847a3d448cd93a47770b2660";
	public static String JPUSH_MASTER_SECRET = "a4efdcfbb35e846eb2e55a8d";

	// APP名称
	public static String APP_NAME = "易打工";
	// 网络链接失败
	public static final String NETWORKBUG = "network_bug";
	// 手机imei号
	public static String IMEI;
	// 当前版本号
	public static String APP_Version;
	// 渠道类型
	public static String APP_Plat = "1"; // 1 ADNROID 2 IOS
	// appid
	public static String APP_Orgid = "LLKC";
	public static String CHANNEL = "wandoujia_market";
	public static boolean IS_STOP_REQUEST = false;// 请求线程是否停止
	/**
	 * 纬度
	 */
	public static double LAT;
	/**
	 * 经度longitude
	 */
	public static double LNG;
	// 查询工种
	public static String JOBS_STRING;
	// 查询城市
	public static String COLL_CITY_STRING;
	// 查询工种id
	public static String JOBS_ID_STRING;
	// 操作城市
	public static String CITY_STRING;
	// 当前定位城市
	public static String CITY_STRING_Current;
	// 帐号
	public static String USER_ACCOUNT;
	// 密码
	public static String PASS_ACCOUNT;
	// UID
	public static String UID_ACCOUNT;
	// 用户
	public static Account ACCOUNT;
	// 用户个人动态最新一条
	public static PersonalDynamic DYNAMIC;

	/**
	 * 时间:一天更新一次
	 */
	public static String Time = "";
	public static boolean haveCheckJpostRecommend = false;

	// 分享内容
	public static String SOURCE_CONTENT = "";
	// APP地址
	public static String APK_URL = "http://t.cn/zRTkK9p";

	// 动态经纬度
	public static double DYNAMIC_LAT, DYNAMIC_LNG;

	// 短信分享文本
	// public static String MSN_TEXT = "";

	public static String APP_DOWN = "";

	/**
	 * 是否为企业用户
	 */
	public static boolean isCorporate = false;

	public static void clear() {
		if (LLKCApplication.getInstance() != null) {
			LLKCApplication.getInstance().setLoginStat(false);
			LLKCApplication.getInstance().setAuthStat(State_Auth_No);
			LLKCApplication.getInstance().setUid(null);
			LLKCApplication.getInstance().setPwd(null);
			LLKCApplication.getInstance().setAccount(null);
			LLKCApplication.getInstance().savePersonInfo(null);
		}
		isCorporate = false;
		USER_ACCOUNT = null;
		PASS_ACCOUNT = null;
		ACCOUNT = null;

	}

	// /**
	// * 是否认证
	// */
	// public static int state_auth = 2;

	public static final int State_Auth_Success = 2;
	public static final int State_Auth_Ing = 1;
	public static final int State_Auth_No = 0;

	/**
	 * 是否登陆
	 */
	// public static boolean isLogin = false;

	public static boolean isLogin() {
		if (LLKCApplication.getInstance() != null)
			return LLKCApplication.getInstance().getLoginStat();
		return false;
	}

	public static int getAuthStat() {
		if (LLKCApplication.getInstance() != null)
			return LLKCApplication.getInstance().getAuthStat();
		return State_Auth_No;
	}

	public static String getMsnText() {
		if (LLKCApplication.getInstance() != null)
			return LLKCApplication.getInstance().getMsnText();
		return "";
	}

	/**
	 * 消息设置：是否接受岗位推荐 提示
	 */
	public static boolean isNewJpostPrompt = true;

	/**
	 * 好友消息提醒
	 */
	public static boolean isFriendMsgPrompt = true;
	/**
	 * 工友帮消息提醒
	 */
	public static boolean isFGroupMsgPrompt = false;
	/**
	 * 系统消息提醒
	 */
	public static boolean isSysMsgPrompt = false;
	/**
	 * 消息设置：声音提示
	 */
	public static boolean isNotiByVoice = false;
	/**
	 * 消息设置：震动提示
	 */
	public static boolean isNotiByShake = false;

	public static int Version_JobType = 1;

	// apk下载路径 APK_SD_PATH:/mnt/sdcard/myhotel/ APK_DATA_PATH：手机内置存储/data/data/
	public static final String APK_SD_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/llkc/";
	public static final String APK_DATA_PATH = Environment.getDataDirectory()
			.getAbsolutePath() + "/data/";

	public static boolean Friend_relation(Context context, String UID) {
		FinalDb db = FinalDb.create(context);
		List<Friend> friends_objs = db.findAllByWhere(Friend.class, "account='"
				+ LlkcBody.USER_ACCOUNT + "'");
		for (int i = 0; i < friends_objs.size(); i++) {
			if (UID.equals(friends_objs.get(i).getUid())) {
				return true;
			}
		}
		return false;
	}

	public static String GroupName(String groupid, Context context) {
		FinalDb db = FinalDb.create(context, true);
		String Group_name = "";

		List<Group> group_Lists = db.findAll(Group.class);
		for (int j = 0; j < group_Lists.size(); j++) {
			if (groupid.equals(group_Lists.get(j).gId)) {
				Group_name = group_Lists.get(j).gName;
				return Group_name;
			}
		}

		return Group_name;

	}

	// public static Bitmap combineImages(Bitmap c, Bitmap s) {
	// Bitmap cs = null;
	// int width, height = 0;
	// if(c.getWidth() > s.getWidth()) {
	// width = c.getWidth();
	// height = c.getHeight() + s.getHeight();
	// } else {
	// width = s.getWidth();
	// height = c.getHeight() + s.getHeight();
	// }
	// cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	// Canvas comboImage = new Canvas(cs);
	// comboImage.drawBitmap(c, 0f, 0f, null);
	// comboImage.drawBitmap(s, 0f, c.getHeight(), null);
	// return cs;
	// }

}
