package br.com.orionsoft.monstrengo.view.jsf.crud;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.report.entities.UserReport;
import br.com.orionsoft.monstrengo.crud.report.entities.UserReportBean;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.security.UserSessionBean;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Este bean controla a view que lista os relat�rios
 * personalizados dos operadores.
 * H� duas lista de operadores:<br>
 * userListBuffer: para op��es de filtragem
 * userTransferListBuffer: para op��es de transfer�ncia
 */
@ManagedBean
@SessionScoped
public class ListUserReportBean extends BeanSessionBasic  
{
	/** Define a view JSF que � ativada para a vis�o QUERY */
	public static final String FACES_VIEW_QUERY = "/pages/basic/listUserReport?faces-redirect=true";

	private long entityId = IDAO.ENTITY_UNSAVED;
	private long userId = IDAO.ENTITY_UNSAVED;

	/**
	 * Define o valor do item que permite a filtragem dos relat�rios de todos os 
	 * operadores, pois -1 (IDAO.ENTITY_UNSAVED) � utilizada pelo fun��o UserReport.listUserReportByUser()
	 * para detectar a listagem de relat�rios sem operadores definidos
	 */
	private long FILTER_ALL_APPLICATION_USER = IDAO.ENTITY_UNSAVED - 1;
	
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

	/** Buffer para evitar in�meras buscas no banco */
	private List<SelectItem> entityListBuffer = null;
	private List<SelectItem> userListBuffer = null;
	/**
	 * Esta lista contem somente os operadores com id >= -1, assim, o crud entender� quando
	 * algum -1 for enviado para o Entity.getPoperty().getValue().setId(-1).
	 * Isto � necess�rio, pois a userListBuffer, contem o elemento (Sem operador definido) e (Todos) com valores -1 e -2 
	 * que s� serve para a filtragem, mas n�o serve na op��o de transfer�ncia de relat�rio
	 * pelo bot�o Transferir na interface.
	 */
	private List<SelectItem> userTransferListBuffer = null;
	
	
	public List<SelectItem> getEntityList() throws BusinessException{
		if(entityListBuffer==null){
			createListsBuffer();
		}
		return entityListBuffer;
	}
	
	/** Buffer para evitar in�meras buscas no banco */
	public List<SelectItem> getUserList() throws BusinessException{
		if(userListBuffer==null){
			createListsBuffer();
		}
		return userListBuffer;
	}

	/** Buffer para evitar in�meras buscas no banco */
	public List<SelectItem> getUserTransferList() throws BusinessException{
		if(userTransferListBuffer==null){
			createListsBuffer();
		}
		return userTransferListBuffer;
	}

	private void createListsBuffer() throws BusinessException{
		IEntity<UserReportBean> userReportBean = UtilsCrud.create(this.getApplicationBean().getProcessManager().getServiceManager(), UserReportBean.class, null);
		/* Verifica se a propriedade ApplicationEntity � obrigat�ria para o UserReportBean, pois o primeiro item � vazio se n�o for obrigatoria */
		entityListBuffer = userReportBean.getProperty(UserReportBean.APPLICATION_ENTITY).getValuesList();
		
		if (userReportBean.getProperty(UserReportBean.APPLICATION_ENTITY).getInfo().isRequired())
			entityListBuffer.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todos)"));
		else
			entityListBuffer.get(0).setLabel("(Todos)");
		
		/* Lista somente os operadores ATIVOS. Assim, esta lista n�o ter� o primeiro elemento vazio */
		userListBuffer = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(ApplicationUser.class,   IDAO.ENTITY_ALIAS_HQL + "." + ApplicationUser.INACTIVE + " = FALSE");
		userTransferListBuffer =  new ArrayList<SelectItem>(userListBuffer);
		
