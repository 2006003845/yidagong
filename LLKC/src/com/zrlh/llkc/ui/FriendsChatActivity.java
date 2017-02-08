package com.zrlh.llkc.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalDb;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gauss.PlayCallback;
import com.gauss.SoundMeter;
import com.gauss.SpeexPlayer;
import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.activity.MainActivity;
import com.zrlh.llkc.activity.MsgCenterActivity;
import com.zrlh.llkc.activity.PreviewPicActivity;
import com.zrlh.llkc.activity.TransmitActivity;
import com.zrlh.llkc.corporate.Face;
import com.zrlh.llkc.corporate.FaceAdapter;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.Friend;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.cache.FileConstant;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.cache.LocalMemory;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.db.BaseMsgTable;
import com.zzl.zl_app.db.MTable;
import com.zzl.zl_app.db.PrivateMsgDBOper;
import com.zzl.zl_app.entity.Collect;
import com.zzl.zl_app.entity.Msg;
import com.zzl.zl_app.entity.PrivateMsg;
import com.zzl.zl_app.util.FileTools;
import com.zzl.zl_app.util.ImageUtils;
import com.zzl.zl_app.util.TextUtil;
import com.zzl.zl_app.util.TimeUtil;
import com.zzl.zl_app.util.Tools;
import com.zzl.zl_app.widget.FixedViewFlipper;
import com.zzl.zl_app.widget.PullToRefreshBase.OnRefreshListener;
import com.zzl.zl_app.widget.PullToRefreshListView;

@SuppressLint("HandlerLeak")
public class FriendsChatActivity extends BaseActivity {

