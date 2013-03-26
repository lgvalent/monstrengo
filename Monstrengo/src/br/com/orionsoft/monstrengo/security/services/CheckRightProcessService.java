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
 * Servi�o para verificar o direito de um usu�rio sobre um determinado processo.<br>
 * IN_USER_ID_OPT ou IN_USER_OPT deve ser fornecido.<br>
 * IN_PROCESS_ID_OPT ou IN_PROCESS_NAME_OPT deve ser fornecido.<br>
 * <b>Obs.: � recomend�vel utilizar IN_USER_OPT para que n�o seja realizada uma busca 
 * pelo Id do usu�rio, pois esta busca pode demorar at� 2 segundos pelo hibernate.</b>
 * 
 *  * <p><b>Argumento:</b>
 * <br> IN_USER_ID_OPT: O Id do operador a ser verificado.
 * <br> IN_USER_OPT: A entidade do operador a ser verificado<b>(recomend�vel)</b>.
 * <br> IN_PROCESS_ID_OPT: A classe da entidade a ser criada.
 * <br> IN_PROCESS_NAME_OPT: A classe da entidade a ser criada.
 * 
 * <p><b>Procedimento:</b>
 * <br>Tentar obter o Direito j� instanciado.
 * <br>Se n�o conseguir cria um novo Direito.
 * <br>Define se o direito de execu��o � permitido ou n�o.
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

            /* Prepara a hql que pesquisar� o direito do procesos */
//        	String hql
        	
            /* Recuperando o usu�rio pelo id se n�o foi informado.
             * Este retrieve pode demorar at� 2 segundos devido � complexidade da estrutura de seguran�a.
             */
        	if(inUser==null)
        		inUser = (ApplicationUser) UtilsCrud.retrieve(this.getServiceManager(), ApplicationUser.class, inUserId, serviceData).getObject();


            // Define o valor padr�o false caso n�o encontre o direito 
            boolean executeAllowed = false;
            
            // Obtendo todos os grupos relacionados ao usuario
            for (SecurityGroup group: (Set<SecurityGroup>) inUser.getSecurityGroups())
            {
                // Obtendo todos os direitos de processo do grupo
                for (RightProcess right: (Set<RightProcess>) group.getRightsProcess())
                {   
                	/* Verificando no atual grupo se o direitro desejado j� foi encontrado */
                	if((inProcessId!=null && right.getApplicationProcess().getId()==inProcessId) || right.getApplicationProcess().getName().equals(inProcessName)){
                		executeAllowed = right.isExecuteAllowed();
                		/* O direiro foi encontrado e n�o procura mais neste grupo */
                		break;
                	}
                	
            		/* Verifica se um direito TRUE j� foi encontrado
            		 * para n�o pesquisa em outros grupos do usu�rio */
                	if(executeAllowed)
                		break;
                }
            }

            // Adiciona o resulta na lista de resultado do servi�o
            serviceData.getOutputData().add(executeAllowed);
            
        } 
        catch (BusinessException e)
        {
            // O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros.
            throw new ServiceException(e.getErrorList());
        }
    }

}