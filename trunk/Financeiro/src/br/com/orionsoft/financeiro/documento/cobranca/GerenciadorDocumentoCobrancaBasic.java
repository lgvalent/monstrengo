package br.com.orionsoft.financeiro.documento.cobranca;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.commons.services.VerificarDiaUtilService;
import br.com.orionsoft.financeiro.documento.cobranca.suporte.DocumentoRetornoResultado;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamentoCategoria;
import br.com.orionsoft.financeiro.documento.pagamento.services.CriarDocumentoPagamentoService;
import br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.ClassificacaoContabil;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.financeiro.gerenciador.services.InserirLancamentoService;
import br.com.orionsoft.financeiro.gerenciador.services.QuitarLancamentoService;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;


/**
 * <p>Este classe abstrata implementa as funcionalidades básicas de qualquer
 * gerenciador de documento.</p>
 * <p>Este gerenciador está registrado em um Provedor de Documentos.</p>
 * <p>Um documento pode ter seus campos preenchidos automaticamente ou manualmente .</p>
 * <p>Esta classe básica implementa o método imprimirDocumento criando uma lista e chamando o
 * método imprimirDocumentos do gerenciador implementado.
 * <p>Cada gerenciador de documento implementa individualmente um método
 *  		- criarDocumento
 *  		- imprimirDocumentos
 *  que está na Interface IGerenciadorBanco</p>
 * @author Lucio
 */
public abstract class GerenciadorDocumentoCobrancaBasic implements IGerenciadorDocumentoCobranca
{
	private IProvedorDocumentoCobranca provedorDocumentoCobranca;

	public static final String LAYOUT_0 = "Nenhum documento impresso";
	
	private boolean preenchimentoManual=false;
	
	protected final Logger log = Logger.getLogger(this.getClass());

	public IProvedorDocumentoCobranca getProvedorDocumentoCobranca() {return this.provedorDocumentoCobranca;}
	public void setProvedorDocumentoCobranca(IProvedorDocumentoCobranca provedor) {this.provedorDocumentoCobranca = provedor;}

	public boolean isPreenchimentoManual() {return preenchimentoManual;}
	public void setPreenchimentoManual(boolean preenchimentoManual) {this.preenchimentoManual = preenchimentoManual;}

	public void registrarGerenciador() {
		this.provedorDocumentoCobranca.registrarGerenciador(this);
	}
	
	/** 
	 * Este método realiza as rotinas básicas de preenchimento das propriedades valorDocumento, dataDocumento e dataVencimento
	 */
	protected IEntity<? extends DocumentoCobranca> criarDocumento(Class<? extends DocumentoCobranca> classeDocumento, IEntity<Contrato>  contrato, IEntity<? extends DocumentoCobrancaCategoria>  documentoCobrancaCategoria, Calendar dataDocumento, Calendar dataVencimento, BigDecimal valorDocumento, Transacao transacao, ServiceData serviceDataOwner) throws BusinessException {
			IEntity<? extends DocumentoCobranca> entity = UtilsCrud.create(this.getProvedorDocumentoCobranca().getServiceManager(), classeDocumento, null) ;
			
			/* Documento não grava o valor sinalizado, somente o absoluto. 
			 * Para indicar o sinal tem a propriedade DocumentoCobranca.TRANSACAO */
			entity.setPropertyValue(DocumentoCobranca.VALOR, valorDocumento.abs());
			
            /* Lucio-29/04/2007 Foi utilizado o calendar.clone() pois a data de vencimento do documento pode ser alterada 
             * e se ela for referência de outra data, esta referência será alterada também. Isto acontecia
             * ao lançar um movimento e alterar o vencimento do cheque, automaticamente a data de vencimento
             * do movimento também era alterada */
			entity.setPropertyValue(DocumentoCobranca.DATA, dataDocumento.clone());
			entity.setPropertyValue(DocumentoCobranca.DATA_VENCIMENTO, dataVencimento.clone());
			entity.setPropertyValue(DocumentoCobranca.CONTRATO, contrato);
			entity.setPropertyValue(DocumentoCobranca.DOCUMENTO_COBRANCA_CATEGORIA, documentoCobrancaCategoria);
			
			/* Grava o atual convênio definido na forma de pagamento, assim, se o operador vier
			 * a alterar as configurações desta forma de pagamento, os documentos já gerados 
			 * por ela não perderão o convenio pelo qual o documento foi gerado, esta informação 
			 * de convenio é muito importante para remessa e retorno de arquivos */
			entity.setPropertyValue(DocumentoCobranca.CONVENIO_COBRANCA, documentoCobrancaCategoria.getPropertyValue(DocumentoCobrancaCategoria.CONVENIO_COBRANCA));
 			entity.setPropertyValue(DocumentoCobranca.LAYOUT_ID, documentoCobrancaCategoria.getPropertyValue(DocumentoCobrancaCategoria.LAYOUT_ID));
 			entity.setPropertyValue(DocumentoCobranca.TRANSACAO, transacao);
 			
			return entity;
	}
	
