package br.com.orionsoft.monstrengo.view.jsf.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.documents.entities.ModelDocumentEntity;
import br.com.orionsoft.monstrengo.crud.documents.processes.CompileDocumentProcess;
import br.com.orionsoft.monstrengo.crud.documents.services.ListModelDocumentEntityService;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Está classe é o controlador da visão de geração de documentos 
 * para uma entidade específica
 * usando um modelo.
 * 
 * @author Lucio 20061009
 * @version 20061009
 * 
 * @jsf.bean name="documentEntityBean" scope="session"
 * @jsf.navigation from="*" result="documentView" to="/pages/basic/documentView.jsp" 
*/
@ManagedBean
@SessionScoped
public class DocumentEntityBean extends CrudBasicBean
{
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que é ativada  */
	public static final String FACES_VIEW_DOCUMENT_VIEW = "/pages/basic/documentView?faces-redirect=true";
	public static final String FACES_VIEW_DOCUMENT_LIST = "/pages/basic/documentList?faces-redirect=true";
	public static final String FACES_VIEW_DOCUMENT_PRINT = "/pages/basic/documentPrint?faces-redirect=true";
    
	public static final String URL_PARAM_DOCUMENT_ID = "documentId";

	private long modelDocumentEntityId = IDAO.ENTITY_UNSAVED;
    
//    private String resultDocumentEntity;
    private String resultDocumentEntityName;
	public String getResultDocumentEntityName() {return resultDocumentEntityName;}
    
	public long getModelDocumentEntityId() {return modelDocumentEntityId;}
	public void setModelDocumentEntityId(long modelDocumentId) {this.modelDocumentEntityId = modelDocumentId;}

    public CompileDocumentProcess compileDocumentProcess = null;

    public List<FieldEntry> documentFieldsEntry = new ArrayList<FieldEntry>();
    public List<FieldEntry> getDocumentFieldsEntry(){return documentFieldsEntry;}
    
    public class FieldEntry{
    	private String key;
    	private String value;
    	
    	public FieldEntry(String key, String value){
    		this.key = key;
    		this.value = value;
    	}
    	
		public String getKey(){return key;}
		public void setKey(String key){this.key = key;}
		
		public String getValue(){return value;}
		public void setValue(String value){this.value = value;}
    }

    /**
     * Esta action permite imprimir uma lista de entidades utilizando um modelo de documento.
     * Cada documento é compilado separadamente. O seu código HTML é então armazenado em um buffer
     * até que todas as entidades tenham sido compiladas.<br>
     * Este buffer então é devolvido para o processo CompileDocumentProcess como sendo o 
     * código fonte geral. Isto porque o próximo passo para a impressão de documentos 
     * consiste em analisar os FieldsEntry e gerar o documento final. 
     * @return
     */
    public String actionPrepareEntitiesToPrint(List<IEntity<?>> entities, long modelDocumentId) throws Exception{
		/* Buffer dos documentos compilados */
    	StringBuffer compiledDocumentsBuffer = new StringBuffer();
    	boolean firstTime = true;
    	
    	getCompileDocumentProcess().setModelDocumentEntityId(modelDocumentId);
    	for(IEntity<?> entity: entities){
    		getCompileDocumentProcess().setEntity(entity);

    		/* A visão FACES_VIEW_DOCUMENT_VIEW pegará o documento compilado diretamente do processo.
    		 * #{documentEntityBean.currentProcess.resultDocumentEntity}*/
    		if(getCompileDocumentProcess().runCompileCrudExpression()){

    			/* Esta preparação é igual para todos os documentos gerados aqui, pois todos são do mesmo modelo.
    			 * Então isto é feito somente uma vez */
    			if(firstTime){
    				this.resultDocumentEntityName = getCompileDocumentProcess().getModelDocumentEntity().getProperty(ModelDocumentEntity.NAME).getValue().getAsString();
    				this.documentFieldsEntry.clear();
    				for(Entry<String,String> entry: getCompileDocumentProcess().getDocumentFieldsMap().entrySet())
    					this.documentFieldsEntry.add(new FieldEntry(entry.getKey().toString(), entry.getValue().toString()));
    				
    				firstTime = false;
    			}
    			
    			/* Adicona no buffer o atual documento compilado */
    			compiledDocumentsBuffer.append(this.getCompileDocumentProcess().getCompiledCrudDocument());

    		}else{
    			FacesUtils.addErrorMsg("FALHA ao criar o documento");
    			FacesUtils.addErrorMsgs(getCompileDocumentProcess().getMessageList());
    			return FacesUtils.FACES_VIEW_FAILURE;
    		}
        }
    	
    	/* Devolve o buffer para o processo */
    	this.getCompileDocumentProcess().setCompiledCrudDocument(compiledDocumentsBuffer.toString());
    	
		FacesUtils.addInfoMsg("Documento gerado com SUCESSO");
		FacesUtils.addInfoMsgs(getCompileDocumentProcess().getMessageList());
		return FACES_VIEW_DOCUMENT_VIEW;
    }
    
