package br.com.orionsoft.monstrengo.auditorship.services;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.auditorship.services.AuditorCrudService;
import br.com.orionsoft.monstrengo.auditorship.services.AuditorProcessService;
import br.com.orionsoft.monstrengo.auditorship.services.AuditorServiceService;
import br.com.orionsoft.monstrengo.auditorship.entities.AuditServiceRegister;
import br.com.orionsoft.monstrengo.auditorship.support.EntityAuditValue;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationProcess;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;

/**
 * Esta classe fornece diversos métodos úteis na manipulação 
 * da auditoria.
 * <br>Seu principal objetivo é facilitar a obtenção de informações sobre o
 * sistema de auditoria, diminuindo o código digitado. 
 * 
 * @author Lucio 2005/11/04
 * @version 2005/11/04
 *
 */
public class UtilsAuditorship
{
    
    /**
     * Este método registra uma auditoria de uma entidade.
     * Se não houver alterações não haverá descrição e nenhum registro
     * será criado na auditoria. O retorno do método será um objeto nulo. 
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param userSession Define a instânciada atual sessão do usuário. 
     * @param entityType Define o tipo da entidade que será auditada.
     * @param entityId Define o Id da instância da entidade que será auditada.
     * @param entityAuditValue Indica o objeto de controle de alteração dos valores.
     * @return Retorna uma entidade do tipo IEntity do registro auditado, ou nulo se 
     * não houve auditoria.
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final IEntity auditUpdate(IServiceManager svcMgr, UserSession userSession, EntityAuditValue entityAuditValue, ServiceData serviceDataOwner) throws BusinessException
    {
        
        // Prepara a Descrição da auditoria
        String description="";
        if (entityAuditValue != null)
            description = entityAuditValue.retrieveAuditDescription();
        
        // Se não houver alterações não grava registro
        if (StringUtils.isEmpty(description)) 
        	return null;

        // Prepara as entidades persistidas de appProcess e appEntity 
        IEntity appEntity = UtilsSecurity.retrieveEntity(svcMgr, entityAuditValue.getEntity().getInfo().getType(), serviceDataOwner);
        
        // Executa o serviço 
        ServiceData sdA = new ServiceData(AuditorCrudService.SERVICE_NAME, serviceDataOwner);
        sdA.getArgumentList().setProperty(AuditorCrudService.IN_USER_SESSION, userSession);
        sdA.getArgumentList().setProperty(AuditorCrudService.IN_APPLICATION_ENTITY, appEntity);
        sdA.getArgumentList().setProperty(AuditorCrudService.IN_ENTITY_ID, entityAuditValue.getEntity().getId());
        sdA.getArgumentList().setProperty(AuditorCrudService.IN_UPDATED_BOOL_OPT, true);
        sdA.getArgumentList().setProperty(AuditorCrudService.IN_DESCRIPTION_STR, description);
        svcMgr.execute(sdA);
        
        // Retorna o registro criado 
        return (IEntity) sdA.getFirstOutput();
    }
    
    /**
     * Este método registra uma auditoria de criaçao de uma entidade.
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param userSession Define a instânciada atual sessão do usuário. 
     * @param entity Define a entidade que acabou de ser criada e sera registrada na auditoria
     * @return Retorna uma entidade do tipo IEntity do registro auditado, ou nulo se 
     * não houve auditoria.
     * @throws BusinessException
     * @since 2006/02/03
     * @version 2007/04/10
     */
    public static final IEntity auditCreate(IServiceManager svcMgr, UserSession userSession, IEntity entity, ServiceData serviceDataOwner) throws BusinessException
    {
        
        // Prepara a Descrição da auditoria
        String description=entity.toString();
        
        // Prepara as entidades persistidas de appProcess e appEntity 
        IEntity appEntity = UtilsSecurity.retrieveEntity(svcMgr, entity.getInfo().getType(), serviceDataOwner);
        
        // Executa o serviço 
        ServiceData sdA = new ServiceData(AuditorCrudService.SERVICE_NAME, serviceDataOwner);
        sdA.getArgumentList().setProperty(AuditorCrudService.IN_USER_SESSION, userSession);
        sdA.getArgumentList().setProperty(AuditorCrudService.IN_APPLICATION_ENTITY, appEntity);
        sdA.getArgumentList().setProperty(AuditorCrudService.IN_ENTITY_ID, entity.getId());
        sdA.getArgumentList().setProperty(AuditorCrudService.IN_CREATED_BOOL_OPT, true);
        sdA.getArgumentList().setProperty(AuditorCrudService.IN_DESCRIPTION_STR, description);
        svcMgr.execute(sdA);
        
        // Retorna o registro criado 
        return (IEntity) sdA.getFirstOutput();
    }
    
