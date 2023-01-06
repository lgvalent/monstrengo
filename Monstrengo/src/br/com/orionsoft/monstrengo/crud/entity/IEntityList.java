package br.com.orionsoft.monstrengo.crud.entity;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;

public interface IEntityList<T> extends IEntityCollection<T>{

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
	public abstract List<Long> getIdList() throws EntityException;
	
	public abstract List<T> getObjectList();
	
	/**
	 * Este método retorna uma lista que implementa a interface java.util.List.<br>
	 * Foi necessário porque a IEntityList não implementa mais esta interface para que o JSF
	 * não tente manupular ela como uma lista e perca os recursos extras adicionados como
	 * get/setRunId().
	 * 
	 * @author lucio
	 * @since 20070502
	 */
	public abstract List<IEntity<T>> getList();

	public abstract IEntity<T> get(int index);

	/**
	 * adiciona todos os elementos de uma coleção a partir de uma determinada posição
	 * @param index índice onde o primeiro elemento será inserido
	 * @param c coleção de elementos à ser inserido
	 * @return
	 */
	public abstract boolean addAll(int index, Collection<? extends IEntity<T>> c);

	/**
	 * Substitui o elemento contido na posição especificada da lista pela 
	 * entidade fornecida
	 * @param index posição do elemento a ser substituido
	 * @param element elemento que substituirá o valor original.
	 * @return
	 */
	public abstract IEntity<T>set(int index, IEntity<T>element);

	/**
	 * Adiciona o elemento fornecido na posição especificada
	 * @param index posição a ser inserido
	 * @param element elemento a ser inserido
	 */
	public abstract void add(int index, IEntity<T>element);

	public abstract IEntity<T>remove(int index);

	public abstract int indexOf(Object o);

	public abstract int lastIndexOf(Object o);

	public abstract ListIterator<IEntity<T>> listIterator();

	public abstract ListIterator<IEntity<T>> listIterator(int index);

	public abstract List<IEntity<T>> subList(int fromIndex, int toIndex);

	public abstract String toString();

}