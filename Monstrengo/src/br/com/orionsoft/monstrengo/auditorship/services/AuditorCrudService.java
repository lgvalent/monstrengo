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
 * Serviço de registro de auditoria de alterações Crud.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_USER_SESSION: Instância da atual sessão do usuário.
 * <br> IN_APPLICATION_ENTITY: Entidade de segurança que indica a entidade do sistema que será auditada.
 * <br> IN_ENTITY_ID: Identificador da instância da entidade que será auditada (LONG).
 * <br> IN_DESCRIPTION_STR: Descrição adicional que será registrada na auditoria.
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
    
    /** Instância da atual sessão do usuário. */
    public static String IN_USER_SESSION = "userSession";
    /** Entidade de segurança que indica a entidade do sistema que será auditada (IEntity)*/
    public static String IN_APPLICATION_ENTITY = "applicationEntity";
    /** Identificador da instância da entidade que será auditada (LONG). */
    public static String IN_ENTITY_ID = "entityId";
    /** Indica se a operação que está sendo efetuada é de criação. (Boolean) */
    public static String IN_CREATED_BOOL_OPT = "created";
    /** Indica se a operação que está sendo efetuada é de atualização. (Boolean) */
    public static String IN_UPDATED_BOOL_OPT = "updated";
    /** Indica se a operação que está sendo efetuada é de exclusão. (Boolean) */
    public static String IN_DELETED_BOOL_OPT = "deleted";
    /** Descrição adicional que será registrada na auditoria. (String) */
    public static String IN_DESCRIPTION_STR = "description";
    
    @SuppressWarnings("static-access")
    public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            log.debug("Iniciando a execução do serviço AuditorCrudService");
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
                throw new BusinessException(MessageList.createSingleInternalError(new Exception("Nenhuma operação crud foi definida")));
            // Cria um novo registro
            IEntity register = UtilsCrud.create(this.getServiceManager(), AuditCrudRegister.class, serviceData);

            /* Limita o tamanho da descrição para o tamanho do banco, evitando erros do tipo:
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
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }
    }
    
    public String getServiceName() {return SERVICE_NAME;}
}