package com.zrlh.llkc.corporate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import net.tsz.afinal.FinalBitmap;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.activity.MainActivity;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.cache.LocalMemory;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.FileTools;
import com.zzl.zl_app.util.ImageUtils;
import com.zzl.zl_app.util.Tools;

@SuppressLint("NewApi")
public class MyResumeActivity extends BaseActivity {
	FinalBitmap finalBitmap;

	public static final String Tag = "myresume";

	private Resume resume;

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, MyResumeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	private boolean createResume = true;

	ProgressDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(Tag, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.myresume);
		finalBitmap = FinalBitmap.create(this);
		finalBitmap.configLoadingImage(R.drawable.head_default);

		if (ApplicationData.resumeList.size() == 0) {
			createResume = true;
			resume = new Resume();
		} else {
			createResume = false;
			resume = ApplicationData.resumeList.get(0);
		}
		initDate();
		initView();
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
		initViewData();
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

	private ArrayList<Obj> educationList = new ArrayList<Obj>();
	private ArrayList<Obj> genderList = new ArrayList<Obj>();
	private ArrayList<Obj> experienceList = new ArrayList<Obj>();
	private ArrayList<Obj> salaryList = new ArrayList<Obj>();

	private void initDate() {
		// 学历
		educationList.clear();
		educationList.add(new Education(Tools.getStringFromRes(getContext(),
				R.string.education1)));
		educationList.add(new Education(Tools.getStringFromRes(getContext(),
				R.string.education2)));
		educationList.add(new Education(Tools.getStringFromRes(getContext(),
				R.string.education3)));
		educationList.add(new Education(Tools.getStringFromRes(getContext(),
				R.string.education4)));
		educationList.add(new Education(Tools.getStringFromRes(getContext(),
				R.string.education5)));
		educationList.add(new Education(Tools.getStringFromRes(getContext(),
				R.string.education6)));
		// 性别\
		genderList.clear();
		genderList.add(new Gender("1", Tools.getStringFromRes(getContext(),
				R.string.man)));
		genderList.add(new Gender("2", Tools.getStringFromRes(getContext(),
				R.string.woman)));

		// 工作经验
		experienceList.clear();
		experienceList.add(new Obj(Tools.getStringFromRes(getContext(),
				R.string.experience1)));
		experienceList.add(new Obj(Tools.getStringFromRes(getContext(),
				R.string.experience2)));
		experienceList.add(new Obj(Tools.getStringFromRes(getContext(),
				R.string.experience3)));
		experienceList.add(new Obj(Tools.getStringFromRes(getContext(),
				R.string.experience4)));
		experienceList.add(new Obj(Tools.getStringFromRes(getContext(),
				R.string.experience5)));

		// 工资
		salaryList.clear();
		salaryList.add(new Salary("1", Tools.getStringFromRes(getContext(),
				R.string.salary1)));
		salaryList.add(new Salary("2", Tools.getStringFromRes(getContext(),
				R.string.salary2)));
		salaryList.add(new Salary("3", Tools.getStringFromRes(getContext(),
				R.string.salary3)));
		salaryList.add(new Salary("4", Tools.getStringFromRes(getContext(),
				R.string.salary4)));
		salaryList.add(new Salary("5", Tools.getStringFromRes(getContext(),
				R.string.unlimit)));

	}

	private TextView titleTV, sexTV, birthTV, educationTV, workExperienceTV,
			expectPostTV, expectSalaryTV;
	private EditText nameTV, telNumTV, selfassessmentTV;
	private ImageView photoImgV;
	private RelativeLayout photoLayout;
	private TextView sureBtn;

