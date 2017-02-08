package com.zrlh.llkc.corporate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.activity.MainActivity;
import com.zrlh.llkc.corporate.base.BaseActivity;

public class AuthenticationResultActivity extends BaseActivity {

	public static final String Tag = "auth_result";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, AuthenticationResultActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(Tag, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.authentication_result);
		initView();
	}

	private TextView titleTV;

	private void initView() {
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText(R.string.authing);
		this.findViewById(R.id.btn).setVisibility(View.GONE);
		this.findViewById(R.id.auth_result_btn_msg).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						closeOneAct(Tag);
						closeOneAct(CorporateActivity.Tag);
						MainActivity.mInstance.setCurrentTab(1);
					}
				});
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(Tag);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(Tag);
	}
	
	@Override
	public void onBackPressed() {
		closeOneAct(Tag);
		super.onBackPressed();
	}

	@Override
	public BaseActivity getContext() {
		return this;
	}

	@Override
	public void setDialogContent(AlertDialog dialog, int layoutId, String msg,
			String id) {
	}

	@Override
	public void setDialogTitle(AlertDialog dialog, int layoutId, String title,
			String id) {
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
