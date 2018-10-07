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
 * @hibernate.class table="framework_query_condiction"
 */
@Entity
@Table(name="framework_query_condiction")
public class QueryCondictionBean
{
    public static final String PROPERTY_PATH = "propertyPath";
    public static final String USER_REPORT = "userReport";
    public static final String ACTIVE = "active";
    public static final String INIT_OPERATOR = "initiOperator";
    public static final String OPEN_PAR = "openPar";
    public static final String OPERATOR_ID = "operatorId";
    public static final String VALUE1 = "value1";
    public static final String VALUE2 = "value2";
    public static final String CLOSE_PAR = "closePar";
    
    
	private long id = IDAO.ENTITY_UNSAVED;
	private boolean active = true;

	private UserReportBean userReport;

	private String propertyPath;

	private int initOperator;
	private boolean openPar;
	private int operatorId;
	private String value1 = "";
	private String value2 = "";
	private boolean closePar;

    /**
     * @hibernate.property
     */
	@Column
	public int getInitOperator(){return initOperator;}
	public void setInitOperator(int initOperator){this.initOperator = initOperator;}

    /**
     * @hibernate.property
     */
	@Column
	public boolean isClosePar(){return closePar;}
	public void setClosePar(boolean closePar){this.closePar = closePar;}
	
    /**
     * @hibernate.property
     */
	@Column
	public boolean isOpenPar(){return openPar;}
	public void setOpenPar(boolean openPar){this.openPar = openPar;}
	
    /**
     * @hibernate.property length="50"
     */
	@Column(length=50)
	public String getValue1(){return value1;}
	public void setValue1(String value1){this.value1 = value1;}

    /**
     * @hibernate.property length="50"
     */
	@Column(length=50)
	public String getValue2(){return value2;}
	public void setValue2(String value2){this.value2 = value2;}
	
    /**
     * @hibernate.property
     */
	@Column
	public int getOperatorId(){return this.operatorId;}
	public void setOperatorId(int id){this.operatorId=id;}
	
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
	public void setActive(boolean active){this.active = active;}

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

	public String toString(){
		return propertyPath;
	}
	
}
