package com.zzl.zl_app.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.zzl.zl_app.android_io.AndroidHTTPConnection;
import com.zzl.zl_app.android_io.AndroidSocketConnection;
import com.zzl.zl_app.android_io.AndroidURLConnection;
import com.zzl.zl_app.android_io.file.AndroidFileConnection;

public class Connector {
	public static final int READ = 0x01;
	public static final int WRITE = 0x02;
	public static final int READ_WRITE = READ | WRITE;

	private static final String PROTOCOL_FILE = "file:";
	private static final String PROTOCOL_HTTP = "http:";
	private static final String PROTOCOL_SOCKET = "socket:";

	public static final Connection open(String name) throws IOException {
		return open(name, READ_WRITE);
	}

	/**
	 * Create and open a Connection. Notice:Flag of timeouts have no effect.
	 * 
	 * @param name
	 * @param mode
	 * @param timeouts
	 * @return
	 * @throws IOException
	 */
	public static final Connection open(String name, int mode, boolean timeouts)
			throws IOException {
		return open(name, READ_WRITE);

	}

	public static final Connection open(String name, int mode)
			throws IOException {
		Connection connection;
		if (name.startsWith(PROTOCOL_FILE)) {
			connection = new AndroidFileConnection(name);
		} else if (name.startsWith(PROTOCOL_HTTP)) {
			connection = new AndroidHTTPConnection(name);
		} else if (name.startsWith(PROTOCOL_SOCKET)) {
			connection = new AndroidSocketConnection(name);
		} else {
			connection = new AndroidURLConnection(name);
		}
		return connection;
	}

	public static final DataInputStream openDataInputStream(String name)
			throws IOException {
		return new DataInputStream(openInputStream(name));
	}

	public static final DataOutputStream openDataOutputStream(String name)
			throws IOException {
		return new DataOutputStream(openOutputStream(name));
	}

	public static final InputStream openInputStream(String name)
			throws IOException {
		Connection connection = open(name, READ);
		return ((InputConnection) connection).openInputStream();
	}

	public static final OutputStream openOutputStream(String name)
			throws IOException {
		Connection connection = open(name, WRITE);
		return ((OutputConnection) connection).openOutputStream();
	}
}
