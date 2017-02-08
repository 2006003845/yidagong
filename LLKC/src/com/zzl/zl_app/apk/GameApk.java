package com.zzl.zl_app.apk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.zrlh.llkc.activity.HomeActivity;
import com.zrlh.llkc.corporate.ApplicationData;
import com.zrlh.llkc.funciton.LlkcBody;
import com.zzl.zl_app.apk.load.Downloader;
import com.zzl.zl_app.entity.Game;
import com.zzl.zl_app.util.FileTools;
import com.zzl.zl_app.util.Tools;

public class GameApk {
	public String apkUrl;
	public String apkName;
	public Game gameItem;
	private Downloader loader;

	public GameApk(String apkUrl, String apkName, Game item) {
		this.apkUrl = apkUrl;
		this.apkName = apkName;
		this.gameItem = item;
	}

	public void pauseDownLoad() {
		Tools.log("OnItem", "onitem" + 3 + "pause1");
		loader.pause();
		gameItem.isLoading = false;
	}

	/* http://game.91juhe.com/PassSystem/upDate/TestB.apk */
	private static final String sdCardPath = Environment
			.getExternalStorageDirectory().getAbsolutePath();

	public static String apkStorePath = LlkcBody.APK_SD_PATH;

	public Context mContext;

	public void lauchApk(Context context, Handler handler) {
		Bundle paramBundle = new Bundle();
		paramBundle.putBoolean("KEY_START_FROM_OTHER_ACTIVITY", true);
		int action = getAPKAction(context, gameItem.pack, 1);
		if (action == DOWNLOADGAME) {
		} else if (action == STARTGAME) {
		}
	}

	public void lauchApk(Context context, String filepath) {
		int action = -1;
		int currentVersion = 1;
		try {
			action = getAPKAction(context, gameItem.pack, currentVersion);
			if (action != STARTGAME) {
				installation(context, filepath);
			} else {
				try {
					// LoadAPK(context, paramBundle, dexpath, dexoutputpath);
					Intent intent = new Intent();
					// if (gameItem.pack.equals("com.zrong.savingYello")) {
					// intent.setAction("com.view.my_action_appId");
					// } else {
					// intent.setAction("com.view.my_action_"
					// + gameItem.gameAppid);
					// }
					context.startActivity(intent);

				} catch (Exception e) {
					Tools.log("error", e.getMessage());
				}
			}
		} catch (Exception e) {
		}
	}

	public int loadOrLauchApk(Context context, Handler handler) {
		Bundle paramBundle = new Bundle();

		paramBundle.putBoolean("KEY_START_FROM_OTHER_ACTIVITY", true);

		FileTools.creatDir(apkStorePath);
		String dexpath = apkStorePath + apkName;

		String dexoutputpath = apkStorePath;

		int currentVersion = 1;
		int action = -1;
		try {
			action = getAPKAction(context, gameItem.pack, currentVersion);
		} catch (Exception e) {
			// Log.e("error", e.getMessage());
		}
		if (action == DOWNLOADGAME) {
			// boolean bool = FileTools.isAPKExist(apkName);
			// if (bool) {
			// gameItem.gameApk.lauchApk(context, dexpath);
			// return 100;
			// }
			if (loader == null) {
				loader = new Downloader(apkUrl, dexpath, 0, context, handler,
						gameItem);
				ApplicationData.downloaderList.add(loader);
			}

			if (loader.isdownloading()) {
				return 200;
			}
			try {
				loader.getDownloaderInfors();
				loader.download();
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else if (action == UPDATEGAME) {
			if (loader == null) {
				loader = new Downloader(apkUrl, dexpath, 0, context, handler,
						gameItem);
				ApplicationData.downloaderList.add(loader);
			}
			if (loader.isdownloading()) {
				return 200;
			}
			loader.getDownloaderInfors();
			loader.download();
		} else if (action == STARTGAME) {
			try {
				if (mPackageManager == null) {
					mPackageManager = HomeActivity.mInstance
							.getPackageManager();
				}
				ICommunity ic = new CommunicationImpl();
				ApplicationItem item = ic.getApplicationItem(mPackageManager,
						gameItem.pack);
				ic.startCommunication(HomeActivity.mInstance, item);
			} catch (Exception e) {
				Tools.log("error", e.getMessage());
			}
		}
		return action;
	}

	public static final byte DOWNLOADGAME = 0;
	public static final byte UPDATEGAME = 1;
	public static final byte STARTGAME = 2;

	private PackageManager mPackageManager;

	/**
	 * 
	 * @param dexpath
	 *            sdcard path
	 * @param dexoutputpath
	 *            apk path
	 * @param curVersionCode
	 *            当前游戏的版本号，如果当前游戏没有安装,就是0
	 * @return apk应该执行的动作 0 应该下载apk,1应该更新apk,2应该开启apk
	 */
	public byte getAPKAction(Context context, String packageName,
			int lastVersionCode) {
		List<PackageInfo> packages = context.getPackageManager()
				.getInstalledPackages(0);

		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);

			if (packageInfo.packageName.equals(packageName)) {
				if (packageInfo.versionCode < lastVersionCode) {
					return UPDATEGAME;
				} else {
					return STARTGAME;
				}
			}
		}

		return DOWNLOADGAME;
	}

