package com.zrlh.llkc.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.Jobs;
import com.zrlh.llkc.funciton.Utility;
import com.zzl.zl_app.util.Tools;

public class JobFairDetailActivity extends BaseActivity {
	public static final String TAG = "jobfairdetail";
	
	ImageButton back, friends;
	// Button jobfair_details_phone_button;
	TextView title_card, jobfair_details_job_name, jobfair_detail_venue,
			jobfair_detail_date, jobfair_detail_contacts,
			jobfair_detail_job_requirements, jobfair_details_adress;
	Jobs.JobFair jobFair;
	ListView phone_ListView;
	RelativeLayout jobfair_details_adress_rel;
	RelativeLayout detail_phone_rel;
	LinearLayout phone_list_layout;
	String cityString;
	ImageView details_phone_ImageView;
	boolean isShow = false;
	String[] phoneStrings;
	TextView details_phone;
	PhoneAdapter phoneAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.jobfairdetail_activity);
		init();
	}

	void init() {
		Intent intent = this.getIntent();
		jobFair = (Jobs.JobFair) intent.getSerializableExtra("jobfair_list");
		cityString = intent.getStringExtra("city");
		title_card = (TextView) findViewById(R.id.title_card);
		back = (ImageButton) findViewById(R.id.back);
		friends = (ImageButton) findViewById(R.id.friends);
		friends.setVisibility(View.GONE);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		title_card.setText(Tools.getStringFromRes(getContext(),
				R.string.recruit) + cityString);
		friends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(
						getContext(),
						Tools.getStringFromRes(getContext(),
								R.string.no_function), Toast.LENGTH_SHORT)
						.show();
			}
		});
		jobfair_details_job_name = (TextView) findViewById(R.id.jobfair_details_job_name);
		jobfair_detail_venue = (TextView) findViewById(R.id.jobfair_detail_venue);
		jobfair_detail_date = (TextView) findViewById(R.id.jobfair_detail_date);
		jobfair_detail_contacts = (TextView) findViewById(R.id.jobfair_detail_contacts);
		jobfair_detail_job_requirements = (TextView) findViewById(R.id.jobfair_detail_job_requirements);
		jobfair_details_adress = (TextView) findViewById(R.id.jobfair_details_adress);
		detail_phone_rel = (RelativeLayout) findViewById(R.id.detail_phone_rel);
		jobfair_details_adress_rel = (RelativeLayout) findViewById(R.id.jobfair_details_adress_rel);
		phone_list_layout = (LinearLayout) findViewById(R.id.phone_list_layout);
		details_phone_ImageView = (ImageView) findViewById(R.id.details_phone_ImageView);
		details_phone = (TextView) findViewById(R.id.details_phone);
		phone_ListView = (ListView) findViewById(R.id.phone_list);
		jobfair_details_adress_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MobclickAgent.onEvent(getContext(),
						"event_jfair_map");
				Intent intent = new Intent();
				intent.putExtra("lng", jobFair.getLng());
				intent.putExtra("lat", jobFair.getLat());
				intent.putExtra("name", jobFair.getName());
				intent.putExtra("address", jobFair.getAddress());
				intent.setClass(getContext(), MapActivity.class);
				startActivity(intent);
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
					phone_list_layout.setVisibility(View.VISIBLE);
					details_phone_ImageView
							.setBackgroundResource(R.drawable.up);
					isShow = true;
				}

			}
		});
		show();
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
		closeOneAct(TAG);
		super.onBackPressed();
	}

	void show() {
		jobfair_details_job_name.setText(jobFair.getName());
		jobfair_detail_venue.setText(jobFair.getVenues());
		jobfair_detail_date.setText(jobFair.getDate());
		jobfair_detail_contacts.setText(jobFair.getContacts());
		jobfair_details_adress.setText(jobFair.getAddress());
		jobfair_detail_job_requirements.setText(jobFair.getContent());
		String phoneString = null;
		if ("".equals(jobFair.getTel()) || jobFair.getTel() == null) {

			phoneString = Tools.getStringFromRes(getContext(),
					R.string.nothing);
		} else {
			phoneString = jobFair.getTel() + ",";
		}
		phoneStrings = phoneString.split(",");
		if (phoneStrings.length > 0)
			if (phoneStrings[0].length() > 7)
				details_phone.setText(phoneStrings[0].substring(0,
						phoneStrings[0].length() - 4) + "****");
			else
				details_phone.setText(phoneString);
		phoneAdapter = new PhoneAdapter(getContext());
		phone_ListView.setAdapter(phoneAdapter);
		Utility.setListViewHeightBasedOnChildren(phone_ListView);
	}

	private void telPhone(String phone) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + phone));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);

	}

	private void sendSMS(String smsBody, String phone)

	{

		Uri smsToUri = Uri.parse("smsto:" + phone);

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SENDTO);
		intent.setData(smsToUri);
		intent.putExtra("sms_body", smsBody);

		startActivity(intent);

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
			phoneView.sms_button.setVisibility(View.GONE);
			convertView.findViewById(R.id.detail_list_line).setVisibility(
					View.GONE);
			// if ("0".equals(phoneStrings[position].substring(0, 1))) {
			// phoneView.sms_button.setVisibility(View.INVISIBLE);
			// } else {
			// phoneView.sms_button.setVisibility(View.VISIBLE);
			// }
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
				if (vid == phoneView.tel_button.getId()) {
					sendSMS(Tools.getStringFromRes(getContext(),
							R.string.is_sms), phoneStrings[position]);

				}
			}

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