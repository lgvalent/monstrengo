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

import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;

/**
 * @hibernate.class table="security_right_crud"
 */
@Entity
@Table(name="security_right_crud")
public class RightCrud
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String CREATE_ALLOWED = "createAllowed";
    public static final String RETRIEVE_ALLOWED = "retrieveAllowed";
    public static final String UPDATE_ALLOWED = "updateAllowed";
    public static final String DELETE_ALLOWED = "deleteAllowed";
    public static final String QUERY_ALLOWED = "queryAllowed";

    public static final String APPLICATION_ENTITY = "applicationEntity";
    public static final String SECURITY_GROUP = "securityGroup";
    
    private long id = -1;
    private boolean createAllowed = false;
    private boolean retrieveAllowed = false;
    private boolean updateAllowed = false;
    private boolean deleteAllowed = false;
    private boolean queryAllowed = false;

    private ApplicationEntity applicationEntity = null;
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
    public boolean isCreateAllowed(){return createAllowed;}
    public void setCreateAllowed(boolean allowed){this.createAllowed = allowed;}
   
    /**
     * @hibernate.property
     */
    @Column
    public boolean isRetrieveAllowed(){return retrieveAllowed;}
    public void setRetrieveAllowed(boolean allowed){this.retrieveAllowed = allowed;}
    
    /**
     * @hibernate.property
     */
    @Column
    public boolean isUpdateAllowed(){return updateAllowed;}
    public void setUpdateAllowed(boolean allowed){this.updateAllowed = allowed;}

    /**
     * @hibernate.property
     */
    @Column
    public boolean isDeleteAllowed(){return deleteAllowed;}
    public void setDeleteAllowed(boolean allowed){this.deleteAllowed = allowed;}

    @Column
    public boolean isQueryAllowed(){return queryAllowed;}
    public void setQueryAllowed(boolean allowed){this.queryAllowed = allowed;}

    /**
     * Relacionamento UNIDIRECIONAL com a Entidade. 
     * A Entidade não conhece todos os direitos Crud sobre ela.
     * No entanto, um foreign-key é declarado para evitar que o hibernate crie-o 
     * por si só. Assim, declarado, o hibernate vai esperar que o outro lado crie.
     * Como é unidirecional, não existe o outro lado. Logo, o índice desnecessário
     * não será criado.
     * @hibernate.many-to-one cascade="save-update" foreign-key="applicationEntity"
     */
    @ManyToOne
    @JoinColumn(name="applicationEntity")
    @ForeignKey(name="applicationEntity")
    public ApplicationEntity getApplicationEntity(){return applicationEntity;}
    public void setApplicationEntity(ApplicationEntity applicationEntity){this.applicationEntity = applicationEntity;}
    
    /**
     * Relacionamento BIDIRECIONAL com o Grupo de Segurança. O grupo possui uma coleção
     * dos direitos ligados a ele, e cada direito sabe a qual Grupo pertence.
     * Nesta caso, a definição de qual chave estrangeira usar é importante
     * @hibernate.many-to-one foreign-key="securitygroup"
    */
    @ManyToOne
    @JoinColumn(name="securityGroup")
    @ForeignKey(name="securityGroup")
    public SecurityGroup getSecurityGroup(){return securityGroup;}
    public void setSecurityGroup(SecurityGroup group){this.securityGroup = group;}
    
    public String toString()
    {
        String result = "";
        
        result += this.createAllowed?"C":"_";
        result += this.retrieveAllowed?"R":"_";
        result += this.updateAllowed?"U":"_";
        result += this.deleteAllowed?"D":"_";
        result += this.queryAllowed?"Q":"_";

        if (this.applicationEntity != null)
            result += this.applicationEntity.toString();
        
        return result;
    }
}
