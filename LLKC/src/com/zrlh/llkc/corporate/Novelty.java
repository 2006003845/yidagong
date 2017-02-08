package com.zrlh.llkc.corporate;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Novelty implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2755813894443127110L;

	public String id;
	public String name;
	public String img;
	public String url;

	public Novelty(JSONObject json) throws JSONException {
		if (!json.isNull("id"))
			id = json.getString("id");
		if (!json.isNull("name"))
			name = json.getString("name");
		if (!json.isNull("img")) {
			img = json.getString("img");
		}
		if (!json.isNull("url"))
			url = json.getString("url");
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static ArrayList<Novelty> getList(JSONArray array)
			throws JSONException {
		if (array == null) {
			return null;
		}
		ArrayList<Novelty> list = new ArrayList<Novelty>();
		for (int i = 0, len = array.length(); i < len; i++) {
			JSONObject obj = array.getJSONObject(i);
			if (obj != null) {
				Novelty p = new Novelty(obj);
				list.add(p);
			}
		}
		return list;
	}

}
