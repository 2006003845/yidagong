package com.zzl.zl_app.net_port;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import com.zrlh.llkc.corporate.City;
import com.zrlh.llkc.corporate.Obj;

public interface IGet2Api {

	public static int From_Assert = 1;
	public static int From_Net = 2;
	public static int From_SDCard = 3;

	List<City> getCityList(int from, String fileName) throws WSError, JSONException;

	ArrayList<HashMap<Obj, ArrayList<Obj>>> getTypeMapList(int from,
			String fileName) throws WSError, JSONException;
	
	String[] getKeyWord(int from, String fileName) throws WSError, JSONException;

}
