package me.andpay.ti.util;

/**
 * 磁道工具类。
 * 
 * @author sea.bao
 */
public class TrackUtil {
	/**
	 * 解析2磁道
	 * 
	 * @param track2
	 * @return
	 */
	public static Track2Info parseTrack2(String aTrack2) {
		aTrack2 = aTrack2.toUpperCase();
		String t = aTrack2.replaceAll("A", "=");
		if (t.endsWith("F")) {
			t = t.substring(0, t.length() - 1);
		}

		Track2Info t2Info = new Track2Info();

		try {
			int idx = t.indexOf("=");
			// PAN
			t2Info.setPan(t.substring(0, idx));
			idx++;
			if (t.substring(idx, idx + 1).equals("=") == false) {
				// ED
				t2Info.setExpiryDate(t.substring(idx, idx + 4));
				idx += 4;
			} else {
				idx++;
			}

			if (t.substring(idx, idx + 1).equals("=") == false) {
				// SC
				t2Info.setServiceCode(t.substring(idx, idx + 3));
				idx += 3;
			} else {
				idx++;
			}

			t2Info.setDiscretionaryData(t.substring(idx));
		} catch (StringIndexOutOfBoundsException e) {
			if (t2Info.getPan() == null) {
				t2Info.setPan(t);
			}
		}

		return t2Info;
	}

	public static String formatTrack2(Track2Info t2Info) {
		StringBuffer sb = new StringBuffer();
		sb.append(t2Info.getPan());
		sb.append("=");
		if (t2Info.getExpiryDate() != null) {
			sb.append(t2Info.getExpiryDate());
		} else {
			sb.append("=");
		}

		if (t2Info.getServiceCode() != null) {
			sb.append(t2Info.getServiceCode());
		} else {
			sb.append("=");
		}

		if (t2Info.getDiscretionaryData() != null) {
			sb.append(t2Info.getDiscretionaryData());
		}

		return sb.toString();
	}

	public static Track1Info parseTrack1(String track1) {
		Track1Info tInfo = new Track1Info();
		if (track1 == null) {
			return tInfo;
		}

		if (track1.startsWith("B") == false) {
			return tInfo;
		}

		try {

			int idx = 1;
			int i = track1.indexOf("^", idx);
			if (i < 0) {
				return tInfo;
			}

			tInfo.setPan(StringUtil.emptyAsNull(track1.substring(idx, i)));
			idx = i + 1;
			i = track1.indexOf("^", idx);
			if (i < 0) {
				return tInfo;
			}

			tInfo.setName(StringUtil.emptyAsNull(track1.substring(idx, i).trim()));

			idx = i + 1;
			if (track1.substring(idx, idx + 1).equals("^") == false) {
				// 有效期
				tInfo.setExpiryDate(track1.substring(idx, idx + 4));
				idx = idx + 4;
			} else {
				idx = idx + 1;
			}

			if (track1.substring(idx, idx + 1).equals("^") == false) {
				tInfo.setServiceCode(track1.substring(idx, idx + 3));
				idx = idx + 3;
			} else {
				idx = idx + 1;
			}

			tInfo.setDiscretionaryData(track1.substring(idx));
		} catch (Exception e) {
		}
		
		return tInfo;
	}
	
	/**
	 * 根据2磁道判断是否是IC卡
	 * 
	 * @param track2
	 * @return
	 */
	public static boolean isICCardByTrack2(String track2) {
		if (track2 != null) {
			int idx = track2.indexOf("=");
			if (idx != -1) {
				String part2 = track2.substring(idx);
				if (part2.length() >= 6 && (part2.charAt(5) == '2' || part2.charAt(5) == '6')) {
					return true;
				}
			}
		}

		return false;
	}
}