    /**
     * Este método registra uma auditoria de uma entidade. 
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param userSession Define a instânciada atual sessão do usuário. 
     * @param entityType Define o tipo da entidade que será auditada.
     * @param entityId Define o Id da instância da entidade que será auditada.
     * @param justification Define a descrição adicional da auditoria.
     * @return Retorna uma entidade do tipo IEntity do registro auditado.
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final IEntity auditDelete(IServiceManager svcMgr, UserSession userSession, Class entityType, long entityId, String justification, ServiceData serviceDataOwner) throws BusinessException
    {
        
        // Prepara as entidades persistidas de appProcess e appEntity 
        IEntity appEntity = UtilsSecurity.retrieveEntity(svcMgr, entityType, serviceDataOwner);
        
        // Executa o serviço 
        ServiceData sdA = new ServiceData(AuditorCrudService.SERVICE_NAME, serviceDataOwner);
        sdA.getArgumentList().setProperty(AuditorCrudService.IN_USER_SESSION, userSession);
        sdA.getArgumentList().setProperty(AuditorCrudService.IN_APPLICATION_ENTITY, appEntity);
        sdA.getArgumentList().setProperty(AuditorCrudService.IN_ENTITY_ID, entityId);
        sdA.getArgumentList().setProperty(AuditorCrudService.IN_DELETED_BOOL_OPT, true);
        sdA.getArgumentList().setProperty(AuditorCrudService.IN_DESCRIPTION_STR, justification);
        svcMgr.execute(sdA);
        
        // Retorna o registro criado 
        return (IEntity) sdA.getFirstOutput();
    }

    /**
     * Este método registra uma auditoria de uma entidade. 
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param userSession Define a instânciada atual sessão do usuário. 
     * @param process Define a instância do processo que será auditado.
     * @param params Define a descrição adicional da auditoria.
     * @return Retorna uma entidade do tipo IEntity do registro auditado.
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final IEntity auditProcess(IProcess process, String params, ServiceData serviceDataOwner) throws BusinessException
    {
        
        // Prepara as entidades persistidas de appProcess e appEntity 
        IEntity appProcess = UtilsCrud.retrieve(process.getProcessManager().getServiceManager(), ApplicationProcess.class, process.getProcessInfo().getId(), serviceDataOwner);
        
        // Executa o serviço 
        ServiceData sdA = new ServiceData(AuditorProcessService.SERVICE_NAME, serviceDataOwner);
        sdA.getArgumentList().setProperty(AuditorProcessService.USER_SESSION, process.getUserSession());
        sdA.getArgumentList().setProperty(AuditorProcessService.IN_APPLICATION_PROCESS, appProcess);
        sdA.getArgumentList().setProperty(AuditorProcessService.DESCRIPTION_STR, params);
        process.getProcessManager().getServiceManager().execute(sdA);
        
        // Retorna o registro criado 
        return (IEntity) sdA.getFirstOutput();
    }

    /**
     * Este método registra uma auditoria para um serviço. 
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param userSession Define a instânciada atual sessão do usuário. 
     * @param serviceData Define a instância do processo que será auditado.
     * @param params Define a descrição adicional da auditoria.
     * @return Retorna uma entidade do tipo IEntity do registro auditado.
     * @throws BusinessException
     * @since 2005/11/04
     */
    @SuppressWarnings("unchecked")
	public static final IEntity<AuditServiceRegister> auditService(ServiceData serviceData, String terminal, ApplicationUser applicationUser, String description, IServiceManager serviceManager, ServiceData serviceDataOwner) throws BusinessException
    {
        
        
        // Executa o serviço 
        ServiceData sdA = new ServiceData(AuditorServiceService.SERVICE_NAME, serviceDataOwner);
        sdA.getArgumentList().setProperty(AuditorServiceService.IN_SERVICE_DATA, serviceData);
        sdA.getArgumentList().setProperty(AuditorServiceService.IN_APPLICATION_USER_OPT, applicationUser);
        sdA.getArgumentList().setProperty(AuditorServiceService.IN_TERMINAL, terminal);
        sdA.getArgumentList().setProperty(AuditorServiceService.IN_DESCRIPTION_STR, description);
        serviceManager.execute(sdA);
        
        // Retorna o registro criado 
        return (IEntity<AuditServiceRegister>) sdA.getFirstOutput();
    }
   
    
}
