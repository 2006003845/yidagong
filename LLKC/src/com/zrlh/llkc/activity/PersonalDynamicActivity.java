package com.zrlh.llkc.activity;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.Friend;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.funciton.PersonalDynamic;
import com.zrlh.llkc.ui.LLKCApplication;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.TimeUtil;
import com.zzl.zl_app.util.Tools;

public class PersonalDynamicActivity extends BaseActivity {
	public static final String TAG = "personaldynamic";

	ImageButton back, release;
	TextView title_card;
	ListView personaldynamic_list;
	private View lvNews_footer;
	private TextView lvNews_foot_more;
	private ProgressBar lvNews_foot_progress;
	String touId;
	String type;
	Friend member;
	String lastDID = "0";// 显示的最后一条动态ID,初始为0
	String dId;// 删除的动态ID
	FinalDb db;
	boolean isLoading;
	ProgressDialog dialog = null;

	List<PersonalDynamic> dynamicList = new ArrayList<PersonalDynamic>();
	PersonalDynamicAdapter dynamicAdapter;

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, PersonalDynamicActivity.class);
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
		setContentView(R.layout.personaldynamic);
		db = FinalDb.create(getContext());
		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		if ("personal".equals(type)) {
			if (LlkcBody.ACCOUNT == null)
				LlkcBody.ACCOUNT = LLKCApplication.getInstance()
						.getPersonInfo();
			touId = LlkcBody.ACCOUNT.getUid();
			if (LlkcBody.DYNAMIC.getDynamicId() == null
					|| LlkcBody.DYNAMIC.getDynamicId().equals("")) {
				lastDID = "0";
			} else
				lastDID = String.valueOf(Integer.parseInt(LlkcBody.DYNAMIC
						.getDynamicId().trim()) + 1);// 初始ID要默认加1
		} else if ("userInfo".equals(type)) {
			member = (Friend) intent.getSerializableExtra("member");
			touId = member.getUid();
			if (member.getDynamic_Id() == null
					|| member.getDynamic_Id().equals("")) {
				lastDID = "0";
			} else
				lastDID = String.valueOf(Integer.parseInt(member
						.getDynamic_Id().trim()) + 1);// 初始ID要默认加1
		}
		init();
		new PersonalDynamicTask(touId, lastDID).execute();
	}

	private void init() {
		title_card = (TextView) findViewById(R.id.title_tv);
		title_card.setText("我的动态");
		back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		this.findViewById(R.id.title_position).setVisibility(View.GONE);
		// 发布
		release = (ImageButton) findViewById(R.id.btn);
		if (type.equals("personal"))
			release.setVisibility(View.VISIBLE);
		else
			release.setVisibility(View.GONE);
		release.setImageResource(R.drawable.all_button_edit);
		release.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getContext(),
						ReleaseDynamicActivity.class);
				startActivityForResult(intent, 101);
				// ReleaseDynamicActivity.launch(getContext(), getIntent());
			}
		});
		// listview的底部
		lvNews_footer = getLayoutInflater().inflate(R.layout.listview_footer,
				null);
		lvNews_foot_more = (TextView) lvNews_footer
				.findViewById(R.id.listview_foot_more);
		lvNews_foot_progress = (ProgressBar) lvNews_footer
				.findViewById(R.id.listview_foot_progress);

		personaldynamic_list = (ListView) findViewById(R.id.personaldynamic_listview);
		personaldynamic_list.addFooterView(lvNews_footer, null, false);// 添加底部视图,必须在setAdapter前

		personaldynamic_list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(lvNews_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				if (scrollEnd) {
					lvNews_footer.setVisibility(View.VISIBLE);
					lvNews_foot_more.setText(R.string.load_ing);
					lvNews_foot_progress.setVisibility(View.VISIBLE);

					if (dynamicList != null && dynamicList.size() > 0) {
						if (!isLoading) {
							isLoading = true;
							lastDID = dynamicList.get(dynamicList.size() - 1)
									.getDynamicId();
							new PersonalDynamicTask(touId, lastDID).execute();
						}
					}

				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void deleteDynamic(String dynamicID) {
		for (int i = 0; i < dynamicList.size(); i++) {
			if (dynamicList.get(i).getDynamicId().equals(dynamicID)) {
				if (i == 0) {
					if (dynamicList.size() == 1) {
						LlkcBody.DYNAMIC.dynamicId = "";
						LlkcBody.DYNAMIC.time = "0";
						LlkcBody.DYNAMIC.bigImg = "";
						LlkcBody.DYNAMIC.smallImg = "";
						LlkcBody.DYNAMIC.content = "";

						lvNews_footer.setVisibility(View.VISIBLE);
						lvNews_foot_more.setText("暂无动态!");
						lvNews_foot_progress.setVisibility(View.INVISIBLE);
					} else {
						LlkcBody.DYNAMIC = dynamicList.get(i + 1);
					}
					// 保存最新个人动态
					db.deleteByWhere(PersonalDynamic.class, "account='"
							+ LlkcBody.USER_ACCOUNT + "'");
					LlkcBody.DYNAMIC.setAccount(LlkcBody.USER_ACCOUNT);
					db.save(LlkcBody.DYNAMIC);
				}
				dynamicList.remove(i);
				break;
			}
		}
		if (dynamicAdapter == null) {
			dynamicAdapter = new PersonalDynamicAdapter(getContext());
			personaldynamic_list.setAdapter(dynamicAdapter);
		} else
			dynamicAdapter.notifyDataSetChanged();
	}

	class PersonalDynamicTask extends
			AsyncTask<Object, Integer, List<PersonalDynamic>> {

		String lastDID;
		String touId;

		public PersonalDynamicTask(String touId, String lastDID) {
			super();
			this.touId = touId;
			this.lastDID = lastDID;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getApplicationContext(),
					R.string.loading));
			if (dynamicList == null || dynamicList.size() == 0) {
				dialog.show();
			}
		}

		@Override
		protected List<PersonalDynamic> doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext()).personalDynamicList(
						touId, lastDID);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<PersonalDynamic> result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			// dynamicList.clear();
			isLoading = false;
			if (result != null && result.size() > 0) {
				dynamicList.addAll(result);
				// 保存最新一条动态 最上面一条
				// db.deleteByWhere(PersonalDynamic.class, "account='" +
				// LlkcBody.USER_ACCOUNT
				// + "'");
				// PersonalDynamic dynamic = dynamicList.get(0);
				// dynamic.setAccount(LlkcBody.USER_ACCOUNT);
				// db.save(dynamic);
				// LlkcBody.DYNAMIC = dynamic;
			}
			if (dynamicAdapter == null) {
				dynamicAdapter = new PersonalDynamicAdapter(getContext());
				personaldynamic_list.setAdapter(dynamicAdapter);
			} else
				dynamicAdapter.notifyDataSetChanged();

			if (dynamicList.size() == 0) {
				lvNews_footer.setVisibility(View.VISIBLE);
				lvNews_foot_more.setText("暂无动态!");
				lvNews_foot_progress.setVisibility(View.INVISIBLE);
			} else
				lvNews_footer.setVisibility(View.GONE);

			super.onPostExecute(result);
		}

	}

	class DeleteDynamicTask extends AsyncTask<Void, Void, Boolean> {
		String dId;

		private DeleteDynamicTask(String dId) {
			this.dId = dId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getApplicationContext(),
					R.string.load_ing));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext()).deleteDynamic(dId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null && result) {
				deleteDynamic(dId);
				// new PersonalDynamicTask(touId, page).execute();
			}
			super.onPostExecute(result);
		}

	}

	private class PersonalDynamicAdapter extends BaseAdapter {

		LayoutInflater layoutInflater;
		PersonalDynamicView personalDynamicView;
		FinalBitmap finalBitmap;

		public final class PersonalDynamicView {
			ImageView head; // 头像
			TextView time; // 发布时间
			TextView name; // 名称
			ImageView sex; // 性别
			TextView age; // 年龄
			TextView content; // 动态内容
			ImageView img; // 动态图片
			ImageButton delete;// 删除
		}

		private PersonalDynamicAdapter(Context context) {
			layoutInflater = LayoutInflater.from(context);
			finalBitmap = FinalBitmap.create(getApplicationContext());
			finalBitmap.configLoadingImage(R.drawable.head_default);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dynamicList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return dynamicList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				personalDynamicView = new PersonalDynamicView();
				convertView = layoutInflater.inflate(
						R.layout.personaldynamic_list_item, null);
				personalDynamicView.time = (TextView) convertView
						.findViewById(R.id.personaldynamic_list_item_time);
				personalDynamicView.name = (TextView) convertView
						.findViewById(R.id.personaldynamic_list_item_name);
				personalDynamicView.age = (TextView) convertView
						.findViewById(R.id.personaldynamic_list_item_age);
				personalDynamicView.content = (TextView) convertView
						.findViewById(R.id.personaldynamic_list_item_content);
				personalDynamicView.head = (ImageView) convertView
						.findViewById(R.id.personaldynamic_list_item_head);
				personalDynamicView.sex = (ImageView) convertView
						.findViewById(R.id.personaldynamic_list_item_sex);
				personalDynamicView.img = (ImageView) convertView
						.findViewById(R.id.personaldynamic_list_item_img);
				personalDynamicView.delete = (ImageButton) convertView
						.findViewById(R.id.personaldynamic_list_item_delete);
				convertView.setTag(personalDynamicView);
			} else {
				personalDynamicView = (PersonalDynamicView) convertView
						.getTag();

			}
			if (type.equals("personal")) {
				String head = LlkcBody.ACCOUNT.getHead();
				if (Tools.isUrl(head))
					finalBitmap.display(personalDynamicView.head, head);
				else
					personalDynamicView.head
							.setImageResource(R.drawable.head_default);
				personalDynamicView.name.setText(LlkcBody.ACCOUNT.getUname());
				personalDynamicView.sex.setVisibility(View.VISIBLE);
				if (LlkcBody.ACCOUNT.getSex().equals("0")) {
					personalDynamicView.sex
							.setImageResource(R.drawable.all_icon_girl);
				} else {
					personalDynamicView.sex
							.setImageResource(R.drawable.all_icon_boy);
				}
				personalDynamicView.age.setText(LlkcBody.ACCOUNT
						.getAge(LlkcBody.ACCOUNT.getBirth()));
			} else {
				finalBitmap.display(personalDynamicView.head, member.getHead());
				personalDynamicView.name.setText(member.getUname());
				personalDynamicView.sex.setVisibility(View.VISIBLE);
				if (member.getSex().equals("1")) {
					personalDynamicView.sex
							.setImageResource(R.drawable.all_icon_boy);
				} else if (member.getSex().equals("0")) {
					personalDynamicView.sex
							.setImageResource(R.drawable.all_icon_girl);
				} else {
					personalDynamicView.sex.setVisibility(View.GONE);
				}
				personalDynamicView.age
						.setText(member.getAge(member.getBirth()));
			}
			final PersonalDynamic dynamic = dynamicList.get(position);
			personalDynamicView.time.setText(TimeUtil.getTimeStr2(
					dynamic.getTime(), "MM-dd"));
			personalDynamicView.content.setVisibility(View.VISIBLE);
			if (!dynamic.getContent().equals("")) {
				personalDynamicView.content.setText(dynamic.getContent());
			} else
				personalDynamicView.content.setVisibility(View.GONE);
			personalDynamicView.img.setVisibility(View.VISIBLE);
			if (dynamic.getSmallImg() != null
					&& !dynamic.getSmallImg().equals("")) {
				ImageCache.getInstance().loadImg(dynamic.getSmallImg(),
						personalDynamicView.img, R.drawable.default_img);
			} else
				personalDynamicView.img.setVisibility(View.GONE);
			personalDynamicView.img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					Bundle b = new Bundle();
					b.putString("picUrl", dynamic.getBigImg());
					intent.putExtras(b);
					PreviewPicActivity.launch(getContext(), intent);
				}
			});
			if (type.equals("personal"))
				personalDynamicView.delete.setVisibility(View.VISIBLE);
			else
				personalDynamicView.delete.setVisibility(View.INVISIBLE);
			personalDynamicView.delete
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dId = dynamic.getDynamicId();
							showMsgDialog("1", R.layout.layout_prompt,
									getContext(), "删除动态", "是否删除这条动态？");
						}
					});
			return convertView;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 101) {
			if (data != null) {
				Bundle b = data.getExtras();
				if (b != null) {
					boolean result = b.getBoolean("result");
					if (result) {
						// 在列表第一行加入发布的动态数据
						PersonalDynamic dynamic = (PersonalDynamic) b
								.getSerializable("dynamic");
						dynamicList.add(0, dynamic);
						if (dynamicAdapter == null) {
							dynamicAdapter = new PersonalDynamicAdapter(
									getContext());
							personaldynamic_list.setAdapter(dynamicAdapter);
						} else
							dynamicAdapter.notifyDataSetChanged();
						lvNews_footer.setVisibility(View.GONE);
						// new PersonalDynamicTask(touId, 1).execute();
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
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		closeOneAct(TAG);
		super.onBackPressed();
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
		switch (layoutId) {
		case R.layout.layout_prompt:
			TextView contTV = (TextView) dialog
					.findViewById(R.id.prompt_tv_cont);
			contTV.setText(content);
			break;
		default:
			break;

		}
	}

	@Override
	public void setDialogTitle(AlertDialog dialog, int layoutId, String title,
			String id) {
		// TODO Auto-generated method stub
		switch (layoutId) {
		case R.layout.layout_prompt:
			TextView titleTV = (TextView) dialog
					.findViewById(R.id.prompt_tv_title);
			titleTV.setText(title);
			break;
		}
	}

	@Override
	public OnClickListener setPositiveClickListener(final AlertDialog dialog,
			int layoutId, String id) {
		// TODO Auto-generated method stub
		switch (layoutId) {
		case R.layout.layout_prompt:
			Button sure = (Button) dialog.findViewById(R.id.prompt_btn_ok);
			sure.setText(R.string.sure);
			sure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					// 删除动态！
					new DeleteDynamicTask(dId).execute();
				}
			});
			break;
		}
		return null;
	}

	@Override
	public OnClickListener setNegativeClickListener(final AlertDialog dialog,
			int layoutId, String id) {
		// TODO Auto-generated method stub
		Button cancel = (Button) dialog.findViewById(R.id.prompt_btn_cancel);
		cancel.setVisibility(View.VISIBLE);
		cancel.setText(R.string.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		return null;
	}

}
