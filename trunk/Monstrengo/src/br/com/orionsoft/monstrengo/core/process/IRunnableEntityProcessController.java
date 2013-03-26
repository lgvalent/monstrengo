package br.com.orionsoft.monstrengo.core.process;

import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.IProcessManager;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
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
public interface IRunnableEntityProcessController
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
    
    /**
     * Algumas vezes, apesar de uma entidade ser compat�vel com um processo, os atuais
     * dados da entidade podem impedir que um processo seja aplicado � ela.<br>
     * <b>Como por exemplo:</b> Um processo GerarMensalidadeProcess n�o pode ser
     * aplicado � um contrato que esteja inativo.<br>
     * Para executar um determinado processo � necess�rio que ele seja 
     * compat�vel com a entidade e que os dados atuais da entidade possibilitem 
     * sua execu��o. Assim, este m�todo deve ser implementado por todo processo que 
     * analisa se uma entidade pode ou n�o ser utilizada por ele.
     * 
     * @return Se os atuais dados forem compat�veis com o proceso � retornado <b>true</b>, sen�o <b>false</b>. 
     */
	public boolean canRunWithEntity(IEntity<?> entity) throws ProcessException;
	
	/**
	 * Permite obter uma descri��o do "Por que" que o m�todo canRunWithEntity n�o pode
	 * ser executado. 
	 * @return
	 */
	public BusinessMessage getMessage();
	
	/**
	 * Verifica se tem mensagem na lista
	 */
	public boolean isHasMessage();
	
	/**
	 * Obtem uma refer�ncia do IProcessManager que gerencia este controller.
	 */
	public IProcessManager getProcessManager();
	public void setProcessManager(IProcessManager processManager);
	
	public Class<? extends IProcess> getProcessClass();
}
