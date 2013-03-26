/**
 * 
 */
package br.com.orionsoft.monstrengo.view;

import java.util.Collection;

import br.com.orionsoft.monstrengo.core.service.ServiceManager;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Esta classe mantem uma lista de items que podem ser acessados obtendo seus
 * valores e suas propriedades. Os items são apresentandos na ordem que é definida
 * pelas propriedades.
 * items[0].value; items[0].property.label.
 *
 */
public class EntityList {
    
    ServiceManager app;
    
    private EntityData[] entities;
    private EntityProperties properties;
    
    public EntityList(ServiceManager app, Collection values, Class entityClass) throws Exception
    {
//        System.out.println("EntityList.constructor:" + entityClass.getName());

        this.app = app;
        
        // Para otimização os Arrays são pegos agora e somente UMA VEZ
        Object[] valuesArray = values.toArray();
        entities = new EntityData[valuesArray.length];

        // Percorre todos os valores criando as entityData com Propriedades VALORADAS
        for(int i = 0; i < valuesArray.length; i++)
        {
            entities[i] = new EntityData(app, valuesArray[i]); 
        }
        
        // Cria uma lista de propriedades com um object NULLO (-1)
        properties = new EntityProperties(app, new EntityData(app, entityClass.getName(), IDAO.ENTITY_UNSAVED));
    }

    public EntityData[] getEntities() {
        return entities;
    }

    public int getCount()
    {
       return entities.length;   
    }

    public EntityProperties getProperties() {
        return properties;
    }

    public ServiceManager getApp() {
        return app;
    }
    
    
    
}