	/**
	 * Útil para verificar se o documento venceu em um dia não útil e não precisa calcular Multa e Juros, pode 
	 * receber o valor ORIGINAL do documento de cobrança.
	 * @param documento
	 * @param dataPagamento
	 * @param serviceDataOwner
	 * @return
	 * @throws BusinessException
	 */
	protected boolean verificarNecessidadeCalcularJurosMulta(DocumentoCobranca documento, Calendar dataPagamento, ServiceData serviceDataOwner) throws BusinessException{
		/* Verifica se o título está vencido de acordo com a data de pagamento */
		if (CalendarUtils.diffDay(dataPagamento, documento.getDataVencimento())>0) {
			
			/* Lucio 20120521: Verifica se o título está vencido ou venceu em algum dia não útil */
			ServiceData sd = new ServiceData(VerificarDiaUtilService.SERVICE_NAME, serviceDataOwner);
			sd.getArgumentList().setProperty(VerificarDiaUtilService.IN_DATA,documento.getDataVencimento());
			this.getProvedorDocumentoCobranca().getServiceManager().execute(sd);
			Calendar proximoDiaUtil = sd.getOutputData(VerificarDiaUtilService.OUT_DIA_UTIL_PROXIMO);

			if(CalendarUtils.diffDay(dataPagamento, proximoDiaUtil)>0)
				return true;
		}
		
		return false;
	}
	
	public void imprimirDocumento(IEntity<? extends DocumentoCobranca> documento, OutputStream outputStream, int printerIndex, String instrucoesAdicionais, ServiceData serviceDataOwner) throws DocumentoCobrancaException {
		/* Cria uma lista de documentoBean vazia*/
		List<DocumentoCobrancaBean> documentos = new ArrayList<DocumentoCobrancaBean>(1);

		/* Adiciona a entidade Documento convertida em um DocumentoCobrancaBean simples */
		documentos.add(new DocumentoCobrancaBean(documento, instrucoesAdicionais));
		
		/* Invoca o outro método que trabalha com uma lista de DocumentoCobrancaBean */
		this.imprimirDocumentos(documentos, outputStream, printerIndex, serviceDataOwner);
	}


	public void imprimirDocumentos(List<DocumentoCobrancaBean> documentos, OutputStream outputStream, int printerIndex, ServiceData serviceDataOwner) throws DocumentoCobrancaException {
		/* Não imprime nada quando a impressão cair no gerenciador basic */
		throw new DocumentoCobrancaException(MessageList.create(GerenciadorDocumentoCobrancaBasic.class, "NENHUM_DOCUMENTO_NECESSARIO_IMPRESSAO"));
	}

	/**
	 * Gerenciadores de documento simples como Dinheiro e Cheque, que não possuem cálculos de juros e multa
	 * não precisam implementar seu próprio método de calcularVencimento(). Poderão utilizar este método no basic.
	 */
	public BigDecimal calcularVencimento(IEntity<? extends DocumentoCobranca> documento, Calendar dataPagamento, ServiceData serviceDataOwner) throws DocumentoCobrancaException {
		try
		{
			/* Para os gerenciadores simples, onde não é aplicavel o cálculo de Juros e multa, o valor
			 * a ser cobrado jah é calculado e é o mesmo que o valor do documento, sem alteração */
			return documento.getProperty(DocumentoCobranca.VALOR).getValue().getAsBigDecimal();
		} catch (BusinessException e)
		{
			throw new DocumentoCobrancaException(e.getErrorList());
		}
	}

