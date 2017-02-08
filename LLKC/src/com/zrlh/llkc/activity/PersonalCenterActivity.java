package com.zrlh.llkc.activity;

import java.util.Date;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalDb;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.AuthenticationResultActivity;
import com.zrlh.llkc.corporate.CorporateActivity;
import com.zrlh.llkc.corporate.JpostManageActivity;
import com.zrlh.llkc.corporate.MyResumeActivity;
import com.zrlh.llkc.corporate.Resume;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.funciton.PersonalDynamic;
import com.zrlh.llkc.joggle.Account;
import com.zrlh.llkc.ui.LLKCApplication;
import com.zrlh.llkc.ui.ModifyPasswordActivity;
import com.zrlh.llkc.ui.PersonalEditorActivity;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.TimeUtil;
import com.zzl.zl_app.util.Tools;

public class PersonalCenterActivity extends BaseActivity {
	public static final String TAG = "personal_center";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.personal_center);
		init();
		initView();
		initDyanmic();
	}

	FinalBitmap finalBitmap;
	FinalDb db;

	public void init() {
		finalBitmap = FinalBitmap.create(this);
		finalBitmap.configLoadingImage(R.drawable.head_default);
		db = FinalDb.create(this, true);
	}

	public void initDyanmic() {
		if (!LlkcBody.isLogin())
			return;
		List<PersonalDynamic> list = db.findAllByWhere(PersonalDynamic.class,
				"account='" + LlkcBody.USER_ACCOUNT + "'");
		if (list.size() > 0) {
			LlkcBody.DYNAMIC = list.get(0);
			if (HomeActivity.mInstance != null) {
				PersonalCenterActivity act = (PersonalCenterActivity) HomeActivity.mInstance
						.getAct(PersonalCenterActivity.TAG);
				if (act != null)
					act.updateDYNAMIC();
			}
		}
		if (LlkcBody.DYNAMIC == null || list.size() == 0) {
			new InitDynamicTask().execute();
		}
	}

	private TextView titleTV, nameTV, localTV, ageTV, sexTV;
	ImageView headImgV;
	ProgressBar resumePb;
	TextView resumeprogressTV;
	TextView resumeTV, enterpriseTV;
	TextView haveAuthTV;
	TextView dynamic_content, dynamic_time;
	ImageView dynamic_img;

	ProgressDialog dialog = null;

	private void initView() {
		titleTV = (TextView) this.findViewById(R.id.tap_top_tv_title);
		titleTV.setText(R.string.mine);
		headImgV = (ImageView) findViewById(R.id.personal_center_image);
		nameTV = (TextView) findViewById(R.id.personal_center_name);
		localTV = (TextView) findViewById(R.id.personal_center_tv_area);
		ageTV = (TextView) findViewById(R.id.personal_center_tv_age);
		sexTV = (TextView) findViewById(R.id.personal_center_tv_sex);
		haveAuthTV = (TextView) this
				.findViewById(R.id.personal_center_isauth_tv);
		dynamic_content = (TextView) findViewById(R.id.personal_center_dynamic_content);
		dynamic_time = (TextView) findViewById(R.id.personal_center_dynamic_date);
		dynamic_img = (ImageView) findViewById(R.id.personal_center_dynamic_image);
		// 个人动态
		this.findViewById(R.id.personal_center_dynamic).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (LlkcBody.ACCOUNT == null)
							LlkcBody.ACCOUNT = LLKCApplication.getInstance()
									.getPersonInfo();
						if (LlkcBody.ACCOUNT == null)
							return;
						Intent intent = new Intent();
						intent.putExtra("type", "personal");
						PersonalDynamicActivity.launch(getContext(), intent);
					}
				});

		this.findViewById(R.id.personal_center_modifyinfo_but)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						PersonalEditorActivity
								.launch(getContext(), getIntent());
					}
				});
		findViewById(R.id.modify_password).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						startActivity(new Intent(getContext(),
								ModifyPasswordActivity.class));
					}
				});
		findViewById(R.id.personal_center_layout_my_collect)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						MyCollectActivity.launch(getContext(), getIntent());
					}
				});
		resumeTV = (TextView) this.findViewById(R.id.personal_center_tv_resume);
		resumeprogressTV = (TextView) this
				.findViewById(R.id.personal_center_tv_resume_progressBar);
		enterpriseTV = (TextView) this
				.findViewById(R.id.personal_center_tv_enterprise);

		this.findViewById(R.id.modify_resume).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						MyResumeActivity.launch(getContext(), getIntent());
						MobclickAgent.onEvent(getContext(), "event_myresume");
					}
				});

		// 岗位联系记录
		this.findViewById(R.id.personal_center_layout_jpostrelations)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO
						Intent intent = new Intent();
						Bundle b = new Bundle();
						b.putString("type", "1");
						intent.putExtras(b);
						RecordJPostActivity.launch(getContext(), intent);
					}
				});
		// 岗位浏览记录
		this.findViewById(R.id.personal_center_layout_browers)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO
						Intent intent = new Intent();
						Bundle b = new Bundle();
						b.putString("type", "0");
						intent.putExtras(b);
						RecordJPostActivity.launch(getContext(), intent);
					}
				});
		this.findViewById(R.id.personal_center_layout_alter_enterpriseinfo)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						switch (LlkcBody.getAuthStat()) {
						case LlkcBody.State_Auth_Success:
							AuthenticationModifyActivity.launch(getContext(),
									new Intent());
							break;
						case LlkcBody.State_Auth_Ing:
							AuthenticationResultActivity.launch(getContext(),
									new Intent());
							break;
						case LlkcBody.State_Auth_No:
							CorporateActivity
									.launch(getContext(), new Intent());
							// AuthenticationEditActivity.launch(getContext(),
							// new Intent());
							break;
						}

						MobclickAgent.onEvent(getContext(),
								"event_updatecompany");
					}
				});
		this.findViewById(R.id.personal_center_layout_manage_jpost)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						switch (LlkcBody.getAuthStat()) {
						case LlkcBody.State_Auth_Success:
							JpostManageActivity.launch(getContext(),
									new Intent());
							break;
						case LlkcBody.State_Auth_Ing:
							AuthenticationResultActivity.launch(getContext(),
									new Intent());
							break;
						case LlkcBody.State_Auth_No:
							CorporateActivity
									.launch(getContext(), new Intent());
							// AuthenticationEditActivity.launch(getContext(),
							// new Intent());
							break;
						}
						MobclickAgent.onEvent(getContext(),
								"event_jpost_manger");
					}
				});
		this.findViewById(R.id.tap_top_btn).setVisibility(View.VISIBLE);
		this.findViewById(R.id.tap_top_btn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						new LogoutTask().execute();
					}
				});
		this.findViewById(R.id.personal_center_layout_setting)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						SettingActivity.launch(getContext(), getIntent());
					}
				});
	}

	public void initViewData() {
		if (LlkcBody.ACCOUNT == null)
			LlkcBody.ACCOUNT = LLKCApplication.getInstance().getPersonInfo();
		if (LlkcBody.ACCOUNT == null || resumeTV == null)
			return;
		enterpriseTV.setText(R.string.manage_jpost);
		resumePb = (ProgressBar) this
				.findViewById(R.id.personal_center_resume_progressBar);
		int progress = setResumeProgress();
		resumePb.setProgress(progress);
		resumeprogressTV.setText(progress
				+ Tools.getStringFromRes(getContext(), R.string.full));
		nameTV.setText(LlkcBody.ACCOUNT.getUname());
		localTV.setText(LlkcBody.ACCOUNT.getLocal());
		ageTV.setText(getAge(LlkcBody.ACCOUNT.getBirth()));
		if (LlkcBody.ACCOUNT.getSex().equals("0"))
			sexTV.setText(R.string.woman);
		else
			sexTV.setText(R.string.man);
		if (!"".equals(LlkcBody.ACCOUNT.getHead())
				&& Tools.isUrl(LlkcBody.ACCOUNT.getHead())) {
			finalBitmap.display(headImgV, LlkcBody.ACCOUNT.getHead());
		} else
			headImgV.setImageResource(R.drawable.head_default);
		if (LlkcBody.getAuthStat() == LlkcBody.State_Auth_Success)
			haveAuthTV.setText("("
					+ Tools.getStringFromRes(getContext(), R.string.auth_done)
					+ ")");
		else
			haveAuthTV.setText("("
					+ Tools.getStringFromRes(getContext(), R.string.auth_no)
					+ ")");
		updateDYNAMIC();
	}

	public void updateDYNAMIC() {
		if (dynamic_content == null)
			return;
		if (LlkcBody.DYNAMIC != null) {
			dynamic_content.setText(LlkcBody.DYNAMIC.getContent());
			if ("0".equals(LlkcBody.DYNAMIC.getTime()))
				dynamic_time.setText("");
			else
				dynamic_time.setText(TimeUtil.getTimeStr2(
						LlkcBody.DYNAMIC.getTime(), "yy-MM-dd HH:mm:ss"));
			if (!"".equals(LlkcBody.DYNAMIC.getSmallImg())) {
				dynamic_img.setVisibility(View.VISIBLE);
				ImageCache.getInstance().loadImg(
						LlkcBody.DYNAMIC.getSmallImg(), dynamic_img,
						R.drawable.default_img);
			} else
				dynamic_img.setVisibility(View.INVISIBLE);
		}
	}

	public String getAge(String date) {
		if (date == null || date.equals(""))
			return "";
		String[] ds = date.split("-");
		if (ds.length > 0) {
			int year = Integer.parseInt(ds[0]);
			Date d = new Date();
			int nowy = d.getYear() + 1900;
			return (nowy - year) + "";
		}
		return "";
	}

	private int setResumeProgress() {
		if (ApplicationData.resumeList.size() == 0)
			return 0;
		Resume resume = ApplicationData.resumeList.get(0);
		int a = 0;
		if (resume.head != null && !resume.head.equals(""))
			a += 10;
		if (resume.rName != null && !resume.rName.equals(""))
			a += 10;
		if (resume.sex != null && !resume.sex.equals(""))
			a += 10;
		if (resume.birth != null && !resume.birth.equals(""))
			a += 10;
		if (resume.education != null && !resume.education.equals(""))
			a += 10;
		if (resume.workExperience != null && !resume.workExperience.equals(""))
			a += 10;
		if (resume.phone != null && !resume.phone.equals(""))
			a += 10;
		if (resume.expectPost != null && !resume.expectPost.equals(""))
			a += 10;
		if (resume.expectSalary != null && !resume.expectSalary.equals(""))
			a += 10;
		if (resume.evaluation != null && !resume.evaluation.equals(""))
			a += 10;
		return a;
	}

	private void updateResume() {
		int progress = setResumeProgress();
		if (resumePb != null) {
			resumePb.setProgress(progress);
		}
		if (resumeprogressTV != null) {
			resumeprogressTV.setText(progress
					+ Tools.getStringFromRes(getContext(), R.string.full));
		}
	}

	@Override
	public BaseActivity getContext() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void setDialogContent(AlertDialog dialog, int layoutId, String msg,
			String id) {
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

	@Override
	protected void onResume() {
		Tools.log("LLKC_Live", TAG + ":onResume");
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(TAG);
		updateResume();
		// updateDynamic();
		initViewData();
		if (LlkcBody.isLogin() && LlkcBody.DYNAMIC == null)
			initDyanmic();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(TAG);
	}

	class LogoutTask extends AsyncTask<Object, Integer, Boolean> {

		public LogoutTask() {
			super();

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getApplicationContext(),
					R.string.corporate));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			try {
				return Community.getInstance(getContext()).logout(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT);

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
				db.deleteByWhere(Account.class, "1=1");
				MainActivity.mInstance.setCurrentTab(0);
				LlkcBody.clear();
				ApplicationData.clearUserData();
				MsgCenterActivity act = (MsgCenterActivity) getAct(MsgCenterActivity.TAG);
				if (act != null)
					act.update2();
				// LLKCApplication.getInstance().stopJpush();
			} else {
			}
			super.onPostExecute(result);
		}
	}

	@Override
	public void onBackPressed() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		if (MainActivity.mInstance != null)
			MainActivity.mInstance.setCurrentTab(0);
	}

	class InitDynamicTask extends AsyncTask<Void, Void, PersonalDynamic> {

		@Override
		protected PersonalDynamic doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getApplicationContext())
						.queryPersonalDynamic(LlkcBody.UID_ACCOUNT);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(PersonalDynamic result) {
			// TODO Auto-generated method stub
			if (result != null) {
				LlkcBody.DYNAMIC = result;
				db.deleteByWhere(PersonalDynamic.class, "account='"
						+ LlkcBody.USER_ACCOUNT + "'");
				LlkcBody.DYNAMIC.setAccount(LlkcBody.USER_ACCOUNT);
				db.save(LlkcBody.DYNAMIC);
			}
			updateDYNAMIC();
			super.onPostExecute(result);
		}

	}
}
