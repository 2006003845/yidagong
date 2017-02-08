package com.zrlh.llkc.ui;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class FindPasswordActivity extends BaseActivity {
	public static final String TAG = "findpassword";
	
	ImageButton backiImageView, seachImageView;
	Button find_pass_button;
	TextView titleTextView;
	EditText find_name, find_email;

	ProgressDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.findpassword);
		init();
	}

	void init() {
		backiImageView = (ImageButton) findViewById(R.id.back);
		seachImageView = (ImageButton) findViewById(R.id.friends);
		titleTextView = (TextView) findViewById(R.id.title_card);
		backiImageView.setImageResource(R.drawable.btn_back);
		find_pass_button = (Button) findViewById(R.id.find_pass_button);
		find_name = (EditText) findViewById(R.id.find_name);
		find_email = (EditText) findViewById(R.id.find_email);
		backiImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		seachImageView.setVisibility(View.INVISIBLE);
		titleTextView.setText(Tools.getStringFromRes(getContext(),
				R.string.find_pass_button));
		find_pass_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				access_content();
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

	private void access_content() {
		String nameString = find_name.getText().toString().trim();
		String emaiString = find_email.getText().toString().trim();
		if ("".equals(nameString)) {
			Toast.makeText(this,
					Tools.getStringFromRes(this, R.string.name_nonull),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if ("".equals(emaiString)) {
			Toast.makeText(this,
					Tools.getStringFromRes(this, R.string.mail_nonull),
					Toast.LENGTH_SHORT).show();
			return;
		}
		new FindPassWordTask(nameString, emaiString).execute();
	}

	class FindPassWordTask extends AsyncTask<Object, Integer, Boolean> {

		String name;
		String email;

		public FindPassWordTask(String name, String email) {
			this.name = name;
			this.email = email;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(FindPasswordActivity.this);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.propt_now));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			try {
				return Community.getInstance(getContext()).findPassWord(name, email);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result)
				MyToast.getToast().showToast(FindPasswordActivity.this,
						"已将您的密码发到您的邮箱中，请查看您的邮箱");
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
