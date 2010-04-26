package elsie;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import botFramework.IrcProtocol;
import botFramework.interfaces.IBot;
import botFramework.interfaces.IChannel;
import botFramework.interfaces.IDatabase;
import botFramework.interfaces.IUser;
import botFramework.interfaces.IUserFunctions;

public class UserFunctions implements IUserFunctions {
	IBot bot;
	IDatabase mysql;
	IrcProtocol irc;
	Random rnd;
	
	final long typingSpeed = 150;
	
	PreparedStatement queryAlias;
	PreparedStatement queryUserInfo;
	PreparedStatement queryUserIdents;
	PreparedStatement queryAddAlias;
	PreparedStatement queryUniqueIdents;
	PreparedStatement queryLinks;
	PreparedStatement queryBotMessage;
	PreparedStatement queryBotState;
	PreparedStatement setBotAngry;
		
	public UserFunctions () {
	}
	
	public void init()
	{
		irc = new IrcProtocol();
		rnd = new Random();
		
		try {
			queryAlias = mysql.getConnection().prepareStatement("SELECT Nick FROM aliases WHERE Alias=? LIMIT 1");
			queryUserInfo = mysql.getConnection().prepareStatement("SELECT * FROM `users` WHERE Username=? LIMIT 1");
			queryUserIdents = mysql.getConnection().prepareStatement("SELECT * FROM `idents` WHERE User=?");
			queryAddAlias = mysql.getConnection().prepareStatement("INSERT INTO `aliases` VALUES(?,?)");
			queryUniqueIdents = mysql.getConnection().prepareStatement("SELECT * FROM `idents` WHERE `Unique`=\"Yes\"");
			queryBotMessage = mysql.getConnection().prepareStatement("SELECT * FROM `errors` WHERE name=?");
			queryBotState = mysql.getConnection().prepareStatement("SELECT * FROM `bot_state`");
			setBotAngry = mysql.getConnection().prepareStatement("UPDATE `bot_state` SET angry = ?");
		}
		catch (SQLException e) {
			bot.sendErrorEvent("UserFunctions.UserFunctions","SQLException",e.getMessage());
		}
	}
	
	public IBot getBot()
	{
		return bot;
	}

	public void setBot(IBot bot)
	{
		this.bot = bot;
	}
	
	public IDatabase getDatabase()
	{
		return mysql;
	}

	public void setDatabase(IDatabase database)
	{
		this.mysql = database;
	}
		
	/* (non-Javadoc)
	 * @see elsie.IUserFunctions#deAlias(java.lang.String)
	 */
	public String deAlias(String nick) {
		Statement query;
		String userName;

		int numAliases;
		ResultSet results;

		try {
			queryAlias.setString(1,nick);
			results = queryAlias.executeQuery();
			if (results.first() == true) {
				userName = results.getString("Nick");
			}
			else {
				userName = nick;
			}

			return userName;
		}
		catch (SQLException e) {
			bot.sendErrorEvent("deAlias", "SQLException", e.getMessage());
			bot.sendErrorEvent("deAlias", "problem", "Assuming database connection broken, attempting to reconnect.");
			mysql.dbReconnect();
			
			return nick;
		}
		catch (Exception e) {
			bot.sendErrorEvent("deAlias", "Exception", e.getMessage());
			return nick;
		}
		
		
	}

