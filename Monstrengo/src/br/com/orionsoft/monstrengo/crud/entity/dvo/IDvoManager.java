package br.com.orionsoft.monstrengo.crud.entity.dvo;

import java.util.Map;

import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.crud.entity.dvo.IDvo;
import br.com.orionsoft.monstrengo.core.IManager;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;

/**
 * Gerenciador que mantém o registro de todos os Dvos das entidades existentes e 
 * controla transações. Existe uma única instância desse gerenciador
 * na aplicação e todos os Dvos possuem referência e são executados por ele.
 *  
 * @author Sergio
 * @version 20070523
 */
public interface IDvoManager extends IManager{
	
	/**
	 * Cada IDvo será registrado na lista interna de IDvo's disponíveis.<br>
	 * Este método registra um Objeto do tipo IDvo que é passado como argumento.<br>
	 * Uma mesma entidade poderá ter mais de um IDvo registrado. Assim, o gerenciador
	 * irá utilizar todos os IDvo's de uma entidade. E uma entidade só será considerada
	 * validada se todos seus IDvo's validaram a mesma.   
	 */
	public <T> void registerDvo(IDvo<T> dvo) throws DvoException;
	
	/**
	 * Cada IDvo será registrado na lista interna de IDvo's disponíveis.<br>
	 * Este método registra um Objeto do tipo IDvo que é passado como argumento.<br>
	 * Uma mesma entidade poderá ter mais de um IDvo registrado. Assim, o gerenciador
	 * irá utilizar todos os IDvo's de uma entidade. E uma entidade só será considerada
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
	 * Obtem a coleção de Dvo's registrados. 
	 * @return
	 */
	public Map<String, IDvo<?>> getDvos() throws DvoException;

	/**
	 * Indica uma referência ao gerenciador de serviço que poderá
	 * ser usado pelos DVOs deste gerenciador
	 * para realizar suas validações utilizando a arquitetura de serviços.
	 */
	public IEntityManager getEntityManager();
	
	
}