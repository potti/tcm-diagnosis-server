package com.artwook.nft.nftshop.utils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

	public static String nextHexString(int len) {
		if (len == 0) {
			return null;
		}
		byte[] buf = new byte[len];
		ThreadLocalRandom random = ThreadLocalRandom.current();
		random.nextBytes(buf);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toLowerCase());
		}
		return sb.toString();
	}

	public static String toUUIDString() {
		return UUID.randomUUID().toString();
	}

	public static String toUUIDString(boolean repace) {
		if (repace)
			return toUUIDString().replace("-", "");
		return UUID.randomUUID().toString();
	}

	public static String frontCompWithZore(String inStr, int length) {
		if (inStr.length() < length)
			inStr = String.format("%0" + (length - inStr.length()) + "d%s", 0, inStr);
		return inStr;
	}

	public static String toDateString(String name) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		String patternString = formatter.format(ZonedDateTime.now());
		return String.format("%s-%s", name, patternString);
	}

	public static String toDateStringNow() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		String patternString = formatter.format(ZonedDateTime.now());
		return patternString;
	}

	public static String genOrderUuid() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		String patternString = formatter.format(ZonedDateTime.now());
		return String.format("%s%s%s", "O", patternString, nextHexString(10));
	}

	public static String genItemUuid() {
		return String.format("%s%s", "I", nextHexString(20));
	}

	public static String genPayOrderUuid() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		String patternString = formatter.format(ZonedDateTime.now());
		return String.format("%s%s%s", "P", patternString, nextHexString(7));
	}
}
