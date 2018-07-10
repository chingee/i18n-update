package chingee.springboot.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chingee.springboot.bean.I18n;
import chingee.springboot.bean.JarPropertyFile;
import chingee.springboot.mapper.I18nMapper;
import chingee.springboot.translate.TranslateUtil;
import chingee.springboot.utils.Compressor;

@Service
public class JarService {
	
	private static Log log = LogFactory.getLog(JarService.class);
	
	@Autowired
	private JspService jspService;
	@Autowired
	private I18nMapper i18nMapper;
	
	public void analysisJars(String rootPath){
		//解压jar包
		unzip(rootPath, rootPath);
		File tarDirFile = new File(rootPath);
		File[] files = tarDirFile.listFiles((n)->n.isDirectory()&&n.getName().contains("carbon"));
		log.debug("一共扫描的文件数量: " + files.length);
		//遍历找出所需文件
		List<JarPropertyFile> propertiesFiles = new ArrayList<>();
		List<File> jspFiles = new ArrayList<>();
		for (File file : files) {
			List<File> propertiesList = new ArrayList<File>();
			List<File> jspList = new ArrayList<File>();
			log.debug("jar包名称: " + file.getName() + ".jar");
			findPropertiesFile(file, propertiesList);
			findJspFile(file, jspList);
			propertiesFiles.add(new JarPropertyFile(file.getName() + ".jar", file, propertiesList));
			jspFiles.addAll(jspList);
		}
		//替换jsp的编码
		jspService.changePageEncoding(jspFiles);
		this.catchJarsI18ns(propertiesFiles);
		//压缩为jar包
		File jarRootFile = new File(tarDirFile, "jars");
		if(!jarRootFile.exists()){
			jarRootFile.mkdirs();
		}
		for (File file : files) {
			this.zip(file, jarRootFile);
		}
	}
	
	
	public void packJars(String rootPath){
		List<String> paths = i18nMapper.selectPropertiesPaths();
		paths.forEach((p)->{
			List<I18n> i18ns = i18nMapper.selectI18nByFilepath(p);
			p.replace(".properties", "_zh_CN.properties");
			this.createChinesePropertiesFiles(i18ns, p);
		});
		File rootPathFile = new File(rootPath);
		File[] files = rootPathFile.listFiles((n)->n.getName().contains("carbon"));
		//压缩为jar包
		File jarRootFile = new File(rootPathFile, "jars");
		if(!jarRootFile.exists()){
			jarRootFile.mkdirs();
		}
		for (File file : files) {
			this.zip(file, jarRootFile);
		}
		File[] directoryFiles = rootPathFile.listFiles((n)->n.isDirectory()&&!n.getName().equals("jars"));
		for (File file : directoryFiles) {
			FileUtils.deleteQuietly(file);
		}
	}

	/**
	 * 筛选出所有i18n语言
	 * @param propertiesFiles
	 */
	public void catchJarsI18ns(List<JarPropertyFile> propertiesFiles){
		final List<I18n> i18ns = new ArrayList<>();
		propertiesFiles.forEach((j)->{
			j.getPropertylist().forEach((f)->{
				Properties prop = new Properties();
				FileInputStream fileInput = null;
				try {
					fileInput = new FileInputStream(f);
					prop.load(fileInput);
					for (Object key : prop.keySet()) {
						String keyone = (String)key;
						String en = (String)prop.getProperty(keyone);
						String zh = TranslateUtil.translate(en, TranslateUtil.CHINA);
						I18n i18n = new I18n(j.getJarname(), f.getAbsolutePath(), keyone, en, zh);
						log.debug("多语言信息: " + i18n);
						i18ns.add(i18n);
					}
					createChinesePropertiesFiles(i18ns, f.getAbsolutePath().replace(".properties", "_zh_CN.properties"));
				} catch (Exception e) {
					log.error("读取资源文件出错: " + f.getName(), e);
				} finally {
					IOUtils.closeQuietly(fileInput);
				}
			});
		});
		log.debug("共翻译多语言: " + i18ns.size());
		i18ns.forEach((i)->{
			i18nMapper.insertI18n(i);
		});
	}
	
