/**
 * 
 */
package br.com.orionsoft.monstrengo.view;


/**
 * Esta classe gerencia uma lista de objetos disponibilizando recursos
 * para obter a lista de valores com cada valor apontando para uma lista de propriedades
 * para possibilitar acessos hierárquicos como: list.item[0].value list.item[0].property.label; 
 * 
 * 
 * @author Lucio
 *
 */
public class EntityListItem {
    
    private Object value;
    private EntityPropertyInfo property;
    
    public EntityListItem(Object value, EntityPropertyInfo property)
    {
//        System.out.println("EntityListItem.constructor:");

        this.value = value;
        this.property = property;
    }

    public EntityPropertyInfo getProperty() {
//        System.out.println("EntityListItem.getProperty():");
        return property;
    }
    

    public Object getValue() {
//        System.out.println("EntityListItem.getValue():");
        return value;
    }
    
}
