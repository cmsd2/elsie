package elsie;

import java.util.Vector;

import elsie.util.Lifecycle;

import botFramework.Bot;
import botFramework.ChanBotEventDispatcher;
import botFramework.Channel;
import botFramework.Channels;
import botFramework.DBHandler;
import botFramework.ErrorEventListenerAdapter;
import botFramework.interfaces.IBot;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IChannels;
import botFramework.interfaces.IDatabase;
import botFramework.interfaces.IPlugins;
import botFramework.interfaces.IUserFunctions;

class Elsie {
	public static void main(String[] args) {
		Elsie e = new Elsie();
		e.run(args);
	}
	
	public Elsie() {
		
	}
	
	public void run(String[] args) {
	
		Vector nicks = new Vector();
		nicks.addElement("elsiebot");
		nicks.addElement("jelsie");
		nicks.addElement("jelsieDev");
		nicks.addElement("kelsie");
		nicks.addElement("kelsieDev");
		
		Vector servers = new Vector();
		servers.addElement(args[0]);
		servers.addElement("efnet.demon.co.uk");
		servers.addElement("irc.isdnet.fr");
		
		Context context = new Context();
		
		Bot elsieBot = new Bot(nicks, servers, 6667, 0, "me","iso-8859-1");
		context.nameObject("bot", IBot.class, elsieBot, Lifecycle.Singleton);
		
		//Maintains a database connection
		String pwd = args[1];
		DBHandler mysql = new DBHandler(elsieBot,"com.mysql.jdbc.Driver","jdbc:mysql://localhost:3306/irc?user=elsiebot&password=" + pwd);
		context.nameObject("database", IDatabase.class, mysql, Lifecycle.Singleton);
		
		IUserFunctions usr = context.createSingleton(UserFunctions.class, new Class[] {IUserFunctions.class});

		Plugins plugins = new Plugins(".");
		context.initialiseObject(plugins);
		context.nameObject("plugins", IPlugins.class, plugins, Lifecycle.Singleton);

		plugins.getPrefixExceptions().add("java");
		plugins.getPrefixExceptions().add("javax");
		plugins.getPrefixExceptions().add("elsie.util");
		plugins.getPrefixExceptions().add("botFramework.interfaces");
		
		plugins.addPluginCommand("!reload", "elsie.plugins.ReloadPlugins");
		plugins.addPluginCommand("!memusage", "elsie.plugins.MemUsage");
		plugins.addPluginCommand("!addplugin", "elsie.plugins.AddPlugin");
		plugins.addPluginCommand(plugins.getFallbackCommand(), "elsie.plugins.MissingCommand");
		
		IChannels chans = context.createSingleton(Channels.class, new Class[] { IChannels.class });
		ErrorConsole err = context.getSingleton(ErrorConsole.class);
		Console console = context.getSingleton(Console.class);

		ChanBotEventDispatcher disp = context.getSingleton(ChanBotEventDispatcher.class);
		InvalidCommandHandler unknownCmd = context.getSingleton(InvalidCommandHandler.class);
		ChannelManager man1 = context.getSingleton("channelManager", ChannelManager.class);
		
		Channel chan = context.getObject(Channel.class);
		chan.setChannel(args[2]);
		context.nameObject("mainChannel", IChannel.class, chan);
		//chan.addChanBotListener(help);

		InputConsole input = context.getSingleton(InputConsole.class);
		
		if(args.length >= 4)
		{
			Channel chan2 = context.getObject(Channel.class);
			chan2.setChannel(args[3]);
			chan2.getChanEvents().add(console.getChanListener());
			//chan2.addChanBotListener(help);
		}
		
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