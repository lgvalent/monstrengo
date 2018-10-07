package br.com.orionsoft.monstrengo.crud.labels.processes;

import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.labels.entities.AddressLabelGroup;
import br.com.orionsoft.monstrengo.crud.labels.entities.ModelLabelEntity;
import br.com.orionsoft.monstrengo.crud.labels.services.CreateLabelFromEntityService;
import br.com.orionsoft.monstrengo.crud.labels.services.ListModelLabelEntityService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este processo controla a criação de uma etiqueta para uma entidade.
 * Ele utiliza um modelo de etiqueta de entidade e 
 * também uma entidade de origem, ou um entityType + entityId para obter a entidade
 * de origem
 *
 * @spring.bean id="CreateLabelFromEntityProcess" init-method="start" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 *
 */
public class CreateLabelFromEntityProcess extends ProcessBasic
{
    public static final String PROCESS_NAME = "CreateLabelFromEntityProcess";

    private IEntity entity=null;
    private IEntity modelLabelEntity=null;
    private IEntity addressLabelGroup=null;
    
    /**
     * Se estes parâmetros forem mudados os objetos entity é invalidado (=null)
     */
    private Class entityType=null;
    private long entityId;

    /**
     * Se estes parâmetro for mudado o modelLabelEntity é invalidado (=null)
     */
    private long modelLabelEntityId;
    private long addressLabelGroupId;
    
    public String getProcessName(){return PROCESS_NAME;}
    
	public IEntity getEntity() throws BusinessException{
		if(entity==null){
			entity = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(),
					                          this.entityType,
					                          this.entityId,
					                          null);
		}
		
		return entity;
	}
	
	public IEntity getAddressLabelGroup() throws BusinessException {
		if(addressLabelGroup==null){
			if (this.addressLabelGroupId==IDAO.ENTITY_UNSAVED)
				addressLabelGroup = null;
			else
				addressLabelGroup = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(),
					                          AddressLabelGroup.class,
					                          this.addressLabelGroupId,
					                          null);
		}
		
		return addressLabelGroup;
	}

	/**
	 * Utilizando este método, automaticamente, os id e type da entidade
	 * serão preenchidos.
	 * Caso contrário, deve ser fornecido o tipo e o id.
	 * @param entity
	 * @throws EntityException 
	 */
	public void setEntity(IEntity sourceEntity) throws EntityException{
		this.entity = sourceEntity;
		if(sourceEntity!=null){
			this.entityId = sourceEntity.getId();
			this.entityType = sourceEntity.getInfo().getType();
		}
	}

	public Class getEntityType(){return entityType;}
    public void setEntityType(Class entityType){
    	if(this.entityType != entityType){
    		this.entityType = entityType;
    		this.entity = null;
    	}
    }

	public long getEntityId(){return entityId;}
	public void setEntityId(long entityId){
    	if(this.entityId != entityId){
    		this.entityId = entityId;
    		this.entity = null;
    	}
	}

	public IEntity getModelLabelEntity() throws BusinessException{
		if(modelLabelEntity==null){
		   modelLabelEntity = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(),
                    ModelLabelEntity.class,
                    this.modelLabelEntityId,
                    null);
		}
		return modelLabelEntity;
	}
	public void setModelLabelEntity(IEntity modelLabelEntity){
		this.modelLabelEntity = modelLabelEntity;
	}

	public long getModelLabelEntityId(){return modelLabelEntityId;}
	public void setModelLabelEntityId(long modelLabelEntityId){
    	if(this.modelLabelEntityId != modelLabelEntityId){
    		this.modelLabelEntityId = modelLabelEntityId;
    		this.modelLabelEntity = null;
    	}
	}
 
	public long getAddressLabelGroupId() {return addressLabelGroupId;}
	public void setAddressLabelGroupId(long addressLabelGroupId) {
		this.addressLabelGroupId = addressLabelGroupId;
    	if(this.addressLabelGroupId != addressLabelGroupId){
    		this.addressLabelGroupId = addressLabelGroupId;
    		this.addressLabelGroup = null;
    	}
		
	}
	/**
	 *  O tipo da entidade já deve estar definido para executar este método, para que ele mostra somente os modelos 
	 *  da entidade selecionada
	 */
	public List<SelectItem> getModelsLabelEntity(){
    	try{
			ServiceData sd = new ServiceData(ListModelLabelEntityService.SERVICE_NAME, null);
			sd.getArgumentList().setProperty(ListModelLabelEntityService.IN_ENTITY_TYPE_NAME, this.entityType.getName());
			this.getProcessManager().getServiceManager().execute(sd);
			
    		return sd.getFirstOutput();
		}catch (ServiceException e){
            this.getMessageList().clear();
            this.getMessageList().addAll(e.getErrorList()); 
			return null;
		}
    }

    public boolean runCreate()
    {
        super.beforeRun();

        try
        {
            /* Cria a etiqueta  */
            ServiceData sdCreateLabel = new ServiceData(CreateLabelFromEntityService.SERVICE_NAME, null);
            sdCreateLabel.getArgumentList().setProperty(CreateLabelFromEntityService.IN_APPLICATION_USER, this.getUserSession().getUser());
            sdCreateLabel.getArgumentList().setProperty(CreateLabelFromEntityService.IN_ENTITY, this.getEntity());
            sdCreateLabel.getArgumentList().setProperty(CreateLabelFromEntityService.IN_MODEL_LABEL_ENTITY, this.getModelLabelEntity());
            sdCreateLabel.getArgumentList().setProperty(CreateLabelFromEntityService.IN_ADDRESS_LABEL_GROUP_OPT, this.getAddressLabelGroup());
            
            this.getProcessManager().getServiceManager().execute(sdCreateLabel);
            
            if(!sdCreateLabel.getMessageList().isTransactionSuccess()){
                this.getMessageList().addAll(sdCreateLabel.getMessageList()); 
            	return false;
            }
            
            return true;
        }
        catch(BusinessException e)
        {
            // Armazenando a lista de erros;
            this.getMessageList().addAll(e.getErrorList());
            return false;
        }
    }


}