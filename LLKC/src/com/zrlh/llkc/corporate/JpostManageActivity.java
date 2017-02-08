package com.zrlh.llkc.corporate;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalDb;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.JobDetail;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.DetailsActivity;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

/*
 *岗位管理 
 **/
public class JpostManageActivity extends BaseActivity {

	public static final String Tag = "jpost_manage";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, JpostManageActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	FinalDb db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(Tag, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		db = FinalDb.create(this);
		setContentView(R.layout.jpost_manage);
		initView();
		new GetJPostListTask(false).execute();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		boolean update = intent.getBooleanExtra("update", false);
		if (update)
			new GetJPostListTask(true).execute();
	}

	private TextView titleTV;

	private ListView jpostListV;
	private ImageButton publishBtn;
	Jpostdapter adapter;
	ProgressDialog dialog = null;

	private List<JobDetail> jpostList = new ArrayList<JobDetail>();

	private void initView() {
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText(R.string.jpost_manage);

		publishBtn = (ImageButton) this.findViewById(R.id.btn);
		publishBtn.setImageResource(R.drawable.btn_publish);
		publishBtn.setVisibility(View.VISIBLE);
		publishBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO
				JpostEditActivity.launch(getContext(), getIntent());
			}
		});
		jpostListV = (ListView) this.findViewById(R.id.jpost_listv);
		adapter = new Jpostdapter();
		jpostListV.setAdapter(adapter);
		jpostListV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO
				JobDetail jd = jpostList.get(arg2);
				String city = jd.area;
				if (city != null) {
					String[] strs = city.split("-");
					if (strs.length > 0) {
						city = strs[0];
					}
				}
				Intent intent = new Intent();
				intent.putExtra("idString", jd.jobId);
				intent.putExtra("cityString", city);
				DetailsActivity.launch(getContext(), intent);
			}
		});

	}

	@Override
	public BaseActivity getContext() {
		return this;
	}

	class Jpostdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public Jpostdapter() {
			inflater = (LayoutInflater) getContext().getSystemService(
					android.content.Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return jpostList.size();
		}

		@Override
		public Object getItem(int position) {
			return jpostList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_listv_jpost, null);
				holder = new ViewHolder();

				holder.name = (TextView) convertView
						.findViewById(R.id.item_listv_jpost_tv_name);
				holder.time = (TextView) convertView
						.findViewById(R.id.item_listv_jpost_tv_time);
				holder.stat = (TextView) convertView
						.findViewById(R.id.item_listv_jpost_tv_state);
				holder.edit = (Button) convertView
						.findViewById(R.id.item_listv_jpost_btn_edit);
				holder.delete = (Button) convertView
						.findViewById(R.id.item_listv_jpost_btn_delete);
				holder.oper = (LinearLayout) convertView
						.findViewById(R.id.item_listv_jpost_layout_oper);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final JobDetail jpost = jpostList.get(position);
			if (jpost != null) {
				holder.name.setText(jpost.name);
				// String time = jpost.fabu_date;
				// holder.time.setText(TimeUtil.getTimeStr2(time,
				// "yyyy-MM-dd"));
				 holder.time.setText(jpost.fabu_date);
				holder.oper.setVisibility(View.VISIBLE);

				// if (TimeUtil.isOverDue(jpost.deadline)) {
				// holder.stat.setText(R.string.overdue);
				// } else {
				// holder.stat.setText(R.string.published);
				// }
				if (jpost.online != null && jpost.online.equals("1")) {
					holder.stat.setText(R.string.published);
				} else {
					holder.stat.setText(R.string.overdue);
				}
				holder.edit.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO
						Intent intent = new Intent();
						Bundle b = new Bundle();
						b.putSerializable("jpost", jpost);
						intent.putExtras(b);
						JpostEditActivity.launch(getContext(), intent);
					}
				});
				holder.delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO
						new DeleteJPostTask(jpost.jobId, jpost.area, position)
								.execute();
					}
				});

			}
			return convertView;
		}

		class ViewHolder {
			TextView name;
			TextView time;
			TextView stat;
			Button edit;
			Button delete;
			LinearLayout oper;
		}
	}

	class DeleteJPostTask extends AsyncTask<Object, Integer, Boolean> {

		String rid;
		String city;
		int position;

		public DeleteJPostTask(String rid, String city, int position) {
			super();
			this.rid = rid;
			this.city = city;
			this.position = position;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getApplicationContext(),
					R.string.delete_now));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			String[] strs = city.split("-");
			if (strs.length > 0) {
				city = strs[0];
			}
			try {
				return Community.getInstance(getContext())
						.recruitDelete(LlkcBody.USER_ACCOUNT,
								LlkcBody.PASS_ACCOUNT, city, rid);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result) {
				JobDetail jd = jpostList.get(position);
				db.deleteByWhere(JobDetail.class, "jobId='" + jd.jobId
						+ "' and account='" + LlkcBody.USER_ACCOUNT
						+ "' and type='3'");
				jpostList.remove(position);
				adapter.notifyDataSetChanged();
			} else {
				MyToast.getToast().showToast(
						getContext(),
						Tools.getStringFromRes(getApplicationContext(),
								R.string.delete_fail));
			}
			super.onPostExecute(result);
		}
	}

	class GetJPostListTask extends AsyncTask<Object, Integer, List<JobDetail>> {

		boolean update = false;

		public GetJPostListTask(boolean update) {
			super();
			this.update = update;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getApplicationContext(),
					R.string.loading_now));
			dialog.show();
		}

		@Override
		protected List<JobDetail> doInBackground(Object... params) {
			List<JobDetail> list = null;
			if (!update)
				list = db.findAllByWhere(JobDetail.class, "account='"
						+ LlkcBody.USER_ACCOUNT + "' and type='3'");
			if (list == null || list.size() == 0) {
				try {
					list = Community.getInstance(getContext()).recruitInfoList(
							LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (list != null && list.size() > 0) {
					for (JobDetail jobDetail : list) {
						jobDetail.setAccount(LlkcBody.USER_ACCOUNT);
						jobDetail.setType("3");
						if (existJob(jobDetail.jobId)) {
							db.update(jobDetail);
						} else
							db.save(jobDetail);
					}
				}
			}
			return list;
		}

		public boolean existJob(String rid) {
			List<JobDetail> j = db.findAllByWhere(JobDetail.class, "jobId='"
					+ rid + "' and account='" + LlkcBody.USER_ACCOUNT
					+ "' and type='3'");
			return j != null && j.size() > 0;
		}

		@Override
		protected void onPostExecute(List<JobDetail> result) {
			// Log.d("LLKC", "岗位数据"+result.size());
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null) {
				if (result.size() > 0) {
					jpostList.clear();
					jpostList.addAll(result);
				} else {
					MyToast.getToast().showToast(
							getContext(),
							Tools.getStringFromRes(getApplicationContext(),
									R.string.jpostManage1));
				}
				adapter.notifyDataSetChanged();
			} else {
				MyToast.getToast().showToast(
						getContext(),
						Tools.getStringFromRes(getApplicationContext(),
								R.string.jpostManage2));
			}
			super.onPostExecute(result);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(Tag);
	}

	@Override
	protected void onResume() {
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
