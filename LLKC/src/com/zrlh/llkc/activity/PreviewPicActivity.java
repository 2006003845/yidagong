package com.zrlh.llkc.activity;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zrlh.llkc.R;
import com.zrlh.llkc.corporate.base.BaseActivity;
import com.zzl.zl_app.cache.ImageCache;
import com.zzl.zl_app.util.ImageUtils;
import com.zzl.zl_app.widget.ImageZoomView;
import com.zzl.zl_app.widget.SimpleZoomListener;
import com.zzl.zl_app.widget.ZoomState;

public class PreviewPicActivity extends BaseActivity {
	public static final String TAG = "previewpic";

	public static void launch(Context c, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(c, PreviewPicActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		c.startActivity(intent);
	}

	private String picUrl;
	private ProgressBar pb;
	private TextView progressTV;
	// ImageZoomView previewView;
	ImageZoomView previewView;
	int c = 0;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				int progress = msg.arg1;
				if (c++ == 0) {
					pb.setMax(msg.arg2);
				}
				pb.setProgress(progress);
				if (msg.arg2 != 0) {
					progressTV.setText(progress * 100 / msg.arg2 + "%");
				}
				pb.invalidate();
				// pb.incrementProgressBy(progress);
				break;
			case 2:
				try {

					if (picBM == null)
						picBM = ImageCache.getInstance()
								.getUnSyncImgCachFromDisk(getContext(), picUrl);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (picBM != null) {
					pb.setVisibility(View.GONE);
					progressTV.setVisibility(View.GONE);
					previewView.setVisibility(View.VISIBLE);
					previewView.setImage(picBM);
					// ImageUtils.setImageViewSrc(previewView, new
					// BitmapDrawable(
					// picBM));
					// previewView.setImageBitmap(picBM);
					previewView.postInvalidate();
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addActMap(TAG, getContext());
		MobclickAgent.openActivityDurationTrack(false);		
		MobclickAgent.updateOnlineConfig(this);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			picUrl = b.getString("picUrl");
		}
		setContentView(R.layout.check_resource_pic);
		initView();
	}

	TextView menuBtn;
	private TextView titleTV;

	private void initView() {
		titleTV = (TextView) this.findViewById(R.id.title_tv);
		titleTV.setText("查看图片");
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
				// ImageUtils.destoryImageViewSrc(previewView);
			}
		});
		menuBtn = (TextView) this.findViewById(R.id.btn);
		menuBtn.setVisibility(View.VISIBLE);
		menuBtn.setText(R.string.save);
		menuBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (picUrl != null && !picUrl.equals("")) {
					ImageCache.getInstance().loadImgAndSave(getContext(),
							picUrl, true);
				}
			}
		});
		pb = (ProgressBar) this.findViewById(R.id.pb);
		progressTV = (TextView) this.findViewById(R.id.progress_tv);
		pb.setMax(100);
		// previewView = (ImageZoomView) this.findViewById(R.id.pic_preview);
		previewView = (ImageZoomView) this.findViewById(R.id.pic_preview);
		// previewView.setImage(contentPicBm);
		if (picUrl != null && !picUrl.equals("")) {
			try {

				picBM = ImageCache.getInstance().getUnSyncImgCachFromDisk(
						getContext(), picUrl);
				// image = new File(FileConstant.savePath +
				// FileConstant.Path_Img
				// + "/" + dirName + "_" + file);
				// FileTools.creatFile(image);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (picBM == null) {
				new LoadPicThread().start();
			} else {
				previewView.setVisibility(View.VISIBLE);
				pb.setVisibility(View.GONE);
				progressTV.setVisibility(View.GONE);
				previewView.setImage(picBM);
				// ImageUtils.setImageViewSrc(previewView, new BitmapDrawable(
				// picBM));
			}
		}
		ZoomState mZoomState = new ZoomState();
		previewView.setZoomState(mZoomState);
		SimpleZoomListener mZoomListener = new SimpleZoomListener();
		mZoomListener.setZoomState(mZoomState);
		previewView.setOnTouchListener(mZoomListener);
		mZoomState.setPanX(0.5f);
		mZoomState.setPanY(0.5f);
		mZoomState.setZoom(1f);
		mZoomState.notifyObservers();
	}

	private byte[] imageBuffer;
	private Bitmap picBM;
	File image;

	class LoadPicThread extends Thread {
		@Override
		public void run() {
			super.run();
			URL url;
			BufferedInputStream is = null;

			ByteArrayOutputStream baos = null;
			int compSize = 0;
			int len = -1;
			try {
				url = new URL(picUrl);
				URLConnection conn = url.openConnection();
				conn.connect();
				len = conn.getContentLength();
				is = new BufferedInputStream(conn.getInputStream());
				byte[] buffer = new byte[4096];
				baos = new ByteArrayOutputStream();
				int b = -1;
				while ((b = is.read(buffer)) != -1) {
					baos.write(buffer, 0, b);
					compSize += b;
					Message msg = handler.obtainMessage(1);
					// msg.arg1 = compSize * 100 / len;
					msg.arg1 = compSize;
					msg.arg2 = len;
					handler.sendMessage(msg);
					// baos.write(b);
					if (isPause) {
						break;
					}
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (baos != null) {
						// picBM = ImageUtils
						// .stream2Bitmap(new FileInputStream(image));
						imageBuffer = baos.toByteArray();
						baos.close();
					}

					if (is != null)
						is.close();

				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			if (compSize == len && imageBuffer != null
					&& imageBuffer.length > 0) {
				try {
					Bitmap bm = ImageUtils.Bytes2Bitmap(imageBuffer);
					imageBuffer = null;
					Bitmap newBm = ImageUtils.zoomBitmap(bm, bm.getWidth() - 1,
							bm.getHeight() - 1);
					if (!bm.isRecycled())
						bm.recycle();
					ImageCache.getInstance().setImgKey(picUrl, newBm);
					Message msg2 = handler.obtainMessage(2);
					handler.sendMessage(msg2);
				} catch (OutOfMemoryError e) {
					// TODO: handle exception
				}
			}

		}
	}

	boolean isPause = false;

	@Override
	protected void onDestroy() {
		isPause = true;
		super.onDestroy();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(TAG);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(TAG);
	}

	@Override
	public BaseActivity getContext() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void setDialogContent(AlertDialog dialog, int layoutId, String msg,
			String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDialogTitle(AlertDialog dialog, int layoutId, String title,
			String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public OnClickListener setPositiveClickListener(AlertDialog dialog,
			int layoutId, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OnClickListener setNegativeClickListener(AlertDialog dialog,
			int layoutId, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onBackPressed() {
		closeOneAct(TAG);
		super.onBackPressed();
		// ImageUtils.destoryImageViewSrc(previewView);
	}

}
