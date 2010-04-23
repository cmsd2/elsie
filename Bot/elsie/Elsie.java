package elsie;

import java.util.Vector;

import elsie.plugins.Help;
import elsie.plugins.Lastlinks;
import elsie.plugins.MemUsage;
import elsie.plugins.Transcriber;

import botFramework.*;
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
		
		Bot elsieBot = new Bot(nicks, servers, 6667, 0, "me","iso-8859-1");
		
		//Maintains a database connection
		String pwd = args[1];
		DBHandler mysql = new DBHandler(elsieBot,"com.mysql.jdbc.Driver","jdbc:mysql://localhost:3306/irc?user=elsiebot&password=" + pwd);
		
		IUserFunctions usr = new UserFunctions(elsieBot,mysql);

		Plugins plugins = new Plugins(".");
		
		plugins.setBot(elsieBot);
		plugins.setDatabase(mysql);
		plugins.setUserFunctions(usr);
		
		plugins.getPrefixExceptions().add("java");
		plugins.getPrefixExceptions().add("javax");
		plugins.getPrefixExceptions().add("botFramework.interfaces");
		
		plugins.addPluginCommand("!reload", "elsie.plugins.ReloadPlugins");
		plugins.addPluginCommand("!memusage", "elsie.plugins.MemUsage");
		plugins.addPluginCommand("!addplugin", "elsie.plugins.AddPlugin");
		plugins.addPluginCommand(plugins.getFallbackCommand(), "elsie.plugins.MissingCommand");
		
		//Error listener - when it all goes wrong
		ErrorConsole err = new ErrorConsole();
		elsieBot.getErrors().add(new ErrorEventListenerAdapter(err));
		
		Channel chan = new Channel(args[2]);
		elsieBot.getIrcEvents().add(chan.getIrcEventListener());
		chan.setBot(elsieBot);
		
		//Console listener - something to look at
		Console console = new Console(elsieBot);
		elsieBot.getIrcEvents().add(console.getIrcListener());
		chan.addChanListener(console.getChanListener());
		
		
		//InputConsole - so elsie can talk
		InputConsole input = new InputConsole(elsieBot,chan);
		
		/*Help help = new Help(elsieBot,mysql,usr);
		chan.addChanBotListener(help);
		chan2.addChanBotListener(help);*/
		
		ChanBotEventDispatcher disp = new ChanBotEventDispatcher(plugins);
		chan.addChanBotListener(disp);
		
		//Perform opping, deopping etc for primary channel
		ChannelManager man1 = new ChannelManager(elsieBot, mysql, usr);
		chan.addChanListener(man1.getChanListener());
		chan.addChanBotListener(man1.getChanBotListener());
		
		//Standard invalid command response;
		InvalidCommandHandler unknownCmd = new InvalidCommandHandler(usr);
		chan.addChanBotUnknownCmdListener(unknownCmd);
		
		if(args.length >= 4)
		{
			Channel chan2 = new Channel(args[3]);
			elsieBot.getIrcEvents().add(chan2.getIrcEventListener());
			chan2.setBot(elsieBot);
			chan2.addChanListener(console.getChanListener());
			chan2.addChanBotListener(disp);
			chan2.addChanBotUnknownCmdListener(unknownCmd);
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
		
		elsieBot.start();		//let the mayhem begin
	}
}