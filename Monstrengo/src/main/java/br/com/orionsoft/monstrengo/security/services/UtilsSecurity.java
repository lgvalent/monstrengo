package br.com.orionsoft.monstrengo.security.services;

import java.util.HashMap;
import java.util.Map;

import br.com.orionsoft.monstrengo.security.services.CheckRightCrudService;
import br.com.orionsoft.monstrengo.security.services.CheckRightProcessService;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurityException;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationProcess;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.UserSession;

/**
 * Esta classe fornece diversos métodos úteis na manipulação 
 * das entidades de segurança.
 * <br>Seu principal objetivo é facilitar a obtenção de informações sobre o
 * sistema de segurança, diminuindo o código digitado. 
 * 
 * @author Lucio 2005/10/26
 * @version 2005/11/04
 *
 */
public class UtilsSecurity
{
    /* Credencial de usuário WEB de internet utilizado para autenticar
     * os processo web que não precisam de um usuário específico, mas que usam
     * toda a infraestrutura de segurança */
	private static ApplicationUser applicationUserWeb = null; 
	public static ApplicationUser getApplicationUserWeb(){
		if(applicationUserWeb!=null)
			return applicationUserWeb;
		
        // Retorna os dados do usuário web (login, password)
    	ApplicationUser user = new ApplicationUser();
    	user.setLogin("Internet");
    	user.setPassword("Internet");
    	
    	return user;
    }

	
	/**
     * Este método recupera um processo do módulo de segurança através do nome e obtém uma entidade. 
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param process Define a instância do processo que será pesquisado o Id.
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Retorna uma entidade do tipo IEntity do processo definido no módulo de segurança.
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final IEntity<ApplicationProcess> retrieveProcessEntity(IServiceManager svcMgr,String processName, ServiceData serviceDataOwner) throws BusinessException
    {
        ServiceData sdL = new ServiceData(ListService.SERVICE_NAME, serviceDataOwner);
        sdL.getArgumentList().setProperty(ListService.CLASS, ApplicationProcess.class);
        sdL.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, ApplicationProcess.NAME + "= '" + processName + "'");
        svcMgr.execute(sdL);
        
        if ( sdL.<IEntityList<ApplicationProcess>>getFirstOutput().size()==0)
            throw new BusinessException(MessageList.create(UtilsSecurityException.class, "PROCESS_NOT_FOUND", processName));
        
        return sdL.<IEntityList<ApplicationProcess>>getFirstOutput().get(0);
    }
    
    /**
     * Este método recupera um processo do módulo de segurança através do nome e obtém o id. 
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param process Define a instância do processo que será pesquisado o Id.
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Retorna o Id do processo definido no módulo de segurança.
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final long retrieveProcessId(IServiceManager svcMgr,String processName, ServiceData serviceDataOwner) throws BusinessException
    {
        return retrieveProcessEntity(svcMgr, processName, serviceDataOwner).getId();
    }
    
    /**
     * Este método recupera uma entidade do módulo de segurança através do nome. 
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param entityType Define o tipo da entidade que será pesquisado o Id.
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Retorna uma entidade do tipo entidade da aplicação definido no módulo de segurança.
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final IEntity<ApplicationEntity> retrieveEntity(IServiceManager svcMgr,Class<?> entityType, ServiceData serviceDataOwner) throws BusinessException
    {
            ServiceData sdL = new ServiceData(ListService.SERVICE_NAME, serviceDataOwner);  
            sdL.getArgumentList().setProperty(ListService.CLASS, ApplicationEntity.class);
            sdL.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, ApplicationEntity.NAME + "= '" + entityType.getSimpleName() + "'");
            svcMgr.execute(sdL);
            
            IEntityList<ApplicationEntity> list = sdL.getFirstOutput();

            if (list.size()==0) 
                throw new BusinessException(MessageList.create(UtilsSecurityException.class, "ENTITY_NOT_FOUND", entityType.getName()));
            
        return list.get(0);
    }
    
    /**
     * Este método recupera uma entidade do módulo de segurança através do nome e obtém o seu id. 
     * @param svcMgr Define a instância do gerenciador de serviços disponível. 
     * @param entityType Define o tipo da entidade que será pesquisado o Id.
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Retorna o Id da entidade definido no módulo de segurança.
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final long retrieveEntityId(IServiceManager svcMgr,Class<?> entityType, ServiceData serviceDataOwner) throws BusinessException
    {
        return retrieveEntity(svcMgr, entityType, serviceDataOwner).getId();
    }
    
    /**
     * Esse método é um atalho para o serviço de checagem de direitos sobre processos  
     * 
     * @param svcMgr Define a instância do gerenciador de serviços disponível
     * @param process Define a instância do processo à ser verificado
     * @param userSession Define a instância da sessão do usuário que está tentando executar o processo
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Retorna "true" se o usuário possui direito de execução do processo e "false" caso contrário. 
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final boolean checkRightProcess(IServiceManager svcMgr, String processName, UserSession userSession, ServiceData serviceDataOwner) throws BusinessException
    {
        
        ServiceData sv = new ServiceData(CheckRightProcessService.SERVICE_NAME, serviceDataOwner);
        sv.getArgumentList().setProperty(CheckRightProcessService.IN_PROCESS_NAME_OPT, processName);
        sv.getArgumentList().setProperty(CheckRightProcessService.IN_USER_OPT, userSession.getUser());
        
        svcMgr.execute(sv);
        
        return (Boolean)sv.getFirstOutput();
    }

    /**
     * Esse método é um atalho para o serviço de checagem de direitos crud sobre entidades
     * Checa as permissões de criação, recuperação, atualização e exclusão de uma entidade
     * 
     * @param svcMgr Define a instância do gerenciador de serviços disponível
     * @param entityType Define o tipo da entidade à ser verificada
     * @param userSession Define a sessão do usuário para verificação se este possui ou não direitos  
     *          CRUD de uma instância do tipo de entidade especificado
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Retorna um map contendo como chave o nome da operação e como valor se o usuário possui 
     *          ou não direito de executar a operação.
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final Map<String, Boolean> checkRightCrud(IServiceManager svcMgr, Class<?> entityType, UserSession userSession, ServiceData serviceDataOwner) throws BusinessException
    {
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        
        ServiceData sv = new ServiceData(CheckRightCrudService.SERVICE_NAME, serviceDataOwner);
        sv.getArgumentList().setProperty(CheckRightCrudService.IN_ENTITY_ID, UtilsSecurity.retrieveEntityId(svcMgr, entityType, serviceDataOwner));
        sv.getArgumentList().setProperty(CheckRightCrudService.IN_USER_ID_OPT, userSession.getUser().getId());
        
        svcMgr.execute(sv);
        
        
        result.put(CheckRightCrudService.CAN_CREATE, (Boolean)sv.getOutputData(CheckRightCrudService.OUT_CREATE));
        result.put(CheckRightCrudService.CAN_RETRIEVE, (Boolean)sv.getOutputData(CheckRightCrudService.OUT_RETRIEVE));
        result.put(CheckRightCrudService.CAN_UPDATE, (Boolean)sv.getOutputData(CheckRightCrudService.OUT_UPDATE));
        result.put(CheckRightCrudService.CAN_DELETE, (Boolean)sv.getOutputData(CheckRightCrudService.OUT_DELETE));
        result.put(CheckRightCrudService.CAN_QUERY, (Boolean)sv.getOutputData(CheckRightCrudService.OUT_QUERY));
        
        return result;
    }
    
    /**
     * Esse método é um atalho para o serviço de checagem de direitos crud sobre entidades, 
     * Checa apenas a permissão de criação de uma entidade
     * 
     * @param svcMgr Define a instância do gerenciador de serviços disponível
     * @param entityType Define o tipo da entidade à ser verificada
     * @param userSession Define a sessão do usuário para verificação se este possui ou não direitos  
     *          criar uma instância do tipo de entidade especificado
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Retorna "true" se o usuário possui direito de criação da entidade e "false" caso contrário
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final boolean checkRightCreate(IServiceManager svcMgr, Class<?> entityType, UserSession userSession, ServiceData serviceDataOwner) throws BusinessException
    {
        return UtilsSecurity.checkRightCrud(svcMgr, entityType, userSession, serviceDataOwner).get(CheckRightCrudService.CAN_CREATE);
    }
    
    /**
     * Esse método é um atalho para o serviço de checagem de direitos crud sobre entidades
     * Checa apenas a permissão de recuperação de uma entidade
     * 
     * @param svcMgr Define a instância do gerenciador de serviços disponível
     * @param entityType Define o tipo da entidade à ser verificada
     * @param userSession Define a sessão do usuário para verificação se este possui ou não direitos  
     *          recuperar uma instância do tipo de entidade especificado
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Retorna "true" se o usuário possui direito de recuperação da entidade e "false" caso contrário
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final boolean checkRightRetrieve(IServiceManager svcMgr, Class<?> entityType, UserSession userSession, ServiceData serviceDataOwner) throws BusinessException
    {
        return UtilsSecurity.checkRightCrud(svcMgr, entityType, userSession, serviceDataOwner).get(CheckRightCrudService.CAN_RETRIEVE);
    }

    /**
     * Esse método é um atalho para o serviço de checagem de direitos crud sobre entidades
     * Checa apenas a permissão de atualização de uma entidade
     * 
     * @param svcMgr Define a instância do gerenciador de serviços disponível
     * @param entiData tyType Define o tipo da entidade à ser verificada
     * @param userSession Define a sessão do usuário para verificação se este possui ou não direitos  
     *          atualizar uma instância do tipo de entidade especificado
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Retorna "true" se o usuário possui direito de atualização da entidade e "false" caso contrário
     * @throws BusinessException
     * @since 2005/11/04
     * 
     */
    public static final boolean checkRightUpdate(IServiceManager svcMgr, Class<?> entityType, UserSession userSession, ServiceData serviceDataOwner) throws BusinessException
    {
        return UtilsSecurity.checkRightCrud(svcMgr, entityType, userSession, serviceDataOwner).get(CheckRightCrudService.CAN_UPDATE);
    }

