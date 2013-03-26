package br.com.orionsoft.monstrengo.auditorship.services;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditCrudRegister;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.UserSession;

/**
 * Servi�o de registro de auditoria de altera��es Crud.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_USER_SESSION: Inst�ncia da atual sess�o do usu�rio.
 * <br> IN_APPLICATION_ENTITY: Entidade de seguran�a que indica a entidade do sistema que ser� auditada.
 * <br> IN_ENTITY_ID: Identificador da inst�ncia da entidade que ser� auditada (LONG).
 * <br> IN_DESCRIPTION_STR: Descri��o adicional que ser� registrada na auditoria.
 * 
 * <p><b>Procedimento:</b>
 * <br>Cria um novo registro da auditoria.
 * <br>Preenche os dados do registro.
 * <br>Grava o registro.
 * <br><b>Retorna a nova entidade do registro.</b>
 * 
 * 
 * @author Lucio 2005/11/04
 * @version 2005/12/22
 * 
 * @spring.bean id="AuditorCrudService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class AuditorCrudService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "AuditorCrudService";
    
    /** Inst�ncia da atual sess�o do usu�rio. */
    public static String IN_USER_SESSION = "userSession";
    /** Entidade de seguran�a que indica a entidade do sistema que ser� auditada (IEntity)*/
    public static String IN_APPLICATION_ENTITY = "applicationEntity";
    /** Identificador da inst�ncia da entidade que ser� auditada (LONG). */
    public static String IN_ENTITY_ID = "entityId";
    /** Indica se a opera��o que est� sendo efetuada � de cria��o. (Boolean) */
    public static String IN_CREATED_BOOL_OPT = "created";
    /** Indica se a opera��o que est� sendo efetuada � de atualiza��o. (Boolean) */
    public static String IN_UPDATED_BOOL_OPT = "updated";
    /** Indica se a opera��o que est� sendo efetuada � de exclus�o. (Boolean) */
    public static String IN_DELETED_BOOL_OPT = "deleted";
    /** Descri��o adicional que ser� registrada na auditoria. (String) */
    public static String IN_DESCRIPTION_STR = "description";
    
    @SuppressWarnings("static-access")
    public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            log.debug("Iniciando a execu��o do servi�o AuditorCrudService");
            // Pega os argumentos
            UserSession userSession = (UserSession) serviceData.getArgumentList().getProperty(IN_USER_SESSION);
            IEntity appEntity = (IEntity) serviceData.getArgumentList().getProperty(IN_APPLICATION_ENTITY);
            long entityId = (Long) serviceData.getArgumentList().getProperty(IN_ENTITY_ID);
            String description = (String) serviceData.getArgumentList().getProperty(IN_DESCRIPTION_STR);
            
            // Opcionais
            boolean created = false;
            if (serviceData.getArgumentList().containsProperty(IN_CREATED_BOOL_OPT))
                created = (Boolean) serviceData.getArgumentList().getProperty(IN_CREATED_BOOL_OPT); 
            
            boolean updated = false;
            if (serviceData.getArgumentList().containsProperty(IN_UPDATED_BOOL_OPT))
                updated = (Boolean) serviceData.getArgumentList().getProperty(IN_UPDATED_BOOL_OPT); 
            
            boolean deleted = false;
            if (serviceData.getArgumentList().containsProperty(IN_DELETED_BOOL_OPT))
                deleted = (Boolean) serviceData.getArgumentList().getProperty(IN_DELETED_BOOL_OPT); 

            if (!(created || updated || deleted))
                throw new BusinessException(MessageList.createSingleInternalError(new Exception("Nenhuma opera��o crud foi definida")));
            // Cria um novo registro
            IEntity register = UtilsCrud.create(this.getServiceManager(), AuditCrudRegister.class, serviceData);

            /* Limita o tamanho da descri��o para o tamanho do banco, evitando erros do tipo:
             * Data truncation: Data too long for column 'description' at row 1 */
            description = StringUtils.substring(description, 0, register.getProperty(AuditCrudRegister.DESCRIPTION).getInfo().getSize());

            // Preenche o novo registro
            register.getProperty(AuditCrudRegister.OCURRENCY_DATE).getValue().setAsCalendar(Calendar.getInstance());
            
            /* Verifica se foi passado algum operador para identificar a auditoria */
            if(userSession != null && userSession.getUser()!=null)
            	register.getProperty(AuditCrudRegister.APPLICATION_USER).getValue().setAsEntity(userSession.getUser());
            
            register.getProperty(AuditCrudRegister.TERMINAL).getValue().setAsString(userSession.getTerminal());
            register.getProperty(AuditCrudRegister.APPLICATION_ENTITY).getValue().setAsEntity(appEntity);
            register.getProperty(AuditCrudRegister.ENTITY_ID).getValue().setAsLong(entityId);
            register.getProperty(AuditCrudRegister.CREATED).getValue().setAsBoolean(created);
            register.getProperty(AuditCrudRegister.UPDATED).getValue().setAsBoolean(updated);
            register.getProperty(AuditCrudRegister.DELETED).getValue().setAsBoolean(deleted);
            register.getProperty(AuditCrudRegister.DESCRIPTION).getValue().setAsString(description);
            
            // Grava o registro
            UtilsCrud.update(this.getServiceManager(), register, serviceData);
            
            // Adiciona o registro criado no resultado no serviceData
            serviceData.getOutputData().add(register);
            
        } 
        catch (BusinessException e)
        {
            // O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros.
            throw new ServiceException(e.getErrorList());
        }
    }
    
    public String getServiceName() {return SERVICE_NAME;}
}