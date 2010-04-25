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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import botFramework.interfaces.IBot;
import botFramework.interfaces.IDatabase;

public class DBHandler implements IDatabase {
	private Log log = LogFactory.getLog(DBHandler.class);
	private Connection db;
	private String driver;
	private String url;
	private IBot bot;
	
	public DBHandler() {
	}
	
	public IBot getBot()
	{
		return this.bot;
	}
	
	public void setBot(IBot bot)
	{
		this.bot = bot;
	}
	
	public String getDriver()
	{
		return this.driver;
	}
	
	public void setDriver(String driver)
	{
		if(this.db != null)
		{
			dbDisconnect();
			this.db = null;
		}

		this.driver = driver;

		try {
			Class.forName(driver);
		}
		catch (ClassNotFoundException e) {
			bot.sendErrorEvent("DBHandler.DBHandler","ClassNotFoundException",e.getMessage());
			bot.sendErrorEvent("DBHandler.DBHandler","problem","Could not locate database driver.");
		}
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	public void init() {
		
	}
	
	public Connection getConnection()
	{
		if(db == null)
		{
			dbConnect();
		}
		return db;
	}
	
	public void dbConnect() {
		try {
			log.info("Connecting to database");
			db = DriverManager.getConnection(url);
		}
		catch (SQLException e) {
			log.error("Failed to connect to database", e);
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
