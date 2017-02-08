package com.zrlh.llkc.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalDb;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.contentprovider.ShareData;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.City;
import com.zrlh.llkc.corporate.Obj;
import com.zrlh.llkc.corporate.PlatformAPI;
import com.zrlh.llkc.funciton.Group;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.joggle.Account;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.cache.LocalMemory;
import com.zzl.zl_app.city.CityDB;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.entity.MsgManager;
import com.zzl.zl_app.io.HttpThreadManager;
import com.zzl.zl_app.net_port.Get2ApiImpl;
import com.zzl.zl_app.net_port.IGet2Api;
import com.zzl.zl_app.net_port.WSError;
import com.zzl.zl_app.util.Tools;

public class LLKCApplication extends Application {

	private CityDB cityDB;
	private FinalDb db;

	IGet2Api api;
	private static LLKCApplication mInstance = null;
	public boolean m_bKeyRight = true;
	BMapManager mBMapManager = null;

	public Handler handler = new Handler();

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		init();
		LocalMemory.getInstance().initStoreData(this, R.string.app);
	}

	public InitDataTask task;

	public void initCityData() {
		if (task == null) {
			task = new InitDataTask();
			task.execute();
		}
	}

	public void resumePush() {
		JPushInterface.resumePush(getApplicationContext());
	}

	public void setAlias() {
		JPushInterface.setAlias(getApplicationContext(), "USER_"
				+ LlkcBody.UID_ACCOUNT, callback);
	}

	public void stopJpush() {
		JPushInterface.stopPush(this);
	}

	TagAliasCallback callback = new TagAliasCallback() {

		@Override
		public void gotResult(int arg0, String arg1, Set<String> arg2) {
			Tools.log("JPush", "result:" + arg0 + "-------------alias:" + arg1);
			if (arg0 != 0) {
				setAlias();
			}

		}
	};

	/**
	 * 设置Lables
	 * 
	 * @param list
	 */
	public void setJpushLabels(List<Group> list) {
		if (list == null)
			return;
		Set<String> group_setSet = new HashSet<String>();

		for (int j = 0; j < list.size(); j++) {
			group_setSet.add("GROUP_" + list.get(j).gId);
		}
		Tools.log("Jpush", group_setSet.toString());
		JPushInterface.setAliasAndTags(LLKCApplication.getInstance()
				.getApplicationContext(), "USER_" + LlkcBody.UID_ACCOUNT,
				group_setSet, labelsCallb);
	}

	TagAliasCallback labelsCallb = new TagAliasCallback() {

		@Override
		public void gotResult(int arg0, String arg1, Set<String> arg2) {
			Tools.log("Jpush", arg2.toString());
			if (arg0 != 0)
				JPushInterface.setAliasAndTags(LLKCApplication.getInstance()
						.getApplicationContext(), "USER_"
						+ LlkcBody.UID_ACCOUNT, arg2, labelsCallb);
		}
	};
	static double lng, lat;// 经纬度

	SharedPreferences sharePreference;

	public void init() {
		// 极光初始化
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);

		if (LlkcBody.IMEI == null) {
			TelephonyManager tm = (TelephonyManager) this
					.getSystemService(TELEPHONY_SERVICE);
			LlkcBody.IMEI = tm.getDeviceId();
		}
		initEngineManager(this);
		// 定位
		initLocation();
		startLocation(null);

		if (sharePreference == null)
			sharePreference = this.getSharedPreferences("account",
					Context.MODE_PRIVATE);

		LlkcBody.isNewJpostPrompt = getNewJobPrompt();
		LlkcBody.isFriendMsgPrompt = getFriendMsgPrompt();
		LlkcBody.isFGroupMsgPrompt = getFGroupMsgPrompt();
		LlkcBody.isSysMsgPrompt = getSysMsgPrompt();
		LlkcBody.isNotiByVoice = getNotiSoundPrompt();
		LlkcBody.isNotiByShake = getNotiShakePrompt();
		if (cityDB == null)
			cityDB = new CityDB(this);
		if (db == null)
			db = FinalDb.create(this);
		if (api == null)
			api = new Get2ApiImpl(this);
		if (LlkcBody.APP_Version == null || LlkcBody.APP_Version.equals("")) {
			String version = "";
			try {
				version = String
						.valueOf(this.getPackageManager().getPackageInfo(
								getApplicationInfo().packageName, 0).versionName);
			} catch (NameNotFoundException e) {

			}
			if (version != null && !version.equals(""))
				LlkcBody.APP_Version = version;
		}
		// zzl add
		Community.initHandler();
		MsgManager.createMsgTables(getInstance());
		ImageCache.getInstance().setHandler(handler);
		HttpThreadManager.sharedManager(this);
	}

	public void updateData() {
		if (LlkcBody.ACCOUNT == null)
			LlkcBody.ACCOUNT = LLKCApplication.getInstance().getPersonInfo();
		if (LlkcBody.USER_ACCOUNT == null)
			LlkcBody.USER_ACCOUNT = LLKCApplication.getInstance().getAccount();
		if (LlkcBody.PASS_ACCOUNT == null)
			LlkcBody.PASS_ACCOUNT = LLKCApplication.getInstance().getPwd();
		if (LlkcBody.UID_ACCOUNT == null)
			LlkcBody.UID_ACCOUNT = LLKCApplication.getInstance().getUid();

	}

	/**
	 * @author 检测是否联网
	 */
	public boolean isOpenNetwork() {
		ConnectivityManager connManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo workinfo = connManager.getActiveNetworkInfo();
		if (workinfo != null) {
			return workinfo.isAvailable();
		}
		return false;
	}

	/**
	 * 账户
	 */
	public void setAccount(String account) {
		setSharePreferenceValue("account", account);
	}

	public String getAccount() {
		return getSharePreferenceValue("account", null);
	}

	/**
	 * 密码
	 */
	public void setPwd(String pwd) {
		setSharePreferenceValue("pwd", pwd);
	}

	public String getPwd() {
		return getSharePreferenceValue("pwd", null);
	}

	public void setUid(String uid) {
		setSharePreferenceValue("uid", uid);
	}

	public String getUid() {
		return getSharePreferenceValue("uid", null);
	}

	public void savePersonInfo(String accountJson) {
		setSharePreferenceValue("account_json", accountJson);
	}

	public Account getPersonInfo() {
		String json = getSharePreferenceValue("account_json", null);
		JSONObject obj;
		try {
			obj = getJSONObjectFromString(json);
			if (obj != null)
				return new Account(obj);
		} catch (JSONException e) {
			Tools.log("error", "error:"
					+ (e == null ? "getJSONObjectFromString" : e.getMessage()));
		}
		return null;
	}

	public JSONObject getJSONObjectFromString(String resp) throws JSONException {
		if (resp == null)
			return null;
		resp = resp.trim();

		if (!resp.substring(0, 1).equals("{")) {
			return null;
		}
		Tools.log("IO", "Result : " + resp);
		JSONTokener jsonParser = new JSONTokener(resp);

		Object obj = null;
		do {
			obj = jsonParser.nextValue();
		} while (obj instanceof String && jsonParser.more());
		if (obj instanceof JSONObject) {
			JSONObject jsonObj = (JSONObject) obj;
			if (!jsonObj.isNull("Stat")) {
				String stat = jsonObj.getString("Stat");
				if (stat.equals("-99")) {
					Message msg = handler.obtainMessage();
					msg.obj = Tools.getStringFromRes(this,
							R.string.connect_timeout);
					;
					handler.sendMessage(msg);
					return null;
				}
			}
			return jsonObj;
		}
		return null;
	}

	/** 登陆状态 */
	public void setLoginStat(boolean loginStat) {
		setSharePreferenceValue("login_stat", loginStat);
	}

	public boolean getLoginStat() {
		return getSharePreferenceValue("login_stat", false);
	}

	/** 认证状态 */
	public void setAuthStat(int authStat) {
		setSharePreferenceValue("auth_stat", authStat);
	}

	public String getMsnText() {
		return getSharePreferenceValue("msn_text", "");
	}

	/** 信息文本 */
	public void setMsnText(String msn_text) {
		setSharePreferenceValue("msn_text", msn_text);
	}

	public int getAuthStat() {
		return getSharePreferenceValue("auth_stat", LlkcBody.State_Auth_No);
	}

	/**************** 设置保存的消息提醒 ******************/

	public void setNewJobPrompt(boolean bool) {
		setSharePreferenceValue("prompt_jpost", bool);
	}

	public boolean getNewJobPrompt() {
		return getSharePreferenceValue("prompt_jpost", true);
	}

	public void setFriendMsgPrompt(boolean bool) {
		setSharePreferenceValue("prompt_friend", bool);
	}

	public boolean getFriendMsgPrompt() {
		return getSharePreferenceValue("prompt_friend", true);
	}

	public void setFGroupMsgPrompt(boolean bool) {
		setSharePreferenceValue("prompt_fgroup", bool);
	}

	public boolean getFGroupMsgPrompt() {
		return getSharePreferenceValue("prompt_fgroup", true);
	}

	public void setSysMsgPrompt(boolean bool) {
		setSharePreferenceValue("prompt_sys", bool);
	}

	public boolean getSysMsgPrompt() {
		return getSharePreferenceValue("prompt_sys", true);
	}

	public void setNotiSoundPrompt(boolean bool) {
		setSharePreferenceValue("noti_sound", bool);
	}

	public boolean getNotiSoundPrompt() {
		return getSharePreferenceValue("noti_sound", true);
	}

	public void setNotiShakePrompt(boolean bool) {
		setSharePreferenceValue("noti_shake", bool);
	}

	public boolean getNotiShakePrompt() {
		return getSharePreferenceValue("noti_shake", true);
	}

	public void setTime(String time) {
		setSharePreferenceValue("time", time);
	}

	public String getTime() {
		return getSharePreferenceValue("time", null);
	}

	public void setVersion_JobType(int version_jobtype) {
		setSharePreferenceValue("version_jobtype", version_jobtype);
	}

	public int getVersion_JobType() {
		return getSharePreferenceValue("version_jobtype", 1);
	}

	public void setHaveCheckJpostRecommend(boolean haveCheck) {
		setSharePreferenceValue("haveCheck", haveCheck);
	}

	public boolean isHaveCheckJpostRecommend() {
		return getSharePreferenceValue("haveCheck", false);
	}

	/**
	 * 设置保存切换城市
	 * 
	 * @param city
	 */
	public void setOperCity(String city) {
		setSharePreferenceValue("city", city);
	}

	/**
	 * 获取保存的切换城市
	 * 
	 * @return
	 */
	public String getOperCity() {
		return getSharePreferenceValue("city", null);
	}

	public String getSharePreferenceValue(String key, String defaultValue) {
		return sharePreference.getString(key, defaultValue);
	}

	public int getSharePreferenceValue(String key, int defaultValue) {
		return sharePreference.getInt(key, defaultValue);
	}

	public Boolean getSharePreferenceValue(String key, boolean defaultValue) {
		return sharePreference.getBoolean(key, defaultValue);
	}

	public void setSharePreferenceValue(String key, String value) {
		sharePreference.edit().putString(key, value).commit();
	}

	public void setSharePreferenceValue(String key, boolean value) {
		sharePreference.edit().putBoolean(key, value).commit();
	}

	public void setSharePreferenceValue(String key, int value) {
		sharePreference.edit().putInt(key, value).commit();
	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			try {
				mBMapManager = new BMapManager(context);
			} catch (UnsatisfiedLinkError e) {
				// TODO: handle exception
			}
		}

		if (!mBMapManager.init(PlatformAPI.BAIDU_Key, new MyGeneralListener())) {
			// Toast.makeText(
			// LLKCApplication.getInstance().getApplicationContext(),
			// "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
		}
	}

	public static LLKCApplication getInstance() {
		return mInstance;
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Toast.makeText(
						LLKCApplication.getInstance().getApplicationContext(),
						"您的网络出错啦！", Toast.LENGTH_LONG).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Toast.makeText(
						LLKCApplication.getInstance().getApplicationContext(),
						"输入正确的检索条件！", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				Toast.makeText(
						LLKCApplication.getInstance().getApplicationContext(),
						"请在 DemoApplication.java文件输入正确的授权Key！",
						Toast.LENGTH_LONG).show();
				LLKCApplication.getInstance().m_bKeyRight = false;
			}
		}
	}

	class InitDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			if (ApplicationData.typeLists.size() == 0) {
				Get2ApiImpl get = new Get2ApiImpl(getApplicationContext());
				try {
					ArrayList<HashMap<Obj, ArrayList<Obj>>> lists = get
							.getTypeMapList(Get2ApiImpl.From_Net,
									"http://nan.51zhixun.com/classify.json");
					if (lists != null && lists.size() > 0) {
						ApplicationData.typeLists.clear();
						ApplicationData.typeLists.addAll(lists);
					} else {
						lists = get.getTypeMapList(Get2ApiImpl.From_Assert,
								"classify.json");
						if (lists != null && lists.size() > 0) {
							ApplicationData.typeLists.clear();
							ApplicationData.typeLists.addAll(lists);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WSError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// TODO Auto-generated method stub

			if (ApplicationData.mCityList.size() == 0) {
				try {
					List<City> mCityList = api.getCityList(
							Get2ApiImpl.From_Net, PlatformAPI.PHPBaseUrl
									+ "MsgType=CityControllers");
					if (mCityList != null && mCityList.size() > 0) {
						ApplicationData.mCityList.clear();
						ApplicationData.mCityList.addAll(mCityList);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (WSError e) {
					e.printStackTrace();
				}
				cityDB.saveCity(ApplicationData.mCityList);
			}
			if (ApplicationData.mCityList.size() == 0)
				ApplicationData.mCityList.addAll(cityDB.getAllCity());
			if (ApplicationData.keywords == null) {
				try {
					ApplicationData.keywords = api.getKeyWord(
							Get2ApiImpl.From_Net, PlatformAPI.KeyWord_Url);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WSError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (ApplicationData.mCityList.size() == 0)
				ApplicationData.mCityList.addAll(cityDB.getAllCity());
			task = null;
		}
	}

	private static final String FORMAT = "^[a-z,A-Z].*$";

	public boolean prepareCityList() {
		if (ApplicationData.mCityList.size() == 0)
			ApplicationData.mCityList.addAll(cityDB.getAllCity());// 获取数据库中的城市
		for (City city : ApplicationData.mCityList) {
			String firstName = city.getFirstPY();// 第一个字拼音的第一個字母
			if (firstName != null) {
				if (firstName.matches(FORMAT)) {
					if (ApplicationData.mSections.contains(firstName)) {
						ApplicationData.mMap.get(firstName).add(city);
					} else {
						ApplicationData.mSections.add(firstName);
						List<City> list = new ArrayList<City>();
						list.add(city);
						ApplicationData.mMap.put(firstName, list);
					}
				} else {
					if (ApplicationData.mSections.contains("#")) {
						ApplicationData.mMap.get("#").add(city);
					} else {
						ApplicationData.mSections.add("#");
						List<City> list = new ArrayList<City>();
						list.add(city);
						ApplicationData.mMap.put("#", list);
					}
				}
			}
		}
		Collections.sort(ApplicationData.mSections);// 按照字母重新排序
		ApplicationData.mSections.add(0, "所在城市");
		City city = new City("-1", LlkcBody.CITY_STRING_Current);
		ApplicationData.mCityList.add(0, city);
		List<City> list = new ArrayList<City>();
		list.add(city);
		ApplicationData.mMap.put("所在城市", list);
		int position = 0;
		for (int i = 0; i < ApplicationData.mSections.size(); i++) {
			ApplicationData.mIndexer.put(ApplicationData.mSections.get(i),
					position);// 存入map中，key为首字母字符串，value为首字母在listview中位�?
			ApplicationData.mPositions.add(position);// 首字母在listview中位置，存入list�?
			position += ApplicationData.mMap.get(
					ApplicationData.mSections.get(i)).size();// 计算下一个首字母在listview的位�?
		}

		return true;
	}

	public long getFreeMenorySize() {
		long totleM = Runtime.getRuntime().freeMemory();
		return totleM;
	}

	public void cleanCache() {
		ImageCache.getInstance().clear();
		try {
			FinalBitmap.create(getInstance()).clearMemoryCache();
		} catch (IllegalStateException e) {
			// TODO: handle exception
		} catch (Exception e) {

		}
		this.deleteDatabase("webview.db");
		this.deleteDatabase("webviewCache.db");
	}

	public void insertUser(Account account) {
		if (account == null)
			return;
		// getContentResolver().delete(ShareData.User.CONTENT_URI,
		// ShareData.User._ID + ">?", new String[] { "'0'" });
		getContentResolver().delete(ShareData.User.CONTENT_URI, null, null);
		ContentValues values = new ContentValues();
		values.put(ShareData.User.USER_ACCOUNT, account.getArccount());
		values.put(ShareData.User.USER_PWD, account.getPrassWord());
		values.put(ShareData.User.USER_NAME, account.getUname());
		values.put(ShareData.User.USER_ID, account.getUid());
		values.put(ShareData.User.USER_SEX, account.getSex());
		values.put(ShareData.User.USER_HEAD, account.getHead());
		values.put(ShareData.User.USER_SCORE, account.getScore());
		getContentResolver().insert(ShareData.User.CONTENT_URI, values);
	}

	/**
	 * 初始化
	 */
	public void initLocation() {
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		locationParam();
	}

	void locationParam() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPoiExtraInfo(true);
		option.setAddrType("all");
		option.setPriority(LocationClientOption.MIN_SCAN_SPAN);
		option.disableCache(true);
		option.setScanSpan(1000 * 100);
		mLocClient.setLocOption(option);
	}

	/**
	 * 单次定位
	 * 
	 * @param callback
	 */
	public void singleLocation(LocationResultCallback callback) {
		this.locationCallback = callback;
		openLocationCallBack();
		if (mLocClient.isStarted())
			mLocClient.requestLocation();
	}

	/**
	 * 开始定位
	 * 
	 * @param callback
	 */
	public void startLocation(LocationResultCallback callback) {
		this.locationCallback = callback;
		openLocationCallBack();
		mLocClient.start();
	}

	public void stopLocation() {
		doLocationCallback = false;
		mLocClient.stop();
	}

	public void closeLocationCallBack() {
		locationCallback = null;
		doLocationCallback = false;
	}

	public void openLocationCallBack() {
		doLocationCallback = true;
	}

	private boolean doLocationCallback = false;

	private LocationClient mLocClient = null;// 定位的
	public MyLocationListenner myListener = new MyLocationListenner();// 定位监听

	private int time = 0;

	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			if (location.getCity() == null)
				return;
			LlkcBody.LAT = location.getLatitude();
			LlkcBody.LNG = location.getLongitude();
			if (getUid() != null
					&& !"".equals(getUid())
					&& (time == 0 || LlkcBody.CITY_STRING_Current == null || !LlkcBody.CITY_STRING_Current
							.equals(location.getCity()))) {
				new PostLocationInfoTask(String.valueOf(LlkcBody.LAT),
						String.valueOf(LlkcBody.LNG)).execute();
				time++;
			}
			LlkcBody.CITY_STRING = location.getCity();
			LlkcBody.CITY_STRING_Current = location.getCity();
			Tools.log("LLKC_Location", "location:" + location.getCity());
			String city = getOperCity();
			if (city != null) {
				LlkcBody.CITY_STRING = city;
			}
			if (locationCallback != null && doLocationCallback)
				locationCallback.getLocationResult(location,
						location.getCity(), location.getDistrict(),
						location.getLatitude(), location.getLongitude());

		}

		@Override
		public void onReceivePoi(BDLocation arg0) {

		}
	}

	private LocationResultCallback locationCallback;

	/**
	 * 定位结果
	 * 
	 * 
	 * @author 子龍
	 * 
	 */
	public interface LocationResultCallback {
		/**
		 * 
		 * @param location
		 * @param city
		 *            定位城市
		 * @param district
		 *            定位区县
		 * @param latitude
		 *            纬度
		 * @param longitude
		 *            经度
		 */
		void getLocationResult(BDLocation location, String city,
				String district, double latitude, double longitude);
	}

	class PostLocationInfoTask extends AsyncTask<Void, Integer, Boolean> {
		String lat, longs;

		public PostLocationInfoTask(String lat, String longs) {
			this.lat = lat;
			this.longs = longs;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				return Community.getInstance(getApplicationContext())
						.postLocationInfo(getUid(),
								LlkcBody.CITY_STRING_Current, lat, longs);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			super.onPostExecute(result);
		}
	}

}