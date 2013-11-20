/*
 * Created on 31/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.gerenciador.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamentoCategoria;
import br.com.orionsoft.financeiro.documento.pagamento.services.CriarDocumentoPagamentoService;
import br.com.orionsoft.financeiro.documento.pagamento.services.ObterPropriedadesPreenchimentoManualPagamentoService;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.financeiro.gerenciador.services.CalcularMultaJurosService;
import br.com.orionsoft.financeiro.gerenciador.services.QuitarLancamentosService;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.auditorship.support.EntityAuditValue;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este processo realiza a quitação de vários lancamentos. Ele localiza o documento
 * ligado ao lancamento para definir um documento de quitação, se não tiver
 * nenhum documento vinculado ele cria um documento de quitação
 * 
 * <p>
 * <b>Procedimentos:</b><br>
 * Primeiramente deve-se definir o id do lancamento a ser quitaqo:
 * <i>setLancamentoId(long)</i><br>
 * Definir o identificador da conta onde será lançado movimento de quitação:
 * <i>setContaId(long)</i><br>
 * Definir a dataQuitacao de quitação : <i>setDataQuitacao(Calendar)</i><br>
 * Definir o valor da quitação: <i>setValor(BigDecimal)</i><br>
 * <br>
 * Verificar se a entidade pode ser criada: <i>boolean mayCreate()</i> <br>
 * Obter a entidade por <i>(IEntity) retrieveEntity()</i>.
 * <li>Realizar edições pela interface com o usuário. <br>
 * Gravar as alterações por <i>runUpdate()</i>.
 * 
 * @author Antonio
 * @since 20/09/2007
 * @version Lucio 23/06/2008: Adiçao do recurso RunnableProcess.
 * @version Lucio 24/06/2012: Quitação de vários CPF/CNPJ.
 * 
 */
@ProcessMetadata(label = "Quitar lançamentos", hint = "Quita lançamentos que ainda possuem saldo em aberto", description = "Quita lançamentos com saldo em aberto, permitindo criar um novo documento de pagamento ou utilizar um documento criado previamente.")
public class QuitarLancamentoProcess extends ProcessBasic implements IRunnableEntityProcess {
	public static final String PROCESS_NAME = "QuitarLancamentoProcess";

	/** Lançamentos que serão quitados */
	private IEntityList<Lancamento> lancamentos;

	/** Contratos AGRUPADOS */
	private List<ContratoBean> contratos;

	private Calendar dataQuitacao = CalendarUtils.getCalendar();
	private Long contaId = IDAO.ENTITY_UNSAVED;

	/**
	 * Lista de documentos de pagamentos já vinculados aos movimentos para
	 * permitir a seleção de um como documento geral de pagamento
	 */
	private IEntityList<DocumentoPagamento> documentosPagamento;

	/*
	 * Documento de pagamento que será criado ou re-utilizado para quitação do
	 * lançamento.
	 */
	private IEntity<DocumentoPagamento> documentoPagamento = null;
	private Long documentoPagamentoId = IDAO.ENTITY_UNSAVED-1;
	private IEntity<DocumentoPagamentoCategoria> documentoPagamentoCategoria = null;
	
	
    private List<EntityAuditValue> lancamentosAuditValue = new ArrayList<EntityAuditValue>();

	@Override
	public void start() throws ProcessException {
		try {
			super.start();

			this.lancamentos = this.getProcessManager().getServiceManager().getEntityManager().getEntityList(null, Lancamento.class);
			this.documentoPagamentoCategoria = UtilsCrud.create(this.getProcessManager().getServiceManager(), DocumentoPagamentoCategoria.class, null);
			this.documentosPagamento = this.getProcessManager().getServiceManager().getEntityManager().getEntityList(null, DocumentoPagamento.class);

		} catch (BusinessException e) {
			throw new ProcessException(e.getErrorList());
		}
	}

