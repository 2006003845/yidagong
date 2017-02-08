package com.zrlh.llkc.corporate;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.corporate.base.MyToast;
import com.zrlh.llkc.funciton.JobDetail;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.connection.Community;
import com.zzl.zl_app.net_port.Get2ApiImpl;
import com.zzl.zl_app.net_port.WSError;
import com.zzl.zl_app.util.Tools;

/*
 * 岗位修改
 * */
public class JpostEditActivity extends BaseActivity {

	public static final String Tag = "jpost_edit";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, JpostEditActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	JobDetail jPost;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(Tag, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		context = this;
		setContentView(R.layout.jpost_edit);
		initView();
		new initDataTask().execute();

		Bundle b = getIntent().getExtras();
		if (b != null) {
			jPost = (JobDetail) b.getSerializable("jpost");
			// init data
			if (jPost != null) {
				state = State_Edit;
				updateView();
			}
		}
	}

	private int state = State_Publish;
	private static final int State_Publish = 1;
	private static final int State_Edit = 2;
	ProgressDialog dialog = null;

	// @Override
	// protected void onNewIntent(Intent intent) {
	// super.onNewIntent(intent);
	//
	//
	// }

	public void updateView() {
		if (state == State_Edit) {
			nameET.setText(jPost.name);
			nameET.setEnabled(false);
			numET.setText(jPost.peonumber);
			addrET.setText(jPost.address);
			desribET.setText(jPost.description == null
					|| jPost.demand.equals("null") ? "" : jPost.description);
			demandET.setText(jPost.demand == null
					|| jPost.demand.equals("null") ? "" : jPost.demand);
			linkmanET.setText(jPost.contacts_name);
			phoneET.setText(jPost.tel);
			String id = "0";
			if ("10000".equals(jPost.salary_max)) {
				id = "4";
			}
			salary = new Salary(id, jPost.salary_min + "-" + jPost.salary_max);
			salaryTV.setText(salary.name);
			// typeTV.setText(jPost.classify);

			// deadlineTV.setText(jPost.deadline);
			tagTV.setText(jPost.tags);
			areaTV.setText(jPost.area);

		}
	}

	private TextView titleTV;

	private EditText nameET, numET, addrET, desribET, demandET, linkmanET,
			phoneET;
	private TextView salaryTV, typeTV, deadlineTV, tagTV, areaTV;

	private ArrayList<Obj> salaryList = new ArrayList<Obj>();
	private ArrayList<Obj> tagList = new ArrayList<Obj>();
	private ArrayList<Obj> deadlineList = new ArrayList<Obj>();

