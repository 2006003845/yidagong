package com.zrlh.llkc.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.tsz.afinal.FinalDb;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.JobDetail;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.DetailsActivity;
import com.zzl.zl_app.util.TimeUtil;
import com.zzl.zl_app.util.Tools;

public class RecordJPostActivity extends BaseActivity {
	public static final String TAG = "record_jpost";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, RecordJPostActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	public String type;
	private FinalDb finalDb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		finalDb = FinalDb.create(getContext());
		Bundle b = getIntent().getExtras();
		if (b != null)
			type = b.getString("type");
		setContentView(R.layout.record_jpost);
		initView();
		new InitDataTask(type == null || type.equals("") ? "0" : type)
				.execute();
	}

	private TextView titleTV;
	private ListView jpostListV;
	private List<JobDetail> jList = new ArrayList<JobDetail>();
	private ColleAdapter adapter;
	private ImageButton clearBtn;

	private void initView() {
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText(R.string.history_record);
		if (type.equals("0")) {
			titleTV.setText(R.string.history_record);
		} else if (type.equals("1")) {
			titleTV.setText(R.string.contact_record);
		} else {
			titleTV.setText(R.string.collect_record);
		}
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		clearBtn = (ImageButton) this.findViewById(R.id.btn);
		clearBtn.setVisibility(View.VISIBLE);
		clearBtn.setImageResource(R.drawable.msg_btn_clear);

		clearBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finalDb.deleteByWhere(JobDetail.class, "account='"
						+ LlkcBody.USER_ACCOUNT + "' and type='" + type + "'");
				jList.clear();
				adapter.notifyDataSetChanged();
			}
		});
		jpostListV = (ListView) this.findViewById(R.id.record_jpost_listv);
		adapter = new ColleAdapter(getContext(), jList);
		jpostListV.setAdapter(adapter);
		jpostListV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				JobDetail j = (JobDetail) arg0.getAdapter().getItem(arg2);
				Intent intent = new Intent();
				intent.putExtra("idString", j.jobId);
				intent.putExtra("cityString", LlkcBody.CITY_STRING);
				Bundle b = new Bundle();
				b.putSerializable("job", j);
				intent.putExtras(b);
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

	private class ColleAdapter extends BaseAdapter {
		List<JobDetail> collJobs_list;
		LayoutInflater layoutInflater;
		CollView collView;

		public final class CollView {
			TextView coll_name; //
			TextView coll_date;//
			TextView coll_job_name;//
			TextView coll_num;//

		}

		private ColleAdapter(Context context, List<JobDetail> collJobs_list) {
			this.collJobs_list = collJobs_list;
			layoutInflater = LayoutInflater.from(context);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return collJobs_list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return collJobs_list.get(arg0);
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
				collView = new CollView();
				arg1 = layoutInflater.inflate(R.layout.collction_list_item,
						null);
				collView.coll_date = (TextView) arg1
						.findViewById(R.id.coll_date);
				collView.coll_job_name = (TextView) arg1
						.findViewById(R.id.coll_job_name);
				collView.coll_name = (TextView) arg1
						.findViewById(R.id.coll_name);
				collView.coll_num = (TextView) arg1.findViewById(R.id.coll_num);
				arg1.setTag(collView);
			} else {
				collView = (CollView) arg1.getTag();

			}
			JobDetail job = collJobs_list.get(arg0);
			String time = job.getFabu_date();
			if (time.contains("-"))
				collView.coll_date.setText(time);
			else
				collView.coll_date.setText(TimeUtil.getTimeStr2(time,
						"yyyy-MM-dd"));
			collView.coll_job_name.setText(job.getName());
			collView.coll_name.setText(job.getCorporate_name());
			collView.coll_num.setText(Tools.getStringFromRes(
					getApplicationContext(), R.string.recruit_num)
					+ job.getPeonumber());
			return arg1;
		}
	}

	class InitDataTask extends AsyncTask<Object, Integer, Boolean> {
		String type;

		public InitDataTask(String type) {
			this.type = type;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Boolean doInBackground(Object... params) {
			List<JobDetail> list = finalDb.findAllByWhere(JobDetail.class,
					"account='" + LlkcBody.USER_ACCOUNT + "' and type='" + type
							+ "'");
			if (list != null) {
				jList.clear();
				jList.addAll(list);
				Collections.reverse(jList);
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean obj) {
			adapter.notifyDataSetChanged();
			super.onPostExecute(obj);
		}
	}
}
