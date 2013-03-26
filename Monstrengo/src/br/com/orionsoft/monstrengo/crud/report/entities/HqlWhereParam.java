package br.com.orionsoft.monstrengo.crud.report.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.crud.report.entities.ReportParam;
import br.com.orionsoft.monstrengo.crud.report.entities.UserReport;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.crud.support.HqlExpressionParserFields;
import br.com.orionsoft.monstrengo.crud.support.HqlExpressionParserFields.HqlExpressionField;

/**
 * Esta classe mantem uma express�o HQLWhere 
 * que pode ser usada na pesquisa
 * Durante a persistencia deste par�metros � utilizado uma propriedade
 * diretamente do UserReportBean
 * 
 * @author Lucio 
 */
public class HqlWhereParam extends ReportParam
{
    private String hqlWhere="";
    private Map<String, HqlExpressionField> hqlFieldsMap = null;
    
    
    public String getHqlWhere(){return hqlWhere;}
    public void setHqlWhere(String hqlWhere){
    	/* Cada vez que a express�o mudar, o mapa de campos dever� ser
    	 * reconstru�do */
    	if(!StringUtils.equals(this.hqlWhere, hqlWhere)){
    		this.hqlWhere = hqlWhere;
    		hqlFieldsMap = null;
    	}
    }
    
    public String getHqlWhereCompiled() throws BusinessException{
    	return HqlExpressionParserFields.replaceFields(hqlWhere, getHqlFieldsMap());
    }
    
    public Map<String, HqlExpressionField> getHqlFieldsMap() throws BusinessException
	{
    	/* Verifica se o mapa � nulo, pois se for, a express�o 
    	 * dever� ser analisada e atraves do findFields, os campos
    	 * ser�o procurados e o mapa ser� construido. Por limita��es da interface.
    	 * os elementos do mapa � acessados e alterados atrav�s de uma lista
    	 * que � constru�da aqui tb */
    	if(hqlFieldsMap == null){
    		hqlFieldsMap  = HqlExpressionParserFields.findFields(hqlWhere);
    		
    		hqlFieldsList.clear();
    		
    		for(Entry<String, HqlExpressionField> entry: hqlFieldsMap.entrySet())
    			hqlFieldsList.add(entry.getValue());
    		
    	}
    	return hqlFieldsMap;
	}

	/**
	 * Lista para facilitar o acesso e altera��o dos elementos do mapa na interface.
	 * Esta lista � manutenida pelo m�todo getHqlFieldsMap().
	 */
    public List<HqlExpressionField> hqlFieldsList = new ArrayList<HqlExpressionField>();
    public List<HqlExpressionField> getHqlFieldsList() throws BusinessException{
    	/* Verifica se o mapa � nulo e a lista precisa ser RECONSTRUIDA */
    	if(hqlFieldsMap == null)
    		getHqlFieldsMap();
    	
    	return hqlFieldsList;
    }
    
    public HqlWhereParam(UserReport userReport) throws BusinessException{
    	super(userReport);
    }
    
	public void clear(){
        this.hqlWhere = "";
	}

	public boolean isHasHqlWhere(){
    	return !StringUtils.isEmpty(hqlWhere);
    }
	
	public boolean isHasHqlFields() throws BusinessException{
		return !this.getHqlFieldsList().isEmpty();
    }

}
