/**
 * 
 */
package botFramework;

import java.util.TimerTask;

import elsie.util.IrcMessage;

import botFramework.interfaces.IBot;

class BotMessage extends TimerTask {

	private IBot bot;
	private String target;
	private String message;
	private long delay;
	
	public BotMessage(IBot bot, String target,String message,long delay) {
		this.bot = bot;
		this.target = target;
		this.message = message;
		this.delay = delay;
	}
	
	public String getTarget()
	{
		return target;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public long getDelay()
	{
		return delay;
	}
	
	public void run() {
		String string = this.bot.getIrc().privmsg(target,message);
		String nextString;
		int i;
		String[] params = new String[1];
		final int limit = 510 - (target.length() + this.bot.getNick().length() + this.bot.getHostname().length() + 15);
		
		while (string.length() > limit) {
			i = string.lastIndexOf(" ",limit - 1);
			if (i <= limit-50) {
				nextString = string.substring(limit);
				string = string.substring(0,limit);
			}
			else {
				nextString = string.substring(i+1);
				string = string.substring(0,i);
			}
			this.bot.enqueueCommand(string + "\n");
			string = this.bot.getIrc().privmsg(target,nextString);
		}
		
		this.bot.enqueueCommand(string);
		params[0] = target;
		boolean isPrivate;
		
		if (target.startsWith("#") | target.startsWith("&")
			| target.startsWith("!") | target.startsWith("+")) {
			isPrivate = false;
		}
		else {
			isPrivate = true;
		}
		
		if (message.matches("\001ACTION .*\001")) {
			this.bot.sendIRCEvent(new IrcMessage("CTCP_ACTION",this.bot.getNick(),params,message.replaceAll("\001ACTION (.*)\001","$1"),this.bot.getNick(),"",isPrivate));
		}
		else {
			this.bot.sendIRCEvent(new IrcMessage("PRIVMSG",this.bot.getNick(),params,message,this.bot.getNick(),"",isPrivate));
		}
	}
}