package com.zrlh.llkc.ui;

import java.util.List;
import java.util.Vector;

import net.tsz.afinal.FinalDb;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

import com.zrlh.llkc.AppManager;
import com.zrlh.llkc.R;
import com.zrlh.llkc.activity.MainActivity;
import com.zrlh.llkc.activity.Notifi;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.joggle.Account;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.connection.Protocol;
import com.zzl.zl_app.db.BaseMsgTable;
import com.zzl.zl_app.db.GroupMsgDBOper;
import com.zzl.zl_app.entity.Msg;
import com.zzl.zl_app.util.Tools;

public class Llkc_MsgReceiver extends BroadcastReceiver {
	NotificationManager nm;
	Notifi noti;

	LLKCApplication app;

	public void init(Context context) {
		if (app != null)
			return;
		app = LLKCApplication.getInstance();
		LlkcBody.isNewJpostPrompt = app.getNewJobPrompt();
		LlkcBody.isFriendMsgPrompt = app.getFriendMsgPrompt();
		LlkcBody.isFGroupMsgPrompt = app.getFGroupMsgPrompt();
		LlkcBody.isSysMsgPrompt = app.getSysMsgPrompt();
		LlkcBody.isNotiByVoice = app.getNotiSoundPrompt();
		LlkcBody.isNotiByShake = app.getNotiShakePrompt();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		init(context);
		if (nm == null) {
			nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			noti = new Notifi(context, nm);
		}
		Bundle bundle = intent.getExtras();

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			// send the Registration Id to your server...
		} else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			// send the UnRegistration Id to your server...
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			processCustomMessage(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			int notifactionId = bundle
					.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {

			// 打开自定义的Activity
			Intent i = new Intent(context, MainActivity.class);
			i.putExtras(bundle);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);

		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..
		} else {

		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	// List<Message_Obj> message_Objs = new ArrayList<Message_Obj>();

	// //send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {

		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		// Message_Obj message_Obj = new Message_Obj();
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(message);
			Tools.log("Msg", "msg_logout:" + jsonObject);
			String gId = null;
			String type = jsonObject.optString("Type");
			// 通知栏提醒
			// 消息提醒

			// 好友聊天
			if ("2".equals(type)) {
				// 消息提醒
				if (app != null && app.getFriendMsgPrompt())
					noti.showNotif(R.drawable.icon, Tools.getStringFromRes(
							context, R.string.app_name), "", jsonObject
							.optString(Protocol.ProtocolKey.KEY_Content), 200,
							MainActivity.mInstance != null);
			}// 群聊
			else if ("7".equals(type)) {
				if (app != null
						&& app.getFGroupMsgPrompt()
						&& !jsonObject.optString(Protocol.ProtocolKey.KEY_Uid)
								.equals(LlkcBody.UID_ACCOUNT))
					noti.showNotif(R.drawable.icon, Tools.getStringFromRes(
							context, R.string.app_name), "", jsonObject
							.optString(Protocol.ProtocolKey.KEY_Content), 200,
							MainActivity.mInstance != null);
			}// 系统
			else if ("3".equals(type) || "5".equals(type) || "9".equals(type)
					|| "11".equals(type) || "12".equals(type)
					|| "13".equals(type) || "14".equals(type)
					|| "15".equals(type) || "16".equals(type)) {
				// 消息提醒
				if (app != null && app.getSysMsgPrompt())
					noti.showNotif(R.drawable.icon, Tools.getStringFromRes(
							context, R.string.app_name), "", jsonObject
							.optString(Protocol.ProtocolKey.KEY_Content), 200,
							MainActivity.mInstance != null);
				if ("14".equals(type))
					LlkcBody.haveCheckJpostRecommend = false;
			} // 请求
			else if ("4".equals(type) || "10".equals(type)) {
				if (app != null && app.getSysMsgPrompt())
					noti.showNotif(R.drawable.icon, Tools.getStringFromRes(
							context, R.string.app_name), "", jsonObject
							.optString(Protocol.ProtocolKey.KEY_Content), 200,
							MainActivity.mInstance != null);
			}
			if (type != null && type.equals("7"))
				gId = jsonObject.optString("Mid");
			else if (type != null && type.equals("6")) {
				// new LogoutTask(context).execute();
				// 退出
				FinalDb.create(context, true).deleteByWhere(Account.class,
						"1=1");
				JPushInterface.setAlias(context, LlkcBody.IMEI, null);// 设置用户别名
				ApplicationData.clearUserData();
				LlkcBody.clear();
				AppManager.getAppManager().AppExit2(context);
				if (MainActivity.mInstance != null)
					LoginActivity.launch(MainActivity.mInstance, null);
				// 消息提醒
				if (!jsonObject.isNull("Content")) {
					String content = jsonObject.optString("Content");
					noti.showNotif(R.drawable.icon,
							Tools.getStringFromRes(context, R.string.app_name),
							"", content, 200, MainActivity.mInstance != null);
					if (MainActivity.mInstance != null)
						MyToast.getToast().showToast(context, content);
				}
				// if (LLKCApplication.getInstance() != null)
				// LLKCApplication.getInstance().stopJpush();
			}
			// new LoadMsgDataTask(context, gId).execute();
			TaskManager task = TaskManager.getInstance(context);
			task.putGid(gId == null ? "" : gId);
			if (!task.isAlive)
				task.start();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static class TaskManager extends Thread {
		private static TaskManager manager = null;
		static Context context;
		boolean isAlive = false;

		private TaskManager(Context context) {
			this.context = context;
		}

		@Override
		public synchronized void start() {
			isAlive = true;
			super.start();
		}

		public static TaskManager getInstance(Context context) {
			if (manager == null)
				manager = new TaskManager(context);
			return manager;
		}

		private LoadMsgDataTask task = null;
		private static Vector<String> gIdVector = new Vector<String>();

		public void putGid(String gId) {
			gIdVector.add(gId);
		}

		public boolean isFinished() {
			return LoadMsgDataTask.t == null;
		}

		boolean doIt = true;

		public void stopThread() {
			doIt = false;
			manager = null;
		}

		public String getFirstGid() {
			if (gIdVector.size() == 0)
				return null;
			String gid = gIdVector.firstElement();
			if (gid != null) {
				gIdVector.remove(0);
			}
			return gid;
		}

		@Override
		public void run() {
			while (doIt) {
				if (isFinished())
					doAction(context);
				else
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
			super.run();
		}

		public void doAction(Context context) {
			String gid = getFirstGid();
			if (gid != null) {
				task = LoadMsgDataTask.getTask(context, gid);
				task.execute();
			} else {
				task = null;
			}
		}
	}

	static class LoadMsgDataTask extends AsyncTask<Object, Integer, Boolean> {
		private Context context;
		private String gId;

		public static LoadMsgDataTask t;

		public static LoadMsgDataTask getTask(Context context, String gId) {
			if (t == null)
				t = new LoadMsgDataTask(context, gId);
			return t;
		}

		private LoadMsgDataTask(Context context, String gId) {
			this.context = context;
			this.gId = gId;
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			try {
				if (gId != null && !gId.equals("")) {
					boolean haveMore = true;
					do {
						Msg lastMsg = null;
						GroupMsgDBOper oper = (GroupMsgDBOper) GroupMsgDBOper
								.getDBOper(context);
						if (!oper.tabbleIsExist(oper.getTableName(gId)))
							oper.creatTable(oper.getTableName(gId));
						Cursor c = oper
								.query(oper.getTableName(gId),
										null,
										BaseMsgTable.Msg_Account + "=?",
										new String[] { LlkcBody.USER_ACCOUNT == null ? ""
												: LlkcBody.USER_ACCOUNT },
										null, null, BaseMsgTable.Msg_Time
												+ " desc");
						if (c != null) {
							c.moveToFirst();
							if (c.getCount() > 0)
								lastMsg = oper.getMsg(c);
							c.close();
						}
						List<Msg> list = Community.getInstance(context)
								.getGroupMsgList(LlkcBody.USER_ACCOUNT,
										LlkcBody.PASS_ACCOUNT, "0", context,
										lastMsg == null ? "0" : lastMsg.id,
										gId, "20");
						haveMore = (list != null && list.size() == 20);
					} while (haveMore);
				}
				Community.getInstance(context).getMsgList(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT, "0",
						context);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			t = null;
		}
	}
}
