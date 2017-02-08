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
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.Group;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.funciton.Utility;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class GroupListActivity extends BaseActivity {
	public static final String TAG = "grouplist";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, GroupListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	ListView my_grouplist, in_grouplist;
	ImageButton back, friends;
	TextView title_card;
	TextView my_group_text;
	TextView in_group_text;
	FinalDb db;
	View view;
	TextView pop_create_group, pop_seach_group;
	private PopupWindow popWin;
	GroupListAdapter selfGroupListAdapter;
	GroupListAdapter joinGroupListAdapter;

	public static GroupListActivity mInstance;

	ProgressDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.grouplist_activity);
		mInstance = this;
		init();
		db = FinalDb.create(this);
		new GetOwnGroupListTask(false).execute();
	}

	void init() {
		title_card = (TextView) findViewById(R.id.title_card);
		back = (ImageButton) findViewById(R.id.back);
		friends = (ImageButton) findViewById(R.id.friends);
		my_group_text = (TextView) findViewById(R.id.my_group_text);
		in_group_text = (TextView) findViewById(R.id.in_group_text);
		title_card.setText(Tools
				.getStringFromRes(getContext(), R.string.worker));
		view = getLayoutInflater().inflate(R.layout.popwindow_item, null);

		pop_create_group = (TextView) view.findViewById(R.id.pop_create_group);
		pop_seach_group = (TextView) view.findViewById(R.id.pop_seach_group);
		pop_create_group.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popWin.dismiss();
				startActivity(new Intent(getContext(),
						CreateGroupActivity.class));
			}
		});
		pop_seach_group.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popWin.dismiss();
				// TODO Auto-generated method stub
				startActivity(new Intent(getContext(),
						SearchGroupActivity.class));
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		friends.setImageResource(R.drawable.fgroup_add_group_but);
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
		my_grouplist = (ListView) findViewById(R.id.my_grouplist);
		in_grouplist = (ListView) findViewById(R.id.in_grouplist);
		my_grouplist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("gId",
						ApplicationData.selfGroupList.get(arg2).gId);
				intent.putExtra("gName",
						ApplicationData.selfGroupList.get(arg2).gName);
				intent.putExtra("gHead",
						ApplicationData.selfGroupList.get(arg2).gHead);
				intent.putExtra("source", "list");
				intent.setClass(getContext(), GroupChatActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}
		});

		in_grouplist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("gId",
						ApplicationData.joinGroupList.get(arg2).gId);
				intent.putExtra("gName",
						ApplicationData.joinGroupList.get(arg2).gName);
				intent.putExtra("gHead",
						ApplicationData.joinGroupList.get(arg2).gHead);
				intent.setClass(getContext(), GroupChatActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}
		});

	}

	void onNewIntent() {
		// List<FGroup> group_Lists = db
		// .findAll(FGroup.class);
		List<Group> group_Lists = db.findAllByWhere(Group.class, "account='"
				+ LlkcBody.USER_ACCOUNT + "'");
		if (group_Lists != null && group_Lists.size() > 0) {
			ApplicationData.allGroupList.clear();
			ApplicationData.allGroupList.addAll(group_Lists);
			for (int j = 0; j < group_Lists.size(); j++) {
				if (LlkcBody.UID_ACCOUNT.equals(group_Lists.get(j).gManagerId)) {
					ApplicationData.selfGroupList.add(group_Lists.get(j));
				} else {
					ApplicationData.joinGroupList.add(group_Lists.get(j));
				}
			}
			selfGroupListAdapter.notifyDataSetChanged();
			joinGroupListAdapter.notifyDataSetChanged();
		}
	}

	void updateView() {
		// 设置Jpush labels

		if (selfGroupListAdapter == null) {
			selfGroupListAdapter = new GroupListAdapter(this,
					ApplicationData.selfGroupList);
			my_grouplist.setAdapter(selfGroupListAdapter);
		}
		selfGroupListAdapter.notifyDataSetChanged();
		if (joinGroupListAdapter == null) {
			joinGroupListAdapter = new GroupListAdapter(this,
					ApplicationData.joinGroupList);
			in_grouplist.setAdapter(joinGroupListAdapter);
		}
		joinGroupListAdapter.notifyDataSetChanged();
		Utility.setListViewHeightBasedOnChildren(my_grouplist);
		Utility.setListViewHeightBasedOnChildren(in_grouplist);
	}

	/**
	 * 设置Lables
	 * 
	 * @param list
	 */
	public void setJpushLabels(List<Group> list) {
		if (list == null)
			return;
		LLKCApplication.getInstance().setJpushLabels(list);
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
					intent.setClass(getApplicationContext(),
							GroupDetailActivity.class);
					startActivity(intent);
				}
			});

			return arg1;
		}
	}

	public void updateGroupInfo() {
		new GetOwnGroupListTask(true).execute();
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

	class GetOwnGroupListTask extends AsyncTask<Object, Integer, List<Group>> {

		public boolean isUpdate = false;

		public GetOwnGroupListTask(boolean isUpdate) {
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
		protected List<Group> doInBackground(Object... params) {

			try {
				return Community.getInstance(getApplicationContext())
						.getOwnFGroupList(LlkcBody.USER_ACCOUNT,
								LlkcBody.PASS_ACCOUNT);
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
				db.deleteByWhere(Group.class, "account='"
						+ LlkcBody.USER_ACCOUNT + "'");
				ApplicationData.selfGroupList.clear();
				ApplicationData.joinGroupList.clear();
				ApplicationData.allGroupList.clear();
				ApplicationData.allGroupList.addAll(result);
				setJpushLabels(result);
				for (Group group : result) {
					group.setAccount(LlkcBody.USER_ACCOUNT);
					db.save(group);
					if (LlkcBody.UID_ACCOUNT.equals(group.gManagerId)) {
						ApplicationData.selfGroupList.add(group);
					} else {
						ApplicationData.joinGroupList.add(group);
					}
				}
				updateView();
				if (ApplicationData.selfGroupList.size() == 0) {
					my_group_text.setVisibility(View.VISIBLE);
				} else {
					my_group_text.setVisibility(View.GONE);
				}
				if (ApplicationData.joinGroupList.size() == 0) {
					in_group_text.setVisibility(View.VISIBLE);
				} else {
					in_group_text.setVisibility(View.GONE);
				}
			}
			super.onPostExecute(result);
		}
	}
}
