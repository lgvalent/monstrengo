package br.com.orionsoft.financeiro.gerenciador.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.commons.Frequencia;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria;
import br.com.orionsoft.financeiro.documento.cobranca.services.CriarDocumentoCobrancaService;
import br.com.orionsoft.financeiro.documento.cobranca.services.ObterPropriedadesPreenchimentoManualService;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamentoCategoria;
import br.com.orionsoft.financeiro.documento.pagamento.services.CriarDocumentoPagamentoService;
import br.com.orionsoft.financeiro.documento.pagamento.services.ObterPropriedadesPreenchimentoManualPagamentoService;
import br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.ClassificacaoContabil;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.Operacao;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.financeiro.gerenciador.services.InserirLancamentoService;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.ProcessParamEntity;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este processo realiza a insercao de um lancamento. Ele localiza o documento ligado
 * ao lancamento para definir um documento de quitação, se não tiver nenhum documento
 * vinculado ele cria um documento de quitação
 * 
 * <p>
 * <b>Procedimentos:</b><br>
 * Primeiramente deve-se definir o id do lancamento a ser quitaqo:
 * <i>setLancamentoId(long)</i><br>
 * Definir o identificador da conta onde será lançado movimento de quitação:
 * <i>setContaId(long)</i><br>
 * Definir a dataQuitacao de quitação : <i>setDataQuitacao(Calendar)</i><br>
 * executar o runConfirmarDocumentoCobranca
 * Definir o valor da quitação: <i>setValor(BigDecimal)</i><br>
 * <br>
 * Verificar se a entidade pode ser criada: <i>boolean mayCreate()</i> <br>
 * Obter a entidade por <i>(IEntity) retrieveEntity()</i>.
 * <li>Realizar edições pela interface com o usuário. <br>
 * Gravar as alterações por <i>runUpdate()</i>.
 * 
 * @author Antonio Alves
 * @version 20070724
 * 
 */
@ProcessMetadata(label="Inserir novo lançamento", hint="Insere um novo lançamento de Crédito ou Débito no financeiro", description="Ao definir os centros e itens de custos, seja criterioso, pois sua correta definição permitirá relatórios financeiros mais precisos e detalhados")
public class InserirLancamentoProcess extends ProcessBasic implements IRunnableEntityProcess{
	public static final String PROCESS_NAME = "InserirLancamentoProcess";
	private IEntity<Lancamento> lancamento;
	
	private ProcessParamEntity<Conta> paramContaPrevista = new ProcessParamEntity<Conta>(Conta.class, true, this);
	private ProcessParamEntity<Contrato> paramContrato = new ProcessParamEntity<Contrato>(Contrato.class, true, this);
	private ProcessParamEntity<Operacao> paramOperacao = new ProcessParamEntity<Operacao>(Operacao.class, false, this);

	private IEntityList<Lancamento> lancamentosInseridos;

	/*
	 * Parametro do Processo
	 */
	private String descricao;
	private List<LancamentoItemBean> lancamentoItemBeanList = null;
//	private List<LancamentoItem> lancamentoItemList = null;
	private BigDecimal valor = DecimalUtils.ZERO;
	private Calendar data;
	private Calendar dataVencimento;
	private int transacao;
	private IEntity<ClassificacaoContabil> classificacaoContabil = null;
	
	/* Repetição do lançamento */
	private int quantidade;
	private int frequencia;
	
	/* Gerado de acordo com a categoria selecionado */
	private IEntity<DocumentoPagamento> documentoPagamento = null;
	private IEntity<DocumentoPagamentoCategoria> documentoPagamentoCategoria = null;

	/* Gerado de acordo com a categoria selecionado */
	private IEntity<DocumentoCobranca> documentoCobranca = null;
	private IEntity<DocumentoCobrancaCategoria> documentoCobrancaCategoria = null;

