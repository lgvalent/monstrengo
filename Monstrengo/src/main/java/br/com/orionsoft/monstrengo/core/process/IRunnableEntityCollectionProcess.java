package br.com.orionsoft.monstrengo.core.process;

import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;

/**
 * Esta interface define os m�todos que um process deve ter para que
 * o gerenciador de vis�o consiga descobrir quais processos s�o compat�veis
 * (ou dependentes) de quais entidades. Possibilitando a gera��o de links nas telas
 * de visualiza��es para o acionamento direto de processos.
 *  
 * @author lucio
 * @version 20140306
 */
public interface IRunnableEntityCollectionProcess extends IProcess
{
	
	/**
     * Este m�todo possibilita que o processo seja invocado com base 
     * nos dados de uma lista de inst�ncia de uma entidade.<br>
     * Ao implementar este m�todo, os processos dever�o verificar o tipo
     * da entidade que foi fornecido e programar o seu comportamento de acordo
     * com a necessidade do programador.<br>
     * Caso o processo n�o seja compat�vel com a entidade fornecida, o mesmo
     * dever� gerar uma mensagem de incompatibilidade. 
     * 
     * @return
     */
	public boolean runWithEntities(IEntityCollection<?> entit);
}
