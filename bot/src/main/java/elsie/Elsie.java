package elsie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import elsie.util.AbstractClassLoader;

import botFramework.Bot;

class Elsie {
	private static final Log log = LogFactory.getLog(Elsie.class);

	public static void main(String[] args) {

		ApplicationContext rootContext = new ClassPathXmlApplicationContext(
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

		//bot.start();
		/*
		Bot elsieBot = (Bot) serverContext.getBean("bot");

		DBHandler mysql = (DBHandler) serverContext.getBean("database");

		IUserFunctions usr = (IUserFunctions) serverContext.getBean("userFunctions");

		Plugins plugins = (Plugins) serverContext.getBean("plugins");
		
		FileSystemClassLoader pluginClassLoader = (FileSystemClassLoader) serverContext.getBean("pluginClassLoader");

		plugins.addPluginCommand("!reload", "elsie.plugins.ReloadPlugins");
		plugins.addPluginCommand("!memusage", "elsie.plugins.MemUsage");
		plugins.addPluginCommand("!addplugin", "elsie.plugins.AddPlugin");
		plugins.addPluginCommand(plugins.getFallbackCommand(), "elsie.plugins.MissingCommand");
		
		IChannels chans = (IChannels) serverContext.getBean("channels");
		ErrorConsole err = (ErrorConsole) serverContext.getBean("errorConsole");
		Console console = (Console) serverContext.getBean("console");

		ChanBotEventDispatcher disp = (ChanBotEventDispatcher) serverContext.getBean("chanBotEventDispatcher");	
		InvalidCommandHandler unknownCmd = (InvalidCommandHandler) serverContext.getBean("invalidCommandHandler");
		ChannelManager man1 = (ChannelManager) serverContext.getBean("ChannelManager");
	
		Channel chan = (Channel) serverContext.getBean("mainChannel");
		//chan.addChanBotListener(help);

		InputConsole input = (InputConsole) serverContext.getBean("inputConsole");
		
		if(args.length >= 4)
		{
			Channel chan2 = (Channel) serverContext.getBean("channel");
			chan2.setChannel(args[3]);
			chan2.getChanEvents().add(console.getChanListener());
			//chan2.addChanBotListener(help);
		}
		
		*/
		
		//Perform opping, deopping etc for secondary channel
		/*ChannelManager man2 = new ChannelManager(elsieBot, mysql, usr);
		chan2.addChanListener(man2);
		chan2.addChanBotListener(man2);*/
		
		//Transcriber listener - maintains sql transcript of channel
		/*Transcriber transcriber = new Transcriber(elsieBot, mysql);
		chan.addChanListener(transcriber);
		chan2.addChanListener(transcriber);
		
		//Lastlinks - prints the last links stored in the transcript
		Lastlinks lastlinks = new Lastlinks(elsieBot, mysql, usr);
		chan.addChanBotListener(lastlinks);
		chan2.addChanBotListener(lastlinks);
		*/
		
		//Everyone loves to talk ;)
		/*Alice chatBot = new Alice(elsieBot,usr,"www.pandorabots.com","/pandora/talk?botid=f784b1d6de344166");
		chan.addChanListener(chatBot);
		//chan2.addChanListener(chatBot);
		chan.addChanBotListener(chatBot);
		//chan2.addChanBotListener(chatBot);
		chan.addChanBotUnknownCmdListener(chatBot);
		//chan2.addChanBotUnknownCmdListener(chatBot);*/
		
		//elsieBot.start();		//let the mayhem begin
	}
}