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
 * Bean que controla a página de edição de uma entidade Crud.
 * Parâmetro de URL necessário: <br>
 * entityType: nome completo da classe da entidade que será alterada.
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

	/** Define a view JSF que é ativada para a visão UPDATE */
	public static final String FACES_VIEW_UPDATE = "/pages/basic/update?faces-redirect=true";
    
    public static final String URL_PARAM_COLL_PROPERTY = "collProperty";
    public static final String URL_PARAM_COLL_ITEM_ID = "collItemId";

    private String collProperty;
    private long collItemId;

	@ManagedProperty(value="#{retrieveBean}")
    private RetrieveBean retrieveBean;
	private boolean actionEditActive = false;
    

    /**
     * Action que prepara a visualização
     * e controla o fluxo de tela. 
     * @return
     */
    public String actionEdit() throws Exception
    {
        /* Indica que a ação de uma nova edição está
         * sendo disparada para que se encontrado um processo
         * já ativo para a entidade solicitada, a mesma seja
         * recarregada e a ediçao enterior seja descartada */
        this.actionEditActive = true;

        log.debug("::Iniciando actionEdit");
    	// Prepara os parâmetros fornecidos
        this.loadEntityParams();
        
        try{
            /* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            prepareCurrentEntity(prepareCurrentEntityKey());
            
        }catch(BusinessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Edição REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        // Redireciona a Edit
        log.debug("::Fim actionEdit");
        this.actionEditActive  = false;
        return UpdateBean.FACES_VIEW_UPDATE;
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
	public String actionEdit(String typeName, long id) throws Exception
	{
        log.debug("::Iniciando actionEdit");
        
   		/* Passa os parâmetros para o controlador de entidade da visao */
   		this.getEntityParam().getParentParam().setTypeName(null);
   		this.getEntityParam().setTypeName(typeName);
   		this.getEntityParam().setId(id);

        /* Indica que a ação de uma nova edição está
         * sendo disparada para que se encontrado um processo
         * já ativo para a entidade solicitada, a mesma seja
         * recarregada e a ediçao enterior seja descartada */
        this.actionEditActive = true;

        try{
            /* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            prepareCurrentEntity(prepareCurrentEntityKey());
            
        }catch(BusinessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Edição REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        // Redireciona a Edit
        log.debug("::Fim actionEdit");
        this.actionEditActive  = false;
        return UpdateBean.FACES_VIEW_UPDATE;
	}

    /**
     * Ação que é invocada para finalizar (gravar) um processo de edição
     * ativo. 
     * @return
     */
    @SuppressWarnings("rawtypes")
	public String actionUpdate() throws Exception{
    
    	log.debug("::Iniciando actionUpdate");
        log.debug("Gravando alterações pelo processo:" + currentEntityKey);
    	UpdateProcess proc = (UpdateProcess) processes.get(currentEntityKey); 
    	
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
    		
    		log.debug("::Fim actionUpdate(). Redirecionando para a visão VIEW.");
        	return nextView;
    	}else{
    		log.debug("::Fim actionUpdate(). Hoveram erros, voltando para a visão EDIT.");
    		FacesUtils.addErrorMsgs(proc.getMessageList());
    		return "";
    	}
    }
    
    
	/**
     * Este método prepara a entidade correntemente em edição, 
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
        	UpdateProcess updateProcess = (UpdateProcess) processes.get(currentEntityKey);
        	
        	/* Verifica se é uma chamada por link para recarregar a entidade
        	 * ou se a actionEdit foi disparada*/
        	if(checkLinkRequest() || this.actionEditActive)
        		updateProcess.runReload();
        	
        	currentEntity = updateProcess.retrieveEntity();
    	}else{
        	log.debug("Iniciando um novo processo de Edição");
        	UpdateProcess updateProcess = (UpdateProcess)this.getApplicationBean().getProcessManager().createProcessByName(UpdateProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
            /* Preenche os parâmetros */
        	updateProcess.setEntityType(Class.forName(this.getEntityParam().getTypeName()));
            updateProcess.setEntityId(this.getEntityParam().getId());
            
            try{
                /* Obtem a entidade de edição , a tentativa causará um throw
                 * e o processo responderá com a mensage de UPDATE_DENIED
                 */
            	currentEntity = updateProcess.retrieveEntity();
            	
            	/* Coloca o processo de edição na lista de processos ativos */
            	processes.put(this.currentEntityKey, updateProcess);
	        	log.debug("Novo processo criado com sucesso");
            }catch(BusinessException e){
	        	log.debug("Finalizando o processo pela ocorrência de algum erro");
            	updateProcess.finish();
            	updateProcess = null;
            	/* Passa o erro pra frente */
            	throw e;
            }
            
    	}
    }
	
    /**
     * Referência para o bean de visualização de uma entidade para que após a
     * alteração ser confirmada, a visão de retrieve seja exibida
     * 
     * @jsf.managed-property value="#{retrieveBean}"
     */
    public RetrieveBean getRetrieveBean(){return retrieveBean;}
	public void setRetrieveBean(RetrieveBean retrieveBean){this.retrieveBean = retrieveBean;}

	/**
     * Este método é responsável por compor a chave de criação da entidade usando
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
        log.debug("Iniciando a ação doOrder");
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
    	/* O id já é alimentado pelo FACES */
    	
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
    	UpdateProcess updateProcess = (UpdateProcess) processes.get(currentEntityKey);
    	
    	/* Recarrega a entidade */
   		updateProcess.runReload();
    	
    	currentEntity = updateProcess.retrieveEntity();
    	
    	return UpdateBean.FACES_VIEW_UPDATE;
    }

    /**
     * Este método solicita para o processo validar a entidade pelos DVO antes 
     * da confirmação do update.
     */
    @SuppressWarnings("rawtypes")
	public void doValidateEntity()
    {
        log.debug("Iniciando a ação doValidateEntity");
    	UpdateProcess updateProcess = (UpdateProcess) processes.get(currentEntityKey);
    	
    	/* Recarrega a entidade */
    	/* Recarrega a entidade */
		if (!updateProcess.runValidate()){
			FacesUtils.addErrorMsgs(updateProcess.getMessageList());
		}
    }

    /**
     * Este método analisa a tual requisição e verifica se
     * foram recebidos parãmetros de ordenação. Se foram, eles são 
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

}