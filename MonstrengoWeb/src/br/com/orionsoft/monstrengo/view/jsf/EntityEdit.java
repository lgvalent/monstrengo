package br.com.orionsoft.monstrengo.view.jsf;

import java.util.HashMap;
import java.util.Map;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.processes.UpdateProcess;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Esta classe manipula as entidades que estão correntemente sendo editadas.
 * A classe mantém uma pilha de entidades que estão sendo editadas na sessão 
 * corrente. 
 * Após um save, as entidades são persistidas e são excluidas da lista. O escopo
 * da instância desta entidade é de Sessão. Para esta classe receber os
 * parâmetros de preparação é utilizado na página JSP tags: <h:commandLink
 * action="entityEdit.prepareEntity"> <f:param name="itemId"
 * value="#{cartItem.item.itemId}"/> </h:commandLink>
 * 
 * Created on 05/04/2005
 * @author Lucio
 */
public class EntityEdit extends BeanSessionBasic
{
	private static final long serialVersionUID = 1L;
	//entities: guarda as entidades em edição na sessão corrente.
    /** Armazena as entidades que estão atualmente em edição */
    private Map<String, IEntity<?>> entities = new HashMap<String, IEntity<?>>();
    /** Armazena os processos das entidades que estão atualmente em edição */
    private Map<String, UpdateProcess<?>> processes = new HashMap<String, UpdateProcess<?>>();
    
    private IEntity<?> currentEntity;
    private String currentEntityKey;
    
