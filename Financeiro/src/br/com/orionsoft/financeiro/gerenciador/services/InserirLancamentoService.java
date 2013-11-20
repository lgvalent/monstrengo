/*
 * Created on 19/04/2006 by antonio
 */
package br.com.orionsoft.financeiro.gerenciador.services;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;


import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.services.LancarDocumentoCobrancaService;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.services.LancarDocumentoPagamentoService;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoDvo;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoSituacao;
import br.com.orionsoft.financeiro.gerenciador.entities.Operacao;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Serviço que insere um movimento na conta. A entidade documento passada como
 * parâmetro não deve estar persistida, dever ser um Documento ainda não
 * persisto. Antes de persistir o Lancamento, este serviço irá ligar o documento
 * passado com o grupo criado, e o mecanismo de persistência irá persistir o
 * Grupo e o Documento juntos, uma vez que Documento depende de grupo para pegar
 * o id (pois é um relacionamento hibernate one-to-one)
 * 
 * <p>
 * <b>Argumento:</b><br>
 * 
 * <p>
 * <b>Procedimento:</b>
 * 
 * @author Antonio Alves
 * @version 20060420
 * 
 * @spring.bean id="InserirLancamentoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class InserirLancamentoService extends ServiceBasic {
	public static final String SERVICE_NAME = "InserirLancamentoService";

	/*
	 * Constantes de parâmetros de entrada.
	 */
	public static final String IN_DESCRICAO_OPT = "descricao";
	public static final String IN_LANCAMENTO_ITEM_LIST = "lancamentoItemList";
	public static final String IN_CONTRATO = "contrato";
	public static final String IN_DOCUMENTO_COBRANCA_OPT = "documentoCobranca";
	public static final String IN_DOCUMENTO_PAGAMENTO_OPT = "documentoPagamento";
	public static final String IN_CONTA_PREVISTA_OPT = "contaPrevista";
	public static final String IN_DATA = "data";
	public static final String IN_DATA_VENCIMENTO = "dataVencimento";
	public static final String IN_TRANSACAO = "transacao";
	public static final String IN_OPERACAO = "operacao";
	public static final String IN_NAO_RECEBER_APOS_VENCIMENTO = "naoReceberAposVencimento";

	public String getServiceName() {
		return SERVICE_NAME;
	}

	@SuppressWarnings("unchecked")
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando a execução do serviço InserirLancamentoService");
		try {
			/* 
			 * Parâmetros obrigatórios 
			 */
			Contrato inContrato = (Contrato) serviceData.getArgumentList().getProperty(IN_CONTRATO);
			Calendar inData = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA);
			Calendar inDataVencimento = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_VENCIMENTO);
			Transacao inTransacao = (Transacao) serviceData.getArgumentList().getProperty(IN_TRANSACAO);
			List<LancamentoItem> inLancamentoItemList = (List<LancamentoItem>) serviceData.getArgumentList().getProperty(IN_LANCAMENTO_ITEM_LIST);

			/* 
			 * Parâmetros opcionais 
			 */
			String inDescricao = (serviceData.getArgumentList().containsProperty(IN_DESCRICAO_OPT) ? 
					(String) serviceData.getArgumentList().getProperty(IN_DESCRICAO_OPT) : null);
			Conta inContaPrevista = (serviceData.getArgumentList().containsProperty(IN_CONTA_PREVISTA_OPT) ? 
					(Conta) serviceData.getArgumentList().getProperty(IN_CONTA_PREVISTA_OPT) : null);
			DocumentoCobranca inDocumentoCobranca = (serviceData.getArgumentList().containsProperty(IN_DOCUMENTO_COBRANCA_OPT) ? 
					(DocumentoCobranca) serviceData.getArgumentList().getProperty(IN_DOCUMENTO_COBRANCA_OPT) : null);
			DocumentoPagamento inDocumentoPagamento = (serviceData.getArgumentList().containsProperty(IN_DOCUMENTO_PAGAMENTO_OPT) ? 
					(DocumentoPagamento) serviceData.getArgumentList().getProperty(IN_DOCUMENTO_PAGAMENTO_OPT) : null);
			Operacao inOperacao = (serviceData.getArgumentList().containsProperty(IN_OPERACAO) ?
					(Operacao) serviceData.getArgumentList().getProperty(IN_OPERACAO) : null);
			Boolean inNaoReceberAposVencimento = serviceData.getArgumentList().containsProperty(IN_NAO_RECEBER_APOS_VENCIMENTO) ? 
					(Boolean) serviceData.getArgumentList().getProperty(IN_NAO_RECEBER_APOS_VENCIMENTO) : false;
			
			/* 
			 * Cria as entidades Lancamento e extrai os objetos dos IEntity 
			 */
			Lancamento lancamento = UtilsCrud.objectCreate(this.getServiceManager(), Lancamento.class, serviceData);

			BigDecimal valorTotal = BigDecimal.ZERO;
			
			/* 
			 * Verificando débito ou crédito e totalizando o valor que é 
			 * armazenado no lancamento ITEM
			 */
			for (LancamentoItem lancamentoItem : inLancamentoItemList) {
				/* 
				 * Extrair o valor digitado na propriedade PESO 
				 */
				BigDecimal valorItem = (BigDecimal) lancamentoItem.getValor().multiply(inTransacao.getMultiplicador());
				
				/* 
				 * Define débito ou crédito de acordo com o tipo de transação.
				 * Se for DEBITO ele negará o valor digitado 
				 */
				lancamentoItem.setValor(valorItem);
//				if (inTransacao == Transacao.DEBITO){
//					valorItem = valorItem.negate();
//					lancamentoItem.setValor(valorItem);
//				}
				
				/* Somando o valor total */
				valorTotal = valorTotal.add(valorItem);
			}

			LancamentoDvo.calculaPesos(inLancamentoItemList, valorTotal);
			
			/* Define as propriedades de Lancamento */
			lancamento.setContrato(inContrato);
			lancamento.setDescricao(inDescricao);
			lancamento.setDocumentoCobranca(inDocumentoCobranca);
			lancamento.setDocumentoPagamento(inDocumentoPagamento);
			lancamento.setContaPrevista(inContaPrevista);
			lancamento.setValor(valorTotal);
			lancamento.setSaldo(valorTotal);
			lancamento.setData(inData);
			lancamento.setDataVencimento(inDataVencimento);
			lancamento.setOperacao(inOperacao);
			lancamento.setLancamentoItens(inLancamentoItemList);
			lancamento.setNaoReceberAposVencimento(inNaoReceberAposVencimento);
			lancamento.setLancamentoSituacao(LancamentoSituacao.PENDENTE);

			/*
			 * Liga o Lancamento com o Documento, pois o documento depende do id
			 * do Lancamento e o lancamento quando é persistido automaticamente
			 * persiste o Documento
			 */
			if (inDocumentoCobranca != null) {
				if (inDocumentoCobranca.getId() == IDAO.ENTITY_UNSAVED) {
					ServiceData sdLancarDocumento = new ServiceData(LancarDocumentoCobrancaService.SERVICE_NAME, serviceData);

					sdLancarDocumento.getArgumentList().setProperty(LancarDocumentoCobrancaService.IN_DOCUMENTO, inDocumentoCobranca);
					this.getServiceManager().execute(sdLancarDocumento);
				}
			}

			if (inDocumentoPagamento != null) {
				if (inDocumentoPagamento.getId() == IDAO.ENTITY_UNSAVED) {
					ServiceData sdLancarDocumento = new ServiceData(LancarDocumentoPagamentoService.SERVICE_NAME, serviceData);

					sdLancarDocumento.getArgumentList().setProperty(LancarDocumentoPagamentoService.IN_DOCUMENTO, inDocumentoPagamento);
					this.getServiceManager().execute(sdLancarDocumento);
				}
			}
			/* Atualiza Lancamento e LancamentoItem */
			UtilsCrud.objectUpdate(this.getServiceManager(), lancamento, serviceData);
