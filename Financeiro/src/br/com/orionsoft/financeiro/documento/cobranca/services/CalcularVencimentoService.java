package br.com.orionsoft.financeiro.documento.cobranca.services;

import java.util.Calendar;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.IGerenciadorDocumentoCobranca;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este serviço é utilizado para calcular um vencimento.
 * 
 * <p>Argumentos:</p>
 * IN_DOCUMENTO: (IEntity) Instancia do documento sobre o qual será calculado o vencimento.
 * IN_DATA_PAGAMENTO: (Calendar) Data do pagamento a ser calculado o vencimento.
 * 
 * @version 20060628
 * 
 * @spring.bean id="CalcularVencimentoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorDocumentoCobranca" ref="ProvedorDocumentoCobranca"
 * @spring.property name="transactional" value="true"
 */
public class CalcularVencimentoService extends DocumentoCobrancaServiceBasic {
	
	public static final String SERVICE_NAME = "CalcularVencimentoService";

	public static final String IN_DOCUMENTO = "documento";
	public static final String IN_DATA_PAGAMENTO = "dataPagamento";
	
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execução do servico CalcularVencimentoService");
		
		try{
			log.debug("Preparando os argumentos");
			IEntity inDocumento = (IEntity) serviceData.getArgumentList().getProperty(IN_DOCUMENTO);
			Calendar inDataPagamento = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_PAGAMENTO);
			
			IGerenciadorDocumentoCobranca gerenciador = this.retrieveGerenciadorPorDocumento((DocumentoCobranca) inDocumento.getObject());
			
			log.debug("Executando o método de impressão avulsa do gerenciador");
			gerenciador.calcularVencimento(inDocumento, inDataPagamento, serviceData);
			
			log.debug("Adicionando a mensagem de sucesso");
			this.addInfoMessage(serviceData, "CALCULAR_VENCIMENTO_SUCESSO", inDocumento.toString());
			
		} catch (BusinessException e) {
			log.fatal(e.getErrorList());
			/* O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros. */
			throw new ServiceException(e.getErrorList());
		} catch (Exception e) {
			log.fatal(e.getMessage());
			
			/* Indica que o serviço falhou por causa de uma exceção do hibernate. */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
		
	}
}
