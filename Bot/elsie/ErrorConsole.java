package elsie;

import botFramework.*;
import botFramework.interfaces.IErrorListener;

/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ErrorConsole implements IErrorListener {
	public ErrorConsole() {
	}
	public void exception(String module, String type, String message) {
		System.out.println(module + ": Caught exception " + type + ": " + message);
	}
	public void problem(String module, String problem) {
		System.out.println(module + ": Problem: " + problem);
	}
}