//			System.out.println("NOSONUMEROOOO:" + lancamento.getDocumentoCobranca().getConvenioCobranca().getSequenciaNumeroDocumento());
//			System.out.println("NOSONUMEROOOO:" + inDocumentoCobranca.getDocumentoCobrancaCategoria().getConvenioCobranca().getSequenciaNumeroDocumento());
//			System.out.println("NOSONUMEROOOO:" + inDocumentoCobranca.getConvenioCobranca().getSequenciaNumeroDocumento());

			serviceData.getOutputData().add(lancamento);

		} catch (BusinessException e) {
			log.fatal(e.getErrorList());
			throw new ServiceException(e.getErrorList());
		} catch (Exception e) {
			log.fatal(e.getMessage());
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
	}

	public static void main(String[] args){
		BigDecimal v1 = new BigDecimal("0.00");
	    BigDecimal v2 = new BigDecimal("0.00");
		
		Double d = v1.doubleValue()/v2.doubleValue();
		System.out.println(d);
		
		v1 = new BigDecimal(477.85);
		v2 = new BigDecimal(55.13);
		BigDecimal r1 = v2.divide(v1, MathContext.DECIMAL32);
		BigDecimal r2 = v2.divide(v1, MathContext.DECIMAL64);
		BigDecimal r3 = v2.divide(v1, MathContext.DECIMAL128);
		BigDecimal r4 = v2.divide(v1, 8, RoundingMode.HALF_DOWN);
		BigDecimal r5 = v2.divide(v1, 16, RoundingMode.HALF_DOWN);
		BigDecimal r6 = v2.divide(v1, 32, RoundingMode.HALF_DOWN);
		System.out.println(r1);
		System.out.println(r2);
		System.out.println(r3);
		System.out.println(r4);
		System.out.println(r5);
		System.out.println(r6);
		System.out.println(r1.multiply(v1));
		System.out.println(r2.multiply(v1));
		System.out.println(r3.multiply(v1));
		System.out.println(r4.multiply(v1));
		System.out.println(r5.multiply(v1));
		System.out.println(r6.multiply(v1));
	}
}
