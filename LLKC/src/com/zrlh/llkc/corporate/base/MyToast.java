package com.zrlh.llkc.corporate.base;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zzl.zl_app.util.Tools;

public class MyToast {

	private MyToast() {

	}

	private static MyToast toast;

	public static void initToast(LayoutInflater inflater, int toastLayoutId,
			int contTvId) {
		toastView = inflater.inflate(toastLayoutId, null);
		contTV = (TextView) toastView.findViewById(contTvId);
	}

	public static MyToast getToast() {
		if (toast == null) {
			toast = new MyToast();
		}
		return toast;
	}

	public static View toastView;
	private static TextView contTV;

	public void showToast(Context context, int resID) {
		String text = Tools.getStringFromRes(context, resID);
		Toast toast = Toast.makeText(context, text == null ? "" : text,
				Toast.LENGTH_SHORT);
		if (toastView != null) {
			contTV.setText(text == null ? "" : text);
			toast.setView(toastView);
		}
		toast.show();
	}

	public void showToast(Context context, String text) {
		if(text == null || text.equals(""))
			return;
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		int gravity = toast.getGravity();
		int mx = toast.getXOffset();
		int my = toast.getYOffset();
		toast.setGravity(gravity, mx, my+50);
		if (toastView != null) {
			contTV.setText(text);
			toast.setView(toastView);
		}
		toast.show();
	}
	
	public void showToast(Context context, String text, int my) {
		if(text == null || text.equals(""))
			return;
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.setGravity(toast.getGravity(), toast.getXOffset(), toast.getYOffset()+my);
		if (toastView != null) {
			contTV.setText(text);
			toast.setView(toastView);
		}
		toast.show();
	}
}
