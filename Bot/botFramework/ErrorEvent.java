package botFramework;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

import java.util.EventObject;

public class ErrorEvent extends EventObject {
	private String module;
	private String type;
	private String message;
	
	public ErrorEvent(Object source, String module, String type, String message) {
		super(source);
		
		this.module = module;
		this.type = type;
		this.message = message;
	}
	public String getModule() {
		return module;
	}
	public String getType() {
		return type;
	}
	public String getMessage() {
		return message;
	}
	public String toString() {
		return module + "|" + type + "|" + message;
	}
}