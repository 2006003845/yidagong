package com.zzl.zl_app.entity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalDb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.zrlh.llkc.R;
import com.zrlh.llkc.activity.MainActivity;
import com.zrlh.llkc.activity.MsgCenterActivity;
import com.zrlh.llkc.activity.Notifi;
import com.zrlh.llkc.activity.RequestMsgActivity;
import com.zrlh.llkc.activity.SysMsgActivity;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.funciton.Group;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.FriendsChatActivity;
import com.zrlh.llkc.ui.GoodFriendsAvtivity;
import com.zrlh.llkc.ui.GroupChatActivity;
import com.zrlh.llkc.ui.GroupListActivity;
import com.zrlh.llkc.ui.LLKCApplication;
import com.zzl.zl_app.connection.Protocol;
import com.zzl.zl_app.db.BaseMsgTable;
import com.zzl.zl_app.db.GroupMsgDBOper;
import com.zzl.zl_app.db.MTable;
import com.zzl.zl_app.db.PrivateMsgDBOper;
import com.zzl.zl_app.db.RequestMsgDBOper;
import com.zzl.zl_app.db.SysMsgDBOper;
import com.zzl.zl_app.util.Tools;

public class MsgManager {

	public static void createMsgTables(Context context) {
		SysMsgDBOper.getDBOper(context).creatTable(
				SysMsgDBOper.getDBOper(context).getTableName(""));
		RequestMsgDBOper.getDBOper(context).creatTable(
				RequestMsgDBOper.getDBOper(context).getTableName(""));
		MTable.getDBOper(context).creatTable(
				MTable.getDBOper(context).getTableName(""));
	}

	static NotificationManager nm;
	static Notifi noti;

