package br.com.orionsoft.monstrengo.crud.report.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.orionsoft.monstrengo.crud.report.entities.ReportParam;
import br.com.orionsoft.monstrengo.crud.report.entities.ResultCondictionBean;
import br.com.orionsoft.monstrengo.crud.report.entities.UserReport;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.util.ArrayUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.services.ResultCondiction;

/**
 * Esta classe mantem os parâmetros de criação de
 * condições que serão utilizados pelo processo de pesquisa.
 * @author Lucio 20060328
 */
public class ResultParam extends ReportParam
{
	private List<ResultCondiction> condictions;
	
    public ResultParam(UserReport userReport) throws BusinessException{
    	super(userReport);
    	
    	this.condictions = new ArrayList<ResultCondiction>();
    	
    	/* Constroi a lista de condições que poderão ser marcadas/desmarcadas para serem visualizadas no resultado */
    	buildCondictions();
    }
	
	public void clear(){
        for(ResultCondiction condiction: condictions)
        	condiction.setVisible(false);
	}

	public void beanToParam (Set<ResultCondictionBean> resultCondictionBean) throws BusinessException{
		this.clear();
		
		buildCondictions();
		
		if(resultCondictionBean!=null){
			for(ResultCondiction condiction: this.condictions){
				condiction.setVisible(false);
				for(ResultCondictionBean bean: resultCondictionBean)
					if(bean.getPropertyPath().equals(condiction.getPropertyPath())){
						condiction.setVisible(true);
						condiction.setResultIndex(bean.getResultIndex());
					}
			}
		}
	}

	public Set<ResultCondictionBean> paramToBean() throws BusinessException{
		/* Cria a copia os dados interno do Param para a estrutura Bean
		 * correspondente. Lembrando de definir o UserReportBean ao 
		 * qual o bean vai pertencer */
		Set<ResultCondictionBean> result = new HashSet<ResultCondictionBean>();
		for(ResultCondiction condiction: this.condictions){
			if(condiction.isVisible()){
					ResultCondictionBean bean = new ResultCondictionBean();
					bean.setPropertyPath(condiction.getPropertyPath());
					bean.setUserReport(this.getUserReport().getUserReportBean());
					bean.setResultIndex(condiction.getResultIndex());
					result.add(bean);
			}
		}
		
		return result;
	}

    public boolean isHasCondictions(){
    	return condictions.size()>0;
    }
    
	public ResultCondiction[] getSelectedCondictions(){
		
		/* Contando quantos items estão marcados para criar um vetor com 
		 * o tamanho otimizado e evitar processos de readequação do tamanho do vetor
		 * a cada inserção */
		int selectedCount = 0;
		for(ResultCondiction cond: this.condictions)
			if(cond.isVisible())
				selectedCount++;
		
		/* Construindo a lista de itens selecionados */
		ResultCondiction[] result = new ResultCondiction[selectedCount];
		
		/* Adicionando o itens selecionados como visíveis na lista */
		for(ResultCondiction cond: this.condictions)
			if(cond.isVisible()){
				/* Verifica se tem um índice válido definido */
				if(cond.getResultIndex() != null){
					int index=0;
					/* Verifica se o índice definido é MAIOR ou IGUAL ao tamanho da lita */
					if(cond.getResultIndex() >= selectedCount){
						/* Coloca num lugar vago */
						index = ArrayUtils.findFirstEmpty(result);
						/* Atualiza o índice do elemento */
						cond.setResultIndex(index);
					}else
						/* Verifica se o índice definido já está ocupado */
						if(result[cond.getResultIndex()]!=null){
							/* Move o elemento entruso para o um lugar vago */
							index = ArrayUtils.findFirstEmpty(result);
							result[index] = result[cond.getResultIndex()];
							/* Atualiza o indice do elemento entruso */
							result[index].setResultIndex(index);
	
							/* Utiliza o índice definido pelo proprio elemento */
							index = cond.getResultIndex();
						}else{
							/* Utiliza o índice definido pelo proprio elemento */
							index = cond.getResultIndex();
						}
					
					/* Define o conteudo da posição calculada acima */
					result[index] = cond;
				}else{
					int index=ArrayUtils.findFirstEmpty(result);
					/* Nenhum índice definido, coloca num lugar vago */
					result[index] = cond;
					/* Atualiza o índice do elemento */
					cond.setResultIndex(index);
					
				}
			}
		return result;
	}

	public List<ResultCondiction> getCondictions(){return condictions;}
	public void setCondictions(List<ResultCondiction> condictions){this.condictions = condictions;}
	
	/**
	 * Este protetor é necessário porque uma entidade A pode referenciar uma entidade B, 
	 * que por sua vez, possui uma referencia pra A. 
	 */
	private Map<String, Integer> deadLockProtect = new HashMap<String, Integer>();
	private void buildCondictions() throws BusinessException{
		/* Limpa as atuais condições de reasultado */
		this.condictions.clear();
		
		/* Adiciona todas as propriedades a atual entidade */
		for(IPropertyMetadata propInfo: this.getUserReport().getEntityManager().getEntityMetadata(this.getUserReport().getEntityType()).getProperties()){
			if(ResultCondiction.checkVersionSupport(propInfo)){
				/* Limpa o protetor de over flow*/
				deadLockProtect.clear();
					
				/* Adiciona a entidade atual na lista de proteção de overflowa */
				deadLockProtect.put(this.getUserReport().getEntityType().getSimpleName()+propInfo.getName(), 0);
					
				/* Constroi o caminho para a propriedae atual */
				buildPropertyPath(propInfo, "", "", 0, true);
			}
		}

			/* Reordena a lista por ordem alfabética */
			Collections.<ResultCondiction>sort(this.condictions, new Comparator<ResultCondiction>(){
				public int compare(ResultCondiction o1, ResultCondiction o2) {
					return o1.getPropertyPathLabel().compareTo(o2.getPropertyPathLabel());
				}
			});
	}
		
	private void buildPropertyPath(IPropertyMetadata  propInfo, String actualPath, String actualLabel,Integer stackLevelId, boolean visible) throws EntityException{
		/* Coloca o separador entre as propriedades se não for a primeira */
		if(!actualPath.equals(""))
			actualPath += IDAO.PROPERTY_SEPARATOR;
		actualPath += propInfo.getName();
		if(!actualLabel.equals(""))
			actualLabel += ReportParam.PROPERTY_PATH_LABEL_SEPARATOR;
		actualLabel += propInfo.getLabel();
		
		/* Adiciona a atual propriedade na lista de resultados */
		this.condictions.add(new ResultCondiction(propInfo, actualPath, actualLabel, visible));
		
		/* Verifica se atual propriedade é uma subClass para buscar suas subPropriedades */
		if(propInfo.isEntity() && !propInfo.isCollection()){
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
					
					if(ResultCondiction.checkVersionSupport(propInfo_))
						buildPropertyPath(propInfo_, actualPath, actualLabel, stackLevelId + 1, false);
					
				}
			}
		}
	}

}