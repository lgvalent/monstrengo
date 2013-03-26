package br.com.orionsoft.monstrengo.security.processes;

import br.com.orionsoft.monstrengo.security.processes.ChangePasswordProcess;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.security.services.ChangePasswordService;

/**
 * Este processo permite alterar a senha do operador
 * atualmente autenticado e conhecendo a senha atual.
 * 
 * <p><b>Procedimentos:</b>
 * <br>Definir o password atual: <i>setCurrentPassword(String)</i>
 * <br>Definir o novo password: <i>setNewPassword(String)</i>
 * <br>Definir a opção overwrite (opcional): <i>setOverwrite(boolean)</i>
 * <br>Executar o método <i>runChange()</i>.
 * <br>Se o método concluir com sucesso:
 * <li>A senha estará alterada e já valendo para a próxima autenticação. 
 * <br>Senão:
 * <li>O erro é fornecido por <i>getErroList</i>.</b> 
 * 
 * @author Lucio 
 * @version 20060511
 * 
 * @spring.bean id="ChangePasswordProcess" init-method="start" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 *
 */
public class ChangePasswordProcess extends ProcessBasic
{
    public static final String PROCESS_NAME = "ChangePasswordProcess";
    
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;

    public String getProcessName(){return PROCESS_NAME;}
    
    public String getCurrentPassword(){return currentPassword;}
    public void setCurrentPassword(String password){this.currentPassword = password;}

    public String getNewPassword(){return newPassword;}
    public void setNewPassword(String terminal){this.newPassword = terminal;}

	public String getConfirmNewPassword() {return confirmNewPassword;}
	public void setConfirmNewPassword(String confirmNewPassword) {this.confirmNewPassword = confirmNewPassword;}

	public boolean runChange()
    {
        super.beforeRun();
        try
        {
        	/* Verifica se o novo password e sua confirmação batem */
        	if(!newPassword.equals(confirmNewPassword))
        		throw new ProcessException(MessageList.create(ChangePasswordProcess.class, "ERROR_CONFIRM_PASSWORD"));
        	
            // Executar o serviço de autenticação
            ServiceData sd = new ServiceData(ChangePasswordService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(ChangePasswordService.IN_LOGIN, this.getUserSession().getUserLogin());
            sd.getArgumentList().setProperty(ChangePasswordService.IN_CURRENT_PASSWORD, currentPassword);
            sd.getArgumentList().setProperty(ChangePasswordService.IN_NEW_PASSWORD, newPassword);
            sd.getArgumentList().setProperty(ChangePasswordService.IN_OVERWRITE_BOL, false);
            this.getProcessManager().getServiceManager().execute(sd);
            
            // Exibe a mensagem sucesso
            this.getMessageList().addAll(sd.getMessageList());
            
            // Registra na auditoria
            UtilsAuditorship.auditProcess(this, "login='" + this.getUserSession().getUserLogin() + "'", null);
            
            return true;
            
        } catch (BusinessException e)
        {
            this.getMessageList().addAll(e.getErrorList()); 
        }
        
        return false;
    }

}
