package br.com.orionsoft.monstrengo.crud.documents.entities;

import java.util.Calendar;

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
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;


/**
 * Esta classe é o item da lista de etiquetas de endereçamento
 * que estão prontas para serem impressas. 
 * 
 * @hibernate.class table="framework_document_model_entity"
 */
@Entity
@Table(name="framework_document_model_entity")
public class ModelDocumentEntity {
	
	public static final String APPLICATION_USER = "applicationUser";
	public static final String APPLICATION_ENTITY = "applicationEntity";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
    public static final String DATE = "date";
	public static final String SOURCE = "source";
	
	/* Propriedades de um campo */
	private long id = IDAO.ENTITY_UNSAVED;
	
	private ApplicationUser applicationUser; 
	private ApplicationEntity applicationEntity; 
	private String name;
	private String description;
    private Calendar date;
	private String source;
	

    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId(){return id;}
	public void setId(long id){this.id = id;}
	
    /**
     * @hibernate.many-to-one foreign-key="applicationUser"
     */
	@ManyToOne
	@JoinColumn(name="applicationUser")
	@ForeignKey(name="applicationUser")
	public ApplicationUser getApplicationUser(){return applicationUser;}
	public void setApplicationUser(ApplicationUser applicationUser){this.applicationUser = applicationUser;}
	
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
     * @hibernate.property
     */
	@Column
	public Calendar getDate() {return date;}
	public void setDate(Calendar date) {this.date = date;}
	
	/**
     * @hibernate.property 
     * @hibernate.column name="source" sql-type="Text"
     */
	@Column(name="source", columnDefinition="Text")
	public String getSource(){return source;}
	public void setSource(String line1){this.source = line1;}
	
	public String toString(){
		String result = this.name;
		
		if(this.applicationEntity!=null)
			result += "(" + this.applicationEntity + ")";
		
		return result;
	}

}
