/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zzl.zl_app.connection;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.zzl.zl_app.util.FileTools;
import com.zzl.zl_app.util.Tools;

public class Utility {

	public static final String BOUNDARY = "7cd4a6d158c";
	public static final String MP_BOUNDARY = "--" + BOUNDARY;
	public static final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";
	public static final String MULTIPART_FORM_DATA = "multipart/form-data";

	public static final String HTTPMETHOD_POST = "POST";
	public static final String HTTPMETHOD_GET = "GET";
	public static final String HTTPMETHOD_DELETE = "DELETE";

	private static final int SET_CONNECTION_TIMEOUT = 10000;
	private static final int SET_SOCKET_TIMEOUT = 20000;

	public static boolean isBundleEmpty(Bundle bundle) {
		if (bundle == null || bundle.size() == 0) {
			return true;
		}
		return false;
	}

	public static String encodePostBody(Bundle parameters, String boundary) {
		if (parameters == null)
			return "";
		StringBuilder sb = new StringBuilder();

		for (String key : parameters.keySet()) {
			if (parameters.getByteArray(key) != null) {
				continue;
			}

			sb.append("Content-Disposition: form-data; name=\"" + key
					+ "\"\r\n\r\n" + parameters.getString(key));
			sb.append("\r\n" + "--" + boundary + "\r\n");
		}

		return sb.toString();
	}

	public static Bundle decodeUrl(String s) {
		Bundle params = new Bundle();
		if (s != null) {
			String array[] = s.split("&");
			for (String parameter : array) {
				String v[] = parameter.split("=");
				params.putString(URLDecoder.decode(v[0]),
						URLDecoder.decode(v[1]));
			}
		}
		return params;
	}

	/**
	 * Parse a URL query and fragment parameters into a key-value bundle.
	 * 
	 * @param url
	 *            the URL to parse
	 * @return a dictionary bundle of keys and values
	 */
	public static Bundle parseUrl(String url) {
		// hack to prevent MalformedURLException
		url = url.replace("weiboconnect", "http");
		try {
			URL u = new URL(url);
			Bundle b = decodeUrl(u.getQuery());
			b.putAll(decodeUrl(u.getRef()));
			return b;
		} catch (MalformedURLException e) {
			return new Bundle();
		}
	}

	/**
	 * Construct a url encoded entity by parameters .
	 * 
	 * @param bundle
	 *            :parameters key pairs
	 * @return UrlEncodedFormEntity: encoed entity
	 */
	public static UrlEncodedFormEntity getPostParamters(Bundle bundle)
			throws Exception {
		if (bundle == null || bundle.isEmpty()) {
			return null;
		}
		try {
			List<NameValuePair> form = new ArrayList<NameValuePair>();
			for (String key : bundle.keySet()) {
				form.add(new BasicNameValuePair(key, bundle.getString(key)));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form,
					"UTF-8");
			return entity;
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedEncodingException();
		}
	}

