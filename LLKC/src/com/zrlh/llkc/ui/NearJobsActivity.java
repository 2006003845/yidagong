package com.zrlh.llkc.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.Jobs;
import com.zrlh.llkc.funciton.Jobs.NearJob;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.LLKCApplication.LocationResultCallback;
import com.zrlh.llkc.ui.PullToRefreshListView.OnRefreshListener;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.TimeUtil;
import com.zzl.zl_app.util.Tools;

public class NearJobsActivity extends BaseActivity {
	public static final String TAG = "nearjob";

	ArrayList<NearJob> nearJobsList = new ArrayList<NearJob>();
	ImageButton back, friends;
	TextView title_card;
	LinearLayout linearlayout_title, layout_distance, layout_jobs,
			layout_money;
	private TextView lvNews_foot_more;
	private ProgressBar lvNews_foot_progress;
	PullToRefreshListView near_jobs_list;
	private View lvNews_footer;
	int page = 1;
	String distanceString = "";
	String classoneString = "";
	String salaryString = "";
	String LAT, LNG;
	boolean DateEmty = true;
	ProgressDialog dialog = null;
	NearJobAdapter nearJobAdapter;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				show();
				lvNews_foot_more.setText("");
				lvNews_foot_progress.setVisibility(View.INVISIBLE);

				break;
			case 2:
				nearJobAdapter.notifyDataSetChanged();
				lvNews_foot_more.setText("");
				lvNews_foot_progress.setVisibility(View.INVISIBLE);

