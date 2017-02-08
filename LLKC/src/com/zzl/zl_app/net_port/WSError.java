package com.zzl.zl_app.net_port;

public class WSError extends Throwable {

	private static final long serialVersionUID = -4517449691263822682L;
	private String message;

	public WSError() {
	}

	public WSError(String message) {
		this.message = message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}