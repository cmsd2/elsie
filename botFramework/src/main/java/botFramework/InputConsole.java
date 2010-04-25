package botFramework;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import botFramework.interfaces.IBot;
import botFramework.interfaces.IChannel;

public class InputConsole extends Thread {
	IBot bot;
	IChannel channel;
	BufferedReader input;
	
	Pattern commandParser;
	
	
	public InputConsole() {
	}
	
	public IBot getBot()
	{
		return bot;
	}

	public void setBot(IBot bot)
	{
		this.bot = bot;
	}
	
	public IChannel getChannel()
	{
		return channel;
	}

	public void setChannel(IChannel channel)
	{
		this.channel = channel;
	}
	
	public void init()
	{
		this.input = new BufferedReader(new InputStreamReader(System.in));
		
		this.commandParser = Pattern.compile("/([^ ]*) *(.*)");
		
		this.start();
	}
	
	public String toString()
	{
		return "InputConsole[" + super.toString() + "]@" + hashCode();
	}
	
	public void run() {
		String in;
		String command = "";
		
		while (true) {
			try {
				in = input.readLine();
				command = "";
				
				Matcher matcher = commandParser.matcher(in);
				
				if (matcher.lookingAt()) {
					command = matcher.group(1);
					in = matcher.group(2);
				}
			
				if (command.equalsIgnoreCase("me")) {
					in = "\001ACTION " + in + "\001";
				}
				
				bot.enqueueMessage(channel.getChannel(),in,0);
				
			
				sleep(50);
			}
			catch (InterruptedException e) {
			}
			catch (IOException e) {
				bot.sendErrorEvent("InputConsole.run","IOException",e.getMessage());
			}
		}
	}
	
}
