package br.com.orionsoft.monstrengo.view.jsf.security;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditCrudRegister;
import br.com.orionsoft.monstrengo.auditorship.services.CheckAuditCrudService;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanRequestBasic;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Bean que controla a view de verificação das alterações de uma determinada
 * entidade, exibindo um relatório da auditoria
 * @jsf.bean name="checkAuditCrudBean" scope="session"
 */
@ManagedBean
@RequestScoped
public class CheckAuditCrudBean extends BeanRequestBasic{

    /** Define a view JSF que é ativada para a visão RETRIEVE */
	public static final String FACES_VIEW = "/pages/basic/securityCheckAuditCrud?faces-redirect=true";
	
	public static String URL_PARAM_ENTITY_TYPE = "entityType";
    public static String URL_PARAM_ENTITY_ID = "entityId";
    
    private Class<?> entityType = null;
    private long entityId = IDAO.ENTITY_UNSAVED;
    
    @Override
    public String actionStart() {
    	return FACES_VIEW;
    }
    
    public static class AuditCollectionBean{
    	AuditCollectionBean(IEntity<?> entity){
    		this.entity = entity;
    	}
    	private final IEntity<?> entity;
    	private IEntityList<AuditCrudRegister> result;

    	public IEntity<?> getEntity() {return entity;}
    	
    	public IEntityList<AuditCrudRegister> getResult() {return result;}
    	public void setResult(IEntityList<AuditCrudRegister> result) {this.result = result;}
    	
    	public boolean isNotEmpty(){
    		return (this.result!=null && !this.result.isEmpty());
    	}

    }
    
    public static class AuditPropertyBean{
    	public AuditPropertyBean(IProperty property) {
			this.property = property;
			/* Verifica se a propriedade é collection para cria a lista de resultados
			 * deste tipo de porpriedade */
			if(property.getInfo().isCollection())
				results = new ArrayList<AuditCollectionBean>();
		}

    	private final IProperty property;
    	private IEntityList<AuditCrudRegister> result;
    	private List<AuditCollectionBean> results;
		
    	public IProperty getProperty() {return property;}

    	public IEntityList<AuditCrudRegister> getResult() {return result;}
		public void setResult(IEntityList<AuditCrudRegister> result) {this.result = result;}
		
		public List<AuditCollectionBean> getResults() {return results;}
		public void setResults(List<AuditCollectionBean> results) {this.results = results;}
		
		public boolean isNotEmpty(){
			boolean result = false;
			
			/* Verifica o resultado de propriedade simples ou de coleção */
			if(property.getInfo().isCollection())
				for(AuditCollectionBean bean: results)
					result = result || bean.isNotEmpty();
			else
				result = (this.result != null && this.result.size()>0);
			
			return result;
		}
    }
    
    
    private IEntity<?> entity = null;
    public IEntity<?> getEntity() throws Exception {
    	if(this.entity == null){
    		loadParams();
    		this.entity = UtilsCrud.retrieve(this.getApplicationBean().getProcessManager().getServiceManager(),
    										 this.entityType,
    										 this.entityId,
    										 null);
    	}

    	return this.entity;
    }
    
    public List<IProperty> getSubEntities(){
    	List<IProperty> result = new ArrayList<IProperty>();
    	if(this.entity != null)
    		for(IProperty prop: this.entity.getPropertiesMap().values())
				if(prop.getInfo().isEntity() &&
				   !prop.getInfo().isCalculated() &&
				   !prop.getInfo().isCollection() &&
				   !prop.getValue().isValueNull())
					result.add(prop);
    	
		return result;
    }
    
    private IEntityList<AuditCrudRegister> result = null;
    public IEntityList<AuditCrudRegister> getResult(){
    	try
    	{
    		if(this.result == null){
    			loadParams();
    			
    			ServiceData sd = new ServiceData(CheckAuditCrudService.SERVICE_NAME, null);
    			sd.getArgumentList().setProperty(CheckAuditCrudService.IN_ENTITY_TYPE, entityType);
    			sd.getArgumentList().setProperty(CheckAuditCrudService.IN_ENTITY_ID, entityId);
    			this.getApplicationBean().getProcessManager().getServiceManager().execute(sd);
    			
    			result = sd.getFirstOutput(); 
    		}
    		return result;

    	} catch (ServiceException e)
    	{
    		FacesUtils.addErrorMsgs(e.getErrorList());
    		return null;
    	}
    	
    }
    
