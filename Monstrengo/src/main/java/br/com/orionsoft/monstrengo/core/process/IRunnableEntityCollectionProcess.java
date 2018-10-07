package br.com.orionsoft.monstrengo.core.process;

import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;

/**
 * Esta interface define os métodos que um process deve ter para que
 * o gerenciador de visão consiga descobrir quais processos são compatíveis
 * (ou dependentes) de quais entidades. Possibilitando a geração de links nas telas
 * de visualizações para o acionamento direto de processos.
 *  
 * @author lucio
 * @version 20140306
 */
public interface IRunnableEntityCollectionProcess extends IProcess
{
	
	/**
     * Este método possibilita que o processo seja invocado com base 
     * nos dados de uma lista de instância de uma entidade.<br>
     * Ao implementar este método, os processos deverão verificar o tipo
     * da entidade que foi fornecido e programar o seu comportamento de acordo
     * com a necessidade do programador.<br>
     * Caso o processo não seja compatível com a entidade fornecida, o mesmo
     * deverá gerar uma mensagem de incompatibilidade. 
     * 
     * @return
     */
	public boolean runWithEntities(IEntityCollection<?> entit);
}
