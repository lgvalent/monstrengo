package br.com.orionsoft.monstrengo.crud.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.crud.entity.BusinessEntity;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.EntityList;
import br.com.orionsoft.monstrengo.crud.entity.EntitySet;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;
import br.com.orionsoft.monstrengo.crud.entity.IEntitySet;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDaoManager;
import br.com.orionsoft.monstrengo.crud.entity.dvo.IDvoManager;
import br.com.orionsoft.monstrengo.crud.entity.metadata.EntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IMetadataHandle;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataException;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.crud.services.QueryService;

/**
 * TODO DOCUMENTAR essa classe
 *
 * @spring.bean id="EntityManager"
 *
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="metadataHandle" ref="MetadataHandle"
 * @spring.property name="daoManager" ref="DaoManager"
 * @spring.property name="dvoManager" ref="DvoManager"
 * @author Lucio
 *
 */
public class EntityManager implements IEntityManager
{
    public static final String MANAGER_NAME = "EntityManager";

    /**
     * Cache dos metadados de entidades já instanciados.
     */
    private Map<String, IEntityMetadata> entitiesMetadata = null;

    /**
     * Manipulador de metadados instanciado externamente e passado para o
     * serviço.
     */
    private IMetadataHandle metadataHandle;

    private IServiceManager serviceManager;

    private IDaoManager daoManager;

    private IDvoManager dvoManager;

    /**
     * Este método cria uma entidade de negócio baseado no objeto fornecido.
     *
     * @param object
     *            Objeto persistido que será transformado em uma entidade de
     *            negócio.
     * @return Retorna uma entidade de negócio com seus dados e metadados.
     */
    public <T> IEntity<T> getEntity(Object object) throws EntityException
    {
        try
        {
            return new BusinessEntity<T>(object, getEntityMetadata(object.getClass()), this);
        } catch (EntityException e)
        {
            // Adiciona a mensagem de erro da tarefa corrente
            e.getErrorList().addAll(
                    MessageList.create(EntityException.class, "ERROR_CONVERTING_OBJECT_ENTITY",
                            object.getClass().getName()));
            throw new EntityException(e.getErrorList());
        }
    }

    /**
     * Este método retorna uma instância do metadado da classe solicitada. Para
     * otimizar, ele controla uma cache de metadados já instanciados.
     *
     * @return
     */
    public IEntityMetadata getEntityMetadataDefaults(Class<?> entityClass) throws EntityException
    {
		/* Obtem os metadados defaults da entidade solicitada */
		IEntityMetadata entity;
		// Criando
		try
		{
			entity = new EntityMetadata(entityClass, this, true);
		} 
		catch (MetadataException e)
		{
			// Adiciona a mensagem de erro da tarefa corrente
			e.getErrorList().addAll(
					MessageList.create(EntityException.class, "ERROR_GETTING_METADATA",
							entityClass.getName()));
			throw new EntityException(e.getErrorList());
		}

        return entity;
    }

    /**
     * Este método retorna uma instância do metadado da classe solicitada. Para
     * otimizar, ele controla uma cache de metadados já instanciados.
     * 
     * @return
     */
    public IEntityMetadata getEntityMetadata(Class<?> entityClass) throws EntityException
    {
        // Tenta pegar uma instância no cache
        IEntityMetadata result = this.getEntitiesMetadata().get(entityClass.getSimpleName());

        if(result==null)
          throw new EntityException(MessageList.create(EntityException.class, "ERROR_ENTITY_METADATA_NOTFOUND",
        		  					entityClass.getName()));

        return result;
    }

    /**
     * Este método retorna uma instância do metadado da classe solicitada. Para
     * otimizar, ele controla uma cache de metadados já instanciados.
     *
     * @return
     */
    public Map<String, IEntityMetadata> getEntitiesMetadata() throws EntityException
    {
		if( this.entitiesMetadata == null){
			this.entitiesMetadata = new HashMap<String, IEntityMetadata>();

			// Obtem a lista de DAOs pelo DaoManager.
			// Percorre a lista dos DAOs registrados.
			for(IDAO<?> dao: this.getDaoManager().getDaos().values())
			{
				/* Obtem os metadados da entidade manipulada pelo atual DAO */
				IEntityMetadata entity;
				// Criando
				try
				{
					entity = new EntityMetadata(dao.getEntityClass(), this, false);
				} 
				catch (MetadataException e)
				{
					// Adiciona a mensagem de erro da tarefa corrente
					e.getErrorList().addAll(
							MessageList.create(EntityException.class, "ERROR_GETTING_METADATA",
									dao.getEntityClass().getName()));
					throw new EntityException(e.getErrorList());
				}

				// Guardando
				entitiesMetadata.put(dao.getEntityClass().getSimpleName(), entity);
			}
		}

        return this.entitiesMetadata;
    }
    
