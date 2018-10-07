package br.com.orionsoft.monstrengo.security.processes;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.security.services.CreateSecurityStructureService;

/**
 * Este processo permite criar/manter um operador
 * utilizando o seu login e nome de seu grupo.
 * 
 * Se o operador, grupo ou direitos j� existirem os
 * existentes ser�o utilizados.
 * 
 * <p><b>Procedimentos:</b>
 * <br>Definir o login: <i>setLogin(String)</i>
 * <br>Definir o grupo: <i>setGroup(String)</i>
 * <br>Definir o direito padr�o: <i>setAllowAll(boolean)</i>
 * <br>Executar o m�todo <i>runCreate()</i>.
 * <br>Se o m�todo concluir com sucesso:
 * <li>O operador estar� cadastrado com seu grupo e com os direitos definidos para todas
 * as entidades e processos. 
 * <br>Sen�o:
 * <li>O erro � fornecido por <i>getErroList</i>.</b> 
 * 
 * @author Lucio 20060501
 * @version 20060501
 * 
 * @spring.bean id="CreateSecurityStructureProcess" init-method="start" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 *
 */
public class CreateSecurityStructureProcess extends ProcessBasic
{
    public static final String PROCESS_NAME = "CreateSecurityStructureProcess";
    
    private String login;
    private String groupName;
    private boolean allowAll=false;

    
    public String getProcessName(){return PROCESS_NAME;}
    
    public String getLogin(){return login;}
    public void setLogin(String login){this.login = login;}

    public String getGroupName(){return groupName;}
    public void setGroupName(String password){this.groupName = password;}

	public boolean isAllowAll() {return allowAll;}
	public void setAllowAll(boolean allowAll) {this.allowAll = allowAll;}

    public boolean runCreate()
    {
        super.beforeRun();

        try
        {
            // Executar o servi�o de autentica��o
            ServiceData sd = new ServiceData(CreateSecurityStructureService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_USER_LOGIN, login);
            sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_GROUP_NAME, groupName);
            sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_ALLOW_ALL_BOOL, allowAll);
            sd.getArgumentList().setProperty(CreateSecurityStructureService.IN_PROCESS_MANAGER, this.getProcessManager());
            this.getProcessManager().getServiceManager().execute(sd);
            
        	/* Adiciona as mensagens do servi�o no processo */
        	this.getMessageList().addAll(sd.getMessageList());
            
        	/* Verifica se o servi�o concluiu com sucesso */ 
        	if(!sd.getMessageList().isTransactionSuccess())
            	return false;
            
            return true;
        } catch (BusinessException e)
        {
            this.getMessageList().addAll(e.getErrorList()); 
        }
        
        return false;
    }



}
