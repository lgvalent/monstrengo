package br.com.orionsoft.monstrengo.security.services;

import java.util.Set;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.RightProcess;
import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;

/**
 * Serviço para verificar o direito de um usuário sobre um determinado processo.<br>
 * IN_USER_ID_OPT ou IN_USER_OPT deve ser fornecido.<br>
 * IN_PROCESS_ID_OPT ou IN_PROCESS_NAME_OPT deve ser fornecido.<br>
 * <b>Obs.: É recomendável utilizar IN_USER_OPT para que não seja realizada uma busca 
 * pelo Id do usuário, pois esta busca pode demorar até 2 segundos pelo hibernate.</b>
 * 
 *  * <p><b>Argumento:</b>
 * <br> IN_USER_ID_OPT: O Id do operador a ser verificado.
 * <br> IN_USER_OPT: A entidade do operador a ser verificado<b>(recomendável)</b>.
 * <br> IN_PROCESS_ID_OPT: A classe da entidade a ser criada.
 * <br> IN_PROCESS_NAME_OPT: A classe da entidade a ser criada.
 * 
 * <p><b>Procedimento:</b>
 * <br>Tentar obter o Direito já instanciado.
 * <br>Se não conseguir cria um novo Direito.
 * <br>Define se o direito de execução é permitido ou não.
 * <br>
 * @author Marcia 2005/10/27
 * @version 2005/10/27
 * 
 * @spring.bean id="CheckRightProcessService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class CheckRightProcessService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "CheckRightProcessService";
    
    public static String IN_USER_ID_OPT = "userId";
    public static String IN_USER_OPT = "user";
    public static String IN_PROCESS_ID_OPT = "processId";
    public static String IN_PROCESS_NAME_OPT = "processName";
    
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @SuppressWarnings("unchecked")
	public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            // Pega os argumentos
            Long inUserId = null;
        	if(serviceData.getArgumentList().containsProperty(IN_USER_ID_OPT))
        		inUserId = (Long) serviceData.getArgumentList().getProperty(IN_USER_ID_OPT);
        	
            ApplicationUser inUser = null;
        	if(serviceData.getArgumentList().containsProperty(IN_USER_OPT))
        		inUser = (ApplicationUser) ((IEntity) serviceData.getArgumentList().getProperty(IN_USER_OPT)).getObject();

            Long inProcessId = null;
        	if(serviceData.getArgumentList().containsProperty(IN_PROCESS_ID_OPT))
        		inProcessId = (Long) serviceData.getArgumentList().getProperty(IN_PROCESS_ID_OPT);

            String inProcessName = null;
        	if(serviceData.getArgumentList().containsProperty(IN_PROCESS_NAME_OPT))
        		inProcessName = (String) serviceData.getArgumentList().getProperty(IN_PROCESS_NAME_OPT);

            /* Prepara a hql que pesquisará o direito do procesos */
//        	String hql
        	
            /* Recuperando o usuário pelo id se não foi informado.
             * Este retrieve pode demorar até 2 segundos devido à complexidade da estrutura de segurança.
             */
        	if(inUser==null)
        		inUser = (ApplicationUser) UtilsCrud.retrieve(this.getServiceManager(), ApplicationUser.class, inUserId, serviceData).getObject();


            // Define o valor padrão false caso não encontre o direito 
            boolean executeAllowed = false;
            
            // Obtendo todos os grupos relacionados ao usuario
            for (SecurityGroup group: (Set<SecurityGroup>) inUser.getSecurityGroups())
            {
                // Obtendo todos os direitos de processo do grupo
                for (RightProcess right: (Set<RightProcess>) group.getRightsProcess())
                {   
                	/* Verificando no atual grupo se o direitro desejado já foi encontrado */
                	if((inProcessId!=null && right.getApplicationProcess().getId()==inProcessId) || right.getApplicationProcess().getName().equals(inProcessName)){
                		executeAllowed = right.isExecuteAllowed();
                		/* O direiro foi encontrado e não procura mais neste grupo */
                		break;
                	}
                	
            		/* Verifica se um direito TRUE já foi encontrado
            		 * para não pesquisa em outros grupos do usuário */
                	if(executeAllowed)
                		break;
                }
            }

            // Adiciona o resulta na lista de resultado do serviço
            serviceData.getOutputData().add(executeAllowed);
            
        } 
        catch (BusinessException e)
        {
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }
    }

}