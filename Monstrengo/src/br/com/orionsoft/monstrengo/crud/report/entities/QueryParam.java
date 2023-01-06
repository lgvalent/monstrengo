package br.com.orionsoft.monstrengo.crud.report.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.crud.report.entities.QueryCondictionBean;
import br.com.orionsoft.monstrengo.crud.report.entities.ReportParam;
import br.com.orionsoft.monstrengo.crud.report.entities.UserReport;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.services.Operator;
import br.com.orionsoft.monstrengo.crud.services.QueryCondiction;

/**
 * Esta classe mantem os parâmetros de criação de
 * condições que serão utilizados pelo processo de pesquisa.
 * @author Lucio 20060328
 */
public class QueryParam extends ReportParam
{
	private List<QueryCondiction> condictions;
    private QueryCondiction newCondiction;
    
    public QueryParam(UserReport userReport) throws BusinessException{
    	super(userReport);
    	
    	this.newCondiction = new QueryCondiction(this.getUserReport().getEntityManager().getServiceManager().getEntityManager(), this.getUserReport().getEntityType());
    	this.condictions = new ArrayList<QueryCondiction>();
    }
	
    public boolean isHasCondictions(){
    	return condictions.size()>0;
    }
    
	public List<QueryCondiction> getCondictions(){return condictions;}
	public void setCondictions(List<QueryCondiction> condictions){this.condictions = condictions;}


	public void clear(){
        this.condictions.clear();
	}
	
	/**
	 * Este método recebe um conjunto de beans para serem aplicados 
	 * como atuais parâmetros.
	 * Primeiro é feita uma limpeza dos dados atuais internos do Param
	 * Depois os dados dos Bean são importados para dentro dos dados do Param. 
	 * @param queryCondictionBean
	 * @throws BusinessException 
	 */
	public void beanToParam (List<QueryCondictionBean> queryCondictionBean) throws BusinessException{
		this.clear();
		
		if(queryCondictionBean!=null){
			for(QueryCondictionBean bean: queryCondictionBean){
				
				/* Copia os dados persistidos do Bean para o Param*/
				this.newCondiction.setActive(bean.isActive());
				this.newCondiction.setClosePar(bean.isClosePar());
				this.newCondiction.setInitOperator(bean.getInitOperator());
				this.newCondiction.setOpenPar(bean.isOpenPar());
				this.newCondiction.setOperatorId(bean.getOperatorId());
				this.newCondiction.setValue1(bean.getValue1());
				this.newCondiction.setValue2(bean.getValue2());
				this.newCondiction.setPropertyPath(bean.getPropertyPath());
				
				/* Adiciona a condição na lista */
				this.addNewCondiction();
			}
		}
	}

	public List<QueryCondictionBean> paramToBean() throws BusinessException{
		/* Cria a copia os dados interno do Param para a estrutura Bean
		 * correspondente. Lembrando de definir o UserReportBean ao 
		 * qual o bean vai pertencer */
		List<QueryCondictionBean> result = new ArrayList<QueryCondictionBean>();
		for(QueryCondiction condiction: this.condictions){
			QueryCondictionBean bean = new QueryCondictionBean();
			bean.setUserReport(this.getUserReport().getUserReportBean());
			bean.setActive(condiction.isActive());
			bean.setClosePar(condiction.isClosePar());
			bean.setInitOperator(condiction.getInitOperator());
			bean.setOpenPar(condiction.isOpenPar());
			bean.setOperatorId(condiction.getOperatorId());
			bean.setValue1(condiction.getValue1());
			bean.setValue2(condiction.getValue2());
			bean.setPropertyPath(condiction.getPropertyPath());
			
			result.add(bean);
		}
		
		return result;
	}
	
	public QueryCondiction getNewCondiction() {return newCondiction;}
	
	public void addNewCondiction() throws BusinessException{
		/*Adicionando a nova condição preenchida na lista de condições */
		this.condictions.add(this.newCondiction);
		 
		/* Guarda o ponteiro para a condição que será subtituída
		 * para posteriormente copiar as propriedades dela para 
		 * a nova condição */
		QueryCondiction oldCondiction = this.newCondiction; 

		/*Limpando a nova condição para esperar por novos parãmetros */
		this.newCondiction = new QueryCondiction(this.getUserReport().getEntityManager(), this.getUserReport().getEntityType());
		
		/* Copia as propriedades da condiçao anterior para a
		 * que foi criada logo acima */
		this.newCondiction.setInitOperator(oldCondiction.getInitOperator());
		this.newCondiction.setPropertyPath(oldCondiction.getPropertyPath());
		this.newCondiction.setOperator(oldCondiction.getOperator());

	}
	
