package com.zzl.zl_app.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class MMsg extends Msg {

	public static final String MSG_SYS = "3";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2967313722134041917L;

	public String label;
	public String lName;

	public MMsg() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MMsg(JSONObject jsonO) throws JSONException {
		super(jsonO);
		// TODO Auto-generated constructor stub
	}

	public MMsg(String id, String label, String type, String uName,
			String head, String time, String content, String uId) {
		super(id, type, uName, head, time, content, uId);
		this.label = label;
	}

}