    /**
     * Este método permite limpar o cache de metadados que é mantido
     * pelo entity manager. Assim, quando houver alguma alteração
     * dos metados no banco, os mesmos poderão ser recarregados
     * utilizando este método para limpar as atuais instâncias.
     */
    public void refreshEntitiesMetadata(){
    	if(this.entitiesMetadata != null){
    		this.entitiesMetadata.clear();
    		this.entitiesMetadata = null;
    	}
    }

    /**
     * <p>Este método converte uma lista de objetos em uma lista de entidade.
     * <p>É realizada somente uma busca de metadados para toda a lista.
     *
     * @param classObj Tipo da classe de objetos que estão na lista.
     * @param list Lista de objetos persistidos que será transformada em uma lista de entidade de negócio.
     *
     * @return Retorna uma lista de entidade de negócio com seus dados e metadados.
     *
     */
    public <T> IEntityList<T> getEntityList(List<T> list, Class<T> classObj) throws EntityException
    {
        try
        {
            // Obtem o metadados da classe
            IEntityMetadata entityMetadata = getEntityMetadata(classObj);

            // Cria uma lista de Entidades
            IEntityList<T> result = new EntityList<T>(list, entityMetadata, this);

            // Converte a lista de objetos para lista de IEntity
            // !!! A lista é convertida pelo próprio objeto EntitySet
            // for(Object obj: list)
            //    result.add(new BusinessEntity(obj, entityMetadata, this));
            // Converte a lista de objetos para lista de IEntity

            return result;

        } catch (BusinessException e)
        {
            // Adiciona a mensagem de erro da tarefa corrente
            e.getErrorList().addAll(MessageList.create(EntityException.class, "ERROR_CONVERTING_LIST_OBJECT_ENTITY", classObj.getName()));
            throw new EntityException(e.getErrorList());
        }
    }

    /**
     * <p>Este método converte um conjunto de objetos em um cojunto de entidade.
     * <p>É realizada somente uma busca de metadados para toda o conjunto.
     *
     * @param classObj Tipo da classe de objetos que estão na lista.
     * @param set Lista de objetos persistidos que será transformada em uma lista de entidade de negócio.
     *
     * @return Retorna uma lista de entidade de negócio com seus dados e metadados.
     *
     */
    public <T> IEntitySet<T> getEntitySet(Set<T> set, Class<T> classObj) throws EntityException
    {
        try
        {
            // Obtem o metadados da classe
            IEntityMetadata entityMetadata = getEntityMetadata(classObj);

            // Cria um conjunto de Entidades
            IEntitySet<T> result = new EntitySet<T>(set, entityMetadata, this);

            // Converte a lista de objetos para lista de IEntity
            // !!! A lista é convertida pelo próprio objeto EntitySet
            // for(Object obj: set)
            //    result.add(new BusinessEntity(obj, entityMetadata, this));

            return result;

        } catch (BusinessException e)
        {
            // Adiciona a mensagem de erro da tarefa corrente
            e.getErrorList().addAll(MessageList.create(EntityException.class, "ERROR_CONVERTING_LIST_OBJECT_ENTITY", classObj.getName()));
            throw new EntityException(e.getErrorList());
        }
    }

    public IMetadataHandle getMetadataHandle(){return metadataHandle;}
    public void setMetadataHandle(IMetadataHandle metadataHandle){this.metadataHandle = metadataHandle;}

    public void setServiceManager(IServiceManager serviceManager){this.serviceManager = serviceManager;}
    public IServiceManager getServiceManager(){return this.serviceManager;}

    public void setDaoManager(IDaoManager daoManager){this.daoManager = daoManager;}
    public IDaoManager getDaoManager(){return this.daoManager;}

	public List<SelectItem> getEntitySelectItems(Class<?> classObj, String hqlWhereExp) throws EntityException {
		List<SelectItem> result;
		try {
			/* Tenta definir uma ordem padrão */
			IEntityMetadata entMeta = this.getEntityMetadata(classObj);

			// Obtem uma lista de entidades através do ListService da entidade
			ServiceData listData = new ServiceData(ListService.SERVICE_NAME, null);
			listData.getArgumentList().setProperty(ListService.CLASS, classObj);

			/* Verificando se a propriedade não é uma collection */
			IPropertyMetadata[] props = entMeta.getProperties();
			int index = 0;
			do{index++;}while(props[index].isCollection() && (index<props.length));
			
			/* Verifica se o while parou por não ter achado uma propriedade e define a .id como padrão */
			if((index==props.length)) index = entMeta.getPropertyMetadata(IDAO.PROPERTY_ID_NAME).getIndex();
				
			/* Ordena pela segunda propriedade da classe */
			if(StringUtils.isEmpty(hqlWhereExp))
				listData.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, "true=true order by " + IDAO.ENTITY_ALIAS_HQL + "." + props[index].getName());
			else
				if(hqlWhereExp.toLowerCase().contains("order by"))
					listData.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, hqlWhereExp);
				else
					listData.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, hqlWhereExp + " order by " + IDAO.ENTITY_ALIAS_HQL + "." + props[index].getName());
				
			this.getServiceManager().execute(listData);

			IEntityList<?> entities = (IEntityList<?>) listData.getFirstOutput();

			result = new ArrayList<SelectItem>(entities.size());
			for(IEntity<?> entity: entities)
			{
				result.add( new SelectItem(entity.getId(), entity.toString()));
			}

			/* Nao pode inserir um item vazio, porque senao darah a impressao
			 * de que a lista achou algum item valido. Assim, ao verificar se a
			 * lista stah vazia sempre havera um item, porem invalido  - Lucio 08/05/2007 */
