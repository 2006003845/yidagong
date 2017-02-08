package com.zrlh.llkc.corporate.base;

import java.util.Map;
import java.util.Set;

import com.zrlh.llkc.activity.MainActivity;
import com.zrlh.llkc.funciton.LlkcBody;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class BaseActivity extends Activity {

	public Map<String, BaseActivity> getActMap() {
		return ActContent.actMap;
	}

	public void addActMap(String key, BaseActivity act) {
		if (key != null && act != null) {
			ActContent.actMap.put(key, act);
		}
	}

	public BaseActivity getAct(String key) {
		return ActContent.actMap.get(key);
	}

	private void removeActFromMap(String key) {
		ActContent.actMap.remove(key);
	}

	public void switchActIn() {
		if (inAnimId == -1)
			return;
		overridePendingTransition(inAnimId, outAnimId);
	}

	private int inAnimId = -1, outAnimId = -1;

	public void setActAwitchAnim(int inAnimId, int outAnimId) {
		this.inAnimId = inAnimId;
		this.outAnimId = outAnimId;
	}

	public void closeOneAct(String key) {
		BaseActivity act = getAct(key);
		if (act != null) {
			act.finish();
			removeActFromMap(key);
			overridePendingTransition(inAnimId, outAnimId);
		}
	}

	public void finishAllActs() {
		Set<String> keySet = ActContent.actMap.keySet();
		for (String key : keySet) {
			BaseActivity act = ActContent.actMap.get(key);
			if (act != null) {
				act.finish();
			}
		}
		ActContent.actMap.clear();
		System.exit(0);
		System.gc();
	}

	private boolean onChange = false;

	public void onChange() {
		onChange = true;
	}

	public void disOnChange() {
		onChange = false;
	}

	public boolean isOnChange() {
		return onChange;
	}

	public abstract BaseActivity getContext();

	public static int width, height;

	// public static void initDiag(Context context, LayoutInflater inflater) {
	// if (inflater != null) {
	// layout = inflater.inflate(R.layout.dialog_result, null);
	// dialog = new AlertDialog.Builder(context).create();
	// dialog.setCanceledOnTouchOutside(true);
	// }
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		if (!isOnChange()) {
			// Music.getInstance(GameContext()).pause();
		}
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// Music.getInstance(GameContext()).resume();
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// Music.getInstance(GameContext()).resume();
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		if (!isOnChange()) {
			// Music.getInstance(GameContext()).stop();
		} else {
			disOnChange();
		}
		super.onStop();
	}

	@Override
	public void finish() {
		onChange();
		super.finish();
	}

	@Override
	public void finishActivity(int requestCode) {
		super.finishActivity(requestCode);
	}

	@Override
	public void finishActivityFromChild(Activity child, int requestCode) {
		super.finishActivityFromChild(child, requestCode);
	}

	@Override
	public void finishFromChild(Activity child) {
		super.finishFromChild(child);
	}

	@Override
	public void startActivity(Intent intent) {
		onChange();
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		super.startActivity(intent);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		onChange();
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startActivityFromChild(Activity child, Intent intent,
			int requestCode) {
		// TODO Auto-generated method stub
		onChange();
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		super.startActivityFromChild(child, intent, requestCode);
	}

	@Override
	public boolean startActivityIfNeeded(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		onChange();
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		return super.startActivityIfNeeded(intent, requestCode);
	}

	Resources res;

	public Drawable getDrawable(int drawableId) {
		if (res == null) {
			res = getContext().getResources();
		}
		if (drawableId == -1 || drawableId == 0) {
			return null;
		}
		Drawable drawable = res.getDrawable(drawableId);
		drawable.setBounds(0, 0, 40, 40);
		return drawable;
	}

	/**
	 * 显示 自定义弹出窗�?
	 * 
	 * @param id
	 * @param dialogLayoutID
	 * @param context
	 * @param title
	 * @param content
	 */
	public void showMsgDialog(String id, int dialogLayoutID, Context context,
			String title, String content) {
		View layout = getLayoutInflater().inflate(dialogLayoutID, null);
		AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		dialog.getWindow().setLayout((int) MainActivity.CURRENT_SCREEN_WIDTH-30,
				(int) MainActivity.CURRENT_SCREEN_HEIGHT-30);
		dialog.getWindow().setContentView(layout);
		if (title != null)
			setDialogTitle(dialog, dialogLayoutID, title, id);
		if (content != null)
			setDialogContent(dialog, dialogLayoutID, content, id);
		setPositiveClickListener(dialog, dialogLayoutID, id);
		setNegativeClickListener(dialog, dialogLayoutID, id);
	}

	/**
	 * 设置弹出窗口的显示内�?
	 * 
	 * @param dialog
	 * @param layoutId
	 * @param msg
	 */
	public abstract void setDialogContent(AlertDialog dialog, int layoutId,
			String content, String id);

	/**
	 * 设置弹出窗口的显示标�?
	 * 
	 * @param dialog
	 * @param layoutId
	 * @param title
	 */
	public abstract void setDialogTitle(AlertDialog dialog, int layoutId,
			String title, String id);

	/**
	 * 设置弹出窗口的确定按键监�?
	 * 
	 * @param dialog
	 * @param layoutId
	 * @return
	 */
	public abstract OnClickListener setPositiveClickListener(
			AlertDialog dialog, int layoutId, String id);

	/**
	 * 设置弹出窗口的取消按键监�?
	 * 
	 * @param dialog
	 * @param layoutId
	 * @return
	 */
	public abstract OnClickListener setNegativeClickListener(
			AlertDialog dialog, int layoutId, String id);

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

}
