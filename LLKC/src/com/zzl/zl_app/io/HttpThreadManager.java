package com.zzl.zl_app.io;

import java.util.Vector;

import android.content.Context;
import android.net.ConnectivityManager;

import com.zzl.zl_app.connection.Connection;
import com.zzl.zl_app.util.SystemAPI;

/**
 * 
 * <p>
 * Titile:HttpThreadManager
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright:Copyright(c)2010
 * </p>
 * <p>
 * Company:zrong
 * </p>
 * 
 * @author yz
 * @version 1.0
 * @date 2012-5-3
 */
public class HttpThreadManager implements Runnable {
	private static final String url = "";

	private static final int ThreadNumber = 3;

	private static Vector<HttpThread> threadPool = new Vector<HttpThread>();

	private HttpThreadManager() {

	}

	private static HttpThreadManager manager = null;

	private static ConnectivityManager conManager;

	public static HttpThreadManager sharedManager(Context context) {
		if (manager == null) {
			manager = new HttpThreadManager();
			conManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			manager.loop = true;
			Thread th = new Thread(manager);
			th.start();
		}
		return manager;
	}

	/**
	 * 
	 * <p>
	 * Description:获取到http后，如果不使用，请将http状态设置成IDLE
	 * </p>
	 * 
	 * @author yz
	 * @param type
	 *            2 同步http 1异步http
	 * @return
	 */
	public static BaseHttp getThread(byte type) {
		if (type == BaseHttp.HTTPTHREAD) {
			for (int i = 0; i < threadPool.size(); i++) {
				HttpThread th = threadPool.elementAt(i);

				if (th != null && th.getStat() == HttpThread.IDLE) {

					th.setStat(HttpThread.PREPARETION);

					// Log.v("IO", "get pool http index ="+i);

					return th;
				}
			}

			if (threadPool.size() < ThreadNumber) {
				// Log.v("IO", "new http");

				HttpThread th = new HttpThread(conManager);

				// th.setStateListener(MessageHandler.sharedHandler());

				// th.setReciever(IOReader.sharedReader());

				th.setStat(HttpThread.PREPARETION);

				threadPool.addElement(th);

				return th;
			}
		} else {
			HttpWorker httpWorker = new HttpWorker(conManager);

			httpWorker.setStat(HttpThread.PREPARETION);

			return httpWorker;
		}

		return null;
	}

	private boolean loop = false;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (loop) {
			if (Connection.isEmpty()) {
				// synchronized(this)
				// {
				// try
				// {
				// wait();
				// } catch (InterruptedException e)
				// {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }

			} else {
				Connection.sendRequest();
			}

			SystemAPI.sleep(1000);
		}
	}

}
