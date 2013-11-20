package br.com.orionsoft.financeiro.gerenciador.services;

import java.math.BigDecimal;
import java.util.Calendar;

import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Serviço que insere uma transferencia
 * 
 * <p>
 * <b>Argumento:</b><br>
 * 
 * <p>
 * <b>Procedimento:</b>
 * 
 * @author Juan Garay III
 * @version 20070716
 * 
 * @spring.bean id="TransferirService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class TransferirService extends ServiceBasic {
	public static final String SERVICE_NAME = "TransferenciaService";

	/*
	 * Constante de parametros de entrada.
	 */
	public static final String IN_DATA = "data";
	public static final String IN_VALOR = "valor";
	public static final String IN_CONTA_ORIGEN = "contaOrigem";
	public static final String IN_CONTA_DESTINO = "contaDestino";
	public static final String IN_DESCRICAO = "descricao";
//	public static final String IN_CATEGORIA = "categoria";

	public void execute(ServiceData serviceData) throws ServiceException {
		try {
			log.debug("::Iniciando a execução do serviço TransferirService");
			/* Parametros Obrigatórios */
			Calendar inData = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA);
			BigDecimal inValor = (BigDecimal) serviceData.getArgumentList().getProperty(IN_VALOR);
			Conta inContaOrigem = (Conta) serviceData.getArgumentList().getProperty(IN_CONTA_ORIGEN);
			Conta inContaDestino = (Conta) serviceData.getArgumentList().getProperty(IN_CONTA_DESTINO);
			String inDescricao = (String) serviceData.getArgumentList().getProperty(IN_DESCRICAO);
			
			/*Ele chama o inserirlancamentoService, para criar um novo lancamento Origem*/
			ServiceData sd = new ServiceData(InserirLancamentoMovimentoService.SERVICE_NAME, serviceData);
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA, inData);
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA_COMPENSACAO_OPT, inData);
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_VALOR, inValor.negate());
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_CONTA, inContaOrigem);
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DESCRICAO, inDescricao);
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_LANCAMENTO, null);
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_LANCAMENTO_MOVIMENTO_CATEGORIA, LancamentoMovimentoCategoria.TRANSFERIDO);
			this.getServiceManager().execute(sd);
			LancamentoMovimento lancamentoMovimentoOrigem = sd.getFirstOutput();
			
			/*Ele chama o inserirlancamentoService, para criar um novo lancamento Destino*/
			sd = new ServiceData(InserirLancamentoMovimentoService.SERVICE_NAME, serviceData);
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA, inData);
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DATA_COMPENSACAO_OPT, inData);
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_VALOR, inValor);
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_CONTA, inContaDestino);
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_DESCRICAO, inDescricao);
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_LANCAMENTO, null);
			sd.getArgumentList().setProperty(InserirLancamentoMovimentoService.IN_LANCAMENTO_MOVIMENTO_CATEGORIA,LancamentoMovimentoCategoria.TRANSFERIDO);
			this.getServiceManager().execute(sd);
			LancamentoMovimento lancamentoMovimentoDestino = sd.getFirstOutput();
			
			lancamentoMovimentoOrigem.setTransferencia(lancamentoMovimentoDestino);
			UtilsCrud.objectUpdate(this.getServiceManager(), lancamentoMovimentoOrigem, serviceData);
			
			lancamentoMovimentoDestino.setTransferencia(lancamentoMovimentoOrigem);
			UtilsCrud.objectUpdate(this.getServiceManager(), lancamentoMovimentoDestino, serviceData);
			
			serviceData.getOutputData().add(lancamentoMovimentoOrigem);

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

	public String getServiceName() {
		return SERVICE_NAME;
	}

}
