/**
 * Criado em 04/08/2005
 */
package br.com.orionsoft.monstrengo.crud.entity;

import java.util.List;
import java.util.Map;

import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;
import br.com.orionsoft.monstrengo.crud.entity.IGroupProperties;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;

/**
 * Esta interface abstrai as fun��es gerais de uma entidade de neg�cio.
 * S�o fornecidos m�todos de acesso �s propriedades, aos metadados e ainda
 * possibilita executar procedimentos como <code>update()</code> e <code>delete()</code>. Estes �ltimos
 * devem ser implementados utilizando o servi�o CRUD que foi utilizado para 
 * criar a inst�ncia da atual classe.<br>
 * 
 * @author Lucio
 * @version 20060413
 */
public interface IEntity<T>
{
    /**
     * <p>Este m�todo faz uma busca por uma entidade usando uma string de busca.</p>
     * TODO Ele � �til para diretamente da tela ter uma refer�ncia a IEntity e poder buscar
     * Mas seria interessante isto estar em um futuro IProcessParam 
     * 
     * @return
     */
    public List<IEntity<T>> find(String queryString) throws EntityException;

	/**
     * <p>Este m�todo retorna o objeto que � manipulado como entidade.
     * Com este objeto � poss�vel obter os valores e as propriedades
     * implementadas.</p> 
     * 
     * @return
     */
    public T getObject();
    
    /**
     * Ao setar o id da entidade, � feito um retrieve para buscar no banco a entidade 
     * correspondente ao id passado como par�metro.
     * @param long id 
     * @throws EntityException
     */
    public void setId(long id) throws EntityException;
    public long getId();
    
    public IProperty getProperty(String propertyName) throws EntityException;

    public Map<String, IProperty> getPropertiesMap();
    public <E> E getPropertyValue(String propertyName) throws EntityException;
    public void setPropertyValue(String propertyName, Object value) throws EntityException;
    
    public IEntityMetadata getInfo();
    
    public String toString();

    /**
     * <p>Fornece uma lista com as propriedades indexadas pelo �ndice definido
     * no metadado da propriedade.<p>
     * 
     * @return Lista indexada das propriedades.
     */
    public IProperty[] getProperties();
    
    /**
     * <p>Fornece uma lista dos grupos das propriedades.<p>
     * @return Lista de grupos com as proriedades de cada grupo.
     * @since 20060413
     */
    public IGroupProperties[] getGroupsProperties() throws EntityException ;
    
	public IProperty[] getPropertiesInQueryGrid();

    
    public IEntityManager getEntityManager();
    
    /**
     * Esta propriedade pode ser utilizada quando a entidade 
     * faz parte de uma cole��o de entidades e que algumas podem
     * ser marcadas como selecionadas e outras n�o, antes da aplica��o de
     * determinados processos. Como tamb�m poder� ser usada para outros fins.
     * @return 
     * @since 20060315
     */
    public boolean isSelected();
    public void setSelected(boolean selected);
    
}