	public void start() throws ProcessException {
		super.start();
		try {
			data = CalendarUtils.getCalendar();
			dataVencimento = CalendarUtils.getCalendar();
			
			quantidade = 1;
			frequencia = Frequencia.UNICA.ordinal();
			
			/* Inicializa os filtros dos parâmetros */
			paramContaPrevista.setStaticHqlWhereFilter(Conta.INATIVO + "!=true AND entity.id IN (SELECT c.id FROM Conta c inner join c.applicationUsers as user where user.id=" + this.getUserSession().getUser().getId() + ")");
			paramContrato.setStaticHqlWhereFilter(Contrato.INATIVO + "!=true");

			/* Inicializa as entidades para permitir a definição por process.getEntity().setId(long) */
			lancamento  = UtilsCrud.create(this.getProcessManager().getServiceManager(), Lancamento.class, null);
			
			lancamentoItemBeanList = new ArrayList<LancamentoItemBean>(0);
			classificacaoContabil  = UtilsCrud.create(this.getProcessManager().getServiceManager(), ClassificacaoContabil.class, null);
			documentoCobrancaCategoria = UtilsCrud.create(this.getProcessManager().getServiceManager(), DocumentoCobrancaCategoria.class, null);
			documentoPagamentoCategoria = UtilsCrud.create(this.getProcessManager().getServiceManager(), DocumentoPagamentoCategoria.class, null);
			// Lucio 20090227: Comentado para continuar NULL, pois foi removido da tela, entao ficava com uma entidade transiente 
			// operacao  = UtilsCrud.create(this.getProcessManager().getServiceManager(), Operacao.class, null);
			criarNovoItem();
			
			lancamentosInseridos = this.getProcessManager().getServiceManager().getEntityManager().getEntityList(new ArrayList<Lancamento>(), Lancamento.class);
		} catch (BusinessException e) {
			throw new ProcessException(e.getErrorList());
		}

	}

	public String getProcessName() {
		return PROCESS_NAME;
	}

	public class LancamentoItemBean {
		private int id;
		private String descricao;
		private double valor;
		private SelectItem centroCustoItem;
		private SelectItem itemCustoItem;

		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getDescricao() {
			return descricao;
		}
		public void setDescricao(String descricao) {
			this.descricao = descricao;
		}
		public double getValor() {
			return valor;
		}
		public void setValor(double valor) {
			this.valor = valor;
		}
		
		public SelectItem getCentroCustoItem() {
			return centroCustoItem;
		}
		public void setCentroCustoItem(SelectItem centroCustoItem) {
			this.centroCustoItem = centroCustoItem;
		}

		public SelectItem getItemCustoItem() {
			return itemCustoItem;
		}
		public void setItemCustoItem(SelectItem itemCustoItem) {
			this.itemCustoItem = itemCustoItem;
		}
		
		public CentroCusto getCentroCusto() {
			try {
				return UtilsCrud.objectRetrieve(getProcessManager().getServiceManager(), CentroCusto.class, (Long) this.centroCustoItem.getValue(), null);
			} catch (Exception e) {}
			return null;
		}
		public ItemCusto getItemCusto() {
			try {
				return UtilsCrud.objectRetrieve(getProcessManager().getServiceManager(), ItemCusto.class, (Long) this.itemCustoItem.getValue(), null);
			} catch (Exception e) {}
			return null;
		}
	}

	public void criarNovoItem() {
		LancamentoItemBean item = new LancamentoItemBean();
		item.setId(this.lancamentoItemBeanList.size()+1);
		item.setValor(0.00);
		this.lancamentoItemBeanList.add(item);
	}

