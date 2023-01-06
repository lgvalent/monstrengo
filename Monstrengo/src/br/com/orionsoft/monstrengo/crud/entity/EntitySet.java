package br.com.orionsoft.monstrengo.crud.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;

/**
 * Esta classe mantem um conjunto de entidades.
 * Internamente ela armazena a lista de objetos persistidos
 * e vai convertendo estes objetos em IEntity<T> conforme haja a solicitação.
 * 
 * @author marcia 2005/11/21
 *
 */
public class EntitySet<T> extends EntityCollection<T> implements IEntitySet<T>
{

    private IEntityManager entityManager;

    private Set<IEntity<T>> set;

    private Set<T> objectSet;
    
    private IEntityMetadata info;
    
    /**
     * Constrói o conjunto de entidades baseado num conjunto de objetos.
     * <p> O conjunto original é mantido para que operações de add e remove
     * sejam refletidas no conjunto original.
     * <p> A conversão de Objeto para Entitdade é otimizada, pois os metadados
     * da classe já é indicado, não havendo a busca do mesmo a cada conversão. 
     *  
     * @param objectSet Conjunto de objetos, geralmente, persistidos.
     * @param info Metadados da entidade.
     * @param entityManager Gerenciador de entidade que criou este conjunto.
     * @throws EntityException 
     */
    public EntitySet(Set<T> objectSet, IEntityMetadata info, IEntityManager entityManager) throws EntityException
    {
    	this.entityManager = entityManager;

        this.objectSet = objectSet;

        /* Verifica se a coleção está inicializada ou é nula */
        if(objectSet==null)
        	this.objectSet = new HashSet<T>();
        
        this.set = new HashSet<IEntity<T>>(this.objectSet.size());

        // Converte cada elemento do conjunto em um IEntity<T>
        for(Object obj: this.objectSet)
           this.set.add(new BusinessEntity<T>(obj, info, entityManager));
        
        this.info = info;
    }

    public IEntityMetadata getInfo()
    {
        return info;
    }

    public int size()
    {
        return set.size();
    }

    public int getSize()
    {
        return set.size();
    }

    public boolean isEmpty()
    {
        return set.isEmpty();
    }

    public boolean contains(Object arg0)
    {
        return set.contains(arg0);
    }

    public Object[] toArray()
    {
        
       return set.toArray();
    }
    
    /**
     * Este método retorna um array com os items ordenados pelos
     * id das entidades. Foi necessário porque a cada conversao de uma
     * collection para um array, a ordem dos items era indeterminada
     * o que impraticava utilizar este metodo em uma listam de interface, pois
     * os items ficavam mudando de ordem toda hora que a interface era atualizada.
     * @author lucio e tatiana
     * @since 20060907
     */
    public Object[] getArray()
    {
    	Object[] result = set.toArray();

    	Arrays.<Object>sort(result, IEntityCollection.COMPARATOR_ID);
        
        return result;
    }
    
    public <T> T[] toArray(T[] arg0)
    {
        return set.toArray(arg0);
    }

    public boolean add(IEntity<T> arg0)
    {
    	set.add(arg0);

    	// Reflete a atual operação no conjunto original
       return objectSet.add(arg0.getObject());
    }

    public boolean remove(Object arg0)
    {
    	set.remove(arg0);
    	
        // Reflete a atual operação no conjunto original
        return objectSet.remove(((IEntity<?>)arg0).getObject());
    }

    public boolean containsAll(Collection<?> arg0)
    {
        return set.containsAll(arg0);
    }

    public boolean addAll(Collection<? extends IEntity<T>> arg0)
    {
        // Reflete a atual operação no conjunto original
        for(IEntity<T> ent: arg0)
            objectSet.add(ent.getObject());
        
        return set.addAll(arg0);
    }

    public boolean removeAll(Collection<?> arg0)
    {
        // Reflete a atual operação no conjunto original
        for(Object obj: arg0)
            objectSet.remove(((IEntity<?>)obj).getObject());

        return set.removeAll(arg0);
    }

    public boolean retainAll(Collection<?> arg0)
    {
        // Reflete a atual operação no conjunto original
        Collection<Object> col = new ArrayList<Object>(arg0.size()); // Cria uma coleção
        for(Object obj: arg0)                                        // Adiciona todos os objetos
            col.add(((IEntity<?>)obj).getObject());
        objectSet.retainAll(col);                                    // Realiza a operação original

        return set.retainAll(arg0);
    }

    public void clear()
    {
        // Reflete a atual operação no conjunto original
        objectSet.clear();
        
        set.clear();
    }
    
	public Set<T> getObjectSet()
    {
        return objectSet;
    }

	public Collection<T> getObjectCollection()
    {
        return objectSet;
    }
	
	public Collection<IEntity<T>> getCollection() {
		return set;
	}

    public IEntityManager getEntityManager()
    {
        return entityManager;
    }

    public Iterator<IEntity<T>> iterator()
    {
        return new EntitySetIterator(set, this.getObjectSet());
    }
    /**
     * Esta classe implementa o padrão PROXY que intermedia as operações sobre
     * um Iterator da lista de entidade para refletir as remoções na lista de objetos.
     * Ela guarda uma referência para a lista de entidade pai.<br>
     * Ao ser criada ele cria um iterator da lista de IEntity<T>. Este iterator será 
     * o original. As operações recebidas serão refletidas nele. Contudo, ao ser
     * executada uma operação de remoção, esta classe irá refletir a remoção
     * na lista original de objetos.<br>
     * Sem esta classe, uma operação remove() no Iterator da IEntity<T>Set não
     * é refletida no ObjectSet.
     *  
     * @author Lucio 20070911
     */
    public class EntitySetIterator implements Iterator<IEntity<T>>{

		private Iterator<IEntity<T>> iterator;
		private Set<T> objectSet;

		public EntitySetIterator(Set<IEntity<T>> set, Set<T> objectSet){
			this.objectSet = objectSet;
			this.iterator = set.iterator();
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
			
			this.objectSet.remove(lastNext.getObject());
		}
    	
    }

    public IEntity<T> getFirst()
    {
        return set.iterator().next();
    }
    
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
	 * @see {@link IEntity<T>Collection.containsId}
	 */
    public boolean containsId(long id)
	{
		/* Percorre o conjunto de entidades pegando o ID e comparando 
		 * para verificar se existe uma entidade com o ID fornecido */
    	for(IEntity<T> entity: this.set)
			if(entity.getId() == id)
				return true;

		return false;
	}

}
