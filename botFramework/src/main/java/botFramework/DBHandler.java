/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

/*
 * The DBHandler object is intended to provide a set of database connection
 * management routines for access by listeners.
 */

package botFramework;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import botFramework.interfaces.IBot;
import botFramework.interfaces.IDatabase;

public class DBHandler implements IDatabase {
	public Connection db;
	private String url;
	private IBot bot;
	public DBHandler(IBot bot, String driver, String url) {
		try {
			Class.forName(driver).newInstance();
		}
		catch (ClassNotFoundException e) {
			bot.sendErrorEvent("DBHandler.DBHandler","ClassNotFoundException",e.getMessage());
			bot.sendErrorEvent("DBHandler.DBHandler","problem","Could not locate database driver.");
		}
		catch (InstantiationException e) {
			bot.sendErrorEvent("DBHandler.DBHandler","InstantiationException",e.getMessage());
		}
		catch (IllegalAccessException e) {
			bot.sendErrorEvent("DBHandler.DBHandler","IllegalAccessException",e.getMessage());
		}
		this.url = url;
		this.bot = bot;
		
		dbConnect();
	}
	
	public Connection getConnection()
	{
		return db;
	}
	
	public void dbConnect() {
		try {	
			db = DriverManager.getConnection(url);
		}
		catch (SQLException e) {
			bot.sendErrorEvent("DBHandler.dbConnect", "SQLException", e.getMessage());
		}
	}
	
	public void dbDisconnect() {
		try {
			if (!db.isClosed()) {
				db.close();
			}
		}
		catch (SQLException e) {
			bot.sendErrorEvent("DBHandler.dbDisconnect", "SQLException", e.getMessage());
		}
		catch (Exception e) {
			bot.sendErrorEvent("DBHandler.dbDisconnect","Exception",e.getMessage());
		}
	}
	
	public void dbReconnect() {
		dbDisconnect();
		try {
			while (db.isClosed()) {
				dbConnect();
			}
		}
		catch (SQLException e) {
			bot.sendErrorEvent("DBHandler.dbReconnect","SQLException",e.getMessage());
		}
	}
}
