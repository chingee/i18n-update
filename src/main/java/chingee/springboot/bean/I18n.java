package chingee.springboot.bean;

public class I18n {

	private int id; 
	private String jar; 
	private String filepath; 
	private String keyone; 
	private String en; 
	private String zh;
	
	public I18n(){}
	
	public I18n(String jar, String filepath, String keyone, String en, String zh) {
		this.jar = jar;
		this.filepath = filepath;
		this.keyone = keyone;
		this.en = en;
		this.zh = zh;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getJar() {
		return jar;
	}
	public void setJar(String jar) {
		this.jar = jar;
	}
	public String getFilepath() {
		return filepath;
	}
	public String getKeyone() {
		return keyone;
	}
	public void setKeyone(String keyone) {
		this.keyone = keyone;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public String getEn() {
		return en;
	}
	public void setEn(String en) {
		this.en = en;
	}
	public String getZh() {
		return zh;
	}
	public void setZh(String zh) {
		this.zh = zh;
	}
	@Override
	public String toString() {
		return "I18n [keyone=" + keyone + ", en=" + en + ", zh="
				+ zh + ", jar=" + jar + ", filepath=" + filepath + "]";
	} 
	
}
