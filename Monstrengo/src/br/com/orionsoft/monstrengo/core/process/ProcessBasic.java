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
     * Este m�todo pode ser chamado pelo usu�rio do processo ou ainda pelo 
     * m�todo disposeProcess do IProcessManager.<br>
     * Cuidado com a recorr�ncia ao alterar a implementa��o
     * deste m�todo ou do ProcessManager.disposeProcess().
     * CASO 1: Quando o m�todo IProcessManager.disposeProcess() � chamado, ele verifica se o processo
     * j� recebeu um sinal de finish. Se n�o recebeu, ent�o � enviado um sinal para 
     * o processo finalizar. O processo por sua vez, ir� executar suas a��es, marcar como finalizado
     * e solicitar ao seu gerenciador que remova ele da lista de processos ativos. Como o processo
     * j� est� marcado como finalizado, este m�todo simplesmente vai remov�-lo da lista de processos.<br>
     * CASO 2: Quando o m�todo IProcess.finish() � chamado, o processo � marcado como finished() e 
     * � solicitado ao gerenciador que o mesmo remova este processo de seu controle de processos ativos.
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
     * Realiza algumas prepara��es antes que o processo execute seus
     * m�todos run...().<br>
     * Importante, por exemplo, para limpar a lista de mensagem acumulada
     * para receber as novas mensagens ou n�o da nova execu��o. Se as mensagens
     * n�o forem limpas, pode haver uma ac�mulo incorreto de mensagens que
     * ser�o exibidas para o operador. 
     *
     */
    protected void beforeRun(){
    	/* Limpa as mensagens bufferizadas antes de uma execu��o */
    	this.messageList.clear();
    }

}
