package br.com.orionsoft.monstrengo.core.process;

import br.com.orionsoft.monstrengo.core.process.IProcessManager;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcessController;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;


public abstract class RunnableProcessControllerBasic implements IRunnableProcessController
{
	private IProcessManager processManager;
    private BusinessMessage message;
    
    public IProcessManager getProcessManager(){return processManager;}
    public void setProcessManager(IProcessManager processManager){this.processManager = processManager;}
    
	public BusinessMessage getMessage(){return message;}
	protected void setMessage(BusinessMessage message){ this.message = message;}
	public boolean isHasMessage(){return message != null;}
	
}
