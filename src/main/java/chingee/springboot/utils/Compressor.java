package chingee.springboot.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Compressor {
	
	private static Log log = LogFactory.getLog(Compressor.class);
	
	private static final int BUFFER = 8192;

	private File jarFile;
	
	private String basedir;

	public Compressor(File jarFile, String basedir) {
		this.jarFile = jarFile;
		this.basedir = basedir==null?"":basedir;
	}
	
	public void compress(File ...pathName) {
		ZipOutputStream out = null;
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(jarFile);
			CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,
					new CRC32());
			out = new ZipOutputStream(cos);
			for (int i = 0; i < pathName.length; i++) {
				compress(pathName[i], out, basedir);
			}
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void compress(String srcPathName) {
		File file = new File(srcPathName);
		if (!file.exists())
			throw new RuntimeException(srcPathName + "不存在！");
		try (
				FileOutputStream fileOutputStream = new FileOutputStream(jarFile);
				CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,new CRC32());
				ZipOutputStream out = new ZipOutputStream(cos);
				){
			compress(file, out, basedir);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void compress(File file, ZipOutputStream out, String basedir) {
		//判断是文件还是目录
		if (file.isDirectory()) {
			//压缩目录
			this.compressDirectory(file, out, basedir);
		} else {
			//压缩文件
			this.compressFile(file, out, basedir);
		}
	}

	private void compressDirectory(File dir, ZipOutputStream out, String basedir) {
		if (!dir.exists())
			return;

		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			/* 递归 */
			compress(files[i], out, basedir + dir.getName() + "/");
		}
	}

	private void compressFile(File file, ZipOutputStream out, String basedir) {
		if (!file.exists()) {
			return;
		}
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));){
			
			String filePath = (basedir + file.getName());
			log.info("压缩文件：" + filePath);
			ZipEntry entry = new ZipEntry(filePath);
			out.putNextEntry(entry);
			int count;
			byte data[] = new byte[BUFFER];
			while ((count = bis.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
			bis.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
