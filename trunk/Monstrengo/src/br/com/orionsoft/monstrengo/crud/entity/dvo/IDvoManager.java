package br.com.orionsoft.monstrengo.crud.entity.dvo;

import java.util.Map;

import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.crud.entity.dvo.IDvo;
import br.com.orionsoft.monstrengo.core.IManager;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;

/**
 * Gerenciador que mant�m o registro de todos os Dvos das entidades existentes e 
 * controla transa��es. Existe uma �nica inst�ncia desse gerenciador
 * na aplica��o e todos os Dvos possuem refer�ncia e s�o executados por ele.
 *  
 * @author Sergio
 * @version 20070523
 */
public interface IDvoManager extends IManager{
	
	/**
	 * Cada IDvo ser� registrado na lista interna de IDvo's dispon�veis.<br>
	 * Este m�todo registra um Objeto do tipo IDvo que � passado como argumento.<br>
	 * Uma mesma entidade poder� ter mais de um IDvo registrado. Assim, o gerenciador
	 * ir� utilizar todos os IDvo's de uma entidade. E uma entidade s� ser� considerada
	 * validada se todos seus IDvo's validaram a mesma.   
	 */
	public <T> void registerDvo(IDvo<T> dvo) throws DvoException;
	
	/**
	 * Cada IDvo ser� registrado na lista interna de IDvo's dispon�veis.<br>
	 * Este m�todo registra um Objeto do tipo IDvo que � passado como argumento.<br>
	 * Uma mesma entidade poder� ter mais de um IDvo registrado. Assim, o gerenciador
	 * ir� utilizar todos os IDvo's de uma entidade. E uma entidade s� ser� considerada
	 * validada se todos seus IDvo's validaram a mesma.   
	 */
	public <T> void unregisterDvo(IDvo<T> dvo) throws DvoException;
	
	
	/**
	 * Obtem o Dvo atualmente registrado para a entidade passada. 
	 * @param entity
	 * @return
	 */
	public <T> IDvo<T> getDvoByEntity(IEntity<T> entity) throws DvoException;

	/**
	 * Verifica se contem algum DVO registrado para uma determinada entidade. 
	 * @param entity
	 * @return
	 */
	public boolean contains(IEntity<?> entity) throws DvoException;

	/**
	 * Obtem a cole��o de Dvo's registrados. 
	 * @return
	 */
	public Map<String, IDvo<?>> getDvos() throws DvoException;

	/**
	 * Indica uma refer�ncia ao gerenciador de servi�o que poder�
	 * ser usado pelos DVOs deste gerenciador
	 * para realizar suas valida��es utilizando a arquitetura de servi�os.
	 */
	public IEntityManager getEntityManager();
	
	
}