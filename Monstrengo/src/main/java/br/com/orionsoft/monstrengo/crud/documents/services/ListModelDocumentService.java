package br.com.orionsoft.monstrengo.crud.documents.services;

import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.documents.entities.ModelDocumentEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Este servi�o retorna a lista de modelos de documentos
 * que n�o utilizam entidades dinamicamente. Isto significa que a
 * propriedade ApplicationEntity do modelo � nula.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_APPLICATION_USER_OPT: Usu�rio que ser� usada para filtrar a pesquisa de modelos de documentos dispon�veis.
 * 
 * <p><b>Procedimento:</b>
 * <br>Pesquisa no banco todos os modelos de documento cuja entidade vinculada � nula.
 * <br><b>Retorna uma lista de itens de sele��o com o id e nome dos modelos.</b>
 * 
 * 
 * @spring.bean id="ListModelDocumentService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class ListModelDocumentService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "ListModelDocumentService";
    
    /** Tipo da entidade */
    public static String IN_APPLICATION_USER_OPT = "applicationUser";

    @SuppressWarnings("unchecked")
	public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            log.debug("Iniciando a execu��o do servi�o ListModelDocumentService");
            // Pega os argumentos
            IEntity<ApplicationUser> inApplicationUser = null;
            if(serviceData.getArgumentList().containsProperty(IN_APPLICATION_USER_OPT))
            	inApplicationUser = (IEntity<ApplicationUser>) serviceData.getArgumentList().getProperty(IN_APPLICATION_USER_OPT);
            
            List<SelectItem> result = null;
        	if(inApplicationUser==null)
        		/*  where entity.applicationEntity is null */
        		result = this.getServiceManager().getEntityManager().getEntitySelectItems(ModelDocumentEntity.class, IDAO.ENTITY_ALIAS_HQL + "." + ModelDocumentEntity.APPLICATION_ENTITY + " is null ");
        	else
        		/*  where entity.applicationEntity is null AND (entity.applicationUser = inApplicationUser.getId() or entity.applicationUser is null)*/
        		result = this.getServiceManager().getEntityManager().getEntitySelectItems(ModelDocumentEntity.class, IDAO.ENTITY_ALIAS_HQL + "." + ModelDocumentEntity.APPLICATION_ENTITY + " is null AND"  +
        																										     "(" + IDAO.ENTITY_ALIAS_HQL + "." + ModelDocumentEntity.APPLICATION_USER  + "=" + inApplicationUser.getId() + " OR " + IDAO.ENTITY_ALIAS_HQL + "." + ModelDocumentEntity.APPLICATION_USER  + " is null)");            
        	
        	// Adiciona o registro criado no resultado no serviceData
            serviceData.getOutputData().add(result);
            
        } 
        catch (BusinessException e)
        {
            // O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros.
            throw new ServiceException(e.getErrorList());
        }
    }
    
    public String getServiceName() {return SERVICE_NAME;}
}