	/**
	 * Analisa a lista de lançamentos e separa por diferentes Contratos.
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public boolean runPrepararContratos() {
		super.beforeRun();
		try {
			this.lancamentosAuditValue.clear();

			/* Define a conta, baseada na conta prevista */
			this.contaId = this.lancamentos.getList().get(0).getObject().getContaPrevista().getId();
			
			/* Prepara as opções padrões dos documento de cobrança */
			this.documentosPagamento.clear();
			this.documentoPagamentoId = IDAO.ENTITY_UNSAVED-1; // Usar o previsto
			
			
			/* Limpa a lista de contratos calculada anteriormente */
			this.contratos = ContratoBean.criarLista(this.lancamentos, this);

			/* Se tiver documento, define-o como a primeira opção de seleção */
			if(!this.documentosPagamento.isEmpty()){
				this.documentoPagamento = this.documentosPagamento.getFirst();
				this.documentoPagamentoId = this.documentoPagamento.getId();
			}
			
			/* Percorre os movimentos em buscas de possíveis documentos de pagamento previstos já criados */
			for(ContratoBean contratoBean: contratos){
				for (IEntity<LancamentoMovimento> movimento : contratoBean.getMovimentos()) {
					if(movimento.getObject().getLancamento().getDocumentoPagamento() != null){
						this.documentosPagamento.add(movimento.getProperty(LancamentoMovimento.LANCAMENTO).getValue().getAsEntity().getProperty(Lancamento.DOCUMENTO_PAGAMENTO).getValue().<DocumentoPagamento>getAsEntity());
					}

					IProperty prop = movimento.getProperty(Lancamento.DOCUMENTO_PAGAMENTO);
					if (!prop.getValue().isValueNull()) {
						this.documentosPagamento.add(prop.getValue().<DocumentoPagamento> getAsEntity());
					}
					
					this.lancamentosAuditValue.add(new EntityAuditValue(movimento.getProperty(LancamentoMovimento.LANCAMENTO).getValue().getAsEntity()));

				}
			}

		} catch (BusinessException e) {
			this.getMessageList().add(e.getErrorList());
			return false;
		}

		/*
		 * Movimentos preparados. Calcula os valores iniciais de juros, multa e
		 * etc
		 */
		return this.runCalcularMultaJuros();
	}

	public boolean runCalcularMultaJuros() {
		super.beforeRun();
		try {
			log.debug("Iniciando o método 'calcularMultaJuros'");
				for (ContratoBean contratoBean : this.contratos) {
					contratoBean.recalcularValores(this.dataQuitacao);
				}
			log.debug("Finalizando o método 'calcularMultaJuros'");
		} catch (BusinessException e) {
			this.getMessageList().add(e.getErrorList());
			UtilsTest.showMessageList(e.getErrorList());
			return false;
		}
		return true;
	}

	public String getProcessName() {
		return PROCESS_NAME;
	}
	
	public IEntityList<DocumentoPagamento> getDocumentosPagamento() {return documentosPagamento;}

	public IEntity<DocumentoPagamento> getDocumentoPagamento() {return documentoPagamento;}
	public void setDocumentoPagamento(IEntity<DocumentoPagamento> documentoPagamento) {this.documentoPagamento = documentoPagamento;}

	public Long getDocumentoPagamentoId() {return documentoPagamentoId;}
	public void setDocumentoPagamentoId(Long documentoPagamentoId) {this.documentoPagamentoId = documentoPagamentoId;}

	public IEntity<DocumentoPagamentoCategoria> getDocumentoPagamentoCategoria() {return documentoPagamentoCategoria;}
	public void setDocumentoPagamentoCategoria(IEntity<DocumentoPagamentoCategoria> documentoPagamentoCategoria) {this.documentoPagamentoCategoria = documentoPagamentoCategoria;}

	/**
	 * Constrói uma lista selecionáveis com os documentos disponíveis. Estes
	 * documentos são preparados durante o runPrepararMovimentos
	 */
	public List<SelectItem> getListDocumentoPagamentoDisponiveis() throws BusinessException {
		List<SelectItem> result = new ArrayList<SelectItem>();
		result.add(new SelectItem(IDAO.ENTITY_UNSAVED-1, "Usar cada um o seu previsto"));
		for (IEntity<DocumentoPagamento> doc : this.documentosPagamento) {
			result.add(new SelectItem(doc.getId(), doc.toString()));
		}
		result.add(new SelectItem(IDAO.ENTITY_UNSAVED, "Criar um novo documento"));
		return result;
	}
	

	// /**
	// * Quando a categoria do documento é alterada um novo documento em
	// * branco deve ser criado
	// *
	// * @throws BusinessException
	// */
	// private void alterarDocumento() throws BusinessException {
	// log.debug("Forma de pagamento mudou, verificando a necessidade de criar ou não documento");

	// Lancamento lancamento = this.lancamento.getObject();
	// System.out.println("YYYYYYYYYYYYYYYYYYYY " + this.documentoPagamento);

	// /* Limpa o buffer da lista de propriedades editaveis */
	// propriedadesEditaveisBuffer = null;

	// /* Obtem o contrato do grupo para criar o documento */
	// Contrato contrato = (Contrato) lancamento.getContrato();

	// /*
	// * Define o tipo da transação.
	// */
	// Transacao transacao = Transacao.CREDITO;
	// if (lancamento.getValor().signum() == -1)
	// transacao = Transacao.DEBITO;

	// IEntity documentoPagamentoCategoriaEntity =
	// UtilsCrud.retrieve(this.getProcessManager().getServiceManager(),
	// DocumentoPagamentoCategoria.class, documentoPagamentoCategoria.getId(),
	// null);
	// IEntity contratoEntity =
	// UtilsCrud.retrieve(this.getProcessManager().getServiceManager(),
	// Contrato.class, contrato.getId(), null);

	// log.debug("Criando um novo documentoPagamento...");
	// ServiceData sd = new
	// ServiceData(CriarDocumentoPagamentoService.SERVICE_NAME, null);
	// sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DOCUMENTO_PAGAMENTO_CATEGORIA,
	// documentoPagamentoCategoriaEntity);
	// sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_CONTRATO,
	// contratoEntity);
	// sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DATA_DOCUMENTO,
	// this.dataQuitacao);
	// sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DATA_VENCIMENTO,
	// this.dataQuitacao);
	// sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_VALOR_DOCUMENTO,
	// DecimalUtils.getBigDecimal(this.valor));
	// sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_TRANSACAO,
	// transacao);
	// this.getProcessManager().getServiceManager().execute(sd);

	// IEntity documentoPagamentoEntity = sd.getFirstOutput();
	// this.documentoPagamento = documentoPagamentoEntity.getObject();

	// log.debug("Ligando o atual grupo com o novo documento");
	// lancamento.setDocumentoPagamento(this.documentoPagamento);
	// System.out.println("ZZZZZZZZZZZZZZZZZZZZZZZZZ " +
	// this.documentoPagamento);
	// }

	/**
	 * Prepara um documento de pagamento de acordo com as opções que estiverem
	 * no atual documentoPagamento e documentoPagamentoCategoria.
	 * 
	 * Primeiro ele verifica se o documentoPagamento possui um Id válido: Se
	 * sim, é porque já existe um documento de pagamento definido. Se não, é
	 * porque precisa criar um novo documento de pagamento. Então ele verifica a
	 * DocumentoPagamentoCategoria para ver qual categoria usar na criação do
	 * novo documento
	 */
	public boolean runPrepararDocumentoPagamento() {
		super.beforeRun();
		try {
			/* Limpa o buffer da lista de propriedades editaveis */
			propriedadesEditaveisBuffer = null;

			/* Verifica se já tem um documentoPagamento definido */
			if (this.documentoPagamentoId == IDAO.ENTITY_UNSAVED) {
				/* Verifica se foi definida alguma categoria de documento de cobranca */
				if (this.documentoPagamentoCategoria.getId() == IDAO.ENTITY_UNSAVED) {
					this.documentoPagamento = null;

				} else {
					/* Define o tipo da transação. */
					Transacao transacao = Transacao.CREDITO;
					if (this.lancamentos.getFirst().getObject().getValor()
							.signum() == -1)
						transacao = Transacao.DEBITO;

					log.debug("Criando um novo documentoPagamento...");
					ServiceData sd = new ServiceData(CriarDocumentoPagamentoService.SERVICE_NAME, null);
					sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DOCUMENTO_PAGAMENTO_CATEGORIA,documentoPagamentoCategoria);
					sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_CONTRATO,this.lancamentos.getFirst().getPropertyValue(Lancamento.CONTRATO));
					sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DATA_DOCUMENTO,this.dataQuitacao);
					sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DATA_VENCIMENTO,this.dataQuitacao);
					sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_VALOR_DOCUMENTO,DecimalUtils.getBigDecimal(this.contratos.get(0).getTotal()));
					sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_TRANSACAO,transacao);
					this.getProcessManager().getServiceManager().execute(sd);

					this.documentoPagamento = sd.getFirstOutput();
				}
			} else {
				/* Usar os previstos */
				if (this.documentoPagamentoId == IDAO.ENTITY_UNSAVED-1) {
					this.getMessageList().add(QuitarLancamentoProcess.class,
							"UTILIZANDO_DOCUMENTOS_PREVISTOS");
					this.documentoPagamento = null;
				}else{
					this.getMessageList().add(QuitarLancamentoProcess.class,
							"UTILIZANDO_DOCUMENTO_PAGAMENTO_PRE_DEFINIDO");

					/* Verifica se o valor do documento será atualizado */
					this.documentoPagamento = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(), DocumentoPagamento.class, this.documentoPagamentoId, null);
					DocumentoPagamento oDocumentoPagamento = this.documentoPagamento.getObject();
					BigDecimal valorTotal = DecimalUtils.getBigDecimal(this.contratos.get(0).getTotal());
					if (valorTotal.compareTo(oDocumentoPagamento.getValor()) != 0) {
						/* O valor do documento é ABSOLUTO, pois há um campo Transação no doc 
						 * que indica se é SAIDA ou ENTRADA*/
						oDocumentoPagamento.setValor(valorTotal.abs());
						this.getMessageList().add(QuitarLancamentoProcess.class,"VALOR_DOCUMENTO_SERA_ATUALIZADO");
					}
				}

			}
			return true;

		} catch (BusinessException e) {
			this.getMessageList().addAll(e.getErrorList());
			return false;
		}
	}

	private List<IProperty> propriedadesEditaveisBuffer = null;
	public List<IProperty> getPropriedadesEditaveis() throws BusinessException {
		if (propriedadesEditaveisBuffer == null) {
			ServiceData sd = new ServiceData(ObterPropriedadesPreenchimentoManualPagamentoService.SERVICE_NAME,
					null);
			sd.getArgumentList().setProperty(ObterPropriedadesPreenchimentoManualPagamentoService.IN_DOCUMENTO, this.documentoPagamento);
			this.getProcessManager().getServiceManager().execute(sd);

			propriedadesEditaveisBuffer = sd.getFirstOutput();
		}
		return propriedadesEditaveisBuffer;
	}


	/**
	 * Constrói uma lista de Contas cadastradas
	 * 
	 * @return List<SelectItem>
	 * @throws BusinessException
	 */
	public List<SelectItem> getListConta() throws BusinessException {
		return this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(Conta.class,
						Conta.INATIVO
								+ "!=true AND entity.id IN (SELECT c.id FROM Conta c inner join c.applicationUsers as user where user.id="
								+ this.getUserSession().getUser().getId() + ")");
	}

	/**
	 * Constrói uma lista de Categoria do documento de cobrança.
	 * 
	 * @return List<SelectItem>
	 * @throws BusinessException
	 */
	public List<SelectItem> getListDocumentoPagamentoCategoria()
			throws BusinessException {
		List<SelectItem> result = this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(DocumentoPagamentoCategoria.class, "");
		result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "Nenhum"));

