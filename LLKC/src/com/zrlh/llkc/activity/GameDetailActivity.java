package com.zrlh.llkc.activity;

import org.json.JSONException;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.DownloadService;
import com.zzl.zl_app.apk.CommunicationImpl;
import com.zzl.zl_app.apk.GameApk;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.entity.Game;
import com.zzl.zl_app.util.Tools;
import com.zzl.zl_app.widget.FixedViewFlipper;

public class GameDetailActivity extends BaseActivity {
	public static final String TAG = "game_detail";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, GameDetailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		c.startActivity(intent);
	}

	private Game game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.activity_game_detail);
		Bundle b = getIntent().getExtras();
		if (b != null)
			game = (Game) b.getSerializable("game");
		init();
		initView();
		registerMessageReceiver();
	}

	private Button loadBtn;
	private TextView titleTV;
	private TextView nameTV, infoTV, introTV;
	private ImageView icon;
	private FixedViewFlipper viewFlipper;
	// @SuppressWarnings("deprecation")
	private GridView imgGallery;
	private ImageAdapter imgAdapter;
	private LinearLayout galleryLayout;

	Handler handler = new Handler();

	private ServiceConnection conn;
	private DownloadService.DownloadBinder binder;

	public void init() {
		conn = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				binder = (DownloadService.DownloadBinder) service;
				binder.start();
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
		};
	}

	private void initView() {
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText(R.string.detail);
		icon = (ImageView) this.findViewById(R.id.game_detail_icon);
		loadBtn = (Button) this.findViewById(R.id.game_detail_load_btn);
		loadBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO
				if (loadBtn.getText().equals("启动游戏")) {
					CommunicationImpl icomm = new CommunicationImpl();
					icomm.startCommunication(getContext(), icomm
							.getApplicationItem(getPackageManager(), game.pack));
				} else {

					GameApk gameApk = new GameApk(game.down, game.pack, game);

					int action = gameApk.getAPKAction(getContext(), game.pack,
							1);
					if (action == GameApk.DOWNLOADGAME
							|| action == GameApk.UPDATEGAME) {
						Intent intent = new Intent(getContext(),
								DownloadService.class);
						intent.putExtra("updateURL", game.down);
						intent.putExtra("other", true);
						intent.putExtra("name", game.name);
						startService(intent); // 如果先调用startService,则在多个服务绑定对象调用unbindService后服务仍不会被销毁
						bindService(intent, conn, Context.BIND_AUTO_CREATE);
						game.isLoading = true;
						loadBtn.setText("正在下载");
						// 统计下载
						new CountGameDownTask(game.id).execute();
					}
				}
			}
		});
		nameTV = (TextView) this.findViewById(R.id.game_detail_name);
		infoTV = (TextView) this.findViewById(R.id.game_detail_info);
		introTV = (TextView) this.findViewById(R.id.game_detail_intro_tv);
		HorizontalScrollView hsv = (HorizontalScrollView) this
				.findViewById(R.id.game_detail_horizontalscrollv);
		int h = Math
				.round((float) (Tools.dip2px(getContext(), 175) / MainActivity.WIDTH_RATIO));
		Tools.log("Gallery", "height:" + h);
		hsv.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, h + 10));
		// galleryLayout = (LinearLayout)
		// findViewById(R.id.game_detail_gallery_layout);
		viewFlipper = (FixedViewFlipper) this
				.findViewById(R.id.game_detail_viewflipper);
		imgGallery = (GridView) this.findViewById(R.id.game_detail_gallery);
		int itemW = Tools.dip2px(getContext(),
				(float) (220 / MainActivity.HEIGHT_RATIO));
		Tools.log("Gallery", "itemW:" + itemW);
		int width = /* Tools.dip2px(getContext(), */itemW * 4
				+ Tools.dip2px(getContext(), 20) + 12/* ) */;
		Tools.log("Gallery", "width:" + width);
		imgGallery.setLayoutParams(new LinearLayout.LayoutParams(width,
				LayoutParams.MATCH_PARENT));
		imgGallery.setColumnWidth(itemW);
		imgAdapter = new ImageAdapter(game.imgs);
		imgGallery.setAdapter(imgAdapter);
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
		updateViewData();
	}

	@Override
	public void onBackPressed() {
		closeOneAct(TAG);
		super.onBackPressed();
	}

	private void updateViewData() {
		if (game == null)
			return;
		nameTV.setText(game.name);
		infoTV.setText(game.playerNum + "人在玩");
		introTV.setText(game.intro);
		viewFlipper.setDisplayedChild(0);
		ImageCache.getInstance().loadImg(game.icon, icon,
				R.drawable.game_default);
	}

	@Override
	public BaseActivity getContext() {
		return this;
	}

	@Override
	public void setDialogContent(AlertDialog dialog, int layoutId,
			String content, String id) {

	}

	@Override
	public void setDialogTitle(AlertDialog dialog, int layoutId, String title,
			String id) {

	}

	@Override
	public OnClickListener setPositiveClickListener(AlertDialog dialog,
			int layoutId, String id) {

		return null;
	}

	@Override
	public OnClickListener setNegativeClickListener(AlertDialog dialog,
			int layoutId, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public class ImageAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		public String[] imgs;

		public ImageAdapter(String[] imgs) {
			this.imgs = imgs;
			inflater = (LayoutInflater) getContext().getSystemService(
					android.content.Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			if (observer != null)
				super.unregisterDataSetObserver(observer);
		}

		public int getCount() {
			return imgs.length;
		}

		public Object getItem(int position) {
			return imgs[position];
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.imgv, null);
				holder = new ViewHolder();
				holder.img = (ImageView) convertView.findViewById(R.id.imgv);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String imgUrl = imgs[position];
			// holder.img.setTag(imgUrl);
			ImageCache.getInstance().loadImg(imgUrl, holder.img,
					R.drawable.default_img);
			return convertView;
		}

		class ViewHolder {
			ImageView img;

		}
	}

	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.zzl.app.app_download";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";

	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				boolean isFinish = intent.getBooleanExtra("isfinish", false);
				String progress = intent.getStringExtra("progress");
				if (isFinish) {
					game.isLoading = false;
					loadBtn.setText("启动游戏");
				} else {
					loadBtn.setText("正在下载(" + progress + ")");
				}
			}
		}
	}

	class CountGameDownTask extends AsyncTask<Object, Integer, Boolean> {
		String gameId;

		public CountGameDownTask(String gameId) {
			this.gameId = gameId;
		}

		@Override
		protected Boolean doInBackground(Object... params) {

			try {
				return Community.getInstance(getContext())
						.countGameDown(gameId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean bool) {

			super.onPostExecute(bool);
		}
	}

}
