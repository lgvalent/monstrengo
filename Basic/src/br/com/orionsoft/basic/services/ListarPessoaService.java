package br.com.orionsoft.basic.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.services.Operator;
import br.com.orionsoft.monstrengo.crud.services.QueryCondiction;
import br.com.orionsoft.monstrengo.crud.services.QueryService;

/**
 * Servi�o que retorna uma lista de pessoas
 * 
 * <p>
 * <b>Argumentos:</b><br>
 * IN_PESSOA_DOCUMENTO : documento (cpf no caso de pessoa f�sica) de um candidato a ser pesquisado. Caso este valor n�o seja fornecido, pesquisa por todos os candidatos no processo seletivo.
 * IN_HQL_WHERE_OPT : cl�usula opcional para pesquisa.
 * <p>
 * <b>Procedimento:</b> 
 * Busca pessoas 
 * - cuja infoma��es sejam iguais �s vari�veis passadas no in�cio do servi�o
 * 
 * Retorna uma lista (ListarPessoaQuery) de pessoas que correspondem as condi��es estabelecidas.
 * TODO - esta classe pode ser melhorada, utilizando mais par�metros de pesquisa na entrada
 *  
 * @spring.bean id="ListarPessoaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class ListarPessoaService extends ServiceBasic {
	
	public static final String SERVICE_NAME = "ListarPessoaService";

	public static final String IN_PESSOA_DOCUMENTO = "pessoaDocumento";
	public static final String IN_HQL_WHERE_OPT = "hqlWhere";

	public String getServiceName() {
		return SERVICE_NAME;
	}

	@SuppressWarnings("unchecked")
	public void execute(ServiceData serviceData) throws ServiceException {
		try {
			log.debug("Preparando os argumentos");
			
			String inPessoaDocumento = (String) serviceData.getArgumentList().getProperty(IN_PESSOA_DOCUMENTO);
			String inHqlWhere = serviceData.getArgumentList().containsProperty(IN_HQL_WHERE_OPT) ? (String) serviceData.getArgumentList().getProperty(IN_HQL_WHERE_OPT) : null;
			
			log.debug("Buscando pessoas");

			List<QueryCondiction> condictions = new ArrayList<QueryCondiction>();
			QueryCondiction condiction;

			//filtrando por documento
			condiction = new QueryCondiction(this.getServiceManager().getEntityManager(),
					Pessoa.class,
					Pessoa.DOCUMENTO,
					Operator.EQUAL,
					inPessoaDocumento,
					"");
			condictions.add(condiction);

			
			log.debug("Executando a consulta");
			ServiceData sdQuery = new ServiceData(QueryService.SERVICE_NAME, serviceData);
			sdQuery.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, Pessoa.class);
			sdQuery.getArgumentList().setProperty(QueryService.IN_QUERY_SELECT, ListarPessoaQuery.QUERY_SELECT);
			sdQuery.getArgumentList().setProperty(QueryService.IN_QUERY_CONDICTIONS, condictions);
			if (StringUtils.isNotBlank(inHqlWhere)){
				sdQuery.getArgumentList().setProperty(QueryService.IN_QUERY_HQLWHERE, inHqlWhere);
			}
			/*ordena��o por nome*/
			sdQuery.getArgumentList().setProperty(QueryService.IN_ORDER_EXPRESSION_OPT, ListarPessoaQuery.QUERY_ORDER_NOME);

			this.getServiceManager().execute(sdQuery);

			/* Obtendo a lista de Pessoas */
			List<ListarPessoaQuery> beanList = (List<ListarPessoaQuery>) sdQuery.getOutputData(QueryService.OUT_OBJECT_LIST);

			serviceData.getOutputData().add(beanList);

		} catch (BusinessException e) {
			log.fatal(e.getErrorList());
			/* O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros. */
			throw new ServiceException(e.getErrorList());
		} catch (Exception e) {
			log.fatal(e.getMessage());
			/* Indica que o servi�o falhou por causa de uma exce��o do hibernate. */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
	}
	
}
