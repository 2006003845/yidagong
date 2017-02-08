package com.zrlh.llkc.funciton;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import org.apache.http.protocol.HTTP;

import android.util.Log;


public class HttpUtils {

	/**
	 * 【获取服务器上的json字符串】
	 * @param conn
	 * @return  json格式的字符串
	 * @throws IOException 
	 */
	public static String getJsonFromServer(HttpURLConnection conn) throws IOException{
		if(conn==null){
			return LlkcBody.NETWORKBUG;
		}
		StringBuffer jsonBuffer = new StringBuffer();
		BufferedReader reader = null;
		InputStream stream = null;
		String jsonString = "";
		stream = conn.getInputStream();
		if(LlkcBody.isGzip){
			reader  = new BufferedReader(new InputStreamReader(new GZIPInputStream(stream), HTTP.UTF_8));
		}
		else{
			reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		}

		String line = "";
		while((line=reader.readLine()) != null){
			jsonBuffer.append(line);
		}
		jsonString = jsonBuffer.toString();
		
		return jsonString;
	}
	

	
	public static String getPostJosnFromServer(URL url,String postStr) throws IOException{
		
		URLConnection connection = url.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) connection;
		httpConn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		httpConn.setRequestProperty("User-Agent",
				"android");
		if(LlkcBody.isGzip){
			httpConn.setRequestProperty("Accept-Encoding", "gzip");
		}else{
			httpConn.setRequestProperty("Accept-Encoding", "default");
		}

		httpConn.setRequestMethod("POST");
		httpConn.setReadTimeout(30*1000);//30s
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		OutputStream out = httpConn.getOutputStream();

		out.write(postStr.getBytes("UTF-8"));
		out.close();

		// Read the response and write it to standard out.
		InputStream isr = httpConn.getInputStream();
		if(LlkcBody.isGzip){
			isr = new GZIPInputStream(httpConn.getInputStream());
		}
		else{
			isr = httpConn.getInputStream();
		}

		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		int b;
		while( (b=isr.read())!=-1){
			bao.write(b);
		}
		isr.close();
		httpConn.disconnect();
		return new String(bao.toByteArray(),"UTf-8");
	}
	
	/**
	 * 【通过url获得connection】
	 * @param urlStr
	 * @return HttpURLConnection 对象
	 */
	public static HttpURLConnection getConnection(String urlStr){
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5*1000);
			conn.setReadTimeout(20*1000);
			conn.setRequestProperty("Charsert", "utf-8"); 
			
//			conn.setRequestMethod("GET");
//			if(NavigationMethod.isGzip){
//				conn.setRequestProperty("Accept-Encoding", "gzip");
//			}else{
//				conn.setRequestProperty("Accept-Encoding", "default");
//			}
//			conn.connect();
			
		}catch (Exception e) {
			return conn;
		}
		return conn;
	}
	

	
	/**
	 * post请求
	 * @param urlStr
	 * @param params
	 * @return
	 */
	public static HttpURLConnection getConnection(String urlStr,String params){
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5*1000);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			if(LlkcBody.isGzip){
				conn.setRequestProperty("Accept-Encoding", "gzip");
			}else{
				conn.setRequestProperty("Accept-Encoding", "default");
			}
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Length", String.valueOf(params.getBytes().length));
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.connect();
			conn.getOutputStream().write(params.getBytes());
		}catch (Exception e) {
			return conn;
		}
		return conn;
	}

}
