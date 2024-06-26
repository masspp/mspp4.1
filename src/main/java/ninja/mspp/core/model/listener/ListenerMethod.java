package ninja.mspp.core.model.listener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import ninja.mspp.MsppManager;
import ninja.mspp.core.annotation.parameter.ActiveChromatogram;
import ninja.mspp.core.annotation.parameter.ActiveSample;
import ninja.mspp.core.annotation.parameter.ActiveSpectrum;

public class ListenerMethod<T extends Annotation> {
	private T annotation;
	private Object object;
	private Method method;
	
	public ListenerMethod(Object object, Method method, T annotation) {
		this.object = object;
		this.method = method;
		this.annotation = annotation;
	}
	
	public T getAnnotation() {
		return this.annotation;
	}
	
	public Object getObject() {
		return this.object;
	}
	
	public Method getMethod() {
		return this.method;
	}
	
	public Object invoke(Object... args) {
		Object result = null;
		MsppManager manager = MsppManager.getInstance();
		List<Object> list = new ArrayList<Object>();
		Parameter[] parametes = this.method.getParameters();
		int index = 0;
		for (Parameter parameter : parametes) {
			if (parameter.getDeclaredAnnotation(ActiveSample.class) != null) {
				list.add(manager.getActiveSample());
			}
			else if (parameter.getDeclaredAnnotation(ActiveSpectrum.class) != null) {
				list.add(manager.getActiveSpectrum());
			}
			else if(parameter.getDeclaredAnnotation(ActiveChromatogram.class) != null) {
				list.add(manager.getActiveChromatogram());
			}
			else {
				list.add(args[index]);
				index++;
			}
		}
		
		try {
			Object[] array = list.toArray();
			result = this.method.invoke(this.object, array);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
