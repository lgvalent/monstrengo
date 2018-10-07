package br.com.orionsoft.monstrengo.crud.processes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.orionsoft.monstrengo.crud.processes.UpdateProcess;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.auditorship.support.EntityAuditValue;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;

/**
 * Este processo controla a edi��o de uma entidade do sistema.
 * 
 * <p><b>Procedimentos:</b>
 * <br>Definir o tipo da entidade: <i>setEntityType(Class)</i>
 * <br>Definir o id da entidade: <i>setEntityId(long)</i>
 * <br>Verificar se a entidade pode ser editada: <i>boolean mayEdit()</i>
 * <br>Obter a entidade por <i>(IEntity) retrieveEntity()</i>.
 * <li>Realizar edi��es pela interface com o usu�rio.
 * <br>Gravar as altera��es por <i>runUpdate()</i>.
 * 
 * @author Lucio 2005/09/30
 * @version 20060322
 *
 * @spring.bean id="UpdateProcess" init-method="start" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 *
 */
public class UpdateProcess <T> extends ProcessBasic
{
    public static final String PROCESS_NAME = "UpdateProcess";

    private Class<T> entityType;
    private long entityId;
    private IEntity<T> entity=null;
    
    // Auditoria
    private EntityAuditValue entityAuditValue;
    private List<EntityAuditValue> entityOneToOnePropertiesAuditValue;
    
    public String getProcessName(){return PROCESS_NAME;}
    
    public boolean runUpdate()
    {
        super.beforeRun();

        try
        {
            // depois que o operador concluiu a altera�ao, solicita a validacao pelo DVO
            if(this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().contains(entity))
            	this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().getDvoByEntity(entity).afterUpdate(entity, this.getUserSession(), null);

            // Atualiza a entidade
            UtilsCrud.update(this.getProcessManager().getServiceManager(),
                             entity, 
                             null);
            
            // Registra a auditoria da atualiza��o
            UtilsAuditorship.auditUpdate(this.getProcessManager().getServiceManager(), 
                                         this.getUserSession(),
                                         this.entityAuditValue, null);
            
            // Registra a auditoria das propriedades oneToOne
            for(EntityAuditValue auditValue: this.entityOneToOnePropertiesAuditValue)
                UtilsAuditorship.auditUpdate(this.getProcessManager().getServiceManager(), 
                        this.getUserSession(),
                        auditValue, null);
            
            // Grava a mensagem de sucesso
            this.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, UpdateProcess.class, "UPDATE_SUCCESS", this.entity.getInfo().getLabel() + ":" + this.entity.toString()));
            
