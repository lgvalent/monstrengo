package br.com.orionsoft.monstrengo.security.services;

import org.apache.commons.codec.digest.DigestUtils;

import br.com.orionsoft.monstrengo.security.services.AuthenticationException;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Serviço de autenticação de um usuário.
 * <p><b>Argumentos:</b>
 * <br> _1_LOGIN_STR: O login do usuário que será autenticado.
 * <br>_2_PASSWORD_STR: O password fornecido pelo usuário.
 * <br>CHECK_PASSWORD_BOOL_OPT: Permite definir se deve ocorrer uma autenticação sem SENHA ou com SENHA
 * <p><b>Procedimento:</b>
 * <br>Utiliza a lista de provedores registrados.
 * <br>Para cada provedor tentar validar o login e password.
 * <br>O primeiro provedor que valida é utilizado.
 * <br><b>Retorna uma entidade do tipo ApplicationUser;</b>
 *
 * @spring.bean id="AuthenticateService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class AuthenticateService extends ServiceBasic
{
	public static String SERVICE_NAME = "AuthenticateService";
	public static String _1_LOGIN_STR = "login";
	public static String _2_PASSWORD_STR = "password";
    public static String CHECK_PASSWORD_BOOL_OPT = "checkPassword";


	public String getServiceName(){return SERVICE_NAME;}

	/**
	 * 
	 * <p>Este service recebe um login e senha e tenta validar
	 * estes dados nos provedores que estão atualmente registrados. 
	 */
    public void execute(ServiceData serviceData) throws ServiceException
    {
    	try
    	{
    		log.debug("Iniciando a execução do serviço AuthenticateService");

    		String inLogin = (String) serviceData.getArgumentList().getProperty(_1_LOGIN_STR);
    		String inPassword = (String) serviceData.getArgumentList().getProperty(_2_PASSWORD_STR);
    		Boolean inCheckPassword = serviceData.getArgumentList().containsProperty(CHECK_PASSWORD_BOOL_OPT)?(Boolean) serviceData.getArgumentList().getProperty(CHECK_PASSWORD_BOOL_OPT):true;

    		/* Encriptografa o password com MD5 que é o armazenado no banco */
    		inPassword = DigestUtils.md5Hex(inPassword);

    		// Cria o resultado que conterá todos os grupos.
    		IEntity<?> result = null;

    		IEntity<?> user = loadUserByLogin(inLogin);

    		if (user != null)
    		{
    			if (log.isDebugEnabled())
    				log.debug("Login " + inLogin + " encontrado, verificando a senha..");
    			// Verifica se a senha coincide
    			if (user.getProperty(ApplicationUser.PASSWORD).getValue().getAsString().equals(inPassword) || !inCheckPassword)
    			{
    				result = user;
    			}

    			// Verifica se o operador está ativo
    			if (user.getProperty(ApplicationUser.INACTIVE).getValue().getAsBoolean())
    			{
    				throw new AuthenticationException(MessageList.create(AuthenticationException.class, "USER_INACTIVE", inLogin));
    			}
    		}

    		// Verifica se não conseguiu autenticação em nenhum provedor
    		if (result == null)
    			throw new AuthenticationException(MessageList.create(AuthenticationException.class, "USER_NOT_AUTHENTICATED", inLogin));

    		// Envia o resultado 
    		serviceData.getOutputData().add(result);
    	} catch (BusinessException e)
    	{
    		// O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
    		throw new ServiceException(e.getErrorList());
    	}
    }

    public IEntity<?> loadUserByLogin(String login) throws AuthenticationException
    {
        try
        {

            // Executa o serviço para obter dados da camada de persistência
            ServiceData serviceData = new ServiceData(ListService.SERVICE_NAME, null);
            serviceData.getArgumentList().setProperty(ListService.CLASS, ApplicationUser.class);
            serviceData.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, ApplicationUser.LOGIN + "='" + login + "'");
            this.getServiceManager().execute(serviceData);
            
            // Obtem o usuário com o login passado
            IEntityList<?> users = (IEntityList<?>) serviceData.getOutputData(0);
            IEntity<?> user=null;

            // Verifica se somente um usuário foi encontrado
            if (users.size()==1)
            {
                user = users.get(0);
            }
            else
            if (users.size()>1)
                throw new AuthenticationException(MessageList.create(AuthenticationException.class, "MORE_THAN_ONE_LOGIN", login, this.getClass().getName()));
//          else
//          Não é necessário dar exceção em caso de não encontrar o login                  
//          if (users.size()==0)
//              throw new AuthenticationException(MessageList.createSingleErrorList(AuthenticationException.class, "LOGIN_NOT_FOUND", login));
            
            return user;

        } catch (BusinessException e)
        {
            // Adiciona mensagem local
            e.getErrorList().addAll(MessageList.create(AuthenticationException.class, "ERROR_LOADING_USER_BY_LOGIN", login));
            throw new AuthenticationException(e.getErrorList());
        }
        
    }


}
