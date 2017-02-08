package com.zzl.zl_app.entity;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zzl.zl_app.connection.Protocol;

public class Msg implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9151284818965386360L;

	public Msg() {

	}

	/**
	 * 
	 * @param id
	 * @param type
	 * @param uName
	 * @param head
	 * @param time
	 * @param content
	 * @param uId
	 */
	public Msg(String id, String type, String uName, String head, String time,
			String content, String uId) {
		super();
		this.id = id;
		this.type = type;
		this.senderName = uName;
		this.head = head;
		this.time = time;
		this.content = content;
		this.senderId = uId;

	}

	public Msg(JSONObject jsonO) throws JSONException {
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_Mid))
			id = jsonO.getString(Protocol.ProtocolKey.KEY_Mid);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_Type))
			type = jsonO.getString(Protocol.ProtocolKey.KEY_Type);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_Content))
			content = jsonO.getString(Protocol.ProtocolKey.KEY_Content);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_Time))
			time = jsonO.getString(Protocol.ProtocolKey.KEY_Time);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_Uid))
			senderId = jsonO.getString(Protocol.ProtocolKey.KEY_Uid);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_Uname))
			senderName = jsonO.getString(Protocol.ProtocolKey.KEY_Uname);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_Rname)) {
			sRName = jsonO.getString(Protocol.ProtocolKey.KEY_Rname);
		}
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_Head))
			head = jsonO.getString(Protocol.ProtocolKey.KEY_Head);
	}

	public int _id;
	public String id;
	public String type;
	public String content;
	public String time;
	public String senderId;
	public String senderName;
	public String sRName;
	public String head;
	public int newitems;
	public int state;

	public static ArrayList<Msg> getMsgList(JSONArray array)
			throws JSONException {
		int size = array.length();
		ArrayList<Msg> os = new ArrayList<Msg>(size);
		for (int i = 0; i < size; i++) {
			JSONObject jsonO = array.getJSONObject(i);
			Msg u = new Msg(jsonO);
			os.add(u);
		}
		return os;
	}

}
