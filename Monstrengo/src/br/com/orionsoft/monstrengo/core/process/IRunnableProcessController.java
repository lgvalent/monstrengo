package br.com.orionsoft.monstrengo.core.process;

import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.IProcessManager;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Esta interface é básica para todos os controladores de processos.
 * Esta interface define os métodos básico que um process deve ter para que
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
 * @version 20140306
 */
public interface IRunnableProcessController
{
	
    /**
     * Algumas vezes, apesar de uma entidade ser compatível com um processo, os atuais
     * dados da entidade podem impedir que um processo seja aplicado à ela.<br>
     * <b>Como por exemplo:</b> Um processo GerarMensalidadeProcess não pode ser
     * aplicado à um contrato que esteja inativo.<br>
     * Para executar um determinado processo é necessário que ele seja 
     * compatível com a entidade e que os dados atuais da entidade possibilitem 
     * sua execução. Assim, este método deve ser implementado por todo processo que 
     * analisa se uma entidade pode ou não ser utilizada por ele.
     * 
     * @return Se os atuais dados forem compatíveis com o proceso é retornado <b>true</b>, senão <b>false</b>. 
     */
	public boolean canRunWithEntity(IEntity<?> entity) throws ProcessException;
	
	/**
	 * Permite obter uma descrição do "Por que" que o método canRunWithEntity não pode
	 * ser executado. 
	 * @return
	 */
	public BusinessMessage getMessage();
	
	/**
	 * Verifica se tem mensagem na lista
	 */
	public boolean isHasMessage();
	
	/**
	 * Obtem uma referência do IProcessManager que gerencia este controller.
	 */
	public IProcessManager getProcessManager();
	public void setProcessManager(IProcessManager processManager);
	
	public Class<? extends IProcess> getProcessClass();
}
