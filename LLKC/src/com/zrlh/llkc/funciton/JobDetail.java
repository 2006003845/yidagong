package com.zrlh.llkc.funciton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.annotation.sqlite.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zzl.zl_app.connection.Protocol;
import com.zzl.zl_app.util.Secret;
import com.zzl.zl_app.util.TimeUtil;

@Table(name = "jobdetail_table2")
public class JobDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1663342049897694073L;

	public JobDetail() {

	}

	private int _id;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public JobDetail(JSONObject json) throws JSONException {
		if (json == null) {
			return;
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_rid)) {
			jobId = json.getString(Protocol.ProtocolKey.KEY_rid);
		}
		if (!json.isNull("id")) {
			jobId = json.getString("id");
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_name)) {
			name = json.getString(Protocol.ProtocolKey.KEY_name);
			name = Secret.decriptXor(name);
		}
		if (!json.isNull("job_name")) {
			name = json.getString("job_name");
			name = Secret.decriptXor(name);
		}
		if (!json.isNull("jobname")) {
			name = json.getString("jobname");
			name = Secret.decriptXor(name);
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_people)) {
			peonumber = json.getString(Protocol.ProtocolKey.KEY_people);
		}
		if (!json.isNull("peonumber")) {
			peonumber = json.getString("peonumber");
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_salary)) {
			String salary = json.getString(Protocol.ProtocolKey.KEY_salary);
			String[] salarys = salary.split("-");
			salary_min = salarys.length > 0 ? salarys[0] : "0";
			salary_max = salarys.length > 1 ? salarys[1] : "2000";
		}
		if (!json.isNull("salary_min")) {
			salary_min = json.getString("salary_min");
		}
		if (!json.isNull("salary_max")) {
			salary_max = json.getString("salary_max");
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_contacts_name)) {
			contacts_name = json
					.getString(Protocol.ProtocolKey.KEY_contacts_name);
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_tel)) {
			tel = json.getString(Protocol.ProtocolKey.KEY_tel);
			tel = Secret.decriptXor(tel);
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_class_ot)) {
			classify = json.getString(Protocol.ProtocolKey.KEY_class_ot);
		}

		// 维度
		if (!json.isNull("lat")) {
			lat = json.getString("lat");
		}
		// 经度
		if (!json.isNull("lng")) {
			lng = json.getString("lng");
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_jobdescription)) {
			description = json
					.getString(Protocol.ProtocolKey.KEY_jobdescription);
			if (description != null)
				description = description.trim();
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_description)) {
			// if (description != null)
			demand = json.getString(Protocol.ProtocolKey.KEY_description);
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_outtime)) {
			deadline = json.getString(Protocol.ProtocolKey.KEY_outtime);

			if (deadline == null || deadline.equals("")
					|| deadline.equals("null"))
				deadline = "";
			else {
				deadline = TimeUtil.getTimeStr2(Long.parseLong(deadline),
						"yyyy-MM-dd");
			}
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_tag)) {
			tags = json.getString(Protocol.ProtocolKey.KEY_tag);
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_address)) {
			address = json.getString(Protocol.ProtocolKey.KEY_address);
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_area)) {
			area = json.getString(Protocol.ProtocolKey.KEY_area);
		}
		if (!json.isNull("city")) {
			area = json.getString("city");
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_online)) {
			online = json.getString(Protocol.ProtocolKey.KEY_online);
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_fabu_date)) {
			fabu_date = json.getString(Protocol.ProtocolKey.KEY_fabu_date);
			if (fabu_date == null || fabu_date.equals("")
					|| fabu_date.equals("null"))
				fabu_date = "";
			else {
				fabu_date = TimeUtil.getTimeStr2(Long.parseLong(fabu_date),
						"yyyy-MM-dd");
			}
		}
		if (!json.isNull("jobtime")) {
			jobtime = json.getString("jobtime");
		}
		if (!json.isNull("identState")) {
			identState = json.getString("identState");
		}
		if (!json.isNull(Protocol.ProtocolKey.KEY_lastOpTime)) {
			lastOpTime = json.getString(Protocol.ProtocolKey.KEY_lastOpTime);
		}

		if (!json.isNull("corporate_name")) {
			corporate_name = json.getString("corporate_name");
			corporate_name = Secret.decriptXor(corporate_name);
		}
		if (!json.isNull("company")) {
			company = json.getString("company");
		}

	}

	public static List<JobDetail> getList(JSONArray array) throws JSONException {
		if (array == null) {
			return null;
		}
		List<JobDetail> list = new ArrayList<JobDetail>();
		for (int i = 0, len = array.length(); i < len; i++) {
			JSONObject obj = array.getJSONObject(i);
			if (obj != null) {
				JobDetail p = new JobDetail(obj);
				list.add(p);
			}
		}
		return list;
	}

	public String jobId;

	public String account;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * 岗位名称
	 */
	public String name;
	/**
	 * 招工人数
	 */
	public String peonumber;
	/**
	 * 工资最小值
	 */
	public String salary_min;
	/**
	 * 工资最大值
	 */
	public String salary_max;
	/**
	 * 单位名称
	 */
	public String corporate_name;
	/**
	 * 联系人
	 */
	public String contacts_name;
	/**
	 * 联系电话
	 */
	public String tel;
	/**
	 * 工作要求
	 */
	public String demand;
	/**
	 * 工作描述
	 */
	public String description;
	/**
	 * 工作地址
	 */
	public String address;
	/**
	 * 工作单位简介
	 */
	public String company;
	/**
	 * 经度
	 */
	public String lat;
	/**
	 * 维度
	 */
	public String lng;
	/**
	 * 发布时间
	 */
	public String fabu_date;
	/**
	 * 截止日期
	 */
	public String deadline;
	/**
	 * 工作分类
	 */
	public String classify;
	/**
	 * 工作所在城市
	 */
	public String area;
	/**
	 * 岗位标签(可以以","隔开)
	 */
	public String tags;

	/**
	 * 状态(1有效 2过期)
	 */
	public String online;

	public String lastOpTime;

	public String jobtime; // 发布时间
	public String identState;// 0未认证 1已认证

	/**
	 * 0: 浏览职位 1:联系职位 2：收藏职位 3.发布的岗位
	 */
	public String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPeonumber() {
		return peonumber;
	}

	public void setPeonumber(String peonumber) {
		this.peonumber = peonumber;
	}

	public String getSalary_min() {
		return salary_min;
	}

	public void setSalary_min(String salary_min) {
		this.salary_min = salary_min;
	}

	public String getSalary_max() {
		return salary_max;
	}

	public void setSalary_max(String salary_max) {
		this.salary_max = salary_max;
	}

	public String getCorporate_name() {
		return corporate_name;
	}

	public void setCorporate_name(String corporate_name) {
		this.corporate_name = corporate_name;
	}

	public String getContacts_name() {
		return contacts_name;
	}

	public void setContacts_name(String contacts_name) {
		this.contacts_name = contacts_name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getDemand() {
		return demand;
	}

	public void setDemand(String demand) {
		this.demand = demand;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getFabu_date() {
		return fabu_date;
	}

	public void setFabu_date(String fabu_date) {
		this.fabu_date = fabu_date;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getClassify() {
		return classify;
	}

	public void setClassify(String classify) {
		this.classify = classify;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getOnline() {
		return online;
	}

	public void setOnline(String online) {
		this.online = online;
	}

	public String getLastOpTime() {
		return lastOpTime;
	}

	public void setLastOpTime(String lastOpTime) {
		this.lastOpTime = lastOpTime;
	}

	public String getJobtime() {
		return jobtime;
	}

	public void setJobtime(String jobtime) {
		this.jobtime = jobtime;
	}

	public String getIdentState() {
		return identState;
	}

	public void setIdentState(String identState) {
		this.identState = identState;
	}

}
