package com.zrlh.llkc.activity;

import java.util.Date;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.MsgAdapter;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.Friend;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.FriendsChatActivity;
import com.zrlh.llkc.ui.GroupChatActivity;
import com.zrlh.llkc.ui.LLKCApplication;
import com.zrlh.llkc.ui.LoginActivity;
import com.zzl.zl_app.db.BaseMsgTable;
import com.zzl.zl_app.db.IMsgDBOper;
import com.zzl.zl_app.db.MTable;
import com.zzl.zl_app.entity.MMsg;
import com.zzl.zl_app.entity.Msg;

public class MsgCenterActivity extends BaseActivity {
	public static final String TAG = "msg_center";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		registerMessageReceiver();
		setContentView(R.layout.msg_center);
		initView();
	}

	private TextView titleTV;
	private ListView msgListV;
	private MsgAdapter msgAdapter;

	private void initView() {
		titleTV = (TextView) this.findViewById(R.id.tap_top_tv_title);
		titleTV.setText(R.string.msg_center);
		msgListV = (ListView) this.findViewById(R.id.msg_center_listv);
		msgAdapter = new MsgAdapter(getContext(), ApplicationData.msgList);
		msgListV.setAdapter(msgAdapter);
		msgListV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				msgAdapter.setDeletePosition(-1);
				MMsg msg = (MMsg) arg0.getAdapter().getItem(arg2);
				if (msg == null)
					return;
				// TODO
				if (!LlkcBody.isLogin()) {
					if (!msg.label.equals("-3"))
						LoginActivity.launch(getContext(), getIntent());
					else {
						RecommendedForYouActivity.launch(getContext(),
								getIntent());
						LlkcBody.haveCheckJpostRecommend = true;
						LLKCApplication.getInstance()
								.setHaveCheckJpostRecommend(true);
					}
				} else {
					if ("2".equals(msg.type)) {
						Friend friend = new Friend();
						friend.setUid(msg.senderId);
						friend.setUname(msg.senderName);
						friend.setHead(msg.head);
						Intent intent = new Intent();
						intent.putExtra("toObj", friend);
						intent.setClass(getApplicationContext(),
								FriendsChatActivity.class);
						startActivity(intent);
					} else if ("7".equals(msg.type)) {
						Intent intent = new Intent();
						intent.putExtra("gId", msg.label);
						intent.putExtra("gName", msg.lName);
						intent.putExtra("gHead", msg.head);
						intent.setClass(getApplicationContext(),
								GroupChatActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						startActivity(intent);
					} else if (msg.label.equals("-1")) {
						// 系统UI
						SysMsgActivity.launch(getContext(), getIntent());
					} else if (msg.label.equals("-2")) {
						// 请求UI
						RequestMsgActivity.launch(getContext(), getIntent());
					} else if (msg.label.equals("-3")) {
						// 推荐岗位
						RecommendedForYouActivity.launch(getContext(),
								getIntent());
						LlkcBody.haveCheckJpostRecommend = true;
						LLKCApplication.getInstance()
								.setHaveCheckJpostRecommend(true);
					}
					if (!msg.label.equals("-3")) {
						MTable mOper = (MTable) MTable.getDBOper(getContext());
						msg.newitems = 0;
						mOper.updateMsg(msg, mOper.getTableName(""), msg.label,
								msg.lName, msg.head);
					}
				}
				msgAdapter.notifyDataSetChanged();
			}
		});

		msgListV.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				msgAdapter.setDeletePosition(arg2);
				return true;
			}
		});

		this.findViewById(R.id.parent).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						msgAdapter.setDeletePosition(-1);
					}
				});

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
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(TAG);
		if (ApplicationData.msgList.size() == 0)
			new InitMsgDataTask().execute();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(TAG);
	}

	class InitMsgDataTask extends AsyncTask<Object, Integer, List<Msg>> {

		@Override
		protected List<Msg> doInBackground(Object... params) {

			// Talk
			MTable mOper = (MTable) MTable.getDBOper(getContext());
			if (LlkcBody.USER_ACCOUNT != null) {
				Cursor mCur = mOper.query(mOper.getTableName(""), null,
						BaseMsgTable.Msg_Account + "=?",
						new String[] { LlkcBody.USER_ACCOUNT }, null, null,
						BaseMsgTable.Msg_Time + " desc");
				return mOper.getMsgList(mCur);

			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Msg> result) {

			if (result != null && result.size() > 0) {
				ApplicationData.msgList.clear();
				ApplicationData.msgList.addAll(result);
			} else {
				ApplicationData.msgList.clear();
			}
			ApplicationData.msgList.add(0,
					new MMsg("0", "-3", "-3", "新岗位提醒", "jpost_head", ""
							+ ((new Date()).getTime()), "您有新的岗位推荐", ""));
			msgAdapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}

	public void update() {
		new UpdateMsgDataTask().execute();
	}

	public void update2() {
		msgAdapter.notifyDataSetChanged();
		new UpdateMsgDataTask().execute();
	}

	class UpdateMsgDataTask extends AsyncTask<Object, Integer, List<Msg>> {

		@Override
		protected List<Msg> doInBackground(Object... params) {
			if (LlkcBody.USER_ACCOUNT == null)
				LlkcBody.USER_ACCOUNT = LLKCApplication.getInstance()
						.getAccount();
			if (LlkcBody.USER_ACCOUNT != null) {
				IMsgDBOper mOper = MTable.getDBOper(getContext());
				Cursor mCur = mOper.query(mOper.getTableName(""), null,
						BaseMsgTable.Msg_Account + "=?",
						new String[] { LlkcBody.USER_ACCOUNT }, null, null,
						BaseMsgTable.Msg_Time + " desc");
				List<Msg> mList = mOper.getMsgList(mCur);

				return mList;
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Msg> result) {
			if (result != null && result.size() > 0) {
				ApplicationData.msgList.clear();
				ApplicationData.msgList.addAll(result);
			} else
				ApplicationData.msgList.clear();
			ApplicationData.msgList.add(0, new MMsg("0", "-3", "-3", "新岗位提醒",
					"jpost_head", "", "您有新的岗位推荐", ""));
			msgAdapter.notifyDataSetChanged();

			super.onPostExecute(result);
		}
	}

	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.zzl.app.msgcenter.MESSAGE_RECEIVED_ACTION";
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				Msg msg = (Msg) intent.getSerializableExtra(KEY_MESSAGE);
				int type = intent.getIntExtra(KEY_TYPE, -1);
				update();

				switch (type) {
				case 5:

					break;
				case 6:
					// 登出
					// // 退出
					// FinalDb.create(context,
					// true).deleteByWhere(Account.class,
					// "1=1");
					// ApplicationData.clearUserData();
					// LlkcBody.clear();
					// AppManager.getAppManager().AppExit2(getContext());
					// LoginActivity.launch(getContext(), null);
					// LLKCApplication.getInstance().stopJpush();
					break;
				case 9:

					break;
				case 11:

					break;

				default:
					break;
				}

			}
		}
	}

	@Override
	public void onBackPressed() {
		if (MainActivity.mInstance != null)
			MainActivity.mInstance.setCurrentTab(0);
	}
}
