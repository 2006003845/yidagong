package com.zrlh.llkc.ui;

import java.util.List;

import net.tsz.afinal.FinalDb;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.JobDetail;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.funciton.Utility;
import com.zzl.zl_app.cache.LocalMemory;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.net_port.WSError;
import com.zzl.zl_app.util.Tools;

public class DetailsActivity extends BaseActivity {
	public static final String TAG = "details";

	ImageButton back;
	ImageButton shareBtn;

	ImageView details_phone_ImageView;
	String idString;
	String cityString;
	String[] phoneStrings;

	TextView title_card;
	TextView details_phone;
	TextView details_job_name;
	TextView details_job_num;
	TextView details_job_money;
	TextView details_job_company;
	TextView deatils_job_contacts;
	TextView detail_job_requirements;
	TextView details_adress;
	TextView details_job_profile;

	ListView phone_ListView;
	PhoneAdapter phoneAdapter;

	String SMS_INFO;
	String SMS_INFO2;

	boolean isShow = false;
	private LinearLayout fatherLayout;
	RelativeLayout detail_phone_rel;
	RelativeLayout details_adress_rel;
	RelativeLayout details_near_rel;
	RelativeLayout details_share_sms;
	RelativeLayout details_share_friends;
	LinearLayout report_rel;
	LinearLayout phone_list_layout;
	JobDetail jobDetail;
	ProgressDialog dialog = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				show();
				break;
			case 2:
				MyToast.getToast().showToast(
						getContext(),
						Tools.getStringFromRes(getContext(),
								R.string.net_error3));

				break;
			case 3:

