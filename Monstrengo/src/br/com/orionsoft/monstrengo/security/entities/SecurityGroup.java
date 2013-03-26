package br.com.orionsoft.monstrengo.security.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.RightCrud;
import br.com.orionsoft.monstrengo.security.entities.RightProcess;

/**
 * @hibernate.class table="security_group"
 */
@Entity
@Table(name="security_group")
public class SecurityGroup 
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    /** String contendo o nome do grupo*/
    public static final String NAME = "name"; 
    /** Collection de elementos do tipo user pertencentes ao grupo*/
    public static final String USERS = "users";
    /**Collection de elementos do tipo rightProcess pertencentes ao grupo*/
    public static final String RIGHTS_PROCESS = "rightsProcess";
    /**Collection de elementos do tipo rightCrud pertencentes ao grupo*/
    public static final String RIGHTS_CRUD = "rightsCrud";

    private long id = -1;
    private String name;
    private Set<ApplicationUser> users = new HashSet<ApplicationUser>();
    private Set<RightProcess> rightsProcess = new HashSet<RightProcess>();
    private Set<RightCrud> rightsCrud = new HashSet<RightCrud>();
    

	/**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}
    
    /**
     * @hibernate.property length="50"
     */
    @Column(length=50)
    public String getName() {return name;}
    public void setName(String nome) {this.name = nome;}
    
    /**
     * @hibernate.set table="security_user_group" cascade="save-update"
     * @hibernate.collection-key-column name="securityGroup" index="securityGroup"
     * @hibernate.collection-key foreign-key="securityGroup"
     * @hibernate.collection-many-to-many class="br.com.orionsoft.monstrengo.security.entities.ApplicationUser" column="applicationUser" foreign-key="applicationUser"
     */
    @ManyToMany @Cascade(CascadeType.SAVE_UPDATE)
    @LazyCollection(LazyCollectionOption.FALSE)
    @OrderBy(ApplicationUser.NAME)
    @JoinTable(
    		name="security_user_group",
    	    joinColumns={@JoinColumn(name="securityGroup")},
    	    inverseJoinColumns={@JoinColumn(name="applicationUser")}
    		)
	@ForeignKey(name="applicationGroup")
	public Set <ApplicationUser>getUsers() {return users;}
    public void setUsers(Set <ApplicationUser>operadores)
    {
        this.users = operadores;
    }

    /**
     * @hibernate.set lazy="false"
     * @hibernate.collection-key-column index="securityGroup" name="securityGroup"  
     * @hibernate.collection-key foreign-key="securityGroup" 
     * @hibernate.collection-one-to-many class="br.com.orionsoft.monstrengo.security.entities.RightProcess"
     */
    @OneToMany @LazyCollection(LazyCollectionOption.FALSE)
    @ForeignKey(name=RightProcess.SECURITY_GROUP) 
    @JoinColumn(name=RightProcess.SECURITY_GROUP)
    public Set <RightProcess>getRightsProcess() {return rightsProcess;}
    public void setRightsProcess(Set <RightProcess>rightsProcess) {this.rightsProcess = rightsProcess;}
    
    /**
     * @hibernate.set  lazy="false" 
     * @hibernate.collection-key-column index="securityGroup" name="securityGroup"  
     * @hibernate.collection-key foreign-key="securityGroup"  
     * @hibernate.collection-one-to-many class="br.com.orionsoft.monstrengo.security.entities.RightCrud" 
     */
    @OneToMany @LazyCollection(LazyCollectionOption.FALSE)
    @ForeignKey(name=RightCrud.SECURITY_GROUP) 
    @JoinColumn(name=RightCrud.SECURITY_GROUP)
    public Set <RightCrud>getRightsCrud() {return rightsCrud;}
    public void setRightsCrud(Set<RightCrud> rightsCrud) {this.rightsCrud = rightsCrud;}

    public String toString()
    {
        String result = "";
        result += this.name; 
        return result;
    }
}