    /**
     * TODO DOCUMENTAR esse metodo.
     * @throws BusinessException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void doEdit() throws BusinessException, ClassNotFoundException
    {
//        System.out.println("EntityEdit.doEdit: ");

        // Obtem os parâmetros
        String entityType = FacesUtils.getRequestParam(EntityBean.URL_PARAM_ENTITY_TYPE);
        long entityId = Long.parseLong(FacesUtils.getRequestParam(EntityBean.URL_PARAM_ENTITY_ID));

        // Constrói a chave da entidades
        this.currentEntityKey = entityType + entityId; 
        
        // Verifica se a entidade já se encontra na lista interna 
//        System.out.println("EntityEdit.editEntity.currentClassKey: " + currentEntityKey);
        this.currentEntity = entities.get(this.currentEntityKey);
        
        // Se não estiver, coloca atual instancia na lista
        if (this.currentEntity == null)
        {
            // de onde ele vai pegar o userSession??
            UserSession userSession = null;
            // Cria um novo processo de edição para a entidade
            UpdateProcess editProcess = (UpdateProcess<?>)this.getApplicationBean().getProcessManager().createProcessByName(UpdateProcess.PROCESS_NAME, userSession);
//          não vai dar certo porque não vai ter o nome do pacote na variável, tipo br.com...
            editProcess.setEntityType(Class.forName(entityType)); 
            editProcess.setEntityId(entityId);
            currentEntity = editProcess.retrieveEntity();
            entities.put(this.currentEntityKey, currentEntity);
            processes.put(this.currentEntityKey, editProcess);
            
//            // preparar a entidade para edição antes de colocar na LISTA
//            this.currentEntity = entity;
//
//           //this.currentEntity.getProperties().prepareForEdit();
//            entities.put(this.currentEntityKey, entity);
        }
        
        
//        System.out.println("////EntityEdit.editEntity.currentClassKey:" + this.currentEntity.getId());
    }
    
//    public void editEntity(EntityData entity)
//    {
//        System.out.println("EntityEdit.editEntity: " + entity);
//
//        // Verifica se a entidade já se encontra na lista interna 
//        this.currentEntityKey = entity.getClassName() + entity.getId(); 
//        System.out.println("EntityEdit.editEntity.currentClassKey: " + currentEntityKey);
//        this.currentEntity = entities.get(this.currentEntityKey);
//        
//        // Se não estiver, coloca atual instancia na lista
//        if (this.currentEntity == null)
//        {
//            // preparar a entidade para edição antes de colocar na LISTA
//            this.currentEntity = entity;
//
//           //this.currentEntity.getProperties().prepareForEdit();
//            entities.put(this.currentEntityKey, entity);
//        }
//        System.out.println("////EntityEdit.editEntity.currentClassKey:" + this.currentEntity.getId());
//    }
    
    public String getCurrentEntityKey()
    {
//        System.out.println("EntityEdit.getCurrentEntityKey:" + this.currentEntityKey); 
        return currentEntityKey;
    }
    
    public void setCurrentEntityKey(String currentEntityKey)
    {
//        System.out.println("EntityEdit.setCurrentEntityKey: " + currentEntityKey);
        // Define a entidade corrente para a edição
        this.currentEntityKey = currentEntityKey;

        // Localiza no mapa a entidade corrente
        currentEntity = entities.get(currentEntityKey);
    }
    
    public IEntity<?> getCurrentEntity()
    {
//        System.out.println("EntityEdit.getCurrentEntity " + currentEntity.getInfo().getName());
        return currentEntity;
    }

//    public void setInit(String param) throws Exception
//    {
//        System.out.println("EntityEdit.setInit: " + param);
//        super.setInit(param);
//    }

//    //--------------------------------------------------------------------------------------    
//    //                        GETTERS E SETTERS    
//    //--------------------------------------------------------------------------------------
//    public String prepareEntity() throws Exception
//    {
//        /*
//         * Pegar os parâmetros passados Estes parâmetros são pegos do mapa de
//         * parâmetros que o Faces monta por URL ou commandLinks com parâmetros
//         */
//        System.out.println("EntityEdit.prepareEntity: ");
////        Object obj;
//////        obj = FacesContext.getCurrentInstance()
//////             .getExternalContext().getRequestParameterMap()
//////             .get(EntityUtils.URL_PARAM_ENTITY_CLASS);
////        if (obj != null)
////            this.paramEntityClassName = (String) obj;
////        obj = FacesContext
////                .getCurrentInstance().getExternalContext()
////                .getRequestParameterMap().get(EntityUtils.URL_PARAM_ENTITY_ID);
////        if (obj != null)
////            this.paramEntityId = Long.parseLong((String) obj);
//         
////        obj = FacesContext.getCurrentInstance()
////             .getExternalContext().getRequestParameterMap()
////             .get(EntityUtils.URL_PARAM_PARENT_CLASS);
////        if (obj != null)
////            this.paramParentClassName = (String) obj;
//
////        obj = FacesContext
////                .getCurrentInstance().getExternalContext()
////                .getRequestParameterMap().get(EntityUtils.URL_PARAM_PARENT_ID);
////        if (obj != null)
////            this.paramParentId = Long.parseLong((String) obj);
////        obj = FacesContext.getCurrentInstance()
////              .getExternalContext().getRequestParameterMap()
////              .get(EntityUtils.URL_PARAM_PARENT_PROPERTY);
////        if (obj != null)
////            this.paramParentProperty = (String) obj;
//
//
//   // Procurar no Map se esta classe já está na lista de edição
////        entityBean = entities.get(this.paramEntityClassName);
////        currentEntity entities.get(entityBean.getEntity().getClassName());
//        // Se não achar, cria-se uma nova EntityBean e coloca no Map
////        if (entity == null)
//        {
//            // Cria a nova entidade
////            entityBean = new EntityBean(this.appBean);
////            entity = new EntityBean();
////            if ((this.paramParentId != IDAO.ENTITY_UNSAVED)
////                 && (this.paramParentClassName != null) && (!paramParentClassName.equals("")))
////                entityBean.prepareParent(this.paramParentClassName, this.paramParentId, this.paramParentProperty);
////            if ((this.parent.getEntity().getId() != IDAO.ENTITY_UNSAVED)
////                    && (this.parent.getEntity().getClassName() != null) && (!this.parent.getEntity().getClassName().equals("")))
////                   entityBean.prepareParent(this.paramParentClassName, this.paramParentId, this.paramParentProperty);
////            if ((this.paramEntityId != IDAO.ENTITY_UNSAVED)
////                 && (this.paramEntityClassName != null))
////                entityBean.prepareEntity(this.paramEntityClassName, this.paramEntityId);
//            // Coloca a nova entidade na lista
////            Object o = entities.put(this.paramEntityClassName, this.entity);
//
//            Object o = entities.put(currentEntity.getClassName(), currentEntity);
//        }
//        return "SUCCESS";
//    }
//
//    public boolean getPrepared()
//    {
//        System.out.println("EntityEdit.getPrepared");
//        try
//        {
//            this.prepareEntity();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        return true;
//    }

//    public String removeEntity(EntityData entityData)
//    {
//        System.out.println("EntityEdit.removeEntity: " + entityData.getClassName() + "Id: " + entityData.getId());
//        try{
//            ICrudService service = (ICrudService) this.appBean.getApp().getServiceByName(ICrudService.SERVICE_NAME);
//            service.delete(entityData.getData());
//            if (entities.containsKey(entityData.getClassName() + entityData.getId()))
//            {
//                System.out.println("EntityEdit.removeEntity.if entities.contains ...");
//                entities.remove(entityData.getClassName() + entityData.getId());
//            }
//        return "success";
//        }
//        catch(Exception e){
//            return "failure";
//        }
//    }
    
