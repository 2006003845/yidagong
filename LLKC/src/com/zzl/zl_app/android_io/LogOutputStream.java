package com.zzl.zl_app.android_io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;

public class LogOutputStream extends OutputStream {
	public static final int LOG_LEVEL = Log.VERBOSE;
	private ByteArrayOutputStream bos = new ByteArrayOutputStream();
	private String name;

	public LogOutputStream(String name) {
		this.name = name;
	}

	@Override
	public void write(int b) throws IOException {
		if (b == '\n') {
			String s = new String(this.bos.toByteArray());
			this.bos = new ByteArrayOutputStream();
		} else {
			this.bos.write(b);
		}
	}

}
