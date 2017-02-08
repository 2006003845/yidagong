package com.zrlh.llkc.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.Friend;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.LLKCApplication;
import com.zrlh.llkc.ui.LLKCApplication.LocationResultCallback;
import com.zrlh.llkc.ui.PullToRefreshListView;
import com.zrlh.llkc.ui.UserInfoActivity;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class NearbyFriendActivity extends BaseActivity {
	public static final String TAG = "nearbyfriend";

	String INVITE_FRIEND_ID;
	ImageButton back;
	TextView titleText;
	PullToRefreshListView nearbyFriendList;
	NearbyFriendAdapter adapter;
	List<Friend> friend_Lists = new ArrayList<Friend>();
	View view;
	private PopupWindow popWin;
	String Sex;
	int page = 1;
	private View lvNews_footer;
	private TextView lvNews_foot_more;
	private ProgressBar lvNews_foot_progress;
	private boolean isClear;

	private double lat = 0;
	private double lng = 0;
	ProgressDialog dialog = null;

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, NearbyFriendActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.nearfriend);
		Sex = "2";
		init();
		lat = 0;
		lng = 0;
		LLKCApplication.getInstance().singleLocation(callback);
	}

	private void init() {
		back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		titleText = (TextView) findViewById(R.id.title_tv);
		titleText.setText("附近的人");

		view = getLayoutInflater().inflate(R.layout.near_friend_popwindow_item,
				null);

		lvNews_footer = getLayoutInflater().inflate(R.layout.listview_footer,
				null);
		lvNews_foot_more = (TextView) lvNews_footer
				.findViewById(R.id.listview_foot_more);
		lvNews_foot_progress = (ProgressBar) lvNews_footer
				.findViewById(R.id.listview_foot_progress);

		findViewById(R.id.title_layout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						showPopWindow(v);
					}
				});

		nearbyFriendList = (PullToRefreshListView) findViewById(R.id.near_friend_list);
		nearbyFriendList.addFooterView(lvNews_footer, null, false);// 添加底部视图
		// 必须在setAdapter前
		adapter = new NearbyFriendAdapter(getContext(), friend_Lists);
		nearbyFriendList.setAdapter(adapter);
		nearbyFriendList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(lvNews_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				if (scrollEnd) {
					lvNews_footer.setVisibility(View.VISIBLE);
					lvNews_foot_more.setText(R.string.load_ing);
					lvNews_foot_progress.setVisibility(View.VISIBLE);

					page = page + 1;
					if (lat == 0 && lng == 0) {
						LLKCApplication.getInstance().singleLocation(callback);
					} else
						new NearbyFriendTask(Sex, page).execute();

				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
		nearbyFriendList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				Friend obj = (Friend) arg0.getAdapter().getItem(arg2);
				intent.putExtra("member", obj);
				intent.putExtra("source", "Search");
				intent.setClass(NearbyFriendActivity.this,
						UserInfoActivity.class);
				startActivity(intent);
			}

		});

	}

	LocationResultCallback callback = new LocationResultCallback() {

		@Override
		public void getLocationResult(BDLocation location, String city,
				String district, double latitude, double longitude) {
			lat = location.getLatitude();
			lng = location.getLongitude();
			new NearbyFriendTask(Sex, page).execute();
			if (!TextUtils.isEmpty(city))
				((LLKCApplication) getApplication()).closeLocationCallBack();
		}
	};

	private void showPopWindow(View parent) {
		if (null == popWin) {
			popWin = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		}
		popWin.setFocusable(true);
		popWin.setBackgroundDrawable(new BitmapDrawable());
		popWin.setOutsideTouchable(true);
		if (popWin.isShowing()) {// 如果当前正在显示，则将被处理
			popWin.dismiss();
		} else {
			// 得到parent在屏幕中的坐标
			int[] pos = new int[2];
			parent.getLocationOnScreen(pos);
			int offsetY = pos[1] + parent.getHeight();
			popWin.showAtLocation(parent, Gravity.TOP
					| Gravity.CENTER_HORIZONTAL, 0, offsetY);
		}

		final ImageView allperson = (ImageView) view
				.findViewById(R.id.pop_allperson_selector);
		final ImageView man = (ImageView) view
				.findViewById(R.id.pop_man_selector);
		final ImageView woman = (ImageView) view
				.findViewById(R.id.pop_woman_selector);
		if (Sex.equals("2")) {
			allperson.setVisibility(View.VISIBLE);
			man.setVisibility(View.INVISIBLE);
			woman.setVisibility(View.INVISIBLE);
		} else if (Sex.equals("1")) {
			allperson.setVisibility(View.INVISIBLE);
			man.setVisibility(View.VISIBLE);
			woman.setVisibility(View.INVISIBLE);
		} else {
			allperson.setVisibility(View.INVISIBLE);
			man.setVisibility(View.INVISIBLE);
			woman.setVisibility(View.VISIBLE);
		}

		view.findViewById(R.id.pop_allperson_layout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (Sex.equals("2")) {
							popWin.dismiss();
							return;
						}
						isClear = true;
						Sex = "2";
						page = 1;
						titleText.setText("附近的人");
						allperson.setVisibility(View.VISIBLE);
						man.setVisibility(View.INVISIBLE);
						woman.setVisibility(View.INVISIBLE);
						popWin.dismiss();
						if (lat == 0 && lng == 0) {
							LLKCApplication.getInstance().singleLocation(
									callback);
						} else
							new NearbyFriendTask(Sex, page).execute();
					}
				});
		view.findViewById(R.id.pop_man_layout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (Sex.equals("1")) {
							popWin.dismiss();
							return;
						}
						isClear = true;
						Sex = "1";
						page = 1;
						titleText.setText("附近男生");
						allperson.setVisibility(View.INVISIBLE);
						man.setVisibility(View.VISIBLE);
						woman.setVisibility(View.INVISIBLE);
						popWin.dismiss();
						if (lat == 0 && lng == 0) {
							LLKCApplication.getInstance().singleLocation(
									callback);
						} else
							new NearbyFriendTask(Sex, page).execute();
					}
				});
		view.findViewById(R.id.pop_woman_layout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (Sex.equals("0")) {
							popWin.dismiss();
							return;
						}
						isClear = true;
						Sex = "0";
						page = 1;
						titleText.setText("附近女生");
						allperson.setVisibility(View.INVISIBLE);
						man.setVisibility(View.INVISIBLE);
						woman.setVisibility(View.VISIBLE);
						popWin.dismiss();
						if (lat == 0 && lng == 0) {
							LLKCApplication.getInstance().singleLocation(
									callback);
						} else
							new NearbyFriendTask(Sex, page).execute();
					}
				});
	}

	class NearbyFriendTask extends AsyncTask<Object, Integer, List<Friend>> {
		String sex;
		int page;
		String lats;
		String lngs;

		public NearbyFriendTask(String sex, int page) {
			// TODO Auto-generated constructor stub
			this.sex = sex;
			this.page = page;
			lats = lat == 0 ? "" : String.valueOf(lat);
			lngs = lng == 0 ? "" : String.valueOf(lng);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.load_ing));
			if (page < 2)
				dialog.show();
		}

		@Override
		protected List<Friend> doInBackground(Object... params) {
			if (!LLKCApplication.getInstance().isOpenNetwork())
				return null;
			try {
				return Community
						.getInstance(getApplicationContext())
						.nearbyFriendList(sex, String.valueOf(page), lats, lngs);
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
			if (isClear) {
				friend_Lists.clear();
				isClear = false;
				nearbyFriendList.setSelection(0);
				adapter.notifyDataSetChanged();
			}
			if (result != null && result.size() > 0) {
				friend_Lists.addAll(result);
			}
			lvNews_foot_more.setText(R.string.noinfor);
			lvNews_foot_progress.setVisibility(View.INVISIBLE);
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}

	private class NearbyFriendAdapter extends BaseAdapter {
		List<Friend> friends_objs;
		LayoutInflater layoutInflater;
		NearFriendView nearFriendView;
		FinalBitmap finalBitmap;

		public final class NearFriendView {
			ImageView friend_head;
			TextView friend_name;
			TextView friend_age;
			TextView friend_distance;
			ImageView friend_sex;
			ImageButton add_friend;
		}

		private NearbyFriendAdapter(Context context, List<Friend> friends_objs) {
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
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (arg1 == null) {
				nearFriendView = new NearFriendView();
				arg1 = layoutInflater.inflate(R.layout.nearfriend_item, null);
				nearFriendView.friend_name = (TextView) arg1
						.findViewById(R.id.nearfriend_item_gname);
				nearFriendView.friend_age = (TextView) arg1
						.findViewById(R.id.nearfriend_item_gage);
				nearFriendView.friend_distance = (TextView) arg1
						.findViewById(R.id.nearfriend_item_gdistance);
				nearFriendView.friend_sex = (ImageView) arg1
						.findViewById(R.id.nearfriend_item_gsex);
				nearFriendView.add_friend = (ImageButton) arg1
						.findViewById(R.id.nearfriend_item_gadd);
				nearFriendView.friend_head = (ImageView) arg1
						.findViewById(R.id.nearfriend_item_head);
				arg1.setTag(nearFriendView);
			} else {
				nearFriendView = (NearFriendView) arg1.getTag();

			}
			final Friend friends = friends_objs.get(arg0);
			nearFriendView.friend_name.setText(friends.getUname());
			nearFriendView.friend_sex.setVisibility(View.VISIBLE);
			if (friends.getSex().equals("0")) {
				nearFriendView.friend_sex
						.setImageResource(R.drawable.all_icon_girl);
			} else {
				nearFriendView.friend_sex
						.setImageResource(R.drawable.all_icon_boy);
			}
			nearFriendView.friend_age.setVisibility(View.VISIBLE);
			if (friends.getAge(friends.getBirth()).equals("0")) {
				nearFriendView.friend_age.setVisibility(View.INVISIBLE);
			} else {
				nearFriendView.friend_age.setText(friends.getAge(friends
						.getBirth()));
			}
			nearFriendView.friend_distance.setText("距离:"
					+ friends.getDistance());
			nearFriendView.add_friend.setFocusable(false);
			nearFriendView.add_friend.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					INVITE_FRIEND_ID = friends.getUid();
					new AddFriendTask(INVITE_FRIEND_ID).execute();

				}
			});
			String head = friends.getHead();
			if (Tools.isUrl(head))
				finalBitmap.display(nearFriendView.friend_head, head);
			else
				nearFriendView.friend_head
						.setImageResource(R.drawable.head_default);

			return arg1;
		}

	}

	class AddFriendTask extends AsyncTask<Object, Integer, Integer> {
		private String uId;

		public AddFriendTask(String uId) {
			// TODO Auto-generated constructor stub
			this.uId = uId;
		}

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

			try {
				return Community.getInstance(getContext()).OperFriend(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT, "3", uId);
			} catch (JSONException e) {

			}
			return -1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
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
