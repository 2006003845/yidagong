package com.zzl.zl_app.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestMsg extends Msg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2188973818187469413L;

	public RequestMsg() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RequestMsg(JSONObject jsonO) throws JSONException {
		super(jsonO);
		// TODO Auto-generated constructor stub
	}

	public RequestMsg(String id, String type, String uName, String head,
			String time, String content, String uId) {
		super(id, type, uName, head, time, content, uId);
		// TODO Auto-generated constructor stub
	}

}
