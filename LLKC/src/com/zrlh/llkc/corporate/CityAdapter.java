package com.zrlh.llkc.corporate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zrlh.llkc.R;
import com.zzl.zl_app.city.PinnedHeaderListView;
import com.zzl.zl_app.city.PinnedHeaderListView.PinnedHeaderAdapter;

@SuppressLint("ResourceAsColor")
public class CityAdapter extends BaseAdapter implements SectionIndexer,
		PinnedHeaderAdapter, OnScrollListener {
	// 首字母集
	private List<City> mCities;
	private Map<String, List<City>> mMap;
	private List<String> mSections;
	private List<Integer> mPositions;
	private LayoutInflater inflater;

	public CityAdapter(Context context, List<City> cities,
			Map<String, List<City>> map, List<String> sections,
			List<Integer> positions) {
		// TODO Auto-generated constructor stub
		inflater = LayoutInflater.from(context);
		mCities = cities;
		mMap = map;
		mSections = sections;
		mPositions = positions;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCities.size();
	}

	@Override
	public City getItem(int position) {
		// TODO Auto-generated method stub
		if (position >= 0) {
			int section = getSelectForPosition(position);
			if (section >= 0)
				return mMap.get(mSections.get(section)).get(
						position - getPositionForSelect(section));
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int section = getSelectForPosition(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_select_city, null);
		}
		TextView group = (TextView) convertView.findViewById(R.id.group_title);
		TextView city = (TextView) convertView.findViewById(R.id.column_title);
		group.setVisibility(View.GONE);
		if (position == 0) {
			group.setVisibility(View.VISIBLE);
			group.setText("所在城市");
		} else {
			if (getPositionForSelect(section) == position) {
				group.setVisibility(View.VISIBLE);
				// if (section < mSections.size()) {
				group.setVisibility(View.VISIBLE);
				String sect = mSections.get(section);
				group.setText(sect);
				// }
			} else {
				group.setVisibility(View.GONE);
			}
		}
		if (section >= 0) {
			int pos = position - getPositionForSelect(section);
			List<City> cList = mMap.get(mSections.get(section));
			if (cList != null && cList.size() > pos) {
				City item = cList.get(pos);
				city.setText(item != null ? item.getName() : "");
				if (position == 0
						&& !detectionCity(item != null ? item.getName() : "")) {
					city.setBackgroundColor(R.color.gray);
				}
			}
		}

		return convertView;
	}

	public boolean detectionCity(String c) {
		if (c == null)
			return false;
		for (City city : ApplicationData.mCityList) {
			if (city != null && c.contains(city.name))
				return true;
		}
		return false;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if (view instanceof PinnedHeaderListView) {
			((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
		}

	}

	@Override
	public int getPinnedHeaderState(int position) {
		int realPosition = position;
		if (realPosition < 0 || position >= getCount()) {
			return PINNED_HEADER_GONE;
		}
		int section = getSelectForPosition(realPosition);
		int nextSectionPosition = getPositionForSelect(section + 1);
		if (nextSectionPosition != -1
				&& realPosition == nextSectionPosition - 1) {
			return PINNED_HEADER_PUSHED_UP;
		}
		return PINNED_HEADER_VISIBLE;
	}

	@Override
	public void configurePinnedHeader(View header, int position, int alpha) {
		// TODO Auto-generated method stub
		int realPosition = position;
		int section = getSelectForPosition(realPosition);
		String title = (String) getSections()[section];
		((TextView) header.findViewById(R.id.group_title)).setText(title);
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return mSections.toArray();
	}

	public int getPositionForSelect(int section) {
		// TODO Auto-generated method stub
		if (section < 0 || section >= mPositions.size()) {
			return -1;
		}
		return mPositions.get(section);
	}

	public int getSelectForPosition(int position) {
		if (position < 0 || position >= getCount()) {
			return -1;
		}
		int index = Arrays.binarySearch(mPositions.toArray(), position);
		return index >= 0 ? index : -index - 2;
	}

	@Override
	public int getPositionForSection(int section) {
		// TODO Auto-generated method stub
		if (section == 35) {
			return 0;
		}
		for (int i = 1; i < mSections.size(); i++) {
			String l = mSections.get(i);
			char firstChar = l.charAt(0);
			if (firstChar == section)
				return mPositions.get(i);
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
}
