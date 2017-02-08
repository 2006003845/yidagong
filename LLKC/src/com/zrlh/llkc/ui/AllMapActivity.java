package com.zrlh.llkc.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.PlatformAPI;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.JobDetail;
import com.zrlh.llkc.funciton.Jobs.NearJob;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.LLKCApplication.LocationResultCallback;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class AllMapActivity extends BaseActivity {
	public static final String TAG = "allmap";

	Handler handler = new Handler();

	LinearLayout button_layout;
	public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	MapView mMapView;
	OverlayTest ov = null;
	OverlayTest centerOv = null;
	Button button;
	ImageButton backiImageView, seachImageView;
	TextView titleTextView;
	ArrayList<NearJob> nearJobsList = new ArrayList<NearJob>();
	HashMap<String, ArrayList<NearJob>> jobsMap = new HashMap<String, ArrayList<NearJob>>();
	// String LAT = "";
	// String LNG = "";
	ProgressDialog dialog = null;
	private MapController mMapController = null;
	// 设置定位间隔
	private static final int TIME_INTERVAL_SHORT = 5 * 60 * 1000; // 刷新定位间隔时间
	private double lat, lng;
	private String addrstr;
	GeoPoint point;
	BMapManager mBMapMan = null;
	GeoPoint centerP;
	private String LAT, LNG;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		/**
		 * 使用地图sdk前需先初始化BMapManager. BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
		 * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
		 */
		LLKCApplication app = (LLKCApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(PlatformAPI.BAIDU_Key,
					new LLKCApplication.MyGeneralListener());

		}

		/**
		 * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
		 */
		setContentView(R.layout.routeplan);

		init();

		Drawable marker = AllMapActivity.this.getResources().getDrawable(
				R.drawable.poii);
		mMapView.getOverlays().clear();
		ov = new OverlayTest(marker, this, mMapView, false);
		centerOv = new OverlayTest(this.getResources().getDrawable(
				R.drawable.umeng_socialize_location_ic), this, mMapView, true);
		/**
		 * 设置地图缩放级别
		 */

		/**
		 * 获取地图控制器
		 */
		mMapController = mMapView.getController();
		/**
		 * 设置地图是否响应点击事件 .
		 */
		mMapController.enableClick(true);
		/**
		 * 设置地图缩放级别
		 */
		mMapController.setZoom(15);
		/**
		 * 显示内置缩放控件
		 */
		mMapView.setBuiltInZoomControls(true);

		mMapView.getOverlays().add(ov);
		mMapView.getOverlays().add(centerOv);

		if (LAT == null || LAT.equals("")) {
			// 添加百度定位

			LLKCApplication.getInstance().singleLocation(
					new LocationResultCallback() {

						@Override
						public void getLocationResult(BDLocation location,
								String city, String district, double latitude,
								double longitude) {
							if (location == null)
								return;
							lat = latitude;
							lng = longitude;
							addrstr = location.getAddrStr();
							double lat = LlkcBody.LAT;
							double lng = LlkcBody.LNG;
							if (location != null) {
								lat = location.getLatitude();
								lng = location.getLongitude();
							}
							setMapPoint2(lat, lng);
						}
					});
		} else {
			double latt = Double.parseDouble(LAT);
			double lngg = Double.parseDouble(LNG);
			lat = latt;
			lng = lngg;
			centerP = new GeoPoint((int) (latt * 1e6), (int) (lngg * 1e6));
		}
		// 定位多点
		// initGeoList();
		initView();
	}

	private void initGeoList() {
		if (jobsMap.get("key" + (page + 1)) != null)
			jobsMap.remove("key" + (page + 1));
		String key = "key" + page;
		ArrayList<NearJob> list = jobsMap.get(key);
		if (list == null)
			return;

		mGeoList.clear();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getLat().trim() == null
					|| list.get(i).getLat().trim().length() <= 0
					|| "null".equals(list.get(i).getLat().trim())) {
				continue;
			} else {
				OverlayItem item = new OverlayItem(
						new GeoPoint((int) (Double.valueOf(list.get(i).getLat()
								.trim()) * 1e6), (int) (Double.valueOf(list
								.get(i).getLng().trim()) * 1e6)), null, null);
				mGeoList.add(item);
			}
		}
		centerOv.removeAll();
		ov.removeAll();
		ov.addItem(mGeoList);
		ov.hidePop();
		centerOv.hidePop();
		if (centerP == null) {
			centerP = new GeoPoint((int) (lat * 1e6), (int) (lng * 1e6));
		}
		// if (centerP != null) {
		mMapController.setCenter(centerP);
		OverlayItem item = new OverlayItem(centerP, null, null);
		centerOv.addItem(item);
		// }
		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(centerOv);
		mMapView.getOverlays().add(ov);
		mMapView.refresh();
	}

	private TextView pageTV;
	private ImageView last, next;
	int page = 1;

	private void initView() {
		pageTV = (TextView) this.findViewById(R.id.map_tv_page);
		last = (ImageView) this.findViewById(R.id.map_imgv_last);
		next = (ImageView) this.findViewById(R.id.map_imgv_next);
		pageTV.setText(Tools.getStringFromRes(getContext(), R.string.di) + page
				+ Tools.getStringFromRes(getContext(), R.string.ye));
		if (page <= 1)
			last.setVisibility(View.GONE);
		last.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (page > 1) {
					--page;
					nearJobsList.clear();
					nearJobsList.addAll(jobsMap.get("key" + page));
					initGeoList();
				}
				pageTV.setText(Tools
						.getStringFromRes(getContext(), R.string.di)
						+ page
						+ Tools.getStringFromRes(getContext(), R.string.ye));
				if (page <= 1) {
					last.setVisibility(View.GONE);
				} else {
					last.setVisibility(View.VISIBLE);
				}
			}
		});

		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				++page;
				new JobRequestTask().execute();
			}
		});
	}

	private JobDetail curJob;

	void init() {
		Intent intent = this.getIntent();
		ArrayList<NearJob> list = (ArrayList<NearJob>) intent
				.getSerializableExtra("nearJobsList");
		LAT = intent.getStringExtra("lat");
		LNG = intent.getStringExtra("lng");
		Bundle b = intent.getExtras();
		if (b != null)
			curJob = (JobDetail) b.getSerializable("current_job");
		if (list != null) {
			nearJobsList.addAll(list);
			jobsMap.put("key" + page, list);
		}
		mMapView = (MapView) findViewById(R.id.bmapView);
		backiImageView = (ImageButton) findViewById(R.id.back);
		seachImageView = (ImageButton) findViewById(R.id.friends);
		titleTextView = (TextView) findViewById(R.id.title_card);
		button_layout = (LinearLayout) findViewById(R.id.button_layout);
		button_layout.setVisibility(View.GONE);
		titleTextView.setText(Tools
				.getStringFromRes(getContext(), R.string.map));
		seachImageView.setVisibility(View.INVISIBLE);
		backiImageView.setImageResource(R.drawable.btn_back);
		backiImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((LLKCApplication) getApplication()).closeLocationCallBack();
				onBackPressed();
			}
		});
	}

	public static Bitmap convertViewToBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();

		return bitmap;
	}

	@Override
	protected void onDestroy() {
		mMapView.destroy();
		super.onDestroy();
	}

	public void onResume() {
		mMapView.onResume();
		initGeoList();
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(TAG);

	}

	public void onPause() {
		mMapView.onPause();
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(TAG);
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
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	class OverlayTest extends ItemizedOverlay<OverlayItem> {

		// public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
		private Context mContext = null;
		PopupOverlay pop = null;

		Toast mToast = null;
		private boolean isCenter;

		public void hidePop() {
			if (pop != null)
				pop.hidePop();
		}

		public OverlayTest(Drawable marker, Context context, MapView mapView,
				final boolean isCenter) {
			super(marker, mapView);
			this.isCenter = isCenter;
			this.mContext = context;
			pop = new PopupOverlay(mMapView, new PopupClickListener() {
				@Override
				public void onClickedPopup(int index) {
					if (isCenter)
						return;
					Intent intent = new Intent();
					intent.putExtra("idString", nearJobsList.get(index).getId());
					intent.putExtra("cityString", nearJobsList.get(index)
							.getCity());
					intent.setClass(getContext(), DetailsActivity.class);
					startActivity(intent);
					pop.hidePop();
				}
			});

		}

		protected boolean onTap(int index) {

			View popview = LayoutInflater.from(mContext).inflate(
					R.layout.allmap, null);// 获取要转换的View资源
			TextView TestText = (TextView) popview
					.findViewById(R.id.allmap_pop_textview);
			TextView allmap_addres_textview = (TextView) popview
					.findViewById(R.id.allmap_addres_textview);
			if (isCenter && curJob != null) {
				TestText.setText(curJob.name);// 将每个点的Title在弹窗中以文本形式显示出来
				allmap_addres_textview.setText(Tools.getStringFromRes(mContext,
						R.string.address) + curJob.address);
				Bitmap popbitmap = convertViewToBitmap(popview);
				OverlayItem item = getItem(index);
				if (pop != null && item != null)
					pop.showPopup(popbitmap, item.getPoint(), 32);
				// return super.onTap(index);
			} else if (nearJobsList.size() > index) {
				NearJob job = nearJobsList.get(index);
				TestText.setText(job.getJob_name());// 将每个点的Title在弹窗中以文本形式显示出来
				allmap_addres_textview.setText(Tools.getStringFromRes(mContext,
						R.string.address) + job.getAddress());
				Bitmap popbitmap = convertViewToBitmap(popview);
				OverlayItem item = getItem(index);
				if (pop != null && item != null)
					pop.showPopup(popbitmap, item.getPoint(), 32);
			}
			return super.onTap(index);
		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			if (pop != null) {
				pop.hidePop();
			}
			super.onTap(pt, mapView);
			return false;
		}
	}

	// 获得定位信息定位给地图
	private void setMapPoint2(double latitude, double longitude) {
		// TODO Auto-generated method stub
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mMapView);
		LocationData locData = new LocationData();
		// 手动将位置源置为天安门，在实际应用中，请使用百度定位SDK获取位置信息，要在SDK中显示一个位置，需要使用百度经纬度坐标（bd09ll）
		locData.latitude = latitude;
		locData.longitude = longitude;
		locData.direction = 2.0f;
		myLocationOverlay.setData(locData);
		mMapView.getOverlays().add(myLocationOverlay);
		mMapView.refresh();
		mMapView.getController().animateTo(
				new GeoPoint((int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
	}

	String distanceString = "";
	String classoneString = "";
	String salaryString = "";

	boolean DateEmty = true;

	class JobRequestTask extends AsyncTask<Object, Integer, ArrayList<NearJob>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.loading));
			if (page < 2)
				dialog.show();
		}

		@Override
		protected ArrayList<NearJob> doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext()).getNearJob(
						LlkcBody.CITY_STRING, String.valueOf(page),
						classoneString, salaryString, distanceString,
						String.valueOf(lat), String.valueOf(lng));

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<NearJob> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result == null || result.size() == 0) {
				DateEmty = false;
				handler.sendEmptyMessage(3);
			} else {
				nearJobsList.addAll(result);
				jobsMap.put("key" + page, result);
				handler.post(new Runnable() {

					@Override
					public void run() {
						initGeoList();
						pageTV.setText(Tools.getStringFromRes(getContext(),
								R.string.di)
								+ page
								+ Tools.getStringFromRes(getContext(),
										R.string.ye));
						if (page <= 1) {
							last.setVisibility(View.GONE);
						} else {
							last.setVisibility(View.VISIBLE);
						}
					}
				});
				DateEmty = true;
			}
		}
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
