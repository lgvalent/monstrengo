
package br.com.orionsoft.monstrengo.core.process;

import java.util.Date;

import br.com.orionsoft.monstrengo.core.process.IProcess;



/**
 * Esta interface define alguns dados de tempo de execução de um processo.
 * As informações aqui contidas são úteis na implementação de visualizadores
 * de processos que motram quais processos e porque eles estão sendo executados.
 * @author lucio
 *
 */
public interface IProcessInfo {

    public abstract IProcess getProcessOwner();

    /**
     * Armazena o id que identifica o processo na módulo de controle de
     * segurança.<br>
     * Os dados sobre todos os processos são persistidos pela entidade
     * ApplicationProcess.class.
     * Com este id é mais fácil recuperar os dados do processo persistido. 
     * @return
     */
    public abstract long getId();
    public abstract void setId(long id);
    
    
    /**
     * Indica se o processo foi finalizado. Ou seja, se uma chamada  
     * @return
     */
    public abstract boolean isFinished();

    public abstract boolean isStarted();

    public abstract Date getStartTime();

}