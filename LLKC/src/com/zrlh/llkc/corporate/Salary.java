package com.zrlh.llkc.corporate;

import org.json.JSONException;
import org.json.JSONObject;
/*
 * 工资
 * */
public class Salary extends Obj {

	public Salary(JSONObject json) throws JSONException {
		super(json);
		// TODO Auto-generated constructor stub
	}

	public Salary(String id, String name) {
		super(id, name);
		// TODO Auto-generated constructor stub
	}

	public Salary(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3512100121081824857L;

}
