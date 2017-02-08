package com.zrlh.llkc.ui;


import com.zrlh.llkc.R;
import com.zrlh.llkc.funciton.LlkcBody;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;


public class CustomLoadingDialog extends Dialog {
	TextView loading_text;
	
	static int theme = R.style.custom_dialog;
	
	public CustomLoadingDialog(Context context) {
		super(context,theme);
		LlkcBody.IS_STOP_REQUEST = false;
	}

	 protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 //去掉标题
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 //设置view样式
		 setContentView(R.layout.custom_loading_dialog);	
		 loading_text= (TextView) findViewById(R.id.loading_text);
	 }
	 //called when this dialog is dismissed
	 protected void onStop() {
		 
	 }
	 @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		LlkcBody.IS_STOP_REQUEST = false;
	}
	 
	 @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		 if (keyCode == KeyEvent.KEYCODE_BACK ) { 
			 LlkcBody.IS_STOP_REQUEST = true;
		 }
		return super.onKeyDown(keyCode, event);
		
	}
	 
}