	// private void LoadAPK(Context context, Bundle paramBundle, String dexpath,
	// String dexoutputpath) {
	// ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
	// DexClassLoader localDexClassLoader = new DexClassLoader(dexpath,
	// dexoutputpath, null, localClassLoader);
	// try {
	// PackageInfo plocalObject = context.getPackageManager()
	// .getPackageArchiveInfo(dexpath, 1);
	//
	// if ((plocalObject.activities != null)
	// && (plocalObject.activities.length > 0)) {
	// String activityname = plocalObject.activities[0].name;
	// Class localClass = localDexClassLoader.loadClass(activityname);
	// Constructor localConstructor = localClass
	// .getConstructor(new Class[] {});
	// Object instance = null;
	// try {
	// instance = localConstructor.newInstance(new Object[] {});
	// } catch (ExceptionInInitializerError e) {
	// // TODO
	// }
	// if (instance == null) {
	// return;
	// }
	// Method localMethodSetActivity = localClass.getDeclaredMethod(
	// "setActivity", new Class[] { Activity.class });
	// localMethodSetActivity.setAccessible(true);
	// localMethodSetActivity.invoke(instance,
	// new Object[] { context });
	// Method methodonCreate = localClass.getDeclaredMethod(
	// "onCreate", new Class[] { Bundle.class });
	// methodonCreate.setAccessible(true);
	// methodonCreate.invoke(instance, new Object[] { paramBundle });
	// HomeActivity.mInstance.isLaucherToOtherAPK = true;
	// }
	// } catch (Exception ex) {
	// Log.e("error", ex.getMessage());
	//
	// }
	// }

	public static void installation(Context context, String path) {
		File file = new File(path);
		installation(context, file);
		// delFile(file);
	}

	public static File getFile(Context context, String fileName) {
		try {
			InputStream is = context.getAssets().open(fileName);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void installation(Context context, File file) {
		openFile(context, file);
		// delFile(file);
	}

	/* 自定义删除文件方法 */
	private static void delFile(File file) {
		// File myFile = new File(strFileName);
		if (file.exists()) {
			file.delete();
		}
	}

	/* 在手机上打开文件的method */
	private static void openFile(Context context, File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		/* 调用getMIMEType()来取得MimeType */
		String type = getMIMEType(f);
		/* 设置intent的file与MimeType */
		intent.setDataAndType(Uri.fromFile(f), type);

		context.startActivity(intent);
	}

	/* 判断文件MimeType的method */
	private static String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		/* 取得扩展名 */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/* 依扩展名的类型决定MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("apk")) {
			/* android.permission.INSTALL_PACKAGES */
			type = "application/vnd.android.package-archive";
		} else {
			type = "*";
		}
		/* 如果无法直接打开，就跳出软件列表给用户选择 */
		if (end.equals("apk")) {
		} else {
			type += "/*";
		}
		return type;
	}
}
