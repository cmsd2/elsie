/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

package elsie.plugins;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IChanEvent;
import botFramework.interfaces.IChanListener;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IIrcMessage;

public class Transcriber extends AbstractPlugin {
	private static final Log log = LogFactory.getLog(Transcriber.class);

	PreparedStatement queryTranscript;
	
	public Transcriber() {

	}
	
	public void prepare()
	{
		try {
			queryTranscript = getDatabase().getConnection().prepareStatement("INSERT INTO `transcript` VALUES(?,NOW(),?,?,?,?)");
		}
		catch (SQLException e) {
			e.printStackTrace(System.err);
			getBot().sendErrorEvent("Transcriber.Transcriber","SQLException",e.getMessage());
		}
	}

	public void chanRespond(IChanEvent event) {
		log.info("Handling event " + event);

		IChannel chan = event.getChannelSource();
		
		IIrcMessage msg = event.getIRCMessage();
		
		log.info("Command is " + msg.getCommand() + ". Private? " + msg.isPrivate());

		if ((msg.getCommand().equalsIgnoreCase("PRIVMSG")
			|| msg.getCommand().equalsIgnoreCase("JOIN")
			|| msg.getCommand().equalsIgnoreCase("PART")
			|| (msg.getCommand().equalsIgnoreCase("MODE"))
			|| msg.getCommand().equalsIgnoreCase("QUIT")
			|| msg.getCommand().equalsIgnoreCase("ERROR")
			|| msg.getCommand().equalsIgnoreCase("CTCP_ACTION")
			|| msg.getCommand().equalsIgnoreCase("NICK")
			|| msg.getCommand().equalsIgnoreCase("KICK")
			|| msg.getCommand().equalsIgnoreCase("TOPIC")) & !msg.isPrivate()) {
				
			log.info("Saving public message " + msg.getCommand() + " to database");
			/* (msg.ident == null) {
				msg.ident = "";
			}*/
				
			if (msg.getCommand().equalsIgnoreCase("ERROR")) {
				msg.setPrefixNick(getBot().getNick());
			}
			else if (msg.getCommand().equalsIgnoreCase("KICK")) {
				msg.setEscapedParams(msg.getParams()[1] + " (" + msg.getEscapedParams() + ")");
			}
			else if (msg.getCommand().equalsIgnoreCase("MODE")) {
				String parameters;
				int j;
				if (msg.getParams().length < 2) {
					parameters = msg.getParams()[0];
					j = 1;
				}
				else {
					parameters = msg.getParams()[1];
					j =2;
				}
				for (int i = j; i < msg.getParams().length; i++) {
					parameters = parameters + " " + msg.getParams()[i];
				}
				msg.setEscapedParams(parameters);
			}
			else if (msg.getCommand().equalsIgnoreCase("JOIN") | msg.getCommand().equalsIgnoreCase("PART")) {
				msg.setEscapedParams("");
			}
			else if (msg.getCommand().equalsIgnoreCase("CTCP_ACTION")) {
				msg.setCommand("ACTION");
			}
			
			if (msg.getPrefixNick().equals("")) {
				msg.setPrefixNick(msg.getPrefix());
			}
				
			try {
				queryTranscript.setString(1,chan.getChannel());
				queryTranscript.setString(2,msg.getCommand());
				queryTranscript.setString(3,msg.getPrefixNick());
				queryTranscript.setString(4,msg.getIdent());
				queryTranscript.setString(5,msg.getEscapedParams());
				queryTranscript.executeUpdate();
			}
			catch (SQLException e) {
				getBot().sendErrorEvent("transcript","SQLException",e.getMessage());
			}
		}
	}
}
