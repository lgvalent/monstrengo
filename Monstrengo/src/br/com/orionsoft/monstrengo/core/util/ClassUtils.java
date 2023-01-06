/**
 * 
 */
package br.com.orionsoft.monstrengo.core.util;

import java.util.ArrayList;
import java.util.List;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditCrudRegister;
import br.com.orionsoft.monstrengo.auditorship.entities.AuditRegister;

/**
 * Classe com métodos estáticos para facilitar a manipulação de classes.
 * @author Marcia
 */
public class ClassUtils {

    
	/**
	 * Este método obtém uma lista com o nome de todas as classes
	 * que fazem parte da hierarquia de uma determinada classe.
	 * Incluindo a própria classe
	 * @param entityClass
	 * @return
	 */
	public static List<Class<?>> getAllHierarchy(Class<?> entityClass) {
		List<Class<?>> ancestorClasses = new ArrayList<Class<?>>();
		Class<?> superClass = entityClass;
		while (superClass != Object.class){
			ancestorClasses.add(superClass);
			superClass = superClass.getSuperclass();
		}
		return ancestorClasses;
	}
	
	/**
	 * Este método verifica se uma classe se encontra na hierarquia de outra 
	 */
	public static boolean isAssignable(Class<?> entityClass, Class<?> toEntityClass) {
		return org.apache.commons.lang.ClassUtils.isAssignable(entityClass, toEntityClass);
	}
	
	public static void main(String[] args) {
		
		System.out.println(org.apache.commons.lang.ClassUtils.getAllSuperclasses(AuditCrudRegister.class));
		System.out.println(getAllHierarchy(AuditCrudRegister.class));
		System.out.println(AuditCrudRegister.class);
		System.out.println(org.apache.commons.lang.ClassUtils.isAssignable(AuditCrudRegister.class, AuditRegister.class));
	}
}
