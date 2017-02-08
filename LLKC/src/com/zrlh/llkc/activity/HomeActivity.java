package com.zrlh.llkc.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.tsz.afinal.FinalBitmap;

import org.json.JSONException;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.corporate.AuthenticationResultActivity;
import com.zrlh.llkc.corporate.City;
import com.zrlh.llkc.corporate.CityLevel1Activity;
import com.zrlh.llkc.corporate.CorporateActivity;
import com.zrlh.llkc.corporate.JpostManageActivity;
import com.zrlh.llkc.corporate.Obj;
import com.zrlh.llkc.corporate.PeriodicalsActivity;
import com.zrlh.llkc.corporate.Type;
import com.zrlh.llkc.corporate.WebViewActivity;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.organization.FastNavigationActivity;
import com.zrlh.llkc.ui.AdvancedListActivity;
import com.zrlh.llkc.ui.CustomLoadingDialog;
import com.zrlh.llkc.ui.DetailsActivity;
import com.zrlh.llkc.ui.JobFairControllersActivity;
import com.zrlh.llkc.ui.LLKCApplication;
import com.zrlh.llkc.ui.LLKCApplication.LocationResultCallback;
import com.zrlh.llkc.ui.LoginActivity;
import com.zrlh.llkc.ui.NearJobsActivity;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.entity.ImageItem;
import com.zzl.zl_app.util.MyCountDownTimer;
import com.zzl.zl_app.util.Tools;
import com.zzl.zl_app.widget.PointLinearLayout;

