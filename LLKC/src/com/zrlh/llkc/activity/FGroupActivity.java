package com.zrlh.llkc.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.PlatformAPI;
import com.zrlh.llkc.corporate.WebViewActivity;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.GoodFriendsAvtivity;
import com.zrlh.llkc.ui.GroupListActivity;
import com.zrlh.llkc.ui.LoginActivity;
import com.zzl.zl_app.util.Tools;

public class FGroupActivity extends BaseActivity {
	public static final String TAG = "fgroup";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.fgroup);
		initView();
	}

	private TextView titleTV;

	private void initView() {
		titleTV = (TextView) this.findViewById(R.id.tap_top_tv_title);
		titleTV.setText(R.string.square);
		// 我的好友
		this.findViewById(R.id.fgroup_layout_myfriend).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!LlkcBody.isLogin()) {
							LoginActivity.launch(getContext(), getIntent());
						} else
							GoodFriendsAvtivity.launch(getContext(),
									getIntent());
					}
				});
		// // 添加友好
		// this.findViewById(R.id.fgroup_layout_addfriend).setOnClickListener(
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (!LlkcBody.isLogin) {
		// LoginActivity.launch(getContext(), getIntent());
		// } else
		// SearchFriendsActivity.launch(getContext(),
		// getIntent());
		// }
		// });
		// 我的工友帮
		this.findViewById(R.id.fgroup_layout_fg).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!LlkcBody.isLogin()) {
							LoginActivity.launch(getContext(), getIntent());
						} else
							GroupListActivity.launch(getContext(), getIntent());
					}
				});
		// // 添加工友帮
		// this.findViewById(R.id.fgroup_layout_addfg).setOnClickListener(
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (!LlkcBody.isLogin) {
		// LoginActivity.launch(getContext(), getIntent());
		// } else
		// SearchGroupActivity.launch(getContext(),
		// getIntent());
		// }
		// });
		// 娱乐中心
		this.findViewById(R.id.fgroup_layout_entertainment).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO
						EntertainmentCenterActivity.launch(getContext(),
								getIntent());
					}
				});
		// 阅读中心
		this.findViewById(R.id.fgroup_layout_readcenter).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO
						// MyToast.getToast().showToast(getContext(),
						// R.string.coming_soon);

						Intent intent = new Intent();
						Bundle b = new Bundle();
						b.putString("name", Tools.getStringFromRes(
								getContext(), R.string.read_center));
						b.putString("url", PlatformAPI.ReadCenter_Url);
						intent.putExtras(b);
						WebViewActivity.launch(getContext(), intent);
					}
				});
		// 应用推荐
		this.findViewById(R.id.fgroup_layout_appcenter).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO
						Intent intent = new Intent();
						Bundle b = new Bundle();
						b.putString("name", Tools.getStringFromRes(
								getContext(), R.string.app_center));
						b.putString("url", PlatformAPI.AppCenter_Url);
						intent.putExtras(b);
						WebViewActivity.launch(getContext(), intent);

					}
				});
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
		if (MainActivity.mInstance != null)
			MainActivity.mInstance.setCurrentTab(0);
	}
}
