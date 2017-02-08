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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.City;
import com.zrlh.llkc.corporate.Obj;
import com.zrlh.llkc.corporate.PullDownList;
import com.zrlh.llkc.corporate.PullOnItemClickListener;
import com.zrlh.llkc.corporate.Salary;
import com.zrlh.llkc.corporate.Type;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.Jobs;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.TimeUtil;
import com.zzl.zl_app.util.Tools;

public class AdvancedListActivity extends BaseActivity {
	public static final String TAG = "advancelist";

	ImageButton back, friends;
	LinearLayout linearlayout_title, layout_address, layout_jobs, layout_money;
	TextView title_card;
	String keyword = "";
	String job_salary = "";

	Type stairType;
	Type secondaryType;
	private ArrayList<Obj> typeList;
	private ArrayList<Obj> salaryList = new ArrayList<Obj>();
	private ArrayList<Obj> countyList = new ArrayList<Obj>();

	public void initData() {
		// 工种
		if (secondaryType != null) {
			typeList = stairType == null ? ApplicationData
					.findTypeList(secondaryType.level) : ApplicationData
					.findTypeList(stairType);
			if (typeList == null)
				typeList = new ArrayList<Obj>();
		}
		// 工资
		salaryList.clear();
		salaryList.add(new Salary("0", Tools.getStringFromRes(this,
				R.string.unlimit)));
		salaryList.add(new Salary("1", Tools.getStringFromRes(this,
				R.string.salary1)));
		salaryList.add(new Salary("2", Tools.getStringFromRes(this,
				R.string.salary2)));
		salaryList.add(new Salary("3", Tools.getStringFromRes(this,
				R.string.salary3)));
		salaryList.add(new Salary("4", Tools.getStringFromRes(this,
				R.string.salary4)));
		// 区县
		if (city != null && city.getCountys() != null) {
			String[] countys = city.getCountys();
			countyList.clear();
			countyList.add(new Obj("-1", Tools.getStringFromRes(this,
					R.string.unlimit)));
			for (int i = 0; i < countys.length; i++) {
				countyList.add(new Obj("" + i, countys[i]));
			}
		}
	}

	City city;
	String district;
	ImageView triangle;
	private View lvNews_footer;
	OnePagerAdapter onePagerAdapter;// 数据适配器
	List<Jobs.JobNew> jobNewsList = new ArrayList<Jobs.JobNew>();// 为你推荐List
	PullToRefreshListView advancedListView;

	int page = 1;
	private TextView lvNews_foot_more;
	private ProgressBar lvNews_foot_progress;
	ProgressDialog dialog = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				// show();
				onePagerAdapter.notifyDataSetChanged();
				lvNews_foot_more.setText("没有查到您所要的信息，建议您适当放宽查询条件！");
				lvNews_foot_progress.setVisibility(View.INVISIBLE);
				break;
			case 2:
				onePagerAdapter.notifyDataSetChanged();
				lvNews_foot_more.setText("没有更多信息了！");
				lvNews_foot_progress.setVisibility(View.INVISIBLE);
				break;
			case 3:
				onePagerAdapter.notifyDataSetChanged();
				// show();
				lvNews_foot_more.setText("");
				lvNews_foot_progress.setVisibility(View.INVISIBLE);
				break;
			case 4:
				onePagerAdapter.notifyDataSetChanged();
				lvNews_foot_more.setText("");
				lvNews_foot_progress.setVisibility(View.INVISIBLE);

