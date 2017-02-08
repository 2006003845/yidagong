package com.zrlh.llkc.corporate;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Periodical implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8203272343823142418L;

	public ArrayList<Novelty> noveltyList;
	public String id;
	public int page;
	public String last;
	public String date;

	public Periodical(JSONObject json) throws JSONException {
		if (!json.isNull("id"))
			id = json.getString("id");
		if (!json.isNull("page")) {
			String p = json.getString("page");
			if (p != null && !p.equals(""))
				page = Integer.parseInt(p);
		}
		if (!json.isNull("last")) {
			last = json.getString("last");
		}
		if (!json.isNull("date"))
			date = json.getString("date");
		if (!json.isNull("List")) {
			JSONArray array = json.getJSONArray("List");
			if (array != null)
				noveltyList = Novelty.getList(array);
		}
	}

	public ArrayList<Novelty> getNoveltyList() {
		return noveltyList;
	}

	public void setNoveltyList(ArrayList<Novelty> noveltyList) {
		this.noveltyList = noveltyList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public static ArrayList<Periodical> getList(JSONArray array)
			throws JSONException {
		if (array == null) {
			return null;
		}
		ArrayList<Periodical> list = new ArrayList<Periodical>();
		for (int i = 0, len = array.length(); i < len; i++) {
			JSONObject obj = array.getJSONObject(i);
			if (obj != null) {
				Periodical p = new Periodical(obj);
				list.add(p);
			}
		}
		return list;
	}

}