	public static Msg msgFactory(JSONObject jsonO, Context context)
			throws JSONException {
		LLKCApplication app = LLKCApplication.getInstance();
		if (nm == null) {
			nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			noti = new Notifi(context, nm);
		}
		String type = "";
		int t = -1;
		MTable mOper = (MTable) MTable.getDBOper(context);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_Type)) {
			type = jsonO.getString(Protocol.ProtocolKey.KEY_Type);
			String uid = jsonO.getString(Protocol.ProtocolKey.KEY_Uid);
			if (type != null && !"".equals(type))
				t = Integer.valueOf(type);
			if (uid != null && !uid.equals(LlkcBody.UID_ACCOUNT)) {
				Intent i = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
				i.putExtra(MainActivity.KEY_TYPE, t);
				context.sendBroadcast(i);
			}
		}

		switch (t) {
		case 2: {
			PrivateMsg msg = new PrivateMsg(jsonO);
			PrivateMsgDBOper oper = (PrivateMsgDBOper) PrivateMsgDBOper
					.getDBOper(context);
			if (!oper.tabbleIsExist(oper.getTableName(msg.senderId)))
				oper.creatTable(oper.getTableName(msg.senderId));
			boolean b = oper.insertMsg(msg, oper.getTableName(msg.senderId));
			Msg m = mOper.isMsgExist(mOper.getTableName(""),
					MTable.MTable_Label + "=? and " + BaseMsgTable.Msg_Account
							+ "=?", new String[] {
							msg.senderId,
							LlkcBody.USER_ACCOUNT == null ? ""
									: LlkcBody.USER_ACCOUNT });
			if (m != null) {
				msg.newitems = m.newitems + 1;
				b = mOper.updateMsg(msg, mOper.getTableName(""), msg.senderId,
						msg.senderName, msg.head);
			} else {
				msg.newitems = 1;
				b = mOper.insertMsg(msg, mOper.getTableName(""), msg.senderId,
						msg.senderName, msg.head);
			}
			// TODO
			Intent msgIntent = new Intent(
					FriendsChatActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(FriendsChatActivity.KEY_MESSAGE, msg);
			context.sendBroadcast(msgIntent);

			Intent msgIntent2 = new Intent(
					MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
			msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 2);
			context.sendBroadcast(msgIntent2);

			// // 消息提醒
			// if (app != null && app.getFriendMsgPrompt())
			// noti.showNotif(R.drawable.icon,
			// Tools.getStringFromRes(context, R.string.app_name), "",
			// msg.content, 200, MainActivity.mInstance != null);
			return msg;
		}
		case 3: {
			SysMsg msg = new SysMsg(jsonO);
			SysMsgDBOper.getDBOper(context).insertMsg(msg,
					SysMsgDBOper.getDBOper(context).getTableName(""));
			Msg m = mOper.isMsgExist(mOper.getTableName(""),
					MTable.MTable_Label + "=? and " + BaseMsgTable.Msg_Account
							+ "=?", new String[] {
							"-1",
							LlkcBody.USER_ACCOUNT == null ? ""
									: LlkcBody.USER_ACCOUNT });
			msg.head = "sys_head";
			if (m != null) {
				msg.newitems = m.newitems + 1;
				mOper.updateMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			} else {
				msg.newitems = 1;
				mOper.insertMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			}
			Intent msgIntent2 = new Intent(
					MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
			msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 3);

			// 通知SysMsgActivity
			Intent msgIntent3 = new Intent(
					SysMsgActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent3.putExtra(SysMsgActivity.KEY_MESSAGE, msg);
			msgIntent3.putExtra(SysMsgActivity.KEY_TYPE, 3);
			context.sendBroadcast(msgIntent3);

			// 消息提醒
			// if (app != null && app.getSysMsgPrompt())
			// noti.showNotif(R.drawable.icon,
			// Tools.getStringFromRes(context, R.string.app_name), "",
			// msg.content, 200, MainActivity.mInstance != null);
			return msg;
		}
		case 4: {
			RequestMsg msg = new RequestMsg(jsonO);
			RequestMsgDBOper.getDBOper(context).insertMsg(msg,
					RequestMsgDBOper.getDBOper(context).getTableName(""));
			Msg m = mOper.isMsgExist(mOper.getTableName(""),
					MTable.MTable_Label + "=? and " + BaseMsgTable.Msg_Account
							+ "=?", new String[] {
							"-2",
							LlkcBody.USER_ACCOUNT == null ? ""
									: LlkcBody.USER_ACCOUNT });
			msg.head = "request_head";
			if (m != null) {
				msg.newitems = m.newitems + 1;
				mOper.updateMsg(msg, mOper.getTableName(""), "-2",
						Tools.getStringFromRes(context, R.string.request),
						msg.head);
			} else {
				msg.newitems = 1;
				mOper.insertMsg(msg, mOper.getTableName(""), "-2",
						Tools.getStringFromRes(context, R.string.request),
						msg.head);
			}
			Intent msgIntent2 = new Intent(
					MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
			msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 4);
			context.sendBroadcast(msgIntent2);

			// 通知SysMsgActivity
			Intent msgIntent3 = new Intent(
					RequestMsgActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent3.putExtra(RequestMsgActivity.KEY_MESSAGE, msg);
			msgIntent3.putExtra(RequestMsgActivity.KEY_TYPE, 4);
			context.sendBroadcast(msgIntent3);

			return msg;
		}
		case 5: {
			SysMsg msg = new SysMsg(jsonO);
			SysMsgDBOper.getDBOper(context).insertMsg(msg,
					SysMsgDBOper.getDBOper(context).getTableName(""));

			Msg m = mOper.isMsgExist(mOper.getTableName(""),
					MTable.MTable_Label + "=? and " + BaseMsgTable.Msg_Account
							+ "=?", new String[] {
							"-1",
							LlkcBody.USER_ACCOUNT == null ? ""
									: LlkcBody.USER_ACCOUNT });
			msg.head = "sys_head";
			if (m != null) {
				msg.newitems = m.newitems + 1;
				mOper.updateMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			} else {
				msg.newitems = 1;
				mOper.insertMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			}

			Intent msgIntent2 = new Intent(
					MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
			msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 5);
			context.sendBroadcast(msgIntent2);

			// 通知SysMsgActivity
			Intent msgIntent3 = new Intent(
					SysMsgActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent3.putExtra(SysMsgActivity.KEY_MESSAGE, msg);
			msgIntent3.putExtra(SysMsgActivity.KEY_TYPE, 5);
			context.sendBroadcast(msgIntent3);

			// 更新好友列表
			if (GoodFriendsAvtivity.mInstance != null)
				GoodFriendsAvtivity.mInstance.updateFriendsDate();

			// 消息提醒
			// if (app != null && app.getSysMsgPrompt())
			// noti.showNotif(R.drawable.icon,
			// Tools.getStringFromRes(context, R.string.app_name), "",
			// msg.content, 200, MainActivity.mInstance != null);
			return msg;
		}
		case 6: {
			Tools.log("Msg", "msg_logout:" + jsonO);
			SysMsg msg = new SysMsg(jsonO);
			SysMsgDBOper.getDBOper(context).insertMsg(msg,
					SysMsgDBOper.getDBOper(context).getTableName(""));

			Msg m = mOper.isMsgExist(mOper.getTableName(""),
					MTable.MTable_Label + "=? and " + BaseMsgTable.Msg_Account
							+ "=?", new String[] {
							"-1",
							LlkcBody.USER_ACCOUNT == null ? ""
									: LlkcBody.USER_ACCOUNT });
			msg.head = "sys_head";
			if (m != null) {
				msg.newitems = m.newitems + 1;
				mOper.updateMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			} else {
				msg.newitems = 1;
				mOper.insertMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			}

			Intent msgIntent2 = new Intent(
					MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
			msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 6);
			context.sendBroadcast(msgIntent2);

			// 通知SysMsgActivity
			Intent msgIntent3 = new Intent(
					SysMsgActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent3.putExtra(SysMsgActivity.KEY_MESSAGE, msg);
			msgIntent3.putExtra(SysMsgActivity.KEY_TYPE, 6);
			context.sendBroadcast(msgIntent3);

			// 消息提醒
			// if (app != null && app.getSysMsgPrompt())
			// noti.showNotif(R.drawable.icon,
			// Tools.getStringFromRes(context, R.string.app_name), "",
			// msg.content, 200, MainActivity.mInstance != null);
			return msg;
		}
		case 7: {
			GroupMsg msg = new GroupMsg(jsonO);
			GroupMsgDBOper oper = (GroupMsgDBOper) GroupMsgDBOper
					.getDBOper(context);
			if (!oper.tabbleIsExist(oper.getTableName(msg.gId)))
				oper.creatTable(oper.getTableName(msg.gId));
			Msg gm = oper.isMsgExist(oper.getTableName(msg.gId),
					BaseMsgTable.Msg_ID + "=? and " + BaseMsgTable.Msg_Account
							+ "=?", new String[] {
							msg.id,
							LlkcBody.USER_ACCOUNT == null ? ""
									: LlkcBody.USER_ACCOUNT });
			boolean b = false;
			if (gm == null) {
				b = oper.insertMsg(msg, oper.getTableName(msg.gId));
			} else {
				msg.voice = ((GroupMsg) gm).voice;
				msg.img_big = ((GroupMsg) gm).img_big;
				b = oper.updateMsg(msg, oper.getTableName(msg.gId));
			}
			Msg m = mOper.isMsgExist(mOper.getTableName(""),
					MTable.MTable_Label + "=? and " + BaseMsgTable.Msg_Account
							+ "=?", new String[] {
							msg.gId,
							LlkcBody.USER_ACCOUNT == null ? ""
									: LlkcBody.USER_ACCOUNT });

			if (msg.gHead == null || msg.gHead.equals("")) {
				if (ApplicationData.allGroupList.size() > 0) {
					for (Group g : ApplicationData.allGroupList) {
						if (g.gId.equals(msg.gId)) {
							msg.head = g.gHead;
						}
					}
				} else {
					List<Group> glist = FinalDb.create(context).findAllByWhere(
							Group.class,
							"account='" + LlkcBody.USER_ACCOUNT + "'");
					Tools.log("GList", "glist:"
							+ (glist != null ? glist.size() : -1));
					if (glist != null && glist.size() > 0) {
						for (Group g : glist) {
							if (g != null && g.gId != null
									&& g.gId.equals(msg.gId)) {
								msg.head = g.gHead;
							}
						}
					}
					if (msg.head == null || msg.head.equals(""))
						msg.head = "group_head";
				}
			}
			Tools.log("Msg", "Group_msg:msgcenter--exist" + (m == null));

			if (m != null) {
				if (!msg.senderId.equals(LlkcBody.UID_ACCOUNT))
					msg.newitems = m.newitems + 1;
				else
					msg.newitems = 0;
				b = mOper.updateMsg(msg, mOper.getTableName(""), msg.gId,
						msg.gName, msg.gHead);
			} else {
				if (!msg.senderId.equals(LlkcBody.UID_ACCOUNT))
					msg.newitems = 1;
				else
					msg.newitems = 0;
				b = mOper.insertMsg(msg, mOper.getTableName(""), msg.gId,
						msg.gName, msg.gHead);
			}
			// TODO
			Intent msgIntent = new Intent(
					GroupChatActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(GroupChatActivity.KEY_MESSAGE, msg);
			context.sendBroadcast(msgIntent);
			Intent msgIntent2 = new Intent(
					MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
			msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 7);
			context.sendBroadcast(msgIntent2);

			// 消息提醒
			// if (app != null && app.getFGroupMsgPrompt())
			// noti.showNotif(R.drawable.icon,
			// Tools.getStringFromRes(context, R.string.app_name), "",
			// msg.content, 200, MainActivity.mInstance != null);
			return msg;
		}
		case 8: {
			// TODO
			Msg msg = new Msg(jsonO);
			return msg;
		}
		case 9: {
			SysMsg msg = new SysMsg(jsonO);
			SysMsgDBOper.getDBOper(context).insertMsg(msg,
					SysMsgDBOper.getDBOper(context).getTableName(""));

			Msg m = mOper.isMsgExist(mOper.getTableName(""),
					MTable.MTable_Label + "=? and " + BaseMsgTable.Msg_Account
							+ "=?", new String[] {
							"-1",
							LlkcBody.USER_ACCOUNT == null ? ""
									: LlkcBody.USER_ACCOUNT });
			msg.head = "sys_head";
			if (m != null) {
				msg.newitems = m.newitems + 1;
				mOper.updateMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			} else {
				msg.newitems = 1;
				mOper.insertMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			}

			Intent msgIntent2 = new Intent(
					MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
			msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 9);
			context.sendBroadcast(msgIntent2);

			// 通知SysMsgActivity
			Intent msgIntent3 = new Intent(
					SysMsgActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent3.putExtra(SysMsgActivity.KEY_MESSAGE, msg);
			msgIntent3.putExtra(SysMsgActivity.KEY_TYPE, 9);
			context.sendBroadcast(msgIntent3);

			// 消息提醒
			// if (app != null && app.getSysMsgPrompt())
			// noti.showNotif(R.drawable.icon,
			// Tools.getStringFromRes(context, R.string.app_name), "",
			// msg.content, 200, MainActivity.mInstance != null);
			return msg;
		}
		case 10: {
			RequestMsg msg = new RequestMsg(jsonO);
			RequestMsgDBOper.getDBOper(context).insertMsg(msg,
					RequestMsgDBOper.getDBOper(context).getTableName(""));

			Msg m = mOper.isMsgExist(mOper.getTableName(""),
					MTable.MTable_Label + "=? and " + BaseMsgTable.Msg_Account
							+ "=?", new String[] {
							"-2",
							LlkcBody.USER_ACCOUNT == null ? ""
									: LlkcBody.USER_ACCOUNT });
			msg.head = "request_head";
			if (m != null) {
				msg.newitems = m.newitems + 1;
				mOper.updateMsg(msg, mOper.getTableName(""), "-2",
						Tools.getStringFromRes(context, R.string.request),
						msg.head);
			} else {
				msg.newitems = 1;
				mOper.insertMsg(msg, mOper.getTableName(""), "-2",
						Tools.getStringFromRes(context, R.string.request),
						msg.head);
			}

			Intent msgIntent2 = new Intent(
					MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
			msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 10);
			context.sendBroadcast(msgIntent2);

			// 通知SysMsgActivity
			Intent msgIntent3 = new Intent(
					RequestMsgActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent3.putExtra(RequestMsgActivity.KEY_MESSAGE, msg);
			msgIntent3.putExtra(RequestMsgActivity.KEY_TYPE, 10);
			context.sendBroadcast(msgIntent3);
			return msg;
		}
		case 11: {
			SysMsg msg = new SysMsg(jsonO);
			SysMsgDBOper.getDBOper(context).insertMsg(msg,
					SysMsgDBOper.getDBOper(context).getTableName(""));

			Msg m = mOper.isMsgExist(mOper.getTableName(""),
					MTable.MTable_Label + "=? and " + BaseMsgTable.Msg_Account
							+ "=?", new String[] {
							"-1",
							LlkcBody.USER_ACCOUNT == null ? ""
									: LlkcBody.USER_ACCOUNT });
			msg.head = "sys_head";
			if (m != null) {
				msg.newitems = m.newitems + 1;
				mOper.updateMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			} else {
				msg.newitems = 1;
				mOper.insertMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			}

			Intent msgIntent2 = new Intent(
					MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
			msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 11);
			context.sendBroadcast(msgIntent2);

			// 通知SysMsgActivity
			Intent msgIntent3 = new Intent(
					SysMsgActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent3.putExtra(SysMsgActivity.KEY_MESSAGE, msg);
			msgIntent3.putExtra(SysMsgActivity.KEY_TYPE, 11);
			context.sendBroadcast(msgIntent3);
			// 更新群列表
			if (GroupListActivity.mInstance != null)
				GroupListActivity.mInstance.updateGroupInfo();

			// 消息提醒
			// if (app != null && app.getSysMsgPrompt())
			// noti.showNotif(R.drawable.icon,
			// Tools.getStringFromRes(context, R.string.app_name), "",
			// msg.content, 200, MainActivity.mInstance != null);
			return msg;
		}
		case 12: {
			SysMsg msg = new SysMsg(jsonO);
			SysMsgDBOper.getDBOper(context).insertMsg(msg,
					SysMsgDBOper.getDBOper(context).getTableName(""));

			Msg m = mOper.isMsgExist(mOper.getTableName(""),
					MTable.MTable_Label + "=? and " + BaseMsgTable.Msg_Account
							+ "=?", new String[] {
							"-1",
							LlkcBody.USER_ACCOUNT == null ? ""
									: LlkcBody.USER_ACCOUNT });
			msg.head = "sys_head";
			if (m != null) {
				msg.newitems = m.newitems + 1;
				mOper.updateMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			} else {
				msg.newitems = 1;
				mOper.insertMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			}

			Intent msgIntent2 = new Intent(
					MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
			msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 12);
			context.sendBroadcast(msgIntent2);

			// 通知SysMsgActivity
			Intent msgIntent3 = new Intent(
					SysMsgActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent3.putExtra(SysMsgActivity.KEY_MESSAGE, msg);
			msgIntent3.putExtra(SysMsgActivity.KEY_TYPE, 12);
			context.sendBroadcast(msgIntent3);
			// 更新群列表
			if (GroupListActivity.mInstance != null)
				GroupListActivity.mInstance.updateGroupInfo();

			// // 消息提醒
			// if (app != null && app.getSysMsgPrompt())
			// noti.showNotif(R.drawable.icon,
			// Tools.getStringFromRes(context, R.string.app_name), "",
			// msg.content, 200, MainActivity.mInstance != null);
			return msg;
		}
		case 13: {
			SysMsg msg = new SysMsg(jsonO);
			SysMsgDBOper.getDBOper(context).insertMsg(msg,
					SysMsgDBOper.getDBOper(context).getTableName(""));

			Msg m = mOper.isMsgExist(mOper.getTableName(""),
					MTable.MTable_Label + "=? and " + BaseMsgTable.Msg_Account
							+ "=?", new String[] {
							"-1",
							LlkcBody.USER_ACCOUNT == null ? ""
									: LlkcBody.USER_ACCOUNT });
			msg.head = "sys_head";
			if (m != null) {
				msg.newitems = m.newitems + 1;
				mOper.updateMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			} else {
				msg.newitems = 1;
				mOper.insertMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			}

			Intent msgIntent2 = new Intent(
					MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
			msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 13);
			context.sendBroadcast(msgIntent2);

			// 通知SysMsgActivity
			Intent msgIntent3 = new Intent(
					SysMsgActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent3.putExtra(SysMsgActivity.KEY_MESSAGE, msg);
			msgIntent3.putExtra(SysMsgActivity.KEY_TYPE, 13);
			context.sendBroadcast(msgIntent3);
			// 更新群列表
			if (GroupListActivity.mInstance != null)
				GroupListActivity.mInstance.updateGroupInfo();

			// // 消息提醒
			// if (app != null && app.getSysMsgPrompt())
			// noti.showNotif(R.drawable.icon,
			// Tools.getStringFromRes(context, R.string.app_name), "",
			// msg.content, 200, MainActivity.mInstance != null);
			return msg;
		}

		case 15: {
			SysMsg msg = new SysMsg(jsonO);
			SysMsgDBOper.getDBOper(context).insertMsg(msg,
					SysMsgDBOper.getDBOper(context).getTableName(""));

			Msg m = mOper.isMsgExist(mOper.getTableName(""),
					MTable.MTable_Label + "=? and " + BaseMsgTable.Msg_Account
							+ "=?", new String[] {
							"-1",
							LlkcBody.USER_ACCOUNT == null ? ""
									: LlkcBody.USER_ACCOUNT });
			msg.head = "sys_head";
			if (m != null) {
				msg.newitems = m.newitems + 1;
				mOper.updateMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			} else {
				msg.newitems = 1;
				mOper.insertMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			}

			LLKCApplication.getInstance().setAuthStat(
					LlkcBody.State_Auth_Success);

			Intent msgIntent2 = new Intent(
					MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
			msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 15);
			context.sendBroadcast(msgIntent2);

			// 通知SysMsgActivity
			Intent msgIntent3 = new Intent(
					SysMsgActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent3.putExtra(SysMsgActivity.KEY_MESSAGE, msg);
			msgIntent3.putExtra(SysMsgActivity.KEY_TYPE, 15);
			context.sendBroadcast(msgIntent3);

			// 消息提醒
			// if (app != null && app.getSysMsgPrompt())
			// noti.showNotif(R.drawable.icon,
			// Tools.getStringFromRes(context, R.string.app_name), "",
			// msg.content, 200, MainActivity.mInstance != null);
			return msg;
		}

		case 16: {
			SysMsg msg = new SysMsg(jsonO);
			SysMsgDBOper.getDBOper(context).insertMsg(msg,
					SysMsgDBOper.getDBOper(context).getTableName(""));

			Msg m = mOper.isMsgExist(mOper.getTableName(""),
					MTable.MTable_Label + "=? and " + BaseMsgTable.Msg_Account
							+ "=?", new String[] {
							"-1",
							LlkcBody.USER_ACCOUNT == null ? ""
									: LlkcBody.USER_ACCOUNT });
			msg.head = "sys_head";
			if (m != null) {
				msg.newitems = m.newitems + 1;
				mOper.updateMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			} else {
				msg.newitems = 1;
				mOper.insertMsg(msg, mOper.getTableName(""), "-1",
						Tools.getStringFromRes(context, R.string.system),
						msg.head);
			}

			LLKCApplication.getInstance().setAuthStat(LlkcBody.State_Auth_No);

			Intent msgIntent2 = new Intent(
					MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
			msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 16);
			context.sendBroadcast(msgIntent2);

			// 通知SysMsgActivity
			Intent msgIntent3 = new Intent(
					SysMsgActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent3.putExtra(SysMsgActivity.KEY_MESSAGE, msg);
			msgIntent3.putExtra(SysMsgActivity.KEY_TYPE, 16);
			context.sendBroadcast(msgIntent3);

			// // 消息提醒
			// if (app != null && app.getSysMsgPrompt())
			// noti.showNotif(R.drawable.icon,
			// Tools.getStringFromRes(context, R.string.app_name), "",
			// msg.content, 200, MainActivity.mInstance != null);
			return msg;
		}
		default:
			break;
		}
		return null;
	}

	public static ArrayList<Msg> getMsgList(JSONArray array, Context context)
			throws JSONException {
		int size = array.length();
		ArrayList<Msg> os = new ArrayList<Msg>(size);
		for (int i = 0; i < size; i++) {
			JSONObject jsonO = array.getJSONObject(i);
			Msg u = msgFactory(jsonO, context);
			os.add(u);
		}
		return os;
	}
}
