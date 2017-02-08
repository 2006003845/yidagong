package com.zrlh.llkc.ui;

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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.activity.PersonalDynamicActivity;
import com.zrlh.llkc.activity.PreviewPicActivity;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.Friend;
import com.zrlh.llkc.funciton.HeadWall;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.TimeUtil;
import com.zzl.zl_app.util.Tools;

public class UserInfoActivity extends BaseActivity {
	public static final String TAG = "userinfo";
	ImageButton back, friends;
	ImageView userinfo_sex;
	TextView userinfo_name;
	TextView title_card;
	TextView userinfo_id;
	TextView userinfo_adress;
	TextView userinfo_sign;
	TextView dynamic_size;
	TextView dynamic_content;
	TextView dynamic_date;
	ImageView dynamic_img;
	Button remove_friends;
	Button add_friend;
	LinearLayout remove_send;
	Button send_news_friends;
	GridView userinfo_gridview;
	UserInfoHeadAdapter adapter;
	List<HeadWall> headWall;
	Friend member;
	String uID;
	String INVITE_FRIEND_ID = "2";
	String statString;
	String msgString;
	// String source;
	ProgressDialog dialog = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(getContext(), msgString, Toast.LENGTH_SHORT)
						.show();
				onBackPressed();
				break;
			case 2:
				Toast.makeText(
						getContext(),
						Tools.getStringFromRes(getContext(), R.string.net_error),
						Toast.LENGTH_SHORT).show();

