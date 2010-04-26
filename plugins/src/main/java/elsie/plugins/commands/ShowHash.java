package elsie.plugins.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import elsie.plugins.AbstractPlugin;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IUser;

public class ShowHash extends AbstractPlugin {

	private static final Log log = LogFactory.getLog(ShowHash.class);

	@Override
	protected boolean chanBotRespond(IChanBotEvent event) {
		String source = event.getCommandSource();
		boolean isPrivate = event.getIsPrivate();
		
		IChannel chan = event.getChannelSource();
		
		log.info("Received chan bot event showhash");
		String[] output = new String[4];
		IUser userInfo;
		
		for(String userName : chan.getUsers())
		{
			output[0] = chan.getChannel();
			
			output[1] = userName;
			
			userInfo = chan.getUserStatus(output[1]);
			
			output[2] = userInfo.getIdent();
			output[3] = userInfo.getStatus();
			
			getUserFunctions().botMessage(source, "showhash", output, isPrivate, chan, false);
		}
		
		return true;
	}

}
