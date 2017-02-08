package com.zrlh.llkc.organization;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Organization {

	public static class PictureNews implements Serializable {

		private String picName;// 图片名称
		private String picUrl;// 图片地址

		public String getPicName() {
			return picName;
		}

		public void setPicName(String picName) {
			this.picName = picName;
		}

		public String getPicUrl() {
			return picUrl;
		}

		public void setPicUrl(String picUrl) {
			this.picUrl = picUrl;
		}
	}

	public static class OrganizationPoll implements Serializable {
		String orgid;// 信息内容唯一ID
		int id;
		String name;// 信息内容名称
		String img;// 机构logo
		String Intro;// 简介
		String clalss;// 分类
		String course;// 课程
		String label;// 标签
		String lng;// 经度
		String type;

		public String getOrgid() {
			return orgid;
		}

		public void setOrgid(String orgid) {
			this.orgid = orgid;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		String lat;// 纬度

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

		public String getImg() {
			return img;
		}

		public void setImg(String img) {
			this.img = img;
		}

		public String getIntro() {
			return Intro;
		}

		public void setIntro(String intro) {
			Intro = intro;
		}

		public String getClalss() {
			return clalss;
		}

		public void setClalss(String clalss) {
			this.clalss = clalss;
		}

		public String getCourse() {
			return course;
		}

		public void setCourse(String course) {
			this.course = course;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getLng() {
			return lng;
		}

		public void setLng(String lng) {
			this.lng = lng;
		}

		public String getLat() {
			return lat;
		}

		public void setLat(String lat) {
			this.lat = lat;
		}

		public static ArrayList<OrganizationPoll> getOrganizationList(JSONArray array)
				throws JSONException {
			int size = array.length();
			ArrayList<OrganizationPoll> os = new ArrayList<OrganizationPoll>();
			for (int i = 0; i < size; i++) {
				JSONObject jsonO = array.getJSONObject(i);
				OrganizationPoll u = new OrganizationPoll(jsonO);
				os.add(u);
			}
			return os;
		}
		
		public OrganizationPoll(JSONObject json) throws JSONException {
			if(json == null)
				return;
			if (!json.isNull("id")) {
				orgid = json.getString("id");
			}
			if (!json.isNull("class")) {
				clalss = json.getString("class");
			}
			if (!json.isNull("course")) {
				course = json.getString("course");
			}
			if (!json.isNull("img")) {
				img = json.getString("img");
			}
			if (!json.isNull("Intro")) {
				Intro = json.getString("Intro");
			}
			if (!json.isNull("label")) {
				label = json.getString("label");
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
			if (!json.isNull("type")) {
				String type2 = json.getString("type");
				if("1".equals(type2))
					type = "培训机构";
				else
					type = "技工学校";
			}
		}
	}

	public static class OrganizationDetail implements Serializable {
		String id;// 信息内容唯一ID
		String name;// 信息内容名称
		String img;// 机构logo
		String region;// 地区索引ID
		String addr;// 地址
		String zip;// 邮编
		String tel;// 电话
		String url;// 网址
		String Intro;// 简介
		String clalss;// 分类
		String course;// 课程
		String label;// 标签
		String type;// 类型索引
		String nature;// 学校性质
		String hits;// 点击量
		String lng;// 经度
		String lat;// 纬度

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

		public String getImg() {
			return img;
		}

		public void setImg(String img) {
			this.img = img;
		}

		public String getRegion() {
			return region;
		}

		public void setRegion(String region) {
			this.region = region;
		}

		public String getAddr() {
			return addr;
		}

		public void setAddr(String addr) {
			this.addr = addr;
		}

		public String getZip() {
			return zip;
		}

		public void setZip(String zip) {
			this.zip = zip;
		}

		public String getTel() {
			return tel;
		}

		public void setTel(String tel) {
			this.tel = tel;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getIntro() {
			return Intro;
		}

		public void setIntro(String intro) {
			Intro = intro;
		}

		public String getClalss() {
			return clalss;
		}

		public void setClalss(String clalss) {
			this.clalss = clalss;
		}

		public String getCourse() {
			return course;
		}

		public void setCourse(String course) {
			this.course = course;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getNature() {
			return nature;
		}

		public void setNature(String nature) {
			this.nature = nature;
		}

		public String getHits() {
			return hits;
		}

		public void setHits(String hits) {
			this.hits = hits;
		}

		public String getLng() {
			return lng;
		}

		public void setLng(String lng) {
			this.lng = lng;
		}

		public String getLat() {
			return lat;
		}

		public void setLat(String lat) {
			this.lat = lat;
		}
		
		public OrganizationDetail(JSONObject json) throws JSONException {
			if(json == null)
				return;
			if (!json.isNull("addr")) {
				addr = json.getString("addr");
			}
			if (!json.isNull("class")) {
				clalss = json.getString("class");
			}
			if (!json.isNull("course")) {
				course = json.getString("course");
			}
			if (!json.isNull("hits")) {
				hits = json.getString("hits");
			}
			if (!json.isNull("id")) {
				id = json.getString("id");
			}
			if (!json.isNull("img")) {
				img = json.getString("img");
			}
			if (!json.isNull("Intro")) {
				Intro = json.getString("Intro");
			}
			if (!json.isNull("label")) {
				label = json.getString("label");
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
			if (!json.isNull("nature")) {
				nature = json.getString("nature");
			}
			if (!json.isNull("region")) {
				region = json.getString("region");
			}
			if (!json.isNull("tel")) {
				tel = json.getString("tel");
			}
			if (!json.isNull("url")) {
				url = json.getString("url");
			}
			if (!json.isNull("zip")) {
				zip = json.getString("zip");
			}
			if (!json.isNull("type")) {
				String type2 = json.getString("type");
				if("1".equals(type2))
					type = "培训机构";
				else
					type = "技工学校";
			}
		}

	}

	public static class ClassfiyHot implements Serializable {
		int id;
		String clsName;// 名称
		String clsId;// 信息ID

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getClsId() {
			return clsId;
		}

		public void setClsId(String clsId) {
			this.clsId = clsId;
		}

		public String getClsName() {
			return clsName;
		}

		public void setClsName(String clsName) {
			this.clsName = clsName;
		}
	}

}
