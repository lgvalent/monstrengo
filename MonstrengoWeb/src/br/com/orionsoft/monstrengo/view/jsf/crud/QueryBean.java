package br.com.orionsoft.monstrengo.view.jsf.crud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.labels.entities.AddressLabelGroup;
import br.com.orionsoft.monstrengo.crud.labels.services.ListModelLabelEntityService;
import br.com.orionsoft.monstrengo.crud.processes.QueryProcess;
import br.com.orionsoft.monstrengo.crud.report.entities.PageParam;
import br.com.orionsoft.monstrengo.crud.report.entities.UserReport;
import br.com.orionsoft.monstrengo.crud.services.OrderCondiction;
import br.com.orionsoft.monstrengo.crud.services.QueryCondiction;
import br.com.orionsoft.monstrengo.crud.services.ResultCondiction;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Bean que cotrola a view de pesquisa avan�ada
 * 
 * Created on 05/04/2005
 * @author Lucio Valentin
 * 
 * @jsf.bean name="queryBean" scope="session"
 * @jsf.property name="runQueryOnOpen" value="false"
 * @jsf.navigation from="*" result="query" to="/pages/basic/query.jsp" 
 */
@ManagedBean
@SessionScoped
public class QueryBean extends CrudBasicBean
{
	/** Define a view JSF que � ativada para a vis�o QUERY */
	public static final String FACES_VIEW_QUERY = "/pages/basic/query?faces-redirect=true";

	public static final String URL_PARAM_ORDER_PROPERTY = "orderProperty";
    public static final String URL_PARAM_ORDER_DIRECTION = "orderDirection";
    public static final String URL_PARAM_FILTER = "filter";
    public static final String URL_PARAM_SELECT_ONE_DEST = "selectOneDest";
    public static final String URL_PARAM_SELECT_PROPERTY = "selectProperty";
    public static final String URL_PARAM_PAGE_INDEX = "pageIndex";
    public static final String URL_PARAM_PAGE_SIZE = "pageSize";

    public static final String URL_PARAM_CONDICTION_ID = "condictionId";
    
	public static final String URL_PARAM_ADVANCED_QUERY = "advancedQuery";
	public static final String URL_PARAM_ADVANCED_HQL_WHERE = "advancedHqlWhere";
	public static final String URL_PARAM_ADVANCED_ORDER = "advancedOrder";
	public static final String URL_PARAM_ADVANCED_RESULT = "advancedResult";
	public static final String URL_PARAM_ADVANCED_USER_REPORT = "advancedUserReport";

	/** Utilizado para indicar o id do report atualmente selecionado pelo operador na tela de sele��o */
    public static final String URL_PARAM_USER_REPORT_ID = "userReportId";

    // Dados internos da classe
    private QueryProcess currentProcess = null;
    private IPropertyMetadata[] properties = null;
    private IEntity parentEntity = null;

