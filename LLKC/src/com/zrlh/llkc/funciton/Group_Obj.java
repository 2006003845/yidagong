package com.zrlh.llkc.funciton;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;

/*
 * 群对象
 * */
public class Group_Obj {
	// @Table(name = "group_tab")
	// public static class Group_List implements Serializable {
	// /**
	// *
	// */
	// private static final long serialVersionUID = 3421551130350694900L;
	// int id;
	// String account; // 账户
	// String G_Id; // 群id
	// String Name; // 群名称
	// String ManagerId;// 群主id
	// String Type_int;// 群分类
	// String Members;// 群成员当前数量
	// String MembersMax;// 群成员最大数量
	// String Level;// 群等级
	// String MsgSum;// 群未读消息
	// String MyGroupString;// 1 是 2 非
	//
	// public String getAccount() {
	// return account;
	// }
	//
	// public void setAccount(String account) {
	// this.account = account;
	// }
	//
	// public int getId() {
	// return id;
	// }
	//
	// public void setId(int id) {
	// this.id = id;
	// }
	//
	// public String getG_Id() {
	// return G_Id;
	// }
	//
	// public void setG_Id(String g_Id) {
	// G_Id = g_Id;
	// }
	//
	// public String getName() {
	// return Name;
	// }
	//
	// public void setName(String name) {
	// Name = name;
	// }
	//
	// public String getManagerId() {
	// return ManagerId;
	// }
	//
	// public void setManagerId(String managerId) {
	// ManagerId = managerId;
	// }
	//
	// public String getType_int() {
	// return Type_int;
	// }
	//
	// public void setType_int(String type_int) {
	// Type_int = type_int;
	// }
	//
	// public String getMembers() {
	// return Members;
	// }
	//
	// public void setMembers(String members) {
	// Members = members;
	// }
	//
	// public String getMembersMax() {
	// return MembersMax;
	// }
	//
	// public void setMembersMax(String membersMax) {
	// MembersMax = membersMax;
	// }
	//
	// public String getLevel() {
	// return Level;
	// }
	//
	// public void setLevel(String level) {
	// Level = level;
	// }
	//
	// public String getMsgSum() {
	// return MsgSum;
	// }
	//
	// public void setMsgSum(String msgSum) {
	// MsgSum = msgSum;
	// }
	//
	// public String getMyGroupString() {
	// return MyGroupString;
	// }
	//
	// public void setMyGroupString(String myGroupString) {
	// MyGroupString = myGroupString;
	// }
	//
	// }
	//
	// public static class Group_Info implements Serializable {
	// String gId;
	// String gName;
	// String gHead;
	// String gManegerId;
	// String gManagerName;
	// String gType;
	// String gMembers;
	// String gMembersMax;
	// String gLevel;
	// String gAddress;
	// String gContent;
	// String gCreateTime;
	//
	// public String getgManegerId() {
	// return gManegerId;
	// }
	//
	// public void setgManegerId(String gManegerId) {
	// this.gManegerId = gManegerId;
	// }
	//
	// public String getgId() {
	// return gId;
	// }
	//
	// public void setgId(String gId) {
	// this.gId = gId;
	// }
	//
	// public String getgName() {
	// return gName;
	// }
	//
	// public void setgName(String gName) {
	// this.gName = gName;
	// }
	//
	// public String getgHead() {
	// return gHead;
	// }
	//
	// public void setgHead(String gHead) {
	// this.gHead = gHead;
	// }
	//
	// public String getgManagerName() {
	// return gManagerName;
	// }
	//
	// public void setgManagerName(String gManagerName) {
	// this.gManagerName = gManagerName;
	// }
	//
	// public String getgType() {
	// return gType;
	// }
	//
	// public void setgType(String gType) {
	// this.gType = gType;
	// }
	//
	// public String getgMembers() {
	// return gMembers;
	// }
	//
	// public void setgMembers(String gMembers) {
	// this.gMembers = gMembers;
	// }
	//
	// public String getgMembersMax() {
	// return gMembersMax;
	// }
	//
	// public void setgMembersMax(String gMembersMax) {
	// this.gMembersMax = gMembersMax;
	// }
	//
	// public String getgLevel() {
	// return gLevel;
	// }
	//
	// public void setgLevel(String gLevel) {
	// this.gLevel = gLevel;
	// }
	//
	// public String getgAddress() {
	// return gAddress;
	// }
	//
	// public void setgAddress(String gAddress) {
	// this.gAddress = gAddress;
	// }
	//
	// public String getgContent() {
	// return gContent;
	// }
	//
	// public void setgContent(String gContent) {
	// this.gContent = gContent;
	// }
	//
	// public String getgCreateTime() {
	// return gCreateTime;
	// }
	//
	// public void setgCreateTime(String gCreateTime) {
	// this.gCreateTime = gCreateTime;
	// }
	//
	// }

	public static class Group_Member implements Serializable {
		String fId;
		String fName;
		String fSex;
		String fHead;
		String sign;
		String local;

		public String getfId() {
			return fId;
		}

		public void setfId(String fId) {
			this.fId = fId;
		}

		public String getfName() {
			return fName;
		}

		public void setfName(String fName) {
			this.fName = fName;
		}

		public String getfSex() {
			return fSex;
		}

		public void setfSex(String fSex) {
			this.fSex = fSex;
		}

		public String getfHead() {
			return fHead;
		}

		public void setfHead(String fHead) {
			this.fHead = fHead;
		}

		public String getSign() {
			return sign;
		}

		public void setSign(String sign) {
			this.sign = sign;
		}

		public String getLocal() {
			return local;
		}

		public void setLocal(String local) {
			this.local = local;
		}

	}
}
