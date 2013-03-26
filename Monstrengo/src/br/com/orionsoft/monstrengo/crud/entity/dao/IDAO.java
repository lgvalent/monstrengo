package br.com.orionsoft.monstrengo.crud.entity.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import br.com.orionsoft.monstrengo.crud.entity.dao.DAOException;


/**
 * TODO DOCUMENTAR essa interface
 * Created on 17/02/2005
 * @author Marcia
 */
public interface IDAO<T>
{
    public final static long ENTITY_UNSAVED = -1;
    public final static String PROPERTY_ID_NAME = "id";
    public final static String ENTITY_ALIAS_HQL = "entity";
    public final static String PROPERTY_SEPARATOR = ".";

    public abstract String getEntityClassName();
    public abstract Class<T> getEntityClass();
    
    public boolean isEmbeddable();
    public boolean isPersistent();
    
    /**
     * Salva ou atualiza o objeto no banco de dados
     * @param obj objeto a ser persistido.
     */
    public abstract void update(Object  obj) throws DAOException;

    /**
     * Salva ou atualiza o objeto no banco de dados
     * @param obj objeto a ser persistido.
     */
    public abstract void update(Object obj, Session session) throws DAOException, HibernateException;
    /**
     * Busca todos os registros no banco de dados que atendam à condição de pesquisa
     * @return Lista de objetos que atendam à condição de pesquisa
     * @param condicao Esta condição é composta das expressões que serão utilizadas
     * na cláusula WHERE.<br>
     * Utilize o alias entity (IDAO.ENTITY_ALIAS_HQL) antes das propriedades para evitar montagem de SQL inválidas. 
     * Não forneça cláusulas SELECT, FROM ou WHERE nesta condição.<br>
     * Na condição pode ser utilizada em seu final a clausua ORDER BY que define a ordem
     * de listagem dos objetosa. 
     */
    public abstract List<T> getList(String condicao) throws DAOException;

    /**
    * Busca todos registros no banco de dados 
    * @return Lista de objetos que atendam à condição de pesquisa
    */
    public abstract List<T> getList() throws DAOException;

    /**
     * Recupera um objeto do banco de dados.
     * @param pk identificador do objeto a ser recuperado
     * @return retorna o objeto recuperado 
     */
    public abstract T retrieve(long pk) throws DAOException;

    /**
     * Exclui um objeto do banco de dados
     * @param obj objeto a ser excluido
     */
    public abstract void delete(Object obj) throws DAOException;

    /**
     * Exclui um objeto do banco de dados
     * @param obj objeto a ser excluido
     */
    public abstract T create() throws DAOException;
}
