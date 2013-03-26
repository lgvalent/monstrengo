package br.com.orionsoft.monstrengo.crud.labels.services;

import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.ClassUtils;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.labels.entities.ModelLabelEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;

/**
 * Este serviço retorna a lista de modelos de etiquetas
 * para uma determinada entidade.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_ENTITY_TYPE_NAME: Nome do tipo da entidade que será usada para pesquisar os modelos de etiquestas disponíveis.
 * 
 * <p><b>Procedimento:</b>
 * <br>Pesquisa no banco todos os modelos de etiquetas para entidade cujo nome da entidade seja igual ao da entidade passada.
 * <br><b>Retorna uma lista de itens de seleção com o id e nome dos modelos.</b>
 * 
 * 
 * @spring.bean id="ListModelLabelEntityService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class ListModelLabelEntityService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "ListModelLabelEntityService";
    
    /** Tipo da entidade */
    public static String IN_ENTITY_TYPE_NAME = "entityTypeName";

    public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            log.debug("Iniciando a execução do serviço CreateLabelFromEntityService");
            // Pega os argumentos
            String inEntityTypeName = (String) serviceData.getArgumentList().getProperty(IN_ENTITY_TYPE_NAME);

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
            
            
        	List<SelectItem> result = this.getServiceManager().getEntityManager().getEntitySelectItems(ModelLabelEntity.class, IDAO.ENTITY_ALIAS_HQL + "." + ModelLabelEntity.APPLICATION_ENTITY + "." + ApplicationEntity.CLASS_NAME + " in (" + entityClasses.toString() +")");
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