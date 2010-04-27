package elsie;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import botFramework.Bot;
import elsie.util.FileSystemClassLoader;

class Elsie implements ApplicationContextAware {
	private static final Log log = LogFactory.getLog(Elsie.class);

	public static void main(String[] args) {
		ClassLoader configClassLoader = new FileSystemClassLoader(System.getProperty("elsie.config.dir"));

		URL url = configClassLoader.getResource("log4j.xml");
		DOMConfigurator.configure(url);

		AbstractApplicationContext rootContext = new ClassPathXmlApplicationContext(
				new String[] {"bootstrap.xml", "botFramework.xml"});
		
		Elsie elsie = new Elsie();
		elsie.setApplicationContext(rootContext);

		elsie.run(args);
	}

	private ApplicationContext rootContext;
	private ApplicationContext serverContext;
	
	public Elsie() {
		
	}
	
	public void setApplicationContext(ApplicationContext context)
	{
		this.rootContext = context;
		this.serverContext = new ClassPathXmlApplicationContext(new String[] {
				"elsie.xml"
		}, rootContext);
	}
	
	public void run(String[] args) {
		log.info("Running Elsie");
		Bot bot = (Bot) serverContext.getBean("bot");

		bot.start();
	}
}
