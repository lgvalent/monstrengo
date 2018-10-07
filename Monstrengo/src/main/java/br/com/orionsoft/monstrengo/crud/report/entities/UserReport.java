package br.com.orionsoft.monstrengo.crud.report.entities;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.crud.report.entities.FilterParam;
import br.com.orionsoft.monstrengo.crud.report.entities.HqlWhereParam;
import br.com.orionsoft.monstrengo.crud.report.entities.OrderParam;
import br.com.orionsoft.monstrengo.crud.report.entities.PageParam;
import br.com.orionsoft.monstrengo.crud.report.entities.ParentParam;
import br.com.orionsoft.monstrengo.crud.report.entities.QueryParam;
import br.com.orionsoft.monstrengo.crud.report.entities.ResultParam;
import br.com.orionsoft.monstrengo.crud.report.entities.UserReport;
import br.com.orionsoft.monstrengo.crud.report.entities.UserReportBean;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.labels.entities.AddressLabelGroup;
import br.com.orionsoft.monstrengo.crud.labels.entities.ModelLabelEntity;
import br.com.orionsoft.monstrengo.crud.labels.services.CreateLabelFromEntityService;
import br.com.orionsoft.monstrengo.crud.services.QueryService;
import br.com.orionsoft.monstrengo.crud.services.ResultCondiction;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.crud.support.CrudExpression;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;

/**
 * Esta classe mantem todas as estruturas e funcionalidades
 * de um gerador de relatório.
 * Este gerador disponibilizar estruturas de definição
 * de condições de filtros, ordenação e seleção dos resultados.
 * 
 * <p><b>Procedimentos:</b>
 * <br>Definir o tipo da entidade: <i>setEntityType(Class)</i>
 * <br>Definir o id da entidade: <i>setEntityId(long)</i>
 * <br>Verificar se a entidade pode ser visualizada: <i>boolean mayView()</i>
 * <br>Obter a entidade por <i>(IEntity) retrieveEntity()</i>.
 * 
 * @author Tatiana 
 *
 */
public class UserReport 
{
    protected Logger log = Logger.getLogger(this.getClass());

    private Class<?> entityType;
    private IEntity<ApplicationUser> applicationUser;
    private IEntityManager entityManager;

    private UserReportBean userReportBean = null;
    
    private String name = "";
    private String description = "";
    
    private FilterParam filterParam = null;
    private HqlWhereParam hqlWhereParam = null;
    private QueryParam condictionParam = null;
    private ResultParam resultParam = null;
    private OrderParam orderParam = null;
    private PageParam pageParam = null;
    private ParentParam parentParam = null;
    
    private Long queryTime = null;
    
    private IEntityCollection<?> entityCollection=null;

	public UserReport(IEntityManager entityManager, Class<?> entityType, IEntity<ApplicationUser> applicationUser) throws BusinessException{
    	this.entityManager = entityManager;
    	this.entityType = entityType;
    	this.applicationUser = applicationUser;
    }
    