    /**
     * Action que prepara os parâmetros de EntityType, EntityId e cria um processo
     * para compilar o modelo de documento utilizando a entidade passada.
     * @return
     */
    public String actionPreparePrint(IEntity<?> entity, long modelDocumentId) throws Exception{
        getCompileDocumentProcess().setEntity(entity);
        getCompileDocumentProcess().setModelDocumentEntityId(modelDocumentId);

        /* A visão FACES_VIEW_DOCUMENT_VIEW pegará o documento compilado diretamente do processo.
         * #{documentEntityBean.currentProcess.resultDocumentEntity}*/
        if(getCompileDocumentProcess().runCompileCrudExpression()){
        	/* Armazena o resultado */
//        	this.resultDocumentEntity = getCompileDocumentProcess().getCompiledCrudDocument();
        	this.resultDocumentEntityName = getCompileDocumentProcess().getModelDocumentEntity().getProperty(ModelDocumentEntity.NAME).getValue().getAsString();
        	this.documentFieldsEntry.clear();
        	for(Entry<String, String> entry: getCompileDocumentProcess().getDocumentFieldsMap().entrySet())
        		this.documentFieldsEntry.add(new FieldEntry(entry.getKey().toString(), entry.getValue().toString()));
        	
        	FacesUtils.addInfoMsg("Documento gerado com SUCESSO");
        	FacesUtils.addInfoMsgs(getCompileDocumentProcess().getMessageList());
            return FACES_VIEW_DOCUMENT_VIEW;
        }else{
        	FacesUtils.addErrorMsg("FALHA ao criar o documento");
        	FacesUtils.addErrorMsgs(getCompileDocumentProcess().getMessageList());
        	return FacesUtils.FACES_VIEW_FAILURE;
        }
    }
    
    /**
     * Action que prepara os parâmetros de EntityType, EntityId e cria um processo
     * para compilar o modelo de documento utilizando a entidade passada.
     * @return
     */
    public String actionCompileFromEntity(String entityClassName, long entityId) throws Exception
    {
    	log.debug("::Iniciando actionCompileFromEntity");
    	IEntity<?> entity = UtilsCrud.retrieve(this.getApplicationBean().getProcessManager().getServiceManager(), Class.forName(entityClassName), entityId, null);

    	return this.actionPreparePrint(entity, this.modelDocumentEntityId);// Este parâmetro deve ser injetado diretamente pelo SUBMIT
    }
    
