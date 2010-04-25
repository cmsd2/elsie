package elsie.plugins;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IDatabase;
import botFramework.interfaces.IUserFunctions;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Lastlinks extends AbstractPlugin {
	private static final Log log = LogFactory.getLog(Lastlinks.class);

	Pattern regexLink;
	PreparedStatement queryLinks;
	
	public Lastlinks() {

	}
	
	public void prepare()
	{
		regexLink = Pattern.compile(".*((http\\://|(^| )www\\.|ftp\\://|https\\://)[^ ]*).*");
		
		try {
			queryLinks = queryLinks = getDatabase().getConnection().prepareStatement("SELECT DISTINCT `Nick`,`Description` FROM `transcript` WHERE `Channel`=? AND `Nick`!=? AND `Nick` !=? AND (`Event`=\"PRIVMSG\" OR `Event`=\"TOPIC\") AND `Description` LIKE ? AND (`Description` LIKE \"%http://%\" OR `Description` LIKE \"%ftp://%\" OR `Description` LIKE \"%https://%\" OR `Description` LIKE \"%www.%\") ORDER BY `DateTime` DESC LIMIT 5");
		}
		catch (SQLException e) {
			getBot().sendErrorEvent("Lastlinks.Lastlinks","SQLException",e.getMessage());
		}
	}
	
	public boolean chanBotRespond(IChanBotEvent event) {
		log.info("Handling event " + event);

		String source = event.getCommandSource();
		String[] botCmd = event.getBotCommand();
		boolean isPrivate = event.getIsPrivate();
		boolean responded = false;
		
		IChannel chan = event.getChannelSource();
		String channel = chan.getChannel();
		if (botCmd[0].equalsIgnoreCase("lastlinks")) {
			responded = true;
			try {
				queryLinks.setString(1,chan.getChannel());
				queryLinks.setString(2,getBot().getNick());
				queryLinks.setString(3,"Huw");
				if (botCmd.length > 1) {
					queryLinks.setString(4,"%" + botCmd[1] + "%");
				}
				else {
					queryLinks.setString(4,"%");
				}
				ResultSet links = queryLinks.executeQuery();
				String link;
				String[] outputLink = new String[2];
				Matcher matcher;
				while(links.next()) {
					link = links.getString("Description");
					matcher = regexLink.matcher(link);
					if (matcher.lookingAt()) {
						outputLink[0] = matcher.group(1);
						outputLink[1] = links.getString("Nick");
						//outputLink[0] = link;
						getUserFunctions().botMessage(source,"link",outputLink,isPrivate, chan, false);
					}
				}
			}
			catch (SQLException e) {
				getBot().sendErrorEvent("Lastlinks.chanBotRespond","SQLException",e.getMessage());
			}
		}
		else {
			responded = false;
		}
		return responded;
	}
}
