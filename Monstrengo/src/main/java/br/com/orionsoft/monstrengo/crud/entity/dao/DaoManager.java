package br.com.orionsoft.monstrengo.crud.entity.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBuilder;

import br.com.orionsoft.monstrengo.crud.entity.dao.DAOException;
import br.com.orionsoft.monstrengo.crud.entity.dao.DaoBasic;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDaoManager;
import br.com.orionsoft.monstrengo.core.IApplication;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.util.AnnotationUtils;

/**
 * Classe gerenciadora de daos que armazena todos os daos da aplica��o
 * e pode localizar o dao correspondente a uma determinada classe.
 * 
 * @author Marcia
 * @version 20060203
 * @spring.bean id="DaoManager"
 * @spring.property name="sessionFactoryBuilder" ref="SessionFactory"
 * @spring.property name="application" ref="Application"
 */
public class DaoManager implements IDaoManager
{
    protected Logger log = LogManager.getLogger(getClass());

    // Lista de Daos dispon�veis e registrados
    private Map<String, IDAO<?>> daos = null;
    
    AnnotationSessionFactoryBuilder sessionFactoryBuilder;
    
	private Map<Class<?>, List<Class<?>>> subEntities = new HashMap<Class<?>, List<Class<?>>>();

	private SessionFactory sessionFactory;

	private IApplication application;

	public AnnotationSessionFactoryBuilder getSessionFactoryBuilder() {return sessionFactoryBuilder;}
	public void setSessionFactoryBuilder(AnnotationSessionFactoryBuilder sessionFactoryBuilder) {this.sessionFactoryBuilder = sessionFactoryBuilder;}

	public IApplication getApplication() {return application;}
	public void setApplication(IApplication application) {this.application = application;}
    
	
	/**
	 * Este m�todo cria a lista de DAOs e busca todas as entidades anotadas no sistema
	 * para criar um dao manipulador para esta entidade.
	 * A lista de DAOs auxilia o restante da arquitetura a saber quantas entidades s�o mantidas, ou seja,
	 * quantas entidades s�o CRUD
	 */
	public void init(){
		if(daos != null)
			throw new RuntimeException("DaoManager j� inciado anteriormente. O m�todo init() n�o pode ser executado.");
		
		daos = new HashMap<String, IDAO<?>>();
		
		/* Prepara as entidades anotadas com @Entity */
		List<String> annotatedClassesNames = new ArrayList<String>();
		for (String module : this.getApplication().getModulesPackages()){
			log.info("Registrando o m�dulo da aplica��o: " + module);
			annotatedClassesNames.addAll(AnnotationUtils.findAnnotatedClassesNames(module, Entity.class, Embeddable.class));
		}
		Class<?>[] annotatedClasses = new Class<?>[annotatedClassesNames.size()];
			for (int i = 0; i < annotatedClasses.length; i++){
				try {
					String className = annotatedClassesNames.get(i);
					log.info("         Classe: " + className);
					annotatedClasses[i] = Class.forName(className);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		sessionFactoryBuilder.setAnnotatedClasses(annotatedClasses);
		
		/* Aplica a nova f�brica de sess�o */
		try {
			sessionFactory = sessionFactoryBuilder.buildSessionFactory();
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}

		for(Class<?> entityClass: annotatedClasses){

			@SuppressWarnings("rawtypes")
			IDAO<?> dao = new DaoBasic(entityClass, this, null){};

			try{
				if(log.isDebugEnabled()) log.debug("Registrando DAO virtual para a entidade: " + entityClass.getName());
				registerDao(dao);

			}catch(DAOException e){
				throw new RuntimeException(e.getMessage());
			}
		}
	}
	
    /**
     * @return Returns the daos.
     */
	public Map<String, IDAO<?>> getDaos()
    {
    	if(daos == null){
    		init();
    	}
        return daos;
    }

    /**
     * @param daos The daos to insert.
     */
    public void registerDao(IDAO<?> dao) throws DAOException
    {
        IDAO<?> duplicated = this.getDaos().get(dao.getEntityClass().getSimpleName());
        if (duplicated != null)
        {
            throw new DAOException(MessageList.create(DAOException.class, "DUPLICATED_DAO", dao.getEntityClassName(), duplicated.getEntityClassName(),  dao.getEntityClassName()));
        }
        Class<?> clas = dao.getEntityClass();
        
        this.getDaos().put(clas.getSimpleName(), dao);
        
        /* Verifica se a classe do DAO � extendida e se registra como uma extens�o */
        Class<?> superClas = clas.getSuperclass();
        if(superClas != Object.class){
        	if(!subEntities.containsKey(superClas)){
				if(log.isDebugEnabled()) log.debug("Registrando subEntity "+ clas.getSimpleName() + " para a entidade " + superClas);
        		subEntities.put(superClas, new ArrayList<Class<?>>(5));
        	}
        	subEntities.get(superClas).add(clas);
        }
    }

    /**
     * @param daos The daos to remove.
     * @throws DAOException 
     */
    public void unregisterDao(IDAO<?> dao) throws DAOException
    {
        if (!this.getDaos().containsKey(dao.getEntityClass().getSimpleName()))
        {
            throw new DAOException(MessageList.create(DAOException.class, "DAO_NOT_FOUND", dao.getEntityClassName()));
        }
        
        this.getDaos().remove(dao.getEntityClass().getSimpleName());
    }

    /**
     * Obtem a inst�ncia do DAO respons�vel para tratar a classe de objetos passada.
     * 
     * @param daos Classe do objeto que se requer o Dao.
     */
    @SuppressWarnings("unchecked")
	public <E> IDAO<E> getDaoByEntity(String entityClassName) throws DAOException
    {
        try {
			return (IDAO<E>) getDaoByEntity(Class.forName(entityClassName));
		} catch (ClassNotFoundException e) {
            throw new DAOException(MessageList.create(DAOException.class, "DAO_NOT_FOUND", entityClassName));
		}
    }    
    /**
     * Obtem a inst�ncia do DAO respons�vel para tratar a classe de objetos passada.
     * 
     * @param daos Classe do objeto que se requer o Dao.
     * @since 20060203
     */
    @SuppressWarnings("unchecked")
	public <E> IDAO<E> getDaoByEntity(Class<E> klazz) throws DAOException
    {
    	
    	if (!this.getDaos().containsKey(klazz.getSimpleName()))
        {
            throw new DAOException(MessageList.create(DAOException.class, "DAO_NOT_FOUND", klazz.getSimpleName()));
        }
        
        return (IDAO<E>) this.getDaos().get(klazz.getSimpleName());

    }

    public List<Class<?>> getSubEntities(Class<?> klazz) throws DAOException {
    	if(!subEntities.containsKey(klazz))
    		return new ArrayList<Class<?>>(0);
    		
    	// TODO Auto-generated method stub
    	return subEntities.get(klazz);
    }
    
	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

}
