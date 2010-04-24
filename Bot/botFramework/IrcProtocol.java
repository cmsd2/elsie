/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

package botFramework;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.Vector;

import botFramework.interfaces.IIrcMessage;
import botFramework.interfaces.IIrcProtocol;

public class IrcProtocol implements IIrcProtocol {
	Pattern cmdParser1;
	Pattern cmdParser2;
	Pattern cmdParser3;
	Pattern ctcpParser;

	public IrcProtocol() {
		cmdParser1 = Pattern.compile("^\\:([^ ]+) ([^ ]+) ([^\n]+)");			//Prefix, command, parameters
		cmdParser2 = Pattern.compile("([\\w]+) ([^\n]+)");					//Command, parameters
		cmdParser3 = Pattern.compile("([^\\:]*) ?\\:(.*)");
		ctcpParser = Pattern.compile("(.*)\001([^\001]*?)\001(.*)");
	}
	public List<IIrcMessage> parse(String input, String myNick) {
		IrcMessage output = new IrcMessage();
		String temp = null;
		String[] temp2 = null;
		String[] temp3;
		
		Matcher matcher;
		boolean match;
		
		matcher = cmdParser1.matcher(input);
		match = matcher.lookingAt();

		if (match == true) {
			output.setPrefix(matcher.group(1));
			temp2 = output.getPrefix().split("!",2);
			if (temp2.length > 1) {
				output.setPrefixNick(temp2[0]);
				output.setIdent(temp2[1]);
			}
			else {
				output.setPrefixNick("");
			}
			output.setCommand(matcher.group(2));
			temp = matcher.group(3);
		}
		else {

			matcher = cmdParser2.matcher(input);
			match = matcher.lookingAt();

			if (match == true) {
				output.setCommand(matcher.group(1));
				temp = matcher.group(2);
			}
		}
		
		matcher = cmdParser3.matcher(temp);
		if (matcher.matches()) {
			matcher.lookingAt();
			
			output.setParams(matcher.group(1).split(" "));
			output.setEscapedParams(matcher.group(2));
		}
		else {
			output.setParams(temp.split(" "));
			output.setEscapedParams("");
		}
		
		if (output.getPrefixNick() == null) {
			output.setPrefixNick("");
		}
		
		if (output.getCommand().equalsIgnoreCase("PRIVMSG") && output.getParams()[0].equalsIgnoreCase(myNick)) {
			output.setPrivate(true);
		}
		else {
			output.setPrivate(false);
		}
		
		//System.out.println(output.escapedParams);
		
		Vector<IIrcMessage> outputs = new Vector<IIrcMessage>();
		IrcMessage output2;
		
		if (output.getCommand().equalsIgnoreCase("PRIVMSG")) {
			output.setEscapedParams(output.getEscapedParams().replaceAll("\020\020","\020"));
			output.setEscapedParams(output.getEscapedParams().replaceAll("\020\060","\000"));
			output.setEscapedParams(output.getEscapedParams().replaceAll("\020\156","\012"));
			output.setEscapedParams(output.getEscapedParams().replaceAll("\020\162","\015"));
			output.setEscapedParams(output.getEscapedParams().replaceAll("\020",""));
			
			output2 = (IrcMessage)output.clone();
			
			matcher = ctcpParser.matcher(output.getEscapedParams());
			
			while(matcher.matches()) {
				match = matcher.lookingAt();
				
				temp = matcher.group(1) + matcher.group(3);
				output.setEscapedParams(temp);
				
				temp = matcher.group(2);
				if (temp.length() > 0) {
					temp = temp.replaceAll("\134\134\141","\001");
					temp = temp.replaceAll("\134\134\134\134","\134\134");
					
					temp2 = temp.split(" ");
					output2.setCommand("CTCP_" + temp2[0]);
					if (temp2.length > 1) {
						output2.setEscapedParams(temp2[1]);
					}
					else {
						output2.setEscapedParams("");
					}
					for(int j = 2; j < temp2.length; j++) {
						output2.setEscapedParams(output2.getEscapedParams() + " " + temp2[j]);
					}
					outputs.add(output2);
				}
				
				matcher = ctcpParser.matcher(output.getEscapedParams());
				output2 = (IrcMessage)output.clone();
			}
			
			if (output.getEscapedParams().length() > 0) {
				outputs.add(0,output);
			}
		}
		else {
			outputs.add(0,output);
		}
		
		return outputs;
	}
	
	public String nick(String nick) {
		String output = "NICK " + nick + "\n";
		return output;
	}
	public String user(String nick, int mode, String username) {
		String output = "USER " + nick + " " + mode + " * " + username + "\n";
		return output;
	}
	public String join(String channel) {
		String output = "JOIN " + channel + "\n";
		return output;
	}
	public String part(String channel) {
		String output = "PART " + channel + "\n";
		return output;
	}
	public String quit(String reason) {
		String output = "QUIT :" + reason + "\n";
		return output;
	}
	public String pong(String hostname) {
		String output = "PONG :" + hostname + "\n";
		return output;
	}
	public String privmsg(String target, String message) {
		String output = "PRIVMSG " + target + " :" + message + "\n";
		return output;
	}
	public String names(String channel) {
		String output = "NAMES " + channel + "\n";
		return output;
	}
	public String op(String nick, String channel) {
		String output = "MODE " + channel + " +o " + nick + "\n";
		return output;
	}
	public String deop(String nick, String channel) {
		String output = "MODE " + channel + " -o " + nick + "\n";
		return output;
	}
	public String voice(String nick, String channel) {
		String output = "MODE " + channel + " +v " + nick + "\n";
		return output;
	}
	public String devoice(String nick, String channel) {
		String output = "MODE " + channel + " -v " + nick + "\n";
		return output;
	}
	public String whois(String nick) {
		String output = "WHOIS " + nick + "\n";
		return output;
	}
	public String kick(String nick, String channel, String reason) {
		String output = "KICK " + channel + " " + nick + " :" + reason + "\n";
		return output;
	}
	public String ban(String hostmask, String channel) {
		String output = "MODE " + channel + " +b " + hostmask + "\n";
		return output;
	}
	public String except(String hostmask, String channel) {
		String output = "MODE " + channel + " +e " + hostmask + "\n";
		return output;
	}
	public String ctcpPing(String nick, String timestamp) {
		String output = "NOTICE " + nick + " :\001PING " + timestamp + "\001\n";
		return output;
	}
	public String ctcpVersion(String nick, String name, String version, String environment) {
		String output = "NOTICE " + nick + " :\001VERSION " + name + " " + version + " " + environment + "\001\n";
		return output;
	}
	public String ctcpTime(String nick, String time) {
		String output = "NOTICE " + nick + " :\001TIME " + time + "\001\n";
		return output;
	}
}