			default:
				break;
			}

		}
	};

	FinalDb finalDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.details_one_activity);
		Intent intent = this.getIntent();
		idString = intent.getStringExtra("idString");
		cityString = intent.getStringExtra("cityString");
		Bundle b = intent.getExtras();
		if (b != null)
			jobDetail = (JobDetail) b.getSerializable("job");
		// initUMShare();
		SMS_INFO = Tools.getStringFromRes(getContext(), R.string.sms_info);
		SMS_INFO2 = Tools.getStringFromRes(getContext(), R.string.sms_info2);
		init();
		show();
		finalDB = FinalDb.create(this);
		new GetJobDetailTask(idString, cityString).execute();
		MobclickAgent.onEvent(getContext(), "event_jpost_scan");

	}

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, DetailsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	void init() {
		title_card = (TextView) findViewById(R.id.title_card);
		details_phone = (TextView) findViewById(R.id.details_phone);
		details_job_num = (TextView) findViewById(R.id.details_job_num);
		details_adress = (TextView) findViewById(R.id.details_adress);
		details_job_name = (TextView) findViewById(R.id.details_job_name);
		details_job_money = (TextView) findViewById(R.id.details_job_money);
		details_job_company = (TextView) findViewById(R.id.details_job_company);
		deatils_job_contacts = (TextView) findViewById(R.id.deatils_job_contacts);
		detail_job_requirements = (TextView) findViewById(R.id.detail_job_requirements);
		detail_phone_rel = (RelativeLayout) findViewById(R.id.detail_phone_rel);
		details_adress_rel = (RelativeLayout) findViewById(R.id.details_adress_rel);
		details_near_rel = (RelativeLayout) findViewById(R.id.details_near_rel);
		details_share_sms = (RelativeLayout) findViewById(R.id.details_share_sms);
		details_share_friends = (RelativeLayout) findViewById(R.id.details_share_friends);
		details_phone_ImageView = (ImageView) findViewById(R.id.details_phone_ImageView);
		details_job_profile = (TextView) findViewById(R.id.details_job_profile);
		report_rel = (LinearLayout) findViewById(R.id.report_rel);
		phone_list_layout = (LinearLayout) findViewById(R.id.phone_list_layout);
		report_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("idString", idString);
				intent.putExtra("cityString", cityString);
				intent.setClass(DetailsActivity.this, ReportActivity.class);
				startActivity(intent);
			}
		});
		details_share_sms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// umeng
				MobclickAgent.onEvent(getContext(), "event_jpost_smsshare");
				sendSMS(SMS_INFO, "");
			}
		});
		phone_ListView = (ListView) findViewById(R.id.phone_list);
		details_share_friends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (LlkcBody.isLogin()) {
					// Intent intent = new Intent();
					// intent.putExtra("source_content",SMS_INFO);
					// intent.setClass(getContext(),
					// GroupListActivity.class);
					// startActivity(intent);
					if (jobDetail == null)
						return;
					MobclickAgent.onEvent(getContext(),
							"event_jpost_wgroupshare");
					LlkcBody.SOURCE_CONTENT = "#岗位分享#看到一个岗位很不错" + "岗位："
							+ jobDetail.getName() + "电话：" + jobDetail.getTel()
							+ "地址：" + jobDetail.getAddress() + "月薪："
							+ jobDetail.getSalary_max();
					startActivity(new Intent(DetailsActivity.this,
							GroupListActivity.class));
				} else {
					LoginActivity.launch(DetailsActivity.this, getIntent());
				}

			}
		});
		detail_phone_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (isShow) {
					phone_list_layout.setVisibility(View.GONE);
					details_phone_ImageView
							.setBackgroundResource(R.drawable.down);
					isShow = false;
				} else {
					// umeng
					MobclickAgent.onEvent(getContext(),
							"event_jpost_phone_unfold");
					phone_list_layout.setVisibility(View.VISIBLE);
					details_phone_ImageView
							.setBackgroundResource(R.drawable.up);
					isShow = true;
				}
			}
		});
		details_adress_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// umeng
				if (jobDetail == null)
					return;
				MobclickAgent.onEvent(getContext(), "event_jpost_map");
				Intent intent = new Intent();
				intent.putExtra("lng", jobDetail.getLng());
				intent.putExtra("lat", jobDetail.getLat());
				intent.putExtra("name", jobDetail.getName());
				intent.putExtra("address", jobDetail.getAddress());
				intent.setClass(getContext(), MapActivity.class);
				startActivity(intent);
			}
		});
		details_near_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (jobDetail == null)
					return;
				MobclickAgent.onEvent(getContext(), "event_jpost_nearby");
				if (jobDetail.getLat() != null
						&& !jobDetail.getLat().equals(""))
					LlkcBody.DYNAMIC_LAT = Double.valueOf(jobDetail.getLat());
				if (jobDetail.getLng() != null
						&& !jobDetail.getLng().equals(""))
					LlkcBody.DYNAMIC_LNG = Double.valueOf(jobDetail.getLng());
				Intent intent = getIntent();
				if (intent == null)
					intent = new Intent();
				intent.putExtra("lat", jobDetail.getLat());
				intent.putExtra("lng", jobDetail.getLng());
				intent.putExtra("From", NearJobsActivity.From_JobD);
				Bundle b = new Bundle();
				b.putSerializable("current_job", jobDetail);
				intent.putExtras(b);
				intent.setClass(getContext(), NearJobsActivity.class);
				startActivity(intent);
			}
		});
		fatherLayout = (LinearLayout) this.findViewById(R.id.detail_layout);
		back = (ImageButton) findViewById(R.id.back);
		shareBtn = (ImageButton) findViewById(R.id.friends);
		shareBtn.setVisibility(View.VISIBLE);
		shareBtn.setImageResource(R.drawable.btn_share);
		shareBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO
				if (jobDetail == null)
					return;
				fatherLayout.setDrawingCacheEnabled(true);
				fatherLayout.buildDrawingCache();
				Bitmap cacheBM = Bitmap.createBitmap(fatherLayout
						.getDrawingCache());
				// share("", cacheBM);
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("image/*");
				String path = LocalMemory.getInstance().saveTempBitmap(cacheBM,
						"temp");
				intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
				intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
				intent.putExtra(Intent.EXTRA_TEXT, "#易打工#" + jobDetail.name
						+ " " + jobDetail.salary_min + "-"
						+ jobDetail.salary_max + "  工作地址:" + jobDetail.address
						+ " " + jobDetail.corporate_name + " " + jobDetail.tel
						+ "  】 - 欢迎下载易打工。网址：http://t.cn/zROKegF ");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, "分享到:"));
				Tools.recycleBitmap(cacheBM);
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

	}

	void show() {
		if (jobDetail == null)
			return;
		String phoneString = null;
		if ("".equals(jobDetail.getTel()) || jobDetail.getTel() == null) {

			phoneString = "010-12580";
		} else {
			phoneString = jobDetail.getTel() + ",";
		}
		SMS_INFO = SMS_INFO + "岗位：" + jobDetail.getName() + "月薪："
				+ jobDetail.getSalary_max() + "很适合你，详情查看" + LlkcBody.APK_URL;
		SMS_INFO2 = SMS_INFO2.replace("job_title", jobDetail.getName());
		String time = jobDetail.getFabu_date();
		title_card.setText(time + "发布");
		phoneStrings = phoneString.split(",");
		details_phone.setText(phoneStrings[0].substring(0,
				phoneStrings[0].length() - 4)
				+ "****");
		details_job_name.setText(jobDetail.getName());
		phoneAdapter = new PhoneAdapter(getContext());
		phone_ListView.setAdapter(phoneAdapter);
		Utility.setListViewHeightBasedOnChildren(phone_ListView);
		details_job_num.setText(jobDetail.getPeonumber());
		details_job_money
				.setText("0".equals(jobDetail.getSalary_min()) ? jobDetail
						.getSalary_max() : jobDetail.getSalary_min() + "-"
						+ jobDetail.getSalary_max());
		details_job_company.setText(jobDetail.getCorporate_name());
		deatils_job_contacts.setText(jobDetail.getContacts_name());
		details_adress.setText(jobDetail.getAddress());
		detail_job_requirements.setText(jobDetail.getDemand());
		details_job_profile.setText(jobDetail.getCompany());
	}

	private void telPhone(String phone) {
		MobclickAgent.onEvent(this, "event_jpost_takephone");
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + phone));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		new RecordJpost(jobDetail.jobId, cityString, "1").execute();
		// TODO 存储本地
		jobDetail.type = "1";
		jobDetail.account = LlkcBody.USER_ACCOUNT;
		List<JobDetail> list = finalDB.findAllByWhere(JobDetail.class,
				"account='" + LlkcBody.USER_ACCOUNT
						+ "' and type='1' and jobId='" + jobDetail.jobId + "'");
		if (list == null || list.size() == 0)
			finalDB.save(jobDetail);
		else
			finalDB.update(jobDetail);

	}

	private void sendSMS(String smsBody, String phone)

	{

		Uri smsToUri = Uri.parse("smsto:" + phone);

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SENDTO);
		intent.setData(smsToUri);
		intent.putExtra("sms_body", smsBody);
		startActivity(intent);
		new RecordJpost(jobDetail.jobId, cityString, "3").execute();
		// TODO 存储本地
		jobDetail.type = "1";
		jobDetail.account = LlkcBody.USER_ACCOUNT;
		List<JobDetail> list = finalDB.findAllByWhere(JobDetail.class,
				"account='" + LlkcBody.USER_ACCOUNT
						+ "' and type='1' and jobId='" + jobDetail.jobId + "'");
		if (list == null || list.size() == 0)
			finalDB.save(jobDetail);
		else
			finalDB.update(jobDetail);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(TAG);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(TAG);
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

	private class PhoneAdapter extends BaseAdapter {
		LayoutInflater layoutInflater;
		PhoneView phoneView;

		private final class PhoneView {
			TextView phoneTextView;
			LinearLayout tel_button;
			LinearLayout sms_button;
		}

		public PhoneAdapter(Context context) {
			// TODO Auto-generated constructor stub
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return phoneStrings.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return phoneStrings[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				phoneView = new PhoneView();
				convertView = layoutInflater.inflate(
						R.layout.detail_phone_item, null);
				phoneView.phoneTextView = (TextView) convertView
						.findViewById(R.id.detail_list_phone);
				phoneView.tel_button = (LinearLayout) convertView
						.findViewById(R.id.tel_button);
				phoneView.sms_button = (LinearLayout) convertView
						.findViewById(R.id.sms_button);
				convertView.setTag(phoneView);
			} else {
				phoneView = (PhoneView) convertView.getTag();
			}
			if ("0".equals(phoneStrings[position].substring(0, 1))) {
				phoneView.sms_button.setVisibility(View.GONE);
				convertView.findViewById(R.id.detail_list_line).setVisibility(
						View.GONE);
			} else {
				convertView.findViewById(R.id.detail_list_line).setVisibility(
						View.VISIBLE);
				phoneView.sms_button.setVisibility(View.VISIBLE);
			}
			phoneView.phoneTextView.setText(phoneStrings[position]);
			phoneView.tel_button.setOnClickListener(new TelPhoneListener(
					position));
			phoneView.sms_button.setOnClickListener(new SmsPhoneListener(
					position));
			return convertView;
		}

		class TelPhoneListener implements OnClickListener {
			private int position;

			TelPhoneListener(int pos) {
				position = pos;
			}

			@Override
			public void onClick(View v) {
				int vid = v.getId();
				if (vid == phoneView.tel_button.getId()) {
					telPhone(phoneStrings[position]);

				}
			}

		}

		class SmsPhoneListener implements OnClickListener {
			private int position;

			SmsPhoneListener(int pos) {
				position = pos;
			}

			@Override
			public void onClick(View v) {
				int vid = v.getId();
				if (vid == phoneView.sms_button.getId()) {
					MobclickAgent.onEvent(getContext(), "event_jpost_sms");
					sendSMS(SMS_INFO2, phoneStrings[position]);
				}
			}

		}

	}

	class RecordJpost extends AsyncTask<Object, Integer, Boolean> {
		String rId;
		String city;
		String type;

		public RecordJpost(String rId, String city, String type) {
			this.rId = rId;
			this.city = city;
			this.type = type;
		}

		@Override
		protected Boolean doInBackground(Object... params) {

			try {
				return Community.getInstance(DetailsActivity.this).recordJpost(
						rId, city, type);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean bool) {

			super.onPostExecute(bool);
		}
	}

	class GetJobDetailTask extends AsyncTask<Object, Integer, JobDetail> {
		String rId;
		String city;

		public GetJobDetailTask(String rId, String city) {
			this.rId = rId;
			this.city = city;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(DetailsActivity.this);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(DetailsActivity.this,
					R.string.load_ing));
			dialog.show();
		}

		@Override
		protected JobDetail doInBackground(Object... params) {

			try {
				return Community.getInstance(DetailsActivity.this)
						.getJobDetail(LlkcBody.USER_ACCOUNT,
								LlkcBody.PASS_ACCOUNT, city, rId);

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (WSError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JobDetail result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result == null)
				return;
			jobDetail = result;
			jobDetail.type = "0";
			jobDetail.account = LlkcBody.USER_ACCOUNT;
			List<JobDetail> list = finalDB.findAllByWhere(JobDetail.class,
					"account='" + LlkcBody.USER_ACCOUNT
							+ "' and type='0' and jobId='" + jobDetail.jobId
							+ "'");
			if (list == null || list.size() == 0)
				finalDB.save(jobDetail);
			else
				finalDB.update(jobDetail);

			handler.sendEmptyMessage(1);
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
