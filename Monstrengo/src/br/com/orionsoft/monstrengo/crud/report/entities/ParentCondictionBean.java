package br.com.orionsoft.monstrengo.crud.report.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;

/**
 * @hibernate.class table="framework_parent_condiction"
 */
@Entity
@Table(name="framework_parent_condiction")
public class ParentCondictionBean
{
	public static final String PROPERTY = "property";
	public static final String APPLICATION_ENTITY = "applicatinoEntity";
	public static final String USER_REPORT = "userReport";

    /** Parâmetros get/set */
    private long id = IDAO.ENTITY_UNSAVED;
    
    private String property;
    private ApplicationEntity applicationEntity;
    
	private UserReportBean userReport;

    /**
     * @hibernate.id generator-class="foreign" unsaved-value="-1"
     * @hibernate.generator-param name="property" value="userReport"
     */
	@Id
    public long getId() {return id;}
	public void setId(long parentId) {this.id = parentId;}

    /**
     * @hibernate.property length="50"
     */
	@Column(length=50)
	public String getProperty() {return property;}
	public void setProperty(String parentProperty) {this.property = parentProperty;}

    /**
     * @hibernate.many-to-one foreign-key="applicationEntity"
     */
	@ManyToOne
	@JoinColumn(name="applicationEntity")
	@ForeignKey(name="applicationEntity")
	public ApplicationEntity getApplicationEntity(){return applicationEntity;}
	public void setApplicationEntity(ApplicationEntity applicationEntity){this.applicationEntity = applicationEntity;}
    
    /**
     * @hibernate.one-to-one
     */
	@OneToOne
	@PrimaryKeyJoinColumn
    public UserReportBean getUserReport(){return userReport;}
	public void setUserReport(UserReportBean userReport){this.userReport = userReport;}
}
