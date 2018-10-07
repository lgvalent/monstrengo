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
 * Esta classe fornece diversos m�todos �teis na manipula��o 
 * das entidades de seguran�a.
 * <br>Seu principal objetivo � facilitar a obten��o de informa��es sobre o
 * sistema de seguran�a, diminuindo o c�digo digitado. 
 * 
 * @author Lucio 2005/10/26
 * @version 2005/11/04
 *
 */
public class UtilsSecurity
{
    /* Credencial de usu�rio WEB de internet utilizado para autenticar
     * os processo web que n�o precisam de um usu�rio espec�fico, mas que usam
     * toda a infraestrutura de seguran�a */
	private static ApplicationUser applicationUserWeb = null; 
	public static ApplicationUser getApplicationUserWeb(){
		if(applicationUserWeb!=null)
			return applicationUserWeb;
		
        // Retorna os dados do usu�rio web (login, password)
    	ApplicationUser user = new ApplicationUser();
    	user.setLogin("Internet");
    	user.setPassword("Internet");
    	
    	return user;
    }

	
	/**
     * Este m�todo recupera um processo do m�dulo de seguran�a atrav�s do nome e obt�m uma entidade. 
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel. 
     * @param process Define a inst�ncia do processo que ser� pesquisado o Id.
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @return Retorna uma entidade do tipo IEntity do processo definido no m�dulo de seguran�a.
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
     * Este m�todo recupera um processo do m�dulo de seguran�a atrav�s do nome e obt�m o id. 
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel. 
     * @param process Define a inst�ncia do processo que ser� pesquisado o Id.
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @return Retorna o Id do processo definido no m�dulo de seguran�a.
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final long retrieveProcessId(IServiceManager svcMgr,String processName, ServiceData serviceDataOwner) throws BusinessException
    {
        return retrieveProcessEntity(svcMgr, processName, serviceDataOwner).getId();
    }
    
    /**
     * Este m�todo recupera uma entidade do m�dulo de seguran�a atrav�s do nome. 
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel. 
     * @param entityType Define o tipo da entidade que ser� pesquisado o Id.
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @return Retorna uma entidade do tipo entidade da aplica��o definido no m�dulo de seguran�a.
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
     * Este m�todo recupera uma entidade do m�dulo de seguran�a atrav�s do nome e obt�m o seu id. 
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel. 
     * @param entityType Define o tipo da entidade que ser� pesquisado o Id.
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @return Retorna o Id da entidade definido no m�dulo de seguran�a.
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final long retrieveEntityId(IServiceManager svcMgr,Class<?> entityType, ServiceData serviceDataOwner) throws BusinessException
    {
        return retrieveEntity(svcMgr, entityType, serviceDataOwner).getId();
    }
    
    /**
     * Esse m�todo � um atalho para o servi�o de checagem de direitos sobre processos  
     * 
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel
     * @param process Define a inst�ncia do processo � ser verificado
     * @param userSession Define a inst�ncia da sess�o do usu�rio que est� tentando executar o processo
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @return Retorna "true" se o usu�rio possui direito de execu��o do processo e "false" caso contr�rio. 
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
     * Esse m�todo � um atalho para o servi�o de checagem de direitos crud sobre entidades
     * Checa as permiss�es de cria��o, recupera��o, atualiza��o e exclus�o de uma entidade
     * 
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel
     * @param entityType Define o tipo da entidade � ser verificada
     * @param userSession Define a sess�o do usu�rio para verifica��o se este possui ou n�o direitos  
     *          CRUD de uma inst�ncia do tipo de entidade especificado
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @return Retorna um map contendo como chave o nome da opera��o e como valor se o usu�rio possui 
     *          ou n�o direito de executar a opera��o.
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
     * Esse m�todo � um atalho para o servi�o de checagem de direitos crud sobre entidades, 
     * Checa apenas a permiss�o de cria��o de uma entidade
     * 
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel
     * @param entityType Define o tipo da entidade � ser verificada
     * @param userSession Define a sess�o do usu�rio para verifica��o se este possui ou n�o direitos  
     *          criar uma inst�ncia do tipo de entidade especificado
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @return Retorna "true" se o usu�rio possui direito de cria��o da entidade e "false" caso contr�rio
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final boolean checkRightCreate(IServiceManager svcMgr, Class<?> entityType, UserSession userSession, ServiceData serviceDataOwner) throws BusinessException
    {
        return UtilsSecurity.checkRightCrud(svcMgr, entityType, userSession, serviceDataOwner).get(CheckRightCrudService.CAN_CREATE);
    }
    
    /**
     * Esse m�todo � um atalho para o servi�o de checagem de direitos crud sobre entidades
     * Checa apenas a permiss�o de recupera��o de uma entidade
     * 
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel
     * @param entityType Define o tipo da entidade � ser verificada
     * @param userSession Define a sess�o do usu�rio para verifica��o se este possui ou n�o direitos  
     *          recuperar uma inst�ncia do tipo de entidade especificado
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @return Retorna "true" se o usu�rio possui direito de recupera��o da entidade e "false" caso contr�rio
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final boolean checkRightRetrieve(IServiceManager svcMgr, Class<?> entityType, UserSession userSession, ServiceData serviceDataOwner) throws BusinessException
    {
        return UtilsSecurity.checkRightCrud(svcMgr, entityType, userSession, serviceDataOwner).get(CheckRightCrudService.CAN_RETRIEVE);
    }

    /**
     * Esse m�todo � um atalho para o servi�o de checagem de direitos crud sobre entidades
     * Checa apenas a permiss�o de atualiza��o de uma entidade
     * 
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel
     * @param entiData tyType Define o tipo da entidade � ser verificada
     * @param userSession Define a sess�o do usu�rio para verifica��o se este possui ou n�o direitos  
     *          atualizar uma inst�ncia do tipo de entidade especificado
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @return Retorna "true" se o usu�rio possui direito de atualiza��o da entidade e "false" caso contr�rio
     * @throws BusinessException
     * @since 2005/11/04
     * 
     */
    public static final boolean checkRightUpdate(IServiceManager svcMgr, Class<?> entityType, UserSession userSession, ServiceData serviceDataOwner) throws BusinessException
    {
        return UtilsSecurity.checkRightCrud(svcMgr, entityType, userSession, serviceDataOwner).get(CheckRightCrudService.CAN_UPDATE);
    }

