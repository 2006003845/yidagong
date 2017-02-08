package com.zzl.zl_app.connection;

public class JsonPacket extends Packet {

	public JsonPacket(byte[] data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	public byte[] getSendData() {
		return this.data;
	}

}
