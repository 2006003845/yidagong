package com.zrlh.llkc.corporate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;

public class InputActivity extends BaseActivity {

	public static final String Tag = "input";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, InputActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	private int limite = -1;
	String text = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(Tag, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.input);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			limite = b.getInt("limite", -1);
			text = b.getString("cont");
		}
		initView();
	}

	private TextView titleTV;
	private ImageButton sureBtn;
	private EditText inputET;
	private TextView limiteTV;

	private void initView() {
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText("编辑");
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		limiteTV = (TextView) this.findViewById(R.id.input_limite);
		limiteTV.setText(0 + "/" + limite);
		if (limite == -1)
			limiteTV.setVisibility(View.GONE);
		inputET = (EditText) this.findViewById(R.id.input_et);
		inputET.setText(text);
		inputET.addTextChangedListener(new TextWatcher() {
			String text = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				int size = inputET.getText().length();
				limiteTV.setText(size + "/" + limite);
				if (size <= limite) {
					text = inputET.getText().toString().trim();
				}
				if (size > limite) {
					inputET.setText(text);
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
		sureBtn = (ImageButton) this.findViewById(R.id.btn);
		sureBtn.setVisibility(View.VISIBLE);
		sureBtn.setImageResource(R.drawable.eidt_config);
		sureBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editable ed = inputET.getText();
				if (ed != null && ed.toString() != null) {
					String text = ed.toString().trim();
					Intent intent = getIntent();
					Bundle b = new Bundle();
					b.putString("content", text);
					intent.putExtras(b);
					getContext().setResult(RESULT_OK, intent);
					getContext().finish();
				} else {
					MyToast.getToast().showToast(getContext(), "输入为空!");
				}
			}
		});
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
		closeOneAct(Tag);
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
