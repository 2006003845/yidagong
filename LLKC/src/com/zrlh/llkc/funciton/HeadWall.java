package com.zrlh.llkc.funciton;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class HeadWall implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7949866288494091231L;

	public HeadWall(JSONObject json) throws JSONException {
		if (json == null) {
			return;
		}
		if (!json.isNull("id")) {
			headId = json.getString("id");
		}
		if (!json.isNull("bimg")) {
			headBimg = json.getString("bimg");
		}
		if (!json.isNull("simg")) {
			headSimg = json.getString("simg");
		}
	}
	
	public String headId;
	public String headBimg;
	public String headSimg;
	
	public String getHeadId() {
		return headId;
	}
	public void setHeadId(String headId) {
		this.headId = headId;
	}
	public String getHeadBimg() {
		return headBimg;
	}
	public void setHeadBimg(String headBimg) {
		this.headBimg = headBimg;
	}
	public String getHeadSimg() {
		return headSimg;
	}
	public void setHeadSimg(String headSimg) {
		this.headSimg = headSimg;
	}
}
