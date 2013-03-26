package br.com.orionsoft.monstrengo.view.jsf.crud;

import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.view.jsf.bean.IBasicBean;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Esta classe é responsável pela manipulação dos parâmetros aceitos
 * pelas requisições que definem uma entidade pai.
 * 
 * @author Lucio 20060206
 */
public class ParentParam
{
    private Logger log = Logger.getLogger(this.getClass());
	public static final String URL_PARAM_PARENT_TYPE = "parentType";
    public static final String URL_PARAM_PARENT_ID = "parentId";
    public static final String URL_PARAM_PARENT_PROPERTY = "parentProperty";
    
    /** Parâmetros get/set */
    private String typeName = "";
    private long id = IDAO.ENTITY_UNSAVED;
    private String property= "";
    
    /** Objetos preparados */
    private IEntity entity = null;
    
    /** Define o objeto Bean que é pai deste objeto 
     * para utilizar as rotinas de comunicação com
     * a sessão e requisição correntes. 
     */
    private IBasicBean ownerBean;
    
    
    /**
     * Construtor com a obrigação de fornecer um owner para
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
     * Carrega os parâmetros pertinente aos Bean da atual transação.   
     * Antes de recarregar os parâmetros, o Bean sofre um reset() para 
     * que os parâmetros atuais sejam limpos e dados processados sejam 
     * descarregados.
     */
    public void loadParams()
    {
    	log.debug("Lendo os parâmetros da requisição");
        // Causa um reset para que os novos parâmetros entrem em ação
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
     * Limpa todos os parâmetros anteriormente carregados,
     * voltando seu valor padrão.
     * Os dados processados internos também são marcados para
     * Se ocorrer alguma mudança nos parâmetros,
     * o controlador da View deverá se resetar.
     * Para isto, os objetos preparados deverão
     * ser destruídos 
     */
    private void reset()
    {
    	log.debug("Resetando os parâmetros atuais");
        // Limpa os parâmetros
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
