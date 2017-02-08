package com.zzl.zl_app.connection;

public class Protocol {
	public static final short JSONTAG = 0x0001;

	/**
	 * 上传企业LOGO/营业执照
	 */
	public static String Request_Url_Company_ImgOp = "Company_ImgOp_Req.jsp";
	/**
	 * 企业认证
	 */
	public static String Request_Url_Company_Ident = "Company_Ident_Req.jsp";
	/**
	 * 修改企业资料
	 */
	public static String Request_Url_Company_Modify = "Company_update_Req.jsp";
	/**
	 * 查询企业资料
	 */
	public static String Request_Url_Company_info = "Company_Info_Req.jsp";

	/**
	 * 注销
	 */
	public static String Request_Url_User_Logout = "User_Logout_Req.jsp";

	/**
	 * 登陆
	 */
	public static String Request_Url_User_Login = "User_Login_Req.jsp";

	/**
	 * 编辑/发布岗位信息请求
	 */
	public static String Request_Url_Recruit_Op = "Recruit_Op_Req.jsp";
	/**
	 * 删除岗位信息请求
	 */
	public static String Request_Url_Recruit_Delete = "Recruit_Delete_Req.jsp";
	/**
	 * 查询岗位详情信息请求
	 */
	public static String Request_Url_Recruit_info = "Recruit_info_Req.jsp";
	
	public static String Request_Url_JobDetail="http://192.168.0.13:8060/app/api.php?MsgType=JobDetailssecretControllers";
	/**
	 * 获取岗位列表信息请求
	 */
	public static String Request_Url_Recruit_InfoList = "Recruit_InfoList_Req.jsp";

	/**
	 * 发布个人动态
	 */
	public static String Request_Url_Release_Dynamic = "Dynamics_Publish_Req.jsp";
	/**
	 * 删除个人动态
	 */
	public static String Request_Url_Delete_Dynamic = "Dynamics_Delete_Req.jsp";

	/**
	 * 获取个人动态列表
	 */
	public static String Request_Url_Dynamic_List = "Dynamics_List_Req.jsp";

	/**
	 * 工友帮申请处理
	 */
	public static String Request_Url_GroupAgrOrRef = "Group_AgrOrRef_Req.jsp";

	/**
	 * 消息列表
	 */
	public static String Request_Url_MsgList = "Msg_List_Req.jsp";

	/**
	 * 群聊天消息列表
	 */
	public static String Request_Url_GroupMsgList = "Msg_ListByGroup_Req.jsp";

	/**
	 * 群聊
	 */
	public static String Request_Url_GroupChat = "Msg_CloudByGroup_Req.jsp";

	/**
	 * 私聊
	 */
	public static String Request_Url_PrivateChat = "Msg_Cloud_Req.jsp";

	/**
	 * 私聊转发
	 */
	public static String Request_Url_MsgForword = "Msg_Forword_Req.jsp";

	/**
	 * 群聊转发
	 */
	public static String Request_Url_MsgForwordByGroup = "Msg_ForwordByGroup_Req.jsp";

	/**
	 * 好友操作
	 */
	public static String Request_Url_FriendOper = "Friend_Op_Req.jsp";
	/**
	 * 添加好友
	 * 
	 */
	public static String Request_Url_AddFriend = "Friend_ToJoin_Req.jsp";
	/**
	 * 推荐好友
	 */
	public static String Request_Url_RecommendFriend = "Recommend_Friend_Req.jsp";
	/**
	 * 附近的人
	 */
	public static String Request_Url_NearbyFriend = "Near_Friend_Req.jsp";

	/**
	 * 退出群
	 * 
	 */
	public static String Request_Url_ExitGroup = "Group_Exit_Req.jsp";
	/**
	 * 解散群
	 * 
	 */
	public static String Request_Url_DisbandGroup = "Group_Disband_Req.jsp";
	/**
	 * 加入群
	 * 
	 */
	public static String Request_Url_AddGroup = "Group_ToJoin_Req.jsp";

	/**
	 * 踢人
	 */
	public static String Request_Url_RemoveMembFromGroup = "Group_Remove_Req.jsp";

	/**
	 * 版本更新
	 */
	public static String Request_Url_CheckVersion = "Client_Update_Req.jsp?";

	/**
	 * 搜索群列表
	 */
	public static String Request_Url_SearchFGroup = "Group_Find_Req.jsp?";
	/**
	 * 搜索好友列表
	 */
	public static String Request_Url_SearchFriendList = "Search_Friend_Req.jsp";

	/**
	 * 查看群基本详情
	 */
	public static String Request_Url_queryGroupInfo = "Group_Info_Req.jsp?";

	/**
	 * 查看个人群列表
	 */
	public static String Request_Url_getOwnGroupList = "Group_List_Req.jsp?";
	/**
	 * 群成员列表
	 */
	public static String Request_Url_GroupMemberList = "Group_MemberList_Req.jsp?";
	/**
	 * 查看好友列表
	 */
	public static String Request_Url_getFriendList = "Friend_List_Req.jsp";
	/**
	 * 邀请好友
	 */
	public static String Request_Url_InviteFriend = "Group_InviteFriend_Req.jsp";

