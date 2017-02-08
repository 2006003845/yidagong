package com.zzl.zl_app.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class FileReaderAndWriter {
	/**
	 * 读取文件内容
	 * 
	 * @param fileName
	 */
	public static String fileReader(String fileName) {
		StringBuffer sb = new StringBuffer("");
		char[] buf = new char[1024 * 512];
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(
					fileName));
			// BufferedReader br = new BufferedReader(new InputStreamReader(
			// new FileInputStream(fileName)));
			// 读取
			while (isr.read(buf) != -1) {
				sb = sb.append(new String(buf));
			}
			isr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString().trim();
	}

	public static byte[] fileOfPicReader(String filePath) throws IOException {
		return fileOfPicReader(new File(filePath));
	}

	public static byte[] fileOfPicReader(File file) throws IOException {
		if (!file.exists()) {
			return null;
		}
		byte[] data = null;
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		byte[] temp = new byte[512];// 字节数组缓冲区
		int off = 0;
		int len = dis.read(temp);// 输入流中的数据读取到temp中
		while (len >= 0) {
			off += len;
			if (off >= temp.length) {
				byte[] b = new byte[temp.length];
				System.arraycopy(temp, 0, b, 0, temp.length);
				temp = new byte[b.length + 10];
				System.arraycopy(b, 0, temp, 0, b.length);
			}
			len = dis.read(temp, off, temp.length - off);
		}
		if (off < temp.length) {
			data = new byte[off];
			System.arraycopy(temp, 0, data, 0, data.length);
		}
		dis.close();
		return data;
	}

	/**
	 * 读取对象
	 * 
	 * @param fileName
	 * @param size
	 * @return
	 */
	public static List<Object> fileReader(String fileName, int size) {
		try {
			List<Object> list = new ArrayList<Object>();
			ObjectInputStream ois = new ObjectInputStream(
					new BufferedInputStream(new FileInputStream(fileName), size));
			Object obj = null;
			while ((obj = ois.readObject()) != null) {
				list.add(obj);
			}
			ois.close();
			return list;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 写内容
	 * 
	 * @param fileName目标文件
	 * @param str写入的字符串
	 */
	public static void fileWriter(String fileName, String str) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName)));
			bw.write(str);// 写入
			bw.flush();// 刷新
			bw.close();// 关闭流
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写入对象
	 * 
	 * @param fileName目标文件
	 * @param obj
	 *            添加的对象
	 */

	public static void fileWriter(String fileName, Object obj) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(fileName)));
			oos.writeObject(obj);
			oos.flush();
			oos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
