package ninja.mspp.core.model.listener;

public class ListenerInfo {
	private Object object;
	private String name;
	
	public ListenerInfo(Object object, String name) {
		this.object = object;
		this.name = name;
	}
	
	public Object getObject() {
		return this.object;
	}
	
	public String getName() {
		return this.name;
	}
}
