package com.zrlh.llkc.corporate;

import java.util.ArrayList;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zzl.zl_app.city.ToPinYin;

public class City extends Obj {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3090497642325624707L;
	private String countyStr;
	private String[] countys;
	private String firstPY;
	private String allPY;
	private String allFristPY;

	public City(JSONObject json) throws JSONException {
		super(json);
		if (!json.isNull("city")) {
			name = json.getString("city");
		}
		if (!json.isNull("city_small")) {
			countyStr = json.getString("city_small");
			countys = countyStr.split(",");
		}
	}

	public City(String id, String name) {
		super(id, name);
		try {
			setAllPY(ToPinYin.getPinYin(name + ""));
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		setFirstPY(getNameFirstPY(name + ""));
		setAllFristPY(getNameAllFirstPY(name + ""));
	}

	public City(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public City(String id, String city, String[] countys, String firstPY,
			String allPY, String allFristPY) {
		super(id, city);
		this.countys = countys;
		this.firstPY = firstPY;
		this.allPY = allPY;
		this.allFristPY = allFristPY;
	}

	public static ArrayList<City> getList(JSONArray array) throws JSONException {
		if (array == null) {
			return null;
		}
		ArrayList<City> list = new ArrayList<City>();
		for (int i = 0, len = array.length(); i < len; i++) {
			JSONObject obj = array.getJSONObject(i);
			if (obj != null) {
				City city = new City(obj);
				try {
					city.setAllPY(ToPinYin.getPinYin(city.name + ""));
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
				city.setFirstPY(getNameFirstPY(city.name + ""));
				city.setAllFristPY(getNameAllFirstPY(city.name + ""));
				if (city.name != null && city.name.equals("长沙")) {// 目前只发现长沙的拼音
					// 匹配成zhang sha
					city.setAllPY("CHANGSHA");
					city.setAllFristPY("CSS");
					city.setFirstPY("C");
				} else if (city.name != null && city.name.equals("重庆")) {
					city.setAllPY("CHONGQING");
					city.setAllFristPY("CQ");
					city.setFirstPY("C");
				} else if (city.name != null && city.name.equals("长春")) {
					city.setAllPY("CHANGCHUN");
					city.setAllFristPY("CC");
					city.setFirstPY("C");
				} else if (city.name != null && city.name.equals("琼海")) {
					city.setAllPY("QIONGHAISHI");
					city.setAllFristPY("QHS");
					city.setFirstPY("Q");
				} else if (city.name != null && city.name.equals("厦门")) {
					city.setAllPY("XIAMEN");
					city.setAllFristPY("XM");
					city.setFirstPY("X");
				}
				list.add(city);
			}
		}
		return list;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCountyStr() {
		return countyStr;
	}

	public void setCountyStr(String countyStr) {
		this.countyStr = countyStr;
	}

	public String[] getCountys() {
		return countys;
	}

	public void setCountys(String[] countys) {
		this.countys = countys;
	}

	public String getFirstPY() {
		return firstPY;
	}

	public void setFirstPY(String firstPY) {
		this.firstPY = firstPY;
	}

	public String getAllPY() {
		return allPY;
	}

	public void setAllPY(String allPY) {
		this.allPY = allPY;
	}

	public String getAllFristPY() {
		return allFristPY;
	}

	public void setAllFristPY(String allFristPY) {
		this.allFristPY = allFristPY;
	}

	@Override
	public String toString() {
		return "City [id=" + id + ", city=" + name + ", countrys=" + countyStr
				+ ", firstPY=" + firstPY + ", allPY=" + allPY + ", allFristPY="
				+ allFristPY + "]";
	}

	private static String getNameAllFirstPY(String name) {
		try {
			if (name != null && name.length() != 0) {
				int len = name.length();
				char[] nums = new char[len];
				for (int i = 0; i < len; i++) {
					String tmp = name.substring(i);
					nums[i] = ToPinYin.getPinYin(tmp).charAt(0);
				}
				return new String(nums);
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return "";
	}

	private static String getNameFirstPY(String name) {
		String pinyin = "#";
		try {
			if (name != null && name.length() != 0) {
				String tmp = name.substring(0);
				pinyin = ToPinYin.getPinYin(tmp).charAt(0) + "";
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return pinyin;
	}

}