	public boolean removeCondiction(long id){
		for(Iterator<QueryCondiction> it = condictions.iterator();it.hasNext();){
			QueryCondiction cond = it.next();
			if(cond.getId()==id){
				it.remove();
				return true;
			}
		}

		return false;
	}
	
	
	public List<SelectItem> getListInitOperator(){
		List<SelectItem> result = new ArrayList<SelectItem>(2);
		result.add(new SelectItem(QueryCondiction.INIT_AND, "e", "Operador restritivo"));
		result.add(new SelectItem(QueryCondiction.INIT_OR, "ou", "Operador abrangente"));
		return result;
	}
	
	/**
	 * Este protetor é necessário porque uma entidade A pode referenciar uma entidade B, 
	 * que por sua vez, possui uma referencia pra A. 
	 */
	private Map<String, Integer> deadLockProtect = new HashMap<String, Integer>();
	private List<SelectItem> listPropertyPathBuffer = null; 
	public List<SelectItem> getListPropertyPath() throws BusinessException{
		if(listPropertyPathBuffer==null){
			/* Cria a lista de propriedades */
			listPropertyPathBuffer = new ArrayList<SelectItem>();
			
			/* Adiciona todas as propriedades a atual entidade */
			for(IPropertyMetadata propInfo: this.getUserReport().getEntityManager().getEntityMetadata(this.getUserReport().getEntityType()).getProperties()){
				if(QueryCondiction.checkVersionSupport(propInfo)){
					/* Limpa o protetor de over flow*/
					deadLockProtect.clear();
					
					/* Adiciona a entidade atual na lista de proteção de overflowa */
					deadLockProtect.put(this.getUserReport().getEntityType().getSimpleName()+propInfo.getName(), 0);
					
					/* Constroi o caminho para a propriedae atual */
					buildPropertyPath(listPropertyPathBuffer, propInfo, "", "", 0);
				}
			}

			/* Reordena a lista por ordem alfabética */
			Collections.<SelectItem>sort(listPropertyPathBuffer, new Comparator<SelectItem>(){
				public int compare(SelectItem o1, SelectItem o2) {
					return o1.getLabel().compareTo(o2.getLabel());
				}
			});
		}			
		return listPropertyPathBuffer;
	}
		
	private void buildPropertyPath(List<SelectItem> list,IPropertyMetadata  propInfo, String actualPath, String actualLabel,Integer stackLevelId) throws EntityException{
		/* Coloca o separador entre as propriedades se não for a primeira */
		if(!actualPath.equals(""))
			actualPath += IDAO.PROPERTY_SEPARATOR;
		actualPath += propInfo.getName();
		if(!actualLabel.equals(""))
			actualLabel += ReportParam.PROPERTY_PATH_LABEL_SEPARATOR;
		actualLabel += propInfo.getLabel();
		
		/* Adiciona a atual propriedade na lista de resultados */
		list.add(new SelectItem(actualPath, actualLabel));
		
		/* Verifica se atual propriedade é uma subClass para buscar suas subPropriedades */
		if(propInfo.isEntity()){
			/* Obtem os metadados da entidade corrente */
			IEntityMetadata entInfo = this.getUserReport().getEntityManager().getEntityMetadata(propInfo.getType());
			
			/* Obtem os metadados da entidade referenciada pela propriedade e
			 * Constrói o caminho (PropertyPath) de navagação entre as propriedades RECURSIVAMENTE */
			for(IPropertyMetadata propInfo_: entInfo.getProperties()){
				/* Verifica no protetor de workflow se esta entidade já foi referenciada
				 * em un nível anterior ao atual. Pois entidades no mesmo nível, ou posterioir, poderáo ser
				 * referenciadas */
				Integer stackItem = deadLockProtect.get(entInfo.getType().getSimpleName()+propInfo_.getName()); 
				if((stackItem == null) || (stackItem.compareTo(stackLevelId)>-1)){
					/* Adiciona a entidade atual na lista de proteção de overflowa */
					deadLockProtect.put(entInfo.getType().getSimpleName()+propInfo_.getName(), stackLevelId);
					
					if(QueryCondiction.checkVersionSupport(propInfo_))
						buildPropertyPath(list, propInfo_, actualPath, actualLabel, stackLevelId + 1);
					
				}
			}
		}
	}

	public List<SelectItem> getListOperatorId() throws BusinessException{
		List<SelectItem> result = new ArrayList<SelectItem>();
		for(Operator op: Operator.getOperators())
			result.add(new SelectItem(op.getId(), op.getLabel() + " (" + op.getSymbol()+")"));
		return result;
	}

}