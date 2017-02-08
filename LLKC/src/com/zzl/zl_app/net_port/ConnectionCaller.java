package com.zzl.zl_app.net_port;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;

import com.zzl.zl_app.cache.FileConstant;
import com.zzl.zl_app.cache.LocalMemory;
import com.zzl.zl_app.net.Connector;
import com.zzl.zl_app.net.HttpConnection;

public class ConnectionCaller {
	private static HttpParams httpParams;

	private static ClientConnectionManager connectionManager;
	/**
	 * 最大连接数
	 */
	public final static int MAX_TOTAL_CONNECTIONS = 100;
	/**
	 * 获取连接的最大等待时间
	 */
	public final static int WAIT_TIMEOUT = 30000;
	/**
	 * 每个路由最大连接数
	 */
	public final static int MAX_ROUTE_CONNECTIONS = 400;
	/**
	 * 连接超时时间
	 */
	public final static int CONNECT_TIMEOUT = 10000;
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

	/** get */
	public static final byte REQ_TYPE_GET = 0;
	/** post */
	public static final byte REQ_TYPE_POST = 1;

	/** HTTP请示类型 */
	byte reqType = 0; // 0:GET, 1:POST
	/** 使用POST方法时的提交数据 */
	byte[] reqData = null;

	private String respData = null;

	private static boolean isUseAgent = false;// 是否使用代理

	public ConnectionCaller() {

	}

	public static String doGet(String fileName, Context context) throws WSError {
		InputStream is = null;
		try {
			is = context.getAssets().open(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String data = null;
		if (is != null)
			data = convertStreamToString(is);
		return data;
	}

	public static String doGet(String fileName, int type) throws WSError {
		if (FileConstant.File_Type_txt == type)
			return LocalMemory.getInstance().getTxt(fileName);
		return null;
	}

	public static String doGet(String uri) throws WSError {
		String data = null;
		URI encodeUri = null;
		HttpGet httpGet = null;

		try {
			encodeUri = new URI(uri);
			httpGet = new HttpGet(encodeUri);
		} catch (URISyntaxException e) {
			String encodedUrl = uri.replace(' ', '+');
			httpGet = new HttpGet(encodedUrl);
			e.printStackTrace();
		}
		HttpClient httpClient = getHttpClient();
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(httpGet);// 获取Http响应
			HttpEntity httpEntity = httpResponse.getEntity();// 获取响应实体
			if (httpEntity != null) {
				InputStream is = httpEntity.getContent();
				data = convertStreamToString(is);
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public String doGet2(String urlStr, boolean isUseAgent) {

		try {
			if (urlStr == null) {
				return null;
			} else {
				// 生成HTTP连接对象
				HttpConnection httpConn = open(urlStr, reqType, isUseAgent);

				DataOutputStream dos = null;

				// 当请求类型为POST时，提交数据
				if (reqType == REQ_TYPE_POST && reqData != null) {
					dos = httpConn.openDataOutputStream();
					dos.write(reqData);// 字节数组写入此输出流
				}
				int code = httpConn.getResponseCode();// 获取响应码
				// 响应成功--连接成功
				if (code == 200) {
					DataInputStream dis = httpConn.openDataInputStream();
					respData = convertStreamToString(dis);
				}
				if (dos != null)
					dos.close();
				httpConn.close();
				urlStr = null; // 请求结束后URL清空
			}
		} catch (SecurityException se) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		return respData;
	}

	public static byte[] doGetImgRes(String urlStr) {
		byte[] imageBuffer = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();
			BufferedInputStream is = new BufferedInputStream(
					con.getInputStream());
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int b = -1;
			while ((b = is.read()) != -1) {
				os.write(b);
			}
			is.close();
			os.close();
			imageBuffer = os.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return imageBuffer;
	}

	/**
	 * 使用POST方法启动目标为指定url的HTTP请求，供外部调用 mode1
	 * 
	 * @param httpURL
	 * @param postData
	 * @return 请求是否被正确发送
	 */
	public String startURLPost(String httpURL, byte[] postData) {
		reqType = REQ_TYPE_POST;
		reqData = postData;
		return startURL(httpURL, false);
	}

	/**
	 * 启动目标为指定url的HTTP请求，内部调用
	 * 
	 * @param httpURL
	 * @return 请求是否被正确发送
	 */
	private String startURL(String httpURL, boolean isNeedInit) {

		return doGet2(httpURL, false);
	}

	/**
	 * 将输入流转换为字符串
	 * 
	 * @param input
	 * @return
	 */
	public static String convertStreamToString(InputStream input) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuilder builder = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return builder.toString();
	}

	/**
	 * 自动根据代理配置生成HTTP连接对象
	 * 
	 * @param url
	 * @param reqType
	 *            TODO
	 * @return
	 * @throws Exception
	 */
	public static HttpConnection open(String url, byte reqType,
			boolean isUserAgent) throws Exception {
		HttpConnection httpConn = null;
		if (isUseAgent) {
			httpConn = (HttpConnection) Connector
					.open(getURLAdnHost(url, true));
			httpConn.setRequestProperty("X-Online-Host",
					getURLAdnHost(url, false));
		} else {
			try {
				httpConn = (HttpConnection) Connector.open(url);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		httpConn.setRequestMethod(reqType == REQ_TYPE_GET ? HttpConnection.GET
				: HttpConnection.POST);// 设置请求方法(get/post)
		return httpConn;
	}

	/**
	 * 在代理连接时，自动分离请求URL和主机
	 * 
	 * @param url
	 * @param isReturnURL
	 *            true:返回请求URL，false:返回主机
	 * @return
	 */
	public static String getURLAdnHost(String url, boolean isReturnURL) {
		String xonlineUrl, sendUrl;
		int pointPos = url.indexOf("//");
		int cutPos = url.indexOf('/', pointPos + 2);
		if (cutPos != -1) {
			xonlineUrl = url.substring(pointPos + 2, cutPos);
			sendUrl = "http://10.0.0.172:80" + url.substring(cutPos);
		} else {
			xonlineUrl = url.substring(pointPos + 2);
			sendUrl = "http://10.0.0.172:80";
		}
		return isReturnURL ? sendUrl : xonlineUrl;
	}

}
