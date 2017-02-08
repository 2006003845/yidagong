package com.zrlh.llkc.joggle;

import java.io.Serializable;

public class User implements Serializable{
int id;
String arccount; //账户
String prassWord;//密码
String loginStatus; //登陆状态 1 自动 0 手动
String uid;//角色ID
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getArccount() {
	return arccount;
}
public void setArccount(String arccount) {
	this.arccount = arccount;
}
public String getPrassWord() {
	return prassWord;
}
public void setPrassWord(String prassWord) {
	this.prassWord = prassWord;
}
public String getLoginStatus() {
	return loginStatus;
}
public void setLoginStatus(String loginStatus) {
	this.loginStatus = loginStatus;
}
public String getUid() {
	return uid;
}
public void setUid(String uid) {
	this.uid = uid;
}

}