    /**
     * Obtem a entidade persistida baseado no Tipo e Id fornecidos.
     * Verifica se a visualização será possível, senão lança uma exceção.  
     * Se a entidade ainda não foi obtidade pelo processo, o serviço
     * é excutado e os valores da auditoria são preparados.
     * Caso a entidade já esteja preparada, ela é retornada. 
     * @return Uma entidade pronta para a visualização.
     * @throws BusinessException
     */
    public void runQuery() throws ProcessException
    {
    	try
    	{
    		ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
    		sd.getArgumentList().getProperties().put(QueryService.IN_ENTITY_TYPE, entityType);
    		sd.getArgumentList().getProperties().put(QueryService.IN_QUERY_FILTER, this.getFilterParam().getFilter());

    		sd.getArgumentList().getProperties().put(QueryService.IN_QUERY_HQLWHERE, this.getHqlWhereParam().getHqlWhereCompiled());

    		log.debug("Verificando se há condições definidas");
    		if(this.getCondictionParam().isHasCondictions()){
    			log.debug("Aplicando os parâmetros de Condições");
    			sd.getArgumentList().getProperties().put(QueryService.IN_QUERY_CONDICTIONS, this.getCondictionParam().getCondictions());
    		}

			log.debug("Verificando se há uma entidade pai definida");
    		if (this.getParentParam().isHasParent()){
    			log.debug("Aplicando os parâmetros de parent");
    			
    			sd.getArgumentList().getProperties().put(QueryService.IN_PARENT_CLASS_OPT, this.getParentParam().getType());
    			sd.getArgumentList().getProperties().put(QueryService.IN_PARENT_ID_OPT, this.getParentParam().getId());
    			sd.getArgumentList().getProperties().put(QueryService.IN_PARENT_PROPERTY_OPT, this.getParentParam().getProperty());
    		}
    		
			log.debug("Verificando se há uma expressão de ordem definida");
    		if(this.getOrderParam().isHasExpression()){
    			log.debug("Aplicando a expressão de Ordem");
    			sd.getArgumentList().getProperties().put(QueryService.IN_ORDER_EXPRESSION_OPT, this.getOrderParam().getOrderExpression());
    		}
    		
			log.debug("Verificando se há condições de ordem definidas");
    		if(this.getOrderParam().isHasCondictions()){
    			log.debug("Aplicando as condições Ordem");
    			sd.getArgumentList().getProperties().put(QueryService.IN_ORDER_CONDICTIONS_OPT, this.getOrderParam().getCondictions());
    		}
    		
    		log.debug("Aplicando os parâmetros de Paginação");
    		sd.getArgumentList().getProperties().put(QueryService.IN_MAX_RESULT_OPT, this.getPageParam().getPageSize());
    		sd.getArgumentList().getProperties().put(QueryService.IN_FIRST_RESULT_OPT, this.getPageParam().getFirstItemIndexPage());
    		
    		this.getEntityManager().getServiceManager().execute(sd);
    		
    		if (sd.getMessageList().isTransactionSuccess()){
    			/* Define a coleção a ser exibida */
    			entityCollection = sd.getFirstOutput();
    			
    			/* Atualiza os dados da paginação  */
    			pageParam.setItemsCount(((Long) sd.getOutputData(QueryService.OUT_LIST_SIZE)).intValue());
    			
    			/* Atualiza o dado do tempo de consulta */
    			queryTime = (Long) sd.getOutputData(QueryService.OUT_QUERY_TIME);
    		}
    		else{
    			throw new ProcessException(sd.getMessageList());
    		}
    		
    	} catch (BusinessException e)
    	{
    		/* Converte a exceção em uma ProcessException */
    		throw new ProcessException(e.getErrorList());
    	} catch (Exception e)
    	{
    		/* Converte a exceção em uma ProcessException */
    		throw new ProcessException(MessageList.createSingleInternalError(e));
    	}
    }


    /** Objetos preparados */
    public IEntity<?> retrieveParentEntity() throws ProcessException
    {
    	try
    	{
    		return UtilsCrud.retrieve(this.getEntityManager().getServiceManager(),
    				this.parentParam.getType(),
    				this.parentParam.getId(), null);
    	} catch (BusinessException e)
    	{
    		throw new ProcessException(e.getErrorList());
    	} 
    }
    
    public Class<?> getEntityType(){return entityType;}

    public IEntity<?> getApplicationUser(){return applicationUser;}
    
    public IEntityManager getEntityManager(){return entityManager;}

	public Long getQueryTime() {return queryTime;}

	public IEntityCollection<?> getEntityCollection() {return entityCollection;}

	/* Verifica se existe uma entityType definida */
	private void checkEntityType() throws BusinessException{
		if (entityType == null)
	        throw new ProcessException(MessageList.create(UserReport.class, "ENTITY_NOT_DEFINED", this.getEntityManager().getEntityMetadata(this.entityType).getLabel()));
	}

	public FilterParam getFilterParam() throws BusinessException{
		if(filterParam==null){
			checkEntityType();
			filterParam = new FilterParam(this);
		}
		return filterParam;
	}