	public boolean runCriarDocumentoCobranca() {
		super.beforeRun();

		log.debug("Categoria do documento de cobrança mudou, verificando a necessidade de criar ou não um novo documento");

		this.valor = DecimalUtils.ZERO;
		for (LancamentoItemBean bean : lancamentoItemBeanList) {
			this.valor = this.valor.add(DecimalUtils.getBigDecimal(bean.getValor()));
		}

		/* Limpa o buffer da lista de propriedades editaveis */
		propriedadesEditaveisCobrancaBuffer = null;

		/* Verifica se foi definida alguma categoria de documento de cobranca */
		if(this.documentoCobrancaCategoria.getId() == IDAO.ENTITY_UNSAVED){
			this.documentoCobranca = null;
		}else{
			try {
				log.debug("Criando um novo documentoCobranca...");
				ServiceData sd = new ServiceData(CriarDocumentoCobrancaService.SERVICE_NAME, null);
				sd.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DOCUMENTO_COBRANCA_CATEGORIA, this.documentoCobrancaCategoria);
				sd.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_CONTRATO, this.paramContrato.getValue());
				sd.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DATA_DOCUMENTO, this.data);
				sd.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DATA_VENCIMENTO, this.dataVencimento);
				sd.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_VALOR_DOCUMENTO, this.valor);
				sd.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_TRANSACAO, Transacao.values()[this.transacao]);
				this.getProcessManager().getServiceManager().execute(sd);

				this.documentoCobranca = sd.getFirstOutput();
			} catch (ServiceException e) {
				this.getMessageList().addAll(e.getErrorList());
				return false;
			}

		}
		return true;
	}

	public boolean runCriarDocumentoPagamento(){
		try {
			/* Limpa o buffer da lista de propriedades editaveis */
			propriedadesEditaveisPagamentoBuffer = null;

			/* Verifica se foi definida alguma categoria de documento de cobranca */
			if(this.documentoPagamentoCategoria.getId() == IDAO.ENTITY_UNSAVED){
				this.documentoPagamento = null;

			}else{
				log.debug("Criando um novo documentoPagamento...");
				ServiceData sd = new ServiceData(CriarDocumentoPagamentoService.SERVICE_NAME, null);
				sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DOCUMENTO_PAGAMENTO_CATEGORIA, this.documentoPagamentoCategoria);
				sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_CONTRATO, this.paramContrato.getValue());
				sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DATA_DOCUMENTO, this.data);
				sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DATA_VENCIMENTO, this.dataVencimento);
				sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_VALOR_DOCUMENTO, this.valor);
				sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_TRANSACAO, Transacao.values()[this.transacao]);
				this.getProcessManager().getServiceManager().execute(sd);

				this.documentoPagamento = sd.getFirstOutput();
			}

			return true;

		} catch (BusinessException e) {
			this.getMessageList().addAll(e.getErrorList());
			return false;
		}
	}

	public boolean runInserirLancamento() {
		super.beforeRun();

		try {
			if(this.frequencia==Frequencia.UNICA.ordinal()){
				this.quantidade = 1;
			}
			
			this.lancamentosInseridos.clear();
			int quantidadeRestante = this.quantidade;

			do{
				/* Prepara a descrição adicional para lançamentos múltiplos */
				String descricaoAdicional;
				if(this.quantidade==1)
					descricaoAdicional = "";
				else
					descricaoAdicional = String.format("- %d de %d.", this.quantidade - quantidadeRestante + 1, this.quantidade);

				List<LancamentoItem> lancamentoItemList = new ArrayList<LancamentoItem>(0);
				for (LancamentoItemBean bean : this.lancamentoItemBeanList) {
					LancamentoItem item = UtilsCrud.objectCreate(this.getProcessManager().getServiceManager(), LancamentoItem.class, null);
					/* Lucio 20120822: Atualizando as dataLancamento e dataCompetencia dos items */
					item.setDataLancamento(data);
					item.setDataCompetencia(dataVencimento);
					item.setCentroCusto(bean.getCentroCusto());
					//				item.setClassificacaoContabil(bean.getClassificacaoContabil());
					item.setDescricao(bean.getDescricao());
					item.setItemCusto(bean.getItemCusto());
					item.setValor(DecimalUtils.getBigDecimal(bean.getValor()));

					lancamentoItemList.add(item);
				}

				ServiceData sd = new ServiceData(InserirLancamentoService.SERVICE_NAME, null);
				sd.getArgumentList().setProperty(InserirLancamentoService.IN_DESCRICAO_OPT, this.descricao + descricaoAdicional);
				sd.getArgumentList().setProperty(InserirLancamentoService.IN_LANCAMENTO_ITEM_LIST, lancamentoItemList);
				sd.getArgumentList().setProperty(InserirLancamentoService.IN_CONTRATO, this.paramContrato.getValue().getObject());

				if(this.documentoCobranca!=null){
					sd.getArgumentList().setProperty(InserirLancamentoService.IN_DOCUMENTO_COBRANCA_OPT, this.documentoCobranca.getObject());
					/* Define a instrução 3 do documento de cobrança. Defini-se aqui e não dentro do serviço, pois um documento de 
					 * cobrança pode estar vinculado a vários lançamentos. Logo a pergunta, de qual lançamento receberá a descrção.
					 * Então está decisão fica fora, logo quando se cria o DocumentoCobranca */
					this.documentoCobranca.setPropertyValue(DocumentoCobranca.INSTRUCOES3, descricao);
				}
				if(this.documentoPagamento!=null)
					sd.getArgumentList().setProperty(InserirLancamentoService.IN_DOCUMENTO_PAGAMENTO_OPT, this.documentoPagamento.getObject());

				sd.getArgumentList().setProperty(InserirLancamentoService.IN_CONTA_PREVISTA_OPT, this.paramContaPrevista.getValue().getObject());
				sd.getArgumentList().setProperty(InserirLancamentoService.IN_DATA, this.data);
				sd.getArgumentList().setProperty(InserirLancamentoService.IN_DATA_VENCIMENTO, this.dataVencimento);
				sd.getArgumentList().setProperty(InserirLancamentoService.IN_TRANSACAO, Transacao.values()[this.transacao]);
				// Lucio 20090302: Comentado para continuar NULL, pois foi removido da tela, entao ficava com uma entidade transiente 
				// sd.getArgumentList().setProperty(InserirLancamentoService.IN_OPERACAO, this.paramOperacao.getValue().getObject());
				this.getProcessManager().getServiceManager().execute(sd);

				Lancamento oLancamentoInserido = sd.getFirstOutput();
				IEntity<Lancamento> lancamentoInserido = this.getProcessManager().getServiceManager().getEntityManager().getEntity(oLancamentoInserido); 
				this.lancamentosInseridos.add(lancamentoInserido);

				/* Pegas as mensagens do serviço */
				this.getMessageList().addAll(sd.getMessageList());

				if (sd.getMessageList().isTransactionSuccess()) {
					UtilsAuditorship.auditCreate(this.getProcessManager().getServiceManager(), this.getUserSession(), lancamentoInserido, null);
					if(this.documentoCobranca!=null)
						UtilsAuditorship.auditCreate(this.getProcessManager().getServiceManager(), this.getUserSession(), this.documentoCobranca, null);
					if(this.documentoPagamento!=null)
						UtilsAuditorship.auditCreate(this.getProcessManager().getServiceManager(), this.getUserSession(), this.documentoPagamento, null);
				}


				/* Lucio 20120823: limpa e prepara os NOVOS documentos 
				 * para o próximo lançamento inserido em sequência */
				quantidadeRestante--;
				if(quantidadeRestante > 0){
					/* Ajusta a data de vencimento */
					Frequencia freq = Frequencia.values()[this.frequencia];
					
					this.dataVencimento.add(freq.getUnidade(),freq.getQuantidade());

					runCriarDocumentoCobranca();
					runCriarDocumentoPagamento();
				}

			}while(quantidadeRestante>0);

			return true;

		} catch (BusinessException e) {
			/* Armazenando a lista de erros */
			this.getMessageList().addAll(e.getErrorList());
			return false;
		}
	}

	/**
	 * Constrói uma lista de opções com os tipo possíveis de 
	 * transação que podem ser geradas pelo processo.
	 * @return
	 */
	public List<SelectItem> getListTransacao() {
		List<SelectItem> result = new ArrayList<SelectItem>(2);
		result.add(new SelectItem(Transacao.CREDITO.ordinal(), Transacao.CREDITO.getNome()));
		result.add(new SelectItem(Transacao.DEBITO.ordinal(), Transacao.DEBITO.getNome()));
		return result;
	}

	/**
	 * Constrói uma lista de opções com os tipo possíveis de 
	 * transação que podem ser geradas pelo processo.
	 * @return
	 */
	public List<SelectItem> getListFrequencia() {
		List<SelectItem> result = new ArrayList<SelectItem>(4);
		result.add(new SelectItem(Frequencia.UNICA.ordinal(), Frequencia.UNICA.getNome()));
		result.add(new SelectItem(Frequencia.MENSAL.ordinal(), Frequencia.MENSAL.getNome()));
		result.add(new SelectItem(Frequencia.SEMESTRAL.ordinal(), Frequencia.SEMESTRAL.getNome()));
		result.add(new SelectItem(Frequencia.ANUAL.ordinal(), Frequencia.ANUAL.getNome()));
		return result;
	}

	public String getTransacaoNome() {
		return Transacao.values()[transacao].getNome();
	}

	/**
	 * Constrói uma lista de CentroCusto
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public List<SelectItem> getListCentroCusto(String filter) throws BusinessException {
		return this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(CentroCusto.class, CentroCusto.INATIVO + "!=true AND " + CentroCusto.NOME + " LIKE '%" + filter+ "%'");
	}

	/**
	 * Constrói uma lista de ItemsCusto
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public List<SelectItem> getListItemCusto(String filter) throws BusinessException {
		return this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(ItemCusto.class, ItemCusto.INATIVO + "!=true AND " + ItemCusto.NOME + " LIKE '%" + filter+ "%'");
	}

	/**
	 * Constrói uma lista de ClassificacaoContabil
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public List<SelectItem> getListClassificacaoContabil(String filter) throws BusinessException {
		return this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(ClassificacaoContabil.class, ClassificacaoContabil.INATIVO + "!=true AND " + ClassificacaoContabil.NOME + " LIKE '%" + filter+ "%'");
	}

	private List<IProperty> propriedadesEditaveisCobrancaBuffer = null;

	public List<IProperty> retrievePropriedadesEditaveisCobranca() throws BusinessException {
		if (propriedadesEditaveisCobrancaBuffer == null) {
			ServiceData sd = new ServiceData(ObterPropriedadesPreenchimentoManualService.SERVICE_NAME, null);
			sd.getArgumentList().setProperty(ObterPropriedadesPreenchimentoManualService.IN_DOCUMENTO, this.documentoCobranca);
			this.getProcessManager().getServiceManager().execute(sd);

			propriedadesEditaveisCobrancaBuffer = sd.getFirstOutput();
		}

		return propriedadesEditaveisCobrancaBuffer;
	}

	private List<IProperty> propriedadesEditaveisPagamentoBuffer = null;

	public List<IProperty> retrievePropriedadesEditaveisPagamento() throws BusinessException {
		if (propriedadesEditaveisPagamentoBuffer == null) {
			ServiceData sd = new ServiceData(ObterPropriedadesPreenchimentoManualPagamentoService.SERVICE_NAME, null);
			sd.getArgumentList().setProperty(ObterPropriedadesPreenchimentoManualPagamentoService.IN_DOCUMENTO, this.documentoPagamento);
			this.getProcessManager().getServiceManager().execute(sd);

			propriedadesEditaveisPagamentoBuffer = sd.getFirstOutput();
		}

		return propriedadesEditaveisPagamentoBuffer;
	}

	/**
	 * Constrói uma lista de Documento Cobranca
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public List<SelectItem> getListDocumentoCobrancaCategoria() throws BusinessException {
		List<SelectItem> result = this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(DocumentoCobrancaCategoria.class, "");
		result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "À vista"));
		return result; 
//	    Lucio 20081124: Utilizando rotina do EntityManager para obter uma lista de selectItems
//		IEntityList entityList = UtilsCrud.list(this.getProcessManager().getServiceManager(), DocumentoCobrancaCategoria.class, null);
//		List<SelectItem> result = new ArrayList<SelectItem>(entityList.getSize());
//		result.add(new SelectItem(IDAO.ENTITY_UNSAVED, "À vista"));
//		for (IEntity entity : entityList) {
//			result.add(new SelectItem(entity.getId(), entity.getPropertyValue(DocumentoCobrancaCategoria.NOME).toString()));
//		}
//		return result;
	}

	public List<SelectItem> getListDocumentoPagamentoCategoria() throws BusinessException {
		List<SelectItem> result = this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(DocumentoPagamentoCategoria.class, "");
		result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Nenhum documento)"));
		return result;
	}


	/**
	 * Quando a categoria do documento é alterada um novo documento em
	 * branco deve ser criado
	 * 
	 * @throws BusinessException
	 */
