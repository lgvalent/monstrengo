package br.com.orionsoft.monstrengo.crud.documents.processes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.documents.entities.ModelDocumentEntity;
import br.com.orionsoft.monstrengo.crud.documents.services.CompileCrudDocumentService;
import br.com.orionsoft.monstrengo.crud.documents.services.CompileFieldsDocumentService;
import br.com.orionsoft.monstrengo.crud.documents.services.ListModelDocumentEntityService;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este processo controla a criação de uma etiqueta para uma entidade.
 * Ele utiliza um modelo de etiqueta de entidade e 
 * também uma entidade de origem, ou um entityType + entityId para obter a entidade
 * de origem
 *
 * <p><b>Procedimentos:</b><br>
 * <li>Definir a entidade: <i>setEntityType(Class)</i> e <i>setEntityId(long)</i> ou <i>setEntity(IEntity)</i>
 * <li>O método getModelsDocumentEntity() retorna um lista de modelos que podem ser utilizados pelo atual operdos.
 * <li>Definir o modelo de documento da entidade: <i>setModelDocumentoENtityId(long)</i> ou <i>setModelDocumentoENtity(IEntity)</i>
 * <li>Executar runCompile() para compilar o documento.
 * <li>Obter o documento compilado pelo método <i>getResultCompiledDocument():String</i>
 * <li>Obter o mapa de campos para preenchimento pelo método <i>getResultDocumentFields(): Map<String, String></i>
 * <li>Executar runReplaceFields() para substituir os valores dos campos preenchidos no mapa.
 * <li>Obter o documento compilado e com campos preenchidos pelo método <i>getResultCompiledDocumentFields():String</i>
 * 
 * @spring.bean id="CompileDocumentProcess" init-method="start" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 *
 */
public class CompileDocumentProcess extends ProcessBasic
{
    public static final String PROCESS_NAME = "CompileDocumentProcess";

    private IEntity entity=null;
    private IEntity modelDocumentEntity=null;
    
    /**
     * Se estes parâmetros forem mudados os objetos entity é invalidado (=null)
     */
    private Class entityType=null;
    private long entityId;

    /**
     * Se estes parâmetro for mudado o modelLabelEntity é invalidado (=null)
     */
    private long modelDocumentEntityId;
    
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
   		this.entityId = entityId;
    		this.entity = null;
	}

	public IEntity getModelDocumentEntity() throws BusinessException{
		if(modelDocumentEntity==null){
		   modelDocumentEntity = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(),
                    ModelDocumentEntity.class,
                    this.modelDocumentEntityId,
                    null);
		}
		return modelDocumentEntity;
	}
	public void setModelDocumentEntity(IEntity modelDocumentEntity){
		this.modelDocumentEntity = modelDocumentEntity;
	}

	public long getModelDocumentEntityId(){return modelDocumentEntityId;}
	public void setModelDocumentEntityId(long modelDocumentEntityId){
		/* Lucio - 10/04/07: Agora força a carga do modelo, pois o mesmo pode ter sido
		 * alterado no banco e o operador está tentando verificar as alterações realizadas. */
		this.modelDocumentEntityId = modelDocumentEntityId;
		this.modelDocumentEntity = null;
	}
 
	/**
	 *  O tipo da entidade já deve estar definido para executar este método, para que ele mostra somente os modelos 
	 *  da entidade selecionada
	 */
	public List<SelectItem> getModelsDocumentEntity(){
    	try{
			ServiceData sd = new ServiceData(ListModelDocumentEntityService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(ListModelDocumentEntityService.IN_APPLICATION_USER_OPT, this.getUserSession().getUser());
			sd.getArgumentList().setProperty(ListModelDocumentEntityService.IN_ENTITY_TYPE_NAME, this.entityType.getName());
			this.getProcessManager().getServiceManager().execute(sd);
			
    		return sd.getFirstOutput();
		}catch (ServiceException e){
            this.getMessageList().clear();
            this.getMessageList().addAll(e.getErrorList()); 
			return null;
		}
    }

    @SuppressWarnings("unchecked")
	public boolean runCompileCrudExpression()
    {
        super.beforeRun();

        try
        {
            /* Cria a etiqueta  */
            ServiceData sdCreateDocument = new ServiceData(CompileCrudDocumentService.SERVICE_NAME, null);

            if(this.getEntityId() != IDAO.ENTITY_UNSAVED)
            	sdCreateDocument.getArgumentList().setProperty(CompileCrudDocumentService.IN_ENTITY_OPT, this.getEntity());
            sdCreateDocument.getArgumentList().setProperty(CompileCrudDocumentService.IN_MODEL_DOCUMENT_ENTITY, this.getModelDocumentEntity());
            
            this.getProcessManager().getServiceManager().execute(sdCreateDocument);
            
            /* Armazena o documento compilado */
            this.compiledCrudDocument = (String) sdCreateDocument.getOutputData(CompileCrudDocumentService.OUT_COMPILED_DOCUMENT);
            
            /* Armazena op mapa com os campos que foram encontrados no documento */
            this.documentFieldsMap = (Map<String, String>) sdCreateDocument.getOutputData(CompileCrudDocumentService.OUT_FIELDS_MAP);
            
            if(!sdCreateDocument.getMessageList().isTransactionSuccess()){
                this.getMessageList().addAll(sdCreateDocument.getMessageList()); 
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
    
    @SuppressWarnings("unchecked")
	public boolean runCompileFieldsExpression()
    {
        super.beforeRun();

        try
        {
            ServiceData sdCreateDocument = new ServiceData(CompileFieldsDocumentService.SERVICE_NAME, null);
            sdCreateDocument.getArgumentList().setProperty(CompileFieldsDocumentService.IN_DOCUMENT_SOURCE, compiledCrudDocument);
            sdCreateDocument.getArgumentList().setProperty(CompileFieldsDocumentService.IN_DOCUMENT_FIELDS_MAP, documentFieldsMap);
            
            this.getProcessManager().getServiceManager().execute(sdCreateDocument);
            
            /* Armazena o documento compilado */
            this.compiledFieldsDocument = sdCreateDocument.getFirstOutput();
            
            if(!sdCreateDocument.getMessageList().isTransactionSuccess()){
                this.getMessageList().addAll(sdCreateDocument.getMessageList()); 
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

    private String compiledCrudDocument;
	/**
	 * Retorna um documento compilado sem os campos
	 **/
    public String getCompiledCrudDocument(){return compiledCrudDocument;}
	public void setCompiledCrudDocument(String compiledCrudDocument){this.compiledCrudDocument = compiledCrudDocument;}

    /**
     * Controla os campos extraidos do documento durante o runCompileCrudExpression().
     * É inicializado com um map vazio, para permitir a pré visualização do documento
     * sem compilar o CrudEpression.
     */
	private Map<String, String> documentFieldsMap = new HashMap<String, String>(0);
	public Map<String, String> getDocumentFieldsMap(){return documentFieldsMap;}
	
    private String compiledFieldsDocument;
	public String getCompiledFieldsDocument(){return compiledFieldsDocument;}

}