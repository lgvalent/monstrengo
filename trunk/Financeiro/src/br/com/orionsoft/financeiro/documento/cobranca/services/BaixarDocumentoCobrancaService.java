package br.com.orionsoft.financeiro.documento.cobranca.services;

import java.util.Calendar;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.IGerenciadorDocumentoCobranca;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este servi�o � utilizado para baixar um documento.
 * 
 * <p>Argumentos:</p>
 * IN_DOCUMENTO: (IEntity) Instancia do documento que ser� baixado.
 * IN_CONTA: (IEntity) 
 * IN_VALOR: (BigDecimal) Valor a ser baixado.
 * IN_DATA_MOVIMENTO: (Calendar) Data do movimento a ser baixado.
 * 
 * @version 20060621
 * 
 * @spring.bean id="BaixarDocumentoCobrancaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorDocumentoCobranca" ref="ProvedorDocumentoCobranca"
 * @spring.property name="transactional" value="true"
 */
public class BaixarDocumentoCobrancaService extends DocumentoCobrancaServiceBasic {
	
	public static final String SERVICE_NAME = "BaixarDocumentoCobrancaService";

	public static final String IN_DOCUMENTO_ID = "documento";
	public static final String IN_DATA_MOVIMENTO = "dataMovimento";
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execu��o do servico BaixarDocumentoCobrancaService");
		
		try{
			log.debug("Preparando os argumentos");
			Long inDocumentoId = (Long) serviceData.getArgumentList().getProperty(IN_DOCUMENTO_ID);
			Calendar inDataMovimento = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_MOVIMENTO);
			
			IEntity documento = UtilsCrud.retrieve(this.getServiceManager(), DocumentoCobranca.class, inDocumentoId, serviceData);
			
			IGerenciadorDocumentoCobranca gerenciador = this.retrieveGerenciadorPorDocumento((DocumentoCobranca) documento.getObject());
			
			log.debug("Executando o m�todo de Baixa avulsa do gerenciador");
			gerenciador.baixarDocumento((DocumentoCobranca)documento.getObject(), inDataMovimento, serviceData);
			
			log.debug("Adicionando a mensagem de sucesso");
			this.addInfoMessage(serviceData, "BAIXA_SUCESSO", documento.toString());
			
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
