package com.zrlh.llkc.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.common.Log;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.City;
import com.zrlh.llkc.corporate.CityLevel1Activity;
import com.zrlh.llkc.corporate.Obj;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.AdvancedListActivity;

public class AllJobActivity extends BaseActivity {

	public static final String TAG = "alljob";
	private TableLayout tLayout;
	private LayoutInflater inflate;
	/**
	 * 列
	 */
	private int COL = 3;
	TextView titleTextView, cityTV;
	ImageButton backiImageView;
	private City city;
	String jobnameString, jobidString, jobtwoidString,
			jobtwonameString;
	private int[] itemNmaeId = { R.id.alljob_item_name1,
			R.id.alljob_item_name2, R.id.alljob_item_name3 };
	private int[] itemNmaeId2 = { R.id.alljob_item2_name1,
			R.id.alljob_item2_name2, R.id.alljob_item2_name3 };

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, AllJobActivity.class);
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
		setContentView(R.layout.alljob);
		initView();
	}

	private void initView() {
		clickPosition = -1;
		jobidString = "";
		jobnameString = "";
		titleTextView = (TextView) findViewById(R.id.title_alljob_card);
		titleTextView.setText(R.string.alljob);
		backiImageView = (ImageButton) findViewById(R.id.title_alljob_back);
		backiImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		city = ApplicationData.getCity(LlkcBody.CITY_STRING);
		cityTV = (TextView) this.findViewById(R.id.title_alljob_tv_position);
		if (city != null && city.name != null) {
			String cityName = city.name.substring(0, city.name.length() > 3 ? 3
					: city.name.length());
			cityTV.setText(cityName);
		}
		// 切換城市
		findViewById(R.id.title_alljob_relayout).setOnClickListener(
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

		inflate = LayoutInflater.from(this);
		tLayout = (TableLayout) this.findViewById(R.id.alljob_tablayout);

		int j = 0;
		int m = 0;
		int len = ApplicationData.typeLists.size();
		int length = len / COL;
		int lens = len % COL;
		boolean bol = false;
		if (lens != 0) {
			length += 1;
			bol = true;
		}
		for (; j < length; j++) {
			int n = j;
			int l = m + 100;
			View view = inflate.inflate(R.layout.alljob_item, null);
			View line = view.findViewById(R.id.alljob_item_line);
			if (j == length - 1)
				line.setVisibility(View.GONE);
			int length1 = COL;
			if (bol) {
				if (j == length - 1) {
					length1 = lens;
				}
			}
			for (int col = 0; col < length1; col++) {
				TextView name = (TextView) view.findViewById(itemNmaeId[col]);
				name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.alljob_item_trans);
				HashMap<Obj, ArrayList<Obj>> objs = ApplicationData.typeLists
						.get(j * COL + col);
				Set<Obj> sets = objs.keySet();
				Obj obj = sets.iterator().next();
				if (obj != null) {
					name.setText(obj.name);
					name.setTag(l);
				}
				name.setOnClickListener(new TxtClick(m, n, name, obj));
				m++;
			}
			TableLayout tl = (TableLayout) view
					.findViewById(R.id.alljob_item_tablayout);
			tl.setTag(n);
			tLayout.addView(view);
		}
	}

	private int clickPosition;
	private TableLayout relTLayout = null;
	private TextView txt = null;

	class TxtClick implements OnClickListener {
		private int mPosition;
		private int mTag;
		private TextView mTxtView;
		private Obj obj;

		public TxtClick(int position, int tag, TextView txtView, Obj obj) {
			mPosition = position;
			mTag = tag;
			mTxtView = txtView;
			this.obj = obj;
		}

		@Override
		public void onClick(View v) {
			if (relTLayout == null) {
				relTLayout = (TableLayout) tLayout.findViewWithTag(mTag);
				relTLayout.setVisibility(View.VISIBLE);
				jobidString = obj.id;
				jobnameString = obj.name;
				initChildView(mPosition);
				txt = mTxtView;
				txt.setBackgroundColor(0xffffd1b2);
				txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						R.drawable.alljob_item_arrow);
			} else {
				if (relTLayout.getVisibility() == View.VISIBLE) {
					relTLayout.setVisibility(View.GONE);
					txt.setBackgroundColor(0xffd4e6ff);
					txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
							R.drawable.alljob_item_trans);

				} else if (relTLayout.getVisibility() == View.GONE) {
					if (clickPosition == mPosition) {
						relTLayout.removeAllViews();
						relTLayout.setVisibility(View.VISIBLE);
						initChildView(mPosition);
						txt.setBackgroundColor(0xffffd1b2);
						txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
								R.drawable.alljob_item_arrow);
					}
				}
				if (clickPosition != mPosition) {
					relTLayout.removeAllViews();
					relTLayout = null;
					relTLayout = (TableLayout) tLayout.findViewWithTag(mTag);
					relTLayout.setVisibility(View.VISIBLE);
					jobidString = obj.id;
					jobnameString = obj.name;
					initChildView(mPosition);
					txt = mTxtView;
					txt.setBackgroundColor(0xffffd1b2);
					txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
							R.drawable.alljob_item_arrow);
				}
			}

			clickPosition = mPosition;
		}

	}

	private ArrayList<Obj> childList;

	private void initChildView(int position) {

		HashMap<Obj, ArrayList<Obj>> objs = ApplicationData.typeLists
				.get(position);
		Set<Obj> sets = objs.keySet();
		final Obj obj = sets.iterator().next();
		childList = objs.get(obj);

		Log.d("lm", "--------------size=" + childList.size());

		int j = 0;
		int len = childList.size();
		int length = len / COL;
		int lens = len % COL;
		boolean bol = false;
		if (lens != 0) {
			length += 1;
			bol = true;
		}
		for (; j < length; j++) {
			View view = inflate.inflate(R.layout.alljob_item2, null);
			View line = view.findViewById(R.id.alljob_item2_line);
			if (j == length - 1)
				line.setVisibility(View.GONE);
			int length1 = COL;
			if (bol) {
				if (j == length - 1) {
					length1 = lens;
				}
			}
			for (int col = 0; col < length1; col++) {
				TextView name = (TextView) view.findViewById(itemNmaeId2[col]);
				// name.setOnClickListener(new TxtClick(m, n, name));
				final Obj obj2 = childList.get(j * COL + col);
				if (obj2 != null) {
					name.setText(obj2.name);
				}
				name.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						jobtwoidString = obj2.id;
						jobtwonameString = obj2.name;
						Intent intent = new Intent();
						intent.putExtra("jobnum", jobidString);
						intent.putExtra("jobname", jobnameString);
						intent.putExtra("jobnum2", jobtwoidString);
						intent.putExtra("jobname2", jobtwonameString);
						intent.putExtra("city", city);
						intent.putExtra("stairType", obj);
						intent.putExtra("secondaryType", obj2);
						intent.putExtra("keyword", "");
						intent.setClass(AllJobActivity.this,
								AdvancedListActivity.class);
						startActivity(intent);
					}
				});
			}
			relTLayout.addView(view);
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
						String c = city.name
								.substring(0, city.name.length() > 3 ? 3
										: city.name.length());
						cityTV.setText(c);
						this.city = city;
					}
				}
			}
		}
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

}
