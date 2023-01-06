package br.com.orionsoft.monstrengo.crud.report.entities;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.crud.report.entities.PageCondictionBean;
import br.com.orionsoft.monstrengo.crud.report.entities.ReportParam;
import br.com.orionsoft.monstrengo.crud.report.entities.UserReport;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;

/**
 * Classe responsável por obter da página jsp informações sobre 
 * paginação, tais como tamanho e numero da página.
 * A primeira página é a 0 (Zero).
 * 
 * Created on 08/03/2006
 * @author 
 *
 */
public class PageParam extends ReportParam
{
    public static final int FIRST_PAGE_INDEX = 1;
	
	private int page = FIRST_PAGE_INDEX;
    private int pageSize = 50;
    private int itemsCount = 0;

    public PageParam(UserReport userReport) throws BusinessException{
    	super(userReport);
    }
    
	public void clear(){
        this.page = FIRST_PAGE_INDEX;
        this.pageSize = 50;
        this.itemsCount = 0;
	}

	public void beanToParam (PageCondictionBean pageCondictionBean){
		this.clear();
		
		if(pageCondictionBean !=null){
			this.setPage(pageCondictionBean.getPage());
			this.setPageSize(pageCondictionBean.getPageSize());
			this.setItemsCount(pageCondictionBean.getItemsCount());
		}
	}

	public PageCondictionBean paramToBean() throws BusinessException{
		/* Cria a copia os dados interno do Param para a estrutura Bean
		 * correspondente. Lembrando de definir o UserReportBean ao 
		 * qual o bean vai pertencer */
		PageCondictionBean result = new PageCondictionBean();
		result.setPage(this.getPage());
		result.setPageSize(this.getPageSize());
		result.setItemsCount(this.getItemsCount());
		result.setUserReport(this.getUserReport().getUserReportBean());
		
		return result;
		
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
    // Métodos getters e Setters																	//
    //////////////////////////////////////////////////////////////////////////////////////////////////
	public int getItemsCount(){return itemsCount;}
	public void setItemsCount(int itemsCount){this.itemsCount = itemsCount;}

	public int getPage(){return page;}
    public void setPage(int currentPage){if (currentPage > 0) this.page = currentPage;}

     public int getPageCount(){
    	if(itemsCount % pageSize == 0)
    		return (itemsCount / pageSize);
    	else
    		return (itemsCount / pageSize) + 1;
    }

    public int getPageSize(){return pageSize;}
    public void setPageSize(int pageSize)
    {
        if (pageSize > 0)
            this.pageSize = pageSize;
        /* Verifica se com o novo tamanho da página
         * Haverá items para ser exibido na atual página exibida.
         * Se não houve, a página atual será reduzida */
        if((this.pageSize*this.page) > itemsCount)
        	this.setPage(getPageCount());
    }

    public boolean isFirst()
    {
        if ((page == FIRST_PAGE_INDEX))
            return true;
        return false;
    }

    /** Direciona para a primeira página e re-enderiza a mesma visão. */
    public void goFirst()
    {
    	page=FIRST_PAGE_INDEX;
    }

    /** Direciona para a página anterior e re-enderiza a mesma visão. */
    public void goPrior()
    {
    	if(page>FIRST_PAGE_INDEX)
    		page-=1;
    }

    /** Direciona para a próxima página e re-enderiza a mesma visão. */
    public void goNext()
    {
        
    	if(page < getPageCount())
    		page+=1;
    }


    /** Direciona para a última página e re-enderiza a mesma visão. */
    public void goLast()
    {
    	page=getPageCount();
    }

    /**
     * 
     * @return
     */
    public boolean isLast()
    {
        /* O número de paginas pode ser 0 (nenhuma) ou 1 (uma única pagina para exibição), neste
         * caso, ambas serão consideradas a última pagina */
    	if ((this.getPageCount()<2) || (this.page == this.getPageCount()))
            return true;
        return false;
    }

    /**
     * Retorna o índice do primeiro item da página atual.
     * O índice inicia com 0 (zero) 
     */
    public int getFirstItemIndexPage(){
    	return (page-1)*pageSize;
    }

    /**
     * Retorna o índice do último item da página atual. 
     * O índice inicia com 0 (zero) 
     */
    public int getLastItemIndexPage(){
    	if(isLast())
    		return getItemsCount() -1;
    	else
    		return (page * pageSize) -1;
	}

	public List<SelectItem> getPageList(){
		
		List<SelectItem> result = new ArrayList<SelectItem>(getPageCount());
		
		for(int i=FIRST_PAGE_INDEX; i<=getPageCount();i++)
			result.add(new SelectItem(new Integer(i), Integer.toString(i)));
		
		return result;
		
	}

	public List<SelectItem> getPageSizesList(){
		List<SelectItem> result = new ArrayList<SelectItem>(6);

		result.add(0, new SelectItem(new Integer(10), "10"));
		result.add(1, new SelectItem(new Integer(20), "20"));
		result.add(2, new SelectItem(new Integer(50), "50"));
		result.add(3, new SelectItem(new Integer(100), "100"));
		result.add(4, new SelectItem(new Integer(200), "200"));
		result.add(5, new SelectItem(new Integer(400), "400"));
		result.add(6, new SelectItem(new Integer(800), "800"));
		result.add(7, new SelectItem(new Integer(1600), "1600"));
		result.add(8, new SelectItem(new Integer(3200), "3200"));
		return result;
	}
	
	public void doReset()
	{
	    page = FIRST_PAGE_INDEX;
	    itemsCount = 0;
	}

}
 