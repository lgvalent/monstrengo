
package br.com.orionsoft.monstrengo.core.process;

import java.util.Date;

import br.com.orionsoft.monstrengo.core.process.IProcess;



/**
 * Esta interface define alguns dados de tempo de execu��o de um processo.
 * As informa��es aqui contidas s�o �teis na implementa��o de visualizadores
 * de processos que motram quais processos e porque eles est�o sendo executados.
 * @author lucio
 *
 */
public interface IProcessInfo {

    public abstract IProcess getProcessOwner();

    /**
     * Armazena o id que identifica o processo na m�dulo de controle de
     * seguran�a.<br>
     * Os dados sobre todos os processos s�o persistidos pela entidade
     * ApplicationProcess.class.
     * Com este id � mais f�cil recuperar os dados do processo persistido. 
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