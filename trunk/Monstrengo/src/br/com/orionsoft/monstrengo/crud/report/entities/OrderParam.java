package br.com.orionsoft.monstrengo.crud.report.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.crud.report.entities.OrderCondictionBean;
import br.com.orionsoft.monstrengo.crud.report.entities.ReportParam;
import br.com.orionsoft.monstrengo.crud.report.entities.UserReport;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.services.OrderCondiction;

/**
 * Esta classe mantem os os parâmetros de ordenação
 * que serão utilizados pelo processo de pesquisa.
 * Created on 08/03/2006
 * @author 
 */
public class OrderParam extends ReportParam
{
    private List<OrderCondiction> condictions;
    private OrderCondiction newCondiction;
    
	// Parâmetros que devem ser fornecidos como entrada
    private String orderExpression = "";

    public OrderParam(UserReport userReport) throws BusinessException{
    	super(userReport);

    	this.newCondiction = new OrderCondiction(this.getUserReport().getEntityManager(), this.getUserReport().getEntityType());
    	this.condictions = new ArrayList<OrderCondiction>();
    }
	
	public void clear(){
        this.condictions.clear();
	}

	public void beanToParam (List<OrderCondictionBean> orderCondictionBean) throws BusinessException{
		this.clear();
		
		if(orderCondictionBean!=null){
			for(OrderCondictionBean bean: orderCondictionBean){
				OrderCondiction condiction = new OrderCondiction(this.getUserReport().getEntityManager(),this.getUserReport().getEntityType());
				
				/* Copia os dados persistidos do Bean para o Param*/
				condiction.setActive(bean.isActive());
				condiction.setOrderDirection(bean.getOrderDirection());
				condiction.setPropertyPath(bean.getPropertyPath());
				
				/* Adiciona a condição na lista */
				this.getCondictions().add(condiction);
			}
		}
	}

	public List<OrderCondictionBean> paramToBean() throws BusinessException{
		/* Cria a copia os dados interno do Param para a estrutura Bean
		 * correspondente. Lembrando de definir o UserReportBean ao 
		 * qual o bean vai pertencer */
		List<OrderCondictionBean> result = new ArrayList<OrderCondictionBean>(this.condictions.size());
		for(OrderCondiction condiction: this.condictions){
			OrderCondictionBean bean = new OrderCondictionBean();
			bean.setActive(condiction.isActive());
			bean.setOrderDirection(condiction.getOrderDirection());
			bean.setPropertyPath(condiction.getPropertyPath());
			bean.setUserReport(this.getUserReport().getUserReportBean());
			
			result.add(bean);
		}
		
		return result;
	}

    public boolean isHasCondictions(){
    	return condictions.size()>0;
    }

    public boolean isHasExpression(){
    	return !StringUtils.isEmpty(orderExpression);
    }
	
	public String getOrderExpression() {return orderExpression;}
	public void setOrderExpression(String orderExpression) {this.orderExpression = orderExpression;}

	public OrderCondiction getNewCondiction() {return newCondiction;}

	public List<OrderCondiction> getCondictions() {return condictions;}

	public void addNewCondiction() throws BusinessException{
		/*Adicionando a nova condição preenchida na lista de condições */
		this.condictions.add(this.newCondiction);
		
		/* Guarda o ponteiro para a condição que será subtituída
		 * para posteriormente copiar as propriedades dela para 
		 * a nova condição */
		OrderCondiction oldCondiction = this.newCondiction; 

		/*Limpando a nova condição para esperar por novos parãmetros */
		this.newCondiction = new OrderCondiction(this.getUserReport().getEntityManager(), this.getUserReport().getEntityType());
		
		/* Copia as propriedades da condiçao anterior para a
		 * que foi criada logo acima */
		this.newCondiction.setOrderDirection(oldCondiction.getOrderDirection());
		this.newCondiction.setPropertyPath(oldCondiction.getPropertyPath());
	}
	
	public boolean removeCondiction(long id){
		for(Iterator<OrderCondiction> it = condictions.iterator();it.hasNext();){
			OrderCondiction cond = it.next();
			if(cond.getId()==id){
				it.remove();
				return true;
			}
		}

		return false;
	}

	public List<SelectItem> getListOrderDirection(){
		List<SelectItem> result = new ArrayList<SelectItem>(2);
		result.add(new SelectItem(OrderCondiction.ORDER_ASC, "Ascendente «"));
		result.add(new SelectItem(OrderCondiction.ORDER_DESC, "Descendente »"));
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
				if(OrderCondiction.checkVersionSupport(propInfo)){
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
		
		/* Somente propriedades PRIMITIVAS podem ser
		 * ordenadas */
		if(propInfo.isPrimitive())
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
					
					if(OrderCondiction.checkVersionSupport(propInfo_))
						buildPropertyPath(list, propInfo_, actualPath, actualLabel, stackLevelId + 1);
					
				}
			}
		}
	}

}