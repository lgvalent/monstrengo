package br.com.orionsoft.financeiro.documento.pagamento;

import java.io.File;
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
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
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
 *
 */
public abstract class GerenciadorDocumentoPagamentoBasic implements IGerenciadorDocumentoPagamento
{
	private IProvedorDocumentoPagamento provedorDocumentoPagamento;

	public static final String LAYOUT_0 = "Nenhum documento impresso";
	
	private boolean preenchimentoManual=false;
	
	protected final Logger log = Logger.getLogger(this.getClass());

	public IProvedorDocumentoPagamento getProvedorDocumentoPagamento() {return this.provedorDocumentoPagamento;}
	public void setProvedorDocumentoPagamento(IProvedorDocumentoPagamento provedor) {this.provedorDocumentoPagamento = provedor;}

	public boolean isPreenchimentoManual() {return preenchimentoManual;}
	public void setPreenchimentoManual(boolean preenchimentoManual) {this.preenchimentoManual = preenchimentoManual;}

	public void registrarGerenciador() {
		this.provedorDocumentoPagamento.registrarGerenciador(this);
	}
	
	/*Este método realiza as rotinas básicas de preenchimento das propriedades valorDocumento, dataDocumento e dataVencimento*/
	protected IEntity<? extends DocumentoPagamento> criarDocumento(Class<? extends DocumentoPagamento> classeDocumento, IEntity<Contrato> contrato, IEntity<? extends DocumentoPagamentoCategoria> documentoPagamentoCategoria, Calendar dataDocumento, Calendar dataVencimento, BigDecimal valorDocumento, Transacao transacao, ServiceData serviceDataOwner) throws BusinessException {
			IEntity<? extends DocumentoPagamento> entity = UtilsCrud.create(this.getProvedorDocumentoPagamento().getServiceManager(), classeDocumento, null) ;
			
			/* Documento não grava o valor sinalizado, somente o absoluto. 
			 * Para indicar o sinal tem a prop. isRecebimnento/isPagamento */
			entity.setPropertyValue(DocumentoPagamento.VALOR, valorDocumento.abs());
			
            /* Lucio-29/04/2007 Foi utilizado o calendar.clone() pois a data de vencimento do documento pode ser alterada 
             * e se ela for referência de outra data, esta referência será alterada também. Isto acontecia
             * ao lançar um movimento e alterar o vencimento do cheque, automaticamente a data de vencimento
             * do movimento também era alterada */
			entity.setPropertyValue(DocumentoPagamento.DATA, dataDocumento.clone());
			entity.setPropertyValue(DocumentoPagamento.DATA_VENCIMENTO, dataVencimento.clone());
			entity.setPropertyValue(DocumentoPagamento.CONTRATO, contrato);
			entity.setPropertyValue(DocumentoPagamento.DOCUMENTO_PAGAMENTO_CATEGORIA, documentoPagamentoCategoria);
			/* grava o atual convênio definido na forma de pagamento, assim, se o operador vier
			 * a alterar as configurações desta forma de pagamento, os documentos já gerados 
			 * por ela não perderão o convenio pelo qual o documento foi gerado, esta informação 
			 * de convenio é muito importante para remessa e retorno de arquivos */
			entity.setPropertyValue(DocumentoPagamento.CONVENIO_PAGAMENTO, documentoPagamentoCategoria.getPropertyValue(DocumentoPagamentoCategoria.CONVENIO_PAGAMENTO));
 			entity.setPropertyValue(DocumentoPagamento.LAYOUT_ID, documentoPagamentoCategoria.getPropertyValue(DocumentoPagamentoCategoria.LAYOUT_ID));
 			entity.setPropertyValue(DocumentoPagamento.TRANSACAO, transacao);
			return entity;
	}
	
	public void imprimirDocumento(IEntity<? extends DocumentoPagamento> documento, OutputStream outputStream, int printIndex, String instrucoesAdicionais, ServiceData serviceDataOwner) throws DocumentoPagamentoException {
		/* Cria uma lista de documentoBean vazia*/
		List<DocumentoPagamentoBean> documentos = new ArrayList<DocumentoPagamentoBean>(1);

		/* Adiciona a entidade Documento convertida em um DocumentoCobrancaBean simples */
		documentos.add(new DocumentoPagamentoBean(documento, instrucoesAdicionais));
		
		/* Invoca o outro método que trabalha com uma lista de DocumentoCobrancaBean */
		this.imprimirDocumentos(documentos, outputStream, printIndex, serviceDataOwner);
	}

