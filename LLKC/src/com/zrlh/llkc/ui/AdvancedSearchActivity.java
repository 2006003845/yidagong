package com.zrlh.llkc.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.City;
import com.zrlh.llkc.corporate.CityLevel1Activity;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zzl.zl_app.util.Tools;

public class AdvancedSearchActivity extends BaseActivity {
	public static final String TAG = "advancesearch";
	ImageButton back, friends;
	Button search_button;
	LinearLayout linearlayout_title;
	TextView title_card, job_type_hint, addres_type_hint;
	RelativeLayout job_type, addres_type;
	String keyword;
	String jobnameString, jobidString;
	City city;
	EditText keywordEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.advancedsearch_activity);
		init();
	}

	void init() {
		linearlayout_title = (LinearLayout) findViewById(R.id.linearlayout_title);
		title_card = (TextView) findViewById(R.id.title_card);
		search_button = (Button) findViewById(R.id.search_button);
		keywordEditText = (EditText) findViewById(R.id.keywords);

		back = (ImageButton) findViewById(R.id.back);
		friends = (ImageButton) findViewById(R.id.friends);
		friends.setVisibility(View.INVISIBLE);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		title_card.setText(Tools.getStringFromRes(getContext(),
				R.string.post_tipe));
		linearlayout_title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		job_type = (RelativeLayout) findViewById(R.id.job_type);
		job_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				job_dailog();
			}
		});
		addres_type = (RelativeLayout) findViewById(R.id.addres_type);
		addres_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getBaseContext(),
						CityLevel1Activity.class);
				intent.putExtra("type", 1);
				startActivityForResult(intent, 101);
			}
		});
		job_type_hint = (TextView) findViewById(R.id.job_type_name);
		addres_type_hint = (TextView) findViewById(R.id.addres_type_name);
		search_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				keyword = keywordEditText.getText().toString().trim();
				Intent intent = new Intent();
				intent.putExtra("jobnum", jobidString);
				intent.putExtra("jobname", jobnameString);
				intent.putExtra("city", city);
				intent.putExtra("keyword", keyword);

				if ("".equals(keyword) || keyword == null) {
					if (city == null || "".equals(jobnameString)
							|| jobnameString == null || "".equals(jobidString)
							|| jobidString == null) {
						Toast.makeText(
								AdvancedSearchActivity.this,
								Tools.getStringFromRes(getContext(),
										R.string.post_prompt1),
								Toast.LENGTH_SHORT).show();
						return;
					}
				} else {
					if (city == null) {
						Toast.makeText(
								AdvancedSearchActivity.this,
								Tools.getStringFromRes(getContext(),
										R.string.post_prompt2),
								Toast.LENGTH_SHORT).show();
						return;
					}
				}

				intent.setClass(AdvancedSearchActivity.this,
						AdvancedListActivity.class);
				startActivity(intent);
				MobclickAgent.onEvent(getContext(),
						"event_jpost_search");
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 101) {
			if (data != null) {
				Bundle b = data.getExtras();
				if (b != null) {
					City city = (City) b.getSerializable("obj");
					if (city != null) {
						// TODO
						addres_type_hint.setText(city.name);
						this.city = city;
					}
				}
			}
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
		closeOneAct(TAG);
		super.onBackPressed();
	}

	void job_dailog() {
		final String[] jobStrings = new String[] { "普工", "技工", "营业员/促销/零售",
				"服务员", "销售/销售助理", "保姆/家政", "司机", "保安/保洁", "美容美发/保健",
				"快递/送货/仓管", "厨师/厨工", "客服/文员", "会计/出纳财务", "工程技术", "其他" };
		final String[] jobnunString = new String[] { "1", "2", "3", "4", "5",
				"6", "7", "8", "9", "10", "11", "12", "13", "15", "14" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("列表框");
		builder.setItems(jobStrings, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Toast.makeText(getContext(), jobStrings[which],
						Toast.LENGTH_SHORT).show();
				job_type_hint.setText(jobStrings[which]);
				jobnameString = jobStrings[which];
				jobidString = jobnunString[which];
			}
		});
		builder.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		builder.show();
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

	// void city_dailog() {
	// final String[] cityStrings = new String[] { "北京", "天津", "重庆", "成都",
	// "青岛", "长春", "苏州", "西安", "武汉", "广州", "深圳" };
	// final String[] cityidStrings = new String[] { "1", "2", "3", "4", "5",
	// "6", "7", "8", "9", "10", "11" };
	// AlertDialog.Builder builder = new AlertDialog.Builder(this)
	// .setTitle("列表框");
	// builder.setItems(cityStrings, new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// // TODO Auto-generated method stub
	// Toast.makeText(getContext(), cityStrings[which],
	// Toast.LENGTH_SHORT).show();
	// addres_type_hint.setText(cityStrings[which]);
	// citysString = cityStrings[which];
	// cityidString = cityidStrings[which];
	// }
	// });
	// builder.setOnItemSelectedListener(new OnItemSelectedListener() {
	//
	// @Override
	// public void onItemSelected(AdapterView<?> arg0, View arg1,
	// int arg2, long arg3) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onNothingSelected(AdapterView<?> arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	// });
	// builder.show();
	// }
}
