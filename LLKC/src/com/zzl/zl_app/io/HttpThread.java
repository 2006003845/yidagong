package com.zzl.zl_app.io;

import android.net.ConnectivityManager;

public class HttpThread extends BaseHttp implements Runnable {

	// private HttpStateListener listener;

	public HttpThread(ConnectivityManager conManager) {
		super(conManager);
	}

	public static final String TAG = "HttpThread";

	private boolean isRun = false;

	public void startRun() {

		if (!isRun) {
			isRun = true;

			Thread t = new Thread(this);

			t.start();
		} else {
			synchronized (this) {
				// Log.v("IO", "notify");
				notify();
			}
		}
	}

	private Object obj = new Object();

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (isRun) {
			// Log.v("IO", "http run");
			synchronized (this) {
				try {
					if (requestUrl == null) {
						// Log.v("IO", "wait");

						wait();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// Log.v("IO", "http working");
			setStat(WORKING);

			if (requestUrl != null) {
				byte[] data = getResponse();

			} else {
				this.setStat(this.IDLE);
				// Log.v("IO", "http idle");
			}
		}
	}

	/**
	 * 
	 * <p>
	 * Description:手动获取数据，当httpThread没有数据接受者的时候使用
	 * </p>
	 * 
	 * @author yz
	 * @return
	 */
	public byte[] pullData() {
		if (this.stat == this.FINISH) {
			byte[] temp = request;
			this.setStat(IDLE);
			request = null;
			return temp;
		}
		return null;
	}

	@Override
	public void setStat(byte stat) {
		// TODO Auto-generated method stub
		super.setStat(stat);

		// if(listener != null && stat ==IDLE)
		// {
		// listener.OnStateIdle(this, stat);
		// }
	}

}
