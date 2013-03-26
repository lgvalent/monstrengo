package br.com.orionsoft.monstrengo.auditorship.entities;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.monstrengo.auditorship.entities.AuditCrudRegister;
import br.com.orionsoft.monstrengo.auditorship.entities.AuditProcessRegister;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;


/**
 * @author Lucio 06/10/2005
 * @version 2005/12/22
 */
@MappedSuperclass
public abstract class AuditRegister
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String APPLICATION_USER = "applicationUser";
    public static final String DESCRIPTION = "description";
    public static final String TERMINAL = "terminal";
    public static final String OCURRENCY_DATE = "ocurrencyDate";
    
    protected long id = -1;
    protected String description;
    protected String terminal;

    protected Calendar ocurrencyDate;
    protected ApplicationUser applicationUser;
    
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
    public Calendar getOcurrencyDate()
    {
        return ocurrencyDate;
    }

    public void setOcurrencyDate(Calendar date)
    {
        this.ocurrencyDate = date;
    }
    
    /**
     * @hibernate.property 
     */
    @Column(length=255)
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
    
    /**
     * @hibernate.many-to-one foreign-key="applicationUser"
     */
    @ManyToOne
    @JoinColumn(name="applicationUser")
    @ForeignKey(name="applicationUser")
    public ApplicationUser getApplicationUser()
    {
        return applicationUser;
    }
    
    public void setApplicationUser(ApplicationUser user)
    {
        this.applicationUser = user;
    }

    /**
     * @hibernate.property 
     */
    @Column
    public String getTerminal()
    {
        return terminal;
    }
    public void setTerminal(String terminal)
    {
        this.terminal = terminal;
    }
    
    public String toString()
    {
        String result = "";
        result += CalendarUtils.formatDateTime(ocurrencyDate) + " " + terminal + "-" + (applicationUser!=null?applicationUser.getLogin():"(noUser)") + ":" ; 
        return result;

    }
    
    /**
     * Propriedade não persistida que indica que o tipo de registro atualmente
     * instanciada é AuditCrudRegister.
     */
    @Transient
    public boolean isCrudRegister() {
        return (this.getClass() == AuditCrudRegister.class);
    }

    /**
     * Propriedade não persistida.
     * 
     * @return Se a pessoa for do tipo AuditCrudRegister, retorna uma instância de
     *         AuditCrudRegister, senão retorna null.
     */
    @Transient
    public AuditCrudRegister getAsCrudRegister() {
        if (isCrudRegister())
            return AuditCrudRegister.class.cast(this);
        return null;
    }


    /**
     * Propriedade não persistida que indica que o tipo de registro atualmente
     * instanciada é AuditProcessRegister.
     */
    @Transient
    public boolean isProcessRegister() {
        return (this.getClass() == AuditProcessRegister.class);
    }

    /**
     * Propriedade não persistida.
     * 
     * @return Se a pessoa for do tipo AuditProcessRegister, retorna uma instância de
     *         AuditProcessRegister, senão retorna null.
     */
    @Transient
    public AuditProcessRegister getAsProcessRegister() {
        if (isProcessRegister())
            return AuditProcessRegister.class.cast(this);
        return null;
    }


}
