package com.zrlh.llkc.activity;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.Utility;
import com.zrlh.llkc.ui.LLKCApplication;
import com.zzl.zl_app.apk.CommunicationImpl;
import com.zzl.zl_app.apk.ICommunity;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.entity.Game;
import com.zzl.zl_app.util.Tools;

public class EntertainmentCenterActivity extends BaseActivity {
	public static final String TAG = "entertainment_center";
	ICommunity icomm;
	FinalDb db;

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, EntertainmentCenterActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		c.startActivity(intent);
	}

	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory("android.intent.category.LAUNCHER");
		icomm = new CommunicationImpl(intent);
		db = FinalDb.create(getContext());
		setContentView(R.layout.activity_entertainment_center);
		initView();
		new GetGameListTask().execute();
	}

	private ArrayList<Game> loadedGameList = new ArrayList<Game>();
	private ArrayList<Game> unloadGameList = new ArrayList<Game>();

	private TextView titleTV;
	private TextView loadedTV, unloadTV;
	private ListView loadedGameListV, unloadGameListV;
	private GameAdapter loadedGameAdapter, unloadGameAdapter;

	ProgressDialog dialog = null;

	private void initView() {
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText(R.string.game_center);
		loadedTV = (TextView) this
				.findViewById(R.id.entertainment_game_loaded_tv);
		unloadTV = (TextView) this
				.findViewById(R.id.entertainment_game_unload_tv);
		loadedGameListV = (ListView) this
				.findViewById(R.id.entertainment_game_loaded_listv);
		loadedGameAdapter = new GameAdapter(loadedGameList,
				GameAdapter.TYPE_LOADED);
		loadedGameListV.setAdapter(loadedGameAdapter);
		loadedGameListV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Game game = loadedGameList.get(arg2);
				intent.setClassName(game.pack, game.pack + ".AppEntry");
				icomm = new CommunicationImpl(intent);
				icomm.startCommunication(getContext(), icomm
						.getApplicationItem(getPackageManager(), game.pack));
			}
		});

		unloadGameListV = (ListView) this
				.findViewById(R.id.entertainment_game_unload_listv);
		unloadGameAdapter = new GameAdapter(unloadGameList,
				GameAdapter.TYPE_UNLOAD);
		unloadGameListV.setAdapter(unloadGameAdapter);
		unloadGameListV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO
				Game game = unloadGameList.get(arg2);
				Intent intent = new Intent();
				Bundle b = new Bundle();
				b.putSerializable("game", game);
				intent.putExtras(b);
				GameDetailActivity.launch(getContext(), intent);
			}
		});
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
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		closeOneAct(TAG);
		super.onBackPressed();
	}

	public void updateViewData() {

		if (loadedGameList.size() == 0) {
			loadedTV.setVisibility(View.VISIBLE);
			loadedGameListV.setVisibility(View.GONE);
		} else {
			loadedTV.setVisibility(View.GONE);
			loadedGameListV.setVisibility(View.VISIBLE);
		}

		if (unloadGameList.size() == 0) {
			unloadTV.setVisibility(View.VISIBLE);
			unloadGameListV.setVisibility(View.GONE);
		} else {
			unloadTV.setVisibility(View.GONE);
			unloadGameListV.setVisibility(View.VISIBLE);
		}
		updateListView();
	}

	private void updateListView() {
		Utility.setListViewHeightBasedOnChildren(loadedGameListV);
		Utility.setListViewHeightBasedOnChildren(unloadGameListV);
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

	class GetGameListTask extends AsyncTask<Object, Integer, List<Game>> {
		// String uId;

		public GetGameListTask() {
			super();
			// this.uId = uId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getApplicationContext(),
					R.string.loading));
			dialog.show();
		}

		@Override
		protected List<Game> doInBackground(Object... params) {
			if (LLKCApplication.getInstance().isOpenNetwork())
				try {
					// return Community.getInstance(getContext()).getGameList();
					List<Game> result = Community.getInstance(getContext())
							.getGameList();
					if (result == null || result.size() == 0)
						result = db.findAll(Game.class);
					return result;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			else
				return db.findAll(Game.class);
			return null;
		}

		@Override
		protected void onPostExecute(List<Game> result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null && result.size() > 0) {
				db.deleteByWhere(Game.class, "1=1");
				// 检测游戏是否安装
				for (Game game : result) {
					if (icomm
							.getApplicationItem(getPackageManager(), game.pack) == null) {
						unloadGameList.add(game);
					} else {
						loadedGameList.add(game);
					}
					db.save(game);
				}
			}
			updateViewData();
			super.onPostExecute(result);
		}
	}

	class GameAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<Game> gameList;
		private int type;

		public static final int TYPE_LOADED = 1;
		public static final int TYPE_UNLOAD = 2;

		public GameAdapter(List<Game> gameList, int type) {
			inflater = (LayoutInflater) getContext().getSystemService(
					android.content.Context.LAYOUT_INFLATER_SERVICE);
			this.gameList = gameList;
			this.type = type;
		}

		@Override
		public int getCount() {
			return gameList.size();
		}

		@Override
		public Object getItem(int position) {
			return gameList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_game, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView
						.findViewById(R.id.item_game_icon);
				holder.name = (TextView) convertView
						.findViewById(R.id.item_game_name);
				holder.info = (TextView) convertView
						.findViewById(R.id.item_game_info);
				holder.start = (Button) convertView
						.findViewById(R.id.item_game_start);
				holder.arrow = (ImageView) convertView
						.findViewById(R.id.item_game_arrow);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final Game game = gameList.get(position);
			holder.icon.setTag(game.icon);
			if (game.icon != null && !game.icon.equals("")) {
				ImageCache.getInstance().loadImg(game.icon, game.icon, parent,
						R.drawable.head_default);
			} else {
				holder.icon.setImageResource(R.drawable.head_default);
			}
			holder.name.setText(game.name);
			if (type == TYPE_LOADED) {
				holder.start.setVisibility(View.VISIBLE);
				holder.arrow.setVisibility(View.GONE);
				if (game.rank != null && !game.rank.equals("")
						&& !game.rank.equals("0"))
					holder.info.setText("第" + game.rank + "名");
				else
					holder.info.setText("游戏暂无排名");
			} else {
				holder.start.setVisibility(View.GONE);
				holder.arrow.setVisibility(View.VISIBLE);
				holder.info.setText(game.playerNum + "人在玩");
			}
			holder.start.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO 运行 游戏
					// if (LlkcBody.isLogin)
					intent.setClassName(game.pack, game.pack + ".AppEntry");
					icomm = new CommunicationImpl(intent);
					icomm.startCommunication(getContext(), icomm
							.getApplicationItem(getPackageManager(), game.pack));
				}
			});

			return convertView;
		}

		class ViewHolder {
			TextView name;
			ImageView icon;
			TextView info;
			Button start;
			ImageView arrow;
		}
	}

}