//	private void alterarDocumento() throws BusinessException {
//	log.debug("Forma de pagamento mudou, verificando a necessidade de criar ou não documento");

//	/* Limpa o buffer da lista de propriedades editaveis */
//	propriedadesEditaveisCobrancaBuffer = null;

//	/* Obtem o contrato do grupo para criar o documento */
//	IEntity contrato = (IEntity) lancamento.getPropertyValue(Lancamento.CONTRATO);

//	/*
//	* Define o tipo da transação.
//	*/
//	int transacao = Lancamento.TRANSACAO_CREDITO;
//	if (lancamento.getProperty(Lancamento.VALOR).getValue().getAsBigDecimal().signum() == -1)
//	transacao = Lancamento.TRANSACAO_DEBITO;
//	seguinte, tem uma 
//	log.debug("Criando um novo documentoCobranca...");
//	ServiceData sd = new ServiceData(CriarDocumentoPagamentoService.SERVICE_NAME, null);
//	sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.DOCUMENTO_PAGAMENTO_CATEGORIA, this.documentoCobrancaCategoria);
//	sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_CONTRATO, contrato);
//	sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DATA_DOCUMENTO, this.data);
//	sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DATA_VENCIMENTO, this.dataVencimento);
//	sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_VALOR_DOCUMENTO, this.valor);
//	sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_TRANSACAO, transacao);
//	this.getProcessManager().getServiceManager().execute(sd);

