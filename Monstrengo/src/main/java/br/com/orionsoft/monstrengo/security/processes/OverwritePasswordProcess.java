package br.com.orionsoft.monstrengo.security.processes;

import br.com.orionsoft.monstrengo.security.processes.ChangePasswordProcess;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.services.ChangePasswordService;

/**
 * Este processo permite alterar a senha de um operador
 * conhecendo a senha atual.
 * 
 * <p><b>Procedimentos:</b>
 * <br>Definir o login: <i>setLogin(String)</i>
 * <br>Definir o novo password: <i>setNewPassword(String)</i>
 * <br>Definir a confirmação do novo password: <i>setConfirmNewPassword(String)</i>
 * <br>Executar o método <i>runOverwrite()</i>.
 * <br>Se o método concluir com sucesso:
 * <li>A senha estará alterada e já valendo para a próxima autenticação. 
 * <br>Senão:
 * <li>O erro é fornecido por <i>getErroList</i>.</b> 
 * 
 * @author Lucio 
 * @version 20060511
 * 
 * @spring.bean id="OverwritePasswordProcess" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 */
@ProcessMetadata(label="Redefinir a senha de um operador", hint="Permite redefinir a senha de um operador sem conhecer a sua atual senha", description="Permite redefinir a senha de um operador sem conhecer a sua atual senha. Útil para os administradores redefinirem a senha esquecida de um operador")
public class OverwritePasswordProcess extends ProcessBasic implements IRunnableEntityProcess
{
    public static final String PROCESS_NAME = "OverwritePasswordProcess";
    
    private String login;
    private String newPassword;
    private String confirmNewPassword;

    public String getProcessName(){return PROCESS_NAME;}
    
    public String getLogin(){return login;}
    public void setLogin(String login){this.login = login;}

    public String getNewPassword(){return newPassword;}
    public void setNewPassword(String terminal){this.newPassword = terminal;}

	public String getConfirmNewPassword() {return confirmNewPassword;}
	public void setConfirmNewPassword(String confirmNewPassword) {this.confirmNewPassword = confirmNewPassword;}

	public boolean runOverwrite()
    {
        super.beforeRun();

        try
        {
        	/* Verifica se o novo password e sua confirmação batem */
        	if(!newPassword.equals(confirmNewPassword))
        		throw new ProcessException(MessageList.create(ChangePasswordProcess.class, "ERROR_CONFIRM_PASSWORD"));
        	
            // Executar o serviço de autenticação
            ServiceData sd = new ServiceData(ChangePasswordService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(ChangePasswordService.IN_LOGIN, login);
            sd.getArgumentList().setProperty(ChangePasswordService.IN_NEW_PASSWORD, newPassword);
            sd.getArgumentList().setProperty(ChangePasswordService.IN_OVERWRITE_BOL, true);
            this.getProcessManager().getServiceManager().execute(sd);
            
            // Exibe a mensagem sucesso
            this.getMessageList().addAll(sd.getMessageList());
            
            // Registra na auditoria										
            UtilsAuditorship.auditProcess(this, "login='" + login + "'", null);
            									
            return true;
            							
        } catch (BusinessException e)
        {
            this.getMessageList().addAll(e.getErrorList()); 
        }
        
        return false;
    }

	/*==============================================================================
	 * IRunnableEntityProcess	
	 *==============================================================================*/
	
	public boolean runWithEntity(IEntity<?> entity) {
        super.beforeRun();

        boolean result = false;
		
		/* Verifica se a entidade é compatível */
		if(entity.getInfo().getType() == ApplicationUser.class){
			try {
				this.login = entity.getProperty(ApplicationUser.LOGIN).getValue().getAsString();

				result = true;
				
			} catch (BusinessException e) {
				this.getMessageList().addAll(e.getErrorList());
			}
			
		}else
		{
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}
		
		return result;
	}
	
	public boolean runWithEntities(IEntityCollection<?> entities) {
		super.beforeRun();
		return false;
	}


}
