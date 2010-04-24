package elsie.plugins;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;
import elsie.util.Beans;

public class MissingCommand extends AbstractPlugin {

	private PreparedStatement queryPlugins;
	
	public void prepare() throws SQLException
	{
		queryPlugins = getDatabase().getConnection().prepareStatement("select classname from plugins where command = ?");
	}
	
	@Override
	protected boolean chanBotRespond(IChanBotEvent event) {
		String command = getCommand(event);
		
		try {
			queryPlugins.setString(1, command);
			
			ResultSet rs = queryPlugins.executeQuery();
			
			if(rs.next()) {
				String className = rs.getString(1);
				
				Object plugin = getPlugins().loadPlugin(className);
				
				if(plugin != null)
				{
					System.out.println("Successfully loaded plugin " + command + " => " + className);
					
					try {
						IChanBotListener l = Beans.findInterface(plugin, IChanBotListener.class);
						
						l.respond(event);
						
						System.out.println("Saving mapping " + command + " => " + className);
						getPlugins().getCommandsMap().addPluginCommand(command, className);

					} catch (Exception e) {
						System.err.println("Exception running plugin " + command + " => " + className);
						e.printStackTrace(System.err);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(System.err);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
		return false;
	}
	
	public String getCommand(IChanBotEvent event)
	{
		return event.getBotCommand()[0];
	}

}
