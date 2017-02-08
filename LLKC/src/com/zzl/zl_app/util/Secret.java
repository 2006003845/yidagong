package com.zzl.zl_app.util;

public class Secret {

	/**
	 * 加密
	 * 
	 * @param a
	 * @return
	 */
	public static String encriptString(byte[] a) {
		String ret = "";
		if (a == null)
			return null;
		for (int i = 0; i < a.length; i++) {
			ret += encriptString(a[i]);
		}
		return ret;
	}

	/**
	 * 加密
	 * 
	 * @param a
	 * @return
	 */
	public static String encriptString(String str) {
		byte[] a = str.getBytes();
		a = encriptXor(a);
		String ret = "";
		if (a == null)
			return null;
		for (int i = 0; i < a.length; i++) {
			ret += encriptString(a[i]);
		}
		return ret;
	}

	/**
	 * 加密
	 * 
	 * @param buf
	 */
	private static byte[] encriptXor(byte[] buf) {

		byte encriptKey = 118;
		byte key;
		System.out.println("加密过程 ：\r\n");
		for (int i = 0; i < buf.length; i++) {
			key = (byte) (buf[i] ^ encriptKey);
			System.out.println(i + "=" + buf[i] + " Xor " + encriptKey + " = "
					+ key);
			buf[i] = key;
			encriptKey = key;
		}
		return buf;
	}

	/**
	 * 解密
	 * 
	 * @param buf
	 */
	public static byte[] decriptXor(byte[] buf) {

		byte encriptKey = 118;
		byte nextencriptKey;
		System.out.println("解密过程 ：\r\n");
		for (int i = 0; i < buf.length; i++) {
			nextencriptKey = buf[i];
			buf[i] = (byte) (nextencriptKey ^ encriptKey);
			encriptKey = nextencriptKey;
		}
		return buf;
	}

	/**
	 * 解密
	 * 
	 * @param objStr
	 * @return
	 */
	public static String decriptXor(String objStr) {
		byte[] buf = decriptString(objStr);
		byte encriptKey = 118;
		byte nextencriptKey;
		System.out.println("解密过程 ：\r\n");
		for (int i = 0; i < buf.length; i++) {
			nextencriptKey = buf[i];
			buf[i] = (byte) (nextencriptKey ^ encriptKey);
			encriptKey = nextencriptKey;
		}
		return new String(buf);
	}

	private static byte[] decriptString(String a) {

		if (a == null)
			return null;
		byte[] c = a.getBytes();
		byte[] d = new byte[c.length / 2];

		for (int i = 0; i < d.length; i++) {
			d[i] = decriptString((char) c[i * 2], (char) c[i * 2 + 1]);
		}
		return d;
	}

	private static String encriptString(byte i) {

		char a = (char) (i / 12 + 77);
		char b = (char) (i % 12 + 77);
		return new String() + a + b;
	}

	private static byte decriptString(char m, char n) {

		int a = (int) (m - 77);
		int b = (int) (n - 77);
		return (byte) (a * 12 + b);
	}
}
