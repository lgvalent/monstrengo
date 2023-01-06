package br.com.orionsoft.monstrengo.core.process;


/**
 * Esta interface especializa os controladores de processo para serem executados
 * a partir de uma única entidade.
 * @author lucio
 * @version 20140306
 */
public interface IRunnableEntityProcessController extends IRunnableProcessController
{
	
	/**
     * Obtem uma lista de classes que são as
     * entidades compatíveis com o processo.<br>
     * Estas entidades são pesquisadas pelo gerenciador
     * de metadados para armazenar quais entidades
     * são suportadas por quais processos.
     * @return
     */
	public Class<?>[] getRunnableEntities();
	
}
