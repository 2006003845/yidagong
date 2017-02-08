package com.zrlh.llkc.corporate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.funciton.SideBar;
import com.zrlh.llkc.ui.LLKCApplication;
import com.zrlh.llkc.ui.LLKCApplication.LocationResultCallback;
import com.zzl.zl_app.city.CityDB;
import com.zzl.zl_app.net_port.Get2ApiImpl;
import com.zzl.zl_app.net_port.IGet2Api;
import com.zzl.zl_app.net_port.WSError;

public class CityLevel1Activity extends BaseActivity {
	public static final String Tag = "level1";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, CityLevel1Activity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	private CityDB cityDB;

	IGet2Api api;

	private int type = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(Tag, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.citylist);
		Intent intent = getIntent();
		if (intent != null)
			type = intent.getIntExtra("type", 0);
		cityDB = new CityDB(getContext());
		api = new Get2ApiImpl(getContext());
		initView();
		// if (ApplicationData.mCityList.size() == 0)
		new MyTask().execute();
	}

	ListView objListV;

	ListView listv;
	ProgressBar pb;
	SideBar indexBar;
	CityAdapter cityAdapter;

	private TextView titleTV;

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		doLocation();
	}

	public void doLocation() {
		LLKCApplication.getInstance().singleLocation(null);
	}

	public void initView() {
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText(R.string.change_citys);
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		pb = (ProgressBar) this.findViewById(R.id.pb);
		listv = (ListView) this.findViewById(R.id.listv);
		cityAdapter = new CityAdapter(this, ApplicationData.mCityList,
				ApplicationData.mMap, ApplicationData.mSections,
				ApplicationData.mPositions);
		listv.setAdapter(cityAdapter);
		indexBar = (SideBar) findViewById(R.id.sideBar);
		indexBar.setVisibility(View.INVISIBLE);
		indexBar.setListView(listv);
		listv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				City obj = (City) arg0.getAdapter().getItem(arg2);
				if (obj == null)
					return;
				if (arg2 == 0 && !detectionCity(obj.name)) {
					return;
				}
				if (type == 1) {
					Intent intent = getIntent();
					Bundle b = new Bundle();
					b.putSerializable("obj", obj);
					intent.putExtras(b);
					getContext().setResult(RESULT_OK, intent);
					getContext().finish();
					return;
				}
				String[] countrys = obj.getCountys();
				if (countrys != null) {
					ArrayList<Obj> list = new ArrayList<Obj>();
					for (int i = 0; i < countrys.length; i++) {
						list.add(new Obj(countrys[i]));
					}
					Intent intent = getIntent();
					intent.setClass(getContext(), Level2Activity.class);
					Bundle b = new Bundle();
					b.putSerializable("list", list);
					b.putSerializable("key", obj);
					intent.putExtras(b);
					startActivityForResult(intent, 200);
				}
			}
		});
	}

	public boolean detectionCity(String c) {
		if (c == null)
			return false;
		for (City city : ApplicationData.mCityList) {
			if (c.contains(city.name))
				return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 200) {
			if (data != null) {
				Bundle b = data.getExtras();
				if (b != null) {
					getContext().setResult(RESULT_OK, data);
					getContext().finish();
				}
			}

		}
	}

	@Override
	public BaseActivity getContext() {
		// TODO Auto-generated method stub
		return this;
	}

	Handler handler = new Handler();

	class MyTask extends AsyncTask<Void, Void, List<City>> {

		@Override
		protected List<City> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			List<City> clist = new ArrayList<City>();
			if (ApplicationData.mCityList.size() == 0) {
				clist.addAll(cityDB.getAllCity());
			} else
				clist.addAll(ApplicationData.mCityList);
			if (clist.size() == 0) {
				try {
					List<City> list = api.getCityList(Get2ApiImpl.From_Net,
							PlatformAPI.PHPBaseUrl + "MsgType=CityControllers");
					if (list != null && list.size() > 0)
						clist.addAll(list);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (WSError e) {
					e.printStackTrace();
				}
				cityDB.saveCity(ApplicationData.mCityList);
			}
			return clist;
		}

		@Override
		protected void onPostExecute(final List<City> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			// new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			if (ApplicationData.mCityList.size() == 0 && result != null
					&& result.size() > 0)
				ApplicationData.mCityList.addAll(result);
			if (ApplicationData.mCityList != null
					&& ApplicationData.mCityList.size() > 0) {
				prepareCityList();
				// handler.post(new Runnable() {
				//
				// @Override
				// public void run() {
				// TODO
				cityAdapter.notifyDataSetChanged();
				indexBar.setVisibility(View.VISIBLE);
				pb.setVisibility(View.GONE);
				// }
				// });
			}
			// }
			// }).start();
		}
	}

	private static final String FORMAT = "^[a-z,A-Z].*$";

	public boolean prepareCityList() {
		if (ApplicationData.mMap.size() != 0)
			return true;
		for (City city : ApplicationData.mCityList) {
			String firstName = city.getFirstPY();// 第一个字拼音的第一個字母
			if (firstName != null) {
				if (firstName.matches(FORMAT)) {
					if (ApplicationData.mSections.contains(firstName)) {
						ApplicationData.mMap.get(firstName).add(city);
					} else {
						ApplicationData.mSections.add(firstName);
						List<City> list = new ArrayList<City>();
						list.add(city);
						ApplicationData.mMap.put(firstName, list);
					}
				} else {
					if (ApplicationData.mSections.contains("#")) {
						ApplicationData.mMap.get("#").add(city);
					} else {
						ApplicationData.mSections.add("#");
						List<City> list = new ArrayList<City>();
						list.add(city);
						ApplicationData.mMap.put("#", list);
					}
				}
			}
		}
		Collections.sort(ApplicationData.mSections);// 按照字母重新排序
		ApplicationData.mSections.add(0, "所在城市");
		City city = ApplicationData.getCity(LlkcBody.CITY_STRING_Current);
		// City city = new City("-1", LlkcBody.CITY_STRING_Current);
		ApplicationData.mCityList.add(0, city);
		List<City> list = new ArrayList<City>();
		list.add(city);
		ApplicationData.mMap.put("所在城市", list);
		int position = 0;
		for (int i = 0; i < ApplicationData.mSections.size(); i++) {
			ApplicationData.mIndexer.put(ApplicationData.mSections.get(i),
					position);// 存入map中，key为首字母字符串，value为首字母在listview中位�?
			ApplicationData.mPositions.add(position);// 首字母在listview中位置，存入list�?
			position += ApplicationData.mMap.get(
					ApplicationData.mSections.get(i)).size();// 计算下一个首字母在listview的位�?
		}

		return true;
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

}
