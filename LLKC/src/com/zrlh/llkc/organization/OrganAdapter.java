package com.zrlh.llkc.organization;

import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zrlh.llkc.R;
import com.zzl.zl_app.util.Tools;

public class OrganAdapter extends BaseAdapter {
	List<Organization.OrganizationPoll> organizationPolls;
	LayoutInflater layoutInflater;
	FinalBitmap finalBitmap;
	OrganView organView;
	private Context context;
	private final ImageDownloader imageDownloader = new ImageDownloader();

	public final class OrganView {
		TextView org_nameTextView;
		TextView org_typTextView;
		TextView org_majorTextView;
		ImageView headView;

	}

	public OrganAdapter(Context context,
			List<Organization.OrganizationPoll> organizationPolls) {
		this.context = context;
		layoutInflater = layoutInflater.from(context);
		this.organizationPolls = organizationPolls;
		finalBitmap = FinalBitmap.create(context);
		finalBitmap.configLoadingImage(R.drawable.icon);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return organizationPolls.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return organizationPolls.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public String getorgString(int position) {

		return organizationPolls.get(position).getOrgid();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			organView = new OrganView();
			convertView = layoutInflater.inflate(R.layout.organitem, null);
			organView.org_nameTextView = (TextView) convertView
					.findViewById(R.id.org_name);
			organView.org_typTextView = (TextView) convertView
					.findViewById(R.id.org_type);
			organView.org_majorTextView = (TextView) convertView
					.findViewById(R.id.org_major);
			organView.headView = (ImageView) convertView
					.findViewById(R.id.headView);

			convertView.setTag(organView);
		} else {
			organView = (OrganView) convertView.getTag();

		}
		// imageDownloader.download(organizationPolls.get(position).getImg(),
		// organView.headView);
		Organization.OrganizationPoll poll = organizationPolls.get(position);
		String imgUrl = poll.getImg();
		if (Tools.isUrl(imgUrl))
			finalBitmap.display(organView.headView, imgUrl);
		else
			organView.headView.setImageResource(R.drawable.icon);
		organView.org_nameTextView.setText(poll.getName());
		organView.org_typTextView.setText(poll.getType());
		organView.org_majorTextView.setText(Tools.getStringFromRes(context,
				R.string.main_major) + poll.getCourse());

		return convertView;

	}

}
