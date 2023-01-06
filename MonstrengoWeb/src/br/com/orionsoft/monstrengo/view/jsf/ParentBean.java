package br.com.orionsoft.monstrengo.view.jsf;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Este bean controla os parâmetros de entidade pai para identificar
 * a classe o id pai e a propriedade que será exibida.
 * Ele analisa a atual requisição e busca parâmetros de URL_PARAM_PARENT_*
 * para trata-los
 * 
 * @author marcia 2005/11/16
 * 
 * @jsf.bean name="parentBean" scope="session"
 */
public class ParentBean extends BeanSessionBasic
{
    public static final String URL_PARAM_PARENT_TYPE = "parentType";
    public static final String URL_PARAM_PARENT_ID = "parentId";
    public static final String URL_PARAM_PARENT_PROPERTY = "parentProperty";
 
    /** Parâmetros get/set */
    private String typeName = "";
    private long id = -1;
    private String property= "";
    
    /** Objetos preparados */
    private IEntity entity = null;
    
    public long getId(){return id;}
    public void setId(long id){this.id = id;}
    
    public String getTypeName(){return typeName;}
    public void setTypeName(String typeName){this.typeName = typeName;}
    
    public String getProperty(){return property;}
    public void setProperty(String property){this.property = property;}
    
    public boolean isHasParent()
    {
        return (typeName != null) && (id!=-1) && (property!=null);
    }
    
    /**
     * Carrega os parâmetros pertinente aos Bean da atual transação.   
     * Antes de recarregar os parâmetros, o Bean sofre um reset() para 
     * que os parâmetros atuais sejam limpos e dados processados sejam 
     * descarregados.
     */
    public void loadEntityParams()
    {
        // Causa um reset para que os novos parâmetros entrem em ação
        this.reset();

        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_PARENT_TYPE)))
        {
            this.typeName = super.getRequestParams().get(URL_PARAM_PARENT_TYPE).toString();
        }
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_PARENT_ID))) //((param.get(URL_PARAM_PARENT_ID)!=null) && (!param.get(URL_PARAM_PARENT_ID).equals("")) && (!param.get(URL_PARAM_PARENT_ID).equals("null")))
        {
            this.id = Long.parseLong(super.getRequestParams().get(URL_PARAM_PARENT_ID).toString());
        }
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_PARENT_PROPERTY)))//!=null)
        {
            this.property = super.getRequestParams().get(URL_PARAM_PARENT_PROPERTY).toString();
        }
        
    }

    /**
     * Este método anula a atual instância da entidade preparada. 
     * Assim, os dados internos do bean serão novamente 
     * recarregada quando solicidados. 
     */
    public void doReload()
    {
    	this.entity = null;
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
        // Limpa os parâmetros
        this.typeName = "";
        this.id = -1;
        this.property = "";
        
        // Limpa os dados processados
        this.doReload();
    }
    
    public IEntity getEntity() throws Exception
    {
        if (entity == null)
        {
            try
            {
                entity = UtilsCrud.retrieve(this.getApplicationBean().getProcessManager().getServiceManager(),
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
//    public EntityData getEntity(){return entity;}
//    
//    public boolean isHasParent(){return entity!=null;}
//    
//    public long getPropertyId() throws Exception
//    {
//        Object parentData = entity.getData();
//        Object property = PropertyUtils.getProperty(parentData, propertyName);
//        // Pega a propriedade 'id' da subClasse para saber a qual objeto se refere o campo 
//        if ((property != null) && !(property instanceof Collection))
//        {
//            // Pega a propriedade 'id' da subClasse para saber a qual objeto se refere o campo
//            return (Long) PropertyUtils.getProperty(property, IDAO.PROPERTY_ID_NAME);
//        }
//        
//        return IDAO.ENTITY_UNSAVED;  
//    }
//
//    public void setInit(Map param)throws Exception
//    {
//        System.out.println("ParentBean.setInit: " + param);
//        
//        super.setInit(param);
//
//        // Variáveis utilizadas internamente para decompor os parâmetros
//        String paramParentClassName=null;
//        long paramParentId = IDAO.ENTITY_UNSAVED;
//
//        if (FacesUtils.isNotNull(param.get(URL_PARAM_PARENT_TYPE)))//!=null)
//        {
//            paramParentClassName = param.get(URL_PARAM_PARENT_TYPE).toString();
//        }
//        if (FacesUtils.isNotNull(param.get(URL_PARAM_PARENT_ID))) //((param.get(URL_PARAM_PARENT_ID)!=null) && (!param.get(URL_PARAM_PARENT_ID).equals("")) && (!param.get(URL_PARAM_PARENT_ID).equals("null")))
//        {
//            paramParentId = Long.parseLong(param.get(URL_PARAM_PARENT_ID).toString());
//        }
//        if (FacesUtils.isNotNull(param.get(URL_PARAM_PARENT_PROPERTY)))//!=null)
//        {
//            propertyName = param.get(URL_PARAM_PARENT_PROPERTY).toString();
//        }
//
//        System.out.println("ParentBean.paramParentClassName: " + paramParentClassName);
//        if ((paramParentId != IDAO.ENTITY_UNSAVED) && (FacesUtils.isNotNull(paramParentClassName)// != null) 
//                /*&& (!paramParentClassName.equals("")*/) && (FacesUtils.isNotNull(propertyName)/* != null*/) 
//                /*&& (!propertyName.equals(""))*/)
//        {
//            entity = new EntityData(this.appBean.getProcessManager(),paramParentClassName, paramParentId);
//        }
//    }
	public void doReset() throws BusinessException, Exception {
		// TODO Auto-generated method stub
		
	}
    
}
