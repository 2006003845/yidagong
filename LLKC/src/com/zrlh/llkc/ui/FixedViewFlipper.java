package com.zrlh.llkc.ui;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ViewFlipper;

public class FixedViewFlipper extends ViewFlipper {

	public FixedViewFlipper(Context context) {
		super(context);
	}

	public FixedViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDetachedFromWindow() {

		int apiLevel = Build.VERSION.SDK_INT;

		if (apiLevel >= 7) {
			try {
				super.onDetachedFromWindow();
			} catch (IllegalArgumentException e) {
				/* Quick catch and continue on api level 7, the Eclair 2.1 */
			} finally {
				super.stopFlipping();
			}
		} else {
			super.onDetachedFromWindow();
		}
	}

	public static void main(String[] args) {

	}
}