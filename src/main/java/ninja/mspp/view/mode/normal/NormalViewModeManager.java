package ninja.mspp.view.mode.normal;

public class NormalViewModeManager {
	private static NormalViewModeManager instance;
	
	private NormalViewModeManager() {
	}
	
	public static NormalViewModeManager getInstance() {
		if (instance == null) {
			instance = new NormalViewModeManager();
		}
		return instance;
	}
}
