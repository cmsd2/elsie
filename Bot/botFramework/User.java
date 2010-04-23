package botFramework;

import botFramework.interfaces.IUser;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class User implements IUser {
	public String ident;
	public String status;
	
	User() {
	}

	User (String i, String s) {
		ident = i;
		status = s;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public String getIdent()
	{
		return ident;
	}
}
