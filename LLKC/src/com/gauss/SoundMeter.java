package com.gauss;

import android.os.Environment;
import android.os.Handler;

public class SoundMeter {
	static final private double EMA_FILTER = 0.6;

	// private MediaRecorder mRecorder = null;
	private SpeexRecorder mRecorder = null;
	private double mEMA = 0.0;

	private Handler handle;

	public SoundMeter(Handler handle) {
		this.handle = handle;
	}

	public void start(String name) {
		if (!Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return;
		}
		if (mRecorder == null) {
			mRecorder = new SpeexRecorder(name, handle);
			Thread th = new Thread(mRecorder);
			th.start();
		}
		mRecorder.setRecording(true);
	}

	public void stop() {
		if (mRecorder != null) {
			mRecorder.setRecording(false);
			mRecorder = null;
		}
	}

	public void destory() {
		if (mRecorder != null)
			destory();
	}

	public double getAmplitude() {
		if (mRecorder != null)
			return (mRecorder.getMaxAmplitude() / 2700.0);
		else
			return 0;

	}

	public double getAmplitudeEMA() {
		double amp = getAmplitude();
		mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
		return mEMA;
	}
}
