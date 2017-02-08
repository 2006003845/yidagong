package com.zrlh.llkc.ui;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.Friend;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class InviteFriendActivity extends BaseActivity {
	public static final String TAG = "invitefriend";
	ImageButton back, friends;
	TextView title_card;
	Button inbirefriend_button;

	String gId;
	String gName;
	String invitaString = "";
	String Stat;
	String Msg;

	ListView group_friend_list;
	FriendsListAdapter friendsListAdapter;

	ProgressDialog dialog = null;

	List<Friend> friends_objs = new ArrayList<Friend>();

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				update();
				break;
			case 2:
				closeOneAct(GroupChatActivity.TAG);
				Intent intent = new Intent();
				intent.putExtra("gId", gId);
				intent.putExtra("gName", gName);
				intent.setClass(getContext(), GroupChatActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				closeOneAct(TAG);
				break;
			case 3:
				Toast.makeText(
						getContext(),
						Tools.getStringFromRes(getContext(),
								R.string.net_error2), Toast.LENGTH_SHORT)
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
		setContentView(R.layout.invirefriend_activity);
		init();
		if (ApplicationData.friendList.size() == 0) {
			new GetFriendListTask().execute();
		} else {
			friends_objs.addAll(ApplicationData.friendList);
		}
		friendsListAdapter = new FriendsListAdapter(this);
		group_friend_list.setAdapter(friendsListAdapter);
	}

	void update() {
		friendsListAdapter.notifyDataSetChanged();
	}

	void init() {
		Intent intent = getIntent();
		gId = intent.getStringExtra("gId");
		gName = intent.getStringExtra("gName");
		group_friend_list = (ListView) findViewById(R.id.group_friend_list);
		title_card = (TextView) findViewById(R.id.title_card);
		back = (ImageButton) findViewById(R.id.back);
		friends = (ImageButton) findViewById(R.id.friends);
		title_card.setText("邀请好友");
		inbirefriend_button = (Button) findViewById(R.id.inbirefriend_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
				closeOneAct(GroupChatActivity.TAG);
				if (GroupListActivity.mInstance != null)
					GroupListActivity.mInstance.updateGroupInfo();
			}
		});
		friends.setVisibility(View.INVISIBLE);

		inbirefriend_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Jupsh_Blend();

			}
		});
	}

	void Jupsh_Blend() {

		List<Friend> newFriends_objs = new ArrayList<Friend>();

		// TODO Auto-generated method stub
		invitaString = "";
		for (int i = 0; i < friends_objs.size(); i++) {
			if (friends_objs.get(i).isChecked) {
				if ("".equals(invitaString)) {
					invitaString = friends_objs.get(i).getUid();
				} else {
					invitaString = invitaString + ","
							+ friends_objs.get(i).getUid();
				}
				newFriends_objs.add(friends_objs.get(i));
			}
		}

		if (invitaString.split(",").length > 10) {
			MyToast.getToast().showToast(getContext(), "一次只能邀请十位好友");
			return;
		}

		// Set<String> jupshGroup = new HashSet<String>();
		// jupshGroup.add("GROUP_" + gId);
		// JPushInterface.setAliasAndTags(getContext(), "USER_"
		// + LlkcBody.UID_ACCOUNT, jupshGroup);
		if (invitaString == null || "".equals(invitaString)) {
			onBackPressed();
			closeOneAct(GroupChatActivity.TAG);
			Intent intent = new Intent();
			intent.putExtra("gId", gId);
			intent.putExtra("gName", gName);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.setClass(getContext(), GroupChatActivity.class);
			startActivity(intent);
		} else {
			new InviteFriendTask().execute();
		}

	}

	class InviteFriendTask extends AsyncTask<Object, Integer, Boolean> {

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
				return Community.getInstance(getContext()).inviteFriend(gId,
						invitaString);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result)
				handler.sendEmptyMessage(2);
			super.onPostExecute(result);
		}

	}

	class GetFriendListTask extends AsyncTask<Object, Integer, List<Friend>> {

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
		protected List<Friend> doInBackground(Object... params) {
			// TODO Auto-generated method stub
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
			// TODO Auto-generated method stub
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null && result.size() > 0) {
				friends_objs.clear();
				friends_objs.addAll(result);
				handler.sendEmptyMessage(1);
			} else
				handler.sendEmptyMessage(3);
			super.onPostExecute(result);
		}

	}

	private class FriendsListAdapter extends BaseAdapter {
		LayoutInflater layoutInflater;
		FriendsView friendsView;
		FinalBitmap finalBitmap;

		private final class FriendsView {
			ImageView friend_head;
			TextView friend_name;
			TextView friend_sign;
			CheckBox select_friend;
		}

		public FriendsListAdapter(Context context) {
			// TODO Auto-generated constructor stub
			layoutInflater = LayoutInflater.from(context);
			finalBitmap = FinalBitmap.create(context);
			finalBitmap.configLoadingImage(R.drawable.head_default);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return friends_objs.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return friends_objs.get(position);
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
						R.layout.group_seach_friend_item, null);
				friendsView.friend_name = (TextView) convertView
						.findViewById(R.id.friend_name);
				friendsView.friend_head = (ImageView) convertView
						.findViewById(R.id.friend_head);
				friendsView.friend_sign = (TextView) convertView
						.findViewById(R.id.friend_adress);
				friendsView.select_friend = (CheckBox) convertView
						.findViewById(R.id.select_friend);
				convertView.setTag(friendsView);
			} else {
				friendsView = (FriendsView) convertView.getTag();

			}
			final Friend friend = friends_objs.get(position);
			friendsView.select_friend
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							friend.isChecked = isChecked;
						}
					});
			friendsView.select_friend.setChecked(friend.isChecked);
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
