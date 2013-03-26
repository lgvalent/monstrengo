
package br.com.orionsoft.monstrengo.crud.entity.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;

import br.com.orionsoft.monstrengo.crud.entity.dao.DAOException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.core.IManager;



/**
 * 
 * @author Lucio
 * @version 20060203
 *
 */
public interface IDaoManager extends IManager{
	

    /**
     * @return Returns the daos.
     */
    public abstract Map<String, IDAO<?>> getDaos();

    /**
     * @param daos The daos to insert.
     * @throws DAOException 
     */
    public abstract void registerDao(IDAO<?> dao) throws DAOException;
    public abstract void unregisterDao(IDAO<?> dao) throws DAOException;

    /**
     * Obtem a inst�ncia do DAO respons�vel para tratar a classe de objetos passada.
     * 
     * @param daos Classe do objeto que se requer o Dao.
     * @throws DAOException 
     */
    public abstract <E> IDAO<E> getDaoByEntity(String entityClassName) throws DAOException;

    /**
     * Obtem a inst�ncia do DAO respons�vel para tratar a classe de objetos passada.
     * 
     * @param daos Classe do objeto que se requer o Dao.
     * @throws DAOException 
     * @since 20060203
     */
    public abstract <E> IDAO<E> getDaoByEntity(Class<E> klazz) throws DAOException;

    /**
     * Obtem uma lista de todas as subEntidades que estende uma classe.
     * �til para que o EntityManager descubra quem s�o as subEntidade de um classe e integre
     * esta informa��o nos metadados da entidade estendida, o que permitir� que uma tela de cadastro
     * da entidade extendida forne�a op��o de cadastros de suas subentidades.
     * Antes, isto era indicado nos metadados das entidades (.properties), mas � uma informa��o que pode ser gerada
     * automaticamente ao se registrar um dao de uma classe.
     * 
     * @param klazz Classe do objeto que se requer as subentidades registradas.
     * @return Retorna uma lista de sub entidades ou uma lista vazia.
     * @throws DAOException 
     * @since 20110614
     * @author Lucio
     */
    public abstract List<Class<?>> getSubEntities(Class<?> klazz) throws DAOException;

    /** Permite ao ServiceManager e outros acessarem a f�brica de sess�o do DaoManager para construir
	 * suas sess�es. Isto foi criado porque o ServiceManager controla as transa��es ah doc.
	 * @return
	 */
	public abstract SessionFactory getSessionFactory();
 
}