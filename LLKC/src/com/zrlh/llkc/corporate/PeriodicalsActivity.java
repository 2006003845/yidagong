package com.zrlh.llkc.corporate;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.SituationDetailActivity;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;
import com.zzl.zl_app.widget.PullToRefreshBase.OnRefreshListener;
import com.zzl.zl_app.widget.PullToRefreshListView;

public class PeriodicalsActivity extends BaseActivity {
	public final String Tag = "Periodicals";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, PeriodicalsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(Tag, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.newsituation_activity);
		imgCache = ImageCache.getInstance();
		initView();
		new GetPeriodicalsTask("0", LlkcBody.CITY_STRING, false).execute();
	}

	private ImageCache imgCache;
	private PullToRefreshListView periodicalListV;
	private PeriodicalAdapter adapter;
	private TextView title_card;
	private List<Periodical> periodicalList = new ArrayList<Periodical>();

	ProgressDialog dialog = null;

	private void initView() {
		periodicalList.clear();

		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		title_card = (TextView) findViewById(R.id.title_card);
		title_card.setText(Tools.getStringFromRes(getContext(),
				R.string.news_of_work));
		this.findViewById(R.id.friends).setVisibility(View.GONE);
		periodicalListV = (PullToRefreshListView) findViewById(R.id.new_situation_list);
		adapter = new PeriodicalAdapter(periodicalList);
		periodicalListV.mRefreshableView.setAdapter(adapter);
		periodicalListV.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (periodicalList.size() > 0) {
					new GetPeriodicalsTask(periodicalList.get(0).page + "",
							LlkcBody.CITY_STRING, true).execute();
				} else {
					new GetPeriodicalsTask("0", LlkcBody.CITY_STRING, false)
							.execute();
				}
			}
		});

	}

	@Override
	public BaseActivity getContext() {
		// TODO Auto-generated method stub
		return this;
	}

	class PeriodicalAdapter extends BaseAdapter {
		List<Periodical> periodicalList;
		LayoutInflater layoutInflater;
		NewSituation newSituation;

		public final class NewSituation {
			ImageView newsituaion_one_imageview, newsituaion_two_imageview,
					newsituaion_three_imageview, newsituaion_four_imageview;
			TextView newsituaion_one_textview, newsituaion_two_textview,
					newsituaion_three_textview, newsituaion_four_textview,
					dateTextView;
			RelativeLayout newsituaion_one, newsituaion_two, newsituaion_three,
					newsituaion_four;
		}

		public PeriodicalAdapter(List<Periodical> periodicalList) {
			this.periodicalList = periodicalList;
			layoutInflater = LayoutInflater.from(getContext());
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return periodicalList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return periodicalList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (arg1 == null) {
				newSituation = new NewSituation();
				arg1 = layoutInflater.inflate(R.layout.newsituation_list_item,
						null);
				newSituation.newsituaion_four_imageview = (ImageView) arg1
						.findViewById(R.id.newsituaion_four_imageview);
				newSituation.newsituaion_four_textview = (TextView) arg1
						.findViewById(R.id.newsituaion_four_textview);
				newSituation.newsituaion_one_imageview = (ImageView) arg1
						.findViewById(R.id.newsituaion_one_imageview);
				newSituation.newsituaion_one_textview = (TextView) arg1
						.findViewById(R.id.newsituaion_one_textview);
				newSituation.newsituaion_three_imageview = (ImageView) arg1
						.findViewById(R.id.newsituaion_three_imageview);
				newSituation.newsituaion_three_textview = (TextView) arg1
						.findViewById(R.id.newsituaion_three_textview);
				newSituation.newsituaion_two_imageview = (ImageView) arg1
						.findViewById(R.id.newsituaion_two_imageview);
				newSituation.newsituaion_two_textview = (TextView) arg1
						.findViewById(R.id.newsituaion_two_textview);
				newSituation.newsituaion_one = (RelativeLayout) arg1
						.findViewById(R.id.newsituaion_one);
				newSituation.newsituaion_two = (RelativeLayout) arg1
						.findViewById(R.id.newsituaion_two);
				newSituation.newsituaion_three = (RelativeLayout) arg1
						.findViewById(R.id.newsituaion_three);
				newSituation.newsituaion_four = (RelativeLayout) arg1
						.findViewById(R.id.newsituaion_four);
				newSituation.dateTextView = (TextView) arg1
						.findViewById(R.id.dateTextView);
				arg1.setTag(newSituation);
			} else {
				newSituation = (NewSituation) arg1.getTag();
			}
			Periodical periodical = periodicalList.get(arg0);
			List<Novelty> list = periodical.getNoveltyList();
			newSituation.dateTextView.setText(periodical.getDate());
			if (list != null) {
				if (list.size() > 0) {
					newSituation.newsituaion_one.setVisibility(View.VISIBLE);
					final Novelty n = list.get(0);
					newSituation.newsituaion_one_textview.setText(n.getName());

					imgCache.loadImg(n.getImg(),
							newSituation.newsituaion_one_imageview,
							R.drawable.img_default);
					newSituation.newsituaion_one
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									Intent intent = new Intent();
									intent.putExtra("url", n.getUrl());
									intent.setClass(getContext(),
											SituationDetailActivity.class);
									startActivity(intent);
								}
							});
				} else {
					newSituation.newsituaion_one.setVisibility(View.GONE);
				}

				if (list.size() > 1) {
					newSituation.newsituaion_two.setVisibility(View.VISIBLE);
					final Novelty n = list.get(1);
					newSituation.newsituaion_two_textview.setText(n.getName());

					imgCache.loadImg(n.getImg(),
							newSituation.newsituaion_two_imageview,
							R.drawable.img_default);
					newSituation.newsituaion_two
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									Intent intent = new Intent();
									intent.putExtra("url", n.getUrl());
									intent.setClass(getContext(),
											SituationDetailActivity.class);
									startActivity(intent);
								}
							});
				} else {
					newSituation.newsituaion_two.setVisibility(View.GONE);
				}

				if (list.size() > 2) {
					newSituation.newsituaion_three.setVisibility(View.VISIBLE);
					final Novelty n = list.get(2);
					newSituation.newsituaion_three_textview
							.setText(n.getName());

					imgCache.loadImg(n.getImg(),
							newSituation.newsituaion_three_imageview,
							R.drawable.img_default);
					newSituation.newsituaion_three
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									Intent intent = new Intent();
									intent.putExtra("url", n.getUrl());
									intent.setClass(getContext(),
											SituationDetailActivity.class);
									startActivity(intent);
								}
							});
				} else {
					newSituation.newsituaion_three.setVisibility(View.GONE);
				}

				if (list.size() > 3) {
					newSituation.newsituaion_four.setVisibility(View.VISIBLE);
					final Novelty n = list.get(3);
					newSituation.newsituaion_four_textview.setText(n.getName());

					imgCache.loadImg(n.getImg(),
							newSituation.newsituaion_four_imageview,
							R.drawable.img_default);
					newSituation.newsituaion_four
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									Intent intent = new Intent();
									intent.putExtra("url", n.getUrl());
									intent.setClass(getContext(),
											SituationDetailActivity.class);
									startActivity(intent);
								}
							});
				} else {
					newSituation.newsituaion_four.setVisibility(View.GONE);
				}

			}
			return arg1;
		}
	}

	class GetPeriodicalsTask extends
			AsyncTask<Object, Integer, List<Periodical>> {

		String page;
		String city;
		boolean more;

		public GetPeriodicalsTask(String page, String city, boolean more) {
			super();
			this.page = page;
			this.city = city;
			this.more = more;
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
		protected List<Periodical> doInBackground(Object... params) {
			try {
				return Community.getInstance(getContext()).getPeriodicalList(
						city, page);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Periodical> result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null) {
				if (!more)
					periodicalList.clear();
				// periodicalList.addAll(result);
				for (int i = 0; i < result.size(); i++) {
					periodicalList.add(i, result.get(i));
				}
			}
			adapter.notifyDataSetChanged();
			periodicalListV.mRefreshableView
					.setSelection(adapter.getCount() - 1);
			periodicalListV.onRefreshComplete();

			super.onPostExecute(result);
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

}
