package br.com.orionsoft.monstrengo.crud.services;

import java.util.List;

import br.com.orionsoft.monstrengo.crud.services.CreateService;
import br.com.orionsoft.monstrengo.crud.services.DeleteService;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.crud.services.RetrieveService;
import br.com.orionsoft.monstrengo.crud.services.UpdateService;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;

/**
 * Esta classe fornece diversos métodos úteis na execução de serviços Crud.
 * <br>Seu principal objetivo é facilitar a execução destes serviços, diminuindo o 
 * código digitado. 
 * 
 * @author Lucio 2005/10/25
 * @version 2005/10/25
 *
 */
public class UtilsCrud
{
    /**
     * Este método cria uma nova instância de uma entidade (IEntity). 
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param klazz Define o tipo da entidade que será criada.
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Retorna uma nova instância de uma entidade.
     * @throws BusinessException
     */
    public static final <T> IEntity<T> create(IServiceManager svcMgr,Class<T> klazz, ServiceData serviceDataOwner) throws BusinessException
    {
        ServiceData sdC = new ServiceData(CreateService.SERVICE_NAME, serviceDataOwner);
        sdC.getArgumentList().setProperty(CreateService.IN_ENTITY_TYPE, klazz);
        svcMgr.execute(sdC);
        
        return sdC.<IEntity<T>>getFirstOutput();
    }
    
    /**
     * Este método cria uma nova instância de um Objeto. 
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param klazz Define o tipo da entidade que será criada.
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Retorna uma nova instância de uma entidade.
     * @throws BusinessException
     */
    public static final <T> T objectCreate(IServiceManager svcMgr,Class<T> klazz, ServiceData serviceDataOwner) throws BusinessException
    {
        ServiceData sdC = new ServiceData(CreateService.SERVICE_NAME, serviceDataOwner);
        sdC.getArgumentList().setProperty(CreateService.IN_ENTITY_TYPE, klazz);
        svcMgr.execute(sdC);
        
        return sdC.<IEntity<T>>getFirstOutput().getObject();
    }
    
    /**
     * Este método recupera a instância de uma entidade com o Id fornecido. 
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param klazz Define o tipo da entidade que será recuperada.
     * @param id Define o Id da entidade que será recuperada.
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Retorna a instância da entidade com o Id fornecido.
     * @throws BusinessException
     */
    public static final <T> IEntity<T> retrieve(IServiceManager svcMgr,Class<T> klazz, long id, ServiceData serviceDataOwner) throws BusinessException
    {
        ServiceData sdR = new ServiceData(RetrieveService.SERVICE_NAME, serviceDataOwner);
        sdR.getArgumentList().setProperty(RetrieveService.CLASS, klazz);
        sdR.getArgumentList().setProperty(RetrieveService.ID_LONG, id);
        svcMgr.execute(sdR);
        
        return sdR.<IEntity<T>>getFirstOutput();
    }
    
    /**
     * Este método recupera a instância de uma entidade com o Id fornecido.
     * Retornando um object
     *  
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param klazz Define o tipo da entidade que será recuperada.
     * @param id Define o Id da entidade que será recuperada.
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Retorna a instância da entidade com o Id fornecido.
     * @throws BusinessException
     */
    public static final <T> T objectRetrieve(IServiceManager svcMgr, Class<T> klazz, long id, ServiceData serviceDataOwner) throws BusinessException
    {
        ServiceData sdR = new ServiceData(RetrieveService.SERVICE_NAME, serviceDataOwner);
        sdR.getArgumentList().setProperty(RetrieveService.CLASS, klazz);
        sdR.getArgumentList().setProperty(RetrieveService.ID_LONG, id);
        svcMgr.execute(sdR);
        
        return sdR.<IEntity<T>>getFirstOutput().getObject();
    }
    
    /**
     * Este método atualiza a instância de uma entidade.
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param entity Define a entidade que será atualizada.
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @throws BusinessException
     */
    public static final void update(IServiceManager svcMgr,IEntity<?> entity, ServiceData serviceDataOwner) throws BusinessException
    {
        ServiceData sdU = new ServiceData(UpdateService.SERVICE_NAME, serviceDataOwner);
        sdU.getArgumentList().setProperty(UpdateService.IN_ENTITY, entity);
        svcMgr.execute(sdU);
    }
    