	public void quitarDocumento(DocumentoCobranca documento, Conta conta, Calendar data, Calendar dataCompensacao, ServiceData serviceDataOwner) throws DocumentoCobrancaException 
	{
//		try
//		{
////			 TODO URGENTE O documento que vem para esta rotina pode não estar gravado ainda, assim o saldo dele ainda nao existe no banco de dados 
////			/* Verificar se o valor de quitação do documento é menor ou igual ao saldo em aberto e maior que zero*/
////			ServiceData calcularSaldo =  new ServiceData(CalcularSaldoMovimentoService.SERVICE_NAME, serviceDataOwner);
////			calcularSaldo.getArgumentList().setProperty(CalcularSaldoMovimentoService.IN_DOCUMENTO_OPT, documento);
////			calcularSaldo.getArgumentList().setProperty(CalcularSaldoMovimentoService.IN_TIPO_OPERACAO, Operacao.TIPO_OPERACAO_LANCAR);
////			this.getProvedorDocumento().getServiceManager().execute(calcularSaldo);
////			
////			BigDecimal saldoAberto = calcularSaldo.getFirstOutput();
////			
////			/* Verifica se o valor quitado é maior que o valor em aberto do documento 
////			 * e se o valor é diferente de zero */
//// TODO URGENTE O documento que vem para esta rotina pode não estar gravado ainda, assim o saldo dele ainda nao existe no banco de dados 
////			if(valor.abs().compareTo(saldoAberto.abs())==1 || valor.signum() == 0)
////				throw new DocumentoCobrancaException(MessageList.create(GerenciadorDocumentoCobrancaBasic.class, "ERRO_QUITAR_DOCUMENTO", documento.toString(), valor.abs().toString(), saldoAberto.abs().toString()));
//			
//			/* Verificando se o documento possui apenas um grupo */
//			IEntityCollection grupos = documento.getProperty(Documento.GRUPOS).getValue().getAsEntityCollection();
//
//			if(grupos.getSize() == 0)
//				// Documento inconsistente sem grupo vinculado
//				throw new DocumentoCobrancaException(MessageList.create(GerenciadorDocumentoCobrancaBasic.class, "ERRO_QUITAR_DOCUMENTO_INCONSISTENTE", documento.toString()));
//			if(grupos.getSize() > 1)
//				// Documento com mais de um grupo. Uma rotina especifica no gerenciador deste documento deverá ser implementada. POis esta rotina
//				// só quita documentos simples com um grupo.
//				throw new DocumentoCobrancaException(MessageList.create(GerenciadorDocumentoCobrancaBasic.class, "ERRO_QUITAR_DOCUMENTO_COMPLEXO", documento.toString()));
//			
//			/* Inserir movimento de quitação */
//			ServiceData inserirMovimento =  new SerquitarviceData(InserirLancamentoMovimentoService.SERVICE_NAME, serviceDataOwner);
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_CONTA, conta);
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_OPERACAO, Operacao.OPERACAO_QUITAR);
//            inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA_VENCIMENTO, data);
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_GRUPO, grupos.getFirst());
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_VALOR, valor.abs());
//           	inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_TIPO_TRANSACAO, tipoTransacao);
//
//			if(!inserirMovimento.getMessageList().isTransactionSuccess()){
//				inserirMovimento.getMessageList().add(new BusinessMessage(GerenciadorDocumentoCobrancaBasic.class, "ERRO_QUITANDO_MOVIMENTO", documento.toString()));
//				throw new DocumentoCobrancaException(inserirMovimento.getMessageList());
//			}
//		} catch (BusinessException e)
//		{
//			throw new DocumentoCobrancaException(e.getErrorList());
//		}
//		
	}

	public void baixarDocumento(DocumentoCobranca documento, Calendar dataBaixa, ServiceData serviceDataOwner) throws DocumentoCobrancaException {
//		try
//		{
//			/* Verificar se o valor a ser baixado é menor ou igual ao saldo em aberto e maior que zero*/
//			ServiceData calcularSaldo =  new ServiceData(CalcularSaldoMovimentoService.SERVICE_NAME, serviceDataOwner);
//			calcularSaldo.getArgumentList().setProperty(CalcularSaldoMovimentoService.IN_DOCUMENTO_OPT, documento);
//			calcularSaldo.getArgumentList().setProperty(CalcularSaldoMovimentoService.IN_TIPO_OPERACAO, Operacao.TIPO_OPERACAO_LANCAR);
//			this.getProvedorDocumento().getServiceManager().execute(calcularSaldo);
//			
//			BigDecimal saldoAberto = calcularSaldo.getFirstOutput();
//			
//			if(valor.compareTo(saldoAberto)==1 && valor.signum() != 1)
//				throw new DocumentoCobrancaException(MessageList.create(GerenciadorDocumentoCobrancaBasic.class, "ERRO_BAIXAR_DOCUMENTO", documento.toString(), valor.toString(), saldoAberto.toString()));
//			
//			IEntityCollection grupos = documento.getProperty(Documento.GRUPOS).getValue().getAsEntityCollection();
//
//			if(grupos.getSize() == 0)
//				// Documento inconsistente sem grupo vinculado
//				throw new DocumentoCobrancaException(MessageList.create(GerenciadorDocumentoCobrancaBasic.class, "ERRO_BAIXAR_DOCUMENTO_INCONSISTENTE", documento.toString()));
//			
//			for(IEntity<? extends DocumentoCobranca> grupo: grupos){
//				
//				/* Inserir movimento baixar */
//				ServiceData inserirMovimento =  new ServiceData(InserirLancamentoMovimentoService.SERVICE_NAME, serviceDataOwner);
//				inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_CONTA, conta);
//				inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_OPERACAO, Operacao.OPERACAO_QUITAR);
//				inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA_VENCIMENTO, data);
//				inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_GRUPO, grupo);
//				inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_VALOR, valor);
//	            /* verifica o sinal do valor para determinar a transação (crédito ou débito) */
//	            if (saldoAberto.signum() == 1)
//	            	inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_TIPO_TRANSACAO, Movimento.TRANSACAO_CREDITO);
//	            else
//	            	inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_TIPO_TRANSACAO, Movimento.TRANSACAO_DEBITO);
//				this.getProvedorDocumento().getServiceManager().execute(inserirMovimento);
//
//				if(!inserirMovimento.getMessageList().isTransactionSuccess()){
//					inserirMovimento.getMessageList().add(new BusinessMessage(GerenciadorDocumentoCobrancaBasic.class, "ERRO_BAIXANDO_MOVIMENTO", documento.toString()));
//					throw new DocumentoCobrancaException(inserirMovimento.getMessageList());
//				}
//
//			}
//			
//		} catch (BusinessException e)
//		{
//			throw new DocumentoCobrancaException(e.getErrorList());
//		}
//		
	}

	public void lancarDocumento(IEntity<? extends DocumentoCobranca> documento, ServiceData serviceDataOwner) throws DocumentoCobrancaException {
		try {
			UtilsCrud.update(this.getProvedorDocumentoCobranca().getServiceManager(), documento, serviceDataOwner);
		} catch (BusinessException e) {
			throw new DocumentoCobrancaException(e.getErrorList());
		}
		
//		try
//		{
//			BigDecimal valor = documento.getProperty(Documento.VALOR_DOCUMENTO).getValue().getAsBigDecimal();
//			
//			/* Verificando se o documento possui apenas um grupo */
//			IEntityCollection grupos = documento.getProperty(Documento.GRUPOS).getValue().getAsEntityCollection();
//
//			if(grupos.getSize() == 0)
//				// Documento inconsistente sem grupo vinculado
//				throw new DocumentoCobrancaException(MessageList.create(GerenciadorDocumentoCobrancaBasic.class, "ERRO_LANCAR_DOCUMENTO_INCONSISTENTE", documento.toString()));
//			
//			/* Inserir movimento de quitação */
//			ServiceData inserirMovimento =  new ServiceData(InserirLancamentoMovimentoService.SERVICE_NAME, serviceDataOwner);
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_CONTA, conta);
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_OPERACAO, Operacao.OPERACAO_LANCAR);
//            /* verifica o sinal do valor para determinar a transação (crédito ou débito) */
//            if (valor.signum() == 1)
//            	inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_TIPO_TRANSACAO, Movimento.TRANSACAO_CREDITO);
//            else
//            	inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_TIPO_TRANSACAO, Movimento.TRANSACAO_DEBITO);
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA_VENCIMENTO, data);
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_GRUPO, grupos.getFirst());
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_VALOR, valor);
//			this.getProvedorDocumento().getServiceManager().execute(inserirMovimento);
//
//			if(!inserirMovimento.getMessageList().isTransactionSuccess()){
//				inserirMovimento.getMessageList().add(new BusinessMessage(GerenciadorDocumentoCobrancaBasic.class, "ERRO_LANCANDO_MOVIMENTO", documento.toString()));
//				throw new DocumentoCobrancaException(inserirMovimento.getMessageList());
//			}
//		} catch (BusinessException e)
//		{
//			throw new DocumentoCobrancaException(e.getErrorList());
//		}
//		
	}

	public void estornarDocumento(IEntity<? extends DocumentoCobranca> documento, long movimentoId, Calendar data, ServiceData serviceDataOwner) throws DocumentoCobrancaException {
//		try
//		{
////			/* Verificar se o saldo do documento está totalmente em aberto, pois documento parcialmente pago
////			 * nao poderá ser estornado, antes devera ser estornado os movimentos de quitação */
////			ServiceData calcularSaldo =  new ServiceData(CalcularSaldoMovimentoService.SERVICE_NAME, serviceDataOwner);
////			calcularSaldo.getArgumentList().setProperty(CalcularSaldoMovimentoService.IN_DOCUMENTO_OPT, documento);
////			calcularSaldo.getArgumentList().setProperty(CalcularSaldoMovimentoService.IN_TIPO_OPERACAO, Operacao.TIPO_OPERACAO_LANCAR);
////			this.getProvedorDocumento().getServiceManager().execute(calcularSaldo);
////			
////			BigDecimal saldoAberto = calcularSaldo.getFirstOutput();
////			BigDecimal valorDocumento = documento.getProperty(Documento.VALOR_DOCUMENTO).getValue().getAsBigDecimal();
////			
////			if(saldoAberto.compareTo(valorDocumento)!=0)
////				throw new DocumentoCobrancaException(MessageList.create(GerenciadorDocumentoCobrancaBasic.class, "ERRO_ESTORNO_DOCUMENTO_QUITADO", documento.toString(), valorDocumento.toString(), saldoAberto.toString()));
//			
////			/* Listar o movimento do documento atual */
////			ServiceData listarMovimento =  new ServiceData(ListarLancamentoMovimentoService.SERVICE_NAME, serviceDataOwner);
////			listarMovimento.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_DOCUMENTO_OPT, documento);
////			listarMovimento.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_ORDEM, ListarLancamentoMovimentoService.ORDEM_DATA);
////			listarMovimento.getArgumentList().setProperty(ListarLancamentoMovimentoService.IN_TIPO_OPERACAO_OPT, Operacao.TIPO_OPERACAO_LANCAR);
////			this.getProvedorDocumento().getServiceManager().execute(listarMovimento);
////			List<QueryLancamentoMovimento> movimentosBean = listarMovimento.getFirstOutput();
////			
////			/* Identificar um movimento do tipo LANCAR que ainda não esteja estornado */
////			for(QueryLancamentoMovimento movimentoBean: movimentosBean){
////				if(!movimentoBean.isEstornado()){
////					
//			IEntity<? extends DocumentoCobranca> movimento = UtilsCrud.retrieve(this.getProvedorDocumento().getServiceManager(), Movimento.class, movimentoId, serviceDataOwner);
//			
//			/* Estornar o movimento */
//			ServiceData estornarMovimento =  new ServiceData(EstornarMovimentoService.SERVICE_NAME, serviceDataOwner);
//			estornarMovimento.getArgumentList().setProperty(EstornarMovimentoService.IN_MOVIMENTO, movimento);
//			estornarMovimento.getArgumentList().setProperty(EstornarMovimentoService.IN_DATA, CalendarUtils.getCalendar());
//			this.getProvedorDocumento().getServiceManager().execute(estornarMovimento);
//			
//			if(!estornarMovimento.getMessageList().isTransactionSuccess()){
//				estornarMovimento.getMessageList().add(new BusinessMessage(GerenciadorDocumentoCobrancaBasic.class, "ERRO_ESTORNANDO_MOVIMENTO", documento.toString()));
//				throw new DocumentoCobrancaException(estornarMovimento.getMessageList());
//			}
//			
//		} catch (BusinessException e)
//		{
//			throw new DocumentoCobrancaException(e.getErrorList());
//		}
//		
	}
	
	public void cancelarDocumento(IEntity<? extends DocumentoCobranca> documento, Calendar dataCancelamento, ServiceData serviceDataOwner) throws DocumentoCobrancaException{
		log.debug("utlizando cancelarDocumento do GerenciadorDocumentoCobrancaBasic");
		try {
			log.debug("atualizando a data de cancelamento através do GerenciadorDocumentoCobrancaBasic");
			if (documento != null){
				documento.getProperty(DocumentoCobranca.DATA_CANCELAMENTO).getValue().setAsCalendar(dataCancelamento);
				UtilsCrud.update(this.provedorDocumentoCobranca.getServiceManager(), documento, serviceDataOwner);
			}else
				log.debug("documento null - não foi cancelado nem atualizado através do GerenciadorDocumentoCobrancaBasic");
		} catch (BusinessException e) {
			throw new DocumentoCobrancaException(e.getErrorList());
		}
	}

	public File gerarRemessa(
			IEntity<? extends ConvenioCobranca> convenioCobranca,
			Calendar inicioPeriodo, Calendar finalPeriodo,
			Integer quantidadeDiasProtesto, ServiceData serviceDataOwner)
	throws DocumentoCobrancaException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<DocumentoRetornoResultado> receberRetorno(
			IEntity<? extends ConvenioCobranca> convenioCobranca,
			InputStream dados, ServiceData serviceDataOwner)
			throws DocumentoCobrancaException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SelectItem> getLayouts(){
		List<SelectItem> result = new ArrayList<SelectItem>(1);
		result.add(new SelectItem(0, LAYOUT_0));
		return result;
	}
	public List<IProperty> retrievePropriedadesPreenchimentoManual(IEntity<? extends DocumentoCobranca> documento) throws BusinessException{
		List<IProperty> result = new ArrayList<IProperty>();
		// Verifica se o documento passado extend Documento
		if(!ClassUtils.getAllSuperclasses(documento.getInfo().getType()).contains(DocumentoCobranca.class))
			// SENAO ERRO: A entidade fornecida não é uma extensão de um Documento do Financeiro;
			throw new BusinessException (MessageList.create(ProvedorDocumentoCobranca.class,"OBJETO_INCOMPATIVEL", documento.toString()));
	
		Field[] fields = documento.getInfo().getType().getDeclaredFields();
		for (Field field: fields)
		{
			// Se o metodo inicia com set* então deve ir para  a lista de campos editáveis
			if(!Modifier.isStatic(field.getModifiers())){
				/* busca e adiciona a propriedade da entidade na lista */
				result.add(documento.getProperty(field.getName()));
			}
		}
		
		return result;
		
	}
	
	protected void inserirLancamentoItem(DocumentoCobranca documento, Conta conta, Calendar dataVencimento, Transacao transacao, BigDecimal valor, CentroCusto centroCusto, ItemCusto itemCusto, ClassificacaoContabil classificacaoContabil, String descricao, ServiceData serviceDataOwner) throws BusinessException {
        /* Cria a lista de lancamentoItem */
		List<LancamentoItem> lancamentoItemList = new ArrayList<LancamentoItem>(1);

		/* Popula a lista com um elemento, de acordo com os parâmetros passados */
		LancamentoItem lancamentoItem = UtilsCrud.objectCreate(this.getProvedorDocumentoCobranca().getServiceManager(), LancamentoItem.class, serviceDataOwner);
		lancamentoItem.setDataLancamento(CalendarUtils.getCalendar());
		lancamentoItem.setDataCompetencia(dataVencimento);
		lancamentoItem.setItemCusto(itemCusto);
		lancamentoItem.setClassificacaoContabil(classificacaoContabil);
		lancamentoItem.setValor(valor);
		lancamentoItemList.add(lancamentoItem);
		
		inserirLancamentoItems(documento, conta, dataVencimento, transacao, lancamentoItemList, descricao, serviceDataOwner);
	}
		

		/**
     * Este método é útil para que os gerenciadores insiram lançamentos com os valores
     * de Taxas e Deduções automáticas que são detectadas durante o recebimento de um determinado Título.
     * Como por exemplo, ao receber uma GRCS do valor recebido, há um recolhimento retido na CEF para as entidades.
     * @param documento Documento de cobrança que está sendo quitado
     * @param conta Conta onde deverá ser registrados os novos grupos
     * @param dataVencimento A data de vencimento que é igual à data de quitação
     * @param serviceDataOwner ServiceData com a atual configuração de sessão
     * @param contrato Identificador do contrato utilizado
     * @param valor Valor do grupo a ser inserido
     * @param centroCusto Centro de Custo
     * @param itemCusto Item de Custo
     * @param transacao Tipo de Transação ENTRADA/SAIDA | CRÉDITO/DÉBITO
     * @throws BusinessException 
     */
	protected void inserirLancamentoItems(DocumentoCobranca documento, Conta conta, Calendar dataVencimento, Transacao transacao, List<LancamentoItem> lancamentoItemList, String descricao, ServiceData serviceDataOwner) throws BusinessException {
        
        /* Invoca o serviço que insere o lançamento */
		ServiceData inserirLancamento = new ServiceData(InserirLancamentoService.SERVICE_NAME, serviceDataOwner);
        inserirLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_CONTA_PREVISTA_OPT, conta);
        inserirLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_CONTRATO, documento.getContrato());
        inserirLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_DATA, CalendarUtils.getCalendar());
        inserirLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_DATA_VENCIMENTO, dataVencimento);
        inserirLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_DESCRICAO_OPT, descricao);
        inserirLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_DOCUMENTO_COBRANCA_OPT, documento);
        inserirLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_TRANSACAO, transacao);
        
        inserirLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_LANCAMENTO_ITEM_LIST, lancamentoItemList);
        
        this.getProvedorDocumentoCobranca().getServiceManager().execute(inserirLancamento);
        Lancamento lancamento = inserirLancamento.getFirstOutput();

        if(!inserirLancamento.getMessageList().isTransactionSuccess()){
        	inserirLancamento.getMessageList().add(new BusinessMessage(GerenciadorDocumentoCobrancaBasic.class, "ERRO_INSERINDO_OUTROS_LANCAMENTOS", documento.toString(), "Desconto"));
        	throw new DocumentoCobrancaException(inserirLancamento.getMessageList());
        }

        /* Criando um documento de quitação, caso tenha sido informado no convênio de cobrança */
        IEntity<DocumentoPagamentoCategoria> documentoPagamentoCategoria = null;
        IEntity<DocumentoPagamento> documentoPagamento = null;
        if (documento.getConvenioCobranca() != null){
        	if (documento.getConvenioCobranca().getDocumentoPagamentoCategoria() != null){
        		documentoPagamentoCategoria = UtilsCrud.retrieve(this.getProvedorDocumentoCobranca().getServiceManager(), DocumentoPagamentoCategoria.class, documento.getConvenioCobranca().getDocumentoPagamentoCategoria().getId(), serviceDataOwner);
        		
        		// TODO Urgente: Por que está sendo criado um documento e por que está quitando o lançamento e não quitando o documento?
        		/* Cria um novo documento (no caso, será o documento de pagamento) */
        		/*
        		 * Andre, 19/08/2008: Os campos documentoPagamentoCategoria e contrato poderiam virar objetos 
        		 * no serviço de criar documento pagamento, assim não é preciso fazer retrieve numa entidade
        		 * que já está na memória através do documento passado por parâmetro.
        		 */
        		ServiceData sdCriarDocumento = new ServiceData(CriarDocumentoPagamentoService.SERVICE_NAME, serviceDataOwner);
        		sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DOCUMENTO_PAGAMENTO_CATEGORIA, documentoPagamentoCategoria);
        		sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_CONTRATO, UtilsCrud.retrieve(this.getProvedorDocumentoCobranca().getServiceManager(), Contrato.class, documento.getContrato().getId(), serviceDataOwner));
        		sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DATA_DOCUMENTO, CalendarUtils.getCalendar());
        		sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DATA_VENCIMENTO, dataVencimento);
        		sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_VALOR_DOCUMENTO, lancamento.getValor().abs());
        		sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_TRANSACAO, transacao);
        		this.getProvedorDocumentoCobranca().getServiceManager().execute(sdCriarDocumento);
        		
        		documentoPagamento = sdCriarDocumento.getFirstOutput();
        		UtilsCrud.update(this.getProvedorDocumentoCobranca().getServiceManager(), documentoPagamento, serviceDataOwner);
        	}
        }
        
        ServiceData quitarLancamento = new ServiceData(QuitarLancamentoService.SERVICE_NAME, serviceDataOwner);
        quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_CONTA, conta);
        quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_DATA, dataVencimento);
        quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_LANCAMENTO, lancamento);
        quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_VALOR, lancamento.getValor().abs());
        /* Lucio 20140417: AutoCompensa movimentos gerados pelo retorno, pois não necessitam de conferência */
        quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_DATA_COMPENSACAO_OPT, dataVencimento);
        if (documentoPagamento != null){
        	quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_DOCUMENTO_PAGAMENTO_OPT, documentoPagamento);
        }
        this.getProvedorDocumentoCobranca().getServiceManager().execute(quitarLancamento);
    }
	
 }