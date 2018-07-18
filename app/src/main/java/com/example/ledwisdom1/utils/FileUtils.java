package com.example.ledwisdom1.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {

	public static final String FILE_SEPARATOR = File.separator;
	private static Object filePath;

	public static boolean isSDCardEnable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public static String getSDCardPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static long getSDCardAllSize() {
		if (isSDCardEnable()) {
			StatFs stat = new StatFs(getSDCardPath());
			// 获取空闲的数据块的数量
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
				long availableBlocks = stat.getAvailableBlocksLong() - 4;
				// 获取单个数据块的大小（byte）
				long freeBlocks = stat.getAvailableBlocksLong();
				return freeBlocks * availableBlocks;

			} else {
				long availableBlocks = stat.getAvailableBlocks() - 4;
				// 获取单个数据块的大小（byte）
				long freeBlocks = stat.getAvailableBlocks();
				return freeBlocks * availableBlocks;
			}

		}
		return 0;
	}

	public static String getRootDirectoryPath() {
		return Environment.getRootDirectory().getAbsolutePath();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static long getAvailableInternalMemorySize() {

		File path = Environment.getDataDirectory();

		StatFs stat = new StatFs(path.getPath());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			long blockSize = stat.getBlockSizeLong();

			long availableBlocks = stat.getAvailableBlocksLong();

			return availableBlocks * blockSize;
		} else {
			long blockSize = stat.getBlockSize();

			long availableBlocks = stat.getAvailableBlocks();

			return availableBlocks * blockSize;

		}

	}

	public static boolean isFileExist(String path) {
		File file = new File(path);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	public static boolean isFileExist(File file) {
		if (null != file && file.exists()) {
			return true;
		}
		return false;
	}

	public static boolean makeFile(String path) {
		File file = new File(path);

		if (path.endsWith(FILE_SEPARATOR)) {
			return false;
		}
		// 判断目标文件所在的目录是否存在
		if (!file.getParentFile().exists()) {
			// 如果目标文件所在的目录不存在，则创建父目录
			if (!file.getParentFile().mkdirs()) {
				return false;
			}
		}
		// 创建目标文件
		try {
			if (file.exists()) {
				file.delete();
			}
			if (file.createNewFile()) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean makeDir(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		file.mkdirs();
		return isFileExist(path);
	}

	public static boolean deleteFile(String path, FileDeleteCallback fileDeleteCallback, boolean... isDeleteDir) {

		File file = new File(path);
		if (!file.exists()) {

			if (null != fileDeleteCallback) {
				fileDeleteCallback.result(2);
			}
		}
		if (!file.isDirectory()) {
			file.delete();
		} else if (file.isDirectory()) {
			String[] fileList = file.list();
			for (int i = 0; i < fileList.length; i++) {
				File delfile = new File(path + FILE_SEPARATOR + fileList[i]);
				if (!delfile.isDirectory()) {
					delfile.delete();
				} else if (delfile.isDirectory()) {
					deleteFile(path + FILE_SEPARATOR + fileList[i], fileDeleteCallback);
				}
			}
			if (isDeleteDir.length > 0 && isDeleteDir[0]) {
				file.delete();
			}

			if (file.getAbsolutePath() != null && file.getAbsolutePath().equals(path)) {

				if (null != fileDeleteCallback) {
					fileDeleteCallback.result(1);
				}
			}
		}

		return true;

	}

	public static long getFileSize(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return 0;
		}
		return getFileSize(file);
	}

	public static long getFileSize(File file) {
		if (file.isFile())
			return file.length();
		File[] children = file.listFiles();
		long total = 0;
		if (children != null)
			for (File child : children)
				total += getFileSize(child);
		return total;
	}

	public static void renameFile(String srcPath, String dstpath) {
		File srcFile = new File(srcPath);
		File dstFile = new File(dstpath);
		if (srcFile.exists()) {
			srcFile.renameTo(dstFile);
		}
	}



	public interface FileDeleteCallback {
		/**
		 * @param state(1,成功;2,没有可清除)
		 */
		public void result(int state);
	}

	public static File createImageFile(Context context) throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		String imageFileName = "IMG_" + timeStamp;
		File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		return File.createTempFile(imageFileName, ".jpg", dir);
	}


}
