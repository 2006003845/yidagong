package com.zrlh.llkc.organization;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zrlh.llkc.R;

public class AddressAdapter extends BaseAdapter{
	String[] addrreStrings;
	LayoutInflater layoutInflater;
	AddresView addresView;
	public final class AddresView{
		public ImageView address_image;
		public TextView address_text;
		
	}
	
	public AddressAdapter(Context context,String[] addrreStrings){
		layoutInflater = LayoutInflater.from(context);
		this.addrreStrings = addrreStrings;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return addrreStrings.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return addrreStrings[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
	if (convertView == null) {
		addresView = new AddresView();
		convertView = layoutInflater.inflate(R.layout.detail_address, null);
		addresView.address_image = (ImageView)convertView.findViewById(R.id.address_image);
		addresView.address_text = (TextView)convertView.findViewById(R.id.address_text);
		convertView.setTag(addresView);
	} else {
		addresView = (AddresView)convertView.getTag();
	}
	
	addresView.address_text.setText(addrreStrings[position]);
	
	return convertView;
	
	}

}
