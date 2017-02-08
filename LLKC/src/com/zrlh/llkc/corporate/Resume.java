package com.zrlh.llkc.corporate;

import java.io.Serializable;
import java.util.ArrayList;

import net.tsz.afinal.annotation.sqlite.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Table(name = "resume_tab")
public class Resume implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8556080089673121809L;

	/**
	 * 简历id
	 */
	public String id;
	/**
	 * 简历名称
	 */
	public String name;
	/**
	 * 头像
	 */
	public String head;
	/**
	 * 真实姓名
	 */
	public String rName;
	/**
	 * 性别
	 */
	public String sex;
	/**
	 * 生日
	 */
	public String birth;
	/**
	 * 学历
	 */
	public String education;
	/**
	 * 工作经验
	 */
	public String workExperience;
	/**
	 * 电话
	 */
	public String phone;
	/**
	 * 期望岗位
	 */
	public String expectPost;
	/**
	 * 期望岗位ID
	 */
	public String expectPostId;
	/**
	 * 期望薪资
	 */
	public String expectSalary;
	/**
	 * 自我评价
	 */
	public String evaluation;
	/**
	 * 创建时间
	 */
	public String time;

	public String getExpectPostId() {
		return expectPostId;
	}

	public void setExpectPostId(String expectPostId) {
		this.expectPostId = expectPostId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getrName() {
		return rName;
	}

	public void setrName(String rName) {
		this.rName = rName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getWorkExperience() {
		return workExperience;
	}

	public void setWorkExperience(String workExperience) {
		this.workExperience = workExperience;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getExpectPost() {
		return expectPost;
	}

	public void setExpectPost(String expectPost) {
		this.expectPost = expectPost;
	}

	public String getExpectSalary() {
		return expectSalary;
	}

	public void setExpectSalary(String expectSalary) {
		this.expectSalary = expectSalary;
	}

	public String getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(String evaluation) {
		this.evaluation = evaluation;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Resume() {
	}

	public Resume(JSONObject json) throws JSONException {
		if (json != null) {

			if (!json.isNull("rId"))
				id = json.getString("rId");
			if (!json.isNull("resumeName"))
				name = json.getString("resumeName");
			if (!json.isNull("photo"))
				head = json.getString("photo");
			if (!json.isNull("name"))
				rName = json.getString("name");
			if (!json.isNull("sex"))
				sex = json.getString("sex");
			if (!json.isNull("dateOfBirth"))
				birth = json.getString("dateOfBirth");
			if (!json.isNull("education"))
				education = json.getString("education");
			if (!json.isNull("workExperience"))
				workExperience = json.getString("workExperience");
			if (!json.isNull("phoneNumber"))
				phone = json.getString("phoneNumber");
			if (!json.isNull("position"))
				expectPost = json.getString("position");
			if (!json.isNull("positionId"))
				expectPostId = json.getString("positionId");
			if (!json.isNull("salary"))
				expectSalary = json.getString("salary");
			if (!json.isNull("evaluation"))
				evaluation = json.getString("evaluation");
			if (!json.isNull("createTime"))
				time = json.getString("createTime");
		}

	}

	public static ArrayList<Resume> getResumeList(JSONArray array)
			throws JSONException {
		ArrayList<Resume> list = new ArrayList<Resume>();
		for (int i = 0, len = array.length(); i < len; i++) {
			JSONObject obj = array.getJSONObject(i);
			if (obj != null) {
				Resume p = new Resume(obj);
				list.add(p);
			}
		}
		return list;
	}
}
