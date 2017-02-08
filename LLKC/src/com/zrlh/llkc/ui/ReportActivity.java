package com.zrlh.llkc.ui;

import org.json.JSONException;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ReportActivity extends BaseActivity {
	public static final String TAG = "report";

	ImageButton back;
	ImageButton friends;

	TextView title_card;

	String idString;
	String cityString;
	String typeString;
	String contentString = "";
	String Msg;

	CheckBox xujia;
	CheckBox zhaoman;
	CheckBox qita;
	EditText tijiao_text;
	Button tijiao_button;
	ProgressDialog dialog = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(getContext(), Msg, Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(
						getContext(),
						Tools.getStringFromRes(getContext(),
								R.string.net_error3), Toast.LENGTH_SHORT)
						.show();

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
		setContentView(R.layout.repct_activity);
		Intent intent = this.getIntent();
		idString = intent.getStringExtra("idString");
		cityString = intent.getStringExtra("cityString");
		init();
	}

	void init() {
		title_card = (TextView) findViewById(R.id.title_card);
		title_card.setText(Tools
				.getStringFromRes(getContext(), R.string.report));
		back = (ImageButton) findViewById(R.id.back);
		friends = (ImageButton) findViewById(R.id.friends);
		friends.setVisibility(View.INVISIBLE);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		xujia = (CheckBox) findViewById(R.id.xujia);
		zhaoman = (CheckBox) findViewById(R.id.zhaoman);
		qita = (CheckBox) findViewById(R.id.qita);
		tijiao_button = (Button) findViewById(R.id.tijiao_button);
		tijiao_text = (EditText) findViewById(R.id.tijiao_text);
		tijiao_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ("".equals(typeString) || typeString == null) {
					Toast.makeText(
							getContext(),
							Tools.getStringFromRes(getContext(),
									R.string.report_con), Toast.LENGTH_SHORT)
							.show();
					return;
				}
				new ReportTask().execute();
			}
		});
		xujia.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				typeString = arg1 ? "1" : "0";
				qita.setChecked(false);
				zhaoman.setChecked(false);

			}
		});
		zhaoman.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				typeString = arg1 ? "1" : "0";
				qita.setChecked(false);
				xujia.setChecked(false);
			}
		});
		qita.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				zhaoman.setChecked(false);
				xujia.setChecked(false);
				typeString = arg1 ? "1" : "0";

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

	class ReportTask extends AsyncTask<Object, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.loading));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext()).report(idString,
						cityString, typeString, contentString);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if(result == null)
				handler.sendEmptyMessage(2);
			else{
				if(result)
					closeOneAct(TAG);
				handler.sendEmptyMessage(1);
			}
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
