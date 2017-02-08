package com.zrlh.llkc.joggle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.tsz.afinal.annotation.sqlite.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zrlh.llkc.funciton.HeadWall;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.connection.Protocol;

@Table(name = "userinfo")
public class Account implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1428391373352119361L;
	int id;
	String arccount; // 账户
	String prassWord;// 密码
	String aid;// 账户ID
	String uid;// 角色ID
	String loginStatus; // 登陆状态 1 自动 0 手动
	String uname;// 昵称
	String nname;// 新昵称
	String sex;// 性别
	String local;// 所在地
	String sign;// 个人签名
	String birth;// 生日
	String age;
	String email;// 邮箱
	String phone;// 电话
	String head;// 好友头像资源
	String backk;// 好友空间背景
	String prof;// 专业
	String cert;// 证书
	int score = 100;
	public List<HeadWall> headWall;

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

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

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(String loginStatus) {
		this.loginStatus = loginStatus;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getNname() {
		return nname;
	}

	public void setNname(String nname) {
		this.nname = nname;
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

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getBackk() {
		return backk;
	}

	public void setBackk(String backk) {
		this.backk = backk;
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

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Account() {
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

	public Account(JSONObject json) throws JSONException {
		arccount = LlkcBody.USER_ACCOUNT;
		prassWord = LlkcBody.PASS_ACCOUNT;
		if (json == null) {
			return;
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_Uid)) {
			uid = json.getString(Protocol.ProtocolKey.KEY_Uid);
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_Uname)) {
			uname = json.getString(Protocol.ProtocolKey.KEY_Uname);
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
		if (!json.isNull("Back")) {
			backk = json.getString("Back");
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
		if (!json.isNull("HeadJson")) {
			headWall = getHeadWallList(json.getJSONArray("HeadJson"));
		}
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

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public List<HeadWall> getHeadWall() {
		return headWall;
	}

	public void setHeadWall(List<HeadWall> headWall) {
		this.headWall = headWall;
	}

}
