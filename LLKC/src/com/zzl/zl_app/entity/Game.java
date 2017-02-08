package com.zzl.zl_app.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.annotation.sqlite.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Table(name = "game_tab")
public class Game implements Serializable {

	public Game() {
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7921984533892606444L;
	/**
	 * 游戏索引
	 */
	public String id;
	/**
	 * 游戏类型
	 */
	public String type;
	/**
	 * 游戏名称
	 */
	public String name;
	/**
	 * 游戏packname
	 */
	public String pack;
	/**
	 * :游戏icon
	 */
	public String icon;
	/**
	 * ：游戏版本
	 */
	public String ver;
	/**
	 * 游戏公司
	 */
	public String comp;

	/**
	 * 游戏简介
	 */
	public String intro;

	/**
	 * 游戏截图
	 * 
	 */
	public String[] imgs = {};

	/**
	 * 游戏大小
	 */
	public String size;

	/**
	 * 游戏下载路径
	 */
	public String down;

	/**
	 * 游戏排名
	 */
	public String rank;

	/**
	 * 一起玩的好友数
	 */
	public String playerNum;

	public int fileSize = 1;
	public int progress = 0;
	public boolean isStartLoad = false;
	public boolean isLoading = false;

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public boolean isStartLoad() {
		return isStartLoad;
	}

	public void setStartLoad(boolean isStartLoad) {
		this.isStartLoad = isStartLoad;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public Game(JSONObject jsonO) throws JSONException {
		if (!jsonO.isNull("id"))
			id = jsonO.getString("id");
		if (!jsonO.isNull("name"))
			name = jsonO.getString("name");
		if (!jsonO.isNull("type"))
			type = jsonO.getString("type");
		if (!jsonO.isNull("pack"))
			pack = jsonO.getString("pack");
		if (!jsonO.isNull("icon"))
			icon = jsonO.getString("icon");
		if (!jsonO.isNull("ver"))
			ver = jsonO.getString("ver");
		if (!jsonO.isNull("comp"))
			comp = jsonO.getString("comp");
		if (!jsonO.isNull("intro"))
			intro = jsonO.getString("intro");
		if (!jsonO.isNull("imgs")) {
			String ss = jsonO.getString("imgs");
			if (ss != null && !ss.equals(""))
				imgs = ss.split("\\|");
		}
		if (!jsonO.isNull("size"))
			size = jsonO.getString("size");
		if (!jsonO.isNull("down"))
			down = jsonO.getString("down");
		if (!jsonO.isNull("friendRanking"))
			rank = jsonO.getString("friendRanking");
		if (!jsonO.isNull("playNum"))
			playerNum = jsonO.getString("playNum");
	}

	public static List<Game> getList(JSONArray array) throws JSONException {
		if (array == null) {
			return null;
		}
		List<Game> list = new ArrayList<Game>();
		for (int i = 0, len = array.length(); i < len; i++) {
			JSONObject obj = array.getJSONObject(i);
			if (obj != null) {
				Game p = new Game(obj);
				list.add(p);
			}
		}
		return list;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPack() {
		return pack;
	}

	public void setPack(String pack) {
		this.pack = pack;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getComp() {
		return comp;
	}

	public void setComp(String comp) {
		this.comp = comp;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String[] getImgs() {
		return imgs;
	}

	public void setImgs(String[] imgs) {
		this.imgs = imgs;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getDown() {
		return down;
	}

	public void setDown(String down) {
		this.down = down;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getPlayerNum() {
		return playerNum;
	}

	public void setPlayerNum(String playerNum) {
		this.playerNum = playerNum;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

}
