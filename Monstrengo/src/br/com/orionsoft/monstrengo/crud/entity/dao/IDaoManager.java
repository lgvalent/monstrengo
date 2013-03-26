
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
     * Obtem a instância do DAO responsável para tratar a classe de objetos passada.
     * 
     * @param daos Classe do objeto que se requer o Dao.
     * @throws DAOException 
     */
    public abstract <E> IDAO<E> getDaoByEntity(String entityClassName) throws DAOException;

    /**
     * Obtem a instância do DAO responsável para tratar a classe de objetos passada.
     * 
     * @param daos Classe do objeto que se requer o Dao.
     * @throws DAOException 
     * @since 20060203
     */
    public abstract <E> IDAO<E> getDaoByEntity(Class<E> klazz) throws DAOException;

    /**
     * Obtem uma lista de todas as subEntidades que estende uma classe.
     * Útil para que o EntityManager descubra quem são as subEntidade de um classe e integre
     * esta informação nos metadados da entidade estendida, o que permitirá que uma tela de cadastro
     * da entidade extendida forneça opção de cadastros de suas subentidades.
     * Antes, isto era indicado nos metadados das entidades (.properties), mas é uma informação que pode ser gerada
     * automaticamente ao se registrar um dao de uma classe.
     * 
     * @param klazz Classe do objeto que se requer as subentidades registradas.
     * @return Retorna uma lista de sub entidades ou uma lista vazia.
     * @throws DAOException 
     * @since 20110614
     * @author Lucio
     */
    public abstract List<Class<?>> getSubEntities(Class<?> klazz) throws DAOException;

    /** Permite ao ServiceManager e outros acessarem a fábrica de sessão do DaoManager para construir
	 * suas sessões. Isto foi criado porque o ServiceManager controla as transações ah doc.
	 * @return
	 */
	public abstract SessionFactory getSessionFactory();
 
}