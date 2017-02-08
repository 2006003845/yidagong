package com.zzl.zl_app.io;

import android.net.ConnectivityManager;

public class HttpWorker extends BaseHttp {

	// @Override
	public void startRun() {
		// TODO Auto-generated method stub
		if (requestUrl == null) {
			setStat(IDLE);
		} else {
			setStat(WORKING);
		}

	}

	public HttpWorker(ConnectivityManager conManager) {
		super(conManager);
	}

	public byte[] getResponse() {
		if (requestUrl == null) {
			setStat(IDLE);
		} else {
			setStat(WORKING);
		}

		return super.getResponse();
	}

}