    public String saveEntity() throws Exception
    {
//        System.out.println("========>>>>>>>EntityEdit.saveEntity: " + currentEntity.getInfo().getName());
//      result será false se a operação lançar uma exceção.
//        boolean result = processes.get(this.currentEntityKey).runUpdate();
        
//        currentEntity.getProperties().save();
        
//        super.finishSession();        
//        super.initSession();
        
//        // Obs.: Existe uma sessão 'mantida', assim o proxies são todos válidos
//        // para
//        // acessar as subColeções e subClasses da classe: Object
//        // entityBean.entity.data.
//        // Popular a classe (Nova ou do Retrieve)
//        Object object = currentEntity.getData();
//        Object propertyValue;
//        String property;
//        for (int i = 0; i <= currentEntity.getFieldsCount(); i++)
//        {
//            property = currentEntity.getProperties()[i].getName();
//            System.out.print(i + ": property: " + property);
//            propertyValue = currentEntity.getProperties()[i].getValue();
//            System.out.println(", propertyValue: " + propertyValue);
////            if ((!entityBean.getEntity().getProperties()[i].isSubClass())
////                    && (!entityBean.getEntity().getProperties()[i].isList()))
////            {
////                propertyValue = entityBean.getEntity().getProperties()[i]
////                        .getValue();
////
////            } else
////            {
//                if (currentEntity.getProperties()[i].isSubClass())
//                {
//                    long id = currentEntity.getProperties()[i]
//                            .getId();
//                    if (id != IDAO.ENTITY_UNSAVED)
//                    {
//                        IDAO dao = this.appBean.getApp().getDaoByEntity(
//                                currentEntity.getProperties()[i]
//                                        .getType());
//                        propertyValue = dao.retrieve(id);
//                    }
//                }
//                if (currentEntity.getProperties()[i].isList())
//                {
//                    Collection col = (Collection) PropertyUtils.getProperty(object,property);
//                }
//                PropertyUtils.setProperty(object, property, propertyValue);
////            }
////            PropertyUtils.setProperty(object, property, propertyValue);
//
//        }
//        IDAO dao = this.appBean.getApp().getDaoByEntity(
//                currentEntity.getEntityClass());
//        dao.update(object);
//        // se for do tipo primitivo: com os dados
//        // entityBean.entity.properties[n].value
//        // entityBean.entity.data('nome propriedade') = {novo valor}
//        // se for do tipo subClass:
//        // Verifica se o properties[n].id é !=-1, o que indica que foi alterado
//        // então
//        // obtem (retrive) o objeto do banco e executa um
//        // setProperty(retrivedObj).
//        // Obs.: Utilizar as rotinas de BeansUtils e PropertiesUtils
//        // Executar alguma validação de negócio (DEPOIS IMPLEMENTA ISTO)
//        // Persiste a entidade
//        // Remove a entidade da lista de classes em edição
        return "";
//        return "success";
    }

	public void loadEntityParams() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void doReset() throws BusinessException, Exception {
		// TODO Auto-generated method stub
		
	}

	public void doReload() throws BusinessException, Exception {
		// TODO Auto-generated method stub
		
	}
}
