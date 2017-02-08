package com.zrlh.llkc.activity;

import java.io.File;
import java.io.IOException;

import net.tsz.afinal.FinalDb;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.funciton.PersonalDynamic;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.FileTools;
import com.zzl.zl_app.util.ImageUtils;
import com.zzl.zl_app.util.Tools;

public class ReleaseDynamicActivity extends BaseActivity {
	public static final String TAG = "releasedynamic";

	ImageButton back;
	TextView title_card, release;
	EditText contentET;
	ImageView img;
	ImageButton img_but;
	Bitmap bm;
	FinalDb db;

	ProgressDialog dialog = null;

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, ReleaseDynamicActivity.class);
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
		setContentView(R.layout.releasedynamic);
		imageUri = Uri.fromFile(new File(IMAGE_FILE_LOCATION));
		db = FinalDb.create(getContext());
		init();
	}

	private void init() {
		title_card = (TextView) findViewById(R.id.title_tv);
		title_card.setText("发布动态");
		back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = getIntent();
				Bundle b = new Bundle();
				b.putBoolean("result", false);
				intent.putExtras(b);
				getContext().setResult(RESULT_OK, intent);
				onBackPressed();
			}
		});
		// 发布
		release = (TextView) findViewById(R.id.btn);
		release.setText("发布");
		release.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String content = contentET.getEditableText().toString().trim();
				if (content.equals("") && bm == null) {
					MyToast.getToast().showToast(getContext(),
							"您还是先说些什么或发张图片吧！");
					return;
				}
				new RealeaseDynamicTask(content, bm).execute();
			}
		});
		contentET = (EditText) findViewById(R.id.releasegynamic_ed_content);
		img = (ImageView) findViewById(R.id.releasegynamic_img);
		img_but = (ImageButton) findViewById(R.id.releasegynamic_but_img);
		img_but.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPicDialog();
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
			 */
			if (imageUri != null) {
				Bitmap bitmap = ImageUtils.compressBimap(IMAGE_FILE_LOCATION);
				if (bitmap == null) {
					return;
				}
				int degree = ImageUtils.readPictureDegree(IMAGE_FILE_LOCATION);
				if (degree != 0)
					bitmap = ImageUtils.rotaingImageView(degree, bitmap);
				img.postInvalidate();
				img.setImageBitmap(bitmap);
				bm = bitmap;
			}
			break;
		case PIC_ALBUM:// from crop_big_picture
			if (resultCode != RESULT_OK) {
				return;
			}
			Uri originalUri = data.getData(); // 获得图片的uri
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(originalUri, proj, null, null, null);
			String path = "";
			if (cursor != null) {
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				path = cursor.getString(column_index);
			} else {
				path = originalUri.getPath();
			}
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
				if (newBm != null) {
					img.setImageBitmap(newBm);
					bm = newBm;
				}
			}
			break;
		}
	}

	class RealeaseDynamicTask extends AsyncTask<Void, Void, PersonalDynamic> {
		Bitmap bm;
		String content;

		private RealeaseDynamicTask(String content, Bitmap bm) {
			this.bm = bm;
			this.content = content;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getApplicationContext(),
					R.string.load_ing));
			dialog.show();
		}

		@Override
		protected PersonalDynamic doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext()).releaseDynamic(
						getContext(), content, bm);
			} catch (Exception e) {
				
			}
			return null;
		}

		@Override
		protected void onPostExecute(PersonalDynamic result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			boolean flag = false;
			if (result != null) {
				flag = true;
				LlkcBody.DYNAMIC = result;
				// 保存最新发表的个人动态
				db.deleteByWhere(PersonalDynamic.class, "account='"
						+ LlkcBody.USER_ACCOUNT + "'");
				result.setAccount(LlkcBody.USER_ACCOUNT);
				db.save(result);
				if (!result.bigImg.equals(""))
					ImageCache.getInstance().setImgKey(result.bigImg, bm);
			}
			Intent intent = getIntent();
			Bundle b = new Bundle();
			b.putBoolean("result", flag);
			b.putSerializable("dynamic", result);
			intent.putExtras(b);
			getContext().setResult(RESULT_OK, intent);
			closeOneAct(TAG);
			super.onPostExecute(result);
		}

	}

	@Override
	public BaseActivity getContext() {
		// TODO Auto-generated method stub
		return this;
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

}
