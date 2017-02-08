package com.zzl.zl_app.connection;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import cn.jpush.android.api.JPushInterface;

import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.Obj;
import com.zrlh.llkc.corporate.Periodical;
import com.zrlh.llkc.corporate.PlatformAPI;
import com.zrlh.llkc.corporate.Resume;
import com.zrlh.llkc.corporate.Type;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.Authentication;
import com.zrlh.llkc.funciton.Friend;
import com.zrlh.llkc.funciton.Group;
import com.zrlh.llkc.funciton.JobDetail;
import com.zrlh.llkc.funciton.Jobs;
import com.zrlh.llkc.funciton.Jobs.JobFair;
import com.zrlh.llkc.funciton.Jobs.NearJob;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zrlh.llkc.funciton.PersonalDynamic;
import com.zrlh.llkc.joggle.Account;
import com.zrlh.llkc.organization.Organization.OrganizationDetail;
import com.zrlh.llkc.organization.Organization.OrganizationPoll;
import com.zrlh.llkc.ui.LLKCApplication;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.entity.Game;
import com.zzl.zl_app.entity.GroupMsg;
import com.zzl.zl_app.entity.ImageItem;
import com.zzl.zl_app.entity.Msg;
import com.zzl.zl_app.entity.MsgManager;
import com.zzl.zl_app.entity.PrivateMsg;
import com.zzl.zl_app.net_port.ConnectionCaller;
import com.zzl.zl_app.net_port.WSError;
import com.zzl.zl_app.util.Tools;

public class Community {

	private static Community comm = null;
	private static Context mContext;

	private static Handler handler;

	public static void initHandler() {
		if (handler == null)
			handler = new Handler() {
				public void handleMessage(Message msg) {
					String cmsg = (String) msg.obj;
					if (msg.what == 1) {
						MyToast.getToast()
								.showToast(mContext, "链接超时，请检查网络", 50);
					} else {
						MyToast.getToast().showToast(mContext, cmsg);
					}
				};
			};
	}

	public static Community getInstance(Context context) {
		mContext = context;
		if (comm == null) {
			comm = new Community();
		}
		if (checkNetWorkStatus(context)) {
			return comm;
		} else {
			handler.post(new Runnable() {

				@Override
				public void run() {
					MyToast.getToast().showToast(mContext, "链接超时，请检查网络");
				}
			});
		}
		return comm;
	}

	private static boolean checkNetWorkStatus(Context context) {
		if (context == null)
			return false;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = cm.getActiveNetworkInfo();

		if (netinfo != null) {
			return netinfo.isAvailable();
		}
		// if (netinfo != null && netinfo.isConnected()) {
		// result = true;
		// } else {
		// result = false;
		// }
		return false;
	}

