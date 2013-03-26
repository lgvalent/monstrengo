package br.com.orionsoft.monstrengo.view.jsf;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Controla a identifica��o da entidade que est� sendo manipulada 
 * em uma requisi��o, geralmente dos beans Crud
 * 
 * Created on 14/04/2005
 * @author marcia
 * 
 * @jsf.bean name="entityBean" scope="session"
 */
public class EntityBean extends BeanSessionBasic
{
    public static final String URL_PARAM_ENTITY_TYPE = "entityType";
    public static final String URL_PARAM_ENTITY_ID = "entityId";

    private String typeName = null;
    private long id = IDAO.ENTITY_UNSAVED;
    
    private IEntity entity = null;

    private ParentBean parentBean;

    /**
     * Carrega os par�metros pertinente aos Bean da atual transa��o.   
     * Antes de recarregar os par�metros, o Bean sofre um reset() para 
     * que os par�metros atuais sejam limpos e dados processados sejam 
     * descarregados.
     */
    public void loadEntityParams() throws Exception
    {
		log.debug("EntityBean.loadParams");
        // Causa um reset para que os novos par�metros entrem em a��o
        this.reset();

        // Solicita que seu pai tamb�m prepare os par�metros
        this.parentBean.loadEntityParams();

        // Pega os par�metros da requisi��o
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_ENTITY_TYPE)))
        {
            this.typeName = super.getRequestParams().get(URL_PARAM_ENTITY_TYPE).toString();
        }
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_ENTITY_ID))) //((param.get(URL_PARAM_PARENT_ID)!=null) && (!param.get(URL_PARAM_PARENT_ID).equals("")) && (!param.get(URL_PARAM_PARENT_ID).equals("null")))
        {
            this.id = Long.parseLong(super.getRequestParams().get(URL_PARAM_ENTITY_ID).toString());
        }

        // Verifica se o pai preparou para pegar os par�metros do Pai
        if(this.parentBean.isHasParent())
        {
            // Atualiza os par�metros atuais, de acordo com a propriedade do pai
            // para manter a consist�ncia do tipo e id da entidade atual.
            // Quando a propriedade do pai for uma cole��o de objetos,
            // n�o ser� poss�vel pegar o Id, pois uma cole��o n�o possui 
            // um �nico Id. Por�m, o tipo da cole��o � poss�vel de ser pego.
            IProperty prop = this.parentBean.getEntity().getProperty(this.parentBean.getProperty()); 
            if (prop.getInfo().isCollection())
            {
                IEntityCollection entityCollection = prop.getValue().getAsEntityCollection(); 
            
                this.typeName = entityCollection.getInfo().getType().getName();
            }
            else
            {
                IEntity entity = prop.getValue().getAsEntity(); 
                
                this.typeName = entity.getInfo().getType().getName();
                this.id       = entity.getId();
            }
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
		log.debug("EntityBean.reset");
        // Limpa os par�metros
        this.typeName = null;
        this.id = IDAO.ENTITY_UNSAVED;
        
        // Limpa os dados processados
        this.doReload();
    }

    /**
     * Este m�todo anula a atual inst�ncia da entidade preparada.
     * E recarrega os par�metros. 
     * Assim, a entidade ser� novamente recarregada quando solicidada. 
     * @throws Exception 
     */
    public void doReload()
    {
		log.debug("EntityBean.reload");
        // Limpa os dados processados e re-le os parametros
		this.entity = null;
    }

    public IEntity getEntity() throws Exception
    {
		log.debug("EntityBean.getEntity");
        if (entity == null)
        {
            try
            {
                if(this.id != IDAO.ENTITY_UNSAVED){
                	entity = UtilsCrud.retrieve(this.getApplicationBean().getProcessManager().getServiceManager(),
                                            	Class.forName(this.typeName),
                                            	this.id, null);
                }
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

    /**
     * Refer�ncia para o bean ParentBean que manipula as propriedades do pai.
     * 
     * @jsf.managed-property value="#{parentBean}"
     */
    public ParentBean getParentBean(){return parentBean;}
    public void setParentBean(ParentBean parentBean){this.parentBean = parentBean;}

    public long getId(){return id;}
    public void setId(long entityId){this.id = entityId;}

    public String getTypeName(){return typeName;}
    public void setTypeName(String entityType){this.typeName = entityType;}

	public void doReset() throws BusinessException, Exception {
		// TODO Auto-generated method stub
		
	}
    
        
//////////////////////////////////////////////////////////////////////////////////////////////    
//
//    private EntityEdit entityEdit;
//    
//
//    public boolean isClassReady(){return entity != null;}
//    
//    
//    public EntityEdit entityEdit(){return entityEdit;}
//    public void setEntityEdit(EntityEdit entityEdit){this.entityEdit = entityEdit;}
//    
//    public void setInit(Map param) throws Exception
//    {
//        System.out.println("EntityBean.setInit: " + param);
//        
//        super.setInit(param);
//
//        String paramEntityClassName = null;
//        long paramEntityId = IDAO.ENTITY_UNSAVED;
//        
////        super.initSession();
//
//        if (FacesUtils.isNotNull(param.get(URL_PARAM_ENTITY_TYPE)))//!=null)
//        {
//            paramEntityClassName = param.get(URL_PARAM_ENTITY_TYPE).toString();
//        }
//        if (FacesUtils.isNotNull(param.get(URL_PARAM_ENTITY_ID))) //(param.get(URL_PARAM_ENTITY_ID)!=null) && (!param.get(URL_PARAM_ENTITY_ID).equals("")) && (!param.get(URL_PARAM_ENTITY_ID).equals("null")))
//        {
//            paramEntityId = Long.parseLong(param.get(URL_PARAM_ENTITY_ID).toString());
//        }
//        
//        // Verifica se houve algum parametro de Entidade para ser tratado
//        // Para lista pode ser fornecido apenas o tipo da classe, sem um id.
//        if ((paramEntityClassName != null))//&&(paramEntityId != IDAO.ENTITY_UNSAVED))
//        {
//
////            if (parentBean.isHasParent())
////                this.entity = new EntityData(this.appBean,paramEntityClassName,parentBean.getPropertyId());
////            else
////                this.entity = new EntityData(this.appBean,paramEntityClassName,paramEntityId);
////          this.entity.prepareForView();
//
//        
//            // Verifica se o Id da entidade foi fornecido
//            if (paramEntityId != IDAO.ENTITY_UNSAVED)
//            {
//                this.entity = new EntityData(super.appBean.getProcessManager(),paramEntityClassName,paramEntityId);
//            }
//            else
//            // se n�o foi tenta pegar da propriedade do Pai. 
//            if (parentBean.isHasParent())
//            {
//                
//                this.entity = new EntityData(super.appBean.getProcessManager(),paramEntityClassName,parentBean.getPropertyId());
//            }
//            // se n�o foi cria uma entidade sem Id. Somente com nome dos campos e suas propriedades (hint, label, etc)
//            else
//            {
//                this.entity = new EntityData(super.appBean.getProcessManager(),paramEntityClassName,IDAO.ENTITY_UNSAVED);
////                throw new Exception("URL inv�lida. N�o foi poss�vel identificar a classe entidade e seu Id ou utilizar uma classe Pai para isto. Aceito: (entity, id) ou (entity, parent, parentId, property) ou (entity, id, parent, parentId)");
//            }
//            
//        }
////        else{
////            this.entity = new EntityData(this.appBean);
////            entity.prepareEntity(paramEntityClassName, paramEntityId);
////        }
//    }
//    
//    public String remove() throws Exception
//    {
//        System.out.println("removendo...");
//        
//        if (this.entity.remove())
//        {
//            FacesUtils.addMsg("Entidade removida com sucesso."); 
//            return PageFlow.SUCCESS;
//        }
//        
//        FacesUtils.addMsg("N�o foi poss�vel excluir a entidade."); 
//        return PageFlow.FAILURE;
//    }
//
//    public String edit() throws Exception
//    {
//        System.out.println("editando...");
//
//        if (this.isClassReady())
//        {
//            System.out.println("EntityBean.edit entityBean.isClassReady():" + this.isClassReady());
//            this.entityEdit.editEntity(this.entity);
//            return PageFlow.EDIT;
//        }
//        FacesUtils.addMsg("N�o foi poss�vel modificar a entidade."); 
//        return PageFlow.FAILURE;
//    }
//
//    public String createNew() throws Exception
//    {
//        System.out.println("EntityBean.createNew");
//
//        if (this.isClassReady())
//        {
//            System.out.println("UpdateBean.setInit entityBean.isClassReady():" + this.isClassReady());
//            this.entityEdit.editEntity(this.entity);
//            return PageFlow.EDIT;
//        }
//        FacesUtils.addMsg("N�o foi poss�vel modificar a entidade."); 
//        return PageFlow.FAILURE;
//    }
//
}
