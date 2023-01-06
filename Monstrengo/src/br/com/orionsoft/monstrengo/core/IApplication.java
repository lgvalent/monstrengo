package br.com.orionsoft.monstrengo.core;

import java.util.List;


/**
 * Esta interface define todos os aspectos de gerenciamento e configuração
 * básica de uma aplicação.
 * Os managers devem ter uma referência a esta interface indicando em qual aplicação eles estão trabalhando.
 * Isto permite que ele obtenham metadados e descubram algo que alterem o seu comportamento.
 * 
 * Inicialmente, o principal dado da aplicação é a lista com o nome dos pacotes dos módulos.
 * Com esta lista, os gerenciados podem 'descobrir' classes a notadas e herdadas e instanciar automaticamente
 * seus objetos. Por exemplo, o DaoManager busca as @Entity e @Embeddable. O service manager precisa
 * saber os módulos da aplicação para procurar os @Service, e assim por diante.
 * 
 * @author Lucio
 * @since 20111121
 */
public interface IApplication {
    
	 /**
     * Mantém uma lista com os nomes (package) dos módulos que serão gerenciados pela aplicação.
     * @return
     */
    public String[] getModulesPackages();
	public void setModulesPackages(String[] modulesPackages);
	
	/**
	 * Recebe uma classe e extrai o nome do módulo que a classe pertence, baseando-se
	 * nos nomes dos módulos já registrados
	 * @param klazz
	 * @return
	 */
	public String extractModuleName(Class<?> klazz);   
	
	
	public <T> List<Class<? extends T>> findModulesClasses(Class<T>... klazz);
}