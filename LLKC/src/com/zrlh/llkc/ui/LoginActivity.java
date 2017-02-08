package com.zrlh.llkc.ui;

import java.util.List;

import net.tsz.afinal.FinalDb;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.activity.MsgCenterActivity;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.Resume;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.joggle.Account;
import com.zrlh.llkc.joggle.User;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class LoginActivity extends BaseActivity {
	public static final String TAG = "login";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	ImageButton seachImageView;
	Button reg_button, login_button;
	TextView titleTextView, find_pass;
	EditText login_name, login_pass;
	String Msg, Stat;
	FinalDb db;
	CheckBox checkBox;
	String statesString; // 1 选中 2未选中

	ProgressDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.login_activity);

		db = FinalDb.create(this, true);

		init();
	}

	void init() {
		seachImageView = (ImageButton) findViewById(R.id.friends);
		titleTextView = (TextView) findViewById(R.id.title_card);
		reg_button = (Button) findViewById(R.id.reg_button);
		login_button = (Button) findViewById(R.id.login_button);
		checkBox = (CheckBox) findViewById(R.id.checkBox1);
		login_name = (EditText) findViewById(R.id.login_name);
		login_pass = (EditText) findViewById(R.id.login_pass);
		find_pass = (TextView) findViewById(R.id.find_pass);
		checkBox.setChecked(true);
		statesString = "1";

		find_pass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LoginActivity.this,
						FindPasswordActivity.class));
			}
		});
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				statesString = arg1 ? "1" : "0";

			}
		});
		login_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String nameString = login_name.getText().toString().trim();
				String passString = login_pass.getText().toString().trim();
				if ("".equals(nameString)) {
					Toast.makeText(
							getApplicationContext(),
							Tools.getStringFromRes(getApplicationContext(),
									R.string.name_nonull), Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if ("".equals(passString)) {
					Toast.makeText(
							getApplicationContext(),
							Tools.getStringFromRes(getApplicationContext(),
									R.string.pas_nonull), Toast.LENGTH_SHORT)
							.show();
					return;
				}
				new LogintPlatformTask(nameString, passString).execute();
			}
		});
		titleTextView.setText(Tools.getStringFromRes(getApplicationContext(),
				R.string.login_button));
		seachImageView.setVisibility(View.INVISIBLE);
		reg_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LoginActivity.this,
						RegisterActivity.class));
				finish();
			}
		});

		findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
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

	@Override
	public void onBackPressed() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		closeOneAct(TAG);
		super.onBackPressed();
	}

	public void saveAccount(String name, String pwd, String uid, String uname) {
		User newUser = new User();
		newUser.setArccount(name);
		newUser.setPrassWord(pwd);
		newUser.setLoginStatus(statesString);
		newUser.setUid(uid);
		db.deleteByWhere(User.class, "1=1");
		db.deleteByWhere(Account.class, "1=1");
		db.save(newUser);
		Account account = null;
		if (LlkcBody.ACCOUNT == null) {
			account = new Account();
			account.setArccount(name);
			account.setPrassWord(pwd);
			account.setUid(uid);
			account.setLoginStatus("1");
			account.setBirth("");
			account.setCert("");
			account.setHead("");
			account.setLocal("");
			account.setEmail("");
			account.setPhone("");
			account.setProf("");
			account.setSex("");
			account.setSign("");
			account.setUname(uname);
		} else
			account = LlkcBody.ACCOUNT;
		db.save(account);
	}

	class LogintPlatformTask extends AsyncTask<Object, Integer, Boolean> {
		Bundle bundle;
		int Stat;
		String name;
		String pwd;

		public LogintPlatformTask(String name, String pwd) {
			this.name = name;
			this.pwd = pwd;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(LoginActivity.this);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getApplicationContext(),
					R.string.logining));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {

			boolean netStat = Tools.checkNetWorkStatus(LoginActivity.this);
			if (!netStat) {
				return false;
			}
			try {
				boolean bool = login(name, pwd);
				if (bool) {
					// LlkcBody.ACCOUNT = Community.getInstance(
					// getApplicationContext()).queryUserInfo(
					// LlkcBody.UID_ACCOUNT);
					saveAccount(LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT,
							LlkcBody.UID_ACCOUNT,
							LlkcBody.ACCOUNT == null ? LlkcBody.USER_ACCOUNT
									: LlkcBody.ACCOUNT.getUname());

					return bool;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result) {
				LLKCApplication.getInstance().resumePush();
				LLKCApplication.getInstance().setAlias();
				LLKCApplication.getInstance().insertUser(LlkcBody.ACCOUNT);
				MsgCenterActivity act = (MsgCenterActivity) getAct(MsgCenterActivity.TAG);
				if (act != null)
					act.update();
				new LoadDataTask().execute();
				finish();
			} /*
			 * else { MyToast.getToast().showToast(LoginActivity.this,
			 * "对不起,您登陆失败！请重新登陆"); }
			 */
			super.onPostExecute(result);

		}
	}

	/*
	 * 登录，判断数size
	 */
	public boolean login(String name, String pwd) throws JSONException {

		return Community.getInstance(this).login(name, pwd);

	}

	class LoadDataTask extends AsyncTask<Object, Integer, Boolean> {

		public LoadDataTask() {

		}

		// ProgressDialog dialog = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// dialog = new ProgressDialog(LoginActivity.this);
			// dialog.setCancelable(false);
			// dialog.setMessage(Tools.getStringFromRes(getApplicationContext(),
			// R.string.load_ing));
			// dialog.show();
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
			// setProgressBarIndeterminateVisibility(false);
			// dialog.dismiss();
			super.onPostExecute(result);
		}
	}

	@Override
	public BaseActivity getContext() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void setDialogContent(AlertDialog dialog, int layoutId,
			String content, String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDialogTitle(AlertDialog dialog, int layoutId, String title,
			String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public OnClickListener setPositiveClickListener(AlertDialog dialog,
			int layoutId, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OnClickListener setNegativeClickListener(AlertDialog dialog,
			int layoutId, String id) {
		// TODO Auto-generated method stub
		return null;
	}
}
