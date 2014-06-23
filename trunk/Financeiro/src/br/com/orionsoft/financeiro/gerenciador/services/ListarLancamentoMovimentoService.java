package br.com.orionsoft.financeiro.gerenciador.services;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.HibernateException;

import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.NativeSQL;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Serviço que lista os movimentos dos lançamentos.
 * 
 * <p>
 * <b>Argumento:</b><br>
 * 
 * <p>
 * <b>Procedimento:</b>
 * 
 * @author Antonio Alves
 * @version 20070430
 * 
 * @spring.bean id="ListarLancamentoMovimentoService"
 *              init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class ListarLancamentoMovimentoService extends ServiceBasic {
	public class QueryLancamentoMovimento {
		public static final String SELECTED = "selected";
		public static final String ID = "id";
		public static final String ESTORNADO = "estornado";
		public static final String DATA_LANCAMENTO = "dataLancamento";
		public static final String DATA_QUITACAO = "dataQuitacao";
		public static final String DATA_COMPENSACAO = "dataCompensacao";
		public static final String DATA_VENCIMENTO = "dataVencimento";
		public static final String VALOR_MOVIMENTO = "valorMovimento";
		public static final String VALOR_JUROS = "valorJuros";
		public static final String VALOR_MULTA = "valorMulta";
		public static final String VALOR_DESCONTO = "valorDesconto";
		public static final String VALOR_ACRESCIMO = "valorAcrescimo";
		public static final String VALOR_TOTAL = "valorTotal";
		public static final String VALOR_ITEM = "valorItem";
		public static final String PESO = "peso";
		public static final String VALOR_CONTABIL = "valorContabil";
		public static final String DESCRICAO = "descricao";
		public static final String LANCAMENTO_MOVIMENTO_CATEGORIA = "lancamentoMovimentoCategoria";
		public static final String CONTA = "conta";
		public static final String PESSOA = "pessoa";
		public static final String DOCUMENTO_PAGAMENTO_DATA = "documentoPagamentoData";
		public static final String DOCUMENTO_PAGAMENTO_DATA_VENCIMENTO = "documentoPagamentoDataVencimento";
		public static final String DOCUMENTO_PAGAMENTO_NUMERO_DOCUMENTO = "documentoPagamentoNumeroDocumento";
		public static final String DOCUMENTO_PAGAMENTO_CATEGORIA = "documentoPagamentoCategoria";
		public static final String CENTRO_CUSTO = "centroCusto";
		public static final String ITEM_CUSTO = "itemCusto";

		public static final String SELECT = ""
				+ "select "
				+ "  lancamentoMovimento.id, "
				+ "  lancamentoMovimento.estornado, "
				+ "  lancamento.data as dataLancamento, "
				+ "  lancamento.dataVencimento as dataVencimento, "
				+ "  lancamentoMovimento.data as dataQuitacao, "
				+ "  lancamentoMovimento.dataCompensacao as dataCompensacao, "
				+ "  lancamentoMovimento.valor as valorMovimento, "
				+ "  lancamentoMovimento.juros as valorJuros, "
				+ "  lancamentoMovimento.multa as valorMulta, "
				+ "  lancamentoMovimento.desconto as valorDesconto, "
				+ "  lancamentoMovimento.acrescimo as valorAcrescimo, "
				+ "  lancamentoMovimento.valorTotal as valorTotal, "
				+ "  lancamentoItem.valor as valorItem, "
				+ "  lancamentoItem.peso, "
				+ "  round(lancamentoMovimento.valor * lancamentoItem.peso, 2) as valorContabil, "
				+ "  lancamento.descricao, "
				+ "  lancamentoMovimento.lancamentoMovimentoCategoria, "
				+ "  conta.nome as conta, "
				+ "  if(lancamentoMovimento.lancamentoMovimentoCategoria = 'TRANSFERIDO', 'Transferência',pessoa.nome) as pessoa, "
				+ "  documentoPagamento.data as documentoPagamentoData, "
				+ "  documentoPagamento.dataVencimento as documentoPagamentoDataVencimento, "
				+ "  documentoPagamento.numeroDocumento as documentoPagamentoNumeroDocumento, "
				+ "  documentoPagamentoCategoria.nome as documentoPagamentoCategoria, "
				+ "  centroCusto.nome as centroCusto, "
				+ "  itemCusto.nome as itemCusto "
				+ "from financeiro_lancamento_movimento as lancamentoMovimento "
				+ "left outer join financeiro_lancamento as lancamento on "
				+ "  lancamento.id = lancamentoMovimento.lancamento "
				+ "left outer join financeiro_lancamento_item as lancamentoItem on "
				+ "  lancamentoItem.lancamento = lancamento.id "
				+ "left outer join financeiro_documento_pagamento as documentoPagamento on "
				+ "  documentoPagamento.id = lancamentoMovimento.documentoPagamento "
				+ "left outer join financeiro_documento_pagamento_categoria as documentoPagamentoCategoria on "
				+ "  documentoPagamentoCategoria.id = documentoPagamento.documentoPagamentoCategoria "
				+ "left outer join financeiro_conta as conta on "
				+ "  conta.id = lancamentoMovimento.conta "
				+ "left outer join basic_contrato as contrato on "
				+ "  contrato.id = lancamento.contrato "
				+ "left outer join basic_pessoa as pessoa on "
				+ "  pessoa.id = contrato.pessoa "
				+ "left outer join financeiro_centro_custo as centroCusto on "
				+ "  centroCusto.id = lancamentoItem.centroCusto "
				+ "left outer join financeiro_item_custo as itemCusto on "
				+ "  itemCusto.id = lancamentoItem.itemCusto ";

		private boolean selected = false;
		private Long id;
		private Boolean estornado;
		private Calendar dataLancamento;
		private Calendar dataQuitacao;
		private Calendar dataCompensacao;
		private Calendar dataVencimento;
		private BigDecimal valorMovimento;
		private BigDecimal valorJuros;
		private BigDecimal valorMulta;
		private BigDecimal valorDesconto;
		private BigDecimal valorAcrescimo;
		private BigDecimal valorTotal;
		private BigDecimal valorItem;
		private BigDecimal peso;
		private BigDecimal valorContabil;
		private String descricao;
		private String lancamentoMovimentoCategoria;
		private String conta;
		private String pessoa;
		private Calendar documentoPagamentoData;
		private Calendar documentoPagamentoDataVencimento;
		private String documentoPagamentoNumeroDocumento;
		private String documentoPagamentoCategoria;
		private String centroCusto;
		private String itemCusto;

		private QueryLancamentoMovimento(Long id, Boolean estornado,
				Calendar dataLancamento, Calendar dataQuitacao,
				Calendar dataCompensacao, Calendar dataVencimento,
				BigDecimal valorMovimento, BigDecimal valorJuros,
				BigDecimal valorMulta, BigDecimal valorDesconto,
				BigDecimal valorAcrescimo, BigDecimal valorTotal,
				BigDecimal valorItem, BigDecimal peso,
				BigDecimal valorContabil, String descricao,
				String lancamentoMovimentoCategoria, String conta,
				String pessoa, Calendar documentoPagamentoData,
				Calendar documentoPagamentoDataVencimento,
				String documentoPagamentoNumeroDocumento,
				String documentoPagamentoCategoria, String centroCusto,
				String itemCusto) {
			this.id = id;
			this.estornado = estornado;
			this.dataLancamento = dataLancamento;
			this.dataQuitacao = dataQuitacao;
			this.dataCompensacao = dataCompensacao;
			this.dataVencimento = dataVencimento;
			this.valorMovimento = valorMovimento;
			this.valorJuros = valorJuros;
			this.valorMulta = valorMulta;
			this.valorDesconto = valorDesconto;
			this.valorAcrescimo = valorAcrescimo;
			this.valorTotal = valorTotal;
			this.valorItem = valorItem;
			this.peso = peso;
			this.valorContabil = valorContabil;
			this.descricao = descricao;
			this.lancamentoMovimentoCategoria = lancamentoMovimentoCategoria;
			this.conta = conta;
			this.pessoa = pessoa;
			this.documentoPagamentoData = documentoPagamentoData;
			this.documentoPagamentoDataVencimento = documentoPagamentoDataVencimento;
			this.documentoPagamentoNumeroDocumento = documentoPagamentoNumeroDocumento;
			this.documentoPagamentoCategoria = documentoPagamentoCategoria;
			this.centroCusto = centroCusto;
			this.itemCusto = itemCusto;
		}

		public String getConta() {
			return conta;
		}

		public Calendar getDataLancamento() {
			return dataLancamento;
		}

		public Calendar getDataQuitacao() {
			return dataQuitacao;
		}

		public Calendar getDataCompensacao() {
			return dataCompensacao;
		}

		public boolean isCompensado() {
			return dataCompensacao != null;
		}

		public Calendar getDataVencimento() {
			return dataVencimento;
		}

		public String getDescricao() {
			return descricao;
		}

		public String getDocumentoPagamentoCategoria() {
			return documentoPagamentoCategoria;
		}

		public Calendar getDocumentoPagamentoData() {
			return documentoPagamentoData;
		}

		public Calendar getDocumentoPagamentoDataVencimento() {
			return documentoPagamentoDataVencimento;
		}

		public String getDocumentoPagamentoNumeroDocumento() {
			return documentoPagamentoNumeroDocumento;
		}

		public Boolean getEstornado() {
			return estornado;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public boolean isSelected() {
			return selected;
		}

		public Long getId() {
			return id;
		}

		public String getLancamentoMovimentoCategoria() {
			return lancamentoMovimentoCategoria;
		}

		public String getPessoa() {
			return pessoa;
		}

		public String getCentroCusto() {
			return centroCusto;
		}

		public String getItemCusto() {
			return itemCusto;
		}

		public BigDecimal getPeso() {
			return peso;
		}

		public BigDecimal getValorContabil() {
			return valorContabil;
		}

		public BigDecimal getValorItem() {
			return valorItem;
		}

		public BigDecimal getValorMovimento() {
			return valorMovimento;
		}

		public BigDecimal getValorTotal() {
			return valorTotal;
		}

		public void setValorTotal(BigDecimal valorTotal) {
			this.valorTotal = valorTotal;
		}

		public BigDecimal getValorJuros() {
			return valorJuros;
		}

		public void setValorJuros(BigDecimal valorJuros) {
			this.valorJuros = valorJuros;
		}

		public BigDecimal getValorMulta() {
			return valorMulta;
		}

		public void setValorMulta(BigDecimal valorMulta) {
			this.valorMulta = valorMulta;
		}

		public BigDecimal getValorDesconto() {
			return valorDesconto;
		}

		public void setValorDesconto(BigDecimal valorDesconto) {
			this.valorDesconto = valorDesconto;
		}

		public BigDecimal getValorAcrescimo() {
			return valorAcrescimo;
		}

		public void setValorAcrescimo(BigDecimal valorAcrescimo) {
			this.valorAcrescimo = valorAcrescimo;
		}
	}

	public static final String SERVICE_NAME = "ListarLancamentoMovimentoService";

	/*
	 * Parâmetros de entrada do serviço.
	 */
	public static final String IN_CONTA_ID_OPT = "conta";
	public static final String IN_CONTRATO_ID_OPT = "contrato";
	public static final String IN_DATA_LANCAMENTO_INICIAL_OPT = "dataLancamentoInicial";
	public static final String IN_DATA_LANCAMENTO_FINAL_OPT = "dataLancamentoFinal";
	public static final String IN_DATA_QUITACAO_INICIAL_OPT = "dataQuitacaoInicial";
	public static final String IN_DATA_QUITACAO_FINAL_OPT = "dataQuitacaoFinal";
	public static final String IN_DATA_COMPENSACAO_INICIAL_OPT = "dataCompensacaoInicial";
	public static final String IN_DATA_COMPENSACAO_FINAL_OPT = "dataCompensacaoFinal";
	public static final String IN_DATA_VENCIMENTO_INICIAL_OPT = "dataVencimentoInicial";
	public static final String IN_DATA_VENCIMENTO_FINAL_OPT = "dataVencimentoFinal";
	public static final String IN_DOCUMENTO_PAGAMENTO_CATEGORIA_OPT = "documentoPagamentoCategoria";
	public static final String IN_CENTRO_CUSTO_ID_OPT = "centroCusto";
	public static final String IN_ITEM_CUSTO_LIST_OPT = "itemCusto";
	public static final String IN_ITEM_CUSTO_LIST_NOT_OPT = "itemCustoNot";
	public static final String IN_TRANSACAO_OPT = "transacao";
	public static final String IN_LISTAGEM_OPT = "listagem";
	public static final String IN_ORDEM_OPT = "ordem";
	public static final String IN_APPLICATION_USER_OPT = "applicationUser";

	/*
	 * Enum usado nos parâmetros de entrada do serviço.
	 */
	public enum Listagem {
		COMPLETA("Completa"), SEM_TRANSFERENCIAS("Sem transferências"), SEM_ESTORNOS(
				"Sem estornos"), SEM_TRANSFERENCIAS_SEM_ESTORNOS(
				"Sem transferências e sem estornos"), SOMENTE_TRANSFERENCIAS(
				"Somente as transferências"), SOMENTE_ESTORNOS(
				"Somente os estornos"), SOMENTE_NAO_COMPENSADOS(
				"Somente movimentos não compensados"), SOMENTE_COMPENSADOS_NO_PERIODO(
				"Somente compensados no período");

		private String nome;

		private Listagem(String nome) {
			this.nome = nome;
		}

		public String getNome() {
			return nome;
		}

		public String toString() {
			return nome;
		}
	}

	public enum Ordem {
		DATA("Data"), NOME("Nome"), DESCRICAO("Descrição"), VALOR("Valor"), LANCAMENTO(
				"Lançamento");

		private String nome;

		private Ordem(String nome) {
			this.nome = nome;
		}

		public String getNome() {
			return nome;
		}

		public String toString() {
			return nome;
		}
	}

	public String getServiceName() {
		return SERVICE_NAME;
	}

	public void execute(ServiceData serviceData) throws ServiceException {
		/*
		 * Obrigatórios
		 */
		int[] inTransacao = (int[]) serviceData.getArgumentList().getProperty(
				IN_TRANSACAO_OPT);

		/*
		 * Parâmetros opcionais.
		 */
		Long inContrato = (serviceData.getArgumentList().containsProperty(
				IN_CONTRATO_ID_OPT) ? (Long) serviceData.getArgumentList()
				.getProperty(IN_CONTRATO_ID_OPT) : null);
		Calendar inDataLancamentoInicial = (serviceData.getArgumentList()
				.containsProperty(IN_DATA_LANCAMENTO_INICIAL_OPT) ? (Calendar) serviceData
				.getArgumentList().getProperty(IN_DATA_LANCAMENTO_INICIAL_OPT)
				: null);
		Calendar inDataLancamentoFinal = (serviceData.getArgumentList()
				.containsProperty(IN_DATA_LANCAMENTO_FINAL_OPT) ? (Calendar) serviceData
				.getArgumentList().getProperty(IN_DATA_LANCAMENTO_FINAL_OPT)
				: null);
		Calendar inDataQuitacaoInicial = (serviceData.getArgumentList()
				.containsProperty(IN_DATA_QUITACAO_INICIAL_OPT) ? (Calendar) serviceData
				.getArgumentList().getProperty(IN_DATA_QUITACAO_INICIAL_OPT)
				: null);
		Calendar inDataQuitacaoFinal = (serviceData.getArgumentList()
				.containsProperty(IN_DATA_QUITACAO_FINAL_OPT) ? (Calendar) serviceData
				.getArgumentList().getProperty(IN_DATA_QUITACAO_FINAL_OPT)
				: null);
		Calendar inDataCompensacaoInicial = (serviceData.getArgumentList()
				.containsProperty(IN_DATA_COMPENSACAO_INICIAL_OPT) ? (Calendar) serviceData
				.getArgumentList().getProperty(IN_DATA_COMPENSACAO_INICIAL_OPT)
				: null);
		Calendar inDataCompensacaoFinal = (serviceData.getArgumentList()
				.containsProperty(IN_DATA_COMPENSACAO_FINAL_OPT) ? (Calendar) serviceData
				.getArgumentList().getProperty(IN_DATA_COMPENSACAO_FINAL_OPT)
				: null);
		Calendar inDataVencimentoInicial = (serviceData.getArgumentList()
				.containsProperty(IN_DATA_VENCIMENTO_INICIAL_OPT) ? (Calendar) serviceData
				.getArgumentList().getProperty(IN_DATA_VENCIMENTO_INICIAL_OPT)
				: null);
		Calendar inDataVencimentoFinal = (serviceData.getArgumentList()
				.containsProperty(IN_DATA_VENCIMENTO_FINAL_OPT) ? (Calendar) serviceData
				.getArgumentList().getProperty(IN_DATA_VENCIMENTO_FINAL_OPT)
				: null);
		Long inContaId = (serviceData.getArgumentList().containsProperty(
				IN_CONTA_ID_OPT) ? (Long) serviceData.getArgumentList()
				.getProperty(IN_CONTA_ID_OPT) : IDAO.ENTITY_UNSAVED);
		Long inCentroCustoId = (serviceData.getArgumentList().containsProperty(
				IN_CENTRO_CUSTO_ID_OPT) ? (Long) serviceData.getArgumentList()
				.getProperty(IN_CENTRO_CUSTO_ID_OPT) : null);
		Long[] inItemCustoList = (serviceData.getArgumentList()
				.containsProperty(IN_ITEM_CUSTO_LIST_OPT) ? (Long[]) serviceData
				.getArgumentList().getProperty(IN_ITEM_CUSTO_LIST_OPT) : null);
		Boolean inItemCustoListNot = (serviceData.getArgumentList()
				.containsProperty(IN_ITEM_CUSTO_LIST_NOT_OPT) ? (Boolean) serviceData
				.getArgumentList().getProperty(IN_ITEM_CUSTO_LIST_NOT_OPT)
				: false);
		Long inDocumentoPagamentoCategoriaId = (serviceData.getArgumentList()
				.containsProperty(IN_DOCUMENTO_PAGAMENTO_CATEGORIA_OPT) ? (Long) serviceData
				.getArgumentList().getProperty(
						IN_DOCUMENTO_PAGAMENTO_CATEGORIA_OPT) : null);
		Listagem inTipoListagem = (serviceData.getArgumentList()
				.containsProperty(IN_LISTAGEM_OPT) ? (Listagem) serviceData
				.getArgumentList().getProperty(IN_LISTAGEM_OPT)
				: Listagem.COMPLETA);
		Ordem inOrdem = (serviceData.getArgumentList().containsProperty(
				IN_ORDEM_OPT) ? (Ordem) serviceData.getArgumentList()
				.getProperty(IN_ORDEM_OPT) : Ordem.DATA);

		ApplicationUser inApplicationUser = (serviceData.getArgumentList()
				.containsProperty(IN_APPLICATION_USER_OPT) ? (ApplicationUser) serviceData
				.getArgumentList().getProperty(IN_APPLICATION_USER_OPT) : null);

		try {
			/*
			 * Cria a SQL com o código pré-definido.
			 */
			NativeSQL sql = new NativeSQL(serviceData.getCurrentSession(),
					QueryLancamentoMovimento.SELECT, null, // where
					null, // having
					null, // order
					null); // group

			/*
			 * Adiciona as clausulás WHERE à SQL.
			 */

			/*
			 * Lucio 20100711: Filtra os movimento das contas que o atual
			 * operador possui direito
			 */
			if (inApplicationUser != null) {
				sql.addWhere("(lancamentoMovimento.conta in (select fcu.conta from financeiro_conta_user fcu where fcu.applicationUser = :applicationUserId))");
				sql.setLong("applicationUserId", inApplicationUser.getId());
			}

			/* centro de custo */
			if ((inCentroCustoId != null)
					&& (inCentroCustoId != IDAO.ENTITY_UNSAVED)) {
				sql.addWhere("(lancamentoItem.centroCusto = :centroCusto)");
				sql.setLong("centroCusto", inCentroCustoId);
			}

			/* item de custo */
			if (!ArrayUtils.isEmpty(inItemCustoList)) {
				if (inItemCustoListNot)
					sql.addWhere("(lancamentoItem.itemCusto not in "
							+ Arrays.toString(inItemCustoList).replace('[', '(').replace(']', ')') + ")");
				else
					sql.addWhere("(lancamentoItem.itemCusto in "
							+ Arrays.toString(inItemCustoList).replace('[', '(').replace(']', ')') + ")");
			}

			/* contrato */
			if (inContrato != null) {
				sql.addWhere("(contrato.id = :contrato)");
				sql.setLong("contrato", inContrato);
			}

			/* data de lançamento */
			if (inDataLancamentoInicial != null
					&& inDataLancamentoFinal != null) {
				if (inTipoListagem == Listagem.SEM_TRANSFERENCIAS
						|| inTipoListagem == Listagem.SEM_TRANSFERENCIAS_SEM_ESTORNOS
						|| inTipoListagem == Listagem.SOMENTE_ESTORNOS) {
					sql.addWhere("(lancamento.data between :dataLancamentoInicial and :dataLancamentoFinal)");
				} else {
					sql.addWhere("((lancamento.data between :dataLancamentoInicial and :dataLancamentoFinal) or lancamento.data is null)");
				}
				sql.setCalendar("dataLancamentoInicial",
						inDataLancamentoInicial);
				sql.setCalendar("dataLancamentoFinal", inDataLancamentoFinal);
			}

			/* data de quitação */
			if (inDataQuitacaoInicial != null && inDataQuitacaoFinal != null) {
				sql.addWhere("(lancamentoMovimento.data between :dataQuitacaoInicial and :dataQuitacaoFinal)");
				sql.setCalendar("dataQuitacaoInicial", inDataQuitacaoInicial);
				sql.setCalendar("dataQuitacaoFinal", inDataQuitacaoFinal);
			}

			/* data de vencimento */
			if (inDataVencimentoInicial != null
					&& inDataVencimentoFinal != null) {
				if (inTipoListagem == Listagem.SEM_TRANSFERENCIAS
						|| inTipoListagem == Listagem.SEM_TRANSFERENCIAS_SEM_ESTORNOS
						|| inTipoListagem == Listagem.SOMENTE_ESTORNOS) {
					sql.addWhere("(lancamento.dataVencimento between :dataVencimentoInicial and :dataVencimentoFinal)");
				} else {
					sql.addWhere("((lancamento.dataVencimento between :dataVencimentoInicial and :dataVencimentoFinal) or lancamento.dataVencimento is null)");
				}
				sql.setCalendar("dataVencimentoInicial",
						inDataVencimentoInicial);
				sql.setCalendar("dataVencimentoFinal", inDataVencimentoFinal);
			}

			/* categoria do documento de cobrança */
			if (inDocumentoPagamentoCategoriaId != null) {
				sql.addWhere("(documentoPagamentoCategoria.id = :documentoPagamentoCategoria)");
				sql.setLong("documentoPagamentoCategoria",
						inDocumentoPagamentoCategoriaId);
			}

			/* transação */
			if (inTransacao.length == 1) {
				if (inTransacao[0] == Transacao.CREDITO.ordinal()) {
					sql.addWhere("(lancamentoMovimento.valor > 0)");
				}
				if (inTransacao[0] == Transacao.DEBITO.ordinal()) {
					sql.addWhere("(lancamentoMovimento.valor < 0)");
				}
			} else if (inTransacao.length == 0)
				sql.addWhere("(lancamentoMovimento.valor = 0)");

			/* tipo de listagem */
			sql.addWhere("(lancamentoMovimento.lancamentoMovimentoCategoria in (:lancamentoMovimentoCategoria))");
			LancamentoMovimentoCategoria[] lancamentoMovimentoCategoria = {
					LancamentoMovimentoCategoria.QUITADO,
					LancamentoMovimentoCategoria.QUITADO_ESTORNADO,
					LancamentoMovimentoCategoria.TRANSFERIDO };
			switch (inTipoListagem) {
			case COMPLETA:
				// Usa as categorias padrão
				break;
			case SEM_TRANSFERENCIAS:
				lancamentoMovimentoCategoria = new LancamentoMovimentoCategoria[] {
						LancamentoMovimentoCategoria.QUITADO,
						LancamentoMovimentoCategoria.QUITADO_ESTORNADO };
				break;
			case SEM_ESTORNOS:
				lancamentoMovimentoCategoria = new LancamentoMovimentoCategoria[] {
						LancamentoMovimentoCategoria.QUITADO,
						LancamentoMovimentoCategoria.TRANSFERIDO };
				break;
			case SEM_TRANSFERENCIAS_SEM_ESTORNOS:
				lancamentoMovimentoCategoria = new LancamentoMovimentoCategoria[] { LancamentoMovimentoCategoria.QUITADO };
				break;
			case SOMENTE_TRANSFERENCIAS:
				lancamentoMovimentoCategoria = new LancamentoMovimentoCategoria[] { LancamentoMovimentoCategoria.TRANSFERIDO };
				break;
			case SOMENTE_ESTORNOS:
				lancamentoMovimentoCategoria = new LancamentoMovimentoCategoria[] { LancamentoMovimentoCategoria.QUITADO_ESTORNADO };
				break;
			case SOMENTE_NAO_COMPENSADOS:
				// Usa as categorias padrão
				sql.addWhere("(lancamentoMovimento.dataCompensacao IS NULL)");
				break;
			case SOMENTE_COMPENSADOS_NO_PERIODO:
				// Usa as categorias padrão

				/* data de compensação */
				if ((inDataCompensacaoInicial != null)
						&& (inDataCompensacaoFinal != null)) {
					sql.addWhere("(lancamentoMovimento.dataCompensacao between :dataCompensacaoInicial and :dataCompensacaoFinal)");
					sql.setCalendar("dataCompensacaoInicial",
							inDataCompensacaoInicial);
					sql.setCalendar("dataCompensacaoFinal",
							inDataCompensacaoFinal);
				} else {
					sql.addWhere("(lancamentoMovimento.dataCompensacao IS NOT NULL)");
				}
				break;
			default:
				break;
			}
			sql.setArrayObject("lancamentoMovimentoCategoria",
					lancamentoMovimentoCategoria);

			// if (inTipoListagem == Listagem.CANCELADO) {
			// sql.addWhere("(lancamentoMovimento.lancamentoMovimentoCategoria in (:lancamentoMovimentoCategoria))");
			// LancamentoMovimentoCategoria[]
			// lancamentoMovimentoCategoriaCancelado =
			// {LancamentoMovimentoCategoria.CANCELADO,
			// LancamentoMovimentoCategoria.CANCELADO_ESTORNADO};
			// sql.setArrayObject("lancamentoMovimentoCategoria",
			// lancamentoMovimentoCategoriaCancelado);
			// }

			/* conta */
			if (inContaId != IDAO.ENTITY_UNSAVED) {
				sql.addWhere("(lancamentoMovimento.conta = :conta)");
				sql.setLong("conta", inContaId);
			}
			// if (inContaList != null && !inContaList.isEmpty()) {
			// List<Long> ids = new ArrayList<Long>(0);
			// for (Conta conta : inContaList) {
			// ids.add(conta.getId());
			// }
			// sql.addWhere("(lancamentoMovimento.conta in (:conta))");
			// sql.setListLong("conta", ids);
			// }

			/*
			 * Adiciona a clausula ORDER a SQL
			 */
			if (inOrdem == Ordem.DATA) {
				sql.addOrder("lancamentoMovimento.data");
				sql.addOrder("lancamentoMovimento.id");
			}
			if (inOrdem == Ordem.DESCRICAO) {
				sql.addOrder("lancamentoMovimento.descricao");
				sql.addOrder("lancamentoMovimento.data");
			}
			if (inOrdem == Ordem.NOME) {
				sql.addOrder("pessoa.nome");
				sql.addOrder("lancamentoMovimento.data");
			}
			if (inOrdem == Ordem.VALOR) {
				sql.addOrder("lancamentoMovimento.valorTotal");
				sql.addOrder("lancamentoMovimento.data");
				sql.addOrder("pessoa.nome");
			}
			if (inOrdem == Ordem.LANCAMENTO) {
				sql.addOrder("lancamentoMovimento.id");
			}

			/*
			 * Executa a SQL e pega o resultado.
			 */
			System.out.println(sql.getSql());
			ResultSet rs = sql.executeQuery();
			List<QueryLancamentoMovimento> lancamentoMovimentoList = new ArrayList<QueryLancamentoMovimento>(
					0);
			Long id = new Long(IDAO.ENTITY_UNSAVED);
			while (rs.next()) {
				if (!id.equals(rs.getLong(QueryLancamentoMovimento.ID))) {
					QueryLancamentoMovimento lancamentoMovimento = new QueryLancamentoMovimento(
							rs.getLong(QueryLancamentoMovimento.ID),
							rs.getBoolean(QueryLancamentoMovimento.ESTORNADO),
							CalendarUtils.getCalendar(rs
									.getDate(QueryLancamentoMovimento.DATA_LANCAMENTO)),
							CalendarUtils.getCalendar(rs
									.getDate(QueryLancamentoMovimento.DATA_QUITACAO)),
							CalendarUtils.getCalendar(rs
									.getDate(QueryLancamentoMovimento.DATA_COMPENSACAO)),
							CalendarUtils.getCalendar(rs
									.getDate(QueryLancamentoMovimento.DATA_VENCIMENTO)),
							rs.getBigDecimal(QueryLancamentoMovimento.VALOR_MOVIMENTO),
							rs.getBigDecimal(QueryLancamentoMovimento.VALOR_JUROS),
							rs.getBigDecimal(QueryLancamentoMovimento.VALOR_MULTA),
							rs.getBigDecimal(QueryLancamentoMovimento.VALOR_DESCONTO),
							rs.getBigDecimal(QueryLancamentoMovimento.VALOR_ACRESCIMO),
							rs.getBigDecimal(QueryLancamentoMovimento.VALOR_TOTAL),
							rs.getBigDecimal(QueryLancamentoMovimento.VALOR_ITEM),
							rs.getBigDecimal(QueryLancamentoMovimento.PESO),
							rs.getBigDecimal(QueryLancamentoMovimento.VALOR_CONTABIL),
							rs.getString(QueryLancamentoMovimento.DESCRICAO),
							rs.getString(QueryLancamentoMovimento.LANCAMENTO_MOVIMENTO_CATEGORIA),
							rs.getString(QueryLancamentoMovimento.CONTA),
							rs.getString(QueryLancamentoMovimento.PESSOA),
							CalendarUtils.getCalendar(rs
									.getDate(QueryLancamentoMovimento.DOCUMENTO_PAGAMENTO_DATA)),
							CalendarUtils.getCalendar(rs
									.getDate(QueryLancamentoMovimento.DOCUMENTO_PAGAMENTO_DATA_VENCIMENTO)),
							rs.getString(QueryLancamentoMovimento.DOCUMENTO_PAGAMENTO_NUMERO_DOCUMENTO),
							rs.getString(QueryLancamentoMovimento.DOCUMENTO_PAGAMENTO_CATEGORIA),
							rs.getString(QueryLancamentoMovimento.CENTRO_CUSTO),
							rs.getString(QueryLancamentoMovimento.ITEM_CUSTO));
					lancamentoMovimentoList.add(lancamentoMovimento);
				}
				id = rs.getLong(QueryLancamentoMovimento.ID);
			}

			serviceData.getOutputData().add(lancamentoMovimentoList);

		} catch (HibernateException e) {
			throw new ServiceException(MessageList.createSingleInternalError(e));
		} catch (SQLException e) {
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
	}

	/*
	 * Teste de listagem de objetos por exemplo. Não funcionou.
	 */
	// public void execute(ServiceData serviceData) throws ServiceException {
	// try {
	// Conta conta = UtilsCrud.objectRetrieve(this.getServiceManager(),
	// Conta.class, 2l, null);
	//
	// LancamentoMovimento object = new LancamentoMovimento();
	// object.setConta(conta);
	//
	// Session session = serviceData.getCurrentSession();
	// Criteria criteria = session.createCriteria(LancamentoMovimento.class);
	// Criterion criterion = Example.create(object);
	// criteria.add(criterion);
	// List<LancamentoMovimento> list = criteria.list();
	//
	// for (LancamentoMovimento obj : list) {
	// System.out.println(obj.getId());
	// }
	//
	// } catch (BusinessException e) {
	// throw new ServiceException(MessageList.createSingleInternalError(e));
	// }
	// }
	
	public static void main(String[] args) {
		long[] l = new long[]{1,2,3};
		System.out.println(Arrays.toString(l).replace('[', '(').replace(']', ')'));
	}
}
