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

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.activity.NearbyGroupActivity;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.Group;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class SearchGroupActivity extends BaseActivity {
	public static final String TAG = "searchgroup";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, SearchGroupActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	TextView title_card, near_TV, recommend_TV;
	EditText seach_friend_key;
	ImageButton back, friends;
	ImageButton seach_friend_button;
	LinearLayout search_group_layout;
	ListView seach_friend_list;
	List<Group> group_Lists = new ArrayList<Group>();
	GroupListAdapter groupListAdapter;
	String key;
	ProgressDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.seachfriends_activity);
		init();
		new RecommendGroupTask().execute();
	}

	void init() {
		title_card = (TextView) findViewById(R.id.title_card);
		back = (ImageButton) findViewById(R.id.back);
		seach_friend_key = (EditText) findViewById(R.id.seach_friend_key);
		friends = (ImageButton) findViewById(R.id.friends);
		friends.setVisibility(View.GONE);
		search_group_layout = (LinearLayout) findViewById(R.id.search_friend_linearlayout);
		near_TV = (TextView) findViewById(R.id.seach_friend_near_tv);
		near_TV.setText("附近的工友帮");
		recommend_TV = (TextView) findViewById(R.id.seach_friend_recommend_tv);
		recommend_TV.setText("推荐工友帮");
		// 附近工友帮
		findViewById(R.id.seach_friend_near_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						NearbyGroupActivity.launch(getContext(),
								getIntent());
					}
				});
		seach_friend_list = (ListView) findViewById(R.id.seach_friend_list);
		seach_friend_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("group", group_Lists.get(arg2));
				intent.putExtra("souse", "search");
				intent.setClass(getContext(),
						GroupDetailActivity.class);

				startActivity(intent);
			}
		});
		groupListAdapter = new GroupListAdapter(getContext());
		seach_friend_list.setAdapter(groupListAdapter);

		seach_friend_button = (ImageButton) findViewById(R.id.seach_friend_button);
		seach_friend_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				key = seach_friend_key.getText().toString().trim();
				if (key == null || key.equals("")) {
					MyToast.getToast().showToast(SearchGroupActivity.this,
							"您尚未填写搜索关键字!");
					return;
				}
				if (LLKCApplication.getInstance().isOpenNetwork())
					new SearchGroupTask(key).execute();
				else
					MyToast.getToast().showToast(SearchGroupActivity.this,
							"无法链接到网络，请检查网络配置");
			}
		});
		title_card.setText(Tools.getStringFromRes(getContext(),
				R.string.search_work));
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

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

	private class GroupListAdapter extends BaseAdapter {
		LayoutInflater layoutInflater;
		GroupListView groupListView;
		FinalBitmap finalBitmap;

		public final class GroupListView {
			TextView gname;
			TextView gnum;
			TextView gtype;
			ImageView g_peop_pic;
			ImageView gpic;
			ImageButton g_add;

		}

		private GroupListAdapter(Context context) {
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
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
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
				groupListView.g_add = (ImageButton) arg1
						.findViewById(R.id.g_add);
				arg1.setTag(groupListView);
			} else {
				groupListView = (GroupListView) arg1.getTag();
			}

			final Group group = group_Lists.get(arg0);
			if (Tools.isUrl(group.getgHead()))
				finalBitmap.display(groupListView.gpic, group.getgHead());
			else
				groupListView.gpic.setImageResource(R.drawable.group_head);
			groupListView.gname.setText(group.gName);
			groupListView.gnum.setText(group.gMembers
					+ Tools.getStringFromRes(getContext(),
							R.string.person));
			groupListView.g_add.setVisibility(View.VISIBLE);
			groupListView.g_add.setFocusable(false);
			groupListView.g_add.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new AddGroupTask(group.gId).execute();
				}
			});

			return arg1;
		}

	}

	class SearchGroupTask extends AsyncTask<Object, Integer, List<Group>> {
		String key;

		public SearchGroupTask(String key) {
			this.key = key;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(SearchGroupActivity.this);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(SearchGroupActivity.this,
					R.string.searching));
			dialog.show();
		}

		@Override
		protected List<Group> doInBackground(Object... params) {

			try {
				return Community.getInstance(getContext())
						.searchFGroupList(key);
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
			group_Lists.clear();
			if (result != null && result.size() > 0) {
				group_Lists.addAll(result);
			} else
				MyToast.getToast().showToast(SearchGroupActivity.this,
						"查询不到您所要的群！");
			groupListAdapter.notifyDataSetChanged();
			if (search_group_layout != null)
				search_group_layout.setVisibility(View.GONE);
			super.onPostExecute(result);
		}
	}

	class RecommendGroupTask extends AsyncTask<Object, Integer, List<Group>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(SearchGroupActivity.this);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(SearchGroupActivity.this,
					R.string.searching));
			dialog.show();
		}

		@Override
		protected List<Group> doInBackground(Object... params) {

			try {
				return Community.getInstance(getContext())
						.recommendGroupList();
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
			group_Lists.clear();
			if (result != null && result.size() > 0) {
				group_Lists.addAll(result);
			}
			groupListAdapter.notifyDataSetChanged();
			super.onPostExecute(result);
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
			if(dialog == null)
				dialog = new ProgressDialog(SearchGroupActivity.this);
			dialog.setCancelable(true);
			dialog.setMessage("加入中...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {

			try {
				return Community.getInstance(getContext()).addGroup(
						gId);
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
