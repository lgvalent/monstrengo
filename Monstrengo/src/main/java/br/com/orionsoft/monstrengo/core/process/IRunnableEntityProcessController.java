package br.com.orionsoft.monstrengo.core.process;


/**
 * Esta interface especializa os controladores de processo para serem executados
 * a partir de uma �nica entidade.
 * @author lucio
 * @version 20140306
 */
public interface IRunnableEntityProcessController extends IRunnableProcessController
{
	
	/**
     * Obtem uma lista de classes que s�o as
     * entidades compat�veis com o processo.<br>
     * Estas entidades s�o pesquisadas pelo gerenciador
     * de metadados para armazenar quais entidades
     * s�o suportadas por quais processos.
     * @return
     */
	public Class<?>[] getRunnableEntities();
	
}
