package com.zrlh.llkc.activity;

import net.tsz.afinal.FinalBitmap;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zzl.zl_app.entity.Collect;
import com.zzl.zl_app.util.TextUtil;
import com.zzl.zl_app.util.TimeUtil;
import com.zzl.zl_app.util.Tools;
import com.zzl.zl_app.widget.FixedViewFlipper;

public class MyCollectDetailActivity extends BaseActivity{
	public static final String TAG = "mycollectdetail";
	
	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, MyCollectDetailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}
	
	ImageButton back, btn;
	TextView title_card;	
	Collect collect;
	ImageView head;
	TextView name, time;
	FinalBitmap finalBitmap;
	FixedViewFlipper viewflipper;
	TextView content, time2; // 内容
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.mycollectdetail);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			collect = (Collect) b.getSerializable("collect");
		}
		finalBitmap = FinalBitmap.create(getContext());
		finalBitmap.configLoadingImage(R.drawable.head_default);
		init();
	}
	
	private void init(){
		title_card = (TextView) findViewById(R.id.title_card);
		title_card.setText("收藏详情");
		back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		btn = (ImageButton) findViewById(R.id.friends);
		btn.setImageResource(R.drawable.btn_more);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showMsgDialog("menu", R.layout.dialog_menu,
						getContext(), "提示", null);
			}
		});
		head = (ImageView) findViewById(R.id.mycollect_detail_head);
		String headUrl = collect.getHead();
		if (Tools.isUrl(headUrl))
			finalBitmap.display(head, headUrl);
		else
			head.setImageResource(R.drawable.head_default);
		name = (TextView) findViewById(R.id.mycollect_detail_name);
		name.setText(collect.getName());

		time = (TextView) findViewById(R.id.mycollect_detail_time);
		time.setText(TimeUtil.getTimeStr2(collect.getPublic_time(),"yy-MM-dd"));
		
		viewflipper = (FixedViewFlipper) findViewById(R.id.mycollect_detail_viewflipper);
		content = (TextView) findViewById(R.id.mycollect_detail_tv);
		time2 = (TextView) findViewById(R.id.mycollect_detail_time2);
		if(collect.getMtype() != null && collect.getMtype().equals("1")){
			viewflipper.setDisplayedChild(0);
			if (collect.getContent() != null && !collect.getContent().equals(""))
				content.setText(TextUtil
						.formatContent(collect.getContent(), getContext()));
			else
				content.setText("");
			time2.setText("收藏于"+TimeUtil.getTimeStr2(collect.getTime(),
			"MM-dd"));
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
	public void setDialogContent(AlertDialog dialog, int layoutId,
			String content, String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDialogTitle(AlertDialog dialog, int layoutId, String title,
			String id) {
		// TODO Auto-generated method stub
		if (id.equals("menu")) {
			TextView titleTV = (TextView) dialog
					.findViewById(R.id.dialog_title);
			titleTV.setText(title);
		}
	}

	@Override
	public OnClickListener setPositiveClickListener(final AlertDialog dialog,
			int layoutId, String id) {
		// TODO Auto-generated method stub
		if (id.equals("menu")) {
			dialog.findViewById(R.id.parent).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
			if (collect.getMtype() != null
					&& collect.getMtype().equals("1")) {
				dialog.findViewById(R.id.dialog_transmit).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_divider2).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_colllect).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_divider3).setVisibility(
						View.GONE);
			} else if (collect.getMtype() != null
					&& collect.getMtype().equals("2")) {
				dialog.findViewById(R.id.dialog_copy).setVisibility(View.GONE);
				dialog.findViewById(R.id.dialog_divider).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_transmit).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_divider2).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_colllect).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_divider3).setVisibility(
						View.GONE);
			} else if (collect.getMtype() != null
					&& collect.getMtype().equals("3")) {
				dialog.findViewById(R.id.dialog_copy).setVisibility(View.GONE);
				dialog.findViewById(R.id.dialog_divider).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_transmit).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_divider2).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_colllect).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_divider3).setVisibility(
						View.GONE);
			}
			dialog.findViewById(R.id.dialog_copy).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (collect == null)
								return;
							Tools.copy(collect.getContent(), getContext());
							MyToast.getToast().showToast(getContext(),
									R.string.copyed);
							dialog.dismiss();
						}
					});
			dialog.findViewById(R.id.dialog_delete).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = getIntent();
							Bundle b = new Bundle();
							b.putSerializable("collect", collect);
							intent.putExtras(b);
							getContext().setResult(RESULT_OK, intent);
							closeOneAct(TAG);
							collect = null;
							MyToast.getToast().showToast(getContext(), "删除成功");
							dialog.dismiss();
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

}
