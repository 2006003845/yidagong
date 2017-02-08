package com.zrlh.llkc.ui;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.util.Tools;

public class AdviceFeedBackActivity extends BaseActivity {
	public static final String TAG = "advicefeedback";

	TextView titleTextView;
	EditText teltext, contenttext;
	Button commitadvice;
	ImageButton backiImageView;
	String telText, contextText;
	private static final int SUCCESS = 1;
	private static final int FAIL = 2;

	ProgressDialog dialog = null;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				Toast.makeText(AdviceFeedBackActivity.this, "提交成功！",
						Toast.LENGTH_SHORT).show();
				closeOneAct(TAG);
			} else {
				Toast.makeText(AdviceFeedBackActivity.this, "提交失败！",
						Toast.LENGTH_SHORT).show();
			}
		}

	};

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.advicefeedbacktitle);
		init();

	}
	
	class AdviceFeedTask extends AsyncTask<Object, Integer, Boolean>{

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
				return Community.getInstance(getContext())
						.adviceFeed(telText, contextText);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if(result)
				handler.sendEmptyMessage(SUCCESS);
			else
				handler.sendEmptyMessage(FAIL);
			super.onPostExecute(result);
		}
		
	}

	void init() {
		backiImageView = (ImageButton) findViewById(R.id.back);

		titleTextView = (TextView) findViewById(R.id.title_tv);
		this.findViewById(R.id.btn).setVisibility(View.GONE);
		titleTextView.setText(R.string.advicefeedback);
		commitadvice = (Button) findViewById(R.id.commitadvice);
		teltext = (EditText) findViewById(R.id.teltext);
		contenttext = (EditText) findViewById(R.id.contenttext);

		commitadvice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				telText = teltext.getText().toString().trim();
				contextText = contenttext.getText().toString().trim();
				if ("".equals(contextText)) {
					Toast.makeText(getContext(), "反馈意见不能为空！", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				new AdviceFeedTask().execute();
			}
		});

		titleTextView.setText("意见反馈");
		// newthingImageView.setVisibility(View.INVISIBLE);
		// backiImageView.setBackgroundResource(R.drawable.btn_back);
		// backiImageView.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.btn_back));
		backiImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
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
