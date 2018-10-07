package br.com.orionsoft.monstrengo.security.processes;

import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
import br.com.orionsoft.monstrengo.security.services.AuthenticateService;

/**
 * Este processo permite autenticar um login e senha.
 * 
 * <p><b>Procedimentos:</b>
 * <br>Definir o login: <i>setLogin(String)</i>
 * <br>Definir o password: <i>setPassword(String)</i>
 * <br>Executar o método <i>runAuthenticate()</i>.
 * <br>Se o método concluir com sucesso:
 * <li>Uma sessão de usuário está pronta e pode ser obtida por <i>getUserSession</i>. 
 * <br>Senão:
 * <li>O erro é fornecido por <i>getErroList</i>.</b> 
 * 
 * @author Lucio
 * @version 30/09/2005
 * 
 * @spring.bean id="AuthenticateProcess" init-method="start" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 *
 */
public class AuthenticateProcess extends ProcessBasic
{
    public static final String PROCESS_NAME = "AuthenticateProcess";
    
    private String login;
    private String password;
    private String terminal;
    private Boolean checkPassword = true;

    private UserSession userSession;
    
    public String getProcessName(){return PROCESS_NAME;}
    
    public String getLogin(){return login;}
    public void setLogin(String login){this.login = login;}

    public String getPassword(){return password;}
    public void setPassword(String password){this.password = password;}

    public String getTerminal(){return terminal;}
    public void setTerminal(String terminal){this.terminal = terminal;}

	public Boolean getCheckPassword() {return checkPassword;}
	public void setCheckPassword(Boolean checkPassword) {this.checkPassword = checkPassword;}

	public boolean runAuthenticate()
    {
        super.beforeRun();
    	try
        {
            // Executar o serviço de autenticação
            ServiceData sd = new ServiceData(AuthenticateService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(AuthenticateService._1_LOGIN_STR, login);
            sd.getArgumentList().setProperty(AuthenticateService._2_PASSWORD_STR, password);
            sd.getArgumentList().setProperty(AuthenticateService.CHECK_PASSWORD_BOOL_OPT, this.checkPassword);

            this.getProcessManager().getServiceManager().execute(sd);
            
            IEntity user = (IEntity) sd.getOutputData(0);
            
            // Cria a UserSession com o usuário validado
            // TODO IMPLEMENTAR Usar o UserSessionManager para criar as sessões
            userSession = new UserSession(user, terminal);
            
            // Registra na auditoria
            UtilsAuditorship.auditProcess(this, "",null);
            
            return true;
            
        } catch (BusinessException e)
        {
            this.getMessageList().addAll(e.getErrorList()); 
        }
        
        return false;
    }

    public UserSession getUserSession(){return userSession;}

}
