package br.com.orionsoft.monstrengo.security.entities;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.orionsoft.monstrengo.security.entities.ApplicationProcess;

/**
 * @hibernate.class table="security_module"
 */
@Entity
@Table(name="security_module")
public class ApplicationModule 
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String NAME = "name";
    public static final String PROCESSES = "processes";
    public static final String ENTITIES = "entities";

    private long id = -1;
    private String name;
    public Set<ApplicationProcess> processes = new HashSet<ApplicationProcess>();
    public Set<ApplicationEntity> entities = new HashSet<ApplicationEntity>();

    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId(){return id;}
    public void setId(long id){this.id = id;}
    
    /**
     * @hibernate.property length="50"
     */
    @Column(length=50)
    public String getName(){return name;}
    public void setName(String nome){this.name = nome;}
    
    /**
     * O módulo se relaciona com vários processos. Para isto, cada
     * processo receberá uma coluna "applicationModule" que identifica 
     * seu módulo.
     * 
     * @hibernate.set cascade="all" lazy="false" 
     * @hibernate.collection-key-column name="applicationModule" index="applicationModule"
     * @hibernate.collection-key foreign-key="applicationModule"
     * @hibernate.collection-one-to-many class="br.com.orionsoft.monstrengo.security.entities.ApplicationProcess"
     */
    @OneToMany(cascade=CascadeType.ALL) @LazyCollection(LazyCollectionOption.FALSE)
    @ForeignKey(name="applicationModule") 
    @JoinColumn(name="applicationModule")
    public Set<ApplicationProcess> getProcesses() {return processes;}
    public void setProcesses(Set<ApplicationProcess> processes) {this.processes = processes;}
    
    /**
     * O módulo se relaciona com várias entidades. Para isto, cada
     * entidade receberá uma coluna "applicationModule" que identifica 
     * seu módulo.
     * 
     * @hibernate.set cascade="all" lazy="false" 
     * @hibernate.collection-key-column name="applicationModule" index="applicationModule"
     * @hibernate.collection-key foreign-key="applicationModule"
     * @hibernate.collection-one-to-many class="br.com.orionsoft.monstrengo.security.entities.ApplicationEntity"
     */
    @OneToMany(cascade=CascadeType.ALL) @LazyCollection(LazyCollectionOption.FALSE)
    @ForeignKey(name="applicationModule")
    @JoinColumn(name="applicationModule")
    public Set<ApplicationEntity> getEntities() {return entities;}
    public void setEntities(Set<ApplicationEntity> entities) {this.entities = entities;}
    
    public String toString()
    {
        String result = "";
        result += this.name;
        return result;
    }
 }
