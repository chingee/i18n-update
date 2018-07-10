package chingee.springboot.bean;

import java.io.File;
import java.util.List;

/**
 * @author Administrator
 *
 */
public class JarPropertyFile {

	private String jarname;
	private File jarpath;
	private List<File> propertylist;
	
	public List<File> getPropertylist() {
		return propertylist;
	}
	public void setPropertylist(List<File> propertylist) {
		this.propertylist = propertylist;
	}
	public String getJarname() {
		return jarname;
	}
	public void setJarname(String jarname) {
		this.jarname = jarname;
	}
	
	public JarPropertyFile(String jarname, File jarpath, List<File> propertylist) {
		this.jarname = jarname;
		this.jarpath = jarpath;
		this.propertylist = propertylist;
	}
	public File getJarpath() {
		return jarpath;
	}
	public void setJarpath(File jarpath) {
		this.jarpath = jarpath;
	}
	@Override
	public String toString() {
		return "JarFile [jarname=" + jarname + ", propertylist=" + propertylist + "]";
	}
	
}