	public void initViewData() {
		if (nameTV == null)
			return;
		if (resume == null)
			return;
		if (resume.rName == null || resume.rName.equals("")) {
			nameTV.setHint(R.string.text_num_limited_10);
		} else {
			nameTV.setText(resume.rName);
		}
		// nameTV.setText(resume.rName == null || resume.rName.equals("") ?
		// Tools
		// .getStringFromRes(getContext(), R.string.text_num_limited_10)
		// : resume.rName);
		sexTV.setText(resume.sex != null && !resume.sex.equals("") ? (resume.sex
				.equals("1") ? Tools.getStringFromRes(getContext(),
				R.string.man) : Tools.getStringFromRes(getContext(),
				R.string.woman)) : Tools.getStringFromRes(getContext(),
				R.string.hinit_select));
		birthTV.setText(resume.birth == null || resume.birth.equals("") ? Tools
				.getStringFromRes(getContext(), R.string.hinit_select)
				: resume.birth);
		educationTV.setText(resume.education == null
				|| resume.education.equals("") ? Tools.getStringFromRes(
				getContext(), R.string.hinit_select) : resume.education);
		workExperienceTV.setText(resume.workExperience == null
				|| resume.workExperience.equals("") ? Tools.getStringFromRes(
				getContext(), R.string.hinit_select) : resume.workExperience);
		if (resume.phone == null || resume.phone.equals("")) {
			telNumTV.setHint(R.string.hinit_input);
		} else {
			telNumTV.setText(resume.phone);
		}
		// telNumTV.setText(resume.phone == null || resume.phone.equals("") ?
		// Tools
		// .getStringFromRes(getContext(), R.string.hinit_input)
		// : resume.phone);
		// expectSalaryTV.setText(resume.expectSalary == null ? ""
		// : resume.expectSalary);
		expectPostTV.setText(resume.expectPost == null
				|| resume.expectPost.equals("") ? Tools.getStringFromRes(
				getContext(), R.string.hinit_select) : resume.expectPost);
		expectSalaryTV.setText(resume.expectSalary == null
				|| resume.expectSalary.equals("") ? Tools.getStringFromRes(
				getContext(), R.string.hinit_select) : resume.expectSalary);
		if (resume.evaluation == null || resume.evaluation.equals("")) {
			selfassessmentTV.setHint(R.string.fullselfinfor);
		} else {
			selfassessmentTV.setText(resume.evaluation);
		}
		// selfassessmentTV.setText(resume.evaluation == null
		// || resume.evaluation.equals("") ? Tools.getStringFromRes(
		// getContext(), R.string.hinit_input) : resume.evaluation);
		if (Tools.isUrl(resume.head))
			finalBitmap.display(photoImgV, resume.head);
		else
			photoImgV.setImageResource(R.drawable.img_phone);
	}

