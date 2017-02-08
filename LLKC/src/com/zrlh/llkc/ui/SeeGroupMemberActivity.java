package com.zrlh.llkc.ui;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.Friend;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class SeeGroupMemberActivity extends BaseActivity {

	public static final String TAG = "seegroupmember";
	GridView group_member;
	ImageButton back, friends;
	TextView title_card;
	String gId;
	boolean isManager;

	List<HashMap<String, Object>> mapList = new ArrayList<HashMap<String, Object>>();
	private Context context;
	GridViewAdapter gridViewAdapter;

	ProgressDialog dialog = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				show();
				break;
			case 2:

				Toast.makeText(
						getContext(),
						Tools.getStringFromRes(getContext(),
								R.string.net_error3), Toast.LENGTH_SHORT)
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
		setContentView(R.layout.seegroupmember_activity);
		context = this;
		init();
		new GroupMemberTask().execute();
	}

	void show() {
		if (gridViewAdapter == null) {
			gridViewAdapter = new GridViewAdapter(this, mapList);
			group_member.setAdapter(gridViewAdapter);
		} else {
			gridViewAdapter.notifyDataSetChanged();
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
	
	private LinearLayout bottom;

	void init() {
		Intent intent = getIntent();
		gId = intent.getStringExtra("gId");
		isManager = intent.getBooleanExtra("isManager", false);
		back = (ImageButton) findViewById(R.id.back);
		friends = (ImageButton) findViewById(R.id.friends);
		title_card = (TextView) findViewById(R.id.title_card);
		bottom = (LinearLayout) this.findViewById(R.id.bottom_tap);
		if (isManager)
			bottom.setVisibility(View.VISIBLE);
		else
			bottom.setVisibility(View.GONE);
		this.findViewById(R.id.btn_deletemembers).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO
						String toUids = "";
						for (HashMap<String, Object> map : mapList) {
							boolean select = (Boolean) map.get("selected");
							if (select) {
								Friend obj = (Friend) map.get("member");
								toUids = toUids + "," + obj.getUid();
							}
						}
						showTwiceDiag(toUids);
					}
				});
		group_member = (GridView) findViewById(R.id.group_member);
		group_member.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				HashMap<String, Object> map = (HashMap<String, Object>) arg0
						.getAdapter().getItem(arg2);
				Friend obj = (Friend) map.get("member");
				Intent intent = new Intent();
				intent.putExtra("member", obj);
				intent.putExtra("source", "Group");
				intent.setClass(getContext(), UserInfoActivity.class);
				startActivity(intent);
			}
		});
		group_member.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				HashMap<String, Object> map = (HashMap<String, Object>) arg0
						.getAdapter().getItem(arg2);
				Friend obj = (Friend) map.get("member");

				showTwiceDiag(obj.getUid());
				return false;
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		title_card.setText(Tools.getStringFromRes(context,
				R.string.group_member));
		friends.setVisibility(View.INVISIBLE);
	}
	class GroupMemberTask extends AsyncTask<Object, Integer, ArrayList<Friend>>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(context);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(context,
					R.string.loading));
			dialog.show();
		}
		
		@Override
		protected ArrayList<Friend> doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(context).groupMemberList(gId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(ArrayList<Friend> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if(result != null && result.size() > 0){
				mapList.clear();
				for(int i=0; i<result.size(); i++){
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("selected", false);
					map.put("member", result.get(i));
					mapList.add(map);
				}
				handler.sendEmptyMessage(1);
			}
			else
				handler.sendEmptyMessage(2);
		}
		
	}

	private class GridViewAdapter extends BaseAdapter {
		LayoutInflater layoutInflater;
		GridViewMember gridViewMember;
		FinalBitmap finalBitmap;
		private List<HashMap<String, Object>> mapList;

		class GridViewMember {
			ImageView group_member_head;
			TextView group_member_text;
			ImageView select;

		}

		GridViewAdapter(Context context, List<HashMap<String, Object>> mapList) {
			layoutInflater = LayoutInflater.from(context);
			finalBitmap = FinalBitmap.create(context);
			finalBitmap.configLoadingImage(R.drawable.head_default);
			this.mapList = mapList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mapList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mapList.get(position);
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
				gridViewMember = new GridViewMember();
				convertView = layoutInflater.inflate(R.layout.groupmember_item,
						null);
				gridViewMember.group_member_head = (ImageView) convertView
						.findViewById(R.id.group_member_head);
				gridViewMember.group_member_text = (TextView) convertView
						.findViewById(R.id.group_member_text);
				gridViewMember.select = (ImageView) convertView
						.findViewById(R.id.group_member_select);
				convertView.setTag(gridViewMember);
			} else {
				gridViewMember = (GridViewMember) convertView.getTag();
			}
			final HashMap<String, Object> map = mapList.get(position);
			Friend member = (Friend) map.get("member");
			if (member != null) {
				gridViewMember.group_member_text.setText(member.getUname());
				if (Tools.isUrl(member.getHead()))
					finalBitmap.display(gridViewMember.group_member_head,
							member.getHead());
				else
					gridViewMember.group_member_head
							.setImageResource(R.drawable.head_default);
			}
			if (isManager)
				gridViewMember.select.setVisibility(View.VISIBLE);
			else
				gridViewMember.select.setVisibility(View.GONE);
			if (member.getUid().equals(LlkcBody.UID_ACCOUNT)) {
				gridViewMember.select.setVisibility(View.GONE);
			}
			gridViewMember.select.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					boolean selected = (Boolean) map.get("selected");
					selected = !selected;
					map.put("selected", selected);
					mapList.set(position, map);
					update();
				}
			});
			boolean selected = (Boolean) map.get("selected");
			if (selected)
				gridViewMember.select
						.setImageResource(R.drawable.btn_checkbox_selected);
			else
				gridViewMember.select.setImageResource(R.drawable.btn_checkbox);
			return convertView;
		}

		public void update() {
			notifyDataSetChanged();
		}

	}

	private void showTwiceDiag(final String toUids) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.dialog_twice_true, null);
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.show();
		dialog.getWindow().setContentView(layout);
		TextView conTV = (TextView) dialog
				.findViewById(R.id.dialog_twice_content);

		conTV.setText(Tools.getStringFromRes(context, R.string.remove_member));
		dialog.findViewById(R.id.dialog_twice_ok).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (toUids.equals(LlkcBody.UID_ACCOUNT)) {
							MyToast.getToast().showToast(
									context,
									Tools.getStringFromRes(context,
											R.string.remove_me));
							return;
						}
						new RemoveMembTask(gId, toUids).execute();
						dialog.dismiss();
					}
				});
		dialog.findViewById(R.id.dialog_twice_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
	}

	class RemoveMembTask extends AsyncTask<Object, Integer, Boolean> {
		String gId;
		String toUids;

		public RemoveMembTask(String gId, String toUids) {
			this.gId = gId;
			this.toUids = toUids;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(context);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(context,
					R.string.remove_now));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			try {
				return Community.getInstance(context).removeMembFromGroup(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT, gId,
						toUids);
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
			if (result) {
				new GroupMemberTask().execute();
			} else {
				MyToast.getToast().showToast(context,
						Tools.getStringFromRes(context, R.string.remove_fail));
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
