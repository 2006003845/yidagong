package com.zrlh.llkc.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalDb;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gauss.PlayCallback;
import com.gauss.SoundMeter;
import com.gauss.SpeexPlayer;
import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.activity.GroupModifyActivity;
import com.zrlh.llkc.activity.MainActivity;
import com.zrlh.llkc.activity.MsgCenterActivity;
import com.zrlh.llkc.activity.PreviewPicActivity;
import com.zrlh.llkc.activity.TransmitActivity;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.Face;
import com.zrlh.llkc.corporate.FaceAdapter;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.Friend;
import com.zrlh.llkc.funciton.Group;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.cache.FileConstant;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.cache.LocalMemory;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.db.BaseMsgTable;
import com.zzl.zl_app.db.GroupMsgDBOper;
import com.zzl.zl_app.db.MTable;
import com.zzl.zl_app.entity.Collect;
import com.zzl.zl_app.entity.GroupMsg;
import com.zzl.zl_app.entity.Msg;
import com.zzl.zl_app.util.FileTools;
import com.zzl.zl_app.util.ImageUtils;
import com.zzl.zl_app.util.TextUtil;
import com.zzl.zl_app.util.TimeUtil;
import com.zzl.zl_app.util.Tools;
import com.zzl.zl_app.widget.FixedViewFlipper;
import com.zzl.zl_app.widget.PullToRefreshBase.OnRefreshListener;

public class GroupChatActivity extends BaseActivity {

	public static final String TAG = "groupchat";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, GroupChatActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	public static boolean isForeground = false;
	com.zzl.zl_app.widget.PullToRefreshListView chat_content;
	List<Msg> message_Objs = new ArrayList<Msg>();
	ImageButton friends;
	TextView title_card;
	String source;
	String Stat;
	String Msg;

	TextView pop_invi_friends_group;
	TextView pop_group_detail;
	TextView pop_modify_groupinfo;
	View view;

	EditText contentET;
	Button group_chat_button;
	GroupChatAdapter groupChatAdapter;
	private PopupWindow popWin;

	GroupMsgDBOper msgDBOper;
	private Context context;
	private FixedViewFlipper flipper;

