package botFramework;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PluginFactory implements ApplicationContextAware {
	private static final Log log = LogFactory.getLog(PluginFactory.class);

	private ApplicationContext context;
	private AbstractApplicationContext pluginContext;
	private ClassLoader loader = null;
	private String loaderId;
	private Map<Class, Object> chanBotPlugins = new HashMap<Class, Object>();

	public void clear()
	{
		this.loader = null;
		this.pluginContext.close();
		this.pluginContext = null;
	}

	public ApplicationContext getPluginContext()
	{
		if(pluginContext == null)
		{
			pluginContext = new ClassPathXmlApplicationContext(
					new String[] {"elsie/plugins/plugins.xml"},
					context);
		}
		return pluginContext;
	}
	
	public ApplicationContext getApplicationContext()
	{
		return context;
	}
	
	public void setApplicationContext(ApplicationContext context)
	{
		this.context = context;
	}
	
	public ClassLoader getPluginClassLoader()
	{
		if (loader == null)
		{
			loader = (ClassLoader) context.getBean(loaderId);
			log.info("Got fresh plugin class loader " + loader);
		}
		return loader;
	}

	public String getClassLoaderId()
	{
		return loaderId;
	}
	
	public void setClassLoaderId(String loaderId)
	{
		this.loaderId = loaderId;
	}
	
	public Object createInstance(Class pluginClass) throws Exception
	{
		log.info("Getting plugin instance of " + pluginClass);
		Object o = chanBotPlugins.get(pluginClass);
		
		if(o == null)
		{
			try {
				o = pluginClass.newInstance();

				chanBotPlugins.put(pluginClass, o);
			} catch (Exception e) {
				log.error("Error loading plugin " + pluginClass, e);
				throw e;
			}
		}
		
		return o;
	}
}
