package br.com.orionsoft.monstrengo.core.process;

import java.util.Calendar;

import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.IProcessInfo;
import br.com.orionsoft.monstrengo.core.process.IProcessManager;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.ProcessInfo;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.security.entities.UserSession;

public abstract class ProcessBasic implements IProcess
{
    protected Logger log = Logger.getLogger(this.getClass());
    
    private long pid;
    
    private IProcessManager processManager;
    private ProcessInfo processInfo = new ProcessInfo(this);
    private MessageList messageList = new MessageList();
    private UserSession userSession;
    
    public long getPid()
    {
        return pid;
    }
    
    public void setPid(long pid)
    {
        this.pid = pid;
    }

    public IProcessInfo getProcessInfo()
    {
        return processInfo;
    }
    

    public void start() throws ProcessException 
    {
        // Define o processo como INICIADO
        processInfo.setStarted(true);
        processInfo.setStartTime(Calendar.getInstance().getTime());
        
    }

    /**
     * Este método pode ser chamado pelo usuário do processo ou ainda pelo 
     * método disposeProcess do IProcessManager.<br>
     * Cuidado com a recorrência ao alterar a implementação
     * deste método ou do ProcessManager.disposeProcess().
     * CASO 1: Quando o método IProcessManager.disposeProcess() é chamado, ele verifica se o processo
     * já recebeu um sinal de finish. Se não recebeu, então é enviado um sinal para 
     * o processo finalizar. O processo por sua vez, irá executar suas ações, marcar como finalizado
     * e solicitar ao seu gerenciador que remova ele da lista de processos ativos. Como o processo
     * já está marcado como finalizado, este método simplesmente vai removê-lo da lista de processos.<br>
     * CASO 2: Quando o método IProcess.finish() é chamado, o processo é marcado como finished() e 
     * é solicitado ao gerenciador que o mesmo remova este processo de seu controle de processos ativos.
     *     
     */
    public void finish() throws ProcessException
    {
    	// Define o processo como FINALIZADO
        processInfo.setFinished(true);
        
    }

    public MessageList getMessageList()
    {
        return messageList;
    }

    public IProcessManager getProcessManager()
    {
        return processManager;
    }
    

    public void setProcessManager(IProcessManager processManager)
    {
        this.processManager = processManager;
    }
    
    public void setUserSession(UserSession userSession) 
    {
        this.userSession = userSession;
    }
    public UserSession getUserSession()
    {
        return userSession;
    }
    
    /**
     * Realiza algumas preparações antes que o processo execute seus
     * métodos run...().<br>
     * Importante, por exemplo, para limpar a lista de mensagem acumulada
     * para receber as novas mensagens ou não da nova execução. Se as mensagens
     * não forem limpas, pode haver uma acúmulo incorreto de mensagens que
     * serão exibidas para o operador. 
     *
     */
    protected void beforeRun(){
    	/* Limpa as mensagens bufferizadas antes de uma execução */
    	this.messageList.clear();
    }

}
