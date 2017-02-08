package com.zrlh.llkc.corporate;

import org.json.JSONException;
import org.json.JSONObject;

public class Deadline extends Obj {

	/**
	 * 截止日期
	 */
	private static final long serialVersionUID = 6347133225051676837L;

	public String months;

	public Deadline(JSONObject json) throws JSONException {
		super(json);
		// TODO Auto-generated constructor stub
	}

	public Deadline(String id, String name) {
		super(id, name);
		// TODO Auto-generated constructor stub
	}

	public Deadline(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

}
