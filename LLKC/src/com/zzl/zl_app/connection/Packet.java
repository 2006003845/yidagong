package com.zzl.zl_app.connection;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

public abstract class Packet {

	public static final String TAG = "Packet";

	/** value����ֵ */
	private static final int MAX = 0xFFFF;
	/** ˳���� */
	private static int value = 0;
	/** ΨһId */
	private long uniqueId;

	private long playId;

	protected byte[] data;

	public byte[] getData() {
		return data;
	}

	public static long getUnique() {
		try {
			return System.currentTimeMillis() << 20 | value;
		} finally {
			value = value++ < MAX ? value : 0;
		}
	}

	/**
	 * 
	 * @param type
	 * @param data
	 */
	public Packet(byte[] data) {
		if (data.length > Short.MAX_VALUE)
			throw new ArrayIndexOutOfBoundsException("packet data to long");
		this.data = data;
		uniqueId = getUnique();
	}

	private ArrayList<BasicNameValuePair> pairs;

	public Packet(ArrayList<BasicNameValuePair> pairs) {
		super();
		this.pairs = pairs;
	}

	public ArrayList<BasicNameValuePair> getPairs() {
		return pairs;
	}

	public void setPairs(ArrayList<BasicNameValuePair> pairs) {
		this.pairs = pairs;
	}

	// public abstract Object getData();
}