	public void initView() {
		imageUri = Uri.fromFile(new File(IMAGE_FILE_LOCATION));
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText(R.string.myresume);
		if (createResume)
			titleTV.setText(R.string.create_resume);
		else
			titleTV.setText(R.string.modify_resume);
		photoLayout = (RelativeLayout) this
				.findViewById(R.id.myresume_rel_head);
		photoImgV = (ImageView) this.findViewById(R.id.myresume_imgv_photo);
		photoLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPicDialog();
			}
		});
		nameTV = (EditText) this.findViewById(R.id.myresume_tv_name);
		nameTV.addTextChangedListener(new TextWatcher() {
			String text = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				int size = nameTV.getText().length();
				if (size <= 10) {
					text = nameTV.getText().toString().trim();
				}
				if (size > 10) {
					nameTV.setText(text);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		// nameTV.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(getContext(), InputActivity.class);
		// Bundle bundle = new Bundle();
		// bundle.putInt("limite", 10);
		// bundle.putString("cont", resume.rName);
		// intent.putExtras(bundle);
		// startActivityForResult(intent, 101);
		// }
		// });
		sexTV = (TextView) this.findViewById(R.id.myresume_tv_sex);
		sexTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PullDownList pullDownList = new PullDownList(getContext(),
						getContext().findViewById(R.id.myresume_rel_sex),
						sexTV, genderList);
				pullDownList
						.setPullOnItemClickListener(new PullOnItemClickListener() {

							@Override
							public void onPullItemClick(AdapterView<?> parent,
									int position) {
								resume.sex = genderList.get(position).id;
								sexTV.setText(genderList.get(position).name);
							}
						});
				pullDownList.popupWindwShowing();
			}
		});
		birthTV = (TextView) this.findViewById(R.id.myresume_tv_birth);
		birthTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSelectDateDiag();
			}
		});
		educationTV = (TextView) this.findViewById(R.id.myresume_tv_education);
		educationTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PullDownList pullDownList = new PullDownList(getContext(),
						getContext().findViewById(R.id.myresume_rel_education),
						educationTV, educationList);
				pullDownList
						.setPullOnItemClickListener(new PullOnItemClickListener() {

							@Override
							public void onPullItemClick(AdapterView<?> parent,
									int position) {
								resume.education = educationList.get(position).name;
								educationTV.setText(resume.education);
							}
						});
				pullDownList.popupWindwShowing();
			}
		});
		workExperienceTV = (TextView) this
				.findViewById(R.id.myresume_tv_workexperience);
		workExperienceTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PullDownList pullDownList = new PullDownList(getContext(),
						getContext().findViewById(
								R.id.myresume_rel_workexperience),
						workExperienceTV, experienceList);
				pullDownList
						.setPullOnItemClickListener(new PullOnItemClickListener() {

							@Override
							public void onPullItemClick(AdapterView<?> parent,
									int position) {
								resume.workExperience = experienceList
										.get(position).name;
								workExperienceTV.setText(resume.workExperience);
							}
						});
				pullDownList.popupWindwShowing();
			}
		});
		telNumTV = (EditText) this.findViewById(R.id.myresume_tv_telnum);
		// telNumTV.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(getContext(), InputActivity.class);
		// Bundle bundle = new Bundle();
		// bundle.putInt("limite", 13);
		// bundle.putString("cont", resume.phone);
		// intent.putExtras(bundle);
		// startActivityForResult(intent, 102);
		// }
		// });
		expectPostTV = (TextView) this
				.findViewById(R.id.myresume_tv_expectpost);
		expectPostTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), Level1Activity.class);
				Bundle b = new Bundle();
				b.putSerializable("list", ApplicationData.typeLists);
				intent.putExtras(b);
				startActivityForResult(intent, 100);
			}
		});
		expectSalaryTV = (TextView) this
				.findViewById(R.id.myresume_tv_expectsalary);
		expectSalaryTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PullDownList pullDownList = new PullDownList(getContext(),
						getContext().findViewById(
								R.id.myresume_rel_expectsalary),
						expectSalaryTV, salaryList);
				pullDownList
						.setPullOnItemClickListener(new PullOnItemClickListener() {

							@Override
							public void onPullItemClick(AdapterView<?> parent,
									int position) {
								resume.expectSalary = salaryList.get(position).name;
								expectSalaryTV.setText(resume.expectSalary);
							}
						});
				pullDownList.popupWindwShowing();
			}
		});
		selfassessmentTV = (EditText) this
				.findViewById(R.id.myresume_tv_selfassessment);
		selfassessmentTV.addTextChangedListener(new TextWatcher() {
			String text = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				int size = selfassessmentTV.getText().length();
				if (size <= 150) {
					text = selfassessmentTV.getText().toString().trim();
				}
				if (size > 150) {
					selfassessmentTV.setText(text);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		// selfassessmentTV.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(getContext(), InputActivity.class);
		// Bundle bundle = new Bundle();
		// bundle.putInt("limite", 150);
		// bundle.putString("cont", resume.evaluation);
		// intent.putExtras(bundle);
		// startActivityForResult(intent, 103);
		// }
		// });
		sureBtn = (TextView) this.findViewById(R.id.btn);
		// sureBtn.setImageResource(R.drawable.eidt_config);
		sureBtn.setVisibility(View.VISIBLE);
		sureBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO
				resume.rName = nameTV.getText().toString().trim();
				resume.phone = telNumTV.getText().toString().trim();
				resume.evaluation = selfassessmentTV.getText().toString()
						.trim();
				if (resume.rName == null || resume.rName.equals("")) {

					MyToast.getToast().showToast(getContext(),
							R.string.name_empty);
					return;
				}
				if (resume.phone == null || resume.phone.equals("")) {
					MyToast.getToast().showToast(getContext(),
							R.string.phone_empty);
					return;
				}

				new CommitResumeTask(resume, createResume ? "1" : "2")
						.execute();
			}
		});
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
		// if (mdatePicker == null)
		mdatePicker = (DatePicker) dialog
				.findViewById(R.id.set_date_mDatePicker);
		Date d = new Date();
		if (MainActivity.androidVersion > 10)
			mdatePicker.setMaxDate(d.getTime());
		if (resume.birth != null && !resume.birth.equals("")) {
			String[] bs = resume.birth.split("-");
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
						birthTV.setText(m_year + "-" + m_month + "-" + m_day);
						resume.setBirth(birthTV.getText().toString().trim());
						dialog.dismiss();
					}
				});
	}

	private void showPicDialog() {
		AlertDialog dialog = new AlertDialog.Builder(getContext()).setItems(
				R.array.picFrom, picListener).create();
		dialog.show();
	}

	private static final int PIC_TAKE_PHONE = 2;
	private static final int PIC_ALBUM = 3;
	private static final int CROP_PICTURE = 4;
	private static final int CROP_PICTURE2 = 5;
	private static String IMAGE_FILE_LOCATION = Environment
			.getExternalStorageDirectory() + "/temp.png";

	private Uri imageUri;
	private DialogInterface.OnClickListener picListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == 1) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
				intent.setType("image/*");
				startActivityForResult(intent, PIC_ALBUM);
			} else if (which == 0) {
				try {
					FileTools.deleteFile(IMAGE_FILE_LOCATION);
				} catch (IOException e) {

				}
				if (!LocalMemory.getInstance().checkSDCard()) {
					MyToast.getToast().showToast(getApplicationContext(),
							"请插入sd卡");
					return;
				}
				if (imageUri != null) {
					// capture a bitmap and store it in Uri
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

					startActivityForResult(intent, PIC_TAKE_PHONE);
				} else {
				}
			}
		}
	};

	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, requestCode);
	}

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
			cropImageUri(Uri.fromFile(new File(IMAGE_FILE_LOCATION)),
					Tools.dip2px(getContext(), 110),
					Tools.dip2px(getContext(), 110), CROP_PICTURE);
			break;
		case PIC_ALBUM:// from crop_big_picture
			if (resultCode != RESULT_OK) {
				return;
			}
			cropImageUri(data.getData(), Tools.dip2px(getContext(), 110),
					Tools.dip2px(getContext(), 110), CROP_PICTURE2);
			break;
		case CROP_PICTURE:
			if (resultCode != RESULT_OK) {
				return;
			}
			if (imageUri != null) {
				// Bitmap bitmap = data.getParcelableExtra("data");
				// if (bitmap == null) {
				Bitmap bitmap = ImageUtils.compressBimap(IMAGE_FILE_LOCATION);
				// }
				if (bitmap == null)
					return;
				int degree = ImageUtils.readPictureDegree(IMAGE_FILE_LOCATION);
				if (degree != 0)
					bitmap = ImageUtils.rotaingImageView(degree, bitmap);
				// photoImgV.setVisibility(View.VISIBLE);
				// photoImgV.setImageBitmap(bitmap);
				// photoImgV.postInvalidate();
				new CommitPhotoTask(bitmap).execute();
			}
			break;
		case CROP_PICTURE2:
			if (resultCode != RESULT_OK) {
				return;
			}
			Bitmap bitmap = data.getParcelableExtra("data");
			if (bitmap == null) {
				return;
			}
			new CommitPhotoTask(bitmap).execute();

			break;

		case 100:
			// if (resultCode == RESULT_OK) {
			if (b != null) {
				Type key = (Type) (Obj) b.getSerializable("key");
				Type type = (Type) (Obj) b.getSerializable("obj");
				if (type != null) {
					resume.expectPost = key.name + " - " + type.name;
					resume.expectPostId = key.id + "-" + type.id;
					expectPostTV.setText(resume.expectPost);
				}
			}
			break;
		case 101:
			// if (resultCode == RESULT_OK) {
			if (b != null) {
				String name = b.getString("content");
				resume.rName = name;
				nameTV.setText(name);
			}
			break;

		case 102:
			// if (resultCode == RESULT_OK) {
			if (b != null) {
				String num = b.getString("content");
				resume.phone = num;
				telNumTV.setText(num);
			}
			break;
		case 103:
			// if (resultCode == RESULT_OK) {
			if (b != null) {
				String assessment = b.getString("content");
				resume.evaluation = assessment;
				selfassessmentTV.setText(assessment);
			}
			break;
		}

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
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	@Override
	public BaseActivity getContext() {
		// TODO Auto-generated method stub
		return this;
	}

	class CommitPhotoTask extends AsyncTask<Object, Integer, String> {

		Bitmap bm;

		public CommitPhotoTask(Bitmap bm) {
			this.bm = bm;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getApplicationContext(),
					R.string.upload_now));
			dialog.show();
		}

		@Override
		protected String doInBackground(Object... params) {
			// TODO
			try {
				return Community.getInstance(getContext()).postResumeImg(
						getContext(), LlkcBody.USER_ACCOUNT,
						LlkcBody.PASS_ACCOUNT, bm);
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
				resume.head = result;
				finalBitmap.display(photoImgV, resume.head);
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

	class CommitResumeTask extends AsyncTask<Object, Integer, Boolean> {

		Resume resu;
		String type;

		public CommitResumeTask(Resume resume, String type) {
			this.resu = resume;
			this.type = type;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getApplicationContext(),
					R.string.upload_now));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			// TODO
			try {
				return Community.getInstance(getContext()).OperResume(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT, type,
						resu);
			} catch (Exception e) {
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
				if (ApplicationData.resumeList.size() > 0)
					ApplicationData.resumeList.set(0, resume);
				else
					ApplicationData.resumeList.add(0, resume);
				if (type.equals("1"))
					MyToast.getToast().showToast(
							getContext(),
							Tools.getStringFromRes(getApplicationContext(),
									R.string.createresume_success));
				else if (type.equals("2"))
					MyToast.getToast().showToast(
							getContext(),
							Tools.getStringFromRes(getApplicationContext(),
									R.string.updateresume_success));
			} else {
				if (type.equals("1"))
					MyToast.getToast().showToast(
							getContext(),
							Tools.getStringFromRes(getApplicationContext(),
									R.string.createresume_fail));
				else if (type.equals("2"))
					MyToast.getToast().showToast(
							getContext(),
							Tools.getStringFromRes(getApplicationContext(),
									R.string.updateresume_fail));
			}
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
}
