package botFramework.interfaces;
/**
 * @author sffubs
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface IErrorListener {
	void exception(String module, String exception, String message);
	void problem(String module, String problem);
}