				break;
			case 3:
				break;
			default:
				break;
			}

		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.userinfo_activity);
		Intent intent = getIntent();

		member = (Friend) intent.getSerializableExtra("member");
		if (member == null)
			uID = intent.getStringExtra("uid");
		else
			uID = member.getUid();
		init();
		show();
		new GetUserInfoTask(uID).execute();
	}

	void init() {
		back = (ImageButton) findViewById(R.id.back);
		friends = (ImageButton) findViewById(R.id.friends);
		friends.setVisibility(View.INVISIBLE);

		title_card = (TextView) findViewById(R.id.title_card);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		title_card.setText(Tools.getStringFromRes(getContext(),
				R.string.userinfo));
		// 个人动态
		this.findViewById(R.id.userinfo_dynamic).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.putExtra("type", "userInfo");
						intent.putExtra("member", member);
						PersonalDynamicActivity.launch(getContext(), intent);
					}
				});
		userinfo_sex = (ImageView) findViewById(R.id.userinfo_sex);
		userinfo_name = (TextView) findViewById(R.id.userinfo_name);
		title_card = (TextView) findViewById(R.id.title_card);
		userinfo_id = (TextView) findViewById(R.id.userinfo_id);
		userinfo_adress = (TextView) findViewById(R.id.userinfo_adress);
		userinfo_sign = (TextView) findViewById(R.id.userinfo_sign);
		remove_friends = (Button) findViewById(R.id.remove_friends);
		add_friend = (Button) findViewById(R.id.add_friend);
		send_news_friends = (Button) findViewById(R.id.send_news_friends);
		remove_send = (LinearLayout) findViewById(R.id.userinfo_layout);
		dynamic_img = (ImageView) findViewById(R.id.userinfo_dynamic_image);
		dynamic_content = (TextView) findViewById(R.id.userinfo_dynamic_content);
		dynamic_date = (TextView) findViewById(R.id.userinfo_dynamic_date);
		dynamic_size = (TextView) findViewById(R.id.userinfo_dynamic_num);
		userinfo_gridview = (GridView) findViewById(R.id.userinfo_gridview);

		// if ("Friend".equals(source)) {
		// if (LlkcBody.Friend_relation(getContext(),
		// member.getUid())) {
		// remove_send.setVisibility(View.VISIBLE);
		// add_friend.setVisibility(View.GONE);
		// } else {
		// remove_send.setVisibility(View.GONE);
		// add_friend.setVisibility(View.VISIBLE);
		// }
		// } else if ("Group".equals(source)) {
		if (member.getUid().equals(LlkcBody.UID_ACCOUNT)) {
			remove_send.setVisibility(View.GONE);
			send_news_friends.setVisibility(View.GONE);
		} else if (LlkcBody.Friend_relation(getContext(), member.getUid())) {
			remove_send.setVisibility(View.VISIBLE);
			add_friend.setVisibility(View.GONE);
		} else {
			remove_send.setVisibility(View.GONE);
			add_friend.setVisibility(View.VISIBLE);
		}

		// } else {
		// remove_friends.setVisibility(View.GONE);
		// add_friend.setVisibility(View.GONE);
		// send_news_friends.setVisibility(View.GONE);
		// }

		remove_friends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showMsgDialog("1", R.layout.layout_prompt, getContext(),
						"删除好友", "是否和" + member.getUname() + "解除好友关系");
			}
		});
		add_friend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				INVITE_FRIEND_ID = "3";
				new OperFriendTask().execute();
			}
		});
		send_news_friends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
				Intent intent = new Intent();
				intent.putExtra("toObj", member);
				intent.setClass(getContext(), FriendsChatActivity.class);
				startActivity(intent);
			}
		});
	}

	void show() {
		if (member == null)
			return;
		headWall = member.getHeadWall();
		adapter = new UserInfoHeadAdapter(getContext());
		userinfo_gridview.setAdapter(adapter);

		if ("0".equals(member.getSex())) {
			userinfo_sex.setImageResource(R.drawable.all_icon_girl);
		} else {
			userinfo_sex.setImageResource(R.drawable.all_icon_boy);
		}
		userinfo_name.setText(member.getUname());
		userinfo_id.setText("ID:" + member.getUid());
		userinfo_adress.setText(member.getLocal());
		userinfo_sign.setText(member.getSign());
		dynamic_img.setVisibility(View.VISIBLE);
		if (member.getDynamicSmallImg() != null
				&& !member.getDynamicSmallImg().equals(""))
			ImageCache.getInstance().loadImg(member.getDynamicSmallImg(),
					dynamic_img, R.drawable.default_img);
		else
			dynamic_img.setVisibility(View.INVISIBLE);
		dynamic_content.setText(member.getDynamicContent());
		if (member.getDynamicSize() != null) {
			if ("0".equals(member.getDynamicSize()))
				dynamic_size.setText("");
			else
				dynamic_size.setText(member.getDynamicSize());
		}
		if (member.getDynamicTime() != null) {
			if ("0".equals(member.getDynamicTime()))
				dynamic_date.setText("");
			else
				dynamic_date.setText(TimeUtil.getTimeStr2(
						member.getDynamicTime(), "yy-MM-dd HH:mm:ss"));
		}
	}

	class GetUserInfoTask extends AsyncTask<Object, Integer, Friend> {
		String uId;

		public GetUserInfoTask(String uId) {
			this.uId = uId;
		}

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
		protected Friend doInBackground(Object... params) {

			try {
				return Community.getInstance(getContext()).queryUserInfo(uId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Friend result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null) {
				member = result;
				show();
			}
			super.onPostExecute(result);
		}
	}

	class OperFriendTask extends AsyncTask<Object, Integer, Integer> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage("申请中...");
			dialog.show();
		}

		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext()).OperFriend(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT,
						INVITE_FRIEND_ID, member.getUid());
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
			if (result == -1)
				handler.sendEmptyMessage(2);
			else if (result == 0)
				closeOneAct(TAG);
			super.onPostExecute(result);
		}

	}

	class UserInfoHeadAdapter extends BaseAdapter {

		LayoutInflater layoutInflater;
		UserInfoHeadView userInfoHeadView;
		FinalBitmap finalBitmap;
		HeadWall headW;

		public final class UserInfoHeadView {
			ImageView head;
		}

		private UserInfoHeadAdapter(Context context) {
			layoutInflater = LayoutInflater.from(context);
			finalBitmap = FinalBitmap.create(context);
			finalBitmap.configLoadingImage(R.drawable.head_default);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			int len = headWall == null ? 0 : headWall.size();
			if (len == 0) {
				len = 1;
			}
			return len;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return headWall.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				userInfoHeadView = new UserInfoHeadView();
				convertView = layoutInflater.inflate(
						R.layout.per_gridview_item, null);
				userInfoHeadView.head = (ImageView) convertView
						.findViewById(R.id.per_gridview_item_photo);
				convertView.setTag(userInfoHeadView);
			} else
				userInfoHeadView = (UserInfoHeadView) convertView.getTag();
			if (headWall != null && headWall.size() != 0) {
				headW = headWall.get(position);
				if (Tools.isUrl(headW.getHeadSimg()))
					finalBitmap.display(userInfoHeadView.head,
							headW.getHeadSimg());
				else
					userInfoHeadView.head
							.setImageResource(R.drawable.head_default);
			} else {
				userInfoHeadView.head.setImageResource(R.drawable.head_default);
			}

			userInfoHeadView.head.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (headWall != null && headWall.size() != 0) {
						headW = headWall.get(position);
						Intent intent = new Intent();
						Bundle b = new Bundle();
						b.putString("picUrl", headW.getHeadBimg());
						intent.putExtras(b);
						PreviewPicActivity.launch(getContext(), intent);
					}

				}
			});
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
		switch (layoutId) {
		case R.layout.layout_prompt:
			TextView contTV = (TextView) dialog
					.findViewById(R.id.prompt_tv_cont);
			contTV.setText(content);
			break;
		default:
			break;

		}
	}

	@Override
	public void setDialogTitle(AlertDialog dialog, int layoutId, String title,
			String id) {
		// TODO Auto-generated method stub
		switch (layoutId) {
		case R.layout.layout_prompt:
			TextView titleTV = (TextView) dialog
					.findViewById(R.id.prompt_tv_title);
			titleTV.setText(title);
			break;
		}
	}

	@Override
	public OnClickListener setPositiveClickListener(final AlertDialog dialog,
			int layoutId, String id) {
		// TODO Auto-generated method stub
		switch (layoutId) {
		case R.layout.layout_prompt:
			Button sure = (Button) dialog.findViewById(R.id.prompt_btn_ok);
			sure.setText(R.string.sure);
			sure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					INVITE_FRIEND_ID = "2";
					new OperFriendTask().execute();
				}
			});
			break;
		}
		return null;
	}

	@Override
	public OnClickListener setNegativeClickListener(final AlertDialog dialog,
			int layoutId, String id) {
		// TODO Auto-generated method stub
		Button cancel = (Button) dialog.findViewById(R.id.prompt_btn_cancel);
		cancel.setVisibility(View.VISIBLE);
		cancel.setText(R.string.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		return null;
	}
}
