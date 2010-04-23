package botFramework.interfaces;


public interface IUserFunctions {

	public abstract String deAlias(String nick);

	public abstract boolean addAlias(String nick, String alias);

	public abstract boolean isUser(String user);

	public abstract boolean isRegisteredIdent(String user, String ident);

	public abstract String matchIdent(String ident);

	public abstract boolean setStatus(String nick, IChannel chan,
			boolean enforceOnly);

	public abstract boolean botMessage(String nick, String error,
			String[] replace, boolean isPrivate, IChannel chan, boolean delay);

	public abstract boolean annoyUser(String username);

}