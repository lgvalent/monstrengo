package br.com.orionsoft.monstrengo.security.services;

import java.util.Set;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.RightCrud;
import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;

/**
 * Servi�o para verificar os direitos de cria��o, exclus�o, e recupera��o de 
 * um usu�rio sobre uma determinada entidade.
 * 
 * <b>Argumento:</b><br>
 * IN_USER_ID_OPT: O identificador do operador.<p>
 * IN_USER_OPT: Uma inst�ncia de operador.<p>
 * IN_ENTITY_ID: O identificador da entidade que se deseja verifica o direito.<p>
 * 
 * <b>Procedimento:</b><br>
 * Obtem o operador pelo Id se o IN_USER_OPT n�o foi passado.<br>
 * Percorre todos os grupo do operador.<br>
 * Percorre todos os direitos CRUD de todos os grupos do operador.<br>
 * <b>Retorna quatro resultados do tipo Boolean</b>

 * <p><b>Procedimento:</b>
 * <br>Obter todos os direitos relacionados � entidade.
 * <br>Obtem todos os grupos do usu�rio.
 * <br>Verifica os direitos relacionados ao usu�rio atraves de seus grupos. 
 * <br>Caso algum dos grupos do usu�rio tenha direito a opera��o, ser� retornado true
 * <br>caso contrario ser� retornado false.
 * <br>
 * @author Marcia 2005/10/28
 * @version 20060329
 * 
 * @spring.bean id="CheckRightCrudService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class CheckRightCrudService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "CheckRightCrudService";
    
    public static String IN_USER_ID_OPT = "userId";
    public static String IN_USER_OPT = "user";
    public static String IN_ENTITY_ID = "entityId";
    
    public static int OUT_CREATE = 0;
    public static int OUT_RETRIEVE = 1;
    public static int OUT_UPDATE = 2;
    public static int OUT_DELETE = 3;
    public static int OUT_QUERY = 4;

	/**
	 * Estas constantes definem o nome das chaves de mapas de direitos
	 * cruds que armazenam a lista de direitos em uma estruturo de f�cil
	 * acesso.
	 */
    public static String CAN_CREATE = "canCreate";
    public static String CAN_RETRIEVE = "canRetrieve";
    public static String CAN_UPDATE = "canUpdate";
    public static String CAN_DELETE = "canDelete";
    public static String CAN_QUERY = "canQuery";

    public String getServiceName() {
        return SERVICE_NAME;
    }

	public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            log.debug("Iniciando a execu��o do servi�o CheckRightCrudService");
            // Pega os argumentos
            Long userId = null;
        	if(serviceData.getArgumentList().containsProperty(IN_USER_ID_OPT))
        		userId = (Long) serviceData.getArgumentList().getProperty(IN_USER_ID_OPT);
        	
            ApplicationUser user = null;
        	if(serviceData.getArgumentList().containsProperty(IN_USER_OPT))
        		user = (ApplicationUser) ((IEntity) serviceData.getArgumentList().getProperty(IN_USER_OPT)).getObject();

            long entityId = (Long) serviceData.getArgumentList().getProperty(IN_ENTITY_ID);

            // Recuperando o usu�rio pelo id se n�o foi informado
        	if(user==null)
        		user = (ApplicationUser) UtilsCrud.retrieve(this.getServiceManager(), ApplicationUser.class, userId, serviceData).getObject();

            // Define o valor padr�o false caso n�o encontre o direito 
            boolean createAllowed = false;
            boolean retrieveAllowed = false;
            boolean updateAllowed = false;
            boolean deleteAllowed = false;
            boolean queryAllowed = false;
            
            // Obtendo todos os grupos relacionados ao usuario
            for (SecurityGroup group: (Set<SecurityGroup>) user.getSecurityGroups())
            {
                // Obtendo todos os direitos de processo do grupo
                for (RightCrud right: (Set<RightCrud>) group.getRightsCrud())
                {   
                	/* Verificando no atual grupo se o direitro desejado j� foi encontrado */
                	if(right.getApplicationEntity().getId()==entityId){
                        /* Prepara os metadados da entidade corrente para verificar
                         * quais opera��es CRUD ela permite. Isto porque, as defini��es
                         * de direitos de acessos dos metadados, definidos pelo programador,
                         * prevalecem sobre os direitos encontrados no banco de dados - Lucio 20081117 
                         * USAR O SIMPLE_NAME  para busca no mapa de metadados */
                		if(this.log.isDebugEnabled())
                			this.log.debug("Obtendo metadados da entidade:" + right.getApplicationEntity().getName());

                		IEntityMetadata metadata = this.getServiceManager().getEntityManager().getEntitiesMetadata().get(right.getApplicationEntity().getName());
                		
                		if(metadata == null){
                            /* Bloco que n�o leva em considera��o os metadados, caso metadata esteja NULL */
                			if (right.isCreateAllowed())
                                createAllowed = true;
                            
                            if (right.isRetrieveAllowed())
                                retrieveAllowed = true;
                            
                            if (right.isUpdateAllowed())
                                updateAllowed = true;
                            
                            if (right.isDeleteAllowed())
                                deleteAllowed = true;

                            if (right.isQueryAllowed())
                                queryAllowed = true;
                		}else{
                            /* Bloco que leva em considera��o os metadados*/
                            if (right.isCreateAllowed() && metadata.getCanCreate())
                                createAllowed = true;
                            
                            if (right.isRetrieveAllowed() && metadata.getCanRetrieve())
                                retrieveAllowed = true;
                            
                            if (right.isUpdateAllowed() && metadata.getCanUpdate())
                                updateAllowed = true;
                            
                            if (right.isDeleteAllowed() && metadata.getCanDelete())
                                deleteAllowed = true;

                            if (right.isQueryAllowed() && metadata.getCanQuery())
                                queryAllowed = true;
                		}
                        

                        /* O direiro foi encontrado e n�o procura mais neste grupo */
                		break;
                	}
                	
            		/* Verifica se TODOS direitos TRUE j� foram encontrados
            		 * para n�o pesquisa em outros grupos do usu�rio */
                	if(createAllowed && retrieveAllowed && updateAllowed && deleteAllowed && queryAllowed)
                		break;
                }
            }

            // Adiciona o resultado na lista de resultado do servi�o
            serviceData.getOutputData().add(OUT_CREATE, createAllowed);
            serviceData.getOutputData().add(OUT_RETRIEVE, retrieveAllowed);
            serviceData.getOutputData().add(OUT_UPDATE, updateAllowed);
            serviceData.getOutputData().add(OUT_DELETE, deleteAllowed);
            serviceData.getOutputData().add(OUT_QUERY, queryAllowed);

        } 
        catch (BusinessException e)
        {
            // O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros.
            throw new ServiceException(e.getErrorList());
        }
    }

}