package br.com.orionsoft.monstrengo.core.process;

import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Esta interface define os m�todos que um process deve ter para que
 * o gerenciador de vis�o consiga descobrir quais processos s�o compat�veis
 * (ou dependentes) de quais entidades. Possibilitando a gera��o de links nas telas
 * de visualiza��es para o acionamento direto de processos.
 * TODO IMPLEMENTACAO Seria interessante que os processos tivessem algum m�todo que 
 * verificasse.
 * TODO IMPLEMENTACAO Os processos CRUD poderia tamb�m utilizar esta interface. no entanto
 * eles seriam aplic�veis a todas as entidades. Exemplo: RUNNABLE_ENTITIES = {AllRunnableEntities.class}
 * Esta class AllRunnableEntities.class seria somente uma classe de par�metro. :)  
 *  
 * @author lucio
 * @version 20070530
 */
public interface IRunnableEntityProcess extends IProcess
{
	
	/**
     * Este m�todo possibilita que o processo seja invocado com base 
     * nos dados de uma determinada inst�ncia de uma entidade.<br>
     * Ao implementar este m�todo, os processos dever�o verificar o tipo
     * da entidade que foi fornecido e programar o seu comportamento de acordo
     * com a necessidade do programador.<br>
     * Caso o processo n�o seja compat�vel com a entidade fornecida, o mesmo
     * dever� gerar uma mensagem de incompatibilidade. 
     * 
     * @return
     */
	public boolean runWithEntity(IEntity<?> entity);

}
