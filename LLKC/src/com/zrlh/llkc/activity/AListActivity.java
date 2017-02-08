package com.zrlh.llkc.activity;

import java.util.List;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.zrlh.llkc.funciton.Group;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.FriendsChatActivity;
import com.zrlh.llkc.ui.GroupChatActivity;
import com.zrlh.llkc.ui.GroupDetailActivity;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.entity.GroupMsg;
import com.zzl.zl_app.entity.PrivateMsg;
import com.zzl.zl_app.util.Tools;

public class AListActivity extends BaseActivity {
	public final static String TAG = "alist";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, AListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.activity_alist);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			type = b.getInt("type", -1);
			from = b.getInt("from", -1);
			if (type == 2) {
				pmsg = (PrivateMsg) b.getSerializable("msg");
				pmsg.time = "";
			} else if (type == 7) {
				gmsg = (GroupMsg) b.getSerializable("msg");
				gmsg.time = "";
			}
		}

		initView();
	}

	private PrivateMsg pmsg;
	private GroupMsg gmsg;

	private int type = -1;
	private int from = -1;
	public static final int From_Friend = 1;
	public static final int From_FGroup = 2;

	ListView transmitListV;
	private FriendsListAdapter friendAdapter;
	private GroupListAdapter groupAdapter;

	ProgressDialog dialog = null;

	private void initView() {
		TextView titleTV = (TextView) this.findViewById(R.id.title_tv);
		transmitListV = (ListView) this.findViewById(R.id.alist_listv);
		if (from == From_Friend) {
			titleTV.setText(R.string.select_friend);
			friendAdapter = new FriendsListAdapter(getContext());
			transmitListV.setAdapter(friendAdapter);
			if (ApplicationData.friendList.size() == 0)
				new GetFriendListTask(false).execute();
		} else if (from == From_FGroup) {
			titleTV.setText(R.string.select_fgroup);
			groupAdapter = new GroupListAdapter(getContext(),
					ApplicationData.allGroupList);
			transmitListV.setAdapter(groupAdapter);
			if (ApplicationData.allGroupList.size() == 0)
				new GetOwnGroupListTask(false).execute();
		} else
			titleTV.setText("选择");
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		transmitListV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (type == -1)
					return;
				if (pmsg == null && gmsg == null)
					return;
				// TODO

				closeOneAct(FriendsChatActivity.TAG);
				closeOneAct(GroupChatActivity.TAG);

				Intent intent = new Intent();
				Bundle b = new Bundle();
				if (from == From_Friend) {
					Friend friend = (Friend) arg0.getAdapter().getItem(arg2);
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

				} else if (from == From_FGroup) {
					Group g = (Group) arg0.getAdapter().getItem(arg2);
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
					gmsg.gId = g.gId;
					gmsg.gHead = g.gHead;
					gmsg.gName = g.gName;
					intent.putExtra("gId", g.gId);
					intent.putExtra("gName", g.gName);
					intent.putExtra("gHead", g.gHead);
					b.putSerializable("msg", gmsg);
					intent.putExtras(b);
					GroupChatActivity.launch(getContext(), intent);
				}
				closeOneAct(TAG);
				closeOneAct(TransmitActivity.TAG);
			}
		});
	}

	@Override
	public BaseActivity getContext() {
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

	private class FriendsListAdapter extends BaseAdapter {
		LayoutInflater layoutInflater;
		FriendsView friendsView;
		FinalBitmap finalBitmap;

		private final class FriendsView {
			ImageView friend_head;
			TextView friend_name;
			TextView friend_sign;
		}

		public FriendsListAdapter(Context context) {
			// TODO Auto-generated constructor stub
			layoutInflater = LayoutInflater.from(context);
			finalBitmap = FinalBitmap.create(getContext());
			finalBitmap.configLoadingImage(R.drawable.head_default);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ApplicationData.friendList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return ApplicationData.friendList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				friendsView = new FriendsView();
				convertView = layoutInflater.inflate(
						R.layout.seach_friend_list_item, null);
				friendsView.friend_name = (TextView) convertView
						.findViewById(R.id.friend_name);
				friendsView.friend_head = (ImageView) convertView
						.findViewById(R.id.friend_head);
				friendsView.friend_sign = (TextView) convertView
						.findViewById(R.id.friend_adress);
				convertView.setTag(friendsView);
			} else {
				friendsView = (FriendsView) convertView.getTag();

			}
			Friend friend = ApplicationData.friendList.get(position);
			friendsView.friend_name.setText(friend.getUname());
			friendsView.friend_sign.setText(friend.getSign());
			if (Tools.isUrl(friend.getHead()))
				finalBitmap.display(friendsView.friend_head, friend.getHead());
			else
				friendsView.friend_head
						.setImageResource(R.drawable.head_default);

			return convertView;
		}
	}

	private class GroupListAdapter extends BaseAdapter {
		LayoutInflater layoutInflater;
		List<Group> newgroup_Lists;
		GroupListView groupListView;
		FinalBitmap finalBitmap;

		public final class GroupListView {
			TextView gname;
			TextView gnum;
			TextView gtype;
			ImageView g_peop_pic;
			ImageView gpic;

		}

		private GroupListAdapter(Context context, List<Group> newgroup_Lists) {
			this.newgroup_Lists = newgroup_Lists;
			layoutInflater = LayoutInflater.from(context);
			finalBitmap = FinalBitmap.create(context);
			finalBitmap.configLoadingImage(R.drawable.group_head);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return newgroup_Lists.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return newgroup_Lists.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (arg1 == null) {
				groupListView = new GroupListView();
				arg1 = layoutInflater.inflate(R.layout.group_list_item, null);
				groupListView.g_peop_pic = (ImageView) arg1
						.findViewById(R.id.g_peop_pic);
				groupListView.gname = (TextView) arg1.findViewById(R.id.gname);
				groupListView.gnum = (TextView) arg1.findViewById(R.id.gnum);
				groupListView.gpic = (ImageView) arg1.findViewById(R.id.gpic);
				groupListView.gtype = (TextView) arg1.findViewById(R.id.gtype);
				arg1.setTag(groupListView);
			} else {
				groupListView = (GroupListView) arg1.getTag();
			}
			// groupListView.gtype.setVisibility(View.VISIBLE);
			// if (!newgroup_Lists.get(arg0).gManagerId
			// .equals(LlkcBody.UID_ACCOUNT)) {
			// groupListView.gtype.setVisibility(View.INVISIBLE);
			// }
			final Group group = newgroup_Lists.get(arg0);
			groupListView.gname.setText(group.gName);
			groupListView.gnum.setText(group.gMembers
					+ Tools.getStringFromRes(getContext(), R.string.person));
			if (Tools.isUrl(group.getgHead()))
				finalBitmap.display(groupListView.gpic, group.getgHead());
			else
				groupListView.gpic.setImageResource(R.drawable.group_head);
			final int position = arg0;
			groupListView.gpic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.putExtra("souse", "list");
					Bundle b = new Bundle();
					b.putSerializable("group", group);
					intent.putExtras(b);
					intent.setClass(getContext(), GroupDetailActivity.class);
					startActivity(intent);
				}
			});

			return arg1;
		}
	}

	class GetOwnGroupListTask extends AsyncTask<Object, Integer, List<Group>> {

		public boolean isUpdate = false;

		public GetOwnGroupListTask(boolean isUpdate) {
			this.isUpdate = isUpdate;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!isUpdate) {
				if (dialog == null)
					dialog = new ProgressDialog(getContext());
				dialog.setCancelable(true);
				dialog.setMessage(Tools.getStringFromRes(getContext(),
						R.string.loading));
				dialog.show();
			}
		}

		@Override
		protected List<Group> doInBackground(Object... params) {

			try {
				return Community.getInstance(getContext()).getOwnFGroupList(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Group> result) {
			if (!isUpdate) {
				setProgressBarIndeterminateVisibility(false);
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
					dialog = null;
				}
			}
			if (result != null && result.size() > 0) {
				ApplicationData.allGroupList.clear();
				ApplicationData.allGroupList.addAll(result);

				for (Group group : result) {
					group.setAccount(LlkcBody.USER_ACCOUNT);
					if (LlkcBody.UID_ACCOUNT.equals(group.gManagerId)) {
						ApplicationData.selfGroupList.add(group);
					} else {
						ApplicationData.joinGroupList.add(group);
					}
				}
				groupAdapter.notifyDataSetChanged();
			}
			super.onPostExecute(result);
		}
	}

	class GetFriendListTask extends AsyncTask<Object, Integer, List<Friend>> {

		public boolean isUpdate = false;

		public GetFriendListTask(boolean isUpdate) {
			this.isUpdate = isUpdate;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!isUpdate) {
				if (dialog == null)
					dialog = new ProgressDialog(getContext());
				dialog.setCancelable(true);
				dialog.setMessage(Tools.getStringFromRes(getContext(),
						R.string.loading));
				dialog.show();
			}
		}

		@Override
		protected List<Friend> doInBackground(Object... params) {

			try {
				return Community.getInstance(getContext()).getFriendList(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Friend> result) {
			if (!isUpdate) {
				setProgressBarIndeterminateVisibility(false);
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
					dialog = null;
				}
			}
			if (result != null && result.size() > 0) {
				ApplicationData.friendList.clear();
				for (Friend friend : result) {
					ApplicationData.friendList.add(friend);
				}
			}
			friendAdapter.notifyDataSetChanged();
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
}
