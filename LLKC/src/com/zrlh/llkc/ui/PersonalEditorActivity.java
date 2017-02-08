package com.zrlh.llkc.ui;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalDb;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.zrlh.llkc.corporate.base.BaseActivity;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.activity.MainActivity;
import com.zrlh.llkc.activity.PreviewPicActivity;
import com.zrlh.llkc.corporate.CityLevel1Activity;
import com.zrlh.llkc.corporate.Obj;
import com.zrlh.llkc.corporate.PlatformAPI;
import com.zrlh.llkc.funciton.HeadWall;
import com.zrlh.llkc.funciton.Http_Utility;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.joggle.Account;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.FileTools;
import com.zzl.zl_app.util.ImageUtils;
import com.zzl.zl_app.util.Tools;

@SuppressLint("NewApi")
public class PersonalEditorActivity extends BaseActivity {

	public static final String TAG = "personaleditor";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, PersonalEditorActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	TextView titleTextView;
	RelativeLayout per_rel_name, per_rel_date, per_rel_sex, per_rel_image,
			per_rel_sign, per_rel_addrs, per_rel_tel;
	TextView per_text_name, per_text_date, per_text_sex, per_text_sign,
			per_text_addres, per_text_tel;
	EditText per_text_email;

	GridView per_gridview;
	PerHeadAdapter adapter;
	List<HeadWall> headWall;
	String headId;
	Calendar mCalendar;
	String Msg, Stat;
	String imagemsg;
	ProgressDialog dialog = null;

	FinalDb db = FinalDb.create(this, true);
	Account account;
	private String[] items;
	/* 请求码 */
	private static final int PIC_TAKE_PHONE = 1;// 拍照
	private static final int PIC_ALBUM = 2;// 选取本地文件
	private static final int RESULT_ADDRES_CODE = 3;

	private Uri imageUri;
	private static final String IMAGE_FILE_NAME = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/inter.jpg";
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				break;
			case 2:

				Toast.makeText(
						getContext(),
						com.zzl.zl_app.util.Tools.getStringFromRes(
								getContext(), R.string.net_error3),
						Toast.LENGTH_SHORT).show();

