package br.com.orionsoft.monstrengo.crud.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;

/**
 * Esta classe mantém uma lista de entidades.
 * 
 * @author marcia 2005/11/21
 * @version marcia-2005/11/28
 *
 */
public class EntityList<T> extends EntityCollection<T> implements IEntityList<T>
{

    private IEntityManager entityManager;

    private List<IEntity<T>> list;
    
    private List<T> objectList;

    private IEntityMetadata info;
    
    /**
     * Constrói o conjunto de entidades baseado num conjunto de objetos. 
     * Todos os objetos da lista original são convertidos no construtor.
     * <p> O conjunto original é mantido para que operações de add e remove
     * sejam refletidas no conjunto original.
     * <p> A conversão de Objeto para Entidade é otimizada, pois os metadados
     * da classe já são indicados, não havendo a busca dos mesmos a cada conversão. 
     *  
     * @param objectList Lista de objetos, geralmente, persistidos.
     * @param info Metadados da entidade.
     * @param entityManager Gerenciador de entidade que criou este conjunto.
     * @throws EntityException 
     */
    public EntityList(List<T> objectList, IEntityMetadata info, IEntityManager entityManager) throws EntityException
    {
        this.entityManager = entityManager;

        /* Verifica se a coleção está inicializada ou é nula */
        if(objectList==null)
        	this.objectList = new ArrayList<T>();
        else
            this.objectList = objectList;
        
        this.list = new ArrayList<IEntity<T>>(this.objectList.size());

        // Converte cada elemento do conjunto em um IEntity
        for(Object obj: this.objectList)
           this.list.add(new BusinessEntity<T>(obj, info, entityManager));
        
        this.info = info;
        
    }

    
    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#getInfo()
	 */
    public IEntityMetadata getInfo()
    {
        return info;
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#size()
	 */
    public int size()
    {
        return list.size();
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#getSize()
	 */
    public int getSize()
    {
        return list.size();
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#isEmpty()
	 */
    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#contains(java.lang.Object)
	 */
    public boolean contains(Object arg0)
    {
        return list.contains(arg0);
    }

	public <T> T[] toArray(T[] a) {
	       return list.toArray(a);
	}
    
    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#toArray()
	 */
    public Object[] toArray()
    {
        
       return list.toArray();
    }
    
    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#getArray()
	 */
    public Object[] getArray()
    {
    	Object[] result = list.toArray();

    	Arrays.<Object>sort(result, IEntityCollection.COMPARATOR_ID);
        
        return result;
    }
    
    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#getArray()
	 */
    public List<IEntity<T>> getList()
    {
        return list;
    }
    
    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#add(br.com.orionsoft.monstrengo.crud.entity.IEntity)
	 */
    public boolean add(IEntity<T> arg0)
    {
    	list.add(arg0);

    	// Reflete a atual operação na lista original
    	return this.objectList.add(arg0.getObject());
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#remove(java.lang.Object)
	 */
    public boolean remove(Object arg0)
    {
    	list.remove(arg0);

    	// Reflete a atual operação na lista original
    	return this.objectList.remove(((IEntity<?>)arg0).getObject());
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#containsAll(java.util.Collection)
	 */
    public boolean containsAll(Collection<?> arg0)
    {
        return list.containsAll(arg0);
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#addAll(java.util.Collection)
	 */
    public boolean addAll(Collection<? extends IEntity<T>> arg0)
    {
        // Reflete a atual operação na lista original
        for(IEntity<T> ent: arg0)
            this.objectList.add(ent.getObject());
        
        return list.addAll(arg0);
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#removeAll(java.util.Collection)
	 */
    public boolean removeAll(Collection<?> arg0)
    {
        // Reflete a atual operação na lista original
        for(Object obj: arg0)
            this.objectList.remove(((IEntity<?>)obj).getObject());

        return list.removeAll(arg0);
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#retainAll(java.util.Collection)
	 */
    public boolean retainAll(Collection<?> arg0)
    {
        // Reflete a atual operação na lista original
        
        //      Cria uma coleção
        Collection<Object> col = new ArrayList<Object>(arg0.size()); 
        //      Adiciona todos os objetos
        for(Object obj: arg0)
            col.add(((IEntity<?>)obj).getObject());
        //      Realiza a operação original
        this.objectList.retainAll(col);

        return list.retainAll(arg0);
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#clear()
	 */
    public void clear()
    {
        // Reflete a atual operação na lista original
        this.objectList.clear();
        
        list.clear();
    }
    
    public List<Long> getIdList() throws EntityException {
    	List<Long> list = new ArrayList<Long>(this.list.size());
    	for (IEntity<T> entity : this.list) {
    		if(entity.isSelected())
    			list.add(entity.getId());
    	}
    	return list;
    }
    
    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#getObjectList()
	 */
    public List<T> getObjectList()
    {
        return objectList;
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#getObjectCollection()
	 */
    public Collection<T> getObjectCollection()
    {
        return objectList;
    }

    public Collection<IEntity<T>> getCollection() {
    	return list;
    }
    
    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#getEntityManager()
	 */
    public IEntityManager getEntityManager()
    {
        return entityManager;
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#get(int)
	 */
    public IEntity<T> get(int index)
    {
        return list.get(index);    
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#addAll(int, java.util.Collection)
	 */
    public boolean addAll(int index, Collection<? extends IEntity<T>> c)
    {
        // Reflete a atual operação na lista original
        //      Cria uma coleção
        Collection<T> col = new ArrayList<T>(c.size()); 
        //      Adiciona todos os objetos
        for(IEntity<T> obj: c)
            col.add(obj.getObject());
        //      Realiza a operação original
        this.objectList.addAll(index, col);

        return list.addAll(index, c);
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#set(int, br.com.orionsoft.monstrengo.crud.entity.IEntity)
	 */
    public IEntity<T> set(int index, IEntity<T> element)
    {
        // Reflete a atual operação na lista original
        this.objectList.set(index, element.getObject());
        
        return list.set(index, element);
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#add(int, br.com.orionsoft.monstrengo.crud.entity.IEntity)
	 */
    public void add(int index, IEntity<T> element)
    {
        // Reflete a atual operação na lista original
        this.objectList.add(index, element.getObject());
        
        list.add(index, element);
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#remove(int)
	 */
    public IEntity<T> remove(int index)
    {
        // Reflete a atual operação na lista original
        this.objectList.remove(index);
        
        return list.remove(index);
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#indexOf(java.lang.Object)
	 */
    public int indexOf(Object o)
    {
        return list.indexOf(o);
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#lastIndexOf(java.lang.Object)
	 */
    public int lastIndexOf(Object o)
    {
        return list.lastIndexOf(o);
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#listIterator()
	 */
    public ListIterator<IEntity<T>> listIterator()
    {
        return list.listIterator();
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#listIterator(int)
	 */
    public ListIterator<IEntity<T>> listIterator(int index)
    {
        return list.listIterator(index);
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#subList(int, int)
	 */
    public List<IEntity<T>> subList(int fromIndex, int toIndex)
    {
        return list.subList(fromIndex, toIndex);
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#iterator()
	 */
    public Iterator<IEntity<T>> iterator()
    {
        return new EntityListIterator(this);
    }
    
    
    /**
     * Esta classe implementa o padrão PROXY que intermedia as operações sobre
     * um Iterator da lista de entidade para refletir as remoções na lista de objetos.
     * Ela guarda uma referência para a lista de entidade pai.<br>
     * Ao ser criada ele cria um iterator da lista de IEntity. Este iterator será 
     * o original. As operações recebidas serão refletidas nele. Contudo, ao ser
     * executada uma operação de remoção, esta classe irá refletir a remoção
     * na lista original de objetos<br>.
     * Sem esta classe, uma operação remove() no Iterator da IEntityList não
     * é refletida no ObjectList.
     *  
     * @author Lucio 20070911
     */
    public class EntityListIterator implements Iterator<IEntity<T>>{

		private IEntityList<T> owner;
		private Iterator<IEntity<T>> iterator;

		public EntityListIterator(IEntityList<T> owner){
			this.owner = owner;
			this.iterator = owner.getList().iterator();
		}

		public boolean hasNext(){return this.iterator.hasNext();}

		IEntity<T> lastNext = null;
		public IEntity<T> next()
		{
			/* Guarda a referência da última entidade obtida no iterator
			 * para refletir uma possível operação remove na lista original
			 * de objetos */
			lastNext = this.iterator.next();
			
			return lastNext;
		}

		/**
		 * Realiza o operação remove no Iterator original
		 * e reflete a operação na lista de objetos
		 */
		public void remove()
		{
			this.iterator.remove();
			
			this.owner.getObjectList().remove(lastNext.getObject());
		}
    	
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#getFirst()
	 */
    public IEntity<T> getFirst()
    {
        return list.get(0);
    }

    /* (non-Javadoc)
	 * @see br.com.orionsoft.monstrengo.crud.entity.IEntityList_#toString()
	 */
    public String toString(){
    	String result = "[";
    	
    	for(IEntity<T> entity: this){
    		result += entity.getObject().toString();
    		result += ", ";
    	}

    	/*Retira a última vírgula se existir*/
    	result = StringUtils.stripEnd(result, ", ");
		result += "]"; 
    	return result;
    }


	/**
	 * @see {@link IEntityCollection.containsId}
	 */
    public boolean containsId(long id)
	{
		/* Percorre o conjunto de entidades pegando o ID e comparando 
		 * para verificar se existe uma entidade com o ID fornecido */
    	for(IEntity<T> entity: this.list)
			if(entity.getId() == id)
				return true;

		return false;
	}


}