public class HomeActivity extends BaseActivity {
	public static final String TAG = "home";
	public static HomeActivity mInstance;
	private TextView cityTV;
	private ViewPager viewPager; // android-support-v4中的滑动组件
	private List<ImageView> imageViews; // 滑动的图片集合
	MyAdapter adapter;
	private PointLinearLayout pointLayout;
	private TextView tv_title;
	private int currentItem = 0; // 当前图片的索引号
	private ScheduledExecutorService scheduledExecutorService;
	private ArrayList<String> titles = new ArrayList<String>(); // 图片标题
	public int downLoadNum;// 正在load的任务数量
	public CustomLoadingDialog progressDialog;
	private int[] hotjobId = { R.id.home_hotsearch_button1,
			R.id.home_hotsearch_button2, R.id.home_hotsearch_button3,
			R.id.home_hotsearch_button4, R.id.home_hotsearch_button5,
			R.id.home_hotsearch_button6, R.id.home_hotsearch_button7,
			R.id.home_hotsearch_button8 };
	private Button[] hotjob = new Button[8];

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:
				viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
				break;
			case 2:
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.home);
		mInstance = this;
		if (!LLKCApplication.getInstance().isOpenNetwork()) {
			netWorkToast();
		}
		initView();

		((LLKCApplication) getApplication())
				.singleLocation(new LocationResultCallback() {

					@Override
					public void getLocationResult(BDLocation location,
							String city, String district, double latitude,
							double longitude) {
						new InitDataTask().execute();
						new HotJobTask(city).execute();
						if (!TextUtils.isEmpty(city))
							((LLKCApplication) getApplication())
									.closeLocationCallBack();
						if (LlkcBody.CITY_STRING_Current != null
								&& !LlkcBody.CITY_STRING_Current.equals("")) {
							String tt = LlkcBody.CITY_STRING_Current.length() > 3 ? LlkcBody.CITY_STRING_Current
									.substring(0, 3)
									: LlkcBody.CITY_STRING_Current;
							curTV.setText("(" + tt + ")");
						}
					}
				});
	}

	private TextView popTV, resfreshTV;
	private TextView curTV;

	private void initView() {
		for (int i = 0; i < 8; i++) {
			hotjob[i] = (Button) this.findViewById(hotjobId[i]);
		}
		curTV = (TextView) this.findViewById(R.id.home_nearjob_tv_cur);
		popTV = (TextView) this.findViewById(R.id.home_tv_pop);
		if (LLKCApplication.getInstance().getOperCity() != null) {
			LlkcBody.CITY_STRING = LLKCApplication.getInstance().getOperCity();
			// LlkcBody.CITY_STRING_Current =
			// DemoApplication.getInstance().getOperCity();
			popTV.setVisibility(View.VISIBLE);
			popTV.setText(Tools.getStringFromRes(getContext(),
					R.string.home_pop1)
					+ LlkcBody.CITY_STRING
					+ Tools.getStringFromRes(getContext(), R.string.home_pop2));
			countDownTimer.start();
		}
		resfreshTV = (TextView) this.findViewById(R.id.home_tv_refresh);
		if (!LLKCApplication.getInstance().isOpenNetwork()) {
			resfreshTV.setVisibility(View.VISIBLE);
			resfreshDownTimer.start();
		}
		cityTV = (TextView) this.findViewById(R.id.home_title_tv_position);
		if (LlkcBody.CITY_STRING != null) {
			String city = LlkcBody.CITY_STRING.substring(0,
					LlkcBody.CITY_STRING.length() > 3 ? 3
							: LlkcBody.CITY_STRING.length());
			cityTV.setText(city);
		}
		// 切换城市
		this.findViewById(R.id.home_title_position).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getBaseContext(),
								CityLevel1Activity.class);
						intent.putExtra("type", 1);
						startActivityForResult(intent, 101);
						MobclickAgent.onEvent(getContext(), "event_cutcity");
					}
				});
		// 搜索岗位~
		this.findViewById(R.id.home_title_seach).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (!LLKCApplication.getInstance().isOpenNetwork()) {
							netWorkToast();
							return;
						}
						if (!detectionCity())
							changeCity();
						else
							SearchJobActivity.launch(getContext(), null);
						MobclickAgent.onEvent(getContext(),
								"event_alljob_search");
					}
				});
		// 招聘会
		this.findViewById(R.id.home_relayout_jobfair).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (!LLKCApplication.getInstance().isOpenNetwork()) {
							netWorkToast();
							return;
						}
						if (!detectionCity())
							changeCity();
						else {
							startActivity(new Intent(getContext(),
									JobFairControllersActivity.class));
						}
						MobclickAgent.onEvent(getContext(), "event_jobfair");
					}
				});
		// 找培训
		this.findViewById(R.id.home_relayout_fortrain).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						MobclickAgent.onEvent(getContext(), "event_findwork");
						if (!LLKCApplication.getInstance().isOpenNetwork()) {
							netWorkToast();
							return;
						}
						if (!detectionCity())
							changeCity();
						else {
							// TODO Auto-generated method stub
							FastNavigationActivity.launch(getContext(), null);
						}
						MobclickAgent.onEvent(getContext(), "event_findwork");
					}
				});
		// 新鲜事
		this.findViewById(R.id.home_relayout_newsomething).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						MobclickAgent.onEvent(getContext(), "event_novelty");
						// TODO Auto-generated method stub
						if (!LLKCApplication.getInstance().isOpenNetwork()) {
							netWorkToast();
							return;
						}
						PeriodicalsActivity.launch(getContext(), null);

					}
				});
		// 发布岗位
		this.findViewById(R.id.home_relayout_postjob).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (!LLKCApplication.getInstance().isOpenNetwork()) {
							netWorkToast();
							return;
						}
						if (LlkcBody.isLogin()) {
							switch (LlkcBody.getAuthStat()) {
							case LlkcBody.State_Auth_Success:
								JpostManageActivity.launch(getContext(),
										new Intent());
								break;
							case LlkcBody.State_Auth_Ing:
								AuthenticationResultActivity.launch(
										getContext(), new Intent());
								break;
							case LlkcBody.State_Auth_No:
								// AuthenticationEditActivity.launch(
								// getContext(), new Intent());
								CorporateActivity.launch(getContext(),
										new Intent());
								break;
							}
						} else {
							// MyToast.getToast().showToast(getContext(),
							// "您尚未登陆!");
							LoginActivity.launch(getContext(), new Intent());
						}
						MobclickAgent.onEvent(getContext(), "event_postjob");
					}
				});

		// 查找附近
		this.findViewById(R.id.home_nearjob).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (!LLKCApplication.getInstance().isOpenNetwork()) {
							netWorkToast();
							return;
						}
						if (!detectionCity())
							changeCity();
						else {
							LlkcBody.DYNAMIC_LAT = LlkcBody.LAT;
							LlkcBody.DYNAMIC_LNG = LlkcBody.LNG;
							Intent intent = new Intent();
							intent.putExtra("lat", String.valueOf(LlkcBody.LAT));
							intent.putExtra("lng", String.valueOf(LlkcBody.LNG));
							intent.putExtra("From", NearJobsActivity.From_Mine);
							intent.setClass(getContext(),
									NearJobsActivity.class);
							startActivity(intent);
						}
						MobclickAgent.onEvent(getContext(), "event_nearjob");
					}
				});
		initHotJobTypes();

		// 所有岗位
		this.findViewById(R.id.home_alljob_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (!LLKCApplication.getInstance().isOpenNetwork()) {
							netWorkToast();
							return;
						}
						if (!detectionCity())
							changeCity();
						else
							AllJobActivity.launch(getContext(), null);

						MobclickAgent.onEvent(getContext(), "event_alljob");
					}
				});

		// 为您推荐
		this.findViewById(R.id.home_recom).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (!LLKCApplication.getInstance().isOpenNetwork()) {
							netWorkToast();
							return;
						}
						if (!detectionCity())
							changeCity();
						else {
							RecommendedForYouActivity
									.launch(getContext(), null);
							findViewById(R.id.home_recom_imgnew).setVisibility(
									View.GONE);
						}
						MobclickAgent.onEvent(getContext(),
								"event_rencomforyou");
					}
				});

		// 轮播图相关
		imageViews = new ArrayList<ImageView>();
		for (int i = 0; i < ApplicationData.imgItemList.size(); i++) {
			final ImageItem item = ApplicationData.imgItemList.get(i);
			titles.add(item.name);
			ImageView imageView = new ImageView(getContext());
			ImageCache.getInstance().loadImg(item.img, imageView,
					R.drawable.bg_default);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!LLKCApplication.getInstance().isOpenNetwork()) {
						netWorkToast();
						return;
					}
					MobclickAgent.onEvent(getContext(), "event_shuffling");
					String url = item.url;
					String type = item.type;
					if ("1".equals(type)) {
						// TODO 岗位
						Intent intent = new Intent();
						intent.putExtra("idString", item.reid);
						intent.putExtra("cityString", item.city);
						DetailsActivity.launch(getContext(), intent);
					} else if ("2".equals(type)) {
						// TODO 活动
						Intent intent = new Intent();
						Bundle b = new Bundle();
						if (url != null && !url.equals("")) {
							b.putString("name", item.name);
							b.putString("url", url);
							intent.putExtras(b);
							WebViewActivity.launch(getContext(), intent);
						}
					}
				}
			});
			imageViews.add(imageView);
		}

		pointLayout = (PointLinearLayout) this.findViewById(R.id.pointlayout);
		pointLayout.addViews(getContext(), ApplicationData.imgItemList.size(),
				-15);
		pointLayout.setPageIndex(0);

		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_title.setText(titles.size() > 0 ? titles.get(0) : "");//

		viewPager = (ViewPager) this.findViewById(R.id.vp);
		adapter = new MyAdapter();
		viewPager.setAdapter(adapter);// 设置填充ViewPager页面的适配器
		// 设置一个监听器，当ViewPager中的页面改变时调用
		viewPager.setOnPageChangeListener(new MyPageChangeListener());

		viewPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View paramView, MotionEvent event) {
				((ViewParent) viewPager.getParent())
						.requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
	}

	private void netWorkToast() {
		MyToast.getToast().showToast(getContext(), "无法链接到网络，请检查网络配置", 50);
	}

	public void initData() {
		if (ApplicationData.recommendTypeList.size() == 0)
			new HotJobTask(LlkcBody.CITY_STRING).execute();
		if (ApplicationData.imgItemList.size() == 0)
			new InitDataTask().execute();
		else
			adapter.notifyDataSetChanged();

	}

	public void initHotJobTypes() {
		for (int i = 0; i < 8; i++) {
			if (hotjob[i] == null)
				hotjob[i] = (Button) this.findViewById(hotjobId[i]);
			if (hotjob[i] != null) {
				if (i < ApplicationData.recommendTypeList.size()) {
					final Type type = (Type) ApplicationData.recommendTypeList
							.get(i);
					hotjob[i].setText(type.name);
					hotjob[i].setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (!LLKCApplication.getInstance().isOpenNetwork()) {
								netWorkToast();
								return;
							}
							if (!detectionCity())
								changeCity();
							else {
								Intent intent = new Intent();
								intent.putExtra("jobnum", type.level);
								intent.putExtra("jobname", type.name);
								intent.putExtra("jobnum2", type.id);
								City city = ApplicationData
										.getCity(LlkcBody.CITY_STRING);
								intent.putExtra("city", city);
								// intent.putExtra("cityidString",
								// cityidString());
								// intent.putExtra("stairType", obj);
								intent.putExtra("secondaryType", type);
								intent.putExtra("keyword", "");
								intent.setClass(getContext(),
										AdvancedListActivity.class);
								startActivity(intent);
							}
							MobclickAgent.onEvent(getContext(), "event_hotjob");
						}
					});
				} else
					hotjob[i].setText("");
			}
		}
	}

	// TODO
	public void initImgItemsView() {
		if (ApplicationData.imgItemList.size() == 0)
			return;
		titles.clear();
		if (imageViews != null && imageViews.size() != 0)
			imageViews.clear();
		// 初始化图片资源
		for (int i = 0; i < ApplicationData.imgItemList.size(); i++) {
			final ImageItem item = ApplicationData.imgItemList.get(i);
			titles.add(item.name);
			ImageView imageView = new ImageView(getContext());
			ImageCache.getInstance().loadImg(item.img, imageView,
					R.drawable.bg_default);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!LLKCApplication.getInstance().isOpenNetwork()) {
						netWorkToast();
						return;
					}
					MobclickAgent.onEvent(getContext(), "event_shuffling");
					String url = item.url;
					String type = item.type;
					if ("1".equals(type)) {
						// TODO 岗位
						Intent intent = new Intent();
						intent.putExtra("idString", item.reid);
						intent.putExtra("cityString", item.city);
						DetailsActivity.launch(getContext(), intent);
					} else if ("2".equals(type)) {
						// TODO 活动
						Intent intent = new Intent();
						Bundle b = new Bundle();
						if (url != null && !url.equals("")) {
							b.putString("name", item.name);
							b.putString("url", url);
							intent.putExtras(b);
							WebViewActivity.launch(getContext(), intent);
						}
					}
				}
			});
			imageViews.add(imageView);
		}
		pointLayout.removeAllViews();
		pointLayout.addViews(getContext(), ApplicationData.imgItemList.size(),
				-15);
		pointLayout.setPageIndex(0);

		tv_title.setText(titles.size() > 0 ? titles.get(0) : "");
		adapter.notifyDataSetChanged();
	}

	public boolean detectionCity() {
		if (LlkcBody.CITY_STRING == null)
			return false;
		if (ApplicationData.mCityList == null) {
			return false;
		}
		for (City city : ApplicationData.mCityList) {
			if (city == null || city.name == null)
				continue;
			if (LlkcBody.CITY_STRING.contains(city.name)
					|| city.name.contains(LlkcBody.CITY_STRING))
				return true;
		}
		return false;
	}

	public void changeCity() {
		showMsgDialog("", R.layout.layout_prompt, getContext(), "", "");
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
	public OnClickListener setPositiveClickListener(final AlertDialog dialog,
			int layoutId, String id) {
		if (layoutId == R.layout.layout_prompt) {
			dialog.findViewById(R.id.prompt_btn_ok).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							Intent intent = new Intent(getBaseContext(),
									CityLevel1Activity.class);
							intent.putExtra("type", 1);
							startActivityForResult(intent, 101);
						}
					});
		}
		return null;
	}

	@Override
	public OnClickListener setNegativeClickListener(AlertDialog dialog,
			int layoutId, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(TAG);
		initData();
		if (LlkcBody.COLL_CITY_STRING == null
				|| "".equals(LlkcBody.COLL_CITY_STRING)) {
			LlkcBody.COLL_CITY_STRING = LlkcBody.CITY_STRING;
		}
		if (LlkcBody.JOBS_ID_STRING == null
				|| "".equals(LlkcBody.JOBS_ID_STRING)) {
			LlkcBody.JOBS_ID_STRING = "1";
		}
		if (LlkcBody.JOBS_STRING == null || "".equals(LlkcBody.JOBS_STRING)) {
			LlkcBody.JOBS_STRING = Tools.getStringFromRes(getContext(),
					R.string.pugong);
		}

		if (LlkcBody.CITY_STRING_Current != null
				&& !LlkcBody.CITY_STRING_Current.equals("")) {
			String tt = LlkcBody.CITY_STRING_Current.length() > 3 ? LlkcBody.CITY_STRING_Current
					.substring(0, 3) : LlkcBody.CITY_STRING_Current;
			curTV.setText("(" + tt + ")");
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// 当Activity显示出来后，每两秒钟切换一次图片显示
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 3,
				TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(TAG);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		// 当Activity不可见的时候停止切换
		scheduledExecutorService.shutdown();
		super.onStop();
	}

	class HotJobTask extends AsyncTask<Object, Integer, List<Obj>> {
		String city;

		public HotJobTask(String city) {
			super();
			this.city = city;
		}

		// ProgressDialog dialog = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// dialog = new ProgressDialog(getContext());
			// dialog.setCancelable(false);
			// dialog.setMessage(Tools.getStringFromRes(getApplicationContext(),
			// R.string.loading));
			// dialog.show();
		}

		@Override
		protected ArrayList<Obj> doInBackground(Object... params) {
			try {
				return Community.getInstance(getContext()).getHotCommendType(
						city);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Obj> result) {
			setProgressBarIndeterminateVisibility(false);
			// dialog.dismiss();
			if (result != null && result.size() > 0) {
				ApplicationData.recommendTypeList.clear();
				ApplicationData.recommendTypeList.addAll(result);
				initHotJobTypes();
			}
			downLoadNum--;
			if (downLoadNum <= 0) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
			super.onPostExecute(result);
		}
	}

	class InitDataTask extends AsyncTask<Void, Void, ArrayList<ImageItem>> {

		@Override
		protected ArrayList<ImageItem> doInBackground(Void... params) {
			return initImgItem();
		}

		@Override
		protected void onPostExecute(ArrayList<ImageItem> result) {
			super.onPostExecute(result);
			if (result != null) {
				ApplicationData.imgItemList.clear();
				ApplicationData.imgItemList.addAll(result);
			}
			initImgItemsView();
			downLoadNum--;
			if (downLoadNum <= 0) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
		}
	}

	public ArrayList<ImageItem> initImgItem() {
		ArrayList<ImageItem> list = null;
		try {
			list = Community.getInstance(getContext()).getImgItemList(
					LlkcBody.CITY_STRING);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 换行切换任务
	 * 
	 * @author 傅强
	 * 
	 */
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				if (imageViews.size() == 0)
					return;
				currentItem = (currentItem + 1) % imageViews.size();
				// handler.obtainMessage().sendToTarget();
				handler.sendEmptyMessage(1);
				// 通过Handler切换图片
			}
		}

	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			currentItem = position;
			tv_title.setText(titles.get(position));
			pointLayout.setPageIndex(position);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 * 填充ViewPager页面的适配器
	 * 
	 * @author 傅强
	 * 
	 */
	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return ApplicationData.imgItemList.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			if (arg1 < imageViews.size()) {
				((ViewPager) arg0).addView(imageViews.get(arg1));
				return imageViews.get(arg1);
			}
			return null;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

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
						LlkcBody.CITY_STRING = city.name;
						LLKCApplication.getInstance().setOperCity(
								LlkcBody.CITY_STRING);
						new HotJobTask(city.name).execute();
						new InitDataTask().execute();
					}
				}
			}
		}
	}

	// **********************************************************//
	int count = 0;
	long startTime;
	long nextTime;

	@Override
	public void onBackPressed() {
		Intent home = new Intent(Intent.ACTION_MAIN);
		home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		home.addCategory(Intent.CATEGORY_HOME);
		startActivity(home);
		// if (count == 0) {
		// startTime = System.currentTimeMillis();
		// MyToast.getToast().showToast(
		// this,
		// Tools.getStringFromRes(this, R.string.pressed_exit_again)
		// + getResources().getString(R.string.app_name));
		// count++;
		// return;
		// } else {
		// nextTime = System.currentTimeMillis();
		// if (nextTime - startTime <= 1500) {
		// super.onBackPressed();
		// Intent service = new Intent(this, DownloadService.class);
		// stopService(service);
		// Intent service2 = new Intent(this, LLKCService.class);
		// stopService(service2);
		// finishAllActs();
		// clear();
		// DBHelper.getHallDBInstance(this).closeDB();
		// Llkc_MsgReceiver.TaskManager.getInstance(getContext())
		// .stopThread();
		// } else {
		// MyToast.getToast().showToast(
		// this,
		// Tools.getStringFromRes(this,
		// R.string.pressed_exit_again)
		// + getResources().getString(R.string.app_name));
		// startTime = nextTime;
		// }
		// }
	}

	public void clear() {
		ApplicationData.clear();
		ImageCache.getInstance().clear();
		FinalBitmap.create(this).clearCache();
	}

	private MyCountDownTimer countDownTimer = new MyCountDownTimer(5000, 1000) {

		@Override
		public void onTick(long millisUntilFinished, int percent) {

		}

		@Override
		public void onFinish() {
			if (popTV != null)
				popTV.setVisibility(View.GONE);
		}
	};
	private MyCountDownTimer resfreshDownTimer = new MyCountDownTimer(5000,
			1000) {

		@Override
		public void onTick(long millisUntilFinished, int percent) {

		}

		@Override
		public void onFinish() {
			if (resfreshTV != null)
				resfreshTV.setVisibility(View.GONE);
		}
	};
}
