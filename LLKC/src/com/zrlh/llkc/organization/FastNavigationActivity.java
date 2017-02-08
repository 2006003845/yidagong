package com.zrlh.llkc.organization;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.City;
import com.zrlh.llkc.corporate.CityLevel1Activity;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.LlkcBody;

public class FastNavigationActivity extends BaseActivity {
	public static final String Tag = "fast_navigation";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, FastNavigationActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(Tag, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.expan_liset);
		initView();
	}

	private TextView titleTV;
	private TextView cityTV;
	private String cityName;

	private void initView() {
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText(R.string.vocational_training);
		this.findViewById(R.id.btn).setVisibility(View.GONE);
		cityTV = (TextView) this.findViewById(R.id.title_tv_position);
		cityName = LlkcBody.CITY_STRING;
		String city = cityName.substring(0, cityName.length() > 3 ? 3
				: cityName.length());
		cityTV.setText(city);
		// 切换城市
		this.findViewById(R.id.title_position).setOnClickListener(
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
		oneArrayListbig = NavigationMethod.oneListRequest();
		twoArrayListBig = NavigationMethod.twoListRequest();
		expandableListView = (ExpandableListView) this.findViewById(R.id.list);
		expandableListView.setGroupIndicator(null);
		myexpandableListAdapter = new MyexpandableListAdapter(getContext(),
				twoArrayListBig, oneArrayListbig);

		expandableListView.setAdapter(myexpandableListAdapter);
		expandableListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,
					int arg2, int arg3, long arg4) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				String id = myexpandableListAdapter.getChildClsid(arg2, arg3);
				intent.putExtra("id", id);
				intent.putExtra("city", cityName);
				intent.setClass(getContext(), HotClassActivity.class);
				startActivity(intent);
				return false;
			}
		});
		expandableListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				Organization.ClassfiyHot obj = (Organization.ClassfiyHot) myexpandableListAdapter
						.getGroup(groupPosition);
				// myexpandableListAdapter.getChildList(groupPosition);
				Intent intent = new Intent();
				intent.putExtra("id", obj.clsId);
				intent.putExtra("city", cityName);
				intent.setClass(getContext(), HotClassActivity.class);
				startActivity(intent);
				return true;
			}
		});
	}

	ExpandableListView expandableListView;
	MyexpandableListAdapter myexpandableListAdapter;
	ArrayList<ArrayList<Organization.ClassfiyHot>> twoArrayListBig;
	ArrayList<Organization.ClassfiyHot> oneArrayListbig;

	
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
						cityName = city.name;
					}
				}
			}
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
