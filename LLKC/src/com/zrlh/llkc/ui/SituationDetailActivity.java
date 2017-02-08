/** @author 傅强
 * 
 */
package com.zrlh.llkc.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zzl.zl_app.util.Tools;

/**
 * @author 傅强
 * 
 */
public class SituationDetailActivity extends BaseActivity {
	public static final String TAG = "situationdetail";
	WebView webView;
	ImageButton back, friends;
	TextView title_card;
	String URL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		setContentView(R.layout.stuation_detailactivity);
		init();
		initWebView();
		// WebSettings webSettings = webView.getSettings();
		// webSettings.setSavePassword(false);
		// webSettings.setSaveFormData(false);
		// webSettings.setPluginsEnabled(true);
		// webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		// webSettings.setJavaScriptEnabled(true);
		// webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// webSettings.supportMultipleWindows();
		// webSettings.setUseWideViewPort(true);// 關鍵點
		// webSettings.setLoadWithOverviewMode(true);
		// webView.loadUrl(URL);
		// webView.setWebViewClient(new WebViewClient() {
		// public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// view.loadUrl(url);
		// return true;
		// }
		// });

	}

	private WebChromeClient m_chromeClient = new WebChromeClient() {
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			// TODO Auto-generated method stub
		}
	};

	public void initWebView() {
		webView.setHorizontalScrollBarEnabled(false);
		webView.getSettings().setSupportZoom(false);
		webView.setHorizontalScrollbarOverlay(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
		// webView.getSettings().setPluginsEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setSupportMultipleWindows(true);
		if (!Tools.checkNetWorkStatus(this))
			webView.getSettings().setCacheMode(
					WebSettings.LOAD_CACHE_ELSE_NETWORK);
		else
			webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);// 适应屏幕
		// 使用内置WebView进行浏览
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webView.setWebChromeClient(m_chromeClient);

		// 使用内置WebView进行浏览
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedHttpAuthRequest(WebView view,
					HttpAuthHandler handler, String host, String realm) {
				super.onReceivedHttpAuthRequest(view, handler, host, realm);
				pbLayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				pbLayout.setVisibility(View.GONE);
			}
		});
		webView.loadUrl(URL);
		webView.pageDown(true);
		webView.pageUp(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(TAG);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(TAG);
	}

	@Override
	public void onBackPressed() {
		closeOneAct(TAG);
		super.onBackPressed();
	}

	private LinearLayout pbLayout;

	void init() {
		Intent intent = this.getIntent();
		URL = intent.getStringExtra("url");
		pbLayout = (LinearLayout) this.findViewById(R.id.stuation_layout_pb);
		pbLayout.setVisibility(View.VISIBLE);
		webView = (WebView) findViewById(R.id.stuation_webview);
		back = (ImageButton) findViewById(R.id.back);
		friends = (ImageButton) findViewById(R.id.friends);
		title_card = (TextView) findViewById(R.id.title_card);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		friends.setVisibility(View.GONE);
		title_card.setText(Tools.getStringFromRes(getApplicationContext(),
				R.string.xinxianshi));
		friends.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(
						getApplicationContext(),
						Tools.getStringFromRes(getApplicationContext(),
								R.string.no_function), Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	@Override
	public BaseActivity getContext() {
		// TODO Auto-generated method stub
		return this;
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
