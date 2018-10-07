package br.com.orionsoft.monstrengo.core.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditCrudRegister;
import br.com.orionsoft.monstrengo.crud.labels.entities.ModelLabel;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Classe que fornece m�todos utilit�rios para tratamento de propriedades
 * das entidades.
 * Implementa alguns recursos que n�o s�o encontrados na classe
 * PropertyUtils do projeto jakarta-commons.
 * Aqui � usado a t�cnica de analisar DIRETAMENTE no m�todo get. Isto porque, h� 
 * propriedades Transientes que n�o tem um Field correspondente:)
 * @author Lucio 20060202
 * @version 20110621
 */
public abstract class PropertyUtils {
	
	/**
	 * Este m�todo procura na entidade o m�todo de leitura do propriedade solicitada.
	 * Se n�o achar um get() tenta achar um is(). Sen�o retorna null.
	 * @param entityClass
	 * @param propName
	 * @return
	 */
	public static Method getPropertyReadMethod(Class<?> entityClass, String propName) {
			try {
				return entityClass.getMethod("get" + propName.toUpperCase().charAt(0) + propName.substring(1));
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (NoSuchMethodException e) {
				try {
					/* Tenta pegar o m�todo de propriedade L�gica */
					return entityClass.getMethod("is" + propName.toUpperCase().charAt(0) + propName.substring(1));
				} catch (Exception e1) {
					return null;
				}
			}
	}
	
	

    /**
     * Este m�todo retorna o tipo da propriedade de uma classe baseando-se somente no m�todo GET
     * @param entityClass Classe que ser� analisada
     * @param propName Nome da propriedade que ser� buscada dentro da 
     * classe para encontrar seu tipo. 
     * @return Retorna a classe da propriedade se encontrado, ou null se n�o encontrada
     */
	public static Class<?> getPropertyType(Class<?> entityClass, String propName) {
		/* Procura em todos os declaredField da hierarquia */
//		System.out.println("======>"+ entityClass + ":" + propName);
		try {
			Type t = getPropertyReadMethod(entityClass, propName).getGenericReturnType();
			if (t instanceof ParameterizedType)
				return (Class<?>)((ParameterizedType) t).getRawType();
			return (Class<?>) t;
		} catch (Exception e) {
			e.printStackTrace();
			/* N�o faz nada, deixa percorrer a hierarquia */
		}
		return null;
	}
    
    /**
     * Este m�todo retorna o ParameterizedType para controlar as defini��es de Generics
     * Com esta classe, � poss�vel acessar os tipos gen�ricos definidos na propriedade de uma classe.
     * Fonte: http://joe.truemesh.com/blog//000495.html
     * ParameterizedType.getRawType(): retorna o tipo Raw que contem a defini��o Generics
     * ParameterizedType.getActualTypeArguments()[0]: retorna um vetor com os tipos pametrizados.
     * Exemplo: Map<String,Integer> f = null; f possui dois argumentos generics 
     * @param entityClass Classe que ser� analisada
     * @param propName Nome da propriedade que ser� buscada dentro da 
     * classe para encontrar seu tipo. 
     * @return Retorna a classe da propriedade se encontrado, ou null se n�o encontrada
     */
    public static ParameterizedType getPropertyGenericsDeclaration(Class<?> entityClass, String propName) {
    	try {
    		Type f = getPropertyReadMethod(entityClass, propName).getGenericReturnType();
    		if (f!=null)
    			return (ParameterizedType) f;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }   
    
    /**
     * Este m�todo retorna o valor da propriedade 
     * @param entityClass Classe que ser� analisada
     * @param propName Nome da propriedade que ser� buscada dentro da 
     * classe para encontrar seu tipo. 
     * @return Retorna a classe da propriedade se encontrado, ou null se n�o encontrada
     */
    public static Object getPropertyValue(Object entity, String propName) {
    	try {
    		Method m = getPropertyReadMethod(entity.getClass(), propName);
    		if (m!=null)
    			return m.invoke(entity);
    		return null;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }   
    
    

    public static void main(String[] args) {
    	System.out.println(getPropertyType(ApplicationUser.class, ApplicationUser.SECURITY_GROUPS));
    	System.out.println(getPropertyGenericsDeclaration(ApplicationUser.class, ApplicationUser.SECURITY_GROUPS).getActualTypeArguments()[0]);
		System.out.println(getPropertyType(ModelLabel.class, ModelLabel.LINES_LABEL));
		System.out.println(getPropertyType(AuditCrudRegister.class, AuditCrudRegister.DESCRIPTION));
		System.out.println("isSet="+getPropertyType(ApplicationUser.class, ApplicationUser.SECURITY_GROUPS).isAssignableFrom(Set.class));
		System.out.println("isList="+getPropertyType(ApplicationUser.class, ApplicationUser.SECURITY_GROUPS).isAssignableFrom(List.class));
//		System.out.println("isSet="+isSet(ApplicationUser.class, ApplicationUser.SECURITY_GROUPS));
//		System.out.println("isList="+isList(ApplicationUser.class, ApplicationUser.SECURITY_GROUPS));
//		System.out.println("isCollection="+isCollection(ApplicationUser.class, ApplicationUser.SECURITY_GROUPS));
	}
}