	public void imprimirDocumentos(List<DocumentoPagamentoBean> documentos, OutputStream outputStream, int printIndex, ServiceData serviceDataOwner) throws DocumentoPagamentoException {
		/* Não imprime nada quando a impressão cair no gerenciador basic */
		throw new DocumentoPagamentoException(MessageList.create(GerenciadorDocumentoPagamentoBasic.class, "NENHUM_DOCUMENTO_NECESSARIO_IMPRESSAO"));
	}

	/**
	 * Gerenciadores de documento simples como Dinheiro e Cheque, que não possuem cálculos de juros e multa
	 * não precisam implementar seu próprio método de calcularVencimento(). Poderão utilizar este método no basic.
	 */
	public BigDecimal calcularVencimento(IEntity<? extends DocumentoPagamento> documento, Calendar dataPagamento, ServiceData serviceDataOwner) throws DocumentoPagamentoException {
		try
		{
			/* Para os gerenciadores simples, onde não é aplicavel o cáculo de Juros e multa, o valor
			 * a ser cobrado jah é calculado e é o mesmo que o valor do documento, sem alteração */
			return documento.getProperty(DocumentoPagamento.VALOR).getValue().getAsBigDecimal();
		} catch (BusinessException e)
		{
			throw new DocumentoPagamentoException(e.getErrorList());
		}
	}

