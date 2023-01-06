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
 * Bean que controla a página de criação de uma entidade Crud.
 * Parâmetro de URL necessário: <br>
 * entityType: nome completo da classe da entidade que será criada uma nova instância.
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

	/** Define a view JSF que é ativada para a visão CREATE */
	public static final String FACES_VIEW_CREATE = "/pages/basic/create?faces-redirect=true";

	public static final String URL_PARAM_COLL_PROPERTY = "collProperty";
    public static final String URL_PARAM_COLL_ITEM_ID = "collItemId";
    
    public static final String URL_PARAM_SELECT_ONE_DEST = "selectOneDest";
    public static final String URL_PARAM_SELECT_PROPERTY = "selectProperty";
    
    /** Se for fornecido algum filtro, o valor será inserido na primeira propriedade 
     * String da entidade que está sendo criada */
    public static final String URL_PARAM_FILTER = "filter";

    private String collProperty;
    private long collItemId;
    
	@ManagedProperty(value="#{retrieveBean}")
    private RetrieveBean retrieveBean;

    /**
     * Action que prepara a visualização
     * e controla o fluxo de tela. 
     * @return
     */
    public String actionCreate() throws Exception
    {
        log.debug("::Iniciando actionCreate");
    	// Prepara os parâmetros fornecidos
        super.loadEntityParams();
        
        try{
            /* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            prepareCurrentEntity(prepareCurrentEntityKey());
            
            /* Verifica se tem algum parâmetro no filtro para 
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
        	/* Edição REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        // Redireciona a create
        log.debug("::Fim actionCreate");
        return CreateBean.FACES_VIEW_CREATE;
    }
    
    /**
     * Action que prepara a visualização baseada
	 * nos parâmetros passados.
	 * Este método é usado pelos outros beans quando desejam 
	 * exibir uma entidade no browser
     * e controla o fluxo de tela. 
     * @return
     * @throws Exception 
     */
	public String actionCreate(String typeName, long id) throws Exception
	{
        log.debug("::Iniciando actionCreate");
        
  		/* Passa os parâmetros para o controlador de entidade da visao */
   		this.getEntityParam().getParentParam().setTypeName(null);
   		this.getEntityParam().setTypeName(typeName);
   		this.getEntityParam().setId(id);

        try{
            /* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            prepareCurrentEntity(prepareCurrentEntityKey());
            
            /* Verifica se tem algum parâmetro no filtro para 
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
        	/* Edição REJEITADA */
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
        log.debug("Gravando alterações pelo processo:" + currentEntityKey);
    	CreateProcess proc = (CreateProcess) processes.get(currentEntityKey); 

    	/* Esta acontecendo que quando ocorre algum erro de validação durante
    	 * a fase de edição, quando o operador corrige os erros e submete novamente
    	 * o faces faz tudo certinho, mas quando vai renderizar a next view,
    	 * ele volta pra ação que não foi completada antes da validação (actionUpdate)
    	 * porem, a actionUpdate já foi completada com sucesso e não há mais este processo.
    	 * Pra contornar este problema, estou enviando diretamente pra RETRIEVE */
    	if(proc == null){
    		/* Processo null e chamada ocorrida por link, indica que o operador
    		 * cancelou a última edição após alguns erros de validação e agora o faces,
    		 * ao renderizar novamente a visão, tenta executar o actionUpdate onde parou,
    		 * mas não houve prepareCurrentyEntity. Então, o fluxo é desviado para o 
    		 * início de um novo processo de criação */
    		if(checkLinkRequest())
    			return actionCreate();

    		return RetrieveBean.FACES_VIEW_RETRIEVE;
    	}
    	
    	if(proc.runUpdate()){ 
    		/* Exibe a mensagem de sucesso */
    		FacesUtils.addInfoMsgs(proc.getMessageList());

    		log.debug("Recarregando a nova entidade para ser mostrada na visão RETRIEVE");
    		String nextView = retrieveBean.actionView(proc.getEntityType().getName(), proc.retrieveEntity().getId());
    		
    		log.debug("Repassando os parâmetros sobre seleção de uma entidade");
    		retrieveBean.currentParams.put(RetrieveBean.URL_PARAM_SELECT_ONE_DEST, this.currentParams.get(URL_PARAM_SELECT_ONE_DEST));
    		retrieveBean.currentParams.put(RetrieveBean.URL_PARAM_SELECT_PROPERTY, this.currentParams.get(URL_PARAM_SELECT_PROPERTY));
    		
    		/* Finaliza o processo e remove ele da lista de processo ativos */
    		processes.remove(currentEntityKey);
    		proc.finish();
    		proc=null;
    		currentEntity = null;
    		currentEntityKey = "";
    		
    		log.debug("::Fim actionSave(). Redirecionando para a visão VIEW.");
        	return nextView;
    	}else{
    		log.debug("::Fim actionSave(). Houveram erros, voltando para a visão CREATE.");
    		FacesUtils.addErrorMsgs(proc.getMessageList());
    		return "";
    	}
    }
    
	/**
     * Este método prepara a entidade correntemente em criação, 
     * baseando-se na chave passada.<br>
     * O mapa de processos ativos é consultado e não for encontrado o processo,
     * um novo processo é criado.  
     * @return Retorna uma chave com entityType+entityId.
	 * @throws BusinessException 
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void prepareCurrentEntity(String currentEntityKey) throws BusinessException, Exception{
    	
        super.prepareCurrentEntity(currentEntityKey);
        
        if(processes.containsKey(currentEntityKey)){
        	log.debug("Utilizando o processo já ativo");
        	currentEntity = ((CreateProcess) processes.get(currentEntityKey)).retrieveEntity();
    	}else{
        	log.debug("Iniciando um novo processo de Criação");
        	CreateProcess createProcess = (CreateProcess)this.getApplicationBean().getProcessManager().createProcessByName(CreateProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
            try{
                /* Preenche os parâmetros */
            	createProcess.setEntityType(Class.forName(this.getEntityParam().getTypeName()));
            	/* Preenche o id da entidade que será copiada. Este id é opcional e pode ser enviado
            	 * pelo link ou f:param que requisitou a tela */
            	createProcess.setEntityCopyId(this.getEntityParam().getId());
                
            	/* Obtem a entidade de criação , a tentativa causará um throw
                 * e o processo responderá com a mensage de CREATE_DENIED
                 */
            	currentEntity = createProcess.retrieveEntity();
            	/* Coloca o processo de criação na lista de processos ativos */
            	processes.put(this.currentEntityKey, createProcess);
	        	log.debug("Novo processo criado com sucesso");
            }catch(BusinessException e){
	        	log.debug("Finalizando o processo pela ocorrência de erro:" + e.getMessage());
            	createProcess.finish();
            	createProcess = null;
            	/* Passa o erro pra frente */
            	throw e;
            }
            
    	}

		/* Sempre verifica se recebeu parâmetros de seleção */
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
     * Referência para o bean de visualização de uma entidade para que após a
     * alteração ser confirmada, a visão de retrieve seja exibida
     * 
     * @jsf.managed-property value="retrieveBean"
     */
    public RetrieveBean getRetrieveBean(){return retrieveBean;}
	public void setRetrieveBean(RetrieveBean retrieveBean){this.retrieveBean = retrieveBean;}

	
	/**
	 * Como a chave de inserção é composta somente pelo nome da classe,
	 * o sistema não permitia abrir duas telas diferentes de criação 
	 * de uma mesma entidade, pois a chave é sempre a mesma. Foi necessário então
	 * criar uma chave para cada ação de inserção. 
	 */
	private static int insertCount = 0;
	/**
     * Este método é responsável por compor a chave de criação da entidade usando
     * o tipo da entidade. 
     * @return Retorna uma chave com entityType.
     */
	public String prepareCurrentEntityKey()
	{
    	return this.getEntityParam().getTypeName() + insertCount++;
	}
	/**
     * Este método pode ser disparado pela interface, a qual fornecerá
     * por meio de parâmetros URL as definições da ordem desejada.
     * Ele lê estes parâmetros da requisição.
     * Interpreta os parãmetros
     * E realiza a remoção do item 
     * @throws EntityException 
     * @throws PropertyValueException 
     * @throws BusinessException
     * @throws Exception
     */
    public void doRemoveFromCollection() throws BusinessException
    {
        log.debug("Iniciando a ação doRemoveFromCollection");
    	/* Carregar os parâmetros de remoção */
    	loadCollectionParams();
    	
    	/* Localiza a propriedade
    	 * Lucio 08112007: Aceita collProperty prop1.prop2 para coleções dentro de entidade.
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

    	/* Remove o item da coleção */
    	if(coll.runRemove())
    		FacesUtils.addInfoMsg(successStr);
    }
    
    /**
     * Este método pode ser disparado pela interface, a qual fornecerá
     * por meio de parâmetros URL as definições da ordem desejada.
     * Ele lê estes parâmetros da requisição.
     * Interpreta os parãmetros
     * E realiza a remoção do item 
     * @throws EntityException 
     * @throws PropertyValueException 
     * @throws BusinessException
     * @throws Exception
     */
    public void doAddToCollection() throws BusinessException
    {
        log.debug("Iniciando a ação doAddToCollection");
    	try {
    	/* Carregar os parâmetros de remoção */
    	loadCollectionParams();
    	
    	/* Localiza a propriedade
    	 * Lucio 08112007: Aceita collProperty prop1.prop2 para coleções dentro de entidade.
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
    	/* O id já é alimentado pelo FACES direto na coleção */
    	
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
     * Este método analisa a tual requisição e verifica se
     * foram recebidos parãmetros da coleção. Se foram, eles são 
     * carregados para o bean e poderão posteriormente ser
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
	 * Este método solicita para o processo recarregar 
	 * a entidade, descartando as atuais alterações. Deve ser
	 * chamado com a tag immediate=true para evitar validações
	 * do faces antes da recarga.
	 * Foi utilizado um método com retorno, porque
	 * o método void ficava dando um erro de validação.
	 */
	@SuppressWarnings("rawtypes")
	public String doReloadEntity() throws BusinessException
	{
	    log.debug("Iniciando a ação doReload");
		CreateProcess createProcess = (CreateProcess) processes.get(currentEntityKey);
		
		/* Recarrega a entidade */
		createProcess.runReload();
		
		currentEntity = createProcess.retrieveEntity();
		
		return CreateBean.FACES_VIEW_CREATE;
	}
	
    /**
     * Este método solicita para o processo validar a entidade pelos DVO antes 
     * da confirmação do update.
     */
    @SuppressWarnings("rawtypes")
	public void doValidateEntity()
    {
        log.debug("Iniciando a ação doValidateEntity");
        CreateProcess createProcess = (CreateProcess) processes.get(currentEntityKey);
    	
    	/* Recarrega a entidade */
		if (!createProcess.runValidate()){
			FacesUtils.addErrorMsgs(createProcess.getMessageList());
		}
    }

	
}