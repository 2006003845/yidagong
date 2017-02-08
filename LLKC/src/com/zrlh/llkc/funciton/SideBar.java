package com.zrlh.llkc.funciton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;

public class SideBar extends View {
	private char[] l;
	private SectionIndexer sectionIndexter = null;
	private ListView list;
	private int m_nItemHeight ;
	public SideBar(Context context) {
		super(context);
		init();
	}
	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	private void init() {
		l = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
				'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		setBackgroundColor(0x44FFFFFF);
	}
	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	public void setListView(ListView _list) {
		list = _list;
		sectionIndexter = (SectionIndexer) _list.getAdapter();
	}
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int i = (int) event.getY();
		int idx = i / m_nItemHeight;
		if (idx >= l.length) {
			idx = l.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
			if (sectionIndexter == null) {
				sectionIndexter = (SectionIndexer) list.getAdapter();
			}
			int position = sectionIndexter.getPositionForSection(l[idx]);
			if (position == -1) {
				return true;
			}
			list.setSelection(position);
		}
		return true;
	}
	protected void onDraw(Canvas canvas) {
		m_nItemHeight = getHeight()/l.length;
		Paint paint = new Paint();
		paint.setColor(0xFFA6A9AA);
		paint.setTextSize(20);
		paint.setTextAlign(Paint.Align.CENTER);
		float widthCenter = getMeasuredWidth() / 2;
		for (int i = 0; i < l.length; i++) {
			canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight + (i * m_nItemHeight), paint);
		}
		super.onDraw(canvas);
	}
}
