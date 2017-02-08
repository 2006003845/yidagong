package com.zrlh.llkc.ui;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class ModifyPasswordActivity extends BaseActivity {
	public static final String TAG = "modifypassword";

	ImageButton backiImageView, seachImageView;
	Button modify_button;
	TextView titleTextView;
	EditText old_password, new_password, repeat_password;
	ProgressDialog dialog = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 2:
				Toast.makeText(getContext(), Tools.getStringFromRes(getContext(),
						R.string.net_error2), Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.modify_password_activity);
		init();
	}

	void init() {
		backiImageView = (ImageButton) findViewById(R.id.back);
		seachImageView = (ImageButton) findViewById(R.id.friends);
		modify_button = (Button) findViewById(R.id.modify_button);
		titleTextView = (TextView) findViewById(R.id.title_card);
		new_password = (EditText) findViewById(R.id.new_password);
		old_password = (EditText) findViewById(R.id.old_password);
		repeat_password = (EditText) findViewById(R.id.repeat_password);
		titleTextView.setText(Tools.getStringFromRes(getContext(),
				R.string.modify_button));
		seachImageView.setVisibility(View.INVISIBLE);
		backiImageView.setImageResource(R.drawable.btn_back);
		modify_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				comparison();
			}
		});

		backiImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
	}

	void comparison() {
		String old_passwordstring = old_password.getText().toString().trim();
		String new_passwordstring = new_password.getText().toString().trim();
		String repeat_passwordString = repeat_password.getText().toString()
				.trim();
		if ("".equals(old_passwordstring) || "".equals(new_passwordstring)
				|| "".equals(repeat_passwordString)) {
			Toast.makeText(this,
					Tools.getStringFromRes(getContext(), R.string.pas_nonull),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (!new_passwordstring.equals(repeat_passwordString)) {
			Toast.makeText(
					this,
					Tools.getStringFromRes(getContext(), R.string.pas_no_equal),
					Toast.LENGTH_SHORT).show();
			return;
		}
		new ModifyPassWordTask(old_passwordstring, new_passwordstring).execute();
	}

	class ModifyPassWordTask extends AsyncTask<Object, Integer, Boolean> {
		String old_password;
		String new_password;

		private ModifyPassWordTask(String old_password, String new_password) {
			this.old_password = old_password;
			this.new_password = new_password;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.propt_now));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext()).modifyPassWord(
						old_password, new_password);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if(result == null)
				handler.sendEmptyMessage(2);
			if(result)
				closeOneAct(TAG);
			super.onPostExecute(result);
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
