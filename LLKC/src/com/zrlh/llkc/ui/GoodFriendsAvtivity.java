package com.zrlh.llkc.ui;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalDb;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.funciton.Friend;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

import com.zrlh.llkc.corporate.base.BaseActivity;

public class GoodFriendsAvtivity extends BaseActivity {
	public static final String TAG = "friends";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, GoodFriendsAvtivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	ImageButton friends;
	TextView title_card;
	LinearLayout seach_friend_rel;
	ListView friend_list;
	static FinalDb db;
	FriendsListAdapter friendsListAdapter;

	ProgressDialog dialog = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				show();
				break;
			case 2:

				break;

			default:
				break;
			}

		}
	};

	public static GoodFriendsAvtivity mInstance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.goodfriends_activity);
		db = FinalDb.create(getContext());
		init();
		mInstance = this;
		new GetFriendListTask(false).execute();
	}

	void init() {

		title_card = (TextView) findViewById(R.id.title_card);

		friends = (ImageButton) findViewById(R.id.friends);
		title_card.setText(Tools.getStringFromRes(getContext(),
				R.string.myfriend));
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		friends.setImageResource(R.drawable.fgroup_add_friend_but);
		friends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getContext(),
						SearchFriendsActivity.class));
			}
		});
		friend_list = (ListView) findViewById(R.id.friend_list);
		friend_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("toObj", ApplicationData.friendList.get(arg2));
				intent.setClass(getContext(),
						FriendsChatActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		closeOneAct(TAG);
		mInstance = null;
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		mInstance = null;
		super.onDestroy();
	}

	void show() {

		// ApplicationData.friendList = db.findAll(Friends_obj.class);
		List<Friend> list = db.findAllByWhere(Friend.class, "account='"
				+ LlkcBody.USER_ACCOUNT + "'");
		if (list != null) {
			ApplicationData.friendList.clear();
			ApplicationData.friendList.addAll(list);
		}
		friendsListAdapter = new FriendsListAdapter(getContext());
		friend_list.setAdapter(friendsListAdapter);

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

	void onNewIntent() {
		List<Friend> list = db.findAllByWhere(Friend.class, "account='"
				+ LlkcBody.USER_ACCOUNT + "'");
		if (list != null) {
			ApplicationData.friendList.clear();
			ApplicationData.friendList.addAll(list);
		}
		friendsListAdapter.notifyDataSetChanged();
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
			friendsView.friend_head.setOnClickListener(new infoClickListener(
					position));
			return convertView;
		}

		class infoClickListener implements OnClickListener {
			private int position;

			infoClickListener(int pos) {
				position = pos;
			}

			@Override
			public void onClick(View v) {
				int vid = v.getId();
				if (vid == friendsView.friend_head.getId()) {
					Intent intent = new Intent();
					intent.putExtra("member",
							ApplicationData.friendList.get(position));
					intent.putExtra("source", "Friend");
					intent.setClass(getContext(),
							UserInfoActivity.class);
					startActivity(intent);
				}
			}
		}
	}

	public void updateFriendsDate() {
		new GetFriendListTask(true).execute();
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
				if(dialog == null)
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
				return Community.getInstance(getContext())
						.getFriendList(LlkcBody.USER_ACCOUNT,
								LlkcBody.PASS_ACCOUNT);
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
				if (db == null)
					db = FinalDb.create(getContext());
				db.deleteByWhere(Friend.class, "account='"
						+ LlkcBody.USER_ACCOUNT + "'");
				ApplicationData.friendList.clear();
				for (Friend friend : result) {
					db.save(friend);
					ApplicationData.friendList.add(friend);
				}
			}
			handler.sendEmptyMessage(1);
			super.onPostExecute(result);
		}
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
