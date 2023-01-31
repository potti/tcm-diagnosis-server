package com.artwook.nft.nftshop.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.artwook.nft.nftshop.common.BaseResponseCode;
import com.artwook.nft.nftshop.common.BizException;
import com.artwook.nft.nftshop.constants.FileType;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

/**
 * file工具
 */
@Slf4j
public class FileUtils {

	/**
	 * 将MultipartFile转为File
	 * 
	 * @param mulFile
	 * @return
	 */
	public static File multipartFileToFile(MultipartFile mulFile) throws IOException {
		InputStream ins = mulFile.getInputStream();
		String fileName = mulFile.getOriginalFilename();
		String prefix = getFileNameNoEx(fileName) + UUID.randomUUID();
		String suffix = "." + getExtensionName(fileName);
		File toFile = File.createTempFile(prefix, suffix);
		OutputStream os = new FileOutputStream(toFile);
		int bytesRead = 0;
		byte[] buffer = new byte[8192];
		while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
			os.write(buffer, 0, bytesRead);
		}
		os.close();
		ins.close();
		return toFile;
	}

	/**
	 * 将MultipartFile转为File
	 * 
	 * @param mulFile
	 * @return
	 */
	public static File multipartFileToFile(MultipartFile mulFile, boolean hashName) throws IOException {
		InputStream ins = mulFile.getInputStream();
		String fileName = mulFile.getOriginalFilename();
		String prefix = getFileNameNoEx(fileName);
		File toFile = null;
		String extName = getExtensionName(fileName);
		try {
			FileType fileTpye = FileType.valueOf(extName.toUpperCase());
			if (fileTpye == null) {
				throw new BizException(BaseResponseCode.ERROR_PARAMILLEGAL);
			}
		} catch (Exception e) {
			throw new BizException(BaseResponseCode.ERROR_PARAMILLEGAL);
		}

		String suffix = "." + extName;
		String path = System.getProperty("java.io.tmpdir") + extName;
		new File(path).mkdirs();
		toFile = new File(path + "/" + ShaUtils.sha256(prefix) + suffix);
		log.info("prefix:{} {} {}", fileName, hashName, toFile.getPath() + toFile.getName());
		if (!toFile.exists()) {
			// 先得到文件的上级目录，并创建上级目录，在创建文件
			toFile.getParentFile().mkdir();
			log.info("mkdir :{} ", toFile.getParentFile().getPath());
			try {
				// 创建文件
				toFile.createNewFile();
			} catch (IOException e) {
				log.error("createNewFile", e);
			}
		}
		OutputStream os = new FileOutputStream(toFile);
		int bytesRead = 0;
		byte[] buffer = new byte[8192];
		while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
			os.write(buffer, 0, bytesRead);
		}
		os.close();
		ins.close();
		return toFile;
	}

	/**
	 * 获取文件扩展名
	 *
	 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	/**
	 * 获取不带扩展名的文件名
	 *
	 */
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	public static boolean createFile(File file, String destFileName) throws Exception {
		// File file = new File(destFileName);
		if (file.exists()) {
			log.info("创建单个文件" + destFileName + "失败，目标文件已存在！");
			return true;
		}
		if (destFileName.endsWith(File.separator)) {
			throw new Exception("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
		}
		// 判断目标文件所在的目录是否存在
		if (!file.getParentFile().exists()) {
			// 如果目标文件所在的目录不存在，则创建父目录
			if (!file.getParentFile().mkdirs()) {
				throw new Exception("创建目标文件所在目录失败！");
			}
		}
		// 创建目标文件
		try {
			if (file.createNewFile()) {
				log.info("创建单个文件" + destFileName + "成功！");
			} else {
				throw new Exception("创建单个文件" + destFileName + "失败！");
			}
		} catch (IOException e) {
			throw new Exception("创建单个文件" + destFileName + "失败！" + e.getMessage());
		}
		return true;
	}

	/**
	 * 将存放在sourceFilePath目录下的源文件,打包成fileName名称的ZIP文件,并存放到zipFilePath。
	 * 
	 * @param sourceFilePath 待压缩的文件路径
	 * @param zipFilePath    压缩后存放路径
	 * @param fileName       压缩后文件的名称
	 * @return flag
	 */
	public static boolean fileToZip(String sourceFilePath, String zipFilePath, String fileName) {
		boolean flag = false;
		File sourceFile = new File(sourceFilePath);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		if (sourceFile.exists() == false) {
			System.out.println(">>>>>> 待压缩的文件目录：" + sourceFilePath + " 不存在. <<<<<<");
		} else {
			try {
				File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
				if (zipFile.exists()) {
					System.out.println(">>>>>> " + zipFilePath + " 目录下存在名字为：" + fileName + ".zip" + " 打包文件. <<<<<<");
				} else {
					File[] sourceFiles = sourceFile.listFiles();
					if (null == sourceFiles || sourceFiles.length < 1) {
						System.out.println(">>>>>> 待压缩的文件目录：" + sourceFilePath + " 里面不存在文件,无需压缩. <<<<<<");
					} else {
						fos = new FileOutputStream(zipFile);
						zos = new ZipOutputStream(new BufferedOutputStream(fos));
						byte[] bufs = new byte[1024 * 10];
						for (int i = 0; i < sourceFiles.length; i++) {
							// 创建ZIP实体,并添加进压缩包
							ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
							zos.putNextEntry(zipEntry);
							// 读取待压缩的文件并写进压缩包里
							fis = new FileInputStream(sourceFiles[i]);
							bis = new BufferedInputStream(fis, 1024 * 10);
							int read = 0;
							while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
								zos.write(bufs, 0, read);
							}
						}
						flag = true;
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				// 关闭流
				try {
					if (null != bis)
						bis.close();
					if (null != zos)
						zos.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
		return flag;
	}

}