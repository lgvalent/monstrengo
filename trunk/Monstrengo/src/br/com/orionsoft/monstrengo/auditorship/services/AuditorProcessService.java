package br.com.orionsoft.monstrengo.auditorship.services;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditProcessRegister;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationProcess;
import br.com.orionsoft.monstrengo.security.entities.UserSession;

/**
 * Servi�o de registro de auditoria de processos.
 * Na descri��o s�o armazenados os par�metros utilizados na execu��o do processo.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_USER_SESSION: Inst�ncia da atual sess�o do usu�rio.
 * <br> IN_APPLICATION_PROCESS: Entidade de seguran�a que indica o processo que ser� auditado.
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
 * @spring.bean id="AuditorProcessService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class AuditorProcessService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "AuditorProcessService";
    
    public static String USER_SESSION = "userSession";
    public static String IN_APPLICATION_PROCESS = "applicationProcess";
    public static String DESCRIPTION_STR = "description";
    
    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            // Pega os argumentos
            UserSession appUser = (UserSession) serviceData.getArgumentList().getProperty(USER_SESSION);
            @SuppressWarnings("unchecked")
			IEntity<ApplicationProcess> appProcess = (IEntity<ApplicationProcess>) serviceData.getArgumentList().getProperty(IN_APPLICATION_PROCESS);
            String description = (String) serviceData.getArgumentList().getProperty(DESCRIPTION_STR);
            
            // Cria um novo registro
            IEntity<AuditProcessRegister> register = UtilsCrud.create(this.getServiceManager(), AuditProcessRegister.class, serviceData);

            /* Limita o tamanho da descri��o para o tamanho do banco, evitando erros do tipo:
             * Data truncation: Data too long for column 'description' at row 1 */
            description = StringUtils.substring(description, 0, register.getProperty(AuditProcessRegister.DESCRIPTION).getInfo().getSize());

            // Preenche o novo registro
            register.getProperty(AuditProcessRegister.OCURRENCY_DATE).getValue().setAsCalendar(Calendar.getInstance());
            register.getProperty(AuditProcessRegister.APPLICATION_USER).getValue().setAsEntity(appUser.getUser());
            register.getProperty(AuditProcessRegister.TERMINAL).getValue().setAsString(appUser.getTerminal());
            register.getProperty(AuditProcessRegister.APPLICATION_PROCESS).getValue().setAsEntity(appProcess);
            register.getProperty(AuditProcessRegister.DESCRIPTION).getValue().setAsString(description);
            
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
}