package botFramework.interfaces;

public interface IIRCMessage {

	public abstract Object clone();

	public String getCommand();
	
	public void setCommand(String command);
	public String getPrefix();
	public void setPrefix(String prefix);
	public String[] getParams();
	public void setParams(String[] params);
	public String getEscapedParams();
	public void setEscapedParams(String escapedParams);
	public String getPrefixNick();
	public void setPrefixNick(String prefixNick);
	public String getIdent();
	public void setIdent(String ident);
	public boolean isPrivate();
	public void setPrivate(boolean isPrivate);
	
}