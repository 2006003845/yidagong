package com.zzl.zl_app.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.zzl.zl_app.connection.Protocol;

public class PrivateMsg extends Msg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3905014065613455445L;
	/**
	 * 1:文本 2.图片 3.语音
	 */
	public String mtype;
	public String img_small;
	public String img_big;
	public String voice;
	public String length;

	public PrivateMsg() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PrivateMsg(JSONObject jsonO) throws JSONException {
		super(jsonO);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_mtype))
			mtype = jsonO.getString(Protocol.ProtocolKey.KEY_mtype);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_simg))
			img_small = jsonO.getString(Protocol.ProtocolKey.KEY_simg);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_bimg))
			img_big = jsonO.getString(Protocol.ProtocolKey.KEY_bimg);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_voice))
			voice = jsonO.getString(Protocol.ProtocolKey.KEY_voice);
		if (!jsonO.isNull("Length"))
			length = jsonO.getString("Length");

	}

	public PrivateMsg(String id, String type, String uName, String head,
			String time, String content, String uId) {
		super(id, type, uName, head, time, content, uId);

	}

}
