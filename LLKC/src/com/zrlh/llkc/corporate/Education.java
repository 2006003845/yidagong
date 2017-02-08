package com.zrlh.llkc.corporate;

import org.json.JSONException;
import org.json.JSONObject;

public class Education extends Obj {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6101657206781009316L;

	public Education(JSONObject json) throws JSONException {
		super(json);
		// TODO Auto-generated constructor stub
	}

	public Education(String id, String name) {
		super(id, name);
		// TODO Auto-generated constructor stub
	}

	public Education(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

}
