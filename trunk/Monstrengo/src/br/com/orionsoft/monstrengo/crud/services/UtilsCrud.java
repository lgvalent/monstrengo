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
 * Esta classe fornece diversos m�todos �teis na execu��o de servi�os Crud.
 * <br>Seu principal objetivo � facilitar a execu��o destes servi�os, diminuindo o 
 * c�digo digitado. 
 * 
 * @author Lucio 2005/10/25
 * @version 2005/10/25
 *
 */
public class UtilsCrud
{
    /**
     * Este m�todo cria uma nova inst�ncia de uma entidade (IEntity). 
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel. 
     * @param klazz Define o tipo da entidade que ser� criada.
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @return Retorna uma nova inst�ncia de uma entidade.
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
     * Este m�todo cria uma nova inst�ncia de um Objeto. 
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel. 
     * @param klazz Define o tipo da entidade que ser� criada.
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @return Retorna uma nova inst�ncia de uma entidade.
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
     * Este m�todo recupera a inst�ncia de uma entidade com o Id fornecido. 
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel. 
     * @param klazz Define o tipo da entidade que ser� recuperada.
     * @param id Define o Id da entidade que ser� recuperada.
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @return Retorna a inst�ncia da entidade com o Id fornecido.
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
     * Este m�todo recupera a inst�ncia de uma entidade com o Id fornecido.
     * Retornando um object
     *  
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel. 
     * @param klazz Define o tipo da entidade que ser� recuperada.
     * @param id Define o Id da entidade que ser� recuperada.
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @return Retorna a inst�ncia da entidade com o Id fornecido.
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
     * Este m�todo atualiza a inst�ncia de uma entidade.
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel. 
     * @param entity Define a entidade que ser� atualizada.
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @throws BusinessException
     */
    public static final void update(IServiceManager svcMgr,IEntity<?> entity, ServiceData serviceDataOwner) throws BusinessException
    {
        ServiceData sdU = new ServiceData(UpdateService.SERVICE_NAME, serviceDataOwner);
        sdU.getArgumentList().setProperty(UpdateService.IN_ENTITY, entity);
        svcMgr.execute(sdU);
    }
    
    /**
     * Este m�todo atualiza a inst�ncia de um objeto.
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel. 
     * @param object Define o objeto que ser� atualizado.
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @throws BusinessException
     */
    public static final void objectUpdate(IServiceManager svcMgr,Object object, ServiceData serviceDataOwner) throws BusinessException
    {
        /* Converte o objeto em uma entidade para utilizar o mesmo padr�o de persist�ncia */
    	IEntity<?> entity = svcMgr.getEntityManager().getEntity(object);
    	
    	ServiceData sdU = new ServiceData(UpdateService.SERVICE_NAME, serviceDataOwner);
        sdU.getArgumentList().setProperty(UpdateService.IN_ENTITY, entity);
        svcMgr.execute(sdU);
    }
    
    /**
     * Este m�todo exclui a inst�ncia de uma entidade.
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel. 
     * @param entity Define a entidade que ser� exclu�da.
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @throws BusinessException
     */
    public static final void delete(IServiceManager svcMgr,IEntity<?> entity, ServiceData serviceDataOwner) throws BusinessException
    {
        ServiceData sdD = new ServiceData(DeleteService.SERVICE_NAME, serviceDataOwner);
        sdD.getArgumentList().setProperty(DeleteService.IN_ENTITY, entity);
        svcMgr.execute(sdD);
    }
    
    /**
     * Este m�todo exclui a inst�ncia de um objeto.
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel. 
     * @param object Define o objeto que ser� exclu�do.
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @throws BusinessException
     */
    public static final void objectDelete(IServiceManager svcMgr,Object object, ServiceData serviceDataOwner) throws BusinessException
    {
        /* Converte o objeto em uma entidade para utilizar o mesmo padr�o de persist�ncia */
    	IEntity<?> entity = svcMgr.getEntityManager().getEntity(object);

    	ServiceData sdD = new ServiceData(DeleteService.SERVICE_NAME, serviceDataOwner);
        sdD.getArgumentList().setProperty(DeleteService.IN_ENTITY, entity);
        svcMgr.execute(sdD);
    }
    
    /**
     * Este m�todo obt�m uma lista de todas as inst�ncias de uma entidade.
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel. 
     * @param klazz Define o tipo de entidade da lista.
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
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
     * Este m�todo obt�m uma lista de todas as inst�ncias de um objeto.
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel. 
     * @param klazz Define o tipo de entidade da lista.
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
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
     * Este m�todo obt�m uma lista de todas as inst�ncias de uma entidade.
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel. 
     * @param klazz Define o tipo de entidade da lista.
     * @param condiction Condi��o de filtragem para ser aplicada na lista.
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
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
     * Este m�todo obt�m uma lista de todos os objetos.
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel. 
     * @param klazz Define o tipo de entidade da lista.
     * @param condiction Condi��o de filtragem para ser aplicada na lista.
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
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
