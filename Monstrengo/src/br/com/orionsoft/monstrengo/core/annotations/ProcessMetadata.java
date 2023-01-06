/**
 * 
 */
package br.com.orionsoft.monstrengo.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anota um processo que será controlado pelo ProcessManager
 * http://java.sun.com/j2se/1.5.0/docs/api/java/lang/annotation/package-summary.html
 * http://java.sun.com/j2se/1.5.0/docs/guide/language/annotations.html
 * Foi lido para ter uma noção básica sobre anotações.
 * 
 * @author yuka
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProcessMetadata
{
	public String label() default "";
	public String hint() default "";
	public String description() default "";
}
