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
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;


/**
 * Esta classe é o item da lista de etiquetas de endereçamento
 * que estão prontas para serem impressas. 
 * 
 * @hibernate.class table="framework_label_address_group"
 */
@Entity
@Table(name="framework_label_address_group")
public class AddressLabelGroup {
	
	public static final String APPLICATION_USER = "applicationUser";
	public static final String NAME = "name";

	/* Propriedades de um campo */
	private long id = IDAO.ENTITY_UNSAVED;
	
	private ApplicationUser applicationUser; 
	private String name;

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
     * @hibernate.property length="100"
     */
	@Column(length=100)
	public String getName(){return name;}
	public void setName(String name){this.name = name;}
	
	public String toString(){
//		Lucio - 20090716 - Para ocupar menos espaço nas tabelas de visualização
//		StringBuffer result = new StringBuffer(this.name);
//
//		if (this.applicationUser != null)
//			result.append("(").append(this.applicationUser.getName()).append(")");

//        return result.toString();
      return this.name;
	}
}
