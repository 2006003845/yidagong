package com.zrlh.llkc.activity;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class LLKCService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	Receiver receive;
	NotificationManager nm;
	Notifi noti;
	SharedPreferences sharePreference;
	private Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		noti = new Notifi(context, nm);
		sharePreference = context.getSharedPreferences("account",
				Context.MODE_PRIVATE);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		noti.cancelNotis();
		unregisterReceiver(receive);
	}

	public class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
		}
	}

}