				break;
			case 3:
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
				Toast.makeText(getContext(), imagemsg, Toast.LENGTH_SHORT)
						.show();

			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personaledit);
		items = new String[] {
				com.zzl.zl_app.util.Tools.getStringFromRes(getContext(),
						R.string.select_img),
				com.zzl.zl_app.util.Tools.getStringFromRes(getContext(),
						R.string.photo) };
		imageUri = Uri.fromFile(new File(IMAGE_FILE_NAME));
		init();
		initDay();
	}

	void init() {

		titleTextView = (TextView) findViewById(R.id.title_tv);
		per_rel_name = (RelativeLayout) findViewById(R.id.per_rel_name);
		per_text_name = (TextView) findViewById(R.id.per_text_name);
		per_rel_date = (RelativeLayout) findViewById(R.id.per_rel_date);
		per_text_date = (TextView) findViewById(R.id.per_text_date);
		per_rel_sex = (RelativeLayout) findViewById(R.id.per_rel_sex);
		per_rel_sign = (RelativeLayout) findViewById(R.id.per_rel_sign);
		per_rel_addrs = (RelativeLayout) findViewById(R.id.per_rel_addrs);
		per_text_sex = (TextView) findViewById(R.id.per_text_sex);
		per_rel_image = (RelativeLayout) findViewById(R.id.per_rel_image);
		per_text_addres = (TextView) findViewById(R.id.per_text_addres);
		per_text_sign = (TextView) findViewById(R.id.per_text_sign);
		per_gridview = (GridView) findViewById(R.id.per_rel_gridview);
		per_rel_tel = (RelativeLayout) findViewById(R.id.per_rel_tel);
		per_text_tel = (TextView) findViewById(R.id.per_text_tel);
		per_text_email = (EditText) findViewById(R.id.per_text_email);
		per_rel_tel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				show_per_rel_tel();
			}
		});

		titleTextView.setText(com.zzl.zl_app.util.Tools.getStringFromRes(
				getContext(), R.string.myedit));

		account = LlkcBody.ACCOUNT;
		if (account != null) {
			// if (Tools.isUrl(account.getHead()))
			// finalBitmap.display(per_image, account.getHead());
			// else
			// per_image.setImageResource(R.drawable.head_default);
			headWall = account.getHeadWall();
			adapter = new PerHeadAdapter(getContext());
			per_gridview.setAdapter(adapter);

			per_text_sign.setText(account.getSign());
			per_text_name.setText(account.getUname());
			per_text_sex
					.setText("0".equals(account.getSex()) ? com.zzl.zl_app.util.Tools
							.getStringFromRes(getContext(), R.string.woman)
							: com.zzl.zl_app.util.Tools.getStringFromRes(
									getContext(), R.string.man));
			per_text_date.setText(account.getBirth());
			per_text_addres.setText(account.getLocal());
			per_text_tel.setText(account.getPhone());
			per_text_email.setText(account.getEmail());
		}

		findViewById(R.id.btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new ModifyUserTask().execute();

			}
		});
		per_rel_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// showDialog();
			}
		});
		per_rel_addrs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PersonalEditorActivity.this,
						CityLevel1Activity.class);
				startActivityForResult(intent, RESULT_ADDRES_CODE);
			}
		});
		per_rel_sex.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				show_per_rel_sex();
			}
		});
		per_rel_name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				show_per_rel_name();
			}
		});
		per_rel_date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showSelectDateDiag();
			}
		});
		per_rel_sign.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				show_per_rel_sign();
			}
		});
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
	}

	void show_per_rel_sign() {

		final EditText editText = new EditText(this);
		editText.setText(account.getSign());
		new AlertDialog.Builder(this)
				.setTitle(
						com.zzl.zl_app.util.Tools.getStringFromRes(
								getContext(), R.string.sign_name))
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(editText)
				.setPositiveButton(
						com.zzl.zl_app.util.Tools.getStringFromRes(
								getContext(), R.string.set_date_btn_sure),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								String aString = editText.getText().toString()
										.trim();
								if ("".equals(aString)) {
									Toast.makeText(
											getContext(),
											com.zzl.zl_app.util.Tools
													.getStringFromRes(
															getContext(),
															R.string.sign_name_nonull),
											Toast.LENGTH_SHORT).show();
								} else {
									account.setSign(aString);
									per_text_sign.setText(aString);
								}
							}
						})
				.setNegativeButton(
						com.zzl.zl_app.util.Tools.getStringFromRes(
								getContext(), R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).show();

	}

	void show_per_rel_tel() {

		final EditText editText = new EditText(this);
		editText.setText(account.getPhone());
		new AlertDialog.Builder(this)
				.setTitle(
						com.zzl.zl_app.util.Tools.getStringFromRes(
								getContext(), R.string.input_phone))
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(editText)
				.setPositiveButton(
						com.zzl.zl_app.util.Tools.getStringFromRes(
								getContext(), R.string.set_date_btn_sure),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								String aString = editText.getText().toString()
										.trim();
								if ("".equals(aString)) {
									Toast.makeText(
											getContext(),
											com.zzl.zl_app.util.Tools
													.getStringFromRes(
															getContext(),
															R.string.phone_nonull),
											Toast.LENGTH_SHORT).show();
								} else {
									per_text_tel.setText(aString);
									account.setPhone(aString);
								}
							}
						})
				.setNegativeButton(
						com.zzl.zl_app.util.Tools.getStringFromRes(
								getContext(), R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).show();

	}

	void show_per_rel_name() {
		final EditText editText = new EditText(this);
		editText.setText(account.getUname());
		new AlertDialog.Builder(this)
				.setTitle(
						com.zzl.zl_app.util.Tools.getStringFromRes(
								getContext(), R.string.input_nickname))
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(editText)
				.setPositiveButton(
						com.zzl.zl_app.util.Tools.getStringFromRes(
								getContext(), R.string.set_date_btn_sure),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								String aString = editText.getText().toString()
										.trim();
								if ("".equals(aString)) {
									Toast.makeText(
											getContext(),
											com.zzl.zl_app.util.Tools
													.getStringFromRes(
															getContext(),
															R.string.nickname_nonull),
											Toast.LENGTH_SHORT).show();
								} else {
									per_text_name.setText(aString);
									account.setNname(aString);
								}
							}
						})
				.setNegativeButton(
						com.zzl.zl_app.util.Tools.getStringFromRes(
								getContext(), R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).show();

	}

	private void initDay() {
		mCalendar = Calendar.getInstance();// 这是一个单例模式不能用new，只有调用内部的
		// getInstance()获得实例
		m_year = mCalendar.get(Calendar.YEAR);// 获得当前的年
		m_month = mCalendar.get(Calendar.MONTH) + 1;// 获得当前的月
		m_day = mCalendar.get(Calendar.DAY_OF_MONTH);// 获得当月的天数
	}

	mOnDateChangedListener dateChangeListener;

	/**
	 * 时间改变，或者日期改变的事件
	 */
	private void dateTimechanged() {
		if (dateChangeListener == null)
			dateChangeListener = new mOnDateChangedListener();
		mdatePicker.init(m_year, m_month - 1, m_day, dateChangeListener);
	}

	private class mOnDateChangedListener implements OnDateChangedListener {

		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			m_year = year;
			m_month = monthOfYear + 1;
			m_day = dayOfMonth;
		}
	}

	DatePicker mdatePicker;
	int m_year = 1990, m_month = 1, m_day = 1;

	private void showSelectDateDiag() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.set_date, null);
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.show();
		dialog.getWindow().setContentView(layout);
		mdatePicker = (DatePicker) dialog
				.findViewById(R.id.set_date_mDatePicker);
		Date d = new Date();
		if (MainActivity.androidVersion > 10)
			mdatePicker.setMaxDate(d.getTime());
		if (account.getBirth() != null && !account.getBirth().equals("")) {
			String[] bs = account.getBirth().split("-");
			try {
				if (bs.length > 0)
					m_year = Integer.parseInt(bs[0]);
				else
					m_year = 1990;
				if (bs.length > 1)
					m_month = Integer.parseInt(bs[1]);
				else
					m_month = 1;
				if (bs.length > 2)
					m_day = Integer.parseInt(bs[2]);
				else
					m_day = 1;
			} catch (NumberFormatException e) {
				// TODO: handle exception
			}
		}
		dateTimechanged();
		dialog.findViewById(R.id.set_date_btn_sure).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						per_text_date.setText(m_year + "-" + m_month + "-"
								+ m_day);
						account.setBirth(per_text_date.getText().toString()
								.trim());
						dialog.dismiss();
					}
				});
	}

	void show_per_rel_sex() {

		final String[] arrayFruit = new String[] {
				com.zzl.zl_app.util.Tools.getStringFromRes(getContext(),
						R.string.man),
				com.zzl.zl_app.util.Tools.getStringFromRes(getContext(),
						R.string.woman) };

		Dialog dialog = new AlertDialog.Builder(this)
				.setTitle(
						com.zzl.zl_app.util.Tools.getStringFromRes(
								getContext(), R.string.select_sex))
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(arrayFruit, 0,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								per_text_sex.setText(arrayFruit[which]);
								if (com.zzl.zl_app.util.Tools.getStringFromRes(
										getContext(), R.string.man).equals(
										arrayFruit[which])) {
									account.setSex("1");
								} else {
									account.setSex("0");
								}
								dialog.dismiss();
							}
						}).create();
		dialog.show();

	}

	private void showDialog() {

		new AlertDialog.Builder(this)
				.setTitle(
						com.zzl.zl_app.util.Tools.getStringFromRes(
								getContext(), R.string.set_pic))
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:// 本地文件
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery, PIC_ALBUM);
							break;
						case 1:// 拍照

							try {
								FileTools.deleteFile(IMAGE_FILE_NAME);
							} catch (IOException e) {

							}
							if (imageUri != null) {
								// capture a bitmap and store it in Uri
								Intent intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								// if (LocalMemory.getInstance().checkSDCard())
								intent.putExtra(MediaStore.EXTRA_OUTPUT,
										imageUri);
								startActivityForResult(intent, PIC_TAKE_PHONE);
							}
							break;
						}
					}
				})
				.setNegativeButton(
						com.zzl.zl_app.util.Tools.getStringFromRes(
								getContext(), R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case PIC_TAKE_PHONE:
				if (resultCode != RESULT_OK) {
					return;
				}
				if (imageUri != null) {
					Bitmap bitmap = ImageUtils.compressBimap(IMAGE_FILE_NAME);
					if (bitmap == null) {
						return;
					}
					int degree = ImageUtils.readPictureDegree(IMAGE_FILE_NAME);
					if (degree != 0)
						bitmap = ImageUtils.rotaingImageView(degree, bitmap);

					Bitmap newBm = ImageUtils.zoomBitmap(bitmap,
							(int) (480 * MainActivity.WIDTH_RATIO),
							(int) (480 * MainActivity.WIDTH_RATIO));
					if (!bitmap.isRecycled())
						bitmap.recycle();
					if (newBm != null)
						// new AddHeadImageTask(newBm).execute();
						setImageToView(newBm);
				}
				break;
			case PIC_ALBUM:
				if (resultCode != RESULT_OK) {
					return;
				}
				Uri originalUri = data.getData(); // 获得图片的uri
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(originalUri, proj, null, null,
						null);
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
							(int) (480 * MainActivity.WIDTH_RATIO),
							(int) (480 * MainActivity.WIDTH_RATIO));
					if (!bitmap.isRecycled())
						bitmap.recycle();
					if (newBm != null)
						// new AddHeadImageTask(newBm).execute();
						setImageToView(newBm);
				}
				break;
			case RESULT_ADDRES_CODE:
				if (data != null) {
					Bundle b = data.getExtras();
					if (b != null) {
						Obj key = (Obj) b.getSerializable("key");
						Obj city = (Obj) b.getSerializable("obj");
						if (city != null) {
							per_text_addres.setText(key.name + "-" + city.name);
							account.setLocal(key.name + "-" + city.name);
						}
					}
					break;
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 480);
		intent.putExtra("outputY", 480);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setImageToView(Bitmap photo) {
		if (photo != null) {
			new AddHeadImageTask(photo).execute();
		}
	}

	class ModifyUserTask extends AsyncTask<Object, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.loading));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext()).modifyUser(account);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result == null)
				handler.sendEmptyMessage(2);
			else {
				if (result) {
					db.update(account);
					closeOneAct(TAG);
				}
			}
		}

	}

	class HeadWallTask extends AsyncTask<Object, Integer, String> {
		String type;// 1.设为默认 2.删除 3.添加
		String headId;

		public HeadWallTask(String type, String headId) {
			this.type = type;
			this.headId = headId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.opping));
			dialog.show();

		}

		@Override
		protected String doInBackground(Object... params) {

			try {
				return Community.getInstance(getContext()).headWall(type,
						headId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null) {
				if (type.equals("1")) {
					account.setHead(result);
				} else if (type.equals("2")) {// 删除
					headWall.remove(Integer.parseInt(headId) - 1);
					account.setHead(result);
					account.setHeadWall(headWall);
					if (adapter != null) {
						adapter.notifyDataSetChanged();
					}
				}
			}
			super.onPostExecute(result);
		}
	}

	class AddHeadImageTask extends AsyncTask<Object, Integer, Boolean> {
		Bitmap bitmap;

		public AddHeadImageTask(Bitmap bitmap) {
			this.bitmap = bitmap;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.opping));
			dialog.show();

		}

		@Override
		protected Boolean doInBackground(Object... params) {

			try {
				return headImageRequestMessage(bitmap);
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
			if (result)
				handler.sendEmptyMessage(3);
			super.onPostExecute(result);
		}
	}

	// Bitmap bm
	Boolean headImageRequestMessage(Bitmap bm) throws Exception {

		String resp = Http_Utility.openUrl(this,
				PlatformAPI.BaseUrl + "User_Head_Req.jsp?" + "Orgid="
						+ LlkcBody.APP_Orgid + "&Name=" + account.getArccount()
						+ "&Pwd=" + account.getPrassWord() + "&Imgext=jpg",
				"POST", bm);
		if (resp != null) {
			JSONObject obj = getJSONObjectFromString(resp);
			if (obj != null) {
				String state = obj.getString("Stat");
				imagemsg = obj.getString("Msg");
				if (state.equals("0")) {
					String Url = obj.getString("Url");
					account.setHead(Url);
					if (headWall != null)
						headWall.clear();
					headWall = Account.getHeadWallList(obj
							.getJSONArray("HeadJson"));
					account.setHeadWall(headWall);
					return true;
				}
			}
		}
		return false;
	}

	private JSONObject getJSONObjectFromString(String resp)
			throws JSONException {
		resp = resp.trim();
		if (!resp.substring(0, 1).equals("{")) {
			return null;
		}
		JSONTokener jsonParser = new JSONTokener(resp);
		JSONObject obj = (JSONObject) jsonParser.nextValue();
		if (!obj.isNull("Stat")) {
			String stat = obj.getString("Stat");
			if (stat.equals("-99")) {
				Message msg = handler.obtainMessage();
				msg.obj = com.zzl.zl_app.util.Tools.getStringFromRes(
						getContext(), R.string.connect_timeout);
				handler.sendMessage(msg);
			}
		}
		return obj;
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

	class PerHeadAdapter extends BaseAdapter {

		LayoutInflater layoutInflater;
		PerHeadView perHeadView;
		FinalBitmap finalBitmap;
		HeadWall headW;

		public final class PerHeadView {
			ImageView head;
		}

		private PerHeadAdapter(Context context) {
			layoutInflater = LayoutInflater.from(context);
			finalBitmap = FinalBitmap.create(context);
			finalBitmap.configLoadingImage(R.drawable.head_default);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			int len = headWall == null ? 0 : headWall.size();
			if (len < 8) {
				len++;
			}
			return len;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return headWall.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				perHeadView = new PerHeadView();
				convertView = layoutInflater.inflate(
						R.layout.per_gridview_item, null);
				perHeadView.head = (ImageView) convertView
						.findViewById(R.id.per_gridview_item_photo);
				convertView.setTag(perHeadView);
			} else
				perHeadView = (PerHeadView) convertView.getTag();
			if (headWall != null && position < headWall.size()) {
				headW = headWall.get(position);
				String headstr = headW.getHeadSimg();
				if (Tools.isUrl(headstr))
					finalBitmap.display(perHeadView.head, headstr);
				else
					perHeadView.head.setImageResource(R.drawable.head_default);
			} else {
				perHeadView.head.setImageResource(R.drawable.touxiang_jiahao);
			}
			perHeadView.head.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (headWall != null && position < headWall.size()) {
						headW = headWall.get(position);
						Intent intent = new Intent();
						Bundle b = new Bundle();
						b.putString("picUrl", headW.getHeadBimg());
						intent.putExtras(b);
						PreviewPicActivity.launch(getContext(), intent);
					} else {
						showDialog();
					}
				}
			});
			perHeadView.head.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					if (headWall != null && position < headWall.size()) {
						headId = String.valueOf(position + 1);
						showMsgDialog("menu", R.layout.dialog_menu,
								getContext(), "提示", null);
					}
					return true;
				}
			});
			return convertView;
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
			dialog.findViewById(R.id.dialog_copy).setVisibility(View.GONE);
			dialog.findViewById(R.id.dialog_divider).setVisibility(View.GONE);
			dialog.findViewById(R.id.dialog_transmit).setVisibility(View.GONE);
			dialog.findViewById(R.id.dialog_divider2).setVisibility(View.GONE);
			TextView headTV = (TextView) dialog
					.findViewById(R.id.dialog_colllect);
			headTV.setText("设为头像");
			headTV.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new HeadWallTask("1", headId).execute();
					dialog.dismiss();
				}
			});
			dialog.findViewById(R.id.dialog_delete).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							new HeadWallTask("2", headId).execute();
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
