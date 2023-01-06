package br.com.orionsoft.monstrengo.security.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
/**
 * @hibernate.class table="security_process"
 */
@Entity
@Table(name="security_process")
public class ApplicationProcess
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    /**
     * Nome do Processo. Variável do tipo String
     */
    public static final String NAME = "name";
    public static final String LABEL = "label";
    public static final String HINT = "hint";
    public static final String DESCRIPTION = "description";

    /**
     * Módulo ao qual o processo pertence. Variável do tipo br.com.orionsoft.monstrengo.security.entities.ApplicationModule
     */
    public static final String APPLICATION_MODULE = "applicationModule";

    private long id = -1;
    private String name;
    private String label;
    private String hint;
    private String description;

    private ApplicationModule applicationModule;

    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId(){return id;}
    public void setId(long id){this.id = id;}

    /**
     * @hibernate.property length="100"
     */
    @Column(length=50)
    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    /**
     * @hibernate.property length="100"
     */
    @Column(length=100)
    public String getLabel(){return label;}
    public void setLabel(String label){this.label = label;}

    /**
     * @hibernate.property length="255"
     */
    @Column(length=255)
	public String getHint(){return hint;}
	public void setHint(String hint){this.hint = hint;}

	/**
     * @hibernate.property
     * @hibernate.column name="description" sql-type="Text"
     */
	@Column(length=500, columnDefinition="Text")
	public String getDescription(){return description;}
	public void setDescription(String description){this.description = description;}

    /**
     * @hibernate.many-to-one foreign-key="applicationModule"
     */
    @ManyToOne
    @JoinColumn(name=APPLICATION_MODULE)
    @ForeignKey(name=APPLICATION_MODULE)
    public ApplicationModule getApplicationModule(){return applicationModule;}
    public void setApplicationModule(ApplicationModule service){this.applicationModule = service;}

    public String toString()
    {
        String result = "";
        if (this.applicationModule != null)
            result +=this.applicationModule.toString() + "-";
        result +=this.name;
        return result;
    }
 }
