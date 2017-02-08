package com.zzl.zl_app.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.zzl.zl_app.connection.Protocol;

public class GroupMsg extends Msg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1816353119637707205L;

	public String gId;
	public String gName;
	public String gHead;

	public String img_small;
	public String img_big;
	public String voice;
	public String mtype;
	public String length;

	public GroupMsg() {
		super();
	}

	public GroupMsg(JSONObject jsonO) throws JSONException {
		super(jsonO);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_Gid))
			gId = jsonO.getString(Protocol.ProtocolKey.KEY_Gid);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_Gname))
			gName = jsonO.getString(Protocol.ProtocolKey.KEY_Gname);
		if (!jsonO.isNull("Ghead"))
			gHead = jsonO.getString("Ghead");
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

	public GroupMsg(String id, String type, String uName, String head,
			String time, String content, String uId) {
		super(id, type, uName, head, time, content, uId);
		gId = id;
	}

}