	/**
     * Action que constr�i a lista e redireciona a view para "list". 
     * @return
     */
    public String actionList() throws Exception
    {
        log.debug("::Iniciando actionList");
        

        try{
        	// Prepara os par�metros fornecidos
            this.loadEntityParams();

            /* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            
            prepareCurrentEntity(prepareCurrentEntityKey());
            
            doRunQuery();
        }catch(ProcessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Visualiza��o REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        // Redireciona a create
        log.debug("::Fim actionList");
        return QueryBean.FACES_VIEW_QUERY;
    }
    
	/**
	 * Este m�todo solicita ao processo para que adicione em sua
	 * lista de condi��es ativas a condi��o atualmente preenchida pela
	 * interface.
	 * Para preencher esta condi��o a interface referencia diretamente
	 * a propriedade QueryParam.currentProcess.condictionParam.newCondiciton. 
	 * @throws BusinessException 
	 */
    public void doAddNewCondiction() throws BusinessException{
        log.debug("Adicionando nova condi��o na lista de condi��es do processo atual");
        currentProcess.getUserReport().getCondictionParam().addNewCondiction();
	}
    
	/**
	 * Este m�todo solicita ao processo para que adicione em sua
	 * lista de condi��es ativas a condi��o atualmente preenchida pela
	 * interface e ATUALIZA IMEDIATAMENTE
	 * Para preencher esta condi��o a interface referencia diretamente
	 * a propriedade QueryParam.currentProcess.condictionParam.newCondiciton. 
	 * @throws BusinessException 
	 */
    public void doAddNewCondictionAndRefresh() throws BusinessException{
        doAddNewCondiction();
    	log.debug("Atualizando a pesquisa imediatamente");
		doRunQuery();
	}
	/**
	 * Este m�todo solicita ao processo para que adicione em sua
	 * lista de ordena��o ativa a condi��o atualmente preenchida pela
	 * interface.
	 * Para preencher esta condi��o a interface referencia diretamente
	 * a propriedade QueryParam.currentProcess.orderParam.newCondiciton. 
	 * @throws BusinessException 
	 */
    public void doAddNewOrder() throws BusinessException{
        log.debug("Adicionando nova ordem na lista de condi��es do processo atual");
        currentProcess.getUserReport().getOrderParam().addNewCondiction();
	}
    
	/**
	 * Este m�todo solicita ao processo para que adicione em sua
	 * lista de ordena��o ativa a condi��o atualmente preenchida pela
	 * interface e ATUALIZA IMEDIATAMENTE
	 * Para preencher esta condi��o a interface referencia diretamente
	 * a propriedade QueryParam.currentProcess.orderParam.newCondiciton. 
	 * @throws BusinessException 
	 */
    public void doAddNewOrderAndRefresh() throws BusinessException{
        doAddNewOrder();
    	log.debug("Atualizando a pesquisa imediatamente");
		doRunQuery();
	}
    
    
	/**
	 * Este remove uma condi��o da 
	 * lista de condi��es ativas a condi��o atualmente preenchida pela
	 * interface.
	 * Para identipreencher esta condi��o a interface referencia diretamente
	 * a propriedade QueryParam.currentProcess.condictionParam.newCondiciton. 
	 * @throws ProcessException 
	 * @throws NumberFormatException 
	 */
	public void doRemoveCondiction() throws Exception{
	    log.debug("Lendo os par�metros das cond��es");
		loadCondictionParams();
	
		log.debug("Removendo a condi��o da lista de condi��es do processo atual");
		currentProcess.getUserReport().getCondictionParam().removeCondiction(Integer.parseInt(this.currentParams.get(URL_PARAM_CONDICTION_ID).toString()));
		
		log.debug("Atualizando a pesquisa imediatamente");
		doRunQuery();
	}
	/**
	 * Este remove uma condi��o da 
	 * lista de condi��es ativas a condi��o atualmente preenchida pela
	 * interface.
	 * Para identipreencher esta condi��o a interface referencia diretamente
	 * a propriedade QueryParam.currentProcess.condictionParam.newCondiciton. 
	 * @throws ProcessException 
	 * @throws NumberFormatException 
	 */
    public void doRemoveOrder() throws Exception{
        log.debug("Lendo os par�metros das ordena��es");
		loadCondictionParams();

		log.debug("Removendo a ordem da lista de condi��es do processo atual");
		currentProcess.getUserReport().getOrderParam().removeCondiction(Integer.parseInt(this.currentParams.get(URL_PARAM_CONDICTION_ID).toString()));
    	
		log.debug("Atualizando a pesquisa imediatamente");
		doRunQuery();
	}

    /**
     * Este m�todo analisa a tual requisi��o e verifica se
     * foram recebidos par�metros de ordena��o. Se foram, eles s�o 
     * carregados para o bean e poder�o posteriormente ser
     * aplicados aos processos ativos.
     */
    private void loadCondictionParams()
	{
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_CONDICTION_ID)))
        {
            this.currentParams.put(URL_PARAM_CONDICTION_ID, super.getRequestParams().get(URL_PARAM_CONDICTION_ID).toString());
        }
	}
    
	/**
	 * Este m�todo remove TODAS AS CONDI��ES 
	 * atualmente preenchida pela interface e ATUALIZA A CONSULTA.
	 * @throws BusinessException 
	 */
	public void doClearCondictions() throws BusinessException{
	    log.debug("Limpando todas as condi��es");
	    currentProcess.getUserReport().getCondictionParam().getCondictions().clear();
		log.debug("Atualizando a pesquisa imediatamente");
		doRunQuery();
	}
	
	/**
	 * Este m�todo remove TODAS AS ORDENS 
	 * atualmente preenchida pela interface e ATUALIZA A CONSULTA.
	 * @throws BusinessException 
	 */
    public void doClearOrders() throws BusinessException{
        log.debug("Limpando todas as ordens");
        currentProcess.getUserReport().getOrderParam().getCondictions().clear();
    	log.debug("Atualizando a pesquisa imediatamente");
		doRunQuery();
	}
    
    /**
     * Este m�todo pode ser disparado pela interface, a qual fornecer�
     * por meio de par�metros URL as defini��es da ordem desejada.
     * Ele l� estes par�metros da requisi��o.
     * Aplica os par�metros no processo de pesquisa corrente
     * E pede para o processo recarregar a pesquisa. 
     * @throws BusinessException 
     */
    public void doOrder() throws BusinessException
    {
        log.debug("Iniciando a a��o doOrder");
    	/* Carregar os par�metros de ordena��o passados */
    	loadOrderParams();
    	
    	/* Altera no processo corrente os par�metros de ordena��o */
    	applyOrderParams();

		/* Solicita o processo para recarregar a cole��o */
		doRunQuery();
    }
    
    /**
     * Este m�todo aplica os atuais par�metros de ordem da pesquisa 
     * no processo de pesquisa corrente.
     * Ele poder� ser chamado quando uma a��o doOrder � requisitada,
     * ou ainda durante a primeira carga da vis�o.
     * @throws BusinessException 
     */
    private void applyOrderParams() throws BusinessException{
		/* Verifica se h� par�metros de ordena��o para serem aplicados */
    	if(this.currentParams.containsKey(URL_PARAM_ORDER_PROPERTY)){
    		/* Limpa as atuais ordena��es */
    		currentProcess.getUserReport().getOrderParam().getCondictions().clear();
    		
    		/* Preenche a Ordem */
    		currentProcess.getUserReport().getOrderParam().getNewCondiction().setPropertyPath(this.currentParams.get(URL_PARAM_ORDER_PROPERTY));
    		
    		String orderDirection = this.currentParams.get(URL_PARAM_ORDER_DIRECTION); 
    		if(StringUtils.equals(orderDirection, "asc"))
    			currentProcess.getUserReport().getOrderParam().getNewCondiction().setOrderAsc(true);
    		else
    			if(StringUtils.equals(orderDirection,"desc"))
    				currentProcess.getUserReport().getOrderParam().getNewCondiction().setOrderDesc(true);
    		
    		currentProcess.getUserReport().getOrderParam().addNewCondiction();
    	}
    }

    /**
     * Este m�todo analisa a tual requisi��o e verifica se
     * foram recebidos par�metros de ordena��o. Se foram, eles s�o 
     * carregados para o bean e poder�o posteriormente ser
     * aplicados aos processos ativos.
     */
    private void loadOrderParams()
	{
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_ORDER_PROPERTY)))
        {
            this.currentParams.put(URL_PARAM_ORDER_PROPERTY, super.getRequestParams().get(URL_PARAM_ORDER_PROPERTY).toString());
        }
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_ORDER_DIRECTION))) 
        {
            this.currentParams.put(URL_PARAM_ORDER_DIRECTION, super.getRequestParams().get(URL_PARAM_ORDER_DIRECTION).toString());
        }
	}

	/**
	 * Atualmente o jsp est� injetando o valor do filtro diretamente
	 * em queryBean.currentProcess.userReport.filterParam.filter
	 * para que o carga de um relat�rio salvo j� exiba o valor do
	 * filtro no componente na tela
	 * @throws BusinessException
	 */
    public void doFilter() throws BusinessException{
		/* Redefine a p�gina inicial antes de realizar a nova pesquisa */
		currentProcess.getUserReport().getPageParam().doReset();
		
		/* L� o par�metro do filtro */
		loadFilterParams();
		
		/* O par�metro j� � enviado pelo queryBean.currentProcess.filterParam.filter */
		applyFilterParams();

		/* Solicita o processo para recarregar a cole��o */
		doRunQuery();
	}
	
	private void applyFilterParams() throws BusinessException{
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_FILTER))) 
			currentProcess.getUserReport().getFilterParam().setFilter(this.currentParams.get(URL_PARAM_FILTER));		
	}
	
	private void loadFilterParams(){
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_FILTER))) 
    		this.currentParams.put(URL_PARAM_FILTER, super.getRequestParams().get(URL_PARAM_FILTER).toString());
	}

	private boolean isHasFilterParams(){
   		return StringUtils.isNotEmpty(this.currentParams.get(URL_PARAM_FILTER));
	}

	private void applyEntityParams() throws BusinessException{
		/* Preenche os par�metros de Entidade e Pai, S�O AS CHAVES DA VIS�O */
		try {
			currentProcess.setEntityType(Class.forName(this.getEntityParam().getTypeName()));

			/* Preenche os par�metros de entidade Pai */
			if(this.getEntityParam().getParentParam().isHasParent()){
				currentProcess.getUserReport().getParentParam().setType(Class.forName(this.getEntityParam().getParentParam().getTypeName()));
				currentProcess.getUserReport().getParentParam().setId(this.getEntityParam().getParentParam().getId());
				currentProcess.getUserReport().getParentParam().setProperty(this.getEntityParam().getParentParam().getProperty());
			}

		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

	}

	public void doPageRefresh(){
		
		/* L� o par�metro do filtro */
		loadPageParams();
		
		/* O par�metro j� � enviado pelo queryBean.currentProcess.pageParam.page */
		/* O par�metro j� � enviado pelo queryBean.currentProcess.pageParam.pageSize */
		//applyPageParams();

		/* Solicita o processo para recarregar a cole��o */
		doRunQuery();
	}
	
	private void applyPageParams() throws BusinessException{
		/* Preenche os par�metros de Pagina��o */
		currentProcess.getUserReport().getPageParam().setPage(Integer.parseInt(this.currentParams.get(URL_PARAM_PAGE_INDEX).toString()));
		currentProcess.getUserReport().getPageParam().setPageSize(Integer.parseInt(this.currentParams.get(URL_PARAM_PAGE_SIZE).toString()));
	} 

	private void loadPageParams()
	{
    	if ((super.getRequestParams().get(URL_PARAM_PAGE_INDEX) != null) && (!super.getRequestParams().get(URL_PARAM_PAGE_INDEX).equals("")))
    		this.currentParams.put(URL_PARAM_PAGE_INDEX, super.getRequestParams().get(URL_PARAM_PAGE_INDEX).toString());
    	else
    		this.currentParams.put(URL_PARAM_PAGE_INDEX, Integer.toString(PageParam.FIRST_PAGE_INDEX));
        
        if ((super.getRequestParams().get(URL_PARAM_PAGE_SIZE) != null) && (!super.getRequestParams().get(URL_PARAM_PAGE_SIZE).equals("")))
        	this.currentParams.put(URL_PARAM_PAGE_SIZE, super.getRequestParams().get(URL_PARAM_PAGE_SIZE).toString());
    	else
    		this.currentParams.put(URL_PARAM_PAGE_SIZE, "50");
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
	
	public void doGoFirstPage() throws BusinessException{
		currentProcess.getUserReport().getPageParam().goFirst();
		doRunQuery();
	}
	public void doGoPriorPage() throws BusinessException{
		currentProcess.getUserReport().getPageParam().goPrior();
		doRunQuery();
	}
	public void doGoNextPage() throws BusinessException{
		currentProcess.getUserReport().getPageParam().goNext();
		doRunQuery();
	}
	public void doGoLastPage() throws BusinessException{
		currentProcess.getUserReport().getPageParam().goLast();
		doRunQuery();
	}

    public Object[] getArray() throws Exception
    {
        return currentProcess.getUserReport().getEntityCollection().toArray();
    }
    
    public QueryCondiction getNewCondictionParam() throws Exception
    {
        return currentProcess.getUserReport().getCondictionParam().getNewCondiction();
    }

    public OrderCondiction getNewOrderParam() throws Exception
    {
        return currentProcess.getUserReport().getOrderParam().getNewCondiction();
    }

    /**
     * Este m�todo analisa as subEntidades de da entidade corrente e constroi uma lista com os metadados destas entidades.
     * @return Retorna um metadado de uma entidade do tipo das entidades que est�o 
     * armazenadas na lista.
     * @throws Exception 
     */
    private List<IEntityMetadata> subEntitiesBuffer = null;
    public List<IEntityMetadata> getSubEntities() throws Exception 
    {
    	if(subEntitiesBuffer == null){
    		subEntitiesBuffer = new ArrayList<IEntityMetadata>(4);
    		for(Class klazz: getInfo().getSubEntities())
    			subEntitiesBuffer.add(currentProcess.getProcessManager().getServiceManager().getEntityManager().getEntityMetadata(klazz));
    	}
    	return subEntitiesBuffer;
    }

    
    /**
     * Este m�todo foi implementado porque a cole��o do currentProcess.userReport.entityCollection �
     * inicialmente nula antes da primeira execu��o da pesquisa.
     * Assim, n�o h� metadados preparados.
     * E um erro era gerado.  
     * @return Retorna um metadado de uma entidade do tipo das entidades que est�o 
     * armazenadas na lista.
     * @throws Exception 
     */
    private IEntityMetadata infoBuffer = null;
    public IEntityMetadata getInfo() throws Exception 
    {
    	if(infoBuffer == null)
    		infoBuffer = currentProcess.getProcessManager().getServiceManager().getEntityManager().getEntityMetadata(this.currentProcess.getEntityType());
    	
    	return infoBuffer;
        
    }

    /**
     * Este m�todo obt�m, uma �nica vez a lista ordenada das propriedades
     * vis�veis.
     * @return
     * @throws Exception
     */
    public IPropertyMetadata[] getProperties() throws Exception
    {
        if(properties == null){
            /* Obtem a lista de todas as propriedades da entidade visiveis e invisiveis */
        	properties = getInfo().getPropertiesInQueryGrid();
        }

        return properties;
    } 
    
    /**
     * Este m�todo obt�m, uma �nica vez a lista ordenada das propriedades
     * vis�veis.
     * @return
     * @throws Exception
     */
    public IEntity getParentEntity() throws Exception
    {
        if(parentEntity == null){
            /* Obtem a lista de todas as propriedades da entidade visiveis e invisiveis */
            parentEntity = getCurrentProcess().getUserReport().retrieveParentEntity();
        }

        return parentEntity;
    }
    
    /**
     * Este m�todo obt�m os metadados da propriedade visualizada.
     * para isto, Utiliza o atual processo e o atual pai.
     * @return
     * @throws Exception
     */
    public IProperty getParentProperty() throws Exception
    {
        return getParentEntity().getProperty(currentProcess.getUserReport().getParentParam().getProperty());
    }
    
    
    /**
     * Este m�todo permite obter o tamanho do vetor de propriedades vis�veis atualmente
     * preparado pelo bean.<br>
     * Tipos Arrays n�o possuem m�todos getSize() no padr�o JavaBean 
     * @return
     * @throws Exception
     */
    public int getPropertiesSize() throws Exception
    {
        return getProperties().length;
    }
	
    /**
     * Este m�todo prepara a cole��o de objetos que ser� mostrada na lista.
     * <p>Se a lista possui uma entidade Pai, a entidade Pai � obtida e a propriedade
     * e o servi�o QueryService � executado com os par�metros do pai.
     * O filtro ser� aplicado �s entidades filhas do pai. 
     */
    public void prepareCurrentEntity(String currentEntityKey) throws BusinessException, Exception
	{
		super.prepareCurrentEntity(currentEntityKey);
		
        if(processes.containsKey(currentEntityKey)){
        	log.debug("Utilizando o processo j� ativo");
        	currentProcess = (QueryProcess) processes.get(currentEntityKey);

        	log.debug("Verificando se � uma chamada por link, para atualizar os par�metros do atual processo");
        	if(this.checkLinkRequest())
            	doReload();
        	
        }else{
    		log.debug("Iniciando um novo processo Query");
    		currentProcess = (QueryProcess) this.getApplicationBean().getProcessManager().createProcessByName(QueryProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
    		log.debug("Colocando o processo Query na lista de processos ativos");
           	processes.put(this.currentEntityKey, currentProcess);
    		
        	log.debug("Carregando os par�metros para o processo rec�m criado");
			doReload();

   		}

		/* Uma nova entidade foi preparada para a atual vis�o. Com isso, os dados locais 
		 * bufferizados precisam ser limpos. Os doRunQuery j� fazem isto, por�m o isRunQueryOpen utiliza
		 * um dado que � buiferrizado, e se o cleanBuffers() n�o for executado antes dele, ele utilizar�
		 * um buffer errado que n�o pertence � atual entidade da vis�o */
		cleanBuffers();

		if(this.isRunQueryOnOpen())
        	doRunQuery();
        else
        	/* Verifica se for link request e tem um filtro definido ent�o d� um run Query para que a 
        	 * tela de pesquisa j� seja exibida com o resultado */  
        	if(this.checkLinkRequest() && this.isHasFilterParams())
        		doRunQuery();
	}
    
    /**
     * Atualmente a chave da vis�o est� muito restrita, fazendo com que muitas vis�es
     * fiquem armazenadas na mem�ria. 
     */
	public String prepareCurrentEntityKey()
	{
		return this.getEntityParam().getTypeName() +
	       		this.getEntityParam().getParentParam().getTypeName() + 
	       		this.getEntityParam().getParentParam().getId() + 
	       		this.getEntityParam().getParentParam().getProperty();
	}
	
	/**
	 * Verifica se a atual vis�o possui uma propriedade de SELECT_ONE_ACTIVE 
	 */
	public boolean isSelectOneActive(){return currentParams.get(URL_PARAM_SELECT_ONE_DEST)!=null;}
	

	/**
	 * Este m�todo analisa o propriedade que est� definida para ser selecionada;
	 * Analisa a atual entidade da cole��o;
	 * Pega o valor da propriedade na atual entidade.
	 * @return Retorna o valor da propriedade da entidade
	 * @throws BusinessException
	 * @throws Exception
	 */
	public String getSelectPropertyValue() throws BusinessException, Exception{
		/* Obtem o valor da propriedade que ser� retornado pela tela de pesquisa */
		if(currentEntity == null)
			throw new Exception("A entidade corrente n�o est� preparada. Execute pelo menos uma vez o m�todo getNextCurrentEntity()");
		
		IProperty prop = currentEntity.getProperty(this.currentParams.get(URL_PARAM_SELECT_PROPERTY));
		
		/* Verifica se a propriedade possui uma m�scara para retorna-la sem a m�scara */
		if(prop.getInfo().isHasEditMask())
			if(prop.getValue().getAsObject()==null)
				return "";
			else
				return prop.getValue().getAsObject().toString();
		else
			return prop.getValue().getAsString();
	}	
	
	public QueryProcess getCurrentProcess() {return currentProcess;}
	
	public void doRunQuery(){
    	/* Solicita ao processo para executar a pesquisa com os atuais par�metros */
		if(!currentProcess.runQuery())
			FacesUtils.addErrorMsgs(currentProcess.getMessageList());

        /* Limpa os buffers ap�s a nova pesquisa ter sido executada */
		cleanBuffers();
	}

	/* Permite iterar a cole��o de entidades atuais */
	private int currentEntityIndex = 0;
	public IEntity getNextCurrentEntity() throws Exception{
		/* Obtem na cole��o de entidades a entidade corrente  e j� incrementa o ponteiro 
		 * para a pr�xima */
		currentEntity = (IEntity) getArray()[currentEntityIndex++];
		
		/* Se o ponteiro atingir o tamanho da lista ele volta ao in�cio para n�o ultrapassar o limite */
		if(currentEntityIndex == currentProcess.getUserReport().getEntityCollection().getSize()) currentEntityIndex = 0;

		return currentEntity;
	}
	
    /** 
     * O bean, por quest�es de desempenho, faz alguns buffers de informa��es
     * da entidade corrente. Assim, a cada troca de entidade ou prepara��o de um novo
     * processo de pesquisa, estes buffers precisam ser limpos */
	private void cleanBuffers(){
        /* Executa algumas rotinas que sempre preparam propriedades para uma nova consulta */ 
        properties = null;

        /* Zera o ponteiro de controle da entidade corrente*/
		currentEntityIndex = 0;
		
		parentEntity = null;
		
		infoBuffer = null;
		
		subEntitiesBuffer = null;
		
		/* Buffer da lista de relat�rio dispon�veis para a entidades */
		listUserReportBuffer = null;
		
		modelsLabelEntityBuffer = null;
	}
	
	public void doReload() throws BusinessException{
		log.debug("Executando doReload");
		/* Recarrega e aplica os par�metros da vis�o */
		applyEntityParams();
		
		loadFilterParams();
		applyFilterParams();
		
		loadOrderParams();
		applyOrderParams();
		
		loadPageParams();
		applyPageParams();
		
		loadSelectParams();
		
		/* Realiza a query */
		/* Lucio 20060525 N�o � necess�rio carregar a query agora...
		 * cada action ou do ir� solicitar se re-carrega ou n�o a query */
		//doRunQuery();
	}

	
	/** Buffer para evitar in�meras buscas no banco */
	private List<SelectItem> listUserReportBuffer = null;
	public List<SelectItem> getListUserReport() throws BusinessException{
		if(listUserReportBuffer==null){
			listUserReportBuffer = new ArrayList<SelectItem>();
			for(IEntity entity: UserReport.listUserReportByEntityAndUser(this.getApplicationBean().getProcessManager().getServiceManager(), this.currentProcess.getEntityType(), this.getUserSessionBean().getUserSession().getUser().getId()))
				/** Os par�metros corrente do atual beam s�o armazenados em String, logo userReportId ser� uma
				 * string e a lista de op��es para ele dever� ser tamb�m uma String */ 
				listUserReportBuffer.add(new SelectItem(Long.toString(entity.getId()), entity.toString()));

			/* Garante que tenha pelo menos um elemento vazio na lista.
			 * Isto porque durante o post do Ajax, se n�o tiver um elemento 
			 * v�lido na lista, ele n�o consegue concluir e d� erro. Lucio - 03/05/2007 */
			if(listUserReportBuffer.isEmpty()){
				listUserReportBuffer.add(new SelectItem(Long.toString(IDAO.ENTITY_UNSAVED), "(Nenhum relat�rio salvo)"));
			}
			
		}
		
		
		return listUserReportBuffer;
	}
	
	/**
	 * Este m�todo for�a a limpeza da atual lista de relat�rios
	 * dispon�veis para serem carregadas novamente do banco. �til
	 * quando o operador gravou um relat�rio recentemente e este n�o
	 * aparece na lista  
	 */
	public void doRefreshListUserReport() throws BusinessException{
		listUserReportBuffer = null;
	}

	/**
     * Esta action necessita basicamente de dois par�metros sejam
     * fornecidos pela requisi��o:
     * -entityType
     * -userReportId 
     */
    public String actionRestoreUserReport() throws Exception
    {
        log.debug("::Iniciando actionRestoreUserReport");

        try{
        	// Prepara os par�metros fornecidos
            this.loadEntityParams();

            /* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            
            prepareCurrentEntity(prepareCurrentEntityKey());
            
            this.doRestoreUserReport();
            
            /* Ativa o painel de propriedades dos filtros */
            this.getCurrentParams().put(URL_PARAM_ADVANCED_QUERY, "true");

        }catch(ProcessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Visualiza��o REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        // Redireciona a create
        log.debug("::Fim action");
        return QueryBean.FACES_VIEW_QUERY;
    }

	
	/**
     * Esta action necessita basicamente de dois par�metros sejam
     * fornecidos diretamente:
     * -entityType
     * -userReportId
     * Ele n�o utiliza os componentes e m�todos que analisam os par�metros
     * da requisi��o, pois os par�metros da requisiao s�o read-only. Assim,
     * este m�todo injeta diretamente onde s�o necess�rios os par�metros entityType e
     * userReportId.
     * Como este m�todo � �til para que outros beans chamem a pesquisa de suas
     * entidades, estes beans podem querar incluir filtros (condi��es) que restrinjam 
     * o acesso aos dados pelo operador. Assim, este m�todo somente carrega os par�metros
     * do relat�rio, mas n�o invoca o doRunQuery() que atualizaria a tela com o resultado
     * da pesquisa.
     * <b>O doRunQuery() deve ser invocado pelo bean chamador.</b>
     */ 
    public String actionRestoreUserReport(String entityType, long userReportId) throws Exception
    {
        log.debug("::Iniciando actionRestoreUserReport");

        /* Prepara os par�metros fornecidos diretamente no bean que controla o par�metro.
         * Pois n�o � poss�vel injetar este par�metro no mapa de par�metro da requisi��o 
         * (this.getRequestParams, FacesUtils.getRequestParams). Ele � read-only.
         * Assim, � necess�rio informar diretamente o que deve ser informado para a 
         * prepara��o do QueryBean
         */
        this.getEntityParam().setTypeName(entityType);

        /* prepara entidade corrente */ 
        log.debug("::Preparando a entidade corrente");
        prepareCurrentEntity(prepareCurrentEntityKey());

        /* Recupera o relat�rio do banco de dados */
       	currentProcess.getUserReport().restoreReport(userReportId);		
        	
       	/* Ativa o painel de propriedades dos filtros */
        this.getCurrentParams().put(URL_PARAM_ADVANCED_QUERY, "true");
        	
       	/* O processo n�o recarregar� a cole��o neste momento,
       	 * pois quem chamou este m�todo pode querer adicionar novas
       	 * condi��es � pesquisa, e o chamador executar� o doRunQuery
       	 * para atualizar */
//       	doRunQuery();

       	FacesUtils.addInfoMsg("O relat�rio foi carregado com SUCESSO.");

        log.debug("::Fim action");
        return QueryBean.FACES_VIEW_QUERY;
    }

	
	public void doRestoreUserReport() throws BusinessException{
		/* N�O L� o par�metro do UserReportId 
		 * O mesmo � ligado ao HtmlInputText */
		loadUserReportIdParam();
		
		/* Verifica se o id � inv�lido */
        if(FacesUtils.isNotNull(this.currentParams.get(URL_PARAM_USER_REPORT_ID)) && (Long.parseLong(this.currentParams.get(URL_PARAM_USER_REPORT_ID)) != -1)){
        	/* Recupera o relat�rio do banco de dados */
        	currentProcess.getUserReport().restoreReport(Long.parseLong(this.currentParams.get(URL_PARAM_USER_REPORT_ID)));		
        	
        	/* Solicita o processo para recarregar a cole��o */
        	doRunQuery();
        	
        	FacesUtils.addInfoMsg("O relat�rio foi carregado com SUCESSO.");
        }else
			FacesUtils.addInfoMsg("O relat�rio selecionado n�o � v�lido.");

	}
	
	@SuppressWarnings("unused")
	private void loadUserReportIdParam(){
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_USER_REPORT_ID))) 
    		this.currentParams.put(URL_PARAM_USER_REPORT_ID, super.getRequestParams().get(URL_PARAM_USER_REPORT_ID).toString());
	}
	
	public String actionSaveUserReport() throws BusinessException{
		
		/* Os dados do relat�rio atual como Nome de outros j�
		 * s�o diretamente alimentados no bean
		 */
		/* O par�metro j� � enviado pelo queryBean.currentProcess.filterParam.filter */
		currentProcess.getUserReport().saveReport();
		
		/* Limpa o buffer de lista de relatorios para for�ar a recarga */
		doRefreshListUserReport();
		
		FacesUtils.addInfoMsg("O relat�rio foi salvo com SUCESSO.");
		
		return FacesUtils.FACES_VIEW_CLOSE;
	}
	
	public void doClearUserReport() throws BusinessException{
		
		/* Limpa todas as condi��es do relat�rio atual */
		currentProcess.getUserReport().clear();

		/* Solicita o processo para recarregar a cole��o */
		doRunQuery();

		FacesUtils.addInfoMsg("Todas as propriedades do relat�rio atual foram limpas com SUCESSO.");
	}
	
	public void doDeleteUserReport() throws BusinessException{
		
		/* N�O L� o par�metro do UserReportId 
		 * O mesmo � ligado ao HtmlInputText */
		//loadUserReportIdParam();
		
        if(FacesUtils.isNotNull(this.currentParams.get(URL_PARAM_USER_REPORT_ID))){
        	/* Remove o relat�rio da lista banco de dados */
        	currentProcess.getUserReport().deleteReport(Long.parseLong(this.currentParams.get(URL_PARAM_USER_REPORT_ID)));
        	
        	/* Define id do atual relat�rio selecionado na lista como -1 para n�o
        	 * dar erro de valida��o pois o relat�rio excluido n�o estar� mais na lista */
        	currentParams.remove(URL_PARAM_USER_REPORT_ID);

        	FacesUtils.addInfoMsg("O relat�rio foi exclu�do da lista com SUCESSO.");
        }else
			FacesUtils.addInfoMsg("O relat�rio selecionado n�o � v�lido.");

		/* Limpa o buffer de lista de relatorios para for�ar a recarga */
		doRefreshListUserReport();
	}
	
	/* Executa o download da lista de contratos  
	 * o arquivo est� na extens�o .csv (Excel)
	 */
	public void actionExport() throws BusinessException{
		
		// Prepara o outPutStream
		try {
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			response.setContentType("csv-content"); //vinculo com o Excel
			
			/* Verifica se o relat�rio possui um nome se nao tiver, usa o label da entidade */
			String fileName = this.getCurrentProcess().getUserReport().getName();
			if(StringUtils.isEmpty(fileName))
				fileName = this.getInfo().getLabel();

			//p�e em cache o nome do arquivo e sua extens�o
			response.setHeader("Content-Disposition",
					"attachment;filename=\"" + fileName + ".csv\""); 
			ServletOutputStream out = response.getOutputStream();
			
			String[][] data = this.getCurrentProcess().getUserReport().getBuildResult();//obtem a bufferiza��o das propriedades
			/*Inicia a linha com n�mero da linha do relat�rio */
			String outRow=";";
			// Montar header do arquivo com os labels das propriedades Ex: "Nome" ; "propriedade"
			for(ResultCondiction condiction: this.getCurrentProcess().getUserReport().getSelectedResult()){
				outRow += "\"" + condiction.getPropertyLabel() + "\";";
			}
			// Escreve a primera linha no arquivo -> header montado;
			out.println(outRow);
			
			int currentLine = 1; //contador para a linha
			//para cada registro na lista de entidade fa�a
			for(String[] row: data){
				// Coloca o n�mero da linha
				outRow = currentLine++ + ";";
				
				for(String col: row)
					outRow += "\"" + col + "\";";
				// Escreve as demais linha no arquivo;
				out.println(outRow);
			}
			// Montar footer do arquivo com os totais das propriedades 
			//for(ResultCondiction condiction: this.getCurrentProcess().getSelectedResult()){
			//propriedade totalizadora
//			outRow += "\"" + condiction.getSumTotal() + "\";";
			//}
			// Escreve a �ltima linha no arquivo -> total das propriedades
//			out.println(outRow);
			
			out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();			
			
		} catch (IOException e) {
			throw new BusinessException(MessageList.createSingleInternalError(e));
		} catch (Exception e) {
			throw new BusinessException(MessageList.createSingleInternalError(e));
		}
		
	}

	/**
	 * Permite definir se durante a abertura da tela deve-se se 
	 * executar a pesquisa ou n�o. Assim para sistemas com
	 * muitas entidades � melhor ficar falso, pois sempre o operador vai querer
	 * pesquisar, ja com poucas entidades cadastradas � interessante
	 * j� mostrar 
	 * @throws Exception 
	 */
//	private boolean runQueryOnOpen = false;
	public boolean isRunQueryOnOpen() throws Exception {
		return this.getInfo().getRunQueryOnOpen();
		
	}
//	public void setRunQueryOnOpen(boolean runQueryOnOpen) {		this.runQueryOnOpen = runQueryOnOpen;}
	
	
	/*
	 * ROTINAS PARA CONTROLE DE GERA��O DE ETIQUETAS
	 */
    private long modelLabelEntityId = IDAO.ENTITY_UNSAVED;
    private long addressLabelGroupId = IDAO.ENTITY_UNSAVED;
    
    public long getModelLabelEntityId() {return modelLabelEntityId;}
	public void setModelLabelEntityId(long modelLabelId) {this.modelLabelEntityId = modelLabelId;}

	public long getAddressLabelGroupId() {return addressLabelGroupId;}
	public void setAddressLabelGroupId(long addressLabelGroupId) {this.addressLabelGroupId = addressLabelGroupId;}

	/**
	 *  Cria uma lista com os modelos de etiquetas de entidades disponivel
	 *  para a entidade atualmente manipulada.
	 *  O tipo da entidade j� deve estar definido para executar este m�todo, para que ele mostra somente os modelos 
	 *  da entidade selecionada
	 */
	private List<SelectItem> modelsLabelEntityBuffer = null;
	public List<SelectItem> getModelsLabelEntity(){
		try{
			if(modelsLabelEntityBuffer==null){
				ServiceData sd = new ServiceData(ListModelLabelEntityService.SERVICE_NAME, null);
				sd.getArgumentList().setProperty(ListModelLabelEntityService.IN_ENTITY_TYPE_NAME, this.getCurrentProcess().getEntityType().getName());
				this.getApplicationBean().getProcessManager().getServiceManager().execute(sd);
				modelsLabelEntityBuffer = sd.getFirstOutput();
			}
			
			return modelsLabelEntityBuffer;
		}catch (ServiceException e){
			FacesUtils.addErrorMsgs(e.getErrorList());
			return null;
		}
    }

	/**
	 *  Cria uma lista com os grupos de etiquetas dispon�veis
	 */
	private List<SelectItem> addressLabelGroupBuffer = null;
	public List<SelectItem> getAddressLabelGroupList(){
		try{
			if(addressLabelGroupBuffer == null){
				addressLabelGroupBuffer = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(AddressLabelGroup.class, "");
		    	/* Adiciona a primeira op��o para mostar todas as etiquetas */
				addressLabelGroupBuffer.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(N�o definido)"));
			}
			
			return addressLabelGroupBuffer;
		}catch (BusinessException e){
			FacesUtils.addErrorMsgs(e.getErrorList());
			return null;
		}
    }

	public boolean isHasModelsLabelEntity(){
		return this.getModelsLabelEntity().size()>0;
	}

	/** Cria etiquetas para todas as entidades listadas na
	 * pesquisa utilizando o modelo de etiqueta de entidade seleiconado  
	 */
	public void actionCreateLabels() throws BusinessException{
		
		// Prepara o outPutStream
		try {
			this.getCurrentProcess().getUserReport().runCreateLabels(this.modelLabelEntityId, this.addressLabelGroupId);//obtem a bufferiza��o das propriedades
			FacesUtils.addInfoMsg("Etiquetas geradas com SUCESSO.");
			
			this.getLabelBean().doReload();

			
		} catch (Exception e) {
			throw new BusinessException(MessageList.createSingleInternalError(e));
		}
		
	}

	/*
	 * FIM - ROTINAS PARA CONTROLE DE GERA��O DE ETIQUETAS
	 */

}