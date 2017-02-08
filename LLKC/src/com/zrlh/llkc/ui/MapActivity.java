package com.zrlh.llkc.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.PlatformAPI;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.util.Tools;

public class MapActivity extends BaseActivity implements MKGeneralListener {
	public static final String TAG = "map";
	
	Button mBtnDrive = null; // 驾车搜索
	Button mBtnTransit = null; // 公交搜索
	Button mBtnWalk = null; // 步行搜索
	Double endlat = 0.0;
	Double endlng = 0.0;
	Double startlat = 0.0;
	Double startlng = 0.0;
	MapView mMapView = null; // 地图View
	MKSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	GeoPoint startPoint, endPoint;
	MKPlanNode stNode = new MKPlanNode();
	MKPlanNode enNode = new MKPlanNode();
	ImageButton backimageImageView, seach;
	TextView title_card;
	String nameString, address;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.routeplan);
		this.findViewById(R.id.bottom).setVisibility(View.GONE);
		Intent intent = getIntent();
		String lat = intent.getStringExtra("lat");
		String lng = intent.getStringExtra("lng");
		if (lat != null)
			lat = lat.trim();
		if (lng != null)
			lng = lng.trim();
		try {
			if (lat != null && !lat.equals(""))
				endlat = Double.valueOf(lat);
			if (lng != null && !lng.equals(""))
				endlng = Double.valueOf(lng);
		} catch (NumberFormatException e) {
			// TODO: handle exception
		}
		nameString = intent.getStringExtra("name");
		address = intent.getStringExtra("address");
		mMapView = (MapView) findViewById(R.id.bmapView);

		backimageImageView = (ImageButton) findViewById(R.id.back);
		backimageImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		seach = (ImageButton) findViewById(R.id.friends);
		seach.setVisibility(View.INVISIBLE);
		title_card = (TextView) findViewById(R.id.title_card);
		title_card.setText(Tools.getStringFromRes(getContext(),
				R.string.map));

		startlat = LlkcBody.LAT;
		startlng = LlkcBody.LNG;
		startPoint = new GeoPoint((int) (startlat * 1E6),
				(int) (startlng * 1E6));
		endPoint = new GeoPoint((int) (endlat * 1E6), (int) (endlng * 1E6));

		stNode.pt = startPoint;

		enNode.pt = endPoint;
		LLKCApplication app = (LLKCApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			app.mBMapManager.init(PlatformAPI.BAIDU_Key,
					new LLKCApplication.MyGeneralListener());
		}
		initMapView();
		View popview = LayoutInflater.from(this).inflate(R.layout.deatail_map,
				null);// 获取要转换的View资源
		TextView TestText = (TextView) popview
				.findViewById(R.id.allmap_pop_textview);
		TextView allmap_pop_addres_textview = (TextView) popview
				.findViewById(R.id.allmap_pop_addres_textview);
		allmap_pop_addres_textview.setText(Tools.getStringFromRes(
				getContext(), R.string.address) + address);
		TestText.setText(nameString);
		Bitmap popbitmap = convertViewToBitmap(popview);
		BitmapDrawable bd = new BitmapDrawable(popbitmap);
		OverlayTest itemOverlay = new OverlayTest(bd, mMapView);
		OverlayItem item2 = new OverlayItem(endPoint, "item2", "item2");
		item2.setMarker(bd);
		itemOverlay.addItem(item2);

		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(itemOverlay);
		mMapView.getController().setCenter(endPoint);
		mMapView.refresh();

		// 初始化搜索模块，注册事件监听
		mSearch = new MKSearch();
		mSearch.init(app.mBMapManager, new MKSearchListener() {

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				if (error != 0 || res == null) {
					Toast.makeText(
							MapActivity.this,
							Tools.getStringFromRes(getContext(),
									R.string.prompt_sorry), Toast.LENGTH_SHORT)
							.show();
					return;
				}
				RouteOverlay routeOverlay = new RouteOverlay(MapActivity.this,
						mMapView);
				routeOverlay.setData(res.getPlan(0).getRoute(0));
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(routeOverlay);
				mMapView.refresh();
				// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
				mMapView.getController().zoomToSpan(
						routeOverlay.getLatSpanE6(),
						routeOverlay.getLonSpanE6());
				mMapView.getController().animateTo(res.getStart().pt);
			}

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				if (error != 0 || res == null) {
					Toast.makeText(
							MapActivity.this,
							Tools.getStringFromRes(getContext(),
									R.string.prompt_sorry), Toast.LENGTH_SHORT)
							.show();
					return;
				}
				TransitOverlay routeOverlay = new TransitOverlay(
						MapActivity.this, mMapView);
				routeOverlay.setData(res.getPlan(0));
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(routeOverlay);
				mMapView.refresh();
				// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
				mMapView.getController().zoomToSpan(
						routeOverlay.getLatSpanE6(),
						routeOverlay.getLonSpanE6());
				mMapView.getController().animateTo(res.getStart().pt);

			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
					int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				// TODO Auto-generated method stub
				if (error != 0 || res == null) {
					Toast.makeText(
							MapActivity.this,
							Tools.getStringFromRes(getContext(),
									R.string.prompt_sorry), Toast.LENGTH_SHORT)
							.show();
					return;
				}
				RouteOverlay routeOverlay = new RouteOverlay(MapActivity.this,
						mMapView);
				routeOverlay.setData(res.getPlan(0).getRoute(0));
				if (mMapView.getOverlays() != null) {
					mMapView.getOverlays().clear();
					mMapView.getOverlays().add(routeOverlay);
				}
				mMapView.refresh();
				// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
				if (mMapView.getController() != null) {
					if (routeOverlay != null)
						mMapView.getController().zoomToSpan(
								routeOverlay.getLatSpanE6(),
								routeOverlay.getLonSpanE6());
					mMapView.getController().animateTo(res.getStart().pt);
				}
			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});
		// 设定搜索按钮的响应
		mBtnDrive = (Button) findViewById(R.id.drive);
		mBtnTransit = (Button) findViewById(R.id.transit);
		mBtnWalk = (Button) findViewById(R.id.walk);

		OnClickListener clickListener = new OnClickListener() {
			public void onClick(View v) {
				SearchButtonProcess(v);
			}
		};

		mBtnDrive.setOnClickListener(clickListener);
		mBtnTransit.setOnClickListener(clickListener);
		mBtnWalk.setOnClickListener(clickListener);

	}

	private void initMapView() {
		mMapView.getController().enableClick(true);
		mMapView.getController().setZoom(12);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setDoubleClickZooming(true);
	}

	public static Bitmap convertViewToBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();

		return bitmap;
	}

	void SearchButtonProcess(View v) {

		// 实际使用中请对起点终点城市进行正确的设定
		if (mBtnDrive.equals(v)) {
			mSearch.drivingSearch(LlkcBody.CITY_STRING, stNode,
					LlkcBody.CITY_STRING, enNode);
		} else if (mBtnTransit.equals(v)) {
			mSearch.transitSearch(LlkcBody.CITY_STRING, stNode, enNode);
		} else if (mBtnWalk.equals(v)) {
			mSearch.walkingSearch(LlkcBody.CITY_STRING, stNode,
					LlkcBody.CITY_STRING, enNode);
		}
	}

	@Override
	public void onPause() {
		mMapView.onPause();
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(TAG);
	}

	@Override
	public void onResume() {
		mMapView.onResume();
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
	protected void onDestroy() {
		mMapView.destroy();
		super.onDestroy();
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
		// 用MapView构造ItemizedOverlay
		public OverlayTest(Drawable marker, MapView mapView) {
			super(marker, mapView);
		}

		protected boolean onTap(int index) {
			// 在此处理item点击事件
			System.out.println("item onTap: " + index);
			return true;
		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			// 在此处理MapView的点击事件，当返回 true时
			super.onTap(pt, mapView);
			return false;
		}
	}

	@Override
	public void onGetNetworkState(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetPermissionState(int arg0) {
		// TODO Auto-generated method stub

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
