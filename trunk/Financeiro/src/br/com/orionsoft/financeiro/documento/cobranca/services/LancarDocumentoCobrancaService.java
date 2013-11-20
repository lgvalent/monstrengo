package br.com.orionsoft.financeiro.documento.cobranca.services;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.IGerenciadorDocumentoCobranca;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;

/**
 * Este serviço é utilizado para lançar um documento.
 * 
 * <p>Argumentos:</p>
 * IN_DOCUMENTO: (DocumentoCobranca) Instancia do documento que será lançado.
 * IN_DATA_MOVIMENTO: (Calendar) Data do movimento a ser lançado.
 * 
 * @version 20060622
 * 
 * @spring.bean id="LancarDocumentoCobrancaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorDocumentoCobranca" ref="ProvedorDocumentoCobranca"
 * @spring.property name="transactional" value="true"
 */
public class LancarDocumentoCobrancaService extends DocumentoCobrancaServiceBasic {
	
	public static final String SERVICE_NAME = "LancarDocumentoCobrancaService";

	public static final String IN_DOCUMENTO = "documento";
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execução do servico LancarDocumentoCobrancaService");
		
		try{
			log.debug("Preparando os argumentos");
			DocumentoCobranca inDocumento = (DocumentoCobranca) serviceData.getArgumentList().getProperty(IN_DOCUMENTO);
			
			IGerenciadorDocumentoCobranca gerenciador = this.retrieveGerenciadorPorDocumento(inDocumento);
			
			log.debug("Executando o método de lançamento do gerenciador");
			gerenciador.lancarDocumento(this.getServiceManager().getEntityManager().<DocumentoCobranca>getEntity(inDocumento), serviceData);
			
			log.debug("Adicionando a mensagem de sucesso");
			this.addInfoMessage(serviceData, "LANCAR_SUCESSO", inDocumento.toString());
			
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
