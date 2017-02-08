package com.zrlh.llkc.corporate;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class Obj implements Serializable {
	/**
	 * 对象
	 */
	private static final long serialVersionUID = 4961113952214955693L;
	public String id;
	public String name;

	public Obj(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Obj(String name) {
		super();
		this.name = name;
	}

	public Obj(JSONObject json) throws JSONException {
		if (json != null) {
			if (!json.isNull("id"))
				id = json.getString("id");
			if (!json.isNull("name"))
				name = json.getString("name");
		}

	}

}
