package com.zrlh.llkc.activity;

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
import android.text.TextUtils;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.Group;
import com.zrlh.llkc.ui.GroupDetailActivity;
import com.zrlh.llkc.ui.LLKCApplication;
import com.zrlh.llkc.ui.LLKCApplication.LocationResultCallback;
import com.zrlh.llkc.ui.PullToRefreshListView;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class NearbyGroupActivity extends BaseActivity {
	public static final String TAG = "nearbygroup";

	ImageButton back;
	TextView titleText;
	PullToRefreshListView nearbyGroupList;
	NearbyGroupAdapter adapter;
	List<Group> group_Lists = new ArrayList<Group>();
	int page = 1;
	private View lvNews_footer;
	private TextView lvNews_foot_more;
	private ProgressBar lvNews_foot_progress;
	ProgressDialog dialog = null;

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, NearbyGroupActivity.class);
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
		setContentView(R.layout.neargroup);
		init();
		lat = 0;
		lng = 0;
		LLKCApplication.getInstance().singleLocation(callback);
	}

	LocationResultCallback callback = new LocationResultCallback() {

		@Override
		public void getLocationResult(BDLocation location, String city,
				String district, double latitude, double longitude) {
			lat = location.getLatitude();
			lng = location.getLongitude();
			new NearbyGroupTask(page).execute();
			if (!TextUtils.isEmpty(city))
				((LLKCApplication) getApplication())
						.closeLocationCallBack();
		}
	};

	private void init() {
		back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		titleText = (TextView) findViewById(R.id.title_tv);
		titleText.setText("附近工友帮");
		findViewById(R.id.title_position).setVisibility(View.GONE);

		lvNews_footer = getLayoutInflater().inflate(R.layout.listview_footer,
				null);
		lvNews_foot_more = (TextView) lvNews_footer
				.findViewById(R.id.listview_foot_more);
		lvNews_foot_progress = (ProgressBar) lvNews_footer
				.findViewById(R.id.listview_foot_progress);

		nearbyGroupList = (PullToRefreshListView) findViewById(R.id.near_group_list);
		nearbyGroupList.addFooterView(lvNews_footer, null, false);// 添加底部视图
		// 必须在setAdapter前
		adapter = new NearbyGroupAdapter(getContext());
		nearbyGroupList.setAdapter(adapter);
		nearbyGroupList.setOnScrollListener(new OnScrollListener() {

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
						new NearbyGroupTask(page).execute();

				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
		nearbyGroupList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Group g = (Group) arg0.getAdapter().getItem(arg2);
				intent.putExtra("group", g);
				intent.putExtra("souse", "search");
				intent.setClass(getApplicationContext(),
						GroupDetailActivity.class);
				startActivity(intent);
			}
		});
	}

	class NearbyGroupTask extends AsyncTask<Object, Integer, List<Group>> {
		int page;
		String lats;
		String lngs;

		public NearbyGroupTask(int page) {
			// TODO Auto-generated constructor stub
			this.page = page;
			lats = lat == 0 ? "" : String.valueOf(lat);
			lngs = lng == 0 ? "" : String.valueOf(lng);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(NearbyGroupActivity.this);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(NearbyGroupActivity.this,
					R.string.load_ing));
			if (page < 2)
				dialog.show();
		}

		@Override
		protected List<Group> doInBackground(Object... params) {
			if (!LLKCApplication.getInstance().isOpenNetwork())
				return null;
			try {
				return Community.getInstance(NearbyGroupActivity.this)
						.nearbyGroupList(String.valueOf(page), lats, lngs);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Group> result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			// group_Lists.clear();
			if (result != null && result.size() > 0) {
				group_Lists.addAll(result);
			}
			lvNews_foot_more.setText(R.string.noinfor);
			lvNews_foot_progress.setVisibility(View.INVISIBLE);
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}

	class NearbyGroupAdapter extends BaseAdapter {

		LayoutInflater layoutInflater;
		NearbyGroupView nearbyGroupView;
		FinalBitmap finalBitmap;

		public final class NearbyGroupView {
			ImageView head;
			TextView gname;
			TextView gnum;
			TextView gdistance;
			TextView gaddress;
			ImageButton gadd;

		}

		private NearbyGroupAdapter(Context context) {
			layoutInflater = LayoutInflater.from(context);
			finalBitmap = FinalBitmap.create(context);
			finalBitmap.configLoadingImage(R.drawable.group_head);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return group_Lists.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return group_Lists.get(arg0);
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
				nearbyGroupView = new NearbyGroupView();
				arg1 = layoutInflater.inflate(R.layout.neargroup_item, null);
				nearbyGroupView.head = (ImageView) arg1
						.findViewById(R.id.neargroup_item_head);
				nearbyGroupView.gname = (TextView) arg1
						.findViewById(R.id.neargroup_item_gname);
				nearbyGroupView.gnum = (TextView) arg1
						.findViewById(R.id.neargroup_item_gnum);
				nearbyGroupView.gdistance = (TextView) arg1
						.findViewById(R.id.neargroup_item_gdistance);
				nearbyGroupView.gaddress = (TextView) arg1
						.findViewById(R.id.neargroup_item_gaddress);
				nearbyGroupView.gadd = (ImageButton) arg1
						.findViewById(R.id.neargroup_item_gadd);
				arg1.setTag(nearbyGroupView);
			} else {
				nearbyGroupView = (NearbyGroupView) arg1.getTag();
			}
			// finalBitmap.display(nearbyGroupView.head, group_Lists.get(arg0)
			// .getgHead());
			String head = group_Lists.get(arg0).getgHead();
			if (Tools.isUrl(head))
				finalBitmap.display(nearbyGroupView.head, head);
			else
				nearbyGroupView.head.setImageResource(R.drawable.group_head);

			nearbyGroupView.gname.setText(group_Lists.get(arg0).gName);
			nearbyGroupView.gnum.setText(group_Lists.get(arg0).gMembers
					+ Tools.getStringFromRes(getApplicationContext(),
							R.string.person));
			nearbyGroupView.gdistance.setText("距离:"
					+ group_Lists.get(arg0).gDistance);
			final int position = arg0;
			nearbyGroupView.gadd.setFocusable(false);
			nearbyGroupView.gadd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new AddGroupTask(group_Lists.get(position).gId).execute();
				}
			});
			return arg1;
		}

	}

	class AddGroupTask extends AsyncTask<Object, Integer, Boolean> {
		private String gId;

		public AddGroupTask(String gId) {
			// TODO Auto-generated constructor stub
			this.gId = gId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage("加入中...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {

			try {
				return Community.getInstance(getContext()).addGroup(gId);
			} catch (JSONException e) {

			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			super.onPostExecute(result);
		}

	}

	private double lat = 0;
	private double lng = 0;

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
