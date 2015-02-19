/*
 * Created on 22/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.gerenciador.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoSituacao;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService.Situacao;
import br.com.orionsoft.financeiro.utils.UtilsConta;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.Operator;
import br.com.orionsoft.monstrengo.crud.services.QueryCondiction;
import br.com.orionsoft.monstrengo.crud.services.QueryService;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Serviço que lista a posição financeira de um contrato.
 * IN_DOCUMENT_OPT pode ser parcial, ou seja, você pode digitar o inicio do CNPJ e pegar todas as filiais!
 * @return EntityList 
 *
 * @author antonio
 * @version 20120521
 */
public class ListarPosicaoContratoService extends ServiceBasic {
    public static final String SERVICE_NAME = "ListarPosicaoContratoService";

    public static final String IN_DOCUMENTO_OPT = "documento";
    public static final String IN_PESSOA_OPT = "pessoa";
    public static final String IN_CONTRATO_OPT = "contrato";
    public static final String IN_ESCRITORIO_CONTABIL_ID_OPT = "escritorioContabilId";
    public static final String IN_CONTA_LIST_OPT = "contaList";
    public static final String IN_ITEM_CUSTO_IDS_OPT = "itemCustoIds";
    public static final String IN_ITEM_CUSTO_LIST_NOT_OPT = "itemCustoListNot";
    public static final String IN_DATA_INICIAL_OPT = "dataInicial";
    public static final String IN_DATA_FINAL_OPT = "dataFinal";
    public static final String IN_SITUACAO_OPT = "situacao";
    public static final String IN_APPLICATION_USER_OPT = "applicationUser";

    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException {
        log.debug("::Iniciando a execução do serviço ListarPosicaoContratoService");

        try {
        	/*
        	 * Parâmetros 
        	 */
        	String inDocumento = (serviceData.getArgumentList().containsProperty(IN_DOCUMENTO_OPT) ? 
        		(String) serviceData.getArgumentList().getProperty(IN_DOCUMENTO_OPT): null);
        	
        	Pessoa inPessoa = (serviceData.getArgumentList().containsProperty(IN_PESSOA_OPT) ? 
            		(Pessoa) serviceData.getArgumentList().getProperty(IN_PESSOA_OPT): null);

        	Contrato inContrato = (serviceData.getArgumentList().containsProperty(IN_CONTRATO_OPT) ? 
            		(Contrato) serviceData.getArgumentList().getProperty(IN_CONTRATO_OPT): null);

        	Long inEscritorioContabilId = (serviceData.getArgumentList().containsProperty(IN_ESCRITORIO_CONTABIL_ID_OPT) ?
        			(Long)serviceData.getArgumentList().getProperty(IN_ESCRITORIO_CONTABIL_ID_OPT) : null);

        	@SuppressWarnings("unchecked")
			IEntityList<Conta> inContas = (serviceData.getArgumentList().containsProperty(IN_CONTA_LIST_OPT) ?
        			(IEntityList<Conta>) serviceData.getArgumentList().getProperty(IN_CONTA_LIST_OPT) : null);
        	
        	Long[] inItemCustoIds = (serviceData.getArgumentList().containsProperty(IN_ITEM_CUSTO_IDS_OPT) ?
        			(Long[])serviceData.getArgumentList().getProperty(IN_ITEM_CUSTO_IDS_OPT) : null);
        	Boolean inItemCustoListNot = (serviceData.getArgumentList().containsProperty(IN_ITEM_CUSTO_LIST_NOT_OPT) ? 
        			(Boolean) serviceData.getArgumentList().getProperty(IN_ITEM_CUSTO_LIST_NOT_OPT) : false);
        	Calendar inDataInicial = (serviceData.getArgumentList().containsProperty(IN_DATA_INICIAL_OPT) ? 
        			(Calendar) serviceData.getArgumentList().getProperty(IN_DATA_INICIAL_OPT) : null);
        	Calendar inDataFinal = (serviceData.getArgumentList().containsProperty(IN_DATA_FINAL_OPT) ? 
        			(Calendar) serviceData.getArgumentList().getProperty(IN_DATA_FINAL_OPT) : null);
        	Situacao inSituacao = (serviceData.getArgumentList().containsProperty(IN_SITUACAO_OPT) ? 
        			(Situacao) serviceData.getArgumentList().getProperty(IN_SITUACAO_OPT) : Situacao.TODOS);

        	ApplicationUser inApplicationUser = (serviceData.getArgumentList().containsProperty(IN_APPLICATION_USER_OPT) ?
        			(ApplicationUser) serviceData.getArgumentList().getProperty(IN_APPLICATION_USER_OPT) : null);

        	/*
        	 * Cria uma instância da data atual e a lista de QueryCondiction.
        	 */
        	Calendar dataAtual = CalendarUtils.getCalendar();
        	List<QueryCondiction> condictionList = new ArrayList<QueryCondiction>();
			QueryCondiction condiction;
			
			/*
			 * CNPJ, inclusive parciais para pegar todas as filiais.
			 */
			if (StringUtils.isNotBlank(inDocumento)) {
				condiction = new QueryCondiction(
						this.getServiceManager().getEntityManager(),
						Lancamento.class,
						Lancamento.CONTRATO + "." + Contrato.PESSOA + "." + Pessoa.DOCUMENTO,
						Operator.LIKE,
						inDocumento + "*",
				"");
				condiction.setInitOperator(QueryCondiction.INIT_AND);
				condictionList.add(condiction);
			}

			/*
			 * Pessoa.
			 */
			if (inPessoa != null) {
				condiction = new QueryCondiction(
						this.getServiceManager().getEntityManager(),
						Lancamento.class,
						Lancamento.CONTRATO + "." + Contrato.PESSOA,
						Operator.EQUAL,
						inPessoa.getId()+"",
				"");
				condiction.setInitOperator(QueryCondiction.INIT_AND);
				condictionList.add(condiction);
			}

			/*
			 * Contrato.
			 */
			if (inContrato != null) {
				condiction = new QueryCondiction(
						this.getServiceManager().getEntityManager(),
						Lancamento.class,
						Lancamento.CONTRATO,
						Operator.EQUAL,
						inContrato.getId()+"",
				"");
				condiction.setInitOperator(QueryCondiction.INIT_AND);
				condictionList.add(condiction);
			}

			/*
			 * Escritório Contabil.
			 */
			if (inEscritorioContabilId != null) {
				condiction = new QueryCondiction(
						this.getServiceManager().getEntityManager(),
						Lancamento.class,
						Lancamento.CONTRATO + "." + Contrato.PESSOA + "." + Pessoa.ESCRITORIO_CONTABIL,
						Operator.EQUAL,
						inEscritorioContabilId.toString(),
				"");
				condiction.setInitOperator(QueryCondiction.INIT_AND);
				condictionList.add(condiction);
			}

			/*
			 * Lista de contas
			 */
			if ((inContas !=null) && (inContas.size() > 0)) {
				StringBuilder contaList = new StringBuilder();
				for (IEntity<Conta> conta: inContas) {
					/* Pega somente os itens selecionados */
					if(conta.isSelected()){
						contaList.append(conta.getId());
						contaList.append(",");
					}
				}
				
				if(contaList.length()>0){
					contaList.setLength(contaList.length()-1); // remove a última vírgula

					condiction = new QueryCondiction(
							this.getServiceManager().getEntityManager(),
							Lancamento.class,
							//Lancamento.LANCAMENTO_MOVIMENTOS+"."+LancamentoMovimento.CONTA,
							Lancamento.CONTA_PREVISTA,
							Operator.IN,
							contaList.toString(),
							"");
					condiction.setInitOperator(QueryCondiction.INIT_AND);
					condictionList.add(condiction);
				}
			}
			
			/*
			 * Lista de item de custo
			 */
			int operator = Operator.IN;
			if (inItemCustoListNot)
				operator = Operator.NOT_IN;
			if ((inItemCustoIds != null) && (inItemCustoIds.length>0)) {
				condiction = new QueryCondiction(
						this.getServiceManager().getEntityManager(),
						Lancamento.class,
						Lancamento.LANCAMENTO_ITENS+'.'+LancamentoItem.ITEM_CUSTO,
						operator,
						Arrays.toString(inItemCustoIds).replace("[", "").replace("]", ""),
						"");
				condiction.setInitOperator(QueryCondiction.INIT_AND);
				condictionList.add(condiction);
			}
			
			/*
			 * Data inicial e final.
			 */
			if (inDataInicial != null && inDataFinal != null) {
				condiction = new QueryCondiction(
						this.getServiceManager().getEntityManager(),
						Lancamento.class,
						Lancamento.DATA_VENCIMENTO,
						Operator.BETWEEN,
						CalendarUtils.formatDate(inDataInicial),
						CalendarUtils.formatDate(inDataFinal));
				condiction.setInitOperator(QueryCondiction.INIT_AND);
				condictionList.add(condiction);
			}
			
			/*
			 * Situação.
			 *
			 * FIXME quando a situação for TODOS vai mostrar até os naoReceberAposVencimento que já venceram.
			 * 
			 * PENDENTE: 
			 * (lancamentoSituacao = PENDENTE and 
			 *  naoReceberAposVencimento = false) or 
			 * (lancamentoSituacao = PENDENTE and 
			 *  naoReceberAposVencimento = true and 
			 *  dataVencimento >= dataAtual) 
			 *
			 * VENCIDO:
			 * (lancamentoSituacao = PENDENTE and 
			 *  dataVencimento < dataAtual and 
			 *  naoReceberAposVencimento = false)
			 *
			 * QUITADO:
			 * (lancamentoSituacao = QUITADO)
			 * 
			 */
			if (inSituacao == Situacao.PENDENTE) {
				/*
				 * (lancamentoSituacao = PENDENTE and naoReceberAposVencimento = false) or 
				 * (lancamentoSituacao = PENDENTE and naoReceberAposVencimento = true and dataVencimento >= dataAtual) 
				 */
				condiction = new QueryCondiction(
						this.getServiceManager().getEntityManager(),
						Lancamento.class,
						Lancamento.LANCAMENTO_SITUACAO,
						Operator.EQUAL,
						LancamentoSituacao.PENDENTE.name(),
						"");
				condiction.setInitOperator(QueryCondiction.INIT_AND);
				condiction.setOpenPar(true);
				condictionList.add(condiction);

				condiction = new QueryCondiction(
						this.getServiceManager().getEntityManager(),
						Lancamento.class,
						Lancamento.NAO_RECEBER_APOS_VENCIMENTO,
						Operator.EQUAL,
						"false",
						"");
				condiction.setInitOperator(QueryCondiction.INIT_AND);
				condiction.setClosePar(true);
				condictionList.add(condiction);

				condiction = new QueryCondiction(
						this.getServiceManager().getEntityManager(),
						Lancamento.class,
						Lancamento.LANCAMENTO_SITUACAO,
						Operator.EQUAL,
						LancamentoSituacao.PENDENTE.name(),
						"");
				condiction.setInitOperator(QueryCondiction.INIT_OR);
				condiction.setOpenPar(true);
				condictionList.add(condiction);

				condiction = new QueryCondiction(
						this.getServiceManager().getEntityManager(),
						Lancamento.class,
						Lancamento.NAO_RECEBER_APOS_VENCIMENTO,
						Operator.EQUAL,
						"true",
						"");
				condiction.setInitOperator(QueryCondiction.INIT_AND);
				condictionList.add(condiction);

				condiction = new QueryCondiction(
						this.getServiceManager().getEntityManager(),
						Lancamento.class,
						Lancamento.DATA_VENCIMENTO,
						Operator.MORE_EQUAL,
						CalendarUtils.formatDate(dataAtual),
						"");
				condiction.setInitOperator(QueryCondiction.INIT_AND);
				condiction.setClosePar(true);
				condictionList.add(condiction);

			}
			
			if (inSituacao == Situacao.VENCIDO) {
				/*
				 * (lancamentoSituacao = PENDENTE and dataVencimento < dataAtual and naoReceberAposVencimento = false)
				 */
				condiction = new QueryCondiction(
						this.getServiceManager().getEntityManager(),
						Lancamento.class,
						Lancamento.LANCAMENTO_SITUACAO,
						Operator.EQUAL,
						LancamentoSituacao.PENDENTE.name(),
						"");
				condiction.setInitOperator(QueryCondiction.INIT_AND);
				condictionList.add(condiction);

				condiction = new QueryCondiction(
						this.getServiceManager().getEntityManager(),
						Lancamento.class,
						Lancamento.DATA_VENCIMENTO,
						Operator.LESS_THEN,
						CalendarUtils.formatDate(dataAtual),
						"");
				condiction.setInitOperator(QueryCondiction.INIT_AND);
				condictionList.add(condiction);

				condiction = new QueryCondiction(
						this.getServiceManager().getEntityManager(),
						Lancamento.class,
						Lancamento.NAO_RECEBER_APOS_VENCIMENTO,
						Operator.EQUAL,
						"false",
						"");
				condiction.setInitOperator(QueryCondiction.INIT_AND);
				condictionList.add(condiction);
			}
			if (inSituacao == Situacao.QUITADO) {
				/*
				 * (lancamentoSituacao = QUITADO)
				 */
				condiction = new QueryCondiction(
						this.getServiceManager().getEntityManager(),
						Lancamento.class,
						Lancamento.LANCAMENTO_SITUACAO,
						Operator.EQUAL,
						LancamentoSituacao.QUITADO.name(),
						"");
				condiction.setInitOperator(QueryCondiction.INIT_AND);
				condictionList.add(condiction);
			}
			
			ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, serviceData);
			sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, Lancamento.class);
			sd.getArgumentList().setProperty(QueryService.IN_QUERY_CONDICTIONS, condictionList);
			sd.getArgumentList().setProperty(QueryService.IN_ORDER_EXPRESSION_OPT,
					IDAO.ENTITY_ALIAS_HQL + "." + Lancamento.CONTRATO + "." + Contrato.PESSOA + "." + Pessoa.NOME + ", " +
					IDAO.ENTITY_ALIAS_HQL + "." + Lancamento.DATA + " DESC, " +
					IDAO.ENTITY_ALIAS_HQL + "." + IDAO.PROPERTY_ID_NAME + " DESC ");
			this.getServiceManager().execute(sd);
			
			EntityList<Lancamento> list = sd.getOutputData(QueryService.OUT_ENTITY_LIST);

			/*
			 * Lucio 20100611: Filtra os movimento das contas que o atual operador possui direito
			 * Lucio 20120619: Desmarca os IEntity.selected = FALSE
			 */
			for(Iterator<IEntity<Lancamento>> it = list.iterator(); it.hasNext();){
				IEntity<Lancamento> lancamento = it.next();
				lancamento.setSelected(false);
				
				if (inApplicationUser != null) {
					Lancamento oLancamento = lancamento.getObject();
					if(!UtilsConta.verificarDireitoAcesso(oLancamento.getContaPrevista(), inApplicationUser)){
						it.remove();
					}
				}
			}
			
			serviceData.getOutputData().add(list);
			
		} catch (BusinessException e) {
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args) {
		Long[] l = new Long[]{1l,3l};
		System.out.println(Arrays.toString(l));
	}
}
