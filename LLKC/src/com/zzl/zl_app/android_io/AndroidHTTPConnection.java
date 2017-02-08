package com.zzl.zl_app.android_io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.zzl.zl_app.net.HttpConnection;


public class AndroidHTTPConnection implements HttpConnection {
	private HttpURLConnection httpConn;
	private URL url;

	public AndroidHTTPConnection(String pathUrl) {
		try {
			url = new URL(pathUrl);
			httpConn = (HttpURLConnection) url.openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		httpConn.setUseCaches(false);

	}

	 
	public String getHeaderField(String key) {
		return httpConn.getHeaderField(key);
	}

	 
	public String getRequestProperty(String key) {
		return httpConn.getRequestProperty(key);
	}

	 
	public int getResponseCode() throws IOException {

		return httpConn.getResponseCode();
	}

	 
	public String getResponseMessage() throws IOException {
		return httpConn.getResponseMessage();
	}

	 
	public void setRequestMethod(String method) throws IOException {
		httpConn.setRequestMethod(method);
	}

	 
	public void setRequestProperty(String key, String value) throws IOException {
		httpConn.setRequestProperty(key, value);
	}

	 
	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(this.openInputStream());
	}

	 
	public InputStream openInputStream() throws IOException {
		return httpConn.getInputStream();
	}

	 
	public void close() throws IOException {
		httpConn.disconnect();
	}

	 
	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(this.openOutputStream());
	}

	 
	public OutputStream openOutputStream() throws IOException {
		return httpConn.getOutputStream();
	}
}
