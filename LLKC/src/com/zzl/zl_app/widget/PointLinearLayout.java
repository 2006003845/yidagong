package com.zzl.zl_app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.zrlh.llkc.R;

public class PointLinearLayout extends LinearLayout {

	private int[] pointImgResIds = { R.drawable.dot_normal,
			R.drawable.dot_focused };
	private ImageView[] imgvs;

	public PointLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public PointLinearLayout(Context context) {
		super(context);

	}

	int count = 0;

	public void addViews(Context context, int count, int top) {
		this.count = count;

		// lp.gravity = Gravity.CENTER;
		imgvs = new ImageView[count];
		for (int i = 0; i < count; i++) {
			LayoutParams lp = new LinearLayout.LayoutParams(15, 15);
			lp.setMargins(15, 15, 0, 0);
			ImageView imgv = new ImageView(context);
			imgv.setScaleType(ScaleType.CENTER_CROP);
			imgv.setImageResource(pointImgResIds[0]);
			imgv.setLayoutParams(lp);
			imgvs[i] = imgv;
			this.addView(imgv);
		}
		if (count > 0)
			imgvs[0].setImageResource(pointImgResIds[1]);
	}

	public void setPageIndex(int index) {
		if (index >= count || index < 0) {
			return;
		}
		for (int i = 0; i < count; i++) {
			imgvs[i].setImageResource(pointImgResIds[0]);
		}
		imgvs[index].setImageResource(pointImgResIds[1]);
		this.postInvalidate();
	}

}
