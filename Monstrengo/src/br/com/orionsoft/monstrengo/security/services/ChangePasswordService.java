package br.com.orionsoft.monstrengo.security.services;

import org.apache.commons.codec.digest.DigestUtils;

import br.com.orionsoft.monstrengo.security.services.AuthenticateService;
import br.com.orionsoft.monstrengo.security.services.ChangePasswordService;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Serviço de alteração da senha de um operador.<p>
 * <b>Argumentos:</b><br>
 *  IN_LOGIN: O login do operador que trocará a senha.<br>
 *  IN_CURRENT_PASSWORD: O password atual do operador, caso a opção IN_OVERWRITE_BOL não seja informada.<br>
 *  IN_NEW_PASSWORD: O novo password fornecido pelo operador.<p>
 *  IN_OVERWRITE_BOL: Se definido como TRUE o serviço não irá autenticar o operador antes da troca. Simplesmente definirá o novo password.<p>
 * <b>Procedimento:</b><br>
 * Opção IN_OVERWRITE_BOL?
 * <li>TRUE: Pega o operador da lista de provedores de autenticação ativa no serviço de autenticação.
 * <li>FALSE: Pega o operador pela autenticação do login no serviço de autenticação.
 * Atualiza a senha se estiver tudo OK.<br>
 * Ou exibe uma mensagem de erro de validação.<br>
 * <b>Não retorna nada. Só a mensagem do resultado na lista de mensagens</b>
 *
 * @spring.bean id="ChangePasswordService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="authenticateService" ref="AuthenticateService"
 */
public class ChangePasswordService extends ServiceBasic
{
    public static String SERVICE_NAME = "ChangePasswordService";
    public static String IN_LOGIN = "login";
    public static String IN_CURRENT_PASSWORD = "currentPassword";
    public static String IN_NEW_PASSWORD = "newPassword";
    public static String IN_OVERWRITE_BOL = "overwritePassword";

    public String getServiceName(){return SERVICE_NAME;}
    
    
    private AuthenticateService authenticateService;
	/**
	 * Permite obter o serviço de autenticação que possui a lista de Provedores 
	 * de Autenticação registrados para tentar obter o operador sem autentica-lo.
	 * É feita uma busca em cada provedor pelo login informado.
	 * @return
	 */
    public AuthenticateService getAuthenticateService() {return authenticateService;}
	public void setAuthenticateService(AuthenticateService autenticateService) {this.authenticateService = autenticateService;}

    public void execute(ServiceData serviceData) throws ServiceException
    {
        try
        {
            log.debug("Iniciando a execução do serviço ChangePasswordService");
            
            /* Obtem os parâmetros */
            String inLogin = (String) serviceData.getArgumentList().getProperty(IN_LOGIN);
            
            String inCurrentPassword = "";
            if(serviceData.getArgumentList().containsProperty(IN_CURRENT_PASSWORD))
            	inCurrentPassword = (String) serviceData.getArgumentList().getProperty(IN_CURRENT_PASSWORD);

            String inNewPassword = (String) serviceData.getArgumentList().getProperty(IN_NEW_PASSWORD);
			/* Encriptografa o password com MD5 que é o armazenado no banco */
            inNewPassword = DigestUtils.md5Hex(inNewPassword);
            
            boolean overwritePassword = false;
            if(serviceData.getArgumentList().containsProperty(IN_OVERWRITE_BOL))
            	overwritePassword = (Boolean) serviceData.getArgumentList().getProperty(IN_OVERWRITE_BOL);
            
            /* Autentica o operador */
            IEntity<?> user = null;
            /* Verifica se o password atual será utilizado ou se é para sobrescrever
             * o atual password sem tentar autenticar o operador*/
            try{
            	ServiceData auth = new ServiceData(AuthenticateService.SERVICE_NAME, serviceData);
            	auth.getArgumentList().setProperty(AuthenticateService._1_LOGIN_STR, inLogin);
            	auth.getArgumentList().setProperty(AuthenticateService._2_PASSWORD_STR, inCurrentPassword);
            	if(overwritePassword)
            		auth.getArgumentList().setProperty(AuthenticateService.CHECK_PASSWORD_BOOL_OPT, false);
            	
            	this.getServiceManager().execute(auth);

            	user = auth.getFirstOutput();
            } catch (ServiceException e)
            {
            	ServiceException se = new ServiceException(e.getErrorList());
            	se.getErrorList().add(ChangePasswordService.class, "ERROR_AUTHENTICATING", inLogin);

            	throw se;
            };
            
            /* Muda a senha do operador */
            user.setPropertyValue(ApplicationUser.PASSWORD, inNewPassword);
            UtilsCrud.update(this.getServiceManager(), user, serviceData);
            
            /* Inclui a mensagem de OK */
            this.addInfoMessage(serviceData, "CHANGE_OK", inLogin);
        } catch (BusinessException e)
        {
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }
    }
}
