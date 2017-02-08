package com.zrlh.llkc.corporate;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zrlh.llkc.R;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.db.BaseMsgTable;
import com.zzl.zl_app.db.MTable;
import com.zzl.zl_app.entity.MMsg;
import com.zzl.zl_app.entity.Msg;
import com.zzl.zl_app.util.TextUtil;
import com.zzl.zl_app.util.TimeUtil;

/*
 * 信息适配
 * */
public class MsgAdapter extends BaseAdapter {

	private List<Msg> msgList;
	Context context;
	LayoutInflater inflater;

	public MsgAdapter(Context context, List<Msg> msgList) {
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

	private int deletePosition = -1;

	public void setDeletePosition(int position) {
		this.deletePosition = position;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_msg, null);
			holder = new ViewHolder();

			holder.head = (ImageView) convertView
					.findViewById(R.id.item_friend_imgv_head);
			holder.name = (TextView) convertView
					.findViewById(R.id.item_friend_tv_name);
			holder.time = (TextView) convertView
					.findViewById(R.id.item_friend_tv_time);
			holder.content = (TextView) convertView
					.findViewById(R.id.item_friend_tv_content);
			holder.not = (TextView) convertView
					.findViewById(R.id.item_friend_tv_not);
			holder.delete = (Button) convertView
					.findViewById(R.id.item_msg_delete_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (deletePosition > 0 && position == deletePosition)
			holder.delete.setVisibility(View.VISIBLE);
		else
			holder.delete.setVisibility(View.GONE);
		holder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MMsg msg = (MMsg) getItem(deletePosition);
				MTable oper = (MTable) MTable.getDBOper(context);
				oper.delete(oper.getTableName(""), BaseMsgTable.Msg_Account
						+ "=? and " + MTable.MTable_Label + "=?", new String[] {
						LlkcBody.USER_ACCOUNT == null ? ""
								: LlkcBody.USER_ACCOUNT, msg.label });
				if (deletePosition < msgList.size())
					msgList.remove(deletePosition);
				deletePosition = -1;
				notifyDataSetChanged();
			}
		});

		final MMsg msg = (MMsg) msgList.get(position);
		if (msg.newitems > 0) {
			holder.not.setVisibility(View.VISIBLE);
			holder.not.setText(msg.newitems + "");
		} else {
			holder.not.setVisibility(View.GONE);
		}

		holder.head.setTag(msg.label);
		// setImage(holder.head, R.drawable.head_default);
		if (msg.label.equals("-3")) {
			setImage(holder.head, R.drawable.msgcenter_item_head_newjpost);
			if (!LlkcBody.haveCheckJpostRecommend) {
				holder.not.setVisibility(View.VISIBLE);
				holder.not.setText("new");
			} else {
				holder.not.setVisibility(View.GONE);
			}
		} else if (msg.label.equals("-2")) {
			setImage(holder.head, R.drawable.verify);
		} else if (msg.label.equals("-1")) {
			setImage(holder.head, R.drawable.msgcenter_item_head_sys);
		} else {
			if ("7".equals(msg.type)) {

				// ImageView head = (ImageView)
				// parent.findViewWithTag(msg.label);
				ImageCache.getInstance().loadImg(msg.head, msg.label, parent,
						R.drawable.msgcenter_item_head_fgroup);
				// setImage(head, R.drawable.msgcenter_item_head_fgroup);
			} else if ("2".equals(msg.type)) {
				String head = msg.head;
				ImageView headImgV = (ImageView) parent
						.findViewWithTag(msg.label);
				setImage(headImgV, R.drawable.head_default);
				if (head != null && !head.equals("")) {
					ImageCache.getInstance().loadImg(head, msg.label, parent,
							R.drawable.head_default);
				} else {
					setImage(holder.head, R.drawable.head_default);
				}
			}
		}

		holder.head.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO
			}
		});
		if (msg.lName == null || "".equals(msg.lName))
			holder.name.setText(msg.senderName);
		else
			holder.name.setText(msg.lName);
		// holder.name.setText(msg.uName);
		try {
			if (msg.time != null) {
				holder.time.setText(TimeUtil.getTimeStr(Long.parseLong(msg.time
						.trim())));
				if (msg.label.equals("-3"))
					holder.time.setText(TimeUtil.getTimeStr2(msg.time,
							"yyyy-MM-dd"));
			}
		} catch (Exception e) {
		}
		String cont = msg.content;
		if (cont != null && cont.length() > 15)
			cont = cont.substring(0, 14) + "...";
		SpannableString c = TextUtil.formatContent(cont, context);
		if ("7".equals(msg.type)) {
			holder.content.setText(msg.senderName + ":" + (c == null ? "" : c));
		} else
			holder.content.setText(c == null ? "" : c);
		return convertView;
	}

	class ViewHolder {
		ImageView head;
		TextView name;
		TextView content;
		TextView time;
		TextView not;
		Button delete;
	}

	Handler handler = new Handler();

	public void setImage(final ImageView imgv, final Bitmap bm) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				imgv.setImageBitmap(bm);
			}
		});
	}

	public void setImage(final ImageView imgv, final int imageResId) {
		if (imgv == null)
			return;
		handler.post(new Runnable() {

			@Override
			public void run() {
				imgv.setImageResource(imageResId);
				imgv.postInvalidate();
			}
		});
	}
}
