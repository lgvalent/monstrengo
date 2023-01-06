package br.com.orionsoft.monstrengo.crud.entity;

import java.util.Set;

import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;

public interface IEntitySet<T> extends IEntityCollection<T>, Set<IEntity<T>>
{
    /**
     * Cria uma lista de Objetos baseada nos objetos que se encontram
     * encapsulados dentro de IEntity.
     * <p>Útil principalmente na camada de persistência. Onde o 
     * importante é o objeto e não a entidade. 
     * 
     * @see IEntity#getObject()
     * 
     * @return
     */
    public abstract Set<T> getObjectSet();
}
