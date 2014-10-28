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

import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.NativeSQL;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Serviço que lista os lançamentos.
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
 * @spring.bean id="ListarLancamentoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class ListarLancamentoService extends ServiceBasic {
	public class QueryLancamento {
		public static final String SELECTED = "selected";
		public static final String ID = "id";
		public static final String DATA = "data";
		public static final String DATA_VENCIMENTO = "dataVencimento";
		public static final String DESCRICAO = "descricao";
		public static final String SITUACAO = "situacao";
		public static final String SALDO = "saldo";
		public static final String VALOR = "valor";
//		public static final String OPERACAO = "operacao";
		public static final String CONTA_PREVISTA = "contaPrevista";
		public static final String PESSOA = "pessoa";
		public static final String PESSOA_DOCUMENTO = "pessoaDocumento";
//		public static final String DOCUMENTO_COBRANCA_CATEGORIA = "documentoCobrancaCategoria";
//		public static final String CENTRO_CUSTO = "centroCusto";
//		public static final String ITEM_CUSTO = "itemCusto";
//		public static final String CLASSIFICACAO_CONTABIL = "classificacaoContabil";
		
		public static final String SELECT = "" +
			"select " +
			"  lancamento.id id, " +
			"  lancamento.data data, " +
			"  lancamento.dataVencimento dataVencimento, " +
			"  lancamento.descricao descricao, " +
			"  lancamento.lancamentoSituacao situacao, " +
			"  lancamento.saldo saldo, " +
			"  lancamento.valor valor, " +	
//			"  lancamentoItem.valor valor, " +
//			"  operacao.nome operacao, " +
			"  conta.nome contaPrevista, " +
			"  pessoa.nome pessoa, " +
			"  pessoa.documento pessoaDocumento " +
//			"  documentoCobrancaCategoria.nome documentoCobrancaCategoria, " +
//			"  centroCusto.nome centroCusto, " +
//			"  itemCusto.nome itemCusto, " +
//			"  classificacaoContabil.nome classificacaoContabil " +
			"from financeiro_lancamento lancamento " +
			"inner join financeiro_lancamento_item as lancamentoItem on " +
			"  lancamentoItem.lancamento = lancamento.id " +
//			"left outer join financeiro_item_custo as itemCusto on " +
//			"  itemCusto.id = lancamentoItem.itemCusto " +
//			"left outer join financeiro_centro_custo as centroCusto on " +
//			"  centroCusto.id = lancamentoItem.centroCusto " +
//			"left outer join financeiro_operacao operacao on " +
//			"  operacao.id = operacao " +
			"left outer join financeiro_conta conta on " +
			"  conta.id = lancamento.contaPrevista " +
			"left outer join basic_contrato contrato on " +
			"  contrato.id = lancamento.contrato " +
			"left outer join basic_pessoa pessoa on " +
			"  pessoa.id = contrato.pessoa ";
//			"left outer join financeiro_documento_cobranca documentoCobranca on " +
//			"  documentoCobranca.id = lancamento.documentoCobranca " +
//			"left outer join financeiro_documento_cobranca_categoria documentoCobrancaCategoria on " +
//			"  documentoCobrancaCategoria.id = documentoCobranca.documentoCobrancaCategoria " +
//			"left outer join financeiro_classificacao_contabil as classificacaoContabil on " +
//			"  classificacaoContabil.id = lancamentoItem.classificacaoContabil ";

		private boolean selected = false;
		private Long id;
		private Calendar data;
		private Calendar dataVencimento;
		private String descricao;
		private String situacao;
		private BigDecimal saldo;
		private BigDecimal valor;
//		private String operacao;
		private String contaPrevista;
		private String pessoa;
		private String pessoaDocumento;
//		private String documentoCobrancaCategoria;
//		private String centroCusto;
//		private String itemCusto;
//		private String classificacaoContabil;
		
//		private QueryLancamento(Long id, Calendar data, Calendar dataVencimento, String descricao, String situacao, BigDecimal saldo, BigDecimal valor, String operacao, String contaPrevista, String pessoa, String documentoCobrancaCategoria, String centroCusto, String itemCusto, String classificacaoContabil) {
		private QueryLancamento(
				Long id, 
				Calendar data, 
				Calendar dataVencimento, 
				String descricao, 
				String situacao, 
				BigDecimal saldo, 
				BigDecimal valor, 
//				String operacao, 
				String contaPrevista, 
				String pessoa,
				String pessoaDocumento 
//				String documentoCobrancaCategoria, 
//				String centroCusto, 
//				String itemCusto, 
//				String classificacaoContabil
				) {
			super();
			this.id = id;
			this.data = data;
			this.dataVencimento = dataVencimento;
			this.descricao = descricao;
			this.situacao = situacao;
			this.saldo = saldo;
			this.valor = valor;
//			this.operacao = operacao;
			this.contaPrevista = contaPrevista;
			this.pessoa = pessoa;
			this.pessoaDocumento = pessoaDocumento;
//			this.documentoCobrancaCategoria = documentoCobrancaCategoria;
//			this.centroCusto = centroCusto;
//			this.itemCusto = itemCusto;
//			this.classificacaoContabil = classificacaoContabil;
		}

//		public String getCentroCusto() {
//			return centroCusto;
//		}

		public String getContaPrevista() {
			return contaPrevista;
		}

		public Calendar getData() {
			return data;
		}

		public Calendar getDataVencimento() {
			return dataVencimento;
		}

		public String getDescricao() {
			return descricao;
		}
		
		public String getSituacao() {
			return situacao;
		}

//		public String getDocumentoCobrancaCategoria() {
//			return documentoCobrancaCategoria;
//		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public boolean isSelected() {
			return selected;
		}

		public Long getId() {
			return id;
		}