//		IEntityList<DocumentoPagamentoCategoria> docPagCategoriaList = UtilsCrud.list(this.getProcessManager().getServiceManager(),
//						DocumentoPagamentoCategoria.class, null);
//		List<SelectItem> result = new ArrayList<SelectItem>();
//		result.add(new SelectItem(IDAO.ENTITY_UNSAVED, "Nenhum"));
//		for (IEntity<DocumentoPagamentoCategoria> documentoPagamentoCategoria : docPagCategoriaList) {
//			result.add(new SelectItem(documentoPagamentoCategoria.getId(),
//					documentoPagamentoCategoria
//							.getProperty(DocumentoPagamentoCategoria.NOME)
//							.getValue().getAsString()));
//		}
		return result;
	}

	public boolean runQuitar() {
		super.beforeRun();

		try {

			for(ContratoBean contratoBean: this.contratos){
				
				IEntity<DocumentoPagamento> cloneDocumentoPagamento = null;
				if((this.contratos.size()>1) && (this.documentoPagamentoId == IDAO.ENTITY_UNSAVED)){ // Criar um novo documento para cada contrato
					/* Define o tipo da transação. */
					Transacao transacao = Transacao.CREDITO;
					if (contratoBean.getMovimentos().getFirst().getObject().getLancamento().getValor().signum() == -1)
						transacao = Transacao.DEBITO;

					/* Cria um documento de pagamento para cada CONTRATO baseado no MODELO */
					log.debug("Criando um novo documentoPagamento...");
					ServiceData sd = new ServiceData(CriarDocumentoPagamentoService.SERVICE_NAME, null);
					sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DOCUMENTO_PAGAMENTO_CATEGORIA, documentoPagamentoCategoria);
					sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_CONTRATO, contratoBean.getContrato());
					sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DATA_DOCUMENTO,this.dataQuitacao);
					sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DATA_VENCIMENTO,this.dataQuitacao);
					sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_VALOR_DOCUMENTO, DecimalUtils.getBigDecimal(contratoBean.getTotal()));
					sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_TRANSACAO, transacao);
					this.getProcessManager().getServiceManager().execute(sd);

					cloneDocumentoPagamento = sd.getFirstOutput();
				}else{
					if(this.documentoPagamentoId != IDAO.ENTITY_UNSAVED-1){ // Diferente de 'Usar previstos', então usa sempre UM MESMO SELECIONADO
						cloneDocumentoPagamento = this.documentoPagamento;
					}
					
				}
				
				/* Define conta e documento de pagamento em todos os movimentos */
				for (IEntity<LancamentoMovimento> mov : contratoBean.getMovimentos()){
					/* Decide se usa documento criado, compartilhado ou previsto */
					if(cloneDocumentoPagamento!=null)
						mov.getProperty(LancamentoMovimento.DOCUMENTO_PAGAMENTO).getValue().setAsEntity(cloneDocumentoPagamento);
					else
						mov.getObject().setDocumentoPagamento(mov.getObject().getLancamento().getDocumentoPagamento());
					
					mov.getProperty(LancamentoMovimento.CONTA).getValue().setAsEntity(getConta());
				}
				
				/* Chama o serviço QuitarLancamentoService. */
				ServiceData sd = new ServiceData(QuitarLancamentosService.SERVICE_NAME, null);
				sd.getArgumentList().setProperty(QuitarLancamentosService.IN_MOVIMENTOS, contratoBean.getMovimentos());
				this.getProcessManager().getServiceManager().execute(sd);
				contratoBean.setMovimentosInseridos(sd.<IEntityList<LancamentoMovimento>>getFirstOutput());
				
				/* Pegas as mensagens do serviço */
				super.getMessageList().addAll(sd.getMessageList());
				
				/* Registra a auditoria de todos os movimentos criados */
				if (sd.getMessageList().isTransactionSuccess()) {
					for(IEntity<LancamentoMovimento> mov: contratoBean.getMovimentosInseridos()){
						UtilsAuditorship.auditCreate(this.getProcessManager().getServiceManager(), this.getUserSession(), mov, null);
					}
				}

				/* Registra as alterações da descrição na AuditoriaCRUD */
				for(EntityAuditValue entityAuditValue: this.lancamentosAuditValue){
					UtilsAuditorship.auditUpdate(this.getProcessManager().getServiceManager(), this.getUserSession(), entityAuditValue, null);
				}

			}
			return true;
		} catch (BusinessException e) {
			/*
			 * Armazenando a lista de erros
			 */
			super.getMessageList().addAll(e.getErrorList());
			return false;
		}
	}

	public Calendar getDataQuitacao() {
		return dataQuitacao;
	}

	public void setDataQuitacao(Calendar dataQuitacao) {
		this.dataQuitacao = dataQuitacao;
	}

	public IEntityList<Lancamento> getLancamentos() {
		return lancamentos;
	}

	public void setLancamentos(IEntityList<Lancamento> lancamentos) {
		this.lancamentos = lancamentos;
	}

	public List<ContratoBean> getContratos(){
		return this.contratos;
	}
	
	public Long getContaId() {
		return this.contaId;
	}
	public void setContaId(Long contaId) {
		this.contaId = contaId;
	}
	public IEntity<Conta> getConta(){
		try {
			return UtilsCrud.retrieve(this.getProcessManager().getServiceManager(), Conta.class, this.contaId, null);
		} catch (BusinessException e) {
			e.printStackTrace();
			throw new RuntimeException(e); 
		}
	}

	public static class ContratoBean{
		/** Cria uma lista de contratos agrupando seus respectivos lançamentos
		 * OBS: A lista deverá vir ordenada!!! 
		 * @throws BusinessException
		 */
		public static List<ContratoBean> criarLista(IEntityList<Lancamento> lancamentos, IProcess process) throws BusinessException{
			List<ContratoBean> result = new ArrayList<QuitarLancamentoProcess.ContratoBean>();
		
			IEntity<Contrato> contrato = null;
			for(IEntity<Lancamento> lancamento: lancamentos){
				/* Quando o contrato atual muda, cria um novo bean com seus movimentos */
				if(!lancamento.getPropertyValue(Lancamento.CONTRATO).equals(contrato)){
					contrato = lancamento.getPropertyValue(Lancamento.CONTRATO);
					result.add(new ContratoBean(contrato, filtrarLancamentos(lancamentos, contrato, process), process));
				}
			}

			return result;
		}
		
		private static IEntityList<LancamentoMovimento> filtrarLancamentos(IEntityList<Lancamento> lancamentos, IEntity<Contrato> contrato, IProcess process) throws BusinessException{
			IEntityList<LancamentoMovimento>  result = process.getProcessManager().getServiceManager().getEntityManager().getEntityList(null, LancamentoMovimento.class);
			
			for (IEntity<Lancamento> lancamento : lancamentos) {
				if(lancamento.getPropertyValue(Lancamento.CONTRATO).equals(contrato)){
					/* Cria os movimentos e os vincula ao seu PROVÁVEL lançamento */
					IEntity<LancamentoMovimento> mov = UtilsCrud.create(process.getProcessManager().getServiceManager(), LancamentoMovimento.class, null);
					mov.getProperty(LancamentoMovimento.LANCAMENTO).getValue().setAsEntity(lancamento);
					/*
					 * O valor deve ser ABSOLUTO, depois os serviços analisam o
					 * saldo devedor e definem se é DEBITO/CREDITO
					 */
					mov.getProperty(LancamentoMovimento.VALOR).getValue().setAsBigDecimal(lancamento.getProperty(Lancamento.SALDO).getValue().getAsBigDecimal().abs());

					/* Seleciona o item criado */
					mov.setSelected(true);

					/* Adiciona o movimento na lista de movimentos TEMPORÁRIOS */
					result.add(mov);
				}
			}
			return result;
		}
		
		private ContratoBean(IEntity<Contrato> contrato, IEntityList<LancamentoMovimento> movimentos, IProcess processOwner){
				this.process = processOwner;
				this.contrato = contrato;
				this.movimentos = movimentos;
		}
		
		public void recalcularValores(Calendar dataQuitacao) throws BusinessException{
			/* Zera os totalizadores de multa, juros, descontos e acréscimos gerais  */
			this.multa = 0.00;
			this.juros = 0.00;
			this.desconto = 0.00;
			this.acrescimo = 0.00;
			this.valor = 0.00;

			for (IEntity<LancamentoMovimento> mov : this.movimentos) {
				/* Calcula todos os movimentos, mas só totaliza os selecionados */
				LancamentoMovimento oMov = mov.getObject();
				Lancamento oLan = oMov.getLancamento();

				/*
				 * Valida o valor fornecido nos movimentos de acordo com o
				 * máximo do saldo em aberto de cada lançamento
				 */
				if (oMov.getValor().abs().doubleValue() > oLan.getSaldo()
						.abs().doubleValue())
					oMov.setValor(oLan.getSaldo().abs());
				/* Define UMA DATA de quitação */
				oMov.setData(dataQuitacao);

				if (oLan.getValor().signum() == 1) {
					/*
					 * Andre, 14/006/2008 - se o lançamento possuir
					 * documento de cobrança, obtém os juros e multas
					 * através da categoria do documento de cobrança; Se o
					 * lançamento não possuir documento de cobrança,
					 * utilizar por padrão multa inicial de 10, juros/mora
					 * de 1Process, multa adicional de 0, dias de tolerância 0;
					 * TODO - verificar onde estes valores padrões devem ser
					 * informados/armazenados???
					 */
					ServiceData sd = new ServiceData(CalcularMultaJurosService.SERVICE_NAME, null);
					sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_DATA,	dataQuitacao);
					sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_DATA_VENCIMENTO,	oLan.getDataVencimento());
					sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_VALOR,	oMov.getValor());
					/*
					 * Caso exista documento de cobrança vinculado ao
					 * lançamento, informar os valores de multa, juros e
					 * dias de tolerância através da categoria de documento
					 * de cobrança.
					 */
					if (oLan.getDocumentoCobranca() != null) {
						sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_MULTA_INICIAL, oLan.getDocumentoCobranca().getDocumentoCobrancaCategoria().getMultaAtraso());
						// TODO - onde obter este valor de multa adicional?
						sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_MULTA_ADICIONAL, DecimalUtils.ZERO);
						sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_JUROS_MORA, oLan.getDocumentoCobranca().getDocumentoCobrancaCategoria().getJurosMora());
						sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_DIAS_TOLERANCIA, oLan.getDocumentoCobranca().getDocumentoCobrancaCategoria().getDiasToleranciaMultaAtraso());
						/*
						 * Senão, informar os valores padrões TODO -
						 * verificar onde estes valores padrões devem ser
						 * informados/armazenados
						 */
					} else {
						sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_MULTA_INICIAL, DecimalUtils.TEN);
						sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_MULTA_ADICIONAL, DecimalUtils.ZERO);
						sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_JUROS_MORA, DecimalUtils.ONE);
						sd.getArgumentList().setProperty(CalcularMultaJurosService.IN_DIAS_TOLERANCIA, 0);
					}
					process.getProcessManager().getServiceManager().execute(sd);
					if (!mov.getProperty(LancamentoMovimento.MULTA).getValue().isModified())
						oMov.setMulta((BigDecimal) sd.getOutputData(CalcularMultaJurosService.OUT_MULTA));
					if (!mov.getProperty(LancamentoMovimento.JUROS).getValue().isModified())
						oMov.setJuros((BigDecimal) sd.getOutputData(CalcularMultaJurosService.OUT_JUROS));

				}
				/* Atualiza o valorTotal do movimento */
				oMov.setValorTotal(oMov.getValor().add(oMov.getJuros() == null ? BigDecimal.ZERO : oMov.getJuros()).add(oMov.getMulta() == null ? BigDecimal.ZERO : oMov.getMulta()).subtract(oMov.getDesconto() == null ? BigDecimal.ZERO : oMov.getDesconto()).add(oMov.getAcrescimo() == null ? BigDecimal.ZERO : oMov.getAcrescimo()));

				/* Totaliza somente os movimentos selecionados */
				if (mov.isSelected()) {
					/* Totaliza o valor, juros, multa, descontos e acréscimo */
					this.multa += oMov.getMulta() == null ? 0.0 : oMov.getMulta().doubleValue();
					this.juros += oMov.getJuros() == null ? 0.0 : oMov.getJuros().doubleValue();
					this.desconto += oMov.getDesconto() == null ? 0.0
							: oMov.getDesconto().doubleValue();
					this.acrescimo += oMov.getAcrescimo() == null ? 0.0
							: oMov.getAcrescimo().doubleValue();
					this.valor += oMov.getValor().doubleValue();
				}
			}
			this.total = this.valor + this.multa + this.juros - this.desconto + this.acrescimo;

		}
		
		
		private IProcess process;
		private IEntity<Contrato> contrato;
		public IEntity<Contrato> getContrato() {
			return contrato;
		}

		/** Movimentos temporários que armazenarão os valores calculados */
		private IEntityList<LancamentoMovimento> movimentos;
		private IEntityList<LancamentoMovimento> movimentosInseridos;

		/* Totalizadores */
		private double valor;
		private double multa;
		private double juros;
		private double desconto;
		private double acrescimo;
		private double total;
		
		public void atualizarValores() {
			this.total = this.valor + this.multa + this.juros - this.desconto;
		}
		public double getAcrescimo() {
			return acrescimo;
		}
		public double getDesconto() {
			return desconto;
		}
		public double getJuros() {
			return juros;
		}
		public double getMulta() {
			return multa;
		}

		public double getTotal() {
			return total;
		}

		public double getValor() {
			return valor;
		}
		
		public IEntityList<LancamentoMovimento> getMovimentos() {
			return movimentos;
		}
		public IEntityList<LancamentoMovimento> getMovimentosInseridos() {
			return movimentosInseridos;
		}

		public void setMovimentos(IEntityList<LancamentoMovimento> movimentos) {
			this.movimentos = movimentos;
		}

		public void setMovimentosInseridos(
				IEntityList<LancamentoMovimento> movimentosInseridos) {
			this.movimentosInseridos = movimentosInseridos;
		}
		

	}
	
	/*
	 * ==========================================================================
	 * IRunnableEntityProcess
	 * ==========================================================================
	 */
	@SuppressWarnings("unchecked")
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();

		boolean result = false;

		/* Verifica se a entidade é compatível */
		if (entity.getInfo().getType() == Lancamento.class) {
			this.lancamentos.clear();
			this.lancamentos.add((IEntity<Lancamento>) entity);

			/* Prepara os movimentos */
			result = this.runPrepararContratos();
		} else {
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class,"ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}

		return result;
	}
	
}
