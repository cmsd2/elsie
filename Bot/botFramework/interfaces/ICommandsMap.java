package botFramework.interfaces;

public interface ICommandsMap {
	public boolean hasPluginCommand(String cmd);
	
	public String getPluginCommand(String cmd);
	
	public void addPluginCommand(String cmd, String cname);
	
	public void removePluginCommand(String cmd);
}