	public void quitarDocumento(
			IEntity<? extends DocumentoPagamento> documento, Long contaId,
			Calendar data, ServiceData serviceDataOwner)
			throws DocumentoPagamentoException {
		// TODO Auto-generated method stub
		
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
////				throw new DocumentoPagamentoException(MessageList.create(GerenciadorDocumentoCobrancaBasic.class, "ERRO_QUITAR_DOCUMENTO", documento.toString(), valor.abs().toString(), saldoAberto.abs().toString()));
//			
//			/* Verificando se o documento possui apenas um grupo */
//			IEntityCollection grupos = documento.getProperty(Documento.GRUPOS).getValue().getAsEntityCollection();
//
//			if(grupos.getSize() == 0)
//				// Documento inconsistente sem grupo vinculado
//				throw new DocumentoPagamentoException(MessageList.create(GerenciadorDocumentoCobrancaBasic.class, "ERRO_QUITAR_DOCUMENTO_INCONSISTENTE", documento.toString()));
//			if(grupos.getSize() > 1)
//				// Documento com mais de um grupo. Uma rotina especifica no gerenciador deste documento deverá ser implementada. POis esta rotina
//				// só quita documentos simples com um grupo.
//				throw new DocumentoPagamentoException(MessageList.create(GerenciadorDocumentoCobrancaBasic.class, "ERRO_QUITAR_DOCUMENTO_COMPLEXO", documento.toString()));
//			
//			/* Inserir movimento de quitação */
//			ServiceData inserirMovimento =  new SerquitarviceData(InserirLancamentoMovimentoService.SERVICE_NAME, serviceDataOwner);
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_CONTA, conta);
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_OPERACAO, Operacao.OPERACAO_QUITAR);
//            inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA, data);
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_GRUPO, grupos.getFirst());
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_VALOR, valor.abs());
//           	inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_TRANSACAO, tipoTransacao);
//
//			if(!inserirMovimento.getMessageList().isTransactionSuccess()){
//				inserirMovimento.getMessageList().add(new BusinessMessage(GerenciadorDocumentoCobrancaBasic.class, "ERRO_QUITANDO_MOVIMENTO", documento.toString()));
//				throw new DocumentoPagamentoException(inserirMovimento.getMessageList());
//			}
//		} catch (BusinessException e)
//		{
//			throw new DocumentoPagamentoException(e.getErrorList());
//		}
//		
	}

	public void baixarDocumento(DocumentoPagamento documento, Calendar dataBaixa, ServiceData serviceDataOwner) throws DocumentoPagamentoException {
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
//				throw new DocumentoPagamentoException(MessageList.create(GerenciadorDocumentoCobrancaBasic.class, "ERRO_BAIXAR_DOCUMENTO", documento.toString(), valor.toString(), saldoAberto.toString()));
//			
//			IEntityCollection grupos = documento.getProperty(Documento.GRUPOS).getValue().getAsEntityCollection();
//
//			if(grupos.getSize() == 0)
//				// Documento inconsistente sem grupo vinculado
//				throw new DocumentoPagamentoException(MessageList.create(GerenciadorDocumentoCobrancaBasic.class, "ERRO_BAIXAR_DOCUMENTO_INCONSISTENTE", documento.toString()));
//			
//			for(IEntity grupo: grupos){
//				
//				/* Inserir movimento baixar */
//				ServiceData inserirMovimento =  new ServiceData(InserirLancamentoMovimentoService.SERVICE_NAME, serviceDataOwner);
//				inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_CONTA, conta);
//				inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_OPERACAO, Operacao.OPERACAO_QUITAR);
//				inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA, data);
//				inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_GRUPO, grupo);
//				inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_VALOR, valor);
//	            /* verifica o sinal do valor para determinar a transação (crédito ou débito) */
//	            if (saldoAberto.signum() == 1)
//	            	inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_TRANSACAO, Movimento.TRANSACAO_CREDITO);
//	            else
//	            	inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_TRANSACAO, Movimento.TRANSACAO_DEBITO);
//				this.getProvedorDocumento().getServiceManager().execute(inserirMovimento);
//
//				if(!inserirMovimento.getMessageList().isTransactionSuccess()){
//					inserirMovimento.getMessageList().add(new BusinessMessage(GerenciadorDocumentoCobrancaBasic.class, "ERRO_BAIXANDO_MOVIMENTO", documento.toString()));
//					throw new DocumentoPagamentoException(inserirMovimento.getMessageList());
//				}
//
//			}
//			
//		} catch (BusinessException e)
//		{
//			throw new DocumentoPagamentoException(e.getErrorList());
//		}
//		
	}

	public void lancarDocumento(
			IEntity<? extends DocumentoPagamento> documento,
			ServiceData serviceDataOwner) throws DocumentoPagamentoException {
		
		try {
			UtilsCrud.update(this.getProvedorDocumentoPagamento().getServiceManager(), documento, serviceDataOwner);
		} catch (BusinessException e) {
			throw new DocumentoPagamentoException(e.getErrorList());
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
//				throw new DocumentoPagamentoException(MessageList.create(GerenciadorDocumentoCobrancaBasic.class, "ERRO_LANCAR_DOCUMENTO_INCONSISTENTE", documento.toString()));
//			
//			/* Inserir movimento de quitação */
//			ServiceData inserirMovimento =  new ServiceData(InserirLancamentoMovimentoService.SERVICE_NAME, serviceDataOwner);
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_CONTA, conta);
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_OPERACAO, Operacao.OPERACAO_LANCAR);
//            /* verifica o sinal do valor para determinar a transação (crédito ou débito) */
//            if (valor.signum() == 1)
//            	inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_TRANSACAO, Movimento.TRANSACAO_CREDITO);
//            else
//            	inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_TRANSACAO, Movimento.TRANSACAO_DEBITO);
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA, data);
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_GRUPO, grupos.getFirst());
//			inserirMovimento.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_VALOR, valor);
//			this.getProvedorDocumento().getServiceManager().execute(inserirMovimento);
//
//			if(!inserirMovimento.getMessageList().isTransactionSuccess()){
//				inserirMovimento.getMessageList().add(new BusinessMessage(GerenciadorDocumentoCobrancaBasic.class, "ERRO_LANCANDO_MOVIMENTO", documento.toString()));
//				throw new DocumentoPagamentoException(inserirMovimento.getMessageList());
//			}
//		} catch (BusinessException e)
//		{
//			throw new DocumentoPagamentoException(e.getErrorList());
//		}
//		
	}

	public void estornarDocumento(
			IEntity<? extends DocumentoPagamento> documento, long movimentoId,
			Calendar data, ServiceData serviceDataOwner)
			throws DocumentoPagamentoException {
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
////				throw new DocumentoPagamentoException(MessageList.create(GerenciadorDocumentoCobrancaBasic.class, "ERRO_ESTORNO_DOCUMENTO_QUITADO", documento.toString(), valorDocumento.toString(), saldoAberto.toString()));
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
//			IEntity movimento = UtilsCrud.retrieve(this.getProvedorDocumento().getServiceManager(), Movimento.class, movimentoId, serviceDataOwner);
//			
//			/* Estornar o movimento */
//			ServiceData estornarMovimento =  new ServiceData(EstornarMovimentoService.SERVICE_NAME, serviceDataOwner);
//			estornarMovimento.getArgumentList().setProperty(EstornarMovimentoService.IN_MOVIMENTO, movimento);
//			estornarMovimento.getArgumentList().setProperty(EstornarMovimentoService.IN_DATA, CalendarUtils.getCalendar());
//			this.getProvedorDocumento().getServiceManager().execute(estornarMovimento);
//			
//			if(!estornarMovimento.getMessageList().isTransactionSuccess()){
//				estornarMovimento.getMessageList().add(new BusinessMessage(GerenciadorDocumentoCobrancaBasic.class, "ERRO_ESTORNANDO_MOVIMENTO", documento.toString()));
//				throw new DocumentoPagamentoException(estornarMovimento.getMessageList());
//			}
//			
//		} catch (BusinessException e)
//		{
//			throw new DocumentoPagamentoException(e.getErrorList());
//		}
//		
	}
	public void cancelarDocumento(br.com.orionsoft.monstrengo.crud.entity.IEntity<? extends DocumentoPagamento> documento, Calendar dataCancelamento, ServiceData serviceDataOwner) throws DocumentoPagamentoException {
		log.debug("utlizando cancelarDocumento do GerenciadorDocumentoCobrancaBasic");
		try {
			log.debug("atualizando a data de cancelamento através do GerenciadorDocumentoCobrancaBasic");
			if (documento != null){
				documento.getProperty(DocumentoPagamento.DATA_CANCELAMENTO).getValue().setAsCalendar(dataCancelamento);
				UtilsCrud.update(this.provedorDocumentoPagamento.getServiceManager(), documento, serviceDataOwner);
			}else
				log.debug("documento null - não foi cancelado nem atualizado através do GerenciadorDocumentoCobrancaBasic");
		} catch (BusinessException e) {
			throw new DocumentoPagamentoException(e.getErrorList());
		}
	}

	public File gerarRemessa(
			IEntity<? extends ConvenioPagamento> convenioPagamento,
			Calendar inicioPeriodo, Calendar finalPeriodo,
			ServiceData serviceDataOwner) throws DocumentoPagamentoException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void receberRetorno(
			IEntity<? extends ConvenioPagamento> convenioPagamento,
			File dados, ServiceData serviceDataOwner)
			throws DocumentoPagamentoException {
		// TODO Auto-generated method stub
	}
	
	public List<SelectItem> getLayouts(){
		List<SelectItem> result = new ArrayList<SelectItem>(1);
		result.add(new SelectItem(0, LAYOUT_0));
		return result;
	}

	public java.util.List<IProperty> retrievePropriedadesPreenchimentoManual(br.com.orionsoft.monstrengo.crud.entity.IEntity<? extends DocumentoPagamento> documento) throws BusinessException {
		List<IProperty> result = new ArrayList<IProperty>();
		// Verifica se o documento passado extend Documento
		if(!ClassUtils.getAllSuperclasses(documento.getInfo().getType()).contains(DocumentoPagamento.class))
			// SENAO ERRO: A entidade fornecida não é uma extensão de um Documento do Financeiro;
			throw new BusinessException (MessageList.create(ProvedorDocumentoPagamento.class,"OBJETO_INCOMPATIVEL", documento.toString()));
	
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
	
//    /**
//     * Este método é útil para que os gerenciadores insiram grupos com os valores
//     * de Taxas, Juros e Multas que são detectadas durante o recebimento de um determinado Título.
//     * @param documento Documento de cobrança que está sendo quitado
//     * @param contaId Conta onde deverá ser registrados os novos grupos
//     * @param dataVencimento A data de vencimento que é igual à data de quitação
//     * @param serviceDataOwner ServiceData com a atual configuração de sessão
//     * @param contratoId Identificador do contrato utilizado
//     * @param valor Valor do grupo a ser inserido
//     * @param centroCustoId Centro de Custo
//     * @param itemCustoId Item de Custo
//     * @param tipoTransacao Tipo de Transação ENTRADA/SAIDA | CRÉDITO/DÉBITO
//     * @throws ServiceException
//     * @throws DocumentoPagamentoException
//     * @throws EntityException
//     */
//	protected void inserirLancamento(IEntity documento, Long contaId, Calendar dataVencimento, ServiceData serviceDataOwner, Long contratoId, BigDecimal valor, Long centroCustoId, Long itemCustoId, int tipoTransacao) throws ServiceException, DocumentoPagamentoException, EntityException {
//        ServiceData inserirMovimento =  new ServiceData(InserirLancamentoService.SERVICE_NAME, serviceDataOwner);
//        inserirMovimento.getArgumentList().setProperty(InserirLancamentoService.IN_CENTRO_CUSTO, centroCustoId);
//        inserirMovimento.getArgumentList().setProperty(InserirLancamentoService.IN_CONTA_PREVISTA_OPT, contaId);
//        inserirMovimento.getArgumentList().setProperty(InserirLancamentoService.IN_CONTRATO, contratoId);
//        inserirMovimento.getArgumentList().setProperty(InserirLancamentoService.IN_DATA, CalendarUtils.getCalendar());
//        inserirMovimento.getArgumentList().setProperty(InserirLancamentoService.IN_DATA_VENCIMENTO, dataVencimento);
//        inserirMovimento.getArgumentList().setProperty(InserirLancamentoService.IN_DESCRICAO_OPT, "");
//        inserirMovimento.getArgumentList().setProperty(InserirLancamentoService.IN_DOCUMENTO_COBRANCA_OPT, documento);
//        inserirMovimento.getArgumentList().setProperty(InserirLancamentoService.IN_ITEM_CUSTO_LIST, Collections.singletonList(itemCustoId));
//        inserirMovimento.getArgumentList().setProperty(InserirLancamentoService.IN_TRANSACAO, tipoTransacao);
//        inserirMovimento.getArgumentList().setProperty(InserirLancamentoService.IN_VALOR, valor);
//        this.getProvedorDocumentoPagamento().getServiceManager().execute(inserirMovimento);
//
//        if(!inserirMovimento.getMessageList().isTransactionSuccess()){
//        	inserirMovimento.getMessageList().add(new BusinessMessage(GerenciadorDocumentoPagamentoBasic.class, "ERRO_INSERINDO_OUTROS_LANCAMENTOS", documento.toString(), "Desconto"));
//        	throw new DocumentoPagamentoException(inserirMovimento.getMessageList());
//        }
//
//        /* Criando um documento de quitação */
//        IEntity contrato = null;
//        IEntity documentoCobrancaCategoria = null;
//		try {
//			documentoCobrancaCategoria = documento.getProperty(DocumentoPagamento.CONVENIO_PAGAMENTO).getValue().getAsEntity().getProperty(ConvenioPagamento.DOCUMENTO_PAGAMENTO_CATEGORIA).getValue().getAsEntity();
//			contrato = UtilsCrud.retrieve(this.getProvedorDocumentoPagamento().getServiceManager(), Contrato.class, contratoId,serviceDataOwner);
//		} catch (PropertyValueException e1) {
//			throw new DocumentoPagamentoException(MessageList.create(GerenciadorDocumentoPagamentoBasic.class, "ERRO_FORMA_PAGAMENTO_INSERINDO_GRUPO", documento.toString(), formaPagamento.toString()));
//		} catch (BusinessException e2) {
//			throw new DocumentoPagamentoException(MessageList.create(GerenciadorDocumentoPagamentoBasic.class, "ERRO_CONTRATO_INSERINDO_GRUPO", documento.toString(), contrato.toString()));
//		}
//
//        /* Cria um novo documento (no caso, será o documento de quitação) */
//        ServiceData sdCriarDocumento = new ServiceData(CriarDocumentoCobrancaService.SERVICE_NAME, null);
//        sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DOCUMENTO_COBRANCA_CATEGORIA, formaPagamento);
//        sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.CONTRATO, contrato);
//        sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DATA_DOCUMENTO, CalendarUtils.getCalendar());
//        sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DATA_VENCIMENTO, dataVencimento);
//        sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_VALOR_DOCUMENTO, valor);
//        sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_TRANSICAO, tipoTransacao == Lancamento.TRANSACAO_CREDITO);
//        this.getProvedorDocumentoPagamento().getServiceManager().execute(sdCriarDocumento);
//
//        IEntity documentoQuitacao = sdCriarDocumento.getFirstOutput();
//        
//        Long grupoId = ((IEntity)inserirMovimento.getFirstOutput()).getId();
//        ServiceData quitarGrupo = new ServiceData(QuitarLancamentoService.SERVICE_NAME, serviceDataOwner);
//        quitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_CONTA, contaId);
//        quitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_DATA, dataVencimento);
//        quitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_DOCUMENTO_PAGAMENTO_OPT, documentoQuitacao);
//        quitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_LANCAMENTO, grupoId);
//        quitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_VALOR, valor);
//        this.getProvedorDocumentoPagamento().getServiceManager().execute(quitarGrupo);
//    }
	
 }