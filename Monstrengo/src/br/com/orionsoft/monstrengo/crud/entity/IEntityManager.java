
package br.com.orionsoft.monstrengo.crud.entity;


import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntitySet;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoManager;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDaoManager;
import br.com.orionsoft.monstrengo.crud.entity.dvo.IDvoManager;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IMetadataHandle;



/**
 *
 * @version 20060613
 *
 */
public interface IEntityManager {

    /**
     * Este m�todo cria uma entidade de neg�cio baseado no objeto fornecido.
     *
     * @param object
     *            Objeto persistido que ser� transformado em uma entidade de
     *            neg�cio.
     * @return Retorna uma entidade de neg�cio com seus dados e metadados.
     * @throws EntityException
     */
    public abstract <T> IEntity<T> getEntity(Object object) throws EntityException;

    /**
     * Este m�todo retorna uma inst�ncia do metadado da classe solicitada
     * obtendo o valor padr�o definido nos arquivos .properties criados pelo
     * programado. Para isto, o MetadataHandle � operado em um modo especial para retornar os 
     * valores padr�es e n�o os valores que ele pode encontrar no banco. 
     * 
     * @return
     * @throws EntityException
     */
    public abstract IEntityMetadata getEntityMetadataDefaults(Class<?> entityClass) throws EntityException;

    /**
     * Este m�todo retorna uma inst�ncia do metadado da classe solicitada. 
     * Ele utiliza o map do m�todo getEntitiesMetadata para localizar a entidade.
     *
     * @return
     * @throws EntityException
     */
    public abstract IEntityMetadata getEntityMetadata(Class<?> entityClass) throws EntityException;

    /**
     * Este m�todo permite limpar o cache de metadados que � mantido
     * pelo entity manager. Assim, quando houver alguma altera��o
     * dos metados no banco, os mesmos poder�o ser recarregados
     * pois este m�todo limpa as atuais inst�ncias e for�a uma
     * recarga.
     * @since 20070510
     */
    public void refreshEntitiesMetadata();

    /**
     * Este m�todo retorna uma map com inst�ncia de todos os metadado das
     * classes registradas no sistema. O mapa � criado somente uma vez
     * e fica bufferizado para futuras chamadas
     *
     * @return
     * @throws EntityException
     */
    public abstract Map<String,IEntityMetadata> getEntitiesMetadata() throws EntityException;

    /**
     * <p>Este m�todo converte um conjunto de objetos em um conjunto de entidade.
     * <p>� realizada somente uma busca de metadados para todo o conjunto.
     *
     * @param classObj Tipo da classe de objetos que est�o na lista.
     * @param set Conjunto de objetos persistidos que ser� transformada em um conjunto de entidade de neg�cio.
     *
     * @return Retorna uma lista de entidade de neg�cio com seus dados e metadados.
     *
     * @throws EntityException
     */
    public abstract <T> IEntitySet<T> getEntitySet(Set<T> set, Class<T> classObj) throws EntityException;

    /**
     * <p>Este m�todo converte uma lista de objetos em uma lista de entidade.
     * <p>� realizada somente uma busca de metadados para toda a lista.
     *
     * @param classObj Tipo da classe de objetos que est�o na lista.
     * @param list Lista de objetos persistidos que ser� transformada em uma lista de entidade de neg�cio.
     *
     * @return Retorna uma lista de entidade de neg�cio com seus dados e metadados.
     *
     * @throws EntityException
     */
    public abstract <T> IEntityList<T> getEntityList(List<T> list, Class<T> classObj) throws EntityException;

    /**
     * Este m�todo cria uma lista de entidades em forma de uma lista de sele��o. Muito
     * �til quando algum processo quer gerar exibir uma lista de poss�veis entidades.<br>
     * Quando a entidade � enum o sistema n�o busca os objetos no banco, mas sim na lista
     * de valores constantes de class enum.
     *
     * @param classObj
     * @return
     * @throws EntityException
     * @since 20060613
     */
    public abstract List<SelectItem> getEntitySelectItems(Class<?> classObj, String hqlWhereExp) throws EntityException;

    /**
     * Este m�todo cria uma lista de entidades em forma de uma lista de sele��o. Muito
     * �til quando algum processo quer gerar exibir uma lista de poss�veis entidades.<br>
     * Quando a entidade � enum o sistema n�o busca os objetos no banco, mas sim na lista
     * de valores constantes de class enum.
     * Um filtro � fornecido para se buscar em todas as propriedades da entidade
     * utilizando o QueryService
     *
     * @param classObj
     * @return
     * @throws EntityException
     * @since 20111208
     */
    public abstract List<SelectItem> queryEntitySelectItems(Class<?> classObj, String filter, int maxResult) throws EntityException;
	public IEntityList<?> queryEntities(Class<?> classObj, String filter, String staticHqlWhereFilter, int maxResult) throws EntityException;

    public abstract IMetadataHandle getMetadataHandle();

    public abstract void setServiceManager(IServiceManager serviceManager);
    public abstract IServiceManager getServiceManager();

    public abstract IDaoManager getDaoManager();

    /**
     * Indica o gerenciador de valida��es que � utilizado por este gerenciador de entidades.
     * @param DvoManager
     */
    public abstract IDvoManager getDvoManager();
    
        
    /**
     * Retorna a lista de metadados das subentidades de uma entidade.<br>
     * Isto � observado na classe {@link Contrato} e suas subclasses {@link ContratoFinanceiro}, {@link ContratoSindicato}.  
     * @throws EntityException 
     */
    public List<IEntityMetadata> getSubEntitiesMetadata(Class<?> entityClass) throws EntityException;

    
}