package br.com.orionsoft.monstrengo.crud.labels.entities;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.monstrengo.crud.labels.entities.AddressLabelGroup;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;


/**
 * Esta classe é o item da lista de etiquetas de endereçamento
 * que estão prontas para serem impressas. 
 * 
 * @hibernate.class table="framework_label_address"
 */
@Entity
@Table(name="framework_label_address")
public class AddressLabel {
	
	public static final String APPLICATION_USER = "applicationUser";
	public static final String APPLICATION_ENTITY = "applicationEntity";
	public static final String PRINT = "print";
	public static final String LINE1 = "line1";
	public static final String LINE2 = "line2";
	public static final String LINE3 = "line3";
	public static final String LINE4 = "line4";
	public static final String LINE5 = "line5";
	public static final String ADDRESS_LABEL_GROUP = "addressLabelGroup";
    public static final String OCURRENCY_DATE = "ocurrencyDate";

	/* Propriedades de um campo */
	private long id = IDAO.ENTITY_UNSAVED;
	
	private ApplicationUser applicationUser; 
	private ApplicationEntity applicationEntity; 
	private boolean print = true;
	private String line1;
	private String line2;
	private String line3;
	private String line4;
	private String line5;
	private AddressLabelGroup addressLabelGroup; 
    protected Calendar ocurrencyDate;
	

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
	public boolean isPrint(){return print;}
	public void setPrint(boolean imprimir){this.print = imprimir;}
	
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
     * @hibernate.property length="150"
     */
	@Column(length=150)
	public String getLine1(){return line1;}
	public void setLine1(String line1){this.line1 = line1;}
	
    /**
     * @hibernate.property length="150"
     */
	@Column(length=150)
	public String getLine2(){return line2;}
	public void setLine2(String line2){this.line2 = line2;}
	
    /**
     * @hibernate.property length="150"
     */
	@Column(length=150)
	public String getLine3(){return line3;}
	public void setLine3(String line3){this.line3 = line3;}
	
    /**
     * @hibernate.property length="150"
     */
	@Column(length=150)
	public String getLine4(){return line4;}
	public void setLine4(String line4){this.line4 = line4;}
	
    /**
     * @hibernate.property length="150"
     */
	@Column(length=150)
	public String getLine5(){return line5;}
	public void setLine5(String line5){this.line5 = line5;}
	
    /**
     * @hibernate.many-to-one foreign-key="addressLabelGroup"
     */
	@ManyToOne
	@JoinColumn(name="addressLabelGroup")
	@ForeignKey(name="addressLabelGroup")
	public AddressLabelGroup getAddressLabelGroup() {return addressLabelGroup;}
	public void setAddressLabelGroup(AddressLabelGroup addressLabelGroup) {this.addressLabelGroup = addressLabelGroup;}
	
    /**
     * @hibernate.property 
     */
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getOcurrencyDate(){return ocurrencyDate;}
    public void setOcurrencyDate(Calendar date){this.ocurrencyDate = date;}
    
	public String toString(){
		String result = "";

		if (this.applicationUser != null)
			result += this.applicationUser.getLogin() + ":";

        result += this.line1 + "...";
        
        return result;
	}

}
