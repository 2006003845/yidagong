package com.zzl.zl_app.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class SysMsg extends Msg {
	
	public static final String MSG_SYS = "3";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2967313722134041917L;

	public SysMsg() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SysMsg(JSONObject jsonO) throws JSONException {
		super(jsonO);
		// TODO Auto-generated constructor stub
	}

	public SysMsg(String id, String type, String uName, String head,
			String time, String content, String uId) {
		super(id, type, uName, head, time, content, uId);
		// TODO Auto-generated constructor stub
	}

}
