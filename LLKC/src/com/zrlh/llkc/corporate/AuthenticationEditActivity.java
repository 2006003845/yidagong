package com.zrlh.llkc.corporate;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;

import android.app.AlertDialog;
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
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.ui.LLKCApplication;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.FileTools;
import com.zzl.zl_app.util.ImageUtils;
import com.zzl.zl_app.util.Tools;

/*
 * 证明
 * */
public class AuthenticationEditActivity extends BaseActivity {
	public static final String Tag = "auth_edit";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, AuthenticationEditActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(Tag, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.authentication_edit);

		initView();
	}

	private TextView titleTV;

	private EditText nameET, phoneET, addrET, introET;
	private TextView leftTV;

	private ImageView licenseImgV;
	ProgressDialog dialog = null;

	private void initView() {
		imageUri = Uri.fromFile(new File(IMAGE_FILE_LOCATION));
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText(R.string.fillin_auth_info);
		this.findViewById(R.id.btn).setVisibility(View.GONE);
		nameET = (EditText) this.findViewById(R.id.auth_edit_et_name);
		phoneET = (EditText) this.findViewById(R.id.auth_edit_et_phone);
		addrET = (EditText) this.findViewById(R.id.auth_edit_et_addr);
		introET = (EditText) this.findViewById(R.id.auth_edit_et_intro);
		leftTV = (TextView) this.findViewById(R.id.auth_edit_tv_left);
		licenseImgV = (ImageView) this
				.findViewById(R.id.auth_edit_imgv_license);
		this.findViewById(R.id.auth_edit_btn_commit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						// TODO
						String name = nameET.getText().toString();
						String phone = phoneET.getText().toString();
						String addr = addrET.getText().toString();
						String intro = introET.getText().toString();
						if (name == null || name.equals("")) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(
											getApplicationContext(),
											R.string.input_unitname));
							return;
						}
						if (phone == null || phone.equals("")) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(
											getApplicationContext(),
											R.string.input_unitphone));
							return;
						}
						if (addr == null || addr.equals("")) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(
											getApplicationContext(),
											R.string.input_unitaddress));
							return;
						}
						if (addr.length() > 39) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(
											getApplicationContext(),
											R.string.unitaddress_long));
							return;
						}
						if (intro == null || intro.equals("")) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(
											getApplicationContext(),
											R.string.input_unitcon));
							return;
						}

						if (lisence == null) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(
											getApplicationContext(),
											R.string.upload_business));
							return;
						}

						new CommitAuthTask(name, phone, addr, intro).execute();
					}
				});
		this.findViewById(R.id.auth_edit_layout_license).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO
						showPicDialog();
					}
				});
		introET.addTextChangedListener(new TextWatcher() {
			String text = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				int size = introET.getText().length();
				if (size == 150) {
					text = introET.getText().toString().trim();
				}
				if (size > 150) {
					introET.setText(text);
				}
				Spanned text1 = Html.fromHtml(" <font color='#b92d2d'>"
						+ (150 - size) + "</font>" + "/150");
				leftTV.setText(text1);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

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
			.getExternalStorageDirectory() + "/auth_temp.jpg";
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
		if (data != null)
			b = data.getExtras();
		switch (requestCode) {

		case PIC_TAKE_PHONE:
			if (resultCode != RESULT_OK) {
				return;
			}
			if (imageUri != null) {
				Bitmap bitmap = ImageUtils.compressBimap(IMAGE_FILE_LOCATION);
				if (bitmap == null) {
					return;
				}
				int degree = ImageUtils.readPictureDegree(IMAGE_FILE_LOCATION);
				if (degree != 0)
					bitmap = ImageUtils.rotaingImageView(degree, bitmap);
				new CommitLisenceTask(bitmap).execute();
				licenseImgV.setVisibility(View.VISIBLE);
				licenseImgV.setImageBitmap(bitmap);

			}
			break;
		case PIC_ALBUM:// from crop_big_picture
			if (resultCode != RESULT_OK) {
				return;
			}
			if (data == null)
				return;
			Uri originalUri = data.getData(); // 获得图片的uri
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(originalUri, proj, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			String path = cursor.getString(column_index);
			Bitmap bitmap = ImageUtils.compressBimap(path);
			if (bitmap != null) {
				int degree = ImageUtils.readPictureDegree(path);
				if (degree != 0)
					bitmap = ImageUtils.rotaingImageView(degree, bitmap);
				Bitmap newBm = ImageUtils.zoomBitmap(bitmap, 450, 780);
				if (!bitmap.isRecycled())
					bitmap.recycle();
				if (newBm != null) {
					new CommitLisenceTask(newBm).execute();
					licenseImgV.setVisibility(View.VISIBLE);
					licenseImgV.setImageBitmap(newBm);
				}
			}
			break;
		}
	}

	private String lisence = null;

	class CommitLisenceTask extends AsyncTask<Object, Integer, String> {

		Bitmap bm;

		public CommitLisenceTask(Bitmap bm) {
			this.bm = bm;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.upload_now));
			dialog.show();
		}

		@Override
		protected String doInBackground(Object... params) {
			// TODO
			try {
				return Community.getInstance(getContext()).companyImgOp(
						getContext(), LlkcBody.USER_ACCOUNT,
						LlkcBody.PASS_ACCOUNT, "2", bm);
			} catch (Exception e) {
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
				lisence = result;
				MyToast.getToast().showToast(
						getContext(),
						Tools.getStringFromRes(getApplicationContext(),
								R.string.upload_success));
			} else {
				MyToast.getToast().showToast(
						getContext(),
						Tools.getStringFromRes(getApplicationContext(),
								R.string.upload_fail));
			}
			super.onPostExecute(result);
		}
	}

	class CommitAuthTask extends AsyncTask<Object, Integer, Boolean> {

		String name;
		String phone;
		String addr;
		String intro;

		public CommitAuthTask(String name, String phone, String addr,
				String intro) {
			super();
			this.name = name;
			this.phone = phone;
			this.addr = addr;
			this.intro = intro;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getContext(),
					R.string.submit));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			try {
				return Community.getInstance(getContext()).companyIdent(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT, name,
						phone, addr, intro);
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
			if (result) {
				AuthenticationResultActivity.launch(getContext(), getIntent());
				LLKCApplication.getInstance().setAuthStat(
						LlkcBody.State_Auth_Ing);
				closeOneAct(Tag);
			} else {
				MyToast.getToast().showToast(
						getContext(),
						Tools.getStringFromRes(getApplicationContext(),
								R.string.submit_fail));
			}

			super.onPostExecute(result);
		}
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

	@Override
	public BaseActivity getContext() {
		return this;
	}

	@Override
	public void setDialogContent(AlertDialog dialog, int layoutId, String msg,
			String id) {
	}

	@Override
	public void setDialogTitle(AlertDialog dialog, int layoutId, String title,
			String id) {
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
