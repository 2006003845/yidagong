package com.zrlh.llkc.funciton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import net.tsz.afinal.annotation.sqlite.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zzl.zl_app.connection.Protocol;

/*
 * 好友
 * */

@Table(name = "friend_table")
public class Friend implements Serializable {

	public static ArrayList<Friend> getFriendList(JSONArray array)
			throws JSONException {
		int size = array.length();
		ArrayList<Friend> os = new ArrayList<Friend>();
		for (int i = 0; i < size; i++) {
			JSONObject jsonO = array.getJSONObject(i);
			Friend u = new Friend(jsonO);
			os.add(u);
		}
		return os;
	}

	public static ArrayList<HeadWall> getHeadWallList(JSONArray array)
			throws JSONException {
		int size = array.length();
		ArrayList<HeadWall> os = new ArrayList<HeadWall>();
		for (int i = 0; i < size; i++) {
			JSONObject jsonO = array.getJSONObject(i);
			HeadWall u = new HeadWall(jsonO);
			os.add(u);
		}
		return os;
	}

	/**
		 * 
		 */
	private static final long serialVersionUID = -7949866288494091141L;

	public Friend() {

	}

	/**
	 * Uid Uname Sex Head Sign
	 */
	public Friend(JSONObject json) throws JSONException {
		account = LlkcBody.USER_ACCOUNT;
		if (json == null) {
			return;
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_Uid)) {
			uid = json.getString(Protocol.ProtocolKey.KEY_Uid);
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_Uname)) {
			uname = json.getString(Protocol.ProtocolKey.KEY_Uname);
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_Rname)) {
			rname = json.getString(Protocol.ProtocolKey.KEY_Rname);
		}
		if (!json.isNull("Sex")) {
			sex = json.getString("Sex");
		}
		if (!json.isNull("Email")) {
			email = json.getString("Email");
		}
		if (!json.isNull("Local")) {
			local = json.getString("Local");
		}
		if (!json.isNull("Phone")) {
			phone = json.getString("Phone");
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_Head)) {
			head = json.getString(Protocol.ProtocolKey.KEY_Head);
		}
		if (!json.isNull("Birth")) {
			birth = json.getString("Birth");
		}
		if (!json.isNull("Age")) {
			age = json.getString("Age");
		}
		if (!json.isNull("Length")) {
			distance = json.getString("Length");
		}
		if (!json.isNull("Back")) {
			back = json.getString("Back");
		}
		if (!json.isNull("Sign")) {
			sign = json.getString("Sign");
		}
		if (!json.isNull("Prof")) {
			prof = json.getString("Prof");
		}
		if (!json.isNull("Cert")) {
			cert = json.getString("Cert");
		}
		if (!json.isNull("DynamicsId")) {
			dynamic_Id = json.getString("DynamicsId");
		}
		if (!json.isNull("Content")) {
			dynamic_content = json.getString("Content");
		}
		if (!json.isNull("Bimg")) {
			String str = json.getString("Bimg");
			String[] url = str.split("\\|");
			dynamic_bigImg = url[0];
		}
		if (!json.isNull("Simg")) {
			String str = json.getString("Simg");
			String[] url = str.split("\\|");
			dynamic_smallImg = url[0];
		}
		if (!json.isNull("Time")) {
			dynamic_time = json.getString("Time");
		}
		if (!json.isNull("Size")) {
			dynamic_size = json.getString("Size");
		}
		if (!json.isNull("HeadJson")) {
			headWall = getHeadWallList(json.getJSONArray("HeadJson"));
		}

	}

	public boolean isChecked = false;

	int id;
	String account; // 账户
	String uid;// 角色ID
	String uname;// 昵称
	String rname;// 真实姓名
	String head;// 好友头像资源
	String back;// 好友空间背景
	String sex;// 性别
	String local;// 所在地
	String sign;// 个人签名
	String birth;// 生日
	String age;// 年龄
	String distance;// 距离
	String email;// 邮箱
	String phone;// 电话
	String prof;// 专业
	String cert;// 证书

	String dynamic_Id;
	String dynamic_smallImg;
	String dynamic_bigImg;
	String dynamic_content;
	String dynamic_time;
	String dynamic_size;
	
	public ArrayList<HeadWall> headWall;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getRname() {
		return rname;
	}

	public void setRname(String rname) {
		this.rname = rname;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getBack() {
		return back;
	}

	public void setBack(String back) {
		this.back = back;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getProf() {
		return prof;
	}

	public void setProf(String prof) {
		this.prof = prof;
	}

	public String getCert() {
		return cert;
	}

	public void setCert(String cert) {
		this.cert = cert;
	}

	public String getDynamicSmallImg() {
		return dynamic_smallImg;
	}

	public void setDynamicSmallImg(String dynamic_smallImg) {
		this.dynamic_smallImg = dynamic_smallImg;
	}

	public String getDynamicBigImg() {
		return dynamic_bigImg;
	}

	public void setDynamicBigImg(String dynamic_bigImg) {
		this.dynamic_bigImg = dynamic_bigImg;
	}

	public String getDynamicContent() {
		return dynamic_content;
	}

	public void setDynamicContent(String dynamic_content) {
		this.dynamic_content = dynamic_content;
	}

	public String getDynamicSize() {
		return dynamic_size;
	}

	public void setDynamicSize(String dynamic_size) {
		this.dynamic_size = dynamic_size;
	}

	public String getDynamicTime() {
		return dynamic_time;
	}

	public void setDynamicTime(String dynamic_time) {
		this.dynamic_time = dynamic_time;
	}

	public String getAge(String date) {
		if (date == null || date.equals(""))
			return "";
		String[] ds = date.split("-");
		if (ds.length > 0) {
			int year = Integer.parseInt(ds[0]);
			Date d = new Date();
			int nowy = d.getYear() + 1900;
			return (nowy - year) + "";
		}
		return "";
	}

	public String getDynamic_Id() {
		return dynamic_Id;
	}

	public void setDynamic_Id(String dynamic_Id) {
		this.dynamic_Id = dynamic_Id;
	}

	public ArrayList<HeadWall> getHeadWall() {
		return headWall;
	}

	public void setHeadWall(ArrayList<HeadWall> headWall) {
		this.headWall = headWall;
	}


}