            // Libera a entidade atual
            entity = null;
            return true;
        }
        catch(BusinessException e)
        {
            // Gravando a mensagem de falha
            e.getErrorList().add(new BusinessMessage(UpdateProcess.class, "UPDATE_FAILURE", this.entity.getInfo().getLabel() + ":" + this.entity.toString()));
            // Armazenando a lista de erros;
            this.getMessageList().addAll(e.getErrorList());
            return false;
        }
    }

    // Verifica se o user tem permiss�o de edi��o.
    public boolean mayEdit() throws BusinessException
    {
        return UtilsSecurity.checkRightUpdate(this.getProcessManager().getServiceManager(), this.entityType, this.getUserSession(), null);
    }
    
    /**
     * Obtem a entidade persistida baseado no Tipo e Id fornecidos.
     * Verifica se a edi��o ser� poss�vel, sen�o lan�a uma exce��o.  
     * Se a entidade ainda n�o foi obtidade pelo processo, o servi�o
     * � excutado e os valores da auditoria s�o preparados.
     * Caso a entidade j� esteja preparada, ela � retornada. 
     * @return Uma entidade pronta para a edi��o.
     * @throws BusinessException
     */
    public IEntity<T> retrieveEntity() throws BusinessException
    {
        // Verificar se pode editar a entidade
        if (this.mayEdit())
        {    
            // Se ainda n�o est� pronta a entidade, prepara-a
            if (entity == null)
            {
            	// Cria uma entidade tempor�ria para ser validade pelo dvo
            	IEntity<T> entityTemp = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(),
                                            entityType,
                                            entityId, 
                                            null);
            	
            	// Se houver algum problema, uma exce��o ser� levantada aqui
            	if(this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().contains(entityTemp))
            		this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().getDvoByEntity(entityTemp).beforeUpdate(entityTemp, this.getUserSession(), null);
            	
            	// Se nada aconteceu, � porque est� tudo OK e a entidade poder� prosseguir para sua exclus�o.
            	entity = entityTemp;
                
                // Prepara a descricao inicial da auditoria para
                // verificar o que ser� alterado
                this.entityAuditValue = new EntityAuditValue(entity);
                /* Lucio 22/11/06
                 * Verifica as propriedades oneToOne:
                 *  1: podem estar nulas porque n�o foram cadastradas ainda
                 *  2: preparar os valores da auditoria */
                this.entityOneToOnePropertiesAuditValue = new ArrayList<EntityAuditValue>();
                Collection<IProperty> col = entity.getPropertiesMap().values();
                for(IProperty prop: col){
                	if(prop.getInfo().isEntity()&&prop.getInfo().isEditShowEmbedded()&&!prop.getInfo().isCollection()){
                		/* Verifica se a propriedade oneToOne � nula para cri�-la antes da auditoria */
                		if(prop.getValue().isValueNull()){
                			/* Criando a nova entidade */
                			IEntity<?> entityOneToOne = UtilsCrud.create(this.getProcessManager().getServiceManager(), prop.getInfo().getType(), null);
                			/* Usando a nova entidade no relacionamento OneToOne */
                			prop.getValue().setAsEntity(entityOneToOne);
                		}

                		/* Preparando as valores de auditoria */
                		this.entityOneToOnePropertiesAuditValue.add(new EntityAuditValue(prop.getValue().getAsEntity()));
                	}

                	/* Lucio 20120821: Por padr�o, dos as entidades vem isSelected(), este trecho desmarca todas para que o operador
                	 * marque as que ele deseja agir */
                	if(prop.getInfo().isEntity()&&prop.getInfo().isCollection()){
                		for(IEntity<?> entity:prop.getValue().getAsEntityCollection()){
                			entity.setSelected(false);
                		}
                	}
                }
            }
            
            return entity;
        }

        // N�o possui direitos de editar este tipo de entidade
        throw new ProcessException(MessageList.create(UpdateProcess.class, "UPDATE_DENIED", getUserSession().getUserLogin(), this.getProcessManager().getServiceManager().getEntityManager().getEntityMetadata(this.entityType).getLabel() + ":" + this.entityId));
        
    }
    
    /**
     * Este m�todo anula a atual inst�ncia da entidade do processo e recarrega novamente
     * sem as altera��es realizadas at� aqui.
     * @return
     * @throws BusinessException 
     * @since 20060322
     */
    public void runReload() throws BusinessException{
        super.beforeRun();

        /* Anula a atual inst�ncia */
    	entity = null;
    	/* Recarrega */
    	retrieveEntity();
    }
    
    /**
     * Este m�todo permite executar uma valida��o no DVO antes de 
     * confirmar propriamente o update. Assim, o operador poder�
     * verificar poss�veis erros de valida��o sem confirmar um Update 
     * @return
     * @throws BusinessException 
     * @since 20080113
     */
    public boolean runValidate(){
        super.beforeRun();
        
        // solicitando a valida��o da entidade pelo DvoManager
        try {
			if(this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().contains(entity)){
				this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().getDvoByEntity(entity).afterUpdate(entity, this.getUserSession(), null);
			}
		} catch (BusinessException e) {
			super.getMessageList().addAll(e.getErrorList());
	    	return false;
		}

    	return true;
    }
    
    public long getEntityId()
    {
        return entityId;
    }
    
    public void setEntityId(long entityId)
    {
        this.entityId = entityId;
    }

    public Class<?> getEntityType()
    {
        return entityType;
    }
    
    public void setEntityType(Class<T> entityType)
    {
        this.entityType = entityType;
    }
    
}
