package br.com.orionsoft.monstrengo.core.process;

import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Esta interface define os métodos que um process deve ter para que
 * o gerenciador de visão consiga descobrir quais processos são compatíveis
 * (ou dependentes) de quais entidades. Possibilitando a geração de links nas telas
 * de visualizações para o acionamento direto de processos.
 * TODO IMPLEMENTACAO Seria interessante que os processos tivessem algum método que 
 * verificasse.
 * TODO IMPLEMENTACAO Os processos CRUD poderia também utilizar esta interface. no entanto
 * eles seriam aplicáveis a todas as entidades. Exemplo: RUNNABLE_ENTITIES = {AllRunnableEntities.class}
 * Esta class AllRunnableEntities.class seria somente uma classe de parâmetro. :)  
 *  
 * @author lucio
 * @version 20070530
 */
public interface IRunnableEntityProcess extends IProcess
{
	
	/**
     * Este método possibilita que o processo seja invocado com base 
     * nos dados de uma determinada instância de uma entidade.<br>
     * Ao implementar este método, os processos deverão verificar o tipo
     * da entidade que foi fornecido e programar o seu comportamento de acordo
     * com a necessidade do programador.<br>
     * Caso o processo não seja compatível com a entidade fornecida, o mesmo
     * deverá gerar uma mensagem de incompatibilidade. 
     * 
     * @return
     */
	public boolean runWithEntity(IEntity<?> entity);

}