    /**
     * Action que prepara os parâmetros de EntityType, EntityId e cria um processo
     * para compilar o modelo de documento utilizando a entidade passada.
     * @return
     */
    public String actionCompileFromEntity(String entityClassName, long entityId, long modelId) throws Exception
    {
    	log.debug("::Iniciando actionCompileFromEntity");
    	IEntity<?> entity = UtilsCrud.retrieve(this.getApplicationBean().getProcessManager().getServiceManager(), Class.forName(entityClassName), entityId, null);

    	return this.actionPreparePrint(entity, modelId);// Este parâmetro deve ser injetado diretamente pelo SUBMIT
    }
/**
     * Compila um modelo de documento que não referencia entidades dinamicamente.
     * Só é necessário o id do modelo de documento de entidade
     * @return
     */
    public String actionCompile() throws Exception
    {
        log.debug("::Iniciando actionCompile");
        try{
            
            /* Solocitando os parâmetros do document */
            if(this.getRequestParams().containsKey(URL_PARAM_DOCUMENT_ID))
            	this.modelDocumentEntityId = Long.parseLong(this.getRequestParams().get(URL_PARAM_DOCUMENT_ID).toString());
            
            getCompileDocumentProcess().setEntityType(null);
            getCompileDocumentProcess().setEntityId(IDAO.ENTITY_UNSAVED);
            getCompileDocumentProcess().setModelDocumentEntityId(this.modelDocumentEntityId);

            /* A visão FACES_VIEW_DOCUMENT_VIEW pegará o documento compilado diretamente do processo.
             * #{documentEntityBean.currentProcess.resultDocumentEntity}*/
            if(getCompileDocumentProcess().runCompileCrudExpression()){
            	/* Armazena o resultado */
//            	this.resultDocumentEntity = getCompileDocumentProcess().getCompiledCrudDocument();
            	this.resultDocumentEntityName = getCompileDocumentProcess().getModelDocumentEntity().getProperty(ModelDocumentEntity.NAME).getValue().getAsString();

            	this.documentFieldsEntry.clear();
            	for(Entry<String, String> entry: getCompileDocumentProcess().getDocumentFieldsMap().entrySet())
            		this.documentFieldsEntry.add(new FieldEntry(entry.getKey().toString(), entry.getValue().toString()));
            	
            	FacesUtils.addInfoMsg("Documento gerado com SUCESSO");
            	FacesUtils.addInfoMsgs(getCompileDocumentProcess().getMessageList());
                return FACES_VIEW_DOCUMENT_VIEW;
            }else{
            	FacesUtils.addErrorMsg("FALHA ao criar o documento");
            	FacesUtils.addErrorMsgs(getCompileDocumentProcess().getMessageList());
            	return FacesUtils.FACES_VIEW_FAILURE;
            }
        }catch(ProcessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Visualização REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        
    }
    
	/**
     * Carrega os parâmetros pertinente aos Bean da atual transação.   
     * Antes de recarregar os parâmetros, o Bean sofre um reset() para 
     * que os parâmetros atuais sejam limpos e dados processados sejam 
     * descarregados.
     */
    public void loadPreviewParams() throws Exception
    {
        log.debug("Lendo parâmetros da entidade do documentBean");
        
        // Causa um reset para que os novos parâmetros entrem em ação
        this.doReset();

        /* Solocitando os parâmetros do document */
        if(this.getRequestParams().containsKey(URL_PARAM_DOCUMENT_ID))
        	this.modelDocumentEntityId = Long.parseLong(this.getRequestParams().get(URL_PARAM_DOCUMENT_ID).toString());
    }
  
    
	
	/**
     * Action que pré-visualiza
     * um modelo de documento de entidade. 
     * @return
     */
    public String actionPreview() throws Exception
    {
        log.debug("::Iniciando actionPreview");
        
        try{
            this.loadPreviewParams();
            
        	IEntity<ModelDocumentEntity> modelDocumentEntity = UtilsCrud.retrieve(this.getApplicationBean().getProcessManager().getServiceManager(),
        			                                         ModelDocumentEntity.class,
        			                                         this.modelDocumentEntityId,
        			                                         null);
        	
            /* Define no processo o código fonte encontrado no modelo, sem compilar,
             * uma vez que o documento está sendo somente pré-visualizado e não há entidades
             * que possam ser utilizadas para compilar o documento */
        	this.getCompileDocumentProcess().setCompiledCrudDocument(modelDocumentEntity.getProperty(ModelDocumentEntity.SOURCE).getValue().getAsString());
        	
        	this.resultDocumentEntityName = modelDocumentEntity.getProperty(ModelDocumentEntity.NAME).getValue().getAsString();

        	/* Limpa antes do preview para não aparecer campos dos ultimos documentos compilados */
        	this.documentFieldsEntry.clear();

        	return FACES_VIEW_DOCUMENT_VIEW;
        }catch(BusinessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Visualização REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
    }
	
    /**
     * Action que pré-visualiza
     * um modelo de documento de entidade. 
     * @return
     */
    public String actionCancel() throws Exception
    {
        log.debug("::Iniciando actionCancel");
        	
            return FACES_VIEW_DOCUMENT_LIST;
    }
	
    /**
     * Action que imprime o documento.
     * É necessário que o documento JÀ ESTEJA PREPARADO
     * pelo metodo Preview ou Create
     * @return
     */
    public String actionPrint() throws Exception
    {
        log.debug("::Iniciando actionPrint");
        
    	for(FieldEntry entry: this.documentFieldsEntry)
    		this.compileDocumentProcess.getDocumentFieldsMap().put(entry.getKey(), entry.getValue());
    	
    	/* A visão FACES_VIEW_DOCUMENT_VIEW pegará o documento compilado diretamente do processo.
         * #{documentEntityBean.currentProcess.resultDocumentEntity}*/
        if(getCompileDocumentProcess().runCompileFieldsExpression()){
        	/* O resultado é obtido diretamente no processo pelo metodo getCompiledFieldsDocument() */

        	FacesUtils.addInfoMsg("Documento gerado com SUCESSO");
        	FacesUtils.addInfoMsgs(getCompileDocumentProcess().getMessageList());
        	return FACES_VIEW_DOCUMENT_PRINT;
        }else{
        	FacesUtils.addErrorMsg("FALHA ao criar o documento");
        	FacesUtils.addErrorMsgs(getCompileDocumentProcess().getMessageList());
        	return FacesUtils.FACES_VIEW_FAILURE;
        }

    }
	
	
	/**
     * Este método é responsável por compor a chave de criação da entidade usando
     * o tipo da entidade e o id da entidade. 
     * @return Retorna uma chave com entityType+entityId.
     */
	public String prepareCurrentEntityKey()
	{
    	return this.getEntityParam().getTypeName() + this.getEntityParam().getId();
	}
	
	public CompileDocumentProcess getCompileDocumentProcess()
	{
		if(this.compileDocumentProcess == null)
			try
			{
				this.compileDocumentProcess = (CompileDocumentProcess) this.getApplicationBean().getProcessManager().createProcessByName(CompileDocumentProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
			} catch (ProcessException e)
			{
				// TODO Auto-generated catch block
				FacesUtils.addErrorMsgs(e.getErrorList());
			}

		return compileDocumentProcess;
	}

	
	public boolean isHasDocumentFieldsEntry()
	{
		return documentFieldsEntry.isEmpty();
	}
	
	
	/*
	 * ROTINAS PARA CONTROLE DE GERAÇÂO DE DOCUMENTOS
	 */

	/**
	 *  Cria uma lista com os modelos de etiquetas de entidades disponivel
	 *  para a entidade atualmente manipulada.
	 *  O tipo da entidade já deve estar definido para executar este método, para que ele mostra somente os modelos 
	 *  da entidade selecionada
	 */
	private List<SelectItem> modelsDocumentEntityBuffer = null;
	private Class<?> lastGetModelsDocumentEntityType = null;
	public List<SelectItem> getModelsDocumentEntity(IEntity<?> entity){
		try{
			if((modelsDocumentEntityBuffer==null) || (lastGetModelsDocumentEntityType != entity.getInfo().getType())){
				ServiceData sd = new ServiceData(ListModelDocumentEntityService.SERVICE_NAME, null);
				sd.getArgumentList().setProperty(ListModelDocumentEntityService.IN_ENTITY_TYPE_NAME, entity.getInfo().getType().getName());
				sd.getArgumentList().setProperty(ListModelDocumentEntityService.IN_APPLICATION_USER_OPT, this.getUserSessionBean().getUserSession().getUser());
				this.getApplicationBean().getProcessManager().getServiceManager().execute(sd);
				modelsDocumentEntityBuffer = sd.getFirstOutput();
				lastGetModelsDocumentEntityType = entity.getInfo().getType();
			}
			
			return modelsDocumentEntityBuffer;
		}catch (ServiceException e){
			FacesUtils.addErrorMsgs(e.getErrorList());
			return null;
		}
    }

	public boolean hasModelsDocumentEntity(IEntity<?> entity){
		return this.getModelsDocumentEntity(entity).size()>0;
	}
	/*
	 * FIM - ROTINAS PARA CONTROLE DE GERAÇÂO DE DOCUMENTOS
	 */


	
}