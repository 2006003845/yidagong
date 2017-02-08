package com.zrlh.llkc.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class CreateGroupActivity extends BaseActivity {
	public static final String TAG = "creategroup";
	ImageButton back, friends;
	Button next_button;
	TextView title_card, bang_name_hit, bang_content_hit;

	RelativeLayout group_name_rel, group_content_rel, group_city_rel;
	String group_nameString = "";
	String group_contextString = "";
	String group_cityString = LlkcBody.CITY_STRING;
	String Msg, Stat, gId, gName;
	ProgressDialog dialog = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(getContext(), Msg, Toast.LENGTH_SHORT).show();
				if ("0".equals(Stat)) {
					Intent intent = new Intent();
					intent.putExtra("gId", gId);
					intent.putExtra("gName", gName);
					intent.setClass(getContext(), InviteFriendActivity.class);
					startActivity(intent);
					closeOneAct(TAG);
				}

				break;
			case 2:
				Toast.makeText(
						getContext(),
						Tools.getStringFromRes(getContext(),
								R.string.net_error2), Toast.LENGTH_SHORT)
						.show();
				break;
			case 3:

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
		setContentView(R.layout.creategroup_activity);
		init();

	}

	void init() {
		title_card = (TextView) findViewById(R.id.title_card);
		back = (ImageButton) findViewById(R.id.back);
		friends = (ImageButton) findViewById(R.id.friends);
		next_button = (Button) findViewById(R.id.next_button);
		group_name_rel = (RelativeLayout) findViewById(R.id.group_name_rel);
		bang_name_hit = (TextView) findViewById(R.id.bang_name_hit);
		group_content_rel = (RelativeLayout) findViewById(R.id.group_content_rel);
		bang_content_hit = (TextView) findViewById(R.id.bang_content_hit);
		group_city_rel = (RelativeLayout) findViewById(R.id.group_city);
		group_content_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				show_group_context();
			}
		});
		group_name_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				show_group_name();
			}
		});
		group_city_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		next_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (group_nameString == null || "".equals(group_nameString)) {
					Toast.makeText(
							getContext(),
							Tools.getStringFromRes(getContext(),
									R.string.group_name_nonull),
							Toast.LENGTH_SHORT).show();
					return;
				} else if (group_contextString == null
						|| "".equals(group_contextString)) {
					Toast.makeText(
							getContext(),
							Tools.getStringFromRes(getContext(),
									R.string.group_con_nonull),
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					new CreateGroupTask().execute();
				}

				// startActivity(new
				// Intent(getContext(),InviteFriendActivity.class));
			}
		});
		title_card.setText(Tools.getStringFromRes(getContext(),
				R.string.create_group));
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		friends.setVisibility(View.INVISIBLE);

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

	void show_group_name() {

		final EditText editText = new EditText(this);
		new AlertDialog.Builder(this)
				.setTitle(
						Tools.getStringFromRes(getContext(),
								R.string.input_groupname))
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(editText)
				.setPositiveButton(
						Tools.getStringFromRes(getContext(),
								R.string.set_date_btn_sure),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								group_nameString = editText.getText()
										.toString().trim();
								if ("".equals(group_nameString)) {
									Toast.makeText(
											getContext(),
											Tools.getStringFromRes(
													getContext(),
													R.string.group_name_nonull),
											Toast.LENGTH_SHORT).show();
								} else {
									bang_name_hit.setText(group_nameString);
								}
							}
						})
				.setNegativeButton(
						Tools.getStringFromRes(getContext(), R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).show();

	}

	void show_group_context() {

		final EditText editText = new EditText(this);
		new AlertDialog.Builder(this)
				.setTitle(
						Tools.getStringFromRes(getContext(),
								R.string.input_groupcon))
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(editText)
				.setPositiveButton(
						Tools.getStringFromRes(getContext(),
								R.string.set_date_btn_sure),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								group_contextString = editText.getText()
										.toString().trim();
								if ("".equals(group_contextString)) {
									Toast.makeText(
											getContext(),
											Tools.getStringFromRes(
													getContext(),
													R.string.group_con_nonull),
											Toast.LENGTH_SHORT).show();
								} else {
									bang_content_hit
											.setText(group_contextString);
								}
							}
						})
				.setNegativeButton(
						Tools.getStringFromRes(getContext(), R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).show();

	}

	class CreateGroupTask extends AsyncTask<Object, Integer, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.creating));
			dialog.show();
		}

		@Override
		protected JSONObject doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext())
						.createGroup(group_nameString, group_contextString,
								group_cityString);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if(result == null){
				handler.sendEmptyMessage(2);
			}
			else{
				try {
					if(!result.isNull("Stat"))
						Stat = result.getString("Stat");
					if(!result.isNull("Msg"))
						Msg = result.getString("Msg");
					if ("0".equals(Stat)) {
						if(!result.isNull("gId"))
							gId = result.getString("gId");
						if(!result.isNull("gName"))
							gName = result.getString("gName");
					}
					handler.sendEmptyMessage(1);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