    /**
     * Esse m�todo � um atalho para o servi�o de checagem de direitos crud sobre entidades
     * Checa apenas a permiss�o de exclus�o de uma entidade
     * 
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel
     * @param entityType Define o tipo da entidade � ser verificada
     * @param userSession Define a sess�o do usu�rio para verifica��o se este possui ou n�o direitos  
     *          excluir uma inst�ncia do tipo de entidade especificado atrav�s do parametro entityId
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @return Retorna "true" se o usu�rio possui direito de exclus�o da entidade e "false" caso contr�rio
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final boolean checkRightDelete(IServiceManager svcMgr, Class<?> entityType, UserSession userSession, ServiceData serviceDataOwner) throws BusinessException
    {
        return UtilsSecurity.checkRightCrud(svcMgr, entityType, userSession, serviceDataOwner).get(CheckRightCrudService.CAN_DELETE);
    }
    
    /**
     * Esse m�todo � um atalho para o servi�o de checagem de direitos crud sobre entidades
     * Checa apenas a permiss�o de pesquisa de uma entidade
     * 
     * @param svcMgr Define a inst�ncia do gerenciador de servi�os dispon�vel
     * @param entityType Define o tipo da entidade � ser verificada
     * @param userSession Define a sess�o do usu�rio para verifica��o se este possui ou n�o direitos  
     *          excluir uma inst�ncia do tipo de entidade especificado atrav�s do parametro entityId
     * @param serviceDataOwner Define os dados do servi�o pai que solicitou a execu��o deste servi�o
     * @return Retorna "true" se o usu�rio possui direito de pesquisar da entidade e "false" caso contr�rio
     * @throws BusinessException
     * @since 2005/11/04
     */
    public static final boolean checkRightQuery(IServiceManager svcMgr, Class<?> entityType, UserSession userSession, ServiceData serviceDataOwner) throws BusinessException
    {
        return UtilsSecurity.checkRightCrud(svcMgr, entityType, userSession, serviceDataOwner).get(CheckRightCrudService.CAN_QUERY);
    }
}
