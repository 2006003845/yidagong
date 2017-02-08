package com.zzl.zl_app.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;

import com.zrlh.llkc.activity.MainActivity;
import com.zrlh.llkc.corporate.Face;

public class TextUtil {
	
	
	/**
	 * 加亮 @****： 或 @****: 或@****[空格] <br>
	 * Html.fromHtml(String) *
	 * 
	 * @param str
	 * @return
	 */
	public static Spanned highLight(String str) {
		Pattern pattern = Pattern.compile("@[^\\s:：]+[:：\\s]");
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			String m = matcher.group();
			str = str.replace(m, "<font color=Navy>" + m + "</font>");
		}
		return Html.fromHtml(str);
	}

	/**
	 * 加亮 @****： 或 @****: 或@****[空格] <br>
	 * SpannableString.setSpan(Object what, int start, int end, int flags)
	 * 
	 * @param text
	 * @return
	 */
	// 这个和hightLight(String str) 的效率对比?
	public static SpannableString light(CharSequence text) {
		SpannableString spannableString = new SpannableString(text);
		Pattern pattern = Pattern.compile("@[^\\s:：]+[:：\\s]");
		Matcher matcher = pattern.matcher(spannableString);
		while (matcher.find()) {
			spannableString.setSpan(new ForegroundColorSpan(Color.CYAN),
					matcher.start(), matcher.end(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return spannableString;
	}

	/**
	 * 格式化名字 <br>
	 * 用于保存微博图像，截取url的最后一段做为图像文件名
	 * 
	 * @param url
	 * @return
	 */
	public static String formatName(String url) {
		if (url == null || "".equals(url)) {
			return url;
		}
		int start = url.lastIndexOf("/");
		int end = url.lastIndexOf(".");
		if (start == -1 || end == -1) {
			return url;
		}
		return url.substring(start + 1, end);
	}

	/**
	 * 格式化来源
	 * 
	 * @param name
	 * @return
	 */
	public static String formatSource(String name) {
		if (name == null || "".equals(name)) {
			return name;
		}
		int start = name.indexOf(">");
		int end = name.lastIndexOf("<");
		if (start == -1 || end == -1) {
			return name;
		}
		return name.substring(start + 1, end);
	}

	public static SpannableString formateText(String text) {
		String exReg = "";
		return null;
	}

	/**
	 * 匹配表情 （两个连续的单字表情未能正确显示）
	 * 
	 * @param text
	 * @param context
	 * @return
	 */
	public static SpannableString formatImage(CharSequence text, Context context) {
		SpannableString spannableString = new SpannableString(text);
		Pattern pattern = Pattern.compile("\\[[^0-9]{1,4}\\]"); // 正则式出错？两个连续的单字表情都无法正确替换
		Matcher matcher = pattern.matcher(spannableString);
		while (matcher.find()) {
			String faceName = matcher.group();
			String key = faceName.substring(1, faceName.length() - 1);
			if (Face.getfaces(context).containsKey(key)) {
				Bitmap b = Face.getfaces(context).get(key);
				Drawable d = new BitmapDrawable(b);
				int w = (int) (35 * MainActivity.WIDTH_RATIO);
				d.setBounds(0, 0, w, w);
				ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
				spannableString.setSpan(span, matcher.start(), matcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				// spannableString
				// .setSpan(new ImageSpan(context, Face.getfaces(context)
				// .get(key)), matcher.start(), matcher.end(),
				// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return spannableString;
	}

	/**
	 * 将text中@某人的字体加亮，匹配的表情文字以表情显示
	 * 
	 * @param text
	 * @param context
	 * @return
	 */
	public static SpannableString formatContent(CharSequence text,
			Context context) {
		if (text == null) {
			return null;
		}
		SpannableString spannableString = new SpannableString(text);
		/*
		 * @[^\\s:：]+[:：\\s] 匹配@某人 \\[[^0-9]{1,4}\\] 匹配表情
		 */
		Pattern pattern = Pattern
				.compile("\\[[^0-9]{1,3}\\]|@[^\\s:：]+[:：\\s]");
		Matcher matcher = pattern.matcher(spannableString);
		while (matcher.find()) {
			String match = matcher.group();
			if (match.startsWith("@")) { // @某人，加亮字体
				spannableString.setSpan(new ForegroundColorSpan(0xff0077ff),
						matcher.start(), matcher.end(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else if (match.startsWith("[")) { // 表情
				String key = match.substring(1, match.length() - 1);
				if (Face.getfaces(context).containsKey(key)) {
					Bitmap b = Face.getfaces(context).get(key);
					Drawable d = new BitmapDrawable(b);
					int w = (int) (35 * MainActivity.WIDTH_RATIO);
					d.setBounds(0, 0, w, w);
					ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
					spannableString.setSpan(span, matcher.start(),
							matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					// spannableString.setSpan(new ImageSpan(context, Face
					// .getfaces(context).get(key)), matcher.start(),
					// matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return spannableString;
	}

}
