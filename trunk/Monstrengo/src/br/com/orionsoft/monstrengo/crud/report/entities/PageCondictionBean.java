package br.com.orionsoft.monstrengo.crud.report.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import br.com.orionsoft.monstrengo.crud.report.entities.UserReportBean;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * @hibernate.class table="framework_page_condiction"
 */
@Entity
@Table(name="framework_page_condiction")
public class PageCondictionBean
{
    public static final String PAGE = "page";
	
    public static final int FIRST_PAGE_INDEX = 1;
	
	private long id = IDAO.ENTITY_UNSAVED;
	
	private int page = FIRST_PAGE_INDEX;
    private int pageSize = 50;
    private int itemsCount = 0;
    
	private UserReportBean userReport;

    /**
     * @hibernate.id generator-class="foreign" unsaved-value="-1"
     * @hibernate.generator-param name="property" value="userReport"
     */
	@Id
	public long getId(){return id;}
	public void setId(long id){this.id = id;}
	
	/**
     * @hibernate.property
     */
	@Column	
	public int getItemsCount(){return itemsCount;}
	public void setItemsCount(int itemsCount){this.itemsCount = itemsCount;}

    /**
     * @hibernate.property
     */
	@Column
	public int getPage(){return page;}
    public void setPage(int currentPage){if (currentPage > 0) this.page = currentPage;}

    /**
     * @hibernate.property
     */
    @Column
    public int getPageSize(){return pageSize;}
    public void setPageSize(int pageSize){this.pageSize = pageSize;}
	
    /**
     * @hibernate.one-to-one
     */
    @OneToOne
	@PrimaryKeyJoinColumn
	public UserReportBean getUserReport(){return userReport;}
	public void setUserReport(UserReportBean userReport){this.userReport = userReport;}
	
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
 