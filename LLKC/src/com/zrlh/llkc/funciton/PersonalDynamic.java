package com.zrlh.llkc.funciton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.annotation.sqlite.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
@Table(name = "dynamic_table")
public class PersonalDynamic implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1663342049897694183L;
	
	public PersonalDynamic(){
		
	}
	
	public String account;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	int id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public PersonalDynamic(JSONObject json) throws JSONException {
		if (json == null) {
			return;
		}
		if (!json.isNull("Id")) {
			dynamicId = json.getString("Id");
		}
		
		if (!json.isNull("DynamicsId")) {
			dynamicId = json.getString("DynamicsId");
		}
		
		if (!json.isNull("Content")) {
			content = json.getString("Content");
//			content = URLDecoder.decode(content);
		}
		if (!json.isNull("Bimg")) {
			String str = json.getString("Bimg");
			String[] url = str.split("\\|");
			bigImg = url[0];
		}
		if (!json.isNull("Simg")) {
			String str = json.getString("Simg");
			String[] url = str.split("\\|");
			smallImg = url[0];
		}
		if (!json.isNull("Time")) {
			time = json.getString("Time");
		}
	}
	
	public static List<PersonalDynamic> getList(JSONArray array) throws JSONException {
		if (array == null) {
			return null;
		}
		List<PersonalDynamic> list = new ArrayList<PersonalDynamic>();
		for (int i = 0, len = array.length(); i < len; i++) {
			JSONObject obj = array.getJSONObject(i);
			if (obj != null) {
				PersonalDynamic p = new PersonalDynamic(obj);
				list.add(p);
			}
		}
		return list;
	}
	
	public String dynamicId;
	
	public String content;
	
	public String bigImg;//图片地址
	public String smallImg;//图片地址
	
	public String time;
	
	public String getDynamicId() {
		return dynamicId;
	}

	public void setDynamicId(String dynamicId) {
		this.dynamicId = dynamicId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getBigImg() {
		return bigImg;
	}

	public void setBigImg(String bigImg) {
		this.bigImg = bigImg;
	}

	public String getSmallImg() {
		return smallImg;
	}

	public void setSmallImg(String smallImg) {
		this.smallImg = smallImg;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
