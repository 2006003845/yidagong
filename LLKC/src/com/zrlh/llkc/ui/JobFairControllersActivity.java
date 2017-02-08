package com.zrlh.llkc.ui;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.City;
import com.zrlh.llkc.corporate.CityLevel1Activity;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.Jobs.JobFair;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class JobFairControllersActivity extends BaseActivity {
	public static final String TAG = "jobfaircontroller";
	
	ImageButton back, friends;
	LinearLayout linearlayout_title;
	TextView title_card;
	JobFairAdapter jobFairAdapter;
	List<JobFair> jobFairsList = new ArrayList<JobFair>();;
	PullToRefreshListView jobfair_list;
	String CRNTER_CITY = LlkcBody.CITY_STRING;
	private View lvNews_footer;
	private TextView lvNews_foot_more;
	private ProgressBar lvNews_foot_progress;
	ProgressDialog dialog = null;
	int page = 1;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				show();
				lvNews_foot_more.setText("");
				lvNews_foot_progress.setVisibility(View.INVISIBLE);

				break;
			case 2:
				lvNews_foot_more.setText(R.string.noinfor);
				lvNews_foot_progress.setVisibility(View.INVISIBLE);

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
		setContentView(R.layout.jobfair_activity);
		init();

	}

	void init() {

		title_card = (TextView) findViewById(R.id.title_card);
		back = (ImageButton) findViewById(R.id.back);
		friends = (ImageButton) findViewById(R.id.friends);
		lvNews_footer = getLayoutInflater().inflate(R.layout.listview_footer,
				null);
		lvNews_foot_more = (TextView) lvNews_footer
				.findViewById(R.id.listview_foot_more);
		lvNews_foot_progress = (ProgressBar) lvNews_footer
				.findViewById(R.id.listview_foot_progress);
		linearlayout_title = (LinearLayout) findViewById(R.id.linearlayout_title);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		title_card.setText(Tools.getStringFromRes(getContext(),
				R.string.recruit) + LlkcBody.CITY_STRING);
		linearlayout_title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getBaseContext(),
						CityLevel1Activity.class);
				intent.putExtra("type", 1);
				startActivityForResult(intent, 101);
			}
		});
		friends.setVisibility(View.INVISIBLE);
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

		jobfair_list = (PullToRefreshListView) findViewById(R.id.jobfair_listview);
		jobfair_list.addFooterView(lvNews_footer, null, false);// 添加底部视图
																// 必须在setAdapter前
		jobfair_list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(lvNews_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				if (scrollEnd) {
					lvNews_footer.setVisibility(View.VISIBLE);
					lvNews_foot_more.setText(R.string.load_ing);
					lvNews_foot_progress.setVisibility(View.VISIBLE);

					page = page + 1;
					new JobFairTask().execute();
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
		jobfair_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				JobFair fair = (JobFair) arg0.getAdapter().getItem(
						arg2);
				Intent intent = new Intent();
				intent.putExtra("jobfair_list", fair);
				intent.putExtra("city", CRNTER_CITY);

				intent.setClass(getContext(),
						JobFairDetailActivity.class);
				startActivity(intent);
			}
		});

		new JobFairTask().execute();
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

	void show() {
		if (jobFairAdapter == null) {
			jobFairAdapter = new JobFairAdapter(this, jobFairsList);
			jobfair_list.setAdapter(jobFairAdapter);
		} else
			jobFairAdapter.notifyDataSetChanged();

	}
	
	class JobFairTask extends AsyncTask<Object, Integer, ArrayList<JobFair>>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.loading));
			if (page < 2)
				dialog.show();
		}
		
		@Override
		protected ArrayList<JobFair> doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext())
						.getJobFair(CRNTER_CITY, String.valueOf(page));

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(ArrayList<JobFair> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if(result!=null && result.size()>0){
				jobFairsList.addAll(result);
				handler.sendEmptyMessage(1);
			}
			else
				handler.sendEmptyMessage(2);
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
						page = 1;
						CRNTER_CITY = city.name;
						jobFairsList.clear();
						jobFairAdapter.notifyDataSetChanged();
						new JobFairTask().execute();

						title_card.setText(Tools.getStringFromRes(
								getContext(), R.string.recruit)
								+ city.name);
					}
				}
			}
		}
	}

	private class JobFairAdapter extends BaseAdapter {
		List<JobFair> jobFairsList;
		LayoutInflater layoutInflater;
		JobFairView jobFairView;

		public final class JobFairView {
			TextView jobfair_jobtime; // 发布时间
			TextView jobfair_jobname; // 发布名称
			TextView jobfair_tel; // 联系人
			TextView jobfair_fixed_line; // 固话
			TextView jobfair_venues; // 场馆
			TextView jobfair_adress; // 地址
		}

		private JobFairAdapter(Context context, List<JobFair> jobFairsList) {
			this.jobFairsList = jobFairsList;
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return jobFairsList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return jobFairsList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (arg1 == null) {
				jobFairView = new JobFairView();
				arg1 = layoutInflater.inflate(R.layout.jobfair_list_item, null);
				jobFairView.jobfair_adress = (TextView) arg1
						.findViewById(R.id.jobfair_adress);
				jobFairView.jobfair_fixed_line = (TextView) arg1
						.findViewById(R.id.jobfair_fixed_line);
				jobFairView.jobfair_jobname = (TextView) arg1
						.findViewById(R.id.jobfair_jobname);
				jobFairView.jobfair_jobtime = (TextView) arg1
						.findViewById(R.id.jobfair_jobtime);
				jobFairView.jobfair_tel = (TextView) arg1
						.findViewById(R.id.jobfair_tel);
				jobFairView.jobfair_venues = (TextView) arg1
						.findViewById(R.id.jobfair_venues);
				arg1.setTag(jobFairView);
			} else {
				jobFairView = (JobFairView) arg1.getTag();

			}
			String telString = jobFairsList.get(arg0).getTel() + ",";
			if ("".equals(telString) || telString == null) {
				telString = "100860000";
			}

			String newtelString = telString
					.substring(0, telString.indexOf(","));
			jobFairView.jobfair_adress.setText(jobFairsList.get(arg0)
					.getAddress());
			jobFairView.jobfair_fixed_line.setText(newtelString);
			jobFairView.jobfair_jobname.setText(jobFairsList.get(arg0)
					.getName());
			jobFairView.jobfair_jobtime.setText(jobFairsList.get(arg0)
					.getDate());
			jobFairView.jobfair_tel.setText(jobFairsList.get(arg0)
					.getContacts());
			jobFairView.jobfair_venues.setText(jobFairsList.get(arg0)
					.getVenues());
			return arg1;
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
