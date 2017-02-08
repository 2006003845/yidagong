package com.zrlh.llkc.activity;

import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.SysMsgAdapter;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.db.BaseMsgTable;
import com.zzl.zl_app.db.IMsgDBOper;
import com.zzl.zl_app.db.SysMsgDBOper;
import com.zzl.zl_app.entity.Msg;

public class SysMsgActivity extends BaseActivity {
	public static final String TAG = "sys";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, SysMsgActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		registerMessageReceiver();
		setContentView(R.layout.msg_sys);
		initView();
		new InitMsgDataTask().execute();
	}

	private TextView titleTV;
	private ListView msgListV;
	private SysMsgAdapter adapter;
	private ImageButton clearBtn;

	private void initView() {
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText(R.string.sys_msg);
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		clearBtn = (ImageButton) this.findViewById(R.id.btn);
		clearBtn.setVisibility(View.VISIBLE);
		clearBtn.setImageResource(R.drawable.msg_btn_clear);

		clearBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SysMsgDBOper oper = (SysMsgDBOper) SysMsgDBOper
						.getDBOper(getContext());
				oper.delete(oper.getTableName(""), BaseMsgTable.Msg_Account
						+ "=?",
						new String[] { LlkcBody.USER_ACCOUNT == null ? ""
								: LlkcBody.USER_ACCOUNT });
				ApplicationData.sysMsgList.clear();
				adapter.notifyDataSetChanged();
			}
		});
		msgListV = (ListView) this.findViewById(R.id.msg_sys_listv);
		adapter = new SysMsgAdapter(getContext(), ApplicationData.sysMsgList);
		msgListV.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	@Override
	public BaseActivity getContext() {
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

	class InitMsgDataTask extends AsyncTask<Object, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Object... params) {
			// SYS
			IMsgDBOper sysOper = SysMsgDBOper.getDBOper(getContext());
			Cursor sysCur = sysOper.query(sysOper.getTableName(""), null,
					BaseMsgTable.Msg_Account + "=?",
					new String[] { LlkcBody.USER_ACCOUNT == null ? ""
							: LlkcBody.USER_ACCOUNT }, null, null,
					BaseMsgTable.Msg_Time + " desc");
			List<Msg> sysList = SysMsgDBOper.getDBOper(getContext())
					.getMsgList(sysCur);
			if (sysList != null && sysList.size() > 0) {
				ApplicationData.sysMsgList.clear();
				ApplicationData.sysMsgList.addAll(sysList);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}

	class UpdateMsgDataTask extends AsyncTask<Object, Integer, List<Msg>> {

		@Override
		protected List<Msg> doInBackground(Object... params) {

			SysMsgDBOper mOper = (SysMsgDBOper) SysMsgDBOper
					.getDBOper(getContext());
			Cursor mCur = mOper.query(mOper.getTableName(""), null,
					BaseMsgTable.Msg_Account + "=?",
					new String[] { LlkcBody.USER_ACCOUNT }, null, null,
					BaseMsgTable.Msg_Time + " desc");
			List<Msg> mList = mOper.getMsgList(mCur);
			return mList;
		}

		@Override
		protected void onPostExecute(List<Msg> result) {
			if (result != null && result.size() > 0) {

				ApplicationData.sysMsgList.clear();
				ApplicationData.sysMsgList.addAll(result);

			}
			adapter.notifyDataSetChanged();

			super.onPostExecute(result);
		}
	}

	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.zzl.app.msg_sys.MESSAGE_RECEIVED_ACTION";
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
				Msg msg = (Msg) intent.getSerializableExtra(KEY_MESSAGE);
				int type = intent.getIntExtra(KEY_TYPE, -1);
				if (type == 4 || type == 10) {
					ApplicationData.requestMsgList.add(msg);

				} else {

					switch (type) {
					case 5:

						break;
					case 6:
						// 登出
						// 退出
//						FinalDb.create(context, true).deleteByWhere(
//								Account.class, "1=1");
//						ApplicationData.clearUserData();
//						LlkcBody.clear();
//						AppManager.getAppManager().AppExit2(getContext());
//						LoginActivity.launch(getContext(), null);
//						JPushInterface.stopPush(getContext());
						break;
					case 9:

						break;
					case 11:

						break;

					default:
						break;
					}
					ApplicationData.sysMsgList.add(msg);
				}
			}
		}
	}
}
