package br.com.orionsoft.monstrengo.core;

import java.util.List;


/**
 * Esta interface define todos os aspectos de gerenciamento e configura��o
 * b�sica de uma aplica��o.
 * Os managers devem ter uma refer�ncia a esta interface indicando em qual aplica��o eles est�o trabalhando.
 * Isto permite que ele obtenham metadados e descubram algo que alterem o seu comportamento.
 * 
 * Inicialmente, o principal dado da aplica��o � a lista com o nome dos pacotes dos m�dulos.
 * Com esta lista, os gerenciados podem 'descobrir' classes a notadas e herdadas e instanciar automaticamente
 * seus objetos. Por exemplo, o DaoManager busca as @Entity e @Embeddable. O service manager precisa
 * saber os m�dulos da aplica��o para procurar os @Service, e assim por diante.
 * 
 * @author Lucio
 * @since 20111121
 */
public interface IApplication {
    
	 /**
     * Mant�m uma lista com os nomes (package) dos m�dulos que ser�o gerenciados pela aplica��o.
     * @return
     */
    public String[] getModulesPackages();
	public void setModulesPackages(String[] modulesPackages);
	
	/**
	 * Recebe uma classe e extrai o nome do m�dulo que a classe pertence, baseando-se
	 * nos nomes dos m�dulos j� registrados
	 * @param klazz
	 * @return
	 */
	public String extractModuleName(Class<?> klazz);   
	
	
	public <T> List<Class<? extends T>> findModulesClasses(Class<T>... klazz);
}