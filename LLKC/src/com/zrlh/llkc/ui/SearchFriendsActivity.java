package com.zrlh.llkc.ui;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONException;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.activity.NearbyFriendActivity;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.Friend;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class SearchFriendsActivity extends BaseActivity {
	public static final String TAG = "searchfriend";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, SearchFriendsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	EditText seach_friend_key;
	ImageButton seach_friend_button;
	ListView seach_friend_list;
	ImageButton back, friends;
	LinearLayout search_group_layout;
	TextView title_card, near_TV, recommend_TV;
	List<Friend> friends_objsList = new ArrayList<Friend>();
	String seachFriendKey;
	String statString;
	String msgString;
	String INVITE_FRIEND = "3";
	// String AGREE_INVITE = "4";
	// String REJECT_INVITE = "5";
	String INVITE_FRIEND_ID;

	FriendsAdapter friendsAdapter;
	ProgressDialog dialog = null;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				update();
				break;
			case 2:
				Toast.makeText(getContext(), msgString,
						Toast.LENGTH_SHORT).show();
				break;
			case 3:

				Toast.makeText(
						getContext(),
						Tools.getStringFromRes(getContext(),
								R.string.net_error), Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.seachfriends_activity);
		init();
		new RecommendFriendTask().execute();
	}

	void init() {
		title_card = (TextView) findViewById(R.id.title_card);
		back = (ImageButton) findViewById(R.id.back);
		friends = (ImageButton) findViewById(R.id.friends);
		title_card.setText(Tools.getStringFromRes(getContext(),
				R.string.search_add_friend));
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		search_group_layout = (LinearLayout) findViewById(R.id.search_friend_linearlayout);
		near_TV = (TextView) findViewById(R.id.seach_friend_near_tv);
		near_TV.setText("附近的人");
		recommend_TV = (TextView) findViewById(R.id.seach_friend_recommend_tv);
		recommend_TV.setText("推荐好友");
		// 附近的人
		findViewById(R.id.seach_friend_near_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						NearbyFriendActivity.launch(getContext(),
								getIntent());
					}
				});
		friends.setVisibility(View.INVISIBLE);
		seach_friend_key = (EditText) findViewById(R.id.seach_friend_key);
		seach_friend_button = (ImageButton) findViewById(R.id.seach_friend_button);
		seach_friend_list = (ListView) findViewById(R.id.seach_friend_list);
		// seach_friend_list.setItemsCanFocus(false);
		seach_friend_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				Friend obj = (Friend) arg0.getAdapter().getItem(arg2);
				intent.putExtra("member", obj);
				intent.putExtra("source", "Search");
				intent.setClass(SearchFriendsActivity.this,
						UserInfoActivity.class);
				startActivity(intent);
			}

		});

		friendsAdapter = new FriendsAdapter(this, friends_objsList);
		seach_friend_list.setAdapter(friendsAdapter);
		seach_friend_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				seachFriendKey = seach_friend_key.getText().toString().trim();
				if (seachFriendKey == null || seachFriendKey.equals("")) {
					MyToast.getToast().showToast(SearchFriendsActivity.this,
							"您尚未填写搜索关键字!");
					return;
				}
				new SearchFriendTask().execute();
			}
		});

	}

	void update() {
		if (search_group_layout != null)
			search_group_layout.setVisibility(View.GONE);
		friendsAdapter.notifyDataSetChanged();
	}
	
	class SearchFriendTask extends AsyncTask<Object, Integer, List<Friend>>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.load_ing));
			dialog.show();
		}
		
		@Override
		protected List<Friend> doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getApplicationContext())
						.searchFriendList(LlkcBody.USER_ACCOUNT,
								LlkcBody.PASS_ACCOUNT, seachFriendKey);
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
			if(result != null){
				friends_objsList.clear();
				for (Friend friend : result) {					
					friends_objsList.add(friend);
				}
				handler.sendEmptyMessage(1);
			}
			super.onPostExecute(result);
		}
		
	}
	
	class AddFriendTask extends AsyncTask<Object, Integer, Integer>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage("申请中..");
			dialog.show();
		}
		
		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext()).OperFriend(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT, INVITE_FRIEND,
						INVITE_FRIEND_ID);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return -1;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if(result == -1)
				handler.sendEmptyMessage(3);
			super.onPostExecute(result);
		}
		
	}

	class RecommendFriendTask extends AsyncTask<Object, Integer, List<Friend>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(SearchFriendsActivity.this);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(
					SearchFriendsActivity.this, R.string.searching));
			dialog.show();
		}

		@Override
		protected List<Friend> doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext())
						.recommendFriendList();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Friend> result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			friends_objsList.clear();
			if (result != null && result.size() > 0) {
				friends_objsList.addAll(result);
			}
			friendsAdapter.notifyDataSetChanged();
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

	private class FriendsAdapter extends BaseAdapter {
		List<Friend> friends_objs;
		LayoutInflater layoutInflater;
		FriendsView friendsView;
		FinalBitmap finalBitmap;

		public final class FriendsView {
			ImageView friend_head;
			TextView friend_name;
			TextView friend_adress;
			ImageButton add_friend;
		}

		private FriendsAdapter(Context context, List<Friend> friends_objs) {
			this.friends_objs = friends_objs;
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
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return friends_objs.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (arg1 == null) {
				friendsView = new FriendsView();
				arg1 = layoutInflater.inflate(R.layout.seach_friend_list_item,
						null);
				friendsView.friend_name = (TextView) arg1
						.findViewById(R.id.friend_name);
				friendsView.friend_adress = (TextView) arg1
						.findViewById(R.id.friend_adress);
				friendsView.add_friend = (ImageButton) arg1
						.findViewById(R.id.add_friend);
				friendsView.friend_head = (ImageView) arg1
						.findViewById(R.id.friend_head);
				arg1.setTag(friendsView);
			} else {
				friendsView = (FriendsView) arg1.getTag();

			}
			final Friend f = friends_objs.get(arg0);
			friendsView.friend_name.setText(f.getUname());
			friendsView.add_friend.setVisibility(View.VISIBLE);
			friendsView.add_friend.setFocusable(false);
			friendsView.add_friend.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					INVITE_FRIEND_ID = f.getUid();
					new AddFriendTask().execute();
				}
			});
			if (Tools.isUrl(f.getHead()))
				finalBitmap.display(friendsView.friend_head, f.getHead());
			else
				friendsView.friend_head
						.setImageResource(R.drawable.head_default);
			friendsView.friend_adress.setText(f.getSign());
			return arg1;
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
