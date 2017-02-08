package com.zrlh.llkc.corporate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zrlh.llkc.funciton.DownloadService;
import com.zzl.zl_app.util.SIMCardInfo;
import com.zzl.zl_app.util.Tools;

public class WebViewActivity extends BaseActivity {

	public static final String Tag = "webv";

	private String url;
	private String name;

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, WebViewActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		c.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(Tag, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			url = b.getString("url");
			name = b.getString("name");
		}
		setContentView(R.layout.webv);
		initView();
	}

	private WebView webView;
	private TextView titleTV;
	private ProgressBar loadPB;
	SIMCardInfo simInfo;

	public Bitmap cacheBM;

	private ServiceConnection conn;
	private DownloadService.DownloadBinder binder;

	public void init() {
		conn = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				binder = (DownloadService.DownloadBinder) service;
				binder.start();
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
		};
	}

	private void initView() {
		init();
		simInfo = SIMCardInfo.getInstance(getContext());
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		this.findViewById(R.id.btn).setVisibility(View.GONE);
		loadPB = (ProgressBar) this.findViewById(R.id.webv_pb);
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		if (name != null)
			titleTV.setText(name);
		webView = (WebView) this.findViewById(R.id.webview);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setHorizontalScrollBarEnabled(false);
		webView.getSettings().setSupportZoom(false);
		webView.setHorizontalScrollbarOverlay(true);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
//		webView.getSettings().setPluginsEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setSupportMultipleWindows(true);
		if (!Tools.checkNetWorkStatus(getContext()))
			webView.getSettings().setCacheMode(
					WebSettings.LOAD_CACHE_ELSE_NETWORK);
		else
			webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);// 适应屏幕
		// 使用内置WebView进行浏览
		webView.setWebChromeClient(m_chromeClient);
		webView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				// Uri uri = Uri.parse(url);
				// Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				// startActivity(intent);
				if (url.contains("apk")) {
					Intent intent = new Intent(getContext(),
							DownloadService.class);
					intent.putExtra("updateURL", url);
					intent.putExtra("name", ".");
					intent.putExtra("other", true);
					startService(intent); // 如果先调用startService,则在多个服务绑定对象调用unbindService后服务仍不会被销毁
					bindService(intent, conn, Context.BIND_AUTO_CREATE);
				}
			}
		});

		// 使用内置WebView进行浏览
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("c")) {
					String num = url.substring(5);
					// phone(num);
				} else
					view.loadUrl(url);

				return true;
			}

			@Override
			public void onReceivedHttpAuthRequest(WebView view,
					HttpAuthHandler handler, String host, String realm) {
				super.onReceivedHttpAuthRequest(view, handler, host, realm);
				loadPB.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				view.loadUrl("javascript:window.local_obj.showSource('<head>'+"
						+ "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
				// sessionCookie = cookieManager.getCookie(url);
				// if (sessionCookie != null) {
				// cookieManager.setCookie(url, sessionCookie);
				// CookieSyncManager.getInstance().sync();
				// }
				super.onPageFinished(view, url);
				loadPB.setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);
			}
		});
		if (url == null || url.equals("") || !Tools.isUrl(url)) {
			webView.setVisibility(View.GONE);
			loadPB.setVisibility(View.GONE);
			this.findViewById(R.id.webv_text).setVisibility(View.VISIBLE);
		} else {
			this.findViewById(R.id.webv_text).setVisibility(View.GONE);
			webView.loadUrl(url);
			webView.pageDown(true);
			webView.pageUp(true);
		}
	}

	private WebChromeClient m_chromeClient = new WebChromeClient() {
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			// TODO Auto-generated method stub
		}
	};
	Handler handler = new Handler();

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
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			closeOneAct(Tag);
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (webView != null)
			webView.destroy();
	}

	@Override
	public BaseActivity getContext() {
		// TODO Auto-generated method stub
		return this;
	}

	public static String fiterHtmlTag(String str, String tag) {
		String regxp = "<" + tag + "\\s*.*>.*</" + tag + ">";
		Pattern pattern = Pattern.compile(regxp);
		Matcher matcher = pattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		boolean result1 = matcher.find();
		String res = "";
		if (result1) {
			res = matcher.group();
		}
		String regxp2 = ">.*<";
		Pattern pattern2 = Pattern.compile(regxp2);
		matcher = pattern2.matcher(res);
		result1 = matcher.find();
		if (result1)
			res = matcher.group();
		if (res.length() > 2) {
			res = res.substring(1, res.length() - 1);
		}
		return res;
	}

	public static String getXMLValue(String sXML, String sTag) {

		int idx = sXML.indexOf("<" + sTag + ">");
		if (idx != -1) {
			idx += sTag.length() + 2;
			int idx2 = sXML.indexOf("</" + sTag + ">", idx);
			if (idx2 != -1) {
				return sXML.substring(idx, idx2);
			}
		}
		return null;
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
