package com.zzl.zl_app.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;

public class FileTools {

	/**
	 * 复制 文件
	 * 
	 * @param 源文件source
	 * @param 目标文件dest
	 * @return boolean
	 */

	public static boolean copyFile(String source, String dest) {
		File sourceFile = new File(source);
		File destFile = new File(dest);
		return copyFile(sourceFile, destFile);
	}

	/**
	 * 复制文件
	 * 
	 * @param source
	 *            源文件
	 * @param dest
	 *            目标文件
	 * @return boolean
	 */
	public static boolean copyFile(File source, File dest) {
		boolean bool = false;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(source),
					1024 * 512);
			bos = new BufferedOutputStream(new FileOutputStream(dest),
					1024 * 512);
			// 读取
			int r = 0;
			while ((r = bis.read()) != -1) {
				// 写
				bos.write(r);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.flush();
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
				bool = true;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return bool;
	}

	/**
	 * 剪切 文件
	 * 
	 * @param 源文件source
	 * @param 目标文件dest
	 * @return boolean
	 */
	public static boolean ClipAndPasteFile(String source, String dest) {
		boolean bool = false;
		// 初始化缓冲流
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			// 缓冲流
			bis = new BufferedInputStream(new FileInputStream(source),
					1024 * 512);
			bos = new BufferedOutputStream(new FileOutputStream(dest),
					1024 * 512);
			// 读取
			int r = 0;
			while ((r = bis.read()) != -1) {
				// 写
				bos.write(r);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.flush();
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
				// 删除源文件
				File file = new File(source);
				file.delete();
				bool = true;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return bool;
	}

	/**
	 * 复制 文件夹
	 * 
	 * @param sourceDirName源目录名
	 * @param destDirName目标目录名
	 * @throws IOException
	 */
	public static void copyDir(String sourceDirName, String destDirName)
			throws IOException {
		File sourceDir = new File(sourceDirName);
		File destDir = new File(destDirName);
		copyDir(sourceDir, destDir);

	}

	/**
	 * 复制 文件夹
	 * 
	 * @param sourceDir源目录
	 * @param destDir目标目录
	 * @throws IOException
	 */

	public static void copyDir(File sourceDir, File destDir) throws IOException {
		FileTools.creatDir(destDir);
		System.out.println(destDir.getName());
		// 过滤出当前目录下的文件
		List<File> list = FileTools.fileFilter(sourceDir);
		for (Iterator<File> it = list.iterator(); it.hasNext();) {
			File file = it.next();
			System.out.println(file);
			FileTools.copyFile(file.getCanonicalPath(),
					destDir.getCanonicalPath() + "\\" + file.getName());
		}
		// 过滤出当前目录下的目录
		List<File> list2 = FileTools.dirFilter(sourceDir);
		for (Iterator<File> it = list2.iterator(); it.hasNext();) {
			File dir = it.next();
			copyDir(dir.getCanonicalPath(), destDir.getCanonicalPath() + "\\"
					+ dir.getName());
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 * @return boolean
	 */
	public static boolean deleteFile(File file) throws IOException {
		if (!file.exists()) {
			return false;
		}
		return file.delete();
	}

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 * @return boolean
	 */
	public static boolean deleteFile(String filePath) throws IOException {
		File file = new File(filePath);
		return deleteFile(file);
	}

	/**
	 * 删除文件夹下所有文件
	 * 
	 * @param dirPath
	 * @return
	 * @throws IOException
	 */
	public static boolean deleteFiles(String dirPath) throws IOException {
		return deleteFiles(new File(dirPath));
	}

	/**
	 * 删除当前文件夹下所有文件
	 * 
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	public static boolean deleteFiles(File dir) throws IOException {
		if (!dir.exists()) {
			throw new FileNotFoundException();
		}

		File[] files = getAllFilesInCurrentDir(dir);
		if (files == null || files.length == 0) {
			dir.delete();
			return true;
		}
		for (File file : files) {
			file.delete();
		}
		File[] files2 = getAllFilesInCurrentDir(dir);
		if (files2.length == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isEmptyOfDir(String dirPath)
			throws FileNotFoundException {
		return isEmptyOfDir(new File(dirPath));
	}

	public static boolean isEmptyOfDir(File dir) throws FileNotFoundException {
		if (!dir.exists()) {
			throw new FileNotFoundException();

		}
		return getAllFilesInCurrentDir(dir).length == 0;
	}

	/**
	 * 创建文件
	 * 
	 * @param 文件名
	 * @throws IOException
	 */
	public static boolean creatFile(String fileName) throws IOException {
		File file = new File(fileName);
		return creatFile(file);
	}

	/**
	 * 创建文件
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static boolean creatFile(File file) throws IOException {
		if (!file.exists()) {
			return file.createNewFile();
		}
		return false;
	}

	/**
	 * 创建目录
	 * 
	 * @param 目录名
	 */
	public static boolean creatDir(String dirName) {
		File dir = new File(dirName);
		return creatDir(dir);
	}

	public static boolean creatDir(File dir) {
		boolean bool = false;
		if (!dir.exists()) {
			bool = dir.mkdir();
		}
		return bool;
	}

	/**
	 * 文件过滤
	 * 
	 * @param 目录名
	 */
	public static List<File> fileFilter(String fileName) {
		File file = new File(fileName);
		return fileFilter(file);

	}

	public static List<File> fileFilter(File file) {
		File[] f = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {

				return pathname.isFile();
			}

		});

		return Arrays.asList(f);
	}

	/**
	 * 目录过滤
	 * 
	 * @param 目录名
	 * @return
	 */
	public static List<File> dirFilter(String dirName) {
		File dir = new File(dirName);
		return dirFilter(dir);

	}

	/**
	 * 目录过滤
	 * 
	 * @param dir
	 * @return List<File>
	 */
	public static List<File> dirFilter(File dir) {

		File[] f = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		return Arrays.asList(f);
	}

	/**
	 * 索引目录下的所有文件
	 * 
	 * @param dir
	 */
	public static void showListAllFile(File dir) {
		try {
			System.out.println(dir.getCanonicalPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 检索当前目录下的文件
		showFile(dir);
		// 过滤出当前目录下的所有目录
		File[] f = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {

				return pathname.isDirectory();
			}
		});
		// 对f进行排序
		Arrays.sort(f);
		// 获取目录
		for (File file : f) {
			showListAllFile(file);// 递归调用
		}

	}

	// 检索文件
	public static void showFile(File dir) {
		// 过滤出当前目录下的文件
		File[] f = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {

				return pathname.isFile();
			}
		});
		// 对f排序
		Arrays.sort(f);
		// 输出文件
		for (File file : f) {
			System.out.println(file.getName());
		}
	}

	/**
	 * 获取当前目录下所有的文件
	 * 
	 * @param dirPath
	 * @return File[]
	 */
	public static File[] getAllFilesInCurrentDir(String dirPath) {
		return getAllFilesInCurrentDir(new File(dirPath));
	}

	/**
	 * 获取当前目录下所有文件
	 * 
	 * @param dir
	 * @return
	 */
	public static File[] getAllFilesInCurrentDir(File dir) {
		if (!dir.exists()) {
			return null;
		}
		// 过滤出当前目录下的文件
		File[] fs = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {

				return pathname.isFile();
			}
		});
		// 对f排序
		Arrays.sort(fs);
		return fs;
	}

	public static String STORE_PATH = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/hall/";

	public static boolean isAPKExist(String apkName) {
		File file = new File(STORE_PATH + apkName);
		return file.exists();
	}

	public static String getStorePath() {
		return STORE_PATH;
	}

	public static void copyFilePiecesToCard(Context context, String fileName,
			String file_piece_name, int piece_start, int piece_end)
			throws IOException {
		InputStream myInput;
		creatDir(STORE_PATH);
		String outFileName = STORE_PATH + fileName;
		FileTools.creatFile(outFileName);
		OutputStream myOutput = new FileOutputStream(outFileName);
		for (int i = piece_start; i < piece_end + 1; i++) {
			myInput = context.getAssets().open(file_piece_name + i);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}
			myOutput.flush();
			myInput.close();
		}
		myOutput.close();
	}

	public static void unzipFile(String targetPath, String zipFilePath) {
		try {
			File zipFile = new File(zipFilePath);
			InputStream is = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry entry = null;
			System.out.println("开始解压:" + zipFile.getName() + "...");
			while ((entry = zis.getNextEntry()) != null) {
				String zipPath = entry.getName();
				try {
					if (entry.isDirectory()) {
						File zipFolder = new File(targetPath + File.separator
								+ zipPath);
						if (!zipFolder.exists()) {
							zipFolder.mkdirs();
						}
					} else {
						File file = new File(targetPath + File.separator
								+ zipPath);
						if (!file.exists()) {
							File pathDir = file.getParentFile();
							pathDir.mkdirs();
							file.createNewFile();
						}
						FileOutputStream fos = new FileOutputStream(file);
						int bread;
						while ((bread = zis.read()) != -1) {
							fos.write(bread);
						}
						fos.close();
					}
					System.out.println("成功解压:" + zipPath);
				} catch (Exception e) {
					System.out.println("解压" + zipPath + "失败");
					continue;
				}
			}
			zis.close();
			is.close();
			System.out.println("解压结束");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
