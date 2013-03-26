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
 * Bean que cotrola a view de pesquisa avançada
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
	/** Define a view JSF que é ativada para a visão QUERY */
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

	/** Utilizado para indicar o id do report atualmente selecionado pelo operador na tela de seleção */
    public static final String URL_PARAM_USER_REPORT_ID = "userReportId";

    // Dados internos da classe
    private QueryProcess currentProcess = null;
    private IPropertyMetadata[] properties = null;
    private IEntity parentEntity = null;

	/**
     * Action que constrói a lista e redireciona a view para "list". 
     * @return
     */
    public String actionList() throws Exception
    {
        log.debug("::Iniciando actionList");
        

        try{
        	// Prepara os parâmetros fornecidos
            this.loadEntityParams();

            /* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            
            prepareCurrentEntity(prepareCurrentEntityKey());
            
            doRunQuery();
        }catch(ProcessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Visualização REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        // Redireciona a create
        log.debug("::Fim actionList");
        return QueryBean.FACES_VIEW_QUERY;
    }
    
	/**
	 * Este método solicita ao processo para que adicione em sua
	 * lista de condições ativas a condição atualmente preenchida pela
	 * interface.
	 * Para preencher esta condição a interface referencia diretamente
	 * a propriedade QueryParam.currentProcess.condictionParam.newCondiciton. 
	 * @throws BusinessException 
	 */
    public void doAddNewCondiction() throws BusinessException{
        log.debug("Adicionando nova condição na lista de condições do processo atual");
        currentProcess.getUserReport().getCondictionParam().addNewCondiction();
	}
    
	/**
	 * Este método solicita ao processo para que adicione em sua
	 * lista de condições ativas a condição atualmente preenchida pela
	 * interface e ATUALIZA IMEDIATAMENTE
	 * Para preencher esta condição a interface referencia diretamente
	 * a propriedade QueryParam.currentProcess.condictionParam.newCondiciton. 
	 * @throws BusinessException 
	 */
    public void doAddNewCondictionAndRefresh() throws BusinessException{
        doAddNewCondiction();
    	log.debug("Atualizando a pesquisa imediatamente");
		doRunQuery();
	}
	/**
	 * Este método solicita ao processo para que adicione em sua
	 * lista de ordenação ativa a condição atualmente preenchida pela
	 * interface.
	 * Para preencher esta condição a interface referencia diretamente
	 * a propriedade QueryParam.currentProcess.orderParam.newCondiciton. 
	 * @throws BusinessException 
	 */
    public void doAddNewOrder() throws BusinessException{
        log.debug("Adicionando nova ordem na lista de condições do processo atual");
        currentProcess.getUserReport().getOrderParam().addNewCondiction();
	}
    
	/**
	 * Este método solicita ao processo para que adicione em sua
	 * lista de ordenação ativa a condição atualmente preenchida pela
	 * interface e ATUALIZA IMEDIATAMENTE
	 * Para preencher esta condição a interface referencia diretamente
	 * a propriedade QueryParam.currentProcess.orderParam.newCondiciton. 
	 * @throws BusinessException 
	 */
    public void doAddNewOrderAndRefresh() throws BusinessException{
        doAddNewOrder();
    	log.debug("Atualizando a pesquisa imediatamente");
		doRunQuery();
	}
    
    
	/**
	 * Este remove uma condição da 
	 * lista de condições ativas a condição atualmente preenchida pela
	 * interface.
	 * Para identipreencher esta condição a interface referencia diretamente
	 * a propriedade QueryParam.currentProcess.condictionParam.newCondiciton. 
	 * @throws ProcessException 
	 * @throws NumberFormatException 
	 */
	public void doRemoveCondiction() throws Exception{
	    log.debug("Lendo os parâmetros das condções");
		loadCondictionParams();
	
		log.debug("Removendo a condição da lista de condições do processo atual");
		currentProcess.getUserReport().getCondictionParam().removeCondiction(Integer.parseInt(this.currentParams.get(URL_PARAM_CONDICTION_ID).toString()));
		
		log.debug("Atualizando a pesquisa imediatamente");
		doRunQuery();
	}
	/**
	 * Este remove uma condição da 
	 * lista de condições ativas a condição atualmente preenchida pela
	 * interface.
	 * Para identipreencher esta condição a interface referencia diretamente
	 * a propriedade QueryParam.currentProcess.condictionParam.newCondiciton. 
	 * @throws ProcessException 
	 * @throws NumberFormatException 
	 */
    public void doRemoveOrder() throws Exception{
        log.debug("Lendo os parâmetros das ordenações");
		loadCondictionParams();

		log.debug("Removendo a ordem da lista de condições do processo atual");
		currentProcess.getUserReport().getOrderParam().removeCondiction(Integer.parseInt(this.currentParams.get(URL_PARAM_CONDICTION_ID).toString()));
    	
		log.debug("Atualizando a pesquisa imediatamente");
		doRunQuery();
	}

    /**
     * Este método analisa a tual requisição e verifica se
     * foram recebidos parãmetros de ordenação. Se foram, eles são 
     * carregados para o bean e poderão posteriormente ser
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
	 * Este método remove TODAS AS CONDIÇÕES 
	 * atualmente preenchida pela interface e ATUALIZA A CONSULTA.
	 * @throws BusinessException 
	 */
	public void doClearCondictions() throws BusinessException{
	    log.debug("Limpando todas as condições");
	    currentProcess.getUserReport().getCondictionParam().getCondictions().clear();
		log.debug("Atualizando a pesquisa imediatamente");
		doRunQuery();
	}
	
	/**
	 * Este método remove TODAS AS ORDENS 
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
     * Este método pode ser disparado pela interface, a qual fornecerá
     * por meio de parâmetros URL as definições da ordem desejada.
     * Ele lê estes parâmetros da requisição.
     * Aplica os parâmetros no processo de pesquisa corrente
     * E pede para o processo recarregar a pesquisa. 
     * @throws BusinessException 
     */
    public void doOrder() throws BusinessException
    {
        log.debug("Iniciando a ação doOrder");
    	/* Carregar os parâmetros de ordenação passados */
    	loadOrderParams();
    	
    	/* Altera no processo corrente os parâmetros de ordenação */
    	applyOrderParams();

		/* Solicita o processo para recarregar a coleção */
		doRunQuery();
    }
    
    /**
     * Este método aplica os atuais parâmetros de ordem da pesquisa 
     * no processo de pesquisa corrente.
     * Ele poderá ser chamado quando uma ação doOrder é requisitada,
     * ou ainda durante a primeira carga da visão.
     * @throws BusinessException 
     */
    private void applyOrderParams() throws BusinessException{
		/* Verifica se há parâmetros de ordenação para serem aplicados */
    	if(this.currentParams.containsKey(URL_PARAM_ORDER_PROPERTY)){
    		/* Limpa as atuais ordenações */
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
     * Este método analisa a tual requisição e verifica se
     * foram recebidos parãmetros de ordenação. Se foram, eles são 
     * carregados para o bean e poderão posteriormente ser
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
	 * Atualmente o jsp está injetando o valor do filtro diretamente
	 * em queryBean.currentProcess.userReport.filterParam.filter
	 * para que o carga de um relatório salvo já exiba o valor do
	 * filtro no componente na tela
	 * @throws BusinessException
	 */
    public void doFilter() throws BusinessException{
		/* Redefine a página inicial antes de realizar a nova pesquisa */
		currentProcess.getUserReport().getPageParam().doReset();
		
		/* Lê o parâmetro do filtro */
		loadFilterParams();
		
		/* O parâmetro já é enviado pelo queryBean.currentProcess.filterParam.filter */
		applyFilterParams();

		/* Solicita o processo para recarregar a coleção */
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
		/* Preenche os parâmetros de Entidade e Pai, SÂO AS CHAVES DA VISÂO */
		try {
			currentProcess.setEntityType(Class.forName(this.getEntityParam().getTypeName()));

			/* Preenche os parâmetros de entidade Pai */
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
		
		/* Lê o parâmetro do filtro */
		loadPageParams();
		
		/* O parâmetro já é enviado pelo queryBean.currentProcess.pageParam.page */
		/* O parâmetro já é enviado pelo queryBean.currentProcess.pageParam.pageSize */
		//applyPageParams();

		/* Solicita o processo para recarregar a coleção */
		doRunQuery();
	}
	
	private void applyPageParams() throws BusinessException{
		/* Preenche os parâmetros de Paginação */
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
     * Este método analisa as subEntidades de da entidade corrente e constroi uma lista com os metadados destas entidades.
     * @return Retorna um metadado de uma entidade do tipo das entidades que estão 
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
     * Este método foi implementado porque a coleção do currentProcess.userReport.entityCollection é
     * inicialmente nula antes da primeira execução da pesquisa.
     * Assim, não há metadados preparados.
     * E um erro era gerado.  
     * @return Retorna um metadado de uma entidade do tipo das entidades que estão 
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
     * Este método obtém, uma única vez a lista ordenada das propriedades
     * visíveis.
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
     * Este método obtém, uma única vez a lista ordenada das propriedades
     * visíveis.
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
     * Este método obtém os metadados da propriedade visualizada.
     * para isto, Utiliza o atual processo e o atual pai.
     * @return
     * @throws Exception
     */
    public IProperty getParentProperty() throws Exception
    {
        return getParentEntity().getProperty(currentProcess.getUserReport().getParentParam().getProperty());
    }
    
    
    /**
     * Este método permite obter o tamanho do vetor de propriedades visíveis atualmente
     * preparado pelo bean.<br>
     * Tipos Arrays não possuem métodos getSize() no padrão JavaBean 
     * @return
     * @throws Exception
     */
    public int getPropertiesSize() throws Exception
    {
        return getProperties().length;
    }
	
    /**
     * Este método prepara a coleção de objetos que será mostrada na lista.
     * <p>Se a lista possui uma entidade Pai, a entidade Pai é obtida e a propriedade
     * e o serviço QueryService é executado com os parâmetros do pai.
     * O filtro será aplicado às entidades filhas do pai. 
     */
    public void prepareCurrentEntity(String currentEntityKey) throws BusinessException, Exception
	{
		super.prepareCurrentEntity(currentEntityKey);
		
        if(processes.containsKey(currentEntityKey)){
        	log.debug("Utilizando o processo já ativo");
        	currentProcess = (QueryProcess) processes.get(currentEntityKey);

        	log.debug("Verificando se é uma chamada por link, para atualizar os parâmetros do atual processo");
        	if(this.checkLinkRequest())
            	doReload();
        	
        }else{
    		log.debug("Iniciando um novo processo Query");
    		currentProcess = (QueryProcess) this.getApplicationBean().getProcessManager().createProcessByName(QueryProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
    		log.debug("Colocando o processo Query na lista de processos ativos");
           	processes.put(this.currentEntityKey, currentProcess);
    		
        	log.debug("Carregando os parâmetros para o processo recém criado");
			doReload();

   		}

		/* Uma nova entidade foi preparada para a atual visão. Com isso, os dados locais 
		 * bufferizados precisam ser limpos. Os doRunQuery já fazem isto, porém o isRunQueryOpen utiliza
		 * um dado que é buiferrizado, e se o cleanBuffers() não for executado antes dele, ele utilizará
		 * um buffer errado que não pertence à atual entidade da visão */
		cleanBuffers();

		if(this.isRunQueryOnOpen())
        	doRunQuery();
        else
        	/* Verifica se for link request e tem um filtro definido então dá um run Query para que a 
        	 * tela de pesquisa já seja exibida com o resultado */  
        	if(this.checkLinkRequest() && this.isHasFilterParams())
        		doRunQuery();
	}
    
    /**
     * Atualmente a chave da visão está muito restrita, fazendo com que muitas visões
     * fiquem armazenadas na memória. 
     */
	public String prepareCurrentEntityKey()
	{
		return this.getEntityParam().getTypeName() +
	       		this.getEntityParam().getParentParam().getTypeName() + 
	       		this.getEntityParam().getParentParam().getId() + 
	       		this.getEntityParam().getParentParam().getProperty();
	}
	
	/**
	 * Verifica se a atual visão possui uma propriedade de SELECT_ONE_ACTIVE 
	 */
	public boolean isSelectOneActive(){return currentParams.get(URL_PARAM_SELECT_ONE_DEST)!=null;}
	

	/**
	 * Este método analisa o propriedade que está definida para ser selecionada;
	 * Analisa a atual entidade da coleção;
	 * Pega o valor da propriedade na atual entidade.
	 * @return Retorna o valor da propriedade da entidade
	 * @throws BusinessException
	 * @throws Exception
	 */
	public String getSelectPropertyValue() throws BusinessException, Exception{
		/* Obtem o valor da propriedade que será retornado pela tela de pesquisa */
		if(currentEntity == null)
			throw new Exception("A entidade corrente não está preparada. Execute pelo menos uma vez o método getNextCurrentEntity()");
		
		IProperty prop = currentEntity.getProperty(this.currentParams.get(URL_PARAM_SELECT_PROPERTY));
		
		/* Verifica se a propriedade possui uma máscara para retorna-la sem a máscara */
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
    	/* Solicita ao processo para executar a pesquisa com os atuais parâmetros */
		if(!currentProcess.runQuery())
			FacesUtils.addErrorMsgs(currentProcess.getMessageList());

        /* Limpa os buffers após a nova pesquisa ter sido executada */
		cleanBuffers();
	}

	/* Permite iterar a coleção de entidades atuais */
	private int currentEntityIndex = 0;
	public IEntity getNextCurrentEntity() throws Exception{
		/* Obtem na coleção de entidades a entidade corrente  e já incrementa o ponteiro 
		 * para a próxima */
		currentEntity = (IEntity) getArray()[currentEntityIndex++];
		
		/* Se o ponteiro atingir o tamanho da lista ele volta ao início para não ultrapassar o limite */
		if(currentEntityIndex == currentProcess.getUserReport().getEntityCollection().getSize()) currentEntityIndex = 0;

		return currentEntity;
	}
	
    /** 
     * O bean, por questões de desempenho, faz alguns buffers de informações
     * da entidade corrente. Assim, a cada troca de entidade ou preparação de um novo
     * processo de pesquisa, estes buffers precisam ser limpos */
	private void cleanBuffers(){
        /* Executa algumas rotinas que sempre preparam propriedades para uma nova consulta */ 
        properties = null;

        /* Zera o ponteiro de controle da entidade corrente*/
		currentEntityIndex = 0;
		
		parentEntity = null;
		
		infoBuffer = null;
		
		subEntitiesBuffer = null;
		
		/* Buffer da lista de relatório disponíveis para a entidades */
		listUserReportBuffer = null;
		
		modelsLabelEntityBuffer = null;
	}
	
	public void doReload() throws BusinessException{
		log.debug("Executando doReload");
		/* Recarrega e aplica os parãmetros da visão */
		applyEntityParams();
		
		loadFilterParams();
		applyFilterParams();
		
		loadOrderParams();
		applyOrderParams();
		
		loadPageParams();
		applyPageParams();
		
		loadSelectParams();
		
		/* Realiza a query */
		/* Lucio 20060525 Não é necessário carregar a query agora...
		 * cada action ou do irá solicitar se re-carrega ou não a query */
		//doRunQuery();
	}

	
	/** Buffer para evitar inúmeras buscas no banco */
	private List<SelectItem> listUserReportBuffer = null;
	public List<SelectItem> getListUserReport() throws BusinessException{
		if(listUserReportBuffer==null){
			listUserReportBuffer = new ArrayList<SelectItem>();
			for(IEntity entity: UserReport.listUserReportByEntityAndUser(this.getApplicationBean().getProcessManager().getServiceManager(), this.currentProcess.getEntityType(), this.getUserSessionBean().getUserSession().getUser().getId()))
				/** Os parâmetros corrente do atual beam são armazenados em String, logo userReportId será uma
				 * string e a lista de opções para ele deverá ser também uma String */ 
				listUserReportBuffer.add(new SelectItem(Long.toString(entity.getId()), entity.toString()));

			/* Garante que tenha pelo menos um elemento vazio na lista.
			 * Isto porque durante o post do Ajax, se não tiver um elemento 
			 * válido na lista, ele não consegue concluir e dá erro. Lucio - 03/05/2007 */
			if(listUserReportBuffer.isEmpty()){
				listUserReportBuffer.add(new SelectItem(Long.toString(IDAO.ENTITY_UNSAVED), "(Nenhum relatório salvo)"));
			}
			
		}
		
		
		return listUserReportBuffer;
	}
	
	/**
	 * Este método força a limpeza da atual lista de relatórios
	 * disponíveis para serem carregadas novamente do banco. Útil
	 * quando o operador gravou um relatório recentemente e este não
	 * aparece na lista  
	 */
	public void doRefreshListUserReport() throws BusinessException{
		listUserReportBuffer = null;
	}

	/**
     * Esta action necessita basicamente de dois parâmetros sejam
     * fornecidos pela requisição:
     * -entityType
     * -userReportId 
     */
    public String actionRestoreUserReport() throws Exception
    {
        log.debug("::Iniciando actionRestoreUserReport");

        try{
        	// Prepara os parâmetros fornecidos
            this.loadEntityParams();

            /* prepara entidade corrente */ 
            log.debug("::Preparando a entidade corrente");
            
            prepareCurrentEntity(prepareCurrentEntityKey());
            
            this.doRestoreUserReport();
            
            /* Ativa o painel de propriedades dos filtros */
            this.getCurrentParams().put(URL_PARAM_ADVANCED_QUERY, "true");

        }catch(ProcessException e){
        	FacesUtils.addErrorMsgs(e.getErrorList());
        	/* Visualização REJEITADA */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        // Redireciona a create
        log.debug("::Fim action");
        return QueryBean.FACES_VIEW_QUERY;
    }

	
	/**
     * Esta action necessita basicamente de dois parâmetros sejam
     * fornecidos diretamente:
     * -entityType
     * -userReportId
     * Ele não utiliza os componentes e métodos que analisam os parâmetros
     * da requisição, pois os parâmetros da requisiao são read-only. Assim,
     * este método injeta diretamente onde são necessários os parâmetros entityType e
     * userReportId.
     * Como este método é útil para que outros beans chamem a pesquisa de suas
     * entidades, estes beans podem querar incluir filtros (condições) que restrinjam 
     * o acesso aos dados pelo operador. Assim, este método somente carrega os parâmetros
     * do relatório, mas não invoca o doRunQuery() que atualizaria a tela com o resultado
     * da pesquisa.
     * <b>O doRunQuery() deve ser invocado pelo bean chamador.</b>
     */ 
    public String actionRestoreUserReport(String entityType, long userReportId) throws Exception
    {
        log.debug("::Iniciando actionRestoreUserReport");

        /* Prepara os parâmetros fornecidos diretamente no bean que controla o parâmetro.
         * Pois não é possível injetar este parâmetro no mapa de parâmetro da requisição 
         * (this.getRequestParams, FacesUtils.getRequestParams). Ele é read-only.
         * Assim, é necessário informar diretamente o que deve ser informado para a 
         * preparação do QueryBean
         */
        this.getEntityParam().setTypeName(entityType);

        /* prepara entidade corrente */ 
        log.debug("::Preparando a entidade corrente");
        prepareCurrentEntity(prepareCurrentEntityKey());

        /* Recupera o relatório do banco de dados */
       	currentProcess.getUserReport().restoreReport(userReportId);		
        	
       	/* Ativa o painel de propriedades dos filtros */
        this.getCurrentParams().put(URL_PARAM_ADVANCED_QUERY, "true");
        	
       	/* O processo não recarregará a coleção neste momento,
       	 * pois quem chamou este método pode querer adicionar novas
       	 * condições à pesquisa, e o chamador executará o doRunQuery
       	 * para atualizar */
//       	doRunQuery();

       	FacesUtils.addInfoMsg("O relatório foi carregado com SUCESSO.");

        log.debug("::Fim action");
        return QueryBean.FACES_VIEW_QUERY;
    }

	
	public void doRestoreUserReport() throws BusinessException{
		/* NÂO Lê o parâmetro do UserReportId 
		 * O mesmo é ligado ao HtmlInputText */
		loadUserReportIdParam();
		
		/* Verifica se o id é inválido */
        if(FacesUtils.isNotNull(this.currentParams.get(URL_PARAM_USER_REPORT_ID)) && (Long.parseLong(this.currentParams.get(URL_PARAM_USER_REPORT_ID)) != -1)){
        	/* Recupera o relatório do banco de dados */
        	currentProcess.getUserReport().restoreReport(Long.parseLong(this.currentParams.get(URL_PARAM_USER_REPORT_ID)));		
        	
        	/* Solicita o processo para recarregar a coleção */
        	doRunQuery();
        	
        	FacesUtils.addInfoMsg("O relatório foi carregado com SUCESSO.");
        }else
			FacesUtils.addInfoMsg("O relatório selecionado não é válido.");

	}
	
	@SuppressWarnings("unused")
	private void loadUserReportIdParam(){
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_USER_REPORT_ID))) 
    		this.currentParams.put(URL_PARAM_USER_REPORT_ID, super.getRequestParams().get(URL_PARAM_USER_REPORT_ID).toString());
	}
	
	public String actionSaveUserReport() throws BusinessException{
		
		/* Os dados do relatório atual como Nome de outros já
		 * são diretamente alimentados no bean
		 */
		/* O parâmetro já é enviado pelo queryBean.currentProcess.filterParam.filter */
		currentProcess.getUserReport().saveReport();
		
		/* Limpa o buffer de lista de relatorios para forçar a recarga */
		doRefreshListUserReport();
		
		FacesUtils.addInfoMsg("O relatório foi salvo com SUCESSO.");
		
		return FacesUtils.FACES_VIEW_CLOSE;
	}
	
	public void doClearUserReport() throws BusinessException{
		
		/* Limpa todas as condições do relatório atual */
		currentProcess.getUserReport().clear();

		/* Solicita o processo para recarregar a coleção */
		doRunQuery();

		FacesUtils.addInfoMsg("Todas as propriedades do relatório atual foram limpas com SUCESSO.");
	}
	
	public void doDeleteUserReport() throws BusinessException{
		
		/* NÂO Lê o parâmetro do UserReportId 
		 * O mesmo é ligado ao HtmlInputText */
		//loadUserReportIdParam();
		
        if(FacesUtils.isNotNull(this.currentParams.get(URL_PARAM_USER_REPORT_ID))){
        	/* Remove o relatório da lista banco de dados */
        	currentProcess.getUserReport().deleteReport(Long.parseLong(this.currentParams.get(URL_PARAM_USER_REPORT_ID)));
        	
        	/* Define id do atual relatório selecionado na lista como -1 para não
        	 * dar erro de validação pois o relatório excluido não estará mais na lista */
        	currentParams.remove(URL_PARAM_USER_REPORT_ID);

        	FacesUtils.addInfoMsg("O relatório foi excluído da lista com SUCESSO.");
        }else
			FacesUtils.addInfoMsg("O relatório selecionado não é válido.");

		/* Limpa o buffer de lista de relatorios para forçar a recarga */
		doRefreshListUserReport();
	}
	
	/* Executa o download da lista de contratos  
	 * o arquivo está na extensão .csv (Excel)
	 */
	public void actionExport() throws BusinessException{
		
		// Prepara o outPutStream
		try {
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			response.setContentType("csv-content"); //vinculo com o Excel
			
			/* Verifica se o relatório possui um nome se nao tiver, usa o label da entidade */
			String fileName = this.getCurrentProcess().getUserReport().getName();
			if(StringUtils.isEmpty(fileName))
				fileName = this.getInfo().getLabel();

			//põe em cache o nome do arquivo e sua extensão
			response.setHeader("Content-Disposition",
					"attachment;filename=\"" + fileName + ".csv\""); 
			ServletOutputStream out = response.getOutputStream();
			
			String[][] data = this.getCurrentProcess().getUserReport().getBuildResult();//obtem a bufferização das propriedades
			/*Inicia a linha com número da linha do relatório */
			String outRow=";";
			// Montar header do arquivo com os labels das propriedades Ex: "Nome" ; "propriedade"
			for(ResultCondiction condiction: this.getCurrentProcess().getUserReport().getSelectedResult()){
				outRow += "\"" + condiction.getPropertyLabel() + "\";";
			}
			// Escreve a primera linha no arquivo -> header montado;
			out.println(outRow);
			
			int currentLine = 1; //contador para a linha
			//para cada registro na lista de entidade faça
			for(String[] row: data){
				// Coloca o número da linha
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
			// Escreve a última linha no arquivo -> total das propriedades
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
	 * executar a pesquisa ou não. Assim para sistemas com
	 * muitas entidades é melhor ficar falso, pois sempre o operador vai querer
	 * pesquisar, ja com poucas entidades cadastradas é interessante
	 * já mostrar 
	 * @throws Exception 
	 */
//	private boolean runQueryOnOpen = false;
	public boolean isRunQueryOnOpen() throws Exception {
		return this.getInfo().getRunQueryOnOpen();
		
	}
//	public void setRunQueryOnOpen(boolean runQueryOnOpen) {		this.runQueryOnOpen = runQueryOnOpen;}
	
	
	/*
	 * ROTINAS PARA CONTROLE DE GERAÇÂO DE ETIQUETAS
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
	 *  O tipo da entidade já deve estar definido para executar este método, para que ele mostra somente os modelos 
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
	 *  Cria uma lista com os grupos de etiquetas disponíveis
	 */
	private List<SelectItem> addressLabelGroupBuffer = null;
	public List<SelectItem> getAddressLabelGroupList(){
		try{
			if(addressLabelGroupBuffer == null){
				addressLabelGroupBuffer = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(AddressLabelGroup.class, "");
		    	/* Adiciona a primeira opção para mostar todas as etiquetas */
				addressLabelGroupBuffer.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Não definido)"));
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
			this.getCurrentProcess().getUserReport().runCreateLabels(this.modelLabelEntityId, this.addressLabelGroupId);//obtem a bufferização das propriedades
			FacesUtils.addInfoMsg("Etiquetas geradas com SUCESSO.");
			
			this.getLabelBean().doReload();

			
		} catch (Exception e) {
			throw new BusinessException(MessageList.createSingleInternalError(e));
		}
		
	}

	/*
	 * FIM - ROTINAS PARA CONTROLE DE GERAÇÂO DE ETIQUETAS
	 */

}