	/**
	 * Implement a weibo http request and return results .
	 * 
	 * @param context
	 *            context of activity
	 * @param url
	 * @param method
	 *            (GET|POST)
	 * @param bm
	 * @return
	 * @throws Exception
	 */
	public static String openUrl(Context context, String url, String method)
			throws Exception {
		String result = "";
		try {
			HttpClient client = getNewHttpClient(context);
			HttpUriRequest request = null;
			ByteArrayOutputStream bos = null;
			if (method.equals("GET")) {
				HttpGet get = new HttpGet(url);
				request = get;
			} else if (method.equals("POST")) {
				HttpPost post = new HttpPost(url);
				bos = new ByteArrayOutputStream(1024 * 50);
				post.setHeader("Content-Type", MULTIPART_FORM_DATA
						+ "; boundary=" + BOUNDARY);
				request = post;
			} else if (method.equals("DELETE")) {
				request = new HttpDelete(url);
			}
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();
			if (statusCode != 200) {
				result = read(response);
				throw new Exception("404");
			}
			// parse content stream from response
			result = read(response);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static boolean loadFile(String loadpath, String fileName,
			String savePath, Context context, String broadcastAction) {
		FileOutputStream fos = null; // 文件输出流
		FileInputStream fis = null; // 文件输出流
		InputStream is = null; // 网络文件输入流
		HttpURLConnection httpConnection = null;
		int readLength = 0; // 一次性下载的长度（以字节为单位）
		int file_length = 0;
		URL url = null;
		try {
			url = new URL(loadpath);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setConnectTimeout(10000);
			httpConnection.setRequestMethod("GET");
			is = httpConnection.getInputStream();
			FileTools.creatDir(savePath);
			String filePath = savePath + fileName;
			FileTools.deleteFile(filePath);
			FileTools.creatFile(filePath);
			File download_file = new File(filePath);
			fos = new FileOutputStream(download_file, true); // 初始化文件输出流
			fis = new FileInputStream(download_file); // 初始化文件输入流
			int total_read = fis.available(); // 初始化“已下载部分”的长度，此处应为0
			file_length = httpConnection.getContentLength(); // 要下载的文件的总长度
			if (is == null) { // 如果下载失败则打印日志，并返回
				Tools.log("Voice", "donload failed...");
				return false;
			}
			byte buf[] = new byte[3072]; // 定义下载缓冲区
			readLength = 0; // 一次性下载的长度
			Tools.log("Voice", "download start...");
			Intent startIntent = new Intent();
			Bundle b = new Bundle();
			if (broadcastAction != null) {
				// 向前台发送开始下载广播
				b.putInt("fileSize", file_length);
				b.putInt("progress", 0);
				startIntent.putExtras(b);
				startIntent.setAction(broadcastAction);
				context.sendBroadcast(startIntent);
			}
			// 如果读取网络文件的数据流成功，且用户没有选择停止下载，则开始下载文件
			while (readLength != -1) {
				if ((readLength = is.read(buf)) > 0) {
					fos.write(buf, 0, readLength);
					total_read += readLength; // 已下载文件的长度增加
				}
				if (broadcastAction != null) {
					b.putInt("fileSize", file_length);
					b.putInt("progress", total_read);
					startIntent.putExtras(b);
					startIntent.setAction(broadcastAction);
					context.sendBroadcast(startIntent);
				}
				if (total_read == file_length) { // 当已下载的长度等于网络文件的长度，则下载完成
					Tools.log("Voice", "download complete...");
					// 向前台发送下载完成广播
					if (broadcastAction != null) {
						Intent completeIntent = new Intent();
						b.putBoolean("isFinish", true);
						completeIntent.putExtras(b);
						completeIntent.setAction(broadcastAction);
						context.sendBroadcast(completeIntent);
					}

				}
				// Thread.sleep(10); // 当前现在休眠10毫秒
			}
		} catch (Exception e) {
			if (broadcastAction != null) {
				Intent errorIntent = new Intent();
				errorIntent.setAction(broadcastAction);
				context.sendBroadcast(errorIntent);
				e.printStackTrace();
			}
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (fis != null) {
					is.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 
	 * 
	 * @param context
	 *            context of activity
	 * @param url
	 * @param method
	 *            (GET|POST)
	 * @param bm
	 * @return
	 * @throws Exception
	 */
	public static String openUrl(Context context, String url, String method,
			Bitmap bm) throws Exception {
		String result = "";
		try {
			HttpClient client = getNewHttpClient(context);
			HttpUriRequest request = null;
			ByteArrayOutputStream bos = null;
			if (method.equals("GET")) {

			} else if (method.equals("POST")) {
				HttpPost post = new HttpPost(url);
				if(bm != null){					
					byte[] data = null;
					bos = new ByteArrayOutputStream(1024 * 50);
					post.setHeader("Content-Type", MULTIPART_FORM_DATA
							+ "; boundary=" + BOUNDARY);
					Utility.imageContentToUpload(bos, bm);
					data = bos.toByteArray();
					bos.close();
					// UrlEncodedFormEntity entity = getPostParamters(params);
					ByteArrayEntity formEntity = new ByteArrayEntity(data);
					post.setEntity(formEntity);
				}
				request = post;
			} else if (method.equals("DELETE")) {
				request = new HttpDelete(url);
			}
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();

			if (statusCode != 200) {
				result = read(response);
				throw new Exception();
			}
			// parse content stream from response
			result = read(response);
			return result;
		} catch (IOException e) {

		}
		return "";
	}

	public static HttpClient getNewHttpClient(Context context) {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);
			// Set the default socket timeout (SO_TIMEOUT) // in
			// milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setConnectionTimeout(params,
					Utility.SET_CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params,
					Utility.SET_SOCKET_TIMEOUT);
			HttpClient client = new DefaultHttpClient(ccm, params);

			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifiManager.getConnectionInfo();
			if (!wifiManager.isWifiEnabled() || -1 == info.getNetworkId()) {
				// 获取当前正在使用的APN接入�?
				Uri uri = Uri.parse("content://telephony/carriers/preferapn");
				Cursor mCursor = context.getContentResolver().query(uri, null,
						null, null, null);
				if (mCursor != null && mCursor.moveToFirst()) {
					// 游标移至第一条记录，当然也只有一�?
					String proxyStr = mCursor.getString(mCursor
							.getColumnIndex("proxy"));
					if (proxyStr != null && proxyStr.trim().length() > 0) {
						HttpHost proxy = new HttpHost(proxyStr, 80);
						client.getParams().setParameter(
								ConnRouteParams.DEFAULT_PROXY, proxy);
					}
					mCursor.close();
				}
			}
			return client;
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	/**
	 * Get a HttpClient object which is setting correctly .
	 * 
	 * @param context
	 *            : context of activity
	 * @return HttpClient: HttpClient object
	 */
	public static HttpClient getHttpClient(Context context) {
		BasicHttpParams httpParameters = new BasicHttpParams();
		// Set the default socket timeout (SO_TIMEOUT) // in
		// milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				Utility.SET_CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters,
				Utility.SET_SOCKET_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParameters);
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		if (!wifiManager.isWifiEnabled() || -1 == info.getNetworkId()) {
			// 获取当前正在使用的APN接入�?
			Uri uri = Uri.parse("content://telephony/carriers/preferapn");
			Cursor mCursor = context.getContentResolver().query(uri, null,
					null, null, null);
			if (mCursor != null && mCursor.moveToFirst()) {
				// 游标移至第一条记录，当然也只有一�?
				String proxyStr = mCursor.getString(mCursor
						.getColumnIndex("proxy"));
				if (proxyStr != null && proxyStr.trim().length() > 0) {
					HttpHost proxy = new HttpHost(proxyStr, 80);
					client.getParams().setParameter(
							ConnRouteParams.DEFAULT_PROXY, proxy);
				}
				mCursor.close();
			}
		}
		return client;
	}

	/**
	 * 
	 * @param out
	 * @param imgBm
	 * @throws Exception
	 */
	private static void imageContentToUpload(OutputStream out, Bitmap imgBm)
			throws Exception {
		StringBuilder temp = new StringBuilder();

		temp.append(MP_BOUNDARY).append("\r\n");
		temp.append("Content-Disposition: form-data; name=\"pic\"; filename=\"")
				.append("news_image").append("\"\r\n");
		String filetype = "image/png";
		temp.append("Content-Type: ").append(filetype).append("\r\n\r\n");

		byte[] res = temp.toString().getBytes();
		BufferedInputStream bis = null;
		try {
			out.write(res);
			imgBm.compress(CompressFormat.JPEG, 75, out);
			out.write("\r\n".getBytes());
			out.write(("\r\n" + END_MP_BOUNDARY).getBytes());
		} catch (IOException e) {
			throw new Exception(e);
		} finally {
			if (null != bis) {
				try {
					bis.close();
				} catch (IOException e) {
					throw new Exception(e);
				}
			}
		}
	}

	private static String CHARSET = "utf-8";

	public static String uploadFile(File file, String RequestURL,
			String fileName) {
		Tools.log("IO", "RequestURL:" + RequestURL);
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(Utility.SET_SOCKET_TIMEOUT);
			conn.setConnectTimeout(Utility.SET_CONNECTION_TIMEOUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);

			if (file != null) {
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				DataOutputStream dos = new DataOutputStream(
						conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */
				sb.append("Content-Disposition: form-data; name=\"voice\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				Tools.log("FileSize", "file:" + file.length());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
					Tools.log("FileSize", "size:" + len);
				}

				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);
				dos.flush();
				dos.close();
				is.close();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */
				int res = conn.getResponseCode();
				Tools.log("IO", "ResponseCode:" + res);
				if (res == 200) {
					InputStream input = conn.getInputStream();
					StringBuffer sb1 = new StringBuffer();
					int ss;
					while ((ss = input.read()) != -1) {
						sb1.append((char) ss);
					}
					result = sb1.toString();
				}

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Read http requests result from response .
	 * 
	 * @param response
	 *            : http response by executing httpclient
	 * 
	 * @return String : http response content
	 */
	private static String read(HttpResponse response) throws Exception {
		String result = "";
		HttpEntity entity = response.getEntity();
		InputStream inputStream;
		try {
			inputStream = entity.getContent();
			ByteArrayOutputStream content = new ByteArrayOutputStream();

			Header header = response.getFirstHeader("Content-Encoding");
			if (header != null
					&& header.getValue().toLowerCase().indexOf("gzip") > -1) {
				inputStream = new GZIPInputStream(inputStream);
			}

			// Read response into a buffered stream
			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}
			// Return result from buffered stream
			result = new String(content.toByteArray());
			return result;
		} catch (IllegalStateException e) {
			throw new IllegalStateException();
		} catch (IOException e) {
			throw new IOException();
		}
	}

	/**
	 * Read http requests result from inputstream .
	 * 
	 * @param inputstream
	 *            : http inputstream from HttpConnection
	 * 
	 * @return String : http response content
	 */
	private static String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}

	/**
	 * Clear current context cookies .
	 * 
	 * @param context
	 *            : current activity context.
	 * 
	 * @return void
	 */
	public static void clearCookies(Context context) {
		@SuppressWarnings("unused")
		CookieSyncManager cookieSyncMngr = CookieSyncManager
				.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}

	/**
	 * Display a simple alert dialog with the given text and title.
	 * 
	 * @param context
	 *            Android context in which the dialog should be displayed
	 * @param title
	 *            Alert dialog title
	 * @param text
	 *            Alert dialog message
	 */
	public static void showAlert(Context context, String title, String text) {
		Builder alertBuilder = new Builder(context);
		alertBuilder.setTitle(title);
		alertBuilder.setMessage(text);
		alertBuilder.create().show();
	}

	/**
	 * Base64 encode mehtod for weibo request.Refer to weibo development
	 * document.
	 * 
	 */
	public static char[] base64Encode(byte[] data) {
		final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
				.toCharArray();
		char[] out = new char[((data.length + 2) / 3) * 4];
		for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
			boolean quad = false;
			boolean trip = false;
			int val = (0xFF & (int) data[i]);
			val <<= 8;
			if ((i + 1) < data.length) {
				val |= (0xFF & (int) data[i + 1]);
				trip = true;
			}
			val <<= 8;
			if ((i + 2) < data.length) {
				val |= (0xFF & (int) data[i + 2]);
				quad = true;
			}
			out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 1] = alphabet[val & 0x3F];
			val >>= 6;
			out[index + 0] = alphabet[val & 0x3F];
		}
		return out;
	}

	public static String digestMD5(String string) {
		String s = null;
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(string.getBytes());
			byte tmp[] = md.digest();
			char str[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(str);
		} catch (Exception e) {
		}
		return s;
	}

}
