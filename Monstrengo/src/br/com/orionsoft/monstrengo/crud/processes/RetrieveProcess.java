package br.com.orionsoft.monstrengo.crud.processes;

import br.com.orionsoft.monstrengo.crud.processes.RetrieveProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;

/**
 * Este processo controla a visualização de uma entidade do sistema.
 * Controlando também as permissões.
 * 
 * <p><b>Procedimentos:</b>
 * <br>Definir o tipo da entidade: <i>setEntityType(Class)</i>
 * <br>Definir o id da entidade: <i>setEntityId(long)</i>
 * <br>Verificar se a entidade pode ser visualizada: <i>boolean mayView()</i>
 * <br>Obter a entidade por <i>(IEntity) retrieveEntity()</i>.
 * 
 * @author Lucio 20060207
 * @version 20060207
 *
 * @spring.bean id="RetrieveProcess" init-method="start" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 *
 */
public class RetrieveProcess <T> extends ProcessBasic
{
    public static final String PROCESS_NAME = "RetrieveProcess";

    private Class<T> entityType;
    private long entityId;
    private IEntity<T> entity=null;
    
    public String getProcessName(){return PROCESS_NAME;}
    
    /** Verifica se o user tem permissão de visualização */
    public boolean mayView() throws BusinessException
    {
        return UtilsSecurity.checkRightRetrieve(this.getProcessManager().getServiceManager(), this.entityType, this.getUserSession(), null);
    }
    
    /**
     * Obtem a entidade persistida baseado no Tipo e Id fornecidos.
     * Verifica se a visualização será possível, senão lança uma exceção.  
     * Se a entidade ainda não foi obtidade pelo processo, o serviço
     * é excutado e os valores da auditoria são preparados.
     * Caso a entidade já esteja preparada, ela é retornada. 
     * @return Uma entidade pronta para a visualização.
     * @throws BusinessException
     */
    public IEntity<T> retrieveEntity() throws BusinessException
    {
        // Verificar se pode editar a entidade
        if (this.mayView())
        {    
            // Se ainda não está pronta a entidade, prepara-a
            if (entity == null)
            {
                entity = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(),
                                            entityType,
                                            entityId, 
                                            null);
            }
            
            return entity;
        }

        // Não possui direitos de editar este tipo de entidade
        throw new ProcessException(MessageList.create(RetrieveProcess.class, "RETRIEVE_DENIED", getUserSession().getUserLogin(), this.getProcessManager().getServiceManager().getEntityManager().getEntityMetadata(this.entityType).getLabel() + ":" + this.entityId));
        
    }
    
    public long getEntityId(){return entityId;}
    public void setEntityId(long entityId)
    {
    	if (this.entityId!=entityId){
    		this.entityId = entityId;

    		this.entity=null;
    	}
    
    }

    public Class<T> getEntityType(){return entityType;}
    public void setEntityType(Class<T> entityType)
    {
    	if (this.entityType!=entityType)
    	this.entityType = entityType;
    	
    	this.entity=null;
    }
    
}
