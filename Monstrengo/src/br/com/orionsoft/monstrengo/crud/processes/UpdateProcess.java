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
 * Este processo controla a edição de uma entidade do sistema.
 * 
 * <p><b>Procedimentos:</b>
 * <br>Definir o tipo da entidade: <i>setEntityType(Class)</i>
 * <br>Definir o id da entidade: <i>setEntityId(long)</i>
 * <br>Verificar se a entidade pode ser editada: <i>boolean mayEdit()</i>
 * <br>Obter a entidade por <i>(IEntity) retrieveEntity()</i>.
 * <li>Realizar edições pela interface com o usuário.
 * <br>Gravar as alterações por <i>runUpdate()</i>.
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
            // depois que o operador concluiu a alteraçao, solicita a validacao pelo DVO
            if(this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().contains(entity))
            	this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().getDvoByEntity(entity).afterUpdate(entity, this.getUserSession(), null);

            // Atualiza a entidade
            UtilsCrud.update(this.getProcessManager().getServiceManager(),
                             entity, 
                             null);
            
            // Registra a auditoria da atualização
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

    // Verifica se o user tem permissão de edição.
    public boolean mayEdit() throws BusinessException
    {
        return UtilsSecurity.checkRightUpdate(this.getProcessManager().getServiceManager(), this.entityType, this.getUserSession(), null);
    }
    
    /**
     * Obtem a entidade persistida baseado no Tipo e Id fornecidos.
     * Verifica se a edição será possível, senão lança uma exceção.  
     * Se a entidade ainda não foi obtidade pelo processo, o serviço
     * é excutado e os valores da auditoria são preparados.
     * Caso a entidade já esteja preparada, ela é retornada. 
     * @return Uma entidade pronta para a edição.
     * @throws BusinessException
     */
    public IEntity<T> retrieveEntity() throws BusinessException
    {
        // Verificar se pode editar a entidade
        if (this.mayEdit())
        {    
            // Se ainda não está pronta a entidade, prepara-a
            if (entity == null)
            {
            	// Cria uma entidade temporária para ser validade pelo dvo
            	IEntity<T> entityTemp = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(),
                                            entityType,
                                            entityId, 
                                            null);
            	
            	// Se houver algum problema, uma exceção será levantada aqui
            	if(this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().contains(entityTemp))
            		this.getProcessManager().getServiceManager().getEntityManager().getDvoManager().getDvoByEntity(entityTemp).beforeUpdate(entityTemp, this.getUserSession(), null);
            	
            	// Se nada aconteceu, é porque está tudo OK e a entidade poderá prosseguir para sua exclusão.
            	entity = entityTemp;
                
                // Prepara a descricao inicial da auditoria para
                // verificar o que será alterado
                this.entityAuditValue = new EntityAuditValue(entity);
                /* Lucio 22/11/06
                 * Verifica as propriedades oneToOne:
                 *  1: podem estar nulas porque não foram cadastradas ainda
                 *  2: preparar os valores da auditoria */
                this.entityOneToOnePropertiesAuditValue = new ArrayList<EntityAuditValue>();
                Collection<IProperty> col = entity.getPropertiesMap().values();
                for(IProperty prop: col){
                	if(prop.getInfo().isEntity()&&prop.getInfo().isEditShowEmbedded()&&!prop.getInfo().isCollection()){
                		/* Verifica se a propriedade oneToOne é nula para criá-la antes da auditoria */
                		if(prop.getValue().isValueNull()){
                			/* Criando a nova entidade */
                			IEntity<?> entityOneToOne = UtilsCrud.create(this.getProcessManager().getServiceManager(), prop.getInfo().getType(), null);
                			/* Usando a nova entidade no relacionamento OneToOne */
                			prop.getValue().setAsEntity(entityOneToOne);
                		}

                		/* Preparando as valores de auditoria */
                		this.entityOneToOnePropertiesAuditValue.add(new EntityAuditValue(prop.getValue().getAsEntity()));
                	}

                	/* Lucio 20120821: Por padrão, dos as entidades vem isSelected(), este trecho desmarca todas para que o operador
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

        // Não possui direitos de editar este tipo de entidade
        throw new ProcessException(MessageList.create(UpdateProcess.class, "UPDATE_DENIED", getUserSession().getUserLogin(), this.getProcessManager().getServiceManager().getEntityManager().getEntityMetadata(this.entityType).getLabel() + ":" + this.entityId));
        
    }
    
    /**
     * Este método anula a atual instância da entidade do processo e recarrega novamente
     * sem as alterações realizadas até aqui.
     * @return
     * @throws BusinessException 
     * @since 20060322
     */
    public void runReload() throws BusinessException{
        super.beforeRun();

        /* Anula a atual instância */
    	entity = null;
    	/* Recarrega */
    	retrieveEntity();
    }
    
    /**
     * Este método permite executar uma validação no DVO antes de 
     * confirmar propriamente o update. Assim, o operador poderá
     * verificar possíveis erros de validação sem confirmar um Update 
     * @return
     * @throws BusinessException 
     * @since 20080113
     */
    public boolean runValidate(){
        super.beforeRun();
        
        // solicitando a validação da entidade pelo DvoManager
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

    public Class<T> getEntityType()
    {
        return entityType;
    }
    
    public void setEntityType(Class<T> entityType)
    {
        this.entityType = entityType;
    }
    
}
