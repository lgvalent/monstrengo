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
 * Controla a identificação da entidade que está sendo manipulada 
 * em uma requisição, geralmente dos beans Crud
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
     * Carrega os parâmetros pertinente aos Bean da atual transação.   
     * Antes de recarregar os parâmetros, o Bean sofre um reset() para 
     * que os parâmetros atuais sejam limpos e dados processados sejam 
     * descarregados.
     */
    public void loadEntityParams() throws Exception
    {
		log.debug("EntityBean.loadParams");
        // Causa um reset para que os novos parâmetros entrem em ação
        this.reset();

        // Solicita que seu pai também prepare os parâmetros
        this.parentBean.loadEntityParams();

        // Pega os parâmetros da requisição
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_ENTITY_TYPE)))
        {
            this.typeName = super.getRequestParams().get(URL_PARAM_ENTITY_TYPE).toString();
        }
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_ENTITY_ID))) //((param.get(URL_PARAM_PARENT_ID)!=null) && (!param.get(URL_PARAM_PARENT_ID).equals("")) && (!param.get(URL_PARAM_PARENT_ID).equals("null")))
        {
            this.id = Long.parseLong(super.getRequestParams().get(URL_PARAM_ENTITY_ID).toString());
        }

        // Verifica se o pai preparou para pegar os parâmetros do Pai
        if(this.parentBean.isHasParent())
        {
            // Atualiza os parâmetros atuais, de acordo com a propriedade do pai
            // para manter a consistência do tipo e id da entidade atual.
            // Quando a propriedade do pai for uma coleção de objetos,
            // não será possível pegar o Id, pois uma coleção não possui 
            // um único Id. Porém, o tipo da coleção é possível de ser pego.
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
		log.debug("EntityBean.reset");
        // Limpa os parâmetros
        this.typeName = null;
        this.id = IDAO.ENTITY_UNSAVED;
        
        // Limpa os dados processados
        this.doReload();
    }

    /**
     * Este método anula a atual instância da entidade preparada.
     * E recarrega os parâmetros. 
     * Assim, a entidade será novamente recarregada quando solicidada. 
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
     * Referência para o bean ParentBean que manipula as propriedades do pai.
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
//            // se não foi tenta pegar da propriedade do Pai. 
//            if (parentBean.isHasParent())
//            {
//                
//                this.entity = new EntityData(super.appBean.getProcessManager(),paramEntityClassName,parentBean.getPropertyId());
//            }
//            // se não foi cria uma entidade sem Id. Somente com nome dos campos e suas propriedades (hint, label, etc)
//            else
//            {
//                this.entity = new EntityData(super.appBean.getProcessManager(),paramEntityClassName,IDAO.ENTITY_UNSAVED);
////                throw new Exception("URL inválida. Não foi possível identificar a classe entidade e seu Id ou utilizar uma classe Pai para isto. Aceito: (entity, id) ou (entity, parent, parentId, property) ou (entity, id, parent, parentId)");
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
//        FacesUtils.addMsg("Não foi possível excluir a entidade."); 
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
//        FacesUtils.addMsg("Não foi possível modificar a entidade."); 
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
//        FacesUtils.addMsg("Não foi possível modificar a entidade."); 
//        return PageFlow.FAILURE;
//    }
//
}
