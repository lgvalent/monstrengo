package br.com.orionsoft.monstrengo.crud.report.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.monstrengo.crud.report.entities.UserReportBean;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * @hibernate.class table="framework_result_condiction"
 */
@Entity
@Table(name="framework_result_condiction")
public class ResultCondictionBean
{
    public static final String RESULT_CONDICTIONS = "resultCondictions";
    
	private long id = IDAO.ENTITY_UNSAVED;
	
	private UserReportBean userReport;
	
	private String propertyPath;
	
	private Integer resultIndex;

    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {return id;}
	public void setId(long id){this.id = id;}

    /**
     * @hibernate.property length="200"
     */
	@Column(length=200)
	public String getPropertyPath(){return propertyPath;}
	public void setPropertyPath(String propertyPath){this.propertyPath = propertyPath;}
	
    /**
     * @hibernate.many-to-one foreign-key="userReport"
     */
	@ManyToOne
	@JoinColumn(name="userReport")
	@ForeignKey(name="userReport")
	public UserReportBean getUserReport(){return userReport;}
	public void setUserReport(UserReportBean userReport){this.userReport = userReport;}
	
    /**
     * @hibernate.property
     */
	@Column
	public Integer getResultIndex(){return resultIndex;}
	public void setResultIndex(Integer resultIndex){this.resultIndex = resultIndex;}

	public String toString(){
		return resultIndex + "-" + propertyPath;
	}
	
}
