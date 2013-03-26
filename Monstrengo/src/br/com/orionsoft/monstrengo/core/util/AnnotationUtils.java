/**
 * 
 */
package br.com.orionsoft.monstrengo.core.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import br.com.orionsoft.monstrengo.core.process.IProcess;

/**
 * Classe com métodos estáticos para facilitar a manipulação de annotations.
 * 
 * @author Lucio 20110620
 */
public abstract class AnnotationUtils {

	/**
	 * Este método procura a anotação no método get, se não encontra, procura
	 * então no método set, se não encontra, então procura no field. Pois este
	 * último, geralmente é privado e toda a hierarquia precisa ser percorrido
	 * 
	 * @param annotationClass
	 * @param propertyName
	 * @return A classe de anotação encontrada, ou null se não encontrou
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T findAnnotation(
			Class<? extends Annotation> annotationClass, Class<?> entityClass,
			String propertyName) {
		try {

			PropertyDescriptor p = new PropertyDescriptor(propertyName,
					entityClass);
			Method m = p.getReadMethod();
			if (m != null && m.isAnnotationPresent(annotationClass)) {
				return (T) m.getAnnotation(annotationClass);
			}
			m = p.getWriteMethod();
			if (m != null && m.isAnnotationPresent(annotationClass)) {
				return (T) m.getAnnotation(annotationClass);
			}

			/* Procura em todos os declaredField da hierarquia */
			Class<?> c = entityClass;
			while (c != Object.class) {
				try {
					Field f = c.getDeclaredField(propertyName);
					if (f.isAnnotationPresent(annotationClass)) {
						return (T) f.getAnnotation(annotationClass);
					}
				} catch (Exception e) {
					// System.out.println("==============>" + c.getName() + ":"
					// + propertyName);
					// e.printStackTrace();
				}
				c = c.getSuperclass();
			}
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return null;
	}

	/**
	 * Este método procura a anotação na classe e superclasses
	 * 
	 * @param annotationClass
	 * @param entityClasss
	 * @return A classe de anotação encontrada, ou null se não encontrou
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T findAnnotation(
			Class<? extends Annotation> annotationClass, Class<?> entityClass) {
		/* Procura na hierarquia completa */
		while ((entityClass != null) && (entityClass != Object.class)) {
			if (entityClass.isAnnotationPresent(annotationClass))
				return (T) entityClass.getAnnotation(annotationClass);

			entityClass = entityClass.getSuperclass();
		}
		return null;
	}

	/**
	 * Obtem uma lista com o nome de todas as classes anotadas com a anotação solicitada.
	 * @param annotationClass Classe de anotação (@interface) que se deseja procurar
	 * @param packageFilter nome inicial do pacote base para a procura. Por exemplo: br.com
	 * @return
	 */
	public static List<String> findAnnotatedClassesNames( String packageFilter, Class<? extends Annotation>... annotationClasses) {
		List<String> classes = new ArrayList<String>();
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false){
			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				/* Lucio 20120320: Por padrão, esta classe só retorna candidatos CONCRETOS e INDEPENDENTES.
				 * Assim, Pessoa e outras classes abstratas não estavam sendo retornadas */
				return beanDefinition.getMetadata().isIndependent();
			}
		};
		for(Class<? extends Annotation> annotation: annotationClasses)
			scanner.addIncludeFilter(new AnnotationTypeFilter(annotation));

		for (BeanDefinition bd : scanner.findCandidateComponents(packageFilter))
			classes.add(bd.getBeanClassName());

		return classes;
	}

	/**
	 * Obtem uma lista com o nome de todas as classes que são descententes de uma determinada interface
	 * @param annotationClass Classe de anotação (@interface) que se deseja procurar
	 * @param packageFilter nome inicial do pacote base para a procura. Por exemplo: br.com
	 * @return
	 */
	public static List<String> findClassesNames(String packageFilter, Class<?>... findClasses) {
		List<String> result = new ArrayList<String>();
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
		for(Class<?> annotation: findClasses)
			scanner.addIncludeFilter(new AssignableTypeFilter(annotation));

		for (BeanDefinition bd : scanner.findCandidateComponents(packageFilter))
			result.add(bd.getBeanClassName());

		return result;
	}

	public static class TestClass {
		private static class MySuperClass {
			@XmlElement(name = "id")
			private Long id;

			@XmlElement(name = "getId")
			public Long getId() {
				return id;
			}

			@XmlElement(name = "setId")
			public void setId(Long id) {
				this.id = id;
			}

		}

		@XmlRootElement
		private static class MyClass extends MySuperClass {
			@XmlElement(name = "field")
			private String field;

			@XmlElement(name = "getField")
			public String getField() {
				return field;
			}

			@XmlElement(name = "setField")
			public void setField(String field) {
				this.field = field;
			}
		}

		public static void main(String[] args) {
			XmlElement sw = AnnotationUtils.findAnnotation(XmlElement.class,
					MyClass.class, "id");
			System.out.println("Value=" + sw.toString());
			
			for(String classs: findAnnotatedClassesNames("br.com.orionsoft.monstrengo", Embeddable.class, Entity.class))
				System.out.println("Annotatded:" + classs);
					
			for(String classs: findClassesNames("br.com.orionsoft.monstrengo", IProcess.class))
				System.out.println("Assignable:" + classs);
		}
	}
}
