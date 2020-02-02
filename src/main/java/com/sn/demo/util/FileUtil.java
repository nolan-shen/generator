package com.sn.demo.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.charset.Charset;

/**
 * 文件操作工具类
 */
@Slf4j
public final class FileUtil {

	/**
	 * 判断文件是否存在
	 */
	@SneakyThrows
	public static Boolean checkFile(String filePath, Boolean boolCreate) {
		File file = new File(filePath);
		if (file.exists()) {
			// 已存在
			if (file.isDirectory()) {
				return false;
			}
			return true;
		} else {
			// 不存在
			if (boolCreate) {
				file.createNewFile();
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断文件夹是否存在
	 */
	@SneakyThrows
	public static Boolean checkDirectory(String directoryPath, Boolean boolCreate) {
		File file = new File(directoryPath);
		if (file.exists()) {
			// 已存在
			if (!file.isDirectory()) {
				return false;
			}
			return true;
		} else {
			// 不存在
			if (boolCreate) {
				file.createNewFile();
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取真实文件名（自动去掉文件路径）
	 */
	public static String getRealFileName(String fileName) {
		return FilenameUtils.getName(fileName);
	}

	/**
	 * 获取真实文件名（自动去掉文件路径）
	 */
	@SneakyThrows
	public static String readFileToString(String filePath) {
		return FileUtils.readFileToString(new File(filePath), Charset.forName("UTF-8"));
	}

	/**
	 * 创建文件
	 */
	public static File createFile(String filePath) {
		File file;
		try {
			file = new File(filePath);
			File parentDir = file.getParentFile();
			if (!parentDir.exists()) {
				FileUtils.forceMkdir(parentDir);
			}
		} catch (Exception e) {
			log.error("create file failure", e);
			throw new RuntimeException(e);
		}
		return file;
	}
}
