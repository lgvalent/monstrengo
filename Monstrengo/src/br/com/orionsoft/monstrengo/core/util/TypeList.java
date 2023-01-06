package br.com.orionsoft.monstrengo.core.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * TODO NÃO-UTILIZADO classe criada para verificação dos tipos de elementos de uma lista. 
 * Com o recurso de genericos do java 5 essa classe não tem mais utilidade.
 * Created on 15/04/2005
 * @author marcia
 *
 */
public class TypeList implements List
{
    private List list;
    
    private Class klazz;
    
    public Class getTypeClass() {
        return klazz;
    }
    
    public TypeList(List list, Class klazz) {
        this.list = list;
        this.klazz = klazz;
    }
    
    public int size()
    {
        return list.size();
    }

    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    public boolean contains(Object obj)
    {
        return list.contains(obj);
    }

    public Iterator iterator()
    {
        return list.iterator();
    }

    public Object[] toArray()
    {
        return list.toArray();
    }

    @SuppressWarnings("unchecked")
    public Object[] toArray(Object[] array)
    {
        return list.toArray(array);
    }

    @SuppressWarnings("unchecked")
    public boolean add(Object arg0)
    {
        return list.add(arg0);
    }

    public boolean remove(Object arg0)
    {
        return list.remove(arg0);
    }

    public boolean containsAll(Collection arg0)
    {
        return list.contains(arg0);
    }

    @SuppressWarnings("unchecked")
    public boolean addAll(Collection arg0)
    {
        return list.addAll(arg0);
    }

    @SuppressWarnings("unchecked")
    public boolean addAll(int arg0, Collection arg1)
    {
        return list.addAll(arg0, arg1);
    }

    public boolean removeAll(Collection arg0)
    {
        return list.remove(arg0);
    }

    public boolean retainAll(Collection arg0)
    {
        return retainAll(arg0);
    }

    public void clear()
    {
        list.clear();
    }

    public Object get(int arg0)
    {
        return list.get(arg0);
    }

    @SuppressWarnings("unchecked")
    public Object set(int arg0, Object arg1)
    {
        return list.set(arg0, arg1);
    }

    @SuppressWarnings("unchecked")
    public void add(int arg0, Object arg1)
    {
        list.add(arg0, arg1);
    }

    public Object remove(int arg0)
    {
        return list.remove(arg0);
    }

    public int indexOf(Object arg0)
    {
        return list.indexOf(arg0);
    }

    public int lastIndexOf(Object arg0)
    {
        return list.lastIndexOf(arg0);
    }

    public ListIterator listIterator()
    {
        return list.listIterator();
    }

    public ListIterator listIterator(int arg0)
    {
        return list.listIterator(arg0);
    }

    public List subList(int arg0, int arg1)
    {
        return list.subList(arg0, arg1);
    }
    
}