	private void initData() {
		salaryList.clear();
		salaryList.add(new Salary("1", Tools.getStringFromRes(context,
				R.string.salary1)));
		salaryList.add(new Salary("2", Tools.getStringFromRes(context,
				R.string.salary2)));
		salaryList.add(new Salary("3", Tools.getStringFromRes(context,
				R.string.salary3)));
		salaryList.add(new Salary("4", Tools.getStringFromRes(context,
				R.string.salary4)));

		tagList.clear();
		tagList.add(new Tag("1", Tools.getStringFromRes(context, R.string.tag1)));
		tagList.add(new Tag("2", Tools.getStringFromRes(context, R.string.tag2)));
		tagList.add(new Tag("3", Tools.getStringFromRes(context, R.string.tag3)));
		tagList.add(new Tag("4", Tools.getStringFromRes(context, R.string.tag4)));
		tagList.add(new Tag("5", Tools.getStringFromRes(context, R.string.tag5)));
		tagList.add(new Tag("6", Tools.getStringFromRes(context, R.string.tag6)));
		tagList.add(new Tag("7", Tools.getStringFromRes(context, R.string.tag7)));

		if (ApplicationData.typeLists.size() == 0) {
			Get2ApiImpl get = new Get2ApiImpl(getContext());
			try {
				// ArrayList<HashMap<Obj, ArrayList<Obj>>> lists = get
				// .getTypeMapList(Get2ApiImpl.From_Assert,
				// "classify.json");
				ArrayList<HashMap<Obj, ArrayList<Obj>>> lists = get
						.getTypeMapList(Get2ApiImpl.From_Net,
								"http://nan.51zhixun.com/classify.json");
				if (lists != null && lists.size() > 0) {
					ApplicationData.typeLists.clear();
					ApplicationData.typeLists.addAll(lists);
				} else {
					lists = get.getTypeMapList(Get2ApiImpl.From_Assert,
							"classify.json");
					if (lists != null && lists.size() > 0) {
						ApplicationData.typeLists.clear();
						ApplicationData.typeLists.addAll(lists);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WSError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		deadlineList.clear();
		Deadline d = new Deadline("1", Tools.getStringFromRes(context,
				R.string.deadline1));
		d.months = 0.5 + "";
		deadlineList.add(d);
		Deadline d1 = new Deadline("1", Tools.getStringFromRes(context,
				R.string.deadline2));
		d1.months = 1 + "";
		deadlineList.add(d1);
		Deadline d2 = new Deadline("2", Tools.getStringFromRes(context,
				R.string.deadline3));
		d2.months = 2 + "";
		deadlineList.add(d2);
		Deadline d3 = new Deadline("3", Tools.getStringFromRes(context,
				R.string.deadline4));
		d3.months = 3 + "";
		deadlineList.add(d3);
	}

	private String tag = null;
	private Deadline dl;
	Salary salary;

	private void initView() {
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText(R.string.publish_job_info);
		this.findViewById(R.id.btn).setVisibility(View.GONE);
		nameET = (EditText) this.findViewById(R.id.jpost_et_name);

		numET = (EditText) this.findViewById(R.id.jpost_et_num);
		addrET = (EditText) this.findViewById(R.id.jpost_et_addr);
		desribET = (EditText) this.findViewById(R.id.jpost_et_describ);
		// desribET.addTextChangedListener(new TextWatcher() {
		// String text = "";
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before,
		// int count) {
		// int size = desribET.getText().length();
		// if (size <= 150) {
		// text = desribET.getText().toString().trim();
		// }
		// if (size > 150) {
		// desribET.setText(text);
		// }
		// // Spanned text1 = Html.fromHtml(" <font color='#b92d2d'>"
		// // + (150 - size) + "</font>" + "/150");
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		// }
		// });
		demandET = (EditText) this.findViewById(R.id.jpost_et_demand);
		linkmanET = (EditText) this.findViewById(R.id.jpost_et_linkman);
		phoneET = (EditText) this.findViewById(R.id.jpost_et_phone);
		salaryTV = (TextView) this.findViewById(R.id.jpost_tv_salary);
		typeTV = (TextView) this.findViewById(R.id.jpost_tv_classify);
		deadlineTV = (TextView) this.findViewById(R.id.jpost_tv_days);
		tagTV = (TextView) this.findViewById(R.id.jpost_tv_tag);
		areaTV = (TextView) this.findViewById(R.id.jpost_tv_area);
		salaryTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PullDownList pullDownList = new PullDownList(getContext(),
						getContext().findViewById(R.id.jpost_layout_salary),
						salaryTV, salaryList);
				pullDownList
						.setPullOnItemClickListener(new PullOnItemClickListener() {

							@Override
							public void onPullItemClick(AdapterView<?> parent,
									int position) {
								salary = (Salary) salaryList.get(position);
								salaryTV.setText(salary.name);
							}
						});
				pullDownList.popupWindwShowing();
			}
		});
		typeTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), Level1Activity.class);
				Bundle b = new Bundle();
				b.putSerializable("list", ApplicationData.typeLists);
				intent.putExtras(b);
				startActivityForResult(intent, 100);
			}
		});
		areaTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(),
						CityLevel1Activity.class);
				startActivityForResult(intent, 101);
			}
		});
		deadlineTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PullDownList pullDownList2 = new PullDownList(getContext(),
						getContext().findViewById(R.id.jpost_layout_days),
						deadlineTV, deadlineList);
				pullDownList2
						.setPullOnItemClickListener(new PullOnItemClickListener() {

							@Override
							public void onPullItemClick(AdapterView<?> parent,
									int position) {
								Deadline c = (Deadline) deadlineList
										.get(position);
								dl = c;
								deadlineTV.setText(c.name);
							}
						});
				pullDownList2.popupWindwShowing();
			}
		});
		tagTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PullDownList pullDownList3 = new PullDownList(getContext(),
						getContext().findViewById(R.id.jpost_layout_tag),
						tagTV, tagList);
				pullDownList3
						.setPullOnItemClickListener(new PullOnItemClickListener() {

							@Override
							public void onPullItemClick(AdapterView<?> parent,
									int position) {
								Tag c = (Tag) tagList.get(position);
								String t = tagTV.getText().toString();
								if (tag == null) {
									tagTV.setText(c.name);
									tag = tagTV.getText().toString();
								} else
									tagTV.setText(t + "," + c.name);
							}
						});
				pullDownList3.popupWindwShowing();
			}
		});
		this.findViewById(R.id.jpost_btn_commit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO
						// TODO
						String name = nameET.getText().toString();
						String peoNum = numET.getText().toString();
						String addr = addrET.getText().toString();
						String description = desribET.getText().toString();
						String demand = demandET.getText().toString();
						String linkman = linkmanET.getText().toString();
						String phone = phoneET.getText().toString();
						// String salary = salaryTV.getText().toString();
						// String type = typeTV.getText().toString();
						String deadline = deadlineTV.getText().toString();
						String tag = tagTV.getText().toString();
						String area = areaTV.getText().toString();

						if (name == null || name.equals("")) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(context,
											R.string.input_gwname));
							return;
						}
						if (peoNum == null || peoNum.equals("")) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(context,
											R.string.input_recnum));
							return;
						}

						if (description == null || description.equals("")
								|| description.length() < 10) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(context,
											R.string.gw_consmall));
							return;
						}
						if (linkman == null || linkman.equals("")) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(context,
											R.string.input_telname));
						}
						if (demand == null || demand.equals("")
								|| demand.length() < 20) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(context,
											R.string.gw_yqsmall));
							return;
						}
						if (phone == null || phone.equals("")) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(context,
											R.string.input_telphone));
							return;
						}
						if (salary == null) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(context,
											R.string.gw_salary));
							return;
						}
						if (type == null) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(context,
											R.string.gw_type));
							return;
						}
						if (dl == null) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(context,
											R.string.gw_dl));
							return;
						}
						if (area == null || area.equals("")) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(context,
											R.string.gw_city));
							return;
						}
						if (addr == null || addr.equals("")) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(context,
											R.string.gw_address));
							return;
						}
						if (addr.length() > 39) {
							MyToast.getToast().showToast(
									getContext(),
									Tools.getStringFromRes(context,
											R.string.arer_long));
						}
						String s = "";
						if (salary.id.equals("4")) {
							s = "6000-10000";
						} else {
							s = salary.name;
						}
						new CommitJPostTask(name, peoNum, addr, description,
								demand, linkman, phone, s, key.id + "-"
										+ type.id, dl.months, tag, area)
								.execute();
					}
				});

	}

	Type key;
	Type type;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100) {
			if (data != null) {
				Bundle b = data.getExtras();
				if (b != null) {
					key = (Type) (Obj) b.getSerializable("key");
					type = (Type) (Obj) b.getSerializable("obj");
					if (type != null)
						typeTV.setText(key.name + "-" + type.name);
				}
			}
		} else if (requestCode == 101) {
			if (data != null) {
				Bundle b = data.getExtras();
				if (b != null) {
					Obj key = (Obj) b.getSerializable("key");
					Obj city = (Obj) b.getSerializable("obj");
					if (city != null)
						areaTV.setText(key.name + "-" + city.name);
				}
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(Tag);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(Tag);
	}
	
	@Override
	public void onBackPressed() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		closeOneAct(Tag);
		super.onBackPressed();
	}

	class CommitJPostTask extends AsyncTask<Object, Integer, Boolean> {

		String name;
		String peoNum;
		String addr;
		String description;
		String demand;
		String linkman;
		String phone;
		String salary;
		String type;
		String deadline;
		String tag;
		String area;

		public CommitJPostTask(String name, String peoNum, String addr,
				String description, String demand, String linkman,
				String phone, String salary, String type, String deadline,
				String tag, String area) {
			super();
			this.name = name;
			this.peoNum = peoNum;
			this.addr = addr;
			this.description = description;
			this.demand = demand;
			this.linkman = linkman;
			this.phone = phone;
			this.salary = salary;
			this.type = type;
			this.deadline = deadline;
			this.tag = tag;
			this.area = area;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(context);
			dialog.setCancelable(true);
			dialog.setMessage(Tools.getStringFromRes(context, R.string.submit));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			try {
				String[] ss = salary.split("-");

				return Community.getInstance(getContext()).recruitOp(
						LlkcBody.USER_ACCOUNT, LlkcBody.PASS_ACCOUNT,
						state == State_Edit ? "2" : "1", area,
						state == State_Edit ? jPost.jobId : "", name, peoNum,
						ss[0], ss.length > 1 ? ss[1] : "", linkman, phone,
						type, description, demand, deadline, tag, addr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (result) {
				Intent intent = new Intent();
				intent.putExtra("update", true);
				JpostManageActivity.launch(getContext(), intent);
			} else {
				MyToast.getToast().showToast(getContext(),
						Tools.getStringFromRes(context, R.string.submit_fail));
			}
			super.onPostExecute(result);
		}
	}

	@Override
	public BaseActivity getContext() {
		return this;
	}

	class initDataTask extends AsyncTask<Object, Integer, Boolean> {

		public initDataTask() {
			super();

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(dialog == null)
				dialog = new ProgressDialog(getContext());
			dialog.setCancelable(true);
			dialog.setMessage(Tools
					.getStringFromRes(context, R.string.datainit));
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Object... params) {
			initData();
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			setProgressBarIndeterminateVisibility(false);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
			if (jPost != null) {
				String cs = jPost.classify;
				if (cs != null) {
					String[] css = cs.split("-");
					if (css.length > 0) {
						Type t1 = (Type) ApplicationData.findType(css[0]);
						key = t1;
					}
					if (css.length > 1) {
						Type t2 = (Type) ApplicationData.findType2(key, css[1]);
						type = t2;
					}
					typeTV.setText(key == null ? "" : key.name + "-"
							+ (type == null ? "" : type.name));
				}
			}
			super.onPostExecute(result);
		}
	}

	@Override
	public void setDialogContent(AlertDialog dialog, int layoutId,
			String content, String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDialogTitle(AlertDialog dialog, int layoutId, String title,
			String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public OnClickListener setPositiveClickListener(AlertDialog dialog,
			int layoutId, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OnClickListener setNegativeClickListener(AlertDialog dialog,
			int layoutId, String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
