package com.zzl.zl_app.entity;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "mycollect_table")
public class Collect implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1663342049897694124L;

	String account;
	int id;
	String name;
	String head;
	// 收藏的时间
	String time;
	// 发布的时间
	String public_time;
	/**
	 * 1:文本 2.图片 3.语音
	 */
	String mtype;
	String content;
	String img_small;
	String img_big;
	String voice;
	String length;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMtype() {
		return mtype;
	}

	public void setMtype(String mtype) {
		this.mtype = mtype;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImg_small() {
		return img_small;
	}

	public void setImg_small(String img_small) {
		this.img_small = img_small;
	}

	public String getImg_big() {
		return img_big;
	}

	public void setImg_big(String img_big) {
		this.img_big = img_big;
	}

	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getPublic_time() {
		return public_time;
	}

	public void setPublic_time(String public_time) {
		this.public_time = public_time;
	}

}
