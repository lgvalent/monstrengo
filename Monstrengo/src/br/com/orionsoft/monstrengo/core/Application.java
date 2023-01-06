package br.com.orionsoft.monstrengo.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.core.IApplication;
import br.com.orionsoft.monstrengo.core.util.AnnotationUtils;

/**
* @spring.bean id="Application"
* @spring.property name="entityManager" value="[br.com.orionsoft.monstrengo]"
*/
public class Application implements IApplication {

    protected Logger log = LogManager.getLogger(getClass());

    /** Define a lista com o nome dos pacotes dos módulos instalados */
    String[] modulesPackages;

    public String[] getModulesPackages() {return modulesPackages;}
	public void setModulesPackages(String[] modulesPackages) {this.modulesPackages = modulesPackages;}
	
	public String extractModuleName(Class<?> klazz) {
		for(String str: modulesPackages){
			if(klazz.getName().startsWith(str))
				return str;
		}

		throw new RuntimeException("Nenhum módulo foi registrado para a classe:" + klazz.getName() + ". Verifique se esta classe pertence a algum módulo registrado no applicatonContext.xml");
	}
	@SuppressWarnings("unchecked")
	public <T> List<Class<? extends T>> findModulesClasses(Class<T>... classes){
		List<Class<? extends T>> result = new ArrayList<Class<? extends T>>();
		/* Prepara as entidades que implementam IRunnableEntityProcess */
		for (String module : modulesPackages){
			log.info("Procurando " + classes[0].getSimpleName() + (classes.length==2?", "+classes[1].getSimpleName():"") + " no módulo: " + module);
			for(String className: AnnotationUtils.findClassesNames(module, classes)){
				log.info("   Encontrado: " + className);
						try {
							result.add((Class<? extends T>) Class.forName(className));
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
			}
		}
		
		return result;
	}

}
