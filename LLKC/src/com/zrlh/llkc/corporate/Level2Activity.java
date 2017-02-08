package com.zrlh.llkc.corporate;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;

public class Level2Activity extends BaseActivity {
	public static final String Tag = "level2";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, Level2Activity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(Tag, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.level1);
		initView();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			@SuppressWarnings("unchecked")
			ArrayList<Obj> list = (ArrayList<Obj>) b.getSerializable("list");
			if (list != null && list.size() > 0) {
				objList.clear();
				objList.addAll(list);
				if (adapter != null)
					adapter.notifyDataSetChanged();
			}
		}
	}

	ListView objListV;
	ArrayList<Obj> objList = new ArrayList<Obj>();

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

	}

	Objdapter adapter;

	private void initView() {
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		objListV = (ListView) this.findViewById(R.id.level1_listv);
		adapter = new Objdapter();
		objListV.setAdapter(adapter);
		objListV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Obj obj = objList.get(arg2);
				Intent intent = getIntent();
				Bundle b = new Bundle();
				b.putSerializable("obj", obj);
				intent.putExtras(b);
				getContext().setResult(RESULT_OK, intent);
				getContext().finish();
				// finishActivity(100);
			}
		});
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
		closeOneAct(Tag);
		super.onBackPressed();
	}

	@Override
	public BaseActivity getContext() {
		// TODO Auto-generated method stub
		return this;
	}

	class Objdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public Objdapter() {
			inflater = (LayoutInflater) getContext().getSystemService(
					android.content.Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return objList.size();
		}

		@Override
		public Object getItem(int position) {
			return objList.get(position);
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
				convertView = inflater.inflate(R.layout.item_tv, null);
				holder = new ViewHolder();

				holder.name = (TextView) convertView.findViewById(R.id.item_tv);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final Obj obj = objList.get(position);
			if (obj != null) {
				holder.name.setText(obj.name);
			}
			return convertView;
		}

		class ViewHolder {
			TextView name;

		}
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
