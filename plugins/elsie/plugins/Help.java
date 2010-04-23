package elsie.plugins;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import botFramework.interfaces.IBot;
import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IDatabase;
import botFramework.interfaces.IUserFunctions;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Help extends AbstractPlugin {
	PreparedStatement queryListHelp;
	PreparedStatement queryHelp;
	IDatabase mysql;
	IBot bot;
	IUserFunctions usr;
	
	public Help() {

	}
	
	public void prepare()
	{
		try {		
			queryListHelp = getDatabase().getConnection().prepareStatement("SELECT DISTINCT `Command` FROM `help` WHERE `Command` != '' ORDER BY `Command`");
			queryHelp = getDatabase().getConnection().prepareStatement("SELECT `Message` FROM `help` WHERE `Command`=? LIMIT 1");
		}
		catch (SQLException e) {
			bot.sendErrorEvent("Help.Help","SQLException",e.getMessage());
		}
	}
	
	public void destroy()
	{
		
	}
	
	public boolean chanBotRespond(IChanBotEvent event) {
		String[] botCommand = event.getBotCommand();
		ResultSet results;
		String[] message = new String[1];
		
		try {
			if (botCommand[0].equalsIgnoreCase("help") | botCommand[0].equalsIgnoreCase("man")) {
				if (botCommand.length == 1) {
					queryHelp.setString(1,"");
					results = queryHelp.executeQuery();
					
					if (results.first()) {
						message[0] = results.getString("Message");
						usr.botMessage(event.getCommandSource(),"default",message,event.getIsPrivate(),event.getChannelSource(),false);
					}
					
					results = queryListHelp.executeQuery();
					
					if (results.first()) {
						message[0] = results.getString("Command");
					}
					
					while (results.next()) {
						message[0] = message[0] + " " + results.getString("Command");
					}
				
					usr.botMessage(event.getCommandSource(),"default",message,event.getIsPrivate(),event.getChannelSource(),false);
				
					return true;
				}
				else {
					queryHelp.setString(1,botCommand[1]);
					results = queryHelp.executeQuery();
				
					if (results.first()) {
						message[0] = results.getString("Message");
						usr.botMessage(event.getCommandSource(),"default",message,event.getIsPrivate(),event.getChannelSource(),false);
					}
					else {
						message[0] = botCommand[1];
						usr.botMessage(event.getCommandSource(),"nohelp",message,event.getIsPrivate(),event.getChannelSource(),false);
					}
					return true;
				}
			}
			return false;
		}
		catch (SQLException e) {
			bot.sendErrorEvent("Help.chanBotRespond","SQLException",e.getMessage());
			return false;
		}
	}
}
