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
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;

/**
 * @hibernate.class table="security_user"
 */
@Entity
@Table(name="security_user")
public class ApplicationUser 
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String NAME = "name";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String SECURITY_GROUPS = "securityGroups";
    public static final String INACTIVE = "inactive";

    private long id = -1;
    private String name;
    private String login;
    private String password;
    private boolean inactive=false;
    private Set<SecurityGroup> securityGroups = new HashSet<SecurityGroup>();
    
    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}
    
    /**
     * @hibernate.property length="50"
     * @return
     */
    @Column(length=50)
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    

    /**
     * @hibernate.property length="20" unique="true"
     * @return
     */
    @Column(length=20, unique=true)
    public String getLogin() {return login;}
    public void setLogin(String login) {this.login = login;}
    
    /**
     * @hibernate.property length="50"
     */
    @Column(length=50)
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    /**
     * @since 20060511
     * @hibernate.property
     */
    @Column
	public boolean isInactive() {return inactive;}
	public void setInactive(boolean inactive) {this.inactive = inactive;}

	/**
     * @hibernate.set table="security_user_group" cascade="save-update" lazy="false"
     * @hibernate.collection-key-column name="applicationUser" index="applicationUser"
     * @hibernate.collection-key foreign-key="applicationUser"
     * @hibernate.collection-many-to-many class="br.com.orionsoft.monstrengo.security.entities.SecurityGroup" column="securityGroup" foreign-key="securityGroup"
     */
    @ManyToMany @LazyCollection(LazyCollectionOption.FALSE)
    @Fetch(FetchMode.SELECT)
	@JoinTable(
    		name="security_user_group",
    	    joinColumns={@JoinColumn(name="applicationUser")},
    	    inverseJoinColumns={@JoinColumn(name="securityGroup")}
    		)
	@ForeignKey(name="applicationUser")
    public Set <SecurityGroup>getSecurityGroups() {return securityGroups;}
    public void setSecurityGroups(Set <SecurityGroup>securityGroups){this.securityGroups = securityGroups;}

    public String toString()
    {
        String result = "";
        result += this.name; 
        return result;
    }
 }
