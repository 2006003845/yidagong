package com.zrlh.llkc.activity;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.Friend;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.FriendsChatActivity;
import com.zrlh.llkc.ui.GroupChatActivity;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.db.BaseMsgTable;
import com.zzl.zl_app.db.MTable;
import com.zzl.zl_app.entity.GroupMsg;
import com.zzl.zl_app.entity.MMsg;
import com.zzl.zl_app.entity.Msg;
import com.zzl.zl_app.entity.PrivateMsg;

public class TransmitActivity extends BaseActivity {

	public static final String TAG = "transmit";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, TransmitActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			type = b.getInt("type", -1);
			if (type == 2) {
				pmsg = (PrivateMsg) b.getSerializable("msg");
				pmsg.time = "";
			} else if (type == 7) {
				gmsg = (GroupMsg) b.getSerializable("msg");
				gmsg.time = "";
			}
		}
		setContentView(R.layout.activity_transmit);
		initView();
		new InitMsgDataTask().execute();
	}

	private PrivateMsg pmsg;
	private GroupMsg gmsg;

	private int type = -1;
	ListView transmitListV;
	private ContactorListAdapter conAdapter;

	public void initView() {
		TextView titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText("转发到");
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		this.findViewById(R.id.transmit_layout_selectfriend)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO
						Intent intent = getIntent();
						Bundle b = intent.getExtras();
						b.putInt("from", 1);
						intent.putExtras(b);
						AListActivity.launch(getContext(), intent);
					}
				});
		this.findViewById(R.id.transmit_layout_selectfgroup)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO
						Intent intent = getIntent();
						Bundle b = intent.getExtras();
						b.putInt("from", 2);
						intent.putExtras(b);
						AListActivity.launch(getContext(), intent);
					}
				});
		transmitListV = (ListView) this.findViewById(R.id.transmit_listv);
		conAdapter = new ContactorListAdapter();
		transmitListV.setAdapter(conAdapter);
		transmitListV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				MMsg msg = (MMsg) arg0.getAdapter().getItem(arg2);
				if (msg == null)
					return;
				if (type == -1)
					return;
				if (pmsg == null && gmsg == null)
					return;
				// TODO
				closeOneAct(TAG);
				closeOneAct(FriendsChatActivity.TAG);
				closeOneAct(GroupChatActivity.TAG);
				Intent intent = new Intent();
				Bundle b = new Bundle();
				if (msg.type.equals("2")) {
					Friend friend = new Friend();
					friend.setUid(msg.label);
					friend.setUname(msg.lName);
					friend.setHead(msg.head);
					intent.putExtra("toObj", friend);
					if (pmsg == null && gmsg != null) {
						pmsg = new PrivateMsg("", "2", LlkcBody.ACCOUNT
								.getUname(), LlkcBody.ACCOUNT.getHead(), "",
								gmsg.content, LlkcBody.UID_ACCOUNT);
						pmsg.mtype = gmsg.mtype;
						pmsg.img_big = gmsg.img_big;
						pmsg.img_small = gmsg.img_small;
					}
					pmsg.senderId = LlkcBody.UID_ACCOUNT;
					pmsg.senderName = LlkcBody.ACCOUNT.getUname();
					pmsg.head = LlkcBody.ACCOUNT.getHead();
					b.putSerializable("msg", pmsg);
					intent.putExtras(b);
					FriendsChatActivity.launch(getContext(), intent);
				} else if (msg.type.equals("7")) {
					if (gmsg == null && pmsg != null) {
						gmsg = new GroupMsg("", "7", LlkcBody.ACCOUNT
								.getUname(), LlkcBody.ACCOUNT.getHead(), "",
								pmsg.content, LlkcBody.UID_ACCOUNT);
						gmsg.mtype = pmsg.mtype;
						gmsg.img_big = pmsg.img_big;
						gmsg.img_small = pmsg.img_small;
					}
					gmsg.senderId = LlkcBody.UID_ACCOUNT;
					gmsg.senderName = LlkcBody.ACCOUNT.getUname();
					gmsg.head = LlkcBody.ACCOUNT.getHead();
					gmsg.gId = msg.label;
					gmsg.gHead = msg.head;
					gmsg.gName = msg.lName;
					intent.putExtra("gId", msg.label);
					intent.putExtra("gName", msg.lName);
					intent.putExtra("gHead", msg.head);
					b.putSerializable("msg", gmsg);
					intent.putExtras(b);
					GroupChatActivity.launch(getContext(), intent);
				}
			}
		});
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
				for (Msg msg : result) {
					if (msg.type.equals("2") || msg.type.equals("7"))
						ApplicationData.msgList.add(msg);
				}
			} else {
				ApplicationData.msgList.clear();
			}
			conAdapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}

	@Override
	public BaseActivity getContext() {
		return this;
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

	private class ContactorListAdapter extends BaseAdapter {

		LayoutInflater inflater;

		private ContactorListAdapter() {
			inflater = LayoutInflater.from(getContext());
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ApplicationData.msgList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return ApplicationData.msgList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.group_list_item, null);
				holder = new ViewHolder();

				holder.head = (ImageView) convertView.findViewById(R.id.gpic);
				holder.name = (TextView) convertView.findViewById(R.id.gname);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final MMsg msg = (MMsg) ApplicationData.msgList.get(position);
			if (msg.lName == null || "".equals(msg.lName))
				holder.name.setText(msg.senderName);
			else
				holder.name.setText(msg.lName);
			holder.head.setTag(msg.label);

			if ("7".equals(msg.type)) {
				ImageCache.getInstance().loadImg(msg.head, msg.label, parent,
						R.drawable.msgcenter_item_head_fgroup);
			} else if ("2".equals(msg.type)) {
				String head = msg.head;
				ImageView headImgV = (ImageView) parent
						.findViewWithTag(msg.label);
				setImage(headImgV, R.drawable.head_default);
				if (head != null && !head.equals("")) {
					ImageCache.getInstance().loadImg(head, msg.label, parent,
							R.drawable.head_default);
				} else {
					setImage(holder.head, R.drawable.head_default);
				}
			}

			return convertView;
		}

		public void setImage(final ImageView imgv, final int imageResId) {
			if (imgv == null)
				return;
			handler.post(new Runnable() {

				@Override
				public void run() {
					imgv.setImageResource(imageResId);
					imgv.postInvalidate();
				}
			});
		}

		class ViewHolder {
			ImageView head;
			TextView name;
		}
	}

	Handler handler = new Handler();
}
