package com.zzl.zl_app.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;

public class Tools {

	@SuppressWarnings("deprecation")
	public static int getSDKVersionNumber() {

		int sdkVersion;
		try {
			sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);

		} catch (NumberFormatException e) {

			sdkVersion = 0;
		}
		return sdkVersion;
	}

	/**
	 * 正则判断是否则数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (str == null)
			return false;
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isUrl(String url) {
		if (url == null) {
			return false;
		}
		String regEx = "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-"
				+ "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"
				+ "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}"
				+ "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"
				+ "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-"
				+ "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"
				+ "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/"
				+ "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*$";
		Pattern p = Pattern.compile(regEx);
		Matcher matcher = p.matcher(url);
		return matcher.matches();
	}

	public static String getPropertyValue(Context context, String key) {
		String strValue = "";
		Properties props = new Properties();
		try {
			props.load(context.getAssets().open("config.properties"));
			strValue = props.getProperty(key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strValue;
	}

	public static String path = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	private static Properties props = null;

	public static String getPropertyValue(Context context, String key,
			String dir) {
		String strValue = "";

		try {
			if (props == null) {
				props = new Properties();
				FileInputStream is = new FileInputStream(new File(path + "/"
						+ dir + "/" + "config.properties"));
				props.load(is);
			}
			strValue = props.getProperty(key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strValue;
	}

	public static boolean checkNetWorkStatus(Context context) {
		boolean result;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (netinfo != null && netinfo.isConnected()) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	public static String getDirNameFromUrl(String url) {
		if (url == null) {
			return null;
		}
		char[] chars = url.toCharArray();
		int index = 0;
		for (int i = chars.length - 1; i >= 0; i--) {
			if (chars[i] == '/') {
				index = i;
				break;
			}
		}
		if (index < 4)
			return "";

		String url2 = url.substring(0, index);
		char[] chars2 = url2.toCharArray();
		int index2 = 0;
		for (int i = chars2.length - 1; i >= 0; i--) {
			if (chars2[i] == '/') {
				index2 = i;
				break;
			}
		}

		String dirName = url2.substring(index2 + 1);
		return dirName;
	}

	public static String getFileNameFromUrl(String url) {
		if (url == null) {
			return null;
		}
		char[] chars = url.toCharArray();
		int start = 0;
		for (int i = chars.length - 1; i >= 0; i--) {
			if (chars[i] == '/') {
				start = i;
				break;
			}
		}
		int end = url.lastIndexOf(".");
		if (start < chars.length && start < end && end < chars.length) {
			return url.substring(start + 1, end);
		} else {
			return null;
		}

	}

	public String getFPS2(Activity act) {
		Runtime runtime = Runtime.getRuntime();
		return "总:" + this.getTotalMemory() + ", " + "可用:"
				+ this.getAvailMemory(act) + ","
				+ (runtime.freeMemory() / 1024) + "M,"
				+ (runtime.maxMemory() / 1024) + "M,"
				+ (runtime.totalMemory() / 1024 + "M");
	}

	private String getAvailMemory(Activity act) {// 获取android当前可用内存大小
		ActivityManager am = (ActivityManager) act
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; 当前系统的可用内存
		return Formatter.formatFileSize(act, mi.availMem);// 将获取的内存大小规格化
	}

	private String getTotalMemory() {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		// String[] arrayOfString;
		// long initial_memory = 0;

		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
			String Str3 = localBufferedReader.readLine();
			String str4 = str2.substring(str2.length() - 9, str2.length() - 3);

			localBufferedReader.close();

			return "" + Integer.parseInt(str4) / 1000 + "M" + "," + Str3 + "M";
		} catch (IOException e) {
		}
		return "";
		// return Formatter.formatFileSize(getBaseContext(), initial_memory);//
		// Byte转换为KB或者MB，内存大小规格化
	}

	public static String getStringFromRes(Context context, int resID) {
		String cont = null;
		try {
			cont = context.getResources().getString(resID);
		} catch (Exception e) {
		}
		return cont;
	}

	public static void log(String tag, String value) {
		// Log.i(tag, value);
	}

	public static void recycleBitmap(Bitmap img) {
		if (img != null && !img.isRecycled()) {
			img.recycle();
			System.gc();
		}
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static void copy(String content, Context context) {
		// 得到剪贴板管理器
		int ver = getSDKVersionNumber();
		if (ver > 11) {
			android.content.ClipboardManager cmb = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(content);
		} else {
			android.text.ClipboardManager cmb = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(content);
		}
	}

}
