package br.com.orionsoft.monstrengo.crud.entity.dvo;
/**
 * Created on 28/05/2007
 * @author Sergio
 */

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoManager;
import br.com.orionsoft.monstrengo.crud.entity.dvo.IDvo;
import br.com.orionsoft.monstrengo.crud.entity.dvo.IDvoManager;
import br.com.orionsoft.monstrengo.core.IApplication;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;

/**
 * Classe que implementa a interface IDvoManager. 
 * É responsável por gerenciar os dvo's, pois possui métodos 
 * que permitem registrar, remover
 * 
 * 
 * @spring.bean id="DvoManager" init-method="init"
 * @spring.property name="entityManager" ref="EntityManager"
 * 
 * @author sergio
 * @version 20070529
 */
public class DvoManager implements IDvoManager {
	
	/***
	 * Manipulador de Log para ser utilizado pelo gerenciador. 
	 */
	private Logger log = LogManager.getLogger(getClass());
	
    private Map<String, IDvo<?>> dvos;  
    private IEntityManager entityManager;

	private IApplication application;

	public IApplication getApplication() {return application;}
	public void setApplication(IApplication application) {this.application = application;}
    
	/**
	 * Este método cria a lista de DAOs e busca todas as entidades anotadas no sistema
	 * para criar um dao manipulador para esta entidade.
	 * A lista de DAOs auxilia o restante da arquitetura a saber quantas entidades são mantidas, ou seja,
	 * quantas entidades são CRUD
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void init(){
		if(dvos != null)
			throw new RuntimeException("DvoManager já iniciado anteriormente. O método init() não pode ser executado.");

		dvos = new HashMap<String, IDvo<?>>();

		for (Class<? extends IDvo> klazz: this.getApplication().findModulesClasses(IDvo.class)){
			log.info("Registrando DVO: " + klazz.getSimpleName());
			try {
				IDvo<?> dvo = (IDvo<?>) klazz.newInstance();
				dvo.setDvoManager(this);
				this.registerDvo(dvo);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

 
	public Map<String, IDvo<?>> getDvos(){
		return dvos;
	}
	/**
     * Obtem a instância do Dvo responsável para tratar a classe de objetos passada.
     * 
     * @param dvos Classe do objeto que se requer o Dvo.
     */
	@SuppressWarnings("unchecked")
	public <T> IDvo<T> getDvoByEntity(IEntity<T> entity) throws DvoException{
		final String entityTypeKey = entity.getInfo().getType().getName();

		if (!dvos.containsKey(entityTypeKey))
        {
            throw new DvoException(MessageList.create(DvoManager.class, "DVO_NOT_FOUND", entityTypeKey));
        }
        
		return (IDvo<T>) dvos.get(entityTypeKey);
	}
	/**
	 * Método de inicialização, usado para registrar o atual Dvo
	 * no seu respectivo gerenciador de Dvo's. 
	 * @throws DvoException
	 */
	
	public <T> void registerDvo(IDvo<T> dvo) throws DvoException {
		final String entityTypeKey = dvo.getEntityType().getName();
        
		/* Verifica se o dvo que quer ser registrado já se encontra na lista de dvos ativos */
		if (dvos.containsKey(entityTypeKey))
        {
			throw new DvoException(MessageList.create(DvoManager.class, "DUPLICATED_DVO", entityTypeKey));
        }
        
        if(log.isDebugEnabled())
        	log.debug("Registrando Dvo para a entidade: " + entityTypeKey);

        dvos.put(entityTypeKey, dvo);		
	}
	
	/**
	 * Método usado para remover o atual Dvo da lista de dvos ativos no seu gerenciador de Dvo's. 
	 */ 
	public <T> void unregisterDvo(IDvo<T> dvo) throws DvoException {
		final String entityTypeKey = dvo.getEntityType().getName();
		
		if (!dvos.containsKey(entityTypeKey))
        {
            throw new DvoException(MessageList.create(DvoManager.class, "DVO_NOT_FOUND", entityTypeKey));
        }
        
        if(log.isDebugEnabled())
        	log.debug("Removendo o Registrando Dvo para a entidade: " + entityTypeKey);

        dvos.remove(entityTypeKey); 
		
	}
	
	public boolean contains(IEntity<?> entity) throws DvoException {
		return dvos.containsKey(prepareEntityKey(entity));
	}
	
	
	public IEntityManager getEntityManager() {
		return entityManager;
	}
	
	public void setEntityManager(IEntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	private String prepareEntityKey(IEntity<?> entity){
		return entity.getInfo().getType().getName();
	}

}