		/* Coloca uma descri��o mais amig�vel no primeiro item vazio da lista de usu�rios para transfer�ncia */
		userTransferListBuffer.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todos)"));
		
		/* Coloca uma descri��o mais amig�vel no primeiro item vazio da lista de operadores */
		userListBuffer.add(0, new SelectItem(FILTER_ALL_APPLICATION_USER, "(Todos)"));;

		/* Define a op��o que permite lista os relat�rios que nao possuem um operador definido */
		userListBuffer.add(1, new SelectItem(IDAO.ENTITY_UNSAVED, "(Sem operador definido)"));

	}
	
	/** Indica que algum par�metro foi alterado e que a lista
	 * deve entao ser recarregada para refletir os novos par�metros */ 
	private void paramChanged(){
		userReports = null;
	}
	
	private IEntityList<UserReportBean> userReports = null;
	public IEntityList<UserReportBean> getUserReports() throws BusinessException
	{
		try
		{
			if(userReports==null){
				/* Null protected*/
				if(entityId == IDAO.ENTITY_UNSAVED && userId == FILTER_ALL_APPLICATION_USER)
					userReports = UserReport.listUserReport(this.getApplicationBean().getProcessManager().getServiceManager());
				if(entityId != IDAO.ENTITY_UNSAVED && userId != FILTER_ALL_APPLICATION_USER){
					IEntity<ApplicationEntity> applicationEntity = UtilsCrud.retrieve(this.getApplicationBean().getProcessManager().getServiceManager(), ApplicationEntity.class, entityId, null);
					Class<?> entityClass = Class.forName(applicationEntity.getProperty(ApplicationEntity.CLASS_NAME).getValue().getAsString());
					
					userReports = UserReport.listUserReportByEntityAndUser(this.getApplicationBean().getProcessManager().getServiceManager(), entityClass, userId);
				}
				if(entityId != IDAO.ENTITY_UNSAVED){
					IEntity<ApplicationEntity> applicationEntity = UtilsCrud.retrieve(this.getApplicationBean().getProcessManager().getServiceManager(), ApplicationEntity.class, entityId, null);
					Class<?> entityClass = Class.forName(applicationEntity.getProperty(ApplicationEntity.CLASS_NAME).getValue().getAsString());
					
					userReports = UserReport.listUserReportByEntity(this.getApplicationBean().getProcessManager().getServiceManager(), entityClass);
				}
				if(userId != FILTER_ALL_APPLICATION_USER){
					userReports = UserReport.listUserReportByUser(this.getApplicationBean().getProcessManager().getServiceManager(), userId);
				}
			}
			
			return userReports;
			
		}catch (BusinessException e)
		{
			throw e;
		}catch (ClassNotFoundException e)
		{
			throw new BusinessException(MessageList.createSingleInternalError(e));
		}
		
	} 
	
	public void doReset() throws BusinessException, Exception
	{
		// TODO Auto-generated method stub
	}

	public void doReload() throws BusinessException, Exception
	{
		entityListBuffer = null;
		userListBuffer = null;
		
		/* Limpa a lista atual */
		paramChanged();
	}
		

	/** 
	 * Permite salvar as latera��es de operadores que podem
	 * ser realizadas na lista do relat�rio.
	 */
	public void doSave()
	{
		try{
			for(IEntity<UserReportBean> report: getUserReports()){
				UtilsCrud.update(this.getApplicationBean().getProcessManager().getServiceManager(),
						         report,
						         null);
			}
			
		} catch (BusinessException e){
			FacesUtils.addErrorMsgs(e.getErrorList());
		}
		
	}
	
	/**
	 * Sobreescreve o m�todo da classe para ao ser definido um operador
	 * este ser usado como padr�o na sele��o dos filtros
	 */
	public void setUserSessionBean(UserSessionBean userSessionBean) {
		super.setUserSessionBean(userSessionBean);
		
		/* Tenta definir o operador atual como o filtro da lista */
		this.userId = userSessionBean.getUserSession().getUser().getId();
	}
}