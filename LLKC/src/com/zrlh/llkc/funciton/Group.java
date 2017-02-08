package com.zrlh.llkc.funciton;

import java.io.Serializable;
import java.util.ArrayList;

import net.tsz.afinal.annotation.sqlite.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Table(name = "group_tab2")
public class Group implements Serializable {

	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 
	 */
	public static final long serialVersionUID = 8779962792331071944L;

	public Group() {
	}

	public Group(JSONObject jsonO) throws JSONException {
		if (!jsonO.isNull("gId"))
			gId = jsonO.getString("gId");
		if (!jsonO.isNull("gName"))
			gName = jsonO.getString("gName");
		if (!jsonO.isNull("gHead"))
			gHead = jsonO.getString("gHead");
		if (!jsonO.isNull("gManagerId"))
			gManagerId = jsonO.getString("gManagerId");
		if (!jsonO.isNull("gManagerName"))
			gManagerName = jsonO.getString("gManagerName");
		if (!jsonO.isNull("gType"))
			gType = jsonO.getString("gType");
		if (!jsonO.isNull("gMembers"))
			gMembers = jsonO.getString("gMembers");
		if (!jsonO.isNull("gMembersMax"))
			gMembersMax = jsonO.getString("gMembersMax");
		if (!jsonO.isNull("gLevel"))
			gLevel = jsonO.getString("gLevel");
		if (!jsonO.isNull("gAddress"))
			gAddress = jsonO.getString("gAddress");
		if (!jsonO.isNull("gContent"))
			gContent = jsonO.getString("gContent");
		if (!jsonO.isNull("gCreateTime"))
			gCreateTime = jsonO.getString("gCreateTime");
		if (!jsonO.isNull("gPower"))
			gPower = jsonO.getString("gPower");
		if (!jsonO.isNull("length"))
			gDistance = jsonO.getString("length");
	}

	public static ArrayList<Group> getFGroupList(JSONArray array)
			throws JSONException {
		int size = array.length();
		ArrayList<Group> os = new ArrayList<Group>();
		for (int i = 0; i < size; i++) {
			JSONObject jsonO = array.getJSONObject(i);
			Group u = new Group(jsonO);
			os.add(u);
		}
		return os;
	}

	/**
	 * 群id
	 */
	public String gId;
	/**
	 * 群名称
	 */
	public String gName;
	/**
	 * 群头像
	 */
	public String gHead;
	/**
	 * 群管理员Id
	 */
	public String gManagerId;
	/**
	 * 群管理员名字
	 */
	public String gManagerName;
	/**
	 * 群分类
	 */
	public String gType;
	/**
	 * 群成员当前数量
	 */
	public String gMembers;
	/**
	 * 群成员最大数量
	 */
	public String gMembersMax;
	/**
	 * 群等级
	 */
	public String gLevel;
	/**
	 * 群地址
	 */
	public String gAddress;
	/**
	 * 群简介
	 */
	public String gContent;
	/**
	 * 去创建时间
	 */
	public String gCreateTime;

	/**
	 * 距离
	 */
	public String gDistance;

	/**
	 * 群权限
	 */
	public String gPower;

	public String account;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getgId() {
		return gId;
	}

	public void setgId(String gId) {
		this.gId = gId;
	}

	public String getgName() {
		return gName;
	}

	public void setgName(String gName) {
		this.gName = gName;
	}

	public String getgHead() {
		return gHead;
	}

	public void setgHead(String gHead) {
		this.gHead = gHead;
	}

	public String getgManagerId() {
		return gManagerId;
	}

	public void setgManagerId(String gManagerId) {
		this.gManagerId = gManagerId;
	}

	public String getgManagerName() {
		return gManagerName;
	}

	public void setgManagerName(String gManagerName) {
		this.gManagerName = gManagerName;
	}

	public String getgType() {
		return gType;
	}

	public void setgType(String gType) {
		this.gType = gType;
	}

	public String getgDistance() {
		return gDistance;
	}

	public void setgDistance(String gDistance) {
		this.gDistance = gDistance;
	}

	public String getgMembers() {
		return gMembers;
	}

	public void setgMembers(String gMembers) {
		this.gMembers = gMembers;
	}

	public String getgMembersMax() {
		return gMembersMax;
	}

	public void setgMembersMax(String gMembersMax) {
		this.gMembersMax = gMembersMax;
	}

	public String getgLevel() {
		return gLevel;
	}

	public void setgLevel(String gLevel) {
		this.gLevel = gLevel;
	}

	public String getgAddress() {
		return gAddress;
	}

	public void setgAddress(String gAddress) {
		this.gAddress = gAddress;
	}

	public String getgContent() {
		return gContent;
	}

	public void setgContent(String gContent) {
		this.gContent = gContent;
	}

	public String getgCreateTime() {
		return gCreateTime;
	}

	public void setgCreateTime(String gCreateTime) {
		this.gCreateTime = gCreateTime;
	}

	public String getgPower() {
		return gPower;
	}

	public void setgPower(String gPower) {
		this.gPower = gPower;
	}

}
