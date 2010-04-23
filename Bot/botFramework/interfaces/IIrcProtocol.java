package botFramework.interfaces;


public interface IIrcProtocol {
	public Object[] parse(String input, String myNick);
	public String nick(String nick);
	public String user(String nick, int mode, String username);
	public String join(String channel);
	public String part(String channel);
	public String quit(String reason);
	public String pong(String hostname);
	public String privmsg(String target, String message);
	public String names(String channel);
	public String op(String nick, String channel);
	public String deop(String nick, String channel);
	public String voice(String nick, String channel);
	public String devoice(String nick, String channel);
	public String whois(String nick);
	public String kick(String nick, String channel, String reason);
	public String ban(String hostmask, String channel);
	public String except(String hostmask, String channel);
	public String ctcpPing(String nick, String timestamp);
	public String ctcpVersion(String nick, String name, String version, String environment);
	public String ctcpTime(String nick, String time);
}