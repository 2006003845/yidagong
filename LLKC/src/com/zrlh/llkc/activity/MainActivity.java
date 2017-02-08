package com.zrlh.llkc.activity;

import java.util.List;

import net.tsz.afinal.FinalDb;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.Resume;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.DownloadService;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.joggle.Account;
import com.zrlh.llkc.ui.CustomLoadingDialog;
import com.zrlh.llkc.ui.LLKCApplication;
import com.zrlh.llkc.ui.LoginActivity;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	private TabHost tabHost;
	FinalDb db;

	public static int androidVersion = 0;

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		c.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		androidVersion = Tools.getSDKVersionNumber();
		mInstance = this;
		db = FinalDb.create(this, true);
		init();
		initView();
	}

	@Override
	protected void onStart() {
		new CheckVersionUpdateTask().execute();
		super.onStart();
	}

	public static MainActivity mInstance;
	private RadioGroup radioGroup;
	public ImageView not;

	public void setNotVisible(boolean visibleable) {
		if (not == null)
			return;
		if (visibleable)
			not.setVisibility(View.VISIBLE);
		else
			not.setVisibility(View.GONE);
	}

	public void initView() {
		tabHost = this.getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		not = (ImageView) this.findViewById(R.id.main_tab_new_message);
		intent = new Intent().setClass(this, HomeActivity.class);
		spec = tabHost.newTabSpec(HomeActivity.TAG)
				.setIndicator(HomeActivity.TAG).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, MsgCenterActivity.class);
		spec = tabHost.newTabSpec(MsgCenterActivity.TAG)
				.setIndicator(MsgCenterActivity.TAG).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, ContactsActivity.class);
		spec = tabHost.newTabSpec(ContactsActivity.TAG)
				.setIndicator(ContactsActivity.TAG).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, SquareActivity.class);
		spec = tabHost.newTabSpec(SquareActivity.TAG)
				.setIndicator(SquareActivity.TAG).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, PersonalCenterActivity.class);
		spec = tabHost.newTabSpec(PersonalCenterActivity.TAG)
				.setIndicator(PersonalCenterActivity.TAG).setContent(intent);
		tabHost.addTab(spec);
		tabHost.setCurrentTab(0);

		radioGroup = (RadioGroup) this.findViewById(R.id.main_tab_group);
		findViewById(R.id.main_tab_home).setOnTouchListener(
				new DoubleOnTouchListener());
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.main_tab_home:// 首页
					tabHost.setCurrentTabByTag(HomeActivity.TAG);
					break;
				case R.id.main_tab_msg:// 消息中心
					setNotVisible(false);
					tabHost.setCurrentTabByTag(MsgCenterActivity.TAG);
					break;
				case R.id.main_tab_contacts:
					tabHost.setCurrentTabByTag(ContactsActivity.TAG);
					break;
				case R.id.main_tab_fgroup:// 发现
					tabHost.setCurrentTabByTag(SquareActivity.TAG);
					break;
				case R.id.main_tab_pcenter:// 个人中心
					if (!LlkcBody.isLogin()) {
						LoginActivity.launch(MainActivity.this, getIntent());
						setCurrentTab(0);
						RadioButton b = (RadioButton) group.getChildAt(0);
						b.setChecked(true);
					} else
						tabHost.setCurrentTabByTag(PersonalCenterActivity.TAG);
					break;
				}
			}
		});
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		Tools.log("LLKC_Live", "MainActivity:onResume");
		super.onResume();
		LLKCApplication.getInstance().init();
		LLKCApplication.getInstance().updateData();

	}

	/**
	 * @author 检测是否联网
	 */
	private boolean isOpenNetwork() {
		ConnectivityManager connManager = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo workinfo = connManager.getActiveNetworkInfo();
		if (workinfo != null) {
			return workinfo.isAvailable();
		}
		return false;
	}

	class DoubleOnTouchListener implements OnTouchListener {

		// 计算点击的次数
		private int count;
		// 第一次点击的时间 long型
		private long firstClick;
		// 最后一次点击的时间
		private long lastClick;
		// 第一次点击的button的id
		private int firstId;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// 如果第二次点击 距离第一次点击时间过长 那么将第二次点击看为第一次点击
				if (firstClick != 0 && firstId != 0
						&& System.currentTimeMillis() - firstClick > 300) {
					count = 0;
					firstId = 0;
				}
				count++;
				if (count == 1) {
					firstClick = System.currentTimeMillis();
					// 记录第一次点得按钮的id
					firstId = v.getId();
				} else if (count == 2) {
					lastClick = System.currentTimeMillis();
					// 两次点击小于300ms 也就是连续点击
					if (lastClick - firstClick < 300) {
						// 第二次点击的button的id
						int id = v.getId();
						// 判断两次点击的button是否是同一个button
						if (id == firstId) {
							switch (id) {
							case R.id.main_tab_home:// 首页
								if (isOpenNetwork()) {
									if (HomeActivity.mInstance != null) {
										HomeActivity.mInstance.downLoadNum = 0;
										if (ApplicationData.imgItemList.size() == 0) {
											HomeActivity.mInstance.downLoadNum++;
											HomeActivity.mInstance.new InitDataTask()
													.execute();
										}
										if (ApplicationData.recommendTypeList
												.size() == 0) {
											HomeActivity.mInstance.downLoadNum++;
											HomeActivity.mInstance.new HotJobTask(
													LlkcBody.CITY_STRING)
													.execute();
										}
										if (!LlkcBody.isLogin()
												&& HomeActivity.mInstance.downLoadNum > 0) {
											HomeActivity.mInstance.downLoadNum++;
											new LogintPlatformTask().execute();
										}
										if (HomeActivity.mInstance.downLoadNum > 0) {
											HomeActivity.mInstance.progressDialog = new CustomLoadingDialog(
													HomeActivity.mInstance);
											HomeActivity.mInstance.progressDialog
													.show();
										}
									}
								} else {
									MyToast.getToast().showToast(
											getApplicationContext(),
											"请先检查网络配置，在尝试刷新吧！", 50);
								}
								break;
							}
						}
					}
					clear();
				}
			}
			return false;
		}

		// 清空状态
		private void clear() {
			count = 0;
			firstClick = 0;
			lastClick = 0;
			firstId = 0;
		}
	}

	class LogintPlatformTask extends AsyncTask<Object, Integer, Boolean> {
		int Stat;

		@Override
		protected Boolean doInBackground(Object... params) {
			try {
				boolean bool = login();
				// if (bool) {
				// LlkcBody.ACCOUNT = Community.getInstance(
				// getApplicationContext()).queryUserInfo(
				// LlkcBody.UID_ACCOUNT);

				return bool;
				// }
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				LLKCApplication.getInstance().resumePush();
				LLKCApplication.getInstance().setAlias();
				LLKCApplication.getInstance().insertUser(LlkcBody.ACCOUNT);
				PersonalCenterActivity act = (PersonalCenterActivity) HomeActivity.mInstance
						.getAct(PersonalCenterActivity.TAG);
				if (act != null)
					act.initDyanmic();
				new LoadDataTask().execute();
			} /*
			 * else { MyToast.getToast().showToast(MainActivity.this,
			 * "对不起,您登陆失败！请重新登陆"); }
			 */
			HomeActivity.mInstance.downLoadNum--;
			if (HomeActivity.mInstance.downLoadNum <= 0) {
				if (HomeActivity.mInstance.progressDialog != null
						&& HomeActivity.mInstance.progressDialog.isShowing()) {
					HomeActivity.mInstance.progressDialog.dismiss();
				}
			}
			super.onPostExecute(result);

		}
	}

	/*
	 * 登录，判断数size
	 */
	public boolean login() throws JSONException {
		List<Account> assconts = db.findAll(Account.class);
		if (assconts != null && assconts.size() > 0) {
			Account asscont_obj = assconts.get(assconts.size() - 1);
			if (asscont_obj != null && asscont_obj.getArccount() != null) {
				LlkcBody.USER_ACCOUNT = asscont_obj.getArccount();
				LlkcBody.PASS_ACCOUNT = asscont_obj.getPrassWord();
				return Community.getInstance(this).login(LlkcBody.USER_ACCOUNT,
						LlkcBody.PASS_ACCOUNT);
			}
		}
		return false;
	}

	class LoadDataTask extends AsyncTask<Object, Integer, Boolean> {

		public LoadDataTask() {

		}

		@Override
		protected Boolean doInBackground(Object... params) {

			try {
				Community.getInstance(getApplicationContext()).getMsgList(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT, "0",
						getApplicationContext());
				List<Resume> list = Community.getInstance(
						getApplicationContext()).getResumeList(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT);
				if (list != null)
					ApplicationData.resumeList.addAll(list);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// DemoApplication.getInstance().initCityData();
			super.onPostExecute(result);
		}
	}

	private ServiceConnection conn;
	private DownloadService.DownloadBinder binder;

	/**
	 * 系统当前的分辨率的宽
	 */
	public static double CURRENT_SCREEN_WIDTH;
	/**
	 * 系统当前的分辨率的高
	 */
	public static double CURRENT_SCREEN_HEIGHT;
	/**
	 * 当前屏幕密度
	 */
	public static double CURRENT_DENSITY;

	public static double Default_SCREEN_WIDTH = 480;

	public static double Default_SCREEN_HEIGHT = 800;

	/**
	 * 屏幕宽度比例
	 */
	public static double WIDTH_RATIO;
	/**
	 * 屏幕高度比例
	 */
	public static double HEIGHT_RATIO;

	public void init() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		CURRENT_SCREEN_WIDTH = displayMetrics.widthPixels;
		CURRENT_SCREEN_HEIGHT = displayMetrics.heightPixels;
		CURRENT_DENSITY = displayMetrics.densityDpi;
		// 计算宽高比率
		WIDTH_RATIO = CURRENT_SCREEN_WIDTH / Default_SCREEN_WIDTH;
		HEIGHT_RATIO = CURRENT_SCREEN_HEIGHT / Default_SCREEN_HEIGHT;

		registerMessageReceiver();
		conn = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				binder = (DownloadService.DownloadBinder) service;
				binder.start();
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
		};
	}

	public void setCurrentTab(int index) {
		if (index < tabHost.getChildCount()) {
			tabHost.setCurrentTab(index);
			RadioButton b = (RadioButton) radioGroup.getChildAt(index);
			if (b != null)
				b.setChecked(true);
		}
	}

	@Override
	protected void onDestroy() {
		mInstance = null;
		unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	void updateSer(String updateURL) {
		Intent intent = new Intent(this, DownloadService.class);
		intent.putExtra("updateURL", updateURL);
		intent.putExtra("name", "llkc");
		startService(intent); // 如果先调用startService,则在多个服务绑定对象调用unbindService后服务仍不会被销毁
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}

	public void showMsgDialog(final Context context, String version,
			String content) {
		View layout = getLayoutInflater().inflate(
				R.layout.layout_version_update, null);
		final AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		dialog.getWindow().setLayout((int) MainActivity.CURRENT_SCREEN_WIDTH,
				(int) MainActivity.CURRENT_SCREEN_HEIGHT);
		dialog.getWindow().setContentView(layout);
		dialog.setCanceledOnTouchOutside(true);
		TextView versionTV = (TextView) dialog
				.findViewById(R.id.version_update_tv_ver);
		versionTV.setText(Tools.getStringFromRes(context, R.string.newest_ver)
				+ version);
		TextView contTV = (TextView) dialog
				.findViewById(R.id.version_update_tv_cont);
		contTV.setText(content);
		dialog.findViewById(R.id.version_update_btn_ok).setOnClickListener(
				new android.view.View.OnClickListener() {

					@Override
					public void onClick(View v) {
						updateSer(Url);
						dialog.dismiss();
						MobclickAgent.onEvent(context, "event_versionupdate");
					}
				});
		contTV.setText(content);
		dialog.findViewById(R.id.version_update_btn_cancel).setOnClickListener(
				new android.view.View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
	}

	class CheckVersionUpdateTask extends AsyncTask<Object, Integer, JSONObject> {

		public CheckVersionUpdateTask() {
		}

		@Override
		protected JSONObject doInBackground(Object... params) {

			try {
				return Community.getInstance(getApplicationContext())
						.checkVersioinUpdate();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject obj) {
			if (obj == null) {
				// MyToast.getToast().showToast(
				// MainActivity.this,
				// Tools.getStringFromRes(MainActivity.this,
				// R.string.net_error2));
				return;
			}
			try {
				Type = obj.getString("Type");
				Version = obj.getString("Version");
				LlkcBody.APP_DOWN = Url = obj.getString("Url");
				Desc = obj.getString("Desc");
				if (!obj.isNull("Msg")) {
					String msn = obj.getString("Msg");
					LLKCApplication.getInstance().setMsnText(msn);
				}
				if (!obj.isNull("WorkVersion")) {
					try {
						int ver_new = Integer.parseInt(obj
								.getString("WorkVersion"));
						int ver_old = LLKCApplication.getInstance()
								.getVersion_JobType();
						if (ver_new > ver_old) {
							// 下载工种
							LLKCApplication.getInstance().setVersion_JobType(
									ver_new);

						}
					} catch (NumberFormatException e) {
						LlkcBody.Version_JobType = LLKCApplication
								.getInstance().getVersion_JobType();
					}
				}
				if (Type.equals("0")) {
					// MyToast.getToast().showToast(context,
					// R.string.version_update_text_newest);
				} else {
					showMsgDialog(MainActivity.this, Version, Desc);
				}
			} catch (JSONException e) {
				// MyToast.getToast().showToast(context,
				// R.string.version_update_text_newest);
			}

			super.onPostExecute(obj);
		}
	}

	String Type, Version, Url, Desc;

	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.zzl.app.main.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_TYPE = "type";
	public static final String KEY_EXTRAS = "extras";

	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				int type = intent.getIntExtra(KEY_TYPE, -1);
				setNotVisible(true);
				switch (type) {
				case 5:

					break;
				case 6:
					// 登出
					// 退出
					// FinalDb.create(context,
					// true).deleteByWhere(Account.class,
					// "1=1");
					// ApplicationData.clearUserData();
					// LlkcBody.clear();
					// AppManager.getAppManager().AppExit2(MainActivity.this);
					// LoginActivity.launch(MainActivity.this, null);
					// LLKCApplication.getInstance().stopJpush();
					break;
				case 9:

					break;
				case 11:

					break;

				default:
					break;
				}

			}
		}
	}

}
