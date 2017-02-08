package com.zzl.zl_app.apk;

import android.app.Activity;
import android.content.pm.PackageManager;

public interface ICommunity {
	public abstract ApplicationItem getApplicationItem(
			PackageManager packageManager, String applicationName);

	public abstract void startCommunication(Activity act, ApplicationItem item);

}
