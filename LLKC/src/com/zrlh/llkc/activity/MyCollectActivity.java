package com.zrlh.llkc.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalDb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gauss.PlayCallback;
import com.gauss.SpeexPlayer;
import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.cache.LocalMemory;
import com.zzl.zl_app.entity.Collect;
import com.zzl.zl_app.util.TextUtil;
import com.zzl.zl_app.util.TimeUtil;
import com.zzl.zl_app.util.Tools;

public class MyCollectActivity extends BaseActivity {
	public static final String TAG = "mycollect";

	ImageButton back;
	TextView title_card;
	ListView mycollect_list;
	List<Collect> myCollect = new ArrayList<Collect>();
	MyCollectAdapter myCollectAdapter;
	FinalDb db;
	private Collect selectCollect;

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, MyCollectActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.mycollect);
		db = FinalDb.create(getContext());
		init();
	}

	private void init() {
		myCollect = db.findAllByWhere(Collect.class, "account='"
				+ LlkcBody.USER_ACCOUNT + "'");
		Collections.reverse(myCollect);

		title_card = (TextView) findViewById(R.id.title_tv);
		title_card.setText(R.string.my_collect);
		back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		this.findViewById(R.id.title_position).setVisibility(View.GONE);

		mycollect_list = (ListView) findViewById(R.id.mycollect_listview);
		myCollectAdapter = new MyCollectAdapter(getContext());
		mycollect_list.setAdapter(myCollectAdapter);
		// 详情
		mycollect_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Collect collect = myCollect.get(arg2);
				if (collect.getMtype().equals("2")) {
					Intent intent = new Intent();
					Bundle b = new Bundle();
					b.putString("picUrl", collect.getImg_big());
					intent.putExtras(b);
					PreviewPicActivity.launch(getContext(), intent);
				} else if (collect.getMtype().equals("1")) {
					Intent intent = new Intent(getContext(),
							MyCollectDetailActivity.class);
					Bundle b = new Bundle();
					b.putSerializable("collect", collect);
					intent.putExtras(b);
					startActivityForResult(intent, 101);
					// MyCollectDetailActivity.launch(getContext(), intent);
				}
			}
		});
		mycollect_list
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// TODO Auto-generated method stub
						selectCollect = myCollect.get(arg2);
						showMsgDialog("menu", R.layout.dialog_menu,
								getContext(), "提示", null);
						return false;
					}
				});
	}

	private class MyCollectAdapter extends BaseAdapter {

		LayoutInflater layoutInflater;
		MyCollectView myCollectView;
		FinalBitmap finalBitmap;

		public final class MyCollectView {
			ImageView head; // 头像
			TextView name; // 名称
			TextView time; // 时间
			LinearLayout content_layout;
			TextView content; // 内容
			LinearLayout img_layout;
			ImageView img; // 图片
			LinearLayout voice_layout;
			LinearLayout voice_cont_layout;
			ImageView voice;// 语音
			ProgressBar voice_p;
			TextView voice_length; // 语音长度
		}

		private MyCollectAdapter(Context context) {
			layoutInflater = LayoutInflater.from(context);
			finalBitmap = FinalBitmap.create(getApplicationContext());
			finalBitmap.configLoadingImage(R.drawable.head_default);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return myCollect.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return myCollect.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		SpeexPlayer splayer;

		public void stopPlayer() {
			if (splayer != null)
				splayer.stopPlay();
		}

		@Override
		public View getView(int position, View convertView,
				final ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				myCollectView = new MyCollectView();
				convertView = layoutInflater.inflate(
						R.layout.mycollect_list_item, null);
				myCollectView.head = (ImageView) convertView
						.findViewById(R.id.mycollect_list_item_head);
				myCollectView.time = (TextView) convertView
						.findViewById(R.id.mycollect_list_item_time);
				myCollectView.name = (TextView) convertView
						.findViewById(R.id.mycollect_list_item_name);
				myCollectView.content_layout = (LinearLayout) convertView
						.findViewById(R.id.mycollect_list_item_content_layout);
				myCollectView.content = (TextView) convertView
						.findViewById(R.id.mycollect_list_item_cont_tv);
				myCollectView.img_layout = (LinearLayout) convertView
						.findViewById(R.id.mycollect_list_item_img_layout);
				myCollectView.img = (ImageView) convertView
						.findViewById(R.id.mycollect_list_item_cont_imgv);
				myCollectView.voice_layout = (LinearLayout) convertView
						.findViewById(R.id.mycollect_list_item_voice_layout);
				myCollectView.voice_cont_layout = (LinearLayout) convertView
						.findViewById(R.id.mycollect_list_item_cont_voice);
				myCollectView.voice = (ImageView) convertView
						.findViewById(R.id.mycollect_list_item_cont_voice_imgv);
				myCollectView.voice_p = (ProgressBar) convertView
						.findViewById(R.id.mycollect_list_item_cont_voice_progressBar);
				myCollectView.voice_length = (TextView) convertView
						.findViewById(R.id.mycollect_list_item_cont_voice_time);
				convertView.setTag(myCollectView);
			} else {
				myCollectView = (MyCollectView) convertView.getTag();
			}
			final Collect collect = myCollect.get(position);
			String head = collect.getHead();
			if (Tools.isUrl(head))
				finalBitmap.display(myCollectView.head, head);
			else
				myCollectView.head.setImageResource(R.drawable.head_default);
			myCollectView.name.setText(collect.getName());
			myCollectView.time.setText(TimeUtil.getTimeStr2(collect.getTime(),
					"MM-dd"));
			if (collect.getMtype().equals("1")) {
				myCollectView.content_layout.setVisibility(View.VISIBLE);
				myCollectView.img_layout.setVisibility(View.GONE);
				myCollectView.voice_layout.setVisibility(View.GONE);
				if (collect.getContent() != null
						&& !collect.getContent().equals(""))
					myCollectView.content.setText(TextUtil.formatContent(
							collect.getContent(), getContext()));
				else
					myCollectView.content.setText("");
			} else if (collect.getMtype().equals("2")) {
				myCollectView.content_layout.setVisibility(View.GONE);
				myCollectView.img_layout.setVisibility(View.VISIBLE);
				myCollectView.voice_layout.setVisibility(View.GONE);
				if (collect.getImg_big() != null
						&& !collect.getImg_big().equals("")) {
					ImageCache.getInstance().loadImg(collect.getImg_big(),
							myCollectView.img, R.drawable.chat_default_bg);
				} else
					myCollectView.img
							.setImageResource(R.drawable.chat_default_bg);
			} else if (collect.getMtype().equals("3")) {
				myCollectView.content_layout.setVisibility(View.GONE);
				myCollectView.img_layout.setVisibility(View.GONE);
				myCollectView.voice_layout.setVisibility(View.VISIBLE);
				if (collect.getVoice().contains("http")) {
					String fileNa = Tools
							.getFileNameFromUrl(collect.getVoice());
					String dirName = Tools
							.getDirNameFromUrl(collect.getVoice());
					final String fileName = dirName + "_" + fileNa + ".spx";
					final File file = LocalMemory.getInstance().getFile(
							fileName);
					myCollectView.voice.setTag(fileName + "imgv");
					myCollectView.voice_p.setTag(fileName + "pb");
					if (file == null) {
						// Load
						myCollectView.voice_length.setVisibility(View.GONE);
						ImageCache.getInstance().loadVoiceFile(
								collect.getVoice(), getContext(),
								myCollectView.voice_length);
					}
					myCollectView.voice_length.setText(collect.getLength()
							+ "''");
					int len = collect.getLength() != null
							&& !collect.getLength().equals("") ? Integer
							.parseInt(collect.getLength()) : -1;
					int width = 90;
					Tools.log("Length", "default-width:" + width);
					if (len > 0) {
						width = (int) (width * (1 + len / (8.0)));
						width = (int) (width > (MainActivity.CURRENT_SCREEN_WIDTH * 3 / 5) ? (MainActivity.CURRENT_SCREEN_WIDTH * 3 / 5)
								: width);
						Tools.log("Length", "width" + width);
						myCollectView.voice_cont_layout
								.setLayoutParams(new LinearLayout.LayoutParams(
										width, LayoutParams.WRAP_CONTENT));
					}
					myCollectView.voice_cont_layout
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									if (myCollectView.voice_length
											.getVisibility() == View.VISIBLE)
										if (LocalMemory.getInstance()
												.checkSDCard()) {
											// TODO

											if (collect.getVoice() != null
													&& !collect.getVoice()
															.equals("")
													&& file != null) {
												if (splayer == null)
													splayer = new SpeexPlayer(
															mHandler);

												if (!splayer.isPlay)
													mHandler.postDelayed(
															new Runnable() {
																@Override
																public void run() {
																	splayer.startPlay(
																			file.getAbsolutePath(),
																			new PlayCallback() {

																				@Override
																				public void finish(
																						String path) {
																					ProgressBar pb = (ProgressBar) parent
																							.findViewWithTag(path
																									+ "pb");
																					if (pb != null)
																						pb.setVisibility(View.GONE);
																					ImageView imgv = (ImageView) parent
																							.findViewWithTag(path
																									+ "imgv");
																					if (imgv != null)
																						imgv.setVisibility(View.VISIBLE);
																				}
																			},
																			fileName);
																	ProgressBar pb = (ProgressBar) parent
																			.findViewWithTag(fileName
																					+ "pb");
																	if (pb != null)
																		pb.setVisibility(View.VISIBLE);
																	ImageView imgv = (ImageView) parent
																			.findViewWithTag(fileName
																					+ "imgv");
																	if (imgv != null)
																		imgv.setVisibility(View.GONE);
																}
															}, 400);
												else {
													ProgressBar pb = (ProgressBar) parent
															.findViewWithTag(fileName
																	+ "pb");
													if (pb != null)
														pb.setVisibility(View.GONE);
													ImageView imgv = (ImageView) parent
															.findViewWithTag(fileName
																	+ "imgv");
													if (imgv != null)
														imgv.setVisibility(View.VISIBLE);
													splayer.stopPlay();
												}
											}
										} else {
											MyToast.getToast()
													.showToast(getContext(),
															"请插入您的SDcard");
										}
								}
							});
				} else {
					myCollectView.voice.setTag(collect.getVoice() + "imgv");
					myCollectView.voice_p.setTag(collect.getVoice() + "pb");
					myCollectView.voice_length.setText(collect.getLength()
							+ "''");
					myCollectView.voice_length.setVisibility(View.VISIBLE);
					int len = collect.getLength() != null
							&& !collect.getLength().equals("") ? Integer
							.parseInt(collect.getLength()) : -1;
					int width = 90;
					Tools.log("Length", "default-width:" + width);
					if (len > 0) {
						width = (int) (width * (1 + len / (8.0)));
						width = (int) (width > (MainActivity.CURRENT_SCREEN_WIDTH * 3 / 5) ? (MainActivity.CURRENT_SCREEN_WIDTH * 3 / 5)
								: width);
						Tools.log("Length", "width" + width);
						myCollectView.voice_cont_layout
								.setLayoutParams(new LinearLayout.LayoutParams(
										width, LayoutParams.WRAP_CONTENT));
					}
					myCollectView.voice_cont_layout
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									if (myCollectView.voice_length
											.getVisibility() == View.VISIBLE)
										if (LocalMemory.getInstance()
												.checkSDCard()) {
											// TODO
											if (collect.getVoice() != null
													&& !collect.getVoice()
															.equals("")) {
												if (splayer == null)
													splayer = new SpeexPlayer(
															mHandler);

												if (!splayer.isPlay)
													mHandler.postDelayed(
															new Runnable() {

																@Override
																public void run() {

																	ProgressBar pb = (ProgressBar) parent
																			.findViewWithTag(collect
																					.getVoice()
																					+ "pb");
																	if (pb != null)
																		pb.setVisibility(View.VISIBLE);
																	ImageView imgv = (ImageView) parent
																			.findViewWithTag(collect
																					.getVoice()
																					+ "imgv");
																	if (imgv != null)
																		imgv.setVisibility(View.GONE);
																	splayer.startPlay(
																			collect.getVoice(),
																			new PlayCallback() {

																				@Override
																				public void finish(
																						String path) {
																					ProgressBar pb = (ProgressBar) parent
																							.findViewWithTag(path
																									+ "pb");
																					if (pb != null)
																						pb.setVisibility(View.GONE);
																					ImageView imgv = (ImageView) parent
																							.findViewWithTag(path
																									+ "imgv");
																					if (imgv != null)
																						imgv.setVisibility(View.VISIBLE);
																					// splayer.isPlay
																					// =
																					// false;
																				}

																			});
																}
															}, 400);
												else {

													ProgressBar pb = (ProgressBar) parent
															.findViewWithTag(collect
																	.getVoice()
																	+ "pb");
													if (pb != null)
														pb.setVisibility(View.GONE);
													ImageView imgv = (ImageView) parent
															.findViewWithTag(collect
																	.getVoice()
																	+ "imgv");
													if (imgv != null)
														imgv.setVisibility(View.VISIBLE);
													splayer.stopPlay();
												}
											}
										} else {
											MyToast.getToast()
													.showToast(getContext(),
															"请插入您的SDcard");
										}
								}
							});
				}
			}

			return convertView;
		}

	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

		};
	};

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
			if (selectCollect.getMtype() != null
					&& selectCollect.getMtype().equals("1")) {
				dialog.findViewById(R.id.dialog_transmit).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_divider2).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_colllect).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_divider3).setVisibility(
						View.GONE);
			} else if (selectCollect.getMtype() != null
					&& selectCollect.getMtype().equals("2")) {
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
			} else if (selectCollect.getMtype() != null
					&& selectCollect.getMtype().equals("3")) {
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
							if (selectCollect == null)
								return;
							Tools.copy(selectCollect.getContent(), getContext());
							MyToast.getToast().showToast(getContext(),
									R.string.copyed);
							dialog.dismiss();
						}
					});
			dialog.findViewById(R.id.dialog_delete).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							myCollect.remove(selectCollect);
							myCollectAdapter.notifyDataSetChanged();
							db.delete(selectCollect);
							selectCollect = null;
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

	@Override
	public void onBackPressed() {
		if (myCollectAdapter != null)
			myCollectAdapter.stopPlayer();

		closeOneAct(TAG);
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 101) {
			if (data != null) {
				Bundle b = data.getExtras();
				if (b != null) {
					Collect collect = (Collect) b.getSerializable("collect");
					if (collect != null) {
						for (int i = 0; i < myCollect.size(); i++) {
							if (collect.getContent().equals(
									myCollect.get(i).getContent())) {
								myCollect.remove(i);
								break;
							}
						}
						// myCollect.remove(collect);
						myCollectAdapter.notifyDataSetChanged();
						db.delete(collect);
					}
				}
			}
		}
	}

}
