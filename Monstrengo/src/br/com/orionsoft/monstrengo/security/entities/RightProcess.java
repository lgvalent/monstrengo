package br.com.orionsoft.monstrengo.security.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.monstrengo.security.entities.ApplicationProcess;
import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;
/**
 * @hibernate.class table="security_right_process"
 */
@Entity
@Table(name="security_right_process")
public class RightProcess
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String EXECUTE_ALLOWED = "executeAllowed";
    public static final String APPLICATION_PROCESS = "applicationProcess";
    public static final String SECURITY_GROUP = "securityGroup";

    private long id=-1;
    private boolean executeAllowed=false;
    private ApplicationProcess applicationProcess = null;
    private SecurityGroup securityGroup;

    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId(){return id;}
    public void setId(long id){this.id = id;}

    /**
     * @hibernate.property
     */
    @Column
    public boolean isExecuteAllowed(){return executeAllowed;}
    public void setExecuteAllowed(boolean allowed){this.executeAllowed = allowed;}
   
    /**
     * @hibernate.many-to-one cascade="save-update" foreign-key="applicationProcess"
     */
    @ManyToOne @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name="applicationProcess")
    @ForeignKey(name="applicationProcess")
    public ApplicationProcess getApplicationProcess(){return applicationProcess;}
    public void setApplicationProcess(ApplicationProcess applicationProcess){this.applicationProcess = applicationProcess;}
    
    /**
     * @hibernate.many-to-one foreign-key="securityGroup"
     */
    @ManyToOne
    @JoinColumn(name="securityGroup")
    @ForeignKey(name="securityGroup")
    public SecurityGroup getSecurityGroup(){return securityGroup;}
    public void setSecurityGroup(SecurityGroup group){this.securityGroup = group;}
    
    
    public String toString()
    {
        String result = "";
        result += this.executeAllowed?"[X]":"[_]";
        if (this.applicationProcess != null)
            result += this.applicationProcess.toString();
        return result;
    }
}
