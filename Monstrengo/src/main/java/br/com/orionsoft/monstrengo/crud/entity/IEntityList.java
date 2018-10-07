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
	 * <p>�til principalmente na camada de persist�ncia. Onde o 
	 * importante � o objeto e n�o a entidade. 
	 * 
	 * @see IEntity#getObject()
	 * 
	 * @return
	 */
	public abstract List<Long> getIdList() throws EntityException;
	
	public abstract List<T> getObjectList();
	
	/**
	 * Este m�todo retorna uma lista que implementa a interface java.util.List.<br>
	 * Foi necess�rio porque a IEntityList n�o implementa mais esta interface para que o JSF
	 * n�o tente manupular ela como uma lista e perca os recursos extras adicionados como
	 * get/setRunId().
	 * 
	 * @author lucio
	 * @since 20070502
	 */
	public abstract List<IEntity<T>> getList();

	public abstract IEntity<T> get(int index);

	/**
	 * adiciona todos os elementos de uma cole��o a partir de uma determinada posi��o
	 * @param index �ndice onde o primeiro elemento ser� inserido
	 * @param c cole��o de elementos � ser inserido
	 * @return
	 */
	public abstract boolean addAll(int index, Collection<? extends IEntity<T>> c);

	/**
	 * Substitui o elemento contido na posi��o especificada da lista pela 
	 * entidade fornecida
	 * @param index posi��o do elemento a ser substituido
	 * @param element elemento que substituir� o valor original.
	 * @return
	 */
	public abstract IEntity<T>set(int index, IEntity<T>element);

	/**
	 * Adiciona o elemento fornecido na posi��o especificada
	 * @param index posi��o a ser inserido
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