package br.com.orionsoft.monstrengo.core.process;

import java.util.Date;

import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.IProcessInfo;

/**
 * Esta classe armazena diversas informações sobre um processo.
 * Estas informações podem ser usadas pela interface quanto pelo
 * gerenciador de processo
 * 
 * @author Lucio
 *
 */public class ProcessInfo implements IProcessInfo
{
    private long id=-1;
    private IProcess processOwner;
     
    private boolean finished = false;
    private boolean started = false;
    
    private Date startTime;

    public ProcessInfo(IProcess processOwner)
    {
        this.processOwner = processOwner;
        
    }
    
    public boolean isFinished(){return finished;}
    public void setFinished(boolean finished){this.finished=finished;}
    
    public boolean isStarted(){return started;}
    public void setStarted(boolean started){this.started=started;}
     
    public Date getStartTime(){return startTime;}
    public void setStartTime(Date startTime){this.startTime = startTime;}

    public long getId(){return id;}
    public void setId(long id){this.id = id;}

    public IProcess getProcessOwner(){return processOwner;}

}
