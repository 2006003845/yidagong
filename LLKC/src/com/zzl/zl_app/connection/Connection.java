package com.zzl.zl_app.connection;

import java.util.ArrayList;
import java.util.Vector;

import org.apache.http.message.BasicNameValuePair;

import com.zrlh.llkc.corporate.PlatformAPI;
import com.zzl.zl_app.io.BaseHttp;
import com.zzl.zl_app.io.HttpThread;
import com.zzl.zl_app.io.HttpThreadManager;
import com.zzl.zl_app.io.HttpWorker;

public class Connection {
	private static Vector<Packet> packetPool = new Vector<Packet>();
	private static Vector<Integer> portVector = new Vector<Integer>();

	public static byte[] getDataSync(byte[] data,
			ArrayList<BasicNameValuePair> pairs, String requestUrl) {
		Packet p = null;

		if (PlatformAPI.packageStructure.equals(PlatformAPI.JSONTAG)) {
			p = new JsonPacket(data);
		} else if (PlatformAPI.packageStructure.equals(PlatformAPI.PAIRTAG)) {
			p = new PairPacket(pairs);
		} else {
			p = new ChunkPacket(data);
		}
		return getRespSync(p, PlatformAPI.BaseUrl + requestUrl);
	}

	private static byte[] getRespSync(Packet p, String requestUrl) {
		BaseHttp http = HttpThreadManager.getThread(BaseHttp.HTTPWORKER);
		// http.setPostURL(PlatformAPI.requestUrl, p.getData());
		http.setPostURL(requestUrl, p.getPairs());

		http.startRun();
		byte[] bytes = ((HttpWorker) http).getResponse();
		return bytes;
	}

	public static synchronized Packet getRequest() throws InterruptedException {
		if (!packetPool.isEmpty()) {
			Packet pak = (Packet) packetPool.elementAt(0);
			packetPool.removeElementAt(0);
			return pak;
		}
		return null;
	}

	public static synchronized int getPortType() throws InterruptedException {
		if (!portVector.isEmpty()) {
			int portType = portVector.elementAt(0);
			packetPool.removeElementAt(0);
			return portType;
		}
		return -1;
	}

	public static synchronized boolean isEmpty() {
		return packetPool.isEmpty();
	}

	/**
	 * 
	 * <p>
	 * Description: 发送packetPool里的数据
	 * </p>
	 * 
	 * @author yz
	 */
	public static void sendRequest() {

		BaseHttp http = HttpThreadManager.getThread(BaseHttp.HTTPTHREAD);

		if (http == null)
			return;

		try {
			Packet p = getRequest();
			int portType = getPortType();
			if (p != null) {
				http.setPostURL(PlatformAPI.BaseUrl, p.getData());
				// switch (portType) {
				// case PlatformAPI.Port_User:
				// http.setPostURL(PlatformAPI.requestUrl, p.getData());
				// break;
				// }
				http.startRun();
			} else {
				http.setStat(HttpThread.IDLE);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block

			http.setStat(HttpThread.IDLE);
			e.printStackTrace();
		}
	}

}
