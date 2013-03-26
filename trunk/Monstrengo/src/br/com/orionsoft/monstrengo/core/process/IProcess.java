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
     * Identificador atribuído pelo gerenciador para cada instância de processo
     * criada.
     * 
     * @return
     */
	public long getPid();
    
    /**
     * Sinal de inicialização do processo. <br>
     * Este método é útilizado para que o processo realize algumas atividades
     * durante sua inicialização. Como por exemplo: ao iniciar o processo precisa
     * iniciar algumas listas, ou propriedades de acordo com o nível do operador que
     * o invocou. Este tipo de inicialização não poderia ser realizado no construtor
     * do processo, pois neste momento ele ainda não se encontra integrado com a arquitetura
     * do sistema, ou seja, suas depêndencias ainda não foram injetadas.
     */
	public void start() throws ProcessException;
    
	/**
	 * Método de finalização do processo.<br>
	 * Ùtil para realizar algumas limpezas de variáveis ou listas.
     * 
     * TODO CORREÇÂO Remover da interface este throws. O problema que ele influencia
     * TODOS os métodos .finish() que atualmente estão com try{} 
	 */
	public void finish() throws ProcessException;
    
    /**
     * Armazena as mensagens que são produzidas pelo métodos run() do processo.
     * Podem ser mensagens de informações ou ainda de erros que ocorreram durante a
     * execução. 
     * @return
     */
	public MessageList getMessageList();
    
    public IProcessManager getProcessManager();
    public void setProcessManager(IProcessManager ProcessManager);
    
    /**
     * Armazena alguns dados de tempo de execução do processo. 
     * @return
     */
    public IProcessInfo getProcessInfo();

    /**
     * Referencia a sessão do operador que invocou o processo.
     * Útil para obter os dados e diretiso do operador.
     * @return
     */
    public UserSession getUserSession();
    
}
