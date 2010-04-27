package elsie.plugins.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IChanBotEvent;
import botFramework.interfaces.IChanBotListener;
import elsie.plugins.AbstractPlugin;
import elsie.util.Beans;

public class MissingCommand extends AbstractPlugin {

	private static Log log = LogFactory.getLog(MissingCommand.class);

	private PreparedStatement queryPlugins;
	
	public void prepare() throws SQLException
	{
		queryPlugins = getDatabase().getConnection().prepareStatement("select classname, beanId from plugins where command = ?");
	}
	
	@Override
	protected boolean chanBotRespond(IChanBotEvent event) {
		log.info("Handling event " + event);

		String command = getCommand(event);
		
		try {
			log.info("Querying database for command " + command);

			queryPlugins.setString(1, command);
			
			ResultSet rs = queryPlugins.executeQuery();
			
			if(rs.next()) {
				String className = rs.getString(1);
				String beanId = rs.getString(2);

				Object plugin = null;

				if(className != null)
				{
					log.info("Command " + command + " mapped to class " + className);
				
					plugin = getPlugins().loadPluginBean(className);
				} else if(beanId != null) {
					log.info("Command " + command + " mapped to beanId " + beanId);
					
					plugin = getApplicationContext().getBean(beanId);
				}
				
				if(plugin != null)
				{
					log.info("Successfully loaded plugin " + command + " => " + className);
					
					try {
						IChanBotListener l = Beans.findInterface(plugin, IChanBotListener.class);
						
						l.respond(event);
						
						log.info("Saving mapping " + command + " => " + className);
						getPlugins().getCommandsMap().addPluginCommand(command, className);

					} catch (Exception e) {
						log.error("Exception running plugin " + command + " => " + className, e);
					}
				}
			}
		} catch (SQLException e) {
			log.error("Exception querying database for plugin to handle command " + command ,e);
		} catch (Exception e) {
			log.error("Exception finding plugin to handle command " + command ,e);
		}
		
		return false;
	}
	
	public String getCommand(IChanBotEvent event)
	{
		return event.getBotCommand()[0];
	}

}