	public HqlWhereParam getHqlWhereParam() throws BusinessException{
		if(hqlWhereParam==null){
			checkEntityType();
			hqlWhereParam = new HqlWhereParam(this);
		}
		return hqlWhereParam;
	}

	public PageParam getPageParam() throws BusinessException{
		if(pageParam==null){
			checkEntityType();
			pageParam = new PageParam(this);
		}
		return pageParam;
	}
	
	public ParentParam getParentParam() throws BusinessException{
		if(parentParam==null){
			checkEntityType();
			parentParam = new ParentParam(this);
		}
		return parentParam;
	}

	public QueryParam getCondictionParam() throws BusinessException{
		if(condictionParam==null){
			checkEntityType();
			condictionParam = new QueryParam(this);
		}
		return condictionParam;
	}
	
	public ResultParam getResultParam() throws BusinessException{
		if(resultParam==null){
			/* Verifica se existe uma entityType definida */
			checkEntityType();
			resultParam = new ResultParam(this);
		}
		return resultParam;
	}

	public OrderParam getOrderParam() throws BusinessException{
		if(orderParam==null){
			checkEntityType();
			orderParam = new OrderParam(this);
		}
		return orderParam;
	}

    
	/**
	 * Buffer dols totalizadores da lista de propriedades selecionados que é atualizada a cada getBuildResult();
	 */
	private Double[] selectedResultTotal;
	public Double[] getSelectedResultTotal(){return this.selectedResultTotal;}

	/**
	 * Buffer da lista de propriedades selecionados que é atualizada a cada getBuildResult();
	 */
	private ResultCondiction[] selectedResult;
	public ResultCondiction[] getSelectedResult(){return this.selectedResult;}

	public String[][] getBuildResult(){
    	try
    	{
    		/* Para otimizar, a lista de propriedades visiveis é criada uma unica vez */
    		this.selectedResult = this.getResultParam().getSelectedCondictions();
    		
    		/* Prepara a lista de totalizadores  */
    		this.selectedResultTotal = new Double[selectedResult.length];
    		for (int i=0; i<selectedResultTotal.length; i++)
    			selectedResultTotal[i] = 0.0;
    		
    		/* Cria um vetor bidimensional com o tamanho de 
    		 * Entidade total da pesquisa atual x núm. de propriedades selecionadas
    		 */
    		int entitiesCount = this.getPageParam().getItemsCount();
    		int propertiesCount = this.getSelectedResult().length;
    		String[][] resultArray = new String[entitiesCount][propertiesCount];
    		
    		/* Define umas páginas maiores para agilizar o processo do relatório*/
    		this.getPageParam().setPageSize(200);
    		
			int currentEntity =0 ;
    		// Para cada p?gina	
    		for(int p=PageParam.FIRST_PAGE_INDEX; p<=this.getPageParam().getPageCount();p++){ 
    			// Define a página atual e pede para o processo busca-la
    			this.getPageParam().setPage(p);
    			this.runQuery();
    			IEntityCollection<?> entities = this.getEntityCollection(); 

    			// Para cada entidade da p?gina
    			for(IEntity<?> entity: entities){ 
    				// Para cada propriedade selecionada
    				int currentProperty =0 ;
    				for(ResultCondiction condiction: selectedResult){
    					String propertyValue = CrudExpression.propertyPathToValue(entity, condiction.getPropertyPath());

    					/* Terminou de percorrer o Path, então pega o valor da popriedade encontrada */
				        resultArray[currentEntity][currentProperty] = propertyValue;
				        
				        /* Verifica se a propriedade que foi pega acima é nume?ica para somar no seu totalizador */
				        if(condiction.getPropertyInfo().isNumber()&&StringUtils.isNotEmpty(propertyValue)&& !condiction.getPropertyPath().endsWith(IDAO.PROPERTY_ID_NAME)){
				        	propertyValue = propertyValue.replace(".", "").replace(",", ".");

				        	if(NumberUtils.isNumber(propertyValue)){
				        		selectedResultTotal[currentProperty] += Double.parseDouble(propertyValue); 
				        	}
				        }

    					/* Sendo nula ou nao a propriedade jáh foi pega e passa para a próxima */
				        currentProperty++;
				     }
    				
    				currentEntity++;
    			}
    		}
    		
    		/* Elimina dígitos decimais incorretos colocando no maximo 4 casas decimais */
    		for (int i=0; i<selectedResultTotal.length; i++)
    			selectedResultTotal[i] = Math.round(selectedResultTotal[i]*10000)/10000.0;
    		
    		return resultArray;
    	} catch (BusinessException e)
    	{
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		
    		return null;
    	}
    }
	