    private List<AuditPropertyBean> propertiesResult = null;
    public List<AuditPropertyBean> getPropertiesResult(){
    	/* Se o resultado já estiver preparado, retorná-o */
    	if(propertiesResult != null)
    		return propertiesResult;
    	
    	/* Cria uma lista com as propriedades que serão auditadas */
		List<AuditPropertyBean> propertiesBean = new ArrayList<AuditPropertyBean>();
    	try
    	{
        	if(this.entity != null)
            	/* Verifica se a propriedade é do tipo entidade, se não é calculada e se tem algum valor */
        		for(IProperty prop: this.entity.getPropertiesMap().values())
    				if(prop.getInfo().isEntity() &&
    				   !prop.getInfo().isCalculated() &&    				   
    				   !prop.getValue().isValueNull()){
    					
    					/* Prepara a auditoria da propriedade */
    					AuditPropertyBean bean = new AuditPropertyBean(prop);
    					
    					/* Se não for coleção será realizada a busca pela auditoria da entidade referenciada pela propriedade */
    					if(!prop.getInfo().isCollection()){
    		    			ServiceData sd = new ServiceData(CheckAuditCrudService.SERVICE_NAME, null);
    		    			sd.getArgumentList().setProperty(CheckAuditCrudService.IN_ENTITY_TYPE, prop.getInfo().getType());
    		    			sd.getArgumentList().setProperty(CheckAuditCrudService.IN_ENTITY_ID, prop.getValue().getId());
    		    			this.getApplicationBean().getProcessManager().getServiceManager().execute(sd);
    		    			
    		    			bean.result = sd.getFirstOutput(); 
    					}else{
    						/* Prepara a auditoria de cada entidade que se encontra na coleção */
    						for(IEntity<?> propColValue: prop.getValue().getAsEntityCollection()){

    							ServiceData sd = new ServiceData(CheckAuditCrudService.SERVICE_NAME, null);
        		    			sd.getArgumentList().setProperty(CheckAuditCrudService.IN_ENTITY_TYPE, propColValue.getInfo().getType());
        		    			sd.getArgumentList().setProperty(CheckAuditCrudService.IN_ENTITY_ID, propColValue.getId());
        		    			this.getApplicationBean().getProcessManager().getServiceManager().execute(sd);
        		    			
    							AuditCollectionBean colBean = new AuditCollectionBean(propColValue);
        		    			colBean.result = sd.getFirstOutput(); 
        						
        						/* Lucio 20111129: Adiciona a entidade mesmo se não houve registro de auditoria na mesma */
//        		    			if(colBean.isNotEmpty())
        		    				bean.results.add(colBean);
    						}
    					}
    					
						/* Lucio 20111129: Adiciona a propriedade mesmo se não houver registros de auditoria na mesma */
//		    			if(colBean.isNotEmpty())
   						propertiesBean.add(bean);
    				}
        	return propertiesBean;

    	} catch (BusinessException e)
    	{
    		FacesUtils.addErrorMsgs(e.getErrorList());
    		return null;
    	}
    	
    }

    private void loadParams(){
    	try
    	{
    		if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_ENTITY_ID))) 
    			this.entityId = Long.parseLong(super.getRequestParams().get(URL_PARAM_ENTITY_ID).toString());
    		if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_ENTITY_TYPE)))
    			this.entityType = Class.forName(super.getRequestParams().get(URL_PARAM_ENTITY_TYPE).toString());
    	} catch (ClassNotFoundException e)
    	{
    		FacesUtils.addErrorMsg(e.getMessage());
    	}
    }

    public void doReset() throws BusinessException, Exception {
	}

	public void doReload() throws BusinessException, Exception {
	}

	
	

}