//		public String getItemCusto() {
//			return itemCusto;
//		}

//		public String getOperacao() {
//			return operacao;
//		}

		public String getPessoa() {
			return pessoa;
		}
		
		public String getPessoaDocumento() {
			return pessoaDocumento;
		}

		public BigDecimal getSaldo() {
			return saldo;
		}

		public BigDecimal getValor() {
			return valor;
		}

//		public String getClassificacaoContabil() {
//			return classificacaoContabil;
//		}
	}
	
	public static final String SERVICE_NAME = "ListarLancamentoService";

	/*
	 * Parâmetros de entrada do serviço.
	 */
    public static final String IN_CONTA_OPT = "conta";
    public static final String IN_CONTRATO_ID_OPT = "contrato";
    public static final String IN_PESSOA_OPT = "pessoa";
    public static final String IN_DATA_INICIAL_OPT = "dataInicial";
    public static final String IN_DATA_FINAL_OPT = "dataFinal";
    public static final String IN_DATA_VENCIMENTO_INICIAL_OPT = "dataVencimentoInicial";
    public static final String IN_DATA_VENCIMENTO_FINAL_OPT = "dataVencimentoFinal";
    public static final String IN_DOCUMENTO_COBRANCA_CATEGORIA_OPT = "documentoCobrancaCategoria";
    public static final String IN_CENTRO_CUSTO_OPT = "centroCusto";
    public static final String IN_ITEM_CUSTO_LIST_OPT = "itemCusto";
    public static final String IN_ITEM_CUSTO_LIST_NOT_OPT = "itemCustoNot";
    public static final String IN_TRANSACAO_OPT = "transacao";
    public static final String IN_SITUACAO_OPT = "listagem";
    public static final String IN_ORDEM_OPT = "ordem";
    public static final String IN_APPLICATION_USER_OPT = "applicationUser";

    public enum Situacao {
    	TODOS("Todos"),
    	PENDENTE("Pendente"),
    	VENCIDO("Vencido"),
    	QUITADO("Quitado");
    	
    	private String nome;
    	
    	private Situacao(String nome) {
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
    	DATA("Data"),
    	DATA_VENCIMENTO("Vencimento"),
    	LANCAMENTO("Lançamento"),
    	NOME("Nome");
    	
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
		 * Parâmetros opcionais.
		 */
    	Pessoa inPessoa = (serviceData.getArgumentList().containsProperty(IN_PESSOA_OPT) ? 
        		(Pessoa) serviceData.getArgumentList().getProperty(IN_PESSOA_OPT): null);
		Long inContratoId = (serviceData.getArgumentList().containsProperty(IN_CONTRATO_ID_OPT) ? 
                (Long) serviceData.getArgumentList().getProperty(IN_CONTRATO_ID_OPT) : IDAO.ENTITY_UNSAVED);
        Calendar inDataInicial = (serviceData.getArgumentList().containsProperty(IN_DATA_INICIAL_OPT) ? 
                (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_INICIAL_OPT) : null);
        Calendar inDataFinal = (serviceData.getArgumentList().containsProperty(IN_DATA_FINAL_OPT) ? 
        		(Calendar) serviceData.getArgumentList().getProperty(IN_DATA_FINAL_OPT) : null);
        Calendar inDataVencimentoInicial = (serviceData.getArgumentList().containsProperty(IN_DATA_VENCIMENTO_INICIAL_OPT) ? 
                (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_VENCIMENTO_INICIAL_OPT) : null);
        Calendar inDataVencimentoFinal = (serviceData.getArgumentList().containsProperty(IN_DATA_VENCIMENTO_FINAL_OPT) ? 
        		(Calendar) serviceData.getArgumentList().getProperty(IN_DATA_VENCIMENTO_FINAL_OPT) : null);
        Long inConta = (serviceData.getArgumentList().containsProperty(IN_CONTA_OPT) ? 
                (Long) serviceData.getArgumentList().getProperty(IN_CONTA_OPT) : IDAO.ENTITY_UNSAVED);
        Long inCentroCusto = (serviceData.getArgumentList().containsProperty(IN_CENTRO_CUSTO_OPT) ? 
                (Long) serviceData.getArgumentList().getProperty(IN_CENTRO_CUSTO_OPT) : null);
        Boolean inItemCustoListNot = (serviceData.getArgumentList().containsProperty(IN_ITEM_CUSTO_LIST_NOT_OPT) ? 
                (Boolean) serviceData.getArgumentList().getProperty(IN_ITEM_CUSTO_LIST_NOT_OPT) : false);
        Long[] inItemCustoList = (serviceData.getArgumentList().containsProperty(IN_ITEM_CUSTO_LIST_OPT) ? 
                (Long[]) serviceData.getArgumentList().getProperty(IN_ITEM_CUSTO_LIST_OPT) : null);
        Integer inSituacao = (serviceData.getArgumentList().containsProperty(IN_SITUACAO_OPT) ? 
                (Integer) serviceData.getArgumentList().getProperty(IN_SITUACAO_OPT) : Situacao.TODOS.ordinal());
        Integer inOrdem = (Integer) (serviceData.getArgumentList().containsProperty(IN_ORDEM_OPT) ? 
                (Integer) serviceData.getArgumentList().getProperty(IN_ORDEM_OPT) : Ordem.DATA);
        
    	ApplicationUser inApplicationUser = (serviceData.getArgumentList().containsProperty(IN_APPLICATION_USER_OPT) ?
    			(ApplicationUser) serviceData.getArgumentList().getProperty(IN_APPLICATION_USER_OPT) : null);

		try {
			/*
			 * Cria a SQL com o código pré-definido.
			 */
			NativeSQL sql = new NativeSQL(serviceData.getCurrentSession(), 
					QueryLancamento.SELECT, 
					null, //where
					null, //having
					null, //order
					null); //
			
			/*
			 * Adiciona as clausulás WHERE à SQL.
			 */
			/*
			 * Lucio 20100711: Filtra os movimento das contas que o atual operador possui direito
			 */
			if (inApplicationUser != null) {
				sql.addWhere("(lancamento.contaPrevista in (select fcu.conta from financeiro_conta_user fcu where fcu.applicationUser = :applicationUserId))");
				sql.setLong("applicationUserId", inApplicationUser.getId());
			}

			/* documento (cpf/cnpj) */
			if (inPessoa != null) {
				sql.addWhere("(pessoa.documento = :documento)");
				sql.setString("documento", inPessoa.getDocumento());
			}
			/* contrato */
			if (inContratoId != IDAO.ENTITY_UNSAVED) {
				sql.addWhere("(contrato.id = :contrato)");
				sql.setLong("contrato", inContratoId);
			}
			/* data */
			if (inDataInicial != null && inDataFinal != null) {
				sql.addWhere("(lancamento.data between :dataInicial and :dataFinal)");
				sql.setCalendar("dataInicial", inDataInicial);
				sql.setCalendar("dataFinal", inDataFinal);
			}
			/* data de vencimento */
			if (inDataVencimentoInicial != null && inDataVencimentoFinal != null) {
				sql.addWhere("(lancamento.dataVencimento between :dataVencimentoInicial and :dataVencimentoFinal)");
				sql.setCalendar("dataVencimentoInicial", inDataVencimentoInicial);
				sql.setCalendar("dataVencimentoFinal", inDataVencimentoFinal);
			}
			/* conta */
			if (inConta != IDAO.ENTITY_UNSAVED) {
				sql.addWhere("(lancamento.contaPrevista = :conta)");
				sql.setLong("conta", inConta);
			}
			/* centro de custo */
			if (inCentroCusto != null) {
				sql.addWhere("(lancamentoItem.centroCusto = :centroCusto)");
				sql.setLong("centroCusto", inCentroCusto);
			}
			/* item de custo */
			/* item de custo */
			if (!ArrayUtils.isEmpty(inItemCustoList)) {
				if (inItemCustoListNot)
					sql.addWhere("(lancamentoItem.itemCusto not in "
							+ Arrays.toString(inItemCustoList).replace('[', '(').replace(']', ')') + ")");
				else
					sql.addWhere("(lancamentoItem.itemCusto in "
							+ Arrays.toString(inItemCustoList).replace('[', '(').replace(']', ')') + ")");
			}

			/* tipo de listagem */
			Calendar dataAtual = CalendarUtils.getCalendar();
			if (inSituacao == Situacao.PENDENTE.ordinal()) {
				sql.addWhere("(lancamento.saldo != 0)");
				sql.addWhere("((lancamento.naoReceberAposVencimento = false) or (lancamento.dataVencimento >= :dataAtualPendentes))");
				sql.setCalendar("dataAtualPendentes", dataAtual);
			}
			if (inSituacao == Situacao.VENCIDO.ordinal()) {
				sql.addWhere("(lancamento.saldo != 0 and lancamento.dataVencimento < :dataAtual)");
				sql.setCalendar("dataAtual", dataAtual);
			}
			if (inSituacao == Situacao.QUITADO.ordinal()) {
				sql.addWhere("(lancamento.saldo = 0)");
			}
			
			/*
			 * Adiciona a clausula ORDER a SQL
			 */
			if (inOrdem == Ordem.DATA.ordinal()) {
				sql.addOrder("lancamento.data");
				sql.addOrder("lancamento.id");
			}
			if (inOrdem == Ordem.DATA_VENCIMENTO.ordinal()) {
				sql.addOrder("lancamento.dataVencimento");
				sql.addOrder("lancamento.id");
			}
			if (inOrdem == Ordem.LANCAMENTO.ordinal()) {
				sql.addOrder("lancamento.id");
			}
			if (inOrdem == Ordem.NOME.ordinal()) {
				sql.addOrder("pessoa.nome");
				sql.addOrder("lancamento.id");
			}
			
			/*
			 * Adiciona a clausula GROUP
			 */
			sql.addGroup("id, data, dataVencimento, descricao, situacao, saldo, valor, contaPrevista, pessoa ");
			
			/*
			 * Executa a SQL e pega o resultado.
			 */
			log.debug("===\nListarLancamentoService.sql = \n" + sql.getSql() + "\n===");
			ResultSet rs = sql.executeQuery();
			List<QueryLancamento> lancamentoList = new ArrayList<QueryLancamento>(0);
			while (rs.next()) {
				QueryLancamento lancamento = new QueryLancamento(
						rs.getLong(QueryLancamento.ID),
						CalendarUtils.getCalendar(rs.getDate(QueryLancamento.DATA)),
						CalendarUtils.getCalendar(rs.getDate(QueryLancamento.DATA_VENCIMENTO)),
						rs.getString(QueryLancamento.DESCRICAO),
						rs.getString(QueryLancamento.SITUACAO),
						rs.getBigDecimal(QueryLancamento.SALDO),
						rs.getBigDecimal(QueryLancamento.VALOR),
//						rs.getString(QueryLancamento.OPERACAO),
						rs.getString(QueryLancamento.CONTA_PREVISTA),
						rs.getString(QueryLancamento.PESSOA),
						rs.getString(QueryLancamento.PESSOA_DOCUMENTO));
//						rs.getString(QueryLancamento.DOCUMENTO_COBRANCA_CATEGORIA),
//						rs.getString(QueryLancamento.CENTRO_CUSTO),
//						rs.getString(QueryLancamento.ITEM_CUSTO),
//						rs.getString(QueryLancamento.CLASSIFICACAO_CONTABIL));
				lancamentoList.add(lancamento);
			}
			
			serviceData.getOutputData().add(lancamentoList);
			
		} catch (HibernateException e) {
            throw new ServiceException(MessageList.createSingleInternalError(e));
		} catch (SQLException e) {
            throw new ServiceException(MessageList.createSingleInternalError(e));
		}
	}
}
/*
		Juridica object = new Juridica();
		object.setNome("pessoateste");
		
		Session session = serviceData.getCurrentSession();
		Criteria criteria = session.createCriteria(Juridica.class);
		criteria.add(Example.create(object)
				.ignoreCase()
				.excludeZeroes()
				.enableLike(MatchMode.START));
		List<Juridica> list = criteria.list();
		
		for (Juridica ob : list)
			System.out.println(ob.getId());

 */
