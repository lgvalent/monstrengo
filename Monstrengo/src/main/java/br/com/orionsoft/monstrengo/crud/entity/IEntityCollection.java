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
 * Define a interface de uma cole��o de entidades;
 * Internamente, s�o manipuladas duas listas:
 * Uma de entidades e uma de objetos;
 * cada opera��o � refletida nas duas listas, assim,
 * a lista de objetos, muitas vezes persistida, refletir�
 * as opera��es realizadas na lista de entidades (calculada).
 * <b>IMPORTANTE: N�o utilizar a interface Iterator para percorrer
 * a lista, pois est� percorrer� a lista de entidade, e caso, uma 
 * opera��o iterator.remove() seja executada, ela n�o refletir� 
 * a remo��o na lista real de objetos n�o encapsulados dentro
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
     * Retorna uma cole��o de objetos baseada nos objetos que se encontram
     * encapsulados dentro dos elementos IEntity.
     * <p>�til principalmente na camada de persist�ncia. Onde o 
     * importante � o objeto e n�o a entidade. 
     * 
     * @see IEntity#getObject()
     * 
     * @return
     */
    public abstract Collection<T> getObjectCollection();

    /**
     * Retorna uma cole��o de IEntity que se encontra
     * encapsulada dentro da class que implementa esta interface.
     * <p>�til principalmente na camada de vis�o para pode pegar a cole��o de entidades
     * sem distinguir se � um SET ou uma LIST. 
     * 
     * @since 20120105 Lucio
     * 
     * @return
     */
    public abstract Collection<IEntity<T>> getCollection();

    /**
     * Este m�todo � �til para obter o tamanho da cole��o 
     * obedecendo o padr�o JavaBean,
     * j� que o tamanho s� � fornecido por size e n�o por getSize 
     * segundo a interface java.utils.Collection. 
     * @return
     */
    public int getSize();
    
	/**
	 * Este m�todo retorna um array com os items ordenados pelos
	 * id das entidades. Foi necess�rio porque a cada conversao de uma
	 * collection para um array, a ordem dos items era indeterminada
	 * o que impraticava utilizar este metodo em uma lista de interface, pois
	 * os items ficavam mudando de ordem toda hora que a interface era atualizada.
	 * 
	 * @author lucio e tatiana
	 * @since 20060907
	 */
    public Object[] getArray();

    /**
     * Este m�todo retorna o primeiro elemento da lista.
     * Ele � muito utilizado quando se realiza ums consulta
     * e se espera apenas um elemento. 
     * @return
     */
    public IEntity<T> getFirst();
    
    /**
     * Este m�todo permite definir um id que ser� utilizado
     * pelos metodos runXxx();
     * O valor definido pelo setRunId prevalece sobre a entidade que
     * tiver sido definida pelo setRunEntity 
     * @since 20060317
     */
    public Long getRunId();
    public void setRunId(Long runId);
    /**
     * Este m�todo permite definir uma entidade que ser� utilizado
     * pelos metodos runXxx();
     * O valor definido pelo setRunId prevalece sobre a entidade que
     * tiver sido definida pelo setRunEntity 
     * @since 20061120
     */
    public IEntity<T> getRunEntity();
    public void setRunEntity(IEntity<T> runEntity);
    /**
     * Localiza a entidade com o id igual ao runId.
     * Adiciona ela na cole��o, caso ela j� n�o se encontre.
     * @return Retorna true se a adi��o foi realizada com sucesso.
     * @throws BusinessException
     * @since 20060317
     */
    public boolean runAdd() throws BusinessException ;
    /**
     * Percorre a cole��o comparando os ids das entidades
     * com o runId, se encontrar, remove a entidade da cole��o, 
     * sen�o  retorna false;
     * @return Retorna true se removeu e false se n�o encontrou.
     * @throws BusinessException
     * @since 20060317
     */
    public boolean runRemove() throws BusinessException ;

    /**
     * Percorre a cole��o verificando as entidades
     * que est�o selecionadas (isSelected()==true);
     * @throws BusinessException
     * @since 20060317
     */
    public void runRemoveSelected() throws BusinessException ;

    
    /**
     * Este m�todo cria uma lista de entidades em forma de uma lista de sele��o. Muito
     * �til quando algum processo quer gerar exibir uma lista de poss�veis entidades
     * 
     * @return Retorna uma lista de itens de sele��o preenchido com as entidades atuais da lista.
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
	 * Este m�todo percorre a cole��o identificando se o id passado se encontra na lista.<br>
	 * Muito �til quando se tem uma cole��o vindas do banco e deseja verificar se um determinado
	 * objeto (id) est� nesta cole��o.<br>
	 * Como um objeto n�o persistido possui um id = -1, este m�todo n�o ser� �il
	 * para verificar se uma entidade n�o persistida j� se encontra na cole��o.
	 * 
	 *  @author Lucio
	 *  @since 20070913
	 */
	public boolean containsId(long id);
}
