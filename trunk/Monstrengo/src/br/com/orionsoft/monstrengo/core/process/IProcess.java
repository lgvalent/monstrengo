package br.com.orionsoft.monstrengo.core.process;


import br.com.orionsoft.monstrengo.core.process.IProcessInfo;
import br.com.orionsoft.monstrengo.core.process.IProcessManager;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.security.entities.UserSession;

public interface IProcess
{
    /**
     * Nome constante que identifica univocamente o processo
     * dentro do sistema. 
     * @return
     */
	public String getProcessName();
    
    /**
     * Identificador atribu�do pelo gerenciador para cada inst�ncia de processo
     * criada.
     * 
     * @return
     */
	public long getPid();
    
    /**
     * Sinal de inicializa��o do processo. <br>
     * Este m�todo � �tilizado para que o processo realize algumas atividades
     * durante sua inicializa��o. Como por exemplo: ao iniciar o processo precisa
     * iniciar algumas listas, ou propriedades de acordo com o n�vel do operador que
     * o invocou. Este tipo de inicializa��o n�o poderia ser realizado no construtor
     * do processo, pois neste momento ele ainda n�o se encontra integrado com a arquitetura
     * do sistema, ou seja, suas dep�ndencias ainda n�o foram injetadas.
     */
	public void start() throws ProcessException;
    
	/**
	 * M�todo de finaliza��o do processo.<br>
	 * �til para realizar algumas limpezas de vari�veis ou listas.
     * 
     * TODO CORRE��O Remover da interface este throws. O problema que ele influencia
     * TODOS os m�todos .finish() que atualmente est�o com try{} 
	 */
	public void finish() throws ProcessException;
    
    /**
     * Armazena as mensagens que s�o produzidas pelo m�todos run() do processo.
     * Podem ser mensagens de informa��es ou ainda de erros que ocorreram durante a
     * execu��o. 
     * @return
     */
	public MessageList getMessageList();
    
    public IProcessManager getProcessManager();
    public void setProcessManager(IProcessManager ProcessManager);
    
    /**
     * Armazena alguns dados de tempo de execu��o do processo. 
     * @return
     */
    public IProcessInfo getProcessInfo();

    /**
     * Referencia a sess�o do operador que invocou o processo.
     * �til para obter os dados e diretiso do operador.
     * @return
     */
    public UserSession getUserSession();
    
}
