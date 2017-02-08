package com.zrlh.llkc.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.JobDetail;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.LLKCApplication;
import com.zrlh.llkc.ui.DetailsActivity;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class RecommendedForYouActivity extends BaseActivity {
	public static final String TAG = "recommendedforyou";

	TextView titleTextView;
	ImageButton backiImageView;
	private ListView jobDetailListView;
	private jobDetailAdapter adapter;
	private List<JobDetail> jobDetailList = new ArrayList<JobDetail>();
	ProgressDialog dialog = null;

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, RecommendedForYouActivity.class);
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
		setContentView(R.layout.recommendedforyou);
		init();
		new GetJobDetailTask("0", LlkcBody.CITY_STRING).execute();
	}

	private void init() {
		jobDetailList.clear();
		backiImageView = (ImageButton) findViewById(R.id.back);
		titleTextView = (TextView) findViewById(R.id.title_tv);
		titleTextView.setText(Tools.getStringFromRes(getContext(),
				R.string.recom));
		backiImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		jobDetailListView = (ListView) this
				.findViewById(R.id.recommendedforyou_list);
		adapter = new jobDetailAdapter(getContext(), jobDetailList);
		jobDetailListView.setAdapter(adapter);
		jobDetailListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String idString = jobDetailList.get(position).getJobId();
				String cityString = jobDetailList.get(position).getArea();

				Intent intent = new Intent();
				intent.putExtra("idString", idString);
				intent.putExtra("cityString", cityString);
				intent.setClass(getContext(), DetailsActivity.class);

				startActivity(intent);
			}

		});

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

	private class jobDetailAdapter extends BaseAdapter {
		List<JobDetail> jobDetail;
		LayoutInflater layoutInflater;
		JobView jobView;

		public final class JobView {
			TextView salary; // 薪资
			TextView jobtime;// 发布时间
			TextView jobname;// 招工单位
			// TextView peonumber;//招工人数
			TextView address;// 招工地址
			ImageView job_att;

		}

		private jobDetailAdapter(Context context, List<JobDetail> jobDetail) {
			this.jobDetail = jobDetail;
			layoutInflater = LayoutInflater.from(context);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return jobDetail.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return jobDetail.get(arg0);
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
			if ("0".equals(jobDetail.get(arg0).getSalary_min())) {
				text = jobDetail.get(arg0).getSalary_max();
			} else {
				text = jobDetail.get(arg0).getSalary_min() + "-"
						+ jobDetail.get(arg0).getSalary_max().trim();
			}
			if (text.length() > 14)
				text = text.substring(0, 12) + "...";
			jobView.salary.setText(text);
			// if ("0".equals(jobNewsList.get(arg0).getIdentState())) {
			// jobView.job_att.setVisibility(View.GONE);
			// } else {
			// jobView.job_att.setVisibility(View.INVISIBLE);
			// }
			jobView.jobtime.setText(jobDetail.get(arg0).getFabu_date());
			jobView.jobname.setText(jobDetail.get(arg0).getName());
			jobView.address.setText(jobDetail.get(arg0).getAddress());
			return arg1;
		}
	}

	class GetJobDetailTask extends AsyncTask<Object, Integer, List<JobDetail>> {

		String page;
		String city;
		boolean more;

		public GetJobDetailTask(String page, String city) {
			super();
			this.page = page;
			this.city = city;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getApplicationContext(),
					R.string.loading));
			dialog.show();
		}

		@Override
		protected List<JobDetail> doInBackground(Object... params) {
			if (LLKCApplication.getInstance().isOpenNetwork())
				try {
					return Community.getInstance(getContext())
							.getRecommendJobList(city, page);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			return null;
		}

		@Override
		protected void onPostExecute(List<JobDetail> result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null && result.size() > 0) {
				jobDetailList.clear();
				jobDetailList.addAll(result);
			}
			adapter.notifyDataSetChanged();

			super.onPostExecute(result);
		}
	}

}