    /**
     * Este método atualiza a instância de um objeto.
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param object Define o objeto que será atualizado.
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @throws BusinessException
     */
    public static final void objectUpdate(IServiceManager svcMgr,Object object, ServiceData serviceDataOwner) throws BusinessException
    {
        /* Converte o objeto em uma entidade para utilizar o mesmo padrão de persistência */
    	IEntity<?> entity = svcMgr.getEntityManager().getEntity(object);
    	
    	ServiceData sdU = new ServiceData(UpdateService.SERVICE_NAME, serviceDataOwner);
        sdU.getArgumentList().setProperty(UpdateService.IN_ENTITY, entity);
        svcMgr.execute(sdU);
    }
    
    /**
     * Este método exclui a instância de uma entidade.
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param entity Define a entidade que será excluída.
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @throws BusinessException
     */
    public static final void delete(IServiceManager svcMgr,IEntity<?> entity, ServiceData serviceDataOwner) throws BusinessException
    {
        ServiceData sdD = new ServiceData(DeleteService.SERVICE_NAME, serviceDataOwner);
        sdD.getArgumentList().setProperty(DeleteService.IN_ENTITY, entity);
        svcMgr.execute(sdD);
    }
    
    /**
     * Este método exclui a instância de um objeto.
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param object Define o objeto que será excluído.
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @throws BusinessException
     */
    public static final void objectDelete(IServiceManager svcMgr,Object object, ServiceData serviceDataOwner) throws BusinessException
    {
        /* Converte o objeto em uma entidade para utilizar o mesmo padrão de persistência */
    	IEntity<?> entity = svcMgr.getEntityManager().getEntity(object);

    	ServiceData sdD = new ServiceData(DeleteService.SERVICE_NAME, serviceDataOwner);
        sdD.getArgumentList().setProperty(DeleteService.IN_ENTITY, entity);
        svcMgr.execute(sdD);
    }
    
    /**
     * Este método obtém uma lista de todas as instâncias de uma entidade.
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param klazz Define o tipo de entidade da lista.
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Lista de entidades (do tipo IEntityList) da classe fornecida
     * @throws BusinessException
     */
    public static final <T> IEntityList<T> list(IServiceManager svcMgr, Class<T> klazz, ServiceData serviceDataOwner) throws BusinessException
    {
        ServiceData sdL = new ServiceData(ListService.SERVICE_NAME, serviceDataOwner);
        sdL.getArgumentList().setProperty(ListService.CLASS, klazz);
        svcMgr.execute(sdL);

        return sdL.getFirstOutput();
    }
    
    /**
     * Este método obtém uma lista de todas as instâncias de um objeto.
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param klazz Define o tipo de entidade da lista.
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Lista de objetos da classe fornecida
     * @throws BusinessException
     */
    public static final <T> List<T> objectList(IServiceManager svcMgr, Class<T> klazz, ServiceData serviceDataOwner) throws BusinessException
    {
        ServiceData sdL = new ServiceData(ListService.SERVICE_NAME, serviceDataOwner);
        sdL.getArgumentList().setProperty(ListService.CLASS, klazz);
        svcMgr.execute(sdL);

        return sdL.<IEntityList<T>>getFirstOutput().getObjectList();
    }
    
    /**
     * Este método obtém uma lista de todas as instâncias de uma entidade.
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param klazz Define o tipo de entidade da lista.
     * @param condiction Condição de filtragem para ser aplicada na lista.
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Lista de entidades (do tipo IEntityList) da classe fornecida
     * @throws BusinessException
     */
    public static final <T> IEntityList<T> list(IServiceManager svcMgr, Class<T> klazz, String condiction, ServiceData serviceDataOwner) throws BusinessException
    {
        ServiceData sdL = new ServiceData(ListService.SERVICE_NAME, serviceDataOwner);
        sdL.getArgumentList().setProperty(ListService.CLASS, klazz);
        sdL.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, condiction);
        svcMgr.execute(sdL);

        return sdL.<IEntityList<T>>getFirstOutput();
    }
    
    /**
     * Este método obtém uma lista de todos os objetos.
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param klazz Define o tipo de entidade da lista.
     * @param condiction Condição de filtragem para ser aplicada na lista.
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Lista de entidades (do tipo IEntityList) da classe fornecida
     * @throws BusinessException
     */
    public static final <T> List<T> objectList(IServiceManager svcMgr, Class<T> klazz, String condiction, ServiceData serviceDataOwner) throws BusinessException
    {
        ServiceData sdL = new ServiceData(ListService.SERVICE_NAME, serviceDataOwner);
        sdL.getArgumentList().setProperty(ListService.CLASS, klazz);
        sdL.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, condiction);
        svcMgr.execute(sdL);

        return sdL.<IEntityList<T>>getFirstOutput().getObjectList();
    }
    
}
