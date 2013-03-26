package br.com.orionsoft.monstrengo.crud.documents.services;

import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.ClassUtils;
import br.com.orionsoft.monstrengo.crud.documents.entities.ModelDocumentEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Este serviço retorna a lista de modelos de documentos
 * para uma determinada entidade.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_ENTITY_TYPE_NAME: Nome do tipo da entidade que será usada para pesquisar os modelos de documentos disponíveis.
 * <br> IN_APPLICATION_USER_OPT: Usuário que será usada para filtrar a pesquisa de modelos de documentos disponíveis.
 * 
 * <p><b>Procedimento:</b>
 * <br>Pesquisa no banco todos os modelos de etiquetas para entidade cujo nome da entidade seja igual ao da entidade passada.
 * <br><b>Retorna uma lista de itens de seleção com o id e nome dos modelos.</b>
 * 
 * 
 * @spring.bean id="ListModelDocumentEntityService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class ListModelDocumentEntityService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "ListModelDocumentEntityService";
    
    /** Tipo da entidade */
    public static String IN_ENTITY_TYPE_NAME = "entityTypeName";
    public static String IN_APPLICATION_USER_OPT = "applicationUser";

    @SuppressWarnings("unchecked")
	public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            log.debug("Iniciando a execução do serviço CreateLabelFromEntityService");
            // Pega os argumentos
            String inEntityTypeName = (String) serviceData.getArgumentList().getProperty(IN_ENTITY_TYPE_NAME);
            IEntity<ApplicationUser> inApplicationUser = null;
            if(serviceData.getArgumentList().containsProperty(IN_APPLICATION_USER_OPT))
            	inApplicationUser = (IEntity<ApplicationUser>) serviceData.getArgumentList().getProperty(IN_APPLICATION_USER_OPT);
            
            StringBuffer entityClasses = new StringBuffer();
            try {
				for(Class<?> c: ClassUtils.getAllHierarchy(Class.forName(inEntityTypeName))){
					entityClasses.append("'");
					entityClasses.append(c.getName());
					entityClasses.append("'");
					entityClasses.append(',');
				}
				entityClasses.setLength(entityClasses.length()-1);
			} catch (ClassNotFoundException e) {
				throw new ServiceException(MessageList.createSingleInternalError(e));
			}
            
            List<SelectItem> result = null;
        	if(inApplicationUser==null)
        		/*  where entity.applicationEntity.className = inApplicationEntity.getInfo().getType().getName() */
        		result = this.getServiceManager().getEntityManager().getEntitySelectItems(ModelDocumentEntity.class, IDAO.ENTITY_ALIAS_HQL + "." + ModelDocumentEntity.APPLICATION_ENTITY + "." + ApplicationEntity.CLASS_NAME + " in (" + entityClasses.toString() +")");
        	else
        		/*  where entity.applicationEntity.className = inApplicationEntity.getInfo().getType().getName() AND (entity.applicationUser = inApplicationUser.getId() or entity.applicationUser is null)*/
        		result = this.getServiceManager().getEntityManager().getEntitySelectItems(ModelDocumentEntity.class, IDAO.ENTITY_ALIAS_HQL + "." + ModelDocumentEntity.APPLICATION_ENTITY + "." + ApplicationEntity.CLASS_NAME + " in (" + entityClasses.toString() +") AND"  +
        																										     "(" + IDAO.ENTITY_ALIAS_HQL + "." + ModelDocumentEntity.APPLICATION_USER  + "=" + inApplicationUser.getId() + " OR " + IDAO.ENTITY_ALIAS_HQL + "." + ModelDocumentEntity.APPLICATION_USER  + " is null)");            
        	
        	// Adiciona o registro criado no resultado no serviceData
            serviceData.getOutputData().add(result);
            
        } 
        catch (BusinessException e)
        {
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }
    }
    
    public String getServiceName() {return SERVICE_NAME;}
}