/**
 * 
 */
package com.gauss;

import java.io.File;

import android.os.Handler;

import com.gauss.speex.encode.SpeexDecoder;

public class SpeexPlayer {
	private String fileName = null;
	private SpeexDecoder speexdec = null;
	private PlayCallback playCallback;
	Handler handler;

	public SpeexPlayer(Handler handler) {
		this.handler = handler;
	}

	RecordPlayThread rpt;

	public void startPlay(String fileName, PlayCallback playCallback) {
		stopPlay();
		this.tag = null;
		this.fileName = fileName;
		this.playCallback = playCallback;
		try {
			speexdec = new SpeexDecoder(new File(this.fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		rpt = new RecordPlayThread();
		isPlay = true;
		Thread th = new Thread(rpt);
		th.start();
	}

	String tag;

	public void startPlay(String fileName, PlayCallback playCallback, String tag) {
		stopPlay();
		this.fileName = fileName;
		this.tag = tag;
		this.playCallback = playCallback;
		try {
			speexdec = new SpeexDecoder(new File(this.fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		rpt = new RecordPlayThread();
		isPlay = true;
		Thread th = new Thread(rpt);
		th.start();
	}

	public void stopPlay() {
		if (speexdec != null) {
			isPlay = false;
			speexdec.setPaused(true);
			speexdec = null;
			if (this.playCallback != null) {
				if (tag != null)
					this.playCallback.finish(tag);
				else
					this.playCallback.finish(fileName);
			}
		}
	}

	public boolean isPlay = false;

	class RecordPlayThread extends Thread {

		public void run() {
			try {
				if (speexdec != null)
					speexdec.decode();
			} catch (Exception t) {
				t.printStackTrace();
			} finally {
				if (handler != null)
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							if (tag != null)
								playCallback.finish(tag);
							else
								playCallback.finish(fileName);
							isPlay = false;
						}
					}, 300);

			}
		}
	};
}