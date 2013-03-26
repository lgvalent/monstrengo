package br.com.orionsoft.monstrengo.view;

import java.lang.reflect.Modifier;

import org.apache.commons.beanutils.PropertyUtils;

import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * 
 * Created on 14/04/2005
 * @author Marcia
 * @version
 */
public class EntityData {
    private IServiceManager app;

    // Dados sobre a entidade corrente da lista
    protected Class entityClass;

    protected long id = -1;

    protected Object data;

    protected EntityProperties properties;

    // /////////////////////////////////////////////////////////////////////////////////////////////////
    // Construtores
    // /////////////////////////////////////////////////////////////////////////////////////////////////
    public EntityData(IServiceManager app, Object objectData) throws Exception {
//        System.out.println("EntityData.constructor: "
//                + objectData.getClass().getName() + " data: " + objectData);

        // Constroi a classe passando a Aplicação correntemente instanciada
        // Este construtor deve ser utilizado quando a instância não for
        // executada
        // por ferramentas que permitem injeção de parâmetros. No caso de
        // instanciação
        // manual, os parâmetros aqui declarados são essenciais para o
        // funcionamento da classe.
        this.app = app;

        // Armazena os dados da entidade
        this.entityClass = objectData.getClass();
        this.id = (Long) PropertyUtils.getProperty(objectData,
                IDAO.PROPERTY_ID_NAME);

        // 1º - Obtem os DADOS da entidade solicitada
        data = objectData;

        // 2º - Cria as propriedades da Entidade já com o Value preenchido
        properties = new EntityProperties(this.app, this);

    }

    public EntityData(IServiceManager app, String entityClassName, long entityId)
            throws Exception {
//        System.out.println("EntityData.constructor: " + entityClassName
//                + " id: " + entityId);

        // Constroi a classe passando a Aplicação correntemente instanciada
        // Este construtor deve ser utilizado quando a instância não for
        // executada
        // por ferramentas que permitem injeção de parâmetros. No caso de
        // instanciação
        // manual, os parâmetros aqui declarados são essenciais para o
        // funcionamento da classe.
        this.app = app;

        // Armazena os dados da entidade
        this.entityClass = Class.forName(entityClassName);
        this.id = entityId;

        // 1º - Obtem os DADOS da entidade solicitada
//        ICrudService service = (ICrudService) app
//                .getServiceByName(ICrudService.SERVICE_NAME);
        if (entityId != IDAO.ENTITY_UNSAVED) {
            data = UtilsCrud.retrieve(this.app, this.entityClass, entityId, null);
        } else {
            // Dá new somente para classes que podem ser Instanciadas
            if (!this.isAbstract())
                data = UtilsCrud.create(this.app, this.entityClass, null);
        }

        // 2º - Cria as propriedades da Entidade já com o Value preenchido
        properties = new EntityProperties(this.app, this);

    }

    /**
     * Obtém os dados da entidade fornecida
     * 
     * @param entityClassName
     *            entidade a ser preparada
     * @param id
     *            id da entidade, caso exista.
     * @return
     * @throws Exception
     */
    // /////////////////////////////////////////////////////////////////////////////////////////////////
    // Rotinas do Filho (EntityData)
    // /////////////////////////////////////////////////////////////////////////////////////////////////
    public String getClassName() {
        if (entityClass == null) {
            return "";
        }
        return entityClass.getName();
    }

    public String getLabel() {
        return new EntityInfo(this.entityClass).getLabel(this.entityClass
                .getSimpleName());
    }

    // public void setEntityId(long id){this.entityId = id;}
    public long getId() {
        return id;
    }

    public EntityProperties getProperties() {
        return properties;
    }

    // public String[] getEntityDataStr() throws Exception
    // {
    // return EntityUtils.convertObjToDataStr(data, properties);
    // }

    public Object getData() {
        return data;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public boolean remove() throws Exception
    {
        // Cria uma nova entidade, somente se a atual não existir
 //       try {
            if (id != IDAO.ENTITY_UNSAVED) {
//                ICrudService service = (ICrudService) this.app
//                        .getServiceByName(ICrudService.SERVICE_NAME);
                UtilsCrud.delete(this.app, this.app.getEntityManager().getEntity(this.data), null);
                return true;
            }
//        } catch (Exception e) {
            throw new Exception("Entidade não definida não pode ser excluida!");
//        }

    }

    public boolean isAbstract() {
        return Modifier.isAbstract(this.entityClass.getModifiers());
    }

}
