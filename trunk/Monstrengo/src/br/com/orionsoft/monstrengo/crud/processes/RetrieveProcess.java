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
 * Este processo controla a visualiza��o de uma entidade do sistema.
 * Controlando tamb�m as permiss�es.
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
    
    /** Verifica se o user tem permiss�o de visualiza��o */
    public boolean mayView() throws BusinessException
    {
        return UtilsSecurity.checkRightRetrieve(this.getProcessManager().getServiceManager(), this.entityType, this.getUserSession(), null);
    }
    
    /**
     * Obtem a entidade persistida baseado no Tipo e Id fornecidos.
     * Verifica se a visualiza��o ser� poss�vel, sen�o lan�a uma exce��o.  
     * Se a entidade ainda n�o foi obtidade pelo processo, o servi�o
     * � excutado e os valores da auditoria s�o preparados.
     * Caso a entidade j� esteja preparada, ela � retornada. 
     * @return Uma entidade pronta para a visualiza��o.
     * @throws BusinessException
     */
    public IEntity<T> retrieveEntity() throws BusinessException
    {
        // Verificar se pode editar a entidade
        if (this.mayView())
        {    
            // Se ainda n�o est� pronta a entidade, prepara-a
            if (entity == null)
            {
                entity = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(),
                                            entityType,
                                            entityId, 
                                            null);
            }
            
            return entity;
        }

        // N�o possui direitos de editar este tipo de entidade
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
