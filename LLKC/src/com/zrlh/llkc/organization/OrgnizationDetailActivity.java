package com.zrlh.llkc.organization;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.organization.Organization.OrganizationDetail;
import com.zrlh.llkc.ui.MapActivity;
import com.zzl.zl_app.cache.LocalMemory;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class OrgnizationDetailActivity extends BaseActivity {
	public static final String Tag = "detail";
	ListView detail_address;
	// ContextAdapter contextAdapter;
	AddressAdapter addressAdapter;
	String conString, detail_idString;
	// String[] conStrings;
	TextView detail_nameTextView, detail_typeTextView, detail_direction,
			detail_addressTextView, detail_telTextView, detail_mapTextView,
			title_card, detail_context, schoolIntoTV;
	OrganizationDetail organizationDetail ;

	RelativeLayout rela_map, relative_tel, rela_address, breakbutton;
	ScrollView scrollView1;

	ProgressDialog dialog = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				breakbutton.setVisibility(View.GONE);
				scrollView1.setVisibility(View.VISIBLE);
				display();
				break;
			case 2:
				breakbutton.setVisibility(View.VISIBLE);
				scrollView1.setVisibility(View.GONE);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(Tag, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.detail_activity);
		init();
		new OrganizationDetailTask().execute();

	}

	@Override
	public void onResume() {
		StatService.onResume(this);
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(Tag);
	}
	
	@Override
	public void onBackPressed() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		closeOneAct(Tag);
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		StatService.onPause(this);
		MobclickAgent.onPageEnd(Tag);
	}

	ImageButton shareBtn;
	private LinearLayout fatherLayout;

	private void init() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		detail_idString = intent.getStringExtra("id");
		detail_nameTextView = (TextView) findViewById(R.id.detail_name);
		detail_typeTextView = (TextView) findViewById(R.id.detail_type);
		rela_map = (RelativeLayout) findViewById(R.id.rela_map);
		detail_direction = (TextView) findViewById(R.id.detail_direction);
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		fatherLayout = (LinearLayout) this.findViewById(R.id.detail_layout);
		shareBtn = (ImageButton) this.findViewById(R.id.btn);
		shareBtn.setVisibility(View.VISIBLE);
		shareBtn.setImageResource(R.drawable.btn_share);
		shareBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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
				intent.putExtra(Intent.EXTRA_TEXT, "#易打工#"
						+ organizationDetail.name + " "
						+ organizationDetail.clalss + "-" + "  地址:"
						+ organizationDetail.addr + " "
						+ organizationDetail.tel
						+ "  】 - 欢迎下载易打工。网址： http://t.cn/zROKegF");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, "分享到:"));
				Tools.recycleBitmap(cacheBM);
			}
		});
		rela_map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(
						getContext(),
						Tools.getStringFromRes(getContext(),
								R.string.no_function), Toast.LENGTH_SHORT)
						.show();
			}
		});
		schoolIntoTV = (TextView) this.findViewById(R.id.detail_intro);
		detail_context = (TextView) findViewById(R.id.detail_context);
		detail_addressTextView = (TextView) findViewById(R.id.lin_addres);
		breakbutton = (RelativeLayout) findViewById(R.id.breakbutton);
		scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
		detail_telTextView = (TextView) findViewById(R.id.lin_tel);
		detail_mapTextView = (TextView) findViewById(R.id.lin_map);
		relative_tel = (RelativeLayout) findViewById(R.id.relative_tel);
		rela_address = (RelativeLayout) findViewById(R.id.rela_address);
		title_card = (TextView) findViewById(R.id.title_tv);
		title_card.setText(Tools.getStringFromRes(getContext(),
				R.string.ins_detail));
		breakbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new OrganizationDetailTask().execute();
			}
		});

	}

	void display() {

		detail_nameTextView.setText(organizationDetail.getName());
		detail_typeTextView.setText(organizationDetail.getType());
		schoolIntoTV.setText(organizationDetail.getIntro());
		String clalsString;
		clalsString = organizationDetail.getClalss().replace(",", "  ");
		detail_direction.setText(clalsString);
		detail_addressTextView.setText(Tools.getStringFromRes(getContext(),
				R.string.all_address) + organizationDetail.getAddr());
		detail_telTextView.setText(Tools.getStringFromRes(getContext(),
				R.string.telphone) + organizationDetail.getTel());
		
		conString = organizationDetail.getCourse();
		detail_context.setText(conString);
		// conStrings = conString.split(";");
		// contextAdapter = new ContextAdapter(this, conStrings);
		// contextListView.setAdapter(contextAdapter);
		// Utility.setListViewHeightBasedOnChildren(contextListView);

		rela_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("lng", organizationDetail.getLng());
				intent.putExtra("lat", organizationDetail.getLat());
				intent.putExtra("name", organizationDetail.getName());
				intent.putExtra("address", organizationDetail.getAddr());
				intent.setClass(OrgnizationDetailActivity.this,
						MapActivity.class);
				startActivity(intent);
			}
		});

		relative_tel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent();
				// intent.setAction(Intent.ACTION_CALL);
				// intent.setData(Uri.parse("tel:"
				// + organizationDetail.getTel()));
				// startActivity(intent);
				String tel = organizationDetail.getTel();
				if (tel != null && !tel.equals(""))
					telPhone(tel);
				else
					MyToast.getToast().showToast(
							getContext(),
							Tools.getStringFromRes(getContext(),
									R.string.tel_null));

			}
		});

	}

	private void telPhone(String phone) {
		MobclickAgent.onEvent(this, "event_vocational_takephone");
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + phone));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	class OrganizationDetailTask extends AsyncTask<Object, Integer, OrganizationDetail>{
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
		protected OrganizationDetail doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext()).organizationDetail(detail_idString);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(OrganizationDetail result) {
			// TODO Auto-generated method stub
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null) {
				organizationDetail = result;
				handler.sendEmptyMessage(1);
			}
			else
				handler.sendEmptyMessage(2);
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
	};

}
