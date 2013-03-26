package br.com.orionsoft.monstrengo.crud.labels.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;


/**
 * Esta classe é o item da lista de etiquetas de endereçamento
 * que estão prontas para serem impressas. 
 * 
 * @hibernate.class table="framework_label_model_entity"
 */
@Entity
@Table(name="framework_label_model_entity")
public class ModelLabelEntity {
	
	public static final String APPLICATION_ENTITY = "applicationEntity";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String LINE1 = "line1";
	public static final String LINE2 = "line2";
	public static final String LINE3 = "line3";
	public static final String LINE4 = "line4";
	public static final String LINE5 = "line5";

	/* Propriedades de um campo */
	private long id = IDAO.ENTITY_UNSAVED;
	
	private ApplicationEntity applicationEntity; 
	private String name;
	private String description;
	private String line1;
	private String line2;
	private String line3;
	private String line4;
	private String line5;
	

    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId(){return id;}
	public void setId(long id){this.id = id;}
	
    /**
     * @hibernate.many-to-one foreign-key="applicationEntity"
     */
	@ManyToOne
	@JoinColumn(name="applicationEntity")
	@ForeignKey(name="applicationEntity")
	public ApplicationEntity getApplicationEntity(){return applicationEntity;}
	public void setApplicationEntity(ApplicationEntity applicationEntity){this.applicationEntity = applicationEntity;}

	/**
     * @hibernate.property length="255"
     */
	@Column(length=255)
	public String getDescription(){return description;}
	public void setDescription(String description){this.description = description;}
	
	/**
     * @hibernate.property length="50"
     */
	@Column(length=50)
	public String getName(){return name;}
	public void setName(String name){this.name = name;}
	
	/**
     * @hibernate.property length="255"
     */
	@Column(length=255)
	public String getLine1(){return line1;}
	public void setLine1(String line1){this.line1 = line1;}
	
    /**
     * @hibernate.property length="255"
     */
	@Column(length=255)
	public String getLine2(){return line2;}
	public void setLine2(String line2){this.line2 = line2;}
	
    /**
     * @hibernate.property length="255"
     */
	@Column(length=255)
	public String getLine3(){return line3;}
	public void setLine3(String line3){this.line3 = line3;}
	
    /**
     * @hibernate.property length="255"
     */
	@Column(length=255)
	public String getLine4(){return line4;}
	public void setLine4(String line4){this.line4 = line4;}
	
    /**
     * @hibernate.property length="255"
     */
	@Column(length=255)
	public String getLine5(){return line5;}
	public void setLine5(String line5){this.line5 = line5;}
	
	public String toString(){
		return this.name;
	}
	
}
