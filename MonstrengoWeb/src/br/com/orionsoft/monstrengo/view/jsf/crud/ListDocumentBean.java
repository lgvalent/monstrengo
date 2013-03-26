package br.com.orionsoft.monstrengo.view.jsf.crud;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.documents.entities.ModelDocumentEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.QueryService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.security.UserSessionBean;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Este bean controla a view que lista os documentos
 * personalizados dos operadores
 * 
 * @jsf.bean name="listDocumentBean" scope="session" 
 */
@ManagedBean
@SessionScoped
public class ListDocumentBean extends BeanSessionBasic  
{

	public static final String FACES_VIEW_DOCUMENT_LIST = "/pages/basic/documentList?faces-redirect=true";
	public static final String FACES_VIEW_DOCUMENT_MENU = "/pages/basic/documentMenu?faces-redirect=true";
	public static final String FACES_VIEW_DOCUMENT_PRINT = "/pages/basic/documentPrint?faces-redirect=true";
	public static final String FACES_VIEW_DOCUMENT_UPDATE = "/pages/basic/documentUpdate?faces-redirect=true";
	public static final String FACES_VIEW_DOCUMENT_VIEW = "/pages/basic/documentView?faces-redirect=true";

	private long entityId = IDAO.ENTITY_UNSAVED;
	private long userId = IDAO.ENTITY_UNSAVED;
	
	/**
	 * Define o valor do item que permite a filtragem dos documentos que não
	 * possuem uma entidade específica (entidade coringa, #{Entity[?]}) ou que não
	 * possuem um operador definido
	 */
	private long APPLICATION_ENTITY_NULL = IDAO.ENTITY_UNSAVED - 1;
	private long APPLICATION_USER_NULL = IDAO.ENTITY_UNSAVED - 1;
	
	public long getEntityId(){return entityId;}
	public void setEntityId(long entityId){
		if(this.entityId != entityId){
			this.entityId = entityId;
			paramChanged();
		}
	}

	public long getUserId(){return userId;}
	public void setUserId(long userId){
		if(this.userId != userId){
			this.userId = userId;
			paramChanged();
		}
	}

	/** Buffer para evitar inúmeras buscas no banco */
	private List<SelectItem> entityListBuffer = null;
	private List<SelectItem> userListBuffer = null;
	private List<SelectItem> userTransferListBuffer = null;
	public List<SelectItem> getEntityList() throws BusinessException{
		if(entityListBuffer==null){
			createListsBuffer();
		}
		return entityListBuffer;
	}
	
	public List<SelectItem> getUserList() throws BusinessException{
		if(userListBuffer==null){
			createListsBuffer();
		}
		return userListBuffer;
	}

	public List<SelectItem> getUserTransferList() throws BusinessException{
		if(userTransferListBuffer==null){
			createListsBuffer();
		}
		return userTransferListBuffer;
	}

	private void createListsBuffer() throws BusinessException{
		entityListBuffer = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(ApplicationEntity.class, "");
		/* Adicionando o item TODOS */
		entityListBuffer.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todos)"));
		entityListBuffer.add(1, new SelectItem(APPLICATION_ENTITY_NULL, "(Sem entidade definida)"));
		
		userListBuffer = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(ApplicationUser.class, IDAO.ENTITY_ALIAS_HQL + "." + ApplicationUser.INACTIVE + " = FALSE");
		/* Adicionando o item TODOS */
		userListBuffer.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todos)"));
		userListBuffer.add(1, new SelectItem(APPLICATION_USER_NULL, "(Sem operador definido)"));

		userTransferListBuffer = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(ApplicationUser.class, IDAO.ENTITY_ALIAS_HQL + "." + ApplicationUser.INACTIVE + " = FALSE");
		/* Adicionando o item TODOS */
		userTransferListBuffer.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todos)"));
	}
	
	/** Indica que algum parâmetro foi alterado e que a lista
	 * deve entao ser recarregada para refletir os novos parâmetros */ 
	private void paramChanged(){
		documents = null;
	}
	
	private IEntityList<ModelDocumentEntity> documents = null;
	public IEntityList<ModelDocumentEntity> getDocuments() throws BusinessException
	{
		if(documents==null){
			ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
			sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, ModelDocumentEntity.class);

			String entityWhereClause = "";
			/* Verifica a expressão de filtro por Entidade */
			if(entityId == IDAO.ENTITY_UNSAVED){
				// Nada é filtrado
			}else
			if(entityId == APPLICATION_ENTITY_NULL){
				entityWhereClause = "(" + ModelDocumentEntity.APPLICATION_ENTITY + " is null)";
			}
			else{
				entityWhereClause = "(" + ModelDocumentEntity.APPLICATION_ENTITY + "=" + entityId + ")";
			}
			
			String userWhereClause = "";
			/* Verifica a expressão de filtro por Operador */
			if(userId == IDAO.ENTITY_UNSAVED){
				// Nada é filtrado
			}else
			if(userId == APPLICATION_USER_NULL){
				userWhereClause = "("+ModelDocumentEntity.APPLICATION_USER + " is null)";
			}else{
				userWhereClause = "("+ModelDocumentEntity.APPLICATION_USER + "=" + userId + ")";
			}

			/* Verfica se precisará do operador AND entre as expressões */
			if(StringUtils.isNotEmpty(entityWhereClause)&& StringUtils.isNotEmpty(userWhereClause))
				sd.getArgumentList().setProperty(QueryService.IN_QUERY_HQLWHERE, entityWhereClause + "and" + userWhereClause);
			else
				sd.getArgumentList().setProperty(QueryService.IN_QUERY_HQLWHERE, entityWhereClause + userWhereClause);

			sd.getArgumentList().setProperty(QueryService.IN_ORDER_EXPRESSION_OPT,  ModelDocumentEntity.NAME);
			sd.getArgumentList().setProperty(QueryService.IN_MAX_RESULT_OPT, new Integer(9999));
			this.getApplicationBean().getProcessManager().getServiceManager().execute(sd);

			documents = (IEntityList<ModelDocumentEntity>) sd.getOutputData(QueryService.OUT_ENTITY_LIST);
		}

		return documents;

	} 
	
	public void doReset() throws BusinessException, Exception
	{
		// TODO Auto-generated method stub
	}

	public void doReload() throws BusinessException, Exception
	{
		entityListBuffer = null;
		userListBuffer = null;
		
		/* Limpa a lista atual de documents */
		paramChanged();
		
	}

	/** 
	 * Permite salvar as laterações de operadores que podem
	 * ser realizadas na lista do relatório.
	 */
	public void doSave()
	{
		try{
			for(IEntity<ModelDocumentEntity> document: getDocuments()){
				UtilsCrud.update(this.getApplicationBean().getProcessManager().getServiceManager(),
						         document,
						         null);
			}
			
		} catch (BusinessException e){
			FacesUtils.addErrorMsgs(e.getErrorList());
		}
		
	}
	
	/**
	 * Sobreescreve o método da classe para ao ser definido um operador
	 * este ser usado como padrão na seleção dos filtros
	 */
	public void setUserSessionBean(UserSessionBean userSessionBean) {
		super.setUserSessionBean(userSessionBean);
		
		/* Tenta definir o operador atual como o filtro da lista */
		this.userId = userSessionBean.getUserSession().getUser().getId();
	}
	
}