package elsie;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import botFramework.*;
import botFramework.interfaces.IBot;
import botFramework.interfaces.IChannel;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class InputConsole extends Thread {
	IBot bot;
	IChannel channel;
	BufferedReader input;
	
	Pattern commandParser;
	
	
	public InputConsole(IBot bot,IChannel channel) {
		this.bot = bot;
		this.channel = channel;
		
		this.input = new BufferedReader(new InputStreamReader(System.in));
		
		this.commandParser = Pattern.compile("/([^ ]*) *(.*)");
		
		this.start();
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
