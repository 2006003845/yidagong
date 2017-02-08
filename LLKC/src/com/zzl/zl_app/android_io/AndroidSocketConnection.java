package com.zzl.zl_app.android_io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

import com.zzl.zl_app.net.SocketConnection;

public class AndroidSocketConnection implements SocketConnection {

	protected Socket connect;

	public AndroidSocketConnection(String url) throws UnknownHostException,
			IOException {

		/* Look for : as in "http:", "file:", or socket: */
		int colon = url.indexOf(':');

		/* Test for null argument */
		if (colon < 1) {
			throw new IllegalArgumentException("no ':' in URL");
		}

		String protocol;
		String address;
		int port = 0;
		/* Strip off the protocol name */
		protocol = url.substring(0, colon);

		/* Strip off the rest of the string */
		address = url.substring(colon + 1);
		colon = address.indexOf(':');
		if(colon < 1)
		{
			
		}
		else
		{
			port = Integer.parseInt(address.substring(colon + 1));
			address = address.substring(2,colon);	
		}
		this.connect = new Socket(address, port);
	}

	 
	public void close() throws IOException {
		this.connect.close();
		this.connect = null;
	}

	 
	public DataInputStream openDataInputStream() throws IOException {

		return new DataInputStream(this.openInputStream());
	}

	 
	public DataOutputStream openDataOutputStream() throws IOException {

		return new DataOutputStream(this.openOutputStream());
	}

	 
	public InputStream openInputStream() throws IOException {

		return this.connect.getInputStream();
	}

	 
	public OutputStream openOutputStream() throws IOException {

		return this.connect.getOutputStream();
	}
	public String getAddress() throws IOException
	{
		return this.connect.getInetAddress().toString();
	}

	public int getLocalPort() throws IOException
	{
		return this.connect.getLocalPort();
	}

	public int getPort() throws IOException
	{
		return this.connect.getPort();
	}
	//TODO:To Be Accomplish
	public int getSocketOption(byte option)
		throws IllegalArgumentException,IOException
		{
			return 0;
		}
	//TODO:To Be Accomplish
	public void setSocketOption(byte option, int value)
		throws IllegalArgumentException,IOException
		{
		
		}

}
