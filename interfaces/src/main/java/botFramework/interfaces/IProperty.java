package botFramework.interfaces;


public interface IProperty {
	public Object getValue();
	
	public void setValue(Object value);
	
	public void clearValue();
	
	public void apply(Object bean);
}