//	this.documentoCobranca = sd.getFirstOutput();

//	log.debug("Ligando o atual grupo com o novo documento");
//	lancamento.getProperty(Lancamento.DOCUMENTO_PAGAMENTO).getValue().setAsEntity(this.documentoCobranca);
//	this.documentoCobranca.getProperty(DocumentoPagamento.LANCAMENTO).getValue().getAsEntityCollection().add(lancamento);
//	}

	public IEntity<ClassificacaoContabil> getClassificacaoContabil() {
		return classificacaoContabil;
	}

	public void setClassificacaoContabil(IEntity<ClassificacaoContabil> classificacaoContabil) {
		this.classificacaoContabil = classificacaoContabil;
	}

	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}

	public Calendar getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Calendar dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getTransacao() {
		return transacao;
	}

	public void setTransacao(int transacao) {
		this.transacao = transacao;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public int getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(int frequencia) {
		this.frequencia = frequencia;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public IEntityList<Lancamento> getLancamentosInseridos() {
		return lancamentosInseridos;
	}

	public IEntity<DocumentoCobranca> getDocumentoCobranca() {
		return documentoCobranca;
	}

	public List<LancamentoItemBean> getLancamentoItemBeanList() {
		return lancamentoItemBeanList;
	}

	public void setLancamentoItemBeanList(List<LancamentoItemBean> lancamentoItemBeanList) {
		this.lancamentoItemBeanList = lancamentoItemBeanList;
	}

	public IEntity<DocumentoCobrancaCategoria> getDocumentoCobrancaCategoria() {
		return documentoCobrancaCategoria;
	}

	public void setDocumentoCobrancaCategoria(IEntity<DocumentoCobrancaCategoria> documentoCobrancaCategoria) {
		this.documentoCobrancaCategoria = documentoCobrancaCategoria;
	}
	
	public IEntity<Lancamento> getLancamento() {return lancamento;}
	
	public ProcessParamEntity<Conta> getParamContaPrevista() {return paramContaPrevista;}
	public ProcessParamEntity<Contrato> getParamContrato() {return paramContrato;}
	public ProcessParamEntity<Operacao> getParamOperacao() {return paramOperacao;}

	public IEntity<DocumentoPagamentoCategoria> getDocumentoPagamentoCategoria() {return documentoPagamentoCategoria;}
	public void setDocumentoPagamentoCategoria(IEntity<DocumentoPagamentoCategoria> documentoPagamentoCategoria) {this.documentoPagamentoCategoria = documentoPagamentoCategoria;}

	public IEntity<DocumentoPagamento> getDocumentoPagamento() {return documentoPagamento;}
	
	@SuppressWarnings("unchecked")
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();
		boolean result = false;
		if (entity.getInfo().getType() == Lancamento.class) {
			IEntity<Lancamento> lancamento = (IEntity<Lancamento>) entity;
			try {
				this.paramContaPrevista.setValue(lancamento.<IEntity<Conta>>getPropertyValue(Lancamento.CONTA_PREVISTA));
				this.paramContrato.setValue(lancamento.<IEntity<Contrato>>getPropertyValue(Lancamento.CONTRATO));
				this.paramOperacao.setValue(lancamento.<IEntity<Operacao>>getPropertyValue(Lancamento.OPERACAO));
				this.data = lancamento.getPropertyValue(Lancamento.DATA);
				this.dataVencimento = lancamento.getPropertyValue(Lancamento.DATA_VENCIMENTO);
				this.descricao = lancamento.getPropertyValue(Lancamento.DESCRICAO);
				this.transacao = lancamento.getObject().getValor().signum() > 0? Transacao.CREDITO.ordinal(): Transacao.DEBITO.ordinal();
				this.dataVencimento = lancamento.getPropertyValue(Lancamento.DATA_VENCIMENTO);
				result = true;
			} catch (EntityException e) {
				this.getMessageList().addAll(e.getErrorList());
			}
			result = false;
		} else {
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}
		return result;
	}

	
}
