package com.zrlh.llkc.activity;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.zrlh.llkc.funciton.Authentication;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class AuthenticationModifyActivity extends BaseActivity {
	public static final String TAG = "authenticationmodify";
	private TextView titleTV;
	private EditText phoneET, addrET, introET;
	private TextView nameTV, leftTV;
	private ImageView licenseImgV;
	private Authentication authen;
	ProgressDialog dialog = null;

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, AuthenticationModifyActivity.class);
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
		setContentView(R.layout.authentication_modify);
		init();
		new AuthInfoTask().execute();
	}

	private void init() {
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText(R.string.aunthencition_modify);
		this.findViewById(R.id.btn).setVisibility(View.GONE);
		nameTV = (TextView) this.findViewById(R.id.auth_modify_tv_name);
		nameTV.setText("");
		phoneET = (EditText) this.findViewById(R.id.auth_modify_et_phone);
		phoneET.setText("");
		addrET = (EditText) this.findViewById(R.id.auth_modify_et_addr);
		addrET.setText("");
		introET = (EditText) this.findViewById(R.id.auth_modify_et_intro);
		introET.setText("");
		leftTV = (TextView) this.findViewById(R.id.auth_modify_tv_left);
		leftTV.setText("150/150");
		licenseImgV = (ImageView) this
				.findViewById(R.id.auth_modify_imgv_license);
		this.findViewById(R.id.auth_modify_btn_commit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String phone = phoneET.getText().toString().trim();
						String addr = addrET.getText().toString().trim();
						String intro = introET.getText().toString().trim();
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
						new ModifyAuthTask(phone, addr, intro).execute();
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

	class AuthInfoTask extends AsyncTask<Void, Void, Authentication> {

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
		protected Authentication doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				return Community.getInstance(getContext()).companyInfo(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Authentication result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result != null) {
				authen = result;
				nameTV.setText(authen.name);
				phoneET.setText(authen.phone);
				addrET.setText(authen.address);
				introET.setText(authen.intro);
				int size = introET.getText().length();
				Spanned text = Html.fromHtml(" <font color='#b92d2d'>"
						+ (150 - size) + "</font>" + "/150");
				leftTV.setText(text);
				ImageCache.getInstance().loadImg(authen.imgurl, licenseImgV,
						R.drawable.bg_default);
			} else {
				MyToast.getToast().showToast(getContext(), "加载企业信息失败！请稍后再试");
				closeOneAct(TAG);
			}

			super.onPostExecute(result);
		}

	}

	class ModifyAuthTask extends AsyncTask<Object, Integer, Boolean> {

		String phone;
		String addr;
		String intro;

		public ModifyAuthTask(String phone, String addr, String intro) {
			super();
			this.phone = phone;
			this.addr = addr;
			this.intro = intro;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(getApplicationContext(),
					R.string.submit));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			try {
				return Community.getInstance(getContext()).companyModify(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT, phone,
						addr, intro);
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
				closeOneAct(TAG);
			}

			super.onPostExecute(result);
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
