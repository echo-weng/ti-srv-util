package me.andpay.ti.util;

/**
 * 属性名称定义类。
 * 
 * @author sea.bao
 */
public class PropertyNames {
	public static String capitalize(String str) {
		if (str.length() == 0) {
			return str;
		}

		StringBuffer sb = new StringBuffer();
		sb.append(Character.toUpperCase(str.charAt(0)));
		sb.append(str.substring(1));

		return sb.toString();
	}

	public static String unixStyleToJavaStyle(String propName) {
		StringBuffer javaStyle = new StringBuffer();
		StringBuffer unixStyle = new StringBuffer(propName);
		int fromIndex = 0;
		int idx;

		do {
			idx = unixStyle.indexOf("-", fromIndex);
			int toIndex;
			if (idx < 0) {
				toIndex = unixStyle.length();
			} else {
				toIndex = idx;
			}

			String word = unixStyle.substring(fromIndex, toIndex);
			if (fromIndex > 0) {
				word = capitalize(word);
			}

			javaStyle.append(word);
			fromIndex = idx + 1;
		} while (idx >= 0);

		return javaStyle.toString();
	}
}