	public static final String TAG = "friendchat";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, FriendsChatActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		sendTransMsg();
	}

	// for receive customer msg from jpush server
	public static boolean isForeground = false;
	PullToRefreshListView friend_chat_list;
	EditText contentET;
	Button friend_chat_button;
	String toUidString;
	Friend toObj;
	TextView title_card;
	String Msg;
	String Stat;
	// String source = "";
	ImageButton friends;
	FinalDb db;

	List<PrivateMsg> message_Objs = new ArrayList<PrivateMsg>();
	FriendChatAdapter friendChatAdapter;

	public void update(boolean isMore, int size) {
		friendChatAdapter.notifyDataSetChanged();
		if (!isMore)
			friend_chat_list.mRefreshableView.setSelection(friendChatAdapter
					.getCount() - 1);
		else
			friend_chat_list.mRefreshableView.setSelection(size > 0 ? size - 1
					: 0);

	}

	PrivateMsgDBOper msgDBOper;

	private FixedViewFlipper flipper;

	private PrivateMsg transMsg;

	public void sendTransMsg() {
		if (toObj == null)
			return;
		if (transMsg != null) {
			// 转发
			MyToast.getToast().showToast(getContext(), "转发...");
			transMsg.time = String.valueOf(new Date().getTime());
			if (transMsg.mtype.equals("1")) {
				new TransmitTalkTask(toObj.getUid(), transMsg,
						transMsg.content, "").execute();
				// new PrivateTalkTask(transMsg, toObj.getUid()).execute();
			} else if (transMsg.mtype.equals("2")) {
				new TransmitTalkTask(toObj.getUid(), transMsg,
						transMsg.img_big, transMsg.img_small).execute();
			}
		}
		transMsg = null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		registerMessageReceiver();
		mSensor = new SoundMeter(mHandler);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.friendschat_activity);
		db = FinalDb.create(getContext());
		Intent intent = this.getIntent();
		Bundle b = intent.getExtras();
		if (b != null)
			transMsg = (PrivateMsg) b.getSerializable("msg");
		toObj = (Friend) intent.getSerializableExtra("toObj");
		if (toObj != null) {
			msgDBOper = (PrivateMsgDBOper) PrivateMsgDBOper.getDBOper(this);
			if (!msgDBOper
					.tabbleIsExist(msgDBOper.getTableName(toObj.getUid())))
				msgDBOper.creatTable(msgDBOper.getTableName(toObj.getUid()));
		}
		imageUri = Uri.fromFile(new File(IMAGE_FILE_LOCATION));
		initView();
		show();
		new InitTalksDataTask(toObj.getUid(), false).execute();
	}

	@Override
	public void onResume() {
		isForeground = true;
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(TAG);

	}

	private ImageView talkTBtn;
	private boolean isTalkInAudio = false;
	private LinearLayout textTalk;
	private TextView audioTalk;

	private View chat_popup;
	private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
			voice_rcd_hint_tooshort;
	private ImageView volume;

	void initView() {
		chat_popup = this.findViewById(R.id.chat_popup);
		voice_rcd_hint_rcding = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_rcding);
		voice_rcd_hint_loading = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_loading);
		voice_rcd_hint_tooshort = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_tooshort);
		volume = (ImageView) this.findViewById(R.id.volume);

		friend_chat_list = (PullToRefreshListView) findViewById(R.id.friend_chat_list);
		friend_chat_list.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new InitTalksDataTask(toObj.getUid(), true).execute();
			}
		});
		// friend_chat_list.setOnScrollListener(new OnScrollListener() {
		// boolean isScroll = false;
		//
		// @Override
		// public void onScrollStateChanged(AbsListView view, int scrollState) {
		// // TODO Auto-generated method stubS
		// isScroll = scrollState == OnScrollListener.SCROLL_STATE_IDLE;
		// }
		//
		// @Override
		// public void onScroll(AbsListView view, int firstVisibleItem,
		// int visibleItemCount, int totalItemCount) {
		//
		// if (firstVisibleItem == 0 && visibleItemCount > 0 && isScroll)
		// new InitTalksDataTask(toObj.getUid(), true).execute();
		// isScroll = false;
		// }
		// });

		contentET = (EditText) findViewById(R.id.friend_chat_edit);
		contentET.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (flipper.getVisibility() == View.VISIBLE) {
					flipper.setVisibility(View.GONE);
				}
			}
		});
		friend_chat_button = (Button) findViewById(R.id.friend_chat_button);
		friends = (ImageButton) findViewById(R.id.friends);
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		friends.setVisibility(View.GONE);
		title_card = (TextView) findViewById(R.id.title_card);
		flipper = (FixedViewFlipper) findViewById(R.id.chat_flipper);
		textTalk = (LinearLayout) this.findViewById(R.id.chat_talk_text_layout);
		audioTalk = (TextView) this.findViewById(R.id.chat_talk_audio_tv);

		audioTalk.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// 按下语音录制按钮时返回false执行父类OnTouch
				return false;
			}
		});
		audioTalk.setVisibility(View.GONE);
		textTalk.setVisibility(View.VISIBLE);
		talkTBtn = (ImageView) this.findViewById(R.id.chat_btn_audio);
		talkTBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flipper.getVisibility() == View.VISIBLE) {
					flipper.setVisibility(View.GONE);
				}
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive())
					imm.hideSoftInputFromWindow(contentET.getWindowToken(), 0);
				// TODO 语音
				if (isTalkInAudio) {
					talkTBtn.setImageResource(R.drawable.chat_btn_audio);
					audioTalk.setVisibility(View.GONE);
					textTalk.setVisibility(View.VISIBLE);
				} else {
					talkTBtn.setImageResource(R.drawable.chat_btn_keyboard);
					audioTalk.setVisibility(View.VISIBLE);
					textTalk.setVisibility(View.GONE);
				}
				isTalkInAudio = !isTalkInAudio;
			}
		});
		this.findViewById(R.id.chat_btn_more).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(contentET.getWindowToken(),
								0);

						talkTBtn.setImageResource(R.drawable.chat_btn_audio);
						audioTalk.setVisibility(View.GONE);
						textTalk.setVisibility(View.VISIBLE);
						isTalkInAudio = false;
						flipper.setVisibility(View.VISIBLE);
						flipper.setDisplayedChild(0);
					}
				});
		friend_chat_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String context_String = contentET.getText().toString();
				if (context_String == null || context_String.equals("")) {
					MyToast.getToast().showToast(
							getApplicationContext(),
							Tools.getStringFromRes(getContext(),
									R.string.send_null));
					return;
				}

				PrivateMsg msg = new PrivateMsg();
				msg.content = context_String.trim();
				msg.head = LlkcBody.ACCOUNT.getHead();
				msg.senderName = LlkcBody.USER_ACCOUNT;
				msg.senderId = LlkcBody.UID_ACCOUNT;
				msg.time = String.valueOf(new Date().getTime());
				msg.id = "0";
				msg.newitems = 0;
				msg.sRName = "";
				msg.state = 0;
				msg.type = "2";
				msg.mtype = "1";
				if (toObj != null)
					new PrivateTalkTask(msg, toObj.getUid()).execute();
			}
		});
		title_card.setText(Tools.getStringFromRes(getContext(), R.string.yu)
				+ toObj.getUname()
				+ Tools.getStringFromRes(getContext(), R.string.talk));

		initExpress();
		this.findViewById(R.id.chat_btn_face).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						showFaceImg();
					}
				});
		this.findViewById(R.id.chat_btn_image).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						showPicDialog();
					}
				});
	}

	private SoundMeter mSensor;

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 100) {
				updateDisplay(msg.arg1);
			}
		};
	};
	private int flag = 1;
	private boolean isShosrt = false;
	private long startVoiceT, endVoiceT;
	String voiceName;

	public void showRecordView() {
		audioTalk.setBackgroundResource(R.drawable.chat_audio_btn_bg);
		chat_popup.setVisibility(View.VISIBLE);
		voice_rcd_hint_loading.setVisibility(View.VISIBLE);
		voice_rcd_hint_rcding.setVisibility(View.GONE);
		voice_rcd_hint_tooshort.setVisibility(View.GONE);
		mHandler.postDelayed(new Runnable() {
			public void run() {
				if (!isShosrt) {
					voice_rcd_hint_loading.setVisibility(View.GONE);
					voice_rcd_hint_rcding.setVisibility(View.VISIBLE);
				}
			}
		}, 300);
	}

	int count = 0;
	long startTime;
	long nextTime;

	// 按下语音录制按钮时
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!Environment.getExternalStorageDirectory().exists()) {
			MyToast.getToast().showToast(getContext(), "No SDCard");
			return false;
		}

		if (isTalkInAudio) {
			int[] location = new int[2];
			audioTalk.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
			int btn_rc_Y = location[1];
			int btn_rc_X = location[0];
			if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
				if (!Environment.getExternalStorageDirectory().exists()) {
					MyToast.getToast().showToast(getContext(), "No SDCard");
					return false;
				}
				if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X) {// 判断手势按下的位置是否是语音录制按钮的范围内
					startVoiceT = new Date().getTime();

					if (count == 0) {
						startTime = System.currentTimeMillis();
						Tools.log("Voice", "startTime:" + startTime);
						count++;
						startVoiceT = new Date().getTime();
						Tools.log("Chat", "startVoiceT:" + startVoiceT);
						// voiceName = startVoiceT + ".amr";
						voiceName = FileConstant.savePath
								+ FileConstant.Path_Txt + "/" + startVoiceT
								+ ".spx";
						start(voiceName);
						showRecordView();
						// TODO 录音
					} else {
						nextTime = System.currentTimeMillis();
						Tools.log("Voice", "nextTime:" + nextTime);
						if (nextTime - startTime > 1100) {

							Tools.log("Chat", "startVoiceT:" + startVoiceT);
							// voiceName = startVoiceT + ".amr";
							voiceName = FileConstant.savePath
									+ FileConstant.Path_Txt + "/" + startVoiceT
									+ ".spx";
							start(voiceName);
							showRecordView();
						}
						startTime = nextTime;
					}
					flag = 2;
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {// 松开手势时执行录制完成
				System.out.println("4");
				audioTalk.setBackgroundResource(R.drawable.chat_audio_btn_bg);
				endVoiceT = new Date().getTime();
				stop();
				if (event.getY() < btn_rc_Y || event.getX() < btn_rc_X) {
					chat_popup.setVisibility(View.GONE);
					// 取消录制
					flag = 1;
					File file = new File(voiceName);
					if (file.exists()) {
						file.delete();
					}
				} else {
					// 成功录制
					voice_rcd_hint_rcding.setVisibility(View.GONE);

					Tools.log("Chat", "endVoiceT:" + endVoiceT);
					flag = 1;
					final int time = (int) (endVoiceT - startVoiceT);
					Tools.log("Chat", "time:" + time);
					if (time < 1000) {
						isShosrt = true;
						voice_rcd_hint_loading.setVisibility(View.GONE);
						voice_rcd_hint_rcding.setVisibility(View.GONE);
						voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
						mHandler.postDelayed(new Runnable() {
							public void run() {
								voice_rcd_hint_tooshort
										.setVisibility(View.GONE);
								chat_popup.setVisibility(View.GONE);
								isShosrt = false;
							}
						}, 500);
						File file = new File(voiceName);
						if (file.exists()) {
							file.delete();
						}
						return false;
					} else {
						isShosrt = false;
						final PrivateMsg msg = new PrivateMsg();
						msg.content = null;
						msg.head = LlkcBody.ACCOUNT.getHead();
						msg.senderName = LlkcBody.USER_ACCOUNT;
						msg.senderId = LlkcBody.UID_ACCOUNT;
						msg.time = String.valueOf(new Date().getTime());
						msg.id = "0";
						msg.newitems = 0;
						msg.sRName = "";
						msg.state = 0;
						msg.type = "2";
						msg.mtype = "3";
						msg.length = time / 1000 + "";
						msg.voice = voiceName;

						if (toObj != null)
							mHandler.postDelayed(new Runnable() {

								@Override
								public void run() {
									new PrivateVoiceTalkTask(voiceName, toObj
											.getUid(), msg, startVoiceT
											+ ".spx", time / 1000 + "")
											.execute();
								}
							}, 500);

					}
					chat_popup.setVisibility(View.GONE);
				}
			}
		}
		return super.onTouchEvent(event);
	}

	private GridView express;

	private void initExpress() {
		express = (GridView) this.findViewById(R.id.chat_express_gridview);
		express.setAdapter(new FaceAdapter(getContext(), Face.faceNames));
		express.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position < Face.faceNames.length) {
					contentET.append(/* TextUtil.formatImage( */"["
							+ Face.faceNames[position] + "]"/*
															 * , getContext ())
															 */);
				}
			}
		});
	}

	/**
	 * 显示表情
	 */
	private void showFaceImg() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(contentET.getWindowToken(), 0);
		flipper.setDisplayedChild(1);
	}

	private void showPicDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this).setItems(
				R.array.picFrom, picListener).create();
		dialog.show();
	}

	private static final int PIC_TAKE_PHONE = 1;
	private static final int PIC_ALBUM = 2;
	private Uri imageUri;
	// private static String IMAGE_FILE_LOCATION = FileConstant.savePath
	// + "/chat_temp.jpg";
	private static String IMAGE_FILE_LOCATION = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/chat_temp.jpg";
	private DialogInterface.OnClickListener picListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == 0) {
				try {
					FileTools.deleteFile(IMAGE_FILE_LOCATION);
				} catch (IOException e) {

				}
				if (imageUri != null) {
					// capture a bitmap and store it in Uri
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// if (LocalMemory.getInstance().checkSDCard())
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivityForResult(intent, PIC_TAKE_PHONE);
				}
			} else if (which == 1) {
				Intent intent = new Intent();
				// Type为image
				intent.setType("image/*");
				// Action:选择数据然后返回
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, PIC_ALBUM);
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle b = null;
		switch (requestCode) {

		case PIC_TAKE_PHONE:
			if (resultCode != RESULT_OK) {
				return;
			}
			/*
			 * if (!LocalMemory.getInstance().checkSDCard()) { Bundle bundle =
			 * data.getExtras(); // 获取相机返回的数据，并转换为图片格式 Bitmap bitmap2 = (Bitmap)
			 * bundle.get("data"); if (bitmap2 != null) { Bitmap bm =
			 * ImageUtils.zoomBitmap(bitmap2, 480, 800); sendImgMsg(bm); } }
			 * else
			 */if (imageUri != null) {
				Bitmap bitmap = ImageUtils.compressBimap(IMAGE_FILE_LOCATION);
				if (bitmap == null) {
					return;
				}
				int degree = ImageUtils.readPictureDegree(IMAGE_FILE_LOCATION);
				if (degree != 0)
					bitmap = ImageUtils.rotaingImageView(degree, bitmap);
				sendImgMsg(bitmap);
			}
			break;
		case PIC_ALBUM:// from crop_big_picture
			if (resultCode != RESULT_OK) {
				return;
			}
			Uri originalUri = data.getData(); // 获得图片的uri
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(originalUri, proj, null, null, null);
			if (cursor == null)
				return;
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			String path = cursor.getString(column_index);
			Bitmap bitmap = ImageUtils.compressBimap(path);
			if (bitmap != null) {
				int degree = ImageUtils.readPictureDegree(path);
				if (degree != 0)
					bitmap = ImageUtils.rotaingImageView(degree, bitmap);
				Bitmap newBm = ImageUtils.zoomBitmap(bitmap,
						(int) (450 * MainActivity.WIDTH_RATIO),
						(int) (780 * MainActivity.WIDTH_RATIO));
				if (!bitmap.isRecycled())
					bitmap.recycle();
				if (newBm != null)
					sendImgMsg(newBm);
			}
			break;
		}
	}

	public void sendImgMsg(Bitmap bitmap) {
		PrivateMsg msg = new PrivateMsg();
		msg.content = null;
		msg.head = LlkcBody.ACCOUNT.getHead();
		msg.senderName = LlkcBody.USER_ACCOUNT;
		msg.senderId = LlkcBody.UID_ACCOUNT;
		msg.time = String.valueOf(new Date().getTime());
		msg.id = "0";
		msg.newitems = 0;
		msg.sRName = "";
		msg.state = 0;
		msg.type = "2";
		msg.mtype = "2";
		if (toObj != null)
			new PrivateImgTalkTask(bitmap, toObj.getUid(), msg).execute();
	}

	class PrivateTalkTask extends AsyncTask<Object, Integer, Boolean> {
		private PrivateMsg msg;
		private String toUid;

		public PrivateTalkTask(PrivateMsg msg, String toUid) {
			this.msg = msg;
			this.toUid = toUid;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.sendnow));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			try {
				return Community.getInstance(getApplicationContext())
						.privateChat(LlkcBody.USER_ACCOUNT,
								LlkcBody.PASS_ACCOUNT, msg, toUid);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}

			if (result && friendChatAdapter != null) {
				msg.state = 1;

				contentET.setText("");
				message_Objs.add(msg);
				friendChatAdapter.notifyDataSetChanged();
				friend_chat_list.mRefreshableView
						.setSelection(friendChatAdapter.getCount() - 1);
				MyToast.getToast().showToast(
						getApplicationContext(),
						Tools.getStringFromRes(getContext(),
								R.string.send_success));
				boolean b = msgDBOper.insertMsg(msg,
						msgDBOper.getTableName(toUid));

				MTable mOper = (MTable) MTable.getDBOper(getContext());
				Msg m = mOper.isMsgExist(mOper.getTableName(""),
						MTable.MTable_Label + "=? and "
								+ BaseMsgTable.Msg_Account + "=?",
						new String[] {
								toUid,
								LlkcBody.USER_ACCOUNT == null ? ""
										: LlkcBody.USER_ACCOUNT });

				Msg ms = new Msg(msg.id, msg.type, toObj.getUname(),
						toObj.getHead(), msg.time, msg.content, toObj.getUid());

				if (m != null) {
					ms.newitems = m.newitems;

					mOper.updateMsg(ms, mOper.getTableName(""), toUid,
							toObj.getUname(), ms.head);
				} else {
					ms.newitems = 0;
					mOper.insertMsg(ms, mOper.getTableName(""), toUid,
							toObj.getUname(), ms.head);
				}
				Intent msgIntent2 = new Intent(
						MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
				msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
				msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 2);
				getContext().sendBroadcast(msgIntent2);
			} else {
				msg.state = 0;
				MyToast.getToast()
						.showToast(
								getApplicationContext(),
								Tools.getStringFromRes(getContext(),
										R.string.send_fail));
			}

			//
			super.onPostExecute(result);
		}
	}

	class PrivateImgTalkTask extends AsyncTask<Object, Integer, PrivateMsg> {
		private Bitmap img;
		private String toUid;
		private PrivateMsg msg;

		public PrivateImgTalkTask(Bitmap img, String toUid, PrivateMsg msg) {
			this.img = img;
			this.toUid = toUid;
			this.msg = msg;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.sendnow));
			dialog.show();
		}

		@Override
		protected PrivateMsg doInBackground(Object... params) {

			try {
				return Community.getInstance(getApplicationContext())
						.privateImgChat(getContext(), LlkcBody.USER_ACCOUNT,
								LlkcBody.PASS_ACCOUNT, img, toUid, msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(PrivateMsg result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null && friendChatAdapter != null) {
				msg = result;
				msg.state = 1;
				contentET.setText("");
				message_Objs.add(msg);
				friendChatAdapter.notifyDataSetChanged();
				friend_chat_list.mRefreshableView
						.setSelection(friendChatAdapter.getCount() - 1);
				MyToast.getToast().showToast(
						getApplicationContext(),
						Tools.getStringFromRes(getContext(),
								R.string.send_success));
				boolean b = msgDBOper.insertMsg(msg,
						msgDBOper.getTableName(toUid));

				// 加入到消息中心
				MTable mOper = (MTable) MTable.getDBOper(getContext());
				Msg m = mOper.isMsgExist(mOper.getTableName(""),
						MTable.MTable_Label + "=? and "
								+ BaseMsgTable.Msg_Account + "=?",
						new String[] {
								toUid,
								LlkcBody.USER_ACCOUNT == null ? ""
										: LlkcBody.USER_ACCOUNT });
				Msg ms = new Msg(msg.id, msg.type, toObj.getUname(),
						toObj.getHead(), msg.time, "(图片)", toObj.getUid());
				if (m != null) {
					ms.newitems = m.newitems;

					mOper.updateMsg(ms, mOper.getTableName(""), toUid,
							toObj.getUname(), ms.head);
				} else {
					ms.newitems = 0;
					mOper.insertMsg(ms, mOper.getTableName(""), toUid,
							toObj.getUname(), ms.head);
				}
				Intent msgIntent2 = new Intent(
						MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
				msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
				msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 2);
				getContext().sendBroadcast(msgIntent2);

			} else {
				msg.state = 0;
				MyToast.getToast()
						.showToast(
								getApplicationContext(),
								Tools.getStringFromRes(getContext(),
										R.string.send_fail));
			}

			super.onPostExecute(result);
		}
	}

	class PrivateVoiceTalkTask extends AsyncTask<Object, Integer, PrivateMsg> {
		private String filePath;
		private String toUid;
		private PrivateMsg msg;

		private String fileName;
		private String time;

		public PrivateVoiceTalkTask(String filePath, String toUid,
				PrivateMsg msg, String fileName, String time) {
			this.filePath = filePath;
			this.toUid = toUid;
			this.msg = msg;
			this.fileName = fileName;
			this.time = time;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.sendnow));
			dialog.show();
		}

		@Override
		protected PrivateMsg doInBackground(Object... params) {

			try {
				return Community.getInstance(getApplicationContext())
						.privateVoiceChat(getContext(), LlkcBody.USER_ACCOUNT,
								LlkcBody.PASS_ACCOUNT, toUid, filePath, msg,
								fileName, time);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(PrivateMsg result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null && friendChatAdapter != null) {
				msg = result;
				msg.time = new Date().getTime() + "";
				message_Objs.add(msg);
				friendChatAdapter.notifyDataSetChanged();
				msg.state = 1;
				contentET.setText("");

				friendChatAdapter.notifyDataSetChanged();
				friend_chat_list.mRefreshableView
						.setSelection(friendChatAdapter.getCount() - 1);
				MyToast.getToast().showToast(
						getApplicationContext(),
						Tools.getStringFromRes(getContext(),
								R.string.send_success));
				boolean b = msgDBOper.insertMsg(msg,
						msgDBOper.getTableName(toUid));

				// 加入到消息中心
				MTable mOper = (MTable) MTable.getDBOper(getContext());
				Msg m = mOper.isMsgExist(mOper.getTableName(""),
						MTable.MTable_Label + "=? and "
								+ BaseMsgTable.Msg_Account + "=?",
						new String[] {
								toUid,
								LlkcBody.USER_ACCOUNT == null ? ""
										: LlkcBody.USER_ACCOUNT });
				Msg ms = new Msg(msg.id, msg.type, toObj.getUname(),
						toObj.getHead(), msg.time, "(语音)", toObj.getUid());
				if (m != null) {
					ms.newitems = m.newitems;

					mOper.updateMsg(ms, mOper.getTableName(""), toUid,
							toObj.getUname(), ms.head);
				} else {
					ms.newitems = 0;
					mOper.insertMsg(ms, mOper.getTableName(""), toUid,
							toObj.getUname(), ms.head);
				}
				Intent msgIntent2 = new Intent(
						MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
				msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
				msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 2);
				getContext().sendBroadcast(msgIntent2);

			} else {
				msg.state = 0;
				MyToast.getToast()
						.showToast(
								getApplicationContext(),
								Tools.getStringFromRes(getContext(),
										R.string.send_fail));
			}

			super.onPostExecute(result);
		}
	}

	ProgressDialog dialog = null;

	class TransmitTalkTask extends AsyncTask<Object, Integer, PrivateMsg> {

		private String toUid;
		private PrivateMsg msg;
		private String paramA;
		private String paramB;

		public TransmitTalkTask(String toUid, PrivateMsg msg, String paramA,
				String paramB) {
			this.toUid = toUid;
			this.msg = msg;
			this.paramA = paramA;
			this.paramB = paramB;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.sendnow));
			dialog.show();
		}

		@Override
		protected PrivateMsg doInBackground(Object... params) {

			try {
				return Community.getInstance(getApplicationContext())
						.transmitChat_Private(LlkcBody.USER_ACCOUNT,
								LlkcBody.PASS_ACCOUNT, toUid, msg.mtype,
								paramA, paramB, msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(PrivateMsg result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}

			if (result != null && friendChatAdapter != null) {
				msg = result;
				msg.state = 1;
				contentET.setText("");
				message_Objs.add(msg);
				friendChatAdapter.notifyDataSetChanged();
				friend_chat_list.mRefreshableView
						.setSelection(friendChatAdapter.getCount() - 1);
				MyToast.getToast().showToast(
						getApplicationContext(),
						Tools.getStringFromRes(getContext(),
								R.string.send_success));
				boolean b = msgDBOper.insertMsg(msg,
						msgDBOper.getTableName(toUid));

				// 加入到消息中心
				MTable mOper = (MTable) MTable.getDBOper(getContext());
				Msg m = mOper.isMsgExist(mOper.getTableName(""),
						MTable.MTable_Label + "=? and "
								+ BaseMsgTable.Msg_Account + "=?",
						new String[] {
								toUid,
								LlkcBody.USER_ACCOUNT == null ? ""
										: LlkcBody.USER_ACCOUNT });
				Msg ms = new Msg(msg.id, msg.type, toObj.getUname(),
						toObj.getHead(), msg.time, "(图片)", toObj.getUid());

				if (m != null) {
					ms.newitems = m.newitems;
					// ms.newitems = m.newitems + 1;
					mOper.updateMsg(ms, mOper.getTableName(""), toUid,
							toObj.getUname(), ms.head);
				} else {
					ms.newitems = 0;
					mOper.insertMsg(ms, mOper.getTableName(""), toUid,
							toObj.getUname(), ms.head);
				}
				Intent msgIntent2 = new Intent(
						MsgCenterActivity.MESSAGE_RECEIVED_ACTION);
				msgIntent2.putExtra(MsgCenterActivity.KEY_MESSAGE, msg);
				msgIntent2.putExtra(MsgCenterActivity.KEY_TYPE, 2);
				getContext().sendBroadcast(msgIntent2);

			} else {
				msg.state = 0;
				MyToast.getToast()
						.showToast(
								getApplicationContext(),
								Tools.getStringFromRes(getContext(),
										R.string.send_fail));
			}

			super.onPostExecute(result);
		}
	}

	void show() {
		friendChatAdapter = new FriendChatAdapter(this);
		friend_chat_list.mRefreshableView.setAdapter(friendChatAdapter);
	}

	@Override
	public void onPause() {
		isForeground = false;
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(TAG);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mMessageReceiver);
		if (mSensor != null)
			mSensor.destory();
		super.onDestroy();
	}

	class FriendChatAdapter extends BaseAdapter {
		LayoutInflater layoutInflater;
		ChatJobView chatJobView;
		FinalBitmap finalBitmap;

		private FriendChatAdapter(Context context) {
			layoutInflater = LayoutInflater.from(context);
			finalBitmap = FinalBitmap.create(getApplicationContext());
			finalBitmap.configLoadingImage(R.drawable.head_default);
		}

		public final class ChatJobView {
			RelativeLayout other_chat_rel;
			FixedViewFlipper other_chat_viewflipper;
			ImageView other_chat_head_imgv;
			TextView other_chat_cont_tv;
			ImageView other_chat_cont_imgv;
			LinearLayout other_chat_cont_voice;
			ImageView other_chat_cont_voice_imgv;
			ProgressBar other_chat_cont_voice_pb;
			TextView other_chat_cont_length;

			RelativeLayout mine_chat_rel;
			FixedViewFlipper mine_chat_viewflipper;
			ImageView mine_chat_head_imgv;
			TextView mine_chat_cont_tv;
			ImageView mine_chat_cont_imgv;
			LinearLayout mine_chat_cont_voice;
			ImageView mine_chat_cont_voice_imgv;
			ProgressBar mine_chat_cont_voice_pb;
			TextView mine_chat_cont_length;

			TextView time;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return message_Objs.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return message_Objs.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		SpeexPlayer splayer;

		public void stopPlayer() {
			if (splayer != null)
				splayer.stopPlay();
		}

		@Override
		public View getView(int arg0, View arg1, final ViewGroup parent) {
			// TODO Auto-generated method stub

			// TODO Auto-generated method stub
			if (arg1 == null) {
				chatJobView = new ChatJobView();
				arg1 = layoutInflater.inflate(R.layout.friendchat_list_item,
						null);
				chatJobView.other_chat_rel = (RelativeLayout) arg1
						.findViewById(R.id.item_chatlist_other_rel);
				chatJobView.other_chat_head_imgv = (ImageView) arg1
						.findViewById(R.id.item_chatlist_other_head_imgv);
				chatJobView.other_chat_viewflipper = (FixedViewFlipper) arg1
						.findViewById(R.id.item_chatlist_other_viewflipper);
				chatJobView.other_chat_cont_tv = (TextView) arg1
						.findViewById(R.id.item_chatlist_other_cont_tv);
				chatJobView.other_chat_cont_imgv = (ImageView) arg1
						.findViewById(R.id.item_chatlist_other_cont_imgv);
				chatJobView.other_chat_cont_voice = (LinearLayout) arg1
						.findViewById(R.id.item_chatlist_other_cont_voice);
				chatJobView.other_chat_cont_voice_imgv = (ImageView) arg1
						.findViewById(R.id.item_chatlist_other_cont_voice_imgv);
				chatJobView.other_chat_cont_voice_pb = (ProgressBar) arg1
						.findViewById(R.id.item_chatlist_other_cont_voice_progressBar);
				chatJobView.other_chat_cont_length = (TextView) arg1
						.findViewById(R.id.item_chatlist_other_cont_voice_time);
				// chatJobView.other_chat_cont_imgv.setMinimumWidth(150);
				// chatJobView.other_chat_cont_imgv.setMinimumHeight(150);
				// chatJobView.other_chat_cont_imgv.setMaxWidth(150);
				// chatJobView.other_chat_cont_imgv.setMaxHeight(150);

				chatJobView.mine_chat_rel = (RelativeLayout) arg1
						.findViewById(R.id.item_chatlist_mine_rel);
				chatJobView.mine_chat_head_imgv = (ImageView) arg1
						.findViewById(R.id.item_chatlist_mine_head_imgv);
				chatJobView.mine_chat_viewflipper = (FixedViewFlipper) arg1
						.findViewById(R.id.item_chatlist_mine_viewflipper);
				chatJobView.mine_chat_cont_tv = (TextView) arg1
						.findViewById(R.id.item_chatlist_mine_cont_tv);
				chatJobView.mine_chat_cont_imgv = (ImageView) arg1
						.findViewById(R.id.item_chatlist_mine_cont_imgv);
				chatJobView.mine_chat_cont_voice = (LinearLayout) arg1
						.findViewById(R.id.item_chatlist_mine_cont_voice);
				chatJobView.mine_chat_cont_voice_imgv = (ImageView) arg1
						.findViewById(R.id.item_chatlist_mine_cont_voice_imgv);
				chatJobView.mine_chat_cont_voice_pb = (ProgressBar) arg1
						.findViewById(R.id.item_chatlist_mine_cont_voice_progressBar);
				chatJobView.mine_chat_cont_length = (TextView) arg1
						.findViewById(R.id.item_chatlist_mine_cont_voice_time);
				// chatJobView.mine_chat_cont_imgv.setMinimumWidth(150);
				// chatJobView.mine_chat_cont_imgv.setMinimumHeight(150);
				// chatJobView.mine_chat_cont_imgv.setMaxWidth(150);
				// chatJobView.mine_chat_cont_imgv.setMaxHeight(150);

				chatJobView.time = (TextView) arg1
						.findViewById(R.id.friend_chat_time);

				arg1.setTag(chatJobView);
			} else {
				chatJobView = (ChatJobView) arg1.getTag();
			}
			final PrivateMsg msg = (PrivateMsg) message_Objs.get(arg0);
			if (LlkcBody.ACCOUNT == null)
				return arg1;
			if (LlkcBody.UID_ACCOUNT.equals(msg.senderId)) {
				chatJobView.mine_chat_cont_imgv.setTag(msg.img_big);
				chatJobView.mine_chat_cont_voice_imgv
						.setTag(msg.voice + "imgv");
				chatJobView.mine_chat_cont_voice_pb.setTag(msg.voice + "pb");
				chatJobView.mine_chat_rel.setVisibility(View.VISIBLE);
				chatJobView.other_chat_rel.setVisibility(View.GONE);
				String head = LlkcBody.ACCOUNT.getHead();
				if (Tools.isUrl(head))
					finalBitmap.display(chatJobView.mine_chat_head_imgv, head);
				else
					chatJobView.mine_chat_head_imgv
							.setImageResource(R.drawable.head_default);
				chatJobView.mine_chat_cont_tv
						.setOnLongClickListener(new OnLongClickListener() {

							@Override
							public boolean onLongClick(View v) {
								selectedMsg = msg;
								showMsgDialog("menu", R.layout.dialog_menu,
										getContext(), toObj == null ? ""
												: toObj.getUname(), null);
								return true;
							}
						});
				if (msg.mtype != null && msg.mtype.equals("1")) {
					chatJobView.mine_chat_viewflipper.setDisplayedChild(0);
					if (msg.content != null && !msg.content.equals(""))
						chatJobView.mine_chat_cont_tv.setText(TextUtil
								.formatContent(msg.content, getContext()));
					else
						chatJobView.mine_chat_cont_tv.setText("");

				} else if (msg.mtype != null && msg.mtype.equals("2")) {
					chatJobView.mine_chat_viewflipper.setDisplayedChild(1);
					chatJobView.mine_chat_cont_imgv
							.setImageResource(R.drawable.chat_default_bg);
					if (msg.img_big != null && !msg.img_big.equals("")) {

						// ImageCache.getInstance().loadImg(msg.img_big, arg2,
						// R.drawable.chat_default_bg);

						ImageCache.getInstance().loadImg(msg.img_big,
								chatJobView.mine_chat_cont_imgv,
								R.drawable.chat_default_bg);
					} else
						chatJobView.mine_chat_cont_imgv
								.setImageResource(R.drawable.chat_default_bg);

					chatJobView.mine_chat_cont_imgv
							.setOnLongClickListener(new OnLongClickListener() {

								@Override
								public boolean onLongClick(View v) {
									selectedMsg = msg;
									showMsgDialog("menu", R.layout.dialog_menu,
											getContext(), toObj == null ? ""
													: toObj.getUname(), null);
									return true;
								}
							});
				} else if (msg.mtype != null && msg.mtype.equals("3")) {
					chatJobView.mine_chat_viewflipper.setDisplayedChild(2);
					chatJobView.mine_chat_cont_length
							.setText(msg.length + "''");
					chatJobView.mine_chat_cont_length
							.setVisibility(View.VISIBLE);
					int len = msg.length != null && !msg.length.equals("") ? Integer
							.parseInt(msg.length) : -1;
					int width = 90;
					Tools.log("Length", "default-width:" + width);
					if (len > 0) {
						width = (int) (width * (1 + len / (8.0)));
						width = (int) (width > (MainActivity.CURRENT_SCREEN_WIDTH * 3 / 5) ? (MainActivity.CURRENT_SCREEN_WIDTH * 3 / 5)
								: width);
						Tools.log("Length", "width" + width);
						chatJobView.mine_chat_cont_voice
								.setLayoutParams(new LinearLayout.LayoutParams(
										width, LayoutParams.WRAP_CONTENT));
					}
					chatJobView.mine_chat_cont_voice
							.setOnLongClickListener(new OnLongClickListener() {

								@Override
								public boolean onLongClick(View v) {
									selectedMsg = msg;
									showMsgDialog("menu", R.layout.dialog_menu,
											getContext(), toObj == null ? ""
													: toObj.getUname(), null);
									return true;
								}
							});
					chatJobView.mine_chat_cont_voice
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									if (chatJobView.mine_chat_cont_length
											.getVisibility() == View.VISIBLE)
										if (LocalMemory.getInstance()
												.checkSDCard()) {
											// TODO
											if (msg.voice != null
													&& !msg.voice.equals("")) {
												if (splayer == null)
													splayer = new SpeexPlayer(
															mHandler);

												if (!splayer.isPlay)
													mHandler.postDelayed(
															new Runnable() {

																@Override
																public void run() {

																	ProgressBar pb = (ProgressBar) parent
																			.findViewWithTag(msg.voice
																					+ "pb");
																	if (pb != null)
																		pb.setVisibility(View.VISIBLE);
																	ImageView imgv = (ImageView) parent
																			.findViewWithTag(msg.voice
																					+ "imgv");
																	if (imgv != null)
																		imgv.setVisibility(View.GONE);
																	splayer.startPlay(
																			msg.voice,
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
															.findViewWithTag(msg.voice
																	+ "pb");
													if (pb != null)
														pb.setVisibility(View.GONE);
													ImageView imgv = (ImageView) parent
															.findViewWithTag(msg.voice
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
					chatJobView.mine_chat_viewflipper.setDisplayedChild(0);
					if (msg.content != null && !msg.content.equals(""))
						chatJobView.mine_chat_cont_tv.setText(TextUtil
								.formatContent(msg.content, getContext()));
					else
						chatJobView.mine_chat_cont_tv.setText("");
				}
			} else {
				chatJobView.other_chat_cont_imgv.setTag(msg.img_small);

				chatJobView.mine_chat_rel.setVisibility(View.GONE);
				chatJobView.other_chat_rel.setVisibility(View.VISIBLE);
				chatJobView.mine_chat_cont_tv.setText(TextUtil.formatContent(
						msg.content, getContext()));
				toObj.setHead(msg.head);
				if (Tools.isUrl(msg.head))
					finalBitmap.display(chatJobView.other_chat_head_imgv,
							msg.head);
				else
					chatJobView.other_chat_head_imgv
							.setImageResource(R.drawable.head_default);
				if (msg.mtype != null && msg.mtype.equals("1")) {
					chatJobView.other_chat_viewflipper.setDisplayedChild(0);
					if (msg.content != null && !msg.content.equals(""))
						chatJobView.other_chat_cont_tv.setText(TextUtil
								.formatContent(msg.content, getContext()));
					else
						chatJobView.other_chat_cont_tv.setText("");
					chatJobView.other_chat_cont_tv
							.setOnLongClickListener(new OnLongClickListener() {

								@Override
								public boolean onLongClick(View v) {
									selectedMsg = msg;
									showMsgDialog("menu", R.layout.dialog_menu,
											getContext(), toObj == null ? ""
													: toObj.getUname(), null);
									return true;
								}
							});
				} else if (msg.mtype != null && msg.mtype.equals("2")) {
					chatJobView.other_chat_viewflipper.setDisplayedChild(1);
					chatJobView.other_chat_cont_imgv
							.setImageResource(R.drawable.chat_default_bg);
					chatJobView.other_chat_cont_imgv
							.setOnLongClickListener(new OnLongClickListener() {

								@Override
								public boolean onLongClick(View v) {
									selectedMsg = msg;
									showMsgDialog("menu", R.layout.dialog_menu,
											getContext(), toObj == null ? ""
													: toObj.getUname(), null);
									return true;
								}
							});
					if (msg.img_small != null && !msg.img_small.equals("")) {
						// ImageCache.getInstance().loadImg(msg.img_small, arg2,
						// R.drawable.chat_default_bg);
						//
						ImageCache.getInstance().loadImg(msg.img_small,
								chatJobView.other_chat_cont_imgv,
								R.drawable.chat_default_bg);
					} else
						chatJobView.other_chat_cont_imgv
								.setImageResource(R.drawable.chat_default_bg);
				} else if (msg.mtype != null && msg.mtype.equals("3")) {
					chatJobView.other_chat_viewflipper.setDisplayedChild(2);

					String fileNa = Tools.getFileNameFromUrl(msg.voice);
					String dirName = Tools.getDirNameFromUrl(msg.voice);
					final String fileName = dirName + "_" + fileNa + ".spx";
					final File file = LocalMemory.getInstance().getFile(
							fileName);
					chatJobView.other_chat_cont_voice_imgv.setTag(fileName
							+ "imgv");
					chatJobView.other_chat_cont_voice_pb
							.setTag(fileName + "pb");
					if (file == null) {
						// Load
						chatJobView.other_chat_cont_length
								.setVisibility(View.GONE);
						ImageCache.getInstance().loadVoiceFile(msg.voice,
								getContext(),
								chatJobView.other_chat_cont_length);
					} else {

					}

					// chatJobView.other_chat_cont_length
					// .setVisibility(View.VISIBLE);

					int len = msg.length != null && !msg.length.equals("") ? Integer
							.parseInt(msg.length) : -1;
					int width = 90;
					Tools.log("Length", "default-width:" + width);
					if (len > 0) {
						width = (int) (width * (1 + len / (8.0)));
						width = (int) (width > (MainActivity.CURRENT_SCREEN_WIDTH * 3 / 5) ? (MainActivity.CURRENT_SCREEN_WIDTH * 3 / 5)
								: width);
						Tools.log("Length", "width" + width);
						chatJobView.other_chat_cont_voice
								.setLayoutParams(new LinearLayout.LayoutParams(
										width, LayoutParams.WRAP_CONTENT));
					}
					chatJobView.other_chat_cont_voice
							.setOnLongClickListener(new OnLongClickListener() {

								@Override
								public boolean onLongClick(View v) {
									selectedMsg = msg;
									showMsgDialog("menu", R.layout.dialog_menu,
											getContext(), toObj == null ? ""
													: toObj.getUname(), null);
									return true;
								}
							});
					chatJobView.other_chat_cont_voice
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									if (chatJobView.other_chat_cont_length
											.getVisibility() == View.VISIBLE)
										if (LocalMemory.getInstance()
												.checkSDCard()) {
											// TODO

											if (msg.voice != null
													&& !msg.voice.equals("")
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
					chatJobView.other_chat_cont_length.setText(msg.length
							+ "''");
				} else {
					chatJobView.other_chat_viewflipper.setDisplayedChild(0);
					if (msg.content != null && !msg.content.equals(""))
						chatJobView.other_chat_cont_tv.setText(TextUtil
								.formatContent(msg.content, getContext()));
					else
						chatJobView.other_chat_cont_tv.setText("");
				}
			}
			String time = msg.time;
			if (time != null && !"".equals(time)) {
				try {
					Date date = new Date(Long.parseLong(time));
					String t = TimeUtil.getTimeStr(date);
					chatJobView.time.setText(t);
				} catch (Exception e) {
					chatJobView.time.setText("");
				}
			} else {
				chatJobView.time.setText("");
			}
			chatJobView.other_chat_head_imgv
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.putExtra("member", toObj);
							intent.putExtra("source", "Friend");
							intent.setClass(getApplicationContext(),
									UserInfoActivity.class);
							startActivity(intent);
						}
					});
			chatJobView.mine_chat_cont_imgv
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO
							Intent intent = new Intent();
							Bundle b = new Bundle();
							b.putString("picUrl", msg.img_big);
							intent.putExtras(b);
							PreviewPicActivity.launch(getContext(), intent);
						}
					});
			chatJobView.other_chat_cont_imgv
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO
							Intent intent = new Intent();
							Bundle b = new Bundle();
							b.putString("picUrl", msg.img_big);
							intent.putExtras(b);
							PreviewPicActivity.launch(getContext(), intent);
						}
					});
			return arg1;
		}
	}

	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.zzl.app.Private.MESSAGE_RECEIVED_ACTION";
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
				PrivateMsg message_Obj = (PrivateMsg) intent
						.getSerializableExtra(KEY_MESSAGE);
				boolean b = setMsg(message_Obj);
				if (!b)
					message_Objs.add(message_Obj);
				update(false, 0);
			}

		}
	}

	public boolean setMsg(PrivateMsg msg) {
		if (msg == null || msg.content == null)
			return false;
		for (int i = 0; i < message_Objs.size(); i++) {
			Msg m = message_Objs.get(i);
			if (m != null && m.id != null && m.id.equals(msg.id)) {
				message_Objs.set(i, msg);
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onStart() {
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// // 隐藏软键盘
		// // imm.hideSoftInputFromWindow(contentET.getWindowToken(), 0);
		// // 显示软键盘
		// imm.showSoftInputFromInputMethod(contentET.getWindowToken(), 0);
		// // 切换软键盘的显示与隐藏
		// // imm.toggleSoftInputFromWindow(view.getWindowToken(), 0,
		// // InputMethodManager.HIDE_NOT_ALWAYS);
		// // 或者
		// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		super.onStart();
	}

	private void setMyCollect(PrivateMsg msg) {
		Collect collect = new Collect();
		collect.setHead(msg.head);
		collect.setName(msg.senderName);
		collect.setTime(String.valueOf(new Date().getTime()));
		collect.setPublic_time(msg.time);
		collect.setMtype(msg.mtype);
		collect.setContent(msg.content);
		collect.setImg_big(msg.img_big);
		collect.setImg_small(msg.img_small);
		collect.setVoice(msg.voice);
		collect.setLength(msg.length);
		collect.setAccount(LlkcBody.USER_ACCOUNT);
		db.save(collect);
	}

	private boolean isMyCollect(PrivateMsg msg) {
		List<Collect> collectList = db.findAllByWhere(Collect.class,
				"account='" + LlkcBody.USER_ACCOUNT + "'");
		for (int i = 0; i < collectList.size(); i++) {
			Collect collect = collectList.get(i);
			if (collect.getMtype().equals("1")) {
				if (collect.getContent().equals(msg.content)) {
					return true;
				}
			} else if (collect.getMtype().equals("2")) {
				if (collect.getImg_big().equals(msg.img_big)) {
					return true;
				}
			} else if (collect.getMtype().equals("3")) {
				if (collect.getVoice().equals(msg.voice)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		if (flipper.getVisibility() == View.VISIBLE) {
			flipper.setVisibility(View.GONE);
			return;
		}
		if (friendChatAdapter != null)
			friendChatAdapter.stopPlayer();
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		closeOneAct(TAG);
		super.onBackPressed();
	}

	class InitTalksDataTask extends AsyncTask<Object, Integer, List<Msg>> {
		String uId;
		boolean addMore = false;

		public InitTalksDataTask(String uId, boolean addMore) {
			this.uId = uId;
			this.addMore = addMore;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// if (addMore) {
			// dialog = new ProgressDialog(getContext());
			// dialog.setCancelable(false);
			// dialog.setMessage(Tools.getStringFromRes(
			// getApplicationContext(), R.string.loading));
			// dialog.show();
			// }
		}

		@Override
		protected List<Msg> doInBackground(Object... params) {
			Cursor cursor = null;
			if (!addMore)
				cursor = msgDBOper.query(msgDBOper.getTableName(uId), null,
						BaseMsgTable.Msg_Account + "=?",
						new String[] { LlkcBody.USER_ACCOUNT }, null, null,
						BaseMsgTable.Msg_Time + " desc", "20");
			else {
				int id = message_Objs.size() > 0 ? +message_Objs.get(0)._id
						: -1;
				cursor = msgDBOper.query(msgDBOper.getTableName(uId), null,
						BaseMsgTable.Msg_Account + "=? and " + BaseMsgTable._ID
								+ "<?", new String[] { LlkcBody.USER_ACCOUNT,
								id + "" }, null, null, BaseMsgTable.Msg_Time
								+ " desc", "20");
			}
			List<Msg> list = msgDBOper.getMsgList(cursor);
			if (list != null && list.size() > 0) {
				return list;
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Msg> result) {
			// if (addMore) {
			// setProgressBarIndeterminateVisibility(false);
			// dialog.dismiss();
			// }
			if (result != null) {
				if (!addMore) {
					message_Objs.clear();
					for (int i = result.size() - 1; i >= 0; i--) {
						message_Objs.add((PrivateMsg) result.get(i));
					}
				} else {
					for (int i = 0; i < result.size(); i++) {
						message_Objs.add(0, (PrivateMsg) result.get(i));
					}
				}
				update(addMore, result.size());
			}
			friend_chat_list.onRefreshComplete();
			sendTransMsg();
			super.onPostExecute(result);
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
		if (id.equals("menu")) {
			TextView titleTV = (TextView) dialog
					.findViewById(R.id.dialog_title);
			titleTV.setText(title);
		}
	}

	public PrivateMsg selectedMsg;

	@Override
	public OnClickListener setPositiveClickListener(final AlertDialog dialog,
			int layoutId, String id) {
		if (id.equals("menu")) {
			dialog.findViewById(R.id.parent).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
			if (selectedMsg.mtype != null && selectedMsg.mtype.equals("2")) {
				dialog.findViewById(R.id.dialog_copy).setVisibility(View.GONE);
				dialog.findViewById(R.id.dialog_divider).setVisibility(
						View.GONE);
				// dialog.findViewById(R.id.dialog_transmit).setVisibility(
				// View.GONE);
				// dialog.findViewById(R.id.dialog_divider2).setVisibility(
				// View.GONE);
			} else if (selectedMsg.mtype != null
					&& selectedMsg.mtype.equals("3")) {
				dialog.findViewById(R.id.dialog_copy).setVisibility(View.GONE);
				dialog.findViewById(R.id.dialog_divider).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_transmit).setVisibility(
						View.GONE);
				dialog.findViewById(R.id.dialog_divider2).setVisibility(
						View.GONE);
			}
			dialog.findViewById(R.id.dialog_copy).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (selectedMsg == null)
								return;
							Tools.copy(selectedMsg.content, getContext());
							MyToast.getToast().showToast(getContext(),
									R.string.copyed);
							dialog.dismiss();
						}
					});
			dialog.findViewById(R.id.dialog_transmit).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = getIntent();
							if (intent == null)
								intent = new Intent();
							Bundle b = new Bundle();
							b.putSerializable("msg", selectedMsg);
							b.putInt("type", 2);
							intent.putExtras(b);
							TransmitActivity.launch(getContext(), intent);
							dialog.dismiss();
						}
					});
			dialog.findViewById(R.id.dialog_colllect).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (selectedMsg == null)
								return;
							if (isMyCollect(selectedMsg)) {
								MyToast.getToast().showToast(getContext(),
										"已收藏");
								dialog.dismiss();
								return;
							}
							setMyCollect(selectedMsg);
							MyToast.getToast().showToast(getContext(),
									R.string.collected);
							dialog.dismiss();
						}
					});
			dialog.findViewById(R.id.dialog_delete).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (toObj != null)
								msgDBOper.delete(
										msgDBOper.getTableName(toObj.getUid()),
										"id=?", new String[] { selectedMsg.id });
							message_Objs.remove(selectedMsg);
							friendChatAdapter.notifyDataSetChanged();
							selectedMsg = null;
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

	private void start(String name) {
		mSensor.start(name);
	}

	private void stop() {
		mSensor.stop();
		volume.setImageResource(R.drawable.amp1);
	}

	private void updateDisplay(double signalEMA) {

		switch ((int) signalEMA) {
		case 0:
		case 1:
			volume.setImageResource(R.drawable.amp1);
			break;
		case 2:
		case 3:
			volume.setImageResource(R.drawable.amp2);

			break;
		case 4:
		case 5:
			volume.setImageResource(R.drawable.amp3);
			break;
		case 6:
		case 7:
			volume.setImageResource(R.drawable.amp4);
			break;
		case 8:
		case 9:
			volume.setImageResource(R.drawable.amp5);
			break;
		case 10:
		case 11:
			volume.setImageResource(R.drawable.amp6);
			break;
		default:
			volume.setImageResource(R.drawable.amp7);
			break;
		}
	}

}
