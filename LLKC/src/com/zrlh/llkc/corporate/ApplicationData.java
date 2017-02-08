package com.zrlh.llkc.corporate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zrlh.llkc.funciton.Friend;
import com.zrlh.llkc.funciton.Group;
import com.zzl.zl_app.apk.load.Downloader;
import com.zzl.zl_app.entity.ImageItem;
import com.zzl.zl_app.entity.Msg;

/*
 * 应用程序数据
 * 
 * */
public class ApplicationData {

	public static List<Downloader> downloaderList = new ArrayList<Downloader>();

	public static List<ImageItem> imgItemList = new ArrayList<ImageItem>();

	public static List<City> mCityList = new ArrayList<City>();
	// 首字母集
	public static List<String> mSections = new ArrayList<String>();
	// 根据首字母存放数据
	public static Map<String, List<City>> mMap = new HashMap<String, List<City>>();
	// 首字母位置集
	public static List<Integer> mPositions = new ArrayList<Integer>();
	// 首字母对应的位置
	public static Map<String, Integer> mIndexer = new HashMap<String, Integer>();

	public static String[] keywords;

	public static ArrayList<HashMap<Obj, ArrayList<Obj>>> typeLists = new ArrayList<HashMap<Obj, ArrayList<Obj>>>();
	public static List<Obj> recommendTypeList = new ArrayList<Obj>();

	public static City getCity(String cName) {
		if (cName == null)
			return null;
		for (City c : mCityList) {
			if (c == null || c.name == null)
				continue;
			if ((c.name.contains(cName) || cName.contains(c.name))
					&& c.id == null) {
				return c;
			}
		}
		return null;
	}

	public static Obj findType(String id) {
		if (id == null || id.equals("")) {
			return null;
		}
		for (HashMap<Obj, ArrayList<Obj>> map : typeLists) {
			Set<Obj> sets = map.keySet();
			Obj obj = sets.iterator().next();
			if (obj != null && obj.id.equals(id)) {
				return obj;
			}
		}
		return null;
	}

	public static ArrayList<Obj> findTypeList(String oneclassId) {
		if (oneclassId == null || oneclassId.equals("")) {
			return null;
		}
		for (HashMap<Obj, ArrayList<Obj>> map : typeLists) {
			Set<Obj> sets = map.keySet();
			Obj obj = sets.iterator().next();
			if (obj != null && obj.id.equals(oneclassId)) {
				return map.get(obj);
			}
		}
		return null;
	}

	public static ArrayList<Obj> findTypeList(Obj type) {
		if (type == null) {
			return null;
		}
		for (HashMap<Obj, ArrayList<Obj>> map : typeLists) {
			Set<Obj> sets = map.keySet();
			Obj obj = sets.iterator().next();
			if (obj != null && obj.id.equals(type.id)) {
				return map.get(obj);
			}
		}
		return null;
	}

	public static Obj findType2(Obj obj, String id) {
		if (obj == null) {
			return null;
		}
		if (id == null || id.equals("")) {
			return null;
		}
		for (HashMap<Obj, ArrayList<Obj>> map : typeLists) {
			ArrayList<Obj> list = map.get(obj);
			if (list == null) {
				continue;
			}
			for (Obj obj2 : list) {
				if (obj2 == null)
					continue;
				if (obj2.id.equals(id)) {
					return obj2;
				}
			}
		}
		return null;
	}

	public static List<Resume> resumeList = new ArrayList<Resume>();
	public static List<Friend> friendList = new ArrayList<Friend>();
	public static List<Group> selfGroupList = new ArrayList<Group>();
	public static List<Group> joinGroupList = new ArrayList<Group>();
	public static List<Group> allGroupList = new ArrayList<Group>();

	public static List<Msg> msgList = new ArrayList<Msg>();
	public static List<Msg> requestMsgList = new ArrayList<Msg>();
	public static List<Msg> sysMsgList = new ArrayList<Msg>();

	public static void clearUserData() {
		msgList.clear();
		requestMsgList.clear();
		sysMsgList.clear();
		resumeList.clear();
		friendList.clear();
		selfGroupList.clear();
		joinGroupList.clear();
		allGroupList.clear();
		downloaderList.clear();
	}

	public static void clear() {
		clearUserData();
		imgItemList.clear();

	}

}
