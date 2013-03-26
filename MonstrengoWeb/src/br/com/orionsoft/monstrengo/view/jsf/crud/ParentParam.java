package br.com.orionsoft.monstrengo.view.jsf.crud;

import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.view.jsf.bean.IBasicBean;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Esta classe � respons�vel pela manipula��o dos par�metros aceitos
 * pelas requisi��es que definem uma entidade pai.
 * 
 * @author Lucio 20060206
 */
public class ParentParam
{
    private Logger log = Logger.getLogger(this.getClass());
	public static final String URL_PARAM_PARENT_TYPE = "parentType";
    public static final String URL_PARAM_PARENT_ID = "parentId";
    public static final String URL_PARAM_PARENT_PROPERTY = "parentProperty";
    
    /** Par�metros get/set */
    private String typeName = "";
    private long id = IDAO.ENTITY_UNSAVED;
    private String property= "";
    
    /** Objetos preparados */
    private IEntity entity = null;
    
    /** Define o objeto Bean que � pai deste objeto 
     * para utilizar as rotinas de comunica��o com
     * a sess�o e requisi��o correntes. 
     */
    private IBasicBean ownerBean;
    
    
    /**
     * Construtor com a obriga��o de fornecer um owner para
     * ser utilizado internamente pela classe.
     * @param ownerBean Define o bean dono desta classe
     */
    public ParentParam(IBasicBean ownerBean){
    	this.ownerBean = ownerBean;
    }
    
    public IBasicBean getOwnerBean(){return ownerBean;}
	public void setOwnerBean(IBasicBean ownerBean){this.ownerBean = ownerBean;}

	public long getId(){return id;}
    public void setId(long id){this.id = id;}
    
    public String getTypeName(){return typeName;}
    public void setTypeName(String typeName){this.typeName = typeName;}
    
    public String getProperty(){return property;}
    public void setProperty(String property){this.property = property;}
    
    public boolean isHasParent()
    {
        return (typeName != null) && (id!=IDAO.ENTITY_UNSAVED) && (property!=null);
    }
    
    /**
     * Carrega os par�metros pertinente aos Bean da atual transa��o.   
     * Antes de recarregar os par�metros, o Bean sofre um reset() para 
     * que os par�metros atuais sejam limpos e dados processados sejam 
     * descarregados.
     */
    public void loadParams()
    {
    	log.debug("Lendo os par�metros da requisi��o");
        // Causa um reset para que os novos par�metros entrem em a��o
        this.reset();

        if (FacesUtils.isNotNull(ownerBean.getRequestParams().get(URL_PARAM_PARENT_TYPE)))
        {
            this.typeName = ownerBean.getRequestParams().get(URL_PARAM_PARENT_TYPE).toString();
        }
        if (FacesUtils.isNotNull(ownerBean.getRequestParams().get(URL_PARAM_PARENT_ID))) //((param.get(URL_PARAM_PARENT_ID)!=null) && (!param.get(URL_PARAM_PARENT_ID).equals("")) && (!param.get(URL_PARAM_PARENT_ID).equals("null")))
        {
            this.id = Long.parseLong(ownerBean.getRequestParams().get(URL_PARAM_PARENT_ID).toString());
        }
        if (FacesUtils.isNotNull(ownerBean.getRequestParams().get(URL_PARAM_PARENT_PROPERTY)))//!=null)
        {
            this.property = ownerBean.getRequestParams().get(URL_PARAM_PARENT_PROPERTY).toString();
        }
        
    }

    /**
     * Limpa todos os par�metros anteriormente carregados,
     * voltando seu valor padr�o.
     * Os dados processados internos tamb�m s�o marcados para
     * Se ocorrer alguma mudan�a nos par�metros,
     * o controlador da View dever� se resetar.
     * Para isto, os objetos preparados dever�o
     * ser destru�dos 
     */
    private void reset()
    {
    	log.debug("Resetando os par�metros atuais");
        // Limpa os par�metros
        this.typeName = "";
        this.id = IDAO.ENTITY_UNSAVED;
        this.property = "";
        entity = null;
        
    }
    
    public IEntity getEntity() throws Exception
    {
        if (entity == null)
        {
            try
            {
                entity = UtilsCrud.retrieve(this.ownerBean.getApplicationBean().getProcessManager().getServiceManager(),
                                            Class.forName(this.typeName),
                                            this.id, null);
            } catch (BusinessException e)
            {
                FacesUtils.addErrorMsgs(e.getErrorList());
                
                throw e;
            } catch (ClassNotFoundException e)
            {
                throw e;
            } 
        }
        
        return entity;
    }
    
}
