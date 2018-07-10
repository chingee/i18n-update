package chingee.springboot.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class JspService {
	
	private static Log log = LogFactory.getLog(JarService.class);
	
	protected static final String DEFAULT_ENCODING = "UTF-8";
	
	protected static final String CHARSET = "charset=ISO-8859-1"; 
	protected static final String CHARSETO = "charset={0}"; 
	
	protected static final String PAGEENCODING = "pageEncoding=\"ISO-8859-1\""; 
	protected static final String PAGEENCODINGTO = "pageEncoding=\"{0}\""; 

	public void changePageEncoding(List<File> jspFiles){
		log.debug("共有带转换jsp文件: " + jspFiles.size());
		jspFiles.forEach((j)->{
			this.changePageEncoding(j, DEFAULT_ENCODING);
		});
	}
	
	public void changePageEncoding(File jspFile){
		this.changePageEncoding(jspFile, DEFAULT_ENCODING);
	}
	
	public void changePageEncoding(File jspFile, String encoding){
		log.debug("修改文件: " + jspFile);
		if(StringUtils.isEmpty(encoding)){
			encoding = DEFAULT_ENCODING;
		}
		final String encoding$ = encoding;
		FileInputStream fileInput = null;
		FileOutputStream fileOutput = null;
		try {
			fileInput = new FileInputStream(jspFile);
			List<String> lines = IOUtils.readLines(fileInput, DEFAULT_ENCODING);
			fileOutput = new FileOutputStream(jspFile);
			StringBuilder sb = new StringBuilder();
			for (String l : lines) {
				l = l.replace(CHARSET, MessageFormat.format(CHARSETO, encoding$))
						.replace(PAGEENCODING, MessageFormat.format(PAGEENCODINGTO, encoding$));
				sb.append(l);
				sb.append("\n");
			}
			IOUtils.write(sb.toString().getBytes(DEFAULT_ENCODING), fileOutput);
		} catch (Exception e) {
			log.error("转换jsp出错: " + jspFile, e);
		} finally {
			IOUtils.closeQuietly(fileInput);
			IOUtils.closeQuietly(fileOutput);
		}
	}
	
}
