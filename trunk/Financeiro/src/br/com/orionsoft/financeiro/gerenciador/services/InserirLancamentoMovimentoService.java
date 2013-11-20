/*
 * Created on 19/04/2006 by antonio
 */
package br.com.orionsoft.financeiro.gerenciador.services;

import java.math.BigDecimal;
import java.util.Calendar;

import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.services.LancarDocumentoPagamentoService;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Serviço que insere um movimento na conta.
 * 
 * <p>
 * <b>Argumento:</b><br>
 * 
 * <p>
 * <b>Procedimento:</b>
 * 
 * @author Antonio Alves
 * @version 20070716
 * @version 20081028 Lucio - Cálculo e persitência do valorTotal
 * 
 * @spring.bean id="InserirLancamentoMovimentoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class InserirLancamentoMovimentoService extends ServiceBasic {
	public static final String SERVICE_NAME = "InserirLancamentoMovimentoService";
	
	/*
	 * Constantes de Parametro de Entrada.
	 * Andre, 19/06/2008: Adicionados os campos Juros e Multa. Tais devem ser informados com valores em Reais (R$)
	 * Andre, 12/07/2008: Adicionados os campos Acréscimo e Desconto. Tais devem ser informados com valores em Reais (R$)
	 */
	public static final String IN_LANCAMENTO = "lancamento";
	public static final String IN_VALOR = "valor";
	public static final String IN_JUROS_OPT = "juros";
	public static final String IN_MULTA_OPT = "multa";
	public static final String IN_ACRESCIMO_OPT = "acrescimo";
	public static final String IN_DESCONTO_OPT = "desconto";
	public static final String IN_DATA = "data";
	public static final String IN_DATA_COMPENSACAO_OPT = "dataCompensacao";
	public static final String IN_DESCRICAO = "descricao";
	public static final String IN_LANCAMENTO_MOVIMENTO_CATEGORIA = "lancamentoMovimentoCategoria";
	public static final String IN_CONTA = "conta";
	public static final String IN_DOCUMENTO_PAGAMENTO_OPT = "documentoPagamento";
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando a execução do serviço InserirLancamentoMovimentoService");
		try {
			/*
			 * Parâmetros obrigatórios
			 */
			BigDecimal inValor =(BigDecimal) serviceData.getArgumentList().getProperty(IN_VALOR);
			Calendar inData = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA);
			String inDescricao = (String) serviceData.getArgumentList().getProperty(IN_DESCRICAO);
			LancamentoMovimentoCategoria inLancamentoMovimentoCategoria = (LancamentoMovimentoCategoria) serviceData.getArgumentList().getProperty(IN_LANCAMENTO_MOVIMENTO_CATEGORIA);
			Conta inConta = (Conta) serviceData.getArgumentList().getProperty(IN_CONTA);
			Lancamento inLancamento = (Lancamento) serviceData.getArgumentList().getProperty(IN_LANCAMENTO);
			
			/*
			 * Parâmetros Opcionais
			 */
			Calendar inDataCompensacao = (serviceData.getArgumentList().containsProperty(IN_DATA_COMPENSACAO_OPT) ? 
					(Calendar) serviceData.getArgumentList().getProperty(IN_DATA_COMPENSACAO_OPT) : null);
			DocumentoPagamento inDocumentoPagamento = (serviceData.getArgumentList().containsProperty(IN_DOCUMENTO_PAGAMENTO_OPT) ? 
					(DocumentoPagamento) serviceData.getArgumentList().getProperty(IN_DOCUMENTO_PAGAMENTO_OPT) : null);
			BigDecimal inJuros = serviceData.getArgumentList().containsProperty(IN_JUROS_OPT) ? (BigDecimal) serviceData.getArgumentList().getProperty(IN_JUROS_OPT) : null;
			BigDecimal inMulta = serviceData.getArgumentList().containsProperty(IN_MULTA_OPT) ? (BigDecimal) serviceData.getArgumentList().getProperty(IN_MULTA_OPT) : null;
			BigDecimal inAcrescimo = serviceData.getArgumentList().containsProperty(IN_ACRESCIMO_OPT) ? (BigDecimal) serviceData.getArgumentList().getProperty(IN_ACRESCIMO_OPT) : null;
			BigDecimal inDesconto = serviceData.getArgumentList().containsProperty(IN_DESCONTO_OPT) ? (BigDecimal) serviceData.getArgumentList().getProperty(IN_DESCONTO_OPT) : null;
			
			/* Inicializa o valor Total */
			BigDecimal valorTotal = inValor;
					
			/* 
			 * Cria a entindade LancamentoMovimento  
			 */	
			LancamentoMovimento lancamentoMovimento = UtilsCrud.objectCreate(this.getServiceManager(), LancamentoMovimento.class, serviceData);

			/* 
			 * Define as propriedades
			 */
			lancamentoMovimento.setLancamentoMovimentoCategoria(inLancamentoMovimentoCategoria);
			lancamentoMovimento.setLancamento(inLancamento);
			lancamentoMovimento.setValor(inValor);
			if (inJuros != null){
				lancamentoMovimento.setJuros(inJuros);
				valorTotal = valorTotal.add(inJuros);
			}
			if (inMulta != null){
				lancamentoMovimento.setMulta(inMulta);
				valorTotal = valorTotal.add(inMulta);
			}
			if (inAcrescimo != null){
				lancamentoMovimento.setAcrescimo(inAcrescimo);
				valorTotal = valorTotal.add(inAcrescimo);
			}
			if (inDesconto != null){
				lancamentoMovimento.setDesconto(inDesconto);
				valorTotal = valorTotal.subtract(inDesconto);
			}
			lancamentoMovimento.setValorTotal(valorTotal);
			lancamentoMovimento.setDataLancamento(CalendarUtils.getCalendar());
			lancamentoMovimento.setData(inData);
			lancamentoMovimento.setDataCompensacao(inDataCompensacao);
			lancamentoMovimento.setDescricao(inDescricao);
			lancamentoMovimento.setConta(inConta);
			lancamentoMovimento.setDocumentoPagamento(inDocumentoPagamento);
		
            /* 
             * Insere o atual movimento no lançamento atual.
             * (pode ser nulo quando vem de transferência).
             */
			if (inLancamento != null)
				inLancamento.getLancamentoMovimentos().add(lancamentoMovimento);

			if (inDocumentoPagamento != null) {
				if (inDocumentoPagamento.getId() == IDAO.ENTITY_UNSAVED) {
					ServiceData sdLancarDocumento = new ServiceData(LancarDocumentoPagamentoService.SERVICE_NAME, serviceData);

					sdLancarDocumento.getArgumentList().setProperty(LancarDocumentoPagamentoService.IN_DOCUMENTO, inDocumentoPagamento);
					this.getServiceManager().execute(sdLancarDocumento);
				}
			}

			/* 
			 * Atualiza Lancamento e LancamentoMovimento 
			 */
			UtilsCrud.objectUpdate(this.getServiceManager(), lancamentoMovimento, serviceData);
			
			/*
			 * Retorna o lancamento movimento criado 
			 */
			serviceData.getOutputData().add(lancamentoMovimento);
		
		} catch (BusinessException e) {
			log.fatal(e.getErrorList());
			/*
			 * O Serviço não precisa adicionar mensagem local. O Manager já
			 * indica qual srv falhou e os parâmetros.
			 */
			throw new ServiceException(e.getErrorList());
		} catch (Exception e) {
			log.fatal(e.getMessage());
			/*
			 * Indica que o serviço falhou por causa de uma exceção do
			 * hibernate.
			 */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
	}
	/**
	 * Constrói uma lista de opções com os tipo possíveis de transação que podem
	 * ser geradas pelo processo. 
	 * @return
	 */
	/*public static List<SelectItem> getTiposTransacao() {
		List<SelectItem> result = new ArrayList<SelectItem>(3);
		result.add(new SelectItem(LancamentoMovimento.TRANSACAO_CREDITO, "Entrada"));
		result.add(new SelectItem(LancamentoMovimento.TRANSACAO_DEBITO, "Saída"));
		return result;
	}*/
}