	/* (non-Javadoc)
	 * @see elsie.IUserFunctions#addAlias(java.lang.String, java.lang.String)
	 */
	public boolean addAlias(String nick, String alias) {
		try {
			queryAddAlias.setString(1,nick);
			queryAddAlias.setString(2,alias);
			queryAddAlias.executeUpdate();
		}
		catch (SQLException e) {
			bot.sendErrorEvent("addAlias","SQLException",e.getMessage());
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see elsie.IUserFunctions#isUser(java.lang.String)
	 */
	public boolean isUser(String user) {
		try {
			queryUserInfo.setString(1,user);
			ResultSet results = queryUserInfo.executeQuery();

			return results.isBeforeFirst();
		}
		catch (SQLException e) {
			bot.sendErrorEvent("isUser","SQLException",e.getMessage());
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see elsie.IUserFunctions#isRegisteredIdent(java.lang.String, java.lang.String)
	 */
	public boolean isRegisteredIdent(String user, String ident) {
		String regIdent;
		
		try {
			queryUserIdents.setString(1,user);
			ResultSet registeredIdents = queryUserIdents.executeQuery();
			
			if (registeredIdents.first() == true) {
				while(registeredIdents.isAfterLast() == false) {
					regIdent = registeredIdents.getString("Ident");
					registeredIdents.next();
					if (ident.matches(regIdent) == true) {
						return true;
					}
				}
			}
			else {
				return false;
			}
			return false;
		}
		catch (SQLException e) {
			bot.sendErrorEvent("isRegisteredIdent","SQLException",e.getMessage());
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see elsie.IUserFunctions#matchIdent(java.lang.String)
	 */
	public String matchIdent(String ident) {
		String ident2;		
		try {
			ResultSet idents = queryUniqueIdents.executeQuery();

			if (idents.first() == true) {
				while(idents.isAfterLast() == false) {
					ident2 = idents.getString("Ident");
					if (ident.matches(ident2)) {
						return idents.getString("User");
					}
					idents.next();
				}
			}
			return "";
		}
		catch (SQLException e) {
			bot.sendErrorEvent("matchIdent","SQLException",e.getMessage());
			return "";
		}
	}

	/* (non-Javadoc)
	 * @see elsie.IUserFunctions#setStatus(java.lang.String, botFramework.interfaces.IChannel, boolean)
	 */
	public boolean setStatus(String nick, IChannel chan, boolean enforceOnly) {
		String userName;
		
		String userOp;
		String userVoice;
		boolean enforce;
		
		userName = deAlias(nick);
			
		if (userName == null) {
			bot.sendErrorEvent("setStatus","problem","deAlias lookup failed, using nick as default alias");
			userName = nick;
		}

		ResultSet userInfo;
		ResultSet registeredIdents;

		try {		
			queryUserInfo.setString(1,userName);
			userInfo = queryUserInfo.executeQuery();
			if (userInfo.first() == true) {
				userOp = userInfo.getString("Op");
				userVoice = userInfo.getString("Voice");
				enforce = userInfo.getString("Enforce").equalsIgnoreCase("Yes");
			}
			else {
				return false;
			}
			
			if (enforceOnly & !enforce) {
				return true;
			}
			
			IUser user = chan.getUserStatus(nick);
			if (user.getIdent().equals("")) {
				bot.enqueueCommand(irc.whois(nick));
				return false;
			}
			
			if (enforce == true) {
				if (isRegisteredIdent(userName,user.getIdent())) {
					if (userOp.equalsIgnoreCase("No") & user.getStatus().equals("@")) {
						bot.enqueueCommand(irc.deop(nick,chan.getChannel()));
					}
					if (userVoice.equalsIgnoreCase("No") & user.getStatus().equals("+")) {
						bot.enqueueCommand(irc.devoice(nick,chan.getChannel()));
					}
				}
			}
			
			if (isRegisteredIdent(userName,user.getIdent()) == true) {
				if (userOp.compareTo("Yes") == 0 && user.getStatus().compareTo("@") != 0) {
					bot.enqueueCommand(irc.op(nick,chan.getChannel()));
				}
				if (userVoice.compareTo("Yes") == 0 && user.getStatus().compareTo("@") != 0 && user.getStatus().compareTo("+") != 0) {
					bot.enqueueCommand(irc.voice(nick,chan.getChannel()));
				}
				
			}	
		}
		
		catch (SQLException e) {
			bot.sendErrorEvent("setStatus", "SQLException", e.getMessage());
			bot.sendErrorEvent("setStatus", "problem", "Assuming database connection broken, attempting to reconnect.");
			mysql.dbReconnect();
			
			return false;
		}
		return true;

	}

	/* (non-Javadoc)
	 * @see elsie.IUserFunctions#botMessage(java.lang.String, java.lang.String, java.lang.String[], boolean, botFramework.interfaces.IChannel, boolean)
	 */
	public boolean botMessage(String nick, String error, String[] replace, boolean isPrivate, IChannel chan, boolean delay) {
		try {
			queryBotMessage.setString(1,error);
			ResultSet results = queryBotMessage.executeQuery();

			int numRows;
			String message;
			String[] splitMessage;
			String forcePublic;
			
			if (results.last() == true) {
				numRows = results.getRow();
				if (numRows > 1) {
					results.absolute(rnd.nextInt(numRows - 1) + 1);
				}
				message = results.getString("message");
				forcePublic = results.getString("Public");

				if (replace != null) {
					for(int i = 0; i < replace.length; i++) {
						replace[i] = replace[i].replaceAll("\\\\","\\\\\\\\");
						replace[i] = replace[i].replaceAll("\\$","\\\\\\$");
						message = message.replaceAll("%" + i,replace[i]);
					}
				}

				splitMessage = message.split("\n");

				if (forcePublic.equals("Yes") | isPrivate == false) {
					if (splitMessage.length > 1) {
						if (nick.compareTo("") != 0 & nick.compareTo(chan.getChannel()) != 0) {
							send(chan.getChannel(),nick+":",delay);
						}
						for(int i = 0; i < splitMessage.length; i++) {
							send(chan.getChannel(),splitMessage[i],delay);
						}
					}
					else {
						if (nick.compareTo("") != 0 & nick.compareTo(chan.getChannel()) != 0) {
							send(chan.getChannel(),nick + ": " + splitMessage[0],delay);
						}
						else {
							send(chan.getChannel(),splitMessage[0],delay);
						}
					}
				}
				else {
					for(int i = 0; i < splitMessage.length; i++) {
						send(nick,splitMessage[i],delay);
					}
				}
				
				return true;
			}
			else {
				bot.sendErrorEvent("botMessage","problem", "No such message!");
				return false;
			}
		}
		catch (SQLException e) {
			bot.sendErrorEvent("botMessage","SQLException",e.getMessage());
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see elsie.IUserFunctions#annoyUser(java.lang.String)
	 */
	public boolean annoyUser(String username) {
		try {
			queryUserInfo.setString(1,username);
			ResultSet results = queryUserInfo.executeQuery();
			boolean annoy;
			if (results.first()) {
				annoy = results.getString("Annoy").equalsIgnoreCase("Yes");
			}
			else {
				annoy = false;
			}
		
			return annoy;
		}
		catch (SQLException e) {
			bot.sendErrorEvent("UserFunctions.annoyUser","SQLException",e.getMessage());
			return false;
		}
	}
		
	private void send(String target,String string,boolean delay) {
		long d;
		if (delay == true) {
			d = 1;
		}
		else {
			d = 0;
		}
		bot.enqueueMessage(target,string,(long)string.length() * typingSpeed * d);
	}

	@Override
	public boolean isAngry() {
		try {
			ResultSet rs = queryBotState.executeQuery();
			return rs.getBoolean("angry");
		} catch (SQLException e) {
			throw new RuntimeException("Error getting bot state", e);
		}
	}

	@Override
	public void setAngry(boolean angry) {
		try {
			setBotAngry.setBoolean(1, angry);
			setBotAngry.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Error setting bot state", e);
		}
	}
}
