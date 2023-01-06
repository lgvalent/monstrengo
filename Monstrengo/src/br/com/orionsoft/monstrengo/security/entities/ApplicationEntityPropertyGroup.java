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
 * @hibernate.class table="security_entity_property_group"
 */
@Entity
@Table(name="security_entity_property_group")
public class ApplicationEntityPropertyGroup 
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String INDEX_GROUP = "indexGroup";
    public static final String NAME = "name";
    public static final String LABEL = "label";
    public static final String HINT = "hint";
    public static final String DESCRIPTION = "description";
    public static final String COLOR_NAME = "colorName";
    public static final String APPLICATION_ENTITY = "applicationEntity";

    private long id = -1;
    private int indexGroup;
    private String name;
    private String label;
    private String hint;
    private String description;
    private String colorName;

	private ApplicationEntity applicationEntity;

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
    public int getIndexGroup(){return indexGroup;}
    public void setIndexGroup(int indexGroup){this.indexGroup = indexGroup;}
    
    /**
     * @hibernate.property length="100"
     */
    @Column(length=100)
    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    @Column(length=100)
    public String getLabel() {return label;}
	public void setLabel(String label) {this.label = label;}
	
    @Column(length=255)
	public String getHint() {return hint;}
	public void setHint(String hint) {this.hint = hint;}

    @Column(length=500, columnDefinition="text")
	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}

    @Column(length=50)
	public String getColorName(){return colorName;}
	public void setColorName(String colorName){this.colorName = colorName;}

    /**
     * @hibernate.many-to-one foreign-key="applicationEntity"
     */
    @ManyToOne
    @JoinColumn(name="applicationEntity")
    @ForeignKey(name="applicationEntity")
    public ApplicationEntity getApplicationEntity(){return applicationEntity;}
    public void setApplicationEntity(ApplicationEntity applicationEntity){this.applicationEntity = applicationEntity;}
    
	public String toString()
    {
        String result = "";
        result +=this.name;
        return result;
    }
}
