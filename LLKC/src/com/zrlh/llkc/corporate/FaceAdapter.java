package com.zrlh.llkc.corporate;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zrlh.llkc.R;

public class FaceAdapter extends BaseAdapter {
	private Context context;
	private String[] faceNames;
	private HashMap<String, Bitmap> faces;
	private LayoutInflater inflater;

	public FaceAdapter(Context context, String[] faceNames) {
		this.context = context;
		this.faceNames = faceNames;
		this.faces = Face.getfaces(context);
		inflater = (LayoutInflater) context
				.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return faceNames == null ? 0 : faceNames.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// ImageView imageView;
		// imageView = (ImageView) convertView;
		// if (imageView == null) {
		// // 给ImageView设置资源
		// imageView = new ImageView(context);
		// // 设置布局 图片120×120显示
		// imageView.setLayoutParams(new GridView.LayoutParams(50, 50));
		// // 设置显示比例类型
		// imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		// }
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.item_imgv, null);
		}
		ImageView imageView = (ImageView) view.findViewById(R.id.item_imgv);
		if (faces.containsKey(Face.faceNames[position])) {
			imageView.setImageBitmap(faces.get(Face.faceNames[position]));
		}
		return view;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetInvalidated() {
		// TODO Auto-generated method stub
		super.notifyDataSetInvalidated();
	}

}