	/**
	 * 推荐工友帮
	 */
	public static String Request_Url_RecommendGroup = "Recommend_Group_Req.jsp";
	/**
	 * 附近工友帮
	 */
	public static String Request_Url_NearbyGroup = "Near_Group_Req.jsp";
	/**
	 * 创建工友帮
	 */
	public static String Request_Url_CreateGroup = "Group_Create_Req.jsp";
	/**
	 * 修改群信息
	 */
	public static String Request_Url_modifyGroupInfo = "Group_ContentUpdate_Req.jsp";

	/**
	 * 创建/修改简历
	 */
	public static String Request_Url_ResumeOper = "Resume_Op_Req.jsp";
	/**
	 * 创建/修改简历
	 */
	public static String Request_Url_ResumeImgOper = "Resume_Photo_Req.jsp";

	/**
	 * 获取简历详情
	 */
	public static String Request_Url_ResumeInfo = "Resume_Info_Req.jsp";

	/**
	 * 获取简历列表
	 */
	public static String Request_Url_ResumeList = "Resume_InfoList_Req.jsp";

	/**
	 * 
	 * 获取用户资料
	 */
	public static String Request_Url_UserInfo = "User_Info_Req.jsp";

	public static String Request_Url_GameList = "GameCenter_List_Req.jsp";
	
	/**
	 * 头像墙操作
	 */
	public static String Request_Url_UserHead = "User_HeadOp_Req.jsp";
	/**
	 * 找回密码
	 */
	public static String Request_Url_FindPassWord = "Obtain_Pwd_Req.jsp";

	/**
	 * 修改密码
	 */
	public static String Request_Url_ModifyPassWord = "User_Account_Req.jsp";
	/**
	 * 注册
	 */
	public static String Request_Url_Register = "User_Register_Req.jsp";

	/**
	 *修改资料
	 */
	public static String Request_Url_ModifyUser = "User_Modify_Req.jsp";
	
	public static String Request_Url_PostLocation = "Post_LocationInfo_Req.jsp";

	public class ProtocolKey {
		/**
		 * Orgid Name Pwd Type Imgext Img
		 */
		public static final String KEY_Name = "Name";
		public static final String KEY_Orgid = "Orgid";
		public static final String KEY_Pwd = "Pwd";
		public static final String KEY_Type = "Type";
		public static final String KEY_Imgext = "Imgext";
		public static final String KEY_Img = "Img";

		public static final String KEY_Stat = "Stat";
		public static final String KEY_Msg = "Msg";
		public static final String KEY_Url = "Url";

		public static final String KEY_name = "name";
		public static final String KEY_phone = "phone";
		public static final String KEY_addr = "addr";
		public static final String KEY_intro = "intro";

		public static final String KEY_type = "type";
		public static final String KEY_job_city = "job_city";
		public static final String KEY_rid = "rid";
		public static final String KEY_people = "people";
		public static final String KEY_salary_min = "salary_min";
		public static final String KEY_salary_max = "salary_max";
		public static final String KEY_contacts_name = "contacts_name";
		public static final String KEY_tel = "tel";
		public static final String KEY_class_ot = "class_ot";
		public static final String KEY_jobdescription = "jobdescription";
		public static final String KEY_description = "description";
		public static final String KEY_outtime = "outtime";
		public static final String KEY_tag = "tag";
		public static final String KEY_address = "address";
		public static final String KEY_area = "city_job";

		public static final String KEY_city = "city";
		public static final String KEY_postId = "postId";
		public static final String KEY_salary = "salary";
		public static final String KEY_online = "online";
		public static final String KEY_fabu_date = "fabu_date";
		public static final String KEY_lastOpTime = "lastOpTime";

		public static final String KEY_List = "List";

		public static final String KEY_Mid = "Mid";

		public static final String KEY_Gid = "Gid";

		public static final String KEY_Gname = "Gname";

		public static final String KEY_Content = "Content";
		public static final String KEY_Uname = "Uname";
		/**
		 * 评论时间
		 */
		public static final String KEY_Time = "Time";
		public static final String KEY_Head = "Head";
		public static final String KEY_Rname = "Rname";
		public static final String KEY_Uid = "Uid";

		public static final String KEY_img = "img";
		public static final String KEY_reid = "reid";
		public static final String KEY_url = "url";

		public static final String KEY_simg = "simg";
		public static final String KEY_bimg = "bimg";
		public static final String KEY_voice = "voice";
		public static final String KEY_mtype = "mtype";
	}

	public static class ProtocolWeibo {
		/**
		 * 普通微博
		 */
		public static final String TYPE_WeiboSend_NORMAL = "1";
		/**
		 * 群微博
		 */
		public static final String TYPE_WeiboSend_GROUP = "2";
		/**
		 * 转发
		 */
		public static final String TYPE_WeiboSend_Transpond = "3";
		/**
		 * 评论
		 */
		public static final String Comment = "";

	}
}
