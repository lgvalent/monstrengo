package br.com.orionsoft.monstrengo.crud.entity;

import java.util.Set;

import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;

public interface IEntitySet<T> extends IEntityCollection<T>, Set<IEntity<T>>
{
    /**
     * Cria uma lista de Objetos baseada nos objetos que se encontram
     * encapsulados dentro de IEntity.
     * <p>�til principalmente na camada de persist�ncia. Onde o 
     * importante � o objeto e n�o a entidade. 
     * 
     * @see IEntity#getObject()
     * 
     * @return
     */
    public abstract Set<T> getObjectSet();
}
