package com.zzl.zl_app.net;

import java.io.IOException;

public interface SocketConnection extends StreamConnection
{
	public static final byte 	DELAY 		=  0;
	public static final byte 	KEEPALIVE 	=  2;
	public static final byte 	LINGER 		=  1;
	public static final byte 	RCVBUF 		=  3;
	public static final byte 	SNDBUF 		=  4;
	
	public String getAddress() throws IOException;

	public int getLocalPort() throws IOException;

	public int getPort() throws IOException;

	public int getSocketOption(byte option)
		throws IllegalArgumentException,IOException;

	public void setSocketOption(byte option, int value)
		throws IllegalArgumentException,IOException;
}
