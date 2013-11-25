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
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.labels.entities.AddressLabel;
import br.com.orionsoft.monstrengo.crud.labels.entities.AddressLabelGroup;
import br.com.orionsoft.monstrengo.crud.labels.entities.ModelLabel;
import br.com.orionsoft.monstrengo.crud.labels.support.JasperPrintLabel;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.security.UserSessionBean;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Bean que controla a view de impressão de etiquetas
 * 
 * @jsf.bean name="labelBean" scope="session"
 * 
 */
@ManagedBean
@SessionScoped
public class LabelBean extends BeanSessionBasic {

	private static final long serialVersionUID = 1L;

	public static final String FACES_VIEW_LABELS = "/pages/basic/labelView?faces-redirect=true";

    private long applicationUserId=IDAO.ENTITY_UNSAVED;
    private long applicationEntityId=IDAO.ENTITY_UNSAVED;
    private long addressLabelGroupId=IDAO.ENTITY_UNSAVED;
    private long modelLabelId=IDAO.ENTITY_UNSAVED;
    private int printerIndex;

    private String orderProperty="";

    private long APPLICATION_ENTITY_NULL = IDAO.ENTITY_UNSAVED - 1;
    
    private IEntityList<AddressLabel> labelsBuffer = null;
    public IEntityList<AddressLabel> getLabels() throws BusinessException {
    	
    	if(labelsBuffer==null){
    		/* Trata os filtros por entidade */
    		String applicationEntityQuery = "";
    		if(applicationEntityId==APPLICATION_ENTITY_NULL)
        		applicationEntityQuery = IDAO.ENTITY_ALIAS_HQL + "." + AddressLabel.APPLICATION_ENTITY + " is null";
    		else
        		if(applicationEntityId!=IDAO.ENTITY_UNSAVED)
        			applicationEntityQuery = IDAO.ENTITY_ALIAS_HQL + "." + AddressLabel.APPLICATION_ENTITY + "=" + applicationEntityId;
    				
    		/* Trata os filtros por operador */
    		String applicationUserQuery = "";
       		if(applicationUserId!=IDAO.ENTITY_UNSAVED)
       			applicationUserQuery = IDAO.ENTITY_ALIAS_HQL + "." + AddressLabel.APPLICATION_USER + "=" + applicationUserId;
    		
    		/* Trata os filtros por grupo */
    		String addressLabelGroupQuery = "";
       		if(addressLabelGroupId!=IDAO.ENTITY_UNSAVED)
       			addressLabelGroupQuery = IDAO.ENTITY_ALIAS_HQL + "." + AddressLabel.ADDRESS_LABEL_GROUP + "=" + addressLabelGroupId;
    		
    		/* Constroi a HQL */
       		String hqlQuery = " TRUE=TRUE ";
       		if(StringUtils.isNotEmpty(applicationUserQuery)){
       			hqlQuery += " AND ";
       			hqlQuery += applicationUserQuery;
       		}

       		if(StringUtils.isNotEmpty(applicationEntityQuery)){
       			hqlQuery += " AND ";
       			hqlQuery += applicationEntityQuery;
       		}

       		if(StringUtils.isNotEmpty(addressLabelGroupQuery)){
       			hqlQuery += " AND ";
       			hqlQuery += addressLabelGroupQuery;
       		}

       		/* Verifica se possui alguma ordem especificada */
        	if(StringUtils.isNotEmpty(this.orderProperty))
        		hqlQuery += " ORDER BY " + this.orderProperty;
    		
    		ServiceData sdList = new ServiceData(ListService.SERVICE_NAME, null);
			sdList.getArgumentList().setProperty(ListService.CLASS, AddressLabel.class);
			sdList.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, hqlQuery);
			this.getApplicationBean().getProcessManager().getServiceManager().execute(sdList);
			
			labelsBuffer = sdList.getFirstOutput();

//			/* Se tiver um operador definido, filtra somente as etiquetas do operador */
//    		if((applicationUserId!=IDAO.ENTITY_UNSAVED) && (applicationEntityId!=IDAO.ENTITY_UNSAVED))
//    		{
//    			ServiceData sdList = new ServiceData(ListService.SERVICE_NAME, null);
//    			sdList.getArgumentList().setProperty(ListService.CLASS, AddressLabel.class);
//    			sdList.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, IDAO.ENTITY_ALIAS_HQL + "." + AddressLabel.APPLICATION_USER + "=" + applicationUserId + " and " + IDAO.ENTITY_ALIAS_HQL + "." + AddressLabel.APPLICATION_ENTITY + "=" + applicationEntityId);
//    			this.getApplicationBean().getProcessManager().getServiceManager().execute(sdList);
//    			
//    			labelsBuffer = sdList.getFirstOutput();
//    		}else if(applicationUserId!=IDAO.ENTITY_UNSAVED)
//    		{
//    			ServiceData sdList = new ServiceData(ListService.SERVICE_NAME, null);
//    			sdList.getArgumentList().setProperty(ListService.CLASS, AddressLabel.class);
//    			sdList.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, IDAO.ENTITY_ALIAS_HQL + "." + AddressLabel.APPLICATION_USER + "=" + applicationUserId);
//    			this.getApplicationBean().getProcessManager().getServiceManager().execute(sdList);
//    			
//    			labelsBuffer = sdList.getFirstOutput();
//    		}else if(applicationEntityId!=IDAO.ENTITY_UNSAVED)
//    		{
//    			ServiceData sdList = new ServiceData(ListService.SERVICE_NAME, null);
//    			sdList.getArgumentList().setProperty(ListService.CLASS, AddressLabel.class);
//    			sdList.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, IDAO.ENTITY_ALIAS_HQL + "." + AddressLabel.APPLICATION_ENTITY + "=" + applicationEntityId);
//    			this.getApplicationBean().getProcessManager().getServiceManager().execute(sdList);
//    			
//    			labelsBuffer = sdList.getFirstOutput();
//    		}else{
//    		
//    			/* Senão, exibe todas as etiquetas que estão no banco */
//    			labelsBuffer  = UtilsCrud.list(this.getApplicationBean().getProcessManager().getServiceManager(),
//    					AddressLabel.class,
//    					null);
//    		}
    	}
    	
