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
 * @hibernate.class table="security_entity_property"
 */
@Entity
@Table(name="security_entity_property")
public class ApplicationEntityProperty 
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String INDEX_PROPERTY = "indexProperty";
    public static final String INDEX_GROUP = "indexGroup";
    public static final String NAME = "name";
    public static final String LABEL = "label";
    public static final String HINT = "hint";
    public static final String DESCRIPTION = "description";
    public static final String REQUIRED = "required";
    public static final String READ_ONLY = "readOnly";
    public static final String VISIBLE = "visible";
    public static final String MAXIMUM = "maximum";
    public static final String MINIMUM = "minimum";
    public static final String DEFAULT_VALUE = "defaultValue";
    public static final String VALUES_LIST = "valuesList";
    public static final String EDIT_MASK = "editMask";
    public static final String COLOR_NAME = "colorName";
    public static final String DISPLAY_FORMAT = "displayFormat";
    public static final String EDIT_SHOW_LIST = "editShowList";
    public static final String ALLOW_SUB_QUERY = "allowSubQuery";
    public static final String APPLICATION_ENTITY = "applicationEntity";

    private long id = -1;
    private int indexProperty;
    private int indexGroup;
    private String name;
    private String label;
    private String hint;
    private String description;
    private boolean required;
    private boolean readOnly;
    private boolean visible;
    private double minimum;
    private double maximum;
    private String defaultValue;
    private String valuesList; 
    private String editMask;
    private String colorName;
    private String displayFormat;
    private boolean editShowList;
    private boolean allowSubQuery;
    
    private ApplicationEntity applicationEntity;
    

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
    public void setName(String name){this.name = name;}

    /**
     * @hibernate.property length="50"
     */
    @Column(length=100)
    public String getLabel(){return label;}
    public void setLabel(String label){this.label = label;}

    /**
     * @hibernate.property 
     */
    @Column
	public boolean isAllowSubQuery(){return allowSubQuery;}
	public void setAllowSubQuery(boolean allowSubQuery){this.allowSubQuery = allowSubQuery;}
	
    /**
     * @hibernate.property length="50"
     */
    @Column(length=50)
	public String getDefaultValue(){return defaultValue;}
	public void setDefaultValue(String defaultValue){this.defaultValue = defaultValue;}
	
	/**
     * @hibernate.property 
     * @hibernate.column name="description" sql-type="Text"
     */
    @Column(length=500, columnDefinition="text")
	public String getDescription(){return description;}
	public void setDescription(String description){this.description = description;}
	
	/**
     * @hibernate.property length="20"
     */
    @Column(length=50)
	public String getColorName(){return colorName;}
	public void setColorName(String colorName){this.colorName = colorName;}

	/**
     * @hibernate.property length="20"
     */
    @Column(length=50)
	public String getDisplayFormat(){return displayFormat;}
	public void setDisplayFormat(String displayFormat){this.displayFormat = displayFormat;}
	
    /**
     * @hibernate.property length="50" 
     */
    @Column(length=50)
	public String getEditMask(){return editMask;}
	public void setEditMask(String editMask){this.editMask = editMask;}
	
    /**
     * @hibernate.property 
     */
    @Column
	public boolean isEditShowList(){return editShowList;}
	public void setEditShowList(boolean editShowList){this.editShowList = editShowList;}
	
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
	public int getIndexProperty(){return indexProperty;}
	public void setIndexProperty(int index){this.indexProperty = index;}
	
    /**
     * @hibernate.property 
     */
	@Column
	public int getIndexGroup(){return indexGroup;}
	public void setIndexGroup(int indexGroup){this.indexGroup = indexGroup;}
	
    /**
     * @hibernate.property 
     */
	@Column
	public double getMaximum(){return maximum;}
	public void setMaximum(double maximum){this.maximum = maximum;}
	
    /**
     * @hibernate.property 
     */
	@Column
	public double getMinimum(){return minimum;}
	public void setMinimum(double minimum){this.minimum = minimum;}
	
    /**
     * @hibernate.property 
     */
    @Column
	public boolean isReadOnly(){return readOnly;}
	public void setReadOnly(boolean readOnly){this.readOnly = readOnly;}
	
    /**
     * @hibernate.property 
     */
    @Column
	public boolean isRequired(){return required;}
	public void setRequired(boolean required){this.required = required;}
	
    /**
     * @hibernate.property length="255"
     */
    @Column(length=255)
	public String getValuesList(){return valuesList;}
	public void setValuesList(String valuesList){this.valuesList = valuesList;}
	
    /**
     * @hibernate.property 
     */
    @Column
	public boolean isVisible(){return visible;}
	public void setVisible(boolean visible){this.visible = visible;}

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
        result +=this.label;
        return result;
    }

}