//			/* verifica se o resultado está vazio para adicionar um item vazio*/
//			if(result.isEmpty())
//				result.add( new SelectItem(IDAO.ENTITY_UNSAVED, ""));

			return result;
		} catch (BusinessException e) {
			throw new EntityException(e.getErrorList());
		}
	}

	public List<SelectItem> queryEntitySelectItems(Class<?> classObj, String filter, int maxResult) throws EntityException {
		List<SelectItem> result;
		try {
			/* Tenta definir uma ordem padrão */
			IEntityMetadata entMeta = this.getEntityMetadata(classObj);

			// Obtem uma lista de entidades através do ListService da entidade
			ServiceData queryData = new ServiceData(QueryService.SERVICE_NAME, null);
			queryData.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, classObj);
			queryData.getArgumentList().setProperty(QueryService.IN_QUERY_FILTER, filter);
			queryData.getArgumentList().setProperty(QueryService.IN_MAX_RESULT_OPT, maxResult);

			/* Verificando se a propriedade não é uma collection */
			IPropertyMetadata[] props = entMeta.getProperties();
			int index = 0;
			do{index++;}while(props[index].isCollection() && (index<props.length));
			
			/* Verifica se o while parou por não ter achado uma propriedade e define a .id como padrão */
			if((index==props.length)) index = entMeta.getPropertyMetadata(IDAO.PROPERTY_ID_NAME).getIndex();
				
			/* Ordena pela segunda propriedade da classe */
			queryData.getArgumentList().setProperty(QueryService.IN_QUERY_HQLWHERE, " true=true ORDER BY " + IDAO.ENTITY_ALIAS_HQL + "." + props[index].getName());
				
			this.getServiceManager().execute(queryData);

			IEntityList<?> entities = (IEntityList<?>) queryData.getFirstOutput();

			result = new ArrayList<SelectItem>(entities.size());
			for(IEntity<?> entity: entities)
			{
				result.add( new SelectItem(entity.getId(), entity.toString()));
			}

			return result;
		} catch (BusinessException e) {
			throw new EntityException(e.getErrorList());
		}
	}

	public IEntityList<?> queryEntities(Class<?> classObj, String filter, String staticHqlWhereFilter, int maxResult) throws EntityException {
		IEntityList<?> result;
		try {
			/* Tenta definir uma ordem padrão */
			IEntityMetadata entMeta = this.getEntityMetadata(classObj);

			// Obtem uma lista de entidades através do ListService da entidade
			ServiceData queryData = new ServiceData(QueryService.SERVICE_NAME, null);
			queryData.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, classObj);
			queryData.getArgumentList().setProperty(QueryService.IN_QUERY_FILTER, filter);
			queryData.getArgumentList().setProperty(QueryService.IN_QUERY_HQLWHERE, staticHqlWhereFilter);
			queryData.getArgumentList().setProperty(QueryService.IN_MAX_RESULT_OPT, maxResult);

			/* Verificando se a propriedade não é uma collection */
			IPropertyMetadata[] props = entMeta.getProperties();
			int index = 0;
			do{index++;}while(props[index].isCollection() && (index<props.length));
			
			/* Verifica se o while parou por não ter achado uma propriedade e define a .id como padrão */
			if((index==props.length)) index = entMeta.getPropertyMetadata(IDAO.PROPERTY_ID_NAME).getIndex();
				
			/* Ordena pela segunda propriedade da classe */
			queryData.getArgumentList().setProperty(QueryService.IN_ORDER_EXPRESSION_OPT, IDAO.ENTITY_ALIAS_HQL + "." + props[index].getName());
				
			this.getServiceManager().execute(queryData);

			result = (IEntityList<?>) queryData.getFirstOutput();

			return result;
		} catch (BusinessException e) {
			throw new EntityException(e.getErrorList());
		}
	}

	public IDvoManager getDvoManager() {return dvoManager;}
	public void setDvoManager(IDvoManager dvoManager) {this.dvoManager = dvoManager;}

	public List<IEntityMetadata> getSubEntitiesMetadata(Class<?> entityClass) throws EntityException
	{
		List<IEntityMetadata> result = null;
		if(result == null){
    		result = new ArrayList<IEntityMetadata>(4);
    		for(Class<?> klazz: this.getEntityMetadata(entityClass).getSubEntities())
    			result.add(this.getEntityMetadata(klazz));
    	}
    	return result;
	}
}
