package br.com.orionsoft.monstrengo.auditorship.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditRegister;
import br.com.orionsoft.monstrengo.security.entities.ApplicationProcess;


/**
 * @author Lucio 06/10/2005
 * @version 06/10/2005
 * @hibernate.class table="auditorship_process"
 */
@Entity
@Table(name="auditorship_process")
public class AuditProcessRegister extends AuditRegister
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String APPLICATION_PROCESS = "applicationProcess";
    
    private ApplicationProcess applicationProcess;
    
    /**
     * @hibernate.many-to-one foreign-key="applicationProcess" 
     */
    @ManyToOne
    @JoinColumn(name="applicationProcess")
    @ForeignKey(name="applicationProcess")
    public ApplicationProcess getApplicationProcess()
    {
        return applicationProcess;
    }
    
    public void setApplicationProcess(ApplicationProcess applicationProcess)
    {
        this.applicationProcess = applicationProcess;
    }
    
    
    public String toString()
    {
        String result = "";
        result += super.toString() + this.applicationProcess.toString() + " [" + super.description + "]"; 
        return result;

    }
}