	/**
	 * 检测版本
	 * 
	 * @return
	 * @throws JSONException
	 */
	public JSONObject checkVersioinUpdate() throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("Plat", LlkcBody.APP_Plat));
		pairs.add(new BasicNameValuePair("Version", LlkcBody.APP_Version));
		pairs.add(new BasicNameValuePair("Orgid", LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair("Channel", LlkcBody.CHANNEL));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_CheckVersion);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			return obj;
		}
		return null;
	}

	public List<Group> searchFGroupList(String key) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("str", key));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_SearchFGroup);
		if (data != null) {
			JSONArray array = getJSONArrayFromByte(data);
			if (array != null) {
				return Group.getFGroupList(array);
			}
		}
		return null;
	}

	public List<Group> recommendGroupList() throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("Orgid", LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair("Name", LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair("Pwd", LlkcBody.PASS_ACCOUNT));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_RecommendGroup);

		if (data != null) {
			JSONArray array = getJSONArrayFromByte(data);
			if (array != null) {
				return Group.getFGroupList(array);
			}
		}

		return null;
	}

	public List<Group> nearbyGroupList(String page, String lat, String lng)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("Orgid", LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair("Name", LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair("Pwd", LlkcBody.PASS_ACCOUNT));
		pairs.add(new BasicNameValuePair("Page", page));
		pairs.add(new BasicNameValuePair("Lat", lat));
		pairs.add(new BasicNameValuePair("Longs", lng));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_NearbyGroup);

		if (data != null) {
			JSONArray array = getJSONArrayFromByte(data);
			if (array != null) {
				return Group.getFGroupList(array);
			}
		}

		return null;
	}

	public List<Friend> recommendFriendList() throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("Orgid", LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair("Name", LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair("Pwd", LlkcBody.PASS_ACCOUNT));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_RecommendFriend);

		if (data != null) {
			JSONArray array = getJSONArrayFromByte(data);
			if (array != null) {
				return Friend.getFriendList(array);
			}
		}

		return null;
	}

	public List<Friend> nearbyFriendList(String sex, String page, String lat,
			String lng) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("Orgid", LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair("Name", LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair("Pwd", LlkcBody.PASS_ACCOUNT));
		pairs.add(new BasicNameValuePair("Sex", sex));
		pairs.add(new BasicNameValuePair("Page", page));
		pairs.add(new BasicNameValuePair("Lat", lat));
		pairs.add(new BasicNameValuePair("Longs", lng));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_NearbyFriend);

		if (data != null) {
			JSONArray array = getJSONArrayFromByte(data);
			if (array != null) {
				return Friend.getFriendList(array);
			}
		}

		return null;
	}

	public Group queryFGroupInfo(String gId) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("gId", gId));
		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_queryGroupInfo);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				return new Group(obj);
			}
		}
		return null;
	}

	public List<Group> getOwnFGroupList(String name, String pwd)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name, name));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_getOwnGroupList);
		if (data != null) {
			JSONArray array = getJSONArrayFromByte(data);
			if (array != null) {
				return Group.getFGroupList(array);
			}
		}
		return null;
	}

	/**
	 * 好友列表
	 * 
	 * @param name
	 * @param pwd
	 * @return
	 * @throws JSONException
	 */
	public List<Friend> getFriendList(String name, String pwd)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name, name));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_getFriendList);
		if (data != null) {
			JSONArray array = getJSONArrayFromByte(data);
			if (array != null) {
				return Friend.getFriendList(array);
			}
		}
		return null;
	}

	public ArrayList<Friend> groupMemberList(String gId) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd,
				LlkcBody.PASS_ACCOUNT));
		pairs.add(new BasicNameValuePair("Gid", gId));
		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_GroupMemberList);
		if (data != null) {
			JSONArray array = getJSONArrayFromByte(data);
			if (array != null) {
				return Friend.getFriendList(array);
			}
		}
		return null;
	}

	/**
	 * 搜索好友列表
	 * 
	 * @param name
	 * @param pwd
	 * @param seachFriendKey
	 * @return
	 * @throws JSONException
	 */
	public List<Friend> searchFriendList(String name, String pwd,
			String seachFriendKey) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name, name));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair("Sname", seachFriendKey));
		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_SearchFriendList);
		if (data != null) {
			JSONArray array = getJSONArrayFromByte(data);
			if (array != null) {
				return Friend.getFriendList(array);
			}
		}
		return null;
	}

	/**
	 * 登出
	 * 
	 * @param name
	 * @param pwd
	 * @return
	 * @throws JSONException
	 */
	public boolean logout(String name, String pwd) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name, name));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_User_Logout);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (state.equals("0")) {
					return true;
				} else {
					Message msg = handler.obtainMessage();
					msg.obj = m;
					handler.sendMessage(msg);
				}
			}
		}
		return false;
	}

	/**
	 * 查看好友用户资料
	 * 
	 * @param uId
	 * @return
	 * @throws JSONException
	 */
	public Friend queryUserInfo(String uId) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("Orgid", LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair("Name", LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair("Pwd", LlkcBody.PASS_ACCOUNT));
		pairs.add(new BasicNameValuePair("Uid", uId));
		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_UserInfo);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				return new Friend(obj);
			}
		}
		return null;
	}

	/**
	 * 查看个人动态
	 * 
	 * @param uId
	 * @return
	 * @throws JSONException
	 */
	public PersonalDynamic queryPersonalDynamic(String uId)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("Orgid", LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair("Name", LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair("Pwd", LlkcBody.PASS_ACCOUNT));
		pairs.add(new BasicNameValuePair("Uid", uId));
		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_UserInfo);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				PersonalDynamic dynamic = new PersonalDynamic(obj);
				return dynamic;
			}
		}
		return null;
	}

	/**
	 * 创建工友帮
	 * 
	 * @param gName
	 * @param gContent
	 * @param gAddress
	 * @return
	 * @throws JSONException
	 */
	public JSONObject createGroup(String gName, String gContent, String gAddress)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd,
				LlkcBody.PASS_ACCOUNT));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd,
				LlkcBody.PASS_ACCOUNT));
		pairs.add(new BasicNameValuePair("uId", LlkcBody.ACCOUNT.getUid()));
		pairs.add(new BasicNameValuePair("gName", gName));
		pairs.add(new BasicNameValuePair("gContent", gContent));
		pairs.add(new BasicNameValuePair("gAddress", gAddress));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_CreateGroup);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			return obj;
		}
		return null;
	}

	/**
	 * 退出群
	 * 
	 * @param name
	 * @param pwd
	 * @param gId
	 * @return
	 * @throws JSONException
	 */
	public boolean exitGroup(String name, String pwd, String gId)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name, name));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair("gId", gId));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_ExitGroup);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (state.equals("0")) {
					return true;
				} else {
					Message msg = handler.obtainMessage();
					msg.obj = m;
					handler.sendMessage(msg);
				}
			}
		}
		return false;
	}

	public boolean disbandGroup(String gId) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd,
				LlkcBody.PASS_ACCOUNT));
		pairs.add(new BasicNameValuePair("gId", gId));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_DisbandGroup);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				if (state.equals("0")) {
					return true;
				} else {
					if (!obj.isNull(Protocol.ProtocolKey.KEY_Msg)) {
						String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
						Message msg = handler.obtainMessage();
						msg.obj = m;
						handler.sendMessage(msg);
					}
				}
			}
		}
		return false;
	}

	/**
	 * 邀请好友
	 * 
	 * @param gId
	 * @param inviteString
	 * @return
	 * @throws JSONException
	 */
	public boolean inviteFriend(String gId, String inviteString)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd,
				LlkcBody.PASS_ACCOUNT));
		pairs.add(new BasicNameValuePair("gId", gId));
		pairs.add(new BasicNameValuePair("toUid", inviteString));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_InviteFriend);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);

				if (state.equals("0")) {
					return true;
				} else {
					if (!obj.isNull(Protocol.ProtocolKey.KEY_Msg)) {
						String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
						Message msg = handler.obtainMessage();
						msg.obj = m;
						handler.sendMessage(msg);
					}
				}
			}
		}
		return false;
	}

	/**
	 * 找回密码
	 * 
	 * @param name
	 * @param email
	 * @return
	 * @throws JSONException
	 */
	public boolean findPassWord(String name, String email) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("Email", email));
		pairs.add(new BasicNameValuePair("Name", name));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_FindPassWord);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (state.equals("0")) {
					return true;
				} else {
					Message msg = handler.obtainMessage();
					msg.obj = m;
					handler.sendMessage(msg);
				}
			}
		}
		return false;
	}

	/**
	 * 修改密码
	 * 
	 * @param old_password
	 * @param new_password
	 * @return
	 * @throws JSONException
	 */
	public boolean modifyPassWord(String old_password, String new_password)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair("Pwd", old_password));
		pairs.add(new BasicNameValuePair("Npwd", new_password));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_ModifyPassWord);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				if (state.equals("0")) {
					return true;
				} else {
					if (!obj.isNull(Protocol.ProtocolKey.KEY_Msg)) {
						String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
						Message msg = handler.obtainMessage();
						msg.obj = m;
						handler.sendMessage(msg);
					}
				}
			}
		}
		return false;
	}

	public JSONObject register(String name, String password, String email)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name, name));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, password));
		pairs.add(new BasicNameValuePair("Imei", LlkcBody.IMEI));
		pairs.add(new BasicNameValuePair("Email", email));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_Register);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			return obj;
		}
		return null;
	}

	/**
	 * 加入工友帮
	 * 
	 * @param gId
	 * @return
	 * @throws JSONException
	 */
	public boolean addGroup(String gId) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd,
				LlkcBody.PASS_ACCOUNT));
		pairs.add(new BasicNameValuePair("gId", gId));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_AddGroup);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);

				if (state.equals("0")) {
					return true;
				} else {
					if (!obj.isNull(Protocol.ProtocolKey.KEY_Msg)) {
						String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
						Message msg = handler.obtainMessage();
						msg.obj = m;
						handler.sendMessage(msg);
					}
				}
			}
		}
		return false;
	}

	public boolean removeMembFromGroup(String name, String pwd, String gId,
			String uId) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name, name));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair("gId", gId));
		pairs.add(new BasicNameValuePair("toUid", uId));
		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_RemoveMembFromGroup);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (state.equals("0")) {
					return true;
				} else {
					Message msg = handler.obtainMessage();
					msg.obj = m;
					handler.sendMessage(msg);
				}
			}
		}
		return false;
	}

	/**
	 * 登陆
	 * 
	 * @param name
	 * @param pwd
	 * @return
	 * @throws JSONException
	 */
	public boolean login(String name, String pwd) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name, name));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair("Ver", LlkcBody.APP_Version));
		pairs.add(new BasicNameValuePair("Platform", LlkcBody.APP_Plat));
		pairs.add(new BasicNameValuePair("Deviceid", ""));
		pairs.add(new BasicNameValuePair("Clientid", ""));
		pairs.add(new BasicNameValuePair("Appid", LlkcBody.JPUSH_APPKEY));
		pairs.add(new BasicNameValuePair("MasterSecret",
				LlkcBody.JPUSH_MASTER_SECRET));
		pairs.add(new BasicNameValuePair("Lat", "" + LlkcBody.LAT));
		pairs.add(new BasicNameValuePair("Longs", "" + LlkcBody.LNG));
		pairs.add(new BasicNameValuePair("City", LlkcBody.CITY_STRING));
		pairs.add(new BasicNameValuePair("Imei", LlkcBody.IMEI));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_User_Login);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String stat = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = "";
				if (!obj.isNull("Msg"))
					m = obj.getString("Msg");
				if (stat.equals("0")) {
					LLKCApplication.getInstance().setLoginStat(true);
					if (!obj.isNull("Uid")) {
						String Uid = obj.getString("Uid");
						LlkcBody.UID_ACCOUNT = Uid;
						LLKCApplication.getInstance().setAlias();
					}
					if (!obj.isNull("CompanyId")) {
						String CompanyId = obj.getString("CompanyId");
						if ("0".equals(CompanyId)) {
							LlkcBody.isCorporate = false;
						} else {
							LlkcBody.isCorporate = true;
						}
					}
					if (!obj.isNull("IdentState")) {
						String IdentState = obj.getString("IdentState");
						if (IdentState.equals("0")) {
							LLKCApplication.getInstance().setAuthStat(
									LlkcBody.State_Auth_No);
						} else if (IdentState.equals("1")) {
							LLKCApplication.getInstance().setAuthStat(
									LlkcBody.State_Auth_Ing);
						} else if (IdentState.equals("2")) {
							LLKCApplication.getInstance().setAuthStat(
									LlkcBody.State_Auth_Success);
						}
					}
					if (!obj.isNull("User")) {
						JSONObject uObj = obj.getJSONObject("User");
						LLKCApplication.getInstance().savePersonInfo(
								uObj.toString());
						LlkcBody.ACCOUNT = new Account(uObj);
						LlkcBody.ACCOUNT.setArccount(name);
						LlkcBody.ACCOUNT.setPrassWord(pwd);
					}

					LlkcBody.USER_ACCOUNT = name;
					LlkcBody.PASS_ACCOUNT = pwd;
					LLKCApplication.getInstance().setUid(LlkcBody.UID_ACCOUNT);
					LLKCApplication.getInstance().setPwd(pwd);
					LLKCApplication.getInstance().setAccount(name);

					return true;
				} else {
					LLKCApplication.getInstance().setLoginStat(false);
					Message msg = handler.obtainMessage();
					msg.obj = m;
					handler.sendMessage(msg);
				}
			} else {
				Message msg = handler.obtainMessage();
				msg.obj = "链接超时,请重试";
				handler.sendMessage(msg);
			}
		}
		return false;
	}

	/**
	 * 企业认证
	 * 
	 * @param accountName
	 * @param pwd
	 * @param companyId
	 * @param name
	 * @param phone
	 * @param addr
	 * @param intro
	 * @return
	 * @throws JSONException
	 */
	public boolean companyIdent(String accountName, String pwd, String name,
			String phone, String addr, String intro) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair("cname", name));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_phone, phone));
		pairs.add(new BasicNameValuePair("address", addr));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_intro, intro));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_Company_Ident);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (state.equals("0")) {
					return true;
				} else {
					Message msg = handler.obtainMessage();
					msg.obj = m;
					handler.sendMessage(msg);
				}
			}
		}
		return false;
	}

	/**
	 * 修改企业资料
	 * 
	 * @param accountName
	 * @param pwd
	 * @param phone
	 * @param addr
	 * @param intro
	 * @return
	 * @throws JSONException
	 */
	public boolean companyModify(String accountName, String pwd, String phone,
			String addr, String intro) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_phone, phone));
		pairs.add(new BasicNameValuePair("address", addr));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_intro, intro));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_Company_Modify);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				// String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				// Message msg = handler.obtainMessage();
				// msg.obj = m;
				// handler.sendMessage(msg);
				if (state.equals("0")) {
					return true;
				} else {
					if (!obj.isNull(Protocol.ProtocolKey.KEY_Msg)) {
						String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
						Message msg = handler.obtainMessage();
						msg.obj = m;
						handler.sendMessage(msg);
					}
				}
			}
		}
		return false;
	}

	/**
	 * 查询企业信息
	 * 
	 * @param accountName
	 * @param pwd
	 * @return
	 * @throws JSONException
	 */
	public Authentication companyInfo(String accountName, String pwd)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_Company_info);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString("stat");
				if (state.equals("0")) {
					return new Authentication(obj);
				} else {
					if (!obj.isNull(Protocol.ProtocolKey.KEY_Msg)) {
						String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
						Message msg = handler.obtainMessage();
						msg.obj = m;
						handler.sendMessage(msg);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 上传企业LOGO/营业执照
	 * 
	 * @param context
	 * @param accountName
	 * @param pwd
	 * @param type
	 * @param imgext
	 * @param img
	 * @return
	 * @throws Exception
	 */
	public String companyImgOp(Context context, String accountName, String pwd,
			String type, Bitmap img) throws Exception {
		String resp = Utility.openUrl(context, PlatformAPI.BaseUrl
				+ Protocol.Request_Url_Company_ImgOp + "?Orgid="
				+ LlkcBody.APP_Orgid + "&Name=" + accountName + "&Pwd=" + pwd
				+ "&Type=" + type + "&Imgext=jpg", "POST", img);

		if (resp != null) {
			JSONObject obj = getJSONObjectFromString(resp);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (state.equals("0")) {
					if (!obj.isNull(Protocol.ProtocolKey.KEY_Url)) {
						String url = obj
								.getString(Protocol.ProtocolKey.KEY_Url);
						if (url != null && !url.equals("")) {
							return url;
						}
						// TODO
					}
					return null;
				} else {
					Message msg = handler.obtainMessage();
					msg.obj = m;
					handler.sendMessage(msg);
				}
			}
		}
		return null;
	}

	/**
	 * 编辑/发布岗位信息
	 * 
	 * @param accountName
	 * @param pwd
	 * @param type
	 *            操作类型（1：添加 2：修改）
	 * @param jobCity
	 *            岗位所在城市
	 * @param rid
	 *            岗位id
	 * @param name
	 *            岗位名称
	 * @param people
	 *            招聘人数
	 * @param salary_min
	 *            薪水下限
	 * @param salary_max
	 *            薪水上限
	 * @param contact_name
	 *            联系人
	 * @param tel
	 *            联系电话
	 * @param Class_ot
	 *            工作类别
	 * @param jobDescription
	 *            岗位描述
	 * @param description
	 *            岗位要求
	 * @param outtime
	 *            有效期限
	 * @param tag
	 *            标签
	 * @param address
	 *            工作地址
	 * @return
	 * @throws JSONException
	 */
	public boolean recruitOp(String accountName, String pwd, String type,
			String jobCity, String rid, String name, String people,
			String salary_min, String salary_max, String contact_name,
			String tel, String Class_ot, String jobDescription,
			String description, String outtime, String tag, String address)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_type, type));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_job_city,
				jobCity));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_rid, rid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_name, name));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_people,
				people));

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_salary_min,
				salary_min));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_salary_max,
				salary_max));
		pairs.add(new BasicNameValuePair(
				Protocol.ProtocolKey.KEY_contacts_name, contact_name));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_tel, tel));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_class_ot,
				Class_ot));
		pairs.add(new BasicNameValuePair(
				Protocol.ProtocolKey.KEY_jobdescription, jobDescription));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_description,
				description));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_outtime,
				outtime));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_tag, tag));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_address,
				address));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_Recruit_Op);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (state.equals("0")) {
					return true;
				} else {
					Message msg = handler.obtainMessage();
					msg.obj = m;
					handler.sendMessage(msg);
				}
			}
		}
		return false;
	}

	/**
	 * 删除岗位
	 * 
	 * @param accountName
	 * @param pwd
	 * @param city
	 * @param rid
	 * @return
	 * @throws JSONException
	 */
	public boolean recruitDelete(String accountName, String pwd, String city,
			String rid) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_city, city));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_rid, rid));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_Recruit_Delete);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (state.equals("0")) {
					return true;
				} else {
					Message msg = handler.obtainMessage();
					msg.obj = m;
					handler.sendMessage(msg);
				}
			}
		}
		return false;
	}

	/**
	 * 查询岗位详情信息请求
	 * 
	 * @param accountName
	 * @param pwd
	 * @param city
	 * @param rid
	 * @return
	 * @throws JSONException
	 * @throws WSError
	 * @throws UnsupportedEncodingException
	 */
	public JobDetail getJobDetail(String accountName, String pwd, String city,
			String rid) throws JSONException, WSError,
			UnsupportedEncodingException {
		String url = PlatformAPI.PHPBaseUrl
				+ "MsgType=JobDetailssecretControllers" + "&id=" + rid
				+ "&city="
				+ URLEncoder.encode(city == null ? "" : city.trim(), "utf-8")
				+ "&imei=" + LlkcBody.IMEI;
		String reponse = ConnectionCaller.doGet(url);

		JSONObject obj = getJSONObjectFromString(reponse);
		if (obj != null)
			return new JobDetail(obj);
		return null;
	}

	/**
	 * 获取岗位信息列表
	 * 
	 * @param accountName
	 * @param pwd
	 * @return
	 * @throws JSONException
	 */
	public List<JobDetail> recruitInfoList(String accountName, String pwd)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_Recruit_InfoList);
		if (data != null) {
			JSONArray array = getJSONArrayFromByte(data);
			return JobDetail.getList(array);
		}
		return null;
	}

	/**
	 * 发布个人动态
	 * 
	 * @param context
	 * @param content
	 * @param img
	 * @return
	 * @throws Exception
	 */

	public PersonalDynamic releaseDynamic(Context context, String content,
			Bitmap img) throws Exception {
		String url = PlatformAPI.BaseUrl
				+ Protocol.Request_Url_Release_Dynamic
				+ "?Orgid="
				+ LlkcBody.APP_Orgid
				+ "&Name="
				+ LlkcBody.USER_ACCOUNT
				+ "&Pwd="
				+ LlkcBody.PASS_ACCOUNT
				+ "&Imgext=jpg"
				+ "&Content="
				+ URLEncoder.encode(content == null ? "" : content.trim(),
						"utf-8");
		String resp = Utility.openUrl(context, url, "POST", img);

		if (resp != null) {
			JSONObject obj = getJSONObjectFromString(resp);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);

				if (state.equals("0")) {
					return new PersonalDynamic(obj);
				} else {
					if (!obj.isNull(Protocol.ProtocolKey.KEY_Msg)) {
						String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
						Message msg = handler.obtainMessage();
						msg.obj = m;
						handler.sendMessage(msg);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 删除个人动态
	 * 
	 * @param accountName
	 * @param pwd
	 * @return
	 * @throws JSONException
	 */
	public Boolean deleteDynamic(String dId) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd,
				LlkcBody.PASS_ACCOUNT));
		pairs.add(new BasicNameValuePair("Did", dId));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_Delete_Dynamic);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);

				if (state.equals("0")) {
					return true;
				} else {
					if (!obj.isNull(Protocol.ProtocolKey.KEY_Msg)) {
						String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
						Message msg = handler.obtainMessage();
						msg.obj = m;
						handler.sendMessage(msg);
					}
				}
			}
		}
		return false;
	}

	/**
	 * 动态列表
	 * 
	 * @param touId
	 * @param page
	 * @return
	 * @throws JSONException
	 */
	public List<PersonalDynamic> personalDynamicList(String touId,
			String lastDID) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd,
				LlkcBody.PASS_ACCOUNT));
		pairs.add(new BasicNameValuePair("Touid", touId));
		pairs.add(new BasicNameValuePair("LastId", lastDID));
		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_Dynamic_List);
		if (data != null) {
			JSONArray array = getJSONArrayFromByte(data);
			return PersonalDynamic.getList(array);
		}
		return null;
	}

	/**
	 * 同意/拒绝 入群
	 * 
	 * @param accountName
	 * @param pwd
	 * @param gId
	 * @param uId
	 * @param gPower
	 *            1:同意 2:拒绝
	 * @param gReqMsg
	 * @return
	 * @throws JSONException
	 */
	public boolean operGroupArgOrRef(String accountName, String pwd,
			String gId, String uId, String gPower, String gReqMsg)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));

		pairs.add(new BasicNameValuePair("gId", gId));
		pairs.add(new BasicNameValuePair("uId", uId));
		pairs.add(new BasicNameValuePair("gPower", gPower));
		pairs.add(new BasicNameValuePair("gReqMsg", gReqMsg));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_GroupAgrOrRef);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (state.equals("0")) {
					return true;
				} else {
					Message msg = handler.obtainMessage();
					msg.obj = m;
					handler.sendMessage(msg);
				}
			}
		}
		return false;
	}

	/**
	 * 获取消息列表（除了群聊）
	 * 
	 * @param Optype
	 * @return
	 * @throws JSONException
	 */
	public ArrayList<Msg> getMsgList(String accountName, String pwd,
			String Optype, Context context) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair("Optype", Optype));
		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_MsgList);
		if (data != null) {
			JSONArray array = getJSONArrayFromByte(data);
			if (array != null) {
				return MsgManager.getMsgList(array, context);
			}
		}
		return null;
	}

	/**
	 * 添加或者删除好友( 1 添加 2 删除 3邀请 4同意 5拒绝)
	 * 
	 * @param OpType
	 * @param uId
	 * @return
	 * @throws JSONException
	 */
	public int OperFriend(String accountName, String pwd, String OpType,
			String uId) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair("Optype", OpType));
		pairs.add(new BasicNameValuePair("Touid", uId));
		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_FriendOper);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);

				if (state.equals("0")) {
					return 0;
				} else {
					if (!obj.isNull(Protocol.ProtocolKey.KEY_Msg)) {
						String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
						Message msg = handler.obtainMessage();
						msg.obj = m;
						handler.sendMessage(msg);
					}
					return Integer.parseInt(state);
				}
			}
		}
		return -1;
	}

	/**
	 * 获取群聊消息列表
	 * 
	 * @param accountName
	 * @param pwd
	 * @param Optype
	 * @param context
	 * @param mId
	 * @param gId
	 * @return
	 * @throws JSONException
	 */
	public ArrayList<Msg> getGroupMsgList(String accountName, String pwd,
			String Optype, Context context, String mId, String gId, String count)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair("Optype", Optype));
		pairs.add(new BasicNameValuePair("mId", mId));
		pairs.add(new BasicNameValuePair("gId", gId));
		pairs.add(new BasicNameValuePair("count", count));
		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_GroupMsgList);
		if (data != null) {
			JSONArray array = getJSONArrayFromByte(data);
			if (array != null) {
				return MsgManager.getMsgList(array, context);
			}
		}
		return null;
	}

	/**
	 * 获取所有群聊消息列表
	 * 
	 * @param accountName
	 * @param pwd
	 * @param Optype
	 * @param context
	 * @param mId
	 * @param gId
	 * @return
	 * @throws JSONException
	 */
	public ArrayList<Msg> getGroupMsgTotalList(String accountName, String pwd,
			String Optype, Context context, String mId, String gId)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair("Optype", Optype));
		pairs.add(new BasicNameValuePair("mId", mId));
		pairs.add(new BasicNameValuePair("gId", gId));
		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_GroupMsgList);
		if (data != null) {
			JSONArray array = getJSONArrayFromByte(data);
			if (array != null) {
				return MsgManager.getMsgList(array, context);
			}
		}
		return null;
	}

	/**
	 * 群聊
	 * 
	 * @param accountName
	 * @param pwd
	 * @param msg
	 * @return
	 * @throws JSONException
	 */
	public GroupMsg groupChat(String accountName, String pwd, GroupMsg msg)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair("ToGroupId", msg.gId));
		pairs.add(new BasicNameValuePair("Content", msg.content));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_GroupChat);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (state.equals("0")) {
					if (!obj.isNull("Mid"))
						msg.id = obj.getString("Mid");
					if (!obj.isNull("Time"))
						msg.time = obj.getString("Time");
					return msg;
				} else {
					Message mm = handler.obtainMessage();
					mm.obj = m;
					handler.sendMessage(mm);
				}
			}
		}
		return null;
	}

	public GroupMsg groupImgChat(Context context, String accountName,
			String pwd, Bitmap img, GroupMsg pmsg) throws Exception {
		String request = PlatformAPI.BaseUrl + Protocol.Request_Url_GroupChat
				+ "?Orgid=" + LlkcBody.APP_Orgid + "&Name=" + accountName
				+ "&Pwd=" + pwd + "&ToGroupId=" + pmsg.gId
				+ "&Content=&Type=2&Imgext=jpg";
		String resp = Utility.openUrl(context, request, "POST", img);

		if (resp != null) {
			JSONObject obj = getJSONObjectFromString(resp);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (state.equals("0")) {
					if (!obj.isNull("Mid"))
						pmsg.id = obj.getString("Mid");
					if (!obj.isNull("Time"))
						pmsg.time = obj.getString("Time");
					if (!obj.isNull("ImgPath")) {
						String url = obj.getString("ImgPath");
						pmsg.img_big = url;
						ImageCache.getInstance().setImgKey(url, img);
						if (!obj.isNull("SimgPath")) {
							String surl = obj.getString("SimgPath");
							pmsg.img_small = surl;
						}
						return pmsg;
					}

					return null;
				} else {
					Message msg = handler.obtainMessage();
					msg.obj = m;
					handler.sendMessage(msg);
				}
			}
		}
		return null;
	}

	/**
	 * 私聊
	 * 
	 * @param accountName
	 * @param pwd
	 * @param msg
	 * @param toUid
	 * @return
	 * @throws JSONException
	 */
	public boolean privateChat(String accountName, String pwd, PrivateMsg msg,
			String toUid) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair("Touid", toUid));
		pairs.add(new BasicNameValuePair("Content", msg.content));
		pairs.add(new BasicNameValuePair("Type", "1"));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_PrivateChat);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (!obj.isNull("Mid"))
					msg.id = obj.getString("Mid");
				if (!obj.isNull("Time"))
					msg.time = obj.getString("Time");
				if (state.equals("0")) {
					return true;
				} else {
					Message mm = handler.obtainMessage();
					mm.obj = m;
					handler.sendMessage(mm);
				}
			}
		}
		return false;
	}

	/**
	 * 图片私聊
	 * 
	 * @param context
	 * @param accountName
	 * @param pwd
	 * @param img
	 * @param toUid
	 * @return
	 * @throws Exception
	 */
	public PrivateMsg privateImgChat(Context context, String accountName,
			String pwd, Bitmap img, String toUid, PrivateMsg pmsg)
			throws Exception {
		String request = PlatformAPI.BaseUrl + Protocol.Request_Url_PrivateChat
				+ "?Orgid=" + LlkcBody.APP_Orgid + "&Name=" + accountName
				+ "&Pwd=" + pwd + "&Touid=" + toUid
				+ "&Content=&Type=2&Imgext=jpg";
		String resp = Utility.openUrl(context, request, "POST", img);

		if (resp != null) {
			JSONObject obj = getJSONObjectFromString(resp);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (state.equals("0")) {
					if (!obj.isNull("Mid"))
						pmsg.id = obj.getString("Mid");
					if (!obj.isNull("Time"))
						pmsg.time = obj.getString("Time");
					if (!obj.isNull("ImgPath")) {
						String url = obj.getString("ImgPath");
						pmsg.img_big = url;
						ImageCache.getInstance().setImgKey(url, img);
						if (!obj.isNull("SimgPath")) {
							String surl = obj.getString("SimgPath");
							pmsg.img_small = surl;
						}
						return pmsg;
					}
					return null;
				} else {
					Message msg = handler.obtainMessage();
					msg.obj = m;
					handler.sendMessage(msg);
				}
			}
		}
		return null;
	}

	public PrivateMsg privateVoiceChat(Context context, String accountName,
			String pwd, String toUid, String filePath, PrivateMsg pmsg,
			String filename, String time) throws Exception {
		accountName = URLEncoder.encode(accountName == null ? "" : accountName,
				"utf-8");
		String request = PlatformAPI.BaseUrl + Protocol.Request_Url_PrivateChat
				+ "?Orgid=" + LlkcBody.APP_Orgid + "&Name=" + accountName
				+ "&Pwd=" + pwd + "&Touid=" + toUid
				+ "&Content=&Type=3&Imgext=spx&Length=" + time;
		String resp = Utility.uploadFile(new File(filePath), request, filename);

		if (resp != null) {
			JSONObject obj = getJSONObjectFromString(resp);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (state.equals("0")) {
					if (!obj.isNull("Mid"))
						pmsg.id = obj.getString("Mid");
					if (!obj.isNull("Time"))
						pmsg.time = obj.getString("Time");
					if (!obj.isNull("ImgPath")) {
						String url = obj.getString("ImgPath");
						// pmsg.voice = url;
						return pmsg;
					}
					return null;
				} else {
					Message msg = handler.obtainMessage();
					msg.obj = m;
					handler.sendMessage(msg);
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param accountName
	 * @param pwd
	 * @param toUid
	 *            发送目标的Id
	 * @param type
	 *            发送类型（1：文本 2：图片 3：语音）
	 * @param paramA
	 *            文本 或者 图片地址（小图） 语音地址
	 * @param paramB
	 *            大图地址
	 * @param pmsg
	 * @return
	 * @throws JSONException
	 */
	public PrivateMsg transmitChat_Private(String accountName, String pwd,
			String toUid, String type, String paramA, String paramB,
			PrivateMsg pmsg) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair("Touid", toUid));
		pairs.add(new BasicNameValuePair("ForType", type));
		pairs.add(new BasicNameValuePair("ParamA", paramA));
		pairs.add(new BasicNameValuePair("ParamB", paramB));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_MsgForword);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (!obj.isNull("Mid"))
					pmsg.id = obj.getString("Mid");
				if (!obj.isNull("Time"))
					pmsg.time = obj.getString("Time");
				if (!obj.isNull("ImgPath")) {
					String url = obj.getString("ImgPath");
					pmsg.img_big = url;
					if (!obj.isNull("SimgPath")) {
						String surl = obj.getString("SimgPath");
						pmsg.img_small = surl;
					}
				}
				if (state.equals("0")) {
					return pmsg;
				} else {
					Message mm = handler.obtainMessage();
					mm.obj = m;
					handler.sendMessage(mm);
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param accountName
	 * @param pwd
	 * @param toUid
	 * @param type
	 * @param paramA
	 * @param paramB
	 * @param pmsg
	 * @return
	 * @throws JSONException
	 */
	public GroupMsg transmitChat_Group(String accountName, String pwd,
			String toGroupId, String type, String paramA, String paramB,
			GroupMsg gmsg) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair("ToGroupId", toGroupId));
		pairs.add(new BasicNameValuePair("ForType", type));
		pairs.add(new BasicNameValuePair("ParamA", paramA));
		pairs.add(new BasicNameValuePair("ParamB", paramB));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_MsgForwordByGroup);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (!obj.isNull("Mid"))
					gmsg.id = obj.getString("Mid");
				if (!obj.isNull("Time"))
					gmsg.time = obj.getString("Time");
				if (!obj.isNull("ImgPath")) {
					String url = obj.getString("ImgPath");
					gmsg.img_big = url;
				}
				if (!obj.isNull("SimgPath")) {
					String surl = obj.getString("SimgPath");
					gmsg.img_small = surl;
				}
				if (state.equals("0")) {
					return gmsg;
				} else {
					Message mm = handler.obtainMessage();
					mm.obj = m;
					handler.sendMessage(mm);
				}
			}
		}
		return null;
	}

	public GroupMsg groupVoiceChat(Context context, String accountName,
			String pwd, String toUid, String filePath, GroupMsg pmsg,
			String filename, String time) throws Exception {
		accountName = URLEncoder.encode(
				accountName == null ? "" : accountName.trim(), "utf-8");
		String request = PlatformAPI.BaseUrl + Protocol.Request_Url_GroupChat
				+ "?Orgid=" + LlkcBody.APP_Orgid + "&Name=" + accountName
				+ "&Pwd=" + pwd + "&ToGroupId=" + toUid
				+ "&Content=&Type=3&Imgext=spx&Length=" + time;
		String resp = Utility.uploadFile(new File(filePath), request, filename);

		if (resp != null) {
			JSONObject obj = getJSONObjectFromString(resp);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (state.equals("0")) {
					if (!obj.isNull("Mid"))
						pmsg.id = obj.getString("Mid");
					if (!obj.isNull("Time"))
						pmsg.time = obj.getString("Time");
					if (!obj.isNull("ImgPath")) {
						String url = obj.getString("ImgPath");
						// pmsg.voice = url;
					}

					return pmsg;
				} else {
					Message msg = handler.obtainMessage();
					msg.obj = m;
					handler.sendMessage(msg);
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param Optype
	 * @return
	 * @throws JSONException
	 */
	public ArrayList<ImageItem> getImgItemList(String city)
			throws JSONException {

		try {
			String reponse = ConnectionCaller.doGet(PlatformAPI.PHPBaseUrl
					+ "MsgType=CarouselControllers&city="
					+ URLEncoder.encode(city == null ? "" : city.trim(),
							"utf-8"));

			JSONArray array = getJSONArrayFromString(reponse);
			if (array != null) {
				return ImageItem.getItemList(array);
			}
		} catch (WSError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {

		}

		return null;
	}

	/**
	 * 
	 * @param Optype
	 * @return
	 * @throws JSONException
	 */
	public ArrayList<Obj> getHotCommendType(String city) throws JSONException {

		try {
			String url = PlatformAPI.PHPBaseUrl
					+ "MsgType=hotControllers"
					+ "&city="
					+ URLEncoder.encode(city == null ? "" : city.trim(),
							"utf-8") + "&imei=" + LlkcBody.IMEI;

			String reponse = ConnectionCaller.doGet(url);
			JSONArray array = getJSONArrayFromString(reponse);
			if (array != null) {
				return Type.getList(array);
			}
		} catch (WSError e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {

		}

		return null;
	}

	/**
	 * 
	 * @param reid
	 *            岗位ID
	 * @param city
	 *            城市
	 * @param type
	 *            类型:1:联系岗位 2：收藏岗位 3发信息
	 * @return
	 * @throws JSONException
	 */
	public boolean recordJpost(String reid, String city, String type)
			throws JSONException {

		String reponse;
		try {
			reponse = ConnectionCaller.doGet(PlatformAPI.PHPBaseUrl
					+ "MsgType=ApplicationControllers&reid=" + reid + "&imei="
					+ LlkcBody.IMEI + "&city=" + city + "&type=" + type);
			JSONObject obj = getJSONObjectFromString(reponse);
			if (obj != null) {
				if (!obj.isNull("Stat")) {
					String stat = obj.getString("Stat");
					return stat.equals("0");
				}
			}
		} catch (WSError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 意见反馈
	 * 
	 * @param tel
	 * @param content
	 * @return
	 * @throws JSONException
	 */
	public boolean adviceFeed(String tel, String content) throws JSONException {

		String reponse;
		try {
			reponse = ConnectionCaller.doGet(PlatformAPI.PHPBaseUrl
					+ "MsgType=OpinionControllers&phone=" + tel + "&content="
					+ content);
			JSONObject obj = getJSONObjectFromString(reponse);
			if (obj != null) {
				obj = obj.getJSONObject("List");
				if (obj != null) {
					if (!obj.isNull("state")) {
						String stat = obj.getString("state");
						return "1".equals(stat);
					}
				}
			}
		} catch (WSError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 统计游戏下载
	 * 
	 * @param reid
	 * @return
	 * @throws JSONException
	 */
	public boolean countGameDown(String reid) throws JSONException {

		String reponse;
		try {
			reponse = ConnectionCaller.doGet(PlatformAPI.PHPBaseUrl
					+ "MsgType= Game_tongjiControllers&reid=" + reid);
			Tools.log("IO", "response:" + reponse);
			JSONObject obj = getJSONObjectFromString(reponse);
			if (obj != null) {
				if (!obj.isNull("Stat")) {
					String stat = obj.getString("Stat");
					return stat.equals("0");
				}
			}
		} catch (WSError e) {

		}

		return false;
	}

	/**
	 * 举报
	 * 
	 * @param reid
	 * @param city
	 * @param type
	 * @param content
	 * @return
	 * @throws JSONException
	 */
	public boolean report(String reid, String city, String type, String content)
			throws JSONException {

		String reponse;
		try {
			reponse = ConnectionCaller.doGet(PlatformAPI.PHPBaseUrl
					+ "MsgType=ReportControllers&reid=" + reid + "&type="
					+ type + "&content=" + content + "&city="
					+ URLEncoder.encode(city == null ? "" : city.trim()));
			JSONObject obj = getJSONObjectFromString(reponse);
			if (obj != null) {
				if (!obj.isNull("Stat")) {
					String stat = obj.getString("Stat");
					return stat.equals("0");
				}
			}
		} catch (WSError e) {

		}

		return false;
	}

	/**
	 * 
	 * @param Optype
	 * @return
	 * @throws JSONException
	 */
	public ArrayList<Periodical> getPeriodicalList(String city, String page)
			throws JSONException {

		try {
			String url = PlatformAPI.PHPBaseUrl
					+ "MsgType=NewsControllers&page="
					+ page
					+ "&city="
					+ URLEncoder.encode(city == null ? "" : city.trim(),
							"utf-8");

			String reponse = ConnectionCaller.doGet(url);

			JSONArray array = getJSONArrayFromString(reponse);
			if (array != null) {
				return Periodical.getList(array);
			}
		} catch (WSError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {

		}

		return null;
	}

	/**
	 * 找培训机构列表
	 * 
	 * @param city
	 * @param classId
	 * @return
	 * @throws JSONException
	 */
	public ArrayList<OrganizationPoll> hotClassList(String city, String classId)
			throws JSONException {

		try {
			String url = "http://zhij.51zhixun.com/api.php?Msgtype=listInfo&class_one="
					+ classId
					+ "&cls="
					+ "&city="
					+ URLEncoder.encode(city == null ? "" : city.trim(),
							"utf-8");
			String reponse = ConnectionCaller.doGet(url);

			JSONObject obj = getJSONObjectFromString(reponse);
			if (obj != null) {
				JSONArray array = obj.getJSONArray("List");
				if (array != null) {
					return OrganizationPoll.getOrganizationList(array);
				}
			}
		} catch (WSError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {

		}

		return null;
	}

	/**
	 * 找培训机构详情
	 * 
	 * @param classId
	 * @return
	 * @throws JSONException
	 */
	public OrganizationDetail organizationDetail(String detail_idString)
			throws JSONException {

		try {
			String url = "http://zhij.51zhixun.com/api.php?Msgtype=viewInfo&id="
					+ detail_idString;
			String reponse = ConnectionCaller.doGet(url);

			JSONObject obj = getJSONObjectFromString(reponse);
			if (obj != null) {
				JSONArray array = obj.getJSONArray("List");
				if (array != null && array.length() > 0) {
					obj = array.getJSONObject(array.length() - 1);
				}
				if (obj != null) {
					return new OrganizationDetail(obj);
				}
			}
		} catch (WSError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * 头像墙操作
	 * 
	 * @param type
	 * @param headId
	 * @return
	 * @throws JSONException
	 */
	public String headWall(String type, String headId) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd,
				LlkcBody.PASS_ACCOUNT));
		pairs.add(new BasicNameValuePair("Type", type));
		pairs.add(new BasicNameValuePair("HeadId", headId));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_UserHead);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				if (state.equals("0") && !obj.isNull("Url")) {
					String Url = obj.getString("Url");
					return Url;
				} else {
					if (!obj.isNull(Protocol.ProtocolKey.KEY_Msg)) {
						String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
						Message msg = handler.obtainMessage();
						msg.obj = m;
						handler.sendMessage(msg);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获取推荐岗位列表
	 * 
	 * @param city
	 * @param page
	 * @return
	 * @throws JSONException
	 */
	public List<JobDetail> getRecommendJobList(String city, String page)
			throws JSONException {
		try {
			String url = PlatformAPI.PHPBaseUrl
					+ "MsgType=RecommendencryptControllers"
					+ "&city="
					+ URLEncoder.encode(city == null ? "" : city.trim(),
							"utf-8") + "&imei=" + LlkcBody.IMEI + "&page="
					+ page;

			String reponse = ConnectionCaller.doGet(url);

			JSONArray array = getJSONArrayFromString(reponse);
			if (array != null) {
				return JobDetail.getList(array);
			}
		} catch (WSError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {

		}

		return null;
	}

	/**
	 * 获取岗位列表
	 * 
	 * @param city
	 * @param page
	 * @return
	 * @throws JSONException
	 */
	public List<Jobs.JobNew> getAdvancedList(String city, String page,
			String classone, String classtwo, String salary, String district,
			String keyword) throws JSONException {
		try {
			String url = PlatformAPI.PHPBaseUrl
					+ "MsgType=SelectControllers"
					+ "&imei="
					+ LlkcBody.IMEI
					+ "&classone="
					+ classone
					+ "&classtwo="
					+ classtwo
					+ "&keyword="
					+ keyword
					+ "&salary="
					+ salary
					+ "&district="
					+ district
					+ "&page="
					+ page
					+ "&city="
					+ URLEncoder.encode(city == null ? "" : city.trim(),
							"utf-8");

			String reponse = ConnectionCaller.doGet(url);

			JSONArray array = getJSONArrayFromString(reponse);
			if (array != null) {
				return Jobs.JobNew.getList(array);
			}
		} catch (WSError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {

		}

		return null;
	}

	/**
	 * 获取附近岗位列表
	 * 
	 * @param city
	 * @param page
	 * @return
	 * @throws JSONException
	 */
	public ArrayList<NearJob> getNearJob(String city, String page,
			String classone, String salary, String distance, String lat,
			String lng) throws JSONException {

		String url;
		try {
			url = PlatformAPI.PHPBaseUrl
					+ "MsgType=NearbyControllers"
					+ "&imei="
					+ LlkcBody.IMEI
					+ "&classone="
					+ classone
					+ "&lat="
					+ lat
					+ "&lng="
					+ lng
					+ "&salary="
					+ salary
					+ "&distance="
					+ distance
					+ "&page="
					+ page
					+ "&city="
					+ URLEncoder.encode(city == null ? "" : city.trim(),
							"utf-8");

			String reponse = ConnectionCaller.doGet(url);

			JSONArray array = getJSONArrayFromString(reponse);
			if (array != null) {
				return NearJob.getList(array);
			}
		} catch (WSError e) {

		} catch (UnsupportedEncodingException e) {

		}

		return null;
	}

	/**
	 * 招聘会
	 * 
	 * @param city
	 * @param page
	 * @return
	 * @throws JSONException
	 */
	public ArrayList<JobFair> getJobFair(String city, String page)
			throws JSONException {

		String url;
		try {
			url = PlatformAPI.PHPBaseUrl
					+ "MsgType=JobFairControllers"
					+ "&page="
					+ page
					+ "&city="
					+ URLEncoder.encode(city == null ? "" : city.trim(),
							"utf-8");

			String reponse = ConnectionCaller.doGet(url);

			JSONArray array = getJSONArrayFromString(reponse);
			if (array != null) {
				return JobFair.getList(array);
			}
		} catch (WSError e) {

		} catch (UnsupportedEncodingException e) {

		}

		return null;
	}

	// GameCenter_List_Req.jsp
	public List<Game> getGameList() throws JSONException {

		// String url = PlatformAPI.PHPBaseUrl + "MsgType=GameControllers"
		// + "&uid=" + uId + "&imei=" + LlkcBody.IMEI;
		//
		// String reponse = ConnectionCaller.doGet(url);
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd,
				LlkcBody.PASS_ACCOUNT));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_GameList);
		JSONArray array = getJSONArrayFromByte(data);
		if (array != null) {
			return Game.getList(array);
		}
		return null;
	}

	public List<Game> getGameList2() throws JSONException {

		String url = PlatformAPI.PHPBaseUrl + "MsgType=GameControllers"
				+ "&uid=" + LlkcBody.UID_ACCOUNT + "&imei=" + LlkcBody.IMEI;

		String reponse;
		try {
			reponse = ConnectionCaller.doGet(url);

			// ArrayList<BasicNameValuePair> pairs = new
			// ArrayList<BasicNameValuePair>();
			//
			// pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
			// LlkcBody.APP_Orgid));
			// pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
			// LlkcBody.USER_ACCOUNT));
			// pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd,
			// LlkcBody.PASS_ACCOUNT));
			//
			// byte[] data = Connection.getDataSync(null, pairs,
			// Protocol.Request_Url_GameList);
			JSONArray array = getJSONArrayFromString(reponse);
			if (array != null) {
				return Game.getList(array);
			}
		} catch (WSError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean ModifyGroupInfo(Group group) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				LlkcBody.USER_ACCOUNT));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd,
				LlkcBody.PASS_ACCOUNT));
		pairs.add(new BasicNameValuePair("gId", group.getgId()));
		pairs.add(new BasicNameValuePair("gName", group.gName));
		pairs.add(new BasicNameValuePair("gContent", group.getgContent()));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_modifyGroupInfo);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);

				if (state.equals("0")) {
					return true;
				} else {
					if (!obj.isNull(Protocol.ProtocolKey.KEY_Msg)) {
						String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
						Message msg = handler.obtainMessage();
						msg.obj = m;
						handler.sendMessage(msg);
					}
				}
			}
		}
		return false;
	}

	/**
	 * 操作简历
	 * 
	 * @param accountName
	 * @param pwd
	 * @param type
	 * @param resume
	 * @return
	 * @throws JSONException
	 */
	public boolean OperResume(String accountName, String pwd, String type,
			Resume resume) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_type, type));
		pairs.add(new BasicNameValuePair("rId", resume.id));
		pairs.add(new BasicNameValuePair("resumeName", resume.name
				+ Tools.getStringFromRes(mContext, R.string.jianli)));
		pairs.add(new BasicNameValuePair("photo", resume.head));
		pairs.add(new BasicNameValuePair("rName", resume.rName));
		pairs.add(new BasicNameValuePair("sex", resume.sex));
		pairs.add(new BasicNameValuePair("dateOfBirth", resume.birth));
		pairs.add(new BasicNameValuePair("education", resume.education));
		pairs.add(new BasicNameValuePair("workExperience",
				resume.workExperience));
		pairs.add(new BasicNameValuePair("phoneNumber", resume.phone));
		pairs.add(new BasicNameValuePair("position", resume.expectPost));
		pairs.add(new BasicNameValuePair("positionId", resume.expectPostId));
		pairs.add(new BasicNameValuePair("salary", resume.expectSalary));
		pairs.add(new BasicNameValuePair("evaluation", resume.evaluation));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_ResumeOper);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (state.equals("0")) {
					return true;
				} else {
					Message mm = handler.obtainMessage();
					mm.obj = m;
					handler.sendMessage(mm);
				}
			}
		}
		return false;
	}

	/**
	 * 修改资料
	 * 
	 * @param account
	 * @return
	 * @throws JSONException
	 */
	public boolean modifyUser(Account account) throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name, account
				.getArccount()));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, account
				.getPrassWord()));
		pairs.add(new BasicNameValuePair("Sex", account.getSex()));
		pairs.add(new BasicNameValuePair("Local", account.getLocal()));
		pairs.add(new BasicNameValuePair("Sign", account.getSign()));
		pairs.add(new BasicNameValuePair("Birth", account.getBirth()));
		pairs.add(new BasicNameValuePair("Email", account.getEmail()));
		pairs.add(new BasicNameValuePair("Phone", account.getPhone()));
		pairs.add(new BasicNameValuePair("Prof", account.getProf()));
		pairs.add(new BasicNameValuePair("Cert", account.getCert()));
		if ("".equals(account.getNname()) || account.getNname() == null) {
			pairs.add(new BasicNameValuePair("Uname", account.getUname()));
			pairs.add(new BasicNameValuePair("Nname", account.getUname()));
		} else {
			pairs.add(new BasicNameValuePair("Uname", account.getUname()));
			pairs.add(new BasicNameValuePair("Nname", account.getNname()));
			account.setUname(account.getNname());
		}

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_ModifyUser);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				if (state.equals("0")) {
					return true;
				} else {
					if (!obj.isNull(Protocol.ProtocolKey.KEY_Msg)) {
						String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
						Message msg = handler.obtainMessage();
						msg.obj = m;
						handler.sendMessage(msg);
					}
				}
			}
		}
		return false;
	}

	public String postResumeImg(Context context, String accountName,
			String pwd, Bitmap img) throws Exception {
		String resp = Utility.openUrl(context, PlatformAPI.BaseUrl
				+ Protocol.Request_Url_ResumeImgOper + "?Orgid="
				+ LlkcBody.APP_Orgid + "&Name=" + accountName + "&Pwd=" + pwd
				+ "&Imgext=jpg", "POST", img);

		if (resp != null) {
			JSONObject obj = getJSONObjectFromString(resp);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);
				String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
				if (state.equals("0")) {
					if (!obj.isNull(Protocol.ProtocolKey.KEY_Url)) {
						String url = obj
								.getString(Protocol.ProtocolKey.KEY_Url);
						if (url != null && !url.equals("")) {
							return url;
						}
						// TODO
					}
					return null;
				} else {
					Message msg = handler.obtainMessage();
					msg.obj = m;
					handler.sendMessage(msg);
				}
			}
		}
		return null;
	}

	public boolean postLocationInfo(String uId, String city, String Lat,
			String Longs) throws Exception {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair("Imei", LlkcBody.IMEI));
		pairs.add(new BasicNameValuePair("City", city));
		pairs.add(new BasicNameValuePair("Uid", uId));
		pairs.add(new BasicNameValuePair("Lat", Lat));
		pairs.add(new BasicNameValuePair("Longs", Longs));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_PostLocation);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				String state = obj.getString(Protocol.ProtocolKey.KEY_Stat);

				if (state.equals("0")) {
					return true;
				} else {
					if (!obj.isNull(Protocol.ProtocolKey.KEY_Msg)) {
						String m = obj.getString(Protocol.ProtocolKey.KEY_Msg);
						Message msg = handler.obtainMessage();
						msg.obj = m;
						handler.sendMessage(msg);
					}
				}
			}
		}
		return false;
	}

	public Resume getResumeInfo(String accountName, String pwd, String rId)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		pairs.add(new BasicNameValuePair("Rid", rId));

		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_ResumeInfo);
		if (data != null) {
			JSONObject obj = getJSONObjectFromByte(data);
			if (obj != null) {
				return new Resume(obj);
			}
		}
		return null;
	}

	public List<Resume> getResumeList(String accountName, String pwd)
			throws JSONException {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Orgid,
				LlkcBody.APP_Orgid));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Name,
				accountName));
		pairs.add(new BasicNameValuePair(Protocol.ProtocolKey.KEY_Pwd, pwd));
		byte[] data = Connection.getDataSync(null, pairs,
				Protocol.Request_Url_ResumeList);
		if (data != null) {
			JSONArray array = getJSONArrayFromByte(data);
			if (array != null) {
				return Resume.getResumeList(array);
			}
		}
		return null;
	}

	public JSONArray getJSONArrayFromByte(byte[] data) throws JSONException {
		if (data == null) {
			return null;
		}
		String json = new String(data);
		json = json.trim();

		if (!json.substring(0, 1).equals("{")) {
			return null;
		}
		Tools.log("IO", "Result : " + json);
		JSONTokener jsonParser = new JSONTokener(json);
		Object obj = null;
		do {
			obj = jsonParser.nextValue();
		} while (obj instanceof String && jsonParser.more());
		if (obj instanceof JSONObject) {
			JSONObject jsonObj = (JSONObject) obj;
			// JSONObject jsonObj = (JSONObject) jsonParser.nextValue();
			if (!jsonObj.isNull(Protocol.ProtocolKey.KEY_List))
				return jsonObj.getJSONArray(Protocol.ProtocolKey.KEY_List);
		}
		return null;
	}

	private JSONObject getJSONObjectFromByte(byte[] data) throws JSONException {
		if (data == null) {
			return null;
		}
		String json = new String(data);
		json = json.trim();
		if (json.equals(""))
			return null;
		Tools.log("IO", "Result : " + json);
		if (!json.substring(0, 1).equals("{")) {
			return null;
		}

		JSONTokener jsonParser = new JSONTokener(json);

		Object obj = null;
		do {
			obj = jsonParser.nextValue();
		} while (obj instanceof String && jsonParser.more());
		if (obj instanceof JSONObject) {
			JSONObject jsonObj = (JSONObject) obj;
			if (!jsonObj.isNull("Stat")) {
				String stat = jsonObj.getString("Stat");
				if (stat.equals("-99")) {
					Message msg = handler.obtainMessage();
					msg.obj = Tools.getStringFromRes(mContext,
							R.string.connect_timeout);
					handler.sendMessage(msg);
					return null;
				}
			}
			return jsonObj;
		}
		return null;
	}

	public JSONArray getJSONArrayFromString(String resp) throws JSONException {
		if (resp == null)
			return null;
		resp = resp.trim();
		if (resp.equals(""))
			return null;

		if (!resp.substring(0, 1).equals("{")) {
			return null;
		}
		Tools.log("IO", "Result : " + resp);
		JSONTokener jsonParser = new JSONTokener(resp);
		Object obj = null;
		do {
			obj = jsonParser.nextValue();
		} while (obj instanceof String && jsonParser.more());
		if (obj instanceof JSONObject) {
			JSONObject jsonObj = (JSONObject) obj;
			// JSONObject jsonObj = (JSONObject) jsonParser.nextValue();
			if (!jsonObj.isNull(Protocol.ProtocolKey.KEY_List))
				return jsonObj.getJSONArray(Protocol.ProtocolKey.KEY_List);
		}
		return null;
	}

	public JSONObject getJSONObjectFromString(String resp) throws JSONException {
		if (resp == null)
			return null;
		resp = resp.trim();
		if (resp.length() == 0)
			return null;

		if (!resp.substring(0, 1).equals("{")) {
			return null;
		}
		Tools.log("IO", "Result : " + resp);
		JSONTokener jsonParser = new JSONTokener(resp);

		Object obj = null;
		do {
			obj = jsonParser.nextValue();
		} while (obj instanceof String && jsonParser.more());
		if (obj instanceof JSONObject) {
			JSONObject jsonObj = (JSONObject) obj;
			if (!jsonObj.isNull("Stat")) {
				String stat = jsonObj.getString("Stat");
				if (stat.equals("-99")) {
					Message msg = handler.obtainMessage();
					msg.obj = Tools.getStringFromRes(mContext,
							R.string.connect_timeout);
					handler.sendMessage(msg);
					return null;
				}
			}
			return jsonObj;
		}
		return null;
	}

	private static Map<String, SimpleDateFormat> formatMap = new HashMap<String, SimpleDateFormat>();

	public static Date parseDate(String str, String format) {
		if (str == null || "".equals(str)) {
			return null;
		}
		SimpleDateFormat sdf = formatMap.get(format);
		if (null == sdf) {
			sdf = new SimpleDateFormat(format, Locale.ENGLISH);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			formatMap.put(format, sdf);
		}
		try {
			synchronized (sdf) {
				// SimpleDateFormat is not thread safe
				return sdf.parse(str);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}
