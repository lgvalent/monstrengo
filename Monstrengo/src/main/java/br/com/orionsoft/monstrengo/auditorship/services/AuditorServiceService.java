package br.com.orionsoft.monstrengo.auditorship.services;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditServiceRegister;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Servi�o de registro de auditoria de execu��o de outros servi�os.
 * Na descri��o s�o armazenados os par�metros utilizados na execu��o do servi�o.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_TERMINAL: (String) Identifica��o do terminal.
 * <br> IN_APPLICATION_USER_OPT: (IEntity) Identifica��o de um usu�rio.
 * <br> IN_SERVICE_DATA: (ServiceData) Estrutura com os dados do Servi�o.
 * <br> IN_DESCRIPTION_STR: Descri��o adicional que ser� registrada na auditoria.
 * 
 * @author Lucio 20120521
 * @version 20120521
 */
public class AuditorServiceService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "AuditorServiceService";
    
    public static String IN_TERMINAL = "terminal";
    public static String IN_APPLICATION_USER_OPT = "applicationUser";
    public static String IN_SERVICE_DATA = "serviceData";
    public static String IN_DESCRIPTION_STR = "description";
    
    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            // Pega os argumentos
        	String inTerminal = serviceData.getArgumentList().containsProperty(IN_TERMINAL)?(String) serviceData.getArgumentList().getProperty(IN_TERMINAL):null;
            ApplicationUser inApplicationUser = serviceData.getArgumentList().containsProperty(IN_APPLICATION_USER_OPT)?(ApplicationUser) serviceData.getArgumentList().getProperty(IN_APPLICATION_USER_OPT):null;
			ServiceData inServiceData = (ServiceData) serviceData.getArgumentList().getProperty(IN_SERVICE_DATA);
            String inDescription = (String) serviceData.getArgumentList().getProperty(IN_DESCRIPTION_STR);
            
            // Cria um novo registro
            IEntity<AuditServiceRegister> register = UtilsCrud.create(this.getServiceManager(), AuditServiceRegister.class, serviceData);
            AuditServiceRegister oRegister = register.getObject();
            
            /* Adiciona os par�metros do servi�o no FINAL da descri��o da auditoria */
            inDescription += " Par�metros: " + serviceData.toString();
            
            /* Limita o tamanho da descri��o para o tamanho do banco, evitando erros do tipo:
             * Data truncation: Data too long for column 'description' at row 1 */
            inDescription = StringUtils.substring(inDescription, 0, register.getProperty(AuditServiceRegister.DESCRIPTION).getInfo().getSize());

            // Preenche o novo registro
            oRegister.setOcurrencyDate(Calendar.getInstance());
            oRegister.setApplicationUser(inApplicationUser);
            oRegister.setTerminal(inTerminal);
            oRegister.setServiceName(inServiceData.getServiceName());
            oRegister.setDescription(inDescription);
            
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