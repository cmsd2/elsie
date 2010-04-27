package elsie.plugins.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChannel;
import elsie.plugins.AbstractPlugin;

public class InvalidCommand extends AbstractPlugin {

	private static final Log log = LogFactory.getLog(InvalidCommand.class);

	@Override
	protected boolean chanBotRespond(IChanBotEvent event) {
		log.info("Responding to event " + event);
		String source = event.getCommandSource();
		String[] botCmd = event.getBotCommand();
		boolean isPrivate = event.getIsPrivate();

		IChannel chan = event.getChannelSource();
		
		String temp = botCmd[0];

		for (int i = 1; i < botCmd.length; i++) {
			temp = temp + " " + botCmd[i];
		}
		String [] replace = {temp};
		getUserFunctions().botMessage(source, "cmd_invalid", replace, isPrivate, chan, false);
		return true;
	}

}