				break;
			default:
				break;
			}

		}
	};

	public void clear() {
		advancedListView.setAdapter(null);
		// onePagerAdapter = null;
		jobNewsList.clear();
	}

	@Override
	public void onBackPressed() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		closeOneAct(TAG);
		clear();
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.advancedlist_activity);
		init();

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

	void show() {

		onePagerAdapter = new OnePagerAdapter(this, jobNewsList);
		advancedListView.setAdapter(onePagerAdapter);
		advancedListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("idString", jobNewsList.get(arg2 - 1)
						.getJobid());
				intent.putExtra("cityString", city == null ? "北京" : city.name);
				intent.setClass(getContext(), DetailsActivity.class);
				startActivity(intent);
			}
		});
		advancedListView.setOnScrollListener(new OnScrollListener() {

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
					new AdvancedRequestTask(true).execute();
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
	}

	PullDownList salaryPullDownList, typePullDownList, countPullDownList;

	void init() {

		// get data
		Intent intent = getIntent();
		city = (City) intent.getSerializableExtra("city");

		keyword = intent.getStringExtra("keyword");
		stairType = (Type) intent.getSerializableExtra("stairType");
		secondaryType = (Type) intent.getSerializableExtra("secondaryType");

		LlkcBody.COLL_CITY_STRING = city == null ? "北京" : city.name;
		if (city == null) {
			city = ApplicationData.getCity(LlkcBody.CITY_STRING);
		}
		initData();
		advancedListView = (PullToRefreshListView) findViewById(R.id.advanced_jobs_list);
		show();

		layout_address = (LinearLayout) findViewById(R.id.layout_address);
		title_card = (TextView) findViewById(R.id.title_card);
		back = (ImageButton) findViewById(R.id.back);
		friends = (ImageButton) findViewById(R.id.friends);
		friends.setVisibility(View.INVISIBLE);
		linearlayout_title = (LinearLayout) findViewById(R.id.linearlayout_title);
		layout_jobs = (LinearLayout) findViewById(R.id.layout_jobs);
		if (secondaryType == null)
			layout_jobs.setVisibility(View.GONE);
		layout_money = (LinearLayout) findViewById(R.id.layout_money);

		lvNews_footer = getLayoutInflater().inflate(R.layout.listview_footer,
				null);

		lvNews_foot_more = (TextView) lvNews_footer
				.findViewById(R.id.listview_foot_more);
		lvNews_foot_progress = (ProgressBar) lvNews_footer
				.findViewById(R.id.listview_foot_progress);
		advancedListView.addFooterView(lvNews_footer, null, false);// 添加底部视图
		// 必须在setAdapter前
		layout_money.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// money_dailog();
				if (salaryPullDownList == null)
					salaryPullDownList = new PullDownList(
							AdvancedListActivity.this,
							AdvancedListActivity.this
									.findViewById(R.id.layout_money),
							layout_money, salaryList, 300);
				salaryPullDownList
						.setPullOnItemClickListener(new PullOnItemClickListener() {

							@Override
							public void onPullItemClick(AdapterView<?> parent,
									int position) {
								if (position < salaryList.size()) {
									Obj salary = (Obj) parent.getAdapter()
											.getItem(position);
									job_salary = (salary != null && !salary.id
											.equals("-1")) ? salary.id : "";
									new AdvancedRequestTask(false).execute();
								}
							}
						});
				salaryPullDownList.popupWindwShowing();
			}
		});
		layout_jobs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// job_dailog();
				if (typePullDownList == null)
					typePullDownList = new PullDownList(
							AdvancedListActivity.this,
							AdvancedListActivity.this
									.findViewById(R.id.layout_jobs),
							layout_jobs, typeList, 300);
				typePullDownList
						.setPullOnItemClickListener(new PullOnItemClickListener() {

							@Override
							public void onPullItemClick(AdapterView<?> parent,
									int position) {
								String level = secondaryType.level;
								// if (position < typeList.size()) {
								secondaryType = (Type) (Obj) parent
										.getAdapter().getItem(position);

								secondaryType.level = level;
								title_card.setText((district == null
										|| district.equals("") ? city.name
										: district) + "-" + secondaryType.name);
								new AdvancedRequestTask(false).execute();

							}
							// }
						});
				typePullDownList.popupWindwShowing();
			}
		});
		layout_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// city_dailog();
				if (countPullDownList == null)
					countPullDownList = new PullDownList(
							AdvancedListActivity.this,
							AdvancedListActivity.this
									.findViewById(R.id.layout_address),
							layout_address, countyList, 300);
				countPullDownList
						.setPullOnItemClickListener(new PullOnItemClickListener() {

							@Override
							public void onPullItemClick(AdapterView<?> parent,
									int position) {
								// if (position < countyList.size()) {
								Obj county = (Obj) parent.getAdapter().getItem(
										position);
								if (county != null) {
									String district_city = county.name;
									if ("不限".equals(district_city)) {
										district = "";
									} else {
										district = district_city;
									}
									// title_card.setText(district + "-"
									// + secondaryType.name);
								}
								LlkcBody.COLL_CITY_STRING = city == null ? "北京"
										: city.name;
								new AdvancedRequestTask(false).execute();
								// }
							}
						});
				countPullDownList.popupWindwShowing();
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		if ("".equals(keyword) || keyword == null) {
			if (secondaryType == null)
				title_card.setText(city == null || city.name.equals("") ? ""
						: (city.name + "-") + "");
			else
				title_card.setText(city == null || city.name.equals("") ? ""
						: (city.name + "-")
								+ (stairType == null ? ""
										: (stairType.name + "-"))
								+ secondaryType.name);
		} else {

			title_card.setText(city == null || city.name.equals("") ? ""
					: (city.name + "-") + keyword);
		}

		linearlayout_title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
		friends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getContext(), "暂无此功能！", Toast.LENGTH_SHORT)
						.show();
			}
		});
		title_card.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent();
				// intent.setClass(AdvancedListActivity.this,
				// AdvancedSearchActivity.class);
				// intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				// startActivity(intent);
				onBackPressed();
			}
		});
		new AdvancedRequestTask(false).execute();
	}

	class AdvancedRequestTask extends
			AsyncTask<Object, Integer, List<Jobs.JobNew>> {

		boolean more;

		public AdvancedRequestTask(boolean more) {
			this.more = more;
			if (!more) {
				page = 1;
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(AdvancedListActivity.this);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.loading));
			if (page < 2)
				dialog.show();
		}

		@Override
		protected List<Jobs.JobNew> doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			try {
				String citys = city == null ? "" : city.name;
				String classone = stairType == null ? (secondaryType == null ? ""
						: secondaryType.level)
						: stairType.id;
				String classtwo = secondaryType == null ? ""
						: (secondaryType.id.equals("0") ? "" : secondaryType.id);
				String districts = district == null ? "" : district;
				return Community.getInstance(getContext()).getAdvancedList(
						citys, String.valueOf(page), classone, classtwo,
						job_salary, districts, keyword);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Jobs.JobNew> result) {
			// TODO Auto-generated method stub
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result == null || result.size() == 0) {
				if (page > 1) {
					handler.sendEmptyMessage(2);

				} else {
					handler.sendEmptyMessage(1);
				}
			} else {
				if (!more)
					jobNewsList.clear();
				jobNewsList.addAll(result);
				if (page > 1) {
					handler.sendEmptyMessage(4);

				} else {
					handler.sendEmptyMessage(3);
				}
			}
			super.onPostExecute(result);
		}

	}

	private class OnePagerAdapter extends BaseAdapter {
		List<Jobs.JobNew> jobNewsList;
		LayoutInflater layoutInflater;
		JobView jobView;

		public final class JobView {
			TextView salary; // 薪资
			TextView jobtime;// 发布时间
			TextView jobname;// 招工单位
			TextView peonumber;// 招工人数
			TextView address;// 招工地址
			ImageView job_att;

		}

		private OnePagerAdapter(Context context, List<Jobs.JobNew> jobNewsList) {
			this.jobNewsList = jobNewsList;
			layoutInflater = LayoutInflater.from(context);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return jobNewsList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return jobNewsList.get(arg0);
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
				jobView = new JobView();
				arg1 = layoutInflater.inflate(R.layout.pagerframe_one_list,
						null);
				jobView.salary = (TextView) arg1.findViewById(R.id.salary);
				jobView.jobtime = (TextView) arg1.findViewById(R.id.jobtime);
				jobView.jobname = (TextView) arg1.findViewById(R.id.jobname);
				jobView.job_att = (ImageView) arg1.findViewById(R.id.job_att);
				jobView.address = (TextView) arg1.findViewById(R.id.address);

				arg1.setTag(jobView);
			} else {
				jobView = (JobView) arg1.getTag();

			}

			String text = "";
			if ("0".equals(jobNewsList.get(arg0).getSalary_min())) {
				text = jobNewsList.get(arg0).getSalary_max();
			} else {
				text = jobNewsList.get(arg0).getSalary_min() + "-"
						+ jobNewsList.get(arg0).getSalary_max().trim();
			}
			if (text.length() > 14)
				text = text.substring(0, 12) + "...";
			jobView.salary.setText(text);
			// if ("0".equals(jobNewsList.get(arg0).getIdentState())) {
			// jobView.job_att.setVisibility(View.GONE);
			// } else {
			// jobView.job_att.setVisibility(View.VISIBLE);
			// }
			String time = jobNewsList.get(arg0).getJobtime();
			jobView.jobtime.setText(TimeUtil.getTimeStr2(time, "yyyy-MM-dd"));
			jobView.jobname.setText(jobNewsList.get(arg0).getJobname());

			jobView.address.setText(jobNewsList.get(arg0).getAddress());
			return arg1;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (salaryPullDownList != null)
			salaryPullDownList.clear();
		if (typePullDownList != null)
			typePullDownList.clear();
		if (countPullDownList != null)
			countPullDownList.clear();

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
