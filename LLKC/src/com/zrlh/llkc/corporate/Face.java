package com.zrlh.llkc.corporate;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;

import com.zzl.zl_app.util.ImageUtils;

public class Face {
	public static String[] faceNames = new String[] { "微笑", "害羞", "伤心", "大哭",
			"酷", "搞怪", "色", "嘘", "抓狂", "拜拜", "撇嘴", "汗", "亲吻", "问号", "噢耶", "困",
			"睡醒", "左斜看", "右斜看", "闭嘴", "西瓜", "闪电", "音乐", "晴天", "阴天", "红酒", "干杯",
			"啤酒", "饮料", "调酒", "咖啡", "火腿肠", "薯条", "玫瑰花", "吃饭", "口红", "眼睫毛",
			"铅笔", "香水", "足球", "药", "枪", "雪糕", "冰淇淋", "彩虹", "唇", "爱心", "钱", "烟",
			"高跟鞋" };

	// private static HashMap<String, Integer> faces;
	private static HashMap<String, Bitmap> faces;

	public void clearFaces() {
		faces.clear();
	}

	public static HashMap<String, Bitmap> getfaces(Context context) {
		int len = faceNames.length;
		if (faces != null) {
			return faces;
		}
		// faces = new HashMap<String, Integer>();
		faces = new HashMap<String, Bitmap>();
		String faceName = "";
		for (int i = 1; i <= len; i++) {
			faceName = "face" + i + ".png";
			try {
				// int id = R.drawable.class.getDeclaredField(faceName).getInt(
				// context);
				Bitmap bm = ImageUtils.getBitmap(context, faceName);
				faces.put(faceNames[i - 1], bm);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}

		return faces;
	}
}
