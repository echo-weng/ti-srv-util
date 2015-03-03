package me.andpay.ti.util;

import java.io.File;

/**
 * 文件路径工具类。
 * 
 * @author sea.bao
 */
public class FileDirUtil {
	private static String formatPath(String path) {
		if (path.endsWith("/")) {
			return path.substring(0, path.length() - 1);
		} else {
			return path;
		}
	}

	private static String mkdirIfNecessary(String path) {
		File dir = new File(path);
		dir.mkdirs();
		return path;
	}

	public static String rollingDirByDate(String path) {
		StringBuffer sb = new StringBuffer();
		sb.append(formatPath(path));
		sb.append("/");
		sb.append(StringUtil.format("yyyyMMdd", new java.util.Date()));

		return mkdirIfNecessary(sb.toString());
	}
}
