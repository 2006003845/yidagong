package com.zrlh.llkc.organization;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zrlh.llkc.R;

public class MyexpandableListAdapter extends BaseExpandableListAdapter {
	// 子列表
	ArrayList<ArrayList<Organization.ClassfiyHot>> twoArrayListBig;
	// 父列表
	ArrayList<Organization.ClassfiyHot> oneArrayListbig;
	LayoutInflater layoutInflater;
	Context context;
	OneList oneList;
	int[] logos = new int[] { R.drawable.sell, R.drawable.it, R.drawable.house,
			R.drawable.account, R.drawable.car, R.drawable.consumable,
			R.drawable.market, R.drawable.hospital, R.drawable.manage,
			R.drawable.consult, R.drawable.serve, R.drawable.agriculture,
			R.drawable.other };
	String[] tipeStrings = new String[] { "客户服务/收钱/售后支持/销售", "计算机软件/系统集成/互联网",
			"建筑装修/市政建设/房地产开发", "财务/审计/税务/银行/保险", "汽车/摩托车制造/工程机械",
			"生产/加工/制造/交通运输/物流", "市场/营销/公关/设计/广告/传媒", "生物/制药/化工/环境科学",
			"高级管理/人力资源/行政/文秘", "咨询/顾问/教育培训/律师", "零售/酒店/美容美发/保安/家政",
			"农林牧渔/气象/航空/光电", "其他" };

	public final class OneList {
		ImageView headView, arrowImageView;
		TextView onelisTextView, onetipe;

	}

	public MyexpandableListAdapter(Context context,
			ArrayList<ArrayList<Organization.ClassfiyHot>> twoArrayListBig,
			ArrayList<Organization.ClassfiyHot> oneArrayListbig) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.twoArrayListBig = twoArrayListBig;
		this.oneArrayListbig = oneArrayListbig;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return twoArrayListBig.get(groupPosition).get(childPosition);
	}

	public List<Organization.ClassfiyHot> getChildList(int groupPostion) {
		return twoArrayListBig.get(groupPostion);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean arg2, View arg3, ViewGroup arg4) {
		// TODO Auto-generated method stub

		if (arg3 == null) {
			arg3 = layoutInflater.inflate(R.layout.two_list, null);
		}
		TextView textView = (TextView) arg3.findViewById(R.id.two_list);
		textView.setText(twoArrayListBig.get(groupPosition).get(childPosition)
				.getClsName());
		return arg3;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return twoArrayListBig.get(groupPosition).size();
	}

	public String getChildClsid(int groupPosition, int childPosition) {

		return twoArrayListBig.get(groupPosition).get(childPosition).getClsId();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return oneArrayListbig.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return oneArrayListbig.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean childPosition,
			View arg2, ViewGroup arg3) {
		// TODO Auto-generated method stub
		if (arg2 == null) {
			oneList = new OneList();
			arg2 = layoutInflater.inflate(R.layout.one_list, null);
			oneList.onelisTextView = (TextView) arg2
					.findViewById(R.id.onelisttext);
			oneList.headView = (ImageView) arg2.findViewById(R.id.oneimage);
			oneList.arrowImageView = (ImageView) arg2
					.findViewById(R.id.arrow_image);
			oneList.onetipe = (TextView) arg2.findViewById(R.id.onetipe);

			arg2.setTag(oneList);
		} else {
			oneList = (OneList) arg2.getTag();

		}
		oneList.headView.setImageResource(logos[groupPosition]);
		oneList.onetipe.setText(tipeStrings[groupPosition]);

		oneList.onelisTextView.setText(oneArrayListbig.get(groupPosition)
				.getClsName());
		if (childPosition)// ture is Expanded or false is not isExpanded
			oneList.arrowImageView.setImageResource(R.drawable.arrow);
		else
			oneList.arrowImageView.setImageResource(R.drawable.arrow);

		return arg2;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
