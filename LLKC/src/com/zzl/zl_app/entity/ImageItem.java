package com.zzl.zl_app.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zzl.zl_app.connection.Protocol;

public class ImageItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5209931524475857642L;

	/**
	 * 名称
	 */
	public String name;
	/**
	 * 类型 1：岗位2：活动
	 */
	public String type;

	/**
	 * 图片地址
	 */
	public String img;

	/**
	 * 对应岗位ID
	 */
	public String reid;
	/**
	 * 城市
	 */
	public String city;
	/**
	 * 活动页面地址
	 */
	public String url;

	public ImageItem(JSONObject jsonO) throws JSONException {

		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_name))
			name = jsonO.getString(Protocol.ProtocolKey.KEY_name);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_type))
			type = jsonO.getString(Protocol.ProtocolKey.KEY_type);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_img))
			img = jsonO.getString(Protocol.ProtocolKey.KEY_img);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_reid))
			reid = jsonO.getString(Protocol.ProtocolKey.KEY_reid);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_city))
			city = jsonO.getString(Protocol.ProtocolKey.KEY_city);
		if (!jsonO.isNull(Protocol.ProtocolKey.KEY_url)) {
			url = jsonO.getString(Protocol.ProtocolKey.KEY_url);
		}

	}

	public static ArrayList<ImageItem> getItemList(JSONArray array)
			throws JSONException {
		ArrayList<ImageItem> list = new ArrayList<ImageItem>();
		for (int i = 0, len = array.length(); i < len; i++) {
			JSONObject obj = array.getJSONObject(i);
			if (obj != null) {
				ImageItem p = new ImageItem(obj);
				list.add(p);
			}
		}
		return list;
	}
}