    /**
     * Este método executa o query utilizando.
     * Verifica se a visualização será possível, senão lança uma exceção.  
     * Se a entidade ainda não foi obtidade pelo processo, o serviço
     * é excutado e os valores da auditoria são preparados.
     * Caso a entidade já esteja preparada, ela é retornada. 
     * @return Uma entidade pronta para a visualização.
     * @throws BusinessException
     */
    public String[][] getBuildResult2() throws ProcessException
    {
    	try
    	{
    		//////////////////////////////////////////////////////////////////////////////////////////
    		// Prepara a clásula SELECT somente com os campos marcados no resultado
    		//////////////////////////////////////////////////////////////////////////////////////////
    		/* Para otimizar, a lista de propriedades visiveis é criada uma unica vez */
    		this.selectedResult = this.getResultParam().getSelectedCondictions();
			String querySelect = "";
			{boolean firstFound = false;
			for(ResultCondiction condiction: selectedResult){
				
				/* Verifica se já foi encontrada a primeira expressão para colocar a virgula antes */
				if(firstFound)
					querySelect += ", ";

				if(condiction.getPropertyInfo().isCollection())
					querySelect += "ELEMENTS (" + IDAO.ENTITY_ALIAS_HQL + IDAO.PROPERTY_SEPARATOR + condiction.getPropertyPath() + ")";
				else
					querySelect += IDAO.ENTITY_ALIAS_HQL + IDAO.PROPERTY_SEPARATOR + condiction.getPropertyPath();
				
				/* Após a primeira execução, marca que a primeira já foi para permitir inserir virgula antes das demais expressoes */
				if(!firstFound) firstFound = true;
			}}

			String hqlWHere = this.getHqlWhereParam().getHqlWhereCompiled() + " true=true ";
			{boolean firstFound = false;
			for(ResultCondiction condiction: selectedResult){
				
				if(!condiction.getPropertyInfo().isCollection()){
					/* Verifica se já foi encontrada a primeira expressão para colocar a virgula antes */
					if(firstFound)
						hqlWHere += ", ";
					else
						hqlWHere += " GROUP BY ";

					hqlWHere += IDAO.ENTITY_ALIAS_HQL + IDAO.PROPERTY_SEPARATOR + condiction.getPropertyPath();

					/* Após a primeira execução, marca que a primeira já foi para permitir inserir virgula antes das demais expressoes */
					if(!firstFound) firstFound = true;
				}
			}}
    		
    		//////////////////////////////////////////////////////////////////////////////////////////
    		// Executa a query com os atuais parâmetros de query e com o select
    		//////////////////////////////////////////////////////////////////////////////////////////
			
    		ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
    		sd.getArgumentList().getProperties().put(QueryService.IN_ENTITY_TYPE, entityType);
    		sd.getArgumentList().getProperties().put(QueryService.IN_QUERY_SELECT, querySelect);
    		sd.getArgumentList().getProperties().put(QueryService.IN_QUERY_FILTER, this.getFilterParam().getFilter());

    		sd.getArgumentList().getProperties().put(QueryService.IN_QUERY_HQLWHERE, hqlWHere);

    		log.debug("Verificando se há condições definidas");
    		if(this.getCondictionParam().isHasCondictions()){
    			log.debug("Aplicando os parâmetros de Condições");
    			sd.getArgumentList().getProperties().put(QueryService.IN_QUERY_CONDICTIONS, this.getCondictionParam().getCondictions());
    		}

			log.debug("Verificando se há uma entidade pai definida");
    		if (this.getParentParam().isHasParent()){
    			log.debug("Aplicando os parâmetros de parent");
    			
    			sd.getArgumentList().getProperties().put(QueryService.IN_PARENT_CLASS_OPT, this.getParentParam().getType());
    			sd.getArgumentList().getProperties().put(QueryService.IN_PARENT_ID_OPT, this.getParentParam().getId());
    			sd.getArgumentList().getProperties().put(QueryService.IN_PARENT_PROPERTY_OPT, this.getParentParam().getProperty());
    		}
    		
			log.debug("Verificando se há uma expressão de ordem definida");
    		if(this.getOrderParam().isHasExpression()){
    			log.debug("Aplicando a expressão de Ordem");
    			sd.getArgumentList().getProperties().put(QueryService.IN_ORDER_EXPRESSION_OPT, this.getOrderParam().getOrderExpression());
    		}
    		
			log.debug("Verificando se há condições de ordem definidas");
    		if(this.getOrderParam().isHasCondictions()){
    			log.debug("Aplicando as condições Ordem");
    			sd.getArgumentList().getProperties().put(QueryService.IN_ORDER_CONDICTIONS_OPT, this.getOrderParam().getCondictions());
    		}
    		
    		log.debug("Aplicando os parâmetros de Paginação");
    		sd.getArgumentList().getProperties().put(QueryService.IN_MAX_RESULT_OPT, this.getPageParam().getPageSize());
    		sd.getArgumentList().getProperties().put(QueryService.IN_FIRST_RESULT_OPT, this.getPageParam().getFirstItemIndexPage());
    		
    		this.getEntityManager().getServiceManager().execute(sd);
    		
    		List<Object[]> list = null; 
    		if (sd.getMessageList().isTransactionSuccess()){
    			/* Define a coleção a ser exibida */
        		list = sd.getOutputData(QueryService.OUT_OBJECT_LIST);
    			entityCollection = (IEntityCollection<?>) sd.getFirstOutput();
    		}
    		else{
    			throw new ProcessException(sd.getMessageList());
    		}

    		
    		//////////////////////////////////////////////////////////////////////////////////////////
    		// Analisa o resultado para montar a saída
    		//////////////////////////////////////////////////////////////////////////////////////////
    		
    		/* Prepara a lista de totalizadores  */
    		this.selectedResultTotal = new Double[selectedResult.length];
    		for (int i=0; i<selectedResultTotal.length; i++)
    			selectedResultTotal[i] = 0.0;
    		
    		/* Cria um vetor bidimensional com o tamanho de 
    		 * Entidade total da pesquisa atual x núm. de propriedades selecionadas
    		 */
    		int entitiesCount = this.getPageParam().getItemsCount();
    		int propertiesCount = this.getSelectedResult().length;
    		String[][] resultArray = new String[entitiesCount+3][propertiesCount];
    		
    		
//    		System.out.println("==>entitiesCount=" + entitiesCount);
//    		System.out.println("==>propertiesCount=" + propertiesCount);
//    		System.out.println("==>list.size=" + list.size());
    		
			int currentEntity =0 ;
            for(Object[] row: list){
				int currentProperty = 0 ;
				for(Object col: row){
					resultArray[currentEntity][currentProperty] = col.toString();
					currentProperty++;
				}
				currentEntity++;
            }
            
    		return resultArray;

    	} catch (BusinessException e)
    	{
    		/* Converte a exceção em uma ProcessException */
    		throw new ProcessException(e.getErrorList());
    	} catch (Exception e)
    	{
    		/* Converte a exceção em uma ProcessException */
    		throw new ProcessException(MessageList.createSingleInternalError(e));
    	}
    	
    }

