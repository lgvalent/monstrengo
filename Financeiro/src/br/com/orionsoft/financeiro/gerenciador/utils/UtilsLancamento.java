package br.com.orionsoft.financeiro.gerenciador.utils;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria;
import br.com.orionsoft.financeiro.documento.cobranca.services.CriarDocumentoCobrancaService;
import br.com.orionsoft.financeiro.documento.cobranca.services.ImprimirDocumentosCobrancaService;
import br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.ClassificacaoContabil;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem;
import br.com.orionsoft.financeiro.gerenciador.entities.Operacao;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.financeiro.gerenciador.services.InserirLancamentoService;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService.Situacao;
import br.com.orionsoft.financeiro.gerenciador.services.ListarPosicaoContratoService;
import br.com.orionsoft.financeiro.gerenciador.services.VerificarLancamentoQuitadoService;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * @author Antonio Alves
 * @since 04/09/2007
 * @version 1.4
 */
public class UtilsLancamento {
	
	/**
	 * Insere um lançamento a partir dos parâmetros.
	 * Cria automaticamente um documento de cobrança com a categoria.
	 * Cria uma coleção de LancamentoItem com um elemento usando CentroCusto, ItemCusto e ClassificacaoContabil.
	 * 
	 * @param serviceManager
	 * @param contaPrevistaOpt
	 * @param contrato
	 * @param data
	 * @param dataVencimento
	 * @param descricaoOpt
	 * @param operacao
	 * @param transacao
	 * @param centroCusto
	 * @param classificacaoContabil
	 * @param itemCusto
	 * @param valorItem
	 * @param documentoCobrancaCategoriaOpt
	 * @param naoReceberAposVencimento,
	 * @param serviceDataOwner
	 * 
	 * @return Lancamento
	 * 
	 * @throws BusinessException
	 */
	public static Lancamento inserir(
			IServiceManager serviceManager, 
			Conta contaPrevistaOpt, 
			Contrato contrato, 
			Calendar data, 
			Calendar dataVencimento, 
			String descricaoOpt, 
			Operacao operacao, 
			Transacao transacao, 
			CentroCusto centroCusto, 
			ClassificacaoContabil classificacaoContabil, 
			ItemCusto itemCusto, 
			BigDecimal valorItem, 
			DocumentoCobrancaCategoria documentoCobrancaCategoriaOpt,
			boolean naoReceberAposVencimento,
			ServiceData serviceDataOwner
			) throws BusinessException {
		/*
		 * Cria a lista de LancamentoItem para usar no serviço InserirLancamentoService.
		 */
		List<LancamentoItem> lancamentoItemList = new ArrayList<LancamentoItem>(0); 
		LancamentoItem lancamentoItem = UtilsCrud.objectCreate(serviceManager, LancamentoItem.class, serviceDataOwner);
		lancamentoItem.setDataLancamento(data);
		lancamentoItem.setDataCompetencia(dataVencimento);
		lancamentoItem.setCentroCusto(centroCusto);
		lancamentoItem.setClassificacaoContabil(classificacaoContabil);
		lancamentoItem.setItemCusto(itemCusto);
		lancamentoItem.setValor(valorItem);
		lancamentoItemList.add(lancamentoItem);
		
		/*
		 * Cria um documento de cobrança para usar no lançamento.
		 */
		IEntity documentoCobranca = null;
		if (documentoCobrancaCategoriaOpt != null) {
			IEntity contratoEntity = serviceManager.getEntityManager().getEntity(contrato);
			IEntity documentoCobrancaCategoriaEntity = serviceManager.getEntityManager().getEntity(documentoCobrancaCategoriaOpt);
			ServiceData sdCriarDocumento = new ServiceData(CriarDocumentoCobrancaService.SERVICE_NAME, serviceDataOwner);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_CONTRATO, contratoEntity);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DATA_DOCUMENTO, data);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DATA_VENCIMENTO, dataVencimento);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DOCUMENTO_COBRANCA_CATEGORIA, documentoCobrancaCategoriaEntity);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_TRANSACAO, transacao);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_VALOR_DOCUMENTO, valorItem);
			serviceManager.execute(sdCriarDocumento);
			documentoCobranca = sdCriarDocumento.getFirstOutput();
		}

		/*
		 * Chama o serviço InserirLancamentoService e o executa.
		 */
		ServiceData sdLancamento = new ServiceData(InserirLancamentoService.SERVICE_NAME, serviceDataOwner);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_CONTA_PREVISTA_OPT, contaPrevistaOpt);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_CONTRATO, contrato);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_DATA, data);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_DATA_VENCIMENTO, dataVencimento);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_DESCRICAO_OPT, descricaoOpt);
		if(documentoCobranca !=null)
			sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_DOCUMENTO_COBRANCA_OPT, documentoCobranca.getObject());
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_LANCAMENTO_ITEM_LIST, lancamentoItemList);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_OPERACAO, operacao);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_TRANSACAO, transacao);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_NAO_RECEBER_APOS_VENCIMENTO, naoReceberAposVencimento);
		serviceManager.execute(sdLancamento); 
		return (Lancamento)sdLancamento.getFirstOutput();
	}

	/**
	 * Insere um lançamento a partir dos parâmetros.
	 * Cria automaticamente um documento de cobrança com a categoria.
	 * 
	 * @param serviceManager
	 * @param contaPrevistaOpt
	 * @param contrato
	 * @param data
	 * @param dataVencimento
	 * @param descricaoOpt
	 * @param operacao
	 * @param transacao
	 * @param lancamentoItemList
	 * @param documentoCobrancaCategoriaOpt
	 * @param naoReceberAposVencimento,
	 * @param serviceDataOwner
	 * 
	 * @return Lancamento
	 * 
	 * @throws BusinessException
	 */
	public static Lancamento inserir(
			IServiceManager serviceManager, 
			Conta contaPrevistaOpt, 
			Contrato contrato, 
			Calendar data, 
			Calendar dataVencimento, 
			String descricaoOpt, 
			Operacao operacao, 
			Transacao transacao, 
			List<LancamentoItem> lancamentoItemList, 
			DocumentoCobrancaCategoria documentoCobrancaCategoriaOpt, 
			boolean naoReceberAposVencimento,
			ServiceData serviceDataOwner
			) throws BusinessException {
		/*
		 * Soma os valores dos item da lista de LancamentoItem.
		 */
		BigDecimal valor = DecimalUtils.ZERO;
		for (LancamentoItem item : lancamentoItemList) {
			valor = valor.add(item.getValor());
		}
		
		/*
		 * Cria um documento de cobrança para usar no lançamento.
		 */
		IEntity documentoCobranca = null;
		if (documentoCobrancaCategoriaOpt != null) {
			IEntity contratoEntity = serviceManager.getEntityManager().getEntity(contrato);
			IEntity documentoCobrancaCategoriaEntity = serviceManager.getEntityManager().getEntity(documentoCobrancaCategoriaOpt);
			ServiceData sdCriarDocumento = new ServiceData(CriarDocumentoCobrancaService.SERVICE_NAME, serviceDataOwner);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_CONTRATO, contratoEntity);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DATA_DOCUMENTO, data);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DATA_VENCIMENTO, dataVencimento);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DOCUMENTO_COBRANCA_CATEGORIA, documentoCobrancaCategoriaEntity);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_TRANSACAO, transacao);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_VALOR_DOCUMENTO, valor);
			serviceManager.execute(sdCriarDocumento);
			documentoCobranca = sdCriarDocumento.getFirstOutput();
		}

		/*
		 * Chama o serviço InserirLancamentoService e o executa.
		 */
		ServiceData sdLancamento = new ServiceData(InserirLancamentoService.SERVICE_NAME, serviceDataOwner);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_CONTA_PREVISTA_OPT, contaPrevistaOpt);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_CONTRATO, contrato);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_DATA, data);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_DATA_VENCIMENTO, dataVencimento);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_DESCRICAO_OPT, descricaoOpt);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_DOCUMENTO_COBRANCA_OPT, documentoCobranca.getObject());
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_LANCAMENTO_ITEM_LIST, lancamentoItemList);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_OPERACAO, operacao);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_TRANSACAO, transacao);
		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_NAO_RECEBER_APOS_VENCIMENTO, naoReceberAposVencimento);
		serviceManager.execute(sdLancamento); 
		return (Lancamento)sdLancamento.getFirstOutput();
	}

	/**
	 * Imprime o documento de cobrança em um stream.
	 * 
	 * @param serviceManager
	 * @param documentoCobranca
	 * @param stream
	 * @param serviceDataOwner
	 * 
	 * @throws BusinessException
	 */
	public static void imprimirDocumentoCobranca(
			IServiceManager serviceManager, 
			DocumentoCobranca documentoCobranca,
			OutputStream stream,
			int printerIndex,
			ServiceData serviceDataOwner
			) throws BusinessException {

		ServiceData sd = new ServiceData(ImprimirDocumentosCobrancaService.SERVICE_NAME, serviceDataOwner);
		sd.getArgumentList().setProperty(ImprimirDocumentosCobrancaService.IN_DOCUMENTO_OPT, serviceManager.getEntityManager().getEntity(documentoCobranca));
		sd.getArgumentList().setProperty(ImprimirDocumentosCobrancaService.IN_OUTPUT_STREAM_OPT, stream);
		sd.getArgumentList().setProperty(ImprimirDocumentosCobrancaService.IN_PRINTER_INDEX_OPT, printerIndex);
		serviceManager.execute(sd);
	}

	/**
	 * Imprime o documento de cobrança em um stream.
	 * 
	 * @param serviceManager
	 * @param lancamento
	 * @param stream
	 * @param serviceDataOwner
	 * 
	 * @throws BusinessException
	 */
	public static void imprimirDocumentoCobranca(
			IServiceManager serviceManager, 
			Lancamento lancamento,
			OutputStream stream,
			int printerIndex,
			ServiceData serviceDataOwner
			) throws BusinessException {
		DocumentoCobranca documentoCobranca = lancamento.getDocumentoCobranca();
		imprimirDocumentoCobranca(serviceManager, documentoCobranca, stream, printerIndex, serviceDataOwner);
	}

	/**
	 * Verifica se um lançamento está quitado.
	 * 
	 * @param serviceManager
	 * @param lancamento
	 * @param serviceDataOwner
	 * 
	 * @return true se o lançamento está quitado e false se não estiver quitado.
	 * 
	 * @throws ServiceException 
	 */
	public static boolean verificarLancamentoQuitado(
			IServiceManager serviceManager, 
			Lancamento lancamento, 
			ServiceData serviceDataOwner
			) throws ServiceException {
		ServiceData sd = new ServiceData(VerificarLancamentoQuitadoService.SERVICE_NAME, serviceDataOwner);
		sd.getArgumentList().setProperty(VerificarLancamentoQuitadoService.IN_LANCAMENTO, lancamento);
		serviceManager.execute(sd);
		
		return sd.getFirstOutput();
	}

	/**
	 * Verifica as pendências vencidas de um determinado contrato, que atendam a uma 
	 * lista de item de custo.
	 * 
	 * @param serviceManager
	 * @param contrato
	 * @param dataLimite
	 * @param itemCustoList
	 * @param serviceDataOwner
	 * @return true se algum item de custo estiver pendente e vencido.
	 * @throws ServiceException 
	 */
	public static boolean verificarPendenciasFinanceirasVencidas(
			IServiceManager serviceManager,
			Pessoa pessoa,
			Calendar dataLimite,
			Long[] itemCustoIds,
			ServiceData serviceDataOwner
			) throws ServiceException {
		ServiceData sd = new ServiceData(ListarPosicaoContratoService.SERVICE_NAME, serviceDataOwner);
		sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_DOCUMENTO_OPT, pessoa.getDocumento());
		sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_ITEM_CUSTO_IDS_OPT, itemCustoIds);
		sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_DATA_INICIAL_OPT, CalendarUtils.getCalendarBaseDate());
		sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_DATA_FINAL_OPT, dataLimite);
		sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_SITUACAO_OPT, Situacao.VENCIDO);
		serviceManager.execute(sd);
		IEntityList<Lancamento> list = sd.getFirstOutput();
		return !list.isEmpty();
	}
}
