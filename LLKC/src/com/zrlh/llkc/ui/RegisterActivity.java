package com.zrlh.llkc.ui;

import net.tsz.afinal.FinalDb;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.joggle.Account;
import com.zrlh.llkc.joggle.User;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

@SuppressLint("HandlerLeak")
public class RegisterActivity extends BaseActivity {

	public static final String TAG = "register";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, RegisterActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	ImageButton backiImageView, seachImageView;
	Button reg_reg_button;
	TextView titleTextView;
	EditText reg_name, reg_email, reg_pass;

	String Msg, Stat;
	String IsCompany = "0";
	String nameString;
	String pasString;
	String emailString;
	String name;
	String password;

	RadioGroup registration_type;
	RadioButton domestic_consumer;
	RadioButton enterprise_users;
	FinalDb db;
	ProgressDialog dialog = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(getContext(), Msg, Toast.LENGTH_SHORT).show();
				new LogintPlatformTask(name, password).execute();
				break;
			case 2:
				Toast.makeText(getContext(), Msg, Toast.LENGTH_SHORT).show();

				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register_activity);
		db = FinalDb.create(this, true);
		init();
	}

	void upnews() {
		String reg_text_name = reg_name.getText().toString().trim();
		if ("".equals(reg_text_name)) {

		} else {

		}
	}

	void init() {
		backiImageView = (ImageButton) findViewById(R.id.back);
		seachImageView = (ImageButton) findViewById(R.id.friends);
		reg_name = (EditText) findViewById(R.id.reg_name);
		// reg_name.setInputType(InputType.TYPE_CLASS_NUMBER);
		// reg_name.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
		reg_pass = (EditText) findViewById(R.id.reg_pass);
		reg_email = (EditText) findViewById(R.id.reg_email);
		titleTextView = (TextView) findViewById(R.id.title_card);
		reg_reg_button = (Button) findViewById(R.id.reg_reg_button);

		registration_type = (RadioGroup) findViewById(R.id.registration_type);
		domestic_consumer = (RadioButton) findViewById(R.id.domestic_consumer);
		enterprise_users = (RadioButton) findViewById(R.id.enterprise_users);
		RadioGroup.OnCheckedChangeListener mChangeListener = new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == domestic_consumer.getId()) {
					IsCompany = "0";
				} else {
					IsCompany = "1";
				}
			}
		};
		registration_type.setOnCheckedChangeListener(mChangeListener);

		titleTextView.setText(Tools.getStringFromRes(getContext(),
				R.string.reg_button));
		seachImageView.setVisibility(View.INVISIBLE);
		backiImageView.setImageResource(R.drawable.btn_back);
		reg_reg_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				nameString = reg_name.getText().toString().trim();
				pasString = reg_pass.getText().toString().trim();
				emailString = reg_email.getText().toString().trim();
				if ("".equals(nameString)) {

					Toast.makeText(
							getContext(),
							Tools.getStringFromRes(getContext(),
									R.string.name_nonull), Toast.LENGTH_SHORT)
							.show();
					return;

				} else if ("".equals(pasString)) {
					Toast.makeText(
							getContext(),
							Tools.getStringFromRes(getContext(),
									R.string.pas_nonull), Toast.LENGTH_SHORT)
							.show();
					return;
				}
				new RegisterTask().execute();
			}
		});
		backiImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
				onBackPressed();
			}
		});
	}

	class RegisterTask extends AsyncTask<Object, Integer, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(RegisterActivity.this);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.propt_now));
			dialog.show();
		}

		@Override
		protected JSONObject doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext()).register(nameString,
						pasString, emailString);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null) {
				try {
					name = result.getString("Name");
					password = result.getString("Pwd");
					Stat = result.getString("Stat");
					Msg = result.getString("Msg");
					if ("0".equals(Stat)) {
						handler.sendEmptyMessage(1);
					} else
						handler.sendEmptyMessage(2);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

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

	/*
	 * 登录，判断数size
	 */
	public boolean login(String name, String pwd) throws JSONException {

		return Community.getInstance(this).login(name, pwd);

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
				dialog = new ProgressDialog(RegisterActivity.this);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.logining));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {

			boolean netStat = Tools.checkNetWorkStatus(RegisterActivity.this);
			if (!netStat) {
				return false;
			}
			try {
				boolean bool = login(name, pwd);
				if (bool) {
					// LlkcBody.ACCOUNT = Community.getInstance(
					// getContext()).queryUserInfo(
					// LlkcBody.UID_ACCOUNT);
					saveAccount(LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT,
							LlkcBody.UID_ACCOUNT,
							LlkcBody.ACCOUNT == null ? LlkcBody.USER_ACCOUNT
									: LlkcBody.ACCOUNT.getUname());
				}
				return bool;
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
				closeOneAct(TAG);
			} /*
			 * else { MyToast.getToast().showToast(RegisterActivity.this,
			 * "对不起,您登陆失败！请重新登陆"); }
			 */
			super.onPostExecute(result);
		}
	}

	public void saveAccount(String name, String pwd, String uid, String uname) {
		User newUser = new User();
		newUser.setArccount(name);
		newUser.setPrassWord(pwd);
		newUser.setLoginStatus("1");
		newUser.setUid(uid);
		db.deleteByWhere(User.class, "1=1");
		db.save(newUser);

		Account asscont = new Account();
		asscont.setArccount(name);
		asscont.setPrassWord(pwd);
		asscont.setUid(uid);
		asscont.setLoginStatus("1");
		asscont.setBirth("");
		asscont.setCert("");
		asscont.setHead("");
		asscont.setLocal("");
		asscont.setEmail(emailString);
		asscont.setPhone("");
		asscont.setProf("");
		asscont.setSex("");
		asscont.setSign("");
		asscont.setUname(uname);
		db.save(asscont);
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
