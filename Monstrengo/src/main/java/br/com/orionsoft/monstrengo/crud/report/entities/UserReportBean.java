package br.com.orionsoft.monstrengo.crud.report.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import br.com.orionsoft.monstrengo.crud.report.entities.OrderCondictionBean;
import br.com.orionsoft.monstrengo.crud.report.entities.PageCondictionBean;
import br.com.orionsoft.monstrengo.crud.report.entities.ParentCondictionBean;
import br.com.orionsoft.monstrengo.crud.report.entities.QueryCondictionBean;
import br.com.orionsoft.monstrengo.crud.report.entities.ResultCondictionBean;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * @hibernate.class table="framework_user_report"
 */
@Entity
@Table(name="framework_user_report")
public class UserReportBean 
{
    /* Constantes com os nomes das propriedades da classe para
     * serem usadas no código e evitar erro de digitação. */
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String DATE = "date";
    public static final String APPLICATION_ENTITY = "applicationEntity";
    public static final String APPLICATION_USER = "applicationUser";
    public static final String QUERY_CONDICTIONS = "queryCondictions";
    public static final String ORDER_CONDICTIONS = "orderCondictions";
    public static final String RESULT_CONDICTIONS = "resultCondictions";
    public static final String FILTER_CONDICTION = "filterCondiction";
    public static final String HQLWHERE_CONDICTION = "hqlWhereCondiction";
    public static final String PAGE_CONDICTION = "pageCondiction";
    public static final String PARENT_CONDICTION = "parentCondiction";

	private long id = -1;
    private String name;
    private String description;
    private Calendar date;
    private String filterCondiction;
    private String hqlWhereCondiction;
    private PageCondictionBean pageCondiction;
    private ParentCondictionBean parentCondiction;
    private ApplicationEntity applicationEntity;
    private ApplicationUser applicationUser;
    public List<QueryCondictionBean>queryCondictions = new ArrayList<QueryCondictionBean>();
//    public Set orderCondictions = new HashSet();
    public List<OrderCondictionBean> orderCondictions = new ArrayList<OrderCondictionBean>();
    public Set<ResultCondictionBean> resultCondictions = new HashSet<ResultCondictionBean>();

	/**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId(){return id;}
    public void setId(long id){this.id = id;}
    
	/**
     * @hibernate.property length="100"
     */
    @Column(length=100)
    public String getName(){return name;}
    public void setName(String nome){this.name = nome;}
    
    /**
     * @hibernate.property length="500"
     * @hibernate.column name="description" sql-type="Text" 
     */
    @Column(length=500, name="description", columnDefinition="Text")
	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}

	/**
     * @hibernate.property
     */
	@Column
	public Calendar getDate() {return date;}
	public void setDate(Calendar date) {this.date = date;}
	
    /**
     * Um relatório se relaciona com vários beans de condições de pesquisa.
     * 
     * @hibernate.list cascade="all" lazy="false" 
     * @hibernate.collection-key-column name="userReport" index="userReport"
     * @hibernate.collection-key foreign-key="userReport"
     * @hibernate.collection-one-to-many class="br.com.orionsoft.monstrengo.crud.report.entities.QueryCondictionBean"
     * @hibernate.collection-index column="orderIndex"
     */
	@OneToMany(cascade=CascadeType.ALL) @LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name="userReport") 
	@JoinColumn(name="userReport") @IndexColumn(name="orderIndex")
    public List<QueryCondictionBean> getQueryCondictions() {return queryCondictions;}
    public void setQueryCondictions(List<QueryCondictionBean> queryCondictions) {this.queryCondictions = queryCondictions;}
    
    /**
     * Um relatório se relaciona com vários beans de condições de ordenação.
     * 
     * @hibernate.list cascade="all" lazy="false" 
     * @hibernate.collection-key-column name="userReport" index="userReport"
     * @hibernate.collection-key foreign-key="userReport"
     * @hibernate.collection-one-to-many class="br.com.orionsoft.monstrengo.crud.report.entities.OrderCondictionBean"
     * @hibernate.collection-index column="orderIndex"
     */
    @OneToMany(cascade=CascadeType.ALL) @LazyCollection(LazyCollectionOption.FALSE)
    @ForeignKey(name="userReport") 
    @JoinColumn(name="userReport") @IndexColumn(name="orderIndex")
    public List <OrderCondictionBean>getOrderCondictions() {return orderCondictions;}
    public void setOrderCondictions(List <OrderCondictionBean>orderCondictions) {this.orderCondictions = orderCondictions;}

    /**
     * Um relatório se relaciona com vários beans de condições de ordenação.
     * 
     * @hibernate.set cascade="all" lazy="false" 
     * @hibernate.collection-key-column name="userReport" index="userReport"
     * @hibernate.collection-key foreign-key="userReport"
     * @hibernate.collection-one-to-many class="br.com.orionsoft.monstrengo.crud.report.entities.ResultCondictionBean"
     */
    @OneToMany(cascade=CascadeType.ALL) @LazyCollection(LazyCollectionOption.FALSE)
    @ForeignKey(name="userReport") 
    @JoinColumn(name="userReport") 
    public Set <ResultCondictionBean>getResultCondictions() {return resultCondictions;}
    public void setResultCondictions(Set<ResultCondictionBean> resultCondictions) {this.resultCondictions = resultCondictions;}
    
    /**
     * @hibernate.property length="50" 
     */
    @Column(length=50)
	public String getFilterCondiction(){return filterCondiction;}
	public void setFilterCondiction(String filter){this.filterCondiction = filter;}
	
    /**
     * @hibernate.property length="500"
     * @hibernate.column name="hqlWhereCondiction" sql-type="Text" 
     */
	@Column(length=500, name="hqlWhereCondiction", columnDefinition="Text")
	public String getHqlWhereCondiction() {return hqlWhereCondiction;}
	public void setHqlWhereCondiction(String hqlWhereCondiction) {this.hqlWhereCondiction = hqlWhereCondiction;}
	
    /**
     * @hibernate.one-to-one constrained="false" cascade="all"
     */
	@OneToOne(cascade=CascadeType.ALL)
	@PrimaryKeyJoinColumn
	public PageCondictionBean getPageCondiction(){return pageCondiction;}
	public void setPageCondiction(PageCondictionBean page){this.pageCondiction = page;}
	
    /**
     * @hibernate.one-to-one constrained="false" cascade="all"
     */
	@OneToOne(cascade=CascadeType.ALL) 
	@PrimaryKeyJoinColumn
	public ParentCondictionBean getParentCondiction(){return parentCondiction;}
	public void setParentCondiction(ParentCondictionBean parent){this.parentCondiction = parent;}

    /**
     * @hibernate.many-to-one foreign-key="applicationEntity" cascade="none"
     */
	@ManyToOne
	@JoinColumn(name="applicationEntity")
	@ForeignKey(name="applicationEntity")
	public ApplicationEntity getApplicationEntity(){return applicationEntity;}
	public void setApplicationEntity(ApplicationEntity applicationEntity){this.applicationEntity = applicationEntity;}
	
    /**
     * @hibernate.many-to-one foreign-key="applicationUser" cascade="none"
     */
	@ManyToOne
	@JoinColumn(name="applicationUser")
	@ForeignKey(name="applicationUser")
	public ApplicationUser getApplicationUser(){return applicationUser;}
	public void setApplicationUser(ApplicationUser applicationUser){this.applicationUser = applicationUser;}
	
	public String toString()
	{
		String result = "";
		result += this.name;
		return result;
	}
 }
