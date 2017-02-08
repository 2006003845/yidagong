package com.zrlh.llkc.funciton;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class Authentication implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1663342049897694073L;
	
	public Authentication(JSONObject json) throws JSONException{
		if (json == null) {
			return;
		}
		if (!json.isNull("name")) {
			name = json.getString("name");
		}
		if (!json.isNull("phone")) {
			phone = json.getString("phone");
		}
		if (!json.isNull("address")) {
			address = json.getString("address");
		}
		if (!json.isNull("intro")) {
			intro = json.getString("intro");
		}
		if (!json.isNull("auth")) {
			imgurl = json.getString("auth");
		}
	}
	
	public String account;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	
	public String name;
	public String phone;
	public String address;
	public String intro;
	public String imgurl;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}
	public String getImgUrl() {
		return imgurl;
	}

	public void setImgUrl(String imgurl) {
		this.imgurl = imgurl;
	}

}
