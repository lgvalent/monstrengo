package br.com.orionsoft.monstrengo.view.jsf.crud;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.crud.processes.UpdateProcess;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;


/**
 * Bean que controla a p�gina de edi��o de uma entidade Crud.
 * Par�metro de URL necess�rio: <br>
 * entityType: nome completo da classe da entidade que ser� alterada.
 * entityId: identificador da entidade.
 * 
 * @author Lucio
 * @version 20060206
 * 
 * @jsf.bean name="updateBean" scope="session"
 * @jsf.navigation from="*" result="update" to="/pages/basic/update.jsp" 
 */
@ManagedBean
@SessionScoped
public class UpdateBean extends CrudBasicBean
{
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que � ativada para a vis�o UPDATE */
	public static final String FACES_VIEW_UPDATE = "/pages/basic/update?faces-redirect=true";
    
    public static final String URL_PARAM_COLL_PROPERTY = "collProperty";
    public static final String URL_PARAM_COLL_ITEM_ID = "collItemId";

    private String collProperty;
    private long collItemId;

	@ManagedProperty(value="#{retrieveBean}")
    private RetrieveBean retrieveBean;
	private boolean actionEditActive = false;
    

    /**
     * Action que prepara a visualiza��o
     * e controla o fluxo de tela. 
     * @return
     */
    public String actionEdit() throws Exception
    {
        /* Indica que a a��o de uma nova edi��o est�
         * sendo disparada para que se encontrado um processo
         * j� ativo para a entidade solicitada, a mesma seja
         * recarregada e a edi�ao enterior seja descartada */
        this.actionEditActive = true;

        log.debug("::Iniciando actionEdit");
    	// Prepara os par�metros fornecidos
        this.loadEntityParams();
        
        try{
            /* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            prepareCurrentEntity(prepareCurrentEntityKey());
            
        }catch(BusinessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Edi��o REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        // Redireciona a Edit
        log.debug("::Fim actionEdit");
        this.actionEditActive  = false;
        return UpdateBean.FACES_VIEW_UPDATE;
    }
    
    /**
     * Action que prepara a visualiza��o baseada
	 * nos par�metros passados.
	 * Este m�todo � usado pelos outros beans quando desejam 
	 * exibir uma entidade no browser
     * e controla o fluxo de tela. 
     * @return
     * @throws Exception 
     */
	public String actionEdit(String typeName, long id) throws Exception
	{
        log.debug("::Iniciando actionEdit");
        
   		/* Passa os par�metros para o controlador de entidade da visao */
   		this.getEntityParam().getParentParam().setTypeName(null);
   		this.getEntityParam().setTypeName(typeName);
   		this.getEntityParam().setId(id);

        /* Indica que a a��o de uma nova edi��o est�
         * sendo disparada para que se encontrado um processo
         * j� ativo para a entidade solicitada, a mesma seja
         * recarregada e a edi�ao enterior seja descartada */
        this.actionEditActive = true;

        try{
            /* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            prepareCurrentEntity(prepareCurrentEntityKey());
            
        }catch(BusinessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Edi��o REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        // Redireciona a Edit
        log.debug("::Fim actionEdit");
        this.actionEditActive  = false;
        return UpdateBean.FACES_VIEW_UPDATE;
	}

    /**
     * A��o que � invocada para finalizar (gravar) um processo de edi��o
     * ativo. 
     * @return
     */
    @SuppressWarnings("rawtypes")
	public String actionUpdate() throws Exception{
    
    	log.debug("::Iniciando actionUpdate");
        log.debug("Gravando altera��es pelo processo:" + currentEntityKey);
    	UpdateProcess proc = (UpdateProcess) processes.get(currentEntityKey); 
    	
    	/* Esta acontecendo que quando ocorre algum erro de valida��o durante
    	 * a fase de edi��o, quando o operador corrige os erros e submete novamente
    	 * o faces faz tudo certinho, mas quando vai renderizar a next view,
    	 * ele volta pra a��o que n�o foi completada antes da valida��o (actionUpdate)
    	 * porem, a actionUpdate j� foi completada com sucesso e n�o h� mais este processo.
    	 * Pra contornar este problema, estou enviando diretamente pra RETRIEVE */
    	if(proc == null){
    		/* Processo null e chamada ocorrida por link, indica que o operador
    		 * cancelou a �ltima edi��o ap�s alguns erros de valida��o e agora o faces,
    		 * ao renderizar novamente a vis�o, tenta executar o actionUpdate onde parou,
    		 * mas n�o houve prepareCurrentyEntity. Ent�o, o fluxo � desviado para o 
    		 * in�cio de um novo processo de cria��o */
    		if(checkLinkRequest())
    			return actionEdit();

    		return RetrieveBean.FACES_VIEW_RETRIEVE;
    	}
    	if(proc.runUpdate()){ 
    		/* Exibe a mensagem de sucesso */
    		FacesUtils.addInfoMsgs(proc.getMessageList());

    		log.debug("Recarregando a nova entidade para ser mostrada na VIEW");
    		String nextView = retrieveBean.actionView(proc.getEntityType().getName(), proc.getEntityId());
    		
    		/* Finaliza o processo e remove ele da lista de processo ativos */
    		processes.remove(currentEntityKey);
    		proc.finish();
    		proc=null;
    		currentEntity = null;
    		currentEntityKey = "";
    		
    		log.debug("::Fim actionUpdate(). Redirecionando para a vis�o VIEW.");
        	return nextView;
    	}else{
    		log.debug("::Fim actionUpdate(). Hoveram erros, voltando para a vis�o EDIT.");
    		FacesUtils.addErrorMsgs(proc.getMessageList());
    		return "";
    	}
    }
    
    
	/**
     * Este m�todo prepara a entidade correntemente em edi��o, 
     * baseando-se na chave passada.<br>
     * O mapa de processos ativos � consultado e n�o for encontrado o processo,
     * um novo processo � criado.  
     * @return Retorna uma chave com entityType+entityId.
	 * @throws BusinessException 
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void prepareCurrentEntity(String currentEntityKey) throws BusinessException, Exception{
    	
        super.prepareCurrentEntity(currentEntityKey);
        
    	if(processes.containsKey(currentEntityKey)){
        	log.debug("Utilizando o processo j� ativo");
        	UpdateProcess updateProcess = (UpdateProcess) processes.get(currentEntityKey);
        	
        	/* Verifica se � uma chamada por link para recarregar a entidade
        	 * ou se a actionEdit foi disparada*/
        	if(checkLinkRequest() || this.actionEditActive)
        		updateProcess.runReload();
        	
        	currentEntity = updateProcess.retrieveEntity();
    	}else{
        	log.debug("Iniciando um novo processo de Edi��o");
        	UpdateProcess updateProcess = (UpdateProcess)this.getApplicationBean().getProcessManager().createProcessByName(UpdateProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
            /* Preenche os par�metros */
        	updateProcess.setEntityType(Class.forName(this.getEntityParam().getTypeName()));
            updateProcess.setEntityId(this.getEntityParam().getId());
            
            try{
                /* Obtem a entidade de edi��o , a tentativa causar� um throw
                 * e o processo responder� com a mensage de UPDATE_DENIED
                 */
            	currentEntity = updateProcess.retrieveEntity();
            	
            	/* Coloca o processo de edi��o na lista de processos ativos */
            	processes.put(this.currentEntityKey, updateProcess);
	        	log.debug("Novo processo criado com sucesso");
            }catch(BusinessException e){
	        	log.debug("Finalizando o processo pela ocorr�ncia de algum erro");
            	updateProcess.finish();
            	updateProcess = null;
            	/* Passa o erro pra frente */
            	throw e;
            }
            
    	}
    }
	
    /**
     * Refer�ncia para o bean de visualiza��o de uma entidade para que ap�s a
     * altera��o ser confirmada, a vis�o de retrieve seja exibida
     * 
     * @jsf.managed-property value="#{retrieveBean}"
     */
    public RetrieveBean getRetrieveBean(){return retrieveBean;}
	public void setRetrieveBean(RetrieveBean retrieveBean){this.retrieveBean = retrieveBean;}

	/**
     * Este m�todo � respons�vel por compor a chave de cria��o da entidade usando
     * o tipo da entidade e o id da entidade. 
     * @return Retorna uma chave com entityType+entityId.
     */
	public String prepareCurrentEntityKey()
	{
    	if(log.isDebugEnabled()) 
    	   log.debug("Preparando a chave da entidade corrente:" + this.getEntityParam().getTypeName() + this.getEntityParam().getId());
		return this.getEntityParam().getTypeName() + this.getEntityParam().getId();
	}

	/**
     * Este m�todo pode ser disparado pela interface, a qual fornecer�
     * por meio de par�metros URL as defini��es da ordem desejada.
     * Ele l� estes par�metros da requisi��o.
     * Interpreta os par�metros
     * E realiza a remo��o do item 
     * @throws EntityException 
     * @throws PropertyValueException 
     * @throws BusinessException
     * @throws Exception
     */
    public void doRemoveFromCollection() throws BusinessException
    {
        log.debug("Iniciando a a��o doOrder");
    	/* Carregar os par�metros de remo��o */
    	loadCollectionParams();
    	
    	/* Localiza a propriedade
    	 * Lucio 08112007: Aceita collProperty prop1.prop2 para cole��es dentro de entidade.
    	 */
    	String[] props = StringUtils.split(this.collProperty, ".");
    	IEntityCollection<?> coll=null;
    	String propLabel = null;
    	if(props.length==1){
    		propLabel = currentEntity.getProperty(this.collProperty).getInfo().getLabel();
    		coll = currentEntity.getProperty(this.collProperty).getValue().getAsEntityCollection();
    	}else
        	if(props.length==2){
        		propLabel = currentEntity.getProperty(props[0]).getValue().getAsEntityCollection().getRunEntity().getProperty(props[1]).getInfo().getLabel();
        		coll = currentEntity.getProperty(props[0]).getValue().getAsEntityCollection().getRunEntity().getProperty(props[1]).getValue().getAsEntityCollection();
        	}
    	

    	/* Define o id a ser removido */
    	coll.setRunId(this.collItemId);

    	String successStr = "";
    	if(coll.getRunId()!=null)
    		successStr = "Item com id="+coll.getRunId()+ " removido com sucesso da lista de " + propLabel + ".";
    	else
    		if(coll.getRunEntity()!=null)
    			successStr = "Item <b>"+coll.getRunEntity().toString()+ "</b> removido com sucesso da lista de " + propLabel + ".";

    	/* Remove o item da cole��o */
    	if(coll.runRemove())
    		FacesUtils.addInfoMsg(successStr);
    }
    
    /**
     * Este m�todo pode ser disparado pela interface, a qual fornecer�
     * por meio de par�metros URL as defini��es da ordem desejada.
     * Ele l� estes par�metros da requisi��o.
     * Interpreta os par�metros
     * E realiza a remo��o do item 
     * @throws EntityException 
     * @throws PropertyValueException 
     * @throws BusinessException
     * @throws Exception
     */
    public void doAddToCollection() throws BusinessException
    {
        log.debug("Iniciando a a��o doAddToCollection");
    	try {
    	/* Carregar os par�metros de remo��o */
    	loadCollectionParams();
    	
    	/* Localiza a propriedade
    	 * Lucio 08112007: Aceita collProperty prop1.prop2 para cole��es dentro de entidade.
    	 */
    	String[] props = StringUtils.split(this.collProperty, ".");
    	IEntityCollection<?> coll=null;
    	String propLabel = null;
    	if(props.length==1){
    		propLabel = currentEntity.getProperty(this.collProperty).getInfo().getLabel();
    		coll = currentEntity.getProperty(this.collProperty).getValue().getAsEntityCollection();
    	}else
        	if(props.length==2){
        		propLabel = currentEntity.getProperty(props[0]).getValue().getAsEntityCollection().getRunEntity().getProperty(props[1]).getInfo().getLabel();
        		coll = currentEntity.getProperty(props[0]).getValue().getAsEntityCollection().getRunEntity().getProperty(props[1]).getValue().getAsEntityCollection();
        	}
    	
    	
    	/* Define o Id a ser removido */
    	/* O id j� � alimentado pelo FACES */
    	
    	/* Adiciona o elemento */
    	String successStr = "";
    	if(coll.getRunId()!=null)
    		successStr = "Item com id="+coll.getRunId()+ " adicionado com sucesso na lista de " + propLabel + ".";
    	else
    		if(coll.getRunEntity()!=null)
    			successStr = "Item <b>"+coll.getRunEntity().toString()+ "</b> adicionado com sucesso na lista de " + propLabel + ".";

    	if(coll.runAdd())
			FacesUtils.addInfoMsg(successStr);
			
		} catch (BusinessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
		}
    }
    
    /**
     * Este m�todo solicita para o processo recarregar 
     * a entidade, descartando as atuais altera��es. Deve ser
     * chamado com a tag immediate=true para evitar valida��es
     * do faces antes da recarga.
     * Foi utilizado um m�todo com retorno, porque
     * o m�todo void ficava dando um erro de valida��o.
     */
    @SuppressWarnings("rawtypes")
	public String doReloadEntity() throws BusinessException
    {
        log.debug("Iniciando a a��o doReload");
    	UpdateProcess updateProcess = (UpdateProcess) processes.get(currentEntityKey);
    	
    	/* Recarrega a entidade */
   		updateProcess.runReload();
    	
    	currentEntity = updateProcess.retrieveEntity();
    	
    	return UpdateBean.FACES_VIEW_UPDATE;
    }

    /**
     * Este m�todo solicita para o processo validar a entidade pelos DVO antes 
     * da confirma��o do update.
     */
    @SuppressWarnings("rawtypes")
	public void doValidateEntity()
    {
        log.debug("Iniciando a a��o doValidateEntity");
    	UpdateProcess updateProcess = (UpdateProcess) processes.get(currentEntityKey);
    	
    	/* Recarrega a entidade */
    	/* Recarrega a entidade */
		if (!updateProcess.runValidate()){
			FacesUtils.addErrorMsgs(updateProcess.getMessageList());
		}
    }

    /**
     * Este m�todo analisa a tual requisi��o e verifica se
     * foram recebidos par�metros de ordena��o. Se foram, eles s�o 
     * carregados para o bean e poder�o posteriormente ser
     * aplicados aos processos ativos.
     */
    private void loadCollectionParams()
	{
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_COLL_PROPERTY)))
        {
            this.collProperty = super.getRequestParams().get(URL_PARAM_COLL_PROPERTY).toString();
        }
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_COLL_ITEM_ID))) 
        {
            this.collItemId = Integer.parseInt(super.getRequestParams().get(URL_PARAM_COLL_ITEM_ID).toString());
        }
	}

	public long getCollItemId() {
		return collItemId;
	}

	public void setCollItemId(long collItemId) {
		this.collItemId = collItemId;
	}

	public String getCollProperty() {
		return collProperty;
	}

	public void setCollProperty(String collProperty) {
		this.collProperty = collProperty;
	}

}