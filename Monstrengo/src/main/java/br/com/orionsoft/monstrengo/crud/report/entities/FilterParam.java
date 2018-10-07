package br.com.orionsoft.monstrengo.crud.report.entities;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.crud.report.entities.ReportParam;
import br.com.orionsoft.monstrengo.crud.report.entities.UserReport;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;

/**
 * Esta classe mantem os os parâmetros de criação de
 * filtros que serão utilizados pelo processo de pesquisa.
 * Durante a persistencia deste parâmetros é utilizado uma propriedade
 * diretamente do UserReportBean
 * 
 * @author 
 */
public class FilterParam extends ReportParam
{
    private String filter="";

    public String getFilter(){return filter;}
    public void setFilter(String filter){this.filter = filter;}
    
    public FilterParam(UserReport userReport) throws BusinessException{
    	super(userReport);
    }
    
	public void clear(){
        this.filter = "";
	}

	public boolean isHasFilter(){
    	return !StringUtils.isEmpty(filter);
    }
}
