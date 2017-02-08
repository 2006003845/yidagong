package com.zzl.zl_app.android_io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.zzl.zl_app.net.HttpConnection;

public class AndroidURLConnection implements HttpConnection {

	protected URLConnection connection;

	public AndroidURLConnection(String url) throws MalformedURLException,
			IOException {
		this(new URL(url));
	}

	public AndroidURLConnection(URL url) throws IOException {
		this(url.openConnection());
	}

	public AndroidURLConnection(URLConnection connection) {
		this.connection = connection;
	}

	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(this.openInputStream());
	}

	public InputStream openInputStream() throws IOException {
		return this.connection.getInputStream();
	}

	public void close() throws IOException {
		this.connection = null;
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(openOutputStream());
	}

	public OutputStream openOutputStream() throws IOException {
		return this.connection.getOutputStream();
	}

	public void setConnection(URLConnection connection) {
		this.connection = connection;
	}

	public String getHeaderField(String key) {
		return this.connection.getHeaderField(key);
	}

	public String getRequestProperty(String key) {
		return this.connection.getRequestProperty(key);
	}

	public int getResponseCode() throws IOException {
		return 0;
	}

	public String getResponseMessage() throws IOException {
		return null;
	}

	public void setRequestMethod(String method) {

	}

	public void setRequestProperty(String key, String value) {
		this.connection.setRequestProperty(key, value);
	}
}
