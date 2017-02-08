package com.zzl.zl_app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.format.Time;

import com.zrlh.llkc.R;
import com.zrlh.llkc.ui.LLKCApplication;

/**
 * 
 * @author 子龙
 * 
 */
public class TimeUtil {

	/**
	 * 判断是否过期
	 * 
	 * @param deadline
	 * @return
	 */
	public static boolean isOverDue(Date deadline) {
		Date d = new Date();
		long interval = d.getTime() - deadline.getTime();
		return interval >= 0;
	}

	/**
	 * 判断是否过期
	 * 
	 * @param deadline
	 * @return
	 * @throws ParseException
	 */
	public static boolean isOverDue(String deadline) {
		if (deadline == null) {
			return false;
		}
		long milliseconds = Long.parseLong(deadline);
		return isOverDue(milliseconds);
	}

	/**
	 * 判断是否过期
	 * 
	 * @param deadline
	 * @return
	 * @throws ParseException
	 */
	public static boolean isOverDue(long deadline) {
		return isOverDue(new Date(deadline));
	}

	/**
	 * long型字符串 转换为date
	 * 
	 * @param longStr
	 * @return
	 */
	public static Date switchToDate(String longStr) {
		long milliseconds = Long.parseLong(longStr);
		return switchToDate(milliseconds);
	}

	/**
	 * 
	 * @param milliseconds
	 * @return
	 */
	public static Date switchToDate(long milliseconds) {
		return new Date(milliseconds);
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String getTimeStr(long date) {
		return getTimeStr(new Date(date));
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String getTimeStr(long date, String format) {
		return getTimeStr(new Date(date), format);
	}

	public static String getTimeStr(Date date) {
		if (date == null) {
			return "";
		}
		Time today = new Time("GMT+8");
		today.setToNow();
		Date d = new Date();
		// long interval = today.toMillis(true) - date.getTime();
		long interval = d.getTime() - date.getTime();
		interval = interval < 0 ? -interval : interval;
		boolean isSameDay = (interval / 86400000) == 0 ? true : false;
		if (isSameDay) {
			if (interval < 60 * 1000) {
				return (interval / 1000)
						+ Tools.getStringFromRes(LLKCApplication.getInstance(),
								R.string.miaoqian);
			} else if (interval < 60 * 60 * 1000) {
				return (interval / (60 * 1000))
						+ Tools.getStringFromRes(LLKCApplication.getInstance(),
								R.string.fenqian);
			} else {
				return (interval / (60 * 60 * 1000))
						+ Tools.getStringFromRes(LLKCApplication.getInstance(),
								R.string.shiqian);
			}
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
					Locale.CHINA);
			return dateFormat.format(date);
		}
	}

	public static String getTimeStr(Date date, String format) {
		if (date == null) {
			return "";
		}
		// Time today = new Time("GMT+8");
		// today.setToNow();
		Date d = new Date();// 当前时间
		long interval = d.getTime() - date.getTime();
		interval = interval < 0 ? -interval : interval;
		boolean isSameDay = (interval / 86400000) == 0 ? true : false;
		if (isSameDay) {
			if (interval < 60 * 1000) {
				return (interval / 1000)
						+ Tools.getStringFromRes(LLKCApplication.getInstance(),
								R.string.miaoqian);
			} else if (interval < 60 * 60 * 1000) {
				return (interval / (60 * 1000))
						+ Tools.getStringFromRes(LLKCApplication.getInstance(),
								R.string.fenqian);
			} else {
				return (interval / (60 * 60 * 1000))
						+ Tools.getStringFromRes(LLKCApplication.getInstance(),
								R.string.shiqian);
			}
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format,
					Locale.CHINA);
			return dateFormat.format(date);
		}
	}

	public static String getTimeStr2(String longdate, String format) {
		if (longdate == null || longdate.equals("")) {
			return "";
		}
		return getTimeStr2(new Date(Long.parseLong(longdate)), format);
	}

	public static String getTimeStr2(long date, String format) {
		return getTimeStr2(new Date(date), format);
	}

	public static String getTimeStr2(Date date, String format) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
		return dateFormat.format(date);

	}

	public static String getLocalTimeString(long date) {
		Date d = new Date(date);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.CHINA);
		return dateFormat.format(d);
	}
}