    /**
     * Esse método é um atalho para o serviço de checagem de direitos crud sobre entidades
     * Checa apenas a permissão de exclusão de uma entidade
     * 
     * @param svcMgr Define a instância do gerenciador de serviços disponível
     * @param entityType Define o tipo da entidade à ser verificada
     * @param userSession Define a sessão do usuário para verificação se este possui ou não direitos  
     *          excluir uma instância do tipo de entidade especificado através do parametro entityId
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Retorna "true" se o usuário possui direito de exclusão da entidade e "false" caso contrário
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final boolean checkRightDelete(IServiceManager svcMgr, Class<?> entityType, UserSession userSession, ServiceData serviceDataOwner) throws BusinessException
    {
        return UtilsSecurity.checkRightCrud(svcMgr, entityType, userSession, serviceDataOwner).get(CheckRightCrudService.CAN_DELETE);
    }
    
    /**
     * Esse método é um atalho para o serviço de checagem de direitos crud sobre entidades
     * Checa apenas a permissão de pesquisa de uma entidade
     * 
     * @param svcMgr Define a instância do gerenciador de serviços disponível
     * @param entityType Define o tipo da entidade à ser verificada
     * @param userSession Define a sessão do usuário para verificação se este possui ou não direitos  
     *          excluir uma instância do tipo de entidade especificado através do parametro entityId
     * @param serviceDataOwner Define os dados do serviço pai que solicitou a execução deste serviço
     * @return Retorna "true" se o usuário possui direito de pesquisar da entidade e "false" caso contrário
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final boolean checkRightQuery(IServiceManager svcMgr, Class<?> entityType, UserSession userSession, ServiceData serviceDataOwner) throws BusinessException
    {
        return UtilsSecurity.checkRightCrud(svcMgr, entityType, userSession, serviceDataOwner).get(CheckRightCrudService.CAN_QUERY);
    }
}