	private boolean isManager;
	FinalDb db;

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		sendTransMsg();
	}

	private GroupMsg transMsg;

	public void sendTransMsg() {
		if (transMsg != null) {
			// 转发
			MyToast.getToast().showToast(getContext(), "转发...");
			if (transMsg.mtype.equals("1")) {
				new TransmitTalkTask(transMsg, transMsg.content, "").execute();
				// new GroupTalkTask(transMsg).execute();
			} else if (transMsg.mtype.equals("2")) {
				new TransmitTalkTask(transMsg, transMsg.img_big,
						transMsg.img_small).execute();
			}
		}
		transMsg = null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.groupchat_activity);
		context = this;
		db = FinalDb.create(getContext());
		mSensor = new SoundMeter(mHandler);
		group_Info = new Group();
		Intent intent = getIntent();
		group_Info.gId = intent.getStringExtra("gId");
		group_Info.gName = intent.getStringExtra("gName");
		group_Info.gHead = intent.getStringExtra("gHead");
		source = intent.getStringExtra("source");
		if (source == null)
			source = "";
		Bundle b = intent.getExtras();
		if (b != null)
			transMsg = (GroupMsg) b.getSerializable("msg");
		imageUri = Uri.fromFile(new File(IMAGE_FILE_LOCATION));
		msgDBOper = (GroupMsgDBOper) GroupMsgDBOper.getDBOper(this);
		registerMessageReceiver();
		initView();
		if (!msgDBOper.tabbleIsExist(msgDBOper.getTableName(group_Info.gId)))
			msgDBOper.creatTable(msgDBOper.getTableName(group_Info.gId));
		show();
		new InitGroupTalksDataTask(group_Info.gId, false).execute();
		new GetGroupInfoTask(group_Info.gId).execute();
	}

	@Override
	public void onResume() {
		isForeground = true;
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(TAG);
	}

	@Override
	public void onPause() {
		isForeground = false;
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(TAG);
	}

	class InitGroupTalksDataTask extends AsyncTask<Object, Integer, List<Msg>> {
		String gId;
		boolean addMore = false;

		public InitGroupTalksDataTask(String gId, boolean addMore) {
			this.gId = gId;
			this.addMore = addMore;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// if (addMore) {
			// dialog = new ProgressDialog(getContext());
			// dialog.setCancelable(false);
			// dialog.setMessage(Tools.getStringFromRes(
			// getApplicationContext(), R.string.loading));
			// dialog.show();
			// }
		}

		@Override
		protected List<Msg> doInBackground(Object... params) {
			Cursor cursor = null;
			if (!addMore)
				cursor = msgDBOper.query(
						msgDBOper.getTableName(group_Info.gId), null,
						BaseMsgTable.Msg_Account + "=?",
						new String[] { LlkcBody.USER_ACCOUNT }, null, null,
						BaseMsgTable.Msg_Time + " desc", "20");
			else {
				int id = message_Objs.size() > 0 ? +message_Objs.get(0)._id
						: -1;
				cursor = msgDBOper.query(
						msgDBOper.getTableName(group_Info.gId), null,
						BaseMsgTable.Msg_Account + "=? and " + BaseMsgTable._ID
								+ "<?", new String[] { LlkcBody.USER_ACCOUNT,
								id + "" }, null, null, BaseMsgTable.Msg_Time
								+ " desc", "20");
			}
			List<Msg> list = msgDBOper.getMsgList(cursor);
			if (list != null && list.size() > 0) {
				return list;
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Msg> result) {
			// if (addMore) {
			// setProgressBarIndeterminateVisibility(false);
			// dialog.dismiss();
			// }
			if (result != null) {
				if (!addMore) {
					message_Objs.clear();
					for (int i = result.size() - 1; i >= 0; i--) {
						message_Objs.add(result.get(i));
					}
				} else {
					for (int i = 0; i < result.size(); i++) {
						message_Objs.add(0, result.get(i));
					}
				}
				update(addMore, result.size());
			}
			chat_content.onRefreshComplete();
			sendTransMsg();
			super.onPostExecute(result);
		}
	}

	public void update(boolean isMore, int size) {
		groupChatAdapter.notifyDataSetChanged();
		if (!isMore)
			chat_content.mRefreshableView.setSelection(groupChatAdapter
					.getCount() - 1);
		else
			chat_content.mRefreshableView.setSelection(size > 0 ? size - 1 : 0);

	}

	ProgressDialog dialog = null;

	class GroupVoiceTalkTask extends AsyncTask<Object, Integer, GroupMsg> {
		private String filePath;
		private String toUid;
		private GroupMsg msg;
		int position;
		private String fileName;
		private String time;

		public GroupVoiceTalkTask(String filePath, String toUid, GroupMsg msg,
				String fileName, String time) {
			this.filePath = filePath;
			this.toUid = toUid;
			this.msg = msg;
			this.fileName = fileName;
			this.time = time;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.sendnow));
			dialog.show();
		}

		@Override
		protected GroupMsg doInBackground(Object... params) {

			try {
				return Community.getInstance(getApplicationContext())
						.groupVoiceChat(getContext(), LlkcBody.USER_ACCOUNT,
								LlkcBody.PASS_ACCOUNT, toUid, filePath, msg,
								fileName, time);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(GroupMsg result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null && groupChatAdapter != null) {
				msg = result;
				msg.mtype = "3";
				msg.time = new Date().getTime() + "";
				message_Objs.add(msg);
				groupChatAdapter.notifyDataSetChanged();
				// groupChatAdapter.notifyDataSetChanged();
				MyToast.getToast().showToast(getApplicationContext(),
						Tools.getStringFromRes(context, R.string.send_success));
				GroupMsgDBOper oper = (GroupMsgDBOper) GroupMsgDBOper
						.getDBOper(getApplicationContext());
				oper.insertMsg(result,
						oper.getTableName(oper.getTableName(group_Info.gId)));
				chat_content.mRefreshableView.setSelection(groupChatAdapter
						.getCount() - 1);

				// 插入消息中心
				MTable mOper = (MTable) MTable.getDBOper(getContext());
				Msg m = mOper.isMsgExist(mOper.getTableName(""),
						MTable.MTable_Label + "=? and "
								+ BaseMsgTable.Msg_Account + "=?",
						new String[] {
								group_Info.gId,
								LlkcBody.USER_ACCOUNT == null ? ""
										: LlkcBody.USER_ACCOUNT });
				Msg ms = new Msg(msg.id, msg.type, msg.senderName, msg.head,
						msg.time, "(语音)", msg.senderId);
				ms.type = "7";
				if (group_Info.gHead == null || group_Info.gHead.equals("")) {
					if (ApplicationData.allGroupList.size() > 0) {
						for (Group g : ApplicationData.allGroupList) {
							if (g.gId.equals(group_Info.gId)) {
								ms.head = g.gHead;
							}
						}
					} else {
						List<Group> glist = FinalDb.create(context)
								.findAllByWhere(
										Group.class,
										"account='" + LlkcBody.USER_ACCOUNT
												+ "'");
						Tools.log("GList",
								"glist:" + (glist != null ? glist.size() : -1));
						if (glist != null && glist.size() > 0) {
							for (Group g : glist) {
								if (g != null && g.gId != null
										&& g.gId.equals(group_Info.gId)) {
									ms.head = g.gHead;
								}
							}
						}
						if (ms.head == null || ms.head.equals(""))
							ms.head = "group_head";
					}
				}
				if (m != null) {
					ms.newitems = m.newitems;
					mOper.updateMsg(ms, mOper.getTableName(""), group_Info.gId,
							group_Info.gName, group_Info.gHead);
				} else {
					ms.newitems = 0;
					mOper.insertMsg(ms, mOper.getTableName(""), group_Info.gId,
							group_Info.gName, group_Info.gHead);
				}
				Intent msgIntent2 = new Intent(
						MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
				msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
				msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 7);
				context.sendBroadcast(msgIntent2);
			} else {
				MyToast.getToast().showToast(getApplicationContext(),
						Tools.getStringFromRes(context, R.string.send_fail));
			}

			super.onPostExecute(result);
		}
	}

	class TransmitTalkTask extends AsyncTask<Object, Integer, GroupMsg> {
		GroupMsg msg;
		String paramA, paramB;

		public TransmitTalkTask(GroupMsg msg, String paramA, String paramB) {
			this.msg = msg;
			this.paramA = paramA;
			this.paramB = paramB;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(context);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(context, R.string.sendnow));
			dialog.show();
		}

		@Override
		protected GroupMsg doInBackground(Object... params) {
			try {
				return Community.getInstance(context).transmitChat_Group(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT,
						group_Info.gId, msg.mtype, paramA, paramB, msg);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(GroupMsg result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null && groupChatAdapter != null) {
				msg = result;
				contentET.setText("");
				msg.time = new Date().getTime() + "";
				message_Objs.add(msg);
				groupChatAdapter.notifyDataSetChanged();
				MyToast.getToast().showToast(getApplicationContext(),
						Tools.getStringFromRes(context, R.string.send_success));
				GroupMsgDBOper oper = (GroupMsgDBOper) GroupMsgDBOper
						.getDBOper(getApplicationContext());
				oper.insertMsg(msg,
						oper.getTableName(oper.getTableName(group_Info.gId)));
				chat_content.mRefreshableView.setSelection(groupChatAdapter
						.getCount() - 1);

				// 插入消息中心
				MTable mOper = (MTable) MTable.getDBOper(getContext());
				Msg m = mOper.isMsgExist(mOper.getTableName(""),
						MTable.MTable_Label + "=? and "
								+ BaseMsgTable.Msg_Account + "=?",
						new String[] {
								group_Info.gId,
								LlkcBody.USER_ACCOUNT == null ? ""
										: LlkcBody.USER_ACCOUNT });
				Msg ms = new Msg(msg.id, msg.type, msg.senderName, msg.head,
						msg.time, msg.content, msg.senderId);
				ms.type = "7";
				if (group_Info.gHead == null || group_Info.gHead.equals("")) {
					if (ApplicationData.allGroupList.size() > 0) {
						for (Group g : ApplicationData.allGroupList) {
							if (g.gId.equals(group_Info.gId)) {
								ms.head = g.gHead;
							}
						}
					} else {
						List<Group> glist = FinalDb.create(context)
								.findAllByWhere(
										Group.class,
										"account='" + LlkcBody.USER_ACCOUNT
												+ "'");
						Tools.log("GList",
								"glist:" + (glist != null ? glist.size() : -1));
						if (glist != null && glist.size() > 0) {
							for (Group g : glist) {
								if (g != null && g.gId != null
										&& g.gId.equals(group_Info.gId)) {
									ms.head = g.gHead;
								}
							}
						}
						if (ms.head == null || ms.head.equals(""))
							ms.head = "group_head";
					}
				}
				if (m != null) {
					ms.newitems = m.newitems;
					mOper.updateMsg(ms, mOper.getTableName(""), group_Info.gId,
							group_Info.gName, group_Info.gHead);
				} else {
					ms.newitems = 0;
					mOper.insertMsg(ms, mOper.getTableName(""), group_Info.gId,
							group_Info.gName, group_Info.gHead);
				}
				Intent msgIntent2 = new Intent(
						MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
				msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
				msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 7);
				context.sendBroadcast(msgIntent2);
			} else {
				MyToast.getToast().showToast(getApplicationContext(),
						Tools.getStringFromRes(context, R.string.send_fail));
			}
			super.onPostExecute(result);
		}
	}

	class GroupTalkTask extends AsyncTask<Object, Integer, GroupMsg> {
		GroupMsg msg;

		public GroupTalkTask(GroupMsg msg) {
			this.msg = msg;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(context);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(context, R.string.sendnow));
			dialog.show();
		}

		@Override
		protected GroupMsg doInBackground(Object... params) {
			try {
				return Community.getInstance(context).groupChat(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT, msg);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(GroupMsg result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null && groupChatAdapter != null) {
				msg = result;
				contentET.setText("");
				msg.time = new Date().getTime() + "";
				message_Objs.add(msg);
				groupChatAdapter.notifyDataSetChanged();
				MyToast.getToast().showToast(getApplicationContext(),
						Tools.getStringFromRes(context, R.string.send_success));
				GroupMsgDBOper oper = (GroupMsgDBOper) GroupMsgDBOper
						.getDBOper(getApplicationContext());
				oper.insertMsg(result,
						oper.getTableName(oper.getTableName(group_Info.gId)));
				chat_content.mRefreshableView.setSelection(groupChatAdapter
						.getCount() - 1);

				// 插入消息中心
				MTable mOper = (MTable) MTable.getDBOper(getContext());
				Msg m = mOper.isMsgExist(mOper.getTableName(""),
						MTable.MTable_Label + "=? and "
								+ BaseMsgTable.Msg_Account + "=?",
						new String[] {
								group_Info.gId,
								LlkcBody.USER_ACCOUNT == null ? ""
										: LlkcBody.USER_ACCOUNT });
				Msg ms = new Msg(msg.id, msg.type, msg.senderName, msg.head,
						msg.time, msg.content, msg.senderId);
				ms.type = "7";
				if (group_Info.gHead == null || group_Info.gHead.equals("")) {
					if (ApplicationData.allGroupList.size() > 0) {
						for (Group g : ApplicationData.allGroupList) {
							if (g.gId.equals(group_Info.gId)) {
								ms.head = g.gHead;
							}
						}
					} else {
						List<Group> glist = FinalDb.create(context)
								.findAllByWhere(
										Group.class,
										"account='" + LlkcBody.USER_ACCOUNT
												+ "'");
						Tools.log("GList",
								"glist:" + (glist != null ? glist.size() : -1));
						if (glist != null && glist.size() > 0) {
							for (Group g : glist) {
								if (g != null && g.gId != null
										&& g.gId.equals(group_Info.gId)) {
									ms.head = g.gHead;
								}
							}
						}
						if (ms.head == null || ms.head.equals(""))
							ms.head = "group_head";
					}
				}
				if (m != null) {
					ms.newitems = m.newitems;
					mOper.updateMsg(ms, mOper.getTableName(""), group_Info.gId,
							group_Info.gName, group_Info.gHead);
				} else {
					ms.newitems = 0;
					mOper.insertMsg(ms, mOper.getTableName(""), group_Info.gId,
							group_Info.gName, group_Info.gHead);
				}
				Intent msgIntent2 = new Intent(
						MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
				msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
				msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 7);
				context.sendBroadcast(msgIntent2);
			} else {
				MyToast.getToast().showToast(getApplicationContext(),
						Tools.getStringFromRes(context, R.string.send_fail));
			}
			super.onPostExecute(result);
		}
	}

	class GroupImgTalkTask extends AsyncTask<Object, Integer, GroupMsg> {
		GroupMsg msg;
		Bitmap img;

		public GroupImgTalkTask(GroupMsg msg, Bitmap img) {
			this.msg = msg;
			this.img = img;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(context);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(context, R.string.sendnow));
			dialog.show();
		}

		@Override
		protected GroupMsg doInBackground(Object... params) {
			try {
				return Community.getInstance(context).groupImgChat(context,
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT, img, msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(GroupMsg result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null && groupChatAdapter != null) {
				contentET.setText("");
				msg = result;
				// msg.time = result.time;
				// msg.img_big = result.img_big;
				// msg.mtype = "2";
				// msg.id = result.id;
				// message_Objs.set(message_Objs.size() - 1, msg);
				message_Objs.add(msg);
				groupChatAdapter.notifyDataSetChanged();
				MyToast.getToast().showToast(getApplicationContext(),
						Tools.getStringFromRes(context, R.string.send_success));
				GroupMsgDBOper oper = (GroupMsgDBOper) GroupMsgDBOper
						.getDBOper(getApplicationContext());
				oper.insertMsg(result,
						oper.getTableName(oper.getTableName(group_Info.gId)));
				chat_content.mRefreshableView.setSelection(groupChatAdapter
						.getCount() - 1);

				// 插入消息中心
				MTable mOper = (MTable) MTable.getDBOper(getContext());
				Msg m = mOper.isMsgExist(mOper.getTableName(""),
						MTable.MTable_Label + "=? and "
								+ BaseMsgTable.Msg_Account + "=?",
						new String[] {
								group_Info.gId,
								LlkcBody.USER_ACCOUNT == null ? ""
										: LlkcBody.USER_ACCOUNT });
				Msg ms = new Msg(msg.id, msg.type, msg.senderName, msg.head,
						msg.time, "(图片)", msg.senderId);
				ms.type = "7";
				if (group_Info.gHead == null || group_Info.gHead.equals("")) {
					if (ApplicationData.allGroupList.size() > 0) {
						for (Group g : ApplicationData.allGroupList) {
							if (g.gId.equals(group_Info.gId)) {
								ms.head = g.gHead;
							}
						}
					} else {
						List<Group> glist = FinalDb.create(context)
								.findAllByWhere(
										Group.class,
										"account='" + LlkcBody.USER_ACCOUNT
												+ "'");
						Tools.log("GList",
								"glist:" + (glist != null ? glist.size() : -1));
						if (glist != null && glist.size() > 0) {
							for (Group g : glist) {
								if (g != null && g.gId != null
										&& g.gId.equals(group_Info.gId)) {
									ms.head = g.gHead;
								}
							}
						}
						if (ms.head == null || ms.head.equals(""))
							ms.head = "group_head";
					}
				}
				if (m != null) {
					ms.newitems = m.newitems;
					mOper.updateMsg(ms, mOper.getTableName(""), group_Info.gId,
							group_Info.gName, group_Info.gHead);
				} else {
					ms.newitems = 0;
					mOper.insertMsg(ms, mOper.getTableName(""), group_Info.gId,
							group_Info.gName, group_Info.gHead);
				}
				Intent msgIntent2 = new Intent(
						MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
				msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
				msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 7);
				context.sendBroadcast(msgIntent2);

			} else {
				MyToast.getToast().showToast(getApplicationContext(),
						Tools.getStringFromRes(context, R.string.send_fail));
			}
			super.onPostExecute(result);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	private ImageView talkTBtn;
	private boolean isTalkInAudio = false;
	private LinearLayout textTalk;
	private TextView audioTalk;

	private View chat_popup;
	private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
			voice_rcd_hint_tooshort;
	private TextView voice_rcd_hint_tooshort_tv;
	private ImageView volume;

	void initView() {
		chat_popup = this.findViewById(R.id.chat_popup);
		voice_rcd_hint_rcding = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_rcding);
		voice_rcd_hint_loading = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_loading);
		voice_rcd_hint_tooshort = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_tooshort);
		voice_rcd_hint_tooshort_tv = (TextView) this
				.findViewById(R.id.voice_rcd_hint_tooshort_tv);
		volume = (ImageView) this.findViewById(R.id.volume);

		friends = (ImageButton) findViewById(R.id.friends);
		contentET = (EditText) findViewById(R.id.group_chat_edit);
		contentET.setText(LlkcBody.SOURCE_CONTENT);
		LlkcBody.SOURCE_CONTENT = "";
		contentET.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// if (flipper.getVisibility() == View.VISIBLE) {
				flipper.setVisibility(View.GONE);
				// }
			}
		});
		group_chat_button = (Button) findViewById(R.id.group_chat_button);

		friends.setImageResource(R.drawable.btn_more);
		friends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (null == popWin) {// (popwin自定义布局文件,popwin宽度,popwin高度)(注：若想指定位置则后两个参数必须给定值不能为WRAP_CONTENT)
					popWin = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
				}
				if (popWin.isShowing()) {// 如果当前正在显示，则将被处理
					popWin.dismiss();
				} else {
					popWin.showAsDropDown(v, 200, 0);
				}

			}
		});

		view = getLayoutInflater()
				.inflate(R.layout.group_popwindows_item, null);

		pop_invi_friends_group = (TextView) view
				.findViewById(R.id.pop_invi_friends_group);
		pop_group_detail = (TextView) view.findViewById(R.id.pop_group_detail);
		pop_modify_groupinfo = (TextView) view
				.findViewById(R.id.pop_modify_groupinfo);
		pop_invi_friends_group.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				popWin.dismiss();
				Intent intent = new Intent();
				intent.putExtra("gId", group_Info.gId);
				intent.putExtra("gName", group_Info.gName);
				intent.setClass(getApplicationContext(),
						InviteFriendActivity.class);
				startActivity(intent);

			}
		});
		pop_group_detail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popWin.dismiss();
				Intent intent = new Intent();
				intent.putExtra("group", group_Info);
				intent.putExtra("souse", "chat");
				intent.setClass(getApplicationContext(),
						GroupDetailActivity.class);
				startActivity(intent);
			}
		});
		pop_modify_groupinfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popWin.dismiss();
				Intent intent = new Intent();
				intent.putExtra("group", group_Info);
				intent.setClass(getApplicationContext(),
						GroupModifyActivity.class);
				startActivity(intent);
			}
		});
		group_chat_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String context_String = contentET.getText().toString();
				if (context_String == null || context_String.equals("")) {
					MyToast.getToast()
							.showToast(
									getApplicationContext(),
									Tools.getStringFromRes(context,
											R.string.send_null));
					return;
				}
				GroupMsg msg = new GroupMsg();
				msg.content = context_String.trim();
				msg.head = LlkcBody.ACCOUNT.getHead();
				msg.senderName = LlkcBody.USER_ACCOUNT;
				msg.senderId = LlkcBody.UID_ACCOUNT;
				msg.mtype = "1";
				msg.gId = group_Info.gId;

				new GroupTalkTask(msg).execute();
			}
		});
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		title_card = (TextView) findViewById(R.id.title_card);
		title_card.setText(group_Info.gName);
		flipper = (FixedViewFlipper) findViewById(R.id.chat_flipper);
		textTalk = (LinearLayout) this.findViewById(R.id.chat_talk_text_layout);
		audioTalk = (TextView) this.findViewById(R.id.chat_talk_audio_tv);

		audioTalk.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// 按下语音录制按钮时返回false执行父类OnTouch
				return false;
			}
		});
		audioTalk.setVisibility(View.GONE);
		textTalk.setVisibility(View.VISIBLE);
		talkTBtn = (ImageView) this.findViewById(R.id.chat_btn_audio);
		talkTBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flipper.getVisibility() == View.VISIBLE) {
					flipper.setVisibility(View.GONE);
				}
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive())
					imm.hideSoftInputFromWindow(contentET.getWindowToken(), 0);
				// TODO 语音
				if (isTalkInAudio) {
					talkTBtn.setImageResource(R.drawable.chat_btn_audio);
					audioTalk.setVisibility(View.GONE);
					textTalk.setVisibility(View.VISIBLE);
				} else {
					talkTBtn.setImageResource(R.drawable.chat_btn_keyboard);
					audioTalk.setVisibility(View.VISIBLE);
					textTalk.setVisibility(View.GONE);
				}
				isTalkInAudio = !isTalkInAudio;
			}
		});
		this.findViewById(R.id.chat_btn_more).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(contentET.getWindowToken(),
								0);

						talkTBtn.setImageResource(R.drawable.chat_btn_audio);
						audioTalk.setVisibility(View.GONE);
						textTalk.setVisibility(View.VISIBLE);
						isTalkInAudio = false;
						flipper.setVisibility(View.VISIBLE);
						flipper.setDisplayedChild(0);
					}
				});

		chat_content = (com.zzl.zl_app.widget.PullToRefreshListView) findViewById(R.id.chat_content_list);
		chat_content.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new InitGroupTalksDataTask(group_Info.gId, true).execute();
			}
		});
		// chat_content.setOnScrollListener(new OnScrollListener() {
		// boolean isScroll = false;
		//
		// @Override
		// public void onScrollStateChanged(AbsListView view, int scrollState) {
		// // TODO Auto-generated method stubS
		// isScroll = scrollState == OnScrollListener.SCROLL_STATE_IDLE;
		// }
		//
		// @Override
		// public void onScroll(AbsListView view, int firstVisibleItem,
		// int visibleItemCount, int totalItemCount) {
		//
		// if (firstVisibleItem == 0 && visibleItemCount > 0 && isScroll)
		// new InitGroupTalksDataTask(group_Info.gId, true).execute();
		// isScroll = false;
		// }
		// });
		// chat_content.setOnItemLongClickListener(new OnItemLongClickListener()
		// {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
		// int arg2, long arg3) {
		// // TODO Auto-generated method stub
		// setMyCollect((GroupMsg) message_Objs.get(arg2));
		// MyToast.getToast().showToast(getContext(),
		// "已收藏");
		// return false;
		// }
		// });

		initExpress();
		this.findViewById(R.id.chat_btn_face).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						showFaceImg();
					}
				});
		this.findViewById(R.id.chat_btn_image).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						showPicDialog();
					}
				});
	}

	public GroupMsg selectedMsg;

	private GridView express;

	public void initExpress() {
		express = (GridView) this.findViewById(R.id.chat_express_gridview);
		express.setAdapter(new FaceAdapter(context, Face.faceNames));
		express.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position < Face.faceNames.length) {
					contentET.append(/* TextUtil.formatImage( */"["
							+ Face.faceNames[position] + "] "/* , context) */);
				}
			}
		});
	}

	/**
	 * 显示表情
	 */
	private void showFaceImg() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(contentET.getWindowToken(), 0);
		flipper.setDisplayedChild(1);
	}

	private void showPicDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this).setItems(
				R.array.picFrom, picListener).create();
		dialog.show();
	}

	private static final int PIC_TAKE_PHONE = 1;
	private static final int PIC_ALBUM = 2;
	private Uri imageUri;
	// private static String IMAGE_FILE_LOCATION = FileConstant.savePath
	// + "/chat_temp.jpg";
	private static String IMAGE_FILE_LOCATION = Environment
			.getExternalStorageDirectory() + "/chat_temp.jpg";
	private DialogInterface.OnClickListener picListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == 0) {
				try {
					FileTools.deleteFile(IMAGE_FILE_LOCATION);
				} catch (IOException e) {

				}
				if (imageUri != null) {
					// capture a bitmap and store it in Uri
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivityForResult(intent, PIC_TAKE_PHONE);
				}
			} else if (which == 1) {
				Intent intent = new Intent();
				// Type为image
				intent.setType("image/*");
				// Action:选择数据然后返回
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, PIC_ALBUM);
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle b = null;
		if (data != null)
			b = data.getExtras();
		switch (requestCode) {

		case PIC_TAKE_PHONE:
			// if (resultCode != RESULT_OK) {
			// return;
			// }
			if (imageUri != null) {
				Bitmap bitmap = ImageUtils.compressBimap(IMAGE_FILE_LOCATION);
				if (bitmap == null) {
					return;
				}
				int degree = ImageUtils.readPictureDegree(IMAGE_FILE_LOCATION);
				if (degree != 0)
					bitmap = ImageUtils.rotaingImageView(degree, bitmap);
				sendImgMsg(bitmap);
			}
			break;
		case PIC_ALBUM:// from crop_big_picture
			// if (resultCode != RESULT_OK) {
			// return;
			// }
			if (data == null)
				return;
			Uri originalUri = data.getData(); // 获得图片的uri
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(originalUri, proj, null, null, null);
			if (cursor == null)
				return;
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			String path = cursor.getString(column_index);
			Bitmap bitmap = ImageUtils.compressBimap(path);
			if (bitmap != null) {
				int degree = ImageUtils.readPictureDegree(path);
				if (degree != 0)
					bitmap = ImageUtils.rotaingImageView(degree, bitmap);
				Bitmap newBm = ImageUtils.zoomBitmap(bitmap, 450, 780);
				if (!bitmap.isRecycled())
					bitmap.recycle();
				if (newBm != null)
					sendImgMsg(newBm);
			}
			break;
		}
	}

	public void sendImgMsg(Bitmap bitmap) {
		if (bitmap == null)
			return;
		GroupMsg msg = new GroupMsg();
		msg.content = null;
		msg.head = LlkcBody.ACCOUNT.getHead();
		msg.senderName = LlkcBody.USER_ACCOUNT;
		msg.senderId = LlkcBody.UID_ACCOUNT;
		msg.gId = group_Info.gId;
		msg.mtype = "2";

		new GroupImgTalkTask(msg, bitmap).execute();
	}

	void show() {
		groupChatAdapter = new GroupChatAdapter(getApplicationContext());
		chat_content.mRefreshableView.setAdapter(groupChatAdapter);

	}

	private void setMyCollect(GroupMsg msg) {
		Collect collect = new Collect();
		collect.setHead(msg.head);
		collect.setName(msg.senderName);
		collect.setTime(String.valueOf(new Date().getTime()));
		collect.setPublic_time(msg.time);
		collect.setMtype(msg.mtype);
		collect.setContent(msg.content);
		collect.setImg_big(msg.img_big);
		collect.setImg_small(msg.img_small);
		collect.setVoice(msg.voice);
		collect.setLength(msg.length);
		collect.setAccount(LlkcBody.USER_ACCOUNT);
		db.save(collect);
	}

	private boolean isMyCollect(GroupMsg msg) {
		List<Collect> collectList = db.findAllByWhere(Collect.class,
				"account='" + LlkcBody.USER_ACCOUNT + "'");
		for (int i = 0; i < collectList.size(); i++) {
			Collect collect = collectList.get(i);
			if (collect.getMtype().equals("1")) {
				if (collect.getContent().equals(msg.content)) {
					return true;
				}
			} else if (collect.getMtype().equals("2")) {
				if (collect.getImg_big().equals(msg.img_big)) {
					return true;
				}
			} else if (collect.getMtype().equals("3")) {
				if (collect.getVoice().equals(msg.voice)) {
					return true;
				}
			}
		}
		return false;
	}

	class GroupChatAdapter extends BaseAdapter {
		LayoutInflater layoutInflater;
		ChatJobView chatJobView;
		FinalBitmap finalBitmap;

		private GroupChatAdapter(Context context) {
			layoutInflater = LayoutInflater.from(context);
			finalBitmap = FinalBitmap.create(getApplicationContext());
			finalBitmap.configLoadingImage(R.drawable.head_default);
		}

		public final class ChatJobView {

			RelativeLayout other_chat_rel;
			FixedViewFlipper other_chat_viewflipper;
			TextView other_chat_name_tv;
			ImageView other_chat_head_imgv;
			TextView other_chat_cont_tv;
			ImageView other_chat_cont_imgv;
			LinearLayout other_chat_cont_voice;
			ImageView other_chat_cont_voice_imgv;
			ProgressBar other_chat_cont_voice_pb;
			TextView other_chat_cont_length;

			RelativeLayout mine_chat_rel;
			FixedViewFlipper mine_chat_viewflipper;
			ImageView mine_chat_head_imgv;
			TextView mine_chat_cont_tv;
			ImageView mine_chat_cont_imgv;
			LinearLayout mine_chat_cont_voice;
			ImageView mine_chat_cont_voice_imgv;
			ProgressBar mine_chat_cont_voice_pb;
			TextView mine_chat_cont_length;

			TextView time;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return message_Objs.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return message_Objs.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		SpeexPlayer splayer;

		public void stopPlayer() {
			if (splayer != null)
				splayer.stopPlay();
		}

		@Override
		public View getView(int arg0, View arg1, final ViewGroup parent) {
			// TODO Auto-generated method stub

			// TODO Auto-generated method stub
			if (arg1 == null) {
				chatJobView = new ChatJobView();
				arg1 = layoutInflater.inflate(R.layout.friendchat_list_item,
						null);
				chatJobView.other_chat_name_tv = (TextView) arg1
						.findViewById(R.id.item_chatlist_other_name_tv);
				chatJobView.other_chat_rel = (RelativeLayout) arg1
						.findViewById(R.id.item_chatlist_other_rel);
				chatJobView.other_chat_head_imgv = (ImageView) arg1
						.findViewById(R.id.item_chatlist_other_head_imgv);
				chatJobView.other_chat_viewflipper = (FixedViewFlipper) arg1
						.findViewById(R.id.item_chatlist_other_viewflipper);
				chatJobView.other_chat_cont_tv = (TextView) arg1
						.findViewById(R.id.item_chatlist_other_cont_tv);
				chatJobView.other_chat_cont_imgv = (ImageView) arg1
						.findViewById(R.id.item_chatlist_other_cont_imgv);
				chatJobView.other_chat_cont_voice = (LinearLayout) arg1
						.findViewById(R.id.item_chatlist_other_cont_voice);
				chatJobView.other_chat_cont_voice_imgv = (ImageView) arg1
						.findViewById(R.id.item_chatlist_other_cont_voice_imgv);
				chatJobView.other_chat_cont_voice_pb = (ProgressBar) arg1
						.findViewById(R.id.item_chatlist_other_cont_voice_progressBar);
				chatJobView.other_chat_cont_length = (TextView) arg1
						.findViewById(R.id.item_chatlist_other_cont_voice_time);

				chatJobView.mine_chat_rel = (RelativeLayout) arg1
						.findViewById(R.id.item_chatlist_mine_rel);
				chatJobView.mine_chat_head_imgv = (ImageView) arg1
						.findViewById(R.id.item_chatlist_mine_head_imgv);
				chatJobView.mine_chat_viewflipper = (FixedViewFlipper) arg1
						.findViewById(R.id.item_chatlist_mine_viewflipper);
				chatJobView.mine_chat_cont_tv = (TextView) arg1
						.findViewById(R.id.item_chatlist_mine_cont_tv);
				chatJobView.mine_chat_cont_imgv = (ImageView) arg1
						.findViewById(R.id.item_chatlist_mine_cont_imgv);
				chatJobView.mine_chat_cont_voice = (LinearLayout) arg1
						.findViewById(R.id.item_chatlist_mine_cont_voice);
				chatJobView.mine_chat_cont_voice_imgv = (ImageView) arg1
						.findViewById(R.id.item_chatlist_mine_cont_voice_imgv);
				chatJobView.mine_chat_cont_voice_pb = (ProgressBar) arg1
						.findViewById(R.id.item_chatlist_mine_cont_voice_progressBar);
				chatJobView.mine_chat_cont_length = (TextView) arg1
						.findViewById(R.id.item_chatlist_mine_cont_voice_time);

				chatJobView.time = (TextView) arg1
						.findViewById(R.id.friend_chat_time);
				arg1.setTag(chatJobView);
			} else {
				chatJobView = (ChatJobView) arg1.getTag();
			}

			final GroupMsg msg = (GroupMsg) message_Objs.get(arg0);
			chatJobView.other_chat_head_imgv.setTag(msg.id);
			chatJobView.mine_chat_head_imgv.setTag(msg.head);
			// chatJobView.mine_chat_cont_imgv.setTag(msg.img_big);
			// chatJobView.other_chat_cont_imgv.setTag(msg.img_small);
			chatJobView.mine_chat_cont_tv
					.setOnLongClickListener(new MyOnLongClickListener(msg));
			chatJobView.mine_chat_cont_imgv
					.setOnLongClickListener(new MyOnLongClickListener(msg));
			chatJobView.mine_chat_cont_voice
					.setOnLongClickListener(new MyOnLongClickListener(msg));
			chatJobView.other_chat_cont_tv
					.setOnLongClickListener(new MyOnLongClickListener(msg));
			chatJobView.other_chat_cont_imgv
					.setOnLongClickListener(new MyOnLongClickListener(msg));
			chatJobView.other_chat_cont_imgv
					.setOnLongClickListener(new MyOnLongClickListener(msg));
			if (LlkcBody.UID_ACCOUNT.equals(msg.senderId)) {
				chatJobView.mine_chat_rel.setVisibility(View.VISIBLE);
				chatJobView.other_chat_rel.setVisibility(View.GONE);
				chatJobView.mine_chat_cont_voice_imgv
						.setTag(msg.voice + "imgv");
				chatJobView.mine_chat_cont_voice_pb.setTag(msg.voice + "pb");
				// finalBitmap.display(chatJobView.mine_chat_head_imgv,
				// LlkcBody.ASSCONT.getHead());
				ImageCache.getInstance().loadImg(msg.head, msg.head, parent,
						R.drawable.head_default);
				if (msg.mtype != null && msg.mtype.equals("1")) {
					chatJobView.mine_chat_viewflipper.setDisplayedChild(0);
					if (msg.content != null && !msg.content.equals(""))
						chatJobView.mine_chat_cont_tv.setText(TextUtil
								.formatContent(msg.content, context));
					else
						chatJobView.mine_chat_cont_tv.setText("");

				} else if (msg.mtype != null && msg.mtype.equals("2")) {
					chatJobView.mine_chat_viewflipper.setDisplayedChild(1);
					chatJobView.mine_chat_cont_imgv
							.setImageResource(R.drawable.chat_default_bg);

					if (msg.img_big != null && !msg.img_big.equals("")) {
						// ImageCache.getInstance().loadImg(msg.img_big, arg2,
						// R.drawable.chat_default_bg);
						ImageCache.getInstance().loadImg(msg.img_big,
								chatJobView.mine_chat_cont_imgv,
								R.drawable.chat_default_bg);
						// ImageCache.getInstance().loadImg(msg.img_big,
						// msg.img_big, arg2, R.drawable.chat_default_bg);
					} else
						chatJobView.mine_chat_cont_imgv
								.setImageResource(R.drawable.chat_default_bg);
				} else if (msg.mtype != null && msg.mtype.equals("3")) {
					chatJobView.mine_chat_viewflipper.setDisplayedChild(2);
					chatJobView.mine_chat_cont_length
							.setText(msg.length + "''");
					chatJobView.mine_chat_cont_length
							.setVisibility(View.VISIBLE);
					int len = msg.length != null && !msg.length.equals("") ? Integer
							.parseInt(msg.length) : -1;
					int width = 90;
					Tools.log("Length", "default-width:" + width);
					if (len > 0) {
						width = (int) (width * (1 + len / (8.0)));
						width = (int) (width > (MainActivity.CURRENT_SCREEN_WIDTH * 3 / 5) ? (MainActivity.CURRENT_SCREEN_WIDTH * 3 / 5)
								: width);
						Tools.log("Length", "width" + width);
						chatJobView.mine_chat_cont_voice
								.setLayoutParams(new LinearLayout.LayoutParams(
										width, LayoutParams.WRAP_CONTENT));
					}

					chatJobView.mine_chat_cont_voice
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									if (chatJobView.mine_chat_cont_length
											.getVisibility() == View.VISIBLE)
										if (LocalMemory.getInstance()
												.checkSDCard()) {
											// TODO
											if (msg.voice != null
													&& !msg.voice.equals("")) {
												if (splayer == null)
													splayer = new SpeexPlayer(
															mHandler);
												if (!splayer.isPlay)
													mHandler.postDelayed(
															new Runnable() {

																@Override
																public void run() {
																	splayer.startPlay(
																			msg.voice,
																			new PlayCallback() {

																				@Override
																				public void finish(
																						String path) {
																					ProgressBar pb = (ProgressBar) parent
																							.findViewWithTag(path
																									+ "pb");
																					pb.setVisibility(View.GONE);
																					ImageView imgv = (ImageView) parent
																							.findViewWithTag(path
																									+ "imgv");
																					imgv.setVisibility(View.VISIBLE);
																				}

																			});
																	ProgressBar pb = (ProgressBar) parent
																			.findViewWithTag(msg.voice
																					+ "pb");
																	pb.setVisibility(View.VISIBLE);
																	ImageView imgv = (ImageView) parent
																			.findViewWithTag(msg.voice
																					+ "imgv");
																	imgv.setVisibility(View.GONE);
																}
															}, 400);

												else {
													splayer.stopPlay();
													ProgressBar pb = (ProgressBar) parent
															.findViewWithTag(msg.voice
																	+ "pb");
													pb.setVisibility(View.GONE);
													ImageView imgv = (ImageView) parent
															.findViewWithTag(msg.voice
																	+ "imgv");
													imgv.setVisibility(View.VISIBLE);
												}
											}
										} else {
											MyToast.getToast()
													.showToast(getContext(),
															"请插入您的SDcard");
										}

								}
							});

				} else {
					chatJobView.mine_chat_viewflipper.setDisplayedChild(0);
					if (msg.content != null && !msg.content.equals(""))
						chatJobView.mine_chat_cont_tv.setText(TextUtil
								.formatContent(msg.content, context));
					else
						chatJobView.mine_chat_cont_tv.setText("");
				}
			} else {

				chatJobView.other_chat_name_tv.setVisibility(View.VISIBLE);
				chatJobView.other_chat_name_tv.setText(msg.senderName);
				chatJobView.mine_chat_rel.setVisibility(View.GONE);
				chatJobView.other_chat_rel.setVisibility(View.VISIBLE);
				chatJobView.mine_chat_cont_tv.setText(TextUtil.formatContent(
						msg.content, context));
				// finalBitmap.display(chatJobView.other_chat_head_imgv,
				// msg.head);
				ImageCache.getInstance().loadImg(msg.head, msg.id, parent,
						R.drawable.head_default);
				if (msg.mtype != null && msg.mtype.equals("1")) {
					chatJobView.other_chat_viewflipper.setDisplayedChild(0);
					if (msg.content != null && !msg.content.equals(""))
						chatJobView.other_chat_cont_tv.setText(TextUtil
								.formatContent(msg.content, context));
					else
						chatJobView.other_chat_cont_tv.setText("");

				} else if (msg.mtype != null && msg.mtype.equals("2")) {
					chatJobView.other_chat_viewflipper.setDisplayedChild(1);
					chatJobView.other_chat_cont_imgv
							.setImageResource(R.drawable.chat_default_bg);

					if (msg.img_small != null && !msg.img_small.equals("")) {
						chatJobView.other_chat_cont_imgv.setTag(msg.img_small);
						// ImageCache.getInstance().loadImg(msg.img_small, arg2,
						// R.drawable.chat_default_bg);
						ImageCache.getInstance().loadImg(msg.img_small,
								chatJobView.other_chat_cont_imgv,
								R.drawable.chat_default_bg);
						// ImageCache.getInstance()
						// .loadImg(msg.img_small, msg.img_small, arg2,
						// R.drawable.chat_default_bg);
					} else
						chatJobView.other_chat_cont_imgv
								.setImageResource(R.drawable.chat_default_bg);
				} else if (msg.mtype != null && msg.mtype.equals("3")) {
					chatJobView.other_chat_viewflipper.setDisplayedChild(2);
					String fileNa = Tools.getFileNameFromUrl(msg.voice);
					String dirName = Tools.getDirNameFromUrl(msg.voice);
					final String fileName = dirName + "_" + fileNa + ".spx";
					chatJobView.other_chat_cont_voice_imgv.setTag(fileName
							+ "imgv");
					chatJobView.other_chat_cont_voice_pb
							.setTag(fileName + "pb");
					final File file = LocalMemory.getInstance().getFile(
							fileName);
					if (file == null) {
						// Load
						chatJobView.other_chat_cont_length
								.setVisibility(View.GONE);
						ImageCache.getInstance().loadVoiceFile(msg.voice,
								getContext(),
								chatJobView.other_chat_cont_length);
					} else {

					}

					int len = msg.length != null && !msg.length.equals("") ? Integer
							.parseInt(msg.length) : -1;
					int width = 90;
					Tools.log("Length", "default-width:" + width);
					if (len > 0) {
						width = (int) (width * (1 + len / (8.0)));
						width = (int) (width > (MainActivity.CURRENT_SCREEN_WIDTH * 3 / 5) ? (MainActivity.CURRENT_SCREEN_WIDTH * 3 / 5)
								: width);
						Tools.log("Length", "width" + width);
						chatJobView.other_chat_cont_voice
								.setLayoutParams(new LinearLayout.LayoutParams(
										width, LayoutParams.WRAP_CONTENT));
					}
					chatJobView.other_chat_cont_voice
							.setOnLongClickListener(new OnLongClickListener() {

								@Override
								public boolean onLongClick(View v) {
									selectedMsg = msg;
									showMsgDialog("menu", R.layout.dialog_menu,
											getContext(), group_Info.gName,
											null);
									return true;
								}
							});
					chatJobView.other_chat_cont_voice
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									if (chatJobView.other_chat_cont_length
											.getVisibility() == View.VISIBLE)
										if (LocalMemory.getInstance()
												.checkSDCard()) {

											// TODO
											if (msg.voice != null
													&& !msg.voice.equals("")
													&& file != null) {
												if (splayer == null)
													splayer = new SpeexPlayer(
															mHandler);

												if (!splayer.isPlay)
													mHandler.postDelayed(
															new Runnable() {
																@Override
																public void run() {
																	splayer.startPlay(
																			file.getAbsolutePath(),
																			new PlayCallback() {

																				@Override
																				public void finish(
																						String path) {
																					ProgressBar pb = (ProgressBar) parent
																							.findViewWithTag(path
																									+ "pb");
																					if (pb != null)
																						pb.setVisibility(View.GONE);
																					ImageView imgv = (ImageView) parent
																							.findViewWithTag(path
																									+ "imgv");
																					if (imgv != null)
																						imgv.setVisibility(View.VISIBLE);
																				}
																			},
																			fileName);
																	ProgressBar pb = (ProgressBar) parent
																			.findViewWithTag(fileName
																					+ "pb");
																	if (pb != null)
																		pb.setVisibility(View.VISIBLE);
																	ImageView imgv = (ImageView) parent
																			.findViewWithTag(fileName
																					+ "imgv");
																	if (imgv != null)
																		imgv.setVisibility(View.GONE);
																}
															}, 400);
												else {
													ProgressBar pb = (ProgressBar) parent
															.findViewWithTag(fileName
																	+ "pb");
													if (pb != null)
														pb.setVisibility(View.GONE);
													ImageView imgv = (ImageView) parent
															.findViewWithTag(fileName
																	+ "imgv");
													if (imgv != null)
														imgv.setVisibility(View.VISIBLE);
													splayer.stopPlay();
												}
											}
										} else {
											MyToast.getToast()
													.showToast(getContext(),
															"请插入您的SDcard");
										}

								}
							});
					chatJobView.other_chat_cont_length.setText(msg.length
							+ "''");
				} else {
					chatJobView.other_chat_viewflipper.setDisplayedChild(0);
					if (msg.content != null && !msg.content.equals(""))
						chatJobView.other_chat_cont_tv.setText(TextUtil
								.formatContent(msg.content, context));
					else
						chatJobView.other_chat_cont_tv.setText("");
				}
			}
			String time = msg.time;
			if (time != null && !"".equals(time)) {
				try {
					Date date = new Date(Long.parseLong(time));
					String t = TimeUtil.getTimeStr(date);
					chatJobView.time.setText(t);
				} catch (Exception e) {
					chatJobView.time.setText("");
				}
			} else {
				chatJobView.time.setText("");
			}

			chatJobView.other_chat_head_imgv
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							Friend f = new Friend();
							f.setUid(msg.senderId);
							f.setUname(msg.senderName);
							f.setHead(msg.head);
							intent.putExtra("member", f);
							intent.putExtra("source", "Friend");
							intent.setClass(getApplicationContext(),
									UserInfoActivity.class);
							startActivity(intent);
						}
					});

			chatJobView.mine_chat_cont_imgv
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO
							Intent intent = new Intent();
							Bundle b = new Bundle();
							b.putString("picUrl", msg.img_big);
							intent.putExtras(b);
							PreviewPicActivity.launch(context, intent);
						}
					});
			chatJobView.other_chat_cont_imgv
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO
							Intent intent = new Intent();
							Bundle b = new Bundle();
							b.putString("picUrl", msg.img_big);
							intent.putExtras(b);
							PreviewPicActivity.launch(context, intent);
						}
					});
			return arg1;
		}
	}

	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.zzl.app.group.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
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
				GroupMsg message_Obj = (GroupMsg) intent
						.getSerializableExtra(KEY_MESSAGE);
				boolean b = setMsg(message_Obj);
				if (!b)
					message_Objs.add(message_Obj);
				update(false, 0);
			}
		}
	}

	public boolean setMsg(GroupMsg msg) {
		if (msg == null || msg.content == null)
			return false;
		for (int i = 0; i < message_Objs.size(); i++) {
			Msg m = message_Objs.get(i);
			if (m != null && m.id != null && m.id.equals(msg.id)) {
				message_Objs.set(i, msg);
				return true;
			}
		}
		return false;
	}

	Group group_Info;

	class MyOnLongClickListener implements OnLongClickListener {
		GroupMsg msg;

		public MyOnLongClickListener(GroupMsg msg) {
			this.msg = msg;
		}

		@Override
		public boolean onLongClick(View v) {
			selectedMsg = msg;
			showMsgDialog("menu", R.layout.dialog_menu, getContext(),
					group_Info.gName, null);
			return true;
		}

	}

	private void start(String name) {
		mSensor.start(name);
	}

	private void stop() {
		mSensor.stop();
		volume.setImageResource(R.drawable.amp1);
	}

	private void updateDisplay(double signalEMA) {

		switch ((int) signalEMA) {
		case 0:
		case 1:
			volume.setImageResource(R.drawable.amp1);
			break;
		case 2:
		case 3:
			volume.setImageResource(R.drawable.amp2);

			break;
		case 4:
		case 5:
			volume.setImageResource(R.drawable.amp3);
			break;
		case 6:
		case 7:
			volume.setImageResource(R.drawable.amp4);
			break;
		case 8:
		case 9:
			volume.setImageResource(R.drawable.amp5);
			break;
		case 10:
		case 11:
			volume.setImageResource(R.drawable.amp6);
			break;
		default:
			volume.setImageResource(R.drawable.amp7);
			break;
		}
	}

	private SoundMeter mSensor;

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 100) {
				updateDisplay(msg.arg1);
			}
		};
	};

	private int flag = 1;
	private boolean isShosrt = false;
	private long startVoiceT, endVoiceT;
	String voiceName;

	public void showRecordView() {
		audioTalk.setBackgroundResource(R.drawable.chat_audio_btn_bg);
		chat_popup.setVisibility(View.VISIBLE);
		voice_rcd_hint_loading.setVisibility(View.VISIBLE);
		voice_rcd_hint_rcding.setVisibility(View.GONE);
		voice_rcd_hint_tooshort.setVisibility(View.GONE);
		mHandler.postDelayed(new Runnable() {
			public void run() {
				if (!isShosrt) {
					voice_rcd_hint_loading.setVisibility(View.GONE);
					voice_rcd_hint_rcding.setVisibility(View.VISIBLE);
				}
			}
		}, 300);
	}

	int count = 0;
	long startTime;
	long nextTime;

	// 按下语音录制按钮时
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!Environment.getExternalStorageDirectory().exists()) {
			MyToast.getToast().showToast(getContext(), "No SDCard");
			return false;
		}

		if (isTalkInAudio) {
			int[] location = new int[2];
			audioTalk.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
			int btn_rc_Y = location[1];
			int btn_rc_X = location[0];
			if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
				if (!Environment.getExternalStorageDirectory().exists()) {
					MyToast.getToast().showToast(getContext(), "No SDCard");
					return false;
				}
				System.out.println("2");
				if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X) {// 判断手势按下的位置是否是语音录制按钮的范围内
					startVoiceT = new Date().getTime();

					if (count == 0) {
						startTime = System.currentTimeMillis();
						Tools.log("Voice", "startTime:" + startTime);
						count++;
						startVoiceT = new Date().getTime();
						Tools.log("Chat", "startVoiceT:" + startVoiceT);
						// voiceName = startVoiceT + ".amr";
						voiceName = FileConstant.savePath
								+ FileConstant.Path_Txt + "/" + startVoiceT
								+ ".spx";
						start(voiceName);
						showRecordView();
						// TODO 录音
					} else {
						nextTime = System.currentTimeMillis();
						Tools.log("Voice", "nextTime:" + nextTime);
						if (nextTime - startTime > 1100) {

							Tools.log("Chat", "startVoiceT:" + startVoiceT);
							// voiceName = startVoiceT + ".amr";
							voiceName = FileConstant.savePath
									+ FileConstant.Path_Txt + "/" + startVoiceT
									+ ".spx";
							start(voiceName);
							showRecordView();
						}
						startTime = nextTime;
					}
					flag = 2;
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {// 松开手势时执行录制完成
				System.out.println("4");
				audioTalk.setBackgroundResource(R.drawable.chat_audio_btn_bg);
				endVoiceT = new Date().getTime();
				stop();
				if (event.getY() < btn_rc_Y || event.getX() < btn_rc_X) {
					chat_popup.setVisibility(View.GONE);
					// 取消录制
					flag = 1;
					File file = new File(voiceName);
					if (file.exists()) {
						file.delete();
					}
				} else {
					// 成功录制
					voice_rcd_hint_rcding.setVisibility(View.GONE);

					Tools.log("Chat", "endVoiceT:" + endVoiceT);
					flag = 1;
					final int time = (int) (endVoiceT - startVoiceT);
					Tools.log("Chat", "time:" + time);
					if (time < 1000) {
						isShosrt = true;
						voice_rcd_hint_loading.setVisibility(View.GONE);
						voice_rcd_hint_rcding.setVisibility(View.GONE);
						voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
						mHandler.postDelayed(new Runnable() {
							public void run() {
								voice_rcd_hint_tooshort
										.setVisibility(View.GONE);
								chat_popup.setVisibility(View.GONE);
								isShosrt = false;
							}
						}, 500);
						File file = new File(voiceName);
						if (file.exists()) {
							file.delete();
						}
						return false;
					} else {
						isShosrt = false;
						final GroupMsg msg = new GroupMsg();
						msg.content = null;
						msg.head = LlkcBody.ACCOUNT.getHead();
						msg.senderName = LlkcBody.USER_ACCOUNT;
						msg.senderId = LlkcBody.UID_ACCOUNT;
						msg.gId = group_Info.gId;
						msg.mtype = "3";
						msg.length = time / 1000 + "";
						msg.voice = voiceName;

						mHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								new GroupVoiceTalkTask(voiceName,
										group_Info.gId, msg, startVoiceT
												+ ".spx", time / 1000 + "")
										.execute();
							}
						}, 500);
					}
					chat_popup.setVisibility(View.GONE);
				}
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onStart() {
		super.onStart();
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

		if (id.equals("menu")) {
			TextView titleTV = (TextView) dialog
					.findViewById(R.id.dialog_title);
			titleTV.setText(title);
		}

	}

	@Override
	public OnClickListener setPositiveClickListener(final AlertDialog dialog,
			int layoutId, String id) {
		if (id.equals("menu")) {
			dialog.findViewById(R.id.parent).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
			if (selectedMsg.mtype != null && selectedMsg.mtype.equals("2")) {
				dialog.findViewById(R.id.dialog_copy).setVisibility(View.GONE);
				dialog.findViewById(R.id.dialog_divider).setVisibility(
						View.GONE);
				// dialog.findViewById(R.id.dialog_transmit).setVisibility(
				// View.GONE);
				// dialog.findViewById(R.id.dialog_divider2).setVisibility(
				// View.GONE);
			} else if (selectedMsg.mtype != null
					&& selectedMsg.mtype.equals("3")) {
				dialog.findViewById(R.id.dialog_copy).setVisibility(View.GONE);
				dialog.findViewById(R.id.dialog_divider).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_transmit).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_divider2).setVisibility(
						View.GONE);
			}
			dialog.findViewById(R.id.dialog_copy).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (selectedMsg == null)
								return;
							Tools.copy(selectedMsg.content, getContext());
							MyToast.getToast().showToast(getContext(),
									R.string.copyed);
							dialog.dismiss();
						}
					});
			dialog.findViewById(R.id.dialog_transmit).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = getIntent();
							if (intent == null)
								intent = new Intent();
							Bundle b = new Bundle();
							b.putSerializable("msg", selectedMsg);
							b.putInt("type", 7);
							intent.putExtras(b);
							TransmitActivity.launch(getContext(), intent);
							dialog.dismiss();
						}
					});
			dialog.findViewById(R.id.dialog_colllect).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (selectedMsg == null)
								return;
							if (isMyCollect(selectedMsg)) {
								MyToast.getToast().showToast(getContext(),
										"已收藏");
								dialog.dismiss();
								return;
							}
							setMyCollect(selectedMsg);
							MyToast.getToast().showToast(getContext(),
									R.string.collected);
							dialog.dismiss();
						}
					});
			dialog.findViewById(R.id.dialog_delete).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							msgDBOper.delete(
									msgDBOper.getTableName(group_Info.gId),
									"id=?", new String[] { selectedMsg.id });
							message_Objs.remove(selectedMsg);
							groupChatAdapter.notifyDataSetChanged();
							selectedMsg = null;
							dialog.dismiss();
						}
					});

		}
		return null;
	}

	@Override
	public OnClickListener setNegativeClickListener(AlertDialog dialog,
			int layoutId, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onBackPressed() {
		if (flipper.getVisibility() == View.VISIBLE) {
			flipper.setVisibility(View.GONE);
			return;
		}
		if (groupChatAdapter != null)
			groupChatAdapter.stopPlayer();
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		closeOneAct(TAG);
		if (!source.equals("list")) {
			if (GroupListActivity.mInstance != null)
				GroupListActivity.mInstance.updateGroupInfo();
		}
		super.onBackPressed();
	}

	class GetGroupInfoTask extends AsyncTask<Object, Integer, Group> {
		String gId;

		public GetGroupInfoTask(String gId) {
			this.gId = gId;
		}

		@Override
		protected Group doInBackground(Object... params) {

			try {
				return Community.getInstance(getApplicationContext())
						.queryFGroupInfo(group_Info.gId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Group result) {
			if (result != null) {
				group_Info = result;
				if (group_Info.gManagerId != null
						&& group_Info.gManagerId.equals(LlkcBody.UID_ACCOUNT)) {
					isManager = true;
				} else {
					isManager = false;
				}
				if (!isManager) {
					if (pop_invi_friends_group != null)
						pop_invi_friends_group.setVisibility(View.GONE);
					if (pop_modify_groupinfo != null)
						pop_modify_groupinfo.setVisibility(View.GONE);
				}
			}
			super.onPostExecute(result);
		}
	}
}
