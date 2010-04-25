package botFramework.interfaces;

import java.sql.Connection;

public interface IDatabase {
	String ROLE = IDatabase.class.getName();

	public Connection getConnection();

	public void dbReconnect();
}