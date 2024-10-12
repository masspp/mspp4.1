package ninja.mspp.core.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ninja.mspp.core.types.SpectrumCondition;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SpectrumAction {
	public String value();
	public int order() default 5;
	public SpectrumCondition condition() default SpectrumCondition.NONE;
}
