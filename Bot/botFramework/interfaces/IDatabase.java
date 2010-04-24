package botFramework.interfaces;

import java.sql.Connection;

public interface IDatabase {
	public Connection getConnection();

	public void dbReconnect();
}