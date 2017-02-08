package com.zzl.zl_app.connection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import android.util.Log;

public class ProtocolUtil {

	public static int getResponseStatus(String[] reponseCodes, String code) {
		if (reponseCodes == null && code == null) {
			return -1;
		}
		for (int i = 0, len = reponseCodes.length; i < len; i++) {
			if (code.equals(reponseCodes[i])) {
				return i;
			}
		}
		return -1;
	}

	

}
