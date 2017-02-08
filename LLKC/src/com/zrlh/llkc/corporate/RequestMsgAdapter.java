package com.zrlh.llkc.corporate;

import java.util.List;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.zrlh.llkc.R;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.db.BaseMsgTable;
import com.zzl.zl_app.db.RequestMsgDBOper;
import com.zzl.zl_app.entity.Msg;
import com.zzl.zl_app.entity.RequestMsg;
import com.zzl.zl_app.util.TimeUtil;

/*
 * 请求信息适配
 * */
public class RequestMsgAdapter extends BaseAdapter {

	private List<Msg> msgList;
	Context context;
	LayoutInflater inflater;

	public RequestMsgAdapter(Context context, List<Msg> msgList) {
		this.msgList = msgList;
		this.context = context;
		inflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {

		return msgList.size();
	}

	@Override
	public Object getItem(int position) {
		return msgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_resqmsg, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView
					.findViewById(R.id.item_msg_tv_name);
			holder.content = (TextView) convertView
					.findViewById(R.id.item_msg_tv_content);
			holder.agree = (Button) convertView
					.findViewById(R.id.item_msg_btn_agree);
			holder.time = (TextView) convertView
					.findViewById(R.id.item_resqmsg_tv_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final RequestMsg msg = (RequestMsg) msgList.get(position);
		if (msg != null) {
			if (msg.sRName == null || "".equals(msg.sRName))
				holder.name.setText(msg.senderName);
			else
				holder.name.setText(msg.sRName);
			// holder.name.setText(msg.uName);
			holder.content.setText(msg.content);
		}
		if (msg.type.equals("-100")) {
			holder.agree.setText(R.string.have_agree);
			holder.agree.setBackgroundColor(R.color.gray);
		} else if (msg.type.equals("-200")) {
			holder.agree.setText(R.string.have_agree);
			holder.agree.setBackgroundColor(R.color.gray);
		} else {
			holder.agree.setText(R.string.agree);
			holder.agree
					.setBackgroundResource(R.drawable.bg_btn_gray2_selector);
		}
		holder.agree.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO
				String type = msg.type;
				if (type.equals("10")) {
					// 入群请求
					new OperGroupTask(position, msg.id, msg.senderId, "1", "")
							.execute();
				} else if (type.equals("4")) {
					// 加好友请求
					new OperFriendTask(position, "4", msg.senderId).execute();
				}
			}
		});
		holder.time.setText(TimeUtil.getLocalTimeString(Long
				.parseLong(msg.time)));
		return convertView;
	}

	public void update() {
		this.notifyDataSetChanged();
	}

	class OperFriendTask extends AsyncTask<Object, Integer, Integer> {
		String operType;
		String uId;
		int position;

		public OperFriendTask(int position, String operType, String uId) {
			this.operType = operType;
			this.uId = uId;
			this.position = position;
		}

		@Override
		protected Integer doInBackground(Object... params) {
			try {
				return Community.getInstance(context).OperFriend(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT, operType,
						uId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return -1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO
			if (result != -1 || operType.equals("5")) {
				Msg msg = msgList.get(position);
				msg.type = "-100";
				msgList.set(position, msg);
				RequestMsgDBOper requestOper = (RequestMsgDBOper) RequestMsgDBOper
						.getDBOper(context);
				requestOper.updateMsg(msg, requestOper.getTableName(""));
				update();
			}
			super.onPostExecute(result);
		}
	}

	class OperGroupTask extends AsyncTask<Object, Integer, Boolean> {

		private String gId;
		private String uId;
		private String gPower;
		private String gReqMsg;
		private int position;

		public OperGroupTask(int position, String gId, String uId,
				String gPower, String gReqMsg) {
			this.position = position;
			this.gId = gId;
			this.uId = uId;
			this.gPower = gPower;
			this.gReqMsg = gReqMsg;
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			try {
				return Community.getInstance(context).operGroupArgOrRef(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT, gId, uId,
						gPower, gReqMsg);
			} catch (JSONException e) {

			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result || gPower.equals("2")) {
				// TODO
				if (position < msgList.size()) {
					RequestMsg msg = (RequestMsg) msgList.get(position);
					msg.type = "-200";
					msgList.set(position, msg);
					RequestMsgDBOper requestOper = (RequestMsgDBOper) RequestMsgDBOper
							.getDBOper(context);
					requestOper.updateMsg(msg, requestOper.getTableName(""));
					update();
				}
			}
			super.onPostExecute(result);
		}
	}

	class ViewHolder {
		Button agree;
		TextView time;
		TextView content;
		TextView name;
	}

}
