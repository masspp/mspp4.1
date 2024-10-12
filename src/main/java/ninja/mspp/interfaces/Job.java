package ninja.mspp.interfaces;

public interface Job {
	public Object execute();
	public void onSucceeded(Object result);
}
