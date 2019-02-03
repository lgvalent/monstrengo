package br.com.orionsoft.monstrengo.crud.entity.dao;

import java.util.List;

import javax.persistence.Entity;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateTemplate;

import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.util.AnnotationUtils;

/**
 * Classe abstrata que especifica as operações executadas pelos daos.
 * Todas as subclasses deverão implementar o método getEntityClass() que retornará
 * o tipo de entidade específico.
 * @author Marcia
 * @version 2005/03/10
 */
public abstract class DaoBasic<T> implements IDAO<T>
{
    protected IDaoManager daoManager;

    private Class<T> entityClass;
    
    private Boolean embeddable = null;

    public DaoBasic(Class<T> entityClass, IDaoManager daoManager, HibernateTemplate hibernateTemplate) {
    	this.entityClass = entityClass;
    	this.daoManager = daoManager;
	}

    public Class<T> getEntityClass() {return entityClass;}
    
    public boolean isEmbeddable() {
    	if(embeddable==null){
    		embeddable = AnnotationUtils.findAnnotation(Entity.class, entityClass) == null;
    	}
    	
    	return embeddable;
    }
    
    public boolean isPersistent() {
    	return !isEmbeddable();
    }

	/**
     * @return Returns the entityClassName.
     */
    public String getEntityClassName(){return getEntityClass().getName();}

    public IDaoManager getDaoManager(){return daoManager;}

    public void setDaoManager(IDaoManager daoManager){this.daoManager = daoManager;}
   
    public void registerDao() throws Exception
    {
        try
        {
            daoManager.registerDao(this);
        }
        catch(DAOException e)
        {
            throw new Exception(e.getErrorList().get(0).getMessage());
        }
            
    }

    /**
     * @see IDAO#update(Object)
     */
    public void update(Object obj) throws DAOException
    {
        if(!isEmbeddable()){
        	/* O TypeCast é usado aqui para forçar uma validação de tipo e provocar um erro
        	 * caso seja passado um objeto que não seja do tipo especificado */
        	Session session = this.getDaoManager().getSessionFactory().openSession();
        	session.saveOrUpdate(this.getEntityClass().cast(obj));
        	session.flush();
        	session.close();
        }
    }       

    /**
     * Método utilizado para atividades que necessitam de transação e consequentemente 
     * dependem do sucesso de todas operações envolvidas nessa atividade. Com o fornecimento
     * da sessão o metodo executa a operação solicitada mas não faz a confirmação, ou seja, não
     * executa commit, podendo ser desfeita depois.
     *  
     * @see IDAO#update(Object)
     */
    public void update(Object obj, Session session) throws DAOException, HibernateException
    {
        if(!isEmbeddable()){
        	session.saveOrUpdate(this.getEntityClass().cast(obj));
        }
    }
    
    /**
     * @see IDAO#getList(String)
     */
    @SuppressWarnings("unchecked")
	public List<T> getList(String condicao) throws DAOException
    {
        try
        {
            // Adiciona a cláusula FROM à HQL
            condicao = "FROM " + getEntityClassName() + " " + IDAO.ENTITY_ALIAS_HQL + " WHERE " + condicao;
        	Session session = this.getDaoManager().getSessionFactory().openSession();
        	List<T> list = session.createQuery(condicao).list();
        	session.flush();
        	session.close();
            return list;
        }
        catch(DataAccessException e)
        {
            DAOException exception = new DAOException(MessageList.createSingleInternalError(e));
            
            exception.getErrorList().addAll(MessageList.create(DAOException.class, "ERROR_GETTING_LIST", condicao));
            
            throw exception; 
        }
    }
    
    @SuppressWarnings("unchecked")
	public List<T> getList() throws DAOException
    {
    	Session session = this.getDaoManager().getSessionFactory().openSession();
    	List<T> list = session.createCriteria(getEntityClass()).list();
    	session.flush();
    	session.close();
    	return list;
    }
    
    /**
     * @see IDAO#retrieve(long)
     */
    @SuppressWarnings("unchecked")
    public T retrieve(long pk) throws DAOException
    {
        Long id = new Long(pk);
        
    	Session session = this.getDaoManager().getSessionFactory().openSession();
		T result = (T) session.get(this.getEntityClass(), id);
    	session.flush();
    	session.close();
        
        // Verifica se foi encontrado o objecto com o id
        if (result == null)
            throw new DAOException(MessageList.create(DAOException.class, "OBJECT_NOT_FOUND", this.getEntityClass().getName(), Long.toString(id)));
        
        return result;
    }

    /**
     * @see IDAO#delete(Object)
     */
    public void delete(Object obj) throws DAOException
    {
        if(!isEmbeddable()){
        	Session session = this.getDaoManager().getSessionFactory().openSession();
        	session.delete(obj);
        	session.flush();
        	session.close();
        }
    }

    /**
     * @see IDAO#create()
     */
    public T create() throws DAOException
    {
        T result;
        try
        {
            result = getEntityClass().newInstance();
            PropertyUtils.setProperty(result, IDAO.PROPERTY_ID_NAME, IDAO.ENTITY_UNSAVED);
        } catch (Exception e)
        {
            DAOException exception = new DAOException(MessageList.createSingleInternalError(e));
            
            exception.getErrorList().addAll(MessageList.create(DAOException.class, "ERROR_GETTING_NEW", getEntityClass().getName()));
            
            throw exception; 
        }
        
        return result;
    }


}