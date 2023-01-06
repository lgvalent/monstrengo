package br.com.orionsoft.monstrengo.security.services;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.RightCrud;

/**
 * Serviço para definir o direito de um grupo sobre uma determinada entidade.
 * <br>Os direitos passados como argumentos para o serviços são opcionais.
 * Somente os direitos passados serão alterados pelo serviço.
 * 
 * <p><b>Procedimento:</b>
 * <br>Tentar obter o Direito já instanciado.
 * <br>Se não conseguir cria um novo Direito.
 * <br>Verifica os direitos que serão alterados.
 * <br>Define se os direitos são permitidos ou não.
 * <br>
 * @author Lucio 26/10/2005
 * @version 26/10/2005
 * 
 * @spring.bean id="DefineRightCrudService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="transactional" value="true"
 */
public class DefineRightCrudService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "DefineRightCrudService";
    
    public static String GROUP_ID = "groupId";
    public static String ENTITY_ID = "entityId";
    
    /** boolean: Define o direito de acesso */
    public static String CREATE_ALLOWED = "createAllowed";
    public static String RETRIEVE_ALLOWED = "retrieveAllowed";
    public static String UPDATE_ALLOWED = "updateAllowed";
    public static String DELETE_ALLOWED = "deleteAllowed";
    public static String QUERY_ALLOWED = "queryAllowed";

    public String getServiceName() {
        return SERVICE_NAME;
    }
    
    public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            log.debug("Pegando os argumentos do serviço");
            long groupId = (Long) serviceData.getArgumentList().getProperty(GROUP_ID);
            long entityId = (Long) serviceData.getArgumentList().getProperty(ENTITY_ID);
            
            log.debug("Tratando os argumentos OPCIONAIS");
            Boolean createAllowed = null;
            Boolean retrieveAllowed = null;
            Boolean updateAllowed = null;
            Boolean deleteAllowed = null;
            Boolean queryAllowed = null;

            if (serviceData.getArgumentList().containsProperty(CREATE_ALLOWED)) 
                createAllowed = (Boolean) serviceData.getArgumentList().getProperty(CREATE_ALLOWED);
            if (serviceData.getArgumentList().containsProperty(RETRIEVE_ALLOWED)) 
                retrieveAllowed = (Boolean) serviceData.getArgumentList().getProperty(RETRIEVE_ALLOWED);
            if (serviceData.getArgumentList().containsProperty(UPDATE_ALLOWED)) 
                updateAllowed = (Boolean) serviceData.getArgumentList().getProperty(UPDATE_ALLOWED);
            if (serviceData.getArgumentList().containsProperty(DELETE_ALLOWED)) 
                deleteAllowed = (Boolean) serviceData.getArgumentList().getProperty(DELETE_ALLOWED);
            if (serviceData.getArgumentList().containsProperty(QUERY_ALLOWED)) 
                queryAllowed = (Boolean) serviceData.getArgumentList().getProperty(QUERY_ALLOWED);

            // TODO IMPLEMENTAR Usar um biblioteca mais eficiente para pesquisas de múltiplas condições
            // Obtem o Direito que se relaciona com o GrupoId e ProcessId indicados
            ServiceData sl = new ServiceData(ListService.SERVICE_NAME, serviceData);
            sl.getArgumentList().setProperty(ListService.CLASS, RightCrud.class);
            sl.getArgumentList().setProperty(ListService.CONDITION_OPT_STR,  IDAO.ENTITY_ALIAS_HQL + "." + RightCrud.SECURITY_GROUP + ".id=" + groupId + " and " + IDAO.ENTITY_ALIAS_HQL + "." + RightCrud.APPLICATION_ENTITY + ".id=" + entityId);
            this.getServiceManager().execute(sl);
            
            IEntityList entCol = (IEntityList) sl.getFirstOutput();
            
            log.debug("Verificando se o direito já existe");
            IEntity right;
            if (entCol.size() == 0)
            {
                log.debug("Criando um novo direito");
                right = UtilsCrud.create(this.getServiceManager(), RightCrud.class, serviceData);
                
                log.debug("Definindo suas novas propriedades básicas");
                right.getProperty(RightCrud.SECURITY_GROUP).getValue().setId(groupId, serviceData);
                right.getProperty(RightCrud.APPLICATION_ENTITY).getValue().setId(entityId, serviceData);
            }
            else
            {
                log.debug("Pegando o direito encontrado.");
                right = entCol.get(0);
            }

            log.debug("Definindo os direitos verificando se foi solicitada sua alteração"); 
            if (createAllowed != null)
                right.getProperty(RightCrud.CREATE_ALLOWED).getValue().setAsBoolean(createAllowed);
            if (retrieveAllowed != null)
                right.getProperty(RightCrud.RETRIEVE_ALLOWED).getValue().setAsBoolean(retrieveAllowed);
            if (updateAllowed != null)
                right.getProperty(RightCrud.UPDATE_ALLOWED).getValue().setAsBoolean(updateAllowed);
            if (deleteAllowed != null)
                right.getProperty(RightCrud.DELETE_ALLOWED).getValue().setAsBoolean(deleteAllowed);
            
            if (queryAllowed != null)
                right.getProperty(RightCrud.QUERY_ALLOWED).getValue().setAsBoolean(queryAllowed);
            
            log.debug("Atualizando o direito");
            UtilsCrud.update(this.getServiceManager(), right, serviceData);
            
//            throw new ServiceException(MessageList.createSingleInternalError(new Exception("TESTE de exceçao CRUD")));
            
        } 
        catch (BusinessException e)
        {
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }
    }

}