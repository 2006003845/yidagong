package com.zrlh.llkc.ui;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.Group;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.TimeUtil;
import com.zzl.zl_app.util.Tools;

public class GroupDetailActivity extends BaseActivity {
	public static final String TAG = "groupdetail";
	private Context context;

	public boolean isManager = false;

	ImageButton back, friends;
	ImageView group_detail_head;
	TextView title_card;
	TextView group_detail_name;
	TextView group_detail_id;
	TextView group_detail_addres_text;
	TextView group_detail_friend_text;
	TextView group_detail_time_text;
	TextView group_detail_text_text;
	LinearLayout send_dismiss;
	Button dismiss_Group;
	Button send_news;
	RelativeLayout apply_add_rel;
	RelativeLayout group_member_rel;
	FinalBitmap finalBitmap;

	String Stat;
	String Msg;
	String souseString;
	Group group;
	ProgressDialog dialog = null;
	// private Thread thread = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				setViewData();
				break;
			case 2:
				closeOneAct(TAG);
				closeOneAct(GroupChatActivity.TAG);
				if (GroupListActivity.mInstance != null)
					GroupListActivity.mInstance.updateGroupInfo();

				break;
			case 3:
				MyToast.getToast().showToast(context, Msg);
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
		context = this;
		Bundle b = getIntent().getExtras();
		souseString = getIntent().getStringExtra("souse");
		if (b != null)
			group = (Group) b.getSerializable("group");
		setContentView(R.layout.group_deatail_activity);
		finalBitmap = FinalBitmap.create(context);
		finalBitmap.configLoadingImage(R.drawable.group_head);
		init();
		setViewData();
		if (group != null)
			new GetGroupInfoTask(group.gId == null ? "" : group.gId).execute();
	}

	void init() {

		title_card = (TextView) findViewById(R.id.title_card);
		back = (ImageButton) findViewById(R.id.back);
		friends = (ImageButton) findViewById(R.id.friends);
		send_dismiss = (LinearLayout) findViewById(R.id.group_layout);
		dismiss_Group = (Button) findViewById(R.id.dismiss_group);
		send_news = (Button) findViewById(R.id.send_news);
		group_detail_head = (ImageView) findViewById(R.id.group_detail_head);
		if (Tools.isUrl(group.getgHead()))
			finalBitmap.display(group_detail_head, group.getgHead());
		else
			group_detail_head.setImageResource(R.drawable.group_head);
		dismiss_Group.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isManager) {
					showMsgDialog("1", R.layout.layout_prompt, getContext(),
							"解散帮", "您确定解散您的工友帮：" + group.gName);
					// new Thread(groupDimissRequestRunnable).start();
				} else {
					showMsgDialog("2", R.layout.layout_prompt, getContext(),
							"退出帮", "是否退出：" + group.gName);
					// new ExitGroupTask().execute();
				}
			}
		});
		send_news.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
				Intent intent = new Intent();
				intent.putExtra("gId", group.gId);
				intent.putExtra("gName", group.gName);
				intent.putExtra("gHead", group.gHead);
				intent.setClass(getContext(), GroupChatActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}
		});
		group_member_rel = (RelativeLayout) findViewById(R.id.group_member_rel);
		group_member_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("gId", group.gId);
				intent.putExtra("isManager", isManager);
				intent.setClass(getApplicationContext(),
						SeeGroupMemberActivity.class);
				startActivity(intent);
			}
		});
		title_card.setText(Tools.getStringFromRes(context, R.string.worker));
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		friends.setVisibility(View.INVISIBLE);
		group_detail_name = (TextView) findViewById(R.id.group_detail_name);
		group_detail_id = (TextView) findViewById(R.id.group_detail_id);
		group_detail_addres_text = (TextView) findViewById(R.id.group_detail_addres_text);
		group_detail_friend_text = (TextView) findViewById(R.id.group_detail_friend_text);
		group_detail_time_text = (TextView) findViewById(R.id.group_detail_time_text);
		group_detail_text_text = (TextView) findViewById(R.id.group_detail_text_text);
		apply_add_rel = (RelativeLayout) findViewById(R.id.apply_add_rel);
		apply_add_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AddGroupTask().execute();
			}
		});
	}

	void setViewData() {
		if (group == null)
			return;
		if ("search".equals(souseString)) {
			apply_add_rel.setVisibility(View.VISIBLE);
			send_dismiss.setVisibility(View.GONE);
		} else if ("chat".equals(souseString)) {
			apply_add_rel.setVisibility(View.GONE);
			send_dismiss.setVisibility(View.VISIBLE);
			dismiss_Group.setVisibility(View.VISIBLE);
			send_news.setVisibility(View.GONE);
		} else {
			apply_add_rel.setVisibility(View.GONE);
			send_dismiss.setVisibility(View.VISIBLE);
			dismiss_Group.setVisibility(View.VISIBLE);
			send_news.setVisibility(View.VISIBLE);
		}
		group_detail_name.setText(group.getgName() == null ? "" : group
				.getgName());
		group_detail_id.setText(group.getgId() == null ? "" : group.getgId());
		group_detail_addres_text.setText(group.getgAddress() == null ? ""
				: group.getgAddress());
		group_detail_friend_text.setText(group.getgManagerName() == null ? ""
				: group.getgManagerName());
		String time = group.getgCreateTime() == null ? "" : group
				.getgCreateTime();
		group_detail_time_text
				.setText(TimeUtil.getTimeStr2(time, "yyyy-MM-dd"));
		group_detail_text_text.setText(group.getgContent() == null ? "" : group
				.getgContent());
		// TODO
		if (group.gManagerId != null
				&& group.gManagerId.equals(LlkcBody.UID_ACCOUNT)) {
			isManager = true;
		} else {
			isManager = false;
		}

		if (isManager) {
			this.findViewById(R.id.group_detail_managelayout).setVisibility(
					View.VISIBLE);
			dismiss_Group.setText(Tools.getStringFromRes(context,
					R.string.remove_group));
		} else {
			dismiss_Group.setText(Tools.getStringFromRes(context,
					R.string.exit_group));
			this.findViewById(R.id.group_detail_managelayout).setVisibility(
					View.GONE);
		}
	}

	class DisbandGroupTask extends AsyncTask<Object, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(context);
			dialog.setCancelable(true);
			dialog.setMessage(Tools
					.getStringFromRes(context, R.string.propt_now));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			try {
				return Community.getInstance(context).disbandGroup(group.gId);
			} catch (JSONException e) {
				e.printStackTrace();
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
				handler.sendEmptyMessage(2);
			} 
			super.onPostExecute(result);
		}

	}
	
	class AddGroupTask extends AsyncTask<Object, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage("加入中...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {

			try {
				return Community.getInstance(getContext()).addGroup(group.gId);
			} catch (JSONException e) {
				e.printStackTrace();
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
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(TAG);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setViewData();
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

	class ExitGroupTask extends AsyncTask<Object, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(context);
			dialog.setCancelable(true);
			dialog.setMessage(Tools
					.getStringFromRes(context, R.string.exit_now));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {

			try {
				return Community.getInstance(context)
						.exitGroup(LlkcBody.USER_ACCOUNT,
								LlkcBody.PASS_ACCOUNT, group.gId);
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
				closeOneAct(TAG);
				closeOneAct(GroupChatActivity.TAG);
				if (GroupListActivity.mInstance != null)
					GroupListActivity.mInstance.updateGroupInfo();
			} else {
				MyToast.getToast().showToast(context,
						Tools.getStringFromRes(context, R.string.exit_fail));
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
			int layoutId, final String id) {
		// TODO Auto-generated method stub
		switch (layoutId) {
		case R.layout.layout_prompt:
			Button sure = (Button) dialog.findViewById(R.id.prompt_btn_ok);
			sure.setText(R.string.sure);
			sure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if (id.equals("1")) {
						new DisbandGroupTask().execute();
					} else {
						new ExitGroupTask().execute();
					}
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

	class GetGroupInfoTask extends AsyncTask<Object, Integer, Group> {
		String gId;

		public GetGroupInfoTask(String gId) {
			this.gId = gId;
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
		protected Group doInBackground(Object... params) {

			try {
				return Community.getInstance(getContext()).queryFGroupInfo(gId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Group result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null) {
				group = result;
				if (group.gManagerId != null
						&& group.gManagerId.equals(LlkcBody.UID_ACCOUNT)) {
					isManager = true;
				} else {
					isManager = false;
				}
				setViewData();
			}
			super.onPostExecute(result);
		}
	}
}
