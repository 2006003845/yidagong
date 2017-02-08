package com.zrlh.llkc.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.LLKCApplication;

public class MsgSettingActivity extends BaseActivity {
	public static final String TAG = "msgsetting";

	TextView titleTextView;

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, MsgSettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.msgsetting);
		app = LLKCApplication.getInstance();
		initView();
	}

	private CheckBox newJpostCB, friendsCB, fgroupCB, sysCB, voiceCB, shakeCB;

	private LLKCApplication app;

	private void initView() {
		titleTextView = (TextView) findViewById(R.id.title_tv);
		titleTextView.setText(R.string.msgsetting);
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		newJpostCB = ((CheckBox) this
				.findViewById(R.id.msgsetting_checkbox_job));
		newJpostCB.setChecked(LlkcBody.isNewJpostPrompt);
		newJpostCB.setOnCheckedChangeListener(onCheckChangeListener);

		friendsCB = ((CheckBox) this
				.findViewById(R.id.msgsetting_checkbox_friends));
		friendsCB.setChecked(LlkcBody.isFriendMsgPrompt);
		friendsCB.setOnCheckedChangeListener(onCheckChangeListener);

		fgroupCB = ((CheckBox) this
				.findViewById(R.id.msgsetting_checkbox_fgroup));
		fgroupCB.setChecked(LlkcBody.isFGroupMsgPrompt);
		fgroupCB.setOnCheckedChangeListener(onCheckChangeListener);

		sysCB = ((CheckBox) this.findViewById(R.id.msgsetting_checkbox_sys));
		sysCB.setChecked(LlkcBody.isSysMsgPrompt);
		sysCB.setOnCheckedChangeListener(onCheckChangeListener);

		voiceCB = ((CheckBox) this.findViewById(R.id.msgsetting_checkbox_sound));
		voiceCB.setChecked(LlkcBody.isNotiByVoice);
		voiceCB.setOnCheckedChangeListener(onCheckChangeListener);

		shakeCB = ((CheckBox) this.findViewById(R.id.msgsetting_checkbox_shake));
		shakeCB.setChecked(LlkcBody.isNotiByShake);
		shakeCB.setOnCheckedChangeListener(onCheckChangeListener);
	}

	OnCheckedChangeListener onCheckChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.msgsetting_checkbox_job:
				LlkcBody.isNewJpostPrompt = isChecked;
				app.setNewJobPrompt(isChecked);
				break;
			case R.id.msgsetting_checkbox_friends:
				LlkcBody.isFriendMsgPrompt = isChecked;
				app.setFriendMsgPrompt(isChecked);
				break;
			case R.id.msgsetting_checkbox_fgroup:
				LlkcBody.isFGroupMsgPrompt = isChecked;
				app.setFGroupMsgPrompt(isChecked);
				break;
			case R.id.msgsetting_checkbox_sys:
				LlkcBody.isSysMsgPrompt = isChecked;
				app.setSysMsgPrompt(isChecked);
				break;
			case R.id.msgsetting_checkbox_sound:
				voiceCB.setChecked(isChecked);
				LlkcBody.isNotiByVoice = isChecked;
				app.setNotiSoundPrompt(isChecked);
				break;
			case R.id.msgsetting_checkbox_shake:
				shakeCB.setChecked(isChecked);
				LlkcBody.isNotiByShake = isChecked;
				app.setNotiShakePrompt(isChecked);
				break;
			default:
				break;
			}
		}
	};

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
		closeOneAct(TAG);
		super.onBackPressed();
	}

	@Override
	public BaseActivity getContext() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void setDialogContent(AlertDialog dialog, int layoutId, String msg,
			String id) {
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
