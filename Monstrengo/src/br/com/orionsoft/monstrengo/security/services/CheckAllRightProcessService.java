package br.com.orionsoft.monstrengo.security.services;

import java.util.HashMap;
import java.util.Map;
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
 * Este servi�o obtem um mapa com o nome dos processos e
 * o direito do operador de executar ou n�o o processo.<p>
 * 
 * <b>Argumento:</b><br>
 * IN_USER_ID_OPT: O identificador do operador.<p>
 * IN_USER_OPT: Uma inst�ncia de operador.<p>
 * 
 * <b>Procedimento:</b><br>
 * Obtem o operador pelo Id se o IN_USER_OPT n�o foi passado.<br>
 * Percorre todos os grupo do operador.<br>
 * Percorre todos os direitos de processos de todos os grupos.<br>
 * Armazena a permiss�o no mapa.<br>
 * <b>Retorna um mapa com o nome simples do processo e o direito de execu��o: Map<String, Boolean> </b>
 * <br>
 * @author Lucio 20060329
 * @version 20060329
 * 
 * @spring.bean id="CheckAllRightProcessService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class CheckAllRightProcessService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "CheckAllRightProcessService";
    
    public static String IN_USER_ID_OPT = "userId";
    public static String IN_USER_OPT = "user";
    
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @SuppressWarnings("unchecked")
	public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            // Pega os argumentos
            Long userId = null;
        	if(serviceData.getArgumentList().containsProperty(IN_USER_ID_OPT))
        		userId = (Long) serviceData.getArgumentList().getProperty(IN_USER_ID_OPT);
        	
            ApplicationUser user = null;
        	if(serviceData.getArgumentList().containsProperty(IN_USER_OPT))
        		user = (ApplicationUser) ((IEntity) serviceData.getArgumentList().getProperty(IN_USER_OPT)).getObject();

        	// Recuperando o usu�rio pelo id se n�o foi informado
        	if(user==null)
        		user = (ApplicationUser) UtilsCrud.retrieve(this.getServiceManager(), ApplicationUser.class, userId, serviceData).getObject();

            // Constroi o mapa de resultado 
            Map<String, Boolean> result = new HashMap<String, Boolean>();
            
            // Obtendo todos os grupos relacionados ao usuario
            for (SecurityGroup group: (Set<SecurityGroup>) user.getSecurityGroups())
            {
                // Obtendo todos os direitos de processo do grupo
                for (RightProcess right: (Set<RightProcess>) group.getRightsProcess())
                {   
                	/* Verificando se o direitro j� foi encontrado em outro grupo
                	 * e j� se encontra no mapa. */
                	Boolean executeAllowed = Boolean.FALSE;
                	if(result.containsKey(right.getApplicationProcess().getName()))
                		executeAllowed = result.get(right.getApplicationProcess().getName());
                	
                	/* Armazenando a jun��o do valor atual com o j� existente */
                	result.put(right.getApplicationProcess().getName(), right.isExecuteAllowed() || executeAllowed);
                }
            }
            // Adiciona o resulta na lista de resultado do servi�o
            serviceData.getOutputData().add(result);
            
        } 
        catch (BusinessException e)
        {
            // O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros.
            throw new ServiceException(e.getErrorList());
        }
    }

}