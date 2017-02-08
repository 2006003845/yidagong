package com.zzl.zl_app.apk;

import java.util.Collections;
import java.util.List;

import com.zzl.zl_app.util.Tools;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class CommunicationImpl implements ICommunity {

	private Intent mIntent;

	public CommunicationImpl(Intent intent) {
		mIntent = intent;
		// mIntent = new Intent(getTargetIntent());
	}

	public CommunicationImpl() {
		// mIntent = intent;
		mIntent = new Intent(getTargetIntent());
	}

	@Override
	public ApplicationItem getApplicationItem(PackageManager packageManager,
			String applicationName) {
		return makeApplicationItem(packageManager, applicationName);
	}

	private ApplicationItem makeApplicationItem(PackageManager mPackageManager,
			String packName) {
		// Load all matching activities and sort correctly
		List<ResolveInfo> list = onQueryPackageManager(mPackageManager, mIntent);
		Collections.sort(list, new ResolveInfo.DisplayNameComparator(
				mPackageManager));
		ApplicationItem applicationItem = null;
		int listSize = list.size();
		for (int i = 0; i < listSize; i++) {
			ResolveInfo resolveInfo = list.get(i);
			ApplicationInfo ai = resolveInfo.activityInfo.applicationInfo;
			String name = resolveInfo.activityInfo.packageName;
			if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) <= 0
					&& packName.equals(name)) {
				applicationItem = new ApplicationItem(mPackageManager,
						resolveInfo, null);
			}
		}
		return applicationItem;
	}

	private List<ResolveInfo> onQueryPackageManager(
			PackageManager mPackageManager, Intent queryIntent) {
		return mPackageManager.queryIntentActivities(queryIntent, 0);
	}

	private Intent getTargetIntent() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory("android.intent.category.LAUNCHER");
		return intent;
	}

	@Override
	public void startCommunication(Activity act, ApplicationItem item) {
		if (item != null) {

			mIntent.setClassName(item.packageName, item.className);
			Tools.log("Activity_Log", "Activity_pack" + item.packageName);
			Tools.log("Activity_Log", "Activity_className" + item.className);

			if (item.extras != null) {
				mIntent.putExtras(item.extras);
			}
			act.startActivity(mIntent);
		}

	}

}