	public void createChinesePropertiesFiles(List<I18n> i18ns, String path){
		final Properties prop = new Properties();
		i18ns.forEach((i)->{
			prop.put(i.getKeyone(), i.getZh());
		});
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(path);
			prop.store(fileOut, "chinese resources");
		} catch (Exception e) {
			log.error("写入中文资源文件出错: " + path, e);
		} finally {
			IOUtils.closeQuietly(fileOut);
		}
	}
	
	private void findPropertiesFile(File root, List<File> fileList){
		File[] children = root.listFiles((n)->n.isFile()&&n.getName().endsWith(".properties")&&!n.getPath().contains("META-INF"));
		fileList.addAll(Arrays.asList(children));
		for (File file : root.listFiles((n)->n.isDirectory())) {
			findPropertiesFile(file, fileList);
		}
	}
	
	private void findJspFile(File root, List<File> fileList){
		File[] children = root.listFiles((n)->n.isFile()&&n.getName().endsWith(".jsp"));
		fileList.addAll(Arrays.asList(children));
		for (File file : root.listFiles((n)->n.isDirectory())) {
			findJspFile(file, fileList);
		}
	}
	
	public void unzip(String rootPath, String tarDir){
		File root = new File(rootPath);
		for (File jarFile : root.listFiles((l)->l.isFile())) {
			File file =new File(tarDir+"/" + jarFile.getName().replace(".jar", ""));
			if(file.exists()){
				FileUtils.deleteQuietly(file);
			}
			try {
				unzip(jarFile, file);
			} catch (IOException e) {
				log.error("解压jar出错: " + jarFile.getName(), e);
			}
		}
	}
	
	/**
	 * 解压jar包
	 * @param jarFile
	 * @param tarDir
	 * @throws IOException
	 */
	public void unzip(File jarFile, File tarDir) throws IOException{
		log.info(jarFile.getName());
        try(JarFile jfInst = new JarFile(jarFile);){
        	Enumeration<JarEntry> enumEntry = jfInst.entries();  
        	while (enumEntry.hasMoreElements()) {  
        		JarEntry jarEntry = enumEntry.nextElement();  
        		File tarFile = new File(tarDir, jarEntry.getName());  
        		String path = tarFile.getPath().substring(0,tarFile.getPath().lastIndexOf("\\")+1);
        		log.info("目录结构 -->" + tarFile.getPath());
        		File file = new File(path);
        		if((!file .exists() && !file .isDirectory())){
        			file.mkdirs();
        		}
        		makeFile(jarEntry, tarFile);  
        		if (jarEntry.isDirectory()) {  
        			continue;  
        		}  
        		try(
    				FileOutputStream fileOut = new FileOutputStream(tarFile);
            		InputStream ins = jfInst.getInputStream(jarEntry);
        				){
        			transferStream(ins, fileOut.getChannel());  
        		}
        	} 
        }
	}
	
	public void zip(File file, File jarRootFile){
		File jarFile = new File(jarRootFile, file.getName() + ".jar");
		new Compressor(jarFile, null).compress(file.listFiles());
	}
	
	private static void transferStream(InputStream ins, FileChannel channel) {  
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 10);  
        ReadableByteChannel rbcInst = Channels.newChannel(ins);  
        try {  
            while (-1 != (rbcInst.read(byteBuffer))) {  
                byteBuffer.flip();  
                channel.write(byteBuffer);  
                byteBuffer.clear();  
            }  
        } catch (IOException ioe) {  
        	log.error(ioe);
        } finally {  
            if (null != rbcInst) {  
                try {  
                    rbcInst.close();  
                } catch (IOException e) {  
                	log.error(e);
                }  
            }  
            if (null != channel) {  
                try {  
                    channel.close();  
                } catch (IOException e) {  
                	log.error(e);
                }  
            }  
        }  
    }
	
	private static void makeFile(JarEntry jarEntry, File fileInst) { 
        if (!fileInst.exists()) {  
            if (jarEntry.isDirectory()) {  
                fileInst.mkdirs();  
            } else {  
                try {
                    fileInst.createNewFile();
                } catch (IOException e) { 
                	log.error("创建文件失败>>>".concat(fileInst.getPath()));  
                }  
            }  
        }
    }
}
