package com.zrlh.llkc.organization;

import java.util.ArrayList;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.organization.Organization.OrganizationPoll;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class HotClassActivity extends BaseActivity {
	public static final String Tag = "hotclass";
	ListView hotListView;
	ArrayList<Organization.OrganizationPoll> hotClassArrayList = new ArrayList<Organization.OrganizationPoll>();
	OrganAdapter organAdapter;
	String clasid, cityName;
	ImageButton backimageImageView, seach;
	TextView title_card;
	ProgressDialog dialog = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				show();
				break;
			case 2:
				Toast.makeText(getApplicationContext(), "没有查询到数据！",
						Toast.LENGTH_SHORT).show();
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
		setContentView(R.layout.hot_class);
		Intent intent = getIntent();
		clasid = intent.getStringExtra("id");
		cityName = intent.getStringExtra("city");
		// oneListRequest();
		new HotclassTask().execute();
		init();
	}

	void init() {
		hotListView = (ListView) findViewById(R.id.hot_class_list);
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		findViewById(R.id.btn).setVisibility(View.GONE);
		title_card = (TextView) findViewById(R.id.title_tv);
		title_card.setText("机构列表");

		hotListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("id", organAdapter.getorgString(arg2));
				intent.setClass(HotClassActivity.this, OrgnizationDetailActivity.class);
				startActivity(intent);
			}
		});
	}

	void show() {
		organAdapter = new OrganAdapter(this, hotClassArrayList);

		hotListView.setAdapter(organAdapter);

	}
	
	class HotclassTask extends AsyncTask<Object, Integer, ArrayList<OrganizationPoll>>{

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
		protected ArrayList<OrganizationPoll> doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext()).hotClassList(cityName, clasid);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(ArrayList<OrganizationPoll> result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null) {
				hotClassArrayList.addAll(result);
				handler.sendEmptyMessage(1);
			}
			else{
				handler.sendEmptyMessage(2);
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

	// void oneListRequest(){
	// OneListRequestMessage oneListRequestMessage = new
	// OneListRequestMessage();
	// oneListRequestMessage.setMsgtype("bigClsList");
	//
	// String resultr = "";
	// try {
	// resultr = oneListRequestMessage.excute();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// Log.d("一次分类——————请求","!!!!"+ resultr);
	// }
	// void oneListRequest(){
	// OneListRequestMessage oneListRequestMessage = new
	// OneListRequestMessage();
	// HashMap<String,String> queryMap = new HashMap<String,String>();
	//
	// queryMap.put("Msgtype","bigClsList");
	// // oneListRequestMessage.setMsgtype("bigClsList");
	//
	// String resultr = "";
	// try {
	// resultr = oneListRequestMessage.excute(queryMap);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// Log.d("一次分类——————请求","!!!!"+ resultr);
	// }
	// void twoListRequest(){
	// TwoListRequestMessage twoListRequestMessage = new
	// TwoListRequestMessage();
	// twoListRequestMessage.setMsgtype("clsList");
	// twoListRequestMessage.setClsId("");
	// }
}
