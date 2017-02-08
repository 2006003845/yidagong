package com.zrlh.llkc.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zrlh.llkc.AppStart;
import com.zrlh.llkc.funciton.LlkcBody;

public class Notifi {

	private Context context;
	public Class<MainActivity> mainCalss = MainActivity.class;
	public Class<AppStart> welCalss = AppStart.class;
	private NotificationManager nm;

	public Notifi(Context context, NotificationManager nm) {
		super();
		this.context = context;
		this.nm = nm;
	}

	private List<Integer> idList = new ArrayList<Integer>();

	public void showNotif(int icon, String tickertext, String title,
			String content, int notifId, boolean isActive) {
		idList.add(notifId);
		Notification notification = new Notification(icon, tickertext,
				System.currentTimeMillis());
		// 后面的参数分别是显示在顶部通知栏的小图标，小图标旁的文字（短暂显示，自动消失）系统当前时间（不明白这个有什么用）
		if (LlkcBody.isNotiByShake && LlkcBody.isNotiByVoice)
			notification.defaults = Notification.DEFAULT_ALL;
		else if (LlkcBody.isNotiByShake && !LlkcBody.isNotiByVoice)
			notification.defaults = Notification.DEFAULT_VIBRATE;
		else if (!LlkcBody.isNotiByShake && LlkcBody.isNotiByVoice)
			notification.defaults = Notification.DEFAULT_SOUND;
		else
			notification.defaults = Notification.DEFAULT_LIGHTS;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		// 这是设置通知是否同时播放声音或振动，声音为Notification.DEFAULT_SOUND
		// 振动为Notification.DEFAULT_VIBRATE;
		// Light为Notification.DEFAULT_LIGHTS，在我的Milestone上好像没什么反应
		// 全部为Notification.DEFAULT_ALL
		// 如果是振动或者全部，必须在AndroidManifest.xml加入振动权限
		Intent intent = null;
		if (isActive)
			intent = new Intent(context, mainCalss);
		else
			intent = new Intent(context, welCalss);
		Bundle b = new Bundle();
		b.putBoolean("not", true);
		intent.putExtras(b);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pt = PendingIntent.getActivity(context, 0, intent, 0);
		// 点击通知后的动作，这里是转回main 这个Acticity
		notification.setLatestEventInfo(context, title, content, pt);
		nm.notify(notifId, notification);
	}

	public void cancelNotis() {
		for (Integer i : idList) {
			nm.cancel(i);
		}
	}
}
