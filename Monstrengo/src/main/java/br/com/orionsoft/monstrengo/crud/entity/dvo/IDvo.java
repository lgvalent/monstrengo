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
	 * Efetua as valida��es que devem ser aplicadas a uma entidade
	 * antes de sua cria��o. Ou seja, antes de sua disponibiliza��o para edi��o pelo operador do sistema
	 * que requisitou.<br>
	 * Lucio 01102007: Como alguma opera��es podem ser negadas durante o before, dependendo do operador que solicitou,
	 * � informado tamb�m o atual operador do sistema que solicitou a a��o.<br>   
	 * @return N�o retorna nada. Em caso de problemas, uma exce��o ser� levantada. Poder� ser do tipo ERROR ou INFO. 
	 */
	public void beforeCreate(IEntity<T> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException;
	
	/**
	 * Efetua as valida��es que devem ser aplicadas a uma entidade
	 * depois de sua edi��o pelo operador do sistema.    
	 * <b>Como por exemplo</b>, um movimento de uma conta n�o poder� ser inserido dentro de um per�odo no qual a conta
	 * a que ele se relaciona esteja com a movimenta��o fechada para aquele per�odo.    
	 * Lucio 20100615: Como alguma opera��es podem ser negadas durante o after, dependendo do operador que solicitou,
	 * � informado tamb�m o atual operador do sistema que solicitou a a��o.<br>   
	 * @return N�o retorna nada. Em caso de problemas, uma exce��o ser� levantada. Poder� ser do tipo ERROR ou INFO. 
	 */
	public void afterCreate(IEntity<T> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException;

	/**
	 * Efetua as valida��es que devem ser aplicadas a uma entidade
	 * antes de sua edi��o. Ou seja, antes de sua disponibiliza��o para edi��o pelo operador do sistema
	 * que requisitou.<br>   
	 * � �til para impedir a altera��o de entidades que n�o podem ser alteradas em caso de alguma
	 * situa��o restritiva.<br>
	 * <b>Como por exemplo</b>, um movimento de uma conta n�o poder� ter seus dados alterados se a conta
	 * a que ele se relaciona encontra-se com a movimenta��o fechada para o per�odo que se refere ao
	 * movimento.<br>    
	 * Lucio 01102007: Como alguma opera��es podem ser negadas durante o before, dependendo do operador que solicitou,
	 * � informado tamb�m o atual operador do sistema que solicitou a a��o.<br>   
	 * @return N�o retorna nada. Em caso de problemas, uma exce��o ser� levantada. Poder� ser do tipo ERROR ou INFO. 
	 */
	public void beforeUpdate(IEntity<T> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException;

	/**
	 * Efetua as valida��es que devem se aplicadas a uma entidade
	 * depois de sua edi��o pelo operador do sistema.  
	 * Lucio 20100615: Como alguma opera��es podem ser negadas durante o after, dependendo do operador que solicitou,
	 * � informado tamb�m o atual operador do sistema que solicitou a a��o.<br>   
	 * @return N�o retorna nada. Em caso de problemas, uma exce��o ser� levantada. Poder� ser do tipo ERROR ou INFO. 
	 */
	public void afterUpdate(IEntity<T> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException;

	/**
	 * Efetua as valida��es que devem ser aplicadas a uma entidade
	 * antes de sua remo��o do banco de dados.
	 * Ou seja, antes de sua disponibiliza��o para que o operador confirme a remo��o.<br>
	 * � �til para impedir exclus�o de entidades que n�o podem ser exclu�das em caso de alguma
	 * situa��o restritiva.<br>
	 * <b>Como por exemplo</b>, um movimento de uma conta n�o pode ser exclu�do se a conta
	 * a que ele se relaciona encontra-se com a movimenta��o fechada para o per�odo que se refere ao
	 * movimento.<br>    
	 * Lucio 01102007: Como alguma opera��es podem ser negadas durante o before, dependendo do operador que solicitou,
	 * � informado tamb�m o atual operador do sistema que solicitou a a��o.<br>   
	 * @return N�o retorna nada. Em caso de problemas, uma exce��o ser� levantada. Poder� ser do tipo ERROR ou INFO. 
	 */
	public void beforeDelete(IEntity<T> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException;

	/**
	 * Efetua as valida��es que devem ser aplicadas a uma entidade
	 * depois que o operador confirmou a sua remo��o.   
	 * Lucio 20100615: Como alguma opera��es podem ser negadas durante o after, dependendo do operador que solicitou,
	 * � informado tamb�m o atual operador do sistema que solicitou a a��o.<br>   
	 * @return N�o retorna nada. Em caso de problemas, uma exce��o ser� levantada. Poder� ser do tipo ERROR ou INFO. 
	 */
	public void afterDelete(IEntity<T> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException;
}
