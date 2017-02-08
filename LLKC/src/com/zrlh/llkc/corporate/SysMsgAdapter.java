package com.zrlh.llkc.corporate;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zrlh.llkc.R;
import com.zzl.zl_app.entity.Msg;
import com.zzl.zl_app.util.TimeUtil;

/*
 * 输出信息适配
 * */
public class SysMsgAdapter extends BaseAdapter {

	private List<Msg> msgList;
	Context context;
	LayoutInflater inflater;

	public SysMsgAdapter(Context context, List<Msg> msgList) {
		this.msgList = msgList;
		this.context = context;
		inflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {

		return msgList.size();
	}

	@Override
	public Object getItem(int position) {
		return msgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void update() {
		notifyDataSetChanged();
	}

	private int selectedPositon = -1;

	public int getSelectedPositon() {
		return selectedPositon;
	}

	public void setSelectedPositon(int selectedPositon) {
		this.selectedPositon = selectedPositon;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_sysmsg, null);
			holder = new ViewHolder();
			holder.time = (TextView) convertView
					.findViewById(R.id.item_sysmsg_tv_time);
			holder.content = (TextView) convertView
					.findViewById(R.id.item_sysmsg_tv_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Msg msg = msgList.get(position);
		try {
			holder.time.setText(TimeUtil.getLocalTimeString(Long
					.parseLong(msg.time)));
		} catch (Exception e) {
		}

		holder.content.setText(msg.content);

		return convertView;
	}

	class ViewHolder {

		TextView content;
		TextView time;

	}
}
