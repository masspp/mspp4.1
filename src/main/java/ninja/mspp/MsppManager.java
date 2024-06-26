package ninja.mspp;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import ninja.mspp.core.annotation.clazz.Listener;
import ninja.mspp.core.model.listener.ListenerInfo;
import ninja.mspp.core.model.listener.ListenerMethod;
import ninja.mspp.core.model.ms.Chromatogram;
import ninja.mspp.core.model.ms.Sample;
import ninja.mspp.core.model.ms.Spectrum;
import ninja.mspp.core.view.ViewInfo;
import ninja.mspp.view.MainFrame;

public class MsppManager {
	private static MsppManager instance = null;
	
	private List<ListenerInfo> listeners;
	
	private List<Sample> openedSamples;
	
	private Sample activeSample;
	private Spectrum activeSpectrum;
	private Chromatogram activeChromatogram;
	private Stage mainStage;
	private MainFrame mainFrame;
	
	private MsppManager() {
		this.openedSamples = new ArrayList<Sample>();
		this.listeners = null;
		this.activeSample = null;
		this.activeSpectrum = null;
		this.activeChromatogram = null;
		this.mainStage = null;
		this.mainFrame = null;
	}
	
	public List<ListenerInfo> getListeners() {
		if (this.listeners == null) {
			this.listeners = this.createListenerList();
		}
		
		return this.listeners;
	}
	
	private List<ListenerInfo> createListenerList() {
		List<ListenerInfo> listeners = new ArrayList<ListenerInfo>();
		
		Reflections reflections = new Reflections(new ConfigurationBuilder()
	            .setUrls(ClasspathHelper.forPackage("ninja.mspp"))
	            .setScanners(new SubTypesScanner(false)));

	    Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
	    for (Class<?> clazz : classes) {
	    	Listener listener = clazz.getAnnotation(Listener.class);
			if (listener != null) {
				String name = listener.value();
				try {
					Constructor<?> constructor = clazz.getConstructor();
					Object object = constructor.newInstance();
					ListenerInfo info = new ListenerInfo(object, name);
					listeners.add(info);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
	    }

		return listeners;
	}
	
	
	public <T extends Annotation> List<ListenerMethod<T>> getMethods(Class<T> clazz) {
		List<ListenerInfo> listeners = this.getListeners();
		List<ListenerMethod<T>> methods = new ArrayList<ListenerMethod<T>>();
		for (ListenerInfo listener : listeners) {
			Object object = listener.getObject();
			Method[] classMethods = object.getClass().getMethods();
			for (Method method : classMethods) {
				T annotation = method.getDeclaredAnnotation(clazz);
				if (annotation != null) {
					ListenerMethod<T> listenerMethod = new ListenerMethod<T>(object, method, annotation);
					methods.add(listenerMethod);
				}
			}
		}
		
		return methods;
	}

	
	public <T extends Annotation> void invoke(Class<T> clazz, Object... args) {
		List<ListenerMethod<T>> methods = this.getMethods(clazz);
		for (ListenerMethod<T> method : methods) {
			method.invoke(args);
		}
	}
	
	public <T> ViewInfo<T> createWindow(Class<T> clazz, String fxml) throws IOException {
		FXMLLoader loader = new FXMLLoader(clazz.getResource(fxml));
		Parent root = loader.load();
		T controller = loader.getController();
		return new ViewInfo<T>(root, controller);
	}
	
	public Sample getActiveSample() {
		return activeSample;
	}

	public void setActiveSample(Sample activeSample) {
		this.activeSample = activeSample;
	}

	public Spectrum getActiveSpectrum() {
		return activeSpectrum;
	}

	public void setActiveSpectrum(Spectrum activeSpectrum) {
		this.activeSpectrum = activeSpectrum;
	}

	public Chromatogram getActiveChromatogram() {
		return activeChromatogram;
	}

	public void setActiveChromatogram(Chromatogram activeChromatogram) {
		this.activeChromatogram = activeChromatogram;
	}
	
	public Stage getMainStage() {
		return mainStage;
	}
	
	public void setMainStage(Stage mainStage) {
		this.mainStage = mainStage;
	}
	
	public MainFrame getMainFrame() {
		return mainFrame;
	}
	
	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}
	
	public List<Sample> getOpenedSamples() {
		return openedSamples;
	}

	public static MsppManager getInstance() {
		if (MsppManager.instance == null) {
            MsppManager.instance = new MsppManager();
        }
        return MsppManager.instance;
    }
}
