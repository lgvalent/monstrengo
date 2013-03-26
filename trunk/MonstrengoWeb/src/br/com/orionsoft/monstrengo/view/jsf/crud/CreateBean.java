package br.com.orionsoft.monstrengo.view.jsf.crud;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.crud.processes.CreateProcess;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;


/**
 * Bean que controla a p�gina de cria��o de uma entidade Crud.
 * Par�metro de URL necess�rio: <br>
 * entityType: nome completo da classe da entidade que ser� criada uma nova inst�ncia.
 * 
 * @author Juliana e Tatiana
 * @version 20060203
 *
 * @jsf.bean name="createBean" scope="session"
 * @jsf.navigation from="*" result="create" to="/pages/basic/create.jsp" 
 */
@ManagedBean
@SessionScoped
public class CreateBean extends CrudBasicBean
{
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que � ativada para a vis�o CREATE */
	public static final String FACES_VIEW_CREATE = "/pages/basic/create?faces-redirect=true";

	public static final String URL_PARAM_COLL_PROPERTY = "collProperty";
    public static final String URL_PARAM_COLL_ITEM_ID = "collItemId";
    
    public static final String URL_PARAM_SELECT_ONE_DEST = "selectOneDest";
    public static final String URL_PARAM_SELECT_PROPERTY = "selectProperty";
    
    /** Se for fornecido algum filtro, o valor ser� inserido na primeira propriedade 
     * String da entidade que est� sendo criada */
    public static final String URL_PARAM_FILTER = "filter";

    private String collProperty;
    private long collItemId;
    
	@ManagedProperty(value="#{retrieveBean}")
    private RetrieveBean retrieveBean;