	/**
	 * Cria 
	 * @param modelLabelEntityId
	 * @param addressLabelGroupId Opcional. Define o grupo no qual a etiqueta será inserida
	 * @throws BusinessException
	 */
    public void runCreateLabels(long modelLabelEntityId, long addressLabelGroupId) throws BusinessException{
		IEntity<?> modelLabelEntity = UtilsCrud.retrieve(this.getEntityManager().getServiceManager(), ModelLabelEntity.class, modelLabelEntityId, null); 
		IEntity<?> addressLabelGroup = addressLabelGroupId!=IDAO.ENTITY_UNSAVED?UtilsCrud.retrieve(this.getEntityManager().getServiceManager(), AddressLabelGroup.class, addressLabelGroupId, null):null; 
		
		// Para cada p?gina	
		for(int p=PageParam.FIRST_PAGE_INDEX; p<=this.getPageParam().getPageCount();p++){ 
			// Define a página atual e pede para o processo busca-la
			this.getPageParam().setPage(p);
			this.runQuery();
			IEntityCollection<?> entities = this.getEntityCollection(); 
			
			// Para cada entidade da p?gina
			for(IEntity<?> entity: entities){ 
				/* Cria a etiqueta  */
				ServiceData sdCreateLabel = new ServiceData(CreateLabelFromEntityService.SERVICE_NAME, null);
				sdCreateLabel.getArgumentList().setProperty(CreateLabelFromEntityService.IN_APPLICATION_USER, this.applicationUser);
				sdCreateLabel.getArgumentList().setProperty(CreateLabelFromEntityService.IN_ENTITY, entity);
				sdCreateLabel.getArgumentList().setProperty(CreateLabelFromEntityService.IN_MODEL_LABEL_ENTITY, modelLabelEntity);
				sdCreateLabel.getArgumentList().setProperty(CreateLabelFromEntityService.IN_ADDRESS_LABEL_GROUP_OPT, addressLabelGroup);
				
				// executa
				this.getEntityManager().getServiceManager().execute(sdCreateLabel);
			}
		}
	}
	
