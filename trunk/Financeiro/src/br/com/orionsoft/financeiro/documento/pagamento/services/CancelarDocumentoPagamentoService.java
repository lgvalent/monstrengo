package br.com.orionsoft.financeiro.documento.pagamento.services;

import java.util.Calendar;

import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.IGerenciadorDocumentoPagamento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este servi�o � utilizado para cancelar um documento.
 * 
 * <p>Argumentos:</p>
 * IN_DOCUMENTO: (IEntity) Instancia do documento que ser� cancelado.
 * 
 * @version 20070209
 * 
 * @spring.bean id="CancelarDocumentoPagamentoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorDocumentoPagamento" ref="ProvedorDocumentoPagamento"
 * @spring.property name="transactional" value="true"
 */
public class CancelarDocumentoPagamentoService extends DocumentoPagamentoServiceBasic {
	
	public static final String SERVICE_NAME = "CancelarDocumentoPagamentoService";

	public static final String IN_DOCUMENTO = "documento";
	public static final String IN_DATA_CANCELAMENTO = "dataCancelamento";
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execu��o do servico CancelarDocumentoPagamentoService");
		
		try{
			log.debug("Preparando os argumentos");
			IEntity<? extends DocumentoPagamento> inDocumento = (IEntity<? extends DocumentoPagamento>) serviceData.getArgumentList().getProperty(IN_DOCUMENTO);
			Calendar inDataCancelamento = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_CANCELAMENTO);
			
			IGerenciadorDocumentoPagamento gerenciador = this.retrieveGerenciadorPorDocumento((DocumentoPagamento) inDocumento.getObject());
			
			log.debug("Executando o m�todo cancelarDocumento avulso do gerenciador");
			gerenciador.cancelarDocumento(inDocumento, inDataCancelamento, serviceData);
			
			log.debug("Adicionando a mensagem de sucesso: cancelarDocumento");
			this.addInfoMessage(serviceData, "CANCELAR_DOCUMENTO_SUCESSO", inDocumento.toString());
			
		} catch (BusinessException e) {
			log.fatal(e.getErrorList());
			/* O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros. */
			throw new ServiceException(e.getErrorList());
		} catch (Exception e) {
			log.fatal(e.getMessage());
			
			/* Indica que o servi�o falhou por causa de uma exce��o do hibernate. */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
		
	}
}
