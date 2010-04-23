package botFramework;

public interface IErrorEvent {

	public abstract String getModule();

	public abstract String getType();

	public abstract String getMessage();

	public abstract String toString();

}