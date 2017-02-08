package com.zrlh.llkc.corporate;

import org.json.JSONException;
import org.json.JSONObject;

public class Gender extends Obj {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6330237437222361508L;

	public Gender(JSONObject json) throws JSONException {
		super(json);
		// TODO Auto-generated constructor stub
	}

	public Gender(String id, String name) {
		super(id, name);
		// TODO Auto-generated constructor stub
	}

	public Gender(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

}