				break;
			case 3:
				show();
				lvNews_foot_more.setText(Tools.getStringFromRes(getContext(),
						R.string.nodata));
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
		setContentView(R.layout.nearjobs_activity);
		init();
		show();
	}

	LocationResultCallback callback = new LocationResultCallback() {

		@Override
		public void getLocationResult(BDLocation location, String city,
				String district, double latitude, double longitude) {
			double lat = latitude;
			double lng = longitude;
			if (lat == 4.9E-324 || lng == 4.9E-324) {
				LAT = String.valueOf(LlkcBody.LAT);
				LNG = String.valueOf(LlkcBody.LNG);
			} else {
				LlkcBody.LAT = lat;
				LlkcBody.LNG = lng;
				LAT = String.valueOf(location.getLatitude());
				LNG = String.valueOf(location.getLongitude());
			}
			if (LLKCApplication.getInstance().isOpenNetwork())
				new JobRequestTask().execute();
			else
				MyToast.getToast().showToast(NearJobsActivity.this,
						"当前网络链接失败 ,  请检查您的网络");
			if (!TextUtils.isEmpty(city))
				((LLKCApplication) getApplication()).closeLocationCallBack();
		}
	};

	int from;
	public static final int From_Mine = 1;
	public static final int From_JobD = 2;

	void init() {
		Intent intent = this.getIntent();
		LAT = intent.getStringExtra("lat");
		LNG = intent.getStringExtra("lng");
		from = intent.getIntExtra("From", -1);
		// if (LLKCApplication.getInstance().isOpenNetwork()) {
		if (from == From_Mine) {
			LLKCApplication.getInstance().singleLocation(callback);
			MyToast.getToast().showToast(getContext(), "正在定位...");
		} else {
			if (LLKCApplication.getInstance().isOpenNetwork())
				// networking_dailog();
				new JobRequestTask().execute();
			else
				MyToast.getToast().showToast(NearJobsActivity.this,
						"当前网络链接失败 ,  请检查您的网络");
		}

		lvNews_footer = getLayoutInflater().inflate(R.layout.listview_footer,
				null);
		lvNews_foot_more = (TextView) lvNews_footer
				.findViewById(R.id.listview_foot_more);
		lvNews_foot_progress = (ProgressBar) lvNews_footer
				.findViewById(R.id.listview_foot_progress);
		// networking_dailog();

		layout_distance = (LinearLayout) findViewById(R.id.layout_distance);
		layout_jobs = (LinearLayout) findViewById(R.id.layout_jobs);
		layout_money = (LinearLayout) findViewById(R.id.layout_money);
		layout_distance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				distance_dailog();
			}
		});
		layout_jobs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				job_dailog();
			}
		});
		layout_money.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				money_dailog();
			}
		});
		// near_jobs_list =(ListView)findViewById(R.id.near_jobs_list);
		near_jobs_list = (PullToRefreshListView) findViewById(R.id.near_jobs_list);
		near_jobs_list.addFooterView(lvNews_footer, null, false);// 添加底部视图
																	// 必须在setAdapter前
		near_jobs_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Jobs.NearJob job = (Jobs.NearJob) arg0.getAdapter().getItem(
						arg2);
				Intent intent = new Intent();
				intent.putExtra("idString", job.getId());
				intent.putExtra("cityString", job.getCity());
				intent.setClass(getContext(), DetailsActivity.class);
				startActivity(intent);
			}
		});

		near_jobs_list.setOnScrollListener(new OnScrollListener() {

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
				if (scrollEnd && DateEmty) {
					lvNews_footer.setVisibility(View.VISIBLE);
					lvNews_foot_more.setText(R.string.load_ing);
					lvNews_foot_progress.setVisibility(View.VISIBLE);

					page = page + 1;
					if (LLKCApplication.getInstance().isOpenNetwork()) {
						if (from == From_Mine) {
							LLKCApplication.getInstance().singleLocation(
									callback);
						} else if (from == From_JobD) {
							new JobRequestTask().execute();
						}
					} else {
						MyToast.getToast().showToast(NearJobsActivity.this,
								"当前网络链接失败 ,  请检查您的网络");
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
		near_jobs_list.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				page = 1;
				if (LLKCApplication.getInstance().isOpenNetwork()) {
					if (from == From_Mine) {
						LLKCApplication.getInstance().singleLocation(callback);
					} else if (from == From_JobD) {
						new JobRequestTask().execute();
					}
				} else
					MyToast.getToast().showToast(NearJobsActivity.this,
							"当前网络链接失败 ,  请检查您的网络");
			}
		});
		title_card = (TextView) findViewById(R.id.title_card);
		back = (ImageButton) findViewById(R.id.back);
		friends = (ImageButton) findViewById(R.id.friends);
		friends.setImageResource(R.drawable.btn_map);
		linearlayout_title = (LinearLayout) findViewById(R.id.linearlayout_title);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		title_card.setText(Tools.getStringFromRes(getContext(),
				R.string.nearby_post));
		linearlayout_title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});
		friends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = getIntent();
				if (intent == null)
					intent = new Intent();
				intent.putExtra("nearJobsList", nearJobsList);
				intent.putExtra("lat", LAT);
				intent.putExtra("lng", LNG);
				intent.setClass(getContext(), AllMapActivity.class);
				startActivity(intent);
			}
		});
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

	void job_dailog() {
		page = 1;
		// nearJobsList.clear();
		final String[] jobStrings = new String[] { "普工", "技工", "营业员/促销/零售",
				"服务员", "销售/销售助理", "保姆/家政", "司机", "保安/保洁", "美容美发/保健",
				"快递/送货/仓管", "厨师/厨工", "客服/文员", "会计/出纳财务", "其他", "工程技术" };
		final String[] jobnumString = new String[] { "1", "2", "3", "4", "5",
				"6", "7", "8", "9", "10", "11", "12", "13", "14", "15" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("列表框");
		builder.setItems(jobStrings, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// Toast.makeText(getContext(), jobStrings[which],
				// Toast.LENGTH_SHORT).show();
				classoneString = jobnumString[which];
				nearJobsList.clear();
				nearJobAdapter.notifyDataSetChanged();
				if (LLKCApplication.getInstance().isOpenNetwork()) {
					if (from == From_Mine) {
						LLKCApplication.getInstance().singleLocation(callback);
					} else if (from == From_JobD) {
						new JobRequestTask().execute();
					}
				} else {
					MyToast.getToast().showToast(NearJobsActivity.this,
							"当前网络链接失败 ,  请检查您的网络");
				}
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

	void money_dailog() {
		// 0 , 全部
		// 1 2000以下
		// 2 2000-4000
		// 3 4000-6000
		// 4 6000以上
		page = 1;
		// nearJobsList.clear();

		final String[] moneytrings = new String[] { "不限", "2000以下",
				"2000-4000", "4000-6000", "6000以上" };
		final String[] moneynubStrings = new String[] { "0", "1", "2", "3", "4" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("列表框");
		builder.setItems(moneytrings, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// Toast.makeText(getContext(), moneytrings[which],
				// Toast.LENGTH_SHORT).show();

				nearJobsList.clear();
				nearJobAdapter.notifyDataSetChanged();
				salaryString = moneynubStrings[which];
				if (LLKCApplication.getInstance().isOpenNetwork()) {
					if (from == From_Mine) {
						LLKCApplication.getInstance().singleLocation(callback);
					} else if (from == From_JobD) {
						new JobRequestTask().execute();
					}
				} else {
					MyToast.getToast().showToast(NearJobsActivity.this,
							"当前网络链接失败 ,  请检查您的网络");
				}
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

	void distance_dailog() {
		// 0 , 全部
		// 1 1KM以内
		// 2 1KM-5KM
		// 3 5KM-7KM
		// 4 7KM以上
		page = 1;
		// nearJobsList.clear();

		final String[] distanceStrings = new String[] { "不限", "1KM以内",
				"1KM-5KM", "5KM-7KM", "7KM以上" };
		final String[] distancenumStrings = new String[] { "0", "1", "2", "3",
				"4" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("列表框");
		builder.setItems(distanceStrings,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// Toast.makeText(getContext(),
						// distanceStrings[which], Toast.LENGTH_SHORT)
						// .show();
						distanceString = distancenumStrings[which];
						nearJobsList.clear();
						nearJobAdapter.notifyDataSetChanged();
						if (LLKCApplication.getInstance().isOpenNetwork()) {
							if (from == From_Mine) {
								LLKCApplication.getInstance().singleLocation(
										callback);
							} else if (from == From_JobD) {
								new JobRequestTask().execute();
							}
						} else {
							MyToast.getToast().showToast(NearJobsActivity.this,
									"当前网络链接失败 ,  请检查您的网络");
						}
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

	void show() {
		if (nearJobAdapter == null) {
			nearJobAdapter = new NearJobAdapter(this, nearJobsList);
			near_jobs_list.setAdapter(nearJobAdapter);
		} else
			nearJobAdapter.notifyDataSetChanged();
	}

	class JobRequestTask extends AsyncTask<Object, Integer, List<NearJob>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(NearJobsActivity.this);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.loading));
			if (page < 2)
				dialog.show();
		}

		@Override
		protected List<NearJob> doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext()).getNearJob(
						LlkcBody.CITY_STRING, String.valueOf(page),
						classoneString, salaryString, distanceString,
						String.valueOf(LAT), String.valueOf(LNG));

			} catch (JSONException e) {
				e.printStackTrace();
				lvNews_footer.setVisibility(View.INVISIBLE);
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<NearJob> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result == null || result.size() == 0) {
				DateEmty = false;
				handler.sendEmptyMessage(3);
			} else {
				nearJobsList.addAll(result);
				DateEmty = true;
				if (page > 1) {
					handler.sendEmptyMessage(2);

				} else {
					handler.sendEmptyMessage(1);
				}
			}
		}

	}

	private class NearJobAdapter extends BaseAdapter {
		List<Jobs.NearJob> nearJobsList;
		LayoutInflater layoutInflater;
		NearJobView nearJobView;

		public final class NearJobView {
			TextView salary;
			TextView jobtime;
			TextView jobname;
			TextView peonumber;
			TextView address;
			ImageView job_att;
		}

		private NearJobAdapter(Context context, List<Jobs.NearJob> nearJobsList) {
			this.nearJobsList = nearJobsList;
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return nearJobsList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return nearJobsList.get(arg0);
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
				nearJobView = new NearJobView();
				arg1 = layoutInflater.inflate(R.layout.near_list_item, null);
				nearJobView.salary = (TextView) arg1.findViewById(R.id.salary);
				nearJobView.jobtime = (TextView) arg1
						.findViewById(R.id.jobtime);
				nearJobView.jobname = (TextView) arg1
						.findViewById(R.id.jobname);
				nearJobView.peonumber = (TextView) arg1
						.findViewById(R.id.peonumber);
				nearJobView.address = (TextView) arg1
						.findViewById(R.id.address);
				nearJobView.job_att = (ImageView) arg1
						.findViewById(R.id.job_att);
				arg1.setTag(nearJobView);
			} else {
				nearJobView = (NearJobView) arg1.getTag();
			}
			Jobs.NearJob job = nearJobsList.get(arg0);
			if (job != null) {
				String text = "";
				if ("0".equals(job.getSalary_min())) {
					text = job.getSalary_max();
				} else {
					text = job.getSalary_min() + "-"
							+ job.getSalary_max().trim();
				}
				if (text.length() > 14)
					text = text.substring(0, 12) + "...";
				nearJobView.salary.setText(text);
				// if ("0".equals(job.getSalary_min())) {
				// nearJobView.salary.setText(job.getSalary_max());
				// } else {
				// nearJobView.salary.setText("￥"
				// + job.getSalary_min() + "-"
				// + job.getSalary_max());
				// }
				if ("0".equals(job.getIdentState())) {
					nearJobView.job_att.setVisibility(View.GONE);
				} else {
					nearJobView.job_att.setVisibility(View.VISIBLE);
				}
				String time = job.getJobtime();
				nearJobView.jobtime.setText(TimeUtil.getTimeStr2(time,
						"yyyy-MM-dd"));
				// nearJobView.jobtime.setText(job.getJobtime());
				nearJobView.jobname.setText(job.getJob_name());
				nearJobView.peonumber.setText(job.getDistance() + "米");
				nearJobView.address.setText(job.getAddress());
			}
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
