package com.zzl.zl_app.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;

import com.zzl.zl_app.net_port.ConnectionCaller;
import com.zzl.zl_app.util.Tools;

public abstract class BaseHttp {
	public static ConnectivityManager conManager;

	private static HttpParams httpParams;

	private static ClientConnectionManager connectionManager;

	/**
	 * 最大连接数
	 */
	public final static int MAX_TOTAL_CONNECTIONS = 800;
	/**
	 * 获取连接的最大等待时间
	 */
	public final static int WAIT_TIMEOUT = 60000;
	/**
	 * 每个路由最大连接数
	 */
	public final static int MAX_ROUTE_CONNECTIONS = 400;
	/**
	 * 连接超时时间
	 */
	public final static int CONNECT_TIMEOUT = 20000;
	/**
	 * 读取超时时间
	 */
	public final static int READ_TIMEOUT = 30000;

	static {
		httpParams = new BasicHttpParams();
		// 设置最大连接数
		ConnManagerParams.setMaxTotalConnections(httpParams,
				MAX_TOTAL_CONNECTIONS);
		// 设置获取连接的最大等待时间
		ConnManagerParams.setTimeout(httpParams, WAIT_TIMEOUT);
		// 设置每个路由最大连接数
		ConnPerRouteBean connPerRoute = new ConnPerRouteBean(
				MAX_ROUTE_CONNECTIONS);
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRoute);
		// 设置连接超时时间
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECT_TIMEOUT);
		// 设置读取超时时间
		HttpConnectionParams.setSoTimeout(httpParams, READ_TIMEOUT);

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		registry.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));

		connectionManager = new ThreadSafeClientConnManager(httpParams,
				registry);
	}

	public static HttpClient getHttpClient() {
		HttpClient client = new DefaultHttpClient(connectionManager, httpParams);
		return client;
	}

	// private Activity activity;

	public BaseHttp(ConnectivityManager Manager) {
		this.conManager = Manager;
	}

	public static final byte HTTPTHREAD = 1;

	public static final byte HTTPWORKER = 2;

	public static final byte IDLE = 0;

	public static final byte PREPARETION = 1;

	public static final byte WORKING = 2;

	public static final byte FINISH = 3;

	protected byte stat = IDLE;

	protected static final byte GET = 0;

	protected static final byte POST = 1;

	protected byte requestMethod = GET;

	protected String requestUrl = null;

	protected byte[] request = null;

	protected ArrayList<BasicNameValuePair> pairs;

	private byte[] response = null;

	public byte getStat() {
		return stat;
	}

	/**
	 * 
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author yz
	 * @param stat
	 * @param notity
	 *            是否通知listener
	 */
	public void setStat(byte stat) {
		this.stat = stat;
	}

	public void setGetURL(String url) {
		requestUrl = url;

		requestMethod = GET;
	}

	public void setPostURL(String url, byte[] request) {
		requestUrl = url;

		requestMethod = POST;

		this.request = request;
	}

	public void setPostURL(String url, ArrayList<BasicNameValuePair> pairs) {
		requestUrl = url;

		requestMethod = POST;

		this.pairs = pairs;
	}

	public void clean() {
		requestUrl = null;

		request = null;
		if (pairs != null)
			pairs.clear();
		pairs = null;
	}

	public abstract void startRun();

	protected byte[] getResponse() {
		byte[] data = null;

		DataInputStream dis = null;

		HttpClient http = getHttpClient();
		if (http == null)
			return null;

		if (isCMWAP(conManager)) {
			String host = Proxy.getDefaultHost();// 此处Proxy源自android.net
			int port = Proxy.getDefaultPort();// 同上
			HttpHost httpHost = new HttpHost(host, port);
			// 设置代理
			http.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
					httpHost);
		}
		Tools.log("IO", "requestUrl : " + requestUrl);
		HttpResponse response = null;
		try {
			if (requestMethod == GET) {
				HttpGet httpget = new HttpGet(requestUrl);
				response = http.execute(httpget);
				Tools.log("IO", "StatusCode : "
						+ response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() != 200) {
					httpget.abort();
					return null;
				}
			} else {
				HttpPost httppost = new HttpPost(requestUrl);

				// InputStream is = new ByteArrayInputStream(request);
				// InputStreamEntity reqEntity = new InputStreamEntity(is,
				// request.length);
				// reqEntity.setContentType("binary/octet-stream");
				// httppost.setEntity(reqEntity);
				if (pairs != null) {
					UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(
							pairs, "utf-8");
					String req = ConnectionCaller
							.convertStreamToString(p_entity.getContent());
					Tools.log("IO", "p_entity : " + req);
					httppost.setEntity(p_entity);
				}

				response = http.execute(httppost);
				int statusCode = response.getStatusLine().getStatusCode();
				Tools.log("IO", "StatusCode : " + statusCode);
				if (statusCode != 200) {
					httppost.abort();
					return null;
				}
			}

			if (response != null
					&& response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity responseEntity = response.getEntity();
				dis = new DataInputStream(responseEntity.getContent());
				int len = (int) responseEntity.getContentLength();
				if (len < 0) {
					return data;
				}
				data = new byte[len];

				int readCount = 0; // 已经成功读取的字节的个数
				while (readCount <= len) {
					if (readCount == len)
						break;
					readCount += dis.read(data, readCount, len - readCount);
				}
			}
		} catch (ConnectTimeoutException e) {
			String json = "{\"Stat\":\"-99\",\"Msg\":\"timeout\"}";
			data = json.getBytes();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// http.getConnectionManager().shutdown();
		}
		return data;
	}

	public static boolean isCMWAP(ConnectivityManager conManager) {

		String currentAPN = "";
		// ConnectivityManager conManager = (ConnectivityManager) activity
		// .getApplication()
		// .getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conManager.getActiveNetworkInfo();
		if (info == null)
			return false;
		currentAPN = info.getExtraInfo();

		if (currentAPN == null || currentAPN == "") {
			return false;
		} else {
			if (currentAPN.equals("cmwap")) {
				return true;
			} else {
				return false;
			}
		}
	}
}