    /**
     * Action que prepara a visualiza��o
     * e controla o fluxo de tela. 
     * @return
     */
    public String actionCreate() throws Exception
    {
        log.debug("::Iniciando actionCreate");
    	// Prepara os par�metros fornecidos
        super.loadEntityParams();
        
        try{
            /* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            prepareCurrentEntity(prepareCurrentEntityKey());
            
            /* Verifica se tem algum par�metro no filtro para 
             * coloca-lo na primeira propriedade string da entidade */
            if(isHasFilter()){
            	for(IProperty prop: this.getCurrentEntity().getProperties()){
            		if(prop.getInfo().isString() && !prop.getInfo().isHasEditMask()){
            			prop.getValue().setAsString(this.getFilter());
            			break;
            		}
            	}
            }
            
        }catch(ProcessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Edi��o REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        // Redireciona a create
        log.debug("::Fim actionCreate");
        return CreateBean.FACES_VIEW_CREATE;
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
	public String actionCreate(String typeName, long id) throws Exception
	{
        log.debug("::Iniciando actionCreate");
        
  		/* Passa os par�metros para o controlador de entidade da visao */
   		this.getEntityParam().getParentParam().setTypeName(null);
   		this.getEntityParam().setTypeName(typeName);
   		this.getEntityParam().setId(id);

        try{
            /* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            prepareCurrentEntity(prepareCurrentEntityKey());
            
            /* Verifica se tem algum par�metro no filtro para 
             * coloca-lo na primeira propriedade string da entidade */
            if(isHasFilter()){
            	for(IProperty prop: this.getCurrentEntity().getProperties()){
            		if(prop.getInfo().isString() && !prop.getInfo().isHasEditMask()){
            			prop.getValue().setAsString(this.getFilter());
            			break;
            		}
            	}
            }
            
        }catch(ProcessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Edi��o REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        // Redireciona a create
        log.debug("::Fim actionCreate");
        return CreateBean.FACES_VIEW_CREATE;
	}
    /**
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
	public String actionUpdate() throws Exception{
    
        log.debug("::Iniciando actionUpdate");
        log.debug("Gravando altera��es pelo processo:" + currentEntityKey);
    	CreateProcess proc = (CreateProcess) processes.get(currentEntityKey); 

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
    			return actionCreate();

    		return RetrieveBean.FACES_VIEW_RETRIEVE;
    	}
    	
    	if(proc.runUpdate()){ 
    		/* Exibe a mensagem de sucesso */
    		FacesUtils.addInfoMsgs(proc.getMessageList());

    		log.debug("Recarregando a nova entidade para ser mostrada na vis�o RETRIEVE");
    		String nextView = retrieveBean.actionView(proc.getEntityType().getName(), proc.retrieveEntity().getId());
    		
    		log.debug("Repassando os par�metros sobre sele��o de uma entidade");
    		retrieveBean.currentParams.put(RetrieveBean.URL_PARAM_SELECT_ONE_DEST, this.currentParams.get(URL_PARAM_SELECT_ONE_DEST));
    		retrieveBean.currentParams.put(RetrieveBean.URL_PARAM_SELECT_PROPERTY, this.currentParams.get(URL_PARAM_SELECT_PROPERTY));
    		
    		/* Finaliza o processo e remove ele da lista de processo ativos */
    		processes.remove(currentEntityKey);
    		proc.finish();
    		proc=null;
    		currentEntity = null;
    		currentEntityKey = "";
    		
    		log.debug("::Fim actionSave(). Redirecionando para a vis�o VIEW.");
        	return nextView;
    	}else{
    		log.debug("::Fim actionSave(). Houveram erros, voltando para a vis�o CREATE.");
    		FacesUtils.addErrorMsgs(proc.getMessageList());
    		return "";
    	}
    }
    
	/**
     * Este m�todo prepara a entidade correntemente em cria��o, 
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
        	currentEntity = ((CreateProcess) processes.get(currentEntityKey)).retrieveEntity();
    	}else{
        	log.debug("Iniciando um novo processo de Cria��o");
        	CreateProcess createProcess = (CreateProcess)this.getApplicationBean().getProcessManager().createProcessByName(CreateProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
            try{
                /* Preenche os par�metros */
            	createProcess.setEntityType(Class.forName(this.getEntityParam().getTypeName()));
            	/* Preenche o id da entidade que ser� copiada. Este id � opcional e pode ser enviado
            	 * pelo link ou f:param que requisitou a tela */
            	createProcess.setEntityCopyId(this.getEntityParam().getId());
                
            	/* Obtem a entidade de cria��o , a tentativa causar� um throw
                 * e o processo responder� com a mensage de CREATE_DENIED
                 */
            	currentEntity = createProcess.retrieveEntity();
            	/* Coloca o processo de cria��o na lista de processos ativos */
            	processes.put(this.currentEntityKey, createProcess);
	        	log.debug("Novo processo criado com sucesso");
            }catch(BusinessException e){
	        	log.debug("Finalizando o processo pela ocorr�ncia de erro:" + e.getMessage());
            	createProcess.finish();
            	createProcess = null;
            	/* Passa o erro pra frente */
            	throw e;
            }
            
    	}

		/* Sempre verifica se recebeu par�metros de sele��o */
        loadSelectParams();
        
    }
    
	private void loadSelectParams(){
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_SELECT_ONE_DEST))) 
        {
            this.currentParams.put(URL_PARAM_SELECT_ONE_DEST, super.getRequestParams().get(URL_PARAM_SELECT_ONE_DEST).toString());
        }
        
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_SELECT_PROPERTY))) 
        {
            this.currentParams.put(URL_PARAM_SELECT_PROPERTY, super.getRequestParams().get(URL_PARAM_SELECT_PROPERTY).toString());
        }

	}
	
	public boolean isHasFilter() {return FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_FILTER));}

	private String getFilter(){
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_FILTER))) 
        {
            return super.getRequestParams().get(URL_PARAM_FILTER).toString();
            
        }

        return "";
	}
	

	/**
     * Refer�ncia para o bean de visualiza��o de uma entidade para que ap�s a
     * altera��o ser confirmada, a vis�o de retrieve seja exibida
     * 
     * @jsf.managed-property value="retrieveBean"
     */
    public RetrieveBean getRetrieveBean(){return retrieveBean;}
	public void setRetrieveBean(RetrieveBean retrieveBean){this.retrieveBean = retrieveBean;}

	
	/**
	 * Como a chave de inser��o � composta somente pelo nome da classe,
	 * o sistema n�o permitia abrir duas telas diferentes de cria��o 
	 * de uma mesma entidade, pois a chave � sempre a mesma. Foi necess�rio ent�o
	 * criar uma chave para cada a��o de inser��o. 
	 */
	private static int insertCount = 0;
	/**
     * Este m�todo � respons�vel por compor a chave de cria��o da entidade usando
     * o tipo da entidade. 
     * @return Retorna uma chave com entityType.
     */
	public String prepareCurrentEntityKey()
	{
    	return this.getEntityParam().getTypeName() + insertCount++;
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
        log.debug("Iniciando a a��o doRemoveFromCollection");
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
    	/* O id j� � alimentado pelo FACES direto na cole��o */
    	
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
     * Este m�todo analisa a tual requisi��o e verifica se
     * foram recebidos par�metros da cole��o. Se foram, eles s�o 
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
		CreateProcess createProcess = (CreateProcess) processes.get(currentEntityKey);
		
		/* Recarrega a entidade */
		createProcess.runReload();
		
		currentEntity = createProcess.retrieveEntity();
		
		return CreateBean.FACES_VIEW_CREATE;
	}
	
    /**
     * Este m�todo solicita para o processo validar a entidade pelos DVO antes 
     * da confirma��o do update.
     */
    @SuppressWarnings("rawtypes")
	public void doValidateEntity()
    {
        log.debug("Iniciando a a��o doValidateEntity");
        CreateProcess createProcess = (CreateProcess) processes.get(currentEntityKey);
    	
    	/* Recarrega a entidade */
		if (!createProcess.runValidate()){
			FacesUtils.addErrorMsgs(createProcess.getMessageList());
		}
    }

	
}