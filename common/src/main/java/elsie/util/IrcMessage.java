package elsie.util;

import botFramework.interfaces.IIrcMessage;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class IrcMessage implements Cloneable, IIrcMessage {
	private String command;
	private String prefix;
	private String[] params;
	private String escapedParams;
	private String prefixNick;
	private String ident;
	private boolean isPrivate;
	
	public IrcMessage(String command, String prefix, String[] params, String
		escapedParams, String prefixNick, String ident, boolean isPrivate) {
		this.command = command;
		this.prefix = prefix;
		this.params = params;
		this.escapedParams = escapedParams;
		this.prefixNick = prefixNick;
		this.ident = ident;
		this.isPrivate = isPrivate;
	}
	public IrcMessage() {
	}
	
	/* (non-Javadoc)
	 * @see botFramework.IIRCMessage#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String[] getParams() {
		return params;
	}
	public void setParams(String[] params) {
		this.params = params;
	}
	public String getEscapedParams() {
		return escapedParams;
	}
	public void setEscapedParams(String escapedParams) {
		this.escapedParams = escapedParams;
	}
	public String getPrefixNick() {
		return prefixNick;
	}
	public void setPrefixNick(String prefixNick) {
		this.prefixNick = prefixNick;
	}
	public String getIdent() {
		return ident;
	}
	public void setIdent(String ident) {
		this.ident = ident;
	}
	public boolean isPrivate() {
		return isPrivate;
	}
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	
	
}
