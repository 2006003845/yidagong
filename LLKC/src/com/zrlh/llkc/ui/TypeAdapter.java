package com.zrlh.llkc.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.Obj;

public class TypeAdapter extends BaseAdapter {

	private List<Obj> typeList;
	Context context;
	LayoutInflater inflater;

	public TypeAdapter(Context context, List<Obj> typeList) {
		this.typeList = typeList;
		this.context = context;
		inflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {

		return typeList.size();
	}

	@Override
	public Object getItem(int position) {
		return typeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.textview, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.item_tv);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Obj obj = typeList.get(position);
		if (obj != null)
			holder.name.setText(obj.name);
		return convertView;
	}

	class ViewHolder {

		TextView name;

	}
}