	public String getName(){return name;}
	public void setName(String name){this.name = name;}

	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}

	public void saveReport() throws BusinessException{
		/* Verifica se o atual UserReportBean já foi persistido */
		if(this.getUserReportBean().getId() != IDAO.ENTITY_UNSAVED){
			/* Verifica se o nome do relatório foi mudado
			 * para então criar um novo e não exluir o velho, 
			 * mas se o nome ainda é o mesmo, o velho será
			 * apagado */
			if(this.getName().equals(this.getUserReportBean().getName())){
				/* Verifica se este relatório ainda existe no banco, pois
				 * pode ter sido excluído do banco enquanto o mesmo estava ativo
				 * na memória */
				try{
					IEntity<?> userReportEntity = UtilsCrud.retrieve(this.getEntityManager().getServiceManager(), UserReportBean.class, this.getUserReportBean().getId(), null);

					/* Se SIM: Excluir todos os PARAMS dele do Banco de dados e definir seu Id como -1 (Unsaved) */
					UtilsCrud.delete(this.getEntityManager().getServiceManager(), userReportEntity, null);

				}catch(Exception e){
					/* Nao conseguiu retornar e nem deletar, anula a atual instancia para criar uma nova durante o 
					 * getUserReportBean() na linha mais abaixo*/
				}
			}

			/* Anula a atual instancia para criar uma nova durante o getUserReportBean()*/
			this.userReportBean = null;
		}
		
		/* Adiciona os Params no atual Bean vazio (vazio pois é novo ou porque foi limpo pela rotina de cima */
		this.getUserReportBean().setName(this.getName());
		this.getUserReportBean().setDescription(this.getDescription());
		this.getUserReportBean().setDate(Calendar.getInstance());
		this.getUserReportBean().setApplicationEntity((ApplicationEntity)UtilsSecurity.retrieveEntity(this.getEntityManager().getServiceManager(), this.getEntityType(), null).getObject());
		this.getUserReportBean().setApplicationUser((ApplicationUser)this.applicationUser.getObject());
		this.getUserReportBean().setHqlWhereCondiction(this.getHqlWhereParam().getHqlWhere());
		this.getUserReportBean().setFilterCondiction(this.getFilterParam().getFilter());
		this.getUserReportBean().setOrderCondictions(this.getOrderParam().paramToBean());
		this.getUserReportBean().setPageCondiction(this.getPageParam().paramToBean());
		this.getUserReportBean().setParentCondiction(this.getParentParam().paramToBean());
		this.getUserReportBean().setQueryCondictions(this.getCondictionParam().paramToBean());
		this.getUserReportBean().setResultCondictions(this.getResultParam().paramToBean());
		
		/* Persiste o Bean*/
		UtilsCrud.update(this.getEntityManager().getServiceManager(), this.getEntityManager().getEntity(this.getUserReportBean()), null);
	}
	
	public void deleteReport(long userReportId) throws BusinessException{
		/* Verifica se o atual UserReportBean já foi persistido */
		if(this.getUserReportBean().getId() != IDAO.ENTITY_UNSAVED){
			IEntity<?> userReportEntity = this.getEntityManager().getEntity(this.getUserReportBean());
			/* Se SIM: Excluir todos os PARAMS dele do Banco de dados e definir seu Id como -1 (Unsaved) */
			UtilsCrud.delete(this.getEntityManager().getServiceManager(), userReportEntity, null);
		}
	}
	
	public void restoreReport(long userReportBeanId) throws BusinessException{
		IEntity<?> userReportEntity = UtilsCrud.retrieve(this.getEntityManager().getServiceManager(), UserReportBean.class, userReportBeanId, null);

		/* Verifica se o UserReportBean é da mesma entidade da atual instância
		 * do UserReport, pois se for de entidades diferentes não será possivel
		 * usar o atual UserReport para manipular os dados armazenados no Bean */
		if(!this.getEntityType().getName().equals(((UserReportBean)userReportEntity.getObject()).getApplicationEntity().getClassName()))
			throw new BusinessException(MessageList.create(UserReport.class, "ENTITY_TYPE_INCOMPATIBLE", this.getEntityType().getName(), ((UserReportBean)userReportEntity.getObject()).getApplicationEntity().getClassName()));
		
		this.clear();
		
		/* Adiciona os Params no atual Bean vazio (vazio pois é novo ou porque foi limpo pela rotina de cima */
		this.userReportBean = (UserReportBean) userReportEntity.getObject();
		
		this.setName(this.getUserReportBean().getName());
		this.setDescription(this.getUserReportBean().getDescription());
		this.getFilterParam().setFilter(this.getUserReportBean().getFilterCondiction());
		this.getHqlWhereParam().setHqlWhere(this.getUserReportBean().getHqlWhereCondiction());
		this.getOrderParam().beanToParam(this.getUserReportBean().getOrderCondictions());
		this.getPageParam().beanToParam(this.getUserReportBean().getPageCondiction());
		this.getParentParam().beanToParam(this.getUserReportBean().getParentCondiction());
		this.getCondictionParam().beanToParam(this.getUserReportBean().getQueryCondictions());
		this.getResultParam().beanToParam(this.getUserReportBean().getResultCondictions());
	}

	public static IEntityList<UserReportBean> listUserReport(IServiceManager svcManager) throws BusinessException{
		ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, UserReportBean.class);
		sd.getArgumentList().setProperty(QueryService.IN_ORDER_EXPRESSION_OPT,  UserReportBean.NAME);
		sd.getArgumentList().setProperty(QueryService.IN_MAX_RESULT_OPT, new Integer(9999));
		svcManager.execute(sd);
		return sd.getOutputData(QueryService.OUT_ENTITY_LIST);
	}

	public static IEntityList<UserReportBean> listUserReportByEntity(IServiceManager svcManager, Class<?> entityType) throws BusinessException{
		IEntity<ApplicationEntity> applicationEntity = UtilsSecurity.retrieveEntity(svcManager, entityType, null);
		
		ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, UserReportBean.class);
		sd.getArgumentList().setProperty(QueryService.IN_QUERY_HQLWHERE,  UserReportBean.APPLICATION_ENTITY + "=" + applicationEntity.getId());
		sd.getArgumentList().setProperty(QueryService.IN_ORDER_EXPRESSION_OPT,  UserReportBean.NAME);
		sd.getArgumentList().setProperty(QueryService.IN_MAX_RESULT_OPT, new Integer(9999));
		svcManager.execute(sd);
		return sd.getOutputData(QueryService.OUT_ENTITY_LIST);
	}

	/**
	 * Se este método receber um userId=-1 ele listará todos os relatórios que não possuem um operador definido.
	 * 
	 * @param svcManager
	 * @param userId
	 * @return Lista de relatórios que pertencen a um operador ou que não possua operador definido
	 * @throws BusinessException
	 */
	public static IEntityList<UserReportBean> listUserReportByUser(IServiceManager svcManager, long userId) throws BusinessException{
		ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, UserReportBean.class);
		
		if(userId == IDAO.ENTITY_UNSAVED)
			sd.getArgumentList().setProperty(QueryService.IN_QUERY_HQLWHERE,  UserReportBean.APPLICATION_USER + " is null");
		else
			sd.getArgumentList().setProperty(QueryService.IN_QUERY_HQLWHERE,  UserReportBean.APPLICATION_USER + "=" + userId);
		
		sd.getArgumentList().setProperty(QueryService.IN_ORDER_EXPRESSION_OPT,  UserReportBean.NAME);
		sd.getArgumentList().setProperty(QueryService.IN_MAX_RESULT_OPT, new Integer(9999));
		svcManager.execute(sd);
		return sd.getOutputData(QueryService.OUT_ENTITY_LIST);
	}

	/**
	/**
	 * Se este método receber um userId=-1 ele listará todos os relatórios que não possuem um operador definido.
	 * 
	 * @param svcManager
	 * @param entityType
	 * @param userId
	 * @return
	 * @throws BusinessException
	 */
	public static IEntityList<UserReportBean> listUserReportByEntityAndUser(IServiceManager svcManager, Class<?> entityType, long userId) throws BusinessException{
		IEntity<?> applicationEntity = UtilsSecurity.retrieveEntity(svcManager, entityType, null);

		ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, UserReportBean.class);
		if(userId == IDAO.ENTITY_UNSAVED)
			sd.getArgumentList().setProperty(QueryService.IN_QUERY_HQLWHERE,  UserReportBean.APPLICATION_USER + " is null and " + UserReportBean.APPLICATION_ENTITY + "=" + applicationEntity.getId());
		else
			sd.getArgumentList().setProperty(QueryService.IN_QUERY_HQLWHERE,  UserReportBean.APPLICATION_USER + "=" + userId + " and " + UserReportBean.APPLICATION_ENTITY + "=" + applicationEntity.getId());
		
		sd.getArgumentList().setProperty(QueryService.IN_ORDER_EXPRESSION_OPT,  UserReportBean.NAME);
		sd.getArgumentList().setProperty(QueryService.IN_MAX_RESULT_OPT, new Integer(9999));
		svcManager.execute(sd);
		return sd.getOutputData(QueryService.OUT_ENTITY_LIST);
	}

	public void clear() throws BusinessException
	{
		
		this.getUserReportBean().setId(IDAO.ENTITY_UNSAVED);
		this.getUserReportBean().setName("");
		
		this.getCondictionParam().clear();
		this.getFilterParam().clear();
		this.getHqlWhereParam().clear();
		this.getOrderParam().clear();
		this.getPageParam().clear();
		this.getParentParam().clear();
		this.getResultParam().clear();
		
	}

	/** Prove um método null-protected para ser usado somente dentro do
	 * próprio bean e pelas classes param, pois de fora o Bean não é visivel, todas as propriedades
	 * devem ser definidas diretamente no UserReport, até mesmo o nome do relatório
	 * @throws BusinessException
	 */
	public UserReportBean  getUserReportBean() throws BusinessException
	{
		if(userReportBean==null){
			checkEntityType();
			userReportBean = (UserReportBean)UtilsCrud.create(this.entityManager.getServiceManager(), UserReportBean.class, null).getObject();
		}
	
		return userReportBean;
	}

}

