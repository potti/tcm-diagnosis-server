package com.artwook.nft.nftshop.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

/**
 * SHA算法
 * 
 * @author liangrong
 */
public class ShaUtils {

	public static String sha1(String data) {
		return DigestUtils.sha1Hex(data);
	}

	public static String sha1(byte[] data) {
		return DigestUtils.sha1Hex(data);
	}

	public static String sha1(InputStream data) {
		try {
			return DigestUtils.sha1Hex(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String sha1File(String file) {
		if (StringUtils.isNotBlank(file)) {
			try {
				return DigestUtils.sha1Hex(new FileInputStream(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String sha256(String data) {
		return DigestUtils.sha256Hex(data);
	}

	public static String sha256(byte[] data) {
		return DigestUtils.sha256Hex(data);
	}

	public static String sha256File(File file) {
		try {
			return sha256(new FileInputStream(file));
		} catch (FileNotFoundException e) {

		}
		return null;
	}

	public static String sha256(InputStream data) {
		try {
			return DigestUtils.sha256Hex(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String sha256File(String file) {
		if (StringUtils.isNotBlank(file)) {
			try {
				return DigestUtils.sha256Hex(new FileInputStream(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
