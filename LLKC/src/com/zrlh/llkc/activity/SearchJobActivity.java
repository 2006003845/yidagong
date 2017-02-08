package com.zrlh.llkc.activity;

import java.util.Random;

import org.json.JSONException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.City;
import com.zrlh.llkc.corporate.CityLevel1Activity;
import com.zrlh.llkc.corporate.KeywordsFlow;
import com.zrlh.llkc.corporate.PlatformAPI;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.AdvancedListActivity;
import com.zzl.zl_app.net_port.Get2ApiImpl;
import com.zzl.zl_app.net_port.WSError;

public class SearchJobActivity extends BaseActivity implements OnClickListener {
	public static final String TAG = "searchjob";

	TextView titleTextView, cityTV;
	ImageButton backiImageView;
	EditText keyWordET;
	private City city;

	// public static final String[] keywords = {"普工", "司机", "电子厂", "机械工",
	// "服务员", "保安", "电焊工", "技师", "厨师", "快递", "销售", "兼职",
	// "建筑工", "搬运工", "学徒", "公务员",
	// };
	private KeywordsFlow keywordsFlow;

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, SearchJobActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.searchjob);
		initView();
	}

	private void initView() {
		titleTextView = (TextView) findViewById(R.id.title_alljob_card);
		titleTextView.setText("搜索岗位");
		backiImageView = (ImageButton) findViewById(R.id.title_alljob_back);
		backiImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		city = ApplicationData.getCity(LlkcBody.CITY_STRING);
		cityTV = (TextView) this.findViewById(R.id.title_alljob_tv_position);
		if (city != null && city.name != null) {
			String cityName = city.name.substring(0, city.name.length() > 3 ? 3
					: city.name.length());
			cityTV.setText(cityName);
		}
		// 切換城市
		findViewById(R.id.title_alljob_relayout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getBaseContext(),
								CityLevel1Activity.class);
						intent.putExtra("type", 1);
						startActivityForResult(intent, 101);
					}
				});

		keyWordET = (EditText) this.findViewById(R.id.searchjob_et);

		// 搜索
		this.findViewById(R.id.searchjob_searchbut).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String keyword = keyWordET.getText().toString().trim();
						Intent intent = new Intent();
						// intent.putExtra("jobnum", jobidString);
						// intent.putExtra("jobname", jobnameString);
						intent.putExtra("city", city);
						intent.putExtra("keyword", keyword);
						intent.setClass(SearchJobActivity.this,
								AdvancedListActivity.class);
						startActivity(intent);
					}
				});

		if (ApplicationData.keywords != null) {
			keywordsFlow = (KeywordsFlow) findViewById(R.id.searchjob_frameLayout);
			keywordsFlow.setDuration(800l);
			keywordsFlow.setOnItemClickListener(this);
			// 添加
			feedKeywordsFlow(keywordsFlow, ApplicationData.keywords);
			keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
		} else {
			new InitKeyWord(this).execute();
		}

	}

	class InitKeyWord extends AsyncTask<Void, Void, String[]> {
		Get2ApiImpl api;

		public InitKeyWord(Context context) {
			api = new Get2ApiImpl(context);
		}

		@Override
		protected String[] doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				return api.getKeyWord(Get2ApiImpl.From_Net,
						PlatformAPI.KeyWord_Url);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WSError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// TODO Auto-generated method stub
			if (result != null) {
				ApplicationData.keywords = result;
				keywordsFlow = (KeywordsFlow) findViewById(R.id.searchjob_frameLayout);
				keywordsFlow.setDuration(800l);
				keywordsFlow.setOnItemClickListener(SearchJobActivity.this);
				// 添加
				feedKeywordsFlow(keywordsFlow, ApplicationData.keywords);
				keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
			}
			super.onPostExecute(result);
		}

	}

	private void feedKeywordsFlow(KeywordsFlow keywordsFlow, String[] arr) {
		Random random = new Random();
		for (int i = 0; i < KeywordsFlow.MAX; i++) {
			int ran = random.nextInt(arr.length);
			ran = i;
			String tmp = arr[ran];
			keywordsFlow.feedKeyword(tmp);
		}
	}

	@Override
	public void onClick(View v) {
		if (v instanceof TextView) {
			String keyword = ((TextView) v).getText().toString();
			Intent intent = new Intent();
			// intent.putExtra("jobnum", jobidString);
			// intent.putExtra("jobname", jobnameString);
			intent.putExtra("city", city);
			intent.putExtra("keyword", keyword);
			intent.setClass(SearchJobActivity.this, AdvancedListActivity.class);
			startActivity(intent);
		}
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
						String c = city.name
								.substring(0, city.name.length() > 3 ? 3
										: city.name.length());
						cityTV.setText(c);
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
