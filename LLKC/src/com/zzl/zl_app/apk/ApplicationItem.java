package com.zzl.zl_app.apk;

import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class ApplicationItem {
	public ResolveInfo resolveInfo;
	public CharSequence label;
	public Drawable icon;
	public String packageName;
	public String className;
	public Bundle extras;

	ApplicationItem(PackageManager pm, ResolveInfo resolveInfo,
			IconResizer resizer) {
		this.resolveInfo = resolveInfo;
		label = resolveInfo.loadLabel(pm);
		ComponentInfo ci = resolveInfo.activityInfo;
		if (ci == null)
			ci = resolveInfo.serviceInfo;
		if (label == null && ci != null) {
			label = resolveInfo.activityInfo.name;
		}

		if (resizer != null) {
			icon = resizer.createIconThumbnail(resolveInfo.loadIcon(pm));
		}
		packageName = ci.applicationInfo.packageName;
		className = ci.name;
	}

	public ApplicationItem() {
	}
}
