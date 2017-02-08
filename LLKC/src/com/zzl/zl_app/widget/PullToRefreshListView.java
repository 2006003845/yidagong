package com.zzl.zl_app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.ListView;

import com.zrlh.llkc.R;
import com.zzl.zl_app.widget.internal.EmptyViewMethodAccessor;
import com.zzl.zl_app.widget.internal.PullToRefreshAdapterViewBase;

public class PullToRefreshListView extends
		PullToRefreshAdapterViewBase<ListView> {

	class InternalListView extends ListView implements EmptyViewMethodAccessor {

		public InternalListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public void setEmptyView(View emptyView) {
			PullToRefreshListView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}

		@Override
		public ContextMenuInfo getContextMenuInfo() {
			return super.getContextMenuInfo();
		}
	}

	public PullToRefreshListView(Context context) {
		super(context);
	}

	public PullToRefreshListView(Context context, int mode) {
		super(context, mode);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected final ListView createRefreshableView(Context context,
			AttributeSet attrs) {
		ListView gv = new InternalListView(context, attrs);
		// Use Generated ID (from res/values/ids.xml)
		gv.setId(R.id.listview);
		return gv;
	}

	@Override
	public ContextMenuInfo getContextMenuInfo() {
		return ((InternalListView) getRefreshableView()).getContextMenuInfo();
	}
}
