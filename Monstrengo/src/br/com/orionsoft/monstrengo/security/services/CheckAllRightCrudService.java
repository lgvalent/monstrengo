package br.com.orionsoft.monstrengo.security.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.com.orionsoft.monstrengo.security.services.CheckAllRightCrudService;
import br.com.orionsoft.monstrengo.security.services.CheckRightCrudService;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.RightCrud;
import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;

/**
 * Este serviço obtem um mapa com o nome das entidades e
 * o direito CRUD do operador sobre cadaentida.<p>
 * 
 * <b>Argumento:</b><br>
 * IN_USER_ID_OPT: O identificador do operador.<p>
 * IN_USER_OPT: Uma instância de operador.<p>
 * 
 * <b>Procedimento:</b><br>
 * Obtem o operador pelo Id se o IN_USER_OPT não foi passado.<br>
 * Percorre todos os grupo do operador.<br>
 * Percorre todos os direitos de processos de todos os grupos.<br>
 * Armazena a permissão no mapa.<br>
 * <b>Retorna um mapa com o nome simples da entidade e um mapa do direito CRUD: Map<String, Map<String,Boolean>></b>
 * <br>
 * @author Lucio 20060816
 * @version 20060816
 * 
 * @spring.bean id="CheckAllRightCrudService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class CheckAllRightCrudService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "CheckAllRightCrudService";
    
    public static String IN_USER_ID_OPT = "userId";
    public static String IN_USER_OPT = "user";
    
    /**
     * Cria um mapa de direitos inicializando com todos os direitos FALSE.
     * Utilizado aqui neste serviço e em outros serviços onde o resultado pode
     * ser complementado com novas entidades.
     * @return
     */
    public static final Map<String, Boolean> retrieveEmptyRightMap(){
    	Map<String, Boolean> result = new HashMap<String, Boolean>(4,1);
    	result.put(CheckRightCrudService.CAN_CREATE, false);
    	result.put(CheckRightCrudService.CAN_RETRIEVE, false);
    	result.put(CheckRightCrudService.CAN_UPDATE, false);
    	result.put(CheckRightCrudService.CAN_DELETE, false);
    	return result;
    }; 
    
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
        		user = ((IEntity<ApplicationUser>) serviceData.getArgumentList().getProperty(IN_USER_OPT)).getObject();

        	// Recuperando o usuário pelo id se não foi informado
        	if(user==null)
        		user = (ApplicationUser) UtilsCrud.retrieve(this.getServiceManager(), ApplicationUser.class, userId, serviceData).getObject();

            // Constroi o mapa de resultado com capacidade inicial de 100 entidades
            Map<String, Map<String,Boolean>> result = new HashMap<String, Map<String,Boolean>>(100);
            
            // Obtendo todos os grupos relacionados ao usuario
            for (SecurityGroup group: (Set<SecurityGroup>) user.getSecurityGroups())
            {
                // Obtendo todos os direitos de processo do grupo
                for (RightCrud right: (Set<RightCrud>) group.getRightsCrud())
                {   
                	
                	/* Prepara o mapa CRUD. Qual instancia será utilizada: Uma nova ou uma jah criada anteriormente */
                	Map<String, Boolean> rightMap;
                	if(result.containsKey(right.getApplicationEntity().getName()))
                    	/* Mapa CRUD já preparado em outra iteração de grupo */
                		rightMap = result.get(right.getApplicationEntity().getName());
                	else{
                    	/* Primeira vez que encontrou a entidade, o mapa CRUD será criado e reutilizado nas próximas vezes */
                		rightMap = CheckAllRightCrudService.retrieveEmptyRightMap();
                    	/* Colocando  o novo mapa CRUD no mapa geral de entidades */
                		result.put(right.getApplicationEntity().getName(), rightMap);
                	}
                	
                	/* Preenchendo os direitos do mapa CRUD */
                	/* Verificando se o direitro já foi encontrado em outro grupo
                	 * e já se encontra no mapa. */
                	Boolean allowed = rightMap.get(CheckRightCrudService.CAN_CREATE);
                	rightMap.put(CheckRightCrudService.CAN_CREATE, allowed || right.isCreateAllowed());

                	allowed = rightMap.get(CheckRightCrudService.CAN_RETRIEVE);
                	rightMap.put(CheckRightCrudService.CAN_RETRIEVE, allowed || right.isRetrieveAllowed());

                	allowed = rightMap.get(CheckRightCrudService.CAN_UPDATE);
                	rightMap.put(CheckRightCrudService.CAN_UPDATE, allowed || right.isUpdateAllowed());

                	allowed = rightMap.get(CheckRightCrudService.CAN_DELETE);
                	rightMap.put(CheckRightCrudService.CAN_DELETE, allowed || right.isDeleteAllowed());
                }
            }
            // Adiciona o resulta na lista de resultado do serviço
            serviceData.getOutputData().add(result);
            
        } 
        catch (BusinessException e)
        {
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }
    }

}