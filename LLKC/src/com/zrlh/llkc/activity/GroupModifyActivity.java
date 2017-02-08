package com.zrlh.llkc.activity;

import net.tsz.afinal.FinalBitmap;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.Group;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class GroupModifyActivity extends BaseActivity {

	public static final String TAG = "setting";
	private TextView titleTV;
	private ImageView head;
	private EditText nameET, introET;
	private Group group;
	FinalBitmap finalBitmap;

	ProgressDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		Bundle b = getIntent().getExtras();
		if (b != null)
			group = (Group) b.getSerializable("group");
		setContentView(R.layout.modify_groupinfo);
		finalBitmap = FinalBitmap.create(getContext());
		finalBitmap.configLoadingImage(R.drawable.group_head);
		initView();

	}

	private void initView() {
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText("编辑工友帮资料");
		this.findViewById(R.id.btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				group.setgName(nameET.getText().toString().trim());
				group.setgContent(introET.getText().toString().trim());

				new modifyGroupInfoTask().execute();
			}
		});

		head = (ImageView) findViewById(R.id.modify_groupinfo_head);
		finalBitmap.display(head, group.getgHead());

		nameET = (EditText) this.findViewById(R.id.modify_groupinfo_et_name);
		nameET.setText(group.getgName());

		introET = (EditText) this.findViewById(R.id.modify_groupinfo_et_intro);
		introET.setText(group.getgContent());

	}

	class modifyGroupInfoTask extends AsyncTask<Object, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getApplicationContext(),
					R.string.load_ing));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext()).ModifyGroupInfo(
						group);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			setProgressBarIndeterminateVisibility(false);
			onBackPressed();
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