    	return labelsBuffer;
	}
    
    public int getLabelsCount(){
    	return labelsBuffer.size();
    }
    
    public boolean isHasLabels() throws BusinessException{
    	return getLabels().size()>0;
    }
    
    public List<SelectItem> getApplicationUsers(){
    	List<SelectItem> result = null;
		try
		{
			result = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(ApplicationUser.class,  IDAO.ENTITY_ALIAS_HQL + "." + ApplicationUser.INACTIVE + " = FALSE");
	    	/* Adiciona a primeira opção para mostar todas as etiquetas */
	    	result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todos os operadores)"));
		} catch (EntityException e)
		{
			FacesUtils.addErrorMsgs(e.getErrorList());
		}
    	return result;
    }

    public List<SelectItem> getApplicationEntities(){
    	List<SelectItem> result = null;
		try
		{
			result = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(ApplicationEntity.class, "");
	    	/* Adiciona a primeira opção para mostar todas as etiquetas */
	    	result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todas as entidades)"));
	    	result.add(1, new SelectItem(APPLICATION_ENTITY_NULL, "(Sem entidade definida)"));
		} catch (EntityException e)
		{
			FacesUtils.addErrorMsgs(e.getErrorList());
		}
    	return result;
    }

    public List<SelectItem> getAddressLabelGroupList(){
    	List<SelectItem> result = null;
		try
		{
			result = this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(AddressLabelGroup.class, "");
	    	/* Adiciona a primeira opção para mostar todas as etiquetas */
	    	result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todos os grupos)"));
		} catch (EntityException e)
		{
			FacesUtils.addErrorMsgs(e.getErrorList());
		}
    	return result;
    }

    public List<SelectItem> getOrderPropertyList(){
    	List<SelectItem> result = new ArrayList<SelectItem>();
		try
		{
			for(IPropertyMetadata prop : this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntityMetadata(AddressLabel.class).getProperties())
		    	result.add(new SelectItem(prop.getName(), prop.getLabel()));
		} catch (EntityException e)
		{
			FacesUtils.addErrorMsgs(e.getErrorList());
		}
    	return result;
    }
    
    public List<SelectItem> getModelsLabel(){
    	try
		{
			return this.getApplicationBean().getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(ModelLabel.class, "");
		} catch (EntityException e)
		{
			FacesUtils.addErrorMsgs(e.getErrorList());
			return null;
		}
    }

    public List<SelectItem> getPrintersIndex() throws EntityException{
    	return PrintUtils.retrievePrinters();
    }
    
    
	public void doPrint() throws BusinessException {
		log.debug("LabelBean.doPrint()");
		
		/* Recuperando o modelo */
		IEntity<ModelLabel> modelo = UtilsCrud.retrieve(this.getApplicationBean().getProcessManager().getServiceManager(),
				ModelLabel.class,
				this.modelLabelId,
				null);
		
		/* Trata a opcao Impressa: 0-Todas 1-Jah impressas 2-Ainda nao impressas */
		List<AddressLabel> etiquetas = getLabels().getObjectList();
		
		
		try {
			JasperPrintLabel.print(etiquetas, (ModelLabel) modelo.getObject(), printerIndex);
			FacesUtils.addInfoMsg("Etiquetas impressas com sucesso");
		} catch (BusinessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
		}
		
    }

    public void doDownload() throws BusinessException {
        log.debug("ImprimirDocumentoCobrancaBean.doDownload");

       	try {
       		/* Define o outputStream */
       		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
       		response.setContentType("pdf-content");
       		String fileName = "Etiquetas-"+ this.getUserSessionBean().getUserSession().getUser().toString();
       		response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + ".pdf\"");
       		ServletOutputStream out = response.getOutputStream();

    		/* Recuperando o modelo */
    		IEntity<ModelLabel> modelo = UtilsCrud.retrieve(this.getApplicationBean().getProcessManager().getServiceManager(),
    				ModelLabel.class,
    				this.modelLabelId,
    				null);
    		
    		/* Trata a opcao Impressa: 0-Todas 1-Jah impressas 2-Ainda nao impressas */
    		List<AddressLabel> etiquetas = getLabels().getObjectList();
    		JasperPrintLabel.makePdf(etiquetas, (ModelLabel) modelo.getObject(), out);
    		
    		FacesUtils.addInfoMsg("Etiquetas impressas com sucesso");
    		
   			out.flush();
   			out.close();
   			FacesContext.getCurrentInstance().responseComplete();
       	} catch (IOException e) {
       		FacesUtils.addErrorMsg(e.getMessage());
		}
    }

    public void doUpdateSelection() throws BusinessException {
		log.debug("LabelBean.gravarSelecao");
		
		/* Gravando todas as etiquetas */
		for(IEntity<AddressLabel> entity: this.getLabels())
			UtilsCrud.update(this.getApplicationBean().getProcessManager().getServiceManager(),
				entity,
				null);
		
		FacesUtils.addInfoMsg("A seleção foi gravada com sucesso");
		
    }

	public void doDeleteSelection() throws BusinessException {
		log.debug("LabelBean.excluirrSelecao");
		
		/* Excluindo todas as etiquetas */
		for(IEntity<AddressLabel> entity: this.getLabels())
			/* Verifica se a etiqeuta está selecionada para imprimir,
			 * Caso contrário, então salva a situação atual dela */
			if(entity.getProperty(AddressLabel.PRINT).getValue().getAsBoolean())
				UtilsCrud.delete(this.getApplicationBean().getProcessManager().getServiceManager(),
						entity,
						null);
			else
				UtilsCrud.update(this.getApplicationBean().getProcessManager().getServiceManager(),
						entity,
						null);

		/* Limpa o buffer atual  para forçar uma recarga */
		this.labelsBuffer = null;
		
		FacesUtils.addInfoMsg("A seleção foi excluída com sucesso");
    }

	public void doReset() throws BusinessException, Exception
	{
		// TODO Auto-generated method stub
		
	}

	public void doReload() throws BusinessException, Exception
	{
		/* Limpa o buffer atual  para forçar uma recarga */
		this.labelsBuffer = null;
	}

	public long getModelLabelId(){return modelLabelId;}
	public void setModelLabelId(long modeloId){this.modelLabelId = modeloId;}

	public long getApplicationUserId(){return applicationUserId;}
	public void setApplicationUserId(long operadorId){
		if(this.applicationUserId != operadorId){
			this.applicationUserId = operadorId;
			/* O Operador mudou, então limpa o atual buffer de etiquetas
			 * para que ele seja recarregado e venham somente as etiquetas
			 * do operador */
			labelsBuffer = null;
		}
	}

	public long getApplicationEntityId(){return applicationEntityId;}
	public void setApplicationEntityId(long applicationEntityId){
		if(this.applicationEntityId != applicationEntityId){
			this.applicationEntityId = applicationEntityId;
			/* O Operador mudou, então limpa o atual buffer de etiquetas
			 * para que ele seja recarregado e venham somente as etiquetas
			 * do operador */
			labelsBuffer = null;
		}
	}

	public long getAddressLabelGroupId(){return addressLabelGroupId;}
	public void setAddressLabelGroupId(long addressLabelGroupId){
		if(this.addressLabelGroupId != addressLabelGroupId){
			this.addressLabelGroupId = addressLabelGroupId;
			/* O Operador mudou, então limpa o atual buffer de etiquetas
			 * para que ele seja recarregado e venham somente as etiquetas
			 * do operador */
			labelsBuffer = null;
		}
	}

	public int getPrinterIndex(){return printerIndex;}
	public void setPrinterIndex(int printerIndex){this.printerIndex = printerIndex;}
	
	/**
	 * Sobreescreve o método da classe para ao ser definido um operador
	 * este ser usado como padrão na seleção dos filtros
	 */
	public void setUserSessionBean(UserSessionBean userSessionBean) {
		super.setUserSessionBean(userSessionBean);
		
		/* Tenta definir o operador atual como o filtro da lista */
		this.applicationUserId = userSessionBean.getUserSession().getUser().getId();
	}

	public String getOrderProperty() {return orderProperty;}
	public void setOrderProperty(String orderProperty) {
		if(!StringUtils.equals(this.orderProperty, orderProperty)){
			this.orderProperty = orderProperty;
			/* A ordem mudou, então limpa o atual buffer de etiquetas
			 * para que ele seja recarregado e venham somente as etiquetas
			 * do operador */
			labelsBuffer = null;
		}
	}

}
