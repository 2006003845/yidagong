package com.zrlh.llkc.funciton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zzl.zl_app.connection.Protocol;

/*
 * 工
 *
 * */
public class Jobs {
	public static class JobNew implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3591531657221288591L;
		String jobid; // 信息id（用户查询详情）
		String salary_min;// 最小薪资
		String salary_max;// 最大薪资
		String jobname; // 招工名称
		String peonumber; // 人数
		String address; // 地址
		String jobtime; // 发布时间
		String cityString;
		String identState;// 0未认证 1已认证

		public String getIdentState() {
			return identState;
		}

		public void setIdentState(String identState) {
			this.identState = identState;
		}

		public String getCityString() {
			return cityString;
		}

		public void setCityString(String cityString) {
			this.cityString = cityString;
		}

		public String getJobid() {
			return jobid;
		}

		public void setJobid(String jobid) {
			this.jobid = jobid;
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

		public String getJobname() {
			return jobname;
		}

		public void setJobname(String jobname) {
			this.jobname = jobname;
		}

		public String getPeonumber() {
			return peonumber;
		}

		public void setPeonumber(String peonumber) {
			this.peonumber = peonumber;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getJobtime() {
			return jobtime;
		}

		public void setJobtime(String jobtime) {
			this.jobtime = jobtime;
		}
		
		public static List<JobNew> getList(JSONArray array) throws JSONException {
			if (array == null) {
				return null;
			}
			List<JobNew> list = new ArrayList<JobNew>();
			for (int i = 0, len = array.length(); i < len; i++) {
				JSONObject obj = array.getJSONObject(i);
				if (obj != null) {
					JobNew p = new JobNew(obj);
					list.add(p);
				}
			}
			return list;
		}
		
		public JobNew(JSONObject json) throws JSONException {
			if (json == null) {
				return;
			}
			if (!json.isNull("id")) {
				jobid = json.getString("id");
			}
			if (!json.isNull("jobname")) {
				jobname = json.getString("jobname");
			}
			if (!json.isNull("peonumber")) {
				peonumber = json.getString("peonumber");
			}
			if (!json.isNull("salary_min")) {
				salary_min = json.getString("salary_min");
			}
			if (!json.isNull("salary_max")) {
				salary_max = json.getString("salary_max");
			}
			if (!json.isNull(Protocol.ProtocolKey.KEY_address)) {
				address = json.getString(Protocol.ProtocolKey.KEY_address);
			}
			if (!json.isNull("jobtime")) {
				jobtime = json.getString("jobtime");
			}
			if (!json.isNull("identState")) {
				identState = json.getString("identState");
			}

		}

	}

	public static class NearJob implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3591531657221288590L;
		String id;// 岗位id
		String salary_min;// 最小金额
		String salary_max;// 最大金额
		String job_name;// 招工名称
		String address;// 公司地址
		String jobtime;// 发布时间
		String lat;// 经度
		String lng;// 纬度
		String distance;// 距离
		String city;// 城市
		String identState;// 0未认证 1已认证

		public String getIdentState() {
			return identState;
		}

		public void setIdentState(String identState) {
			this.identState = identState;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
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

		public String getJob_name() {
			return job_name;
		}

		public void setJob_name(String job_name) {
			this.job_name = job_name;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getJobtime() {
			return jobtime;
		}

		public void setJobtime(String jobtime) {
			this.jobtime = jobtime;
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

		public String getDistance() {
			return distance;
		}

		public void setDistance(String distance) {
			this.distance = distance;
		}
		
		public static ArrayList<NearJob> getList(JSONArray array) throws JSONException {
			if (array == null) {
				return null;
			}
			ArrayList<NearJob> list = new ArrayList<NearJob>();
			for (int i = 0, len = array.length(); i < len; i++) {
				JSONObject obj = array.getJSONObject(i);
				if (obj != null) {
					NearJob p = new NearJob(obj);
					list.add(p);
				}
			}
			return list;
		}
		
		public NearJob(JSONObject json) throws JSONException {
			if (json == null) {
				return;
			}
			if (!json.isNull("id")) {
				id = json.getString("id");
			}
			if (!json.isNull("jobname")) {
				job_name = json.getString("jobname");
			}
			if (!json.isNull("distance")) {
				distance = json.getString("distance");
			}
			if (!json.isNull("lat")) {
				lat = json.getString("lat");
			}
			if (!json.isNull("lng")) {
				lng = json.getString("lng");
			}
			if (!json.isNull("salary_min")) {
				salary_min = json.getString("salary_min");
			}
			if (!json.isNull("salary_max")) {
				salary_max = json.getString("salary_max");
			}
			if (!json.isNull(Protocol.ProtocolKey.KEY_address)) {
				address = json.getString(Protocol.ProtocolKey.KEY_address);
			}
			if (!json.isNull("jobtime")) {
				jobtime = json.getString("jobtime");
			}
			if (!json.isNull("city")) {
				city = json.getString("city");
			}
			if (!json.isNull("identState")) {
				identState = json.getString("identState");
			}

		}

	}

	public static class JobFair implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3591531657221288592L;
		String id;
		String name;// 招聘会名称
		String contacts;// 联系人
		String tel;// 联系电话
		String venues;// 场馆
		String address;// 地点
		String date;// 时间
		String content;
		String lat;
		String lng;

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

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
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

		public String getContacts() {
			return contacts;
		}

		public void setContacts(String contacts) {
			this.contacts = contacts;
		}

		public String getTel() {
			return tel;
		}

		public void setTel(String tel) {
			this.tel = tel;
		}

		public String getVenues() {
			return venues;
		}

		public void setVenues(String venues) {
			this.venues = venues;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}
		
		public static ArrayList<JobFair> getList(JSONArray array) throws JSONException {
			if (array == null) {
				return null;
			}
			ArrayList<JobFair> list = new ArrayList<JobFair>();
			for (int i = 0, len = array.length(); i < len; i++) {
				JSONObject obj = array.getJSONObject(i);
				if (obj != null) {
					JobFair p = new JobFair(obj);
					list.add(p);
				}
			}
			return list;
		}
		
		public JobFair(JSONObject json) throws JSONException {
			if (json == null) {
				return;
			}
			if (!json.isNull("id")) {
				id = json.getString("id");
			}
			if (!json.isNull("address")) {
				address = json.getString("address");
			}
			if (!json.isNull("date")) {
				date = json.getString("date");
			}
			if (!json.isNull("lat")) {
				lat = json.getString("lat");
			}
			if (!json.isNull("lng")) {
				lng = json.getString("lng");
			}
			if (!json.isNull("name")) {
				name = json.getString("name");
			}
			if (!json.isNull("tel")) {
				tel = json.getString("tel");
			}
			if (!json.isNull("venues")) {
				venues = json.getString("venues");
			}
			if (!json.isNull("content")) {
				content = json.getString("content");
			}

		}

	}

}
