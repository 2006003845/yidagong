package com.zrlh.llkc.activity;

import java.util.ArrayList;
import java.util.HashMap;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.Obj;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.DownloadService;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.AdviceFeedBackActivity;
import com.zrlh.llkc.ui.LLKCApplication;
import com.zrlh.llkc.ui.Llkc_MsgReceiver;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.db.DBHelper;
import com.zzl.zl_app.net_port.Get2ApiImpl;
import com.zzl.zl_app.net_port.WSError;
import com.zzl.zl_app.util.Tools;

public class SettingActivity extends BaseActivity {
	public static final String TAG = "setting";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, SettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.setting);
		init();
		initView();

	}

	String Type, Version, Url, Desc;

	private ServiceConnection conn;
	private DownloadService.DownloadBinder binder;
	ProgressDialog dialog = null;

	public void init() {
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

	private TextView titleTV;
	private TextView versionTV;

	public void initView() {
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText(R.string.set);
		versionTV = (TextView) this.findViewById(R.id.setting_version_tv);
		versionTV.setText("(当前版本号：V" + LlkcBody.APP_Version + ")");
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		// 推荐给朋友
		this.findViewById(R.id.setting_relayout_postjob).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO
						sendSMS(LlkcBody.getMsnText(), "");
						MobclickAgent.onEvent(getContext(),
								"event_recommend_friend");
					}
				});
		// 消息设置
		this.findViewById(R.id.setting_relayout_msgset).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO
						MsgSettingActivity.launch(getContext(), null);
						MobclickAgent.onEvent(getContext(), "event_msgsetting");
					}
				});
		// 清除缓存
		this.findViewById(R.id.setting_relayout_clearcache).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO
						long old_ms = LLKCApplication.getInstance()
								.getFreeMenorySize();
						LLKCApplication.getInstance().cleanCache();
						long now_ms = LLKCApplication.getInstance()
								.getFreeMenorySize();
						long clearSize = now_ms - old_ms;
						MyToast.getToast().showToast(getContext(),
								"清除" + clearSize / 1000 + "k");
					}
				});
		// 版本更新
		this.findViewById(R.id.setting_relayout_verupdate).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 检测版本更新
						new CheckVersionUpdateTask().execute();

					}
				});
		// 关于
		this.findViewById(R.id.setting_relayout_about).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						AboutActivity.launch(getContext(), getContext()
								.getIntent());
					}
				});
		// 意见反馈
		this.findViewById(R.id.setting_relayout_feedback).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getContext(),
								AdviceFeedBackActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						startActivity(intent);
					}
				});

		this.findViewById(R.id.setting_relayout_exit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO exit
						showMsgDialog(getContext());
					}
				});
	}

	public void clear() {
		ApplicationData.clear();
		ImageCache.getInstance().clear();
		FinalBitmap.create(this).clearCache();
	}

	public void showMsgDialog(Context context) {
		View layout = getLayoutInflater().inflate(R.layout.dialog_exit, null);
		final AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		dialog.getWindow().setLayout((int) MainActivity.CURRENT_SCREEN_WIDTH,
				(int) MainActivity.CURRENT_SCREEN_HEIGHT);
		dialog.getWindow().setContentView(layout);
		dialog.setCanceledOnTouchOutside(true);
		dialog.findViewById(R.id.dialog_exit_btn_ok).setOnClickListener(
				new android.view.View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent service = new Intent(getContext(),
								DownloadService.class);
						stopService(service);
						Intent service2 = new Intent(getContext(),
								LLKCService.class);
						stopService(service2);
						finishAllActs();
						clear();
						DBHelper.getHallDBInstance(getContext()).closeDB();
						Llkc_MsgReceiver.TaskManager.getInstance(getContext())
								.stopThread();
						LLKCApplication.getInstance().stopJpush();
						dialog.dismiss();

					}
				});

		dialog.findViewById(R.id.dialog_exit_btn_cancel).setOnClickListener(
				new android.view.View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
	}

	void updateVersion(String updateURL) {
		Intent intent = new Intent(getContext(), DownloadService.class);
		intent.putExtra("updateURL", updateURL);
		intent.putExtra("name", "llkc");
		startService(intent); // 如果先调用startService,则在多个服务绑定对象调用unbindService后服务仍不会被销毁
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}

	@Override
	public BaseActivity getContext() {
		return this;
	}

	@Override
	public void setDialogContent(AlertDialog dialog, int layoutId,
			String content, String id) {
		switch (layoutId) {
		case R.layout.layout_version_update: {
			TextView contTV = (TextView) dialog
					.findViewById(R.id.version_update_tv_cont);
			contTV.setText(content);
			TextView versionTV = (TextView) dialog
					.findViewById(R.id.version_update_tv_ver);
			versionTV.setText(Tools.getStringFromRes(getContext(),
					R.string.newest_ver) + Version);
			break;
		}

		default:
			break;
		}
	}

	@Override
	public void setDialogTitle(AlertDialog dialog, int layoutId, String title,
			String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public OnClickListener setPositiveClickListener(final AlertDialog dialog,
			int layoutId, String id) {
		switch (layoutId) {
		case R.layout.layout_version_update:
			dialog.findViewById(R.id.version_update_btn_ok).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							updateVersion(Url);
							dialog.dismiss();
							MobclickAgent.onEvent(getContext(),
									"event_versionupdate");
						}
					});
			break;
		}
		return null;
	}

	@Override
	public OnClickListener setNegativeClickListener(final AlertDialog dialog,
			int layoutId, String id) {
		dialog.findViewById(R.id.version_update_btn_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
		return null;
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(TAG);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(TAG);
	}

	class CheckVersionUpdateTask extends AsyncTask<Object, Integer, JSONObject> {

		public CheckVersionUpdateTask() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.test_version));
			dialog.show();
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
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (obj == null) {
				MyToast.getToast().showToast(
						getContext(),
						Tools.getStringFromRes(getContext(),
								R.string.net_error2));
				return;
			}
			try {
				Type = obj.getString("Type");
				Version = obj.getString("Version");
				Url = obj.getString("Url");
				Desc = obj.getString("Desc");
				try {
					int ver_new = Integer
							.parseInt(obj.getString("WorkVersion"));
					int ver_old = LLKCApplication.getInstance()
							.getVersion_JobType();
					if (ver_new > ver_old) {
						// 下载工种
						LLKCApplication.getInstance().setVersion_JobType(
								ver_new);

					}
				} catch (NumberFormatException e) {
					LlkcBody.Version_JobType = LLKCApplication.getInstance()
							.getVersion_JobType();
				}
				if (!obj.isNull("Msg")) {
					String msn = obj.getString("Msg");
					LLKCApplication.getInstance().setMsnText(msn);
				}
				if (Type.equals("0")) {
					MyToast.getToast().showToast(getContext(),
							R.string.version_update_text_newest);
				} else {
					showMsgDialog("", R.layout.layout_version_update,
							getContext(), "", Desc);
				}
			} catch (JSONException e) {
				MyToast.getToast().showToast(getContext(),
						R.string.version_update_text_newest);
			}

			super.onPostExecute(obj);
		}
	}

	/**
	 * 分享应用给朋友
	 * 
	 * @param smsBody
	 * @param phone
	 */
	private void sendSMS(String smsBody, String phone) {

		Uri smsToUri = Uri.parse("smsto:" + phone);

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SENDTO);
		intent.setData(smsToUri);
		intent.putExtra("sms_body", smsBody);
		startActivity(intent);

	}

	@Override
	public void onBackPressed() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		closeOneAct(TAG);
		super.onBackPressed();
	}

	class UpdatePostTypeTask extends
			AsyncTask<Object, Integer, ArrayList<HashMap<Obj, ArrayList<Obj>>>> {

		public UpdatePostTypeTask() {
		}

		@Override
		protected ArrayList<HashMap<Obj, ArrayList<Obj>>> doInBackground(
				Object... params) {

			try {
				return new Get2ApiImpl(getContext()).getTypeMapList(
						Get2ApiImpl.From_Net,
						"http://nan.51zhixun.com/classify.json");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WSError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<Obj, ArrayList<Obj>>> obj) {

			super.onPostExecute(obj);
		}
	}
}
