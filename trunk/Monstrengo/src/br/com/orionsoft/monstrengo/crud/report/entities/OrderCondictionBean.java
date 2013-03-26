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
 * @hibernate.class table="framework_order_condiction"
 */
@Entity
@Table(name="framework_order_condiction")
public class OrderCondictionBean
{
    public static final String PROPERTY_PATH = "propertyPath";
    public static final String ORDER_DIRECTION = "orderDirection";
    public static final String ACTIVE = "active";
    public static final String USER_REPORT = "userReport";
	
	private long id = IDAO.ENTITY_UNSAVED;
	
	private boolean active = true;
	
	private UserReportBean userReport;

	private String propertyPath;

	private int orderDirection;

    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public long getId() {return id;}
	public void setId(long id){this.id = id;}

    /**
     * @hibernate.property
     */
	@Column
	public boolean isActive() {return active;}
	public void setActive(boolean active) {this.active = active;}

    /**
     * @hibernate.property length="200"
     */
	@Column(length=200)
	public String getPropertyPath(){return propertyPath;}
	public void setPropertyPath(String propertyPath){this.propertyPath = propertyPath;}

    /**
     * @hibernate.property
     */
	@Column
	public int getOrderDirection() {return orderDirection;}
	public void setOrderDirection(int orderDirection) {this.orderDirection = orderDirection;}
	
    /**
     * @hibernate.many-to-one foreign-key="userReport"
     */
	@ManyToOne
	@JoinColumn(name="userReport")
	@ForeignKey(name="userReport")
	public UserReportBean getUserReport(){return userReport;}
	public void setUserReport(UserReportBean userReport){this.userReport = userReport;}
	
	public String toString(){
		return id + "-" + propertyPath;
	}

}
