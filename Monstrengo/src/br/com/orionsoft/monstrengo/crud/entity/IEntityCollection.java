package br.com.orionsoft.monstrengo.crud.entity;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;

/**
 * Define a interface de uma coleção de entidades;
 * Internamente, são manipuladas duas listas:
 * Uma de entidades e uma de objetos;
 * cada operação é refletida nas duas listas, assim,
 * a lista de objetos, muitas vezes persistida, refletirá
 * as operações realizadas na lista de entidades (calculada).
 * <b>IMPORTANTE: Não utilizar a interface Iterator para percorrer
 * a lista, pois está percorrerá a lista de entidade, e caso, uma 
 * operação iterator.remove() seja executada, ela não refletirá 
 * a remoção na lista real de objetos não encapsulados dentro
 * da interface IEntity.</b>
 * @author Lucio
 * @version 20060317
 *
 */
public interface IEntityCollection<T> extends Collection<IEntity<T>>
{
    public static Comparator<Object> COMPARATOR_ID = new Comparator<Object>(){
		public int compare(Object arg0, Object arg1){
			if(arg0 == null) if (arg1 ==null) return 0; else return -1;
			if(arg1 == null) return 1;
			return  new Long(((IEntity<?>)arg0).getId()).compareTo(((IEntity<?>)arg1).getId());
		}
	};
	
	public abstract IEntityMetadata getInfo();
    public abstract IEntityManager getEntityManager();
    
    /**
     * Retorna uma coleção de objetos baseada nos objetos que se encontram
     * encapsulados dentro dos elementos IEntity.
     * <p>Útil principalmente na camada de persistência. Onde o 
     * importante é o objeto e não a entidade. 
     * 
     * @see IEntity#getObject()
     * 
     * @return
     */
    public abstract Collection<T> getObjectCollection();

    /**
     * Retorna uma coleção de IEntity que se encontra
     * encapsulada dentro da class que implementa esta interface.
     * <p>Útil principalmente na camada de visão para pode pegar a coleção de entidades
     * sem distinguir se é um SET ou uma LIST. 
     * 
     * @since 20120105 Lucio
     * 
     * @return
     */
    public abstract Collection<IEntity<T>> getCollection();

    /**
     * Este método é útil para obter o tamanho da coleção 
     * obedecendo o padrão JavaBean,
     * já que o tamanho só é fornecido por size e não por getSize 
     * segundo a interface java.utils.Collection. 
     * @return
     */
    public int getSize();
    
	/**
	 * Este método retorna um array com os items ordenados pelos
	 * id das entidades. Foi necessário porque a cada conversao de uma
	 * collection para um array, a ordem dos items era indeterminada
	 * o que impraticava utilizar este metodo em uma lista de interface, pois
	 * os items ficavam mudando de ordem toda hora que a interface era atualizada.
	 * 
	 * @author lucio e tatiana
	 * @since 20060907
	 */
    public Object[] getArray();

    /**
     * Este método retorna o primeiro elemento da lista.
     * Ele é muito utilizado quando se realiza ums consulta
     * e se espera apenas um elemento. 
     * @return
     */
    public IEntity<T> getFirst();
    
    /**
     * Este método permite definir um id que será utilizado
     * pelos metodos runXxx();
     * O valor definido pelo setRunId prevalece sobre a entidade que
     * tiver sido definida pelo setRunEntity 
     * @since 20060317
     */
    public Long getRunId();
    public void setRunId(Long runId);
    /**
     * Este método permite definir uma entidade que será utilizado
     * pelos metodos runXxx();
     * O valor definido pelo setRunId prevalece sobre a entidade que
     * tiver sido definida pelo setRunEntity 
     * @since 20061120
     */
    public IEntity<T> getRunEntity();
    public void setRunEntity(IEntity<T> runEntity);
    /**
     * Localiza a entidade com o id igual ao runId.
     * Adiciona ela na coleção, caso ela já não se encontre.
     * @return Retorna true se a adição foi realizada com sucesso.
     * @throws BusinessException
     * @since 20060317
     */
    public boolean runAdd() throws BusinessException ;
    /**
     * Percorre a coleção comparando os ids das entidades
     * com o runId, se encontrar, remove a entidade da coleção, 
     * senão  retorna false;
     * @return Retorna true se removeu e false se não encontrou.
     * @throws BusinessException
     * @since 20060317
     */
    public boolean runRemove() throws BusinessException ;

    /**
     * Percorre a coleção verificando as entidades
     * que estão selecionadas (isSelected()==true);
     * @throws BusinessException
     * @since 20060317
     */
    public void runRemoveSelected() throws BusinessException ;

    
    /**
     * Este método cria uma lista de entidades em forma de uma lista de seleção. Muito
     * útil quando algum processo quer gerar exibir uma lista de possíveis entidades
     * 
     * @return Retorna uma lista de itens de seleção preenchido com as entidades atuais da lista.
     * @throws EntityException
     * @since 20070607
     */
    public abstract List<SelectItem> getEntitySelectItems() throws EntityException;

	/**
	 * Permite obter um vetor com os ids das atuais entidades que se encontram na lista.
	 * @return
	 * @throws PropertyValueException
	 */
    public Long[] getIds() throws BusinessException;
	public void setIds(Long[] ids) throws BusinessException;
	public void setIds(Long[] ids, ServiceData serviceDataOwner) throws BusinessException;
	
	/**
	 * Este método percorre a coleção identificando se o id passado se encontra na lista.<br>
	 * Muito útil quando se tem uma coleção vindas do banco e deseja verificar se um determinado
	 * objeto (id) está nesta coleção.<br>
	 * Como um objeto não persistido possui um id = -1, este método não será úil
	 * para verificar se uma entidade não persistida já se encontra na coleção.
	 * 
	 *  @author Lucio
	 *  @since 20070913
	 */
	public boolean containsId(long id);
}
