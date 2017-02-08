package com.zrlh.llkc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.FinalDb;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.LinearLayout;
import cn.jpush.android.api.JPushInterface;

import com.baidu.location.BDLocation;
import com.baidu.location.BDNotifyListener;
import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.activity.MainActivity;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.Obj;
import com.zrlh.llkc.corporate.Resume;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.joggle.Account;
import com.zrlh.llkc.ui.LLKCApplication;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.entity.ImageItem;
import com.zzl.zl_app.util.MyCountDownTimer;
import com.zzl.zl_app.util.TimeUtil;
import com.zzl.zl_app.util.Tools;

/*1.定位(保存定位)
 * 2.经纬度
 * 3.振动器
 * 4.获取手机号
 * 5.设置网络
 * 6.登录，判断数size
 * 7.用到AsyncTask（doInBackgroud,OnPostExecute）
 * 
 * */

public class AppStart extends Activity {

	public Vibrator mVibrator01;// 振动器

	FinalDb db;
	LLKCApplication app;
	LinearLayout pbLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		app = LLKCApplication.getInstance();
		init();
		setContentView(R.layout.appstart);
		pbLayout = (LinearLayout) this.findViewById(R.id.app_start_layout_pb);
		pbLayout.setVisibility(View.GONE);
		// 打开网络
		if (isOpenNetwork()) {
			LLKCApplication.getInstance().initCityData();
			new LogintPlatformTask().execute();
			new InitDataTask().execute();
			timer.start();
		} else {
			startvoin();
		}
		updateTime();
	}

	public void updateTime() {
		Date date = new Date();
		String now = TimeUtil.getTimeStr2(date, "yyyy-MM-dd");
		String last = app.getTime();
		if (last == null || last.equals("")) {
			app.setTime(now);
			LlkcBody.Time = now;
			LlkcBody.haveCheckJpostRecommend = false;
			app.setHaveCheckJpostRecommend(false);
		} else {
			if (needUpdateTime(last, now)) {
				LlkcBody.Time = now;
				app.setTime(now);
				LlkcBody.haveCheckJpostRecommend = false;
				app.setHaveCheckJpostRecommend(false);
			} else {
				LlkcBody.haveCheckJpostRecommend = app
						.isHaveCheckJpostRecommend();
			}
		}
	}

	public boolean needUpdateTime(String lastDate, String nowDate) {
		String[] nows = nowDate.split("-");
		String[] lasts = lastDate.split("-");
		if (nows.length == 3 && lasts.length == 3) {
			int nowYear = Integer.parseInt(nows[0]);
			int nowMonth = Integer.parseInt(nows[1]);
			int nowDay = Integer.parseInt(nows[2]);
			int lastYear = Integer.parseInt(lasts[0]);
			int lastMonth = Integer.parseInt(lasts[1]);
			int lastDay = Integer.parseInt(lasts[2]);
			if (nowYear > lastYear)
				return true;
			else if (nowYear == lastYear) {
				if (nowMonth > lastMonth)
					return true;
				else if (nowMonth == lastMonth) {
					if (nowDay > lastDay)
						return true;
				}
			}
		} else {
			return false;
		}
		return false;
	}

	private void init() {
		TelephonyManager tm = (TelephonyManager) AppStart.this
				.getSystemService(TELEPHONY_SERVICE);
		LlkcBody.IMEI = tm.getDeviceId();// 从设备上获取手机LlkcBody的IMEI号。
		db = FinalDb.create(this, true);
		ImageCache.getInstance().setHandler(new Handler());

		MyToast.initToast(getLayoutInflater(), R.layout.toast, R.id.toast_tv);

		String city = LLKCApplication.getInstance().getOperCity();
		if (city != null) {
			LlkcBody.CITY_STRING = city;
			// LlkcBody.CITY_STRING_Current = city;
		}

	}

	private boolean isOpenNetwork() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	/*
	 * 登录，判断数size
	 */
	public boolean login() throws JSONException {
		List<Account> assconts = db.findAll(Account.class);
		if (assconts != null && assconts.size() > 0) {
			Account asscont_obj = assconts.get(assconts.size() - 1);
			if (asscont_obj != null) {
				LlkcBody.USER_ACCOUNT = asscont_obj.getArccount();
				LlkcBody.PASS_ACCOUNT = asscont_obj.getPrassWord();
				return Community.getInstance(this).login(LlkcBody.USER_ACCOUNT,
						LlkcBody.PASS_ACCOUNT);
			}
		}
		return false;
	}

	void startvoin() {
		pbLayout.setVisibility(View.VISIBLE);
		Timer timer = new Timer();
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				MainActivity.launch(getApplicationContext(), getIntent());
				finish();
			}
		};
		timer.schedule(timerTask, 1000 * 3);

	}

	class InitDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			ArrayList<ImageItem> imglist = initImgItem();
			if (imglist != null)
				ApplicationData.imgItemList.addAll(imglist);

			if (ApplicationData.recommendTypeList.size() == 0) {
				try {
					List<Obj> list = Community.getInstance(
							getApplicationContext()).getHotCommendType(
							LlkcBody.CITY_STRING);
					if (list != null) {
						ApplicationData.recommendTypeList.addAll(list);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			MainActivity.launch(getApplicationContext(), getIntent());
			finish();
		}
	}

	public ArrayList<ImageItem> initImgItem() {
		ArrayList<ImageItem> list = null;
		try {
			list = Community.getInstance(this).getImgItemList(
					LlkcBody.CITY_STRING);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public class NotifyLister extends BDNotifyListener {
		public void onNotify(BDLocation mlocation, float distance) {
			mVibrator01.vibrate(1000);
		}
	}

	class LogintPlatformTask extends AsyncTask<Object, Integer, Boolean> {
		Bundle bundle;
		int Stat;

		public LogintPlatformTask() {
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			boolean netStat = Tools.checkNetWorkStatus(AppStart.this);
			if (!netStat) {
				return false;
			}
			try {
				boolean bool = login();
				return bool;
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
				new LoadPersonalDataTask().execute();
			} else {
				// 获取手机IMEI号
				JPushInterface.setAlias(AppStart.this, LlkcBody.IMEI, null);// 设置用户别名
			}
			super.onPostExecute(result);

		}
	}

	class LoadPersonalDataTask extends AsyncTask<Object, Integer, Boolean> {

		public LoadPersonalDataTask() {

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
				e.printStackTrace();
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			super.onPostExecute(result);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	MyCountDownTimer timer = new MyCountDownTimer(1500, 1000) {

		@Override
		public void onTick(long millisUntilFinished, int percent) {
		}

		@Override
		public void onFinish() {
			pbLayout.setVisibility(View.VISIBLE);
		}
	};
}
