package chingee.springboot;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import chingee.springboot.service.JarService;

@SpringBootApplication
public class App {

	private static Log log = LogFactory.getLog(App.class);
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		ConfigurableApplicationContext ac = SpringApplication.run(App.class, args);
		try {
			if(args == null || args.length < 2){
				throw new IllegalArgumentException("需要2个参数");
			}
			int len = args.length;
			if(len != 2){
				throw new IllegalArgumentException("有且只有需要2个参数");
			}
			if(len == 2){
				if(!args[0].equals("a") && !args[0].equals("p")){
					throw new IllegalArgumentException("第一个参数必须为a或p, 第二个参数为jar包的所在的目录");
				}
				if(args[0].equals("a")){
					ac.getBean(JarService.class).analysisJars(args[1]);
				}else if(args[0].equals("p")){
					ac.getBean(JarService.class).packJars(args[1]);
				}
				
			}
		} catch (Exception e) {
			log.error("参数错误: ", e);
		} finally {
			ac.close();
			long times = System.currentTimeMillis() - startTime;
			SimpleDateFormat formatter = new SimpleDateFormat("HH时mm分ss秒");
			formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
			log.info("运行完毕,总共耗时: " + formatter.format(times));
		}
	}
}
