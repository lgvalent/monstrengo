 package br.com.orionsoft.monstrengo.crud.entity.dvo;

import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.crud.entity.dvo.IDvoManager;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.security.entities.UserSession;

/**
 * 
 * @author sergio
 * @version 20070523
 *
 */
public interface IDvo<T> {
	
	/***
	 * Retorna o tipo da entidade a qual pertence o dvo.  
	 */
	public Class<T> getEntityType();

	/**
	 * Obtem o DvoManager do atuao Dvo.
	 * @param dvoManager
	 * @throws DvoException
	 */
	public IDvoManager getDvoManager() throws DvoException;
	public void setDvoManager(IDvoManager dvoManager);
	
	/**
	 * Efetua as validações que devem ser aplicadas a uma entidade
	 * antes de sua criação. Ou seja, antes de sua disponibilização para edição pelo operador do sistema
	 * que requisitou.<br>
	 * Lucio 01102007: Como alguma operações podem ser negadas durante o before, dependendo do operador que solicitou,
	 * é informado também o atual operador do sistema que solicitou a ação.<br>   
	 * @return Não retorna nada. Em caso de problemas, uma exceção será levantada. Poderá ser do tipo ERROR ou INFO. 
	 */
	public void beforeCreate(IEntity<T> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException;
	
	/**
	 * Efetua as validações que devem ser aplicadas a uma entidade
	 * depois de sua edição pelo operador do sistema.    
	 * <b>Como por exemplo</b>, um movimento de uma conta não poderá ser inserido dentro de um período no qual a conta
	 * a que ele se relaciona esteja com a movimentação fechada para aquele período.    
	 * Lucio 20100615: Como alguma operações podem ser negadas durante o after, dependendo do operador que solicitou,
	 * é informado também o atual operador do sistema que solicitou a ação.<br>   
	 * @return Não retorna nada. Em caso de problemas, uma exceção será levantada. Poderá ser do tipo ERROR ou INFO. 
	 */
	public void afterCreate(IEntity<T> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException;

	/**
	 * Efetua as validações que devem ser aplicadas a uma entidade
	 * antes de sua edição. Ou seja, antes de sua disponibilização para edição pelo operador do sistema
	 * que requisitou.<br>   
	 * É útil para impedir a alteração de entidades que não podem ser alteradas em caso de alguma
	 * situação restritiva.<br>
	 * <b>Como por exemplo</b>, um movimento de uma conta não poderá ter seus dados alterados se a conta
	 * a que ele se relaciona encontra-se com a movimentação fechada para o período que se refere ao
	 * movimento.<br>    
	 * Lucio 01102007: Como alguma operações podem ser negadas durante o before, dependendo do operador que solicitou,
	 * é informado também o atual operador do sistema que solicitou a ação.<br>   
	 * @return Não retorna nada. Em caso de problemas, uma exceção será levantada. Poderá ser do tipo ERROR ou INFO. 
	 */
	public void beforeUpdate(IEntity<T> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException;

	/**
	 * Efetua as validações que devem se aplicadas a uma entidade
	 * depois de sua edição pelo operador do sistema.  
	 * Lucio 20100615: Como alguma operações podem ser negadas durante o after, dependendo do operador que solicitou,
	 * é informado também o atual operador do sistema que solicitou a ação.<br>   
	 * @return Não retorna nada. Em caso de problemas, uma exceção será levantada. Poderá ser do tipo ERROR ou INFO. 
	 */
	public void afterUpdate(IEntity<T> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException;

	/**
	 * Efetua as validações que devem ser aplicadas a uma entidade
	 * antes de sua remoção do banco de dados.
	 * Ou seja, antes de sua disponibilização para que o operador confirme a remoção.<br>
	 * É útil para impedir exclusão de entidades que não podem ser excluídas em caso de alguma
	 * situação restritiva.<br>
	 * <b>Como por exemplo</b>, um movimento de uma conta não pode ser excluído se a conta
	 * a que ele se relaciona encontra-se com a movimentação fechada para o período que se refere ao
	 * movimento.<br>    
	 * Lucio 01102007: Como alguma operações podem ser negadas durante o before, dependendo do operador que solicitou,
	 * é informado também o atual operador do sistema que solicitou a ação.<br>   
	 * @return Não retorna nada. Em caso de problemas, uma exceção será levantada. Poderá ser do tipo ERROR ou INFO. 
	 */
	public void beforeDelete(IEntity<T> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException;

	/**
	 * Efetua as validações que devem ser aplicadas a uma entidade
	 * depois que o operador confirmou a sua remoção.   
	 * Lucio 20100615: Como alguma operações podem ser negadas durante o after, dependendo do operador que solicitou,
	 * é informado também o atual operador do sistema que solicitou a ação.<br>   
	 * @return Não retorna nada. Em caso de problemas, uma exceção será levantada. Poderá ser do tipo ERROR ou INFO. 
	 */
	public void afterDelete(IEntity<T> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException;
}
