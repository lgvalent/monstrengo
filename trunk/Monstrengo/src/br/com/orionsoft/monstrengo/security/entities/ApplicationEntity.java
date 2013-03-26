package br.com.orionsoft.monstrengo.security.entities;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.orionsoft.monstrengo.security.entities.ApplicationEntityProperty;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntityPropertyGroup;
import br.com.orionsoft.monstrengo.security.entities.ApplicationModule;

/**
 * @hibernate.class table="security_entity"
 */
@Entity
@Table(name="security_entity")
public class ApplicationEntity 
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String NAME = "name";
    public static final String LABEL = "label";
    public static final String CLASS_NAME = "className";
    public static final String HINT = "hint";
    public static final String DESCRIPTION = "description";
    public static final String COLOR_NAME = "colorName";
    public static final String RUN_QUERY_ON_OPEN = "runQueryOnOpen";
    public static final String APPLICATION_MODULE = "applicationModule";
    public static final String APPLICATION_ENTITY_PROPERTY = "applicationEntityProperty";
    public static final String APPLICATION_ENTITY_PROPERTY_GROUP = "applicationEntityPropertyGroup";

    private long id = -1;
    private String name;
    private String label;
    private String className;
    private String hint;
    private String description;
    private String colorName;
    private boolean runQueryOnOpen;
    private ApplicationModule applicationModule;
    private List<ApplicationEntityProperty> applicationEntityProperty = new ArrayList<ApplicationEntityProperty>();
    private List<ApplicationEntityPropertyGroup> applicationEntityPropertyGroup = new ArrayList<ApplicationEntityPropertyGroup>();

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
    @Column(length=100)
    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    /**
     * @hibernate.property length="100"
     */
    @Column(length=100)
    public String getLabel(){return label;}
    public void setLabel(String label){this.label = label;}

    /**
     * @hibernate.property length="200"
     */
    @Column(length=200)
	public String getClassName(){return className;}
	public void setClassName(String className){this.className = className;}

	/**
     * @hibernate.property 
     * @hibernate.column name="description" sql-type="Text"
     */
    @Column(length=500, columnDefinition="text")
	public String getDescription(){return description;}
	public void setDescription(String description){this.description = description;}
	
    @Column(length=50)
	public String getColorName(){return colorName;}
	public void setColorName(String colorName){this.colorName = colorName;}

    /**
     * @hibernate.property length="255"
     */
    @Column(length=255)
	public String getHint(){return hint;}
	public void setHint(String hint){this.hint = hint;}
	
    /**
     * @hibernate.property 
     */
    @Column
	public boolean isRunQueryOnOpen(){return runQueryOnOpen;}
	public void setRunQueryOnOpen(boolean runQueryOnOpen){this.runQueryOnOpen = runQueryOnOpen;}
	
    /**
     * @hibernate.many-to-one foreign-key="applicationModule"
     */
	@ManyToOne
	@JoinColumn(name="applicationModule")
	@ForeignKey(name="applicationModule")
    public ApplicationModule getApplicationModule(){return applicationModule;}
    public void setApplicationModule(ApplicationModule applicationModule){this.applicationModule = applicationModule;}
    
    /**
     * @hibernate.bag cascade="all" lazy="false" order-by="label" 
     * @hibernate.collection-key-column name="applicationEntity" index="applicationEntity"
     * @hibernate.collection-key foreign-key="applicationEntity"
     * @hibernate.collection-one-to-many class="br.com.orionsoft.monstrengo.security.entities.ApplicationEntityProperty"
     */
    @Transient //Lucio desativando metadados no bancos, pois ainnda está IMATURO e deixando lerdo os checkRightsCrud
    @OneToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name="applicationEntity") 
	@JoinColumn(name="applicationEntity")
    public List<ApplicationEntityProperty> getApplicationEntityProperty(){return applicationEntityProperty;}
    public void setApplicationEntityProperty(List<ApplicationEntityProperty> applicationEntityProperty){this.applicationEntityProperty = applicationEntityProperty;}
    
    /**
     * @hibernate.bag cascade="all" lazy="false" order-by="name"
     * @hibernate.collection-key-column name="applicationEntity" index="applicationEntity"
     * @hibernate.collection-key foreign-key="applicationEntity"
     * @hibernate.collection-one-to-many class="br.com.orionsoft.monstrengo.security.entities.ApplicationEntityPropertyGroup"
     */
    @Transient //Lucio desativando metadados no bancos, pois ainnda está IMATURO
    @OneToMany 
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name="applicationEntity") 
	@JoinColumn(name="applicationEntity")
	public List<ApplicationEntityPropertyGroup> getApplicationEntityPropertyGroup(){return applicationEntityPropertyGroup;}
	public void setApplicationEntityPropertyGroup(List<ApplicationEntityPropertyGroup> applicationEntityPropertyGroup){this.applicationEntityPropertyGroup = applicationEntityPropertyGroup;}

    public String toString()
    {
        String result = "";
        result +=this.label;
        return result;
    }
}
